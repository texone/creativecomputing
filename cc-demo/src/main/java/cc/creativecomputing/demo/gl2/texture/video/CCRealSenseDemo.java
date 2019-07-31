package cc.creativecomputing.demo.gl2.texture.video;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.imaging.CCGPUSeperateGaussianBlur;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.realsense.CCRealSense;
import cc.creativecomputing.realsense.CCRealSensePlayer;

public class CCRealSenseDemo extends CCGL2Adapter {
	
	@CCProperty(name = "real sense")
	private CCRealSense _myRealSense;
	
	private CCShaderBuffer _myDepthMap;
	private CCShaderBuffer _myForceField;
	
	private CCTexture2D _myTexture0;
	private CCTexture2D _myTexture1;
	
	@CCProperty(name = "shader")
	private CCGLProgram _myShader;
	
	@CCProperty(name = "threshold")
	private CCGLProgram _myThresholdShader;
	
	private CCGPUSeperateGaussianBlur _myBlur;	
	
	public final static float MAXIMUM_BLUR_RADIUS = 150;
	
	@CCProperty(name = "blur radius", min = 0, max = MAXIMUM_BLUR_RADIUS)
	private float _cBlurRadius = MAXIMUM_BLUR_RADIUS;
	
	private static boolean USE_PLAYER = false;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		if(USE_PLAYER) {
			_myRealSense = new CCRealSense(CCNIOUtil.appPath("exports/realsense02.byt"), 1280,720);
		}else {
			_myRealSense = new CCRealSense();
			_myRealSense.start();
		}
		
		
		_myDepthMap = new CCShaderBuffer(_myRealSense.width, _myRealSense.height, CCTextureTarget.TEXTURE_2D);
		_myForceField = new CCShaderBuffer(_myRealSense.width, _myRealSense.height, CCTextureTarget.TEXTURE_2D);
		
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "realsense_vertex.glsl"), 
			CCNIOUtil.classPath(this, "realsense_fragment.glsl")
		);
		
		_myThresholdShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "threshold_vertex.glsl"), 
			CCNIOUtil.classPath(this, "threshold_fragment.glsl")
		);
		
		_myBlur = new CCGPUSeperateGaussianBlur(120, _myRealSense.width, _myRealSense.height, 1);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myRealSense.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		
		if(_myTexture1 == null)_myTexture1 = new CCTexture2D(_myRealSense.depthImage());
		if(_myTexture0 == null)_myTexture0 = new CCTexture2D(_myRealSense.lastDepthImage());
		
		
		if(_myTexture0 == null)return;

			
		_myTexture1.updateData(_myRealSense.depthImage());
		_myTexture0.updateData(_myRealSense.lastDepthImage());
		
		
		_myDepthMap.beginDraw(g);
		g.clear();
		g.texture(0,_myTexture0);
		g.texture(1,_myTexture1);
		_myThresholdShader.start();
		_myThresholdShader.uniform1i("depthTex0", 0);
		_myThresholdShader.uniform1i("depthTex1", 1);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0);
		g.vertex(0,0);
		g.textureCoords2D(1, 0);
		g.vertex(_myRealSense.width,0);
		g.textureCoords2D(1, 1);
		g.vertex(_myRealSense.width,_myRealSense.height);
		g.textureCoords2D(0, 1);
		g.vertex(0,_myRealSense.height);
		g.endShape();
		_myThresholdShader.end();
		g.noTexture();
		_myDepthMap.endDraw(g);
		_myDepthMap.attachment(0).textureFilter(CCTextureFilter.LINEAR);
		_myDepthMap.attachment(0).textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		_myDepthMap.attachment(0).generateMipmaps(true);

		
		g.ortho();
		
		_myForceField.beginDraw(g);
		g.clear();
		g.texture(0,_myTexture0);
		g.texture(1,_myTexture1);
		g.texture(2,_myDepthMap.attachment(0));
		_myShader.start();
		_myShader.uniform1i("depthTex0", 0);
		_myShader.uniform1i("depthTex1", 1);
		_myShader.uniform1i("thresh", 2);
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0);
		g.vertex(0,0);
		g.textureCoords2D(1, 0);
		g.vertex(_myRealSense.width,0);
		g.textureCoords2D(1, 1);
		g.vertex(_myRealSense.width,_myRealSense.height);
		g.textureCoords2D(0, 1);
		g.vertex(0,_myRealSense.height);
		g.endShape();
		_myShader.end();
		
		g.noTexture();
		_myForceField.endDraw(g);

		_myBlur.radius(_cBlurRadius);
		_myBlur.beginDraw(g);
		//g.ortho();
		g.clear();
		g.image(_myForceField.attachment(0), -_myRealSense.width/2,-_myRealSense.height/2);
		_myBlur.endDraw(g);
		g.image(_myBlur.blurredTexture(), 0,0);
		g.image(_myDepthMap.attachment(0), _myRealSense.width,0);
		
//		g.image(_myTexture0, 0,0);
//		g.image(_myTexture1, 640,0);
	}

	public static void main(String[] args) {

		CCRealSenseDemo demo = new CCRealSenseDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280 * 2, 720);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
