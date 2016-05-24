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
package cc.creativecomputing.demo.gl2.fractal;


import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCTriangle2;
import cc.creativecomputing.math.CCVector2;

public class CCMosaicTriangleMeshDemo extends CCGL2Adapter {
	
	@CCProperty(name = "mosaic")
	private CCMosaicTriangleMesh _myParticleTriangleMesh;
	
	@CCProperty(name = "camera1")
	private CCCameraController _myCameraController1;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {

		
		_myCameraController1 = new CCCameraController(this, g, 100);
		g.clearColor(0.2f, 0.2f, 0.2f);
		
		int myColumns = 10;
		
		double myEdgelength = g.width() / (double)myColumns;
		double myEdgeScale = myEdgelength / CCMath.SQRT3;
		double myTriangleHeight = myEdgelength / 2 * CCMath.SQRT3;
		double _myShortCenter = myEdgelength / 2 * CCMath.tan(CCMath.radians(30));
		double myLongCenter = myTriangleHeight - _myShortCenter;
		
		List<CCTriangle2> myTriangles = new ArrayList<CCTriangle2>();
		
		int myRows = CCMath.ceil(g.height() / myTriangleHeight);
		
		for(int myColumn = 0; myColumn < 10; myColumn++) {
			for(int myRow = 0; myRow < myRows;myRow++) {
				CCVector2 myOrigin0;
				CCVector2 myOrigin1;
				CCVector2 myOrigin2;

				myOrigin0 = new CCVector2(CCMath.cos(CCMath.radians( -30)), CCMath.sin(CCMath.radians( -30))).multiplyLocal(myEdgeScale);
				myOrigin1 = new CCVector2(CCMath.cos(CCMath.radians(-150)), CCMath.sin(CCMath.radians(-150))).multiplyLocal(myEdgeScale);
				myOrigin2 = new CCVector2(CCMath.cos(CCMath.radians(-270)), CCMath.sin(CCMath.radians(-270))).multiplyLocal(myEdgeScale);
					
				CCVector2 myTranslation = new CCVector2(
					(myColumn + 0.5f) * myEdgelength,
					myRow * myTriangleHeight + _myShortCenter
				);
				
				myTriangles.add(new CCTriangle2(
					myOrigin0.addLocal(myTranslation),
					myOrigin1.addLocal(myTranslation),
					myOrigin2.addLocal(myTranslation)
				));
				
				myOrigin0 = new CCVector2(CCMath.cos(CCMath.radians(-210)), CCMath.sin(CCMath.radians(-210))).multiplyLocal(myEdgeScale);
				myOrigin1 = new CCVector2(CCMath.cos(CCMath.radians(-330)), CCMath.sin(CCMath.radians(-330))).multiplyLocal(myEdgeScale);
				myOrigin2 = new CCVector2(CCMath.cos(CCMath.radians( -90)), CCMath.sin(CCMath.radians( -90))).multiplyLocal(myEdgeScale);
					
				myTranslation = new CCVector2(
					(myColumn + 1) * myEdgelength,
					myRow * myTriangleHeight + myLongCenter
				);
				
				myTriangles.add(new CCTriangle2(
					myOrigin0.add(myTranslation),
					myOrigin1.add(myTranslation),
					myOrigin2.add(myTranslation)
				));
			}
		}
		
		_myParticleTriangleMesh = new CCMosaicTriangleMesh(g, myTriangles, 6);
		_myParticleTriangleMesh.textureSize(g.width(), g.height());

		_myParticleTriangleMesh.texture0(new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/09_FlightInfo_b.png"))));
		_myParticleTriangleMesh.texture1(new CCTexture2D(CCImageIO.newImage(CCNIOUtil.dataPath("textures/10_Wolken.jpg"))));
	}

	@Override
	public void update(CCAnimator theAnimator) {
		
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clearColor(40, 40, 40);
		g.clear();
		
		g.pushMatrix();
		_myCameraController1.camera().draw(g);
				
		g.color(255,0,0);
			
	
		g.translate(-g.width()/2, -g.height()/2);

		// draw the "real" triangles with its full volume
		g.blend();
//		_myTriangleManager.draw(g);
		_myParticleTriangleMesh.draw(g);

		g.popMatrix();
		g.color(255);
//		g.image(_myTriangleManager.forceField().texture(), -_myVisual.heightMap().width()/2,-_myVisual.heightMap().height()/2);
//		g.image(_myParticleTriangleMesh.forceBlendTexture(),0,0);
		g.blend();
//		CCScreenCapture.capture("export/disney/fract"+CCFormatUtil.nf(frameCount, 4)+".png",width,height);
//		g.image(_myTextureVisual.renderTexture(), 0,0);
	}
	
	public static void main(String[] args) {
		CCMosaicTriangleMeshDemo demo = new CCMosaicTriangleMeshDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(800, 300);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
