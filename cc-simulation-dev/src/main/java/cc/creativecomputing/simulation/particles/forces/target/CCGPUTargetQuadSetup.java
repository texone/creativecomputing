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
import cc.creativecomputing.math.util.CCQuad3f;
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;

/**
 * @author info
 *
 */
public class CCGPUTargetQuadSetup implements CCGPUTargetSetup{
	
	public static enum CCGPUTargetQuadCreationMode{
		RANDOM,
		GRID
	}
	
	private CCQuad3f _myQuad;
	private CCGPUTargetQuadCreationMode _myMode;
	
	public CCGPUTargetQuadSetup(CCQuad3f theQuad, CCGPUTargetQuadCreationMode theMode) {
		_myQuad = theQuad;
		_myMode = theMode;
	}
	
	public void setParticleTargets(final CCGraphics g, int theX, int theY, final int theWidth, final int theHeight) {
		
		switch(_myMode) {
		case RANDOM:
			for(int x = theX; x < theX + theWidth;x++) {
				for(int y = theY; y < theY + theHeight;y++) {
					CCVector3f myQuadPosition = _myQuad.randomPoint();
					g.textureCoords(0, myQuadPosition);
					g.vertex(x, y);
				}
			}
			break;
		case GRID:
			for(int x = theX; x < theX + theWidth;x++) {
				for(int y = theY; y < theY + theHeight;y++) {
					CCVector3f myQuadPosition = _myQuad.gridVector(x / (theWidth + 1f), y / (theHeight + 1f));
					g.textureCoords(0, myQuadPosition);
					g.vertex(x, y);
				}
			}
			break;
		}
		
		
	}
	
	@Override
	public void setParticleTargets(CCGraphics g, CCGPUIndexParticleEmitter theGroup) {
		switch(_myMode) {
		case RANDOM:
			for (int i = 0; i < theGroup.numberOfParticles(); i++) {
				CCVector3f myQuadPosition = _myQuad.randomPoint();
				g.textureCoords(0, myQuadPosition);
				g.vertex(theGroup.xforIndex(i), theGroup.yforIndex(i));
			}
			break;
		case GRID:
//			for(int i = 0; i < theGroup.numberOfParticles();i++) {
//				CCVector3f myQuadPosition = _myQuad.gridVector(x / (theWidth + 1f), y / (theHeight + 1f));
//				g.textureCoords(0, myQuadPosition);
//				g.vertex(theGroup.xforIndex(i), theGroup.yforIndex(i));
//			}
			break;
		}
	}
}
