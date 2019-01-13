package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_imgproc.*;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;
import cc.creativecomputing.opencv.CCImageProcessor.CCBorderType;
import cc.creativecomputing.opencv.filtering.CCMorphologyFilter.CCMorphShape;

/**
 * In short: A set of operations that process images based on shapes.
 * Morphological operations apply a structuring element to an input image and
 * generate an output image. The most basic morphological operations are Erosion
 * and Dilation.
 * <p>
 * They have a wide array of uses, i.e. :
 * <ul>
 * <li>Removing noise</li>
 * <li>Isolation of individual elements and joining disparate elements in an
 * image.</li>
 * <li>Finding of intensity bumps or holes in an image</li>
 * </ul>
 * Performs advanced morphological transformations.
 * <p>
 * The function cv::morphologyEx can perform advanced morphological
 * transformations using an erosion and dilation as basic operations.
 * <p>
 * Any of the operations can be done in-place. In case of multi-channel images,
 * each channel is processed independently.
 * 
 * @author chris
 *
 */
public class CCMorphologyFilter extends CCImageProcessor {

	/**
	 * Shape of the structuring element
	 * 
	 * @author chris
	 *
	 */
	public static enum CCMorphShape {
	/**
	 * a rectangular structuring element:
	 */
	RECT(CV_SHAPE_RECT),
	/**
	 * a cross-shaped structuring element:
	 */
	CROSS(CV_SHAPE_CROSS),
	/**
	 * an elliptic structuring element, that is, a filled ellipse inscribed into the
	 * rectangle Rect(0, 0, esize.width, 0.esize.height)
	 */
	ELLIPSE(CV_SHAPE_ELLIPSE);

		public final int id;

		private CCMorphShape(int theID) {
			id = theID;
		}
	}

	@CCProperty(name = "morph shape")
	protected CCMorphShape _cStructureShape = CCMorphShape.RECT;

	/**
	 * Shape of the structuring element
	 * 
	 * @author chris
	 *
	 */
	public static enum CCMorphType {
		/**
		 * erosion
		 */
		ERODE(MORPH_ERODE ),
		/**
		 * dilation
		 */
		DILATE(MORPH_DILATE ),
		/**
		 * an opening operation
		 */
		OPEN(MORPH_OPEN ),
		/**
		 * a closing operation
		 */
		CLOSE(MORPH_CLOSE ),
		/**
		 * a morphological gradient 
		 */
		GRADIENT(MORPH_GRADIENT ),
		/**
		 * top hat
		 */
		TOPHAT(MORPH_TOPHAT ),
		/**
		 * black hat
		 */
		BLACKHAT(MORPH_BLACKHAT ),
		/**
		 * hit or miss
		 */
		HITMISS(MORPH_HITMISS );

		public final int id;

		private CCMorphType(int theID) {
			id = theID;
		}
	}

	@CCProperty(name = "morph type")
	protected CCMorphType _cMorphType = CCMorphType.ERODE;

	@CCProperty(name = "structure size", min = 1, max = 10)
	protected int _cStructureSize = 1;

	@CCProperty(name = "iterations", min = 1, max = 10)
	protected int _cIterations = 1;

	@CCProperty(name = "border type")
	protected CCBorderType _cBorderType = CCBorderType.TRANSPARENT;

	protected Mat structuringElement(CCMorphShape theShape, int theSize) {
		return getStructuringElement(theShape.id, new Size(theSize * 2 + 1, theSize * 2 + 1));
	}

	@Override
	public Mat implementation(Mat theSource) {
		Mat myStructure = structuringElement(_cStructureShape, _cStructureSize);
		morphologyEx(theSource, theSource, _cMorphType.id, myStructure, null, _cIterations, _cBorderType.id, morphologyDefaultBorderValue());
		return theSource;
	}

}
