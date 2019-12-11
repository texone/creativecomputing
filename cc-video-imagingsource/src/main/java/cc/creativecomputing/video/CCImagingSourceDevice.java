package cc.creativecomputing.video;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimatorUpdateListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.video.CCISLibraryInterface.FRAME_READY_CALLBACK;

/** Simple example of JNA interface mapping and usage. */
public class CCImagingSourceDevice extends CCVideo{



	public static enum CCISFileType {
		BMP, JPEG
	}

	public static enum CCISColorFormat {
		Y800, RGB24, RGB32, UYVY, Y16, NONE, // used as return value
	}

	public static enum CCISVideoProperty {
		BRIGHTNESS, CONTRAST, HUE, SATURATION, SHARPNESS, GAMMA, COLORENABLE, WHITEBALANCE, BLACKLIGHTCOMPENSATION, GAIN
	}

	public static enum CCISCameraProperty {
		PAN, TILT, ROLL, ZOOM, EXPOSURE, IRIS, FOCUS
	}

	public static enum CCISFrameFilterParameterType {
		eParamLong, eParamBoolean, eParamFloat, eParamString, eParamData
	}

	public static enum CCISPropertyInterfaceType {
		RANGE, ABSOLUTE_VALUE, SWITCH, BUTTON, MAP_STRINGS, UNKNOWN
	}
	
	public static class CCISNoHandleException extends RuntimeException{

		public CCISNoHandleException() {
			super("Grabber is not a valid handle. GetGrabber was not called.");
		}
		
	}
	public static class CCISNoDeviceException extends RuntimeException{

		public CCISNoDeviceException() {
			super("No device opened. Open a device, before this function can be used.");
		}
		
	}
	
	public static class CCISNoPropertySetException extends RuntimeException{

		public CCISNoPropertySetException() {
			super("The property is not supported by the current device.");
		}
		
	}

	
	public static class CCISNotAvailableException extends RuntimeException{

		public CCISNotAvailableException() {
			super("The property is not supported by the current device.");
		}
		
	}
	
	
	public static class CCISNotInLiveModeException extends RuntimeException{

		public CCISNotInLiveModeException(String theMessage) {
			super(theMessage);
		}
		
	}
	
	private static CCISLibraryInterface instance = null;

	private static CCISLibraryInterface instance() {
		if (instance == null) {
			instance = (CCISLibraryInterface) Native.load("tisgrabber_x64", CCISLibraryInterface.class);
			instance.IC_InitLibrary(null);
		}
		return instance;
	}
	
	private static interface CCImagingSourceParameterChange{
		public void change(long theValue);
	}
	
	private static interface CCImagingSourceAutoEnableChange{
		public void change(boolean theValue);
	}
	
	private static class CCImagingSourceParameter implements CCAnimatorUpdateListener{
		private double _myMin;
		private double _myMax;
		@CCProperty(name = "value", min = 0, max = 1)
		private double _myValue;
		private double _myLastValue = -1000;
		private CCImagingSourceParameterChange _myChanger;
		
		public CCImagingSourceParameter(double theMin, double theMax, double theValue, CCImagingSourceParameterChange theChanger) {
			_myMin = theMin;
			_myMax = theMax;
			_myValue = CCMath.norm(theValue, theMin, theMax);
			_myChanger = theChanger;
		}
		
		public void update(CCAnimator theAnimator) {
			if(_myValue == _myLastValue)return;
			_myLastValue = _myValue;
			_myChanger.change((long)CCMath.blend(_myMin, _myMax, _myValue));
		}
	}
	
	private static class CCImagingSourceFlag implements CCAnimatorUpdateListener{
		@CCProperty(name = "value")
		private boolean _myValue;
		private boolean _myLastValue;
		private CCImagingSourceParameterChange _myChanger;
		
		public CCImagingSourceFlag(boolean theValue, CCImagingSourceParameterChange theChanger) {
			_myValue = theValue;
			_myLastValue = !_myValue;
			_myChanger = theChanger;
		}
		
		public void update(CCAnimator theAnimator) {
			if(_myValue == _myLastValue)return;
			_myLastValue = _myValue;
			_myChanger.change(_myValue ? 1 : 0);
		}
	}
	
	private static class CCImageSourceAutoParameter extends CCImagingSourceParameter{
		@CCProperty(name = "auto")
		private boolean _cSetAuto = false;
		private boolean _myLastSet;
		
		private CCImagingSourceAutoEnableChange _myEnableChanger;
		
		public CCImageSourceAutoParameter(double theMin, double theMax, double theValue, boolean theIsEnabled, CCImagingSourceParameterChange theChanger, CCImagingSourceAutoEnableChange theEnableChanger) {
			super(theMin, theMax, theValue, theChanger);
			_cSetAuto = theIsEnabled;
			_myLastSet = !_cSetAuto;
			_myEnableChanger = theEnableChanger;
		}
		
		@Override
		public void update(CCAnimator theAnimator) {
			super.update(theAnimator);
			if(_cSetAuto == _myLastSet)return;
			_myLastSet = _cSetAuto;
			_myEnableChanger.change(_cSetAuto);
		}
		
	}
	
	@CCProperty(name = "parameters", hide = true)
	private Map<String, CCAnimatorUpdateListener> _cParameterMap = new LinkedHashMap<>();
	
	@CCProperty(name = "fps", readBack = true)
	private float _cFPS = 0;
	
	@CCProperty(name = "white auto")
	private boolean _cWhiteAuto = false;
	
	@CCProperty(name = "white red")
	private int _cWhiteRed = 0;
	@CCProperty(name = "white green")
	private int _cWhiteGreen = 0;
	@CCProperty(name = "white blue")
	private int _cWhiteBlue = 0;
	

	/**
	 * Get the number of the currently available devices. This function creates an
	 * internal array of all connected video capture devices. With each call to this
	 * function, this array is rebuild. The name and the unique name can be
	 * retrieved from the internal array using the functions IC_GetDevice() and
	 * IC_GetUniqueNamefromList. They are useful for retrieving device names for
	 * opening devices.
	 * 
	 * @return >= 0 Success, count of found devices or IC_NO_HANDLE Internal Error.
	 * 
	 * @see #device
	 * @see #IC_GetUniqueNamefromList
	 */
	public static int deviceCount() {
		int myResult = instance().IC_GetDeviceCount();
		if (myResult == CCISLibraryInterface.IC_NO_HANDLE)
			throw new RuntimeException("no handle");
		return myResult;
	}

	/**
	 * Get the name of a video capture device.
	 * <p>
	 * Get a string representation of a device specified by theIndex. theIndex must be
	 * between 0 and IC_GetDeviceCount(). IC_GetDeviceCount() must have been called
	 * before this function, otherwise it will always fail.
	 * 
	 * @param theIndex The number of the device whose name is to be returned. It must
	 *               be in the range from 0 to IC_GetDeviceCount(),
	 * @return Returns the string representation of the device on success, NULL
	 *         otherwise.
	 * 
	 * @see #deviceCount
	 * @see #uniqueNamefromList
	 */
	public static String device(int theIndex) {
		return instance().IC_GetDevice(theIndex);
	}

	private final CCISLibraryInterface _myInterface;

	private  Pointer _myGrabberPointer;
	private  PointerByReference _myGrabberReference;
	
	private final Lock bufferLock = new ReentrantLock();
	private boolean _myIsDataUpdated = false;
	
	private static  ByteBuffer clone(ByteBuffer original) {
		ByteBuffer clone = ByteBuffer.allocate(original.capacity());
		original.rewind();//copy from the beginning
		clone.put(original);
		original.rewind();
		clone.flip();
		return clone;
	}

