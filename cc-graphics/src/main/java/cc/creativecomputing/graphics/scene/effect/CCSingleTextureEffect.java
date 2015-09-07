package cc.creativecomputing.graphics.scene.effect;

import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLUniform;
import cc.creativecomputing.gl4.GLUniformParameters;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.texture.GLTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

public class CCSingleTextureEffect extends CCSimpleEffect{

	@GLUniform(name = "s")
	private GLTexture2D _uTexture;

	private GLUniformParameters _myParameters;
	private GLShaderProgram _myShader;
	
	public CCSingleTextureEffect(GLTexture2D theTexture){
		_uTexture = theTexture;
		
		_myShader = GLShaderProgram.createShaderProgram(CCNIOUtil.classPath(this, "shader") + "/singletexture");
		_myParameters = _myShader.createUniformParameters(this);
	    shader(_myShader);
	}
	
	@Override
	protected GLUniformParameters parameters(GLShaderProgram theShader) {
		return _myParameters;
	}
	
	public GLTexture2D texture(){
		return _uTexture;
	}
}
