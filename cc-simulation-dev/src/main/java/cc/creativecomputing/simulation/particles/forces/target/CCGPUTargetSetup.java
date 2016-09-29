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
import cc.creativecomputing.simulation.particles.CCGPUIndexParticleEmitter;

/**
 * @author info
 *
 */
public interface CCGPUTargetSetup {
	
	public void setParticleTargets(final CCGraphics g, int theX, int theY, final int theWidth, final int theHeight);
	
	public void setParticleTargets(final CCGraphics theGraphics, CCGPUIndexParticleEmitter theGroup);
	
}
