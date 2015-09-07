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
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.gstreamer.Buffer;
import org.gstreamer.Caps;
import org.gstreamer.Element;
import org.gstreamer.ElementFactory;
import org.gstreamer.Fraction;
import org.gstreamer.Pad;
import org.gstreamer.Pipeline;
import org.gstreamer.Structure;
import org.gstreamer.ValueList;
import org.gstreamer.elements.RGBDataAppSink;
import org.gstreamer.interfaces.Property;
import org.gstreamer.interfaces.PropertyProbe;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCSystem;
import cc.creativecomputing.core.CCSystem.CCOS;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.math.CCMath;

/**
 * 
 * Data type for storing and manipulating video frames from an attached capture device such as a camera. Use
 * <b>Capture.list()</b> to show the names of any attached devices. Using the version of the constructor without
 * <b>name</b> will attempt to use the last device used by a QuickTime program.
 * 
 * <h3>Advanced</h3> Class for storing and manipulating video frames from an attached capture device such as a camera.originally adapted from the Processing project - http://processing.org
 * @author Andres Colubri
 * @author christianr
 */
public class CCGStreamerCapture extends CCVideo{
	
	/**
	 * Utility class to store the resolution (width, height and fps) of a capture device.
	 * 
	 */
	public static class CCGStreamerCaptureResolution {
		public int width, height;
		public float fps;
		public String fpsString;

		public CCGStreamerCaptureResolution() {
			width = height = 0;
			fps = 0.0f;
			fpsString = "";
		}

		public CCGStreamerCaptureResolution(int width, int height, int fpsDenominator, int fpsNumerator) {
			this.width = width;
			this.height = height;
			this.fps = (float) fpsDenominator / (float) fpsNumerator;
			this.fpsString = fpsDenominator + "/" + fpsNumerator;
		}

		public CCGStreamerCaptureResolution(int width, int height, String fpsString) {
			this.width = width;
			this.height = height;

			String[] parts = fpsString.split("/");
			if (parts.length == 2) {
				int fpsDenominator = Integer.parseInt(parts[0]);
				int fpsNumerator = Integer.parseInt(parts[1]);

				this.fps = (float) fpsDenominator / (float) fpsNumerator;
				this.fpsString = fpsString;
			} else {
				this.fps = 0.0f;
				this.fpsString = "";
			}
		}

		public CCGStreamerCaptureResolution(CCGStreamerCaptureResolution source) {
			this.width = source.width;
			this.height = source.height;
			this.fps = source.fps;
			this.fpsString = source.fpsString;
		}

		public String toString() {
			return width + "x" + height + ", " + CCFormatUtil.nfc(fps, 2) + " fps (" + fpsString + ")";
		}
	}
	
	public static String CAPTURE_PLUGIN;
	public static String DEVICE_PROPERTY_NAME;
	public static String INDEX_PROPERTY_NAME;

	// Default gstreamer capture plugin for each platform, and property names.
	static {
		switch (CCSystem.os) {
		case MACOSX:
			CAPTURE_PLUGIN = "qtkitvideosrc";
			DEVICE_PROPERTY_NAME = "device-name";
			INDEX_PROPERTY_NAME = "device-index";
			break;
		case WINDOWS:
			CAPTURE_PLUGIN = "ksvideosrc";
			DEVICE_PROPERTY_NAME = "device-name";
			INDEX_PROPERTY_NAME = "device-index";
			break;
		case LINUX:
			CAPTURE_PLUGIN = "v4l2src";
			// The "device" property in v4l2src expects the device location (/dev/video0, etc).
			// v4l2src has "device-name", which requires the human-readable name... but how
			// to query in linux?.
			DEVICE_PROPERTY_NAME = "device";
			INDEX_PROPERTY_NAME = "device-fd";
			break;
		default:
		}
	}

	/**
	 * Gets a list of all available capture devices such as a camera. Use <b>print()</b> to write the information to the
	 * text window.
	 */
	static public String[] list() {
		return list(CAPTURE_PLUGIN);
	}

