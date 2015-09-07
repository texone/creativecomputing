package cc.creativecomputing.controlui.timeline.controller;

import cc.creativecomputing.control.timeline.TimeRange;

public interface TransportTimeListener {
	
	public void time(double theTime);
	
	public void update(double theDeltaTime);

	public void onChangeLoop(TimeRange theRange, boolean theLoopIsActive);
}
