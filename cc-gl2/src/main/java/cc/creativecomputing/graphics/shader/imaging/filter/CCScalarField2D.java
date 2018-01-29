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
package cc.creativecomputing.graphics.shader.imaging.filter;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

public class CCScalarField2D {
	private CCVBOMesh _myMesh;
	private CCGLProgram _myShader;
	
	public CCScalarField2D (int w, int h) {

		_myMesh = new CCVBOMesh(CCDrawMode.TRIANGLE_STRIP, w*h*3);
		for (int x=0; x<w; x++) {
			for (int y=0; y<h; y++) {
				_myMesh.addVertex(x,y,0);
				_myMesh.addVertex(x,y+1,0);
				_myMesh.addVertex(x+1,y+1,0);
				
			}
		}
		_myShader = new CCGLProgram (CCNIOUtil.classPath(this, "shader/scalarField2Points_vp.glsl"), CCNIOUtil.classPath(this, "shader/scalarField2Points_fp.glsl"));
	}
	
	public void draw (CCGraphics g, CCTexture2D field) {
		g.texture(0,  field);	
		_myShader.start ();
		_myShader.uniform1i ("field", 0);
		_myShader.uniform1f ("scale", 1f);
		_myShader.uniform1f ("downscale", 1f);
		
		_myMesh.draw(g);
		_myShader.end();
		g.noTexture();
	}
	
		
}
