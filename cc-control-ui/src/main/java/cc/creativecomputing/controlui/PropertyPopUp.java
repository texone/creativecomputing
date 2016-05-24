package cc.creativecomputing.controlui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;

public class PropertyPopUp extends JPopupMenu {
	
	private static CCDataObject clipboard = null;
	
	private class AddToTimelineAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			_myControlComponent.timeline().addTrack(_myProperty);
		}
	}
	
	private class WriteToTimelineAction implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			_myControlComponent.timeline().writeValues(_myProperty);
		}
	}
	
	private class RestorePresetAction implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			_myControlComponent.timeline().writeValues(_myProperty);
			_myProperty.restorePreset();
		}
	}
	
	private class RestoreDefaultAction implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			_myControlComponent.timeline().writeValues(_myProperty);
			_myProperty.restoreDefault();
		}
	}
	
	private class CopyAction implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			clipboard = _myProperty.data();
		}
	}

	private class PasteAction implements ActionListener{
	
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			if(clipboard == null)return;
			_myProperty.data(clipboard);
		}
	}
	
	private class ExportAction implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			CCDataIO.saveDataObject(_myProperty.data(), CCNIOUtil.selectOutput("export preset", null, "json"));
		}
	}

	private class ImportAction implements ActionListener{
	
		@Override
		public void actionPerformed(ActionEvent theArg0) {
			_myProperty.data(CCDataIO.createDataObject(CCNIOUtil.selectInput("import preset", null, "json")));
		}
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7869280073371142253L;
	
	private CCPropertyHandle<?> _myProperty;
	
	private CCControlComponent _myControlComponent;

	public PropertyPopUp(CCPropertyHandle<?> theProperty, CCControlComponent theControlComponent) {
		_myProperty = theProperty;
		_myControlComponent = theControlComponent;

		JMenuItem entryHead = new JMenuItem("Property Actions");
		entryHead.setFont(SwingGuiConstants.ARIAL_11);
		add(entryHead);
		
		addSeparator();
		
		JMenuItem myAddToTimelineAction = new JMenuItem("Add To Timeline");
		myAddToTimelineAction.setFont(SwingGuiConstants.ARIAL_11);
		myAddToTimelineAction.addActionListener(new AddToTimelineAction());
		add(myAddToTimelineAction);
		
		JMenuItem myWriteToTimelineAction = new JMenuItem("Write To Timeline");
		myWriteToTimelineAction.setFont(SwingGuiConstants.ARIAL_11);
		myWriteToTimelineAction.addActionListener(new WriteToTimelineAction());
		add(myWriteToTimelineAction);
		
		JMenuItem myRestoreDefaultItem = new JMenuItem("Restore Default");
		myRestoreDefaultItem.setFont(SwingGuiConstants.ARIAL_11);
		myRestoreDefaultItem.addActionListener(new RestoreDefaultAction());
		add(myRestoreDefaultItem);
		
		JMenuItem myRestorePresetItem = new JMenuItem("Restore Preset");
		myRestorePresetItem.setFont(SwingGuiConstants.ARIAL_11);
		myRestorePresetItem.addActionListener(new RestorePresetAction());
		add(myRestorePresetItem);
		
		addSeparator();
		
		JMenuItem myCopyItem = new JMenuItem("copy");
		myCopyItem.setFont(SwingGuiConstants.ARIAL_11);
		myCopyItem.addActionListener(new CopyAction());
		add(myCopyItem);
		
		JMenuItem myPasteItem = new JMenuItem("paste");
		myPasteItem.setFont(SwingGuiConstants.ARIAL_11);
		myPasteItem.addActionListener(new PasteAction());
		add(myPasteItem);
		
		JMenuItem myExportItem = new JMenuItem("export");
		myExportItem.setFont(SwingGuiConstants.ARIAL_11);
		myExportItem.addActionListener(new ExportAction());
		add(myExportItem);
		
		JMenuItem myImportItem = new JMenuItem("import");
		myImportItem.setFont(SwingGuiConstants.ARIAL_11);
		myImportItem.addActionListener(new ImportAction());
		add(myImportItem);
		
		
	}
	
}