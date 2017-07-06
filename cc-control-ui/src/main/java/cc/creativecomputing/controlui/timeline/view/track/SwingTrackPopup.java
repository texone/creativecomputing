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

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.core.logging.CCLog;

public abstract class SwingTrackPopup<ControllerType extends CCTrackController> extends JPopupMenu {
	
	static List<SwingTrackPopup<?>> _myPopUps = new ArrayList<>();
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	protected CCTrackController _myTrackController;
	private List<JRadioButtonMenuItem> _myToolGroup = new ArrayList<>();
	private TimelineController _myTimelineController;
	
	private static List<ControlPoint> clipBoard = null;

	public SwingTrackPopup(ControllerType theTrackController, TimelineController theTimelineController) {
		_myPopUps.add(this);
		_myTrackController = theTrackController;
		_myTimelineController = theTimelineController;

		JMenuItem entryHead = new JMenuItem("Track Edit Tools");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
		addSeparator();
		addTools();
		addSeparator();
		addFunctions();
		
		addShortCuts();
	}
	
	protected double _myMouseTime;
	
	public void mouseTime(double theTime){
		_myMouseTime = theTime;
	}
	
	public void addShortCuts(){
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e-> {
			if(e.getID() != KeyEvent.KEY_PRESSED)return false;
			switch(e.getKeyCode()){
			case KeyEvent.VK_BACK_SPACE:
				_myTrackController.deleteSelection();
				break;
			}
			System.out.println(e.getID() == KeyEvent.KEY_PRESSED);
			if(!(e.isControlDown() || e.isMetaDown()))return false;
			switch(e.getKeyCode()){
			case KeyEvent.VK_C:
				clipBoard = _myTrackController.copySelection();
				break;
			case KeyEvent.VK_X:
				clipBoard = _myTrackController.cutSelection();
				break;
			case KeyEvent.VK_V:
				if(_myTimelineController == null)return false;
				_myTrackController.paste(clipBoard, _myTimelineController.transportController().time());
				break;
			}
			return false;  
		});
	}
	
	protected double _myClickedTime = 0;
	
	@Override
	public void show(Component invoker, int x, int y) {
		super.show(invoker, x, y);
		_myClickedTime = _myTrackController.viewXToTime(x, true);
	}
	
	public void activevateTool(CCTimelineTools theTool){
		for(JRadioButtonMenuItem myItem:_myToolGroup){
			if(myItem.getText().equals(theTool.name())){
				myItem.setSelected(true);
				_myTrackController.setTool(theTool);
				break;
			}
		}
	}
	
	public void addItem(String theName, ActionListener theListener){
		JMenuItem myItem = new JMenuItem(theName);
		myItem.setFont(SwingGuiConstants.ARIAL_11);
		myItem.addActionListener(theListener);
		add(myItem);
	}
	
	
	
	public void addFunctions() {
		addItem("Reset Track",theEvent -> {
			if(_myTrackController == null)return;
			_myTrackController.reset();
		});
		
		addItem("Clear Selection", theEvent -> {
			if(_myTrackController == null)return;
			_myTrackController.clearSelection();
		});
		
		addItem("Delete",theEvent -> {
			if(_myTrackController == null)return;
			_myTrackController.deleteSelection();
		});
		
		addItem("Copy",theEvent -> {
			if(_myTrackController == null)return;
			clipBoard = _myTrackController.copySelection();
		});
		
		addItem("Cut",theEvent -> {
			if(_myTrackController == null)return;
			clipBoard = _myTrackController.cutSelection();
		});
		
		addItem("Paste",theEvent -> {
			if(_myTrackController == null)return;
			if(_myTimelineController == null)return;
			_myTrackController.paste(clipBoard, _myTimelineController.transportController().time());
		});
	}
	
	public void addTools(){
		ButtonGroup myToolGroup = new ButtonGroup();
		int i = 0;
		for(CCTimelineTools myTool:_myTrackController.tools()) {
			
			JRadioButtonMenuItem myStepItem = new JRadioButtonMenuItem(myTool.name());
			myStepItem.setFont(SwingGuiConstants.ARIAL_11);
			myStepItem.addActionListener(theEvent -> {
				for(SwingTrackPopup<?> myPopUP:_myPopUps){
					myPopUP.activevateTool(myTool);
				}
				
			});
			add(myStepItem);
			_myToolGroup.add(myStepItem);
			if(_myTrackController.tools().length == 1 && i == 0){
				myStepItem.setSelected(true);
			}else{
				if(myTool == CCTimelineTools.CREATE_LINEAR_POINT)myStepItem.setSelected(true);
			}
			myToolGroup.add(myStepItem);
			i++;
		}
	}
}