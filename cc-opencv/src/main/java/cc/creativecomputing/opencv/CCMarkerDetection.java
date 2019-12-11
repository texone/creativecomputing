package cc.creativecomputing.opencv;

import static org.bytedeco.javacpp.opencv_aruco.detectMarkers;
import static org.bytedeco.javacpp.opencv_aruco.drawAxis;
import static org.bytedeco.javacpp.opencv_aruco.drawDetectedMarkers;
import static org.bytedeco.javacpp.opencv_aruco.estimatePoseSingleMarkers;
import static org.bytedeco.javacpp.opencv_aruco.getPredefinedDictionary;
import static org.bytedeco.javacpp.opencv_core.CV_64F;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.bytedeco.javacpp.opencv_aruco.DetectorParameters;
import org.bytedeco.javacpp.opencv_aruco.Dictionary;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacpp.indexer.DoubleIndexer;
import org.bytedeco.javacpp.indexer.FloatIndexer;
import org.bytedeco.javacpp.indexer.IntIndexer;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.filter.CCMedianFilter;
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCThreshold;

public class CCMarkerDetection implements Iterable<CCMarker>{
	
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

	protected Mat _myRotationVectors = new Mat();
	protected Mat _myTranslationVectors = new Mat();
	
	private Mat _myCameraMat = new Mat(3,3,CV_64F);
	private Mat _myCameraDistortions = new Mat(5,1,CV_64F);
	
	private List<CCMarker> _myMarker = new ArrayList<>();
	
	@CCProperty(name = "median")
	private CCMedianFilter _cMedian = new CCMedianFilter();
	
	private Map<Integer, CCMarker>_myMarkerMap = new HashMap<>();
	
	private CCCVVideoIn _myVideoIn;
	
	@CCProperty(name = "blur")
	private CCBlur _cBlur = new CCBlur();

	@CCProperty(name = "threshold")
	private CCThreshold _cThreshold = new CCThreshold();
	
	private static enum CCDrawMat {
		ORIGIN, BLUR, THRESHOLD
	}

	@CCProperty(name = "draw mat")
	private CCDrawMat _cDrawMat = CCDrawMat.THRESHOLD;
	
	@CCProperty(name = "draw corners")
	private boolean _cDrawCorners = true;
	@CCProperty(name = "draw axis")
	private boolean _cDrawAxis = true;
	
	private Mat _myDrawMat = new Mat();
	
	final Lock lock = new ReentrantLock();
	
	@CCProperty(name = "x rot", min = -180, max = 180)
	private double _cXRot = 0;
	@CCProperty(name = "y rot", min = -180, max = 180)
	private double _cYRot = 0;
	@CCProperty(name = "z rot", min = -180, max = 180)
	private double _cZRot = 0;

	public CCMarkerDetection(CCCVVideoIn theVideoIn, Path theCameraSettings) {
		
		_myVideoIn = theVideoIn;
		_myVideoIn.events.add(mat -> {
			if(mat == null)return;
			if(mat.empty())return;
			lock.lock();
			if (_cDrawMat == CCDrawMat.ORIGIN)
				_myDrawMat = mat.clone();
			
			_cBlur.process(mat);
			if (_cDrawMat == CCDrawMat.BLUR)
				_myDrawMat = mat.clone();
			mat = CCCVUtil.rgbToGray(mat);
			_cThreshold.process(mat);
			if (_cDrawMat == CCDrawMat.THRESHOLD)
				_myDrawMat = CCCVUtil.grayToRGB(mat.clone());
			detect(mat);
			estimatePose();

			if(_cDrawCorners)drawMarkers(_myDrawMat);
			if(_cDrawAxis)drawAxisF(_myDrawMat);
			try {
				createObjects();
			}catch(Exception e) {
				
			}
			lock.unlock();
		});
		
		CCDataElement myXML = CCXMLIO.createXMLElement(theCameraSettings);
		_myCameraMat = loadMat(myXML,"CameraMatrices");
		_myCameraDistortions = loadMat(myXML,"DistCoeffs");
	}
	
