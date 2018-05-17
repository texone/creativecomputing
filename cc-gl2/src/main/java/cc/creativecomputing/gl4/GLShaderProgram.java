package cc.creativecomputing.gl4;

import static org.lwjgl.opengl.GL11.GL_DOUBLE;
import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_INT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_INT;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_ATTRIBUTES;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_ATTRIBUTE_MAX_LENGTH;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORMS;
import static org.lwjgl.opengl.GL20.GL_ACTIVE_UNIFORM_MAX_LENGTH;
import static org.lwjgl.opengl.GL20.GL_ATTACHED_SHADERS;
import static org.lwjgl.opengl.GL20.GL_BOOL;
import static org.lwjgl.opengl.GL20.GL_BOOL_VEC2;
import static org.lwjgl.opengl.GL20.GL_BOOL_VEC3;
import static org.lwjgl.opengl.GL20.GL_BOOL_VEC4;
import static org.lwjgl.opengl.GL20.GL_DELETE_STATUS;
import static org.lwjgl.opengl.GL20.GL_FLOAT_MAT2;
import static org.lwjgl.opengl.GL20.GL_FLOAT_MAT3;
import static org.lwjgl.opengl.GL20.GL_FLOAT_MAT4;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC2;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC3;
import static org.lwjgl.opengl.GL20.GL_FLOAT_VEC4;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_INT_VEC2;
import static org.lwjgl.opengl.GL20.GL_INT_VEC3;
import static org.lwjgl.opengl.GL20.GL_INT_VEC4;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.GL_SAMPLER_1D;
import static org.lwjgl.opengl.GL20.GL_SAMPLER_1D_SHADOW;
import static org.lwjgl.opengl.GL20.GL_SAMPLER_2D;
import static org.lwjgl.opengl.GL20.GL_SAMPLER_2D_SHADOW;
import static org.lwjgl.opengl.GL20.GL_SAMPLER_3D;
import static org.lwjgl.opengl.GL20.GL_SAMPLER_CUBE;
import static org.lwjgl.opengl.GL20.GL_VALIDATE_STATUS;
import static org.lwjgl.opengl.GL20.glAttachShader;
import static org.lwjgl.opengl.GL20.glCreateProgram;
import static org.lwjgl.opengl.GL20.glDeleteProgram;
import static org.lwjgl.opengl.GL20.glDetachShader;
import static org.lwjgl.opengl.GL20.glGetActiveAttrib;
import static org.lwjgl.opengl.GL20.glGetActiveUniform;
import static org.lwjgl.opengl.GL20.glGetAttribLocation;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgramiv;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glLinkProgram;
import static org.lwjgl.opengl.GL20.glUseProgram;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT2x3;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT2x4;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT3x2;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT3x4;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT4x2;
import static org.lwjgl.opengl.GL21.GL_FLOAT_MAT4x3;
import static org.lwjgl.opengl.GL30.GL_INTERLEAVED_ATTRIBS;
import static org.lwjgl.opengl.GL30.GL_INT_SAMPLER_1D;
import static org.lwjgl.opengl.GL30.GL_INT_SAMPLER_1D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_INT_SAMPLER_2D;
import static org.lwjgl.opengl.GL30.GL_INT_SAMPLER_2D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_INT_SAMPLER_3D;
import static org.lwjgl.opengl.GL30.GL_INT_SAMPLER_CUBE;
import static org.lwjgl.opengl.GL30.GL_SAMPLER_1D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_SAMPLER_1D_ARRAY_SHADOW;
import static org.lwjgl.opengl.GL30.GL_SAMPLER_2D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_SAMPLER_2D_ARRAY_SHADOW;
import static org.lwjgl.opengl.GL30.GL_SAMPLER_CUBE_SHADOW;
import static org.lwjgl.opengl.GL30.GL_SEPARATE_ATTRIBS;
import static org.lwjgl.opengl.GL30.GL_TRANSFORM_FEEDBACK_BUFFER_MODE;
import static org.lwjgl.opengl.GL30.GL_TRANSFORM_FEEDBACK_VARYINGS;
import static org.lwjgl.opengl.GL30.GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_SAMPLER_1D;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_SAMPLER_1D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_SAMPLER_2D;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_SAMPLER_2D_ARRAY;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_SAMPLER_3D;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_SAMPLER_CUBE;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_VEC2;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_VEC3;
import static org.lwjgl.opengl.GL30.GL_UNSIGNED_INT_VEC4;
import static org.lwjgl.opengl.GL31.GL_INT_SAMPLER_2D_RECT;
import static org.lwjgl.opengl.GL31.GL_INT_SAMPLER_BUFFER;
import static org.lwjgl.opengl.GL31.GL_INVALID_INDEX;
import static org.lwjgl.opengl.GL31.GL_SAMPLER_2D_RECT;
import static org.lwjgl.opengl.GL31.GL_SAMPLER_2D_RECT_SHADOW;
import static org.lwjgl.opengl.GL31.GL_SAMPLER_BUFFER;
import static org.lwjgl.opengl.GL31.GL_UNSIGNED_INT_SAMPLER_2D_RECT;
import static org.lwjgl.opengl.GL31.GL_UNSIGNED_INT_SAMPLER_BUFFER;
import static org.lwjgl.opengl.GL31.glGetUniformBlockIndex;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_INPUT_TYPE;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_OUTPUT_TYPE;
import static org.lwjgl.opengl.GL32.GL_GEOMETRY_VERTICES_OUT;
import static org.lwjgl.opengl.GL32.GL_INT_SAMPLER_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY;
import static org.lwjgl.opengl.GL32.GL_SAMPLER_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_SAMPLER_2D_MULTISAMPLE_ARRAY;
import static org.lwjgl.opengl.GL32.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL32.GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY;
import static org.lwjgl.opengl.GL40.GL_ACTIVE_SUBROUTINES;
import static org.lwjgl.opengl.GL40.GL_ACTIVE_SUBROUTINE_MAX_LENGTH;
import static org.lwjgl.opengl.GL40.GL_ACTIVE_SUBROUTINE_UNIFORMS;
import static org.lwjgl.opengl.GL40.GL_ACTIVE_SUBROUTINE_UNIFORM_LOCATIONS;
import static org.lwjgl.opengl.GL40.GL_ACTIVE_SUBROUTINE_UNIFORM_MAX_LENGTH;
import static org.lwjgl.opengl.GL40.GL_COMPATIBLE_SUBROUTINES;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT2;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT2x3;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT2x4;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT3;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT3x2;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT3x4;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT4;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT4x2;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_MAT4x3;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_VEC2;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_VEC3;
import static org.lwjgl.opengl.GL40.GL_DOUBLE_VEC4;
import static org.lwjgl.opengl.GL40.GL_NUM_COMPATIBLE_SUBROUTINES;
import static org.lwjgl.opengl.GL40.glGetActiveSubroutineName;
import static org.lwjgl.opengl.GL40.glGetActiveSubroutineUniformName;
import static org.lwjgl.opengl.GL40.glGetActiveSubroutineUniformiv;
import static org.lwjgl.opengl.GL40.glGetProgramStageiv;
import static org.lwjgl.opengl.GL40.glGetSubroutineIndex;
import static org.lwjgl.opengl.GL40.glGetSubroutineUniformLocation;
import static org.lwjgl.opengl.GL40.glUniformSubroutinesuiv;
import static org.lwjgl.opengl.GL41.GL_PROGRAM_BINARY_LENGTH;
import static org.lwjgl.opengl.GL41.glBindProgramPipeline;
import static org.lwjgl.opengl.GL41.glGenProgramPipelines;
import static org.lwjgl.opengl.GL41.glProgramUniform1f;
import static org.lwjgl.opengl.GL41.glProgramUniform1fv;
import static org.lwjgl.opengl.GL41.glProgramUniform1i;
import static org.lwjgl.opengl.GL41.glProgramUniform2f;
import static org.lwjgl.opengl.GL41.glProgramUniform3f;
import static org.lwjgl.opengl.GL41.glProgramUniform4f;
import static org.lwjgl.opengl.GL41.glProgramUniform4fv;
import static org.lwjgl.opengl.GL41.glProgramUniformMatrix3fv;
import static org.lwjgl.opengl.GL41.glProgramUniformMatrix4fv;
import static org.lwjgl.opengl.GL42.GL_ACTIVE_ATOMIC_COUNTER_BUFFERS;
import static org.lwjgl.opengl.GL42.GL_IMAGE_1D;
import static org.lwjgl.opengl.GL42.GL_IMAGE_1D_ARRAY;
import static org.lwjgl.opengl.GL42.GL_IMAGE_2D;
import static org.lwjgl.opengl.GL42.GL_IMAGE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.GL_IMAGE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL42.GL_IMAGE_2D_MULTISAMPLE_ARRAY;
import static org.lwjgl.opengl.GL42.GL_IMAGE_2D_RECT;
import static org.lwjgl.opengl.GL42.GL_IMAGE_3D;
import static org.lwjgl.opengl.GL42.GL_IMAGE_BUFFER;
import static org.lwjgl.opengl.GL42.GL_IMAGE_CUBE;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_1D;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_1D_ARRAY;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_2D;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_2D_MULTISAMPLE_ARRAY;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_2D_RECT;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_3D;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_BUFFER;
import static org.lwjgl.opengl.GL42.GL_INT_IMAGE_CUBE;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_ATOMIC_COUNTER;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_1D;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_1D_ARRAY;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_2D;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_2D_RECT;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_3D;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_BUFFER;
import static org.lwjgl.opengl.GL42.GL_UNSIGNED_INT_IMAGE_CUBE;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.system.MemoryStack;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMatrix3x3;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector1;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

public class GLShaderProgram{
	
	/**
	 * Use this function to simplify creation of basic shaders. This function is looking in the filenames 
	 * for the shadertype to load the file for the right shader object so as an example the following 
	 * filestructure.
	 * <pre>
	 *  ./shader_vertex.glsl
	 *  ./shader_geometry.glsl
	 *  ./shader_fragment.glsl
	 * </pre>
	 * would create a program with one {@linkplain GLShaderType#VERTEX}, one  {@linkplain GLShaderType#GEOMETRY}
	 * and one  {@linkplain GLShaderType#FRAGMENT} shader, the allowed name suffixes are vertex, geometry,
	 * fragment, tess_control and tess_evaluation
	 * @param thepass path to the shader to load without the shader type suffix
	 * @return shader program with the shader types provided
	 */
	public static GLShaderProgram createShaderProgram(String theName){
		GLShaderObject[] myObjects = new GLShaderObject[GLShaderType.values().length];
		
		for(GLShaderType myType:GLShaderType.values()){
			Path myShaderPath = Paths.get(theName + "_" + myType.name().toLowerCase() +".glsl");
			if(Files.exists(myShaderPath)){
				myObjects[myType.ordinal()] = new GLShaderObject(myType, myShaderPath);
			}else{
				myObjects[myType.ordinal()] = null;
			}
		}
		
		return new GLShaderProgram(myObjects);
	}
	
