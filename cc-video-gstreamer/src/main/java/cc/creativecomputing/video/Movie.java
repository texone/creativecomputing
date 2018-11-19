/* -*- mode: java; c-basic-offset: 2; indent-tabs-mode: nil -*- */

/*
  Part of the Processing project - http://processing.org

  Copyright (c) 2004-12 Ben Fry and Casey Reas
  The previous version of this code was developed by Hernando Barragan

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU Lesser General Public
  License as published by the Free Software Foundation; either
  version 2.1 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU Lesser General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/

package cc.creativecomputing.video;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.file.Path;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.freedesktop.gstreamer.Buffer;
import org.freedesktop.gstreamer.Bus;
import org.freedesktop.gstreamer.Caps;
import org.freedesktop.gstreamer.Element;
import org.freedesktop.gstreamer.FlowReturn;
import org.freedesktop.gstreamer.Format;
import org.freedesktop.gstreamer.Fraction;
import org.freedesktop.gstreamer.GstObject;
import org.freedesktop.gstreamer.Sample;
import org.freedesktop.gstreamer.SeekFlags;
import org.freedesktop.gstreamer.SeekType;
import org.freedesktop.gstreamer.Structure;
import org.freedesktop.gstreamer.elements.AppSink;
import org.freedesktop.gstreamer.elements.PlayBin;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.math.CCMath;

/**
 * Datatype for storing and playing movies in Apple's QuickTime format. Movies
 * must be located in the sketch's data directory or an accessible place on the
 * network to load without an error.
 */
public class Movie extends CCMovieData {

	private double nativeFrameRate; // the file's native fps
	private double frameRate; // the current playback fps
	private double rate; // speed multiplier (1.0: frameRate = nativeFrameRate)

	private PlayBin _myPlaybin;

	private boolean _myIsSinkReady;

	private AppSink _myRGBSink = null;

	private boolean _myIsDataUpdated = false;
	private boolean _myIsSeeking = false;

	public static ByteBuffer cloneByteBuffer(final ByteBuffer original) {
	    // Create clone with same capacity as original.
	    final ByteBuffer clone = (original.isDirect()) ?
	        ByteBuffer.allocateDirect(original.capacity()) :
	        ByteBuffer.allocate(original.capacity());

	    // Create a read-only copy of the original.
	    // This allows reading from the original without modifying it.
	    final ByteBuffer readOnlyCopy = original.asReadOnlyBuffer();

	    // Flip and read from the original.
	    readOnlyCopy.flip();
	    clone.put(readOnlyCopy);
	    clone.position(original.position());
	    clone.limit(original.limit());
	    clone.order(original.order());
	    return clone;
	}
	
	private final Lock bufferLock = new ReentrantLock();
	private class NewSampleListener implements AppSink.NEW_SAMPLE {

		public  ByteBuffer clone(ByteBuffer original) {
		       ByteBuffer clone = ByteBuffer.allocate(original.capacity());
		       original.rewind();//copy from the beginning
		       clone.put(original);
		       original.rewind();
		       clone.flip();
		       return clone;
		}
		
		@Override
		public FlowReturn newSample(AppSink sink) {
			Sample sample = sink.pullSample();

			// pull out metadata from caps
			Structure capsStruct = sample.getCaps().getStructure(0);
			_myWidth = capsStruct.getInteger("width");
			_myHeight = capsStruct.getInteger("height");
			
			Fraction fps = capsStruct.getFraction("framerate");
			nativeFrameRate = (double) fps.numerator / fps.denominator;

			// set the playback rate to the file's native framerate
			// unless the user has already set a custom one
			if (frameRate == -1.0) {
				frameRate = nativeFrameRate;
			}

			Buffer buffer = sample.getBuffer();
			ByteBuffer bb = buffer.map(false);
			if (bb != null) {
				// If the EDT is still copying data from the buffer, just drop this frame
				if (!bufferLock.tryLock()) {
					return FlowReturn.OK;
				}
				buffer(clone(bb));
				bufferLock.unlock();
				buffer.unmap();
			}
			sample.dispose();
			_myIsDataUpdated = true;
			
			return FlowReturn.OK;
		}
	}

