package cc.creativecomputing.video;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.FloatByReference;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.LongByReference;
import com.sun.jna.ptr.PointerByReference;

import cc.creativecomputing.video.CCImagingSourceDevice.CCISFileType;

public interface CCISLibraryInterface extends Library {

	/**
	 * Return value for success. Indicates that a function has been performed
	 * without an error.
	 */
	public static int IC_SUCCESS = 1;

	/**
	 * Return value that indicates an error. A function returns IC_ERROR, then
	 * something went wrong.
	 */
	public static int IC_ERROR = 0;

	/**
	 * No device handle. Pointer is NULL. Indicates, that an Pointer handle has
	 * not been created yet. Please see IC_CreateGrabber() for creating an Pointer
	 * handle.
	 */
	public static int IC_NO_HANDLE = -1;

	/**
	 * No device opened, but Pointer is valid. Indicates that no device has been
	 * opened. Please refer to IC_OpenVideoCaptureDevice().
	 */
	public static int IC_NO_DEVICE = -2;

	/**
	 * Property not avaiable, but Pointer is valid. Indicates, that the video
	 * capture device is not in live mode, but live mode is for the current function
	 * call required. Please refer to IC_StartLive().
	 */
	public static int IC_NOT_AVAILABLE = -3;

	/**
	 * The Propertyset was not queried. This return value indicates, that the video
	 * capture device does not support the specified property.
	 */
	public static int IC_NO_PROPERTYSET = -3;

	/**
	 * The live display window size could not be set Indicates, that the porperty
	 * set was not queried for the current grabber handle. Please check, whether
	 * IC_QueryPropertySet() was called once before using the function.
	 */
	public static int IC_DEFAULT_WINDOW_SIZE_SET = -3;

	/**
	 * A device has been opened, but is is not in live mode. Indicates, that setting
	 * of a custom live display window size failed, because
	 * IC_SetDefaultWindowPosition() was not called with parameter false somewhere
	 * before.
	 */
	public static int IC_NOT_IN_LIVEMODE = -3;

	/**
	 * A requested property item is not available Indicates, that a device does not
	 * support the requested property, or the name of a property was written in
	 * wrong way.
	 */
	public static int IC_PROPERTY_ITEM_NOT_AVAILABLE = -4;

	/**
	 * A requested element of a given property item is not available Indicates, that
	 * a device does not support the requested element property, or the name of an
	 * element was written in wrong way.
	 */
	public static int IC_PROPERTY_ELEMENT_NOT_AVAILABLE = -5;

	/**
	 * A requested element has not the interface, which is needed. Indicates, that a
	 * property element does not support the request, that is wanted. e.g. Exposure
	 * Auto has no range, therefore IC_GetPropertyValueRange(hGrabber,
	 * "Epxosure","Auto", &min, &max ) will return
	 * IC_PROPERTY_ELEMENT_WRONG_INTERFACE.
	 */
	public static int IC_PROPERTY_ELEMENT_WRONG_INTERFACE = -6;

	/**
	 * A requested element has not the interface, which is needed. Indicates, that
	 * there was an index passed, which was out of range of the number of available
	 * elements
	 */
	public static int IC_INDEX_OUT_OF_RANGE = -7;

	/**
	 * Indicates, that that the passed XML file contains no valid XML
	 */
	public static int IC_WRONG_XML_FORMAT = -1;

	/**
	 * Indicates, that the passed XML file contains no compatible XML data.
	 */
	public static int IC_WRONG_INCOMPATIBLE_XML = -3;

	/**
	 * Indicates, that not all properties have been restored as desired, but the
	 * camera itself was opened.
	 */
	public static int IC_NOT_ALL_PROPERTIES_RESTORED = -4;

	/**
	 * Indicates, that the device specified in the XML was not found. E.g. The same
	 * model, but different serial number, or no camera connected at all.
	 */
	public static int IC_DEVICE_NOT_FOUND = -5;

	/**
	 * Indicates, that the passed file does not exist
	 */
	public static int IC_FILE_NOT_FOUND = 35;

	/**
	 * Initialize the ICImagingControl class library. This function must be called
	 * only once before any other functions of this library are called.
	 * 
	 * @param theLicenseKey Imaging Control license key or NULL if only a trial
	 *                      version is available.
	 * @return IC_SUCCESS on success IC_ERROR on wrong license key or other errors.
	 */
	public int IC_InitLibrary(String theLicenseKey);

	/**
	 * Closes the library, cleans up memory. Must be called at the of the
	 * application to release allocated memory.
	 * 
	 * @see #IC_InitLibrary
	 */
	public void IC_CloseLibrary();

	/**
	 * Get the number of the currently available devices. This function creates an
	 * internal array of all connected video capture devices. With each call to this
	 * function, this array is rebuild. The name and the unique name can be
	 * retrieved from the internal array using the functions IC_GetDevice() and
	 * IC_GetUniqueNamefromList. They are usefull for retrieving device names for
	 * opening devices.
	 * 
	 * @return >= 0 Success, count of found devices or IC_NO_HANDLE Internal Error.
	 * 
	 * @see #IC_GetDevice
	 * @see #IC_GetUniqueNamefromList
	 */
	public int IC_GetDeviceCount();

	/**
	 * Get the name of a video capture device.
	 * <p>
	 * Get a string representation of a device specified by theIndex. theIndex must
	 * be between 0 and IC_GetDeviceCount(). IC_GetDeviceCount() must have been
	 * called before this function, otherwise it will always fail.
	 * 
	 * @param theIndex The number of the device whose name is to be returned. It
	 *                 must be in the range from 0 to IC_GetDeviceCount(),
	 * @return Returns the string representation of the device on success, NULL
	 *         otherwise.
	 * 
	 * @see #IC_GetDeviceCount
	 * @see #IC_GetUniqueNamefromList
	 */
	public String IC_GetDevice(int theIndex);

	/**
	 * Creates a new grabber handle and returns it. A new created grabber should be
	 * release with a call to IC_ReleaseGrabber if it is no longer needed.
	 * 
	 * @return new created grabber.
	 * @see #IC_ReleaseGrabber
	 */
	public Pointer IC_CreateGrabber();

	/**
	 * Release the grabber object. Must be called, if the calling application does
	 * no longer need the grabber.
	 * 
	 * @param hGrabber The handle to grabber to be released.
	 * @see #IC_CreateGrabber
	 */
	public void IC_ReleaseGrabber(PointerByReference hGrabber);

