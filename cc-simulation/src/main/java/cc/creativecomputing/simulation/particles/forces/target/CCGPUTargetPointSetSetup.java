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
package cc.creativecomputing.simulation.particles.forces.target;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.math.CCVector4f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;

/**
 * Form that sets the particle targets to create a texture
 * @author info
 *
 */
public class CCGPUTargetPointSetSetup implements CCGPUTargetSetup{
	
	private List<CCVector4f> _myPointSet = new ArrayList<CCVector4f>();
	
	private boolean _myKeepTargets;
	
	private float[] _myTargets;
	
	
	public CCGPUTargetPointSetSetup(){
	}
	
	public void keepTargets(final boolean theIsKeepingTargets){
		_myKeepTargets = theIsKeepingTargets;
	}
	
	public CCVector3f target(final int theParticleID) {
		return new CCVector3f(
			_myTargets[theParticleID * 3],
			_myTargets[theParticleID * 3 + 1],
			_myTargets[theParticleID * 3 + 2]
		);
	}
	
	public List<CCVector4f> points(){
		return _myPointSet;
	}

	public void setParticleTargets(final CCGraphics g, int theX, int theY, final int theWidth, final int theHeight) {
		if(_myKeepTargets){
			_myTargets = new float[theWidth * theHeight * 3];
		}
		
		if(_myPointSet.size() == 0)return;
		
		for(int x = theX; x < theX + theWidth;x++) {
			for(int y = theY; y < theY + theHeight;y++) {
				CCVector4f myPoint = _myPointSet.get((int)CCMath.random(_myPointSet.size()));
				int id = y * theWidth + x;
				
				if(_myKeepTargets) {
					_myTargets[id * 3 + 0] = myPoint.x;
					_myTargets[id * 3 + 1] = myPoint.y;
					_myTargets[id * 3 + 2] = myPoint.z;
				}
				g.textureCoords(0, myPoint.x, myPoint.y, myPoint.z, myPoint.w);
				g.vertex(x + 0.5f, y + 0.5f);
			}
		}
	}
	
	@Override
	public void setParticleTargets(CCGraphics g, CCGPUIndexParticleEmitter theGroup) {
		if(_myKeepTargets){
			_myTargets = new float[theGroup.numberOfParticles() * 3];
		}
		
		if(_myPointSet.size() == 0)return;
		
		for(int i = 0; i < theGroup.size(); i++) {
			CCVector4f myPoint = _myPointSet.get((int)CCMath.random(_myPointSet.size()));
				
			if(_myKeepTargets) {
				_myTargets[i * 3 + 0] = myPoint.x;
				_myTargets[i * 3 + 1] = myPoint.y;
				_myTargets[i * 3 + 2] = myPoint.z;
			}
			
			
			g.textureCoords(0, myPoint.x, myPoint.y, myPoint.z, myPoint.w);
			g.vertex(theGroup.xforIndex(i) + 0.5f, theGroup.yforIndex(i) + 0.5f);
		}
		
	}
}

