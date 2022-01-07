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
public class CCFlip extends CCImageProcessor{

	@CCProperty(name = "flip x")
	private boolean _cFlipX = false;
	
	@CCProperty(name = "flip y")
	private boolean _cFlipY = false;
	
	@Override
	public Mat implementation(Mat...theSources) {
		int myFlip = 0;
		if(_cFlipY) {
			if(_cFlipX) {
				myFlip = -1;
			}else {
				myFlip = 0;
			}
		}else {
			if(_cFlipX) {
				myFlip = 1;
			}else {
				return theSources[0];
			}
		}
		
		flip(theSources[0].clone(), theSources[0], myFlip);
		return theSources[0];
	}

	
}
