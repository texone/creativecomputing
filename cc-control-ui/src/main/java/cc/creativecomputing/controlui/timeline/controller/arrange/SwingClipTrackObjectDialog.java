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
import java.awt.MouseInfo;
import java.awt.Point;
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

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cc.creativecomputing.control.timeline.point.CCEventPoint;
import cc.creativecomputing.control.timeline.point.CCEventPoint.TimedData;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer.TimelineChangeListener;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.ui.widget.CCUIDropDownWidget;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;

public class SwingClipTrackObjectDialog extends JDialog implements ActionListener, PropertyChangeListener {

	private CCUIDropDownWidget _myTimelines;

	private final CCTimelineContainer _myTimelineContainer;
	
	private JPanel _myPanel;

	/** Creates the reusable dialog. */
	public SwingClipTrackObjectDialog(CCTimelineContainer theTimelineContainer) {
		super();

		setTitle("Edit Event");
		
		_myTimelineContainer = theTimelineContainer;
		_myTimelineContainer.addEvents.add(theTimeline -> {
			_myTimelines.addItem(theTimeline);
			_myTimelines.setSelectedItem(_myTimelineContainer.defaultTimelineKey());
		});
		_myTimelineContainer.resetEvents.add(e ->{
			_myTimelines.removeAllItems();
		});

		_myPanel = new JPanel();
		_myPanel.setPreferredSize(new Dimension(200,100));
		createTimelineCombo();
		// Make this dialog display it.
		setContentPane(_myPanel);

		// Handle window closing correctly.
		setDefaultCloseOperation(HIDE_ON_CLOSE);

		pack();
	}
	
	private CCEventPoint _myEventPoint;
	private CCEventTrackController _myController;
	
	public void edit(CCEventTrackController theController, CCEventPoint theEventPoint){
		_myEventPoint = theEventPoint;
		_myController = theController;
		Point myLoc = MouseInfo.getPointerInfo().getLocation();
		setLocation(myLoc.x, myLoc.y);
		setVisible(true);
	}
	
	private void createTimelineCombo(){
		JLabel myTimelineLabel = new JLabel("timeline");
		myTimelineLabel.setFont(CCUIConstants.ARIAL_11);
		add(myTimelineLabel);
		_myTimelines = new JComboBox<String>();
		for(String myKey:_myTimelineContainer.timelineKeys()){
			_myTimelines.addItem(myKey);
		}
		_myTimelines.setSelectedItem(_myTimelineContainer.defaultTimelineKey());
		_myTimelines.setEditable(true);
		_myTimelines.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent theE) {
				if(_myEventPoint == null)return;
				switch(theE.getStateChange()){
				case ItemEvent.SELECTED:
					_myEventPoint.content(new TimedData(_myTimelines.getSelectedItem().toString()));
					_myController.view().render();
					break;
				}
			}
		});
		
        _myPanel.add(_myTimelines);
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
