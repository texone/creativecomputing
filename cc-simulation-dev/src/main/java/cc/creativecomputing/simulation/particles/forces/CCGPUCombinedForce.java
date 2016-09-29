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

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.particles.CCParticles;
import cc.creativecomputing.simulation.particles.CCGPUUpdateShader;
import cc.creativecomputing.util.logging.CCLog;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;


/**
 * The combined force is useful when blending forces. This way it is possible to blend
 * between two different sets of forces.
 * @author info
 *
 */
public class CCGPUCombinedForce extends CCForce{
	
	private List<CCForce> _myForces;
	
	/**
	 * Create a new combined force using the given forces
	 * @param theForces
	 */
	public CCGPUCombinedForce(final List<CCForce> theForces){
		super("CombinedForce");
		_myForces = theForces;
	}

	@Override
	/**
	 * @invisible
	 */
	public void setShader(CCParticles theParticles, CCGPUUpdateShader theShader, int theIndex, final int theWidth, final int theHeight) {
		setShader(theParticles, theShader, "forces["+theIndex+"]", theWidth, theHeight);
	}
	
	@Override
	/**
	 * @invisible
	 */
	public void setShader(CCParticles theParticles, CCGPUUpdateShader theShader, final String theIndex, final int theWidth, final int theHeight) {
		_myParticles = theParticles;
		_myVelocityShader = theShader;
		_myParameterIndex = theIndex;
		_myVelocityShader.checkError("Problem creating force.");
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		_myVelocityShader.checkError("Problem creating force.");
		CCLog.info(_myParameterIndex + ".forces");
		CGparameter myForcesParameter = _myVelocityShader.fragmentParameter(_myParameterIndex + ".forces");
		CgGL.cgSetArraySize(myForcesParameter, _myForces.size());
		
		int myCounter = 0;
		
		for(CCForce myForce:_myForces) {
			myForce.setShader(theParticles, theShader, _myParameterIndex + ".forces[" + myCounter + "]", theWidth, theHeight);
			myCounter++;
		}
		setupParameter(0, 0);
	}
	
	@Override
	/**
	 * @invisible
	 */
	public void setupParameter(int theWidth, int theHeight){
		super.setupParameter(theWidth, theHeight);
		for(CCForce myForce:_myForces) {
			myForce.setupParameter(theWidth, theHeight);
		}
	}

	@Override
	/**
	 * @invisible
	 */
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {
		for(CCForce myForce:_myForces) {
			myForce.setSize(theG, theWidth, theHeight);
		}
	}

	@Override
	/**
	 * @invisible
	 */
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		for(CCForce myForce:_myForces) {
			myForce.update(theDeltaTime);
		}
	}
}
