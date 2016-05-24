package cc.creativecomputing.demo.gl2.texture;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.signal.CCMixSignal;
import cc.creativecomputing.model.collada.CCColladaLoader;
import cc.creativecomputing.model.collada.CCColladaScene;
import cc.creativecomputing.model.collada.CCColladaSceneNode;

public class CCTextureSphereMapDemo extends CCGL2Adapter{
	
	@CCProperty(name = "texture")
	private CCTexture2DAsset _myTextureAsset;
	private CCGLProgram _myShader;
	
	@CCProperty(name = "radius", min = 0, max = 1000)
	private double _cRadius = 100;

	@CCProperty(name = "height", min = 0, max = 300)
	private double _cHeight = 100;
	@CCProperty(name = "geom offset", min = -300, max = 300)
	private double _cOffset = 100;
	@CCProperty(name = "angle", min = 0, max = CCMath.HALF_PI)
	private double _cANgle = CCMath.QUARTER_PI;
	

	@CCProperty(name = "rot 0", min = -CCMath.HALF_PI, max = CCMath.HALF_PI)
	private double _cRot0 = 0;
	@CCProperty(name = "rot 1", min = -CCMath.PI, max = CCMath.PI)
	private double _cRot1 = 0;
	
	@CCProperty(name = "amount", min = 0, max = 1)
	private float _cAmount = 1;
	
	@CCProperty(name = "alpha", min = 0, max = 1)
	private float _cAlpha = 1;
	
	@CCProperty(name = "light x", min = -1, max = 1)
	private float _cLightX = 0;
	
	@CCProperty(name = "light y", min = -1, max = 1)
	private float _cLightY = 0;
	
	@CCProperty(name = "light z", min = -1, max = 1)
	private float _cLightZ = 1;
	
	@CCProperty(name = "specularPow", min = -1, max = 10)
	private float _cSpecularPow = 1;
	@CCProperty(name = "specularBrightPow", min = -1, max = 150)
	private float _cSpecularBrightPow = 1;
	

	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	@CCProperty (name = "noise amount", min = 0, max = 5)
	private float _cNoiseAmount = 1;
	
	@CCProperty (name = "noise scale", min = 0, max = 1)
	private float _cNoiseScale = 1;
	
	@CCProperty (name = "tex offset", min = 0, max = 1)
	private float _cTexOffset = 1;
	
	@CCProperty (name = "filter")
	private CCTextureFilter _cFilter = CCTextureFilter.LINEAR;

	private Path _myFolder = CCNIOUtil.dataPath("3d/");
	
	private CCTexture3D _myTexture;
	
	private CCColladaLoader _myColladaLoader;
	
	private Map<String, CCColladaSceneNode> _myCameras = new HashMap<>();
	private Map<String, CCColladaSceneNode> _myGeometries = new HashMap<>();
	

	private CCGLProgram _myLightShader;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		List<Path> myFiles = CCNIOUtil.list(_myFolder, "png");
		_myTexture = new CCTexture3D(CCImageIO.newImage(myFiles.get(0)), myFiles.size());
		_myTexture.wrap(CCTextureWrap.REPEAT);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		
		int i = 0;
		for(Path myPath:myFiles) {
			_myTexture.updateData(CCImageIO.newImage(myPath), i++);
			CCLog.info(i + myPath.toAbsolutePath().toString());
//			if(i >= 11)break;
		}
		
		_myTextureAsset = new CCTexture2DAsset(glContext());
		_myShader = new CCGLProgram(CCNIOUtil.classPath(this, "spheremap_vp.glsl"), CCNIOUtil.classPath(this, "spheremap_fp.glsl"));
		
		_myCameraController = new CCCameraController(this, g, 100);
		
		_myColladaLoader = new CCColladaLoader(CCNIOUtil.dataPath("fraport_christian_export.dae"));
		
		for(CCColladaScene myScene:_myColladaLoader.scenes()){
			System.out.println("SCENE:" + myScene.name());
			for(CCColladaSceneNode myNode:myScene.nodes()){
				System.out.println(myNode.instanceType() +":" +myNode.name());
				if(myNode.instanceType() == null)continue;
				switch(myNode.instanceType()){
				case CAMERA:
					_myCameras.put(myNode.name(), myNode);
					break;
				case GEOMETRY:
					System.out.println(myNode.name());
					_myGeometries.put(myNode.name(), myNode);
					break;
				default:
					System.out.println(myNode.instanceType() +":" +myNode.name());
					break;
				}
			}
		}
		
		_myLightShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "light_weights_vert.glsl"), 
			CCNIOUtil.classPath(this, "light_weights_frag.glsl")
		);
	}

	@CCProperty(name = "offset", min = 0, max = 10)
	private float _myOffset = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
//		_myOffset += theAnimator.deltaTime() * 0.1f;
		
	}
	
	@CCProperty(name = "noise")
	private CCMixSignal _myNoise = new CCMixSignal();
	
	
	@Override
	public void display(CCGraphics g) {
		_myTexture.textureFilter(_cFilter);
//		_myShader.reload();
		g.clear();
		

		_myCameraController.camera().draw(g);
		
		_myShader.start();
		_myShader.uniform1i("texture", 0);
//		if(_myTextureAsset.value() != null)g.texture(_myTextureAsset.value());
		g.texture(_myTexture);
		g.beginShape(CCDrawMode.TRIANGLE_STRIP);
		for(int i = 0; i < 180;i++){
			double angle0 = CCMath.radians(i);
			double x = CCMath.cos(angle0) * _cRadius;
			double z = CCMath.sin(angle0) * _cRadius;
			
			double angle1 = CCMath.HALF_PI - _cANgle;
			double xs = CCMath.sin(angle1 + _cRot0) * CCMath.cos(angle0 + _cRot1);
			double ys = CCMath.sin(angle1 + _cRot0) * CCMath.sin(angle0 + _cRot1);
			double zs = CCMath.cos(angle1 + _cRot0);
			
			g.textureCoords4D(0, xs, ys, zs, _myNoise.value(angle0 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount + _cTexOffset);
			g.vertex(x, -_cHeight / 2 + _cOffset, z);
			
			angle1 = CCMath.HALF_PI + _cANgle;
			xs = CCMath.sin(angle1 + _cRot0) * CCMath.cos(angle0 + _cRot1);
			ys = CCMath.sin(angle1 + _cRot0) * CCMath.sin(angle0 + _cRot1);
			zs = CCMath.cos(angle1 + _cRot0);
			g.textureCoords4D(0, xs, ys, zs, _myNoise.value(angle0 * _cNoiseScale, _myOffset * _cNoiseScale) * _cNoiseAmount + _cTexOffset);
			g.vertex(x, _cHeight / 2 + _cOffset, z);
			
		}
		g.endShape();
//		if(_myTextureAsset.value() != null)g.noTexture();
		g.noTexture();
		_myShader.end();
		
//		_myLightShader.start();
//		_myLightShader.uniform1f("amount", _cAmount);
//		_myLightShader.uniform1f("alpha", _cAlpha);
//		_myLightShader.uniform3f("lightDir", new CCVector3(_cLightX, _cLightY, _cLightZ).normalize());
//		_myLightShader.uniform1f("specularPow", _cSpecularPow);
//		_myLightShader.uniform1f("specularBrightPow", _cSpecularBrightPow);
//		for(CCColladaScene myScene:_myColladaLoader.scenes()){
//			myScene.draw(g);
//		}
//		_myLightShader.end();
	}
	
	public static void main(String[] args) {
		
		
		CCTextureSphereMapDemo demo = new CCTextureSphereMapDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