	/**
	 * Type declaration for the frame ready callback function.
	 * 
	 * @see #IC_SetFrameReadyCallback
	 * @see #IC_SetCallbacks
	 * 
	 */
	public interface FRAME_READY_CALLBACK extends Callback {
		void invoke(Pointer hGrabber, Pointer pData, long frameNumber, Pointer userdata);
	}

	/**
	 * Enable frame ready callback.
	 * 
	 * @param hGrabber                     Handle to a grabber object.
	 * @param cb                           Callback function of type
	 *                                     FRAME_READY_CALLBACK
	 * @param x1_argument_in_void_userdata Pointer to some userdata.
	 * 
	 * @return IC_SUCCESS on success or IC_ERROR on errors.
	 * 
	 * @see #FRAME_READY_CALLBACK
	 * 
	 */
	public int IC_SetFrameReadyCallback(Pointer hGrabber, FRAME_READY_CALLBACK cb, Pointer x1_argument_in_void_userdata);

	/**
	 * Type declaration for the device lost callback function.
	 * 
	 * @see IC_SetCallbacks
	 */
	public interface DEVICE_LOST_CALLBACK extends Callback {
		void invoke(Pointer hGrabber, Pointer userdata);
	}

	/**
	 * Set callback function
	 * 
	 * @param hGrabber                     Handle to a grabber object.
	 * @param cb                           Callback function of type
	 *                                     FRAME_READY_CALLBACK, can be NULL, if no
	 *                                     callback is needed
	 * @param dlcb                         Callback function of type
	 *                                     DEVICE:LOST_CALLBACK, can be NULL, if no
	 *                                     device lost handler is needed
	 * @param x1_argument_in_void_userdata Pointer to some userdata.
	 * 
	 * @sa FRAME_READY_CALLBACK
	 */
	public int IC_SetCallbacks(Pointer hGrabber, FRAME_READY_CALLBACK cb, Pointer x1_argument_in_void_userdata,
			DEVICE_LOST_CALLBACK dlCB, Pointer x2_argument_in_void_userdata);

	/**
	 * Set Continuous mode
	 * 
	 * In continuous mode, the callback is called for each frame, so that there is
	 * no need to use IC_SnapImage etc.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param cont     0 : Snap continuous, 1 : do not automatically snap.
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NOT_IN_LIVEMODE The device is currently streaming, therefore
	 *         setting continuous mode failed.
	 *         <li>IC_NO_HANDLE Internal Grabber does not exist or hGrabber is NULL
	 * 
	 * @remarks Not available in live mode.
	 * 
	 */
	public int IC_SetContinuousMode(Pointer hGrabber, int cont);

	/**
	 * Open a video capture device. The hGrabber handle must have been created
	 * previously by a call to IC_CreateGrabber(). Once a hGrabber handle has been
	 * created it can be recycled to open different video capture devices in
	 * sequence.
	 * 
	 * @param hGrabber     The handle to grabber object, that has been created by a
	 *                     call to IC_CreateGrabber
	 * @param szDeviceName Friendly name of the video capture device e.g.
	 *                     "DFK21F04".
	 * @return IC_SUCCESS on success or IC_ERROR on errors.
	 * @see #IC_CloseVideoCaptureDevice
	 */
	public int IC_OpenVideoCaptureDevice(Pointer hGrabber, String szDeviceName);

	/**
	 * Close the current video capture device. The Pointer object will not be
	 * deleted. It can be used again for opening another video capture device.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 */
	public void IC_CloseVideoCaptureDevice(Pointer hGrabber);

	/**
	 * Returns the name of the current video capture Retrieve the name of the
	 * current video capture device. If the device is invalid, NULL is returned.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @return The name of the video capture device or NULL If no video capture
	 *         device is currently opened.
	 */
	public String IC_GetDeviceName(Pointer hGrabber);

	/**
	 * Returns the width of the video format.
	 * 
	 * @param hGrabber
	 * @return
	 */
	public int IC_GetVideoFormatWidth(Pointer hGrabber);

	/**
	 * returns the height of the video format.
	 * 
	 * @param hGrabber
	 * @return
	 */
	public int IC_GetVideoFormatHeight(Pointer hGrabber);/// <

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
	 * @param hGrabber The handle to the grabber object.
	 * @param format   The desired color format. Possible values for format are:
	 *                 <li>Y800
	 *                 <li>RGB24
	 *                 <li>RGB32
	 *                 <li>UYVY
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 * 
	 * @note Please note that UYVY can only be used in conjunction with a UYVY video
	 *       format.
	 * 
	 * 
	 */
	public int IC_SetFormat(Pointer hGrabber, int format);

	/**
	 * Returns the current color format of the sink. Retrieves the format of the
	 * sink type currently set (See IC_SetFormat() for possible formats). If no sink
	 * type is set or an error occurred, NONE is returned. The function returns a
	 * valid value only after IC_PreprareLive() or IC_StartLive() was called. Before
	 * these calls, NONE is returned.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @return The current sink color format.
	 */
	public int IC_GetFormat(Pointer hGrabber);

	/**
	 * Set a video format for the current video capture device. The video format
	 * must be supported by the current video capture device.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @param szFormat A string that contains the desired video format.
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 */
	public int IC_SetVideoFormat(Pointer hGrabber, String szFormat);

	/**
	 * Set a video norm for the current video capture device.
	 * 
	 * @note The current video capture device must support video norms.
	 * @param hGrabber The handle to the grabber object.
	 * @param szNorm   A string that contains the desired video format.
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 */
	public int IC_SetVideoNorm(Pointer hGrabber, String szNorm);

	/**
	 * Set a input channel for the current video capture device.
	 * 
	 * @note The current video capture device must support input channels..
	 * @param hGrabber  The handle to the grabber object.
	 * @param szChannel A string that contains the desired video format.
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 */
	public int IC_SetInputChannel(Pointer hGrabber, String szChannel);

	/**
	 * Start the live video.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @param iShow    The parameter indicates:
	 *                 <li>1 : Show the video
	 *                 <li>0 : Do not show the video, but deliver frames. (For
	 *                 callbacks etc.)
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 * @see #IC_StopLive
	 */
	public int IC_StartLive(Pointer hGrabber, int iShow);

