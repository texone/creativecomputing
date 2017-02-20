package cc.creativecomputing.controlui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.controlui.controls.CCUIStyler;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer.TimelineChangeListener;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.controlui.timeline.view.SwingTimelineContainerView;
import cc.creativecomputing.controlui.timeline.view.SwingTimelineView;
import cc.creativecomputing.core.CCProperty;

public class CCControlComponent extends JSplitPane{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3570221683598509895L;

	private CCControlTreeComponent _myTreeComponent;
	
	private final JScrollPane _myScrollPane;
	private final JSplitPane _myControlsTimelinePane;
	
	private JPanel _myInfoPanel;
	
	private CCPresetComponent _myPresetComponent;
	
	private TimelineContainer _myTimelineContainer;
	private SwingTimelineContainerView _myTimelineView;
	
	public CCControlComponent(JFrame theMainFrame){
		super(JSplitPane.VERTICAL_SPLIT, true);
		CCUIStyler.styleSplitPane(this);
		_myInfoPanel = new JPanel();
		_myInfoPanel.setBackground(Color.RED);
		_myScrollPane = new JScrollPane(_myInfoPanel);
		_myScrollPane.setBorder(new EmptyBorder(0, 0, 0, 0));
		_myInfoPanel.setBackground(Color.GRAY);
        ((FlowLayout)_myInfoPanel.getLayout()).setVgap(0);
        ((FlowLayout)_myInfoPanel.getLayout()).setHgap(0);
		_myScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		
		_myTreeComponent = new CCControlTreeComponent("app", this);
		_myTimelineView = new SwingTimelineContainerView(theMainFrame);
		_myTimelineContainer = new TimelineContainer(_myTreeComponent.propertyMap());
		_myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
			
			@Override
			public void resetTimelines() {}
			
			@Override
			public void changeTimeline(TimelineController theController) {
				((SwingTimelineView)_myTimelineContainer.activeTimeline().view()).controller(theController);
//				_myControlsTimelinePane.setRightComponent(((SwingTimelineView)theController.view()).container());
			}

			@Override
			public void addTimeline(String theTimeline) {
			}
		});
		_myTimelineContainer.view(_myTimelineView);
//		_myTimelineView.setSize(1900, 500);
		
        _myPresetComponent = new CCPresetComponent();
        
        JSplitPane myControlsPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, true);
        CCUIStyler.styleSplitPane(myControlsPane);
        myControlsPane.setDividerLocation(30 * SwingGuiConstants.SCALE);
        myControlsPane.setTopComponent(_myPresetComponent);
        myControlsPane.setBottomComponent(_myScrollPane);
        

        _myControlsTimelinePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        CCUIStyler.styleSplitPane(_myControlsTimelinePane);
        _myControlsTimelinePane.setLeftComponent(myControlsPane);
        _myControlsTimelinePane.setDividerLocation(330 * SwingGuiConstants.SCALE);
        _myControlsTimelinePane.setRightComponent((SwingTimelineView)_myTimelineContainer.activeTimeline().view());
        
        _myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
        	
        	@Override
        	public void resetTimelines() {}
			
			@Override
			public void changeTimeline(TimelineController theController) {
		        _myControlsTimelinePane.setRightComponent((SwingTimelineView)_myTimelineContainer.activeTimeline().view());
			}
			
			@Override
			public void addTimeline(String theTimeline) {
			}
		});
        
        JSplitPane myTreeControlsTimelinePane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, true);
        CCUIStyler.styleSplitPane(myTreeControlsTimelinePane);
        myTreeControlsTimelinePane.setLeftComponent(_myTreeComponent);
        
        myTreeControlsTimelinePane.setRightComponent(_myControlsTimelinePane);
        myTreeControlsTimelinePane.setDividerLocation(200 * SwingGuiConstants.SCALE); 
        
        setTopComponent(_myTimelineView.transportView());
        setBottomComponent(myTreeControlsTimelinePane);
		setDividerLocation(30 * SwingGuiConstants.SCALE);
        
        Dimension minimumSize = new Dimension(200 * SwingGuiConstants.SCALE, 50 * SwingGuiConstants.SCALE);
        _myTreeComponent.setMinimumSize(minimumSize);
        _myInfoPanel.setMinimumSize(minimumSize);
        setPreferredSize(new Dimension(1800 * SwingGuiConstants.SCALE, 300 * SwingGuiConstants.SCALE));
	}

	
	
	public SwingTimelineContainerView view(){
		return _myTimelineView;
	}
	
	public TimelineContainer timeline(){
		return _myTimelineContainer;
	}
	
	public void showContent(JPanel theControlPanel){
        if(_myInfoPanel != null && theControlPanel != null){
        	_myInfoPanel.removeAll();
        	_myInfoPanel.invalidate(); 
        	_myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
        	_myInfoPanel.repaint();
        	_myInfoPanel.add(theControlPanel);
        	_myInfoPanel.invalidate(); 
        	_myInfoPanel.validate(); // or ((JComponent) getContentPane()).revalidate();
        	_myInfoPanel.repaint();
        	
        	_myScrollPane.invalidate(); 
        	_myScrollPane.validate(); // or ((JComponent) getContentPane()).revalidate();
        	_myScrollPane.repaint();
        }
	}
	
	public void setPresets(CCObjectPropertyHandle theObjectHandle){
		_myPresetComponent.setPresets(theObjectHandle);
	}
	
	public JScrollPane scrollPane(){
		return _myScrollPane;
	}
	
	public JPanel infoPanel(){
		return _myInfoPanel;
	}
	
	public void setData(Object theData, String thePresetPath){
		_myTreeComponent.setData(theData, thePresetPath);
        _myPresetComponent.setPresets(_myTreeComponent.rootHandle());
        _myPresetComponent.loadFirstPreset();
	}
	
	public CCPropertyMap propertyMap(){
		return _myTreeComponent.propertyMap();
	}
	
	/**
     * Create the GUI and show it.  For thread safety,
     * this method should be invoked from the
     * event dispatch thread.
     */
    private static void createAndShowGUI() {
//        if (useSystemLookAndFeel) {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                System.err.println("Couldn't use system look and feel.");
            }
//        }
 
        //Create and set up the window.
        JFrame frame = new JFrame("TreeDemo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
 
        CCControlComponent myControlComponent = new CCControlComponent(frame);
        myControlComponent.setData(new CCTestClass2(), "settings");
//        myControlComponent.setData();
        
        //Add content to the window.
        frame.add(myControlComponent);
 
        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public static class CCTestClass{
		@CCProperty(name = "int test", min = 0, max = 100)
		private int _myInt = 10;
		@CCProperty(name = "float test", min = 0, max = 100)
		private float _myFloat = 10;
	}
	
	public static class CCTestClass2{
		@CCProperty(name = "bool test")
		private boolean _myBoolean = true;
		@CCProperty(name = "object test")
		private CCTestClass _myInnerObject = new CCTestClass();
	}
 
    public static void main(String[] args) {
        //Schedule a job for the event dispatch thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }

}