	public static class CCGLShaderProgramPipeline{
		
		private int _myID;
		
		public CCGLShaderProgramPipeline(){
			_myID = glGenProgramPipelines();
		}
		
		public void bind(){
			glBindProgramPipeline(_myID);
		}
		
		public void finalize(){
			glDeleteProgram(_myID);
		}
	}
	
	private Map<String, GLUniformBlock> _myUniformBlockMap = new HashMap<>();
	private int _myID;
	private Map<GLShaderType, GLShaderObject> _myShaderObjectMap = new HashMap<>();
	private List<GLUniformInfo> _myUniforms = new ArrayList<>();
	private Map<String, GLUniformInfo> _myUniformMap = new HashMap<>();
	private List<GLAttributeInfo> _myAttributes = new ArrayList<>();
	private Map<GLShaderType, GLSubroutineManager> _mySubroutineManager = new HashMap<>();

	/**
	 * Creates a shader program, attaches the given shader objects to it and links the shader.
	 * In case of a linkage error an exception is thrown
	 * @param theObjects
	 */
	public GLShaderProgram(GLShaderObject...theObjects){
		
		_myID = glCreateProgram();
		
		for(GLShaderObject myShaderObject:theObjects){
			if(myShaderObject == null)continue;
			attach(myShaderObject);
			_myShaderObjectMap.put(myShaderObject.type(), myShaderObject);
		}
		
		if(!link()){
			throw new GLException(infoLog());
		}
		
		for(GLShaderObject myShaderObject:theObjects){
			if(myShaderObject == null)continue;
			_mySubroutineManager.put(myShaderObject.type(), new GLSubroutineManager(myShaderObject.type()));
		}
		
		for(int i = 0; i < activeUniforms(); i++){
			GLUniformInfo myUniformInfo = activeUniform(i);
			_myUniforms.add(myUniformInfo);
			_myUniformMap.put(myUniformInfo.name(), myUniformInfo);
		}
		
		for(int i = 0; i < activeAttributes(); i++){
			_myAttributes.add(activeAttribute(i));
		}
	}
	
	public Map<GLShaderType, GLShaderObject> shaderObjectMap(){
		return _myShaderObjectMap;
	}
	
	/**
	 * In order to create a complete shader program, there must be a way to specify the list of things 
	 * that will be linked together. Program objects provide this mechanism. Shaders that are to be linked 
	 * together in a program object must first be attached to that shader program. <code>attach</code> 
	 * attaches the shader object specified to the shader program. This indicates that shader will be 
	 * included in link operations that will be performed on program.
	 * <p>
	 * All operations that can be performed on a shader object are valid whether or not the shader object 
	 * is attached to a shader program. It is permissible to attach a shader object to a shader program 
	 * before source code has been loaded into the shader object or before the shader object has been 
	 * compiled. It is permissible to attach multiple shader objects of the same type because each may 
	 * contain a portion of the complete shader. It is also permissible to attach a shader object to 
	 * more than one program object. If a shader object is deleted while it is attached to a program object,
	 *  it will be flagged for deletion, and deletion will not occur until detach is called to detach it 
	 *  from all shader programs to which it is attached.
	 * @param theShaderObject the object to attach
	 */
	public void attach(GLShaderObject theShaderObject){
		glAttachShader(_myID, theShaderObject.id());
	}
	
	/**
	 * Detaches the shader object specified by shader from the program object specified by program. 
	 * This command can be used to undo the effect of the command {@linkplain #attach(GLShaderObject)}.
	 * <p>
	 * If shader has already been flagged for deletion by a call to glDeleteShader and it is not 
	 * attached to any other shader program, it will be deleted after it has been detached.
	 * @param theShaderObject the object to be detached
	 */
	public void detach(GLShaderObject theShaderObject){
		glDetachShader(_myID, theShaderObject.id());
	}
	
	/**
	 * Links the shader program. If any shader objects of type <code>VERTEX</code> are attached to program, 
	 * they will be used to create an executable that will run on the programmable vertex processor. If any shader 
	 * objects of type <code>GEOMETRY</code> are attached to program, they will be used to create an executable 
	 * that will run on the programmable geometry processor. If any shader objects of type <code>FRAGMENT</code> 
	 * are attached to program, they will be used to create an executable that will run on the programmable fragment processor.
	 * <p>
	 * The status of the link operation will be returned. This value will be set to <code>true</code> if the program 
	 * object was linked without errors and is ready for use, and <code>false</code> otherwise. 
	 * <p>
	 * As a result of a successful link operation, all active user-defined uniform variables belonging to program will 
	 * be initialized to 0, and each of the program object's active uniform variables will be assigned a location 
	 * that can be queried by calling {@linkplain #uniformLocation(String)}. Also, any active user-defined attribute 
	 * variables that have not been bound to a generic vertex attribute index will be bound to one at this time.
	 * <p>
	 * Linking of a program object can fail for a number of reasons as specified in the OpenGL Shading Language Specification. 
	 * The following lists some of the conditions that will cause a link error.
	 * <ul>
	 * <li>The number of active attribute variables supported by the implementation has been exceeded.
	 * 
	 * <li>The storage limit for uniform variables has been exceeded.
	 * 
	 * <li>The number of active uniform variables supported by the implementation has been exceeded.
	 * 
	 * <li>The main function is missing for the vertex, geometry or fragment shader.
	 * 
	 * <li>A varying variable actually used in the fragment shader is not declared in the same way 
	 * (or is not declared at all) in the vertex shader, or geometry shader shader if present.
	 * 
	 * <li>A reference to a function or variable name is unresolved.
	 * 
	 * <li>A shared global is declared with two different types or two different initial values.
	 * 
	 * <li>One or more of the attached shader objects has not been successfully compiled.
	 * 
	 * <li>Binding a generic attribute matrix caused some rows of the matrix to fall outside the allowed maximum of GL_MAX_VERTEX_ATTRIBS.
	 * 
	 * <li>Not enough contiguous vertex attribute slots could be found to bind attribute matrices.
	 * 
	 * <li>The program object contains objects to form a fragment shader but does not contain objects to form a vertex shader.
	 * 
	 * <li>The program object contains objects to form a geometry shader but does not contain objects to form a vertex shader.
	 * 
	 * <li>The program object contains objects to form a geometry shader and the input primitive type, output primitive type, 
	 * or maximum output vertex count is not specified in any compiled geometry shader object.
	 * 
	 * <li>The program object contains objects to form a geometry shader and the input primitive type, output primitive type, 
	 * or maximum output vertex count is specified differently in multiple geometry shader objects.
	 * 
	 * <li>The number of active outputs in the fragment shader is greater than the value of GL_MAX_DRAW_BUFFERS.
	 * <li>The program has an active output assigned to a location greater than or equal to the value of
	 *  GL_MAX_DUAL_SOURCE_DRAW_BUFFERS and has an active output assigned an index greater than or equal to one.
	 *  
	 * <li>More than one varying out variable is bound to the same number and index.
	 * 
	 * <li>The explicit binding assigments do not leave enough space for the linker to automatically assign a location for a varying out array, which requires multiple contiguous locations.

	 * <li>The count specified by glTransformFeedbackVaryings is non-zero, but the program object has no vertex or geometry shader.

	 * <li>Any variable name specified to glTransformFeedbackVaryings in the varyings array is not declared as an 
	 * output in the vertex shader (or the geometry shader, if active).

	 * <li>Any two entries in the varyings array given glTransformFeedbackVaryings specify the same varying variable.

	 * <li>The total number of components to capture in any transform feedback varying variable is greater than the constant 
	 * GL_MAX_TRANSFORM_FEEDBACK_SEPARATE_COMPONENTS and the buffer mode is GL_SEPARATE_ATTRIBS.

	 * When a program object has been successfully linked, the program object can be made part of current state by calling 
	 * glUseProgram. Whether or not the link operation was successful, the program object's information log will be overwritten. 
	 * The information log can be retrieved by calling glGetProgramInfoLog.
	 * <p>
	 * link will also install the generated executables as part of the current rendering state if the link operation was successful 
	 * and the specified program object is already currently in use as a result of a previous call to start. If the program object 
	 * currently in use is relinked unsuccessfully, its link status will be set to GL_FALSE , but the executables and associated state 
	 * will remain part of the current state until a subsequent call to glUseProgram removes it from use. After it is removed from use, 
	 * it cannot be made part of current state until it has been successfully relinked.
	 * <p>
	 * If program contains shader objects of type {@linkplain GLShaderObject.GLShaderType#VERTEX}, and optionally of type {@linkplain GLShaderObjectGLShaderType#GEOMETRY}, 
	 * but does not contain shader objects of type {@linkplain GLShaderObjectGLShaderType#FRAGMENT}, the vertex shader executable will be installed
	 * on the programmable vertex processor, the geometry shader executable, if present, will be installed on the programmable geometry 
	 * processor, but no executable will be installed on the fragment processor. The results of rasterizing primitives with such a 
	 * program will be undefined.
	 * <p>
	 * The program object's information log is updated and the program is generated at the time of the link operation. After the link operation, 
	 * applications are free to modify attached shader objects, compile attached shader objects, detach shader objects, delete shader objects, 
	 * and attach additional shader objects. None of these operations affects the information log or the program that is part of the program object.
	 * @return
	 */
	public boolean link(){
		glLinkProgram(_myID);
		return linkStatus();
	}
	
	/**
	 * Returns the information log. The information log is modified when the shader program is linked or validated. 
	 * The string that is returned will be null terminated.
	 * <p>
	 * The information log for a program object is either an empty string, or a string containing information about 
	 * the last link operation, or a string containing information about the last validation operation. It may contain 
	 * diagnostic messages, warning messages, and other information. When a program object is created, its information 
	 * log will be a string of length 0.
	 * <p>
	 * The information log for a program object is the OpenGL implementer's primary mechanism for conveying information 
	 * about linking and validating. Therefore, the information log can be helpful to application developers during the 
	 * development process, even when these operations are successful. Application developers should not expect different 
	 * OpenGL implementations to produce identical information logs.
	 * @return Returns the information log
	 */
	public String infoLog(){
		return glGetProgramInfoLog(_myID);
	}
	
	/**
	 * Returns the opengl shader id of the program
	 * @return opengl shader id
	 */
	public int id(){
		return _myID;
	}
	
