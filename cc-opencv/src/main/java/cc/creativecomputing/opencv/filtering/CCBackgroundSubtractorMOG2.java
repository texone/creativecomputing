package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_video.createBackgroundSubtractorMOG2;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_video.BackgroundSubtractorMOG2;

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
	public Mat implementation(Mat...theSources) {
		_myBackgroundSubtractor.setDetectShadows(_cDetectShadows);
		_myBackgroundSubtractor.setVarThreshold(_cVarThreshold);
		_myBackgroundSubtractor.setHistory(_cHistory);
		_myBackgroundSubtractor.apply(theSources[0], theSources[0],_cLearningRate);
		return theSources[0];
	}

}
