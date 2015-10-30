package cc.creativecomputing.controlui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.nio.file.Path;

import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;

public class CCPresetComponent extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8320443266534949330L;
	
	private JComboBox<String> _myPresetList;
	private ComboBoxEditor _myEditor;
	
	private JButton _myRemoveButton;
	private JButton _mySaveButton;
	private JButton _myTimelineButton;
	
	private Path _myPresetsPath;
	
	private CCObjectPropertyHandle _myPropertyHandle;
	
	private boolean _mySelectPreset = true;
	
	public CCPresetComponent(){
		CCNIOUtil.createDirectories(CCNIOUtil.dataPath("settings"));
		_myPresetList = new JComboBox<String>();
        _myPresetList.setEditable(true);
      
        _myPresetList.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent theE) {
				switch(theE.getStateChange()){
				case ItemEvent.SELECTED:
					if(_mySelectPreset){
						loadPreset();
					}
					break;
				}
			}
		});
        _myPresetList.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
//				JComboBox cb = (JComboBox)e.getSource();
//		        String petName = (String)cb.getSelectedItem();
//		        updateLabel(petName);

				if(_mySelectPreset){
					loadPreset();
				}
			}
		});
        _myEditor = _myPresetList.getEditor();
        _myEditor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				addPreset(_myEditor.getItem().toString());
			}
		});
        
        add(_myPresetList);
        CCUIStyler.styleCombo(_myPresetList);
        
        _myRemoveButton = new JButton("remove");
        _myRemoveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				removePreset();
			}
		});
        add(_myRemoveButton);
        CCUIStyler.styleButton(_myRemoveButton, 50, 15);
        
        _mySaveButton = new JButton("save");
        _mySaveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				if(_myPresetList.getSelectedItem() == null)return;
				savePreset(_myPresetList.getSelectedItem().toString());
			}
		});
        add(_mySaveButton);
        CCUIStyler.styleButton(_mySaveButton, 50, 15);
        
        _myTimelineButton = new JButton("to timeline");
        _myTimelineButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
			}
		});
        add(_myTimelineButton);
        CCUIStyler.styleButton(_myTimelineButton, 50, 15);
	}
	
	public void setPresets(CCObjectPropertyHandle theObjectHandle){
		_myPresetsPath = theObjectHandle.presetPath();
		_myPropertyHandle = theObjectHandle;
		CCNIOUtil.createDirectories(_myPresetsPath);
		_mySelectPreset = false;
		_myPresetList.removeAllItems();
		_myPresetList.addItem("select preset");
		for(Path myPath:CCNIOUtil.list(_myPresetsPath, "json")){
			_myPresetList.addItem(CCNIOUtil.fileName(myPath.getFileName().toString()));
		}
		if(theObjectHandle.preset() != null){
			_myPresetList.setSelectedItem(theObjectHandle.preset());
		}
		_mySelectPreset = true;
	}
	
	public void loadPreset(){
		if(_myPresetList.getSelectedItem() == null)return;
		loadPreset(_myPresetList.getSelectedItem().toString());
	}
	
	public void loadPreset(String thePreset){
		if(thePreset.equals(""))return;
		if(thePreset.equals("select preset"))return;
		
		_myPropertyHandle.preset(thePreset);
	}
	
	public void loadFirstPreset(){
		if(_myPresetList.getItemCount() > 1)loadPreset(_myPresetList.getItemAt(1).toString());
	}
	
	private boolean addPreset(String thePreset){
		if(containsItem(thePreset))return false;
		
		_myPresetList.addItem(thePreset);
		
		savePreset(thePreset.toString());
		return true;
	}
	
	private void savePreset(String thePreset){
		if(_myPropertyHandle == null)return;
		
		Path myPresetPath = _myPresetsPath.resolve(thePreset + ".json");
		CCDataIO.saveDataObject(_myPropertyHandle.presetData(), myPresetPath, CCDataFormats.JSON);
		_myPropertyHandle.preset(thePreset);
	}
	
	public void removePreset(){
		_myPresetList.removeItem(_myPresetList.getSelectedItem());
	}
	
	private boolean containsItem(Object theItem){
		for(int i = 0; i < _myPresetList.getItemCount();i++){
			if(_myPresetList.getItemAt(i) != null && _myPresetList.getItemAt(i).equals(theItem))return true;
		}
		return false;
	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event-dispatching thread.
     */
    private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("ComboBoxDemo2");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        //Create and set up the content pane.
        JComponent newContentPane = new CCPresetComponent();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
 
    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