	public enum GLProgramParameter {
		/**
		 * returns GL_TRUE if program is currently flagged for deletion, and GL_FALSE otherwise.
		 */
		DELETE_STATUS(GL_DELETE_STATUS),
		/**
		 * returns GL_TRUE if the last link operation on program was successful, and GL_FALSE otherwise.
		 */
		LINK_STATUS(GL_LINK_STATUS),
		/**
		 * returns GL_TRUE or if the last validation operation on program was successful, and GL_FALSE otherwise.
		 */
		VALIDATE_STATUS(GL_VALIDATE_STATUS),
		/**
		 * returns the number of characters in the information log for program including the null 
		 * termination character (i.e., the size of the character buffer required to store the information log). 
		 * If program has no information log, a value of 0 is returned.
		 */
		INFO_LOG_LENGTH(GL_INFO_LOG_LENGTH),
		/**
		 * returns the number of shader objects attached to program.
		 */
		ATTACHED_SHADERS(GL_ATTACHED_SHADERS),
		/**
		 * returns the number of active attribute atomic counter buffers used by program.
		 */
		ACTIVE_ATOMIC_COUNTER_BUFFERS(GL_ACTIVE_ATOMIC_COUNTER_BUFFERS),
		/**
		 * returns the number of active attribute variables for program.
		 */
		ACTIVE_ATTRIBUTES(GL_ACTIVE_ATTRIBUTES),
		/**
		 * returns the length of the longest active attribute name for program, including the null termination 
		 * character (i.e., the size of the character buffer required to store the longest attribute name). 
		 * If no active attributes exist, 0 is returned.
		 */
		ACTIVE_ATTRIBUTE_MAX_LENGTH(GL_ACTIVE_ATTRIBUTE_MAX_LENGTH),
		/**
		 * returns the number of active uniform variables for program.
		 */
		ACTIVE_UNIFORMS(GL_ACTIVE_UNIFORMS),
		/**
		 * returns the length of the longest active uniform variable name for program, including the null termination 
		 * character (i.e., the size of the character buffer required to store the longest uniform variable name). 
		 * If no active uniform variables exist, 0 is returned.
		 */
		ACTIVE_UNIFORM_MAX_LENGTH(GL_ACTIVE_UNIFORM_MAX_LENGTH),
		/**
		 * returns the length of the program binary, in bytes that will be returned by a call to glGetProgramBinary. 
		 * When a progam's GL_LINK_STATUS is GL_FALSE, its program binary length is zero.
		 */
		PROGRAM_BINARY_LENGTH(GL_PROGRAM_BINARY_LENGTH),
		/*
		 * returns an array of three integers containing the local work group size of the compute program as specified 
		 * by its input layout qualifier(s). program must be the name of a program object that has been previously 
		 * linked successfully and contains a binary for the compute shader stage.
		 */
//		COMPUTE_WORK_GROUP_SIZE(GL_COMPUTE_WORK_GROUP_SIZE),
		/**
		 * returns a symbolic constant indicating the buffer mode used when transform feedback is active. 
		 * This may be GL_SEPARATE_ATTRIBS or GL_INTERLEAVED_ATTRIBS.
		 */
		TRANSFORM_FEEDBACK_BUFFER_MODE(GL_TRANSFORM_FEEDBACK_BUFFER_MODE),
		/**
		 * returns the number of varying variables to capture in transform feedback mode for the program.
		 */
		TRANSFORM_FEEDBACK_VARYINGS(GL_TRANSFORM_FEEDBACK_VARYINGS),
		/**
		 * returns the length of the longest variable name to be used for transform feedback, including the null-terminator.
		 */
		TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH(GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH),
		/**
		 * returns the maximum number of vertices that the geometry shader in program will output.
		 */
		GEOMETRY_VERTICES_OUT(GL_GEOMETRY_VERTICES_OUT),
		/**
		 * returns a symbolic constant indicating the primitive type accepted as input to the geometry shader contained in program.
		 */
		GEOMETRY_INPUT_TYPE(GL_GEOMETRY_INPUT_TYPE),
		/**
		 * returns a symbolic constant indicating the primitive type that will be output by the geometry shader contained in program.
		 */
		GEOMETRY_OUTPUT_TYPE(GL_GEOMETRY_OUTPUT_TYPE);
		
		private int _myGLID;
		
		GLProgramParameter(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLProgramParameter fromGLID(int theGLID){
			switch(theGLID){
			case GL_DELETE_STATUS:return DELETE_STATUS;
			case GL_LINK_STATUS:return LINK_STATUS;
			case GL_VALIDATE_STATUS:return VALIDATE_STATUS;
			case GL_INFO_LOG_LENGTH:return INFO_LOG_LENGTH;
			case GL_ATTACHED_SHADERS:return ATTACHED_SHADERS;
			case GL_ACTIVE_ATOMIC_COUNTER_BUFFERS:return ACTIVE_ATOMIC_COUNTER_BUFFERS;
			case GL_ACTIVE_ATTRIBUTES:return ACTIVE_ATTRIBUTES;
			case GL_ACTIVE_ATTRIBUTE_MAX_LENGTH:return ACTIVE_ATTRIBUTE_MAX_LENGTH;
			case GL_ACTIVE_UNIFORMS:return ACTIVE_UNIFORMS;
			case GL_ACTIVE_UNIFORM_MAX_LENGTH:return ACTIVE_UNIFORM_MAX_LENGTH;
			case GL_PROGRAM_BINARY_LENGTH:return PROGRAM_BINARY_LENGTH;
//			case GL_COMPUTE_WORK_GROUP_SIZE:return COMPUTE_WORK_GROUP_SIZE;
			case GL_TRANSFORM_FEEDBACK_BUFFER_MODE:return TRANSFORM_FEEDBACK_BUFFER_MODE;
			case GL_TRANSFORM_FEEDBACK_VARYINGS:return TRANSFORM_FEEDBACK_VARYINGS;
			case GL_TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH:return TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH;
			case GL_GEOMETRY_VERTICES_OUT:return GEOMETRY_VERTICES_OUT;
			case GL_GEOMETRY_INPUT_TYPE:return GEOMETRY_INPUT_TYPE;
			case GL_GEOMETRY_OUTPUT_TYPE:return GEOMETRY_OUTPUT_TYPE;
			}
			return null;
		}
	}
	
	private int glGetProgram(GLProgramParameter theParameter){
		try ( MemoryStack stack = MemoryStack.stackPush() ) { 
		    IntBuffer ip = stack.mallocInt(1); 
		    glGetProgramiv(_myID, theParameter.glID(), ip);
		    return ip.get();
		}
	}
	
	/**
	 * returns <code>true</code> if program is currently flagged for deletion, and <code>false</code> otherwise.
	 * @return
	 */
	public boolean deleteStatus(){
		return glGetProgram(GLProgramParameter.DELETE_STATUS) == GL_TRUE;
	}
	
	/**
	 * returns GL_TRUE if the last link operation on program was successful, and GL_FALSE otherwise.
	 * @return
	 */
	public boolean linkStatus(){
		return glGetProgram(GLProgramParameter.LINK_STATUS) == GL_TRUE;
	}
	
	/**
	 * returns GL_TRUE or if the last validation operation on program was successful, and GL_FALSE otherwise.
	 * @return
	 */
	public boolean validateStatus(){
		return glGetProgram(GLProgramParameter.VALIDATE_STATUS) == GL_TRUE;
	}
	
	/**
	 * returns the number of characters in the information log for program including the null 
	 * termination character (i.e., the size of the character buffer required to store the information log). 
	 * If program has no information log, a value of 0 is returned.
	 * @return
	 */
	public int infoLogLength(){
		return glGetProgram(GLProgramParameter.INFO_LOG_LENGTH);
	}
	
	/**
	 * returns the number of shader objects attached to program.
	 */
	public int attachedShaders(){
		return glGetProgram(GLProgramParameter.ATTACHED_SHADERS);
	}
	
	/**
	 * returns the number of active attribute atomic counter buffers used by program.
	 */
	public int activeAtomicCounterBuffers(){
		return glGetProgram(GLProgramParameter.ACTIVE_ATOMIC_COUNTER_BUFFERS);
	}
	
	/**
	 * returns the length of the program binary, in bytes that will be returned by a call to glGetProgramBinary. 
	 * When a progam's GL_LINK_STATUS is GL_FALSE, its program binary length is zero.
	 */
	public int programBinaryLength(){
		return glGetProgram(GLProgramParameter.PROGRAM_BINARY_LENGTH);
	}
	
	/*
	 * returns an array of three integers containing the local work group size of the compute program as specified 
	 * by its input layout qualifier(s). program must be the name of a program object that has been previously 
	 * linked successfully and contains a binary for the compute shader stage.
	 */
//	COMPUTE_WORK_GROUP_SIZE(GL_COMPUTE_WORK_GROUP_SIZE),
	
	public enum GLTransformFeedbackMode {
		INTERLEAVED_ATTRIBS(GL_INTERLEAVED_ATTRIBS),
		SEPARATE_ATTRIBS(GL_SEPARATE_ATTRIBS);
		
		private int _myGLID;
		
		GLTransformFeedbackMode(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLTransformFeedbackMode fromGLID(int theGLID){
			switch(theGLID){
			case GL_INTERLEAVED_ATTRIBS:return INTERLEAVED_ATTRIBS;
			case GL_SEPARATE_ATTRIBS:return SEPARATE_ATTRIBS;
			}
			return null;
		}
	}
	
	/**
	 * returns a symbolic constant indicating the buffer mode used when transform feedback is active. 
	 * This may be {@linkplain GLTransformFeedbackMode#SEPARATE_ATTRIBS} or {@linkplain GLTransformFeedbackMode#INTERLEAVED_ATTRIBS}.
	 */
	public GLTransformFeedbackMode transformFeedbackBufferMode(){
		return GLTransformFeedbackMode.fromGLID(glGetProgram(GLProgramParameter.TRANSFORM_FEEDBACK_BUFFER_MODE));
	}
	
	/**
	 * returns the number of varying variables to capture in transform feedback mode for the program.
	 */
	public int transformFeedbackVaryings(){
		return glGetProgram(GLProgramParameter.TRANSFORM_FEEDBACK_VARYINGS);
	}
	
	/**
	 * returns the length of the longest variable name to be used for transform feedback, including the null-terminator.
	 */
	public int transformFeedbackVaryingsMaxLength(){
		return glGetProgram(GLProgramParameter.TRANSFORM_FEEDBACK_VARYING_MAX_LENGTH);
	}
	
	/**
	 * returns the maximum number of vertices that the geometry shader in program will output.
	 */
	public int geometryVerticesOut(){
		return glGetProgram(GLProgramParameter.GEOMETRY_VERTICES_OUT);
	}
	
	/**
	 * returns a symbolic constant indicating the primitive type accepted as input to the geometry shader contained in program.
	 */
	public GLPrimitiveType geometryInputType(){
		return GLPrimitiveType.fromGLID(glGetProgram(GLProgramParameter.GEOMETRY_INPUT_TYPE));
	}
	
	/**
	 * returns a symbolic constant indicating the primitive type that will be output by the geometry shader contained in program.
	 */
	public GLPrimitiveType geometryOutputType(){
		return GLPrimitiveType.fromGLID(glGetProgram(GLProgramParameter.GEOMETRY_OUTPUT_TYPE));
	}
	
