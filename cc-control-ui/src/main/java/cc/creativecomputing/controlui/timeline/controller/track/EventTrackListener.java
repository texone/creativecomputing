/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.controlui.timeline.controller.track;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.TimedEventPoint;

public interface EventTrackListener{
	public void onCreate(EventTrackController theController, TimedEventPoint thePoint);
	
	public void onChange(EventTrackController theController, TimedEventPoint thePoint);
	
	public void onDelete(EventTrackController theController, TimedEventPoint thePoint);
	
	public void onProperties(EventTrackController theController, TimedEventPoint thePoint);
	
	public void onClick(EventTrackController theController, TimedEventPoint thePoint);
	
	public void onTime(double theTime, EventTrackController theController, TimedEventPoint thePoint);
	
	public void onTimeChange(double theTime, double theOffset, EventTrackController theController, TimedEventPoint thePoint);
	
	public void onOut();
	
	public void renderTimedEvent(TimedEventPoint theTimedEvent, Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d);
}