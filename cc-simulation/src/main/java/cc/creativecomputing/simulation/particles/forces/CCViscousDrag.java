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

public class CCViscousDrag extends CCForce{
	private String _myDragParameter;
	
	@CCProperty(name = "drag", min = 0, max = 1)
	private double _cDrag = 0;
	
	public CCViscousDrag(final double theDrag) {
		super("ViscousDrag");
		_cDrag = theDrag;
		_myDragParameter  = parameter("drag");
	}
	
	public CCViscousDrag(){
		this(0);
	}
	
	public void drag(double theDrag) {
		_cDrag = theDrag;
	}

	@Override
	public void setUniforms() {
		super.setUniforms();
		_myShader.uniform1f(_myDragParameter, _cDrag);
	}


	
	
}
