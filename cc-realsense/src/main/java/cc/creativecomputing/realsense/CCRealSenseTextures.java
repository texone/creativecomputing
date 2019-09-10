package cc.creativecomputing.realsense;

import java.nio.FloatBuffer;
import java.nio.file.Path;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.shader.imaging.CCGPUSeperateGaussianBlur;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureMipmapFilter;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class CCRealSenseTextures{
	@CCProperty(name = "real sense")
	private CCRealSense _myRealSense;
	
	private CCShaderBuffer _myDepthMap;
	private CCShaderBuffer _myData;
	
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
	@CCProperty(name = "mirror")
	private boolean _cMirror = false;
	
	public CCRealSenseTextures() {
		this(null,640,480);
	}
	
	public CCVBOMesh _myMesh;
	
	@CCProperty(name = "mesh")
	private CCGLProgram _myMeshShader;
	@CCProperty(name = "min")
	private CCVector3 _myMin = new CCVector3();

	@CCProperty(name = "max")
	private CCVector3 _myMax = new CCVector3();
	
	
	public CCRealSenseTextures(Path thePath, int theWidth, int theHeight) {
		if(thePath == null) {
			_myRealSense = new CCRealSense();
			_myRealSense.start(theWidth,theHeight,30);
		}else {
			_myRealSense = new CCRealSense(thePath, theWidth, theHeight);
		}
		
		_myDepthMap = new CCShaderBuffer(_myRealSense.width, _myRealSense.height, CCTextureTarget.TEXTURE_2D);
		_myData = new CCShaderBuffer(32,4,4,_myRealSense.width, _myRealSense.height, CCTextureTarget.TEXTURE_2D);
		
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "realsense_vertex.glsl"), 
			CCNIOUtil.classPath(this, "realsense_fragment.glsl")
		);
		
		_myThresholdShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "threshold_vertex.glsl"), 
			CCNIOUtil.classPath(this, "threshold_fragment.glsl")
		);

		_myMeshShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "realsense_mesh_vertex.glsl"), 
			CCNIOUtil.classPath(this, "realsense_mesh_fragment.glsl")
		);
		
		_myBlur = new CCGPUSeperateGaussianBlur(120, _myRealSense.width, _myRealSense.height, 1);
		
		_myMesh = new CCVBOMesh(CCDrawMode.POINTS,  _myRealSense.width * _myRealSense.height);
		for(double x = 0.5; x <  _myRealSense.width;x++) {
			for(double y = 0.5; y <  _myRealSense.height;y++) {
				_myMesh.addVertex(x / _myRealSense.width, y / _myRealSense.height);
			}
		}
		
		
	}
	
	public double amountInBounds = 0;
	
	public void preDisplay(CCGraphics g) {
		if(_myTexture1 == null) {
			_myTexture1 = new CCTexture2D(_myRealSense.depthImage());
			_myTexture1.textureFilter(CCTextureFilter.LINEAR);
			_myTexture1.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
			_myTexture1.generateMipmaps(true);
		}
		if(_myTexture0 == null) {
			_myTexture0 = new CCTexture2D(_myRealSense.lastDepthImage());
			_myTexture0.textureFilter(CCTextureFilter.LINEAR);
			_myTexture0.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
			_myTexture0.generateMipmaps(true);
		}
		
		if(_myTexture0 == null)return;
		
		_myTexture1.updateData(_myRealSense.depthImage());
		_myTexture1.textureFilter(CCTextureFilter.LINEAR);
		_myTexture1.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		_myTexture1.generateMipmaps(true);
		_myTexture0.updateData(_myRealSense.lastDepthImage());
		_myTexture0.textureFilter(CCTextureFilter.LINEAR);
		_myTexture0.textureMipmapFilter(CCTextureMipmapFilter.LINEAR);
		_myTexture0.generateMipmaps(true);
		
		_myDepthMap.beginDraw(g);
		g.clear();
		g.texture(0,_myTexture0);
		g.texture(1,_myTexture1);
		_myThresholdShader.start();
		_myThresholdShader.uniform1i("depthTex0", 0);
		_myThresholdShader.uniform1i("depthTex1", 1);

		_myThresholdShader.uniform1f("depthToMeters", _myRealSense.depthScale);
		_myThresholdShader.uniform2f("depthOffset", _myRealSense.depthOffset);
		_myThresholdShader.uniform2f("depthFocalLength", _myRealSense.depthFocalLength);
		_myThresholdShader.uniform2f("depthTextureSize", _myRealSense.width, _myRealSense.height);
		
		_myThresholdShader.uniform3f("boundMin", _myMin);
		_myThresholdShader.uniform3f("boundMax", _myMax);
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

	
		_myData.beginDraw(g);
		g.clear();
		g.texture(0,_myTexture0);
		g.texture(1,_myTexture1);
		g.texture(2,_myDepthMap.attachment(0));
		_myShader.start();
		_myShader.uniform1i("depthTex0", 0);
		_myShader.uniform1i("depthTex1", 1);
		_myShader.uniform1i("thresh", 2);

		_myShader.uniform1i("mode", 0);
		
		_myShader.uniform1f("depthToMeters", _myRealSense.depthScale);
		_myShader.uniform2f("depthOffset", _myRealSense.depthOffset);
		_myShader.uniform2f("depthFocalLength", _myRealSense.depthFocalLength);
		_myShader.uniform2f("depthTextureSize", _myRealSense.width, _myRealSense.height);

		_myShader.uniform3f("boundMin", _myMin);
		_myShader.uniform3f("boundMax", _myMax);
		
		_myShader.uniform1i("mirror", _cMirror ? 1 : 0);
		
		
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
		_myData.endDraw(g);

		long myMillis = System.currentTimeMillis();
		FloatBuffer myData = _myData.getPBOData(3);
		double countZero = 0;
		double countData = 0;
		while(myData.hasRemaining()) {
			CCVector4 myPoint = new CCVector4(myData.get(),myData.get(),myData.get(),myData.get());
			if(myPoint.x == 0)countZero++;
			else amountInBounds++;
			//CCLog.info(myPoint);
		}
		amountInBounds /= _myRealSense.width * _myRealSense.height;
		//CCLog.info( amountInBounds * 100, System.currentTimeMillis() - myMillis);
		//CCLog.info(_myData.attachment(3).format(),_myData.attachment(3).pixelType());
		//CCLog.info(_myData.attachment(3).dataBuffer(1).asFloatBuffer().capacity() / (320 * 240));
		_myBlur.radius(_cBlurRadius);
		_myBlur.beginDraw(g);
		//g.ortho();
		g.clear();
		g.image(_myData.attachment(0), -_myRealSense.width / 2,-_myRealSense.height / 2);
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
	
	public void drawPointCloud(CCGraphics g) {
		g.pushMatrix();
		g.scale(3);
		g.rotate(180);
		g.pushAttribute();
		g.color(255);
		
		g.texture(0, _myData.attachment(3));
		_myMeshShader.start();
		_myMeshShader.uniform1i("positions",0);
		_myMesh.draw(g);
		_myMeshShader.end();
		g.noTexture();
		

		g.color(255,0,0);
		g.beginShape(CCDrawMode.LINES);
		g.vertex(_myMin.x, _myMin.y, _myMin.z);g.vertex( _myMax.x, _myMin.y, _myMin.z);
		g.vertex(_myMin.x, _myMax.y, _myMin.z);g.vertex( _myMax.x, _myMax.y, _myMin.z);
		g.vertex(_myMin.x, _myMin.y, _myMax.z);g.vertex( _myMax.x, _myMin.y, _myMax.z);
		g.vertex(_myMin.x, _myMax.y, _myMax.z);g.vertex( _myMax.x, _myMax.y, _myMax.z);
		
		g.vertex(_myMin.x, _myMin.y, _myMin.z);g.vertex( _myMin.x, _myMax.y, _myMin.z);
		g.vertex(_myMax.x, _myMin.y, _myMin.z);g.vertex( _myMax.x, _myMax.y, _myMin.z);
		g.vertex(_myMin.x, _myMin.y, _myMax.z);g.vertex( _myMin.x, _myMax.y, _myMax.z);
		g.vertex(_myMax.x, _myMin.y, _myMax.z);g.vertex( _myMax.x, _myMax.y, _myMax.z);

		g.vertex(_myMin.x, _myMin.y, _myMin.z);g.vertex( _myMin.x, _myMin.y, _myMax.z);
		g.vertex(_myMax.x, _myMin.y, _myMin.z);g.vertex( _myMax.x, _myMin.y, _myMax.z);
		g.vertex(_myMin.x, _myMax.y, _myMin.z);g.vertex( _myMin.x, _myMax.y, _myMax.z);
		g.vertex(_myMax.x, _myMax.y, _myMin.z);g.vertex( _myMax.x, _myMax.y, _myMax.z);
		g.endShape();
		g.popAttribute();
		g.popMatrix();
	}
}