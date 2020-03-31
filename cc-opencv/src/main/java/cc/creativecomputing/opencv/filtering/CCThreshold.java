package cc.creativecomputing.opencv.filtering;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

/**
 * Blurs an image using the normalized box filter. 
 * @author chris
 *
 */
public class CCThreshold extends CCImageProcessor{
	
	/**
	 * type of the threshold operation 
	 * 
	 * @author chris
	 *
	 */
	public static enum CCThresholdType {

		BINARY(THRESH_BINARY ),
		CROSS(THRESH_BINARY_INV),
		TRUNC(THRESH_TRUNC ),
		TOZERO(THRESH_TOZERO ),
		TOZERO_INV(THRESH_TOZERO_INV ),
		MASK(THRESH_MASK ),
		OTSU(THRESH_OTSU ),
		TRIANGLE(THRESH_TRIANGLE );

		public final int id;

		private CCThresholdType(int theID) {
			id = theID;
		}
	}
	
	@CCProperty(name = "threshold type")
	private CCThresholdType _cThresholdType = CCThresholdType.BINARY;

	@CCProperty(name = "threshold", min = 0, max = 255)
	private double  _cThreshold = 1;
	@CCProperty(name = "max value", min = 0, max = 255)
	private double  _cMaxValue = 1;
	
	@Override
	public Mat implementation(Mat...theSources) {
		threshold(theSources[0], theSources[0], _cThreshold, _cMaxValue, _cThresholdType.id);
		return theSources[0];
	}

	
}
