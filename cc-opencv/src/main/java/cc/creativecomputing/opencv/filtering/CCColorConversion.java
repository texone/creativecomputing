package cc.creativecomputing.opencv.filtering;

import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HLS;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2Lab;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2Luv;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2XYZ;
import static org.bytedeco.javacpp.opencv_imgproc.CV_BGR2YCrCb;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HLS2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HLS2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HSV2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_HSV2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.CV_Lab2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_Lab2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.CV_Luv2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_Luv2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2HLS;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2HSV;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2Lab;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2Luv;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2XYZ;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2YCrCb;
import static org.bytedeco.javacpp.opencv_imgproc.CV_XYZ2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_XYZ2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.CV_YCrCb2BGR;
import static org.bytedeco.javacpp.opencv_imgproc.CV_YCrCb2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.opencv.CCImageProcessor;

public class CCColorConversion extends CCImageProcessor{
	/**
	 * color space conversion code
	 * 
	 * @author chris
	 *
	 */
	public static enum CCConversionCode {
		
		/**
		 * RGB <-> CIE XYZ.Rec 709 with D65 white point 
		 */
		BGR2XYZ(CV_BGR2XYZ),
		RGB2XYZ(CV_RGB2XYZ),
		
		XYZ2BGR(CV_XYZ2BGR),
		XYZ2RGB(CV_XYZ2RGB ),
		
		/**
		 * RGB <-> YCrCb JPEG (or YCC) 
		 */
		BGR2YCrCb(CV_BGR2YCrCb),
		RGB2YCrCb(CV_RGB2YCrCb),
		
		YCrCb2BGR(CV_YCrCb2BGR),
		YCrCb2RGB(CV_YCrCb2RGB ),

		/**
		 * RGB <-> HSV
		 */
		BGR2HSV(CV_BGR2HSV),
		RGB2HSV(CV_RGB2HSV),
		
		HSV2BGR(CV_HSV2BGR),
		HSV2RGB(CV_HSV2RGB ),

		/**
		 * RGB <-> HLS  
		 */
		BGR2HLS(CV_BGR2HLS),
		RGB2HLS(CV_RGB2HLS),
		
		HLS2BGR(CV_HLS2BGR),
		HLS2RGB(CV_HLS2RGB ),

		/**
		 * RGB <-> CIELAB    
		 */
		BGR2Lab(CV_BGR2Lab),
		RGB2Lab(CV_RGB2Lab),
		
		Lab2BGR(CV_Lab2BGR),
		Lab2RGB(CV_Lab2RGB ),

		/**
		 * RGB <-> CIELuv , , ,  
		 */
		BGR2Luv(CV_BGR2Luv),
		RGB2Luv(CV_RGB2Luv),
		
		Luv2BGR(CV_Luv2BGR),
		Luv2RGB(CV_Luv2RGB );
		
		public final int id;

		private CCConversionCode(int theID) {
			id = theID;
		}
	}
	
	@CCProperty(name = "conversion type")
	protected CCConversionCode _cConversionType = CCConversionCode.RGB2HSV;
	
	@Override
	public Mat implementation(Mat theSource) {
		if(theSource.channels() == 1)return theSource;
		cvtColor(theSource.clone(), theSource,  _cConversionType.id);
		return theSource;
	}
}