	/**
	 * Prepare the grabber for starting the live video.
	 * 
	 * @param hGrabber
	 * @param iShow
	 * @return
	 */
	public int IC_PrepareLive(Pointer hGrabber, int iShow);

	/**
	 * Suspends an image stream and puts it into prepared state.
	 * 
	 * @param hGrabber
	 * @return
	 */
	public int IC_SuspendLive(Pointer hGrabber);

	/**
	 * Check, whether the passed grabber already provides are live video
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @return
	 *         <li>1 : Livevideo is running,
	 *         <li>0 : Livevideo is not running.
	 *         <li>IC_NO_HANDLE hGrabber is not a valid handle. GetGrabber was not
	 *         called.
	 *         <li>IC_NO_DEVICE No device opened. Open a device, before this
	 *         function can be used.
	 * 
	 */
	public int IC_IsLive(Pointer hGrabber);

	/**
	 * Stop the live video.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @see #IC_StartLive
	 */
	public void IC_StopLive(Pointer hGrabber);

	/**
	 * Check, whether a property is supported by the current video capture device.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @see #theProperty The cammera property to be checked
	 * @return
	 *         <li>IC_SUCCESS on success or
	 *         <li>IC_ERROR if something went wrong.
	 *         <li>IC_NO_HANDLE hGrabber is not a valid handle. GetGrabber was not
	 *         called.
	 *         <li>IC_NO_DEVICE No device opened. Open a device, before this
	 *         function can be used.
	 * 
	 */
	public int IC_IsCameraPropertyAvailable(Pointer hGrabber, int theProperty);

	/**
	 * Set a camera property like exposure, zoom.
	 * 
	 * @param hGrabber    The handle to the grabber object.
	 * @param theProperty The property to be set. It can have following values:
	 *                    <li>PROP_CAM_PAN
	 *                    <li>PROP_CAM_TILT,
	 *                    <li>PROP_CAM_ROLL,
	 *                    <li>PROP_CAM_ZOOM,
	 *                    <li>PROP_CAM_EXPOSURE,
	 *                    <li>PROP_CAM_IRIS,
	 *                    <li>PROP_CAM_FOCUS
	 * @param lValue      The value the property is to be set to.
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 * 
	 * @note lValue should be in the range of the specified property. If the value
	 *       could not be set (out of range, auto is currently enabled), the
	 *       function returns 0. On success, the functions returns 1.
	 */
	public int IC_SetCameraProperty(Pointer hGrabber, int theProperty, long lValue);

	/**
	 * Get the minimum and maximum value of a camera property
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @return
	 */
	public int IC_CameraPropertyGetRange(Pointer hGrabber, int theProperty, LongByReference lMin, LongByReference lMax);

	/**
	 * Get a camera property's value.
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @return
	 */
	public int IC_GetCameraProperty(Pointer hGrabber, int theProperty, LongByReference lValue);

	/**
	 * Enable or disable automatic for a camera property.
	 * 
	 * @param hGrabber    The handle to the grabber object.
	 * @param theProperty The property to be set. It can have following values:
	 *                    <li>PROP_CAM_PAN
	 *                    <li>PROP_CAM_TILT,
	 *                    <li>PROP_CAM_ROLL,
	 *                    <li>PROP_CAM_ZOOM,
	 *                    <li>PROP_CAM_EXPOSURE,
	 *                    <li>PROP_CAM_IRIS,
	 *                    <li>PROP_CAM_FOCUS
	 * @param iOnOFF      Enables or disables the automation. Possible values ar
	 *                    <li>1 : Enable automatic
	 *                    <li>0 : Disable Automatic
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 * 
	 * @note If the property is not supported by the current video capture device or
	 *       automation of the property is not available with the current video
	 *       capture device, the function returns 0. On success, the function
	 *       returns 1.
	 */
	public int IC_EnableAutoCameraProperty(Pointer hGrabber, int theProperty, int theOnOff);

	/**
	 * Check whether automation for a camera property is available.
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @return
	 */
	public int IC_IsCameraPropertyAutoAvailable(Pointer hGrabber, int theProperty);

	/**
	 * Retrieve whether automatic is enabled for the specifield camera property.
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @return
	 */
	public int IC_GetAutoCameraProperty(Pointer hGrabber, int theProperty, IntByReference theOnOff);

	/**
	 * Check whether the specified video property is available.
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @return
	 */
	public int IC_IsVideoPropertyAvailable(Pointer hGrabber, int theProperty);

	/**
	 * Retrieve the lower and upper limit of a video property.
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @param lMin
	 * @param lMax
	 * @return
	 */
	public int IC_VideoPropertyGetRange(Pointer hGrabber, int theProperty, LongByReference lMin, LongByReference lMax);

	/**
	 * Retrieve the the current value of the specified video property.
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @param lValue
	 * @return
	 */
	public int IC_GetVideoProperty(Pointer hGrabber, int theProperty, LongByReference lValue);

	/**
	 * Check whether the specified video property supports automation.
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @return
	 */
	public int IC_IsVideoPropertyAutoAvailable(Pointer hGrabber, int theProperty);

	/**
	 * Get the automation state of a video property.
	 * 
	 * @param hGrabber
	 * @param theProperty
	 * @return
	 */
	public int IC_GetAutoVideoProperty(Pointer hGrabber, int theProperty, IntByReference theOnOff);

	/**
	 * Set a video property like brightness, contrast.
	 * 
	 * @param hGrabber    The handle to the grabber object.
	 * @param theProperty The property to be set. It can have following values:
	 *                    <li>PROP_VID_BRIGHTNESS ,
	 *                    <li>PROP_VID_CONTRAST,
	 *                    <li>PROP_VID_HUE,
	 *                    <li>PROP_VID_SATURATION,
	 *                    <li>PROP_VID_SHARPNESS,
	 *                    <li>PROP_VID_GAMMA,
	 *                    <li>PROP_VID_COLORENABLE,
	 *                    <li>PROP_VID_WHITEBALANCE,
	 *                    <li>PROP_VID_BLACKLIGHTCOMPENSATION,
	 *                    <li>PROP_VID_GAIN
	 * @param lValue      The value the property is to be set to.
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 * 
	 * @note lValue should be in the range of the specified property. If the value
	 *       could not be set (out of range, auto is currently enabled), the
	 *       function returns 0. On success, the functions returns 1.
	 */
	public int IC_SetVideoProperty(Pointer hGrabber, int theProperty, long lValue);

