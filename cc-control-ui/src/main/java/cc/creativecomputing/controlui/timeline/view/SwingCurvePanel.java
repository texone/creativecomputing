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

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.controlui.timeline.controller.CCTrackContext;
import cc.creativecomputing.controlui.timeline.controller.track.CCDoubleTrackController;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackView;
import cc.creativecomputing.controlui.timeline.view.track.SwingCurveTrackDataView;

/**
 * @author christianriekoff
 *
 */
public class SwingCurvePanel extends CCAbstractTrackView implements ComponentListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1507288020816738412L;
	private CCTrackContext _myTrackContext;
	private CCDoubleTrackController _myTrackController;
	private CCTrack _myTrack;
	
	public SwingCurvePanel(JFrame theFrame) {
		_myMainFrame = theFrame;
		_myTrackContext = new CCTrackContext();
		_myTrack = new CCTrack(null);
		_myTrackController = new CCDoubleTrackController(_myTrackContext,_myTrack, null);
		_myDataView = new SwingCurveTrackDataView(null, _myTrackController);
		_myDataView.isEnvelope(true);
		_myTrackController.view(this);
//		((TrackDataController)_myTrackController).trackDataView(_myTrackDataView);
		
		_myDataView.addComponentListener(this);
		_myTrackContext.zoomController().addZoomable(_myTrackController);
	}
	
	public CCTrack track(){
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
		_myDataView.render();
	}

	@Override
	public void componentShown(ComponentEvent theArg0) {
	}
	
	public void updateView(){
		_myDataView.render();
	}

	@Override
	public void mute(boolean theMute) {}
	
	@Override
	public void min(double theMin) {}
	
	@Override
	public void max(double theMax) {}

	@Override
	public void color(Color theColor) {}
}
