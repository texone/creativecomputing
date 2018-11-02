package cc.creativecomputing.demo.topic.geometry;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCLine3;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class CCLineRendererDemo extends CCGL2Adapter {
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	@CCProperty(name = "renderer")
	private CCLineRenderer _myLineRenderer;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cCameraController = new CCCameraController(this, g, 100);
		
		_myLineRenderer = new CCLineRendererVertex(createLines(g));
	}
	
	private List<CCLine3> createLines(CCGraphics g){
		
		CCMath.randomSeed(1);
		CCVector3 myPos  = new CCVector3(0,0,0);
		List<CCLine3> myResult = new ArrayList<>();
		double myLastAngle = 0;
		
		for(int i = 0; i < 2000;i++) {
			CCVector3 myStart = myPos.clone();
			double mySegLength = CCMath.random(50,100);
			double myAngle = myLastAngle;
			while(myAngle == myLastAngle) {
				myAngle = CCMath.HALF_PI * CCMath.floor(CCMath.random(4));
			}
			myLastAngle = myAngle;
			
			CCVector2 myAdd = CCVector2.toCartesian(myAngle, mySegLength);
			double myRand = CCMath.random();
			if(myRand > 0.66)myPos.addLocal(myAdd.x, myAdd.y, 0);
			else if(myRand > 0.33)myPos.addLocal(myAdd.x, 0, myAdd.y);
			else myPos.addLocal(0, myAdd.x, myAdd.y);

			CCVector3 myEnd = myPos.clone();
			
			myResult.add(new CCLine3(myStart, myEnd));
			
			if(myPos.x > g.width() / 2)myPos.x -= g.width();
			if(myPos.x < -g.width() / 2)myPos.x += g.width();
			if(myPos.y > g.height() / 2)myPos.y -= g.height();
			if(myPos.y < -g.width() / 2)myPos.y += g.height();
			if(myPos.z > g.height() / 2)myPos.z -= g.height();
			if(myPos.z < -g.width() / 2)myPos.z += g.height();
		}
		
		return myResult;
	}

	@Override
	public void update(CCAnimator theAnimator) {
		
	}

	@Override 
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);
		
		
//		for(int x = 0; x < 4;x++) {
//			for(int y = 0; y < 4;y++) {
//				for(int z = 0; z < 4;z++) {
			
			g.noDepthTest();
			g.blendMode(CCBlendMode.ADD);
			g.strokeWeight(5);
			_myLineRenderer.draw(g);
//		}
//			}
//		}
	}

	public static void main(String[] args) {

		CCLineRendererDemo demo = new CCLineRendererDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
