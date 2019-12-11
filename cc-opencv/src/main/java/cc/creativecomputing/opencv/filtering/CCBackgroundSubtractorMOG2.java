package cc.creativecomputing.opencv.filtering;

import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_video.*;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

public class CCBackgroundSubtractorMOG2 extends CCImageProcessor{
	
	@CCProperty(name = "detect shadows")
	private boolean _cDetectShadows = false;
	@CCProperty(name = "var threshold", min = 1, max = 100)
	private double _cVarThreshold = 16;
	@CCProperty(name = "learning rate", min = -1, max = 1)
	private double _cLearningRate = 16;
	@CCProperty(name = "history", min = 1, max = 1000)
	private int _cHistory = 500;
	
	private BackgroundSubtractorMOG2 _myBackgroundSubtractor;
	
	
	public CCBackgroundSubtractorMOG2() {
		_myBackgroundSubtractor = createBackgroundSubtractorMOG2();
	}

	@Override
	public Mat implementation(Mat theSource) {
		_myBackgroundSubtractor.setDetectShadows(_cDetectShadows);
		_myBackgroundSubtractor.setVarThreshold(_cVarThreshold);
		_myBackgroundSubtractor.setHistory(_cHistory);
		_myBackgroundSubtractor.apply(theSource, theSource,_cLearningRate);
		return theSource;
	}

}
