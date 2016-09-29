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

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;

/**
 * @author info
 *
 */
public class CCGPUTargetRectSetup implements CCGPUTargetSetup{
	
	private float _myXSpace;
	private float _myYSpace;
	
	private CCVector3f _myTranslation;
	
	public CCGPUTargetRectSetup(final float theSpace) {
		this(theSpace, theSpace);
	}
	
	public CCGPUTargetRectSetup(final float theXspace, final float theYspace) {
		_myXSpace = theXspace;
		_myYSpace = theYspace;
		_myTranslation = new CCVector3f();
	}
	
	public CCVector3f translation() {
		return _myTranslation;
	}
	
	public void setParticleTargets(final CCGraphics g, int theX, int theY, final int theWidth, final int theHeight) {
		
		float myXtranspose = -_myXSpace * theWidth / 2;
		float myYtranspose = +_myYSpace * theHeight / 2;
		
		for(int x = theX; x < theX + theWidth;x++) {
			for(int y = theY; y < theY + theHeight;y++) {
				float xPos =  + x * _myXSpace + myXtranspose + _myTranslation.x;
				float yPos =  - y * _myYSpace + myYtranspose + _myTranslation.y;
				float zPos = 0 + _myTranslation.z;
					
				g.textureCoords(0, xPos , yPos, zPos);
				g.vertex(x + 0.5f, y + 0.5f);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.simulation.gpuparticles.forces.target.CCGPUTargetSetup#setParticleTargets(cc.creativecomputing.graphics.CCGraphics, cc.creativecomputing.simulation.gpuparticles.CCGPUParticleGroup)
	 */
	@Override
	public void setParticleTargets(CCGraphics theGraphics, CCGPUIndexParticleEmitter theGroup) {
//		float myXtranspose = -_myXSpace * theWidth / 2;
//		float myYtranspose = +_myYSpace * theHeight / 2;
//		
//		for (int i = 0; i < theGroup.numberOfParticles(); i++) {
//			float xPos = +x * _myXSpace + myXtranspose + _myTranslation.x;
//			float yPos = -y * _myYSpace + myYtranspose + _myTranslation.y;
//			float zPos = 0 + _myTranslation.z;
//
//			g.textureCoords(0, xPos, yPos, zPos);
//			g.vertex(x + 0.5f, y + 0.5f);
//
//		}
	}
}
