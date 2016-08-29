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

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import cc.creativecomputing.controlui.timeline.controller.TimelineTool;
import cc.creativecomputing.controlui.timeline.controller.ToolController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;


public class SwingToolChooserPopup extends JPopupMenu {
	
	private class SwingToolChooserAction implements ActionListener{

		private TimelineTool _myTool;
		
		private SwingToolChooserAction(TimelineTool theTool) {
			_myTool = theTool;
		}
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			_myCurveToolController.toolMode(_myTool);
		}
		
	}
	
	private class ResetAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			if(_myActiveTrackController == null)return;
			
			_myActiveTrackController.trackData().clear();
			_myActiveTrackController.view().render();
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	private ToolController _myCurveToolController;
	private TrackController _myActiveTrackController;

	public SwingToolChooserPopup(ToolController theToolController) {
		_myCurveToolController = theToolController;

		JMenuItem entryHead = new JMenuItem("Track Edit Tools");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
		addSeparator();
		
		ButtonGroup myToolGroup = new ButtonGroup();
		for(TimelineTool myTool:theToolController.tools()) {
			JRadioButtonMenuItem myStepItem = new JRadioButtonMenuItem(myTool.name());
			myStepItem.setFont(SwingGuiConstants.ARIAL_11);
			myStepItem.addActionListener(new SwingToolChooserAction(myTool));
			add(myStepItem);
			if(myTool.name().equals("LINEAR"))myStepItem.setSelected(true);
			myToolGroup.add(myStepItem);
		}
		
		addSeparator();
		
		JMenuItem myDeleteItem = new JMenuItem("Reset Track");
		myDeleteItem.setFont(SwingGuiConstants.ARIAL_11);
		myDeleteItem.addActionListener(new ResetAction());
		add(myDeleteItem);
		
	}
	
	public void trackController(TrackController theController) {
		_myActiveTrackController = theController;
	}
}