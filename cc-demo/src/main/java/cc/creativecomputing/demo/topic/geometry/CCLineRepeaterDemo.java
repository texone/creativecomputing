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
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector4;

public class CCLineRepeaterDemo extends CCGL2Adapter {
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	
	@CCProperty(name = "shader")
	private CCGLProgram _cshader;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cCameraController = new CCCameraController(this, g, 100);
		
		_cshader = new CCGLProgram(
			CCNIOUtil.classPath(this, "lines_reeat_vertex.glsl"),
			CCNIOUtil.classPath(this, "lines_reeat_fragment.glsl")
		);
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
			CCMath.randomSeed(1);
			CCVector4 myPos  = new CCVector4(0,0,0,0);
			List<CCVector4> myPoints = new ArrayList<>();
			double myLastAngle = 0;
			double myLength = 0;
			for(int i = 0; i < 2000;i++) {
				myPoints.add(myPos.clone());
				double mySegLength = CCMath.random(50,100);
				double myAngle = myLastAngle;
				while(myAngle == myLastAngle) {
					myAngle = CCMath.HALF_PI * CCMath.floor(CCMath.random(4));
				}
				myLastAngle = myAngle;
				
				CCVector2 myAdd = CCVector2.toCartesian(myAngle, mySegLength);
				double myRand = CCMath.random();
				if(myRand > 0.66)myPos.addLocal(myAdd.x, myAdd.y, 0, mySegLength);
				else if(myRand > 0.33)myPos.addLocal(myAdd.x, 0, myAdd.y, mySegLength);
				else myPos.addLocal(0, myAdd.x, myAdd.y, mySegLength);

				myPoints.add(myPos.clone());
				
				if(myPos.x > g.width() / 2)myPos.x -= g.width();
				if(myPos.x < -g.width() / 2)myPos.x += g.width();
				if(myPos.y > g.height() / 2)myPos.y -= g.height();
				if(myPos.y < -g.width() / 2)myPos.y += g.height();
				if(myPos.z > g.height() / 2)myPos.z -= g.height();
				if(myPos.z < -g.width() / 2)myPos.z += g.height();
				
				myLength += mySegLength;
			}
			CCLog.info(myPos.w, myLength);
			g.noDepthTest();
			g.blendMode(CCBlendMode.ADD);
			g.strokeWeight(5);
			_cshader.start();
			_cshader.uniform1f("time", animator().time());
			g.beginShape(CCDrawMode.LINES);
			for(int i = 0; i < myPoints.size();i++) {
				CCVector4 myPoint = myPoints.get(i);
				g.textureCoords2D(CCMath.norm(myPoint.w, 0, myLength), 0);
				g.vertex(myPoint.xyz());
			}
			g.endShape();
			_cshader.end();
//		}
//			}
//		}
	}

	public static void main(String[] args) {

		CCLineRepeaterDemo demo = new CCLineRepeaterDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
