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

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCVector3;

public class CCGravity extends CCForce{
	private String _myDirectionParameter;
	
	@CCProperty(name = "direction", min = -1, max = 1)
	private CCVector3 _myDirection;
	
	public CCGravity(final CCVector3 theGravity) {
		super("gravity");
		_myDirection = new CCVector3(theGravity);
		_myDirectionParameter  = parameter("direction");
	}
	
	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform3f(_myDirectionParameter, _myDirection);
	}
	
	public CCVector3 direction() {
		return _myDirection;
	}
}
