package cc.creativecomputing.controlui.timeline.controller.track;

import cc.creativecomputing.control.timeline.point.TimedEventPoint;

public abstract class EventTrackAdapter implements EventTrackListener{

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
	public void onOut() {}

}
