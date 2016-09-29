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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.simulation.domain.CCDomain;

/**
 * Base class for force that use domains like bounce and avoidance
 * @author info
 *
 */
public abstract class CCDomainForce extends CCForce{

	
	protected List<CCDomain> _myDomains = new ArrayList<CCDomain>();
	
	public void addDomain(final CCDomain theDomain){
		_myDomains.add(theDomain);
	}
}