	/**
	 * Enable or disable automatic for a video propertery.
	 * 
	 * @param hGrabber    The handle to the grabber object.
	 * @param theProperty The property to be set. It can have following values:
	 *                    <li>PROP_VID_BRIGHTNESS,
	 *                    <li>PROP_VID_CONTRAST,
	 *                    <li>PROP_VID_HUE,
	 *                    <li>PROP_VID_SATURATION,
	 *                    <li>PROP_VID_SHARPNESS,
	 *                    <li>PROP_VID_GAMMA,
	 *                    <li>PROP_VID_COLORENABLE,
	 *                    <li>PROP_VID_WHITEBALANCE,
	 *                    <li>PROP_VID_BLACKLIGHTCOMPENSATION,
	 *                    <li>PROP_VID_GAIN
	 * @param iOnOFF      Enables or disables the automation. Possible values ar
	 *                    <li>1 : Enable automatic
	 *                    <li>0 : Disable Automatic
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 * 
	 * @note If the property is not supported by the current video capture device or
	 *       automation of the property is not available with the current video
	 *       capture device, the function reurns 0. On success, the function returns
	 *       1.
	 */
	public int IC_EnableAutoVideoProperty(Pointer hGrabber, int theProperty, int theOnOff);

	/**
	 * Retrieve the properties of the current video format and sink type
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @param          *lWidth This receives the width of the image buffer.
	 * @param          *lHeight This receives the height of the image buffer.
	 * @param          *iBitsPerPixel This receives the count of bits per pixel.
	 * @param          *format This receives the current color format.
	 * @return
	 *         <li>IC_SUCCESS on success or
	 *         <li>IC_ERROR if something went wrong.
	 */
	public int IC_GetImageDescription(Pointer hGrabber, LongByReference lWidth, LongByReference lHeight,
			IntByReference iBitsPerPixel, IntByReference format);

	/**
	 * Snaps an image. The video capture device must be set to live mode and a sink
	 * type has to be set before this call. The format of the snapped images depend
	 * on the selected sink type.
	 * 
	 * @param hGrabber         The handle to the grabber object.
	 * @param iTimeOutMillisek The Timeout time is passed in milli seconds. A value
	 *                         of -1 indicates, that no time out is set.
	 * 
	 * 
	 * @return
	 *         <li>IC_SUCCESS on success
	 *         <li>IC_ERROR if something went wrong.
	 *         <li>IC_NOT_IN_LIVEMODE if the live video has not been started.
	 * 
	 * @see #IC_StartLive
	 * @see #IC_SetFormat
	 * 
	 */
	public int IC_SnapImage(Pointer hGrabber, int iTimeOutMillisek);

	/**
	 * Save the contents of the last snapped image by IC_SnapImage into a file.
	 * 
	 * @param hGrabber    The handle to the grabber object.
	 * @param theFileName String containing the file name to be saved to.
	 * @param ft          File type if the image, It have be
	 *                    <li>FILETYPE_BMP for bitmap files
	 *                    <li>FILETYPE_JPEG for JPEG file.
	 * @param quality     If the JPEG format is used, the image quality must be
	 *                    specified in a range from 0 to 100.
	 * @return
	 *         <li>IC_SUCCESS on success
	 *         <li>IC_ERROR if something went wrong.
	 * 
	 * @remarks The format of the saved images depend on the sink type. If the sink
	 *          type is set to Y800, the saved image will be an 8 Bit grayscale
	 *          image. In any other case the saved image will be a 24 Bit RGB image.
	 * 
	 * @note IC Imaging Control 1.41 only supports FILETYPE_BMP.
	 * @see #IC_SnapImage
	 * @see #IC_SetFormat
	 */
	public int IC_SaveImage(Pointer hGrabber, String theFileName, CCISFileType ft, long quality);

	/**
	 * Returns a pointer to the image data Retrieve a byte pointer to the image data
	 * (pixel data) of the last snapped image (see SnapImage()). If the function
	 * fails, the return value is NULL otherwise the value is a pointer to the first
	 * byte in the lowest image line (the image is saved bottom up!).
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @return
	 *         <li>Nonnull Pointer to the image data
	 *         <li>NULL Indicates that an error occurred.
	 * @see #IC_SnapImage
	 * @see #IC_SetFormat
	 */
	// unsigned char* IC_GetImagePtr( Pointer hGrabber );

	/**
	 * Assign an Window handle to display the video in.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @param hWnd     The handle of the window where to display the live video in.
	 * @return
	 *         <li>IC_SUCCESS on success
	 *         <li>IC_ERROR if something went wrong.
	 */
	// public int IC_SetHWnd(Pointer hGrabber, __HWND hWnd);

	/**
	 * Return the serial number of the current device. Memory for the serial number
	 * must has been allocated by the application:
	 * 
	 * This function decodes the The Imaging Source serial numbers.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @param szSerial char array that receives the serial number.
	 * @return
	 *         <li>IC_SUCCESS The serial number could be retrieved.
	 *         <li>IC_IC_NOT_AVAILABLE The video capture device does not provide a
	 *         serial number.
	 *         <li>IC_NO_DEVICE No video capture device opened-
	 *         <li>IC_NO_HANDLE hGrabber is NULL.
	 */
	public int IC_GetSerialNumber(Pointer hGrabber, char[] szSerial);

	/**
	 * Count all connected video capture devices. If the Parameter szDeviceList is
	 * NULL, only the number of devices is queried. The Parameter szDeviceList must
	 * be a two dimensional array of char. The iSize parameter specifies the length
	 * of the strings, that are used in the array.
	 * 
	 * @param szDeviceList A two dimensional char array that receives the list. Or
	 *                     NULL if only the count of devices is to be returned.
	 * @param iSize        Not used.
	 * @return
	 *         <li>>= 0 Success, count of found devices
	 *         <li><0 An error occurred.
	 */
	public int IC_ListDevices(char[][] szDeviceList, int iSize);

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
	int IC_ListDevicesbyIndex(String szDeviceName, int iSize, int DeviceIndex);