	/**
	 * <p>
	 * Queries the previously linked program object specified by program for the attribute variable 
	 * specified by name and returns the index of the generic vertex attribute that is bound to that 
	 * attribute variable. If name is a matrix attribute variable, the index of the first column of 
	 * the matrix is returned. If the named attribute variable is not an active attribute in the 
	 * specified program object or if name starts with the reserved prefix "gl_", a value of -1 is returned.
	 * </p>
	 * <p>
	 * The association between an attribute variable name and a generic attribute index can be specified 
	 * at any time by calling glBindAttribLocation. Attribute bindings do not go into effect until 
	 * glLinkProgram is called. After a program object has been linked successfully, the index values 
	 * for attribute variables remain fixed until the next link command occurs. The attribute values can 
	 * only be queried after a link if the link was successful. glGetAttribLocation returns the binding 
	 * that actually went into effect the last time glLinkProgram was called for the specified program object. 
	 * Attribute bindings that have been specified since the last link operation are not returned by glGetAttribLocation.
	 * @param theName Points to a null terminated string containing the name of the attribute variable whose location is to be queried.
	 * @return Returns the location of an attribute variable
	 */
	public int attribLocation(String theName){
		return glGetAttribLocation(_myID, theName);
	}
	
	/**
	 * returns the number of active attribute variables for program.
	 */
	public int activeAttributes(){
		return glGetProgram(GLProgramParameter.ACTIVE_ATTRIBUTES);
	}
	
	public List<GLAttributeInfo> attributes(){
		return _myAttributes;
	}
	
	public enum GLAttributeType{
		FLOAT(GL_FLOAT), 
		FLOAT_VEC2(GL_FLOAT_VEC2), 
		FLOAT_VEC3(GL_FLOAT_VEC3), 
		FLOAT_VEC4(GL_FLOAT_VEC4), 
		FLOAT_MAT2(GL_FLOAT_MAT2), 
		FLOAT_MAT3(GL_FLOAT_MAT3), 
		FLOAT_MAT4(GL_FLOAT_MAT4), 
		FLOAT_MAT2x3(GL_FLOAT_MAT2x3), 
		FLOAT_MAT2x4(GL_FLOAT_MAT2x4), 
		FLOAT_MAT3x2(GL_FLOAT_MAT3x2), 
		FLOAT_MAT3x4(GL_FLOAT_MAT3x4), 
		FLOAT_MAT4x2(GL_FLOAT_MAT4x2), 
		FLOAT_MAT4x3(GL_FLOAT_MAT4x3), 
		INT(GL_INT), 
		INT_VEC2(GL_INT_VEC2), 
		INT_VEC3(GL_INT_VEC3), 
		INT_VEC4(GL_INT_VEC4), 
		UNSIGNED_INT(GL_UNSIGNED_INT), 
		UNSIGNED_INT_VEC2(GL_UNSIGNED_INT_VEC2), 
		UNSIGNED_INT_VEC3(GL_UNSIGNED_INT_VEC3), 
		UNSIGNED_INT_VEC4(GL_UNSIGNED_INT_VEC4), 
		DOUBLE(GL_DOUBLE), 
		DOUBLE_VEC2(GL_DOUBLE_VEC2), 
		DOUBLE_VEC3(GL_DOUBLE_VEC3), 
		DOUBLE_VEC4(GL_DOUBLE_VEC4), 
		DOUBLE_MAT2(GL_DOUBLE_MAT2), 
		DOUBLE_MAT3(GL_DOUBLE_MAT3), 
		DOUBLE_MAT4(GL_DOUBLE_MAT4), 
		DOUBLE_MAT2x3(GL_DOUBLE_MAT2x3), 
		DOUBLE_MAT2x4(GL_DOUBLE_MAT2x4), 
		DOUBLE_MAT3x2(GL_DOUBLE_MAT3x2), 
		DOUBLE_MAT3x4(GL_DOUBLE_MAT3x4), 
		DOUBLE_MAT4x2(GL_DOUBLE_MAT4x2), 
		DOUBLE_MAT4x3(GL_DOUBLE_MAT4x3);
		
		private int _myGLID;
		
		GLAttributeType(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLAttributeType fromGLID(int theGLID){
			switch(theGLID){
			case GL_FLOAT:return FLOAT;
			case GL_FLOAT_VEC2:return FLOAT_VEC2;
			case GL_FLOAT_VEC3:return FLOAT_VEC3;
			case GL_FLOAT_VEC4:return FLOAT_VEC4;
			case GL_FLOAT_MAT2:return FLOAT_MAT2;
			case GL_FLOAT_MAT3:return FLOAT_MAT3;
			case GL_FLOAT_MAT4:return FLOAT_MAT4;
			case GL_FLOAT_MAT2x3:return FLOAT_MAT2x3;
			case GL_FLOAT_MAT2x4:return FLOAT_MAT2x4;
			case GL_FLOAT_MAT3x2:return FLOAT_MAT3x2;
			case GL_FLOAT_MAT3x4:return FLOAT_MAT3x4;
			case GL_FLOAT_MAT4x2:return FLOAT_MAT4x2;
			case GL_FLOAT_MAT4x3:return FLOAT_MAT4x3;
			case GL_INT:return INT;
			case GL_INT_VEC2:return INT_VEC2;
			case GL_INT_VEC3:return INT_VEC3;
			case GL_INT_VEC4:return INT_VEC4;
			case GL_UNSIGNED_INT:return UNSIGNED_INT;
			case GL_UNSIGNED_INT_VEC2:return UNSIGNED_INT_VEC2;
			case GL_UNSIGNED_INT_VEC3:return UNSIGNED_INT_VEC3;
			case GL_UNSIGNED_INT_VEC4:return UNSIGNED_INT_VEC4;
			case GL_DOUBLE:return DOUBLE;
			case GL_DOUBLE_VEC2:return DOUBLE_VEC2;
			case GL_DOUBLE_VEC3:return DOUBLE_VEC3;
			case GL_DOUBLE_VEC4:return DOUBLE_VEC4;
			case GL_DOUBLE_MAT2:return DOUBLE_MAT2;
			case GL_DOUBLE_MAT3:return DOUBLE_MAT3;
			case GL_DOUBLE_MAT4:return DOUBLE_MAT4;
			case GL_DOUBLE_MAT2x3:return DOUBLE_MAT2x3;
			case GL_DOUBLE_MAT2x4:return DOUBLE_MAT2x4;
			case GL_DOUBLE_MAT3x2:return DOUBLE_MAT3x2;
			case GL_DOUBLE_MAT3x4:return DOUBLE_MAT3x4;
			case GL_DOUBLE_MAT4x2:return DOUBLE_MAT4x2;
			case GL_DOUBLE_MAT4x3:return DOUBLE_MAT4x3;
			}
			return null;
		}
	}
	
	public static class GLAttributeInfo{
		
		private String _myName;
		
		private GLAttributeType _myType;
		
		private int _myLocation;
		
		private GLAttributeInfo(String theName, GLAttributeType theType, int theLocation){
			_myName = theName;
			_myType = theType;
			_myLocation = theLocation;
		}

		public String name() {
			return _myName;
		}

		public GLAttributeType type() {
			return _myType;
		}

		public int location() {
			return _myLocation;
		}
		
		@Override
		public String toString() {
			return "[" +getClass().getSimpleName() + " type:" + _myType + " name:" + _myName + " location:" + _myLocation+"]";
		}
		
	}
	
	public GLAttributeInfo activeAttribute(int theIndex){
		int maxSize = activeAttributeMaxLength();
		int myType;
		String myAttributeName;
		int myLocation;
		try ( MemoryStack stack = MemoryStack.stackPush() ) {
			ByteBuffer nameBuffer = stack.malloc(maxSize);
			IntBuffer lengthBuffer = stack.mallocInt(1);
			IntBuffer sizeBuffer = stack.mallocInt(1);
			IntBuffer typeBuffer = stack.mallocInt(1);
			glGetActiveAttrib(_myID, theIndex, lengthBuffer, sizeBuffer, typeBuffer, nameBuffer);
		
			int myStringLength = lengthBuffer.get();
			myType = typeBuffer.get(0);

			byte[] infoBytes = new byte[myStringLength];
			nameBuffer.get(infoBytes);
			myAttributeName = new String(infoBytes);
			myLocation = glGetAttribLocation(_myID, myAttributeName);
		}
		return new GLAttributeInfo(
			myAttributeName,
			GLAttributeType.fromGLID(myType),
			myLocation
		);
	}
	
	/**
	 * returns the length of the longest active attribute name for program, including the null termination 
	 * character (i.e., the size of the character buffer required to store the longest attribute name). 
	 * If no active attributes exist, 0 is returned.
	 */
	public int activeAttributeMaxLength(){
		return glGetProgram(GLProgramParameter.ACTIVE_ATTRIBUTE_MAX_LENGTH);
	}
	
	/**
	 * returns the number of active uniform variables for program.
	 */
	public int activeUniforms(){
		return glGetProgram(GLProgramParameter.ACTIVE_UNIFORMS);
	}
	
	/**
	 * returns the length of the longest active uniform variable name for program, including the null termination 
	 * character (i.e., the size of the character buffer required to store the longest uniform variable name). 
	 * If no active uniform variables exist, 0 is returned.
	 */
	public int activeUniformMaxLength(){
		return glGetProgram(GLProgramParameter.ACTIVE_UNIFORM_MAX_LENGTH);
	}
	
	public GLUniformInfo uniform(String theName){
		return _myUniformMap.get(theName);
	}
	
	public List<GLUniformInfo> uniforms(){
		return _myUniforms;
	}

	public GLUniformParameters createUniformParameters(Object theObject){
		return new GLUniformParameters(this, theObject);
	}
	
	public void uniforms(Object theObject){
		uniforms(createUniformParameters(theObject));
	}
	
	public void uniforms(GLUniformParameters theParameters){
		if(theParameters == null)return;
		theParameters.apply(this);
	}
	
	public enum GLUniformType{
		FLOAT(GL_FLOAT), 
		FLOAT_VEC2(GL_FLOAT_VEC2), 
		FLOAT_VEC3(GL_FLOAT_VEC3), 
		FLOAT_VEC4(GL_FLOAT_VEC4),

		DOUBLE(GL_DOUBLE), 
		DOUBLE_VEC2(GL_DOUBLE_VEC2), 
		DOUBLE_VEC3(GL_DOUBLE_VEC3), 
		DOUBLE_VEC4(GL_DOUBLE_VEC4), 
		
		INT(GL_INT), 
		INT_VEC2(GL_INT_VEC2), 
		INT_VEC3(GL_INT_VEC3), 
		INT_VEC4(GL_INT_VEC4), 

		UNSIGNED_INT(GL_UNSIGNED_INT), 
		UNSIGNED_INT_VEC2(GL_UNSIGNED_INT_VEC2), 
		UNSIGNED_INT_VEC3(GL_UNSIGNED_INT_VEC3), 
		UNSIGNED_INT_VEC4(GL_UNSIGNED_INT_VEC4), 
		
		BOOL(GL_BOOL), 
		BOOL_VEC2(GL_BOOL_VEC2), 
		BOOL_VEC3(GL_BOOL_VEC3), 
		BOOL_VEC4(GL_BOOL_VEC4), 
		
