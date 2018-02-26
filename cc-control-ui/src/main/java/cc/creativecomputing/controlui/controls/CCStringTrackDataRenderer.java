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
package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackDataView;
import cc.creativecomputing.controlui.timeline.view.track.CCTrackDataRenderer;

public class CCStringTrackDataRenderer extends CCTrackDataRenderer{
	
	@Override
	public void renderTimedEvent(CCTimedEventPoint theTimedEvent, CCAbstractTrackDataView<?> theView, Graphics2D theG2d) {
		if(theTimedEvent.content() == null || theTimedEvent.content().value() == null) {
			return;
		}
		
		
		Point2D myPos = theView.controller().curveToViewSpace(new CCControlPoint(theTimedEvent.time(),1));
		Point2D myEndPos = theView.controller().curveToViewSpace(new CCControlPoint(theTimedEvent.endTime(),1));
		double width = myEndPos.getX() - myPos.getX();
		theG2d.setColor(new Color(0,0,0,100));
		
		FontMetrics myMetrix = theG2d.getFontMetrics();
		String myString = theTimedEvent.content().value().toString();
		int myIndex = myString.length() - 1;
		StringBuffer myText = new StringBuffer();
		while(myIndex >= 0 && myMetrix.stringWidth(myText.toString() + myString.charAt(myIndex)) < width - 5){
			myText.insert(0, myString.charAt(myIndex));
			myIndex--;
		}
		theG2d.drawString(myText.toString(), (int) myPos.getX() + 5, (int) myPos.getY() + 15);
	}
	
	
}
