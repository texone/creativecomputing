package cc.creativecomputing.demo.gl2.fractal;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCLineBreakMode;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCInterpolatableData;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCProceduralBlendDemo extends CCGL2Adapter {
	
	@CCProperty(name = "shader")
	private CCGLProgram _cProgram;
	
	
	private CCTexture2D _cTypoTexture;
	
	private CCTexture2D _myShopForeGround;
	private CCTexture2D _myShopWindows;
	
	private CCTexture3D _myTexture3D;
	
	@CCProperty(name = "use gradients")
	private boolean _cUseGradients = true;
	
	@CCProperty(name = "gradient0")
	private CCGradient _cGradient0 = new CCGradient();
	@CCProperty(name = "gradient1")
	private CCGradient _cGradient1 = new CCGradient();
	@CCProperty(name = "gradient2")
	private CCGradient _cGradient2 = new CCGradient();
	
	private CCInterpolatableData _myGradients;
	
	@CCProperty(name = "texture a")
	private CCTexture2DAsset _cTextureA;
	@CCProperty(name = "texture b")
	private CCTexture2DAsset _cTextureB;
	
	@CCProperty(name = "draw shop")
	private boolean _cDrawShop = true;
	
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _cScreenCapture;
	
	private CCShaderBuffer _cShaderBuffer;
	
	private int scaler = 2;
	
	private List<CCText> tweets = new ArrayList<>();
	
	private CCText myTweet;
	private int tweetCounter = 0;
	
	private CCRenderBuffer _myTwitterTexture;
	
	private CCText createText(final CCFont<?> theFont, String theText) {
		CCLog.info(theText);
		CCText myText = new CCText(theFont);
		myText.position(-2000,500);
		myText.dimension(2048, 1400);
		myText.text(theText);
		return myText;
	}
	
	private String breakText(String theText) {
		String[] Yo = theText.split(" ");
		StringBuilder result = new StringBuilder();
		int i = 0;
		for(String bla:Yo) {
			result.append(bla + " ");
			i++;
			if(i%5 == 0)result.append("\n");
		}
		return result.toString();
	}

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		String myFont = "Courier New Bold";
		float mySize = 120;
		CCFont bla = CCFontIO.createTextureMapFont(myFont, mySize, true, CCCharSet.EXTENDED_CHARSET);
		_cScreenCapture = new CCScreenCaptureController(this);
		
		_cProgram = new CCGLProgram(
			CCNIOUtil.classPath(this, "procedural_blend_vertex.glsl"), 
			CCNIOUtil.classPath(this, "procedural_blend_fragment.glsl")
		);
		
		_cTypoTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("found/01_IHK_GW_2019_WBM3.png")));
		_cTypoTexture.generateMipmaps(true);
		_cTypoTexture.textureFilter(CCTextureFilter.LINEAR);
		_cTypoTexture.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		
		_myShopForeGround = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("found/shop_front.png")));
		_myShopWindows = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("found/shop_windows.png")));

		_cTextureA = new CCTexture2DAsset(glContext());
		_cTextureB = new CCTexture2DAsset(glContext());
		
		_myGradients = new CCInterpolatableData(200,3,CCTextureTarget.TEXTURE_2D);
		_myGradients.add(_cGradient0);
		_myGradients.add(_cGradient1);
		_myGradients.add(_cGradient2);
		

		Path _myFolder = CCNIOUtil.dataPath("found/schwerin4/");
		
		List<Path> myFiles = CCNIOUtil.list(_myFolder, "png");
		_myTexture3D = new CCTexture3D(CCImageIO.newImage(myFiles.get(0)), myFiles.size());
		_myTexture3D.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myTexture3D.textureFilter(CCTextureFilter.LINEAR);
		
		int i = 0;
		for(Path myPath:myFiles) {
			_myTexture3D.updateData(CCImageIO.newImage(myPath), i++);
		}
		//String bla;
		
		_cShaderBuffer = new CCShaderBuffer(1024 * 3 * scaler, 768 * scaler);
		_myTwitterTexture = new CCRenderBuffer(1024 * 3 * scaler, 768 * scaler);
		
		for(CCDataElement itemXML:CCXMLIO.createXMLElement(CCNIOUtil.dataPath("dataset_my-task_2021-08-14_15-37-34-458.xml"))) {
			tweets.add(CCMath.random(tweets.size()),createText(bla,breakText( itemXML.child("full_text").content())));
		}
		for(CCDataElement itemXML:CCXMLIO.createXMLElement(CCNIOUtil.dataPath("dataset_my-task-1_2021-08-14_15-42-07-749.xml"))) {
			tweets.add(CCMath.random(tweets.size()),createText(bla,breakText( itemXML.child("full_text").content())));
		}
		for(CCDataElement itemXML:CCXMLIO.createXMLElement(CCNIOUtil.dataPath("dataset_my-task-2_2021-08-14_15-45-11-911.xml"))) {
			tweets.add(CCMath.random(tweets.size()),createText(bla,breakText( itemXML.child("full_text").content())));
		}
		
		myTweet = tweets.get(tweetCounter);
	}
	
	@CCProperty(name = "nextTweet")
	public void nextTweet() {
		CCLog.info("NEXT TWEET");
		tweetCounter++;
		tweetCounter %= tweets.size();
		myTweet = tweets.get(tweetCounter);
	}
	

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		
		_myTwitterTexture.beginDraw(g);
		g.clearColor(255,0,0,0);
		g.clear();
		myTweet.draw(g);
		_myTwitterTexture.endDraw(g);
		
		_myGradients.preDisplay(g);
		g.clearColor(0,0,0);
		g.clear();
		g.ortho2D();
		g.color(1d);
		if(_cTextureA.value() == null)return;
		if(_cTextureB.value() == null)return;
		
		_cShaderBuffer.beginDraw(g);
		if(_cUseGradients)g.texture(0, _myGradients.texture());
		else g.texture(0, _cTextureA.value());
		g.texture(1, _myTwitterTexture.attachment(0));
		g.texture(2, _myTexture3D);
		_cProgram.start();
		_cProgram.uniform1f("aspect", g.width()/(double)g.height());
		_cProgram.uniform1f("time", animator().time());
		_cProgram.uniform1i("textureA", 0);
		_cProgram.uniform1i("textureB", 1);
		_cProgram.uniform1i("tex3D", 2);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0,0);
		g.vertex(0,0);
		g.textureCoords2D(1,0);
		g.vertex(g.width() * scaler,0);
		g.textureCoords2D(1,1);
		g.vertex(g.width() * scaler,g.height() * scaler);
		g.textureCoords2D(0,1);
		g.vertex(0,g.height() * scaler);
		g.endShape();
		_cProgram.end();
		g.noTexture();
		_cShaderBuffer.endDraw(g);
		
		_cShaderBuffer.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		g.image(_cShaderBuffer.attachment(0), 0,0,1024*3, 768);
		g.image(_myTwitterTexture.attachment(0), 0,0,1024*3, 768);
//		g.image(_myShopForeGround, 0,0,g.width(),g.height());
////		
//		g.color(1d, 0.25d);
//		g.image(_myShopWindows, 0,0,g.width(),g.height());
	}

	public static void main(String[] args) {

		CCProceduralBlendDemo demo = new CCProceduralBlendDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1024*3, 768);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
