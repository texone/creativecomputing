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
package cc.creativecomputing.controlui.timeline.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;


public  class SwingEventCreatePopup extends JPopupMenu {
	
	private class CreateAction implements ActionListener{
		
		private String _myEventType;
		
		public CreateAction(String theEventType) {
			_myEventType = theEventType;
		}
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			if(_myEvent != null) {
				_myEventTrackController.createPoint(_myEvent, _myEventType);
			}
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	private EventTrackController _myEventTrackController;
	private MouseEvent _myEvent;

	public SwingEventCreatePopup(EventTrackController theEventTrackController) {
		_myEventTrackController = theEventTrackController;

		JMenuItem entryHead = new JMenuItem("Create Event");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
		addSeparator();
		
		for(String myEventType:theEventTrackController.eventTypes()) {
			JMenuItem myStepItem = new JMenuItem(myEventType);
			myStepItem.setFont(SwingGuiConstants.ARIAL_11);
			myStepItem.addActionListener(new CreateAction(myEventType));
			add(myStepItem);
		}
	}
	
	public void event(MouseEvent theEvent) {
		_myEvent = theEvent;
	}
}