package cc.creativecomputing.demo.topic.noise;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.CCFontSettings;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLShaderUtil;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

public class CCGoldDemo extends CCGL2Adapter {

	@CCProperty(name = "gold")
	private CCGLProgram _myProgram;
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _cScreenCapture;
	
	private CCTexture2D _myTexture;
	
	@CCProperty(name = "flow speed", min = 0, max = 1)
	private double _cFlowSpeed = 0;
	
	private double _myFlowTime = 0;
	
	@CCProperty(name = "text")
	private CCText _myText;
	
	private static int WIDTH = 3500;
	private static int HEIGHT = 768;
	
	private CCRenderBuffer _myRenderBuffer;
	private CCRenderBuffer _myTypoBuffer;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myProgram = new CCGLProgram(null, CCNIOUtil.classPath(this, "gold_flow.glsl"));
		_cScreenCapture = new CCScreenCaptureController(this, theAnimator);
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/presets/tex05.jpg")));
		_myTexture.generateMipmaps();
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myTexture.textureFilter(CCTextureFilter.LINEAR);
		_myTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		_myTypoBuffer = new CCRenderBuffer(WIDTH, HEIGHT);
		_myTypoBuffer.attachment(0).wrap(CCTextureWrap.CLAMP);
		
		_myRenderBuffer = new CCRenderBuffer(WIDTH, HEIGHT);
		
		String myFont = "Times";
		float mySize = 32;
		
		CCFontSettings mySettings = new CCFontSettings(myFont, mySize, true, CCCharSet.REDUCED_CHARSET);
		mySettings.doSDF(true);
		mySettings.sdfSpread(8);
		
		_myText = new CCText(CCFontIO.createTextureMapFont(mySettings));
		_myText.size(32);
		_myText.width(300);
//		_myText.lineBreak(CCLineBreakMode.BLOCK);
		_myText.text("GOLD");
		_myText.position(0,0);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myFlowTime += theAnimator.deltaTime() * _cFlowSpeed;
	}
	
	
	@CCProperty(name = "typo x", min = 0, max = 2000)
	private double _cTypoX = 0;
	@CCProperty(name = "typo y", min = 0, max = 2000)
	private double _cTypoY = 0;
	@CCProperty(name = "typo scale", min = 0, max = 10)
	private double _cTypoScale = 0;
	@CCProperty(name = "typo alpha", min = 0, max = 1)
	private double _cTypoAlpha = 0;
	

	@CCProperty(name = "output scale", min = 0, max = 1)
	private double _cOutputscale = 0;

	@Override
	public void display(CCGraphics g) {
		_myTypoBuffer.beginDraw(g);
		g.ortho();
		g.clear();
		g.pushMatrix();
		g.translate(_cTypoX,_cTypoY);
		g.scale(_cTypoScale);
		g.color(255);
		_myText.draw(g);
		g.popMatrix();
		_myTypoBuffer.endDraw(g);
		
		_myRenderBuffer.beginDraw(g);
		g.ortho();
		g.clear();
		
		g.texture(0, CCGLShaderUtil.randomTexture());
		g.texture(1, _myTexture);
		g.texture(2, _myTypoBuffer.attachment(0));
		_myProgram.start();
		_myProgram.uniform2f("iResolution", g.width(), g.height());
		_myProgram.uniform1f("iTime", animator().time());
		_myProgram.uniform1f("flowTime", _myFlowTime);
		_myProgram.uniform1i("randomTexture", 0);
		_myProgram.uniform1i("iChannel0", 1);
		_myProgram.uniform1i("typo", 2);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex( g.width(),  g.height());
		g.vertex(0,  g.height());
		g.vertex(0, 0);
		g.vertex( g.width(), 0);
		g.endShape();
		_myProgram.end();
		g.noTexture();
		_myRenderBuffer.endDraw(g);

		g.color(1d);
		g.image(_myRenderBuffer.attachment(0), 0, 0,_myRenderBuffer.width() * _cOutputscale, _myRenderBuffer.height() * _cOutputscale);
		
		
	}

	public static void main(String[] args) {

		CCGoldDemo demo = new CCGoldDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(3500 / 2, 768 / 2);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
