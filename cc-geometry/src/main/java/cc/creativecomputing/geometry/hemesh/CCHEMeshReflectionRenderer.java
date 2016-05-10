package cc.creativecomputing.geometry.hemesh;

import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCPlane;
import cc.creativecomputing.math.CCVector3;

public class CCHEMeshReflectionRenderer {

	private CCVector3 _myLightPosition = new CCVector3();
	private CCPlane _myPlane;
	private CCGLProgram _myShader;
	
	public CCHEMeshReflectionRenderer(CCPlane thePlane){
		_myShader = new CCGLProgram(
			CCNIOUtil.classPath(this, "shader/hemesh_light_reflection_vp.glsl"), 
			CCNIOUtil.classPath(this, "shader/hemesh_light_reflection_fp.glsl")
		);
		
		_myPlane = thePlane;
	}
	
	public CCVector3 lightPosition(){
		return _myLightPosition;
	}
	
	public void start(){
		_myShader.start();
		_myShader.uniform3f("lightPos", _myLightPosition.x, _myLightPosition.y, _myLightPosition.z);
		_myShader.uniform3f("planeNormal", _myPlane.normal().x, _myPlane.normal().y, _myPlane.normal().z);
		_myShader.uniform1f("planeConstant", _myPlane.constant());
	}
	
	public void end(){
		_myShader.end();
	}
}
