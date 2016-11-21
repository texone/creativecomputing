package cc.creativecomputing.demo.simulation.fluid;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.events.CCMouseSimpleInfo;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCGLWriteDataShader;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.fluid.CCFluidGrid;
import cc.creativecomputing.simulation.fluid.CCFluidSolver;

public class CCFluidDemo extends CCGL2Adapter {
	
	
	
	private CCFluidGrid _myGrid = new CCFluidGrid();
	
	@CCProperty(name = "solver")
	private CCFluidSolver _mySolver;
	
	@CCProperty(name = "update")
	private boolean _cUpdate = true;
	
	@CCProperty(name = "speed x", min = -1, max = 1)
	private double _cSpeedX = 0;
	@CCProperty(name = "speed y", min = -1, max = 1)
	private double _cSpeedY = 0;
	@CCProperty(name = "speed z", min = -1, max = 1)
	private double _cSpeedZ = 0;
	

	@CCProperty(name = "mouse impulse", min = 0, max = 1)
	private double _cMouseImpulse = 0;
	@CCProperty(name = "mouse temperature", min = 0, max = 1)
	private double _cMouseTemperature = 0;
	
	private CCMouseSimpleInfo _myMouse = new CCMouseSimpleInfo();
	
	private CCGLWriteDataShader _myWriteDataShader;

	
	@CCProperty(name = "draw lighted")
	private boolean _cDrawLighted = true;
	@CCProperty(name = "lighting")
	private CCGLProgram _myLightingShader;
	
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

	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _myScreenCapture;
	@CCProperty(name = "gradient")
	private CCGradient _myGradient = new CCGradient();
	@CCProperty(name = "color speed", min = 0, max = 1)
	private double _cColorSpeed = 0.1;
	
	private CCText _myText;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myGrid.size.set(g.width() / 2, g.height() / 2);
		_mySolver = new CCFluidSolver(_myGrid, new CCVector2(g.width(), g.height()));
		
		
		mouseListener().add(_myMouse);
		mouseMotionListener().add(_myMouse);
		
		_myText = new CCText(CCFontIO.createTextureMapFont("Helvetica-Bold", 40));
		_myText.text(
				"We can modify CHO cell lines\n to provide viral resistance to \nMVM. Learn more at booth\n 608 at #BPSMT");
		_myText.position(20, 200);
		
		_myScreenCapture = new CCScreenCaptureController(this);
		
		_myWriteDataShader = new CCGLWriteDataShader();
		

		_myLightingShader = new CCGLProgram(null, CCNIOUtil.classPath(this,"lighting.glsl"));
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_mySolver.noiseOffset().x = theAnimator.time() * _cSpeedX;
		_mySolver.noiseOffset().y = theAnimator.time() * _cSpeedY;
		_mySolver.noiseOffset().z = theAnimator.time() * _cSpeedZ;
	}
	
	
	
	public void addForces(CCGraphics g) {
		_mySolver.addColor(g, _myMouse.position, _myGradient.color((animator().time() * _cColorSpeed) % 1).invert());
		_mySolver.addForce(g, _myMouse.position, new CCVector2( _myMouse.motion.x, - _myMouse.motion.y).multiplyLocal(_cMouseImpulse));
		_mySolver.addTemperature(g, _myMouse.position, new CCVector2( _myMouse.motion.x, - _myMouse.motion.y).length() * (_cMouseTemperature));
	}
	
	@CCProperty(name = "linespeed", min = 0, max = 1)
	private double _cLineSpeed = 0.1;
	
	@CCProperty(name = "speed impulse scale")
	private double _cSpeedImpulseScale = 1;

	@Override
	public void display(CCGraphics g) {
		
		if(_cUpdate){
			addForces(g);
//			_mySolver.density.beginDraw();
//			g.color(CCColor.createFromHSB(animator().time() * 0.1, 1d, 1d));
//			g.rect(animator().time() * 100 % _myGrid.size.x,100, 10, 10);
//			_mySolver.density.endDraw();
//			_mySolver.velocity.beginDraw();
//			g.color(255,0,0);
//			g.rect(animator().time() * 100 % _myGrid.size.x,100, 10, 10);
//			_mySolver.velocity.endDraw();
			_mySolver.clearBounds(g);
			_mySolver.bounds().beginDraw();

			_myWriteDataShader.start();
			double _myY = (CCMath.sin(animator().time() * _cLineSpeed) + 1) / 2 * _myGrid.size.y;
			double _myY2 = (CCMath.sin((animator().time() + animator().deltaTime()) * _cLineSpeed ) + 1) / 2 * _myGrid.size.y;
			
			double _myDir = (_myY2 - _myY) * _cSpeedImpulseScale;
			g.textureCoords3D(0, 1, 0, _myDir);
			g.rect(0.5, _myY, _myGrid.size.x, 20);
			
			g.ellipse(_myGrid.size.x / 2,_myY, 200);
			_mySolver.bounds().endDraw();
			_myWriteDataShader.end();
			
			_mySolver.step(g);
		}else{
			_mySolver._myDensityData.beginDrawCurrent();
//			g.color(255,0,0);
//			g.rect(100,100, 100, 100);
//			g.color(0,255,0);
//			g.rect(200,100, 100, 100);
//			g.color(0,0,255);
//			g.rect(300,100, 100, 100);
			g.clear();
			g.color(1d);
			_myText.draw(g);
			_mySolver._myDensityData.endDrawCurrent();
		}

		 g.clear();
		 
		 
		 if(_cDrawLighted){
			 g.texture(0, _mySolver._myDensityData.attachment(0));
			 g.texture(1, _mySolver._myDensityData.attachment(0));
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
			 g.vertex(-g.width()/2, -g.height()/2);
			 g.vertex(g.width()/2, -g.height()/2);
			 g.vertex(g.width()/2, g.height()/2);
			 g.vertex(-g.width()/2, g.height()/2);
			 g.endShape();
			 _myLightingShader.end();
			 g.noTexture();
		 }else{
			 _mySolver.display(g);
		 }
	}

	public static void main(String[] args) {

		CCFluidDemo demo = new CCFluidDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
