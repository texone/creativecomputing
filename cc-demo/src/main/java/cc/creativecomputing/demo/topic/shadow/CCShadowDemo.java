package cc.creativecomputing.demo.topic.shadow;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShadowDemo extends CCGL2Adapter {
	@CCProperty(name = "shader")
	private CCGLProgram _cShader;
	
	@CCProperty(name = "simple depth")
	private CCGLProgram _cSimpleDepth;
	
	@CCProperty(name = "Debug Depth Quad")
	private CCGLProgram _cDebugDepthQuad;
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "shadow_mapping_vertex.glsl"),
			CCNIOUtil.classPath(this, "shadow_mapping_fragment.glsl")
		);
		_cDebugDepthQuad = new CCGLProgram(
			CCNIOUtil.classPath(this, "debug_quad_vertex.glsl"),
			CCNIOUtil.classPath(this, "debug_quad_fragment.glsl")
		);
		_cDebugDepthQuad = new CCGLProgram(
			CCNIOUtil.classPath(this, "debug_quad.vs"),
			CCNIOUtil.classPath(this, "debug_quad.fs")
		);
		
		_cCameraController = new CCCameraController(this, g, 100);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		_cCameraController.camera().draw(g);
	}

	public static void main(String[] args) {

		CCShadowDemo demo = new CCShadowDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