		FLOAT_MAT2(GL_FLOAT_MAT2), 
		FLOAT_MAT3(GL_FLOAT_MAT3), 
		FLOAT_MAT4(GL_FLOAT_MAT4), 
		FLOAT_MAT2x3(GL_FLOAT_MAT2x3), 
		FLOAT_MAT2x4(GL_FLOAT_MAT2x4), 
		FLOAT_MAT3x2(GL_FLOAT_MAT3x2), 
		FLOAT_MAT3x4(GL_FLOAT_MAT3x4), 
		FLOAT_MAT4x2(GL_FLOAT_MAT4x2), 
		FLOAT_MAT4x3(GL_FLOAT_MAT4x3), 
		
		DOUBLE_MAT2(GL_DOUBLE_MAT2), 
		DOUBLE_MAT3(GL_DOUBLE_MAT3), 
		DOUBLE_MAT4(GL_DOUBLE_MAT4), 
		DOUBLE_MAT2x3(GL_DOUBLE_MAT2x3), 
		DOUBLE_MAT2x4(GL_DOUBLE_MAT2x4), 
		DOUBLE_MAT3x2(GL_DOUBLE_MAT3x2), 
		DOUBLE_MAT3x4(GL_DOUBLE_MAT3x4), 
		DOUBLE_MAT4x2(GL_DOUBLE_MAT4x2), 
		DOUBLE_MAT4x3(GL_DOUBLE_MAT4x3),
		
		SAMPLER_1D(GL_SAMPLER_1D),
		SAMPLER_2D(GL_SAMPLER_2D),
		SAMPLER_3D(GL_SAMPLER_3D),
		SAMPLER_CUBE(GL_SAMPLER_CUBE),
		SAMPLER_1D_SHADOW(GL_SAMPLER_1D_SHADOW),
		SAMPLER_2D_SHADOW(GL_SAMPLER_2D_SHADOW),
		SAMPLER_1D_ARRAY(GL_SAMPLER_1D_ARRAY),
		SAMPLER_2D_ARRAY(GL_SAMPLER_2D_ARRAY),
		SAMPLER_1D_ARRAY_SHADOW(GL_SAMPLER_1D_ARRAY_SHADOW),
		SAMPLER_2D_ARRAY_SHADOW(GL_SAMPLER_2D_ARRAY_SHADOW),
		SAMPLER_2D_MULTISAMPLE(GL_SAMPLER_2D_MULTISAMPLE),
		SAMPLER_2D_MULTISAMPLE_ARRAY(GL_SAMPLER_2D_MULTISAMPLE_ARRAY),
		SAMPLER_CUBE_SHADOW(GL_SAMPLER_CUBE_SHADOW),
		SAMPLER_BUFFER(GL_SAMPLER_BUFFER),
		SAMPLER_2D_RECT(GL_SAMPLER_2D_RECT),
		SAMPLER_2D_RECT_SHADOW(GL_SAMPLER_2D_RECT_SHADOW),
		
		INT_SAMPLER_1D(GL_INT_SAMPLER_1D),
		INT_SAMPLER_2D(GL_INT_SAMPLER_2D),
		INT_SAMPLER_3D(GL_INT_SAMPLER_3D),
		INT_SAMPLER_CUBE(GL_INT_SAMPLER_CUBE),
		INT_SAMPLER_1D_ARRAY(GL_INT_SAMPLER_1D_ARRAY),
		INT_SAMPLER_2D_ARRAY(GL_INT_SAMPLER_2D_ARRAY),
		INT_SAMPLER_2D_MULTISAMPLE(GL_INT_SAMPLER_2D_MULTISAMPLE),
		INT_SAMPLER_2D_MULTISAMPLE_ARRAY(GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY),
		INT_SAMPLER_BUFFER(GL_INT_SAMPLER_BUFFER),
		INT_SAMPLER_2D_RECT(GL_INT_SAMPLER_2D_RECT),
		
		UNSIGNED_INT_SAMPLER_1D(GL_UNSIGNED_INT_SAMPLER_1D),
		UNSIGNED_INT_SAMPLER_2D(GL_UNSIGNED_INT_SAMPLER_2D),
		UNSIGNED_INT_SAMPLER_3D(GL_UNSIGNED_INT_SAMPLER_3D),
		UNSIGNED_INT_SAMPLER_CUBE(GL_UNSIGNED_INT_SAMPLER_CUBE),
		UNSIGNED_INT_SAMPLER_1D_ARRAY(GL_UNSIGNED_INT_SAMPLER_1D_ARRAY),
		UNSIGNED_INT_SAMPLER_2D_ARRAY(GL_UNSIGNED_INT_SAMPLER_2D_ARRAY),
		UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE),
		UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY(GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY),
		UNSIGNED_INT_SAMPLER_BUFFER(GL_UNSIGNED_INT_SAMPLER_BUFFER),
		UNSIGNED_INT_SAMPLER_2D_RECT(GL_UNSIGNED_INT_SAMPLER_2D_RECT),
		
		IMAGE_1D(GL_IMAGE_1D),
		IMAGE_2D(GL_IMAGE_2D),
		IMAGE_3D(GL_IMAGE_3D),
		IMAGE_2D_RECT(GL_IMAGE_2D_RECT),
		IMAGE_CUBE(GL_IMAGE_CUBE),
		IMAGE_BUFFER(GL_IMAGE_BUFFER),
		IMAGE_1D_ARRAY(GL_IMAGE_1D_ARRAY),
		IMAGE_2D_ARRAY(GL_IMAGE_2D_ARRAY),
		IMAGE_2D_MULTISAMPLE(GL_IMAGE_2D_MULTISAMPLE),
		IMAGE_2D_MULTISAMPLE_ARRAY(GL_IMAGE_2D_MULTISAMPLE_ARRAY),
		
		INT_IMAGE_1D(GL_INT_IMAGE_1D),
		INT_IMAGE_2D(GL_INT_IMAGE_2D),
		INT_IMAGE_3D(GL_INT_IMAGE_3D),
		INT_IMAGE_2D_RECT(GL_INT_IMAGE_2D_RECT),
		INT_IMAGE_CUBE(GL_INT_IMAGE_CUBE),
		INT_IMAGE_BUFFER(GL_INT_IMAGE_BUFFER),
		INT_IMAGE_1D_ARRAY(GL_INT_IMAGE_1D_ARRAY),
		INT_IMAGE_2D_ARRAY(GL_INT_IMAGE_2D_ARRAY),
		INT_IMAGE_2D_MULTISAMPLE(GL_INT_IMAGE_2D_MULTISAMPLE),
		INT_IMAGE_2D_MULTISAMPLE_ARRAY(GL_INT_IMAGE_2D_MULTISAMPLE_ARRAY),
		
		UNSIGNED_INT_IMAGE_1D(GL_UNSIGNED_INT_IMAGE_1D),
		UNSIGNED_INT_IMAGE_2D(GL_UNSIGNED_INT_IMAGE_2D),
		UNSIGNED_INT_IMAGE_3D(GL_UNSIGNED_INT_IMAGE_3D),
		UNSIGNED_INT_IMAGE_2D_RECT(GL_UNSIGNED_INT_IMAGE_2D_RECT),
		UNSIGNED_INT_IMAGE_CUBE(GL_UNSIGNED_INT_IMAGE_CUBE),
		UNSIGNED_INT_IMAGE_BUFFER(GL_UNSIGNED_INT_IMAGE_BUFFER),
		UNSIGNED_INT_IMAGE_1D_ARRAY(GL_UNSIGNED_INT_IMAGE_1D_ARRAY),
		UNSIGNED_INT_IMAGE_2D_ARRAY(GL_UNSIGNED_INT_IMAGE_2D_ARRAY),
		UNSIGNED_INT_IMAGE_2D_MULTISAMPLE(GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE),
		UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY(GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY),
		UNSIGNED_INT_ATOMIC_COUNTER(GL_UNSIGNED_INT_ATOMIC_COUNTER);
		
		private int _myGLID;
		
