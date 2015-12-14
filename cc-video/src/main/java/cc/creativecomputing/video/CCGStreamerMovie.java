/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.video;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.nio.IntBuffer;
import java.util.List;

import org.gstreamer.Bus;
import org.gstreamer.Element;
import org.gstreamer.Format;
import org.gstreamer.Gst;
import org.gstreamer.GstObject;
import org.gstreamer.Pad;
import org.gstreamer.SeekFlags;
import org.gstreamer.SeekType;
import org.gstreamer.elements.PlayBin2;
import org.gstreamer.elements.RGBDataAppSink;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorModule;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.image.CCImageException;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

/**
 * originally adapted from the Processing project - http://processing.org
 * 
 * @author Andres Colubri
 * @author christianr
 * 
 */
public class CCGStreamerMovie extends CCMovieData {

	private class CCGStreamerEndOfStreamAction implements Bus.EOS {

		public void endOfStream(GstObject theArg0) {
			if (_myDoRepeat) {
				goToBeginning();
			} else {
				_myIsRunning = false;
			}
			_myMovieEvents.proxy().onEnd();
		}

	}

	private class CCGStreamerUpdateAction implements RGBDataAppSink.Listener {

		public void rgbFrame(int theWidth, int theHeight, IntBuffer theBuffer) {
			_myWidth = theWidth;
			_myHeight = theHeight;
			buffer(theBuffer);
			_myIsDataUpdated = true;
		}
	}

	private PlayBin2 gplayer;

	private boolean _myIsDataUpdated = false;

	private RGBDataAppSink _myBufferSink = null;

	/**
	 * full path of the movie file
	 * 
	 * @param thePath
	 */
	public CCGStreamerMovie(final CCAnimatorModule theAnimator, final String thePath) {
		super(theAnimator);

		setMovie(thePath);

//		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
//			_myCopyMask = "red_mask=(int)0xFF000000, green_mask=(int)0xFF0000, blue_mask=(int)0xFF00";
//		} else {
//			_myCopyMask = "red_mask=(int)0xFF, green_mask=(int)0xFF00, blue_mask=(int)0xFF0000";
//		}

		play();
		goToBeginning();
		pause();
	}

	@SuppressWarnings("unused")
	private float nanoSecToSecFrac(double theNanoSeconds) {
		theNanoSeconds /= 1E9;
		return (float) theNanoSeconds;
	}

	private long secToNanoLong(float theSeconds) {
		Float f = new Float(theSeconds * 1E9);
		return f.longValue();
	}

	/**
	 * Prints all the gstreamer elements currently used in the current player
	 * instance.
	 * 
	 */
	public void printElements() {
		List<Element> list = gplayer.getElementsRecursive();
		System.out.println(list);
		for (Element element : list) {
			System.out.println(element.getName());
			for (Pad myPad : element.getPads()) {
				System.out.println("   " + myPad.getName());
			}
		}

	}

