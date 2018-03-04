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
package cc.creativecomputing.controlui.controls;

import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.handles.CCTriggerProgress.CCTriggerProgressListener;
import cc.creativecomputing.controlui.CCControlComponent;
import cc.creativecomputing.controlui.CCProgressWindow;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.uinano.CCUILabel;

public class CCEventTriggerControl extends CCValueControl<Object, CCEventTriggerHandle>{
	
	private CCUILabelWidget _myButton;
	
	private CCProgressWindow _myProgressWindow;
	
	private CCTriggerProgressListener _myProgressListener;
	
	public CCEventTriggerControl(CCEventTriggerHandle theHandle, CCControlComponent theControlComponent){
		super(theHandle, theControlComponent);
 
        //Create the Button.

        _myButton = new CCUILabelWidget(CCUIConstants.DEFAULT_FONT, "bang");
        _myButton.mouseReleased.add(theE -> {
			_myHandle.trigger();
		});
        
        _myHandle.progress().events().add(_myProgressListener = new CCTriggerProgressListener() {
    		@Override
    		public void start() {
    			_myProgressWindow = new CCProgressWindow();
    		}
    		
    		@Override
    		public void progress(double theProgress) {
    			_myProgressWindow.progress(theProgress);
    		}
    		
    		@Override
    		public void end() {
    			_myProgressWindow.setVisible(false);
    		}
    		
    		@Override
    		public void interrupt() {}
    	});
	}
	
	@Override
	public void dispose() {
		super.dispose();
		_myHandle.progress().events().remove(_myProgressListener);
	}
	
	@Override
	public Object value() {
		return null;
	}
	
	
	
	@Override
	public void addToPane(CCUIGridPane thePanel, int theY, int theDepth) {
		thePanel.add(_myLabel, 	constraints(0, theY, GridBagConstraints.LINE_END, 	5, 5, 1, 5));
		thePanel.add(_myButton, constraints(1, theY, GridBagConstraints.LINE_START, 5, 4, 1, 5));
	}
}
