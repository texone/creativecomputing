package cc.creativecomputing.controlui.timeline.controller.arrange;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.file.Path;

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint.TimedEventPointContent;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

public class SwingGroupTrackObjectDialog extends JDialog implements ActionListener, PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 139787124989150690L;

	private JComboBox<String> _myPresetList;

	
	private JPanel _myPanel;
	

	private Path _myPresetsPath;

	/** Creates the reusable dialog. */
	public SwingGroupTrackObjectDialog() {
		super();

		setTitle("Edit Preset");

		_myPanel = new JPanel();
		_myPanel.setPreferredSize(new Dimension(300,300));
		createPresetCombo();
		// Make this dialog display it.
		setContentPane(_myPanel);

		// Handle window closing correctly.
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				
			}
		});

		// Ensure the text field always gets the first focus.
		addComponentListener(new ComponentAdapter() {
			public void componentShown(ComponentEvent ce) {
			}
		});

		
	}
	
	private TimedEventPoint _myEventPoint;
	private EventTrackController _myController;
	
	public void edit(CCObjectPropertyHandle theObject, EventTrackController theController, TimedEventPoint theEventPoint){
		setPresets(theObject);
		_myEventPoint = theEventPoint;
		_myController = theController;
		setVisible(true);
	}
	
	public void setPresets(CCObjectPropertyHandle theObjectHandle){
		_myPresetsPath = theObjectHandle.presetPath();
		CCNIOUtil.createDirectories(_myPresetsPath);
		_myPresetList.removeAllItems();
		for(Path myPath:CCNIOUtil.list(_myPresetsPath, "json")){
			_myPresetList.addItem(CCNIOUtil.fileName(myPath.getFileName().toString()));
		}
	}
	
	private void createPresetCombo(){
		JLabel myTimelineLabel = new JLabel("timeline");
		myTimelineLabel.setFont(SwingGuiConstants.ARIAL_11);
		add(myTimelineLabel);
		CCNIOUtil.createDirectories(CCNIOUtil.dataPath("settings"));
		_myPresetList = new JComboBox<String>();
        _myPresetList.setEditable(true);
        
        _myPresetList.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent theE) {
				CCLog.info("item changed");
				if(_myEventPoint == null)return;
				switch(theE.getStateChange()){
				case ItemEvent.SELECTED:
					CCLog.info("item changed set yo");
					_myEventPoint.content(new TimedEventPointContent(_myPresetList.getSelectedItem().toString()));
					_myController.view().render();
					break;
				}
			}
		});
		
        CCUIStyler.styleTransportComponent(_myPresetList, 120, 20);
        _myPanel.add(_myPresetList);
	}




	@Override
	public void propertyChange(PropertyChangeEvent theEvt) {
		// TODO Auto-generated method stub
		
	}




	@Override
	public void actionPerformed(ActionEvent theE) {
		// TODO Auto-generated method stub
		
	}
}