package cc.creativecomputing.opencv.filtering;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_core.*;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

/**
 * Flips a 2D array around vertical, horizontal, or both axes.
 * 
 * a flag to specify how to flip the array; 0 means flipping around the x-axis and positive value (for example, 1) means flipping around y-axis. Negative value (for example, -1) means flipping around both axes.
 * 
 * @author chris
 *
 */
public class CCRotate90 extends CCImageProcessor{

	/**
	 * Various border types
	 * @author chris
	 *
	 */
	public static enum CCRotateFlag{
		/**
		 * Rotate 90 degrees clockwise
		 */
		ROTATE_90_CLOCKWISE ( org.bytedeco.javacpp.opencv_core.ROTATE_90_CLOCKWISE   ),
		/**
		 * Rotate 180 degrees clockwise
		 */
		ROTATE_180 ( org.bytedeco.javacpp.opencv_core.ROTATE_180   ),
		/**
		 * Rotate 270 degrees clockwise
		 */
		ROTATE_90_COUNTERCLOCKWISE( org.bytedeco.javacpp.opencv_core.ROTATE_90_COUNTERCLOCKWISE    );
		
		public final int id;
		
		private CCRotateFlag(int theID) {
			id = theID;
		}
	}
	
	@CCProperty(name = "rotate flag")
	private CCRotateFlag _cRotateFlag = CCRotateFlag.ROTATE_90_CLOCKWISE;
	
	@Override
	public Mat implementation(Mat...theSources) {
		rotate(theSources[0].clone(), theSources[0], _cRotateFlag.id);
		return theSources[0];
	}

	
}
