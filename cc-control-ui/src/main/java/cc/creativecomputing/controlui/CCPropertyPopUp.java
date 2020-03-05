package cc.creativecomputing.controlui;

import java.awt.event.ActionListener;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPresetHandling;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.controls.CCObjectControl;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;

public class CCPropertyPopUp extends JPopupMenu {
	
	private static CCDataObject clipboard = null;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	
	private CCPropertyHandle<?> _myProperty;
	
	private CCControlComponent _myControlComponent;
	
	private JMenu _myPresetMenue;

	public CCPropertyPopUp(CCPropertyHandle<?> theProperty, CCControlComponent theControlComponent) {
		_myProperty = theProperty;
		_myControlComponent = theControlComponent;

		JMenuItem entryHead = new JMenuItem("Property Actions");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
		
		addSeparator();
		
		addItem("Add To Timeline", theEvent -> {_myControlComponent.timeline().addTrack(_myProperty);});
		addItem("Write To Timeline", theEvent -> {_myControlComponent.timeline().writeValues(_myProperty);});
		addItem("Restore Default", theEvent -> {
			_myProperty.restoreDefault();
		});
		addItem("Restore Preset", theEvent -> {
			_myProperty.restorePreset();
		} );
		
		addSeparator();
		
		addItem("copy", theEvent -> {clipboard = _myProperty.data();});
		addItem("paste", theEvent -> {
			if(clipboard == null)return;
			_myProperty.data(clipboard);
		});
		addItem("export", theEvent -> {CCDataIO.saveDataObject(_myProperty.data(), CCNIOUtil.selectOutput("export preset", null, "json"));});
		addItem("import", theEvent -> {_myProperty.data(CCDataIO.createDataObject(CCNIOUtil.selectInput("import preset", null, "json")));});
	}
	
	public CCPropertyPopUp(CCObjectControl theObjectControl, CCPropertyHandle<?> theProperty, CCControlComponent theControlComponent) {
		this(theProperty, theControlComponent);
		
		addSeparator();
		addItem("hide unchanged", theEvent -> {theObjectControl.hideUnchanged();});
		addItem("show unchanged", theEvent -> {theObjectControl.showUnchanged();});
		addSeparator();
		_myPresetMenue = new JMenu("preset");
		_myPresetMenue.setFont(SwingGuiConstants.ARIAL_11);
		_myPresetMenue.addMenuListener(new MenuListener() {
			
			@Override
			public void menuSelected(MenuEvent e) {
				_myPresetMenue.removeAll();
				setPresets(theObjectControl.propertyHandle());
			}
			
			@Override
			public void menuDeselected(MenuEvent e) {
				//System.out.println("deselected");
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
				//System.out.println("canceled");
			}
		});
		add(_myPresetMenue);
		addItem("add preset", action -> {
			String myPreset = (String)JOptionPane.showInputDialog(
				CCPropertyPopUp.this,
				"enter the preset name",
				"add preset",
				JOptionPane.PLAIN_MESSAGE,
				null,
				null,
				null
			);

			savePreset(myPreset);
		});
		
//		addItem("remove current preset", action -> {
//			CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
//			for(int i = 0; i < _myPresetMenue.getItemCount();i++) {
//				JMenuItem myItem = _myPresetMenue.getItem(i);
//				if(myItem.isSelected()) {
//					myHandle.deletePreset(myItem.getText());
//					myHandle.preset(null);
//					setPresets(myHandle);
//				}
//			}
//		});
		
		addItem("update current preset", action -> {
			for(int i = 0; i < _myPresetMenue.getItemCount();i++) {
				JMenuItem myItem = _myPresetMenue.getItem(i);
				if(myItem.isSelected()) {
					savePreset(myItem.getText());
				}
			}
		});
	}
	
	private void savePreset(String thePreset) {
		if(thePreset == null)return;
		if(thePreset.length() <= 0)return;
		
		CCPresetHandling myHandling = CCPresetHandling.SELFCONTAINED;
		
		if(_myProperty.hasSubPreset()) {
			Object[] possibilities = {
				CCPresetHandling.SELFCONTAINED.desc, 
				CCPresetHandling.UPDATED.desc, 
				CCPresetHandling.RESTORED.desc
			};
            String myChosenOption = (String)JOptionPane.showInputDialog(
            		CCPropertyPopUp.this,
            		"There exist subpresets:\n" + 
            		"Define how to handle them",
            		"Handle Sub Presets",
                 JOptionPane.PLAIN_MESSAGE,
                 null,
                 possibilities,
            		"save selfcontained"
            	);

            //If a string was returned, say so.
            if ((myChosenOption != null) && (myChosenOption.length() > 0)) {
                if (myChosenOption.equals(CCPresetHandling.SELFCONTAINED.desc)){
                		myHandling = CCPresetHandling.SELFCONTAINED;
                }else if (myChosenOption.equals(CCPresetHandling.UPDATED.desc)){
                		myHandling = CCPresetHandling.UPDATED;
                }else if (myChosenOption.equals(CCPresetHandling.RESTORED.desc)){
                		myHandling = CCPresetHandling.RESTORED;
                }
            }

            //If you're here, the return value was null/empty.
            setLabel("Come on, finish the sentence!");
		}
		CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
		myHandle.savePreset(thePreset, myHandling);
	}
	
	private void addItem(String theName, ActionListener theListener){
		JMenuItem myItem = new JMenuItem(theName);
		myItem.setFont(SwingGuiConstants.ARIAL_11);
		myItem.addActionListener(theListener);
		add(myItem);
	}
	
	public void setPresets(CCObjectPropertyHandle theObjectHandle){
		for(String myPreset:theObjectHandle.presets()){
			JMenuItem myPresetItem = new JCheckBoxMenuItem(myPreset, myPreset.equals(theObjectHandle.preset()));
			myPresetItem.setFont(SwingGuiConstants.ARIAL_11);
			myPresetItem.addActionListener(theArg -> {
				for(int i = 0; i < _myPresetMenue.getItemCount();i++) {
					_myPresetMenue.getItem(i).setSelected(false);
				}
				myPresetItem.setSelected(true);
				((CCObjectPropertyHandle)_myProperty).preset(myPreset);
			});
			_myPresetMenue.add(myPresetItem);
		}
	}
}