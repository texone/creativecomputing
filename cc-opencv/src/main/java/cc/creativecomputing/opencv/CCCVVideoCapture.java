package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_BRIGHTNESS;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_CONTRAST;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_EXPOSURE;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FPS;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_COUNT;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_WIDTH;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_GAIN;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_HUE;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_SATURATION;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_TEMPERATURE;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

import cc.creativecomputing.core.CCProperty;

public class CCCVVideoCapture extends CCCVVideoIn{
	
	@CCProperty(name = "temperatur", min = 2800, max = 10000)
	private double _cTemperature = 4800;
	@CCProperty(name = "brightness", min = 0, max = 255)
	private double _cBrightness = 30;
	@CCProperty(name = "contrast", min = 0, max = 10)
	private double _cContrast = 5;
	@CCProperty(name = "saturation", min = 0, max = 255)
	private double _cSaturation = 100;
	@CCProperty(name = "exposure", min = -11, max = 1)
	private double _cExpousure = 0;

	public CCCVVideoCapture() {
		super(new VideoCapture(0));
	}
	
	public CCCVVideoCapture(int theID) {
		super(new VideoCapture(theID));
	}

	/**
	 * temperature of the image (only for cameras).
	 * 
	 * @return temperature of the image (only for cameras).
	 */
	public double temperature() {
		return _myCapture.get(CV_CAP_PROP_TEMPERATURE);
	}
	
	/**
	 * temperature of the image (only for cameras).
	 * 
	 * @return temperature of the image (only for cameras).
	 */
	public void temperature(double theTemperature) {
		_myCapture.set(CV_CAP_PROP_TEMPERATURE, theTemperature);
	}

	/**
	 * Brightness of the image (only for cameras).
	 * 
	 * @return brightness
	 */
	public double brightness() {
		return _myCapture.get(CV_CAP_PROP_BRIGHTNESS);
	}

	/**
	 * Set the Brightness of the image (only for cameras).
	 * 
	 * @param theBrightness Brightness of the image (only for cameras).
	 */
	public void brightness(double theBrightness) {
		_myCapture.set(CV_CAP_PROP_BRIGHTNESS, theBrightness);
	}

	/**
	 * Contrast of the image (only for cameras).
	 * 
	 * @return Contrast of the image (only for cameras).
	 */
	public double contrast() {
		return _myCapture.get(CV_CAP_PROP_CONTRAST);
	}

	/**
	 * Set the Contrast of the image (only for cameras).
	 * 
	 * @param theContrast Contrast of the image (only for cameras).
	 */
	public void contrast(double theContrast) {
		_myCapture.set(CV_CAP_PROP_CONTRAST, theContrast);
	}

	/**
	 * Saturation of the image (only for cameras).
	 * 
	 * @return Saturation of the image (only for cameras).
	 */
	public double saturation() {
		return _myCapture.get(CV_CAP_PROP_SATURATION);
	}

	/**
	 * Set the Saturation of the image (only for cameras).
	 * 
	 * @param theSaturation Saturation of the image (only for cameras).
	 */
	public void saturation(double theSaturation) {
		_myCapture.set(CV_CAP_PROP_SATURATION, theSaturation);
	}

	/**
	 * Hue of the image (only for cameras).
	 * 
	 * @return Hue of the image (only for cameras).
	 */
	public double hue() {
		return _myCapture.get(CV_CAP_PROP_HUE);
	}

	/**
	 * Set the Hue of the image (only for cameras).
	 * 
	 * @param theHue Hue of the image (only for cameras).
	 */
	public void hue(double theHue) {
		_myCapture.set(CV_CAP_PROP_HUE, theHue);
	}

	/**
	 * Gain of the image (only for cameras).
	 * 
	 * @return Gain of the image (only for cameras).
	 */
	public double gain() {
		return _myCapture.get(CV_CAP_PROP_GAIN);
	}

	/**
	 * Set the Gain of the image (only for cameras).
	 * 
	 * @param theGain Gain of the image (only for cameras).
	 */
	public void gain(double theGain) {
		_myCapture.set(CV_CAP_PROP_GAIN, theGain);
	}

	/**
	 * Belichtung -11 to 1
	 * Exposure (only for cameras).
	 * 
	 * @return Exposure (only for cameras).
	 */
	public double exposure() {
		return _myCapture.get(CV_CAP_PROP_EXPOSURE);
	}

	/**
	 * Set the Exposure
	 * 
	 * @param theExposure exposure
	 */
	public void exposure(double theExposure) {
//		_myCapture.set(CV_CAP_PROP_EXPOSURE, theExposure);
	}

	/*
	 * CV_CAP_PROP_FPS Frame rate. CV_CAP_PROP_FOURCC 4-character code of codec.
	 * CV_CAP_PROP_FORMAT Format of the Mat objects returned by retrieve() .
	 * CV_CAP_PROP_MODE Backend-specific value indicating the current capture mode.
	 * 
	 * CV_CAP_PROP_CONVERT_RGB Boolean flags indicating whether images should be
	 * converted to RGB. CV_CAP_PROP_WHITE_BALANCE_U The U value of the whitebalance
	 * setting (note: only supported by DC1394 v 2.x backend currently)
	 * CV_CAP_PROP_WHITE_BALANCE_V The V value of the whitebalance setting (note:
	 * only supported by DC1394 v 2.x backend currently) CV_CAP_PROP_RECTIFICATION
	 * Rectification flag for stereo cameras (note: only supported by DC1394 v 2.x
	 * backend currently) CV_CAP_PROP_ISO_SPEED The ISO speed of the camera (note:
	 * only supported by DC1394 v 2.x backend currently) CV_CAP_PROP_BUFFERSIZE
	 * Amount of frames stored in internal buffer memory (note: only supported by
	 * DC1394 v 2.x backend currently)
	 */
	
	/**
	 * Open video file or a capturing device for video capturing
	 * The methods first calls VideoCapture::release() to close the already opened file or camera.
	 * @param theDevice id of the opened video capturing device (i.e. a camera index).
	 */
	public void open(int theDevice) {
		_myCapture.open(theDevice);
	}
	
	
	
	private double _myLastExposure = -100;
	
	@Override
	protected void updateSettings() {
//		if(_cExpousure != _myLastExposure) {
//			exposure(_cExpousure);
//			brightness(0);
//			_myLastExposure = _cExpousure;
//			return;
//		}
//		temperature(_cTemperature);
//		contrast(_cContrast);
//		saturation(_cSaturation);
//		brightness(_cBrightness);
	}
	
	
	
}