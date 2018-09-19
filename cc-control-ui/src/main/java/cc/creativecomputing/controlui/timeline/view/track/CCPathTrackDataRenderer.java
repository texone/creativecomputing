/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui.timeline.view.track;

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

public class CCPathTrackDataRenderer extends CCTrackDataRenderer{

	
	@Override
	public void renderTimedEvent(CCEventPoint theTimedEvent, CCAbstractTrackDataView<?> theView, CCGraphics theG2d) {	
		if(theTimedEvent.content() == null || theTimedEvent.content().value() == null) {
			return;
		}
		CCVector2 myPos = theView.controller().curveToViewSpace(new CCControlPoint(theTimedEvent.time(),1));
		CCVector2 myEndPos = theView.controller().curveToViewSpace(new CCControlPoint(theTimedEvent.endTime(),1));
		double width = myEndPos.x - myPos.x;
		theG2d.color(0,0,0,100);
		
		String myString = theTimedEvent.content().value().toString();
		//if(myString.contains("/"))myString = myString.substring(myString.lastIndexOf("/"));
	
		theG2d.text(myString, (int) myPos.x + 5, (int) myPos.y + 15);
	}
	
	
}