	/**
	 * Count all available video formats. If the Parameter szFormatList is NULL,
	 * only the number of formats is queried. The Parameter szFormatList must be a
	 * two dimensional array of char. The iSize parameter specifies the length of
	 * the strings, that are used in the array to store the format names.
	 * 
	 * @param hGrabber     The handle to the grabber object.
	 * @param szFormatList A two dimensional char array that receives the list. Or
	 *                     NULL if only the count of formats is to be returned.
	 * 
	 * @return
	 *         <li>>= 0 Success, count of found video formats
	 *         <li><0 An error occurred.
	 * 
	 *         Simple sample to list the video capture devices:
	 * <pre> char szFormatList[80][40]; int iFormatCount; PointerByReference
	 *       hGrabber; hGrabber = IC_CreateGrabber();
	 *       IC_OpenVideoCaptureDevice(hGrabber, "DFK 21F04" ); iFormatCount =
	 *       IC_ListDevices(hGrabber, (char*)szFormatList,40 ); for( i = 0; i < min(
	 *       iFormatCount, 80); i++ ) { printf("%2d. %s\n",i+1,szFormatList[i]); }
	 *       IC_ReleaseGrabber( hGrabber ); </pre>
	 */
	int IC_ListVideoFormats(Pointer hGrabber, String szFormatList, int iSize);

	/**
	 * Simpler approach of enumerating video formats. No 2D char array needed.
	 * 
	 * @param hGrabber     The handle to the grabber object.
	 * @param szFormatName char memory, that will receive the name of the video
	 *                     format. Should be big enough.
	 * @param iSize        Size in byte of szFormatName
	 * @theIndex Index of the video format to query.
	 * 
	 * <pre> char szVideoFormatName[40]; // Use max 39 chars for a video format name
	 *       int FormatCount; Pointer hGrabber; hGrabber = IC_CreateGrabber();
	 *       IC_OpenVideoCaptureDevice(hGrabber, "DFK 21AU04" ); FormatCount =
	 *       IC_GetVideoFormatCount(hGrabber); // Query number of connected devices
	 *       for( i = 0; i < FormatCount; i++ ) {
	 *       IC_ListVideoFormatbyIndex(szVideoFormatName,39, i); printf("%2d.
	 *       %s\n",i+1,szVideoFormatName); } </pre> @param szDeviceName Char
	 *       memory, that receives the device name @param iSize Size of the char
	 *       memory. If names are longer, they will be truncated. @param DeviceIndex
	 *       Index of the device to be query. Must be between 0 and
	 *       IC_GetDeviceCount.
	 * 
	 * 		@return
	 *       <li>IC_SUCCESS Success,
	 *       <li>IC_NO_DEVICE No video capture device selected.
	 *       <li>IC_NO_HANDLE No handle to the grabber object.
	 * 
	 */
	int IC_ListVideoFormatbyIndex(Pointer hGrabber, String szFormatName, int iSize, int theIndex);

	/**
	 * Get unique device name of a device specified by theIndex. The unique device
	 * name consist from the device name and its serial number. It allows to differ
	 * between more then one device of the same type connected to the computer. The
	 * unique device name is passed to the function IC_OpenDevByUniqueName
	 * 
	 * @param theIndex The number of the device whose name is to be returned. It
	 *                 must be in the range from 0 to IC_GetDeviceCount(),
	 * @return Returns the string representation of the device on success, NULL
	 *         otherwise.
	 * 
	 * @see #IC_GetDeviceCount
	 * @see #IC_GetUniqueNamefromList
	 * @see #IC_OpenDevByUniqueName
	 */
	public int IC_GetUniqueNamefromList(int theIndex);

	/**
	 * Get the number of the available input channels for the current device. A
	 * video capture device must have been opened before this call.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * 
	 * @return
	 *         <li>>= 0 Success
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE No handle to the grabber object.
	 * 
	 * @see #IC_GetInputChannel
	 */
	public int IC_GetInputChannelCount(Pointer hGrabber);

	/**
	 * Get a string representation of the input channel specified by theIndex.
	 * theIndex must be between 0 and IC_GetInputChannelCount().
	 * IC_GetInputChannelCount() must have been called before this function,
	 * otherwise it will always fail.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @param theIndex Number of the input channel to be used..
	 * 
	 * @return
	 *         <li>Nonnull The name of the specified input channel
	 *         <li>NULL An error occurred.
	 * @see #IC_GetInputChannelCount
	 */
	public String IC_GetInputChannel(Pointer hGrabber, int theIndex);

	/**
	 * Get the number of the available video norms for the current device. A video
	 * capture device must have been opened before this call.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * 
	 * @return
	 *         <li>>= 0 Success
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE No handle to the grabber object.
	 * 
	 * @see #IC_GetVideoNorm
	 */
	public int IC_GetVideoNormCount(Pointer hGrabber);

	/**
	 * Get a string representation of the video norm specified by theIndex. theIndex
	 * must be between 0 and IC_GetVideoNormCount(). IC_GetVideoNormCount() must
	 * have been called before this function, otherwise it will always fail.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @param theIndex Number of the video norm to be used.
	 * 
	 * @return
	 *         <li>Nonnull The name of the specified video norm.
	 *         <li>NULL An error occurred.
	 * @see #IC_GetVideoNormCount
	 * 
	 */
	public String IC_GetVideoNorm(Pointer hGrabber, int theIndex);

	/**
	 * Get the number of the available video formats for the current device. A video
	 * capture device must have been opened before this call.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * 
	 * @return
	 *         <li>>= 0 Success
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE No handle to the grabber object.
	 * 
	 * @see #IC_GetVideoFormat
	 */
	public int IC_GetVideoFormatCount(Pointer hGrabber);

	/**
	 * Return the name of a video format. Get a string representation of the video
	 * format specified by theIndex. theIndex must be between 0 and
	 * IC_GetVideoFormatCount(). IC_GetVideoFormatCount() must have been called
	 * before this function, otherwise it will always fail.
	 * 
	 * @param hGrabber The handle to the grabber object.
	 * @param theIndex Number of the video format to be used.
	 * 
	 * @return
	 *         <li>Nonnull The name of the specified video format.
	 *         <li>NULL An error occurred.
	 * @see #IC_GetVideoFormatCount
	 */
	public String IC_GetVideoFormat(Pointer hGrabber, int theIndex);

	/**
	 * Save the state of a video capture device to a file.
	 * 
	 * @param hGrabber    The handle to the grabber object.
	 * @param theFileName Name of the file where to save to.
	 * 
	 * @return IC_SUCCESS on success or IC_ERROR if something went wrong.
	 * 
	 * @see #IC_LoadDeviceStateFromFile
	 */
	public int IC_SaveDeviceStateToFile(Pointer hGrabber, String theFileName);

