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

//import java.awt.Dimension;
//import java.io.*;
//import java.net.URI;
import java.nio.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.EnumSet;
import java.util.List;
import java.lang.reflect.*;

import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.Buffer;
import org.freedesktop.gstreamer.device.*;
import org.freedesktop.gstreamer.elements.*;
import org.freedesktop.gstreamer.event.SeekFlags;
import org.freedesktop.gstreamer.event.SeekType;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCSystem;
import cc.creativecomputing.core.CCSystem.CCOS;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.math.CCMath;

/**
 * ( begin auto-generated from Movie.xml )
 *
 * Datatype for storing and playing movies in Apple's QuickTime format. Movies
 * must be located in the sketch's data directory or an accessible place on the
 * network to load without an error.
 *
 * ( end auto-generated )
 *
 * @webref video
 * @usage application
 */
public class Capture extends CCVideo {
	public static String[] supportedProtocols = { "http" };
	public float frameRate;
//  public String filename;
	public Pipeline pipe;
	Bin bin;

	protected boolean playing = false;
	protected boolean paused = false;
	protected boolean repeat = false;

	protected float rate;
	protected int bufWidth;
	protected int bufHeight;
	protected float volume;

	protected Method movieEventMethod;
	protected Object eventHandler;

	protected boolean sinkReady;

	protected AppSink rgbSink = null;

	protected boolean firstFrame = true;
	protected boolean seeking = false;

	protected boolean useBufferSink = false;
	protected boolean outdatedPixels = true;
	
	protected Buffer natBuffer = null;
//  protected BufferDataAppSink natSink = null;
	protected String device;
	protected static List<Device> devices; // we're caching this list for speed reasons

	NewSampleListener newSampleListener;
//  NewPrerollListener newPrerollListener;
	private final Lock bufferLock = new ReentrantLock();
	private boolean _myIsDataUpdated = false;
	


	/**
	 * Open a specific capture device with a given resolution and framerate
	 * 
	 * @param parent PApplet, typically "this"
	 * @param width  width in pixels
	 * @param height height in pixels
	 * @param device device name (null opens the default device)
	 * @param fps    frames per second (0 uses the default framerate)
	 * @see list()
	 */
	public Capture(final CCAnimator theAnimator, int width, int height, String device, float fps) {
		super(theAnimator);
		this.device = device;
		this.frameRate = fps;
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.BGRA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;

		_myIsDataCompressed = false;
		_myMustFlipVertically = true;
		
		_myIsFirstFrame = true;
		
		initGStreamer();
	}

	/**
	 * Open the default capture device with a given resolution and framerate
	 * 
	 * @param parent PApplet, typically "this"
	 * @param width  width in pixels
	 * @param height height in pixels
	 * @param fps    frames per second
	 */
	public Capture(final CCAnimator theAnimator, int width, int height, float fps) {
		this(theAnimator, width, height, null, fps);
	}

	/**
	 * Open a specific capture device with a given resolution
	 * 
	 * @param parent PApplet, typically "this"
	 * @param width  width in pixels
	 * @param height height in pixels
	 * @param device device name
	 * @see list()
	 */
	public Capture(final CCAnimator theAnimator, int width, int height, String device) {
		this(theAnimator, width, height, device, 0);
	}

	/**
	 * Open the default capture device with a given resolution
	 * 
	 * @param parent PApplet, typically "this"
	 * @param width  width in pixels
	 * @param height height in pixels
	 */
	public Capture(final CCAnimator theAnimator, int width, int height) {
		this(theAnimator, width, height, 0);
	}

	/**
	 * Open a specific capture device
	 * 
	 * @param parent PApplet, typically "this"
	 * @param device device name
	 * @see list()
	 */
	public Capture(final CCAnimator theAnimator, String device) {
		// attemt to use a default resolution
		this(theAnimator, 640, 480, device, 0);
	}

	/**
	 * Open the default capture device
	 * 
	 * @param parent PApplet, typically "this"
	 */
	public Capture(final CCAnimator theAnimator) {
		// attemt to use a default resolution
		this(theAnimator, null);
	}

	/**
	 * Disposes all the native resources associated to this movie.
	 *
	 * NOTE: This is not official API and may/will be removed at any time.
	 */
	public void dispose() {
		if (pipe == null)
			return;
//      try {
//        if (playbin.isPlaying()) {
//          playbin.stop();
//          playbin.getState();
//        }
//      } catch (Exception e) {
//        e.printStackTrace();
//      }

		if (rgbSink != null) {
			rgbSink.disconnect(newSampleListener);
			rgbSink.dispose();
		}
		pipe.setState(org.freedesktop.gstreamer.State.NULL);
		pipe.getState();
		pipe.getBus().dispose();
		pipe.dispose();

	}

