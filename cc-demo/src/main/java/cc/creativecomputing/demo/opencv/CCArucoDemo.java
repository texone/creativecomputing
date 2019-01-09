package cc.creativecomputing.demo.opencv;

import static org.bytedeco.javacpp.opencv_aruco.calibrateCameraAruco;
import static org.bytedeco.javacpp.opencv_aruco.calibrateCameraCharuco;
import static org.bytedeco.javacpp.opencv_aruco.detectMarkers;
import static org.bytedeco.javacpp.opencv_aruco.drawDetectedCornersCharuco;
import static org.bytedeco.javacpp.opencv_aruco.drawDetectedMarkers;
import static org.bytedeco.javacpp.opencv_aruco.getPredefinedDictionary;
import static org.bytedeco.javacpp.opencv_aruco.interpolateCornersCharuco;
import static org.bytedeco.javacpp.opencv_aruco.refineDetectedMarkers;
import static org.bytedeco.javacpp.opencv_aruco.*;
import static org.bytedeco.javacpp.opencv_calib3d.CALIB_FIX_ASPECT_RATIO;
import static org.bytedeco.javacpp.opencv_core.*;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_aruco.CharucoBoard;
import org.bytedeco.javacpp.opencv_aruco.DetectorParameters;
import org.bytedeco.javacpp.opencv_aruco.Dictionary;
import org.bytedeco.javacpp.opencv_core.FileStorage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.MatVectorVector;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacpp.indexer.IntIndexer;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.opencv.CCCVTexture;
import cc.creativecomputing.opencv.CCCVUtil;
import cc.creativecomputing.opencv.CCCVVideoCapture;
import cc.creativecomputing.opencv.CCCVVideoIn;
import cc.creativecomputing.opencv.CCCVVideoPlayer;
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCCVShaderFilter;
import cc.creativecomputing.opencv.filtering.CCThreshold;
import cc.creativecomputing.video.CCVideoTexture;
import cc.creativecomputing.video.Movie;

public class CCArucoDemo extends CCGL2Adapter {

	public static class CCCVCameraCalibration {

		/**
		 */
		public boolean saveCameraParams(String filename, Size imageSize, float aspectRatio, int flags, Mat cameraMatrix, Mat distCoeffs, double totalAvgErr) {
			FileStorage fs = new FileStorage(filename, FileStorage.WRITE);
			if (!fs.isOpened())
				return false;

//		    time_t tt;
//		    time(&tt);
//		    struct tm *t2 = localtime(&tt);
//		    char buf[1024];
//		    strftime(buf, sizeof(buf) - 1, "%c", t2);
//
//		    fs << "calibration_time" << buf;

			fs.write("image_width", imageSize.width());
			fs.write("image_height", imageSize.height());

			if ((flags & CALIB_FIX_ASPECT_RATIO) > 0)
				fs.write("aspectRatio", aspectRatio);

//		    if(flags != 0) {
//		        sprintf(buf, "flags: %s%s%s%s",
//		                flags & CALIB_USE_INTRINSIC_GUESS ? "+use_intrinsic_guess" : "",
//		                flags & CALIB_FIX_ASPECT_RATIO ? "+fix_aspectRatio" : "",
//		                flags & CALIB_FIX_PRINCIPAL_POINT ? "+fix_principal_point" : "",
//		                flags & CALIB_ZERO_TANGENT_DIST ? "+zero_tangent_dist" : "");
//		    }

			fs.write("flags", flags);
			fs.write("camera_matrix", cameraMatrix);
			fs.write("distortion_coefficients", distCoeffs);
			fs.write("avg_reprojection_error", totalAvgErr);
			return true;
		}
	}

