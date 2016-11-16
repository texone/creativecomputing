package cc.creativecomputing.demo.gl2.shader.imaging;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCVornoise extends CCGL2Adapter {

//	@CCProperty(name = "voronoise")
	private CCGLProgram _myProgram;
	
	private CCTexture2D _myTexture;
	private CCTexture2D _myTexture2;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "voronoise.fs"));
		
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/Clouds.jpg")));
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);

		_myTexture2 = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/test1.jpg")));
		_myTexture2.wrap(CCTextureWrap.MIRRORED_REPEAT);
		CCLog.info(_myTexture.width() + ":" + _myTexture.height());
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

	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.texture(0,_myTexture);
		g.texture(1,_myTexture2);
		_myProgram.start();
		_myProgram.uniform2f("iResolution", g.width(), g.height());
		_myProgram.uniform3f("noiseBlend", _cNoiseBlendX, _cNoiseBlendY, _cNoiseBlendZ);
		_myProgram.uniform1f("scale", _cScale);
		_myProgram.uniform1f("gain", _cGain);
		_myProgram.uniform1f("octaves", _cOctaves);
		_myProgram.uniform1f("lacunarity", _cLacunarity);
		_myProgram.uniform1f("randomOffset", _cRandomness);
		_myProgram.uniform1i("tex0", 0);
		_myProgram.uniform1i("tex1", 1);
		_myProgram.uniform1f("blend", _cBlend);
		_myProgram.uniform1f("blendRandom", _cBlendRandom);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex( g.width()/2,  g.height()/2);
		g.vertex(-g.width()/2,  g.height()/2);
		g.vertex(-g.width()/2, -g.height()/2);
		g.vertex( g.width()/2, -g.height()/2);
		g.endShape();
		_myProgram.end();
		g.noTexture();
	}

	public static void main(String[] args) {

		CCVornoise demo = new CCVornoise();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1680, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
