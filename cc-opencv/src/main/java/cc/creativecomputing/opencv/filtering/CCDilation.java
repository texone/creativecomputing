package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_imgproc.dilate;
import static org.bytedeco.javacpp.opencv_imgproc.morphologyDefaultBorderValue;

import org.bytedeco.javacpp.opencv_core.Mat;

/**
 * Dilates an image by using a specific structuring element. 
 * <p>
 * The function supports the in-place mode. Erosion can be applied several ( iterations ) times. In case of multi-channel images, each channel is processed independently.
 * 
 * @author chris
 *
 */
public class CCDilation extends CCMorphologyFilter {

	@Override
	public Mat implementation(Mat...theSources) {
		Mat myStructure = structuringElement(_cStructureShape, _cStructureSize);
		dilate(theSources[0], theSources[0], myStructure, null, _cIterations, _cBorderType.id, morphologyDefaultBorderValue());
		return theSources[0];
	}

}
