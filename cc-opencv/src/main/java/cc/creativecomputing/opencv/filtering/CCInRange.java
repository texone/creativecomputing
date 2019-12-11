package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_core.CV_32SC4;
import static org.bytedeco.javacpp.opencv_core.inRange;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Scalar;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

/**
 * Blurs an image using the normalized box filter. 
 * @author chris
 *
 */
public class CCInRange extends CCImageProcessor{

	
	@CCProperty(name = "min hue", min = 0, max = 180)
	private int _cMinHue = 0;
	@CCProperty(name = "max hue", min = 0, max = 180)
	private int _cMaxHue = 180;
	
	@CCProperty(name = "min saturation", min = 0, max = 255)
	private int _cMinSaturation = 0;
	@CCProperty(name = "max saturation", min = 0, max = 255)
	private int _cMaxSaturation = 255;

	@CCProperty(name = "min brightness", min = 0, max = 255)
	private int _cMinBrightness = 0;
	@CCProperty(name = "max brightness", min = 0, max = 255)
	private int _cMaxBrightness = 255;
	
	@Override
	public Mat implementation(Mat theSource) {
		if(theSource.channels() == 1)return theSource;
		Mat mask1 = new Mat();
		try {
			inRange(theSource, new Mat(1, 1, CV_32SC4, new Scalar(_cMinHue, _cMinSaturation, _cMinBrightness, 0)), new Mat(1, 1, CV_32SC4, new Scalar(_cMaxHue, _cMaxSaturation, _cMaxBrightness, 0)), mask1);
		}catch(Exception e) {
			
		}
		//
		//add()
		return mask1;
	}

	
}
