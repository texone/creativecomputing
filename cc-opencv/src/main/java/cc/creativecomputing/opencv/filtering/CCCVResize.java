package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_imgproc.INTER_AREA;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_CUBIC;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_LANCZOS4;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_LINEAR;
import static org.bytedeco.javacpp.opencv_imgproc.INTER_NEAREST;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

/**
 * Blurs an image using the normalized box filter. 
 * @author chris
 *
 */
public class CCCVResize extends CCImageProcessor{
	
	/**
	 * Shape of the structuring element
	 * 
	 * @author chris
	 *
	 */
	public static enum CCInterpolationType {
		/**
		 * a nearest-neighbor interpolation
		 */
		NEAREST(INTER_NEAREST),
		/**
		 * a bilinear interpolation (used by default)
		 */
		LINEAR(INTER_LINEAR),
		/**
		 * Resampling using pixel area relation. It may be a preferred method for image decimation, as it gives moire’-free results. 
		 * But when the image is zoomed, it is similar to the INTER_NEAREST method.
		 */
		AREA(INTER_AREA),
		/**
		 * a bicubic interpolation over 4x4 pixel neighborhood
		 */
		CUBIC(INTER_CUBIC),
		/**
		 * a Lanczos interpolation over 8x8 pixel neighborhood
		 */
		LANCZOS4(INTER_LANCZOS4);

		public final int id;

		private CCInterpolationType(int theID) {
			id = theID;
		}
	}

	@CCProperty(name = "scale", min = 0.1, max = 1)
	private double _cScale = 1;
	
	private double _myScaleX;
	private double _myScaleY;
	
	@CCProperty(name = "morph type")
	protected CCInterpolationType _cInterpolationType = CCInterpolationType.NEAREST;
	
	@Override
	public Mat implementation(Mat theSource) {
		int myWidth = (int)(theSource.cols() * _cScale) / 4 * 4;
		int myHeight = (int)(theSource.rows() * _cScale) / 4 * 4;
		_myScaleX = (double)myWidth / theSource.cols();
		_myScaleY = (double)myHeight / theSource.rows();
		resize(theSource, theSource, new Size(myWidth, myHeight), _cScale, _cScale, _cInterpolationType.id);
		return theSource;
	}

	public double scaleX() {
		return _cBypass ? 1 : _myScaleX;
	}


	public double scaleY() {
		return _cBypass ? 1 : _myScaleY;
	}
}
