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
package cc.creativecomputing.demo.simulation.gpuparticles.nearest;

import java.nio.FloatBuffer;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.CCVBOMesh;
import cc.creativecomputing.graphics.shader.CCGLSLShader;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.util.CCArcball;

public class CCGPUNearestNeighbourDemo extends CCApp {
	
	private CCShaderBuffer _myData;
	private CCShaderBuffer _myConnectionData;
	private CCShaderBuffer _myConnectionDataTmp;
	private CCMesh _myMesh;
	private CCMesh _mySortMesh;
	
	private int xRes = 10;
	private int yRes = 10;
	
	private CCGLSLShader _myDrawShader;
	private CCGLSLShader _mySortShader;
	
	private CCArcball _myArcball;

	@Override
	public void setup() {
		_myData = new CCShaderBuffer(32,4,xRes, yRes);
		FloatBuffer myFloatBuffer = FloatBuffer.allocate(xRes * yRes * 4);
		for(int i = 0;i < xRes * yRes;i++) {
			myFloatBuffer.put(CCMath.random(-300,300));
			myFloatBuffer.put(CCMath.random(-300,300));
			myFloatBuffer.put(CCMath.random(-300,300));
			myFloatBuffer.put(1f);
		}
		myFloatBuffer.rewind();
		_myData.loadData(myFloatBuffer);
		
		_myConnectionData = new CCShaderBuffer(32,4,xRes * 6, yRes);
		myFloatBuffer = FloatBuffer.allocate(xRes * 6 * yRes * 4);
		for(int x = 0;x < xRes;x++) {
			for(int y = 0;y < yRes; y++) {
				myFloatBuffer.put(x);
				myFloatBuffer.put(y);
				myFloatBuffer.put(0);
				myFloatBuffer.put(0);
				for(int i = 0; i < 5;i++) {
					myFloatBuffer.put((int)CCMath.random(xRes));
					myFloatBuffer.put((int)CCMath.random(yRes));
					myFloatBuffer.put(0);
					myFloatBuffer.put(0);
				}
			}
		}
		myFloatBuffer.rewind();
		_myConnectionData.loadData(myFloatBuffer);
		_myConnectionDataTmp = new CCShaderBuffer(32,4,xRes * 6, yRes);
		
		_myDrawShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "nearest_draw_vert.glsl"),
			CCIOUtil.classPath(this, "nearest_draw_frag.glsl")
		);
		_myDrawShader.load();
		
		_myMesh = new CCVBOMesh(CCDrawMode.LINES, xRes * yRes * 10);
		for(int x = 0;x < xRes;x++) {
			for(int y = 0;y < yRes; y++) {
				for(int i = 0; i < 5;i++) {
					_myMesh.addVertex(x * 6, y);
					_myMesh.addVertex(x * 6 + 1 + i, y);
				}
			}
		}
		
		_mySortShader = new CCGLSLShader(
			CCIOUtil.classPath(this, "nearest_sort_vert.glsl"),
			CCIOUtil.classPath(this, "nearest_sort_frag.glsl")
		);
		_mySortShader.load();
		
		_mySortMesh = new CCVBOMesh(CCDrawMode.POINTS, xRes * yRes * 6);
		for(int x = 0;x < xRes;x++) {
			for(int y = 0;y < yRes; y++) {
				for(int i = 0; i < 6;i++) {
					_mySortMesh.addVertex(x * 6 + i, y, x * 6, y);
				}
			}
		}
		
		_myArcball = new CCArcball(this);
	}
	
	boolean _myDoUpdate = false;

	@Override
	public void update(final float theDeltaTime) {
		if(!_myDoUpdate)return;
		_myDoUpdate = false;
		g.noBlend();
		_myConnectionDataTmp.beginDraw();
		g.clear();
		_mySortShader.start();
		g.texture(0,_myData.attachment(0));
		g.texture(1,_myConnectionData.attachment(0));
		_mySortShader.uniform1i("data",0);
		_mySortShader.uniform1i("connectionData",1);
		_mySortMesh.draw(g);
		_mySortShader.end();
		g.noTexture();
		_myConnectionDataTmp.endDraw();
		
		CCShaderBuffer myKeep = _myConnectionDataTmp;
		_myConnectionDataTmp = _myConnectionData;
		_myConnectionData = myKeep;
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		g.blend();
		g.noDepthTest();
		g.texture(0,_myData.attachment(0));
		g.texture(1,_myConnectionData.attachment(0));
		_myDrawShader.start();
		_myDrawShader.uniform1i("data",0);
		_myDrawShader.uniform1i("connectionData",1);
		_myMesh.draw(g);
		_myDrawShader.end();
		g.noTexture();
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.CCApp#keyPressed(cc.creativecomputing.events.CCKeyEvent)
	 */
	@Override
	public void keyPressed(CCKeyEvent theKeyEvent) {
		_myDoUpdate = true;
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCGPUNearestNeighbourDemo.class);
		myManager.settings().size(1400, 900);
		myManager.start();
	}
}