	/**
	 * Shape of the structuring element
	 * 
	 * @author chris
	 *
	 */
	public static enum CCPredefinedDictionary {
		DICT_4X4_50(org.bytedeco.javacpp.opencv_aruco.DICT_4X4_50),
		DICT_4X4_100(org.bytedeco.javacpp.opencv_aruco.DICT_4X4_100),
		DICT_4X4_250(org.bytedeco.javacpp.opencv_aruco.DICT_4X4_250),
		DICT_4X4_1000(org.bytedeco.javacpp.opencv_aruco.DICT_4X4_1000),
		DICT_5X5_50(org.bytedeco.javacpp.opencv_aruco.DICT_5X5_50),
		DICT_5X5_100(org.bytedeco.javacpp.opencv_aruco.DICT_5X5_100),
		DICT_5X5_250(org.bytedeco.javacpp.opencv_aruco.DICT_5X5_250),
		DICT_5X5_1000(org.bytedeco.javacpp.opencv_aruco.DICT_5X5_1000),
		DICT_6X6_50(org.bytedeco.javacpp.opencv_aruco.DICT_6X6_50),
		DICT_6X6_100(org.bytedeco.javacpp.opencv_aruco.DICT_6X6_100),
		DICT_6X6_250(org.bytedeco.javacpp.opencv_aruco.DICT_6X6_250),
		DICT_6X6_1000(org.bytedeco.javacpp.opencv_aruco.DICT_6X6_1000),
		DICT_7X7_50(org.bytedeco.javacpp.opencv_aruco.DICT_7X7_50),
		DICT_7X7_100(org.bytedeco.javacpp.opencv_aruco.DICT_7X7_100),
		DICT_7X7_250(org.bytedeco.javacpp.opencv_aruco.DICT_7X7_250),
		DICT_7X7_1000(org.bytedeco.javacpp.opencv_aruco.DICT_7X7_1000),
		DICT_ARUCO_ORIGINAL(org.bytedeco.javacpp.opencv_aruco.DICT_ARUCO_ORIGINAL);

		public final int id;

		private CCPredefinedDictionary(int theID) {
			id = theID;
		}
	}

	public static class CCMarkerDetection {
		@CCProperty(name = "Thresh Win Size Min", min = 0, max = 100)
		private int _cAdaptiveThreshWinSizeMin = 3;
		@CCProperty(name = "Thresh Win Size Max", min = 0, max = 100)
		private int _cAdaptiveThreshWinSizeMax = 23;
		@CCProperty(name = "Thresh Win Size Step", min = 0, max = 100)
		private int _cAdaptiveThreshWinSizeStep = 10;

		@CCProperty(name = "min perimeter", min = 0, max = 1)
		private double _cMinMarkerPerimeterRate = 0.03;
		@CCProperty(name = "max perimeter", min = 0, max = 10)
		private double _cMaxMarkerPerimeterRate = 4.0;
		@CCProperty(name = "polygonal accuracy", min = 0, max = 1)
		private double _cPolygonalApproxAccuracyRate = 0.05;

		@CCProperty(name = "dictionary")
		private CCPredefinedDictionary _cDictionary = CCPredefinedDictionary.DICT_ARUCO_ORIGINAL;

		private CCPredefinedDictionary _myLastDictionary = null;

		protected Dictionary _myDictionary;

		protected MatVector _myCorners = new MatVector();
		protected MatVector _myRejected = new MatVector();
		protected Mat _myIds = new Mat();

		protected Mat rvecs = new Mat();
		protected Mat tvecs = new Mat();
		

		private Mat camMat = new Mat(3,3,CV_64F);
		private Mat camDist = new Mat(5,1,CV_64F);

