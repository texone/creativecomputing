package cc.creativecomputing.controlui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.file.Path;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.controls.CCObjectControl;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;

public class CCPropertyPopUp extends JPopupMenu {
	
	private static CCDataObject clipboard = null;
	

	
	private class PresetAction implements ActionListener{
		
		private String _myPreset;
		
		public PresetAction(String thePreset){
			_myPreset = thePreset;
		}
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			((CCObjectPropertyHandle)_myProperty).preset(_myPreset);
		}
	}
	
	private class AddPresetAction implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			String myPreset = (String)JOptionPane.showInputDialog(
				CCPropertyPopUp.this,
                "enter the preset name",
                "add preset",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                null
			);

			//If a string was returned, say so.
			if (myPreset != null && myPreset.length() > 0) {
				CCObjectPropertyHandle myHandle = (CCObjectPropertyHandle)_myProperty;
				Path myPresetPath = myHandle.presetPath().resolve(myPreset + ".json");
				CCDataIO.saveDataObject(myHandle.presetData(), myPresetPath, CCDataFormats.JSON);
				myHandle.preset(myPreset);
			    return;
			}
		}
		
	}
	
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
			_myControlComponent.timeline().writeValues(_myProperty);
			_myProperty.restoreDefault();
		});
		addItem("Restore Preset", theEvent -> {
			_myControlComponent.timeline().writeValues(_myProperty);
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
				System.out.println("deselected");
			}
			
			@Override
			public void menuCanceled(MenuEvent e) {
				System.out.println("canceled");
			}
		});
		add(_myPresetMenue);
		addItem("add preset", new AddPresetAction());
	}
	
	private void addItem(String theName, ActionListener theListener){
		JMenuItem myItem = new JMenuItem(theName);
		myItem.setFont(SwingGuiConstants.ARIAL_11);
		myItem.addActionListener(theListener);
		add(myItem);
	}
	
	public void setPresets(CCObjectPropertyHandle theObjectHandle){
		CCNIOUtil.createDirectories(theObjectHandle.presetPath());
		for(Path myPath:CCNIOUtil.list(theObjectHandle.presetPath(), "json")){
			String myPresetString = CCNIOUtil.fileName(myPath.getFileName().toString());
			JMenuItem myPresetItem = new JMenuItem(myPresetString);
			myPresetItem.setFont(SwingGuiConstants.ARIAL_11);
			myPresetItem.addActionListener(new PresetAction(myPresetString));
			_myPresetMenue.add(myPresetItem);
		}
	}
}