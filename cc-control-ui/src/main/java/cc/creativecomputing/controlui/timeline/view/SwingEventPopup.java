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

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;


public  class SwingEventPopup extends JPopupMenu {
	
	private class DeleteAction implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			if(_myEvent != null) {
				_myEventTrackController.trackData().remove(_myEvent);
				_myEventTrackController.delete(_myEvent);
				_myEventTrackController.view().render();
			}
		}
	}
	
	private class DeleteAllAction implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			if(_myEvent != null) {
				_myEventTrackController.trackData().clear();
				_myEventTrackController.view().render();
			}
		}
	}
	
	private class PropertyAction implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			if(_myEvent != null) {
				_myEventTrackController.properties(_myEvent);
			}
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	private EventTrackController _myEventTrackController;
	private TimedEventPoint _myEvent;

	public SwingEventPopup(EventTrackController theEventTrackController) {
		_myEventTrackController = theEventTrackController;

		JMenuItem entryHead = new JMenuItem("Timed Event");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
		addSeparator();
		
		JMenuItem myDeleteItem = new JMenuItem("Delete");
		myDeleteItem.setFont(SwingGuiConstants.ARIAL_11);
		myDeleteItem.addActionListener(new DeleteAction());
		add(myDeleteItem);
		
		JMenuItem myDeleteAllItem = new JMenuItem("Delete All");
		myDeleteAllItem.setFont(SwingGuiConstants.ARIAL_11);
		myDeleteAllItem.addActionListener(new DeleteAllAction());
		add(myDeleteAllItem);
		
		JMenuItem myPropertyItem = new JMenuItem("Properties");
		myPropertyItem.setFont(SwingGuiConstants.ARIAL_11);
		myPropertyItem.addActionListener(new PropertyAction());
		add(myPropertyItem);
	}
	
	public void event(TimedEventPoint theEvent) {
		_myEvent = theEvent;
	}
}