		public CCMarkerDetection() {
			DoubleIndexer myIndexer = camMat.createIndexer();
			myIndexer.put(0, 4278.18153119249);
			myIndexer.put(1, 0);
			myIndexer.put(2, 220.22038500033852);
			myIndexer.put(3, 0);
			myIndexer.put(4, 2530.4829048778429);
			myIndexer.put(5, 148.80000087208535);
			myIndexer.put(6, 0);
			myIndexer.put(7, 0);
			myIndexer.put(8, 1);
			

//			myIndexer.put(0, 4278.18153119249);
//			myIndexer.put(1, 0);
//			myIndexer.put(2, 0);
//			myIndexer.put(3, 0);
//			myIndexer.put(4, 2530.4829048778429);
//			myIndexer.put(5, 0);
//			myIndexer.put(6, 220.22038500033852);
//			myIndexer.put(7, 148.80000087208535);
//			myIndexer.put(8, 1);
			
			DoubleIndexer myIndexer2 = camDist.createIndexer();
//			myIndexer2.put(0, 12.154837218327105);
//			myIndexer2.put(1, 933.11745331373641);
//			myIndexer2.put(2, 3.7550942115312025);
//			myIndexer2.put(3, 0.83757121026507453);
//			myIndexer2.put(4, -10510.641074538722);

			myIndexer2.put(0, 0);
			myIndexer2.put(1, 0);
			myIndexer2.put(2, 0);
			myIndexer2.put(3, 0);
			myIndexer2.put(4, 0);
		}

		public void detect(Mat theMat) {
			if (_cDictionary != _myLastDictionary) {
				_myLastDictionary = _cDictionary;
				_myDictionary = getPredefinedDictionary(_cDictionary.id);
			}

			DetectorParameters myParameters = new DetectorParameters();

			myParameters.adaptiveThreshWinSizeMin(_cAdaptiveThreshWinSizeMin);
			myParameters.adaptiveThreshWinSizeMax(_cAdaptiveThreshWinSizeMax);
			myParameters.adaptiveThreshWinSizeStep(_cAdaptiveThreshWinSizeStep);

			myParameters.minMarkerPerimeterRate(_cMinMarkerPerimeterRate);
			myParameters.maxMarkerPerimeterRate(_cMaxMarkerPerimeterRate);
			myParameters.polygonalApproxAccuracyRate(_cPolygonalApproxAccuracyRate);

			detectMarkers(theMat, _myDictionary, _myCorners, _myIds, myParameters, _myRejected, null, null);

		}
		
		public void estimatePose() {
			
			estimatePoseSingleMarkers(_myCorners, 0.02f, camMat, camDist, rvecs, tvecs);
			
//			CCLog.info(tvecs);
		}

		public void drawMarkers(Mat theDrawMat) {
			drawDetectedMarkers(theDrawMat, _myCorners);
		}
		
		public void drawAxisF(Mat theDrawMat) {
			for(int i = 0; i < rvecs.rows();i++) {
				if(i == 0) {
					DoubleIndexer myRIndexer = rvecs.row(i).createIndexer();
					DoubleIndexer myTIndexer = tvecs.row(i).createIndexer();
					CCLog.info(myTIndexer.get(0),myTIndexer.get(1),myTIndexer.get(2),myRIndexer.get(0),myRIndexer.get(1),myRIndexer.get(2));
					
					/*
					 * float theta = (float)(Math.Sqrt(m.x*m.x + m.y*m.y + m.z*m.z)*180/Math.PI);
Vector3 axis = new Vector3 (-m.x, m.y, -m.z);
Quaternion rot = Quaternion.AngleAxis (theta, axis);
					 */
				}
				drawAxis(theDrawMat, camMat, camDist,  rvecs.row(i), tvecs.row(i), 0.02f);
			}
		}

	}

	public static class CCArucoCameraCalibration extends CCMarkerDetection {

		@CCProperty(name = "use refining")
		private boolean _cUseRefinedStrategy = false;

		@CCProperty(name = "square length", min = 0, max = 0.1)
		private double _cSquareLength = 0.04;
		@CCProperty(name = "marker length", min = 0, max = 0.1)
		private double _cMarkerLength = 0.02;

		@CCProperty(name = "use fix aspect ratio")
		private boolean _cUseFixAspectRatio = false;
		@CCProperty(name = "aspect ratio", min = 0, max = 0.1)
		private double _cAspectRatio = 4d / 3d;

