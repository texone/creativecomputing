package cc.creativecomputing.demo.topic.geometry;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector4;

public class CCLineRepeaterVolumeDemo extends CCGL2Adapter {
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	
	@CCProperty(name = "shader")
	private CCGLProgram _cshader;
	
	private CCVBOMesh _myMesh;
	
	@CCProperty(name = "capture")
	private CCScreenCaptureController _cScreenCapture;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cCameraController = new CCCameraController(this, g, 100);
		
		_cshader = new CCGLProgram(
			CCNIOUtil.classPath(this, "lines_repeat_volumetric_vertex.glsl"),
			CCNIOUtil.classPath(this, "lines_repeat_volumetric_fragment.glsl")
		);
		
		_cScreenCapture = new CCScreenCaptureController(this);
		
		createMesh(g);
	}
	
	private void addSegment(CCVector4 theStart, CCVector4 theEnd) {
		_myMesh.addTextureCoords(0, theEnd.xyz());
		_myMesh.addTextureCoords(0, theEnd.xyz());
		_myMesh.addTextureCoords(0, theEnd.xyz());
		_myMesh.addTextureCoords(0, theEnd.xyz());

		_myMesh.addTextureCoords(1, 1.0f, 1.0f, 1.0f, 0.0f);
		_myMesh.addTextureCoords(1, 1.0f,-1.0f, 1.0f, 1.0f);
		_myMesh.addTextureCoords(1, 0.0f,-1.0f, 0.5f, 1.0f);
		_myMesh.addTextureCoords(1, 0.0f, 1.0f, 0.5f, 0.0f);

		_myMesh.addTextureCoords(2, theStart.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theStart.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theStart.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theStart.w, 0, 0, 0);

		_myMesh.addVertex(theStart.xyz());
		_myMesh.addVertex(theStart.xyz());
		_myMesh.addVertex(theStart.xyz());
		_myMesh.addVertex(theStart.xyz());

		_myMesh.addTextureCoords(0, theEnd.xyz());
		_myMesh.addTextureCoords(0, theEnd.xyz());
		_myMesh.addTextureCoords(0, theStart.xyz());
		_myMesh.addTextureCoords(0, theStart.xyz());
		
		_myMesh.addTextureCoords(1, 0.0f, 1.0f, 0.5f, 0.0f);
		_myMesh.addTextureCoords(1, 0.0f,-1.0f, 0.5f, 1.0f);
		_myMesh.addTextureCoords(1, 0.0f, 1.0f, 0.5f, 1.0f);
		_myMesh.addTextureCoords(1, 0.0f,-1.0f, 0.5f, 0.0f);

		_myMesh.addTextureCoords(2, theStart.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theStart.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theEnd.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theEnd.w, 0, 0, 0);
		
		_myMesh.addVertex(theStart.xyz());
		_myMesh.addVertex(theStart.xyz());
		_myMesh.addVertex(theEnd.xyz());
		_myMesh.addVertex(theEnd.xyz());
		
		_myMesh.addTextureCoords(1, 0.0f,-1.0f, 0.5f, 0.0f);
		_myMesh.addTextureCoords(1, 0.0f, 1.0f, 0.5f, 1.0f);
		_myMesh.addTextureCoords(1, 1.0f, 1.0f, 0.0f, 1.0f);
		_myMesh.addTextureCoords(1, 1.0f,-1.0f, 0.0f, 0.0f);

		_myMesh.addTextureCoords(2, theEnd.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theEnd.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theEnd.w, 0, 0, 0);
		_myMesh.addTextureCoords(2, theEnd.w, 0, 0, 0);
		
		_myMesh.addVertex(theEnd.xyz());
		_myMesh.addVertex(theEnd.xyz());
		_myMesh.addVertex(theEnd.xyz());
		_myMesh.addVertex(theEnd.xyz());
		
		_myMesh.addTextureCoords(0, theStart.xyz());
		_myMesh.addTextureCoords(0, theStart.xyz());
		_myMesh.addTextureCoords(0, theStart.xyz());
		_myMesh.addTextureCoords(0, theStart.xyz());
	}
	
	private void createMesh(CCGraphics g) {
		int mySize = 2000;
		_myMesh = new CCVBOMesh(CCDrawMode.QUADS);
		_myMesh.prepareVertexData(mySize * 12, 3);
		_myMesh.prepareTextureCoordData(mySize * 12, 0, 3);
		_myMesh.prepareTextureCoordData(mySize * 12, 1, 4);
		_myMesh.prepareTextureCoordData(mySize * 12, 2, 4);
		
		CCMath.randomSeed(1);
		CCVector4 myPos  = new CCVector4(0,0,0,0);
		double myLastAngle = 0;
		double myLength = 0;
		for(int i = 0; i < mySize;i++) {
			CCVector4 myStart = myPos.clone();
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

			CCVector4 myEnd = myPos.clone();
			
			addSegment(myStart, myEnd);
			
			if(myPos.x >  g.width() / 2)myPos.x -= g.width();
			if(myPos.x < -g.width() / 2)myPos.x += g.width();
			if(myPos.y >  g.height() / 2)myPos.y -= g.height();
			if(myPos.y < -g.width() / 2)myPos.y += g.height();
			if(myPos.z >  g.height() / 2)myPos.z -= g.height();
			if(myPos.z < -g.width() / 2)myPos.z += g.height();
			
			myLength += mySegLength;
		}
	}

	@Override
	public void update(CCAnimator theAnimator) {
		
	}

	@Override 
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);
		
		g.noDepthTest();
		g.blendMode(CCBlendMode.ADD);
		g.strokeWeight(5);
		_cshader.start();
		_cshader.uniform1f("aspect", g.aspectRatio());
		_cshader.uniform1f("time", animator().time());
		_myMesh.draw(g);
		_cshader.end();	
	}

	public static void main(String[] args) {

		CCLineRepeaterVolumeDemo demo = new CCLineRepeaterVolumeDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
