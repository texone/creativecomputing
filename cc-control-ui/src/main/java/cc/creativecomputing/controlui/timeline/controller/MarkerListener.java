package cc.creativecomputing.controlui.timeline.controller;

import cc.creativecomputing.control.timeline.point.MarkerPoint;

/**
 * Listener to react on marker events. These events happen when the time cursor goes over the marker.
 * @author artcom
 *
 */
public interface MarkerListener {
	
	/**
	 * Implement this method to define what happens when the time cursor goes over a marker
	 * @param thePoint marker the timeline cursor has moved over
	 */
	public void onMarker(MarkerPoint thePoint);
}
