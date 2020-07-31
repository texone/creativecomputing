package cc.creativecomputing.opencv;

import org.bytedeco.javacpp.DoublePointer;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_dnn.Net;
import static  org.bytedeco.javacpp.opencv_imgproc.circle;

import static org.bytedeco.javacpp.opencv_videoio.CAP_PROP_FRAME_WIDTH;

import java.util.ArrayList;
import java.util.List;

import static org.bytedeco.javacpp.opencv_videoio.CAP_PROP_FRAME_HEIGHT;

import static org.bytedeco.javacpp.opencv_core.getTickCount;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

import static org.bytedeco.javacpp.opencv_dnn.blobFromImage;
import static org.bytedeco.javacpp.opencv_dnn.readNetFromCaffe;

import org.bytedeco.javacpp.opencv_videoio.VideoCapture;
import org.bytedeco.javacpp.opencv_videoio.VideoWriter;

import cc.creativecomputing.core.logging.CCLog;
public class HandPoseVideo {
//	 int[][] POSE_PAIRS = new int[][]
//		{
//		    {0,1}, {1,2}, {2,3}, {3,4},         // thumb
//		    {0,5}, {5,6}, {6,7}, {7,8},         // index
//		    {0,9}, {9,10}, {10,11}, {11,12},    // middle
//		    {0,13}, {13,14}, {14,15}, {15,16},  // ring
//		    {0,17}, {17,18}, {18,19}, {19,20}   // small
//		};
//
//		static String protoFile = "hand/pose_deploy.prototxt";
//		static String weightsFile = "hand/pose_iter_102000.caffemodel";
//
//		int nPoints = 22;
//
//		public static void main(String[] args) {
//		
//		    float thresh = 0.01f;
//
//		    VideoCapture cap = new VideoCapture("asl.mp4");
//
//		    if (!cap.isOpened())
//		    {
//		        CCLog.info( "Unable to connect to camera");
//		        return;
//		    }
//
//		    Mat frame, frameCopy;
//		    
//		    int frameWidth = (int)cap.get(CAP_PROP_FRAME_WIDTH);
//		    int frameHeight = (int)cap.get(CAP_PROP_FRAME_HEIGHT);
//		    double aspect_ratio = frameWidth/(float)frameHeight;
//		    int inHeight = 368;
//		    int inWidth = ((int)(aspect_ratio*inHeight) * 8) / 8;
//
//		    CCLog.info("inWidth = " , inWidth , " ; inHeight = " , inHeight);
//
//		    VideoWriter video = new VideoWriter("Output-Skeleton.avi",VideoWriter.fourcc((byte)'M',(byte)'J',(byte)'P',(byte)'G'), 10, new Size(frameWidth,frameHeight));
//
//		    Net net = readNetFromCaffe(protoFile, weightsFile);
//
//		    double t=0;
//		    while(1)
//		    {
//		        t = getTickCount();
//		        cap.read(frame);
//		        frameCopy = frame.clone();
//		        Mat inpBlob = blobFromImage(frame, 1.0 / 255, new Size(inWidth, inHeight), new Scalar(0, 0, 0,0), false, false,0);
//
//		        net.setInput(inpBlob);
//
//		        Mat output = net.forward();
//
//		        int H = output.size(2);
//		        int W = output.size(3);
//
//		        // find the position of the body parts
//		        List<Point> points = new ArrayList<>();
//		        for (int n=0; n < nPoints; n++)
//		        {
//		            // Probability map of corresponding body's part.
//		            Mat probMap = new Mat(H, W, CV_32F, output.ptr(0,n));
//		            resize(probMap, probMap, new Size(frameWidth, frameHeight));
//
//		            Point maxLoc;
//		            DoublePointer prob = new DoublePointer();
//		            minMaxLoc(probMap, null, prob, null, maxLoc,null);
//		            if (prob > thresh)
//		            {
//		                circle(frameCopy, new Point((int)maxLoc.x(), (int)maxLoc.y()), 8, new Scalar(0,255,255,0), -1);
//		                cv::putText(frameCopy, cv::format("%d", n), cv::Point((int)maxLoc.x, (int)maxLoc.y), cv::FONT_HERSHEY_COMPLEX, 1, cv::Scalar(0, 0, 255), 2);
//
//		            }
//		            points[n] = maxLoc;
//		        }
//
//		        int nPairs = sizeof(POSE_PAIRS)/sizeof(POSE_PAIRS[0]);
//
//		        for (int n = 0; n < nPairs; n++)
//		        {
//		            // lookup 2 connected body/hand parts
//		            Point2f partA = points[POSE_PAIRS[n][0]];
//		            Point2f partB = points[POSE_PAIRS[n][1]];
//
//		            if (partA.x<=0 || partA.y<=0 || partB.x<=0 || partB.y<=0)
//		                continue;
//
//		            line(frame, partA, partB, Scalar(0,255,255), 8);
//		            circle(frame, partA, 8, Scalar(0,0,255), -1);
//		            circle(frame, partB, 8, Scalar(0,0,255), -1);
//		        }
//
//		        t = ((double)getTickCount() - t)/getTickFrequency();
//		        cout << "Time Taken for frame = " << t << endl;
//		        putText(frame, format("time taken = %.2f sec", t), new Point(50, 50), cv::FONT_HERSHEY_COMPLEX, .8, new Scalar(255, 50, 0), 2);
//		        // imshow("Output-Keypoints", frameCopy);
//		        imshow("Output-Skeleton", frame);
//		        video.write(frame);
//		        char key = waitKey(1);
//		        if (key==27)
//		            break;
//		    }
//		    // When everything done, release the video capture and write object
//		    cap.release();
//		    video.release();
//
//		    return 0;
//		}
}
