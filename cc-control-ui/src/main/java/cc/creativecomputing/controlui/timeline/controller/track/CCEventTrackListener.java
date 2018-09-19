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
package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;

public interface CCEventTrackListener{
	void onCreate(CCEventTrackController theController, CCEventPoint thePoint);
	
	void onChange(CCEventTrackController theController, CCEventPoint thePoint);
	
	void onDelete(CCEventTrackController theController, CCEventPoint thePoint);
	
	void onProperties(CCEventTrackController theController, CCEventPoint thePoint);
	
	void onClick(CCEventTrackController theController, CCEventPoint thePoint);
	
	void onTime(double theTime, CCEventTrackController theController, CCEventPoint thePoint);
	
	void onTimeChange(double theTime, double theOffset, CCEventTrackController theController, CCEventPoint thePoint);
	
	void onOut();
	
	void renderTimedEvent(CCEventPoint theTimedEvent, CCVector2 theLower, CCVector2 theUpper, double lowerTime, double UpperTime, CCGraphics theG2d);
}
