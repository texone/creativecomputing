package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_core.BORDER_CONSTANT;
import static org.bytedeco.javacpp.opencv_core.BORDER_ISOLATED;
import static org.bytedeco.javacpp.opencv_core.BORDER_REFLECT;
import static org.bytedeco.javacpp.opencv_core.BORDER_REFLECT_101;
import static org.bytedeco.javacpp.opencv_core.BORDER_REPLICATE;
import static org.bytedeco.javacpp.opencv_core.BORDER_TRANSPARENT;
import static org.bytedeco.javacpp.opencv_core.BORDER_WRAP;
import static org.bytedeco.javacpp.opencv_imgproc.CV_SHAPE_CROSS;
import static org.bytedeco.javacpp.opencv_imgproc.CV_SHAPE_ELLIPSE;
import static org.bytedeco.javacpp.opencv_imgproc.CV_SHAPE_RECT;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_BLACKHAT;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_CLOSE;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_DILATE;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_ERODE;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_GRADIENT;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_HITMISS;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_OPEN;
import static org.bytedeco.javacpp.opencv_imgproc.MORPH_TOPHAT;
import static org.bytedeco.javacpp.opencv_imgproc.getStructuringElement;
import static org.bytedeco.javacpp.opencv_imgproc.morphologyDefaultBorderValue;
import static org.bytedeco.javacpp.opencv_imgproc.morphologyEx;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

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
	 * Various border types
	 * @author chris
	 *
	 */
	public static enum CCBorderType{
		/**
		 * iiiiii|abcdefgh|iiiiiii
		 */
		CONSTANT ( BORDER_CONSTANT  ),
		/**
		 * aaaaaa|abcdefgh|hhhhhhh
		 */
		REPLICATE ( BORDER_REPLICATE  ),
		/**
		 * fedcba|abcdefgh|hgfedcb
		 */
		REFLECT( BORDER_REFLECT   ),
		/**
		 * cdefgh|abcdefgh|abcdefg
		 */
		WRAP ( BORDER_WRAP  ),
		/**
		 * gfedcb|abcdefgh|gfedcba
		 */
		REFLECT_101 ( BORDER_REFLECT_101  ),
		/**
		 * uvwxyz|abcdefgh|ijklmno
		 */
		TRANSPARENT ( BORDER_TRANSPARENT  ),
		/**
		 * 
		 */
		ISOLATED ( BORDER_ISOLATED  );
		
		public final int id;
		
		private CCBorderType(int theID) {
			id = theID;
		}
	}

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
	public Mat implementation(Mat...theSources) {
		Mat myStructure = structuringElement(_cStructureShape, _cStructureSize);
		morphologyEx(theSources[0], theSources[0], _cMorphType.id, myStructure, null, _cIterations, _cBorderType.id, morphologyDefaultBorderValue());
		return theSources[0];
	}

}
