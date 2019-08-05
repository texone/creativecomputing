package cc.creativecomputing.demo.svg;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGIO;

public class CCSVGDemo extends CCGL2Adapter {
	
	
	private List<List<CCVector3>> _mySplines;
	
	private CCTexture2D _myBack;
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	CCSVGDocument _myDocument;

	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myBack = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("Wittgenstein.png")));
		_myBack.mustFlipVertically(false);
		_myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("Wittgenstein.svg"));
		_mySplines = new ArrayList<>();
		int c = 0;
		for(CCLinearSpline mySpline:_myDocument.contours(1)) {
			List<CCVector3> myPoints = new ArrayList<>();
			double myLength = mySpline.totalLength();
			int myNumberOfPoints = CCMath.ceil(myLength / 1);
			for(int i = 0; i <= myNumberOfPoints;i++) {
				double d = CCMath.norm(i, 0, myNumberOfPoints);
				myPoints.add(mySpline.interpolate(d));
				c++;
			}
			_mySplines.add(myPoints);
		}
		
		c = 0;
		for(CCLinearSpline mySpline:_myDocument.contours(1)) {
			List<CCVector3> myPoints = new ArrayList<>();
			double myLength = mySpline.totalLength();
			int myNumberOfPoints = CCMath.ceil(myLength / 5);
			
			for(int i = 0; i < myNumberOfPoints;i++) {
				double d0 = CCMath.norm(i, 0, myNumberOfPoints);
				double d1 = CCMath.norm(i + 1, 0, myNumberOfPoints);
				CCVector3 p0 = mySpline.interpolate(d0);
				CCVector3 p1 = mySpline.interpolate(d1);
				CCVector3 dir = p1.subtract(p0).normalizeLocal();
				myPoints.add(p0.add(dir.y*2, -dir.x*2,0));
				myPoints.add(p0.add(-dir.y*2, dir.x*2,0));
				c++;
			}
			_mySplines.add(myPoints);
		}
		
		CCLog.info(c);
		
		_cCameraController = new CCCameraController(this, g, 100);
	}
	
	

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.ortho();
//		_cCameraController.dr
		g.scale(1);
		g.color(1d);
		g.image(_myBack, 0,0);
		_mySplines.clear();
		for(CCLinearSpline mySpline:_myDocument.contours(1)) {
			List<CCVector3> myPoints = new ArrayList<>();
			double myLength = mySpline.totalLength();
			int myNumberOfPoints = CCMath.ceil(myLength / 5);
			
			for(int i = 0; i < myNumberOfPoints;i++) {
				double d0 = CCMath.norm(i, 0, myNumberOfPoints);
				double d1 = CCMath.norm(i + 1, 0, myNumberOfPoints);
				CCVector3 p0 = mySpline.interpolate(d0);
				CCVector3 p1 = mySpline.interpolate(d1);
				CCVector3 dir = p1.subtract(p0).normalizeLocal();
				myPoints.add(p0.add(dir.y*10, -dir.x*10,0));
				myPoints.add(p0.add(-dir.y*10, dir.x*10,0));
			}
			_mySplines.add(myPoints);
		}
		
		g.pushAttribute();
		g.polygonMode(CCPolygonMode.LINE);
		g.color(1d,0,0);
		for(List<CCVector3> mySpline :_mySplines) {
			g.beginShape(CCDrawMode.TRIANGLE_STRIP);
			for(CCVector3 myPoint:mySpline) {
				g.vertex(myPoint);
			}
			g.endShape();
		}
		g.popAttribute();
	}

	public static void main(String[] args) {

		CCSVGDemo demo = new CCSVGDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(3500, 1200);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
		
//		CCSVGDocument myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("notes01.svg"));
//		myDocument.forEach(e -> CCLog.info(e));
//		CCLog.info(myDocument);
	}
}
