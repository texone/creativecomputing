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
package cc.creativecomputing.controlui.timeline.controller;

import cc.creativecomputing.control.timeline.point.CCMarkerPoint;

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
    void onMarker(CCMarkerPoint thePoint);
}
