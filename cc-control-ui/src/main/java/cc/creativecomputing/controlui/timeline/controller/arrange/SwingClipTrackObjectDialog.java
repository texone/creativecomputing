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

import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;

import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint.TimedEventPointContent;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer.TimelineChangeListener;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;

public class SwingClipTrackObjectDialog extends JDialog implements ActionListener, PropertyChangeListener {
	/**
	 * 
	 */
	private static final long serialVersionUID = 139787124989150690L;

	private JComboBox<String> _myTimelines;

	private final TimelineContainer _myTimelineContainer;
	
	private JPanel _myPanel;

	/** Creates the reusable dialog. */
	public SwingClipTrackObjectDialog(TimelineContainer theTimelineContainer) {
		super();

		setTitle("Edit Event");
		
		_myTimelineContainer = theTimelineContainer;
		_myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {

			@Override
			public void changeTimeline(TimelineController theController) {}
			
			@Override
			public void addTimeline(String theTimeline) {
				_myTimelines.addItem(theTimeline);
				_myTimelines.setSelectedItem(_myTimelineContainer.defaultTimelineKey());
			}
		});

		_myPanel = new JPanel();
		_myPanel.setPreferredSize(new Dimension(300,300));
		createTimelineCombo();
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
	
	public void edit(EventTrackController theController, TimedEventPoint theEventPoint){
		_myEventPoint = theEventPoint;
		_myController = theController;
		setVisible(true);
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
				if(_myEventPoint == null)return;
				switch(theE.getStateChange()){
				case ItemEvent.SELECTED:
					_myEventPoint.content(new TimedEventPointContent(_myTimelines.getSelectedItem().toString()));
					_myController.view().render();
					break;
				}
			}
		});
		
        CCUIStyler.styleTransportComponent(_myTimelines, 120, 20);
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