	public CCImagingSourceDevice(String theDevice, final CCAnimator theAnimator) {
		super(theAnimator);
		
		if(theDevice == null) {
			deviceCount();
			theDevice = device(0);
		}
		
		_myInterface = instance();
		_myGrabberPointer = _myInterface.IC_CreateGrabber();
		_myGrabberReference = new PointerByReference(_myGrabberPointer);
		
		_myPixelInternalFormat = CCPixelInternalFormat.RGB;
		_myPixelFormat = CCPixelFormat.BGR;
		_myPixelType = CCPixelType.UNSIGNED_BYTE;

		_myIsDataCompressed = false;
		_myMustFlipVertically = true;
		
		_myIsFirstFrame = true;
		
		CCLog.info(theDevice);
		CCLog.info(openVideoCaptureDevice(theDevice));
		
		
		
		frameReadyCallback((data,frame)->{
			if(!_myIsActive)return;
			_myWidth = videoFormatWidth();
			_myHeight = videoFormatHeight();
			
			if (!bufferLock.tryLock()) {
				return;
			}
			ByteBuffer myBuffer = data.getByteBuffer(0, _myWidth * _myHeight * 3);
			buffer(myBuffer);
			bufferLock.unlock();
			
			_myIsDataUpdated = true;
			
		});
		continuousMode(true);
		
		for(String myProperty:enumProperties()) {
			if(isPropertyAvailable(myProperty, "Value")) {
				CCVector2i myRange = propertyValueRange(myProperty, null);
				if(isPropertyAvailable(myProperty, "Auto")) {
					_cParameterMap.put(
						myProperty, 
						new CCImageSourceAutoParameter(
							myRange.x, 
							myRange.y, 
							propertyValue(myProperty, null), 
							propertyAutoEnabled(myProperty, null),
							v -> propertyValue(myProperty, null, (int)v), 
							v -> propertyAutoEnabled(myProperty, null, v)
						)
					);
				}else {
					_cParameterMap.put(
						myProperty, 
						new CCImagingSourceParameter(
							myRange.x, 
							myRange.y, 
							propertyValue(myProperty, null), 
							v -> propertyValue(myProperty, null, (int)v)
						)
					);
				}
			}else {
				if(isPropertyAvailable(myProperty, "Auto")) {
//					_cParameterMap.put(
//						myProperty, 
//						new CCImageSourceAutoParameter(
//							myRange.x, 
//							myRange.y, 
//							propertyValue(myProperty, null), 
//							propertyAutoEnabled(myProperty, null),
//							v -> propertyValue(myProperty, null, (int)v), 
//							v -> propertyAutoEnabled(myProperty, null, v)
//						)
//					);
				}else {
					_cParameterMap.put(
						myProperty, 
						new CCImagingSourceFlag(
							propertyValue(myProperty, null) == 1, 
							v -> propertyValue(myProperty, null, (int)v)
						)
					);
				}
			}
			
		}
	
//		for (CCISVideoProperty myProperty : CCISVideoProperty.values()) {
//			if(!isVideoPropertyAvailable(myProperty))continue;
//			
//			
//			CCVector2i myRange = videoPropertyGetRange(myProperty);
//			
//			if(isVideoPropertyAutoAvailable(myProperty)) {
//				_cParameterMap.put(
//					myProperty.name(), 
//					new CCImageSourceAutoParameter(
//						myRange.x, 
//						myRange.y, 
//						videoProperty(myProperty), 
//						isVideoPropertyAutoEnabled(myProperty), 
//						v -> videoProperty(myProperty, v), 
//						v -> enableAutoVideoProperty(myProperty, v)
//					)
//				);
//			}else {
//				_cParameterMap.put(
//					myProperty.name(), 
//					new CCImagingSourceParameter(
//						myRange.x, 
//						myRange.y, 
//						videoProperty(myProperty), 
//						v -> videoProperty(myProperty, v)
//					)
//				);
//			}
//		}
//		
//		
//		for (CCISCameraProperty myProperty : CCISCameraProperty.values()) {
//			if(!isCameraPropertyAvailable(myProperty))continue;
//			CCVector2i myRange = cameraPropertyGetRange(myProperty);
//			CCLog.info(myProperty,isCameraPropertyAutoAvailable(myProperty));
//			if(isCameraPropertyAutoAvailable(myProperty)) {
//				_cParameterMap.put(
//					myProperty.name(), 
//					new CCImageSourceAutoParameter(
//						myRange.x, 
//						myRange.y, 
//						cameraProperty(myProperty), 
//						isCameraPropertyAutoEnabled(myProperty), 
//						v -> cameraProperty(myProperty, v), 
//						v -> enableAutoCameraProperty(myProperty, v)
//					)
//				);
//			}else {
//				_cParameterMap.put(
//					myProperty.name(), 
//					new CCImagingSourceParameter(
//						myRange.x, 
//						myRange.y, 
//						cameraProperty(myProperty), 
//						v -> cameraProperty(myProperty, v)
//					)
//				);
//			}
//			
//		}
	}
	
	public CCImagingSourceDevice(final CCAnimator theAnimator) {
		this(null, theAnimator);
	}

