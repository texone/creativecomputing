package cc.creativecomputing.controlui.timeline.view.transport;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.controller.CCZoomController;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer.TimelineChangeListener;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.Zoomable;
import cc.creativecomputing.controlui.timeline.view.SwingDraggableValueBox;
import cc.creativecomputing.controlui.timeline.view.SwingDraggableValueBox.ChangeValueListener;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.math.CCMath;

@SuppressWarnings("serial")
public class SwingTransportView extends JPanel implements TransportView, ChangeValueListener, Zoomable{
	
	private class PlayButtonAction implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent theArg0) {
			if (_myTimelineContainer.activeTimeline().transportController().isPlaying()) {
				_myPlayButton.setText("play");
				_myTimelineContainer.activeTimeline().transportController().stop();
			} else {
				_myPlayButton.setText("stop");
				_myTimelineContainer.activeTimeline().transportController().play();
			}
		}
	}
	
	private static final int MAX_SLIDER_VALUE = 1000;
	
	private static final float MAX_RANGE = 3600 * 5;
	
	private static final float CURVE_POW = 4;

	private TimelineContainer _myTimelineContainer;
	private CCZoomController _myZoomController;
	
	private JButton _myPlayButton;
	private JButton _myLoopButton;
	
	private TimeField _myTimeField;
	
	private SwingDraggableValueBox _mySpeedValue;
	
	private JSlider _mySlider;
	
	private boolean _myTriggerEvent = true;
	
	private JComboBox<String> _myTimelines;
	private ComboBoxEditor _myTimelinesComboEditor;
	
	public SwingTransportView(TimelineContainer theTimelineContainer) {
		_myTimelineContainer = theTimelineContainer;
		_myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
			
			@Override
			public void changeTimeline(TimelineController theController) {
				_myZoomController.removeZoomable(SwingTransportView.this);
				_myZoomController = theController.zoomController();
				_myZoomController.addZoomable(SwingTransportView.this);
			}
			
			@Override
			public void addTimeline(String theTimeline) {
				_myTimelines.addItem(theTimeline);
				_myTimelines.setSelectedItem(_myTimelineContainer.defaultTimelineKey());
			}
		});
		_myZoomController = _myTimelineContainer.activeTimeline().zoomController();
		
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
		_myPlayButton.addActionListener(new PlayButtonAction());
		
		_myLoopButton = createButton("loop");
		_myLoopButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				_myTimelineContainer.activeTimeline().transportController().loop();
			}
		});
		
		createTimeField();
		createZoomSlider();
		createTimelineCombo();
        
		_myZoomController.addZoomable(this);
	}
	
	public JComboBox<String> timelineCombox(){
		return _myTimelines;
	}
	
	private void createTimeField(){
		_myTimeField = new TimeField(_myTimelineContainer);
		add(_myTimeField);
		
		if(SwingGuiConstants.CREATE_SPEED_CONTROL) {
			_mySpeedValue = new SwingDraggableValueBox(1,0.1f,10,0.1f);
			_mySpeedValue.addListener(this);
			CCUIStyler.styleTransportComponent(_mySpeedValue, 64, 20);
			add(_mySpeedValue);
			
			JLabel mySpeedLabel = new JLabel("speed");
			mySpeedLabel.setFont(SwingGuiConstants.ARIAL_11);
			add(mySpeedLabel);
		}
	}
	
	private void createZoomSlider(){
		_mySlider = new JSlider(JSlider.HORIZONTAL, 0, MAX_SLIDER_VALUE, 0);
        _mySlider.addChangeListener(new ChangeListener() {
			
			@Override
			public void stateChanged(ChangeEvent theE) {
				if(!_myTriggerEvent)return;
				float myBlend = CCMath.pow(_mySlider.getValue() / (float)MAX_SLIDER_VALUE, CURVE_POW);
				_myZoomController.setRange(_myZoomController.lowerBound(), _myZoomController.lowerBound() + myBlend * MAX_RANGE);
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
	
	private void createTimelineCombo(){
		JLabel myTimelineLabel = new JLabel("timeline");
		myTimelineLabel.setFont(SwingGuiConstants.ARIAL_11);
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
	
	
	private void removeTimeline(){
		_myTimelines.removeItem(_myTimelines.getSelectedItem());
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

	public void speed(double theSpeed){
		if(_mySpeedValue != null)_mySpeedValue.value(theSpeed);
	}
	
	public void changeValue(double theValue) {
		_myTimelineContainer.activeTimeline().transportController().speed(theValue);	
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.timeline.view.TransportRulerView#time(double)
	 */
	@Override
	public void time(double theTime) {
		_myTimeField.time(theTime);
		
		if (_myTimelineContainer.activeTimeline().transportController().isPlaying() && theTime % 1 > 0.5) {
			_myPlayButton.setSelected(true);
		} else {
			_myPlayButton.setSelected(false);
		}
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
