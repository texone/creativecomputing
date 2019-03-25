package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_FRAME_COUNT;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_POS_AVI_RATIO;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_POS_FRAMES;
import static org.bytedeco.javacpp.opencv_videoio.CV_CAP_PROP_POS_MSEC;

import org.bytedeco.javacpp.opencv_videoio.VideoCapture;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCCVVideoPlayer extends CCCVVideoIn{
	
	@CCProperty(name = "loop start", min = 0, max = 1)
	private double _cLoopStart = 0;
	@CCProperty(name = "loop length", min = 0, max = 1)
	private double _cLoopLength = 1;
	@CCProperty(name = "pause")
	private boolean _cPause = false;

	
	public CCCVVideoPlayer(String theFileName) {
		super(new VideoCapture(theFileName));
	}

	/**
	 * Current position of the video file in milliseconds or video capture
	 * timestamp.
	 * 
	 * @return Current position of the video file in milliseconds or video capture
	 *         timestamp.
	 */
	public double positionMSec() {
		return _myCapture.get(CV_CAP_PROP_POS_MSEC);
	}

	/**
	 * Set the current position of the video file in milliseconds or video capture
	 * timestamp.
	 * 
	 * @param thePosition Current position of the video file in milliseconds or
	 *                    video capture timestamp.
	 */
	public void positionMSec(double thePosition) {
		_myCapture.set(CV_CAP_PROP_POS_MSEC, thePosition);
	}

	/**
	 * 0-based index of the frame to be decoded/captured next.
	 * 
	 * @return 0-based index of the frame to be decoded/captured next.
	 */
	public double positionFrames() {
		return _myCapture.get(CV_CAP_PROP_POS_FRAMES);
	}

	/**
	 * set the 0-based index of the frame to be decoded/captured next.
	 * 
	 * @param 0-based index of the frame to be decoded/captured next.
	 */
	public void positionFrames(double thePosition) {
		_myCapture.set(CV_CAP_PROP_POS_FRAMES, thePosition);
	}

	/**
	 * Relative position of the video file: 0 - start of the film, 1 - end of the
	 * film.
	 * 
	 * @return Relative position of the video file: 0 - start of the film, 1 - end
	 *         of the film.
	 */
	public double positionRatio() {
		return _myCapture.get(CV_CAP_PROP_POS_AVI_RATIO);
	}

	/**
	 * Set the relative position of the video file: 0 - start of the film, 1 - end
	 * of the film.
	 * 
	 * @param Relative position of the video file: 0 - start of the film, 1 - end of
	 *                 the film.
	 */
	public void positionRatio(double thePosition) {
		_myCapture.set(CV_CAP_PROP_POS_AVI_RATIO, thePosition);
	}

	/**
	 * Number of frames in the video file.
	 * 
	 * @return Number of frames in the video file.
	 */
	public double frameCount() {
		return _myCapture.get(CV_CAP_PROP_FRAME_COUNT);
	}
	
	private boolean _myLastPause = false;
	private double _myRatio = 0;
	
	@Override
	protected void updateSettings() {
		double myEnd = CCMath.min(_cLoopStart+ _cLoopLength, (frameCount() - 2) / frameCount());
		if(_cPause != _myLastPause) {
			_myRatio = positionRatio();
		}
		_myLastPause = _cPause;
		
		if(_cPause) {
			positionRatio(_myRatio);
		}
		if(positionRatio() >= myEnd) {
			positionRatio(_cLoopStart);
		}
	}
	
}