	public void lock() {
		lock.lock();
	}
	
	public void unlock() {
		lock.unlock();
	}
	
	public Mat infoMat() {
		return _myDrawMat;
	}
	
//	public void start() {
//		
//		new Thread(()->{
//			while(true) {
//				Mat myMat = _myVideoIn.mat();
//				detect(myMat);
//				estimatePose();
//				Thread.yield();
//			}
//		}).start();
//	}

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
		estimatePoseSingleMarkers(_myCorners, 0.02f, _myCameraMat, _myCameraDistortions, _myRotationVectors, _myTranslationVectors);
	}
	
//	public void poseParameters(double position[3], double orientation[4])  {
//	     //check if paremeters are valid
//	     boolean invalid=false;
//	     for (int i=0;i<3 && !invalid ;i++){
//	       if (Tvec.at<float>(i,0)!=-999999) invalid|=false;
//	       if (Rvec.at<float>(i,0)!=-999999) invalid|=false;
//	     }
//	     if (invalid) throw new RuntimeException("extrinsic parameters are not set Marker::getModelViewMatrix");
//		     
//		     // calculate position vector
//		     position[0] = Tvec.ptr<float>(0)[0];
//		     position[1] = Tvec.ptr<float>(0)[1];
//		     position[2] = +Tvec.ptr<float>(0)[2];
//		     
//		     // now calculare orientation quaternion
//		     cv::Mat Rot(3,3,CV_32FC1);
//		     cv::Rodrigues(Rvec, Rot);
//		     
//		     // calculate axes for quaternion
//		     double stAxes[3][3];
//		     // x axis
//		     stAxes[0][0] = -Rot.at<float>(0,0);
//		     stAxes[0][1] = -Rot.at<float>(1,0);
//		     stAxes[0][2] = +Rot.at<float>(2,0);
//		     // y axis
//		     stAxes[1][0] = -Rot.at<float>(0,1);
//		     stAxes[1][1] = -Rot.at<float>(1,1);
//		     stAxes[1][2] = +Rot.at<float>(2,1);
//		     // for z axis, we use cross product
//		     stAxes[2][0] = stAxes[0][1]*stAxes[1][2] - stAxes[0][2]*stAxes[1][1];
//		     stAxes[2][1] = - stAxes[0][0]*stAxes[1][2] + stAxes[0][2]*stAxes[1][0];
//		     stAxes[2][2] = stAxes[0][0]*stAxes[1][1] - stAxes[0][1]*stAxes[1][0];
//		     
//		     // transposed matrix
//		     double axes[3][3];
//		     axes[0][0] = stAxes[0][0];
//		     axes[1][0] = stAxes[0][1];
//		     axes[2][0] = stAxes[0][2];
//		     
//		     axes[0][1] = stAxes[1][0];
//		     axes[1][1] = stAxes[1][1];
//		     axes[2][1] = stAxes[1][2];
//		     
//		     axes[0][2] = stAxes[2][0];
//	00174     axes[1][2] = stAxes[2][1];
//	00175     axes[2][2] = stAxes[2][2];
//	00176     
//		     // Algorithm in Ken Shoemake's article in 1987 SIGGRAPH course notes
//		     // article "Quaternion Calculus and Fast Animation".
//		     double fTrace = axes[0][0]+axes[1][1]+axes[2][2];
//		     double fRoot;
//		 
//		     if ( fTrace > 0.0 )
//		     {
//		       // |w| > 1/2, may as well choose w > 1/2
//		       fRoot = sqrt(fTrace + 1.0);  // 2w
//	00186       orientation[0] = 0.5*fRoot;
//	00187       fRoot = 0.5/fRoot;  // 1/(4w)
//	00188       orientation[1] = (axes[2][1]-axes[1][2])*fRoot;
//	00189       orientation[2] = (axes[0][2]-axes[2][0])*fRoot;
//	00190       orientation[3] = (axes[1][0]-axes[0][1])*fRoot;
//	00191     }
//	00192     else
//	00193     {
//	00194       // |w| <= 1/2
//	00195       static unsigned int s_iNext[3] = { 1, 2, 0 };
//	00196       unsigned int i = 0;
//	00197       if ( axes[1][1] > axes[0][0] )
//	00198         i = 1;
//	00199       if ( axes[2][2] > axes[i][i] )
//	00200         i = 2;
//		       unsigned int j = s_iNext[i];
//	00202       unsigned int k = s_iNext[j];
//		 
//		       fRoot = sqrt(axes[i][i]-axes[j][j]-axes[k][k] + 1.0);
//		       double* apkQuat[3] = { &orientation[1], &orientation[2], &orientation[3] };
//		       *apkQuat[i] = 0.5*fRoot;
//		       fRoot = 0.5/fRoot;
//		       orientation[0] = (axes[k][j]-axes[j][k])*fRoot;
//		       *apkQuat[j] = (axes[j][i]+axes[i][j])*fRoot;
//		       *apkQuat[k] = (axes[k][i]+axes[i][k])*fRoot;
//		     }
//		   }
	
	public void createObjects() {
		_myMarker.clear();
		if(_myIds == null)return;
		
		IntIndexer myIDIndexer = _myIds.createIndexer();
		for(int i = 0; i < _myIds.rows();i++) {
			
			CCMarker myMarker = new CCMarker();
			myMarker.id = myIDIndexer.get(i);

			FloatIndexer myCornersIndexer = _myCorners.get(i).createIndexer();
			myMarker.corners.clear();
			for(int j = 0; j < 4;j++) {
				myMarker.corners.add(new CCVector2(myCornersIndexer.get(0, j, 0),myCornersIndexer.get(0, j, 1)));
			}

			double myX = 0, myY = 0, myZ = 0;
			DoubleIndexer myRotationIndexer = _myRotationVectors.row(i).createIndexer();
			boolean myNegZ = myRotationIndexer.get(0) < 0;
			try {
			 myX = _cMedian.process(myMarker.id * 3, myRotationIndexer.get(0), 0);
			 myY = _cMedian.process(myMarker.id * 3 + 1, myRotationIndexer.get(1), 0);
			 myZ = _cMedian.process(myMarker.id * 3 + 2, myRotationIndexer.get(2), 0);
//			CCLog.info("YO",myNegZ);
//			CCLog.info(myX, myY, myZ);
			}catch(Exception e) {
				e.printStackTrace();
			}
			myMarker.rotationVector.set(
				myX,
				myY,
				myZ
			);
			
			double theta = myMarker.rotationVector.length();
			CCVector3 axis = new CCVector3 (myMarker.rotationVector.x, myMarker.rotationVector.y, myMarker.rotationVector.z);
			CCQuaternion myRotation = new CCQuaternion().fromAngleAxis(theta, axis);
			myMarker.rotation.fromAngleAxis(theta, axis);
					 
			double [] euler = new double[3];
			myRotation.toEulerAngles(euler);
//			 if(euler[0] < 0) {
//				euler[0] *= -1;
////				euler[2] *= -1;
//			 }
			 if(euler[2] < 0) {
				 euler[0] *= -1;
				 euler[2] *= -1;
			 }
			 myX = CCMath.degrees(euler[0]) * 1 + _cXRot;
			 myY = CCMath.degrees(euler[1]) * 1 + _cYRot;
			 myZ = CCMath.degrees(euler[2]) * 1 + _cZRot;

			 euler[0] = CCMath.radians(myX);
			 euler[1] = CCMath.radians(myY);
			 euler[2] = CCMath.radians(myZ);

			 myMarker.rotation.fromEulerAngles(euler);
//			 Mat myRotationMat = new Mat();
//			 CCMatrix3x3 myRotationMatrix = new CCMatrix3x3();
//			 CCMatrix4x4 myMatrix = new CCMatrix4x4();
//			 myMatrix.fromAngleAxis(theta, axis);
//			 Rodrigues(_myRotationVectors.row(0), myRotationMat);
//			 Mat myEigenMat = new Mat();
//			 eigen(myRotationMat, myEigenMat);
//			 
//			 CCLog.info(myEigenMat);
//			 
//			 DoubleIndexer myIndexer = myRotationMat.createIndexer();
//			 for(int j = 0; j < 9;j++) {
//				 CCLog.info( myIndexer.get(j));
//			 }
//			 
//			 myRotationMatrix.fromAngleAxis(theta, axis);
//			 CCLog.info(myRotationMatrix);
//			 CCLog.info(myMatrix);
				
			 
			/*
			 * float theta = (float)(Math.Sqrt(m.x*m.x + m.y*m.y + m.z*m.z)*180/Math.PI);
Vector3 axis = new Vector3 (-m.x, m.y, -m.z);
Quaternion rot = Quaternion.AngleAxis (theta, axis);
			 */
			
			DoubleIndexer myTranslationIndexer = _myTranslationVectors.row(i).createIndexer();
			myMarker.translationVector.set(
				myTranslationIndexer.get(0),
				myTranslationIndexer.get(1),
				myTranslationIndexer.get(2)
			);
			
			_myMarker.add(myMarker);
		}
	}

	public void drawMarkers(Mat theDrawMat) {
		drawDetectedMarkers(theDrawMat, _myCorners);
	}
		
	public void drawAxisF(Mat theDrawMat) {
		for(int i = 0; i < _myRotationVectors.rows();i++) {
			if(i == 0) {
				DoubleIndexer myRIndexer = _myRotationVectors.row(i).createIndexer();
				DoubleIndexer myTIndexer = _myTranslationVectors.row(i).createIndexer();
//				CCLog.info(myTIndexer.get(0),myTIndexer.get(1),myTIndexer.get(2),myRIndexer.get(0),myRIndexer.get(1),myRIndexer.get(2));
				
				
					

			}
			drawAxis(theDrawMat, _myCameraMat, _myCameraDistortions,  _myRotationVectors.row(i), _myTranslationVectors.row(i), 0.02f);
		}
	}
	
	public static Mat loadMat(CCDataElement theXML, String theNodeName) {
		String myType = theXML.child(theNodeName+"Type").content();
		CCDataElement myMatrixValue = theXML.child(theNodeName+"Values");
		
		
		if(!myType.equals("CV_64F"))return null;
		int myRows = myMatrixValue.child(0).child(0).countChildren();
		int myCols =  myMatrixValue.child(0).countChildren();
		
		Mat myResult = new Mat(myRows,myCols,CV_64F);
		DoubleIndexer myIndexer = myResult.createIndexer();
		int i = 0;
		for(CCDataElement myArrayArray:myMatrixValue.child("ArrayOfArrayOfDouble")) {
			for(CCDataElement myArray:myArrayArray) {
				myIndexer.put(i++, myArray.doubleContent());
			}
		}
			
		return myResult;
		
	}
	
	public static void main(String[] args) {
		
		CCDataElement myXML = CCXMLIO.createXMLElement(CCNIOUtil.dataPath("camera.xml"));
		loadMat(myXML,"CameraMatrices");
		loadMat(myXML,"DistCoeffs");
		CCDataElement myCameraMatricesValue = myXML.child("CameraMatricesValues");
		for(CCDataElement myArrayArray:myCameraMatricesValue.child("ArrayOfArrayOfDouble")) {
			for(CCDataElement myArray:myArrayArray) {
				CCLog.info(myArray.doubleContent());
			}
		}
	}

	@Override
	public Iterator<CCMarker> iterator() {
		return new ArrayList<>(_myMarker).iterator();
	}
	
	public List<CCMarker> marker() {
		return new ArrayList<>(_myMarker);
	}
}