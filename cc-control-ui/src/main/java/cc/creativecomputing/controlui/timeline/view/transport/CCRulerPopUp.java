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
package cc.creativecomputing.controlui.timeline.view.transport;

import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.view.CCTextInputDialog;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.ui.widget.CCUIMenu;
import net.objecthunter.exp4j.ExpressionBuilder;

public class CCRulerPopUp extends CCUIMenu {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	protected CCTransportController _myTransportController;
	private CCTimelineController _myTimelineController;
	
	private CCRulerView _myRulerView;

	public CCRulerPopUp(CCRulerView theRulerView, CCTimelineController theTimelineController) {
		super(CCUIConstants.DEFAULT_FONT);
		_myRulerView = theRulerView;
		_myTimelineController = theTimelineController;
		_myTransportController = _myTimelineController.transportController();

		addItem("Ruler Functions");
		addSeparator();
		addFunctions();
	}
	
	protected double _myMouseTime;
	
	public void mouseTime(double theTime){
		_myMouseTime = theTime;
	}
	
	protected double _myClickedTime = 0;
	
	public void show( double x, double y) {
		_myClickedTime = _myTransportController.viewXToTime(x, true);
	}
	
	private CCGLMouseEvent _myMouseEvent;
	
	public void show(CCGLMouseEvent e) {
		_myMouseEvent = e;
		show(e.x, e.y);
	}
	
	public void addFunctions() {
		addItem("Insert Time",() -> {
			
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
		
		addItem("Remove Time", () -> {
			_myTimelineController.removeTime();
		});
		
		addItem("Add Marker",() -> {
			SwingRulerMarkerDialog _myMarkerFrame = new SwingRulerMarkerDialog(_myRulerView, "MARKER");
		    _myMarkerFrame.setSize( 300, 200 ); 
			_myMarkerFrame.setLocation(_myMouseEvent.getXOnScreen(), _myMouseEvent.getYOnScreen());
			_myMarkerFrame.setVisible(true);
		});
		
		addSeparator();
		
		addItem("Reset Zoom", () -> {
			_myTimelineController.zoomController().reset();
		});

		addItem("Zoom to Max", () -> {
			_myTimelineController.zoomToMaximum();
		});

		addItem("Zoom to Loop",() -> {
			_myTimelineController.zoomToLoop();
		});
	}
}
