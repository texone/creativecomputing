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
package cc.creativecomputing.simulation.force;

import cc.creativecomputing.math.CCVector3;


public abstract class CCFlowField {

	public CCVector3 flowAtPoint(final CCVector3 thePosition){
		return flowAtPoint(thePosition.x,thePosition.y,thePosition.z);
	}
	
	public abstract CCVector3 flowAtPoint(final double theX, final double theY, final double theZ);
}