	/**
	 * Finalizer of the class.
	 */
	protected void finalize() throws Throwable {
		try {
			dispose();
		} finally {
			super.finalize();
		}
	}

	/**
	 * ( begin auto-generated from Movie_frameRate.xml )
	 *
	 * Sets how often frames are read from the movie. Setting the <b>fps</b>
	 * parameter to 4, for example, will cause 4 frames to be read per second.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @param ifps speed of the movie in frames per second
	 * @brief Sets the target frame rate
	 */
	public void frameRate(float ifps) {
		if (seeking)
			return;

		// We calculate the target ratio in the case both the
		// current and target framerates are valid (greater than
		// zero), otherwise we leave it as 1.
		float f = (0 < ifps && 0 < frameRate) ? ifps / frameRate : 1;

		if (playing) {
			pipe.pause();
			pipe.getState();
		}

		long t = pipe.queryPosition(TimeUnit.NANOSECONDS);

		boolean res;
		long start, stop;
		if (rate > 0) {
			start = t;
			stop = -1;
		} else {
			start = 0;
			stop = t;
		}

		EnumSet<SeekFlags> myFlags = EnumSet.of(SeekFlags.FLUSH);
		res = pipe.seek(rate * f, Format.TIME, myFlags, SeekType.SET, start, SeekType.SET, stop);
		pipe.getState();

		if (!res) {
			CCLog.info("Seek operation failed.");
		}

		if (playing) {
			pipe.play();
		}

		// getState() will wait until any async state change
		// (like seek in this case) has completed
		seeking = true;
		pipe.getState();
		seeking = false;
	}

	/**
	 * ( begin auto-generated from Movie_speed.xml )
	 *
	 * Sets the relative playback speed of the movie. The <b>rate</b> parameters
	 * sets the speed where 2.0 will play the movie twice as fast, 0.5 will play at
	 * half the speed, and -1 will play the movie in normal speed in reverse.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @param irate speed multiplier for movie playback
	 * @brief Sets the relative playback speed
	 */
	public void speed(float irate) {
		// If the frameRate() method is called continuously with very similar
		// rate values, playback might become sluggish. This condition attempts
		// to take care of that.
		if (CCMath.abs(rate - irate) > 0.1) {
			rate = irate;
			frameRate(frameRate); // The framerate is the same, but the rate (speed) could be different.
		}
	}

	/**
	 * ( begin auto-generated from Movie_duration.xml )
	 *
	 * Returns the length of the movie in seconds. If the movie is 1 minute and 20
	 * seconds long the value returned will be 80.0.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @brief Returns length of movie in seconds
	 */
	public double duration() {
		float sec = pipe.queryDuration(TimeUnit.SECONDS);
		float nanosec = pipe.queryDuration(TimeUnit.NANOSECONDS);
		return sec + Video.nanoSecToSecFrac(nanosec);
	}

	/**
	 * ( begin auto-generated from Movie_time.xml )
	 *
	 * Returns the location of the playback head in seconds. For example, if the
	 * movie has been playing for 4 seconds, the number 4.0 will be returned.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @brief Returns location of playback head in units of seconds
	 */
	public double time() {
		float sec = pipe.queryPosition(TimeUnit.SECONDS);
		float nanosec = pipe.queryPosition(TimeUnit.NANOSECONDS);
		return sec + Video.nanoSecToSecFrac(nanosec);
	}

	/**
	 * ( begin auto-generated from Movie_jump.xml )
	 *
	 * Jumps to a specific location within a movie. The parameter <b>where</b> is in
	 * terms of seconds. For example, if the movie is 12.2 seconds long, calling
	 * <b>jump(6.1)</b> would go to the middle of the movie.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @param where position to jump to specified in seconds
	 * @brief Jumps to a specific location
	 */
	public void jump(float where) {

//    if (seeking) return;
//
//    if (!sinkReady) {
//      initSink();
//    }
//
//    // Round the time to a multiple of the source framerate, in
//    // order to eliminate stutter. Suggested by Daniel Shiffman
//    float fps = getSourceFrameRate();
//    int frame = (int)(where * fps);
//    where = frame / fps;
//
//    boolean res;
//    long pos = Video.secToNanoLong(where);
//
//    res = pipe.seek(rate, Format.TIME, SeekFlags.FLUSH,
//                       SeekType.SET, pos, SeekType.NONE, -1);
//
//    if (!res) {
//      PGraphics.showWarning("Seek operation failed.");
//    }

		// getState() will wait until any async state change
		// (like seek in this case) has completed
//    seeking = true;
//    pipe.getState();
//    seeking = false;
		/*
		 * if (seeking) return; // don't seek again until the current seek operation is
		 * done. if (!sinkReady) { initSink(); } // Round the time to a multiple of the
		 * source framerate, in // order to eliminate stutter. Suggested by Daniel
		 * Shiffman float fps = getSourceFrameRate(); int frame = (int)(where * fps);
		 * final float seconds = frame / fps; // Put the seek operation inside a thread
		 * to avoid blocking the main // animation thread Thread seeker = new Thread() {
		 * 
		 * @Override public void run() { long pos = Video.secToNanoLong(seconds);
		 * boolean res = playbin.seek(rate, Format.TIME, SeekFlags.FLUSH, SeekType.SET,
		 * pos, SeekType.NONE, -1); if (!res) {
		 * PGraphics.showWarning("Seek operation failed."); } // getState() will wait
		 * until any async state change // (like seek in this case) has completed
		 * seeking = true; playbin.getState(); seeking = false; } }; seeker.start();
		 */
	}

