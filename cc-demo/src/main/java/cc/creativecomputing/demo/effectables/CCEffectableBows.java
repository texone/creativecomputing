package cc.creativecomputing.demo.effectables;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.effects.modulation.CCColumnRowRingSource;
import cc.creativecomputing.effects.modulation.CCColumnRowSpiralSource;
import cc.creativecomputing.effects.modulation.CCPositionSource;
import cc.creativecomputing.effects.modulation.CCXYEuclidianDistanceSource;
import cc.creativecomputing.effects.modulation.CCXYManhattanDistanceSource;
import cc.creativecomputing.effects.modulation.CCXYRadialSource;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCShapeMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.primitives.CCSphereMesh;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCEffectableBows extends CCGL2Adapter {
	
	private class CCQuadEffectable extends CCEffectable{
		
		private double _myAlpha = 1;
		
		private double _myDist;
		
		private double _myRot;
		
		private double _myAngle;
		private double _myOffset;

		public CCQuadEffectable(int theId, CCVector3 theCenter, double theDist, double theRot) {
			super(theId);
			position().set(theCenter);
			
			_myDist = theDist;
			_myRot = theRot;
		}
		
		@Override
		public void apply(double...theValues) {
			_myAngle = theValues[0] * _cAngle;
			_myOffset = theValues[1] * _cHeight;
		}
		
		public void draw(CCGraphics g) {
			CCVector3 myPoint0 = new CCVector3(_myDist, 0);
			CCVector3 myPoint1 = new CCVector3(-_myDist, 0);
			
			g.pushMatrix();
			g.translate(position());
			g.rotate(_myRot);
			g.rotateY(0);
			CCMatrix4x4 myMat = new CCMatrix4x4();
			myMat.applyRotationY(CCMath.radians(_myAngle));
			CCVector3 myP0 = myMat.applyPostPoint(myPoint0);
			CCVector3 myP1 = myMat.applyPostPoint(myPoint1);
			myMat.applyPostPoint(new CCVector3(myPoint1));
			
			g.beginShape(CCDrawMode.LINES);
			g.color(255);
			g.vertex(myP0.x, myP0.y, myP0.z + _myOffset);
			g.vertex(myP1.x, myP1.y, myP1.z + _myOffset);
			g.color(255, 30);
			g.vertex(myP0.x, myP0.y, -200);
			g.vertex(myP0.x, myP0.y, myP0.z + _myOffset);
			g.vertex(myP1.x, myP1.y,  -200);
			g.vertex(myP1.x, myP1.y, myP1.z + _myOffset);
			g.endShape();
			
			g.beginShape(CCDrawMode.QUADS);
			g.color(255, 20);
			g.vertex(myP0.x, myP0.y, myP0.z + _myOffset);
			g.vertex(myP1.x, myP1.y, myP1.z + _myOffset);
			g.vertex(myP1.x, myP1.y, myP1.z + _myOffset + 100);
			g.vertex(myP0.x, myP0.y, myP0.z + _myOffset + 100);
			g.endShape();
			g.popMatrix();
		}
		
	}

	@CCProperty(name = "effects")
	private CCEffectManager<CCQuadEffectable> _myEffectManager;
	private List<CCQuadEffectable> _myCubes = new ArrayList<>();

	@CCProperty(name = "height", min = 0, max = 300)
	private double _cHeight = 100;
	@CCProperty(name = "angle", min = 0, max = 45)
	private double _cAngle = 100;
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	@CCProperty(name = "attributes")
	private CCDrawAttributes _cAttributes = new CCDrawAttributes();
	
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _cScreenCapture;

	
	private CCTexture2D _myPointTexture;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myPointTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/Visualisierung_Perspektive von oben_ohne Kronleuchter.jpg")));
		
		_cCameraController = new CCCameraController(this, g, 100);
		
		
		
		int i = 0;
		
		for(int r = 0; r < 10;r++) {
			double rad = (r + 1) * 20;
			for(int a = 0; a <= 8; a++) {
				CCVector3 myPoint0 = new CCVector3(CCVector2.circlePoint(a / 8d * CCMath.TWO_PI, rad, 0, 0));
				CCVector3 myPoint1 = new CCVector3(CCVector2.circlePoint((a + 1) / 8d * CCMath.TWO_PI, rad, 0, 0));
				CCVector3 myCenter = myPoint0.add(myPoint1).multiply(0.5);
				double myDist = myPoint0.distance(myPoint1) * 0.5;
				double myRot = a / 8d * 360 - 67.6666;
				
				CCQuadEffectable myCube = new CCQuadEffectable(i, myCenter, myDist, myRot);
				myCube.column(a);
				myCube.row(r);
				_myCubes.add(myCube);
				i++;
				
				
			}
		}
		
		g.rectMode(CCShapeMode.CENTER);
		
		_myEffectManager = new CCEffectManager<CCQuadEffectable>(_myCubes, "r", "l");
		_myEffectManager.addIdSources(CCEffectable.COLUMN_SOURCE, CCEffectable.ROW_SOURCE);
		_myEffectManager.addRelativeSources(
			new CCColumnRowRingSource(),
			new CCColumnRowSpiralSource(),
			new CCPositionSource("position"),
			new CCXYEuclidianDistanceSource("euclidian", 200, new CCVector2()),
			new CCXYManhattanDistanceSource("manhattan", 200, 200, new CCVector2()),
			new CCXYRadialSource("radial", new CCVector2())
		);
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
		
		_cScreenCapture = new CCScreenCaptureController(this);
		
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
//		g.clearColor(255);
		g.clear();
		g.color(255);
		g.image(_myPointTexture, -g.width()/2, -g.height() /2, g.width(), g.height());
		g.clearDepthBuffer();
		
		
		_cCameraController.camera().draw(g);
		
		_cAttributes.start(g);
		for(CCQuadEffectable myCube:_myCubes){
			myCube.draw(g);
		}
		
		_cAttributes.end(g);
		
		
	}

	public static void main(String[] args) {

		CCEffectableBows demo = new CCEffectableBows();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
