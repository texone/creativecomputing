package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_imgproc.erode;
import static org.bytedeco.javacpp.opencv_imgproc.morphologyDefaultBorderValue;

import org.bytedeco.javacpp.opencv_core.Mat;

/**
 * This operation is the sister of dilation. It computes a local minimum over the area of given kernel.
 * <p>
 * The function supports the in-place mode. Erosion can be applied several ( iterations ) times. In case of multi-channel images, each channel is processed independently.
 * 
 * @author chris
 *
 */
public class CCErosion extends CCMorphologyFilter {

	@Override
	public Mat implementation(Mat...theSources) {
		Mat myStructure = structuringElement(_cStructureShape, _cStructureSize);
		erode(theSources[0], theSources[0], myStructure, null, _cIterations, _cBorderType.id, morphologyDefaultBorderValue());
		return theSources[0];
	}

}
