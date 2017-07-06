package cc.creativecomputing.controlui.timeline.controller.track;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.TimedEventPoint;

public abstract class CCEventTrackAdapter implements CCEventTrackListener{

	@Override
	public void onCreate(CCEventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onChange(CCEventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onDelete(CCEventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onProperties(CCEventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onClick(CCEventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onTime(double theTime, CCEventTrackController theController, TimedEventPoint thePoint) {}
	
	@Override
	public void onTimeChange(double theTime, double theOffset, CCEventTrackController theController, TimedEventPoint thePoint) {}
	
	public void renderTimedEvent(TimedEventPoint theTimedEvent, Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		
	}
	
	@Override
	public void onOut() {}

}
