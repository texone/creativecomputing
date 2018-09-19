package cc.creativecomputing.graphics.shader;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_DELETE_STATUS;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.glCompileShader;
import static org.lwjgl.opengl.GL20.glCreateShader;
import static org.lwjgl.opengl.GL20.glDeleteShader;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderiv;
import static org.lwjgl.opengl.GL20.glShaderSource;

import java.nio.IntBuffer;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.code.CCShaderObject;
import cc.creativecomputing.control.code.CCShaderObject.CCShaderInsert;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCBufferUtil;
import cc.creativecomputing.io.CCNIOUtil;

public class CCGLShader extends CCShaderObject{
	
	private static String[] KEYWORDS = new String[]{
			
		//Preprocessor
		"#define", 
		"#undef", 
		"#if", 
		"#ifdef", 
		"#ifndef", 
		"#else", 
		"#elif", 
		"#endif", 
		"#error", 
		"#pragma", 
		"#extension", 
		"#version", 
		"#line",
		
		// basic types
		"void",
		"bool",
		"int",
		"uint",
		"float",
		"vec2",
		"vec3",
		"vec4",
		"bec2",
		"bec3",
		"bec4",
		"iec2",
		"iec3",
		"iec4",
		"uvec2",
		"uvec3",
		"uvec4",
		"mat2",
		"mat3",
		"mat4",
		"mat2x2",
		"mat2x3",
		"mat2x4",
		"mat3x2",
		"mat3x3",
		"mat3x4",
		"mat4x2",
		"mat4x3",
		"mat4x4",
		
		//samplertypes
		"sampler1D",
		"sampler2D",
		"sampler3D",
		"samplerCube",
		"sampler2DRect",
		"sampler1DShadow",
		"sampler2DShadow",
		"sampler2DRectShadow",
		"sampler1DArray",
		"sampler2DArray",
		"sampler1DShadow",
		"sampler2DShadow",
		"samplerBuffer",
		"sampler2DMS",
		
		//Build in Variables
		"gl_FragCoord",
		"gl_FrontFacing",
		"gl_ClipDistance[]",
		"gl_FragColor", 
		"gl_FragData[]", 
		"gl_FragDepth",
		"gl_PointCoord",
		"gl_PrimitiveID",
		
			
		//Compatibility Profile Vertex Shader Built-In Inputs
		"gl_Color",
		"gl_SecondaryColor",
		"gl_Normal",
		"gl_Vertex",
		"gl_MultiTexCoord0",
		"gl_MultiTexCoord1",
		"gl_MultiTexCoord2",
		"gl_MultiTexCoord3",
		"gl_MultiTexCoord4",
		"gl_MultiTexCoord5",
		"gl_MultiTexCoord6",
		"gl_MultiTexCoord7",
		"gl_FogCoord;",
			
		//Built-In Constants
		"gl_MaxVertexAttribs",
		"gl_MaxVertexUniformComponents",
		"gl_MaxVaryingFloats" + 
		"gl_MaxVaryingComponents" + 
		"gl_MaxVertexOutputComponents",
		"gl_MaxGeometryInputComponents",
		"gl_MaxGeometryOutputComponents",
		"gl_MaxFragmentInputComponents",
		"gl_MaxVertexTextureImageUnits",
		"gl_MaxCombinedTextureImageUnits",
		"gl_MaxTextureImageUnits",
		"gl_MaxFragmentUniformComponents",
		"gl_MaxDrawBuffers",
		"gl_MaxClipDistances",
		"gl_MaxGeometryTextureImageUnits",
		"gl_MaxGeometryOutputVertices",
		"gl_MaxGeometryTotalOutputComponents",
		"gl_MaxGeometryUniformComponents",
		"gl_MaxGeometryVaryingComponents",
		"gl_MaxTextureUnits",
		"gl_MaxTextureCoords",
		"gl_MaxClipPlane",
		
		//Profile State
		"gl_ModelViewMatrix",
		"gl_ProjectionMatrix",
		"gl_ModelViewProjectionMatrix",
		"gl_TextureMatrix[]",
		"gl_NormalMatrix",
		"gl_ModelViewMatrixInverse",
		"gl_ProjectionMatrixInverse",
		"gl_ModelViewProjectionMatrixInverse",
		"gl_TextureMatrixInverse[]",
		"gl_ModelViewMatrixTranspose",
		"gl_ProjectionMatrixTranspose",
		"gl_ModelViewProjectionMatrixTranspose",
		"gl_TextureMatrixTranspose[]",
		"gl_ModelViewMatrixInverseTranspose",
		"gl_ProjectionMatrixInverseTranspose",
		"gl_ModelViewProjectionMatrixInverseTranspose",
		"gl_TextureMatrixInverseTranspose[]",
		"gl_NormalScale",
		"gl_ClipPlane",
		
		//per vertex
		"gl_FrontColor",
		"gl_BackColor",
		"gl_FrontSecondaryColor",
		"gl_BackSecondaryColor",
		"gl_TexCoord[]",
		"gl_FogFragCoord",
		"gl_Color",
		"gl_SecondaryColor",
		
		// texture functions
		"textureSize",
		"texture",
		"textureProj",
		"textureLod",
		"textureOffset",
		"texelFetch",
		"texelFetchOffset",
		"textureProjOffset",
		"textureLodOffset",
		"textureProjLod",
		"textureProjLodOffset",
		"textureGrad",
		"textureGradOffset",
		"textureProjGrad",
		"textureProjGradOffset",
		
		"texture1D",
		"texture1DProj",
		"texture1DLod",
		"texture1DProjLod",
		
		"texture2D",
		"texture2DProj",
		"texture2DLod",
		"texture2DProjLod",
		
		"texture3D",
		"texture3DProj",
		"texture3DLod",
		"texture3DProjLod",

		"textureCube",
		"textureCubeLod",

		"shadow1D",
		"shadow1DProj",
		"shadow1DLod",
		"shadow1DProjLod",

		"shadow2D",
		"shadow2DProj",
		"shadow2DLod",
		"shadow2DProjLod"
	};
	
