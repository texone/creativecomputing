package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_imgproc.CV_GRAY2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGBA2RGB;
import static org.bytedeco.javacpp.opencv_imgproc.CV_RGB2GRAY;
import static org.bytedeco.javacpp.opencv_imgproc.cvtColor;
import static org.bytedeco.javacpp.opencv_core.*;

import org.bytedeco.javacpp.opencv_core.Mat;

public class CCCVUtil {
	
	public static Mat rgbToGray(Mat theSource) {
		Mat myResult = new Mat();
		cvtColor(theSource, myResult, CV_RGB2GRAY);
		return myResult;
	}
	
	public static Mat grayToRGB(Mat theSource) {
		Mat myResult = new Mat();
		cvtColor(theSource, myResult, CV_GRAY2RGB);
		return myResult;
	}
	
	public static Mat RGBAToRGB(Mat theSource) {
		Mat myResult = new Mat();
		cvtColor(theSource, myResult, CV_RGBA2RGB);
		return myResult;
	}
	
	public static String matDepth(Mat mat){
	    int depth = mat.depth();

	    switch (depth)
	    {
	    case CV_8U:  return "CV_8U";
	    case CV_8S:  return "CV_8S";
	    case CV_16U: return "CV_16U";
	    case CV_16S: return "CV_16S";
	    case CV_32S: return "CV_32S";
	    case CV_32F: return "CV_32F";
	    case CV_64F: return "CV_64F";
	    default:
	        return "Invalid depth type of matrix!";
	    }
	} 

	public static String matType(Mat mat){
	 
		if(mat.type() == CV_8UC1)  return "CV_8UC1";
		if(mat.type() == CV_8UC2)  return "CV_8UC2";
		if(mat.type() == CV_8UC3)  return "CV_8UC3";
		if(mat.type() == CV_8UC4)  return "CV_8UC4";

		if(mat.type() == CV_8SC1)  return "CV_8SC1";
		if(mat.type() == CV_8SC2)  return "CV_8SC2";
		if(mat.type() == CV_8SC3)  return "CV_8SC3";
		if(mat.type() == CV_8SC4)  return "CV_8SC4";
		
		if(mat.type() == CV_16UC1) return "CV_16UC1";
	    if(mat.type() == CV_16UC2) return "CV_16UC2";
	    if(mat.type() == CV_16UC3) return "CV_16UC3";
	    if(mat.type() == CV_16UC4) return "CV_16UC4";

	    if(mat.type() == CV_16SC1) return "CV_16SC1";
	    if(mat.type() == CV_16SC2) return "CV_16SC2";
	    if(mat.type() == CV_16SC3) return "CV_16SC3";
	    if(mat.type() == CV_16SC4) return "CV_16SC4";

	    if(mat.type() == CV_32SC1) return "CV_32SC1";
	    if(mat.type() == CV_32SC2) return "CV_32SC2";
	    if(mat.type() == CV_32SC3) return "CV_32SC3";
	    if(mat.type() == CV_32SC4) return "CV_32SC4";

	    if(mat.type() == CV_32FC1) return "CV_32FC1";
	    if(mat.type() == CV_32FC2) return "CV_32FC2";
	    if(mat.type() == CV_32FC3) return "CV_32FC3";
	    if(mat.type() == CV_32FC4) return "CV_32FC4";

	    if(mat.type() == CV_64FC1) return "CV_64FC1";
	    if(mat.type() == CV_64FC2) return "CV_64FC2";
	    if(mat.type() == CV_64FC3) return "CV_64FC3";
	    if(mat.type() == CV_64FC4) return "CV_64FC4";

	    return "Invalid type of matrix!";
	}
}
