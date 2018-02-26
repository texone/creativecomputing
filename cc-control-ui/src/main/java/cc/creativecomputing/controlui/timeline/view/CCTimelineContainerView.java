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

import javax.swing.JFrame;
import javax.swing.JMenu;

import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.view.transport.CCTransportView;
import cc.creativecomputing.controlui.view.menu.CCFileMenu;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.controlui.view.menu.CCTimelineMenu;
import cc.creativecomputing.ui.widget.CCUIMenu;

public class CCTimelineContainerView {

	private CCTransportView _myTransport;
	
	private CCUIMenu _myTimelineMenue;
	private CCUIMenu _myFileMenue;
	
	private CCFont<?> _myFont;
	
	public CCTimelineContainerView(CCFont<?> theFont){
		_myFont = theFont;
	}
	
	public void timelineContainer(CCTimelineContainer theTimelineContainer){
		_myTransport = new CCTransportView(theTimelineContainer);
		_myTimelineMenue = new CCTimelineMenu(_myFont, theTimelineContainer);
		_myFileMenue = new CCFileMenu(_myFont, theTimelineContainer);
	}

	public CCUIMenu fileMenu(){
		return _myFileMenue;
	}
	
	public CCUIMenu timelineMenu(){
		return _myTimelineMenue;
	}
	
	public CCTransportView transportView() {
		return _myTransport;
	}

	public SwingTimelineView createView(CCTimelineContainer theTimelineContainer) {
		return new SwingTimelineView(theTimelineContainer);
	}
}
