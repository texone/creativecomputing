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
package cc.creativecomputing.demo.graphics.font;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.font.CC3DFont;
import cc.creativecomputing.graphics.font.CCFontIO;
import cc.creativecomputing.graphics.font.text.CCText;
import cc.creativecomputing.graphics.font.text.CCTextAlign;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CC3DTextDemo extends CCGL2Adapter {
	
	private class Number{
		private CCText _myText;
		private CCVector3 _myTranslation;
		private CCVector3 _myRotationAxis;
		private float _myAngle;
		private float _myScale;
		
		public Number() {
			_myText = new CCText(_myFont);
			_myText.text("CC");
			_myText.align(CCTextAlign.CENTER);
			
			_myTranslation = new CCVector3().randomize(CCMath.random(300));
			_myRotationAxis = new CCVector3().randomize();
			_myAngle = CCMath.random(360);
			_myScale = CCMath.random(1);
		}
		
		public void update(final CCAnimator theAnimator) {
			_myAngle += theAnimator.deltaTime() * 10;
		}
		
		public void draw(CCGraphics g) {
			g.pushMatrix();
			g.translate(_myTranslation);
			g.rotate(_myAngle, _myRotationAxis);
			g.scale(_myScale);
			_myText.draw(g);
			g.popMatrix();
		}
	}
	
	private CC3DFont _myFont;
	
	private CCCameraController _myCameraController;
    
    private List<Number> _myNumbers = new ArrayList<Number>();

    @Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myFont = CCFontIO.create3DFont("Helvetica", 50, 10);
		
		_myCameraController = new CCCameraController(this, g, 100);
		
		for(int i = 0; i < 500;i++) {
			_myNumbers.add(new Number());
		}
	}

	@Override
	public void update(CCAnimator theAnimator) {
		for(Number myNumber:_myNumbers) {
			myNumber.update(theAnimator);
		}
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myCameraController.camera().draw(g);
		
		for(Number myNumber:_myNumbers) {
			myNumber.draw(g);
		}
	}

	public static void main(String[] args) {
		CC3DTextDemo demo = new CC3DTextDemo();
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1280, 720);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