		GLUniformType(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLUniformType fromGLID(int theGLID){
			switch(theGLID){
			case GL_FLOAT:return FLOAT;
			case GL_FLOAT_VEC2:return FLOAT_VEC2;
			case GL_FLOAT_VEC3:return FLOAT_VEC3;
			case GL_FLOAT_VEC4:return FLOAT_VEC4;
			
			case GL_DOUBLE:return DOUBLE;
			case GL_DOUBLE_VEC2:return DOUBLE_VEC2;
			case GL_DOUBLE_VEC3:return DOUBLE_VEC3;
			case GL_DOUBLE_VEC4:return DOUBLE_VEC4;
			
			case GL_INT:return INT;
			case GL_INT_VEC2:return INT_VEC2;
			case GL_INT_VEC3:return INT_VEC3;
			case GL_INT_VEC4:return INT_VEC4;
			
			case GL_UNSIGNED_INT:return UNSIGNED_INT;
			case GL_UNSIGNED_INT_VEC2:return UNSIGNED_INT_VEC2;
			case GL_UNSIGNED_INT_VEC3:return UNSIGNED_INT_VEC3;
			case GL_UNSIGNED_INT_VEC4:return UNSIGNED_INT_VEC4;
			
			case GL_BOOL:return BOOL;
			case GL_BOOL_VEC2:return BOOL_VEC2;
			case GL_BOOL_VEC3:return BOOL_VEC3;
			case GL_BOOL_VEC4:return BOOL_VEC4;
			
			case GL_FLOAT_MAT2:return FLOAT_MAT2;
			case GL_FLOAT_MAT3:return FLOAT_MAT3;
			case GL_FLOAT_MAT4:return FLOAT_MAT4;
			case GL_FLOAT_MAT2x3:return FLOAT_MAT2x3;
			case GL_FLOAT_MAT2x4:return FLOAT_MAT2x4;
			case GL_FLOAT_MAT3x2:return FLOAT_MAT3x2;
			case GL_FLOAT_MAT3x4:return FLOAT_MAT3x4;
			case GL_FLOAT_MAT4x2:return FLOAT_MAT4x2;
			case GL_FLOAT_MAT4x3:return FLOAT_MAT4x3;
			
			case GL_DOUBLE_MAT2:return DOUBLE_MAT2;
			case GL_DOUBLE_MAT3:return DOUBLE_MAT3;
			case GL_DOUBLE_MAT4:return DOUBLE_MAT4;
			case GL_DOUBLE_MAT2x3:return DOUBLE_MAT2x3;
			case GL_DOUBLE_MAT2x4:return DOUBLE_MAT2x4;
			case GL_DOUBLE_MAT3x2:return DOUBLE_MAT3x2;
			case GL_DOUBLE_MAT3x4:return DOUBLE_MAT3x4;
			case GL_DOUBLE_MAT4x2:return DOUBLE_MAT4x2;
			case GL_DOUBLE_MAT4x3:return DOUBLE_MAT4x3;
			
			case GL_SAMPLER_1D:return SAMPLER_1D;
			case GL_SAMPLER_2D:return SAMPLER_2D;
			case GL_SAMPLER_3D:return SAMPLER_3D;
			case GL_SAMPLER_CUBE:return SAMPLER_CUBE;
			case GL_SAMPLER_1D_SHADOW:return SAMPLER_1D_SHADOW;
			case GL_SAMPLER_2D_SHADOW:return SAMPLER_2D_SHADOW;
			case GL_SAMPLER_1D_ARRAY:return SAMPLER_1D_ARRAY;
			case GL_SAMPLER_2D_ARRAY:return SAMPLER_2D_ARRAY;
			case GL_SAMPLER_1D_ARRAY_SHADOW:return SAMPLER_1D_ARRAY_SHADOW;
			case GL_SAMPLER_2D_ARRAY_SHADOW:return SAMPLER_2D_ARRAY_SHADOW;
			case GL_SAMPLER_2D_MULTISAMPLE:return SAMPLER_2D_MULTISAMPLE;
			case GL_SAMPLER_2D_MULTISAMPLE_ARRAY:return SAMPLER_2D_MULTISAMPLE_ARRAY;
			case GL_SAMPLER_CUBE_SHADOW:return SAMPLER_CUBE_SHADOW;
			case GL_SAMPLER_BUFFER:return SAMPLER_BUFFER;
			case GL_SAMPLER_2D_RECT:return SAMPLER_2D_RECT;
			case GL_SAMPLER_2D_RECT_SHADOW:return SAMPLER_2D_RECT_SHADOW;
			
			case GL_INT_SAMPLER_1D:return INT_SAMPLER_1D;
			case GL_INT_SAMPLER_2D:return INT_SAMPLER_2D;
			case GL_INT_SAMPLER_3D:return INT_SAMPLER_3D;
			case GL_INT_SAMPLER_CUBE:return INT_SAMPLER_CUBE;
			case GL_INT_SAMPLER_1D_ARRAY:return INT_SAMPLER_1D_ARRAY;
			case GL_INT_SAMPLER_2D_ARRAY:return INT_SAMPLER_2D_ARRAY;
			case GL_INT_SAMPLER_2D_MULTISAMPLE:return INT_SAMPLER_2D_MULTISAMPLE;
			case GL_INT_SAMPLER_2D_MULTISAMPLE_ARRAY:return INT_SAMPLER_2D_MULTISAMPLE_ARRAY;
			case GL_INT_SAMPLER_BUFFER:return INT_SAMPLER_BUFFER;
			case GL_INT_SAMPLER_2D_RECT:return INT_SAMPLER_2D_RECT;
			
			case GL_UNSIGNED_INT_SAMPLER_1D:return UNSIGNED_INT_SAMPLER_1D;
			case GL_UNSIGNED_INT_SAMPLER_2D:return UNSIGNED_INT_SAMPLER_2D;
			case GL_UNSIGNED_INT_SAMPLER_3D:return UNSIGNED_INT_SAMPLER_3D;
			case GL_UNSIGNED_INT_SAMPLER_CUBE:return UNSIGNED_INT_SAMPLER_CUBE;
			case GL_UNSIGNED_INT_SAMPLER_1D_ARRAY:return UNSIGNED_INT_SAMPLER_1D_ARRAY;
			case GL_UNSIGNED_INT_SAMPLER_2D_ARRAY:return UNSIGNED_INT_SAMPLER_2D_ARRAY;
			case GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE:return UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE;
			case GL_UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY:return UNSIGNED_INT_SAMPLER_2D_MULTISAMPLE_ARRAY;
			case GL_UNSIGNED_INT_SAMPLER_BUFFER:return UNSIGNED_INT_SAMPLER_BUFFER;
			case GL_UNSIGNED_INT_SAMPLER_2D_RECT:return UNSIGNED_INT_SAMPLER_2D_RECT;
			
			case GL_IMAGE_1D:return IMAGE_1D;
			case GL_IMAGE_2D:return IMAGE_2D;
			case GL_IMAGE_3D:return IMAGE_3D;
			case GL_IMAGE_2D_RECT:return IMAGE_2D_RECT;
			case GL_IMAGE_CUBE:return IMAGE_CUBE;
			case GL_IMAGE_BUFFER:return IMAGE_BUFFER;
			case GL_IMAGE_1D_ARRAY:return IMAGE_1D_ARRAY;
			case GL_IMAGE_2D_ARRAY:return IMAGE_2D_ARRAY;
			case GL_IMAGE_2D_MULTISAMPLE:return IMAGE_2D_MULTISAMPLE;
			case GL_IMAGE_2D_MULTISAMPLE_ARRAY:return IMAGE_2D_MULTISAMPLE_ARRAY;
			
			case GL_INT_IMAGE_1D:return INT_IMAGE_1D;
			case GL_INT_IMAGE_2D:return INT_IMAGE_2D;
			case GL_INT_IMAGE_3D:return INT_IMAGE_3D;
			case GL_INT_IMAGE_2D_RECT:return INT_IMAGE_2D_RECT;
			case GL_INT_IMAGE_CUBE:return INT_IMAGE_CUBE;
			case GL_INT_IMAGE_BUFFER:return INT_IMAGE_BUFFER;
			case GL_INT_IMAGE_1D_ARRAY:return INT_IMAGE_1D_ARRAY;
			case GL_INT_IMAGE_2D_ARRAY:return INT_IMAGE_2D_ARRAY;
			case GL_INT_IMAGE_2D_MULTISAMPLE:return INT_IMAGE_2D_MULTISAMPLE;
			case GL_INT_IMAGE_2D_MULTISAMPLE_ARRAY:return INT_IMAGE_2D_MULTISAMPLE_ARRAY;
			
			case GL_UNSIGNED_INT_IMAGE_1D:return UNSIGNED_INT_IMAGE_1D;
			case GL_UNSIGNED_INT_IMAGE_2D:return UNSIGNED_INT_IMAGE_2D;
			case GL_UNSIGNED_INT_IMAGE_3D:return UNSIGNED_INT_IMAGE_3D;
			case GL_UNSIGNED_INT_IMAGE_2D_RECT:return UNSIGNED_INT_IMAGE_2D_RECT;
			case GL_UNSIGNED_INT_IMAGE_CUBE:return UNSIGNED_INT_IMAGE_CUBE;
			case GL_UNSIGNED_INT_IMAGE_BUFFER:return UNSIGNED_INT_IMAGE_BUFFER;
			case GL_UNSIGNED_INT_IMAGE_1D_ARRAY:return UNSIGNED_INT_IMAGE_1D_ARRAY;
			case GL_UNSIGNED_INT_IMAGE_2D_ARRAY:return UNSIGNED_INT_IMAGE_2D_ARRAY;
			case GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE:return UNSIGNED_INT_IMAGE_2D_MULTISAMPLE;
			case GL_UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY:return UNSIGNED_INT_IMAGE_2D_MULTISAMPLE_ARRAY;
			case GL_UNSIGNED_INT_ATOMIC_COUNTER:return UNSIGNED_INT_ATOMIC_COUNTER;
			}
			return null;
		}
	}
	
	public static class GLUniformInfo{
		
		private String _myName;
		
		private GLUniformType _myType;
		
		private int _myLocation;
		
		private GLUniformInfo(String theName, GLUniformType theType, int theLocation){
			_myName = theName;
			_myType = theType;
			_myLocation = theLocation;
		}

		public String name() {
			return _myName;
		}

		public GLUniformType type() {
			return _myType;
		}

		public int location() {
			return _myLocation;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " [name:"+ _myName +", type:" + _myType +", location:" + _myLocation+"]";
		}
		
	}
	
	public GLUniformInfo activeUniform(int theIndex){
		int maxSize = activeUniformMaxLength();
		int myType;
		String myUniformName;
		int myLocation;
		try ( MemoryStack stack = MemoryStack.stackPush() ) { 
			ByteBuffer nameBuffer = stack.malloc(maxSize);
			IntBuffer lengthBuffer = stack.mallocInt(1);
			IntBuffer sizeBuffer = stack.mallocInt(1);
			IntBuffer typeBuffer = stack.mallocInt(1);
			glGetActiveUniform(_myID, theIndex, lengthBuffer, sizeBuffer, typeBuffer, nameBuffer);
		
			int myStringLength = lengthBuffer.get();
			myType = typeBuffer.get(0);

			byte[] infoBytes = new byte[myStringLength];
			nameBuffer.get(infoBytes);
			myUniformName = new String(infoBytes);
			myLocation = glGetUniformLocation(_myID, myUniformName);
		}
		return new GLUniformInfo(
			myUniformName,
			GLUniformType.fromGLID(myType),
			myLocation
		);
	}
	
	/**
	 * Specifies the parameter of the shader program stage to query. 
	 */
	public enum GLProgramStageQuery{
		/**
		 * number of active subroutine variables in the stage
		 */
		ACTIVE_SUBROUTINE_UNIFORMS(GL_ACTIVE_SUBROUTINE_UNIFORMS),
		/**
		 * number of active subroutine variable locations in the stage
		 */
		ACTIVE_SUBROUTINE_UNIFORM_LOCATIONS(GL_ACTIVE_SUBROUTINE_UNIFORM_LOCATIONS),
		/**
		 * number of active subroutines in the stage
		 */
		ACTIVE_SUBROUTINES(GL_ACTIVE_SUBROUTINES),
		/**
		 * length of the longest subroutine uniform for the stage
		 */
		ACTIVE_SUBROUTINE_UNIFORM_MAX_LENGTH(GL_ACTIVE_SUBROUTINE_UNIFORM_MAX_LENGTH),
		/**
		 *  length of the longest subroutine name for the stage
		 */
		ACTIVE_SUBROUTINE_MAX_LENGTH(GL_ACTIVE_SUBROUTINE_MAX_LENGTH);
		
		private int _myGLID;
		
		GLProgramStageQuery(int theGLID){
			_myGLID = theGLID;
		}
		
		public int glID(){
			return _myGLID;
		}
		
		public static GLProgramStageQuery fromGLID(int theGLID){
			switch(theGLID){
			case GL_ACTIVE_SUBROUTINE_UNIFORMS:return ACTIVE_SUBROUTINE_UNIFORMS;
			case GL_ACTIVE_SUBROUTINE_UNIFORM_LOCATIONS:return ACTIVE_SUBROUTINE_UNIFORM_LOCATIONS;
			case GL_ACTIVE_SUBROUTINES:return ACTIVE_SUBROUTINES;
			case GL_ACTIVE_SUBROUTINE_UNIFORM_MAX_LENGTH:return ACTIVE_SUBROUTINE_UNIFORM_MAX_LENGTH;
			case GL_ACTIVE_SUBROUTINE_MAX_LENGTH:return ACTIVE_SUBROUTINE_MAX_LENGTH;
			}
			return null;
		}
	} 
	
