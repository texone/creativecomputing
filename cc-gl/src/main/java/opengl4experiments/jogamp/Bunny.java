/*
 * Copyright (c) 2003 Sun Microsystems, Inc. All Rights Reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 * 
 * - Redistribution of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * 
 * - Redistribution in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * 
 * Neither the name of Sun Microsystems, Inc. or the names of
 * contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
 * INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN
 * MICROSYSTEMS, INC. ("SUN") AND ITS LICENSORS SHALL NOT BE LIABLE FOR
 * ANY DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR
 * DISTRIBUTING THIS SOFTWARE OR ITS DERIVATIVES. IN NO EVENT WILL SUN OR
 * ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR FOR
 * DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE
 * DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY OF LIABILITY,
 * ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF
 * SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 * 
 * You acknowledge that this software is not designed or intended for use
 * in the design, construction, operation or maintenance of any nuclear
 * facility.
 * 
 * Sun gratefully acknowledges that this software was originally authored
 * and developed by Kenneth Bradley Russell and Christopher John Kline.
 */

package opengl4experiments.jogamp;

import static cc.creativecomputing.gl4.GLBufferUtil.asBuffer;
import static cc.creativecomputing.gl4.GLBufferUtil.sizeof;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.StreamTokenizer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

import opengl4experiments.utils.ShaderLoader;

import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL4;

/**
 * Renders a bunny.
 * 
 * <P>
 * This file was produced by 3D Exploration Plugin: CPP Export filter.
 * 
 * <P>
 * 3D Exploration
 * 
 * <P>
 * Copyright (c) 1999-2000 X Dimension Software
 * 
 * <P>
 * WWW http://www.xdsoft.com/explorer/ <BR>
 * eMail info@xdsoft.com
 */
public class Bunny {
	
	private IntBuffer vao = IntBuffer.allocate(1);
	private IntBuffer vbo = IntBuffer.allocate(2);
	private IntBuffer ebo = IntBuffer.allocate(1);
	private int render_prog;
	private int faceCount;
	private ShortBuffer indexBuffer;
	private FloatBuffer vertexBuffer, normalBuffer;
	

	public void draw(GL4 gl) {
		gl.glBindVertexArray(vao.get(0));
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
		gl.glEnable(GL.GL_CULL_FACE);
		gl.glDrawElements(GL.GL_TRIANGLES, faceCount * 3, GL.GL_UNSIGNED_SHORT, 0);
	}
	
	public void load() throws IOException {
		/*
		StreamTokenizer tok = new StreamTokenizer(new BufferedReader(
				new InputStreamReader(Bunny.class.getClassLoader()
						.getResourceAsStream("data/models/bunny.txt"))));
						*/
		StreamTokenizer tok = new StreamTokenizer(new BufferedReader(
				new FileReader("data/models/bunny.txt")));
		// Reset tokenizer's syntax so numbers are not parsed
		tok.resetSyntax();
		tok.wordChars('a', 'z');
		tok.wordChars('A', 'Z');
		tok.wordChars('0', '9');
		tok.wordChars('-', '-');
		tok.wordChars('.', '.');
		tok.wordChars(128 + 32, 255);
		tok.whitespaceChars(0, ' ');
		tok.whitespaceChars(',', ',');
		tok.whitespaceChars('{', '{');
		tok.whitespaceChars('}', '}');
		tok.commentChar('/');
		tok.quoteChar('"');
		tok.quoteChar('\'');
		tok.slashSlashComments(true);
		tok.slashStarComments(true);

		// Read in file
		int numFaceIndices = nextInt(tok, "number of face indices");
		faceCount = numFaceIndices;
		short[] faceVertexIndices = new short[numFaceIndices * 3];
		short[] faceNormalIndices = new short[numFaceIndices * 3];
		
		for (int i = 0; i < numFaceIndices; i++) {
			for (int j = 0; j < 3; j++) {				
				faceVertexIndices[i*3 + j] = (short) nextInt(tok, "face vertex index");
//				System.out.println("face vertex no: " + (i*3 + j) + " : " + faceVertexIndices[i*3 + j]);
			}
			for (int j = 0; j < 3; j++) {				
				faceNormalIndices[i*3 + j] = (short) nextInt(tok, "face normal index");
//				System.out.println("face normal no: " + (i*3 + j) + " : " + faceNormalIndices[i*3 + j]);
			}
		}
		
		int numVertices = nextInt(tok, "number of vertices");
		float[] vertices = new float[numVertices * 3];
		for (int i = 0; i < numVertices * 3; i++) {
			vertices[i] = nextFloat(tok, "vertex");
//			System.out.println(vertices[i]);
		}
		
		int numNormals = nextInt(tok, "number of normals");
		float[] normalsTemp = new float[numNormals * 3];
		
		for (int i = 0; i < numNormals * 3; i++) {
			normalsTemp[i] = nextFloat(tok, "normal");
//			System.out.println(normalsTemp[i]);
		}
		
		
		float[] normals = new float[vertices.length];
		for (int i = 0; i < faceVertexIndices.length; i++) {
			for (int j = 0; j < 3; j++) {
				normals[faceVertexIndices[i]*3 + j] = normalsTemp[faceNormalIndices[i]*3 + j];
			}
		}

		
		indexBuffer = Buffers.newDirectShortBuffer(faceVertexIndices);
		vertexBuffer = Buffers.newDirectFloatBuffer(vertices);
		normalBuffer = Buffers.newDirectFloatBuffer(normals);
	}
	
