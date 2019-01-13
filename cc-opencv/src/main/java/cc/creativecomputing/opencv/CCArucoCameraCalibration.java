package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_aruco.calibrateCameraAruco;
import static org.bytedeco.javacpp.opencv_aruco.calibrateCameraCharuco;
import static org.bytedeco.javacpp.opencv_aruco.drawDetectedCornersCharuco;
import static org.bytedeco.javacpp.opencv_aruco.interpolateCornersCharuco;
import static org.bytedeco.javacpp.opencv_aruco.refineDetectedMarkers;
import static org.bytedeco.javacpp.opencv_calib3d.CALIB_FIX_ASPECT_RATIO;
import static org.bytedeco.javacpp.opencv_core.CV_32SC1;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.indexer.IntIndexer;
import org.bytedeco.javacpp.opencv_aruco.CharucoBoard;
import org.bytedeco.javacpp.opencv_core.FileStorage;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.opencv_core.MatVectorVector;
import org.bytedeco.javacpp.opencv_core.Size;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

public class CCArucoCameraCalibration extends CCMarkerDetection {

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
			super(null,null);
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