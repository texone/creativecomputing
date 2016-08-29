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
package cc.creativecomputing.simulation.particles.forces;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCGPUUpdateShader;

public class CCGPUNeighborhoodForce extends CCGPUTextureForceField{
	
	private CCRenderBuffer _myDensityMap;
	
	private CCGraphics _myGraphics;
	private int _myWidth;
	private int _myHeight;

	public CCGPUNeighborhoodForce(CCGraphics theGraphcis, int theWidth, int theHeight) {
		super(null, null, null);
	}

	@Override
	public void setShader(CCParticles theParticles, CCGPUUpdateShader theShader, int theIndex, int theWidth, int theHeight) {
		super.setShader(theParticles, theShader, theIndex, theWidth, theHeight);
		
		_myParticles = theParticles;
		_myDensityMap = new CCRenderBuffer(_myGraphics, _myWidth, _myHeight);
		_myTexture = _myDensityMap.attachment(0);
	}
	
	@Override
	public void update(float theDeltaTime) {
		super.update(theDeltaTime);
		
		_myDensityMap.beginDraw();
		
		_myDensityMap.endDraw();
	}
}