	/**
	 * <h3>Advanced</h3> Get a list of all available captures as a String array. i.e. println(Capture.list()) will show
	 * you the goodies.
	 * 
	 * @param sourceName String
	 */
	static public String[] list(String sourceName) {
		String[] res;
		try {
			res = list(sourceName, DEVICE_PROPERTY_NAME);
		} catch (IllegalArgumentException e) {
			if (CCSystem.os == CCOS.LINUX) {
				// Linux hack to detect currently connected cameras
				// by looking for device files named /dev/video0,
				// /dev/video1, etc.
				ArrayList<String> devices = new ArrayList<String>();
				String dir = "/dev";
				File libPath = new File(dir);
				String[] files = libPath.list();
				if (files != null) {
					for (int i = 0; i < files.length; i++) {
						if (-1 < files[i].indexOf("video")) {
							devices.add("/dev/" + files[i]);
						}
					}
				}
				res = new String[devices.size()];
				for (int i = 0; i < res.length; i++) {
					res[i] = devices.get(i);
				}
			} else {
				CCLog.error("The capture plugin doesn't support device query!");
				res = new String[0];
			}
		}
		return res;
	}

	static protected String[] list(String sourceName, String propertyName) {
		CCGStreamer.init();
		String[] valuesListing = new String[0];
		Element videoSource = ElementFactory.make(sourceName, "Source");
		PropertyProbe probe = PropertyProbe.wrap(videoSource);
		if (probe != null) {
			Property property = probe.getProperty(propertyName);
			if (property != null) {
				Object[] values = probe.getValues(property);
				if (values != null) {
					valuesListing = new String[values.length];
					for (int i = 0; i < values.length; i++)
						if (values[i] instanceof String)
							valuesListing[i] = (String) values[i];
				}
			}
		}
		return valuesListing;
	}

	private class CCGStreamerCaptureListener implements RGBDataAppSink.Listener{

		public void rgbFrame(int theWidth, int theHeight, IntBuffer theBuffer) {
			_myWidth = theWidth;
			_myHeight = theHeight;
			buffer(theBuffer);
			_myIsDataUpdated = true;
		}
	}
	
//	private static

	private String _mySource;

	private boolean _myIsCapturing = false;

	private String fps;

	private Pipeline _myPipeline;
	private Element _myGSource;

	private boolean _myIsPipelineReady;
	private boolean _myIsDataUpdated = false;

	private RGBDataAppSink _mySink = null;

	private ArrayList<CCGStreamerCaptureResolution> _myResolutions;

	private int _myRequestWidth;
	private int _myRequestHeight;

	/**
	 * @param theAnimator typically use "this"
	 * @param theRequestWidth width of the frame
	 * @param theRequestHeight height of the frame
	 */
	public CCGStreamerCapture(CCAnimator theAnimator, int theRequestWidth, int theRequestHeight) {
		super(theAnimator);
		initGStreamer(theAnimator, theRequestWidth, theRequestHeight, CAPTURE_PLUGIN, null, "");
	}

	/**
	 * <h3>Advanced</h3> Constructor that takes resolution and framerate.
	 * 
	 * @param theFrameRate number of frames to read per second
	 */
	public CCGStreamerCapture(CCAnimator theAnimator, int theRequestWidth, int theRequestHeight, int theFrameRate) {
		super(theAnimator);
		initGStreamer(theAnimator, theRequestWidth, theRequestHeight, CAPTURE_PLUGIN, null, theFrameRate + "/1");
	}

	/**
	 * <h3>Advanced</h3> This constructor allows to specify resolution and camera name.
	 * 
	 * @param theCameraName name of the camera
	 */
	public CCGStreamerCapture(CCAnimator theAnimator, int theRequestWidth, int theRequestHeight, String theCameraName) {
		super(theAnimator);
		
		HashMap<String, Object> properties = new HashMap<String, Object>();
		if (DEVICE_PROPERTY_NAME.equals("")) {
			// For plugins without device name property, the name is casted as an index
			properties.put(INDEX_PROPERTY_NAME, Integer.parseInt(theCameraName));
		} else {
			properties.put(DEVICE_PROPERTY_NAME, theCameraName);
		}
		initGStreamer(theAnimator, theRequestWidth, theRequestHeight, CAPTURE_PLUGIN, properties, "");
	}