	private NewSampleListener _myNewSampleListener;

	/**
	 * Creates an instance of GSMovie loading the movie from filename.
	 *
	 * @param parent   PApplet
	 * @param filename String
	 */
	public Movie(final CCAnimator theAnimator, final Path thePath) {
		super(theAnimator);
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.BGRA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;

		_myIsDataCompressed = false;
		_myMustFlipVertically = true;
		
		_myIsFirstFrame = true;
		
		initGStreamer(thePath);
	}
	
	protected void initGStreamer(Path theFilename) {
		_myPlaybin = null;

		File myFile = theFilename.toFile();
		GStreamerLibrary.getInstance().init();

		// first check to see if this can be read locally from a file.
		
		try {
			// first try a local file using the dataPath. usually this will
			// work ok, but sometimes the dataPath is inside a jar file,
			// which is less fun, so this will crap out.
			if (myFile.exists()) {
				_myPlaybin = new PlayBin("Movie Player");
				_myPlaybin.setInputFile(myFile);
			}
		} catch (Exception e) {
			// XXX: we want to see exceptions here such as
			// "java.lang.IllegalArgumentException: No such Gstreamer factory: playbin"
			// just hide the file-not-found ones
		}

		if (_myPlaybin == null) {
			throw new RuntimeException("Could not load movie file " + theFilename);
		}

		// we've got a valid movie! let's rock.
		try {
			nativeFrameRate = -1;
			frameRate = -1;
			rate = 1.0f;
			_myIsSinkReady = false;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	protected void initSink() {
		_myRGBSink = new AppSink("sink");
		_myRGBSink.set("emit-signals", true);
		_myNewSampleListener = new NewSampleListener();
		_myRGBSink.connect(_myNewSampleListener);
		_myRGBSink.connect(new AppSink.NEW_PREROLL() {
			
			@Override
			public FlowReturn newPreroll(AppSink arg0) {
				return null;
			}
		});
		
		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
			_myRGBSink.setCaps(Caps.fromString("video/x-raw, format=BGRx"));
		} else {
			_myRGBSink.setCaps(Caps.fromString("video/x-raw, format=xRGB"));
		}
		_myPlaybin.setVideoSink(_myRGBSink);

		makeBusConnections(_myPlaybin.getBus());
		_myPlaybin.setState(org.freedesktop.gstreamer.State.READY);

		_myIsSinkReady = true;
	}

	private void makeBusConnections(Bus bus) {
		bus.connect(new Bus.ERROR() {

			public void errorMessage(GstObject arg0, int arg1, String arg2) {
				System.err.println(arg0 + " : " + arg2);
			}
		});
		bus.connect(new Bus.EOS() {

			public void endOfStream(GstObject arg0) {
				if (_myDoRepeat) {
					if (0 < rate) {
						// Playing forward, so we return to the beginning
						time(0);
					} else {
						// Playing backwards, so we go to the end.
						time(duration());
					}

					// The rate is set automatically to 1 when restarting the
					// stream, so we need to call frameRate in order to reset
					// to the latest fps rate.
					frameRate(frameRate);
				} else {
					_myIsRunning = false;
					endEvents.proxy().event();
				}
			}
		});
	}
	
	private double _myUpdateDelta = 0;
	private double _myUpdateRate = 30;

	@Override
	public void update(CCAnimator theAnimator) {
		_myUpdateDelta += theAnimator.deltaTime();
		if (!_myIsDataUpdated) return;
	
		_myIsDataUpdated = false;
		
		if (_myIsFirstFrame) {
			_myIsFirstFrame = false;
			initEvents.proxy().event(this);
		} else {
			updateEvents.proxy().event(this);
			_myUpdateRate = (0.1f) * 1f / _myUpdateDelta + (0.9f) * _myUpdateRate;
			_myUpdateDelta = 0;
		}
	}

	/**
	 * Disposes all the native resources associated to this movie.
	 * 
	 * NOTE: This is not official API and may/will be removed at any time.
	 */
	public void dispose() {
		if (_myPlaybin == null) return;

		_myRGBSink.disconnect(_myNewSampleListener);
		_myRGBSink.dispose();
		_myPlaybin.setState(org.freedesktop.gstreamer.State.NULL);
		_myPlaybin.getState();
		_myPlaybin.getBus().dispose();
		_myPlaybin.dispose();
	}

	/**
	 * Finalizer of the class.
	 */
	protected void finalize() throws Throwable {
		dispose();
	}

	/**
	 * Sets how often frames are read from the movie. Setting the <b>fps</b>
	 * parameter to 4, for example, will cause 4 frames to be read per second.
	 * 
	 * @param ifps speed of the movie in frames per second
	 */
	public void frameRate(double ifps) {
		if (_myIsSeeking)
			return;

		// We calculate the target ratio in the case both the
		// current and target framerates are valid (greater than
		// zero), otherwise we leave it as 1.
		double f = (0 < ifps && 0 < frameRate) ? ifps / frameRate : 1;

		if (_myIsRunning) {
			_myPlaybin.pause();
			_myPlaybin.getState();
		}

		long t = _myPlaybin.queryPosition(TimeUnit.NANOSECONDS);

		boolean res;
		long start, stop;
		if (rate > 0) {
			start = t;
			stop = -1;
		} else {
			start = 0;
			stop = t;
		}

		res = _myPlaybin.seek(rate * f, Format.TIME, SeekFlags.FLUSH, SeekType.SET, start, SeekType.SET, stop);
		_myPlaybin.getState();

		if (!res) {
			CCLog.info("Seek operation failed.");
		}

		if (_myIsRunning) {
			_myPlaybin.play();
		}

		frameRate = ifps;
	}

	/**
	 * Sets the relative playback speed of the movie. The <b>rate</b> parameters
	 * sets the speed where 2.0 will play the movie twice as fast, 0.5 will play at
	 * half the speed, and -1 will play the movie in normal speed in reverse.
	 *
	 * @param theRate speed multiplier for movie playback
	 */
	@Override
	public void rate(double theRate) {
		// If the frameRate() method is called continuously with very similar
		// rate values, playback might become sluggish. This condition attempts
		// to take care of that.
		if (CCMath.abs(rate - theRate) <= 0.1) return;
		
		rate = theRate;
		frameRate(frameRate); // The framerate is the same, but the rate (speed) could be different.
	}
	
	@Override
	public double rate() {
		return rate;
	}

	/**
	 * Returns the length of the movie in seconds. If the movie is 1 minute and 20
	 * seconds long the value returned will be 80.0.
	 */
	@Override
	public double duration() {
		double sec = _myPlaybin.queryDuration().toSeconds();
		double nanosec = _myPlaybin.queryDuration().getNanoSeconds();
		return sec + Video.nanoSecToSecFrac(nanosec);
	}

	/**
	 * Returns the location of the playback head in seconds. For example, if the
	 * movie has been playing for 4 seconds, the number 4.0 will be returned.
	 */
	@Override
	public double time() {
		double sec = _myPlaybin.queryPosition().toSeconds();
		double nanosec = _myPlaybin.queryPosition().getNanoSeconds();
		return sec + Video.nanoSecToSecFrac(nanosec);
	}

	/**
	 * Jumps to a specific location within a movie. The parameter <b>where</b> is in
	 * terms of seconds. For example, if the movie is 12.2 seconds long, calling
	 * <b>jump(6.1)</b> would go to the middle of the movie.
	 * 
	 * @param where position to jump to specified in seconds
	 */
	@Override
	public void time(double where) {
		if (_myIsSeeking)
			return;

		if (!_myIsSinkReady) {
			initSink();
		}

		// Round the time to a multiple of the source framerate, in
		// order to eliminate stutter. Suggested by Daniel Shiffman
		if (nativeFrameRate != -1) {
			int frame = (int) (where * nativeFrameRate);
			where = frame / nativeFrameRate;
		}
		
//		_myPlaybin.stop();
//		_myPlaybin.getState();

		long pos = Video.secToNanoLong(where);
		boolean res = _myPlaybin.seek(rate, Format.TIME, SeekFlags.FLUSH, SeekType.SET, pos, SeekType.NONE, -1);
		
		
		if (!res) {
			CCLog.info("Seek operation failed.");
		}

		// getState() will wait until any async state change
		// (like seek in this case) has completed

		
		_myIsSeeking = true;
		_myPlaybin.getState();
		_myIsSeeking = false;
		
	}

	/**
	 * Plays a movie one time and stops at the last frame.
	 */
	public void play() {
		if (_myIsSeeking)
			return;

		if (!_myIsSinkReady) {
			initSink();
		}

		_myPlaybin.play();
		
		_myIsRunning = true;
		_myIsPaused = false;

		playEvents.proxy().event();
	}
	
	/**
	 * Plays a movie one time and stops at the last frame.
	 */
	public void play(boolean theDoRestart) {
		if (_myIsSeeking)
			return;

		if (!_myIsSinkReady) {
			initSink();
		}
		
		if (theDoRestart) {
			_myPlaybin.stop();
		}
			
		_myPlaybin.play();
		
		_myIsRunning = true;
		_myIsPaused = false;


		playEvents.proxy().event();
	}

	/**
	 * Plays a movie continuously, restarting it when it's over.
	 */
	public void loop() {
		if (_myIsSeeking)
			return;

		_myDoRepeat = true;
		play();
	}

	/**
	 * If a movie is looping, calling noLoop() will cause it to play until the end
	 * and then stop on the last frame.
	 */
	public void noLoop() {
		if (_myIsSeeking)
			return;

		if (!_myIsSinkReady) {
			initSink();
		}

		_myDoRepeat = false;
	}

	/**
	 * Pauses a movie during playback. If a movie is started again with play(), it
	 * will continue from where it was paused.
	 */
	public void pause() {
		if (_myIsSeeking)
			return;

		if (!_myIsSinkReady) {
			initSink();
		}

		_myPlaybin.pause();
		
		super.pause();
	}

	/**
	 *Stops a movie from continuing. The playback returns to the beginning so when
	 * a movie is played, it will begin from the beginning.
	 */
	@Override
	public void stop() {
		if (_myIsSeeking)
			return;

		if (!_myIsSinkReady) {
			initSink();
		}
		_myPlaybin.stop();
		_myPlaybin.getState(-1);
		
		_myIsRunning = false;
		_myIsPaused = false;
		stopEvents.proxy().event();
	}
	
	private double _myVolume = -1;

	/**
	 * Change the volume. Values are from 0 to 1.
	 *
	 * @param double v
	 */
	@Override
	public void volume(double theVolume) {
		if (_myIsRunning && CCMath.abs(_myVolume - theVolume) > 0.001) {
			_myPlaybin.setVolume(theVolume);
			_myVolume = theVolume;
		}
	}
	
	@Override
	public double volume() {
		return _myPlaybin.getVolume();
	}

	/**
	 * Get the original framerate of the source video. Note: calling this method
	 * repeatedly can slow down playback performance.
	 *
	 * @return double
	 */
	protected double getSourceFrameRate() {
		// This doesn't work any longer with GStreamer 1.x
		// return (double)playbin.getVideoSinkFrameRate();
		// so use the field extracted from the caps instead:
		return nativeFrameRate;
	}

	@Override
	public double frameRate() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void goToBeginning() {
		time(0);
	}

}