		@CCProperty(name = "squares x")
		public int _cSquaresX = 5;
		@CCProperty(name = "squares y")
		public int _cSquareyY = 7;

		private Mat _myCurrentCharucoCorners = new Mat();
		private Mat _myCurrentCharucoIds = new Mat();

		private MatVectorVector allCorners = new MatVectorVector();
		private MatVector allIds = new MatVector();
		private MatVector allImgs = new MatVector();
		private Size imgSize;
		private Mat _myMat;
		private CharucoBoard charucoboard;
		

		

		public CCArucoCameraCalibration() {
		}

		public void detectBoard(Mat theMat) {
			imgSize = theMat.size();
			_myMat = theMat;

			// detect markers
			detect(theMat);
			charucoboard = CharucoBoard.create(_cSquaresX, _cSquareyY, (float) _cSquareLength, (float) _cMarkerLength,
					_myDictionary);

			// refind strategy to detect more markers
			if (_cUseRefinedStrategy)
				refineDetectedMarkers(theMat, charucoboard, _myCorners, _myIds, _myRejected);

			// interpolate charuco corners
			if (_myIds.rows() > 0)
				interpolateCornersCharuco(_myCorners, _myIds, theMat, charucoboard, _myCurrentCharucoCorners, _myCurrentCharucoIds);
		}

//		@CCProperty(name = "add frame")
		public void addFrame() {
			if (_myIds.rows() <= 0) return;
			
			allCorners.push_back(_myCorners);
			allIds.push_back(_myIds);
			allImgs.push_back(_myMat);
			imgSize = _myMat.size();
			

//			calibrate();
			CCLog.info("ADD FRAME", allImgs.size());
		}

//		@CCProperty(name = "calibrate")
		public void calibrate() {
			if (allIds.size() < 1) {
				CCLog.error("Not enough captures for calibration");
				return;
			}
			
			CCLog.info("CALIBRATE");


//		    if(_cUseFixAspectRatio) {
//		        cameraMatrix = Mat.eye(3, 3, CV_64F).asMat();
//		        cameraMatrix.createIndexer().put(0l, 0l,_cAspectRatio);
//		    }

			// prepare data for calibration
			MatVector allCornersConcatenated = new MatVector();
			Mat markerCounterPerFrame = new Mat((int)allCorners.size(), 1, CV_32SC1);
			//CCCVUtil
			IntIndexer markerCounterPerFrameIndexer = markerCounterPerFrame.createIndexer();
			List<Integer> allIdsConcatenated = new ArrayList<>();
			
			for (int i = 0; i < allCorners.size(); i++) {
				markerCounterPerFrameIndexer.put(i,(int) allCorners.get(i).size());
				IntIndexer myIDIndexer = allIds.get(i).createIndexer();
				for (int j = 0; j < allCorners.get(i).size(); j++) {
					allCornersConcatenated.put(allCorners.get(i).get(j));
					allIdsConcatenated.add(myIDIndexer.get(j));
				}
			}
			Mat allIdsConcatenatedMat = new Mat(allIdsConcatenated.size(), 1, CV_32SC1);
			IntIndexer allIdsConcatenatedIndexer = allIdsConcatenatedMat.createIndexer();
			for(int i = 0; i < allIdsConcatenated.size(); i++) {
				allIdsConcatenatedIndexer.put(i, allIdsConcatenated.get(i));
			}

			Mat cameraMatrix = new Mat();
			Mat distCoeffs = new Mat();
			// calibrate camera using aruco markers
			double arucoRepErr = calibrateCameraAruco(
				allCornersConcatenated, 
				allIdsConcatenatedMat, 
				markerCounterPerFrame,
				charucoboard, 
				imgSize, 
				cameraMatrix, 
				distCoeffs
			);

			// prepare data for charuco calibration
			int nFrames = (int) allCorners.size();
			MatVector allCharucoCorners = new MatVector(nFrames);
			MatVector allCharucoIds = new MatVector(nFrames);
			MatVector filteredImages = new MatVector();

			for (int i = 0; i < nFrames; i++) {
				// interpolate using camera parameters
				Mat currentCharucoCorners = new Mat();
				Mat currentCharucoIds = new Mat();
				interpolateCornersCharuco(
					allCorners.get(i), 
					allIds.get(i), 
					allImgs.get(i), 
					charucoboard,
					currentCharucoCorners, 
					currentCharucoIds, 
					cameraMatrix, 
					distCoeffs, 
					2
				);

				allCharucoCorners.push_back(currentCharucoCorners);
				allCharucoIds.push_back(currentCharucoIds);
				filteredImages.put(allImgs.get(i));
			}

			if (allCharucoCorners.size() < 4) {
				CCLog.error("Not enough corners for calibration");
				return;
			}

			// calibrate camera using charuco
			double repError = calibrateCameraCharuco(allCharucoCorners, allCharucoIds, charucoboard, imgSize, cameraMatrix, distCoeffs);

			boolean saveOk = saveCameraParams(CCNIOUtil.dataPath("check.txt").toAbsolutePath().toString(), imgSize, 4f / 3f, 0, cameraMatrix,distCoeffs, repError);
			if (!saveOk) {
				CCLog.error("Cannot save output file");
				return;
			}

		}
		
