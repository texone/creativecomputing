package cc.creativecomputing.demo.topic.simulation.reactivediffusion;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCGLSwapBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCBezierSpline;
import cc.creativecomputing.math.spline.CCBlendSpline;
import cc.creativecomputing.math.spline.CCSpline;

public class CCReactiveDiffusionDemo extends CCGL2Adapter {
	
	@CCProperty(name = "spline 0")
	private CCSpline _cSpline0 = new CCBezierSpline();
	@CCProperty(name = "spline 1")
	private CCSpline _cSpline1 = new CCBezierSpline();
	
	@CCProperty(name = "density", min = 0, max = 1)
	private double _cDensity = 0;
	@CCProperty(name = "viscosity", min = 0, max = 1)
	private double _cViscosity = 0;
	
	@CCProperty(name = "density scale", min = 0, max = 0.1)
	private double _cDensityScale = 0.1;
	@CCProperty(name = "viscosity scale", min = 0, max = 0.1)
	private double _cViscosityScale = 0.1;
	@CCProperty(name = "v scale", min = 0, max = 4)
	private double _cVscale = 0.1;
	@CCProperty(name = "diffusion x", min = 0, max = 2)
	private double _cDiffusionX = 0.8;
	@CCProperty(name = "diffusion y", min = 0, max = 2)
	private double _cDiffusionY = 0.3;
	

	@CCProperty(name = "k", min = 0, max = 1)
	private double _cK = 0;
	@CCProperty(name = "f", min = 0, max = 1)
	private double _cF = 0;
	@CCProperty(name = "draw parameter space")
	private boolean _cDrawParameterSpace = false;
	
	@CCProperty(name = "drawMask")
	private boolean _cDrawMask = false;
	
	@CCProperty(name = "speed", min = 0, max = 50)
	private double _cSpeed = 0.1;
	@CCProperty(name = "mask strokeweight", min = 0, max = 500)
	private double _cMaskStrokeWEight = 4;
	@CCProperty(name = "mask val", min = 0, max = 1)
	private double _cMaskVal = 1;
	
	@CCProperty(name = "strokeweight", min = 0, max = 50)
	private double _cStrokeWEight = 4;
	
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
	
	private CCBlendSpline _myBlendSpline;
	
	private CCGLSwapBuffer _mySwapBuffer;
	
//	@CCProperty(name = "reaction diffusion shader")
	private CCGLProgram _myReactiveDiffusionProgram;
	
	private CCShaderBuffer _myInterpolationMap;
	private CCGLWriteDataShader _myWriteDataShader;
	
	@CCProperty(name = "lighting")
	private CCGLProgram _myLightingShader;
	
	@CCProperty(name = "recfraction", min = 0, max = 1000)
	private double _cRefraction = 500;
	
	private CCTexture2D _myTexture;
	private CCTexture2D _myTexture2;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_mySwapBuffer = new CCGLSwapBuffer(g, g.width(), g.height(), CCTextureTarget.TEXTURE_RECT);
		_mySwapBuffer.clear(g);
		
		_myReactiveDiffusionProgram = new CCGLProgram(null, CCNIOUtil.classPath(this,"reactive_diffusion.glsl"));
		
		_myBlendSpline = new CCBlendSpline(_cSpline0, _cSpline1);
		
		_myInterpolationMap = new CCShaderBuffer(100, 100, CCTextureTarget.TEXTURE_2D);
		_myInterpolationMap.clear(g);
		_myInterpolationMap.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		_myInterpolationMap.attachment(0).wrap(CCTextureWrap.MIRRORED_REPEAT);
		
		_myWriteDataShader = new CCGLWriteDataShader();
		
		_myLightingShader = new CCGLProgram(null, CCNIOUtil.classPath(this, "lighting.glsl"));
		
		_myTexture = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/Clouds.jpg")));
		_myTexture.wrap(CCTextureWrap.MIRRORED_REPEAT);

		_myTexture2 = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/gradient.png")));
		_myTexture2.wrap(CCTextureWrap.MIRRORED_REPEAT);
		
