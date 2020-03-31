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
public class CCBlur extends CCImageProcessor{

	@CCProperty(name = "size", min = 1, max = 50)
	private int _cSize = 1;
	
	@Override
	public Mat implementation(Mat...theSources) {
		blur(theSources[0].clone(), theSources[0], new Size(_cSize,_cSize));
		return theSources[0];
	}

	
}