		/**
		 */
		public boolean saveCameraParams(String filename, Size imageSize, float aspectRatio, int flags, Mat cameraMatrix, Mat distCoeffs, double totalAvgErr) {
			FileStorage fs = new FileStorage(filename, FileStorage.WRITE);
			if (!fs.isOpened())
				return false;

//		    time_t tt;
//		    time(&tt);
//		    struct tm *t2 = localtime(&tt);
//		    char buf[1024];
//		    strftime(buf, sizeof(buf) - 1, "%c", t2);
//
//		    fs << "calibration_time" << buf;

			fs.write("image_width", imageSize.width());
			fs.write("image_height", imageSize.height());

			if ((flags & CALIB_FIX_ASPECT_RATIO) > 0)
				fs.write("aspectRatio", aspectRatio);

//		    if(flags != 0) {
//		        sprintf(buf, "flags: %s%s%s%s",
//		                flags & CALIB_USE_INTRINSIC_GUESS ? "+use_intrinsic_guess" : "",
//		                flags & CALIB_FIX_ASPECT_RATIO ? "+fix_aspectRatio" : "",
//		                flags & CALIB_FIX_PRINCIPAL_POINT ? "+fix_principal_point" : "",
//		                flags & CALIB_ZERO_TANGENT_DIST ? "+zero_tangent_dist" : "");
//		    }

			fs.write("flags", flags);
			fs.write("camera_matrix", cameraMatrix);
			fs.write("distortion_coefficients", distCoeffs);
			fs.write("avg_reprojection_error", totalAvgErr);
			return true;
		}

		public void drawCorners(Mat theMat) {
			if (_myCurrentCharucoCorners.total() <= 0)
				return;
			drawDetectedCornersCharuco(theMat, _myCurrentCharucoCorners);
		}
	}

	private boolean USE_CAPTURE = true;

	@CCProperty(name = "capture")
	private CCCVVideoCapture _myCapture;

	@CCProperty(name = "player")
	private CCCVVideoPlayer _myPlayer;
	

	private Movie _myData;
	private CCVideoTexture _myVideoTexture;

	private CCCVVideoIn _myVideoIn;

	private CCCVTexture _myTexture;

	@CCProperty(name = "marker detection")
	private CCMarkerDetection _cMarkerDetection = new CCMarkerDetection();
	@CCProperty(name = "calibration")
	private CCArucoCameraCalibration _cCameraCalibration = new CCArucoCameraCalibration();

	private static enum CCDrawMat {
		ORIGIN, SHADER, BLUR, THRESHOLD
	}

