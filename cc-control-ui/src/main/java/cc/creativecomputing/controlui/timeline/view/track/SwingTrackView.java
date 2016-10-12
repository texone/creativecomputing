/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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
package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.Color;

import javax.swing.JFrame;

import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.timeline.view.SwingToolChooserPopup;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackControlView;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataRenderer;


/**
 * @author christianriekoff
 *
 */
public class SwingTrackView extends SwingAbstractTrackView{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3531407762182546621L;
	
	public static final double PICK_RADIUS = 10;
    public static final double GRID_INTERVAL = 5; // curve is calculated every GRID_INTERVAL points
	
	private SwingTrackControlView _myControlView;
	
	public SwingTrackView(
		JFrame theMainFrame,
		SwingToolChooserPopup theToolChooserPopUp, 
    	SwingTrackDataRenderer theDataRenderer,
		TimelineController theTimelineController,
		TrackController theController
	) {
		super(theMainFrame, theToolChooserPopUp, theDataRenderer, theTimelineController, theController);
		_myControlView = new SwingTrackControlView(theTimelineController, theController);
		_myControlView.address(theController.property().path().getFileName().toString());
	}
	
	public SwingTrackControlView controlView() {
		return _myControlView;
	}

	public void color(Color theColor) {
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
}
