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
package cc.creativecomputing.controlui.timeline.view.transport;

import java.awt.Component;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.view.CCTextInputDialog;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import net.objecthunter.exp4j.ExpressionBuilder;

public class SwingRulerPopUp extends JPopupMenu {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	protected CCTransportController _myTransportController;
	private TimelineController _myTimelineController;
	
	private SwingRulerView _myRulerView;

	public SwingRulerPopUp(SwingRulerView theRulerView, TimelineController theTimelineController) {
		_myRulerView = theRulerView;
		_myTimelineController = theTimelineController;
		_myTransportController = _myTimelineController.transportController();

		JMenuItem entryHead = new JMenuItem("Ruler Functions");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
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
				break;
			}
			if(!(e.isControlDown() || e.isMetaDown()))return false;
			switch(e.getKeyCode()){
			case KeyEvent.VK_C:
				break;
			}
			return false;  
		});
	}
	
	protected double _myClickedTime = 0;
	
	@Override
	public void show(Component invoker, int x, int y) {
		super.show(invoker, x, y);
		_myClickedTime = _myTransportController.viewXToTime(x, true);
	}
	
	private MouseEvent _myMouseEvent;
	
	public void show(Component invoker, MouseEvent e) {
		_myMouseEvent = e;
		show(invoker, e.getX(), e.getY());
	}
	
	public void addItem(String theName, ActionListener theListener){
		JMenuItem myItem = new JMenuItem(theName);
		myItem.setFont(SwingGuiConstants.ARIAL_11);
		myItem.addActionListener(theListener);
		add(myItem);
	}
	
	public void addFunctions() {
		addItem("Insert Time",theEvent -> {
			
			new CCTextInputDialog(
				"Insert Time", 
				"Specify the time to insert in seconds.", 
				"insert",
				input ->{
					double myTime = 0;
					try {
						myTime = new ExpressionBuilder(input).build().evaluate();
					} catch (Exception ex) {}
					_myTimelineController.insertTime(_myTransportController.time(),myTime);
				}
			)
			.location(_myMouseEvent.getXOnScreen(), _myMouseEvent.getYOnScreen())
			.size(400,200)
			.open();
			
		});
		
		addItem("Remove Time", theEvent -> {
			_myTimelineController.removeTime();
		});
		
		addItem("Add Marker",theEvent -> {
			SwingRulerMarkerDialog _myMarkerFrame = new SwingRulerMarkerDialog(_myRulerView, "MARKER");
		    _myMarkerFrame.setSize( 300, 200 ); 
			_myMarkerFrame.setLocation(_myMouseEvent.getXOnScreen(), _myMouseEvent.getYOnScreen());
			_myMarkerFrame.setVisible(true);
		});
		
		addSeparator();
		
		addItem("Reset Zoom", theE -> {
			_myTimelineController.zoomController().reset();
		});

		addItem("Zoom to Max", theE -> {
			_myTimelineController.zoomToMaximum();
		});

		addItem("Zoom to Loop",theE -> {
			_myTimelineController.zoomToLoop();
		});
	}
}