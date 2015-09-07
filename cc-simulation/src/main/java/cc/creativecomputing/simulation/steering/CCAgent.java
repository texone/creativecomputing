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
package cc.creativecomputing.simulation.steering;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.simulation.CCParticle;
import cc.creativecomputing.simulation.force.CCForce;

public class CCAgent extends CCParticle{
	
	protected CCMind _myMind;

	public CCAgent(){
		super();
		maxSpeed = 0.3F;
		maxForce = 0.02F;
		_myMind = new CCMind();
	}
	
	public void addBehavior(final CCForce theBehavior){
		_myMind.addBehavior(theBehavior);
	}
	
	public CCMind mind(){
		return _myMind;
	}

	public void update(final float theDeltaTime){
		_myMind.update(this, theDeltaTime);
		
		super.update(theDeltaTime);
	}
}