	/**
	 * <h3>Advanced</h3> This constructor allows to specify the camera name and the desired frame rate, in addition to
	 * the resolution.
	 */
	public CCGStreamerCapture(CCAnimator theAnimator, int theRequestWidth, int theRequestHeight, String theCameraName, int theFrameRate) {
		super(theAnimator);
		HashMap<String, Object> properties = new HashMap<String, Object>();
		if (DEVICE_PROPERTY_NAME.equals("")) {
			// For plugins without device name property, the name is casted as an index
			properties.put(INDEX_PROPERTY_NAME, Integer.parseInt(theCameraName));
		} else {
			properties.put(DEVICE_PROPERTY_NAME, theCameraName);
		}
		initGStreamer(theAnimator, theRequestWidth, theRequestHeight, CAPTURE_PLUGIN, properties, theFrameRate + "/1");
	}

	/**
	 * <h3>Advanced</h3> This constructor allows to specify the source element, properties and desired framerate (in
	 * fraction form).
	 */
	public CCGStreamerCapture(
		CCAnimator theAnimator, 
		int theRequestWidth, int theRequestHeight, 
		String sourceName, HashMap<String, Object> properties, 
		String theFrameRate
	) {
		super(theAnimator);
		initGStreamer(theAnimator, theRequestWidth, theRequestHeight, sourceName, properties, theFrameRate);
	}
	

	// The main initialization here.
	private void initGStreamer(
		CCAnimator theAnimator, 
		int theRequestWidth, int theRequestHeight, 
		String theSource, 
		HashMap<String, Object> properties,
		String theFrameRate
	) {

		CCGStreamer.init();

		// register methods
//
//		if (ByteOrder.nativeOrder() == ByteOrder.LITTLE_ENDIAN) {
//			_myCopyMask = "red_mask=(int)0xFF000000, green_mask=(int)0xFF0000, blue_mask=(int)0xFF00";
//		} else {
//			_myCopyMask = "red_mask=(int)0xFF, green_mask=(int)0xFF00, blue_mask=(int)0xFF0000";
//		}

		_myPipeline = new Pipeline("GSCapture");

		_mySource = theSource;

		fps = theFrameRate;
		_myRequestWidth = theRequestWidth;
		_myRequestHeight = theRequestHeight;

		_myGSource = ElementFactory.make(theSource, "Source");

		if (properties != null) {
			for(String name:properties.keySet()) {
				Object value = properties.get(name);
				_myGSource.set(name, value);
			}
		}

		_myWidth = _myHeight = 0;
		_myIsPipelineReady = false;
	}

	private void initPipeline() {
		String fpsStr = "";
		if (!fps.equals("")) {
			// If the framerate string is empty we left the source element
			// to use the default value.
			fpsStr = ", framerate=" + fps;
		}

		Element myColorConverter = ElementFactory.make("ffmpegcolorspace", "ColorConverter");
		Element myColorFilter = ElementFactory.make("capsfilter", "ColorFilter");
		myColorFilter.setCaps(
			new Caps(
				"video/x-raw-rgb, " +
				"width=" + _myRequestWidth + ", " +
				"height=" + _myRequestHeight + ", " +
				"bpp=32, " +
				"depth=24" + 
				fpsStr
			)
		);

		_mySink = new RGBDataAppSink("rgb", new CCGStreamerCaptureListener());
		// Setting direct buffer passing in the video sink, so no new buffers are created
		// and disposed by the GC on each frame (thanks to Octavi Estape for pointing
		// out this one).
		_mySink.setPassDirectBuffer(CCGStreamer.passDirectBuffer);

		// No need for rgbSink.dispose(), because the addMany() doesn't increment the
		// refcount of the videoSink object.

		_myPipeline.addMany(_myGSource, myColorConverter, myColorFilter, _mySink);
		Element.linkMany(_myGSource, myColorConverter, myColorFilter, _mySink);

		_myIsPipelineReady = true;
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGBA;
		_myPixelFormat = CCPixelFormat.BGRA;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;

		_myIsDataCompressed = false;
		_myMustFlipVertically = true;

		_myIsDataUpdated = false;
		_myIsFirstFrame = true;
	}
  
