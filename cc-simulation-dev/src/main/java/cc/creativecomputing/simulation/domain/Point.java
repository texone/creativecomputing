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
package cc.creativecomputing.simulation.domain;

import cc.creativecomputing.math.CCVector3;

/**
 * Domain representing a single point.
 * @author christianr
 *
 */
public class Point extends CCDomain{
	
	public CCVector3 a;

	public Point(final CCVector3 i_a){
		a = i_a;
	}

	public CCVector3 generate(){
		return a;
	}
}