	/**
	 * Queries a parameter of a shader stage attached to a program object. shadertype specifies the stage 
	 * from which to query the parameter. query specifies which parameter should be queried. The value of 
	 * the parameter to be queried is returned.
	 * @param theShaderType Specifies the shader stage from which to query for the subroutine parameter
	 * @param theQuery Specifies the parameter of the shader to query.
	 * @return the queried value
	 */
	public int programStage(GLShaderType theShaderType, GLProgramStageQuery theQuery){
		try ( MemoryStack stack = MemoryStack.stackPush() ) { 
		    IntBuffer ip = stack.mallocInt(1); 
		    glGetProgramStageiv(_myID, theShaderType.glID, theQuery.glID(), ip);
		    return ip.get();
		}
	}
	
	private class GLSubroutineManager{
		private GLShaderType _myShaderType;
		private Map<String, GLSubroutineUniformInfo> _mySubroutineUniformsMap;
		private List<GLSubroutineUniformInfo> _mySubroutineUniforms;
		private int _myActiveSubrountineUniforms;
		private IntBuffer _mySubroutineIndices;
		
		private GLSubroutineManager(GLShaderType theShaderType){
			_myShaderType = theShaderType;
			_myActiveSubrountineUniforms = programStage(_myShaderType, GLProgramStageQuery.ACTIVE_SUBROUTINE_UNIFORM_LOCATIONS);
			if(_myActiveSubrountineUniforms == 0){
				return ;
			}
			_mySubroutineIndices = IntBuffer.allocate(_myActiveSubrountineUniforms);
			_mySubroutineUniformsMap = new HashMap<>();
			_mySubroutineUniforms = new ArrayList<>();
			for(int i = 0; i < _myActiveSubrountineUniforms;i++){
				GLSubroutineUniformInfo myInfo = activeSubroutine(i,_myShaderType);
				_mySubroutineUniformsMap.put(myInfo.name(),myInfo);
				_mySubroutineUniforms.add(myInfo);
			}
		}
		
		private void subroutine(String theUniform, String theSubroutine){
			if(_mySubroutineUniformsMap == null)throw new GLException("No Subroutines defined for the ShaderType: " + _myShaderType);
			
			GLSubroutineUniformInfo myUniformInfo = _mySubroutineUniformsMap.get(theUniform);
			if(myUniformInfo == null)throw new GLException("No SubroutineUniform with the name: " + theUniform + " defined for the ShaderType: " + _myShaderType);
			
			GLSubroutineInfo  mySubroutineInfo = myUniformInfo.compatibleSubroutines().get(theSubroutine);
			if(mySubroutineInfo == null)throw new GLException("No compatible Subroutine with the name: " + theSubroutine + " defined for the uniform " + theUniform + " for the ShaderType: " + _myShaderType);
		
			_mySubroutineIndices.put(myUniformInfo.location(), mySubroutineInfo.index());
			apply();
		}
		
		
		private void apply(){
			if(_myActiveSubrountineUniforms <= 0)return;
			_mySubroutineIndices.rewind();
			uniformSubroutines(_myShaderType, _mySubroutineIndices);
		}
	}
	
	public void subroutine(GLShaderType theShaderType, String theSubroutineUniform, String theSubroutine){
		_mySubroutineManager.get(theShaderType).subroutine(theSubroutineUniform, theSubroutine);
	}
	
	public void uniformSubroutines(GLShaderType theShaderType,  IntBuffer theIndices){
		glUniformSubroutinesuiv(theShaderType.glID, theIndices);
	}
	
	public static class GLSubroutineInfo{
		private int _myIndex;
		private String _myName;
		
		private GLSubroutineInfo(int theIndex, String theName){
			_myIndex = theIndex;
			_myName = theName;
		}
		
		public int index(){
			return _myIndex;
		}
		
		public String name(){
			return _myName;
		}
		
		@Override
		public String toString() {
			return getClass().getSimpleName() + " name: " + _myName + " location: " + _myIndex;
		}
	}
	
	public static class GLSubroutineUniformInfo{
		private int _myLocation;
		private String _myName;
		private Map<String, GLSubroutineInfo> _myCompatibleSubroutines = new HashMap<>();
		
		private GLSubroutineUniformInfo(int theLocation, String theName){
			_myLocation = theLocation;
			_myName = theName;
		}
		
		private void addCompatibleSubroutine(GLSubroutineInfo theCompatibleSubroutine){
			_myCompatibleSubroutines.put(theCompatibleSubroutine._myName, theCompatibleSubroutine);
		}
		
		public int location(){
			return _myLocation;
		}
		
		public String name(){
			return _myName;
		}
		
		public Map<String, GLSubroutineInfo> compatibleSubroutines(){
			return _myCompatibleSubroutines;
		}
		
		@Override
		public String toString() {
			StringBuffer myResult = new StringBuffer();
			myResult.append(getClass().getSimpleName() + " name: " + _myName + " location: " + _myLocation+"\n");
			myResult.append("compatible Subroutines:\n");
			for(GLSubroutineInfo mySubroutine:_myCompatibleSubroutines.values()){
				myResult.append(mySubroutine+"\n");
			}
			return myResult.toString();
		}
	}
	
	public List<GLSubroutineUniformInfo> subroutineUniforms(GLShaderType theType){
		return _mySubroutineManager.get(theType)._mySubroutineUniforms;
	}
	
	/**
	 * TODO test this
	 * @param theID
	 * @param theShaderType
	 * @return
	 */
	public GLSubroutineUniformInfo activeSubroutine(int theID, GLShaderType theShaderType){
		int maxSize = programStage(theShaderType, GLProgramStageQuery.ACTIVE_SUBROUTINE_UNIFORM_MAX_LENGTH);
		ByteBuffer myNameBuffer = ByteBuffer.allocate(maxSize);
		IntBuffer myLengthBuffer = IntBuffer.allocate(1);
		
	    glGetActiveSubroutineUniformName(_myID, theShaderType.glID, theID, myLengthBuffer, myNameBuffer);
	    
		int myStringLength = myLengthBuffer.get();
		byte[] infoBytes = new byte[myStringLength];
		myNameBuffer.get(infoBytes);
		String mySubroutineUniformName = new String(infoBytes);
		int mySubroutineLocation = glGetSubroutineUniformLocation(_myID, theShaderType.glID, mySubroutineUniformName);
		
		GLSubroutineUniformInfo mySubroutineUniformInfo = new GLSubroutineUniformInfo(mySubroutineLocation, mySubroutineUniformName);
		int myNumberOfCompatibleSubroutines = 0;
		try ( MemoryStack stack = MemoryStack.stackPush() ) { 
		    IntBuffer ip = stack.mallocInt(1); 
		    glGetActiveSubroutineUniformiv(_myID, theShaderType.glID, theID, GL_NUM_COMPATIBLE_SUBROUTINES, ip);
		    myNumberOfCompatibleSubroutines = ip.get();
		}
		
		IntBuffer mySubroutines = IntBuffer.allocate(myNumberOfCompatibleSubroutines);		   
	    glGetActiveSubroutineUniformiv(_myID, theShaderType.glID, theID, GL_COMPATIBLE_SUBROUTINES, mySubroutines);
	   
	    int myMaxSubroutineNameLength = programStage(theShaderType, GLProgramStageQuery.ACTIVE_SUBROUTINE_MAX_LENGTH);
		ByteBuffer mySubroutineNameBuffer = ByteBuffer.allocate(myMaxSubroutineNameLength);
		IntBuffer mySubroutineNameLengthBuffer = IntBuffer.allocate(1);
		
	    for (int i=0; i < myNumberOfCompatibleSubroutines; i++) {
	    	mySubroutineNameLengthBuffer.rewind();
	    	mySubroutineNameBuffer.rewind();
	        glGetActiveSubroutineName(_myID, theShaderType.glID, mySubroutines.get(i), mySubroutineNameLengthBuffer, mySubroutineNameBuffer);
	        
	        int mySubroutineStringLength = mySubroutineNameLengthBuffer.get();
			byte[] mySubroutineNameBytes = new byte[mySubroutineStringLength];
			mySubroutineNameBuffer.get(mySubroutineNameBytes);
			
			String mySubroutineName = new String(mySubroutineNameBytes);
			int mySubroutineIndex = glGetSubroutineIndex(_myID, theShaderType.glID, mySubroutineName);
			mySubroutineUniformInfo.addCompatibleSubroutine(new GLSubroutineInfo(mySubroutineIndex, mySubroutineName));
	    }
	    
	    return mySubroutineUniformInfo;
	}
	
	/**
	 * TODO finish doc
	 * Returns the index of the uniform variable name. name is a null-terminated
	 * character string with no spaces. A value of -1 is returned if name does not correspond
	 * to a uniform variable in the active shader program, or if a reserved shader variable name
	 * (those starting with gl_) is specified
	 * <p>
	 * Name can be a single variable name, an element of an array
	 * @param theUniform
	 * @return
	 */
	public int uniformLocation(String theUniform){
		return glGetUniformLocation(_myID, theUniform);
	}
	
	/**
	 * TODO implement uniform block indices
	 * @param theBlockName
	 * @return
	 */
	public GLUniformBlock uniformBlock(String theBlockName, String...theUniformNamess){
		if(!_myUniformBlockMap.containsKey(theBlockName)){
			int myID = glGetUniformBlockIndex(_myID, theBlockName);
			if(myID == GL_INVALID_INDEX){
				_myUniformBlockMap.put(theBlockName, null);
			}else{
				_myUniformBlockMap.put(theBlockName, new GLUniformBlock(myID, this));
			}
		}
		
		return _myUniformBlockMap.get(theBlockName);
	}
	
	/**
	 * Installs the program object specified by program as part of current rendering state.
	 * <p>
	 * A shader program will contain an executable that will run on the vertex processor if it 
	 * contains one or more shader objects of type {@linkplain GLShaderType#VERTEX} that have been successfully compiled and linked. 
	 * A program object will contain an executable that will run on the geometry processor if it 
	 * contains one or more shader objects of type {@linkplain GLShaderType#GEOMETRY} that have been successfully compiled and linked.
	 * Similarly, a program object will contain an executable that will run on the fragment processor if it 
	 * contains one or more shader objects of type {@linkplain GLShaderType#FRAGMENT} that have been successfully compiled and linked.
	 * <p>
	 * While a program object is in use, applications are free to modify attached shader objects, compile attached 
	 * shader objects, attach additional shader objects, and detach or delete shader objects. None of these operations 
	 * will affect the executables that are part of the current state. However, relinking the program object that is 
	 * currently in use will install the program object as part of the current rendering state if the link operation
	 * was successful (see {@linkplain #link()} ). If the program object currently in use is relinked unsuccessfully, 
	 * its link status will be set to <code>false</code>, but the executables and associated state will remain part of 
	 * the current state until {@linkplain #end()} removes it from use. After it is removed from use, it 
	 * cannot be made part of current state until it has been successfully relinked.
	 * <p>
	 * If program does not contain shader objects of type {@linkplain GLShaderType#FRAGMENT}, an executable will be installed on the vertex, 
	 * and possibly geometry processors, but the results of fragment shader execution will be undefined.
	 */
	public void use(){
		glUseProgram(_myID);
	}

