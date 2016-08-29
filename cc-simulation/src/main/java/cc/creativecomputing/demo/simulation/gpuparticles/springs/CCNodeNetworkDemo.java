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
package cc.creativecomputing.demo.simulation.gpuparticles.springs;

import cc.creativecomputing.CCApp;
import cc.creativecomputing.CCApplicationManager;
import cc.creativecomputing.math.util.CCArcball;
import cc.creativecomputing.util.CCStopWatch;

public class CCNodeNetworkDemo extends CCApp {
	
	private CCArcball _myArcball;
	private CCNodeNetwork _myNodeNetwork;

	@Override
	public void setup() {
		_myArcball = new CCArcball(this);
		_myNodeNetwork = new CCNodeNetwork(this);
		
		fixUpdateTime(1/30f);
		
		addControls("stopwatch", "stopwatch", CCStopWatch.instance());
	}

	@Override
	public void update(final float theDeltaTime) {
		_myNodeNetwork.update(theDeltaTime);
		
		CCStopWatch.instance().update(theDeltaTime);
	}

	@Override
	public void draw() {
		g.clear();
		_myArcball.draw(g);
		_myNodeNetwork.draw(g);
		
		CCStopWatch.instance().draw(g);
		System.out.println(frameRate);
	}

	public static void main(String[] args) {
		CCApplicationManager myManager = new CCApplicationManager(CCNodeNetworkDemo.class);
		myManager.settings().size(1500, 800);
		myManager.settings().antialiasing(8);
		myManager.start();
	}
}

