package cc.creativecomputing.controlui.timeline.controller.track;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataView;

public abstract class ColorTrackAdapter implements EventTrackListener{

	@Override
	public void onCreate(EventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onChange(EventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onDelete(EventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onProperties(EventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onClick(EventTrackController theController, TimedEventPoint thePoint) {}

	@Override
	public void onTime(double theTime, EventTrackController theController, TimedEventPoint thePoint) {}
	
	@Override
	public void onTimeChange(double theTime, double theOffset, EventTrackController theController, TimedEventPoint thePoint) {}
	
	public void renderTimedEvent(TimedEventPoint theTimedEvent, Point2D theLower, Point2D theUpper, double lowerTime, double UpperTime, Graphics2D theG2d) {
		
	}
	
	@Override
	public void onOut() {}

}