	@CCProperty(name = "draw mat")
	private CCDrawMat _cDrawMat = CCDrawMat.THRESHOLD;

	@CCProperty(name = "shader filter")
	private CCCVShaderFilter _cFilter;

	@CCProperty(name = "blur")
	private CCBlur _cBlur = new CCBlur();

	@CCProperty(name = "threshold")
	private CCThreshold _cThreshold = new CCThreshold();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		if (USE_CAPTURE) {
			_myCapture = new CCCVVideoCapture(1);
			_myCapture.exposure(-8);
			_myCapture.frameWidth(1280);
			_myCapture.frameHeight(960);
			_myVideoIn = _myCapture;
		} else {
			_myPlayer = new CCCVVideoPlayer(CCNIOUtil.dataPath("videos/charuco.mp4").toAbsolutePath().toString());
			_myVideoIn = _myPlayer;

//			_myData = new Movie(theAnimator, CCNIOUtil.dataPath("videos/charuco.mp4"));//
////			_myData.loop();
//			_myData.endEvents.add(()->{_myData.stop();_myData.play();});
//			_myData.play();
//			
//			_myVideoTexture = new CCVideoTexture(this,_myData);
		}
		_myTexture = new CCCVTexture();
		_myTexture.mustFlipVertically(true);

		_cFilter = new CCCVShaderFilter(
			CCNIOUtil.classPath(this, "aruco_shader_vertex.glsl"),
			CCNIOUtil.classPath(this, "aruco_shader_fragment.glsl")
		);
		
		keyReleased().add(e -> {
			switch(e.keyCode()) {
			case VK_A:
				_cCameraCalibration.addFrame();
				break;
			case VK_C:
				_cCameraCalibration.calibrate();
				break;
			}
		});
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();

		Mat myOrigin;
		
//		if(_myVideoTexture.height() <= 0 || _myVideoTexture.width() <= 0)return;
//		if(USE_CAPTURE) {
			myOrigin = _myVideoIn.read();
//		}else {
//			myOrigin = new Mat(_myVideoTexture.height(), _myVideoTexture.width(), CV_8UC3);
////			CCLog.info(myOrigin.getByteBuffer());
//			myOrigin.getByteBuffer().rewind();
//			myOrigin.getByteBuffer().put(_myVideoTexture.getTexImage());
//			myOrigin.getByteBuffer().rewind();
//		}
//
		Mat myDrawMat = new Mat();
//
//		if (_cDrawMat == CCDrawMat.ORIGIN)
			myDrawMat = myOrigin.clone();
//		_cFilter.process(myOrigin);
//		_cFilter.preDisplay(g);
//		if (_cDrawMat == CCDrawMat.SHADER)
//			myDrawMat = myOrigin.clone();
//		_cBlur.process(myOrigin);
//		if (_cDrawMat == CCDrawMat.BLUR)
//			myDrawMat = myOrigin.clone();
////		myOrigin = CCCVUtil.rgbToGray(myOrigin);
//		_cThreshold.process(myOrigin);
//		if (_cDrawMat == CCDrawMat.THRESHOLD)
//			myDrawMat = myOrigin.clone();
//
////		myDrawMat = CCCVUtil.grayToRGB(myDrawMat);
//
//		_cMarkerDetection.detect(myOrigin);
//		_cMarkerDetection.drawMarkers(myDrawMat);
//
		_cCameraCalibration.detectBoard(myOrigin);
		_cCameraCalibration.estimatePose();
		_cCameraCalibration.drawMarkers(myDrawMat);
		_cCameraCalibration.drawCorners(myDrawMat);
		_cCameraCalibration.drawAxisF(myDrawMat);
//
		_myTexture.image(myDrawMat);
		g.ortho2D();
		g.image(_myTexture, 0, 0);
	}

	public static void main(String[] args) {

		CCArucoDemo demo = new CCArucoDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
