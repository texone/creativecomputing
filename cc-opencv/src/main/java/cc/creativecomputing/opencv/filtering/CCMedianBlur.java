package cc.creativecomputing.opencv.filtering;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

/**
 * Blurs an image using the median filter. 
 * @author chris
 *
 */
public class CCMedianBlur extends CCImageProcessor{

	@CCProperty(name = "size", min = 1, max = 10)
	private int _cSize = 1;
	
	@Override
	public void implementation(Mat theSource) {
		medianBlur(theSource.clone(), theSource, _cSize * 2 + 1);
	}

	
}
