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
package cc.creativecomputing.controlui.timeline.view;

import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import cc.creativecomputing.control.timeline.Track;
import cc.creativecomputing.controlui.timeline.controller.CurveToolController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.track.DoubleTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackDataController;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataView;

/**
 * @author christianriekoff
 *
 */
public class SwingCurvePanel implements ComponentListener{

	private SwingTrackDataView _myTrackDataView;
	private TrackContext _myTrackContext;
	private TrackController _myTrackController;
	private Track _myTrack;
	
	public SwingCurvePanel() {
		_myTrackContext = new TrackContext();
		_myTrack = new Track(null);
		CurveToolController myCurveToolController = new CurveToolController(_myTrackContext);
		_myTrackController = new DoubleTrackController(_myTrackContext, myCurveToolController,_myTrack, null);
		_myTrackDataView = new SwingTrackDataView(new SwingToolChooserPopup(myCurveToolController), null, null, _myTrackController);
//		((TrackDataController)_myTrackController).trackDataView(_myTrackDataView);
		
		_myTrackDataView.addComponentListener(this);
		_myTrackContext.zoomController().addZoomable(_myTrackController);
	}
	
	public SwingTrackDataView view() {
		return _myTrackDataView;
	}
	
	public Track track(){
		return _myTrack;
	}
	
	public double value(double theIn) {
		return _myTrack.trackData().value(theIn);
	}

	@Override
	public void componentHidden(ComponentEvent theArg0) {
	}

	@Override
	public void componentMoved(ComponentEvent theArg0) {
	}

	@Override
	public void componentResized(ComponentEvent theArg0) {
		_myTrackDataView.render();
	}

	@Override
	public void componentShown(ComponentEvent theArg0) {
	}
	
	public void updateView(){
		_myTrackDataView.render();
	}
}
