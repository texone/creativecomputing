package cc.creativecomputing.demo.gl2.postprocess;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.postprocess.CCPostProcess;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCSSAODemo extends CCGL2Adapter {
	
	private class Box{
		private float size;
		private CCVector3 position;
		
		private Box() {
			size = CCMath.random(45, 175);
			position = new CCVector3().randomize(500);
		}
		
		void display(CCGraphics g) {
			g.pushMatrix();
			g.translate(position);
			g.box(size);
			g.popMatrix();
		}
	}
	
	@CCProperty(name = "post process")
	private CCPostProcess _myPostProcess;
	
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "sample radius", min = 0, max = 20)
	private double sampleRadius = 0.4f;

	@CCProperty(name = "intensity", min = 0, max = 20)
	private double intensity = 2.5f;

	@CCProperty(name = "scale", min = 0, max = 1f)
	private double scale = 0.34f;

	@CCProperty(name = "bias", min = 0, max = 1)
	private double bias = 0.05f;

	@CCProperty(name = "jitter", min = 0, max = 100)
	private double jitter = 64.0f;

	@CCProperty(name = "self occlusion", min = 0, max = 1)
	private double selfOcclusion = 0.12f;

	@CCProperty(name = "shader")
	private CCGLProgram _mySSAOShader;
	private CCTexture2D _mySSAORandomTexture;

	private CCShaderBuffer _myShaderBuffer;
	
	private List<Box> _myBoxes = new ArrayList<>();

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myPostProcess = new CCPostProcess(g,g.width(),g.height());
		
		for(int i = 0; i< 500;i++) {
			_myBoxes.add(new Box());
		}
		
		_myCameraController = new CCCameraController(this, g, 100);
		
		_mySSAOShader = new CCGLProgram(
			null,
			CCNIOUtil.classPath(this, "ssao.glsl")
		);

		_mySSAORandomTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "randomNormals.png")));
		_mySSAORandomTexture.wrap(CCTextureWrap.REPEAT);
		_mySSAORandomTexture.textureFilter(CCTextureFilter.NEAREST);

		_myShaderBuffer = new CCShaderBuffer(g.width(), g.height(), CCTextureTarget.TEXTURE_2D);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myPostProcess.beginDraw(g);
		g.clearColor(0, 255);
		g.clear();
		
		_myCameraController.camera().draw(g);
		_myPostProcess.geometryBuffer().updateMatrix();
		for(Box myBox:_myBoxes) {
			myBox.display(g);
		}
		_myPostProcess.endDraw(g);
		
//		g.texture(0, _myPostProcess.geometryBuffer().positions());
//		g.texture(1, _myPostProcess.geometryBuffer().normals());
//		g.texture(2, _mySSAORandomTexture);
//
		_mySSAOShader.start();
//
//		_mySSAOShader.uniform1i("positions", 0);
//		_mySSAOShader.uniform1i("normals", 1);
//		_mySSAOShader.uniform1i("random", 2);
//
//		_mySSAOShader.uniform1f("sampleRadius", sampleRadius);
//		_mySSAOShader.uniform1f("intensity", intensity);
//		_mySSAOShader.uniform1f("scale", scale / 25f);
//		_mySSAOShader.uniform1f("bias", bias);
//		_mySSAOShader.uniform1f("jitter", jitter);
//		_mySSAOShader.uniform1f("selfOcclusion", selfOcclusion);
//		_mySSAOShader.uniform2f("screenSize", _myShaderBuffer.width(), _myShaderBuffer.height());
//		_mySSAOShader.uniform2f("invScreenSize", 1.0f / _myShaderBuffer.width(), 1.0f / _myShaderBuffer.height());

		g.color(255);
		_myShaderBuffer.clear(g);
		_myShaderBuffer.draw(g);

		_mySSAOShader.end();
//
//		g.noTexture();
		
		g.pushMatrix();
		g.translate(-g.width()/2, -g.height()/2);
		
		g.image(_myShaderBuffer.attachment(0), 0,0);
		g.popMatrix();
	}

	public static void main(String[] args) {

		CCSSAODemo demo = new CCSSAODemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
