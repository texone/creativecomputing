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
package cc.creativecomputing.controlui;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPresetHandling;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.controls.CCObjectControl;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.ui.widget.CCUICheckBox;
import cc.creativecomputing.ui.widget.CCUIDialog;
import cc.creativecomputing.ui.widget.CCUIMenu;
import cc.creativecomputing.ui.widget.CCUIMenuItem;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCPropertyPopUp extends CCUIMenu {
	
	private static CCDataObject clipboard = null;
	
	private CCPropertyHandle<?> _myProperty;
	
	private CCControlComponent _myControlComponent;
	
	private CCUIMenu _myPresetMenue;

	public CCPropertyPopUp(CCPropertyHandle<?> theProperty, CCControlComponent theControlComponent) {
		super(CCUIConstants.DEFAULT_FONT);
		_myProperty = theProperty;
		_myControlComponent = theControlComponent;

		addItem("Property Actions");
		
		addSeparator();
		
		addItem("Add To Timeline", () -> {_myControlComponent.timeline().addTrack(_myProperty);});
		addItem("Write To Timeline", () -> {_myControlComponent.timeline().writeValues(_myProperty);});
		addItem("Restore Default", () -> {_myProperty.restoreDefault();});
		addItem("Restore Preset", () -> {_myProperty.restorePreset();} );
		
		addSeparator();
		
		addItem("copy", () -> {clipboard = _myProperty.data();});
		addItem("paste", () -> {
			if(clipboard == null)return;
			_myProperty.data(clipboard);
		});
		addItem("export", () -> {CCDataIO.saveDataObject(_myProperty.data(), CCNIOUtil.selectOutput("export preset", null, "json"));});
		addItem("import", () -> {_myProperty.data(CCDataIO.createDataObject(CCNIOUtil.selectInput("import preset", null, "json")));});
	}
	
	public CCPropertyPopUp(CCObjectControl theObjectControl, CCPropertyHandle<?> theProperty, CCControlComponent theControlComponent) {
		this(theProperty, theControlComponent);
		
		addSeparator();
		addItem("hide unchanged", () -> {theObjectControl.hideUnchanged();});
		addItem("show unchanged", () -> {theObjectControl.showUnchanged();});
		addSeparator();
		_myPresetMenue = new CCUIMenu(CCUIConstants.DEFAULT_FONT);
	
		addItem("presets", () -> {
			_myPresetMenue.removeAll();
			setPresets(theObjectControl.propertyHandle());
		});
		addItem("add preset", () -> {
			String myPreset = CCUIDialog.input(
				"enter the preset name",
				"add preset",
				""
			);
			if(myPreset == null)return;
			
			CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
			myHandle.savePreset(myPreset, CCPresetHandling.RESTORED);
		});
		addItem("add preset selfcontained", () -> {
			String myPreset = CCUIDialog.input(
				"enter the preset name",
				"add preset",
				""
			);
			if(myPreset == null)return;
			
			CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
			myHandle.savePreset(myPreset, CCPresetHandling.SELFCONTAINED);
		});
		
		addItem("remove current preset", () -> {
			CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
			for(CCUIWidget myWidget:_myPresetMenue) {
				CCUIMenuItem myItem = (CCUIMenuItem)myWidget;
				if(myItem.checkBox().isSelected()) {
					myHandle.deletePreset(myItem.text());
					myHandle.preset(null);
					setPresets(myHandle);
				}
			}
		});
		
		addItem("update current preset", () -> {
			for(CCUIWidget myWidget:_myPresetMenue) {
				CCUIMenuItem myItem = (CCUIMenuItem)myWidget;
				if(myItem.checkBox().isSelected()){
					CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
					myHandle.savePreset(myItem.text(), CCPresetHandling.UPDATED);
					return;
				}
				
			}
		});
	}
	
//	private void savePreset(String thePreset) {
//		if(thePreset == null)return;
//		if(thePreset.length() <= 0)return;
//		
//		CCPresetHandling myHandling = CCPresetHandling.SELFCONTAINED;
//		
//		if(_myProperty.hasSubPreset()) {
//			Object[] possibilities = {
//				CCPresetHandling.SELFCONTAINED.desc, 
//				CCPresetHandling.UPDATED.desc, 
//				CCPresetHandling.RESTORED.desc
//			};
//            String myChosenOption = (String)JOptionPane.showInputDialog(
//            		CCPropertyPopUp.this,
//            		"There exist subpresets:\n" + 
//            		"Define how to handle them",
//            		"Handle Sub Presets",
//                 JOptionPane.PLAIN_MESSAGE,
//                 null,
//                 possibilities,
//            		"save selfcontained"
//            	);
//
//            //If a string was returned, say so.
//            if ((myChosenOption != null) && (myChosenOption.length() > 0)) {
//                if (myChosenOption.equals(CCPresetHandling.SELFCONTAINED.desc)){
//                		myHandling = CCPresetHandling.SELFCONTAINED;
//                }else if (myChosenOption.equals(CCPresetHandling.UPDATED.desc)){
//                		myHandling = CCPresetHandling.UPDATED;
//                }else if (myChosenOption.equals(CCPresetHandling.RESTORED.desc)){
//                		myHandling = CCPresetHandling.RESTORED;
//                }
//            }
//		}
//		CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
//		myHandle.savePreset(thePreset, myHandling);
//	}
	
	public void setPresets(CCObjectPropertyHandle theObjectHandle){
		for(String myPreset:theObjectHandle.presets()){
			CCUICheckBox myCheckBox = new CCUICheckBox();
			_myPresetMenue.addItem(myCheckBox,myPreset, () -> {
				for(CCUIWidget myWidget: _myPresetMenue) {
					CCUIMenuItem myItem = (CCUIMenuItem)myWidget;
					myItem.checkBox().isSelected(false);
				}
				myCheckBox.isSelected(true);
				((CCObjectPropertyHandle)_myProperty).preset(myPreset);
			});
		}
	}
}
