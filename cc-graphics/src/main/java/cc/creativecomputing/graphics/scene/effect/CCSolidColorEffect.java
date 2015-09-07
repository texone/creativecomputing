package cc.creativecomputing.graphics.scene.effect;

import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLUniform;
import cc.creativecomputing.gl4.GLUniformParameters;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;

public class CCSolidColorEffect extends CCSimpleEffect{

	@GLUniform(name = "color")
	private CCColor _uColor = new CCColor(1.0f, 1.0f, 1.0f);

	
	private GLUniformParameters _myParameters;
	private GLShaderProgram _myShader;
	
	public CCSolidColorEffect(CCColor theColor){
		_uColor = theColor;
		
	    _myShader = GLShaderProgram.createShaderProgram(CCNIOUtil.classPath(this, "shader") + "/solidcolor");
		_myParameters = _myShader.createUniformParameters(this);

	    shader(_myShader);
	}
	
	@Override
	protected GLUniformParameters parameters(GLShaderProgram theShader) {
		return _myParameters;
	}
	
	public CCColor color(){
		return _uColor;
	}
}
