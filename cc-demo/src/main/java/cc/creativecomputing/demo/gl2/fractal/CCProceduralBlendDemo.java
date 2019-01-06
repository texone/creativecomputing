package cc.creativecomputing.demo.gl2.fractal;

import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCapture;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCInterpolatableData;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture2DAsset;
import cc.creativecomputing.graphics.texture.CCTexture3D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;

public class CCProceduralBlendDemo extends CCGL2Adapter {
	
	@CCProperty(name = "shader")
	private CCGLProgram _cProgram;
	
	private CCTexture2D _cGradientTexture02;
	private CCTexture2D _cGradientTexture03;
	private CCTexture2D _cGradientTexture04;
	
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

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cScreenCapture = new CCScreenCaptureController(this);
		
		_cProgram = new CCGLProgram(
			CCNIOUtil.classPath(this, "procedural_blend_vertex.glsl"), 
			CCNIOUtil.classPath(this, "procedural_blend_fragment.glsl")
		);
		
		_cGradientTexture02 = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("gradient02.jpg")));
		_cGradientTexture03 = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("gradient03.jpg")));
		_cGradientTexture04 = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("gradient04.jpg")));
		
		_myShopForeGround = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("found/shop_front.png")));
		_myShopWindows = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("found/shop_windows.png")));

		_cTextureA = new CCTexture2DAsset(glContext());
		_cTextureB = new CCTexture2DAsset(glContext());
		
		_myGradients = new CCInterpolatableData(200,3,CCTextureTarget.TEXTURE_2D);
		_myGradients.add(_cGradient0);
		_myGradients.add(_cGradient1);
		_myGradients.add(_cGradient2);
		

		Path _myFolder = CCNIOUtil.dataPath("found/inShop/");
		
		List<Path> myFiles = CCNIOUtil.list(_myFolder, "png");
		_myTexture3D = new CCTexture3D(CCImageIO.newImage(myFiles.get(0)), myFiles.size());
		_myTexture3D.wrap(CCTextureWrap.MIRRORED_REPEAT);
		_myTexture3D.textureFilter(CCTextureFilter.LINEAR);
		
		int i = 0;
		for(Path myPath:myFiles) {
			_myTexture3D.updateData(CCImageIO.newImage(myPath), i++);
		}
	}
	
	

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		_myGradients.preDisplay(g);
		g.clear();
		g.ortho2D();
		g.color(1d);
		if(_cTextureA.value() == null)return;
		if(_cTextureB.value() == null)return;
		
		if(_cUseGradients)g.texture(0, _myGradients.texture());
		else g.texture(0, _cTextureA.value());
		g.texture(1, _cTextureB.value());
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
		g.vertex(g.width(),0);
		g.textureCoords2D(1,1);
		g.vertex(g.width(),g.height());
		g.textureCoords2D(0,1);
		g.vertex(0,g.height());
		g.endShape();
		_cProgram.end();
		g.noTexture();
		
//		g.image(_myShopForeGround, 0,0,g.width(),g.height());
////		
//		g.color(1d, 0.25d);
//		g.image(_myShopWindows, 0,0,g.width(),g.height());
	}

	public static void main(String[] args) {

		CCProceduralBlendDemo demo = new CCProceduralBlendDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1600, 1200);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