		reset();
	}
	
	@Override
	public void setupControls(CCControlApp theControlApp) {
		super.setupControls(theControlApp);
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	private boolean _myReset = false;
	
	@CCProperty(name = "reset")
	public void reset(){
		_myReset = true;
	}

	@Override
	public void display(CCGraphics g) {
		
		_myInterpolationMap.beginDraw(g);
		_myWriteDataShader.start();
		g.clear();
		g.beginShape(CCDrawMode.POINTS);
		for(double x = 0; x <= 100;x++){
			double myXBlend = x / 100;
			for(double y = 0; y <= 100;y++){
				double myYBlend = y / 100;
				CCVector3 myPos = _myBlendSpline.interpolate(myXBlend, myYBlend);
				g.textureCoords3D(myPos.x, myPos.y, myPos.z);
				g.vertex(x + 0.5, y + 0.5);
			}
		}
		g.endShape();
		_myWriteDataShader.end();
		_myInterpolationMap.endDraw(g);
		
		if(_myReset){
			_mySwapBuffer.clear(g);
		}
		
		CCVector3 myDV = _myBlendSpline.interpolate(_cDensity, _cViscosity);
		if(myDV == null)myDV = new CCVector3();
		myDV.y = 1 - myDV.y;
		myDV.multiplyLocal(0.1);
		
		for(int i = 0; i < 25;i++){
			g.texture(0, _mySwapBuffer.attachment(0));
			g.texture(1, _myInterpolationMap.attachment(0));

			_myReactiveDiffusionProgram.start();
			_myReactiveDiffusionProgram.uniform1i( "dataBuffer", 0 );
			_myReactiveDiffusionProgram.uniform1i( "interpolationMap", 1 );
			_myReactiveDiffusionProgram.uniform1f( "kBlend", _cK );
			_myReactiveDiffusionProgram.uniform1f( "fBlend", _cF );
			_myReactiveDiffusionProgram.uniform1f( "dt", 0.04 * _cSpeed);
			_myReactiveDiffusionProgram.uniform2f( "Diffusion", _cDiffusionX, _cDiffusionY);
			_myReactiveDiffusionProgram.uniform1f( "vScale", _cVscale);
			
			_myReactiveDiffusionProgram.uniform2f( "iResolution", g.width(), g.height());
			_myReactiveDiffusionProgram.uniform1i( "reset", _myReset ? 1 : 0);
			_myReactiveDiffusionProgram.uniform1f( "drawParameterSpace", _cDrawParameterSpace ? 1 : 0);
			
			_mySwapBuffer.draw(g);
			_myReactiveDiffusionProgram.end();
			
			g.noTexture();
			
			
			_mySwapBuffer.swap();
			
		}
		g.color(255);

		_mySwapBuffer.beginDrawCurrent(g);
		g.pushAttribute();
		if(_cDrawMask){
			g.color(0,0,_cMaskVal);
			if(mouse().isPressed)g.ellipse(mouse().position.x, g.height() - mouse().position.y, _cMaskStrokeWEight);
		}else{
			g.strokeWeight(_cStrokeWEight);
			g.color(0,255,0);
			if(mouse().isPressed)g.line(mouse().position.x, g.height() - mouse().position.y, mouse().lastPosition.x, g.height() - mouse().lastPosition.y);
		}
		g.popAttribute();
		_mySwapBuffer.endDrawCurrent(g);
		
		if(_cDrawLighted){

			g.clear();
			
			g.texture(0, _myTexture2);
			g.texture(1, _mySwapBuffer.attachment(0));
			_myLightingShader.start();
			_myLightingShader.uniform1i("colorTex", 0);
			_myLightingShader.uniform1i("brightTex", 1);
	
			_myLightingShader.uniform3f("lightDir", new CCVector3(_cLightX, _cLightY, _cLightZ).normalizeLocal());
			_myLightingShader.uniform1f("specularPow", _cSpecularPow);
			_myLightingShader.uniform1f("specularBrightPow", _cSpecularBrightPow);
	
			_myLightingShader.uniform1f("diffuseAmp", _cDiffuseAmp);
			_myLightingShader.uniform1f("specularAmp", _cSpecularAmp);
			_myLightingShader.uniform1f("specularBrightAmp", _cSpecularBrightAmp);
			
			_myLightingShader.uniform1f("refraction", _cRefraction);
	
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
			g.clear();
			g.pushMatrix();
			g.translate(-g.width() / 2, -g.height() / 2);
			g.image(_mySwapBuffer.attachment(0), 0,0);
			
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(double i = 0; i <= 100;i++){
				double myBlend = i / 100;
				CCVector3 myVertex = _cSpline0.interpolate(myBlend);
				if(myVertex == null)break;
				g.vertex(myVertex.x * g.width(), (1 - myVertex.y) * g.height());
			}
			g.endShape();
			
			g.beginShape(CCDrawMode.LINE_STRIP);
			for(double i = 0; i <= 100;i++){
				double myBlend = i / 100;
				CCVector3 myVertex = _cSpline1.interpolate(myBlend);
				if(myVertex == null)break;
				g.vertex(myVertex.x * g.width(), (1 - myVertex.y) * g.height());
			}
			g.endShape();
			g.popMatrix();
			
	
			_myReset = false;
			
			g.image(_myInterpolationMap.attachment(0), 0,0);
		}
		_myReset = false;
	}

	public static void main(String[] args) {

		CCReactiveDiffusionDemo demo = new CCReactiveDiffusionDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
