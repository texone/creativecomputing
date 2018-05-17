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
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIGridPane;
import cc.creativecomputing.ui.widget.CCUICheckBox;
import cc.creativecomputing.ui.widget.CCUIDialog;
import cc.creativecomputing.ui.widget.CCUILabelWidget;
import cc.creativecomputing.ui.widget.CCUIMenu;
import cc.creativecomputing.ui.widget.CCUIMenuItem;
import cc.creativecomputing.ui.widget.CCUIPopUpMenu;
import cc.creativecomputing.ui.widget.CCUITextFieldWidget;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;

public class CCPropertyPopUp extends CCUIPopUpMenu {
	
	private static CCDataObject clipboard = null;
	
	private CCPropertyHandle<?> _myProperty;
	
	private CCUIMenu _myPresetMenue;

	public CCPropertyPopUp(CCPropertyHandle<?> theProperty, CCUIWidget theParent, double theX, double theY) {
		super(theParent, theX, theY);
		_myProperty = theProperty;

		addItem("Property Actions");
		
		addSeparator();
		
//		addItem("Add To Timeline", e -> {_myControlComponent.timeline().addTrack(_myProperty);});
//		addItem("Write To Timeline", e -> {_myControlComponent.timeline().writeValues(_myProperty);});
		addItem(CCEntypoIcon.ICON_CCW,"Restore Default", e -> {_myProperty.restoreDefault();});
		addItem(CCEntypoIcon.ICON_CCW,"Restore Preset", e -> {_myProperty.restorePreset();} );
		
		addSeparator();
		
		addItem(CCEntypoIcon.ICON_COPY,"copy", e -> {clipboard = _myProperty.data();CCLog.info("COPY",_myProperty.data());});
		addItem(CCEntypoIcon.ICON_CLIPBOARD, "paste", e -> {if(clipboard != null)_myProperty.data(clipboard);CCLog.info("PASTE",clipboard);});
		addItem(CCEntypoIcon.ICON_LOG_OUT,"export", e -> {CCDataIO.saveDataObject(_myProperty.data(), CCNIOUtil.selectOutput("export preset", null, "json"));});
		addItem(CCEntypoIcon.ICON_LOGIN,"import", e -> {_myProperty.data(CCDataIO.createDataObject(CCNIOUtil.selectInput("import preset", null, "json")));});
		

		updateMatrices();
	}
	
	private CCVector2i _myWindowPosition = new CCVector2i();
	
	public CCPropertyPopUp(CCObjectControl theObjectControl, CCPropertyHandle<?> theProperty, CCUIWidget theParent, double theX, double theY) {
		this(theProperty, theParent, theX, theY);
		
		addSeparator();
		addItem(CCEntypoIcon.ICON_EYE_WITH_LINE, "hide unchanged", e -> {theObjectControl.hideUnchanged();});
		addItem(CCEntypoIcon.ICON_EYE, "show unchanged", e -> {theObjectControl.showUnchanged();});
		addSeparator();
		_myPresetMenue = new CCUIMenu(_myStyle);
	
		addItem(CCEntypoIcon.ICON_TEXT, "presets", e -> {
			_myPresetMenue.removeAll();	
			setPresets(theObjectControl.propertyHandle());
		});
		addItem(CCEntypoIcon.ICON_ADD_TO_LIST, "add preset", e -> {
			CCLog.info("add");
			CCUIDialog myDialog = CCUIDialog.createTextInput(CCControlApp.appManager, "Enter the Preset Name", "preset name",e.screenX, e.screenY);
//			String myPreset = CCUIDialog2.input(
//				"enter the preset name",
//				"add preset",
//				""
//			);
//			CCLog.info("add", myPreset);
//			if(myPreset == null)return;
//			
//			CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
//			myHandle.savePreset(myPreset, CCPresetHandling.RESTORED);
		});
		
		addItem(CCEntypoIcon.ICON_ADD_TO_LIST, "add preset selfcontained", e -> {
//			String myPreset = CCUIDialog2.input(
//				"enter the preset name",
//				"add preset",
//				""
//			);
//			if(myPreset == null)return;
//			
//			CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
//			myHandle.savePreset(myPreset, CCPresetHandling.SELFCONTAINED);
		});
		
		addItem(CCEntypoIcon.ICON_TRASH, "remove current preset", e -> {
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
		
		addItem(CCEntypoIcon.ICON_CW, "update current preset", e -> {
			for(CCUIWidget myWidget:_myPresetMenue) {
				CCUIMenuItem myItem = (CCUIMenuItem)myWidget;
				if(myItem.checkBox().isSelected()){
					CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
					myHandle.savePreset(myItem.text(), CCPresetHandling.UPDATED);
					return;
				}
				
			}
		});
		

		updateMatrices();
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
			_myPresetMenue.addItem(myCheckBox,myPreset, e -> {
				for(CCUIWidget myWidget: _myPresetMenue) {
					CCUIMenuItem myItem = (CCUIMenuItem)myWidget;
					myItem.checkBox().isSelected(false, true);
				}
				myCheckBox.isSelected(true, true);
				((CCObjectPropertyHandle)_myProperty).preset(myPreset);
			});
		}
	}
}
