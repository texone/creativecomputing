package cc.creativecomputing.demo.opencv;

import java.util.ArrayList;
import java.util.List;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.camera.CCCameraController.CCCameraState;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCQuaternion;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.opencv.CCCVTexture;
import cc.creativecomputing.opencv.CCCVUtil;
import cc.creativecomputing.opencv.CCCVVideoCapture;
import cc.creativecomputing.opencv.CCCVVideoIn;
import cc.creativecomputing.opencv.CCCVVideoPlayer;
import cc.creativecomputing.opencv.CCMarker;
import cc.creativecomputing.opencv.CCMarkerDetection;
import cc.creativecomputing.opencv.filtering.CCBlur;
import cc.creativecomputing.opencv.filtering.CCCVShaderFilter;
import cc.creativecomputing.opencv.filtering.CCThreshold;

public class CCArucoDemo extends CCGL2Adapter {

	@CCProperty(name = "capture")
	private CCCVVideoCapture _myCapture;

	@CCProperty(name = "player")
	private CCCVVideoPlayer _myPlayer;

	private CCCVVideoIn _myVideoIn;

	private CCCVTexture _myTexture;

	@CCProperty(name = "marker detection")
	private CCMarkerDetection _cMarkerDetection;

	


	

	private boolean USE_CAPTURE = true;
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		if (USE_CAPTURE) {
			_myCapture = new CCCVVideoCapture(1);
			_myCapture.exposure(-8);
			_myCapture.frameWidth(1280);
			_myCapture.frameHeight(960);
			_myVideoIn = _myCapture;
		} else {
			_myPlayer = new CCCVVideoPlayer(CCNIOUtil.dataPath("cv/marker.mp4").toAbsolutePath().toString());
			_myVideoIn = _myPlayer;
		}
		_myVideoIn.start();
		_cMarkerDetection = new CCMarkerDetection(_myVideoIn, CCNIOUtil.dataPath("cv/microsoft_hd_life.xml"));
		
		_myTexture = new CCCVTexture();
		_myTexture.mustFlipVertically(true);
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	private List<CCVector3> _myRotationData = new ArrayList<>(); 

