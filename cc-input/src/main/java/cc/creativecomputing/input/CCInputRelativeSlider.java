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
package cc.creativecomputing.input;

import net.java.games.input.Component;


class CCInputRelativeSlider extends CCInputSlider{

	CCInputRelativeSlider(Component i_component){
		super(i_component);
		// TODO Auto-generated constructor stub
	}

	@Override
	/**
	 * This method is called before each frame to update the slider values.
	 */
	void update(final float theDeltaTime){
		if(Math.abs(_myActualValue) < _myComponent.getDeadZone()){
		}else{
			_myActualValue = _myComponent.getPollData() * _myMultiplier;
		}
	}
}
