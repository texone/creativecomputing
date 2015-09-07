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
package cc.creativecomputing.simulation.steering.behavior;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCPursue extends CCMovingTargetBehavior{
	
	public CCPursue(final CCAgent theAgent){
		super(theAgent,new CCSeekFlee(new CCVector3()));
	}

}