	/**
	 * Ends this program from being executed
	 */
	public void end(){
		glUseProgram(0);
	}
	
	@Override
	protected void finalize() {
		glDeleteProgram(_myID);
	}
	
	public void uniform1i(final int theLocation, final int theValue){
		glProgramUniform1i(_myID, theLocation, theValue);
	}
	
	public void uniform1i(final String theName, final int theValue){
		uniform1i(uniformLocation(theName), theValue);
	}
	
	public void uniform(final int theLocation, final boolean theValue){
		if(theValue)glProgramUniform1i(_myID, theLocation, 1);
		else glProgramUniform1i(_myID, theLocation, 0);
	}
	
	public void uniform(final String theName, final boolean theValue){
		uniform(uniformLocation(theName), theValue);
	}
	
	public void uniform1f(final int theLocation, final float theValue){
		glProgramUniform1f(_myID, theLocation, theValue);
	}
	
	public void uniform1f(final String theName, final float theValue){
		uniform1f(uniformLocation(theName), theValue);
	}
	
	public void uniform1f(final int theLocation, final CCVector1 theValue){
		uniform1f(theLocation, theValue.x);
	}
	
	public void uniform1f(final String theName, final CCVector1 theValue){
		uniform1f(theName, theValue.x);
	}
	
	public void uniform1fv(final int theLocation, final List<?> theVectors){
		if(theVectors.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theVectors.size() * 4);
		for(Object myObject:theVectors){
			if(myObject instanceof CCVector1){
				CCVector1 myVector = (CCVector1)myObject;
				myData.put(myVector.x);
			}else if(myObject instanceof Float){
				Float myVector = (Float)myObject;
				myData.put(myVector);
			}
		}
		myData.rewind();
		glProgramUniform1fv(_myID, theLocation, myData);
	}
	
	public void uniform1fv(final String theName, final List<?> theVectors){
		uniform1fv(uniformLocation(theName), theVectors);
	}
	
	public void uniform1fv(final int theLocation, float...theValues){
		if(theValues.length == 0)return;
		
		FloatBuffer myData = FloatBuffer.wrap(theValues);
		myData.rewind();
		glProgramUniform1fv(_myID, theLocation, myData);
	}
	
	public void uniform1fv(final String theName, float...theValues){
		uniform1fv(uniformLocation(theName), theValues);
	}
	
	
	public void uniform2f(final int theLocation, final float theX, final float theY){
		glProgramUniform2f(_myID, theLocation, theX, theY);
	}
	
	public void uniform2f(final int theLocation, final CCVector2 theVector){
		uniform2f(theLocation, (float)theVector.x, (float)theVector.y);
	}
	
	public void uniform2f(final String theName, final float theX, final float theY){
		uniform2f(uniformLocation(theName), theX, theY);
	}
	
	public void uniform2f(final String theName, final CCVector2 theValue){
		uniform2f(uniformLocation(theName), theValue);
	}
	
	public void uniform3f(final int theLocation, final float theX, final float theY, final float theZ){
		glProgramUniform3f(_myID, theLocation, theX, theY, theZ);
	}
	
	public void uniform(final int theLocation, final CCVector3 theVector){
		uniform3f(theLocation, (float)theVector.x, (float)theVector.y, (float)theVector.z);
	}
	
	public void uniform3f(final String theName, final float theX, final float theY, final float theZ){
		uniform3f(uniformLocation(theName), theX, theY, theZ);
	}
	
	public void uniform3f(final String theName, final CCVector3 theValue){
		uniform(uniformLocation(theName), theValue);
	}
	
	public void uniform4f(int theLocation, final float theX, final float theY, final float theZ, float theW){
		glProgramUniform4f(_myID, theLocation, theX, theY, theZ, theW);
	}
	
	public void uniform4f(final String theName, final float theX, final float theY, final float theZ, float theW){
		uniform4f(uniformLocation(theName), theX, theY, theZ, theW);
	}
	
	public void uniform4f(final int theLocation, final CCVector4 theVector){
		uniform4f(theLocation, (float)theVector.x, (float)theVector.y, (float)theVector.z, (float)theVector.w);
	}
	
	public void uniform4f(final int theLocation, final CCColor theColor){
		uniform4f(theLocation, (float)theColor.r, (float)theColor.g, (float)theColor.b, (float)theColor.a);
	}
	
	public void uniform4f(final String theName, final CCVector4 theValue){
		uniform4f(uniformLocation(theName), theValue);
	}
	
	public void uniform4f(final String theName, final CCColor theValue){
		uniform4f(uniformLocation(theName), theValue);
	}
	
	public void uniform4fv(final int theLocation, final List<?> theVectors){
		if(theVectors.size() == 0)return;
		
		FloatBuffer myData = FloatBuffer.allocate(theVectors.size() * 4);
		for(Object myObject:theVectors){
			if(myObject instanceof CCVector4){
				CCVector4 myVector = (CCVector4)myObject;
				myData.put((float)myVector.x);
				myData.put((float)myVector.y);
				myData.put((float)myVector.z);
				myData.put((float)myVector.w);
			}else if(myObject instanceof CCVector3){
				CCVector3 myVector = (CCVector3)myObject;
				myData.put((float)myVector.x);
				myData.put((float)myVector.y);
				myData.put((float)myVector.z);
				myData.put(0);
			}
		}
		myData.rewind();
		glProgramUniform4fv(_myID, theLocation, myData);
	}
	
	public void uniform4fv(final String theName, final List<?> theVectors){
		uniform4fv(uniformLocation(theName), theVectors);
	}
	
	
	public void uniform(final int theLocation, final CCColor theColor){
		glProgramUniform4f(_myID, theLocation, (float)theColor.red(), (float)theColor.green(), (float)theColor.blue(), (float)theColor.alpha());
	}
	
	public void uniform(final String theName, final CCColor theColor){
		uniform(uniformLocation(theName), theColor);
	}
	
	public void uniformMatrix3f(final int theLocation, FloatBuffer theMatrix) { 
		theMatrix.rewind();
		glProgramUniformMatrix3fv(_myID, theLocation, false, theMatrix);
	}
	
	public void uniformMatrix3f(final int theLocation, CCMatrix3x3 theMatrix) { 
		try ( MemoryStack stack = MemoryStack.stackPush() ) { 
		    FloatBuffer ip = stack.mallocFloat(9);
		    uniformMatrix3f(theLocation, theMatrix.toBuffer(ip));
		}
	}
	
	public void uniformMatrix3f(final String theName, CCMatrix3x3 theMatrix) {
		uniformMatrix3f(uniformLocation(theName), theMatrix);
	}
	
	public void uniformMatrix4f(final int theLocation, FloatBuffer theMatrix) { 
		theMatrix.rewind();
		glProgramUniformMatrix4fv(_myID, theLocation, false, theMatrix);
	}
	
	public void uniformMatrix4f(final String theName, FloatBuffer theMatrix) { 
		theMatrix.rewind();
		glProgramUniformMatrix4fv(_myID, uniformLocation(theName), false, theMatrix);
	}
	
	public void uniformMatrix4f(final int theLocation, float[] theMatrix) { 
		glProgramUniformMatrix4fv(_myID, theLocation, false, theMatrix);
	}
	
	public void uniformMatrix4f(final String theName, float[] theMatrix) { 
		glProgramUniformMatrix4fv(_myID, uniformLocation(theName), false, theMatrix);
	}
	
	public void uniformMatrix4f(final int theLocation, CCMatrix4x4 theMatrix) { 	
		try ( MemoryStack stack = MemoryStack.stackPush() ) { 
		    FloatBuffer fp = stack.mallocFloat(16);
			uniformMatrix4f(theLocation, theMatrix.toFloatBuffer(fp));
		}
	}
	
	public void uniformMatrix4f(final String theName, CCMatrix4x4 theMatrix) {
		uniformMatrix4f(uniformLocation(theName), theMatrix);
	}
	
	public void uniformMatrix4fv(final int theLocation, final List<CCMatrix4x4> theMatrices){
		if(theMatrices.size() == 0)return;
		
		try ( MemoryStack stack = MemoryStack.stackPush() ) { 
		    FloatBuffer fp = stack.mallocFloat(theMatrices.size() * 16);
		    for(CCMatrix4x4 myMatrix:theMatrices){
		    	if(myMatrix == null)continue;
			    FloatBuffer fpm = stack.mallocFloat( 16);
		    	fp.put(myMatrix.toFloatBuffer(fpm));
		    }
			fp.rewind();
			glProgramUniformMatrix4fv(_myID, theLocation, false, fp);
		}
	}
	
	public void uniformMatrix4fv(final String theName, final List<CCMatrix4x4> theMatrices){
		uniformMatrix4fv(uniformLocation(theName), theMatrices);
	}
	

	
	private Map<String, List<Map<String, String>>> updateMap = null;
    private boolean update = false;
    
    public void setSourceMap(Map<String, List<Map<String, String>>> theMap) {
            updateMap = theMap;
            update = true;
    }
    
    /**
     * Assemble and return map that stores all source files together with sourcecode. This Map is to be used by the Shader Controller Module.
     */
    public Map<String, List<Map<String, String>>> getSourceMap() {
            Map<String,List<Map<String, String>>> myResult = new HashMap<>();

            for(GLShaderType myType:_myShaderObjectMap.keySet()){
                    GLShaderObject myObject = _myShaderObjectMap.get(myType);

                    Path[] mySourceFiles = myObject.sourcePaths();
                    String[] mySources = myObject.sources();
    
                    List<Map<String, String>> myObjectSources = new ArrayList<>();
    
                    for(int i = 0; i < mySourceFiles.length;i++) {
                    	Path mySourceFile = mySourceFiles[i];
                    	String mySource = mySources[i];
                    	Map<String, String> myObjectSourceMap = new HashMap<>();
                    	myObjectSourceMap.put("file", mySourceFile.toString());
                    	myObjectSourceMap.put("code", mySource);
                    	myObjectSources.add(myObjectSourceMap);
                    }

                    myResult.put(myType.name(),myObjectSources);
            }
            return myResult;
    }
	
	public void updateSources () {
        if (update==false) return;
        update = false;
        
        try{
	        for(GLShaderType myType:_myShaderObjectMap.keySet()){
	                GLShaderObject myObject = _myShaderObjectMap.get(myType);
	                String src = "";
	                
	                List<Map<String,String>> sources = updateMap.get(myType.toString());
	                
	                for (Map<String, String> s: sources) {
	                        src += s.get("code");
	                }
	                myObject.source(src);
	                if(!myObject.compile()){
	        			CCLog.info(myObject.infoLog());
	        			return;
	        		}
	        }
	        link();
        }catch(Exception e){
        	e.printStackTrace();
        }
	}
}
