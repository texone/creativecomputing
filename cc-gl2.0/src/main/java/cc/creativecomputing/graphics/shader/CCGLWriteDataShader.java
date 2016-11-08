package cc.creativecomputing.graphics.shader;

public class CCGLWriteDataShader extends CCGLProgram{

	public CCGLWriteDataShader() {
		super();
		
		attachShader(
			"void main(){\n" + 
			"	gl_FragData[0] = gl_TexCoord[0];\n" + 
			"	gl_FragData[1] = gl_TexCoord[1];\n" + 
			"	gl_FragData[2] = gl_TexCoord[2];\n" + 
			"	gl_FragData[3] = gl_TexCoord[3];\n" + 
			"}", 
			CCShaderObjectType.FRAGMENT
		);
		
		link();
	}
}