	/**
	 * Releases the gstreamer resources associated to this capture object. It shouldn't be used after this.
	 */
	public void delete() {
		if (_myPipeline != null) {
			try {
				if (_myPipeline.isPlaying()) {
					_myPipeline.stop();
				}
			} catch (IllegalStateException e) {
				CCLog.error("error when deleting player, maybe some native resource is already disposed");
			} catch (Exception e) {
				e.printStackTrace();
			}

			if (_mySink != null) {
				_mySink.removeListener();
				_mySink.dispose();
				_mySink = null;
			}

			_myPipeline.dispose();
			_myPipeline = null;
		}
	}

	/**
	 * Same as delete.
	 */
	public void dispose() {
		delete();
	}   
	
	public void post() {}

	@Override
	public void update(CCAnimator theAnimator) {
		if (_myIsDataUpdated) {
			_myIsDataUpdated = false;
			if (_myIsFirstFrame) {
				_myIsFirstFrame = false;
				_myListener.proxy().onInit(this);
			} else {
				_myListener.proxy().onUpdate(this);
			}
		}
	}

	/**
	 * Finalizer of the class.
	 */
	protected void finalize() throws Throwable {
		try {
			delete();
		} finally {
			super.finalize();
		}
	} 
    
	/**
	 * Returns true if the capture device is already producing frames.
	 * 
	 * @return boolean
	 */
	public boolean ready() {
		return _myWidth > 0 && _myHeight > 0 && _myIsPipelineReady;
	}

	/**
	 * Returns whether the device is capturing frames or not.
	 * 
	 * @return boolean
	 */
	public boolean isCapturing() {
		return _myIsCapturing;
	}

	/**
	 * Starts capturing frames from the selected device.
	 */
	public void start() {
		boolean init = false;
		if (!_myIsPipelineReady) {
			initPipeline();
			init = true;
		}

		_myIsCapturing = true;
		_myPipeline.play();

		if (init) {
			// Resolution and FPS initialization needs to be done after the
			// pipeline is set to play.
			initResolutions();
		}
	}

	/**
	 * Stops capturing frames from an attached device.
	 */
	public void stop() {
		if (!_myIsPipelineReady) {
			initPipeline();
		}

		_myIsCapturing = false;
		_myPipeline.stop();
	} 

	/**
	 * <h3>Advanced</h3> Returns the name of the source element used for capture.
	 * 
	 * @return String
	 */
	public String source() {
		return _mySource;
	}
  
	/**
	 * <h3>Advanced</h3> Returns a list with the resolutions supported by the capture device, including width, height
	 * and frame rate.
	 * 
	 * @return Resolution[]
	 */
	public CCGStreamerCaptureResolution[] resolutions() {
		CCGStreamerCaptureResolution[] res;

		if (_myResolutions == null) {
			res = new CCGStreamerCaptureResolution[0];
		} else {
			int n = _myResolutions.size();
			res = new CCGStreamerCaptureResolution[n];
			for (int i = 0; i < n; i++) {
				res[i] = new CCGStreamerCaptureResolution(_myResolutions.get(i));
			}
		}

		return res;
	}

	/**
	 * <h3>Advanced</h3> Prints all the gstreamer elements currently used in the current pipeline instance.
	 * 
	 */
	public void printElements() {
		List<Element> list = _myPipeline.getElementsRecursive();
		System.out.println(list);
		for (Element element : list) {
			System.out.println(element.toString());
		}
	}
	
