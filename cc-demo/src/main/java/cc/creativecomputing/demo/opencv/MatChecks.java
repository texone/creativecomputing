package cc.creativecomputing.demo.opencv;

import org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.*;

import cc.creativecomputing.core.logging.CCLog;

public class MatChecks {
	public static void main(String[] args) {
		Mat myMat = new Mat(1,10, CV_16UC1);
		CCLog.info(myMat);
	}
}
