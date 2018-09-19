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
package learningopengl.chap01gettingstartet;

import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;
import cc.creativecomputing.gl4.GLDataType;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLProgram;
import cc.creativecomputing.gl4.GLVertexArray;
import cc.creativecomputing.graphics.CCGraphics;
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

public class GL02HelloTriangle extends CCGLApp {

	GLProgram shaderProgram;
	GLVertexArray VAO;
	@Override
	public void setup() {
		
		// build and compile our shader program
	    // ------------------------------------
	  
	    // link shaders
		shaderProgram = new GLProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this, "01.vs")), 
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this, "01.fs"))
		);

	    // set up vertex data (and buffer(s)) and configure vertex attributes
	    // ------------------------------------------------------------------
	
		VAO = new GLVertexArray();
		GLBuffer VBO = new GLBuffer(GLBufferTarget.ARRAY);
		GLBuffer EBO = new GLBuffer(GLBufferTarget.ELEMENT_ARRAY);
	    // bind the Vertex Array Object first, then bind and set vertex buffer(s), and then configure vertex attributes(s).
		VAO.bind();

		VBO.bind();
		VBO.data(
			 0.5f,  0.5f, 0.0f,  // top right
			 0.5f, -0.5f, 0.0f,  // bottom right
			-0.5f, -0.5f, 0.0f,  // bottom left
			-0.5f,  0.5f, 0.0f   // top left 
		);

	    VAO.attributes(0, 3, GLDataType.FLOAT, false, 3 * 4, 0);
	    VAO.enableVertexAttribArray(0);
	    // note that this is allowed, the call to glVertexAttribPointer 
	    // registered VBO as the vertex attribute's bound vertex buffer 
	    // object so afterwards we can safely unbind
	    VBO.unbind(); 
	    VAO.unbind();

	    VAO.bind();
	    EBO.bind();
	    EBO.data(// note that we start from 0!
			0, 1, 3,  // first Triangle
			1, 2, 3   // second Triangle
		);

	    

	    // remember: do NOT unbind the EBO while a VAO is active as the bound element buffer object IS stored in the VAO; keep the EBO bound.
	    //glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);

	    // You can unbind the VAO afterwards so other VAO calls won't accidentally modify this VAO, but this rarely happens. Modifying other
	    // VAOs requires a call to glBindVertexArray anyways so we generally don't unbind VAOs (nor VBOs) when it's not directly necessary.
	    VAO.unbind();

	    EBO.unbind();
	}

	public void display(CCGraphics g) {
		g.clearColor(0.2, 0.3, 0.3, 1.0);
		g.clear();

        // draw our first triangle
		shaderProgram.use();
		VAO.bind(); // seeing as we only have a single VAO there's no need to bind it every time, but we'll do so to keep things a bit more organized
		VAO.drawElements(GLDrawMode.TRIANGLES, 6, GLDataType.UNSIGNED_INT);
		shaderProgram.end();
		
		g.clearDepthBuffer();
		g.color(255);
		g.rect(0, 0, 100, 100);
	}
	
	public static void main(String[] args) {
		GL02HelloTriangle demo = new GL02HelloTriangle();
		
		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		myAppManager.run();
	}
}