	private void initResolutions() {
		_myResolutions = new ArrayList<CCGStreamerCaptureResolution>();

		for (Element src : _myPipeline.getSources()) {
			for (Pad pad : src.getPads()) {

				Caps caps = pad.getCaps();
				int n = caps.size();
				for (int i = 0; i < n; i++) {
					Structure myStructure = caps.getStructure(i);

					if (!myStructure.hasIntField("width") || !myStructure.hasIntField("height"))
						continue;

					int w = ((Integer) myStructure.getValue("width")).intValue();
					int h = ((Integer) myStructure.getValue("height")).intValue();

					if (CCSystem.os == CCOS.WINDOWS) {
						// In Windows the getValueList() method doesn't seem to
						// return a valid list of fraction values, so working on
						// the string representation of the caps structure.
						getFpsFromString(myStructure.toString(), w, h);
					} else {
						fpsFromStructure(myStructure, w, h);
					}
				}
			}
		}
		
		boolean suppRes = false;
		for (int i = 0; i < _myResolutions.size(); i++) {
			CCGStreamerCaptureResolution res = _myResolutions.get(i);
			if (_myRequestWidth == res.width && _myRequestHeight == res.height && fps.equals("") || fps.equals(res.fpsString)) {
				suppRes = true;
				break;
			}
		}

		if (!suppRes) {
			String fpsStr = "";
			if (!fps.equals("")) {
				fpsStr = ", " + fps + "fps";
			}
			CCLog.error("The requested resolution of " + _myRequestWidth + "x" + _myRequestHeight + fpsStr + " is not supported by selected the capture device.");
			CCLog.error("Use one of the following resolutions instead:");
			for (int i = 0; i < _myResolutions.size(); i++) {
				CCGStreamerCaptureResolution res = _myResolutions.get(i);
				CCLog.error(res.toString());
			}
		}
	}

	private void getFpsFromString(String str, int w, int h) {
		int n0 = str.indexOf("framerate=(fraction)");
		if (-1 < n0) {
			String temp = str.substring(n0 + 20, str.length());
			int n1 = temp.indexOf("[");
			int n2 = temp.indexOf("]");
			if (-1 < n1 && -1 < n2) {
				// A list of fractions enclosed between '[' and ']'
				temp = temp.substring(n1 + 1, n2);
				String[] fractions = temp.split(",");
				for (int k = 0; k < fractions.length; k++) {
					String fpsStr = fractions[k].trim();
					_myResolutions.add(new CCGStreamerCaptureResolution(w, h, fpsStr));
				}
			} else {
				// A single fraction
				int n3 = temp.indexOf(",");
				int n4 = temp.indexOf(";");
				if (-1 < n3 || -1 < n4) {
					int n5 = -1;
					if (n3 == -1) {
						n5 = n4;
					} else if (n4 == -1) {
						n5 = n3;
					} else {
						n5 = CCMath.min(n3, n4);
					}

					temp = temp.substring(0, n5);
					String fpsStr = temp.trim();
					_myResolutions.add(new CCGStreamerCaptureResolution(w, h, fpsStr));
				}
			}
		}
	}

	private void fpsFromStructure(Structure theStructure, int w, int h) {
		boolean singleFrac = false;
		try {
			Fraction fr = theStructure.getFraction("framerate");
			_myResolutions.add(new CCGStreamerCaptureResolution(w, h, fr.numerator, fr.denominator));
			singleFrac = true;
		} catch (Exception e) {
		}

		if (singleFrac)return;
		
		ValueList flist = null;

		try {
			flist = theStructure.getValueList("framerate");
		} catch (Exception e) {
		}
		
		if (flist != null) {
			// All the framerates are put together, but this is not
			// entirely accurate since there might be some of them'				
			// that work only for certain resolutions.
			for (int k = 0; k < flist.getSize(); k++) {
				Fraction fr = flist.getFraction(k);
				_myResolutions.add(new CCGStreamerCaptureResolution(w, h, fr.numerator, fr.denominator));
			}
		}
	}

	public synchronized void disposeBuffer(Object buf) {
		((Buffer) buf).dispose();
	}
}