	/**
	 * ( begin auto-generated from Movie_play.xml )
	 *
	 * Plays a movie one time and stops at the last frame.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @brief Plays movie one time and stops at the last frame
	 */
	public void start() {
		if (seeking)
			return;

		if (!sinkReady) {
			initSink();
		}

		playing = true;
		paused = false;
		pipe.play();
		pipe.getState();
	}

	/**
	 * ( begin auto-generated from Movie_loop.xml )
	 *
	 * Plays a movie continuously, restarting it when it's over.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @brief Plays a movie continuously, restarting it when it's over.
	 */
//  public void loop() {
//    if (seeking) return;
//
//    repeat = true;
//    play();
//  }

	/**
	 * ( begin auto-generated from Movie_noLoop.xml )
	 *
	 * If a movie is looping, calling noLoop() will cause it to play until the end
	 * and then stop on the last frame.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @brief Stops the movie from looping
	 */
	public void noLoop() {
		if (seeking)
			return;

		if (!sinkReady) {
			initSink();
		}

		repeat = false;
	}

	/**
	 * ( begin auto-generated from Movie_pause.xml )
	 *
	 * Pauses a movie during playback. If a movie is started again with play(), it
	 * will continue from where it was paused.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @brief Pauses the movie
	 */
	public void pause() {
		if (seeking)
			return;

		if (!sinkReady) {
			initSink();
		}

		playing = false;
		paused = true;
		pipe.pause();
		pipe.getState();
	}

