package cc.creativecomputing.demo.graphics.noise;

import java.io.File;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.control.CCColorControl;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureWrap;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.random.CCRandom;

public class STNoiseRaymarch extends CCGL2Adapter{

	
	private CCTexture2D _myRandomTexture;
	
	private CCTexture2D _myInputMask;
	
	private CCRandom _myRandom = new CCRandom();
	
	private class CCNoiseControl{
		@CCProperty(name = "octaves", min = 1, max = 10)
		private int octaves = 4; 
		@CCProperty(name = "gain", min = 0, max = 1)
		private float gain = 0.5f; 
		@CCProperty(name = "lacunarity", min = 0, max = 10)
		private float lacunarity = 2f; 
		
		@CCProperty(name = "speed x", min = -10, max = 10)
		private float speedX = 0f; 
		@CCProperty(name = "speed y", min = -10, max = 10)
		private float speedY = 1.0f; 
		@CCProperty(name = "speed z", min = -10, max = 10)
		private float speedZ = 0f; 
		@CCProperty(name = "speed gain", min = 0, max = 2)
		private float speedGain = 0.5f; 
	}
	
	@CCProperty(name = "noise")
	private CCNoiseControl _cNoiseControl = new CCNoiseControl();

	@CCProperty(name = "map density start", min = -5, max = 5)
	private float _cMapDensityStart = 0.2f; 
	@CCProperty(name = "map density noise amp", min = 0, max = 10)
	private float _cMapDensityNoiseAmp = 4f; 
	@CCProperty(name = "map density sinus color mod", min = 0, max = 1)
	private float _cMapDensitySinusMod = 4f; 
	@CCProperty(name = "map density scale", min = 0, max = 1)
	private float _cMapDensityScale = 0.6f; 
	
	private class CCRGBControl{
		@CCProperty(name = "r", min = 0, max = 1)
		private float _cR = 0;
		@CCProperty(name = "g", min = 0, max = 1)
		private float _cG = 0;
		@CCProperty(name = "b", min = 0, max = 1)
		private float _cB = 0;
		@CCProperty(name = "amp", min = 0, max = 5)
		private float _cAmp = 0;
	}
	
	@CCProperty(name = "density0")
	private CCRGBControl _cDensity0 = new CCRGBControl();
	@CCProperty(name = "density1")
	private CCRGBControl _cDensity1 = new CCRGBControl();
	
	@CCProperty(name = "depth0")
	private CCRGBControl _cDepth0 = new CCRGBControl();
	@CCProperty(name = "depth1")
	private CCRGBControl _cDepth1 = new CCRGBControl();
	
	@CCProperty(name = "z depth blend", min = -0.5f, max = 1.5f)
	private float _cZDepthBlend = 0.05f; 
	@CCProperty(name = "z depth range", min = 0, max = 10)
	private float _cZDepthRange = 0.05f; 
	
	@CCProperty(name = "march step size", min = 0, max = 1)
	private float _cMarchStepSize = 0.05f; 
	@CCProperty(name = "march steps", min = 0, max = 200)
	private int _cMarchSteps = 100; 
	
	@CCProperty(name = "mask blend", min = 0, max = 1)
	private float _cMaskBlend = 0.5f; 
	

	
	@CCProperty(name = "brightness", min = 0, max = 10)
	private float _cBrightness = 0.5f;
	@CCProperty(name = "saturation", min = 0, max = 10)
	private float _cSaturation = 0.5f;
	@CCProperty(name = "contrast", min = 0, max = 10)
	private float _cContrast = 0.5f;
	
	@CCProperty(name = "origin x", min = -10, max = 10)
	private float _cOriginX = 4f; 
	@CCProperty(name = "origin y", min = -10, max = 10)
	private float _cOriginY = 6.0f; 
	@CCProperty(name = "origin z", min = -10, max = 10)
	private float _cOriginZ = 0f; 
	

	@CCProperty(name = "shader")
	private CCGLProgram _myShader;
	
	@CCProperty(name = "screen capture")
	private CCScreenCaptureController _myScreenCaptureController;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myShader = new CCGLProgram(
			null,
			CCNIOUtil.classPath(this, "noise_raymarch.glsl")
		);
		
