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
package cc.creativecomputing.model.obj;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;

public class CCObject {
	private String _myName;
	private List<CCSegment> _mySegments;
	private boolean _myIsActive = true;

	public CCObject(final String theGroupName){
		_mySegments = new ArrayList<CCSegment>();
		_myName = theGroupName;
	}

	public String name() {
		return _myName;
	}

	public List<CCSegment> segments() {
		return _mySegments;
	}

	public void isActiv(final boolean theIsActive){
		_myIsActive = theIsActive;
	}

	public boolean isActiv(){
		return _myIsActive;
	}
}
