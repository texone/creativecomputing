package cc.creativecomputing.opencv.filtering;

import org.bytedeco.javacpp.opencv_core.Mat;
import static org.bytedeco.javacpp.opencv_video.*;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

public class CCBackgroundSubtractorKNN extends CCImageProcessor{
	
	@CCProperty(name = "detect shadows")
	private boolean _cDetectShadows = false;
	@CCProperty(name = "distance", min = 1, max = 100)
	private double _cDistance = 20;
	@CCProperty(name = "history", min = 1, max = 1000)
	private int _cHistory = 500;
	@CCProperty(name = "learning rate", min = -1, max = 1)
	private double _cLearningRate = 0;
	
	private BackgroundSubtractorKNN _myBackgroundSubtractorKNN;
	
	
	public CCBackgroundSubtractorKNN() {
		_myBackgroundSubtractorKNN = createBackgroundSubtractorKNN();
	}

	@Override
	public Mat implementation(Mat theSource) {
		_myBackgroundSubtractorKNN.setDetectShadows(_cDetectShadows);
		_myBackgroundSubtractorKNN.setDist2Threshold(_cDistance * _cDistance);
		_myBackgroundSubtractorKNN.setHistory(_cHistory);
		_myBackgroundSubtractorKNN.apply(theSource, theSource, _cLearningRate);
		return theSource;
	}

}
