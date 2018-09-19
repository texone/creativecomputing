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
package cc.creativecomputing.gl2;

import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShaderDemo extends CCGLApp {
	
	@CCProperty(name = "shader")
	private CCGLProgram _cProgram;

	@Override
	public void setup() {
		_cProgram = new CCGLProgram(
			CCNIOUtil.classPath(this, "CCShaderDemo_vertex.glsl"), 
			CCNIOUtil.classPath(this, "CCShaderDemo_fragment.glsl")
		);
	}

	public void display(CCGraphics g) {
		g.clearColor(0.2, 0.3, 0.3, 1.0);
		g.clear();
		
		_cProgram.start();
		g.beginShape(CCDrawMode.QUADS);
		g.textureCoords2D(0, 0);
		g.vertex(-g.width() / 3, -g.height() / 3);
		g.textureCoords2D(1, 0);
		g.vertex( g.width() / 3, -g.height() / 3);
		g.textureCoords2D(1, 1);
		g.vertex( g.width() / 3,  g.height() / 3);
		g.textureCoords2D(0, 1);
		g.vertex(-g.width() / 3,  g.height() / 3);
		g.endShape();
		_cProgram.end();
		
		//_cProgram.start();
	}
	
	public static void main(String[] args) {
		CCShaderDemo demo = new CCShaderDemo();
		
		CCGLApplicationManager myAppManager = new CCGLApplicationManager(demo);
		CCControlApp _myControls = new CCControlApp(myAppManager, demo);
		myAppManager.run();
	}
}
