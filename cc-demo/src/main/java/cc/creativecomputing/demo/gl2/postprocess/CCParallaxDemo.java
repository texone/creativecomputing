package cc.creativecomputing.demo.gl2.postprocess;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.primitives.CCQuadMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCParallaxDemo extends CCGL2Adapter {
	
	private CCTexture2D _myHeightMap;
	private CCTexture2D _myDiffuseMap;
	private CCTexture2D _myNormalMap;
	
	@CCProperty(name = "paralax")
	private CCGLProgram _myParalaxShader;
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	
	private CCMesh _myMesh;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myHeightMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/paralax/height.jpg")));
		_myDiffuseMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/paralax/diffuse.jpg")));
		_myNormalMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/paralax/normal.jpg")));
		
		_myParalaxShader = new CCGLProgram(CCNIOUtil.classPath(this, "paralax_vertex.glsl"), CCNIOUtil.classPath(this, "paralax_fragment.glsl"));
		
		_cCameraController = new CCCameraController(this, g, 100);
		
		_myMesh = new CCQuadMesh(800,800,20,20);
		_myMesh.generateTangents(0, 1);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);
		g.texture(0, _myHeightMap);
		g.texture(1, _myDiffuseMap);
		g.texture(2, _myNormalMap);
		_myParalaxShader.start();
		_myParalaxShader.uniform1i("heightMap", 0);
		_myParalaxShader.uniform1i("diffuseMap", 1);
		_myParalaxShader.uniform1i("normalMap", 2);
		_myParalaxShader.uniform3f("cameraPos", _cCameraController.camera().position());
		_myMesh.draw(g);
		_myParalaxShader.end();
		g.noTexture();
	}

	public static void main(String[] args) {

		CCParallaxDemo demo = new CCParallaxDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
