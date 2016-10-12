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
package cc.creativecomputing.simulation.particles.constraints;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.simulation.particles.CCGLProgramInterface;
import cc.creativecomputing.simulation.particles.CCParticlesUpdateShader;
import cc.creativecomputing.simulation.particles.CCParticles;

public abstract class CCConstraint extends CCGLProgramInterface {
	
	protected String _myParameterIndex;
	protected CCParticlesUpdateShader _myVelocityShader;
	
	@CCProperty(name = "resilience", min = 0, max = 1)
	private double _myResilience;
	@CCProperty(name = "friction", min = 0, max = 1)
	private double _myFriction;
	@CCProperty(name = "minimal velocity", min = 0, max = 100)
	private double _myMinimalVelocity;
	
	private String _myResilienceParameter;
	private String _myFrictionarameter;
	private String _myMinimalVelocityParameter;
	
	public CCConstraint(final String theShaderTypeName, final double theResilience, final double theFriction, final double theMinimalVelocity){
		super(theShaderTypeName);

		_myResilienceParameter  = parameter("resilience");
		_myFrictionarameter  = parameter("friction");
		_myMinimalVelocityParameter  = parameter("minimalVelocity");

		_myResilience = theResilience;
		_myFriction = theFriction;
		_myMinimalVelocity = theMinimalVelocity;
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_myResilienceParameter, _myResilience);
		_myShader.uniform1f(_myFrictionarameter, 1 - _myFriction);
		_myShader.uniform1f(_myMinimalVelocityParameter, _myMinimalVelocity);
	}
	
	public void setParticles(CCParticles theParticles){
		
	}
	
	public void setSize(CCGraphics theG, int theWidth, int theHeight) {}
	
	public void update(final CCAnimator theAnimator) {}
	
	public void reset() {}
	
	public void resilience(final double theResilience) {
		_myResilience = theResilience;
	}
	
	public void friction(final double theFriction) {
		_myFriction =  theFriction;
	}
	
	public void minimalVelocity(final double theMinimalVelocity) {
		_myMinimalVelocity = theMinimalVelocity;
	}
	
}
