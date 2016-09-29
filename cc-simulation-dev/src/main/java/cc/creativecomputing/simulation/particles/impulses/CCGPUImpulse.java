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
package cc.creativecomputing.simulation.particles.impulses;


import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.simulation.particles.CCGPUUpdateShader;

import com.jogamp.opengl.cg.CGparameter;
import com.jogamp.opengl.cg.CgGL;


public abstract class CCGPUImpulse {
	
	protected String _myParameterIndex;
	protected String _myShaderTypeName;
	protected CCGPUUpdateShader _myVelocityShader;
	
	private float _myStrength;
	
	private CGparameter _myStrengthParameter;
	
	public CCGPUImpulse(final String theShaderTypeName, final float theStrength){
		_myShaderTypeName = theShaderTypeName;

		_myStrength = theStrength;
	}
	
	public void setShader(CCGPUUpdateShader theShader, final int theIndex, final int theWidth, final int theHeight){
		_myVelocityShader = theShader;
		_myParameterIndex = "impulses["+theIndex+"]";
		CgGL.cgConnectParameter(
			_myVelocityShader.createFragmentParameter(_myShaderTypeName), 
			_myVelocityShader.fragmentParameter(_myParameterIndex)
		);
		
		_myStrengthParameter  = parameter("strength");

		strength(_myStrength);
		
		setupParameter(theWidth, theHeight);
		_myVelocityShader.checkError("Problem creating constrain.");
	}
	
	public abstract void setupParameter(final int theWidth, final int theHeight);
	
	public CGparameter parameter(final String theName){
		return _myVelocityShader.fragmentParameter(_myParameterIndex+"."+theName);
	}
	
	public void strength(final float theStrength) {
		_myStrength = theStrength;
	}
	
	private boolean _myTrigger = false;
	
	public void trigger() {
		_myTrigger = true;
	}
	
	public boolean isTriggering() {
		return _myTrigger;
	}
	
	public void update(final CCAnimator theAnimator) {
		if(_myTrigger) {
			_myTrigger = false;
			_myVelocityShader.parameter(_myStrengthParameter, _myStrength);
		}else {
			_myVelocityShader.parameter(_myStrengthParameter, 0);
		}
	}
}
