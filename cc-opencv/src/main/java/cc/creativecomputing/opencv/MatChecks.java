package cc.creativecomputing.opencv;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.indexer.Indexer;
import org.bytedeco.javacpp.indexer.IntIndexer;

import cc.creativecomputing.core.logging.CCLog;

import static org.bytedeco.javacpp.opencv_core.*;

import java.nio.IntBuffer;

public class MatChecks {
//	CCCVUtil
	public static void main(String[] args) {
		Mat myMat = new Mat(10,1,CV_32SC1);
		IntIndexer myIndexer = myMat.createIndexer();
		for(int i = 0; i < myMat.rows();i++) {
			myIndexer.put(i,i);
		}
		for(int i = 0; i < myMat.rows();i++) {
			CCLog.info(myIndexer.get(i));
		}
		
		CCLog.info(myMat.getIntBuffer().capacity());
	}

}