package opengl4experiments.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.jogamp.opengl.GL2ES2;
import com.jogamp.opengl.GL4;

public class ShaderLoader {
	
	//code source: https://raw.github.com/xranby/jogl-demos/master/src/demos/es2/RawGL2ES2demo.java
	public static int loadAndCompileShader(GL4 gl, String vertexShaderFileName, String fragmentShaderFileName) throws IOException {
		String vertexShaderString = readFile(vertexShaderFileName);
		String fragmentShaderString = readFile(fragmentShaderFileName);

		int vertShader = gl.glCreateShader(GL4.GL_VERTEX_SHADER);
		int fragShader = gl.glCreateShader(GL4.GL_FRAGMENT_SHADER);
		
		compileShader(gl, vertexShaderString, vertShader, true);
		compileShader(gl, fragmentShaderString, fragShader, true);
		
		int shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(shaderProgram, vertShader);
		gl.glAttachShader(shaderProgram, fragShader);
		
		gl.glLinkProgram(shaderProgram);
		
		gl.glDeleteShader(vertShader);
		gl.glDeleteShader(fragShader);
		
		return shaderProgram;
	}

	public static int loadAndCompileShader(GL4 gl, String computeShaderFileName) throws IOException {
		String computeShaderString = readFile(computeShaderFileName);

		int compShader = gl.glCreateShader(GL4.GL_COMPUTE_SHADER);
		
		compileShader(gl, computeShaderString, compShader, true);
		
		int shaderProgram = gl.glCreateProgram();
		gl.glAttachShader(shaderProgram, compShader);
		
		gl.glLinkProgram(shaderProgram);
		
		gl.glDeleteShader(compShader);
		
		return shaderProgram;
	}
	
	private static void compileShader(GL4 gl, String source, int shader, boolean checkForErrors) {
		//this is probably different if the shader is inline!
		String[] lines = new String[] {source};
		int[] lengths = new int[]{lines[0].length()};
		gl.glShaderSource(shader, lines.length, lines, lengths, 0);
		gl.glCompileShader(shader);
		
		if (checkForErrors) {
	        int[] compiled = new int[1];
	        int[] type = new int[1];
	        gl.glGetShaderiv(shader, GL4.GL_COMPILE_STATUS, compiled, 0);
	        gl.glGetShaderiv(shader, GL4.GL_SHADER_TYPE, type, 0);
	        if (compiled[0] != 0) {
	        	String shaderType = "...?";
	        	switch (type[0]) {
				case GL4.GL_VERTEX_SHADER:
					shaderType = "OpneGL vertex shade";
					break;
				case GL4.GL_FRAGMENT_SHADER:
					shaderType = "OpneGL fragment shader";
					break;
				case GL4.GL_GEOMETRY_SHADER_BIT:
					shaderType = "OpneGL geometry shader";
					break;
				case GL4.GL_TESS_CONTROL_SHADER:
					shaderType = "OpneGL tess control shader";
					break;
				case GL4.GL_TESS_EVALUATION_SHADER:
					shaderType = "OpneGL tess evaluation shader";
					break;
				case GL4.GL_COMPUTE_SHADER:
					shaderType = "OpneGL compute shader";
					break;
				
				default:
					break;
				} 
	        	System.out.println("shader type " +shaderType+ " compiled!");
	        } else {
	        	int[] logLength = new int[1];
	            gl.glGetShaderiv(shader, GL2ES2.GL_INFO_LOG_LENGTH, logLength, 0);

	            byte[] log = new byte[logLength[0]];
	            gl.glGetShaderInfoLog(shader, logLength[0], null, 0, log, 0);

	            System.err.println("Error compiling the shader type: "  + type[0] + new String(log));
	            System.exit(1);
	        }

		}
	}
	
	private static String readFile( String file ) throws IOException {
	    BufferedReader reader = new BufferedReader( new FileReader (file));
	    String         line = null;
	    StringBuilder  stringBuilder = new StringBuilder();
	    String         ls = System.getProperty("line.separator");

	    while( ( line = reader.readLine() ) != null ) {
	        stringBuilder.append( line );
	        stringBuilder.append( ls );
	    }

	    return stringBuilder.toString();
	}
}