	/**
	 * Load a device settings file. On success the device is opened automatically.
	 * 
	 * @param hGrabber    The handle to the grabber object. If it is NULL then a new
	 *                    Pointer handle is created. This should be released by a
	 *                    call to IC_ReleaseGrabber when it is no longer needed.
	 * @param theFileName Name of the file where to save to.
	 * 
	 * @return Pointer The handle of the grabber object, that contains the new
	 *         opened video capture device.
	 * 
	 * @see #IC_SaveDeviceStateToFile
	 * @see #IC_ReleaseGrabber
	 */
	public PointerByReference IC_LoadDeviceStateFromFile(Pointer hGrabber, String theFileName);

	/**
	 * Load a device settings file.
	 * 
	 * @param hGrabber    The handle to the grabber object. If it is NULL then a new
	 *                    Pointer handle is created, in case OpenDevice is true. If
	 *                    OpenDevice is set to false, the a device must be already
	 *                    open in the grabber handle. The properties in the passed
	 *                    XML file will be apllied to the opened device. This should
	 *                    be released by a call to IC_ReleaseGrabber when it is no
	 *                    longer needed.
	 * @param theFileName Name of the file where to save to.
	 * @param OpenDevice  If 1, the device specified in the XML file is opened. If
	 *                    0, then a device must be opened in the hGrabber. The
	 *                    properties and video format specified in the XML file will
	 *                    be applied to the opened device.
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
	 * @see #IC_SaveDeviceStateToFile
	 * @see #IC_ReleaseGrabber
	 */
	public int IC_LoadDeviceStateFromFileEx(Pointer hGrabber, String theFileName, int OpenDevice);

	/**
	 * Open a video capture by using its DisplayName.
	 * 
	 * @param hGrabber       The handle to the grabber object.
	 * @param theDisplayName Displayname of the device. Can be retrieved by a call
	 *                       to IC_GetDisplayName().
	 * 
	 * 
	 * @return
	 *         <li>IC_SUCCESS on success
	 *         <li>IC_ERROR if something went wrong.
	 * 
	 * @see #IC_GetDisplayName
	 */
	public int IC_OpenDevByDisplayName(Pointer hGrabber, String theDisplayName);

	/**
	 * Get a DisplayName from a currently open device. The display name of a device
	 * can be another on different computer for the same video capture device.
	 * 
	 * @param hGrabber      Handle to a grabber object
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
	 * @see #IC_ReleaseGrabber
	 * 
	 */
	public int IC_GetDisplayName(Pointer hGrabber, String theDisplayName, int iLen);

	/**
	 * Open a video capture by using its UniqueName. Use IC_GetUniqueName() to
	 * retrieve the unique name of a camera.
	 * 
	 * @param hGrabber      Handle to a grabber object
	 * @param szDisplayName Memory that will take the display name.
	 * 
	 * @see #IC_GetUniqueName
	 * @see #IC_ReleaseGrabber
	 * 
	 */
	public int IC_OpenDevByUniqueName(Pointer hGrabber, String theDisplayName);

	/**
	 * Get a UniqueName from a currently open device.
	 * 
	 * @param hGrabber     Handle to a grabber object
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
	public int IC_GetUniqueName(Pointer hGrabber, String szUniquename, int iLen);

	/**
	 * This returns 1, if a valid device has been opened, otherwise it is 0.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * 
	 * <li> IC_ERROR There is no valid video capture device opened
	 * <li> IC_SUCCESS There is a valid video capture device openend.
	 */
	public int IC_IsDevValid(Pointer hGrabber);

	/**
	 * Show the VCDProperty dialog.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * 
	 * @return
	 *         <li>IC_SUCCESS on success or IC_ERROR if something went wrong.
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE Nullpointer.
	 */
	public int IC_ShowPropertyDialog(Pointer hGrabber);

	/**
	 * Show the device selection dialog. This dialogs enables to select the video
	 * capture device, the video norm, video format, input channel and frame rate.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * 
	 * @return The passed hGrabber object or a new created if hGrabber was NULL.
	 */
	public PointerByReference IC_ShowDeviceSelectionDialog(Pointer hGrabber);

	/**
	 * Return whether the current video capture device supports an external trigger.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @return
	 *         <li>IC_SUCCESS An external trigger is supported
	 *         <li>IC_ERROR No external trigger is supported.
	 *         <li>IC_NO_DEVICE No video capture device selected.
	 *         <li>IC_NO_HANDLE Internal Grabber does not exist.
	 * 
	 * @see #IC_EnableTrigger
	 */
	public int IC_IsTriggerAvailable(Pointer hGrabber);

	/**
	 * Enable or disable the external trigger.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param iEnable  1 = enable the trigger, 0 = disable the trigger
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
	public int IC_EnableTrigger(Pointer hGrabber, int iEnable);

	/**
	 * Remove or insert the the overlay bitmap to the grabber object. If Y16 format
	 * is used, the overlay must be removed,
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param iEnable  = 1 inserts overlay, 0 removes the overlay.
	 */
	public void IC_RemoveOverlay(Pointer hGrabber, int iEnable);

	/**
	 * Enable or disable the overlay bitmap on the live video
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param iEnable  = 1 enables the overlay, 0 disables the overlay.
	 */
	public void IC_EnableOverlay(Pointer hGrabber, int iEnable);
	
	/**
	 * Gets the current value of Color enhancement property
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param OnOff
	 * @li 0 : Color enhancement is off
	 * @li 1 : Color enhancement is on
	 * 
	 * @return
	 *         <li>IC_SUCCESS : Success
	 *         <li>IC_NOT:AVAILABLE : The property is not supported by the current
	 *         device
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 */
	public int IC_GetColorEnhancement(Pointer hGrabber, IntByReference OnOff);

	/**
	 * Sets the value of Colorenhancement property Sample:
	 * 
	 * <pre> int OnOFF = 1 IC_GetColorEnhancement(hGrabber, OnOFF); </pre>
	 * @param hGrabber Handle to a grabber object.
	 * @param OnOff
	 * @li 0 : Color enhancement is off
	 * @li 1 : Color enhancement is on
	 * 
	 * @return
	 *         <li>IC_SUCCESS : Success
	 *         <li>IC_NOT:AVAILABLE : The property is not supported by the current
	 *         device
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 */
	public int IC_SetColorEnhancement(Pointer hGrabber, int OnOff);

