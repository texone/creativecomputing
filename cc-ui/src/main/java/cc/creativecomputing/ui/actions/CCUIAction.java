/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.ui.actions;

import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.ui.CCUI;
import cc.creativecomputing.ui.event.CCUIWidgetEvent;
import cc.creativecomputing.ui.event.CCUIWidgetEventListener;
import cc.creativecomputing.ui.event.CCUIWidgetEventType;
import cc.creativecomputing.ui.event.CCUIWidgetUpdateEvent;
import cc.creativecomputing.ui.widget.CCUIWidget;

/**
 * @author christianriekoff
 *
 */
public abstract class CCUIAction implements CCUIWidgetEventListener{

	
	@CCXMLProperty(name="states", node=false)
	private CCUIWidgetEventType[] _myStates;
	
	public CCUIAction(CCUIWidgetEventType...theTypes) {
		_myStates = theTypes;
	} 
	
	protected CCUIAction() {
		
	}

	public void widget(CCUIWidget theWidget) {
		theWidget.addListener(this);
	}
	
	public void init(CCUI theUI) {
		
	}
	
	public void update(final double theDeltaTime) {
		
	}
	
	public abstract void execute(CCUIWidget theWidget);
	
	public void onEvent(CCUIWidgetEvent theEvent, CCUIWidget theWidget) {
		if(theEvent instanceof CCUIWidgetUpdateEvent) {
			CCUIWidgetUpdateEvent myEvent = (CCUIWidgetUpdateEvent)theEvent;
			update(myEvent.updateTime());
			return;
		}
		
		for(CCUIWidgetEventType myState:_myStates) {
			if(myState == theEvent.type()) {
				execute(theWidget);
				return;
			}
		}
	}
}
