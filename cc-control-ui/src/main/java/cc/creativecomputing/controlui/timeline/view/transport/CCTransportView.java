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
package cc.creativecomputing.controlui.timeline.view.transport;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cc.creativecomputing.controlui.CCNumberBox;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.controller.CCTransportable;
import cc.creativecomputing.controlui.timeline.controller.CCZoomable;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer.TimelineChangeListener;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;

@SuppressWarnings("serial")
public class CCTransportView extends CCUIHorizontalFlowPane implements CCZoomable, CCTransportable{
	
	private static final int MAX_SLIDER_VALUE = 1000;
	
	private static final float MAX_RANGE = 3600 * 5;
	
	private static final float CURVE_POW = 4;

	private CCTimelineContainer _myTimelineContainer;
	private CCTimelineController _myTimelineController;
	
	private JButton _myPlayButton;
	private JButton _myLoopButton;
	
	private JToggleButton _myShowBPMButton;
	
	private TimeField _myTimeField;
	
	private CCNumberBox _mySpeedValue;
	private CCNumberBox _myBPMValue;
	
	private JSlider _mySlider;
	
	private boolean _myTriggerEvent = true;
	
	private JComboBox<String> _myTimelines;
	private ComboBoxEditor _myTimelinesComboEditor;
	
	public CCTransportView(CCTimelineContainer theTimelineContainer) {
		_myTimelineContainer = theTimelineContainer;
		_myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
			
			@Override
			public void changeTimeline(CCTimelineController theController) {
				_myTimelineController.zoomController().removeZoomable(CCTransportView.this);
				_myTimelineController.transportController().transportEvents().remove(CCTransportView.this);
				
				_myTimelineController = theController;
				
				_myTimelineController.zoomController().addZoomable(CCTransportView.this);
				_myTimelineController.transportController().transportEvents().add(CCTransportView.this);
				zoomFromSlider();
			}
			
			@Override
			public void resetTimelines() {
				_myTimelines.removeAllItems();
			}
			
			@Override
			public void addTimeline(String theTimeline) {
				_myTimelines.addItem(theTimeline);
				_myTimelines.setSelectedItem(_myTimelineContainer.defaultTimelineKey());
			}
		});
		_myTimelineController = _myTimelineContainer.activeTimeline();
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(_myTimelineContainer.activeTimeline().transportController() == null)return;
				
				_myTimelineContainer.activeTimeline().transportController().mouseReleased(e);
				
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(_myTimelineContainer.activeTimeline().transportController() == null)return;
				_myTimelineContainer.activeTimeline().transportController().mousePressed(e);
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (_myTimelineContainer.activeTimeline().transportController() == null)return;
				
				_myTimelineContainer.activeTimeline().transportController().mouseDragged(e);
				updateUI();
			}	
		});
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
		
		_myPlayButton = createButton("play");
		_myPlayButton.addActionListener(e->{
			if (_myTimelineContainer.activeTimeline().transportController().isPlaying()) {
				_myPlayButton.setText("play");
				_myTimelineContainer.activeTimeline().transportController().stop();
			} else {
				_myPlayButton.setText("stop");
				_myTimelineContainer.activeTimeline().transportController().play();
			}
		});
		
		_myLoopButton = createButton("loop");
		_myLoopButton.addActionListener(e->{
			_myTimelineContainer.activeTimeline().transportController().loop();
		});
		
		createTimeField();
		
		_myShowBPMButton = createToggle("bpm");
		_myShowBPMButton.addActionListener(e ->{
			_myTimelineContainer.activeTimeline().transportController().useBeats(_myShowBPMButton.isSelected());
		});
		
		createZoomSlider();
		createTimelineCombo();
        
		_myTimelineController.zoomController().addZoomable(this);
		_myTimelineController.transportController().transportEvents().add(CCTransportView.this);
		zoomFromSlider();
		
		time(0);
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(e-> {
			if(e.getID() != KeyEvent.KEY_PRESSED)return false;
			System.out.println(e.getID() == KeyEvent.KEY_PRESSED);
			switch(e.getKeyCode()){
			case KeyEvent.VK_SPACE:
				if (_myTimelineContainer.activeTimeline().transportController().isPlaying()) {
					_myPlayButton.setText("play");
					_myTimelineContainer.activeTimeline().transportController().stop();
				} else {
					_myPlayButton.setText("stop");
					_myTimelineContainer.activeTimeline().transportController().play();
				}
				break;
			}
			return false;  
		});
	}
	
	public JComboBox<String> timelineCombox(){
		return _myTimelines;
	}
	
	private void createTimeField(){
		_myTimeField = new TimeField(_myTimelineContainer);
		add(_myTimeField);
		
		if(CCUIConstants.CREATE_SPEED_CONTROL) {
			_mySpeedValue = new CCNumberBox(1,0.1f,10, 2);
			_mySpeedValue.changeEvents().add(theValue -> {
				_myTimelineContainer.activeTimeline().transportController().speed(theValue);
			});
			CCUIStyler.styleTransportComponent(_mySpeedValue, 64, 20);
			add(_mySpeedValue);
			
			JLabel mySpeedLabel = new JLabel("speed");
			mySpeedLabel.setFont(CCUIConstants.ARIAL_11);
			add(mySpeedLabel);
		}
		if(CCUIConstants.CREATE_SPEED_CONTROL){
			_myBPMValue = new CCNumberBox(120,1,360, 2);
			_myBPMValue.changeEvents().add(theValue -> {
				_myTimelineContainer.activeTimeline().transportController().bpm(theValue);
			});
			CCUIStyler.styleTransportComponent(_myBPMValue, 64, 20);
			add(_myBPMValue);
			
			JLabel myBPMLabel = new JLabel("bpm");
			myBPMLabel.setFont(CCUIConstants.ARIAL_11);
			add(myBPMLabel);
		}
	}
	
	private void createZoomSlider(){
		_mySlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_SLIDER_VALUE, 0);
        _mySlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent theE) {
				if(!_myTriggerEvent)return;
				zoomFromSlider();
			}
			
		});
        
 
        //Turn on labels at major tick marks.
 
        _mySlider.setMajorTickSpacing(MAX_SLIDER_VALUE / 10);
        _mySlider.setMinorTickSpacing(MAX_SLIDER_VALUE / 20);
        _mySlider.setPaintTicks(false);
        _mySlider.setPaintLabels(false);
        _mySlider.setBorder(BorderFactory.createEmptyBorder(0,0,10,0));
        _mySlider.putClientProperty( "JComponent.sizeVariant", "mini" );
        _mySlider.setPreferredSize(new Dimension(130,17));
        add(_mySlider);
	}
	
	private void zoomFromSlider(){
		float myBlend = CCMath.pow(_mySlider.getValue() / (float)MAX_SLIDER_VALUE, CURVE_POW);
		_myTimelineController.zoomController().setRange(
			_myTimelineController.zoomController().lowerBound(), 
			_myTimelineController.zoomController().lowerBound() + myBlend * MAX_RANGE
		);
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
				switch(theE.getStateChange()){
				case ItemEvent.SELECTED:
					_myTimelineContainer.setActiveTimeline(_myTimelines.getSelectedItem().toString());
					break;
				}
			}
		});
		_myTimelinesComboEditor = _myTimelines.getEditor();
		_myTimelinesComboEditor.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent theE) {
				addTimeline(_myTimelinesComboEditor.getItem().toString());
			}
		});
        CCUIStyler.styleTransportComponent(_myTimelines, 120, 20);
        add(_myTimelines);
	}
	
	private boolean addTimeline(String theTimeline){
		if(containsItem(theTimeline))return false;
		
		_myTimelineContainer.addTimeline(theTimeline);
		return true;
	}
	
	private boolean containsItem(Object theItem){
		for(int i = 0; i < _myTimelines.getItemCount();i++){
			if(_myTimelines.getItemAt(i) != null && _myTimelines.getItemAt(i).equals(theItem))return true;
		}
		return false;
	}
	
	private JButton createButton(String theText){
		JButton myPButton = new JButton(theText);
		CCUIStyler.styleTransportComponent(myPButton, 64, 20);
		add(myPButton);
		return myPButton;
	}
	
	private JToggleButton createToggle(String theText){
		JToggleButton myPButton = new JToggleButton(theText);
		CCUIStyler.styleTransportComponent(myPButton, 64, 20);
		add(myPButton);
		return myPButton;
	}

	public void speed(double theSpeed){
		if(_mySpeedValue != null)_mySpeedValue.value(theSpeed);
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.view.TransportRulerView#time(double)
	 */
	@Override
	public void time(double theTime) {
		_myTimeField.time(theTime);
	}
	
	public Dimension getMinimumSize() {
		return new Dimension(0, 20);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(0, 40);
	}
	
	public Dimension getMaximumSize() {
		return new Dimension(5000, 40);
	}

	@Override
	public void setRange(double theLowerBound, double theUpperBound) {
		double myRange = CCMath.pow((theUpperBound - theLowerBound) / MAX_RANGE , 1f / CURVE_POW);
		double myValue = CCMath.constrain(myRange * MAX_SLIDER_VALUE, 0, MAX_SLIDER_VALUE);
		_myTriggerEvent = false;
		_mySlider.setValue((int)myValue);
		_myTriggerEvent = true;
	}
}