	/**
	 * Initialize the movie by getting all data
	 * 
	 * @param thePath
	 */
	public void setMovie(final String thePath) {
		delete();
		gplayer = null;

		//CCNativeLibUtil.prepareLibraryForLoading (Gst.class, "OpenNI");
		
		// experimental change: use gstreamer-java bindings directly without the CCGStreamer library loader
		// TODO: call Gst.quit() on freeing
		CCGStreamer.init();

		
		try {
			Field fieldSysPath = ClassLoader.class.getDeclaredField( "sys_paths" );
			fieldSysPath.setAccessible( true );
			fieldSysPath.set( null, null );
		} catch (Exception e) {
			e.printStackTrace();
		}
		String[] myArguments = { "" };
		Gst.setUseDefaultContext(false);
		Gst.init("cc video", myArguments);
		
		
		// first check to see if this can be read locally from a file.
		try {
			// first try a local file using the dataPath. usually this will
			// work ok, but sometimes the dataPath is inside a jar file,
			// which is less fun, so this will crap out.
			File file = CCNIOUtil.dataPath(thePath).toFile();

			// read from a file just hanging out in the local folder.
			// this might happen when the video library is used with some
			// other application, or the person enters a full path name
			if (!file.exists()) {
				file = new File(thePath);
			}
			if (file.exists()) {
				gplayer = new PlayBin2("Movie Player");
				gplayer.setInputFile(file);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} // ignored
		CCLog.info("network read:" + gplayer);
		try {
			// Network read...
			if (gplayer == null && thePath.startsWith("http://")) {
				try {
					CCLog.info("network read");
					gplayer = new PlayBin2("Movie Player");
					gplayer.setURI(URI.create(thePath));
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception se) {
			se.printStackTrace();
			// online, whups. catch the security exception out here rather than
			// doing it three times (or whatever) for each of the cases above.
		}

		if (gplayer == null) {
			throw new CCImageException("Could not load movie file " + thePath);
		}

		_myBufferSink = new RGBDataAppSink("rgb", new CCGStreamerUpdateAction());

		// _myBufferSink.setAutoDisposeBuffer(false);
		gplayer.setVideoSink(_myBufferSink);
		// The setVideoSink() method sets the videoSink as a property of the
		// PlayBin,
		// which increments the refcount of the videoSink element. Disposing
		// here once
		// to decrement the refcount.
		_myBufferSink.dispose();

		// captureThread.addMovie(natSink, this);

		// Creating bus to handle end-of-stream event.
		Bus bus = gplayer.getBus();
		bus.connect(new CCGStreamerEndOfStreamAction());

		_myBorder = 0;

		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.BGRA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;

		_myIsDataCompressed = false;
		_myMustFlipVertically = true;

		_myIsDataUpdated = false;
		_myIsFirstFrame = true;
	}

	/**
	 * Releases the gstreamer resources associated to this movie object. It
	 * shouldn't be used after this.
	 */
	public void delete() {
		if (gplayer == null)
			return;

		try {
			if (gplayer.isPlaying()) {
				gplayer.stop();
			}
		} catch (IllegalStateException e) {
			CCLog.error("error when deleting player, maybe some native resource is already disposed");
		} catch (Exception e) {
			e.printStackTrace();
		}

		_myBufferSink.removeListener();
		_myBufferSink.dispose();
		_myBufferSink = null;

		gplayer.dispose();
		gplayer = null;
	}

	public void dispose() {
		delete();
	}

	public void post() {
	}

	/**
	 * Update the texture data
	 */
	protected void updateData() {
		// _myRaw.copyToArray(0, _myPixels, 0, _myPixels.length);
	}

	private float _myUpdateDelta = 0;
	private float _myUpdateRate = 30;

	@Override
	public void update(CCAnimator theAnimator) {
		_myUpdateDelta += theAnimator.deltaTime();
		if (_myIsDataUpdated) {
			_myIsDataUpdated = false;
			if (_myIsFirstFrame) {
				_myIsFirstFrame = false;
				_myListener.proxy().onInit(this);
			} else {
				_myListener.proxy().onUpdate(this);
				_myUpdateRate = (0.1f) * 1f / _myUpdateDelta + (0.9f)
						* _myUpdateRate;
				_myUpdateDelta = 0;
			}
		}
	}

	public float updateRate() {
		return _myUpdateRate;
	}

	/**
	 * this should free some memory
	 */
	@Override
	public void finalize() {
		delete();
	}

	public float duration() {
		return gplayer.queryDuration().toMillis() / 1000f;
	}

	public void goToBeginning() {
		time(0);
	}

	@Override
	public float volume() {
		return (float) gplayer.getVolume();
	}

	@Override
	public void volume(final float theVolume) {
		if (_myIsRunning) {
			gplayer.setVolume(theVolume);
		}
	}

	public void rate(float theSpeed) {
	}

	public float rate() {
		return 1.0f;
	}

	/**
	 * Get the original framerate of the source video. Note: calling this method
	 * repeatedly can slow down playback performance.
	 * 
	 * @return float
	 */
	public float frameRate() {
		return (float) gplayer.getVideoSinkFrameRate();
	}

	public int frame() {
		return CCMath.ceil(time() * frameRate() - 0.25f) - 1;
	}

	public void frame(int theFrame) {
		if (!_myIsRunning)
			gplayer.play();

		System.out.println(gplayer.queryPosition().toMillis() + "/"
				+ gplayer.queryDuration().toMillis());

		float srcFramerate = frameRate();

		// The duration of a single frame:
		float frameDuration = 1.0f / srcFramerate;

		// We move to the middle of the frame by adding 0.5:
		float where = (theFrame + 0.5f) * frameDuration;

		// Taking into account border effects:
		float diff = duration() - where;
		if (diff < 0) {
			where += diff - 0.25 * frameDuration;
		}

		boolean res;
		long pos = secToNanoLong(where);

		System.out.println("FRAME:");
		System.out.println(theFrame + " / " + CCFormatUtil.formatTime(where)
				+ " / " + pos + " / " + frame());

		res = gplayer.seek(1.0, Format.TIME, SeekFlags.FLUSH, SeekType.SET,
				pos, SeekType.NONE, -1);

		if (!res) {
			System.err.println("Seek operation failed.");
		}

		// getState() will wait until any async state change
		// (like seek in this case) has completed
		// seeking = true;
		gplayer.getState();
		System.out.println(theFrame + " / " + CCFormatUtil.formatTime(where)
				+ " / " + pos + " / " + res + " / " + frame());

		if (!_myIsRunning)
			gplayer.pause();
	}

	public int numberOfFrames() {
		double myDuration = gplayer.queryDuration().toNanos() / 1e9;
		return (int) (myDuration * frameRate());
	}

	@Override
	public void play(boolean theDoRestart) {
		super.play(theDoRestart);
		gplayer.play();
	}

	/**
	 * Plays a movie continuously, restarting it when it is over.
	 */
	public void loop() {
		_myDoRepeat = true;
		play();
	}

	/**
	 * Pauses a movie during play back. If a movie is started again with play(),
	 * it will continue from where it was paused.
	 */
	public void pause() {
		super.pause();
		gplayer.pause();
	}

	/**
	 * Stops a movie from continuing. The playback returns to the beginning so
	 * when a movie is played, it will begin from the beginning.
	 */
	public void stop() {
		super.stop();
		gplayer.stop();
	}

	@Override
	public float time() {
		return gplayer.queryPosition().toMillis() / 1000f;
	}

	@Override
	public void time(float theTime) {

		boolean res;
		long pos = secToNanoLong(theTime);

		res = gplayer.seek(1.0, Format.TIME, SeekFlags.FLUSH, SeekType.SET,
				pos, SeekType.NONE, -1);

		if (!res) {
			System.err.println("Seek operation failed.");
		}

		// getState() will wait until any async state change
		// (like seek in this case) has completed
		// seeking = true;
		gplayer.getState();
		// seeking = false;
	}
}
