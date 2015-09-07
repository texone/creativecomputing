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
package cc.creativecomputing.app.events;

public abstract class CCEventAdapter<EventType extends CCEvent> implements CCEventListener<EventType>{

	private Class<EventType> _myType;
	
	public CCEventAdapter(Class<EventType> theType){
		_myType = theType;
	}
	
	
	@Override
	public Class<EventType> eventType() {
		return _myType;
	}

}
