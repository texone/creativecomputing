package cc.creativecomputing.demo.simulation.particles.realsense;

import java.nio.file.Path;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.imaging.CCGPUSeperateGaussianBlur;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.realsense.CCRealSense;

public class CCRealSenseForceField{
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
	
	public CCRealSenseForceField() {
		this(null,0,0);
	}
	public CCRealSenseForceField(Path thePath, int theWidth, int theHeight) {
		if(thePath == null) {
			_myRealSense = new CCRealSense();
			_myRealSense.start();
		}else {
			_myRealSense = new CCRealSense(thePath, theWidth, theHeight);
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
	
	public void preDisplay(CCGraphics g) {
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
		g.image(_myForceField.attachment(0), -_myRealSense.width / 2,-_myRealSense.height / 2);
		_myBlur.endDraw(g);
	}
	
	public CCTexture2D texture0() {
		return _myTexture0;
	}

	
	public CCTexture2D texture1() {
		return _myTexture1;
	}
	
	public void update(CCAnimator theAnimator) {
		_myRealSense.update(theAnimator);
	}
	
	public CCTexture2D depthMap() {
		return _myDepthMap.attachment(0);
	}
	
	public CCTexture2D forceField() {
		return _myBlur.blurredTexture();
	}
}