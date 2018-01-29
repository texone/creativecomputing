package cc.creativecomputing.demo.gl2.postprocess;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawAttributes;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.primitives.CCCylinderMesh;
import cc.creativecomputing.graphics.primitives.CCQuadMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCParallaxDemo4 extends CCGL2Adapter {
	
	public static class CCBoxMesh extends CCVBOMesh{
		public CCBoxMesh(){
			super(CCDrawMode.TRIANGLES, 24);
			// Positions
			prepareVertexData(3);
			_myVertices.put(new float[]{
				-100, -100,  100,   100,  100,  100,  -100,  100,  100,   100, -100,  100, // Front
				-100, -100, -100,   100,  100, -100,  -100,  100, -100,   100, -100, -100, // Back
				 100, -100, -100,   100,  100,  100,   100, -100,  100,   100,  100, -100, // Right
				-100, -100, -100,  -100,  100,  100,  -100, -100,  100,  -100,  100, -100, // Left
				-100,  100, -100,   100,  100,  100,  -100,  100,  100,   100,  100, -100, // Top
				-100, -100, -100,   100, -100,  100,  -100, -100,  100,   100, -100, -100, // Bottom
			});
			// normal
			prepareNormalData();
			_myNormals.put(new float[]{
				 0,  0,  1,   0,  0,  1,   0,  0,  1,   0,  0,  1, // Front
				 0,  0, -1,   0,  0, -1,   0,  0, -1,   0,  0, -1, // Back
				 1,  0,  0,   1,  0,  0,   1,  0,  0,   1,  0,  0, // Right
				-1,  0,  0,  -1,  0,  0,  -1,  0,  0,  -1,  0,  0, // Left
				 0,  1,  0,   0,  1,  0,   0,  1,  0,   0,  1,  0, // Top
				 0, -1,  0,   0, -1,  0,   0, -1,  0,   0, -1,  0, // Bot	
			});
			// uvs
			prepareTextureCoordData(0, 2);
			_myTextureCoords[0].put(new float[]{
				0,  1,  1,  0,  0,  0,  1,  1, // Front
				1,  1,  0,  0,  1,  0,  0,  1, // Back
				1,  1,  0,  0,  0,  1,  1,  0, // Right
				0,  1,  1,  0,  1,  1,  0,  0, // Left
				0,  0,  1,  1,  0,  1,  1,  0, // Top
				0,  1,  1,  0,  0,  0,  1,  1, // Bottom
			});
			
			indices(new int[]{
				0 , 1 , 2 ,    0 , 3 , 1 , // Front
				4 , 6 , 5 ,    4 , 5 , 7 , // Back
				8 , 9 , 10,    8 , 11, 9 , // Right
				12, 14, 13,    12, 13, 15, // Left
				16, 18, 17,    16, 17, 19, // Top
				20, 21, 22,    20, 23, 21, // Bottom
			});
		}
	}
	
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
		_myHeightMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/parallax/depth.png")));
		_myDiffuseMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/parallax/diffuse.png")));
		_myNormalMap = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/parallax/normal.png")));
		
		_myParalaxShader = new CCGLProgram(CCNIOUtil.classPath(this, "paralax_vertex4.glsl"), CCNIOUtil.classPath(this, "paralax_fragment4.glsl"));
		
		_cCameraController = new CCCameraController(this, g, 100);
		
//		_myMesh = new CCBoxMesh();
//		_myMesh.generateTangents(0, 1);
		
		_myMesh = new CCCylinderMesh(100, 600, 100, 100);
		_myMesh.generateTangents(0, 1);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@CCProperty(name = "draw attributes")
	private CCDrawAttributes _cDrawAttributes = new CCDrawAttributes();

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_cCameraController.camera().draw(g);
		
		_cDrawAttributes.start(g);
		g.texture(0, _myHeightMap);
		g.texture(1, _myDiffuseMap);
		g.texture(2, _myNormalMap);
		_myParalaxShader.start();
		_myParalaxShader.uniform1i("tex_depth", 0);
		_myParalaxShader.uniform1i("tex_diffuse", 1);
		_myParalaxShader.uniform1i("tex_norm", 2);
		_myParalaxShader.uniform3f("cameraPos", _cCameraController.camera().position());
		_myMesh.draw(g);
		_myParalaxShader.end();
		g.noTexture();
		_cDrawAttributes.end(g);
	}

	public static void main(String[] args) {

		CCParallaxDemo4 demo = new CCParallaxDemo4();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
