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
package cc.creativecomputing.demo.simulation.gpuparticles.emit;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.input.touch.CCTouch;
import cc.creativecomputing.input.touch.CCTouchManager;
import cc.creativecomputing.input.touch.CCTouchManager.CCTouchImplementations;

public class CCTouchDrawDemo extends CCApp {
	
	private CCTouchManager _myTouchManager;

	@Override
	public void setup() {
		_myTouchManager = CCTouchManager.createTouchManager(CCTouchImplementations.OSX_TRACKPAD);
	}

	@Override
	public void update(final float theDeltaTime) {}

	@Override
	public void draw() {
		g.clear();
		g.ortho2D();
		for (CCTouch myTouch : _myTouchManager) {
			int x = (int) (width * (myTouch.position().x));
			int y = (int) (height * (myTouch.position().y));
			int xsize = (int) (10 * myTouch.size() * (myTouch.majorAxis() / 2));
			int ysize = (int) (10 * myTouch.size() * (myTouch.minorAxis() / 2));
//			int ang = myTouch.getAngle();

			// AffineTransform at = AffineTransform.getTranslateInstance(0,0);
			// at.translate(x-xsize/2, y-ysize/2);
			// at.rotate((Math.PI/180)*-ang, xsize/2, ysize/2); // convert degrees to radians
			g.pushMatrix();
			g.translate(x,y);
			g.rotate(myTouch.getAngle());
			g.color(255, 0, 0);
			g.ellipse(0, 0, xsize, ysize);
			g.popMatrix();

			g.color(255);
		}
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCTouchDrawDemo.class);
		myManager.settings().size(500, 500);
		myManager.start();
	}
}

