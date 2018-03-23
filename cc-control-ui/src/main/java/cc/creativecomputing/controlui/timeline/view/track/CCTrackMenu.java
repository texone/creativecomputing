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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.CCTextInputDialog;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.ui.widget.CCUIMenu;
import net.objecthunter.exp4j.ExpressionBuilder;

public abstract class CCTrackMenu<ControllerType extends CCTrackController> extends CCUIMenu {
	
	protected CCTrackController _myTrackController;
	private CCTimelineController _myTimelineController;
	
	private static List<CCControlPoint> clipBoard = null;
	
	private static CCTrackController controller = null;

	public CCTrackMenu(ControllerType theTrackController, CCTimelineController theTimelineController) {
		super(CCUIConstants.DEFAULT_FONT);
		_myTrackController = theTrackController;
		_myTimelineController = theTimelineController;

		addItem("Track Edit Tools");
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
			if(!(e.isControlDown() || e.isMetaDown()))return false;
			switch(e.getKeyCode()){
			case KeyEvent.VK_C:
				if(_myTrackController.selectedPoints().size() <= 0) {
					break;
				}
				controller = _myTrackController;
				clipBoard = _myTrackController.copySelection();
				break;
			case KeyEvent.VK_X:
				controller = _myTrackController;
				clipBoard = _myTrackController.cutSelection();
				break;
			case KeyEvent.VK_V:
				if(_myTimelineController == null)return false;
				if(_myTrackController != controller)return false;
				_myTrackController.paste(clipBoard, _myTimelineController.transportController().time());
				break;
			}
			return false;  
		});
	}
	
	protected double _myClickedTime = 0;
	
	public void show(double x, double y) {
		_myClickedTime = _myTrackController.viewXToTime(x, true);
	}
	
	private CCGLMouseEvent _myMouseEvent;
	
	public void show(CCGLMouseEvent e) {
		_myMouseEvent = e;
		show(e.x, e.y);
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
	
	public void addFunctions() {
		addItem("Insert Time",e -> {
			
//			new CCTextInputDialog(
//				"Insert Time", 
//				"Specify the time to insert in seconds.", 
//				"insert",
//				input ->{
//					double myTime = 0;
//					try {
//						myTime = new ExpressionBuilder(input).build().evaluate();
//					} catch (Exception ex) {}
//					_myTrackController.trackData().insertTime(_myTrackController.viewXToTime(_myMouseEvent.x, true),myTime);
//					_myTrackController.view().render();
//				}
//			)
//			.location(_myMouseEvent.getXOnScreen(), _myMouseEvent.getYOnScreen())
//			.size(400,200)
//			.open();
			
		});
		
		addItem("Remove Time", e -> {
			double myLowerBound = _myTimelineController.transportController().loopStart();
			double myUpperBound = _myTimelineController.transportController().loopEnd();
			double myRange = myUpperBound - myLowerBound;

			_myTrackController.trackData().cutRangeAndTime(myLowerBound, myRange);
			_myTrackController.view().render();
		});

		addItem("Reset Track",e -> {
			if(_myTrackController == null)return;
			_myTrackController.reset();
		});
		
		addItem("Clear Selection", e -> {
			if(_myTrackController == null)return;
			_myTrackController.clearSelection();
		});
		
		addItem("Delete",e -> {
			if(_myTrackController == null)return;
			_myTrackController.deleteSelection();
		});
		
		addItem("Copy",e -> {
			if(_myTrackController == null)return;
			if(_myTrackController.selectedPoints().size() <= 0) {
				return;
			}
			controller = _myTrackController;
			clipBoard = _myTrackController.copySelection();
		});
		
		addItem("Cut",e -> {
			if(_myTrackController == null)return;
			clipBoard = _myTrackController.cutSelection();
		});
		
		addItem("Paste",e -> {
			if(_myTrackController == null)return;
			if(_myTimelineController == null)return;
			_myTrackController.paste(clipBoard, _myTimelineController.transportController().time());
		});
	}
	
	public void addTools(){
		int i = 0;
		for(CCTimelineTools myTool:_myTrackController.tools()) {
			addItem(myTool.name(),e -> {
				for(CCTrackMenu<?> myPopUP:_myPopUps){
					myPopUP.activevateTool(myTool);
				}
				
			});
			add(myStepItem);
			_myToolGroup.add(myStepItem);
			if(_myTrackController.tools().length == 1 && i == 0){
				myStepItem.setSelected(true);
			}else{
				if(myTool == CCTimelineTools.LINEAR_POINT)myStepItem.setSelected(true);
			}
			myToolGroup.add(myStepItem);
			i++;
		}
	}
}
