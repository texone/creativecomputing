/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.learn.chap01gettingstartet;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl4.CCGLMesh;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;
import cc.creativecomputing.gl4.GLDataType;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLTexture2D;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLProgram;
import cc.creativecomputing.gl4.GLVertexArray;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL13.*;
import static org.lwjgl.opengl.GL14.*;
import static org.lwjgl.opengl.GL15.*;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL21.*;

import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.opengl.GL31.*;
import static org.lwjgl.opengl.GL32.*;
import static org.lwjgl.opengl.GL33.*;

import static org.lwjgl.opengl.GL40.*;
import static org.lwjgl.opengl.GL41.*;
import static org.lwjgl.opengl.GL42.*;
import static org.lwjgl.opengl.GL43.*;
import static org.lwjgl.opengl.GL44.*;
import static org.lwjgl.opengl.GL45.*;
import static org.lwjgl.opengl.GL46.*;

public class CC0401Textures extends CCGLApp {

	GLTexture2D texture;
	GLProgram shaderProgram;
	CCGLMesh _myMesh;
	
	@Override
	public void setup() {
		
		// build and compile our shader program
	    // ------------------------------------
	  
	    // link shaders
		shaderProgram = new GLProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this, "041.vs")), 
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this, "041.fs"))
		);

	    // set up vertex data (and buffer(s)) and configure vertex attributes
	    // ------------------------------------------------------------------
		_myMesh = new CCGLMesh();
		// positions
		_myMesh.data(0,3,
			 0.5f,  0.5f, 0.0f,  // top right
			 0.5f, -0.5f, 0.0f,  // bottom right
			-0.5f, -0.5f, 0.0f,  // bottom left
			-0.5f,  0.5f, 0.0f   // top left 
		);
		// colors
		_myMesh.data(1,3,
			1.0f, 0.0f, 0.0f,   // top right
			0.0f, 1.0f, 0.0f,   // bottom right
			0.0f, 0.0f, 1.0f,   // bottom left
			1.0f, 1.0f, 0.0f   // top left 
		);
		// texture coords
		_myMesh.data(2,2,
			1.0f, 1.0f, // top right
			1.0f, 0.0f, // bottom right
			0.0f, 0.0f, // bottom left
			0.0f, 1.0f  // top left 
		);
	    _myMesh.indices(// note that we start from 0!
			0, 1, 3,  // first Triangle
			1, 2, 3   // second Triangle
		);
	    
	    texture = new GLTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/container.jpg")));
	    texture.generateMipmaps(true);
	}

	public void display(CCGraphics g) {
		g.clearColor(0.2, 0.3, 0.3, 1.0);
		g.clear();
		
		texture.bind();

        // draw our first triangle
		shaderProgram.use();
		_myMesh.draw();
		shaderProgram.end();
		
		g.clearDepthBuffer();
		g.color(255);
		g.rect(0, 0, 100, 100);
	}
	
	public static void main(String[] args) {
		CC0401Textures demo = new CC0401Textures();
		
		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		myAppManager.run();
	}
}