		CCColor[][] myBaseColorMap = new CCColor[256][256];
		
		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				myBaseColorMap[x][y] = new CCColor(_myRandom.random(),0,0,0);
			}
		}

		for (int y=0;y<256;y++){
			for (int x=0;x<256;x++){
				int x2 = (x + 37) % 256;
				int y2 = (y + 17) % 256;
				myBaseColorMap[x2][y2].g = myBaseColorMap[x][y].r;
			}
		}
		
		CCImage myData = new CCImage(256,256);
		for(int x = 0; x < myData.width(); x++){
			for(int y = 0; y < myData.height(); y++){
				myData.setPixel(x, y, myBaseColorMap[x][y]);
			}
		}
		
		_myRandomTexture = new CCTexture2D(myData);
		_myRandomTexture.textureFilter(CCTextureFilter.LINEAR);
		_myRandomTexture.wrap(CCTextureWrap.REPEAT);
		
		_myInputMask = new CCTexture2D(CCImageIO.newImage(CCNIOUtil.classPath(this, "satelite.png")));
		
		_myScreenCaptureController = new CCScreenCaptureController(this);
	}
	
	private float _myTime = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myTime += theAnimator.deltaTime();
//		File myFile = new File(CCIOUtil.classPath(this, "noise_raymarch.glsl"));
//		if(myFile.lastModified() > myLastFileTime){
//			myLastFileTime = myFile.lastModified();
//			_myShader.reload();
//		}
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.texture(0,_myRandomTexture);
		g.texture(1,_myInputMask);

		_myShader.start();
		_myShader.uniform1f("time", _myTime);
		_myShader.uniform2f("resolution", g.width(), g.height());
		_myShader.uniform1i("randomTexture",0);
		_myShader.uniform2f("randomTextureResolution",_myRandomTexture.width(), _myRandomTexture.height());

		_myShader.uniform1i("inputMask", 1);
		
		_myShader.uniform1i("octaves", _cNoiseControl.octaves);
		_myShader.uniform1f("gain", _cNoiseControl.gain);
		_myShader.uniform1f("lacunarity", _cNoiseControl.lacunarity);
		
		_myShader.uniform3f("noiseMovement", _cNoiseControl.speedX, _cNoiseControl.speedY, _cNoiseControl.speedZ);
		_myShader.uniform1f("speedGain", _cNoiseControl.speedGain);

		_myShader.uniform1f("densityStart", _cMapDensityStart);
		_myShader.uniform1f("densityNoiseAmp", _cMapDensityNoiseAmp);
		_myShader.uniform1f("densitySinusColorMod", _cMapDensitySinusMod);
		_myShader.uniform1f("densityScale", _cMapDensityScale);
		
		
		_myShader.uniform4f("densityColor0", new CCColor(_cDensity0._cR, _cDensity0._cG, _cDensity0._cB)); //vec3(1.0,0.9,0.8), 
		_myShader.uniform1f("densityColor0Amp", _cDensity0._cAmp);
		_myShader.uniform4f("densityColor1", new CCColor(_cDensity1._cR, _cDensity1._cG, _cDensity1._cB)); //vec3(0.4,0.15,0.1),
		_myShader.uniform1f("densityColor1Amp", _cDensity1._cAmp);
		
		_myShader.uniform4f("depthColor0", new CCColor(_cDepth0._cR, _cDepth0._cG, _cDepth0._cB)); //
		_myShader.uniform1f("depthColor0Amp", _cDepth0._cAmp);
		_myShader.uniform4f("depthColor1", new CCColor(_cDepth1._cR, _cDepth1._cG, _cDepth1._cB)); //
		_myShader.uniform1f("depthColor1Amp", _cDepth1._cAmp);
		
		_myShader.uniform1f("zBlendStart", _cZDepthBlend);
		_myShader.uniform1f("zBlendRange", _cZDepthRange);
		
		_myShader.uniform1f("marchStepSize", _cMarchStepSize);
		_myShader.uniform1i("marchSteps", _cMarchSteps);

		_myShader.uniform1f("maskBlend", _cMaskBlend);
		

		_myShader.uniform1f("brightness", _cBrightness);
		_myShader.uniform1f("saturation", _cSaturation);
		_myShader.uniform1f("contrast", _cContrast);

		_myShader.uniform3f("cameraPosition", _cOriginX, _cOriginY, _cOriginZ);
//		_myShader.uniformMatrix4f("cameraTransformation", _myCameraTransformation);
//		_myShader.uniform3f("cameraPosition", _myCameraPosition);
		
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0.0f, 0.0f);
		g.vertex(-g.width() / 2, -g.height() / 2);
		g.textureCoords2D(1.0f, 0.0f);
		g.vertex( g.width() / 2, -g.height() / 2);
		g.textureCoords2D(1.0f, 1.0f);
		g.vertex( g.width() / 2,  g.height() / 2);
		g.textureCoords2D(0.0f, 1.0f);
		g.vertex(-g.width() / 2,  g.height() / 2);
        g.endShape();
        
        g.noTexture();
        
        _myShader.end();
        
//        CCLog.info(frameRate);
	}
	
	
	
	public static void main(String[] args) {
		STNoiseRaymarch demo = new STNoiseRaymarch();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