	/**
	 * Sends a software trigger to the camera. The camera must support external
	 * trigger. The external trigger has to be enabled previously
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @return
	 *         <li>IC_SUCCESS : Success
	 *         <li>IC_NOT:AVAILABLE : The property is not supported by the current
	 *         device
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 * 
	 * @sa IC_EnableTrigger
	 * 
	 */
	public int IC_SoftwareTrigger(Pointer hGrabber);

	/**
	 * Sets a new frame rate.
	 * 
	 * @param hGrabber  Handle to a grabber object.
	 * @param FrameRate The new frame rate.
	 * @return
	 *         <li>IC_SUCCESS : Success
	 *         <li>IC_NOT_AVAILABLE : The property is not supported by the current
	 *         device
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_NOT_IN_LIVEMODE Frame rate can not set, while live video is
	 *         shown. Stop Live video first!
	 */
	public int IC_SetFrameRate(Pointer hGrabber, float FrameRate);

	/**
	 * Retrieves the current frame rate
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @return The current frame rate. If it is 0.0, then frame rates are not
	 *         supported.
	 */
	public float IC_GetFrameRate(Pointer hGrabber);

	/**
	 * Retrieves available frame rates. The count of available frame rates depends
	 * on the used video capture device and the currently used video format. After a
	 * video was changed, the available frame rates usually are changed by the video
	 * capture device too.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Index    Index of the frame rates, starting at 0
	 * @param fps      Pointer to a float variable, that will receive the frame rate
	 *                 of the passed index.
	 * @return IC_SUCCESS, if the frame rate at Index exists, otherwise IC_ERROR,
	 */
	public int IC_GetAvailableFrameRates(Pointer hGrabber, int Index, FloatByReference fps);

	public int IC_SetWhiteBalanceAuto(Pointer hGrabber, int iOnOff);

	/**
	 * Sets the value for white balance red.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Value    Value of the red white balance to be set
	 * @return
	 *         <li>IC_SUCCESS : Success
	 *         <li>IC_NO_HANDLE : Invalid grabber handle
	 *         <li>IC_NO_DEVICE : No video capture device opened
	 *         <li>IC_NOT_AVAILABLE : The property is not supported by the current
	 *         device
	 * 
	 */
	public int IC_SetWhiteBalanceRed(Pointer hGrabber, long Value);

	/**
	 * Sets the value for white balance green.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Value    Value of the green white balance to be set
	 * @return
	 *         <li>IC_SUCCESS : Success
	 *         <li>IC_NO_HANDLE : Invalid grabber handle
	 *         <li>IC_NO_DEVICE : No video capture device opened
	 *         <li>IC_NOT_AVAILABLE : The property is not supported by the current
	 *         device
	 * 
	 */
	public int IC_SetWhiteBalanceGreen(Pointer hGrabber, long Value);

	/**
	 * Sets the value for white balance blue.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Value    Value of the blue white balance to be set
	 * @return
	 *         <li>IC_SUCCESS : Success
	 *         <li>IC_NO_HANDLE : Invalid grabber handle
	 *         <li>IC_NO_DEVICE : No video capture device opened
	 *         <li>IC_NOT_AVAILABLE : The property is not supported by the current
	 *         device
	 * 
	 */
	public int IC_SetWhiteBalanceBlue(Pointer hGrabber, long Value);
	

	/**
	 * Enables or disables the default window size lock of the video window.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Default  0 = disable, custom size can be set, 1 = enable, the
	 *                 standard size, which is video format, is used.
	 * 
	 * @return <li> IC_SUCCESS Success
	 * <li> IC_ERROR Setting of the values failed
	 * <li> IC_NO_PROPERTYSET The property set was not retrieved or is not
	 *         available.
	 * <li> IC_NO_HANDLE Invalid grabber handle
	 * <li> IC_NO_DEVICE No video capture device opened
	 * 
	 */
	public int IC_SetDefaultWindowPosition(Pointer hGrabber, int Default);

	/**
	 * This function Sets the position and size of the video window.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param PosX     Specifies the x-coordinate of the upper left hand corner of
	 *                 the video window. It defaults to 0.
	 * @param PosY     Specifies the y-coordinate of the upper left hand corner of
	 *                 the video window. It defaults to 0.
	 * @param width    Specifies the width of the video window.
	 * @param height   Specifies the height of the video window.
	 * 
	 * @return <li> IC_SUCCESS Success
	 * <li> IC_ERROR Setting of the values failed
	 * <li> IC_DEFAULT_WINDOW_SIZE_SET The property set was not retrieved or is
	 *         not available.
	 * <li> IC_NO_HANDLE Invalid grabber handle
	 * <li> IC_NO_DEVICE No video capture device opened
	 * 
	 */
	public int IC_SetWindowPosition(Pointer hGrabber, int PosX, int PosY, int Width, int Height);
	
	public interface IC_ENUMCB extends Callback {
		void invoke(String Name, Pointer userdata);
	}
	
	/**
	 * Enumerate the available properties of a video capture device.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param cb       Callback functions called by the enum function.
	 * @param data     User data
	 * 
	 * @return IC_SUCCESS No error otherwise an error occurred, e.g. no device
	 *         selected.
	 * 
	 */
	public int IC_enumProperties(Pointer hGrabber, IC_ENUMCB cb, Pointer data);
	

	/**
	 * Enumerate the available interfaces of of a video capture device, property and
	 * element.
	 * 
	 * The string passed to the callback function can contain - Range - Switch -
	 * Button - Mapstrings - AbsoluteValues - Unknown
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property Name of the property
	 * @param Property Name of the element
	 * @param cb       Callback functions called by the enum function.
	 * @param data     User data
	 * 
	 * @return
	 *         <li>IC_SUCCESS No error otherwise an error occurred, e.g. no device
	 *         selected.
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE The passed property in Property is
	 *         not available.
	 */
	public int IC_enumPropertyElementInterfaces(Pointer hGrabber, String Property, String Element, IC_ENUMCB cb,Pointer data);

