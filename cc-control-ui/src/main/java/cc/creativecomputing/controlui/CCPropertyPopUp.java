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

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.ui.widget.CCUIPopUpMenu;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCPropertyPopUp extends CCUIPopUpMenu {
	
	private static CCDataObject clipboard = null;
	
	protected CCPropertyHandle<?> _myProperty;

	public CCPropertyPopUp(CCPropertyHandle<?> theProperty, CCUIWidget theParent, double theX, double theY) {
		super(theParent, theX, theY);
		
		_myProperty = theProperty;

		updateMatrices();
	}
	
	@Override
	public void addElements() {

		addItem("Property Actions");
		
		addSeparator();
		
//		addItem("Add To Timeline", e -> {_myControlComponent.timeline().addTrack(_myProperty);});
//		addItem("Write To Timeline", e -> {_myControlComponent.timeline().writeValues(_myProperty);});
		addItem(
			CCEntypoIcon.ICON_CCW,
			"Restore Default", 
			e -> _myProperty.restoreDefault()
		);
		addItem(
			CCEntypoIcon.ICON_CCW,
			"Restore Preset", 
			e -> _myProperty.restorePreset()
		);
		
		addSeparator();
		
		addItem(
			CCEntypoIcon.ICON_COPY,
			"copy", 
			e -> clipboard = _myProperty.data()
		);
		addItem(
			CCEntypoIcon.ICON_CLIPBOARD, 
			"paste", 
			e -> {if(clipboard != null)_myProperty.data(clipboard);}
		);
		addItem(
			CCEntypoIcon.ICON_LOG_OUT,
			"export", 
			e -> {CCNIOUtil.selectOutput("export preset", null, "json").ifPresent(path -> CCDataIO.saveDataObject(_myProperty.data(), path));}
		);
		addItem(
			CCEntypoIcon.ICON_LOGIN,
			"import", 
			e -> {CCNIOUtil.selectInput("import preset", null, "json").ifPresent(path -> _myProperty.data(CCDataIO.createDataObject(path)));}
		);
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
	
	
}
