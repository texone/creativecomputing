package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_core.absdiff;
import static org.bytedeco.javacpp.opencv_core.subtract;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.opencv.CCImageProcessor;

/**
 * Blurs an image using the normalized box filter. 
 * @author chris
 *
 */
public class CCAbsDifference extends CCImageProcessor{
	
	@Override
	public Mat implementation(Mat...theSources) {
		Mat myResult = new Mat();
		absdiff(theSources[0], theSources[1], myResult);
		return myResult;
	}
}
