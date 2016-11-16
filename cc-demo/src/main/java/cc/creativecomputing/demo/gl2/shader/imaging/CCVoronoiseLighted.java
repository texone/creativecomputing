package cc.creativecomputing.demo.gl2.shader.imaging;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;

public class CCVoronoiseLighted extends CCGL2Adapter {

	private CCGLProgram _myProgram;
	
	@CCProperty(name = "lighting")
	private CCGLProgram _myLightingShader;
	
	private CCTexture2D _myTexture;
	private CCTexture2D _myTexture2;
	
	private CCShaderBuffer _myNoiseMap;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "voronoise3.fs"));
		
		_myLightingShader = new CCGLProgram(null, CCNIOUtil.classPath(this, "lighting.glsl"));
		
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/Clouds.jpg")));
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);

		_myTexture2 = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/gradient.png")));
		_myTexture2.wrap(CCTextureWrap.MIRRORED_REPEAT);
		CCLog.info(_myTexture.width() + ":" + _myTexture.height());
		
		_myNoiseMap = new CCShaderBuffer(g.width(), g.height(), CCTextureTarget.TEXTURE_2D);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@CCProperty(name = "noise blend x", min = 0, max = 1)
	private double _cNoiseBlendX = 0;
	@CCProperty(name = "noise blend y", min = 0, max = 1)
	private double _cNoiseBlendY = 0;
	@CCProperty(name = "noise blend z", min = 0, max = 1)
	private double _cNoiseBlendZ = 0;

	@CCProperty(name = "scale", min = 0, max = 1)
	private double _cScale = 0;
	@CCProperty(name = "gain", min = 0, max = 1)
	private double _cGain = 0;
	@CCProperty(name = "octaves", min = 0, max = 10)
	private double _cOctaves = 0;
	@CCProperty(name = "lacunarity", min = 0, max = 2)
	private double _cLacunarity = 0;
	@CCProperty(name = "randomness", min = 0, max = 200)
	private double _cRandomness = 0;
	@CCProperty(name = "blend", min = 0, max = 1)
	private double _cBlend = 0;
	@CCProperty(name = "blend random", min = 0, max = 1)
	private double _cBlendRandom = 0;

	@CCProperty(name = "tex x blend", min = 0, max = 1)
	private double _cTexXBlend = 0;
	@CCProperty(name = "tex y blend", min = 0, max = 1)
	private double _cTexYBlend = 0;
	@CCProperty(name = "tex z blend", min = 0, max = 1)
	private double _cTexZBlend = 0;
	@CCProperty(name = "tex w blend", min = 0, max = 1)
	private double _cTexWBlend = 0;

	@CCProperty(name = "normal x blend", min = 0, max = 1)
	private double _cNormalXBlend = 0;
	@CCProperty(name = "normal y blend", min = 0, max = 1)
	private double _cNormalYBlend = 0;
	@CCProperty(name = "normal z blend", min = 0, max = 1)
	private double _cNormalZBlend = 0;
	@CCProperty(name = "normal w blend", min = 0, max = 1)
	private double _cNormalWBlend = 0;
	

	@CCProperty(name = "draw lighted")
	private boolean _cDrawLighted = true;
	
	@CCProperty(name = "light x", min = -1, max = 1)
	private double _cLightX = 0;
	@CCProperty(name = "light y", min = -1, max = 1)
	private double _cLightY = 0;
	@CCProperty(name = "light z", min = -1, max = 1)
	private double _cLightZ = 0;
	
	@CCProperty(name = "specular pow", min = 0, max = 10)
	private double _cSpecularPow = 0;
	@CCProperty(name = "specular bright pow", min = 0, max = 10)
	private double _cSpecularBrightPow = 0;
	
	@CCProperty(name = "diffuse amp", min = 0, max = 1)
	private double _cDiffuseAmp = 0;
	@CCProperty(name = "specular amp", min = 0, max = 1)
	private double _cSpecularAmp = 0;
	@CCProperty(name = "specular bright amp", min = 0, max = 1)
	private double _cSpecularBrightAmp = 0;

	@Override
	public void display(CCGraphics g) {
		_myNoiseMap.clear();
		_myNoiseMap.beginDraw();
		g.clear();
		_myProgram.start();
		_myProgram.uniform2f("iResolution", g.width(), g.height());
		_myProgram.uniform3f("noiseBlend", _cNoiseBlendX, _cNoiseBlendY, _cNoiseBlendZ);
		_myProgram.uniform1f("scale", _cScale);
		_myProgram.uniform1f("gain", _cGain);
		_myProgram.uniform1f("octaves", _cOctaves);
		_myProgram.uniform1f("lacunarity", _cLacunarity);
		_myProgram.uniform1f("time", animator().time());
		_myProgram.uniform4f("fTexOffsetBlends", _cTexXBlend, _cTexYBlend, _cTexZBlend, _cTexWBlend);
		_myProgram.uniform4f("fNormalOffsetBlends", _cNormalXBlend, _cNormalYBlend, _cNormalZBlend, _cNormalWBlend);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(g.width(), g.height());
		g.vertex(0, g.height());
		g.vertex(0, 0);
		g.vertex(g.width(), 0);
		g.endShape();
		_myProgram.end();
		_myNoiseMap.endDraw();
		
		g.clear();
		
		
		if(_cDrawLighted){
			g.texture(0, _myTexture);
			g.texture(1, _myNoiseMap.attachment(0));
			_myLightingShader.start();
			_myLightingShader.uniform1i("colorTex", 0);
			_myLightingShader.uniform1i("brightTex", 1);
	
			_myLightingShader.uniform3f("lightDir", new CCVector3(_cLightX, _cLightY, _cLightZ).normalizeLocal());
			_myLightingShader.uniform1f("specularPow", _cSpecularPow);
			_myLightingShader.uniform1f("specularBrightPow", _cSpecularBrightPow);
	
			_myLightingShader.uniform1f("diffuseAmp", _cDiffuseAmp);
			_myLightingShader.uniform1f("specularAmp", _cSpecularAmp);
			_myLightingShader.uniform1f("specularBrightAmp", _cSpecularBrightAmp);
	
			_myLightingShader.uniform2f("windowSize", g.width(), g.height());
	
			g.beginShape(CCDrawMode.QUADS);
			g.vertex(-g.width() / 2, -g.height() / 2);
			g.vertex(g.width() / 2, -g.height() / 2);
			g.vertex(g.width() / 2, g.height() / 2);
			g.vertex(-g.width() / 2, g.height() / 2);
			g.endShape();
			_myLightingShader.end();
			g.noTexture();
		}else{
			g.image(_myNoiseMap.attachment(0), -g.width()/2, -g.height()/2);
		}
	}

	public static void main(String[] args) {

		CCVoronoiseLighted demo = new CCVoronoiseLighted();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1680, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
