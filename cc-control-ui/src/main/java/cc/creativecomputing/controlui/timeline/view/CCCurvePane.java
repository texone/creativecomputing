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
package cc.creativecomputing.controlui.timeline.view;

import java.awt.Color;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JFrame;

import cc.creativecomputing.control.timeline.CCTrack;
import cc.creativecomputing.controlui.timeline.controller.CCTrackContext;
import cc.creativecomputing.controlui.timeline.controller.track.CCDoubleTrackController;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackView;
import cc.creativecomputing.controlui.timeline.view.track.CCCurveTrackDataView;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;

/**
 * @author christianriekoff
 *
 */
public class CCCurvePane extends CCAbstractTrackView implements ComponentListener{

	private CCTrackContext _myTrackContext;
	private CCDoubleTrackController _myTrackController;
	private CCTrack _myTrack;
	
	public CCCurvePane(CCGLWindow theFrame) {
		_myMainFrame = theFrame;
		_myTrackContext = new CCTrackContext();
		_myTrack = new CCTrack(null);
		_myTrackController = new CCDoubleTrackController(_myTrackContext,_myTrack, null);
		_myDataView = new CCCurveTrackDataView(null, _myTrackController);
		_myDataView.isEnvelope(true);
		_myTrackController.view(this);
//		((TrackDataController)_myTrackController).trackDataView(_myTrackDataView);
		
		_myDataView.addComponentListener(this);
		_myTrackContext.zoomController().events.add(_myTrackController);
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
	
	public void display(CCGraphics g){
		_myDataView.render();
	}

	@Override
	public void mute(boolean theMute) {}
	
	@Override
	public void min(double theMin) {}
	
	@Override
	public void max(double theMax) {}

	@Override
	public void color(CCColor theColor) {}
}