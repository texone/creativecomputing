package cc.creativecomputing.demo.gl2.shader.imaging;

import java.nio.FloatBuffer;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.image.format.CCPNGImage;
import cc.creativecomputing.io.CCNIOUtil;

public class CCVornoise2 extends CCGL2Adapter {

	@CCProperty(name = "voronoise")
	private CCGLProgram _myProgram;
	
	@CCProperty(name = "texture 0")
	private CCTexture2DAsset _myTexture;
	@CCProperty(name = "texture 1")
	private CCTexture2DAsset _myTexture2;
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _myScreenCapture;
	

	private CCShaderBuffer _myShaderBuffer;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "voronoise2.fs"));
		_myTexture = new CCTexture2DAsset(glContext());
		_myTexture2 = new CCTexture2DAsset(glContext());
		_myScreenCapture = new CCScreenCaptureController(this);
		
		_myShaderBuffer = new CCShaderBuffer(g.width(), g.height());
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
	@CCProperty(name = "blend a refraction", min = 0, max = 1)
	private double _cBlendARefraction = 0;
	@CCProperty(name = "blend b refraction", min = 0, max = 1)
	private double _cBlendBRefraction = 0;
	@CCProperty(name = "blend ab", min = 0, max = 1)
	private double _cBlendAB = 0;
	@CCProperty(name = "blend random", min = 0, max = 1)
	private double _cBlendRandom = 0;

	@CCProperty(name = "f x blend", min = 0, max = 1)
	private double _cFxBlend = 0;
	@CCProperty(name = "f y blend", min = 0, max = 1)
	private double _cFyBlend = 0;
	@CCProperty(name = "f z blend", min = 0, max = 1)
	private double _cFzBlend = 0;
	@CCProperty(name = "f w blend", min = 0, max = 1)
	private double _cFwBlend = 0;
	

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myShaderBuffer.beginDraw(g);
		g.clear();
		if(_myTexture.value() != null)g.texture(0,_myTexture.value());
		if(_myTexture2.value() != null)g.texture(1,_myTexture2.value());
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
		_myProgram.uniform1f("blendARefraction", _cBlendARefraction);
		_myProgram.uniform1f("blendBRefraction", _cBlendBRefraction);
		_myProgram.uniform1f("blendAB", _cBlendAB);
		_myProgram.uniform1f("time", animator().time());
		_myProgram.uniform1f("blendRandom", _cBlendRandom);
		_myProgram.uniform4f("fBlends", _cFxBlend, _cFyBlend, _cFzBlend, _cFwBlend);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex( g.width(),  g.height());
		g.vertex( 0,  		  g.height());
		g.vertex( 0,  		  0);
		g.vertex( g.width(),  0);
		g.endShape();
		_myProgram.end();
		g.noTexture();
		_myShaderBuffer.endDraw(g);
		
		/*
		CCPNGImage myImage = new CCPNGImage(g.width(), g.height(), 16, false, false);
		FloatBuffer myBuf = _myShaderBuffer.getData();

		for(int y = 0; y < g.height(); y++){
			for(int x = 0; x < g.width(); x++){
				myImage.pixel(x, y, myBuf.get(), myBuf.get(), myBuf.get());
			}
		}
		
		myImage.write(CCNIOUtil.appPath("export/" + CCFormatUtil.nf(animator().frames(), 4)+".png"));
		*/
		g.image(_myShaderBuffer.attachment(0), -g.width()/2, -g.height()/2);
	}

	public static void main(String[] args) {

		CCVornoise2 demo = new CCVornoise2();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(4320, 337);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