	protected CCShaderObjectType _myType;
	
	private boolean _myReloadSource = false;
	
	
	public CCGLShader(CCShaderObjectType theType, Map<String, CCShaderInsert> theInsertMap, Path...theFiles){
		super(theInsertMap, theFiles);
		_myType = theType;
		_myShaderID = glCreateShader(_myType.glID);
		
		try{
			loadShader();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	CCGLShader(CCShaderObjectType theType, Path...theFiles){
		this(theType, new HashMap<>(), theFiles);
	}
	
	CCGLShader(CCShaderObjectType theType, Map<String, CCShaderInsert> theInsertMap, String...theSource){
		super(theInsertMap, theSource);
		_myType = theType;
		
		_myShaderID = glCreateShader(_myType.glID);

		loadShader(true);
	}
	

	CCGLShader(CCShaderObjectType theType, String...theSource){
		this(theType, new HashMap<>(), theSource);
	}
	
	@Override
	public void update() {
		_myReloadSource = true;
	}
	
	@Override
	public String[] keywords() {
		return KEYWORDS;
	}
	
	@Override
	public Path[] templates() {
		Path myTemplatePath = CCNIOUtil.classPath(CCGLShader.class, "templates");
		List<Path> myTemplates = CCNIOUtil.list(myTemplatePath, true, "glsl");
		Path[] myResult = new Path[myTemplates.size()];
		for(int i = 0; i < myTemplates.size();i++){
			myResult[i] = myTemplatePath.relativize(myTemplates.get(i));
		}
		return myResult;
	}
	
	@Override
	public String templateSource(Path thePath) {
		return CCNIOUtil.loadString(CCNIOUtil.classPath(this, "templates/" + thePath.toString()));
	}

	@Override
	public String errorLog() {
		return _myInfoLog;
	}
	
	/**
	 * returns the value of a parameter.
	 * <p>
	 * The following parameters are defined:
	 * <ul>
	 * <li><code>GL_SHADER_TYPE</code> returns <code>GL_VERTEX_SHADER</code> if shader is a vertex shader object, and <code>GL_FRAGMENT_SHADER</code> if shader is a fragment shader object.</li>
	 * <li><code>GL_DELETE_STATUS</code> returns <code>GL_TRUE</code> if shader is currently flagged for deletion, and <code>GL_FALSE</code> otherwise.</li>
	 * <li><code>GL_COMPILE_STATUS</code> returns <code>GL_TRUE</code> if the last compile operation on shader was successful, and <code>GL_FALSE</code> otherwise.</li>
	 * <li><code>GL_INFO_LOG_LENGTH</code> returns the number of characters in the information log for shader including the null termination character (i.e., the size of the character buffer required to store the information log). If shader has no information log, a value of 0 is returned.</li>
	 * <li><code>GL_SHADER_SOURCE_LENGTH</code> returns the length of the concatenation of the source strings that make up the shader source for the shader, including the null termination character. (i.e., the size of the character buffer required to store the shader source). If no source code exists, 0 is returned.</li>
	 * </ul>
	 * @param theParameter
	 * @return the value of a parameter.
	 */
	public int get(int theParameter){
		IntBuffer iVal = CCBufferUtil.newDirectIntBuffer(1);
		
		glGetShaderiv(_myShaderID, theParameter,iVal);
		return  iVal.get();
	}
	
	/**
	 * Returns the number of characters in the information log for shader including the 
	 * null termination character (i.e., the size of the character buffer required to 
	 * store the information log). If shader has no information log, a value of 0 is returned.
	 * @return the number of characters in the information log
	 */
	public int infoLogLength(){
		return get(GL_INFO_LOG_LENGTH);
	}
	
	/**
	 * Returns <code>true</code> if the last compile operation on shader was successful, and <code>false</code> otherwise.
	 * @return
	 */
	public boolean compileStatus(){
		return get(GL_COMPILE_STATUS) == GL_TRUE;
	}
	
	/**
	 * Returns <code>true</code> if shader is currently flagged for deletion, and <code>false</code> otherwise.
	 * @return
	 */
	public boolean deleteStatus(){
		return get(GL_DELETE_STATUS) == GL_TRUE;
	}
	
	/**
	 * Returns the information log for the specified shader object. The information log 
	 * for a shader object is modified when the shader is compiled. The string that is 
	 * returned will be null terminated.
	 * @param gl
	 * @param theObject
	 * @param theFiles
	 * @return
	 */
	public String getInfoLog() {
		return glGetShaderInfoLog(_myShaderID);
	}
	
	/**
	 * Sets the source code in shader to the source code in the array of strings specified by string. 
	 * Any source code previously stored in the shader object is completely replaced.
	 * The source code strings are not scanned or parsed at this time; they are simply copied into 
	 * the specified shader object.
	 * @param theSource string containing the source code to be loaded into the shader
	 */
	public void source(String theSource){
		
		glShaderSource(_myShaderID,  theSource );
	}
	
	/**
	 * compiles the source code strings that have been stored in the shader object specified by shader.
	 * The compilation status will be stored as part of the shader object's state. This value will be 
	 * set to <code>true</code> if the shader was compiled without errors and is ready for use, and 
	 * <code>false</code> otherwise. It can be queried by calling {@linkplain #compileStatus()}.
	 * <p>
	 * Compilation of a shader can fail for a number of reasons as specified by the OpenGL ES 
	 * Shading Language Specification. Whether or not the compilation was successful, 
	 * information about the compilation can be obtained from the shader object's information log 
	 * by calling {@linkplain #getInfoLog()}.
	 */
	public void compile(){
		
		glCompileShader(_myShaderID);
	}
	
	/**
	 * Frees the memory and invalidates the name associated with the shader object specified by shader.
	 * If a shader object to be deleted is attached to a program, it will be flagged for deletion, but 
	 * it will not be deleted until it is no longer attached to any program object, for any rendering 
	 * context (i.e., it must be detached from wherever it was attached before it will be deleted). 
	 * A value of 0 for shader will be silently ignored.
	 * <p>
	 * To determine whether an object has been flagged for deletion, call {@linkplain #deleteStatus()}
	 */
	public void delete(){
		glDeleteShader(_myShaderID);
	}
	
	private String errorSource() {
		switch(_myType) {
		case FRAGMENT:
			return "void main(){gl_FragColor = vec4(1,0,1,1);}";
		case VERTEX:
			return "void main(){gl_Position = gl_ModelViewProjectionMatrix * gl_Vertex;}";
		}
		return "void main(){}";
	}
	
	private boolean loadShader(boolean theThrowException){
		String mySource = preprocessSources();
		source(mySource);
		compile();
		if(!compileStatus()){
			_myInfoLog = getInfoLog();
			CCLog.info(_myInfoLog);
			CCLog.info(mySource);
			errorEvents.event(this);
			if(theThrowException)throw new CCShaderException(_myInfoLog);
			source(errorSource());
			compile();
			return false;
		}else{
			_myInfoLog = getInfoLog();
			CCLog.info(_myInfoLog);
		}
		compileEvents.event(this);

		return true;
	}
	
	public boolean loadShader() {
		return loadShader(false);
	}
	
	@CCProperty(name = "info", readBack = true)
	private String _myInfoLog = "";
	
	int _myShaderID = -1;
	
	void applyUniforms(CCGLProgram theProgram){
		for(CCShaderUniform myUniform:uniforms()){
			switch(myUniform.properties().length){
			case 1:
				theProgram.uniform1f(
					myUniform.name(), 
					myUniform.properties()[0].value().doubleValue()
				);
				break;
			case 2:
				theProgram.uniform2f(
					myUniform.name(), 
					myUniform.properties()[0].value().doubleValue(), 
					myUniform.properties()[1].value().doubleValue()
				);
				break;
			case 3:
				theProgram.uniform3f(
					myUniform.name(), 
					myUniform.properties()[0].value().doubleValue(), 
					myUniform.properties()[1].value().doubleValue(), 
					myUniform.properties()[2].value().doubleValue()
				);
				break;
			case 4:
				theProgram.uniform4f(
					myUniform.name(), 
					myUniform.properties()[0].value().doubleValue(), 
					myUniform.properties()[1].value().doubleValue(), 
					myUniform.properties()[2].value().doubleValue(), 
					myUniform.properties()[3].value().doubleValue()
				);
				break;
			}
		}
	}
	
	boolean checkReloadSource(){
		if(!_myReloadSource)return false;
		_myReloadSource = false;
        return loadShader(false);
    }
	
	public void reload(){
		loadShader();
	}
	
	public static void main(String[] args) {
		
		Path myTemplatePath = CCNIOUtil.classPath(CCGLShader.class, "templates");
		for(Path myPath:CCNIOUtil.list(myTemplatePath, true, "glsl")){
			CCLog.info(myTemplatePath.relativize(myPath));
		}
	}
}