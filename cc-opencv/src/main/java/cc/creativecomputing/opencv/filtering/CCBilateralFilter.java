package cc.creativecomputing.opencv.filtering;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * Applies the bilateral filter to an image.
 * <p>
 * The function applies bilateral filtering to the input image, as described in
 * http://www.dai.ed.ac.uk/CVonline/LOCAL_COPIES/MANDUCHI1/Bilateral_Filtering.html
 * bilateralFilter can reduce unwanted noise very well while keeping edges
 * fairly sharp. However, it is very slow compared to most filters.
 * <p>
 * Sigma values: For simplicity, you can set the 2 sigma values to be the same.
 * If they are small (< 10), the filter will not have much effect, whereas if
 * they are large (> 150), they will have a very strong effect, making the image
 * look “cartoonish”.
 * <p>
 * Filter size: Large filters (d > 5) are very slow, so it is recommended to use
 * d=5 for real-time applications, and perhaps d=9 for offline applications that
 * need heavy noise filtering.
 * <p>
 * This filter does not work inplace.
 * 
 * @author chris
 *
 */
public class CCBilateralFilter extends CCImageProcessor {

	@CCProperty(name = "sigma color", min = 0, max = 150)
	private double _cSigmaColor = 1;

	@CCProperty(name = "sigma space", min = 0, max = 150)
	private double _cSigmaSpace = 1;

	private int d = 0;

	@Override
	public void implementation(Mat theSource) {
		bilateralFilter(theSource, theSource, d, _cSigmaColor, _cSigmaSpace);
	}

}
