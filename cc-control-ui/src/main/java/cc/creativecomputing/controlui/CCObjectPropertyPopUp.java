package cc.creativecomputing.controlui;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPresetHandling;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.controls.CCObjectControl;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.ui.widget.CCUICheckBox;
import cc.creativecomputing.ui.widget.CCUIDialog;
import cc.creativecomputing.ui.widget.CCUIMenu;
import cc.creativecomputing.ui.widget.CCUIMenuItem;
import cc.creativecomputing.ui.widget.CCUIWidget;
import cc.creativecomputing.yoga.CCYogaNode;

public class CCObjectPropertyPopUp extends CCPropertyPopUp{

	
	private CCUIMenu _myPresetMenue;
	private CCObjectControl _myObjectControl;
	
	public CCObjectPropertyPopUp(CCObjectControl theObjectControl, CCPropertyHandle<?> theProperty, CCUIWidget theParent, double theX, double theY) {
		super(theProperty, theParent, theX, theY);

		_myPresetMenue = new CCUIMenu(_myStyle);
		_myObjectControl = theObjectControl;
	}
	
	@Override
	public void addElements() {
		super.addElements();
		
		addSeparator();
		addItem(CCEntypoIcon.ICON_EYE_WITH_LINE, "hide unchanged", e -> {_myObjectControl.hideUnchanged();});
		addItem(CCEntypoIcon.ICON_EYE, "show unchanged", e -> {_myObjectControl.showUnchanged();});
		addSeparator();
	
		CCUIMenuItem myPresetItem = addItem(CCEntypoIcon.ICON_TEXT, "presets", e -> {
			
			
		});
		myPresetItem.onOver.add((o)->{
			_myPresetMenue.removeAll();
			setPresets(_myObjectControl.propertyHandle());
			_myPresetMenue.positionType(CCYogaPositionType.ABSOLUTE);
			_myPresetMenue.position(CCYogaEdge.LEFT, myPresetItem.width());
			_myPresetMenue.position(CCYogaEdge.TOP, 0);
			myPresetItem.addChild(_myPresetMenue);
			root().calculateLayout();
			CCLog.info("YO");
			updateMatrices();
		});
		myPresetItem.onOut.add((o)->{
			myPresetItem.removeChild(_myPresetMenue);
		});
		addItem(CCEntypoIcon.ICON_ADD_TO_LIST, "add preset", e -> {
			CCLog.info("add");
			CCUIDialog<String> myDialog = CCUIDialog.createTextInput(
				CCControlApp.appManager, 
				"Enter the Preset Name", 
				"preset name",
				e.screenX, 
				e.screenY
			);
			
			myDialog.events.add(myPreset -> {
				CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
				myHandle.savePreset(myPreset, CCPresetHandling.RESTORED);
			});
			
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
			for(CCYogaNode myWidget:_myPresetMenue) {
				CCUIMenuItem myItem = (CCUIMenuItem)myWidget;
				if(myItem.checkBox().isSelected()) {
					myHandle.deletePreset(myItem.text());
					myHandle.preset(null);
					setPresets(myHandle);
				}
			}
		});
		
		addItem(CCEntypoIcon.ICON_CW, "update current preset", e -> {
			for(CCYogaNode myWidget:_myPresetMenue) {
				CCUIMenuItem myItem = (CCUIMenuItem)myWidget;
				if(myItem.checkBox().isSelected()){
					CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
					myHandle.savePreset(myItem.text(), CCPresetHandling.UPDATED);
					return;
				}
				
			}
		});
	}
	
	public void setPresets(CCObjectPropertyHandle theObjectHandle){
		for(String myPreset:theObjectHandle.presets()){
			CCUICheckBox myCheckBox = new CCUICheckBox();
			_myPresetMenue.addItem(myCheckBox,myPreset, e -> {
				CCLog.info(myPreset);
				for(CCYogaNode myWidget: _myPresetMenue) {
					CCUIMenuItem myItem = (CCUIMenuItem)myWidget;
					myItem.checkBox().isSelected(false, true);
				}
				myCheckBox.isSelected(true, true);
				((CCObjectPropertyHandle)_myProperty).preset(myPreset);
			});
		}
	}
}