	@Override
	public void display(CCGraphics g) {
		g.clear();

		Mat myOrigin = _myVideoIn.mat();
		
		g.color(1d);
		_myTexture.mustFlipVertically(false);
		_myTexture.image(_cMarkerDetection.infoMat());

		for(CCMarker myMarker : _cMarkerDetection) {
			
//			double [] euler = new double[3];
//			myMarker.rotation.toEulerAngles(euler);
//			
//			_myRotationData.add(new CCVector3(euler[0], euler[1], euler[2]));
//			while(_myRotationData.size() > 1200) {
//				_myRotationData.remove(0);
//			}
		}

		g.strokeWeight(1);
//
		g.ortho2D();
		g.image(_myTexture, 0, 0);
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		int x = 0;
		g.color(255,0,0);
		for(CCVector3 myData:new ArrayList<>(_myRotationData)) {
			g.vertex(x,CCMath.degrees(myData.x) + 180);
			x++;
		}
		g.endShape();
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		x = 0;
		g.color(0,255,0);
		for(CCVector3 myData:new ArrayList<>(_myRotationData)) {
			g.vertex(x,CCMath.degrees(myData.y) + 180);
			x++;
		}
		g.endShape();
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		x = 0;
		g.color(0,0,255);
		for(CCVector3 myData:new ArrayList<>(_myRotationData)) {
			g.vertex(x,CCMath.degrees(myData.z) + 180);
			x++;
		}
		g.endShape();
		g.color(CCColor.WHITE);
		g.line(0, 0,2000,0);
		g.line(0, 90,2000,90);
		g.line(0, 180,2000,180);
		g.line(0, 270,2000,270);
		g.line(0, 360,2000,360);
		
		g.color(CCColor.RED);
		for(CCMarker myMarker : _cMarkerDetection) {
			g.beginShape(CCDrawMode.LINE_LOOP);
			for(CCVector2 myCorner:myMarker.corners) {
				g.vertex(myCorner);
			}
			g.endShape();
			

			CCVector2 myCenter = new CCVector2();
			for(CCVector2 myCorner:myMarker.corners) {
				myCenter.addLocal(myCorner);
			}
			myCenter.multiplyLocal(0.25);
			CCVector2 myXCenter0 = myMarker.corners.get(0).add(myMarker.corners.get(1)).multiplyLocal(0.5);
			CCVector2 myYCenter0 = myMarker.corners.get(1).add(myMarker.corners.get(2)).multiplyLocal(0.5);
			CCVector2 myXCenter1 = myMarker.corners.get(2).add(myMarker.corners.get(3)).multiplyLocal(0.5);
			CCVector2 myYCenter1 = myMarker.corners.get(3).add(myMarker.corners.get(0)).multiplyLocal(0.5);
			
			g.line(myCenter, myXCenter0);
			g.line(myCenter, myYCenter0);
			g.line(myCenter, myXCenter1);
			g.line(myCenter, myYCenter1);
			
			double myXlength0 = myXCenter0.distance(myCenter);
			double myYlength0 = myYCenter0.distance(myCenter);
			double myXlength1 = myXCenter1.distance(myCenter);
			double myYlength1 = myYCenter1.distance(myCenter);
			
			
			double myLength = 0;
			myLength += myMarker.corners.get(0).distance(myMarker.corners.get(1));
			myLength += myMarker.corners.get(1).distance(myMarker.corners.get(2));
			myLength += myMarker.corners.get(2).distance(myMarker.corners.get(3));
			myLength += myMarker.corners.get(3).distance(myMarker.corners.get(0));
			

			CCLog.info(myXlength0 / myXlength1 ,myYlength0 / myYlength1);
			//CCLog.info();
			
			CCVector2 myRotateZ = myMarker.corners.get(0).subtract(myCenter);
			double myAngleZ = CCMath.atan2(myRotateZ.y, myRotateZ.x);
			
			g.pointSize(5);
			g.beginShape(CCDrawMode.POINTS);
			g.vertex(myMarker.corners.get(0));
			g.vertex(myCenter);
			g.vertex(myXCenter0);
			g.vertex(myYCenter0);
			g.vertex(myYCenter1);
			g.vertex(myYCenter1);
			g.endShape();
			
			_myRotationData.add(new CCVector3(0,myLength / 180, myAngleZ));
			while(_myRotationData.size() > 1200) {
				_myRotationData.remove(0);
			}
			
			
		}
		
		
	
		g.strokeWeight(3);
		
		
		g.clearDepthBuffer();
		g.noDepthTest();
		
		g.camera().draw(g);
//		for(CCMarker myMarker : _cMarkerDetection) {
//			CCVector2 myCornerAvg = new CCVector2();
//			for(CCVector2 myCorner:myMarker.corners) {
//				myCornerAvg.addLocal(myCorner);
//			}
//			myCornerAvg.multiplyLocal(0.25);
//			
//			
//
//			_cCameraController.setState(
//				new CCCameraState(
//					myMarker.rotation, //modelRotation.y, 0, modelRotation.x
//					new CCVector3(
//						myMarker.translationVector.x,
//						myMarker.translationVector.y,
//						0
//					), 
//+ 					100 + myMarker.translationVector.z * 100
//				)
//			);
//			
//			g.pushMatrix();
//			
//			g.translate(myCornerAvg.x - 640, myCornerAvg.y - 360);
//			CCLog.info(myMarker.rotationVector);
//			g.rotate(myMarker.rotation);
//			g.color(1d, 0.25);
//			g.box(100);
//			g.color(CCColor.RED);
//			g.line(0,0,0,200,0,0);
//			g.color(CCColor.GREEN);
//			g.line(0,0,0,0,200,0);
//			g.color(CCColor.BLUE);
//			g.line(0,0,0,0,0,200);
//			g.popMatrix();
//		}
//		_cCameraController.camera().draw(g);
//		
//		
//	
//		g.color(1d, 0.25);
//		g.box(100);
//		g.color(CCColor.RED);
//		g.line(0,0,0,200,0,0);
//		g.color(CCColor.GREEN);
//		g.line(0,0,0,0,200,0);
//		g.color(CCColor.BLUE);
//		g.line(0,0,0,0,0,200);

		
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