	/**
	 * ( begin auto-generated from Movie_stop.xml )
	 *
	 * Stops a movie from continuing. The playback returns to the beginning so when
	 * a movie is played, it will begin from the beginning.
	 *
	 * ( end auto-generated )
	 *
	 * @webref movie
	 * @usage web_application
	 * @brief Stops the movie
	 */
	public void stop() {
		if (seeking)
			return;

		if (!sinkReady) {
			initSink();
		}

		if (playing) {
			jump(0);
			playing = false;
		}
		paused = false;
		pipe.stop();
		pipe.getState();
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

	

//  public int get(int x, int y) {
//    if (outdatedPixels) loadPixels();
//    return super.get(x, y);
//  }
//
//
//  protected void getImpl(int sourceX, int sourceY,
//                         int sourceWidth, int sourceHeight,
//                         PImage target, int targetX, int targetY) {
//    if (outdatedPixels) loadPixels();
//    super.getImpl(sourceX, sourceY, sourceWidth, sourceHeight,
//                  target, targetX, targetY);
//  }

	////////////////////////////////////////////////////////////

	// Initialization methods.

	protected void initGStreamer() {
		pipe = null;

		GStreamerLibrary.getInstance().init();

		Element srcElement = null;
		if (device == null) {

			// use the default device from GStreamer
			srcElement = ElementFactory.make("autovideosrc", null);

		} else if (CCSystem.os == CCOS.WINDOWS || CCSystem.os == CCOS.LINUX) {

			// look for device
			if (devices == null) {
				DeviceMonitor monitor = new DeviceMonitor();
				monitor.addFilter("Video/Source", null);
				devices = monitor.getDevices();
			}

			for (int i = 0; i < devices.size(); i++) {
				if (devices.get(i).getDisplayName().equals(device)) {
					// found device
					srcElement = devices.get(i).createElement(null);
					break;
				}
			}

			// error out if we got passed an invalid device name
			if (srcElement == null) {
				throw new RuntimeException("Could not find device " + device);
			}

		} else if (CCSystem.os == CCOS.MACOSX) {

			// use numeric index
			srcElement = ElementFactory.make("avfvideosrc", null);
			srcElement.set("device-index", Integer.parseInt(device));

		} else {

			// unused fallback
			srcElement = ElementFactory.make("autovideosrc", null);

		}

		pipe = new Pipeline();

		Element videoscale = ElementFactory.make("videoscale", null);
		Element videoconvert = ElementFactory.make("videoconvert", null);
		Element capsfilter = ElementFactory.make("capsfilter", null);

		String frameRateString;
		if (frameRate != 0.0) {
			frameRateString = ", framerate=" + fpsToFramerate(frameRate);
		} else {
			frameRateString = "";
		}
		capsfilter.set("caps",
				Caps.fromString("video/x-raw, width=" + width() + ", height=" + height() + frameRateString));

		rgbSink = new AppSink("sink");
		rgbSink.set("emit-signals", true);
		newSampleListener = new NewSampleListener();
		rgbSink.connect(newSampleListener);
		// XXX: unsure about BGRx
		rgbSink.setCaps(Caps.fromString("video/x-raw, format=BGRx"));

		pipe.addMany(srcElement, videoscale, videoconvert, capsfilter, rgbSink);
		Pipeline.linkMany(srcElement, videoscale, videoconvert, capsfilter, rgbSink);

		makeBusConnections(pipe.getBus());

		try {
			// register methods

//      setEventHandlerObject(parent);

			rate = 1.0f;
			volume = -1;
			sinkReady = false;
			bufWidth = bufHeight = 0;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String fpsToFramerate(float fps) {
		String formatted = Float.toString(fps);
		// this presumes the delimitter is always a dot
		int i = formatted.indexOf('.');
		if (Math.floor(fps) != fps) {
			int denom = (int) Math.pow(10, formatted.length() - i - 1);
			int num = (int) (fps * denom);
			return num + "/" + denom;
		} else {
			return (int) fps + "/1";
		}
	}

	/**
	 * Uses a generic object as handler of the movie. This object should have a
	 * movieEvent method that receives a GSMovie argument. This method will be
	 * called upon a new frame read event.
	 *
	 */
	protected void setEventHandlerObject(Object obj) {
		eventHandler = obj;

		try {
			movieEventMethod = eventHandler.getClass().getMethod("captureEvent", Capture.class);
			return;
		} catch (Exception e) {
			// no such method, or an error... which is fine, just ignore
		}

		// movieEvent can alternatively be defined as receiving an Object, to allow
		// Processing mode implementors to support the video library without linking
		// to it at build-time.
		try {
			movieEventMethod = eventHandler.getClass().getMethod("captureEvent", Object.class);
		} catch (Exception e) {
			// no such method, or an error... which is fine, just ignore
		}
	}

	protected void initSink() {
		pipe.setState(org.freedesktop.gstreamer.State.READY);
		sinkReady = true;
	}

	private void makeBusConnections(Bus bus) {
		bus.connect(new Bus.ERROR() {

			public void errorMessage(GstObject arg0, int arg1, String arg2) {
				System.err.println(arg0 + " : " + arg2);
			}
		});
		bus.connect(new Bus.EOS() {

			public void endOfStream(GstObject arg0) {
				try {
					if (repeat) {
						pipe.seek(0, TimeUnit.NANOSECONDS);
					} else {
						stop();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
	}

	

	

	

	
	/**
	 * NOTE: This is not official API and may/will be removed at any time.
	 */
	public synchronized void disposeBuffer(Object buf) {
		((Buffer) buf).dispose();
	}

	/**
	 * Returns a list of all capture devices
	 * 
	 * @return array of device names
	 */
	static public String[] list() {
		Video.init();

		String[] out;
		if (CCSystem.os == CCOS.WINDOWS || CCSystem.os == CCOS.LINUX) {

			DeviceMonitor monitor = new DeviceMonitor();
			monitor.addFilter("Video/Source", null);
			devices = monitor.getDevices();

			out = new String[devices.size()];
			for (int i = 0; i < devices.size(); i++) {
				Device dev = devices.get(i);
				out[i] = dev.getDisplayName();
			}

		} else {

			// device enumeration is currently not supported on macOS
			out = new String[1];
			out[0] = "0";
			System.err.println("Device enumeration is currently not supported on your platform.");

		}

		return out;
	}

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
			
			Structure capsStruct = sample.getCaps().getStructure(0);
			_myWidth = capsStruct.getInteger("width");
			_myHeight = capsStruct.getInteger("height");
			
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

	public static void main(String[] args) {
		GStreamerLibrary.getInstance().init();
		for(String myCam:list()) {
			CCLog.info(myCam);
		}
	}
}