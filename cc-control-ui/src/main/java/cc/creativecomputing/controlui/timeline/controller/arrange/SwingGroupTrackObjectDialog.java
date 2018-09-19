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
import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.control.timeline.point.CCEventPoint.TimedData;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
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
	
	private CCEventPoint _myEventPoint;
	private CCEventTrackController _myController;
	
	public void edit(CCObjectPropertyHandle theObject, CCEventTrackController theController, CCEventPoint theEventPoint){
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
		myTimelineLabel.setFont(CCUIConstants.ARIAL_11);
		add(myTimelineLabel);
		CCNIOUtil.createDirectories(CCNIOUtil.dataPath("settings"));
		_myPresetList = new JComboBox<String>();
        _myPresetList.setEditable(true);
        
        _myPresetList.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent theE) {
				if(_myEventPoint == null)return;
				switch(theE.getStateChange()){
				case ItemEvent.SELECTED:
					_myEventPoint.content(new TimedData(_myPresetList.getSelectedItem().toString()));
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