	public void load(GL4 gl) throws IOException {
		/*
		StreamTokenizer tok = new StreamTokenizer(new BufferedReader(
				new InputStreamReader(Bunny.class.getClassLoader()
						.getResourceAsStream("data/models/bunny.txt"))));
						*/
		StreamTokenizer tok = new StreamTokenizer(new BufferedReader(
				new FileReader("data/models/bunny.txt")));
		// Reset tokenizer's syntax so numbers are not parsed
		tok.resetSyntax();
		tok.wordChars('a', 'z');
		tok.wordChars('A', 'Z');
		tok.wordChars('0', '9');
		tok.wordChars('-', '-');
		tok.wordChars('.', '.');
		tok.wordChars(128 + 32, 255);
		tok.whitespaceChars(0, ' ');
		tok.whitespaceChars(',', ',');
		tok.whitespaceChars('{', '{');
		tok.whitespaceChars('}', '}');
		tok.commentChar('/');
		tok.quoteChar('"');
		tok.quoteChar('\'');
		tok.slashSlashComments(true);
		tok.slashStarComments(true);

		// Read in file
		int numFaceIndices = nextInt(tok, "number of face indices");
		faceCount = numFaceIndices;
		short[] faceVertexIndices = new short[numFaceIndices * 3];
		short[] faceNormalIndices = new short[numFaceIndices * 3];
		
		for (int i = 0; i < numFaceIndices; i++) {
			for (int j = 0; j < 3; j++) {				
				faceVertexIndices[i*3 + j] = (short) nextInt(tok, "face vertex index");
//				System.out.println("face vertex no: " + (i*3 + j) + " : " + faceVertexIndices[i*3 + j]);
			}
			for (int j = 0; j < 3; j++) {				
				faceNormalIndices[i*3 + j] = (short) nextInt(tok, "face normal index");
//				System.out.println("face normal no: " + (i*3 + j) + " : " + faceNormalIndices[i*3 + j]);
			}
		}
		
		int numVertices = nextInt(tok, "number of vertices");
		float[] vertices = new float[numVertices * 3];
		for (int i = 0; i < numVertices * 3; i++) {
			vertices[i] = nextFloat(tok, "vertex");
//			System.out.println(vertices[i]);
		}
		
		int numNormals = nextInt(tok, "number of normals");
		float[] normalsTemp = new float[numNormals * 3];
		
		for (int i = 0; i < numNormals * 3; i++) {
			normalsTemp[i] = nextFloat(tok, "normal");
//			System.out.println(normalsTemp[i]);
		}
		
		
		float[] normals = new float[vertices.length];
		for (int i = 0; i < faceVertexIndices.length; i++) {
			for (int j = 0; j < 3; j++) {
				normals[faceVertexIndices[i]*3 + j] = normalsTemp[faceNormalIndices[i]*3 + j];
			}
		}

		
		/*
		for (int i = 0; i < faceVertexIndices.length; i++) {
			System.out.println("index : " + faceVertexIndices[i]);
			System.out.println("vert : " + vertices[faceVertexIndices[i]*3] + " : " + vertices[faceVertexIndices[i]*3+1] + " : " + vertices[faceVertexIndices[i]*3+2]);
			System.out.println("norm : " + normals[faceVertexIndices[i]*3] + " : " + normals[faceVertexIndices[i]*3+1] + " : "  +  normals[faceVertexIndices[i]*3+2]);
			System.out.println("----");
		}
		*/

		try {
			render_prog = ShaderLoader.loadAndCompileShader(gl, "shader/bunny.vert", "shader/bunny.frag");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		gl.glUseProgram(render_prog);
				
		gl.glGenBuffers(1, ebo);
		gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, ebo.get(0));
		gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, sizeof(faceVertexIndices), asBuffer(faceVertexIndices), GL.GL_STATIC_DRAW);
		
	    int vertex_position_loc = gl.glGetAttribLocation(render_prog, "vertex_position");
	    int vertex_normal_loc = gl.glGetAttribLocation(render_prog, "vertex_normal");
		
		gl.glGenVertexArrays(1, vao);
		gl.glBindVertexArray(vao.get(0));
		
		gl.glGenBuffers(1,vbo);
		gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vbo.get(0));
//		gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(vertices), asBuffer(vertices), GL.GL_STATIC_DRAW);
		gl.glBufferData(GL.GL_ARRAY_BUFFER, sizeof(vertices) + sizeof(normals), null, GL.GL_STATIC_DRAW);
		gl.glBufferSubData(GL.GL_ARRAY_BUFFER, 0, sizeof(vertices), asBuffer(vertices));
		gl.glBufferSubData(GL.GL_ARRAY_BUFFER, sizeof(vertices), sizeof(normals), asBuffer(normals));
	    gl.glVertexAttribPointer(vertex_position_loc, 3, GL.GL_FLOAT, false, 0, 0);
	    gl.glVertexAttribPointer(vertex_normal_loc, 3, GL.GL_FLOAT, false, 0, sizeof(vertices));
		
	    gl.glEnableVertexAttribArray(vertex_position_loc);
	    gl.glEnableVertexAttribArray(vertex_normal_loc);
		
	}

	private static int nextInt(StreamTokenizer tok, String error)
			throws IOException {
		if (tok.nextToken() != StreamTokenizer.TT_WORD) {
			throw new IOException("Parse error reading " + error + " at line "
					+ tok.lineno());
		}
		try {
			return Integer.parseInt(tok.sval);
		} catch (NumberFormatException e) {
			throw new IOException("Parse error reading " + error + " at line "
					+ tok.lineno());
		}
	}

	private static float nextFloat(StreamTokenizer tok, String error)
			throws IOException {
		if (tok.nextToken() != StreamTokenizer.TT_WORD) {
			throw new IOException("Parse error reading " + error + " at line "
					+ tok.lineno());
		}
		try {
			return Float.parseFloat(tok.sval);
		} catch (NumberFormatException e) {
			throw new IOException("Parse error reading " + error + " at line "
					+ tok.lineno());
		}
	}
}
