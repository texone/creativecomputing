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

import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.track.CCTrackControlView;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.math.CCColor;


/**
 * @author christianriekoff
 *
 */
public class CCTrackView extends CCAbstractTrackView{
	
    public static final double GRID_INTERVAL = 5; // curve is calculated every GRID_INTERVAL points
	
	private CCTrackControlView _myControlView;
	
	public CCTrackView(
		CCGLWindow theMainFrame,
    	CCAbstractTrackDataView<?> theDataView,
		CCTimelineController theTimelineController,
		CCTrackController theController
	) {
		super(theMainFrame, theDataView);
		_myControlView = new CCTrackControlView(theTimelineController, theController);
		_myControlView.address(theController.property().path().getFileName().toString());
	}
	
	public CCTrackControlView controlView() {
		return _myControlView;
	}

	public void color(CCColor theColor) {
		_myControlView.color(theColor);
		_myDataView.color(theColor);
	}

	@Override
	public void mute(boolean theIsMuted) {
		_myControlView.mute(theIsMuted);
	}
	
	@Override
	public void min(double theMin) {
		_myControlView.min(theMin);
	}
	
	@Override
	public void max(double theMax) {
		_myControlView.max(theMax);
	}

	@Override
	public void value(String theValue) {
		_myControlView.value(theValue);
	}

	public void render() {
		// TODO Auto-generated method stub
		
	}
}
