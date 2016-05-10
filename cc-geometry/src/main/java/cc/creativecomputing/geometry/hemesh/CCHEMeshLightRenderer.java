package cc.creativecomputing.geometry.hemesh;

import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector3;

public class CCHEMeshLightRenderer {

	private CCVector3 _myLightPosition = new CCVector3();
	private CCGLProgram _myShader;
	
	public CCHEMeshLightRenderer(){
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "shader/hemesh_baselight_vp.glsl"), 
			CCNIOUtil.classPath(this, "shader/hemesh_baselight_fp.glsl")
		);
	}
	
	public CCVector3 lightPosition(){
		return _myLightPosition;
	}
	
	public void start(){
		_myShader.start();
		_myShader.uniform3f("lightPos", _myLightPosition.x, _myLightPosition.y, _myLightPosition.z);
	}
	
	public void end(){
		_myShader.end();
	}
}
