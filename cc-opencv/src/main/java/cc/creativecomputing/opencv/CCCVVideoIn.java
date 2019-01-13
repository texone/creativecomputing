package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FPS;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_COUNT;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_HEIGHT;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_WIDTH;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.events.CCTriggerEvent;
import cc.creativecomputing.core.logging.CCLog;

public abstract class CCCVVideoIn {
	
	public static interface CCCVVideoInEvent{
		public void event(Mat theMat);
	}
	
	protected VideoCapture _myCapture;
	
	private Mat _myCurrentMat;
	
	public CCListenerManager<CCCVVideoInEvent> events = CCListenerManager.create(CCCVVideoInEvent.class);
	
	@CCProperty(name = "active", readBack = true)
	private boolean _cActive = true;
	
	protected CCCVVideoIn(VideoCapture theCapture) {
		_myCapture = theCapture;
	}
	
	public void isActive(boolean theIsActive) {
		_cActive = theIsActive;
	}
	
	public boolean isActive() {
		return _cActive;
	}
	
	public void start() {
		_myCurrentMat = read();
		
		new Thread(()->{
			while(true) {
				if(_cActive) {
					_myCurrentMat = read();
					events.proxy().event(_myCurrentMat);
				}
				try {
					Thread.sleep(30);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}
	
	public Mat mat() {
		return _myCurrentMat;
	}
	
	/**
	 * Width of the frames in the video stream.
	 * 
	 * @return Width of the frames in the video stream.
	 */
	public double frameWidth() {
		return _myCapture.get(CV_CAP_PROP_FRAME_WIDTH);
	}

	/**
	 * Width of the frames in the video stream.
	 * 
	 * @param theFrameWidth Width of the frames in the video stream.
	 */
	public void frameWidth(double theFrameWidth) {
		_myCapture.set(CV_CAP_PROP_FRAME_WIDTH, theFrameWidth);
	}

	/**
	 * Height of the frames in the video stream.
	 * 
	 * @return Height of the frames in the video stream.
	 */
	public double frameHeight() {
		return _myCapture.get(CV_CAP_PROP_FRAME_HEIGHT);
	}

	/**
	 * Set the Height of the frames in the video stream.
	 * 
	 * @param theHeight Height of the frames in the video stream.
	 */
	public void frameHeight(double theHeight) {
		_myCapture.set(CV_CAP_PROP_FRAME_HEIGHT, theHeight);
	}

	/**
	 * Frame rate.
	 * 
	 * @return Frame rate.
	 */
	public double frameRate() {
		return _myCapture.get(CV_CAP_PROP_FPS);
	}

	/**
	 * Set the Frame rate.
	 * 
	 * @param theFrameRate Frame rate.
	 */
	public void frameRate(double theFrameRate) {
		_myCapture.set(CV_CAP_PROP_FPS, theFrameRate);
	}

	/**
	 * Returns true if video capturing has been initialized already.
	 * @return Returns true if video capturing has been initialized already.
	 */
	public boolean isOpened() {
		return _myCapture.isOpened();
	}
	
	/**
	 * Closes video file or capturing device.
	 */
	public void release() {
		_myCapture.release();
	}
	
	/**
	 * Grabs the next frame from video file or capturing device.
	 * <p>
	 * The methods/functions grab the next frame from video file or camera and return true (non-zero) in the case of success.
	 * <p>
	 * The primary use of the function is in multi-camera environments, especially when the cameras do not have hardware synchronization. 
	 * That is, you call VideoCapture::grab() for each camera and after that call the slower method VideoCapture::retrieve() to decode 
	 * and get frame from each camera. This way the overhead on demosaicing or motion jpeg decompression etc. is eliminated and the 
	 * retrieved frames from different cameras will be closer in time.
	 * <p>
	 * Also, when a connected camera is multi-head (for example, a stereo camera or a Kinect device), the correct way of retrieving 
	 * data from it is to call VideoCapture::grab first and then call VideoCapture::retrieve() one or more times with different values 
	 * of the channel parameter.
	 * @return true in the case of success.
	 */
	public boolean grab() {
		return _myCapture.grab();
	}
	
	protected void updateSettings() {}
	
	/**
	 * The methods/functions decode and return the just grabbed frame. If no frames has been grabbed (camera has been disconnected, 
	 * or there are no more frames in video file), the methods return false and the functions return NULL pointer.
	 * @param theMat
	 * @param theChannel
	 * @return true in the case of success.
	 */
	public boolean retrieve(Mat theMat, int theChannel) {
		updateSettings();
		return _myCapture.retrieve(theMat, theChannel);
	}

	/**
	 * Grabs, decodes and returns the next video frame.
	 * The methods/functions combine VideoCapture::grab() and VideoCapture::retrieve() in one call. 
	 * This is the most convenient method for reading video files or capturing data from decode and return the just grabbed frame.
	 *  If no frames has been grabbed (camera has been disconnected, or there are no more frames in video file),
	 *  the methods return false and the functions return NULL pointer.
	 * @param theMat
	 * @return true in the case of success.
	 */
	public boolean read(Mat theMat) {
		updateSettings();
		return _myCapture.read(theMat);
	}
	
	

	public Mat read() {
		Mat myResult = new Mat();
		read(myResult);
		return myResult;
	}
}