	/**
	 * Open a video capture device. The hGrabber handle must have been created
	 * previously by a call to IC_CreateGrabber(). Once a hGrabber handle has been
	 * created it can be recycled to open different video capture devices in
	 * sequence.
	 * 
	 * @param szDeviceName Friendly name of the video capture device e.g.
	 *                     "DFK21F04".
	 * @return true on success or false on errors.
	 * @see #closeVideoCaptureDevice
	 */
	private boolean openVideoCaptureDevice(String szDeviceName) {
		return _myInterface.IC_OpenVideoCaptureDevice(_myGrabberPointer, szDeviceName) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Close the current video capture device.
	 */
	public void closeVideoCaptureDevice() {
		_myInterface.IC_CloseVideoCaptureDevice(_myGrabberPointer);
	}

	/**
	 * Returns the name of the current video capture Retrieve the name of the
	 * current video capture device. If the device is invalid, NULL is returned.
	 * 
	 * @return The name of the video capture device or NULL If no video capture
	 *         device is currently opened.
	 */
	public String deviceName() {
		return _myInterface.IC_GetDeviceName(_myGrabberPointer);
	}

	/**
	 * Returns the width of the video format.
	 * 
	 * @return width of the video format.
	 */
	public int videoFormatWidth() {
		return _myInterface.IC_GetVideoFormatWidth(_myGrabberPointer);
	}

	/**
	 * Returns the height of the video format.
	 * 
	 * @return height of the video format.
	 */
	public int videoFormatHeight() {
		return _myInterface.IC_GetVideoFormatHeight(_myGrabberPointer);
	}

	/**
	 * Sets the color format of the sink. Set the sink type. A sink type must be set
	 * before images can be snapped. The sink type basically describes the format of
	 * the buffer where the snapped images are stored.
	 * <p>
	 * Possible values for format are:
	 * <li>Y800
	 * <li>RGB24
	 * <li>RGB32
	 * <li>UYVY
	 * <p>
	 * The sink type may differ from the currently set video format.
	 * 
	 * @param format The desired color format. Possible values for format are:
	 *               <li>Y800
	 *               <li>RGB24
	 *               <li>RGB32
	 *               <li>UYVY
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 * 
	 * @note Please note that UYVY can only be used in conjunction with a UYVY video
	 *       format.
	 */
	public boolean format(CCISColorFormat format) {
		return _myInterface.IC_SetFormat(_myGrabberPointer, format.ordinal()) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Returns the current color format of the sink. Retrieves the format of the
	 * sink type currently set (See IC_SetFormat() for possible formats). If no sink
	 * type is set or an error occurred, NONE is returned. The function returns a
	 * valid value only after IC_PreprareLive() or IC_StartLive() was called. Before
	 * these calls, NONE is returned.
	 * 
	 * @return The current sink color format.
	 */
	public CCISColorFormat format() {
		return CCISColorFormat.values()[_myInterface.IC_GetFormat(_myGrabberPointer)];
	}

	/**
	 * Set a video format for the current video capture device. The video format
	 * must be supported by the current video capture device.
	 * 
	 * @param theFormat A string that contains the desired video format.
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 */
	public boolean videoFormat(String theFormat) {
		return _myInterface.IC_SetVideoFormat(_myGrabberPointer, theFormat) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Set a video norm for the current video capture device.
	 * 
	 * @note The current video capture device must support video norms.
	 * @param szNorm A string that contains the desired video format.
	 * @return<code>true</code> on success or <code>false</code> if something went
	 *                          wrong.
	 */
	public boolean videoNorm(String szNorm) {
		return _myInterface.IC_SetVideoNorm(_myGrabberPointer, szNorm) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Set a input channel for the current video capture device.
	 * 
	 * @note The current video capture device must support input channels..
	 * @param theChannel A string that contains the desired video format.
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 */
	public boolean inputChannel(String theChannel) {
		return _myInterface.IC_SetInputChannel(_myGrabberPointer, theChannel) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Start the live video.
	 * 
	 * @param theShow The parameter indicates <code>true</code> Show the video
	 *                <code>false</code> Do not show the video, but deliver frames.
	 *                (For callbacks etc.)
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 * @see #stopLive
	 */
	public boolean startLive(boolean theShow) {
		return _myInterface.IC_StartLive(_myGrabberPointer, theShow ? 1 : 0) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Prepare the grabber for starting the live video.
	 * 
	 * @param theShow The parameter indicates <code>true</code> Show the video
	 *                <code>false</code> Do not show the video, but deliver frames.
	 *                (For callbacks etc.)
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 */
	public boolean prepareLive(boolean theShow) {
		return _myInterface.IC_PrepareLive(_myGrabberPointer, theShow ? 1 : 0) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Suspends an image stream and puts it into prepared state.
	 * 
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 */
	public boolean suspendLive() {
		return _myInterface.IC_SuspendLive(_myGrabberPointer) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Check, whether the passed grabber already provides are live video
	 * 
	 * @return <code>true</code> if Live video is running <code>false</code> if Live
	 *         video is not running.
	 */
	public boolean isLive() {
		int myResult = _myInterface.IC_IsLive(_myGrabberPointer);
		switch (myResult) {
		case 1:
			return true;
		case 0:
			return false;
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNoDeviceException();
		}
		return true;
	}

	/**
	 * Stop the live video.
	 * 
	 * @see #startLive
	 */
	public void stopLive() {
		_myInterface.IC_StopLive(_myGrabberPointer);
	}
	
	public static interface CCISFrameReady{
		public void onFrame(Pointer theData, long theFrame);
	}
	
	private CCListenerManager<CCISFrameReady> _myFrameEvents = CCListenerManager.create(CCISFrameReady.class);
	
	private FRAME_READY_CALLBACK _myCallback;
	
	public void frameReadyCallback(CCISFrameReady theEvent) {
		if(_myCallback == null) {
			_myCallback = new FRAME_READY_CALLBACK() {

				@Override
				public void invoke(Pointer hGrabber, Pointer pData, long frameNumber, Pointer userdata) {
					_myFrameEvents.proxy().onFrame(pData, frameNumber);
				}
				
			};
			_myInterface.IC_SetFrameReadyCallback(_myGrabberPointer, _myCallback, null);
		}
		_myFrameEvents.add(theEvent);
	}
	
	/**
	 * Set Continuous mode
	 * 
	 * In continuous mode, the callback is called for each frame, so that there is
	 * no need to use IC_SnapImage etc.
	 * 
	 * @param <code>true</code> Snap continuous, <code>false</code> do not automatically snap.
	 * 
	 * @return <code>true</code> if the operation was successful
	 *         <code>false</code> otherwise
	 * 
	 * @remarks Not available in live mode.
	 * 
	 */
	public boolean continuousMode(boolean cont) {
		int myResult = _myInterface.IC_SetContinuousMode(_myGrabberPointer, cont ? 0 : 1);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_ERROR:
			return false;
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NOT_IN_LIVEMODE:
			throw new CCISNotInLiveModeException("The device is currently streaming, therefore setting continuous mode failed.");
		}
		return true;
	}

	/**
	 * Check, whether a property is supported by the current video capture device.
	 * 
	 * @see #CCImagingSourceCameraProperty The cammera property to be checked
	 * @return <code>true</code> if the property is available
	 *         <code>false</code> otherwise
	 * 
	 */
	public boolean isCameraPropertyAvailable(CCISCameraProperty theProperty) {
		int myResult = _myInterface.IC_IsCameraPropertyAvailable(_myGrabberPointer, theProperty.ordinal());
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_ERROR:
			return false;
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNoDeviceException();
		}
		return true;
	}

	/**
	 * Set a camera property like exposure, zoom.
	 * 
	 * @param  The property to be set. It can have following values:
	 *                  <li>PAN
	 *                  <li>TILT,
	 *                  <li>ROLL,
	 *                  <li>ZOOM,
	 *                  <li>EXPOSURE,
	 *                  <li>IRIS,
	 *                  <li>FOCUS
	 * @param theValue    The value the property is to be set to.
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 * 
	 * @note lValue should be in the range of the specified property. If the value
	 *       could not be set (out of range, auto is currently enabled), the
	 *       function returns 0. On success, the functions returns 1.
	 */
	public boolean cameraProperty(CCISCameraProperty theProperty, long theValue) {
		return _myInterface.IC_SetCameraProperty(_myGrabberPointer, theProperty.ordinal(), theValue) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Get the minimum and maximum value of a camera property
	 * 
	 * @param theProperty
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 */
	public CCVector2i cameraPropertyGetRange(CCISCameraProperty theProperty) {
		LongByReference lMin = new LongByReference();
		LongByReference lMax = new LongByReference();
		
		_myInterface.IC_CameraPropertyGetRange(_myGrabberPointer, theProperty.ordinal(), lMin, lMax);
		
		return new CCVector2i(
			(int)lMin.getValue(),
			(int)lMax.getValue()	
		);
	}

	/**
	 * Get a camera property's value.
	 * 
	 * @param theProperty
	 * @return value of the property.
	 */
	public long cameraProperty(CCISCameraProperty theProperty) {
		LongByReference myReference = new LongByReference();
		_myInterface.IC_GetCameraProperty(_myGrabberPointer, theProperty.ordinal(), myReference);
		return myReference.getValue();
	}

	/**
	 * Enable or disable automatic for a camera property.
	 * 
	 * @param theProperty The property to be set. It can have following values:
	 *                  <li>PAN
	 *                  <li>TILT,
	 *                  <li>ROLL,
	 *                  <li>ZOOM,
	 *                  <li>EXPOSURE,
	 *                  <li>IRIS,
	 *                  <li>FOCUS
	 * @param iOnOFF    Enables or disables the automation
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 * 
	 * @note If the property is not supported by the current video capture device or
	 *       automation of the property is not available with the current video
	 *       capture device, the function returns 0. On success, the function
	 *       returns 1.
	 */
	public boolean enableAutoCameraProperty(CCISCameraProperty theProperty, boolean theOnOff) {
		return _myInterface.IC_EnableAutoCameraProperty(_myGrabberPointer, theProperty.ordinal(), theOnOff ? 1 : 0) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Check whether automation for a camera property is available.
	 * 
	 * @param theProperty
	 */
	public boolean isCameraPropertyAutoAvailable(CCISCameraProperty theProperty) {
		return _myInterface.IC_IsCameraPropertyAutoAvailable(_myGrabberPointer, theProperty.ordinal()) == 1;
	}

	/**
	 * Retrieve whether automatic is enabled for the specified camera property.
	 * 
	 * @param theProperty
	 * @return
	 */
	public boolean isCameraPropertyAutoEnabled(CCISCameraProperty theProperty) {
		IntByReference myReference = new IntByReference();
		_myInterface.IC_GetAutoCameraProperty(_myGrabberPointer, theProperty.ordinal(), myReference);
		return myReference.getValue() == 1;
	}

	/**
	 * Check whether the specified video property is available.
	 * 
	 * @param theProperty
	 * @return <code>true</code> if property is available <code>false</code>
	 *         otherwise
	 */
	public boolean isVideoPropertyAvailable(CCISVideoProperty theProperty) {
		return _myInterface.IC_IsVideoPropertyAvailable(_myGrabberPointer, theProperty.ordinal()) == 1;
	}

	/**
	 * Retrieve the lower and upper limit of a video property.
	 * 
	 * @param theProperty
	 * @param lMin
	 * @param lMax
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 */
	public CCVector2i videoPropertyGetRange(CCISVideoProperty theProperty) {
		LongByReference lMin = new LongByReference();
		LongByReference lMax = new LongByReference();
		
		_myInterface.IC_VideoPropertyGetRange(_myGrabberPointer, theProperty.ordinal(), lMin, lMax);
		
		return new CCVector2i(
			(int)lMin.getValue(),
			(int)lMax.getValue()	
		);
	}

	/**
	 * Retrieve the the current value of the specified video property.
	 * 
	 * @param theProperty
	 * @return value of the property
	 */
	public long videoProperty(CCISVideoProperty theProperty) {
		LongByReference lValue = new LongByReference();
		_myInterface.IC_GetVideoProperty(_myGrabberPointer, theProperty.ordinal(), lValue);
		return lValue.getValue();
	}

	/**
	 * Check whether the specified video property supports automation.
	 * 
	 * @param theProperty
	 * @return
	 */
	public boolean isVideoPropertyAutoAvailable(CCISVideoProperty theProperty) {
		return _myInterface.IC_IsVideoPropertyAutoAvailable(_myGrabberPointer, theProperty.ordinal()) == 1;
	}

	/**
	 * Get the automation state of a video property.
	 * 
	 * @param theProperty
	 * @return
	 */
	public boolean isVideoPropertyAutoEnabled(CCISVideoProperty theProperty) {
		IntByReference theOnOff = new IntByReference();
		_myInterface.IC_GetAutoVideoProperty(_myGrabberPointer, theProperty.ordinal(), theOnOff);
		return theOnOff.getValue() == 1;
	}

	/**
	 * Set a video property like brightness, contrast.
	 * 
	 * @param theProperty The property to be set. It can have following values:
	 *                  <li>BRIGHTNESS ,
	 *                  <li>CONTRAST,
	 *                  <li>HUE,
	 *                  <li>SATURATION,
	 *                  <li>SHARPNESS,
	 *                  <li>GAMMA,
	 *                  <li>COLORENABLE,
	 *                  <li>WHITEBALANCE,
	 *                  <li>BLACKLIGHTCOMPENSATION,
	 *                  <li>GAIN
	 * @param lValue    The value the property is to be set to.
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 * 
	 * @note lValue should be in the range of the specified property. If the value
	 *       could not be set (out of range, auto is currently enabled), the
	 *       function returns 0. On success, the functions returns 1.
	 */
	public boolean videoProperty(CCISVideoProperty theProperty, long lValue) {
		return _myInterface.IC_SetVideoProperty(_myGrabberPointer, theProperty.ordinal(), lValue) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Enable or disable automatic for a video propertery.
	 * 
	 * @param theProperty The property to be set. It can have following values:
	 *                  <li>BRIGHTNESS,
	 *                  <li>CONTRAST,
	 *                  <li>HUE,
	 *                  <li>SATURATION,
	 *                  <li>SHARPNESS,
	 *                  <li>GAMMA,
	 *                  <li>COLORENABLE,
	 *                  <li>WHITEBALANCE,
	 *                  <li>BLACKLIGHTCOMPENSATION,
	 *                  <li>GAIN
	 * @param iOnOFF    Enables or disables the automation. Possible values ar
	 *                  <li>1 : Enable automatic
	 *                  <li>0 : Disable Automatic
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 * 
	 * @note If the property is not supported by the current video capture device or
	 *       automation of the property is not available with the current video
	 *       capture device, the function returns 0. On success, the function returns
	 *       1.
	 */
	public boolean enableAutoVideoProperty(CCISVideoProperty theProperty, boolean theOnOff) {
		return _myInterface.IC_EnableAutoVideoProperty(_myGrabberPointer, theProperty.ordinal(), theOnOff ? 1 : 0) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Retrieve the properties of the current video format and sink type
	 * 
	 * @param *lWidth This receives the width of the image buffer.
	 * @param *lHeight This receives the height of the image buffer.
	 * @param *iBitsPerPixel This receives the count of bits per pixel.
	 * @param *format This receives the current color format.
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 */
	public boolean imageDescription(LongByReference lWidth, LongByReference lHeight, IntByReference iBitsPerPixel, IntByReference format) {
		return _myInterface.IC_GetImageDescription(_myGrabberPointer, lWidth, lHeight, iBitsPerPixel, format) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Snaps an image. The video capture device must be set to live mode and a sink
	 * type has to be set before this call. The format of the snapped images depend
	 * on the selected sink type.
	 * 
	 * @param iTimeOutMillisek The Timeout time is passed in milli seconds. A value
	 *                         of -1 indicates, that no time out is set.
	 * 
	 * 
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 * 
	 * @see #startLive
	 * @see #format
	 * 
	 */
	public boolean snapImage(int iTimeOutMillisek) {
		switch (_myInterface.IC_SnapImage(_myGrabberPointer, iTimeOutMillisek)) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_ERROR:
			return false;
		case CCISLibraryInterface.IC_NOT_IN_LIVEMODE:
			throw new RuntimeException(" live video has not been started");
		}
		return true;
	}

	/**
	 * Save the contents of the last snapped image by IC_SnapImage into a file.
	 * 
	 * @param theFileName String containing the file name to be saved to.
	 * @param ft         File type if the image, It have be
	 *                   <li>FILETYPE_BMP for bitmap files
	 *                   <li>FILETYPE_JPEG for JPEG file.
	 * @param quality    If the JPEG format is used, the image quality must be
	 *                   specified in a range from 0 to 100.
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 * 
	 * @remarks The format of the saved images depend on the sink type. If the sink
	 *          type is set to Y800, the saved image will be an 8 Bit grayscale
	 *          image. In any other case the saved image will be a 24 Bit RGB image.
	 * 
	 * @note IC Imaging Control 1.41 only supports FILETYPE_BMP.
	 * @see #snapImage
	 * @see #format
	 */
	public int saveImage(String theFileName, CCISFileType ft, long quality) {
		return _myInterface.IC_SaveImage(_myGrabberPointer, theFileName, ft, quality);
	}

	/**
	 * Returns a pointer to the image data Retrieve a byte pointer to the image data
	 * (pixel data) of the last snapped image (see SnapImage()). If the function
	 * fails, the return value is NULL otherwise the value is a pointer to the first
	 * byte in the lowest image line (the image is saved bottom up!).
	 * 
	 * @return
	 *         <li>Nonnull Pointer to the image data
	 *         <li>NULL Indicates that an error occurred.
	 * @see #IC_SnapImage
	 * @see #IC_SetFormat
	 */
	// unsigned char* IC_GetImagePtr( PointerByReference hGrabber );

	/**
	 * Assign an Window handle to display the video in.
	 * 
	 * @param hWnd     The handle of the window where to display the live video in.
	 * @return
	 *         <li>IC_SUCCESS on success
	 *         <li>IC_ERROR if something went wrong.
	 */
	// public int IC_SetHWnd(PointerByReference hGrabber, __HWND hWnd);

	/**
	 * Return the serial number of the current device. Memory for the serial number
	 * must has been allocated by the application:
	 * 
	 * This function decodes the The Imaging Source serial numbers.
	 * 
	 * @param szSerial char array that receives the serial number.
	 * @return
	 *         <li>IC_SUCCESS The serial number could be retrieved.
	 *         <li>IC_IC_NOT_AVAILABLE The video capture device does not provide a
	 *         serial number.
	 *         <li>IC_NO_DEVICE No video capture device opened-
	 *         <li>IC_NO_HANDLE hGrabber is NULL.
	 */
	public int serialNumber(char[] szSerial) {
		return _myInterface.IC_GetSerialNumber(_myGrabberPointer, szSerial);
	}

	/**
	 * Count all connected video capture devices. If the Parameter szDeviceList is
	 * NULL, only the number of devices is queried. The Parameter szDeviceList must
	 * be a two dimensional array of char. The iSize parameter specifies the length
	 * of the strings, that are used in the array.
	 * 
	 * @param szDeviceList A two dimensional char array that receives the list. Or
	 *                     NULL if only the count of devices is to be returned.
	 * @param iSize        Not used.
	 * @return <code>true</code> on success or <code>false</code> if something went
	 *         wrong.
	 */
	public boolean listDevices(char[][] szDeviceList, int iSize) {
		return _myInterface.IC_ListDevices(szDeviceList, iSize) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Simpler approach of enumerating devices. No 2D char array needed
	 * 
	 * @param szDeviceName Char memory, that receives the device name @param iSize
	 *                     Size of the char memory. If names are longer, they will
	 *                     be truncated. @param DeviceIndex Index of the device to
	 *                     be query. Must be between 0 and IC_GetDeviceCount.
	 * 
	 * @return
	 *         <li>>= 0 Success, count of found devices
	 *         <li><0 An error occurred.
	 * 
	 */
	public int devicesbyIndex(String szDeviceName, int iSize, int DeviceIndex) {
		return _myInterface.IC_ListDevicesbyIndex(szDeviceName, iSize, DeviceIndex);
	}

	/**
	 * Count all available video formats. If the Parameter szFormatList is NULL,
	 * only the number of formats is queried. The Parameter szFormatList must be a
	 * two dimensional array of char. The iSize parameter specifies the length of
	 * the strings, that are used in the array to store the format names.
	 * 
	 * @param szFormatList A two dimensional char array that receives the list. Or
	 *                     NULL if only the count of formats is to be returned.
	 * 
	 * @return
	 *         <li>>= 0 Success, count of found video formats
	 *         <li><0 An error occurred.
	 */
	public boolean listVideoFormats(String szFormatList, int iSize) {
		return _myInterface.IC_ListVideoFormats(_myGrabberPointer, szFormatList,
				iSize) == CCISLibraryInterface.IC_SUCCESS;
	}

	/**
	 * Simpler approach of enumerating video formats. No 2D char array needed.
	 * 
	 * @param szFormatName char memory, that will receive the name of the video
	 *                     format. Should be big enough.
	 * @param iSize        Size in byte of szFormatName
	 * @theIndex Index of the video format to query.
	 * 
	 * @param szDeviceName Char memory, that receives the device name @param iSize
	 *                     Size of the char memory. If names are longer, they will
	 *                     be truncated. @param DeviceIndex Index of the device to
	 *                     be query. Must be between 0 and IC_GetDeviceCount.
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success,
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE No handle to the grabber object.
	 * 
	 */
	public int listVideoFormatbyIndex(String szFormatName, int iSize, int theIndex) {
		return _myInterface.IC_ListVideoFormatbyIndex(_myGrabberPointer, szFormatName, iSize, theIndex);
	}

	/**
	 * Get unique device name of a device specified by theIndex. The unique device
	 * name consist from the device name and its serial number. It allows to differ
	 * between more then one device of the same type connected to the computer. The
	 * unique device name is passed to the function IC_OpenDevByUniqueName
	 * 
	 * @param theIndex The number of the device whose name is to be returned. It must
	 *               be in the range from 0 to IC_GetDeviceCount(),
	 * @return Returns the string representation of the device on success, NULL
	 *         otherwise.
	 * 
	 * @see #IC_GetDeviceCount
	 * @see #IC_GetUniqueNamefromList
	 * @see #IC_OpenDevByUniqueName
	 */
	public int uniqueNamefromList(int theIndex) {
		return _myInterface.IC_GetUniqueNamefromList(theIndex);
	}

	/**
	 * Get the number of the available input channels for the current device. A
	 * video capture device must have been opened before this call.
	 * 
	 * @return
	 *         <li>>= 0 Success
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE No handle to the grabber object.
	 * 
	 * @see #IC_GetInputChannel
	 */
	public int inputChannelCount() {
		return _myInterface.IC_GetInputChannelCount(_myGrabberPointer);
	}

	/**
	 * Get a string representation of the input channel specified by theIndex. theIndex
	 * must be between 0 and IC_GetInputChannelCount(). IC_GetInputChannelCount()
	 * must have been called before this function, otherwise it will always fail.
	 * 
	 * @param theIndex Number of the input channel to be used..
	 * 
	 * @return
	 *         <li>Nonnull The name of the specified input channel
	 *         <li>NULL An error occured.
	 * @see #IC_GetInputChannelCount
	 */
	public String inputChannel(int theIndex) {
		return _myInterface.IC_GetInputChannel(_myGrabberPointer, theIndex);
	}

	/**
	 * Get the number of the available video norms for the current device. A video
	 * capture device must have been opened before this call.
	 * 
	 * @return
	 *         <li>>= 0 Success
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE No handle to the grabber object.
	 * 
	 * @see #IC_GetVideoNorm
	 */
	public int videoNormCount() {
		return _myInterface.IC_GetVideoNormCount(_myGrabberPointer);
	}

	/**
	 * Get a string representation of the video norm specified by theIndex. theIndex
	 * must be between 0 and IC_GetVideoNormCount(). IC_GetVideoNormCount() must
	 * have been called before this function, otherwise it will always fail.
	 * 
	 * @param theIndex Number of the video norm to be used.
	 * 
	 * @return
	 *         <li>Nonnull The name of the specified video norm.
	 *         <li>NULL An error occured.
	 * @see #videoNormCount
	 * 
	 */
	public String videoNorm(int theIndex) {
		return _myInterface.IC_GetVideoNorm(_myGrabberPointer, theIndex);
	}

	/**
	 * Get the number of the available video formats for the current device. A video
	 * capture device must have been opened before this call.
	 * 
	 * @return
	 *         <li>>= 0 Success
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE No handle to the grabber object.
	 * 
	 * @see #videoFormat
	 */
	public int videoFormatCount() {
		return _myInterface.IC_GetVideoFormatCount(_myGrabberPointer);
	}

	/**
	 * Return the name of a video format. Get a string representation of the video
	 * format specified by theIndex. theIndex must be between 0 and
	 * IC_GetVideoFormatCount(). IC_GetVideoFormatCount() must have been called
	 * before this function, otherwise it will always fail.
	 * 
	 * @param theIndex Number of the video format to be used.
	 * 
	 * @return
	 *         <li>Nonnull The name of the specified video format.
	 *         <li>NULL An error occured.
	 * @see #IC_GetVideoFormatCount
	 */
	public String videoFormat(int theIndex) {
		return _myInterface.IC_GetVideoFormat(_myGrabberPointer, theIndex);
	}

	/**
	 * Save the state of a video capture device to a file.
	 * 
	 * @param theFileName Name of the file where to save to.
	 * 
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 * 
	 * @see #loadDeviceStateFromFile
	 */
	public int saveDeviceStateToFile(String theFileName) {
		return _myInterface.IC_SaveDeviceStateToFile(_myGrabberPointer, theFileName);
	}

	/**
	 * Load a device settings file. On success the device is opened automatically.
	 * 
	 * @param theFileName Name of the file where to save to.
	 * 
	 * @return HGRABBER The handle of the grabber object, that contains the new
	 *         opened video capture device.
	 * 
	 * @see #saveDeviceStateToFile
	 */
	public PointerByReference loadDeviceStateFromFile(String theFileName) {
		return _myInterface.IC_LoadDeviceStateFromFile(_myGrabberPointer, theFileName);
	}

	/**
	 * Load a device settings file.
	 * 
	 * @param theFileName Name of the file where to save to.
	 * @param OpenDevice If 1, the device specified in the XML file is opened. If 0,
	 *                   then a device must be opened in the hGrabber. The
	 *                   properties and video format specified in the XML file will
	 *                   be applied to the opened device.
	 * 
	 * @return
	 *         <li>IC_SUCCESS The device was successfully opened and the settings
	 *         saved in the XML file were set.
	 *         <li>IC_NO_DEVICE False was passed to OpenDevice, but no device was
	 *         opened in the grabber handle or the handle is NULL
	 *         <li>IC_WRONG_XML_FORMAT No device opened.
	 *         <li>IC_WRONG_INCOMPATIBLE_XML No device opened.
	 *         <li>IC_DEVICE_NOT_FOUND No device opened.
	 *         <li>IC_FILE_NOT_FOUND Passed XML file does not exist.
	 *         <li>IC_NOT_ALL_PROPERTIES_RESTORED The device was opened, but not all
	 *         properties could be set as wanted.
	 * 
	 * @see #saveDeviceStateToFile
	 */
	public int loadDeviceStateFromFileEx(String theFileName, int OpenDevice) {
		return _myInterface.IC_LoadDeviceStateFromFileEx(_myGrabberPointer, theFileName, OpenDevice);
	}

	/**
	 * Open a video capture by using its DisplayName.
	 * 
	 * @param theDisplayName Displayname of the device. Can be retrieved by a call to
	 *                      IC_GetDisplayName().
	 * 
	 * 
	 * @return
	 *         <li>IC_SUCCESS on success
	 *         <li>IC_ERROR if something went wrong.
	 * 
	 * @see #displayName
	 */
	public int openDevByDisplayName(String theDisplayName) {
		return _myInterface.IC_OpenDevByDisplayName(_myGrabberPointer, theDisplayName);
	}

	/**
	 * Get a DisplayName from a currently open device. The display name of a device
	 * can be another on different computer for the same video capture device.
	 * 
	 * @param szDisplayName Memory that will take the display name. If it is NULL,
	 *                      the length of the display name will be returned.
	 * @param iLen          Size in Bytes of the memory allocated by szDisplayName.
	 * 
	 * 
	 * @return
	 *         <li>IC_SUCCESS On success. szDisplayName contains the display name of
	 *         the device.
	 *         <li>IC_ERROR iLen is less than the length of the retrieved display
	 *         name.
	 *         <li>IC_NO_HANDLE hGrabber is not a valid handle. GetGrabber was not
	 *         called.
	 *         <li>IC_NO_DEVICE No device opened. Open a device, before this
	 *         function can be used.
	 *         <li>>1 Length of the display name, if szDisplayName is NULL.
	 * 
	 * @see #IC_OpenDevByDisplayName
	 * 
	 */
	public int displayName(String theDisplayName, int iLen) {
		return _myInterface.IC_GetDisplayName(_myGrabberPointer, theDisplayName, iLen);
	}

	/**
	 * Open a video capture by using its UniqueName. Use IC_GetUniqueName() to
	 * retrieve the unique name of a camera.
	 * 
	 * @param szDisplayName Memory that will take the display name.
	 * 
	 * @see #IC_GetUniqueName
	 * @see #IC_ReleaseGrabber
	 * 
	 */
	public int openDevByUniqueName(String theDisplayName) {
		return _myInterface.IC_OpenDevByUniqueName(_myGrabberPointer, theDisplayName);
	}

	/**
	 * Get a UniqueName from a currently open device.
	 * 
	 * @param szUniqueName Memory that will take the Unique name. If it is NULL, the
	 *                     length of the Unique name will be returned.
	 * @param iLen         Size in Bytes of the memory allocated by szUniqueName.
	 * 
	 * 
	 * @return
	 *         <li>IC_SUCCESS On success. szUniqueName contains the Unique name of
	 *         the device.
	 *         <li>IC_ERROR iLen is less than the length of the retrieved Unique
	 *         name.
	 *         <li>IC_NO_HANDLE hGrabber is not a valid handle. GetGrabber was not
	 *         called.
	 *         <li>IC_NO_DEVICE No device opened. Open a device, before this
	 *         function can be used.
	 *         <li>>1 Length of the Unique name, if szUniqueName is NULL.
	 * 
	 */
	public int IC_GetUniqueName(String szUniquename, int iLen) {
		return _myInterface.IC_GetUniqueName(_myGrabberPointer, szUniquename, iLen);
	}

	/**
	 * This returns 1, if a valid device has been opened, otherwise it is 0.
	 * 
	 * @retval IC_ERROR There is no valid video capture device opened
	 * @retval IC_SUCCESS There is a valid video capture device openend.
	 */
	public int isDevValid() {
		return _myInterface.IC_IsDevValid(_myGrabberPointer);
	}

	/**
	 * Show the VCDProperty dialog.
	 * 
	 * @return
	 *         <li>IC_SUCCESS on success or IC_ERROR if something went wrong.
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE Nullpointer.
	 */
	public int showPropertyDialog() {
		return _myInterface.IC_ShowPropertyDialog(_myGrabberPointer);
	}

	/**
	 * Show the device selection dialog. This dialogs enables to select the video
	 * capture device, the video norm, video format, input channel and frame rate.
	 * 
	 * @return The passed hGrabber object or a new created if hGrabber was NULL.
	 */
	public PointerByReference showDeviceSelectionDialog() {
		return _myInterface.IC_ShowDeviceSelectionDialog(_myGrabberPointer);
	}

	/**
	 * Return whether the current video capture device supports an external trigger.
	 * 
	 * @return
	 *         <li>IC_SUCCESS An external trigger is supported
	 *         <li>IC_ERROR No external trigger is supported.
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE Internal Grabber does not exist.
	 * 
	 * @see #IC_EnableTrigger
	 */
	public int isTriggerAvailable() {
		return _myInterface.IC_IsTriggerAvailable(_myGrabberPointer);
	}

	/**
	 * Enable or disable the external trigger.
	 * 
	 * @param iEnable 1 = enable the trigger, 0 = disable the trigger
	 * 
	 * @return
	 *         <li>IC_SUCCESS Trigger was enabled or disabled successfully.
	 *         <li>IC_NOT_AVAILABLE The device does not support triggering.
	 *         <li>IC_NO_PROPERTYSET Failed to query the property set of the device.
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 * 
	 *         <li>IC_NO_HANDLE Internal Grabber does not exist or hGrabber is NULL.
	 * 
	 * @see #IC_IsTriggerAvailable
	 */
	public int enableTrigger(int iEnable) {
		return _myInterface.IC_EnableTrigger(_myGrabberPointer, iEnable);
	}

	/**
	 * Remove or insert the the overlay bitmap to the grabber object. If Y16 format
	 * is used, the overlay must be removed,
	 * 
	 * @param iEnable = 1 inserts overlay, 0 removes the overlay.
	 */
	public void removeOverlay(int iEnable) {
		_myInterface.IC_RemoveOverlay(_myGrabberPointer, iEnable);
	}

	/**
	 * Enable or disable the overlay bitmap on the live video
	 * 
	 * @param iEnable  = 1 enables the overlay, 0 disables the overlay.
	 */
	public void enableOverlay(int iEnable) {
		_myInterface.IC_EnableOverlay(_myGrabberPointer, iEnable);
	}
	
	/**
	 * Gets the current value of Color enhancement property
	 * 
	 * @return <code>true</code> if available otherwise <code>false</code>
	 */
	public boolean isColorEnhancementSupported() {
		IntByReference OnOff = new IntByReference();
		int myResult = _myInterface.IC_GetColorEnhancement(_myGrabberPointer, OnOff);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return OnOff.getValue() == 1;
		case CCISLibraryInterface.IC_NOT_AVAILABLE:
			throw new CCISNotAvailableException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}

	/**
	 * Sets the value of Color enhancement property Sample:
	 * 
	 * @param OnOff
	 * 
	 * @return <code>true</code> if successful <code>false</code> otherwise
	 */
	public boolean colorEnhancement(boolean OnOff) {
		int myResult = _myInterface.IC_SetColorEnhancement(_myGrabberPointer, OnOff ? 1: 0);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_NOT_AVAILABLE:
			throw new CCISNotAvailableException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}

	/**
	 * Sends a software trigger to the camera. The camera must support external
	 * trigger. The external trigger has to be enabled previously
	 * 
	 * @return <code>true</code> if successful <code>false</code> otherwise
	 * 
	 * @see #enableTrigger
	 * 
	 */
	public boolean softwareTrigger() {
		int myResult = _myInterface.IC_SoftwareTrigger(_myGrabberPointer);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_NOT_AVAILABLE:
			throw new CCISNotAvailableException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}

	/**
	 * Sets a new frame rate.
	 * 
	 * @param FrameRate The new frame rate.
	 * @return <code>true</code> if successful <code>false</code> otherwise
	 */
	public boolean frameRate(float FrameRate) {
		int myResult = _myInterface.IC_SetFrameRate(_myGrabberPointer, FrameRate);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		case CCISLibraryInterface.IC_NOT_IN_LIVEMODE:
			throw new CCISNotInLiveModeException("Frame rate can not set, while live video is shown or feature is not available. Stop Live video first!");
		}
		return false;
	}

	/**
	 * Retrieves the current frame rate
	 * 
	 * @return The current frame rate. If it is 0.0, then frame rates are not supported.
	 */
	public float frameRate() {
		return _myInterface.IC_GetFrameRate(_myGrabberPointer);
	}

	/**
	 * Retrieves available frame rates. The count of available frame rates depends
	 * on the used video capture device and the currently used video format. After a
	 * video was changed, the available frame rates usually are changed by the video
	 * capture device too.
	 * 
	 * @param Index    Index of the frame rates, starting at 0
	 * @param fps      Pointer to a float variable, that will receive the frame rate
	 *                 of the passed index.
	 * @return IC_SUCCESS, if the frame rate at Index exists, otherwise IC_ERROR,
	 */
	public List<Float> frameRates() {
	
		int Index = 0;
		FloatByReference fps = new FloatByReference();
		List<Float> myResult = new ArrayList<>();

		while( _myInterface.IC_GetAvailableFrameRates(_myGrabberPointer, Index, fps ) == CCISLibraryInterface.IC_SUCCESS )
		{
			myResult.add(fps.getValue());
			Index++;
		}
		return myResult;
	}
	
	private int _myLastOnOff = -1;

	public boolean whiteBalanceAuto(boolean iOnOff) {
		int myOnOff = iOnOff ? 1: 0;
		if(myOnOff == _myLastOnOff)return true;
		_myLastOnOff = myOnOff;
		int myResult = _myInterface.IC_SetWhiteBalanceAuto(_myGrabberPointer, myOnOff);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_NOT_AVAILABLE:
			throw new CCISNotAvailableException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}
	
	private long _myLastRed = -1;

	/**
	 * Sets the value for white balance red.
	 * 
	 * @param theValue value of the red white balance to be set
	 * @return <code>true</code> if successful <code>false</code> otherwise
	 * 
	 */
	public boolean whiteBalanceRed(long theValue) {
		if(theValue == _myLastRed)return true;
		_myLastRed = theValue;
		int myResult = _myInterface.IC_SetWhiteBalanceRed(_myGrabberPointer, theValue);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_NOT_AVAILABLE:
			throw new CCISNotAvailableException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}
	
	private long _myLastGreen = -1;

	/**
	 * Sets the value for white balance green.
	 * 
	 * @param theValue value of the green white balance to be set
	 * @return <code>true</code> if successful <code>false</code> otherwise
	 * 
	 */
	public boolean whiteBalanceGreen(long theValue) {
		if(theValue == _myLastGreen)return true;
		_myLastGreen = theValue;
		int myResult = _myInterface.IC_SetWhiteBalanceGreen(_myGrabberPointer, theValue);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_NOT_AVAILABLE:
			throw new CCISNotAvailableException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}
	
	private long _myLastBlue = -1;

	/**
	 * Sets the value for white balance blue.
	 * 
	 * @param theValue  value of the blue white balance to be set
	 * @return <code>true</code> if successful <code>false</code> otherwise
	 * 
	 */
	public boolean whiteBalanceBlue(long theValue) {
		if(theValue == _myLastBlue)return true;
		_myLastBlue = theValue;
		int myResult = _myInterface.IC_SetWhiteBalanceBlue(_myGrabberPointer, theValue);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_NOT_AVAILABLE:
			throw new CCISNotAvailableException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}
	
	/**
	 * Enables or disables the default window size lock of the video window.
	 * 
	 * @param theDefault  <li><code>false</code> disable, custom size can be set, 
	 * 
	 * <li><code>true</code> enable, the standard size, which is video format, is used.
	 * 
	 * @return <code>true</code> if successful <code>false</code> otherwise
	 * 
	 */
	public boolean defaultWindowPosition(Pointer hGrabber, boolean theDefault) {
		int myResult = _myInterface.IC_SetDefaultWindowPosition(_myGrabberPointer, theDefault ? 1 : 0);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_ERROR:
			return false;
		case CCISLibraryInterface.IC_NO_PROPERTYSET:
			throw new CCISNoPropertySetException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}

	/**
	 * This function Sets the position and size of the video window.
	 * 
	 * @param PosX     Specifies the x-coordinate of the upper left hand corner of
	 *                 the video window. It defaults to 0.
	 * @param PosY     Specifies the y-coordinate of the upper left hand corner of
	 *                 the video window. It defaults to 0.
	 * @param width    Specifies the width of the video window.
	 * @param height   Specifies the height of the video window.
	 * 
	 * @return <code>true</code> if successful <code>false</code> otherwise
	 * 
	 */
	public boolean windowPosition(int PosX, int PosY, int Width, int Height) {
		int myResult = _myInterface.IC_SetWindowPosition(_myGrabberPointer, PosX, PosY, Width,Height);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_ERROR:
			return false;
		case CCISLibraryInterface.IC_DEFAULT_WINDOW_SIZE_SET:
			throw new CCISNotAvailableException();
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}
	
	/**
	 * Enumerate the available properties of a video capture device.
	 * 
	 * @return list of properties
	 * 
	 */
	public List<String> enumProperties() {
		List<String> myResult = new ArrayList<>();
		_myInterface.IC_enumProperties(_myGrabberPointer, (p,d)->myResult.add(p), null);
		return myResult;
	}
	
	/**
	 * Enumerate the available interfaces of of a video capture device, property and
	 * element.
	 * 
	 * The string passed to the callback function can contain - Range - Switch -
	 * Button - Mapstrings - AbsoluteValues - Unknown
	 * 
	 * @param Property Name of the property
	 * @param Property Name of the element
	 * 
	 * @return list of interfaces
	 */
	public List<String> enumPropertyElementInterfaces(String Property, String Element) {
		List<String> myResult = new ArrayList<>();
		_myInterface.IC_enumPropertyElementInterfaces(_myGrabberPointer, Property, Element,(p,d)->myResult.add(p), null);
		return myResult;
	}
	
	/**
	 * Check, whether a property is available.. For a list of properties and
	 * elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 not checked.
	 * 
	 * @return <code>true</code> if the property is available otherwise <code>false</code>
	 */
	public boolean isPropertyAvailable(String theProperty, String theElement) {
		int myResult = _myInterface.IC_IsPropertyAvailable(_myGrabberPointer, theProperty, theElement);
		switch (myResult) {
		case CCISLibraryInterface.IC_SUCCESS:
			return true;
		case CCISLibraryInterface.IC_PROPERTY_ELEMENT_NOT_AVAILABLE:
			return false;
		case CCISLibraryInterface.IC_PROPERTY_ELEMENT_WRONG_INTERFACE:
			return false;
		case CCISLibraryInterface.IC_NO_HANDLE:
			throw new CCISNoHandleException();
		case CCISLibraryInterface.IC_NO_DEVICE:
			throw new CCISNotInLiveModeException("No video capture device opened.");
		}
		return false;
	}
	
	/**
	 * This returns the range of a property. For a list of properties and elements
	 * use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * 
	 * @return vector with the range
	 */
	public CCVector2i propertyValueRange(String theProperty, String theElement) {
		IntByReference lMin = new IntByReference();
		IntByReference lMax = new IntByReference();
		
		_myInterface.IC_GetPropertyValueRange(_myGrabberPointer, theProperty, theElement, lMin, lMax);
		
		return new CCVector2i(
			lMin.getValue(),
			lMax.getValue()	
		);
	}
	
	/**
	 * This returns the current value of a property. For a list of properties and
	 * elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is "Value".
	 * 
	 * @return value of the Property
	 */
	public int propertyValue(String theProperty, String theElement) {
		IntByReference myReference = new IntByReference();
		_myInterface.IC_GetPropertyValue(_myGrabberPointer, theProperty, theElement, myReference);
		return myReference.getValue();
	}
	
	/**
	 * This sets a new value of a property. For a list of properties and elements
	 * use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * @param theValue    Receives the value of the property
	 * 
	 * @return <code> true</code> if successful <code>false</code> otherwise
	 */
	public boolean propertyValue(String theProperty, String theElement, int theValue) {
		return _myInterface.IC_SetPropertyValue(_myGrabberPointer, theProperty, theElement, theValue) == CCISLibraryInterface.IC_SUCCESS;
	}


	/**
	 * This returns the range of an absolute value property. Usually it is used for
	 * exposure. a list of properties and elements use the VCDPropertyInspector of
	 * IC Imaging Control.
	 * 
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is
	 * 
	 * @return min max as vector2
	 */
	public CCVector2 propertyAbsoluteValueRange(String theProperty, String theElement) {
		FloatByReference lMin = new FloatByReference();
		FloatByReference lMax = new FloatByReference();
		
		_myInterface.IC_GetPropertyAbsoluteValueRange(_myGrabberPointer, theProperty, theElement, lMin, lMax);
		
		return new CCVector2(
			lMin.getValue(),
			lMax.getValue()	
		);
	}



	/**
	 * This returns the current value of an absolute value property. Usually it is
	 * used for exposure. For a list of properties and elements use the
	 * VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * 
	 * @return the value
	 */
	public float propertyAbsoluteValue(String theProperty, String theElement) {
		FloatByReference myReference = new FloatByReference();
		_myInterface.IC_GetPropertyAbsoluteValue(_myGrabberPointer, theProperty, theElement, myReference);
		return myReference.getValue();
	}


	/**
	 * This sets a new value of an absolute value property. Usually it is used for
	 * exposure. a list of properties and elements use the VCDPropertyInspector of
	 * IC Imaging Control.
	 * 
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * @param theValue    Receives the value of the property
	 * 
	 * @return <code> true</code> if successful <code>false</code> otherwise
	 */
	public boolean propertyAbsoluteValue(String theProperty, String theElement, float theValue) {
		return _myInterface.IC_SetPropertyAbsoluteValue(_myGrabberPointer, theProperty, theElement, theValue) == CCISLibraryInterface.IC_SUCCESS;
	}
	
	/**
	 * This returns the current value of a switch property. Switch properties are
	 * usually used for enabling and disabling of automatics. For a list of
	 * properties and elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Auto".
	 * 
	 * @return value of the switch
	 */
	public boolean propertyAutoEnabled(String theProperty, String theElement) {
		IntByReference On = new IntByReference();
		int myResult = _myInterface.IC_GetPropertySwitch(_myGrabberPointer, theProperty, theElement, On);
		return myResult == 1 ? On.getValue() == 1 : false;
	}


	/**
	 * This sets the value of a switch property. Switch properties are usually used
	 * for enabling and disabling of automatics. For a list of properties and
	 * elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param theProperty The name of the property, e.g. Gain, Exposure
	 * @param theElement  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Auto".
	 * @param theIsOn       the value of the property
	 * 
	 */
	public void propertyAutoEnabled(String theProperty, String theElement, boolean theIsOn) {
		_myInterface.IC_SetPropertySwitch(_myGrabberPointer, theProperty, theElement, theIsOn ? 1: 0);
	}

	
	private double _myUpdateDelta = 0;
	private double _myUpdateRate = 30;
	
	@Override
	public void update(CCAnimator theAnimator) {
		for(CCAnimatorUpdateListener myParameter:_cParameterMap.values()) {
			myParameter.update(theAnimator);
		}
		if(_cWhiteAuto) {
			whiteBalanceAuto(_cWhiteAuto);
		}else {
			whiteBalanceAuto(_cWhiteAuto);
			whiteBalanceRed(_cWhiteRed);
			whiteBalanceGreen(_cWhiteGreen);
			whiteBalanceBlue(_cWhiteBlue);
		}
		_cFPS = frameRate();
		
		_myUpdateDelta += theAnimator.deltaTime();
		if (!_myIsDataUpdated) return;
	
		_myIsDataUpdated = false;
		
		if (_myIsFirstFrame) {
			_myIsFirstFrame = false;
			initEvents.proxy().event(this);
		} else {
			updateEvents.proxy().event(this);
			_myUpdateRate = (0.1f) * 1f / _myUpdateDelta + (0.9f) * _myUpdateRate;
			_cFPS = (float)(_myUpdateRate);
			//CCLog.info(_myUpdateRate);
			_myUpdateDelta = 0;
		}
	}

	public static void main(String[] args) {


		int myDeviceCount = CCImagingSourceDevice.deviceCount();
		CCLog.info("DeviceCount", myDeviceCount);

		String myDevice = CCImagingSourceDevice.device(0);
		CCLog.info("Device", myDevice);
			
		CCImagingSourceDevice mySource = new CCImagingSourceDevice(myDevice, null);
		mySource.startLive(false);


		int myWidth = mySource.videoFormatWidth();
		int myHeight = mySource.videoFormatHeight();
		CCLog.info("videoFormatWidth", mySource.videoFormatWidth());
		CCLog.info("videoFormatHeight", mySource.videoFormatHeight());
		CCLog.info("frameRate        ", mySource.frameRate());
		for(float myFPS:mySource.frameRates()) {
			CCLog.info(myFPS);
		}
//		CCLog.info(mySource.format(CCISColorFormat.RGB24), CCISColorFormat.RGB24.ordinal());
//			
		CCLog.info("Camera Properties");
		for (CCISCameraProperty myProperty : CCISCameraProperty.values()) {
			CCLog.info(myProperty);
			CCLog.info("  is available     ", mySource.isCameraPropertyAvailable(myProperty));
			CCLog.info("  is auto available", mySource.isCameraPropertyAutoAvailable(myProperty));
			CCLog.info("  is auto enabled  ", mySource.isCameraPropertyAutoEnabled(myProperty));
			CCLog.info("  min max          ", mySource.cameraPropertyGetRange(myProperty));
			CCLog.info("  value            ", mySource.cameraProperty(myProperty));
		}

		CCLog.info("Video Properties");
		for (CCISVideoProperty myProperty : CCISVideoProperty.values()) {
			CCLog.info(myProperty);
			CCLog.info("  is available     ", mySource.isVideoPropertyAvailable(myProperty));
			CCLog.info("  is auto available", mySource.isVideoPropertyAutoAvailable(myProperty));
			CCLog.info("  is auto enabled  ", mySource.isVideoPropertyAutoEnabled(myProperty));
			CCLog.info("  min max          ", mySource.videoPropertyGetRange(myProperty));
			CCLog.info("  value            ", mySource.videoProperty(myProperty));
		}
//			
//		CCLog.info("Video formats");
//		for(int j = 0; j < mySource.videoFormatCount();j++) {
//			CCLog.info(mySource.videoFormat(j));
//		}
//			
//		CCLog.info("Video norms");
//		for(int j = 0; j < mySource.videoNormCount();j++) {
//			CCLog.info(mySource.videoNorm(j));
//		}

		CCLog.info("format", mySource.format());

		mySource.enumProperties().forEach(p -> {
			CCLog.info(p, mySource.isPropertyAvailable(p, "Value"), mySource.isPropertyAvailable(p, "Auto"), mySource.propertyValueRange(p, null), mySource.propertyValue(p, null),mySource.propertyAutoEnabled(p, null));
//			mySource.enumPropertyElementInterfaces(p, "Value").forEach(i -> CCLog.info(i));
			
		});
			
		while(true) {
			
		}

		//mySOurce.closeVideoCaptureDevice();
			
	}
}