	/**
	 * Check, whether a property is available.. For a list of properties and
	 * elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 not checked.
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 * 
	 *         Simple call:
	 * 
	 *         <pre>
	 *         if (IC_IsPropertyAvailable(hGrabber, "Brightness", NULL) == IC_SUCCESS) {
	 *         	printf("Brightness is supported\n");
	 *         } else {
	 *         	printf("Brightness is not supported\n");
	 *         }
	 *         </pre>
	 * 
	 *         Complex call for a special element:
	 * 
	 *         <pre>
	 *         if (IC_IsPropertyAvailable(hGrabber, "Trigger", "Software Trigger") == IC_SUCCESS) {
	 *         	printf("Software trigger is supported\n");
	 *         } else {
	 *         	printf("Software trigger is not supported\n");
	 *         }
	 *         </pre>
	 */
	public int IC_IsPropertyAvailable(Pointer hGrabber, String Property, String Element);


	/**
	 * This returns the range of a property. For a list of properties and elements
	 * use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * @param Min      Receives the min value of the property
	 * @param Max      Receives the max value of the property
	 * 
	 * @return
	 * <ul>
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 * </ul>
	 *         <pre>
	HGRABBER hGrabber; // The handle of the grabber object.
	
	int Min;
	int Max;
	int Result = IC_ERROR;
	HGRABBER hGrabber;
	
	if( IC_InitLibrary(0) )
	{
	hGrabber = IC_CreateGrabber();
	IC_OpenVideoCaptureDevice(hGrabber, "DFx 31BG03.H");
	
	if( hGrabber )
	{
	Result = IC_GetPropertyValueRange(hGrabber,"Exposure","Auto Reference", &Min, &Max );
	
	if( Result == IC_SUCCESS )
	printf("Expsure Auto Reference Min %d, Max %d\n", Min, Max);
	
	Result = IC_GetPropertyValueRange(hGrabber,"Exposure",NULL, &Min, &Max );
	
	if( Result == IC_SUCCESS )
	printf("Exposure Value Min %d, Max %d\n", Min, Max);
	}
	IC_ReleaseGrabber( hGrabber );
	 *         </pre>
	 * 
	 * 
	 */
	public int IC_GetPropertyValueRange(Pointer hGrabber, String Property, String Element, IntByReference Min, IntByReference Max);




	/**
	 * This returns the current value of a property. For a list of properties and
	 * elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * @param Value    Receives the value of the property
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_GetPropertyValue(Pointer hGrabber, String Property, String Element, IntByReference Value);


	/**
	 * This sets a new value of a property. For a list of properties and elements
	 * use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * @param Value    Receives the value of the property
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_SetPropertyValue(Pointer hGrabber, String Property, String Element, int Value);


	/**
	 * This returns the range of an absolute value property. Usually it is used for
	 * exposure. a list of properties and elements use the VCDPropertyInspector of
	 * IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * @param Min      Receives the min value of the property
	 * @param Max      Receives the max value of the property
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_GetPropertyAbsoluteValueRange(Pointer hGrabber, String Property, String Element, FloatByReference Min,
			FloatByReference Max);



	/**
	 * This returns the current value of an absolute value property. Usually it is
	 * used for exposure. For a list of properties and elements use the
	 * VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * @param Value    Receives the value of the property
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_GetPropertyAbsoluteValue(Pointer hGrabber, String Property, String Element, FloatByReference Value);


	/**
	 * This sets a new value of an absolute value property. Usually it is used for
	 * exposure. a list of properties and elements use the VCDPropertyInspector of
	 * IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Value".
	 * @param Value    Receives the value of the property
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_SetPropertyAbsoluteValue(Pointer hGrabber, String Property, String Element, float Value);


	/**
	 * This returns the current value of a switch property. Switch properties are
	 * usually used for enabling and disabling of automatics. For a list of
	 * properties and elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Auto".
	 * @param On       Receives the value of the property
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_GetPropertySwitch(Pointer hGrabber, String Property, String Element, IntByReference On);


	/**
	 * This sets the value of a switch property. Switch properties are usually used
	 * for enabling and disabling of automatics. For a list of properties and
	 * elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. Gain, Exposure
	 * @param Element  The type of the interface, e.g. Value, Auto. If NULL, it is
	 *                 "Auto".
	 * @param On       Receives the value of the property
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_SetPropertySwitch(Pointer hGrabber, String Property, String Element, int On);

	/**
	 * This executes the on push on a property. These properties are used for white
	 * balance one push or for software trigger. For a list of properties and
	 * elements use the VCDPropertyInspector of IC Imaging Control.
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. "Trigger"
	 * @param Element  The type of the interface, e.g. "Software Trigger"
	 * @param On       Receives the value of the property
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_PropertyOnePush(Pointer hGrabber, String Property, String Element);

	/**
	 * 
	 * @param hGrabber    Handle to a grabber object.
	 * @param Property    The name of the property, e.g. "Strobe"
	 * @param Element     The type of the interface, e.g. "Mode"
	 * @param StringCount Receives the count of strings, that is modes, available
	 * @param Strings     pointer to an array of char*, that will contain the mode
	 *                    strings. The array size should be StringCount * 20.
	 *                    Parameter can be null in order to query the number of
	 *                    strings
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_GetPropertyMapStrings(Pointer hGrabber, String Property, String Element, IntByReference StringCount, String[] Strings);

	/**
	 * Return the current set string of a mapstring interface
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. "Strobe"
	 * @param Element  The type of the interface, e.g. "Mode"
	 * @param String   pointer to a char*. Size should be atleast 50. There is no
	 *                 check! This contains the result.
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_GetPropertyMapString(Pointer hGrabber, String Property, String Element, String String);

	/**
	 * Set the string of a mapstring interface
	 * 
	 * @param hGrabber Handle to a grabber object.
	 * @param Property The name of the property, e.g. "Strobe"
	 * @param Element  The type of the interface, e.g. "Mode"
	 * @param String   pointer to a char*. Size should be at least 50. There is no
	 *                 check! This contains the result.
	 * 
	 * @return
	 *         <li>IC_SUCCESS Success
	 *         <li>IC_NO_HANDLE Invalid grabber handle
	 *         <li>IC_NO_DEVICE No video capture device opened
	 *         <li>IC_PROPERTY_ITEM_NOT_AVAILABLE A requested property item is not
	 *         available
	 *         <li>IC_PROPERTY_ELEMENT_NOT_AVAILABLE A requested element of a given
	 *         property item is not available
	 *         <li>IC_PROPERTY_ELEMENT_WRONG_INTERFACE requested element has not the
	 *         interface, which is needed.
	 */
	public int IC_SetPropertyMapString(Pointer hGrabber, String Property, String Element, String string);
}