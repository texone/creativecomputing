package cc.creativecomputing.controlui;

import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.InputMap;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.TimelineContainer.TimelineChangeListener;
import cc.creativecomputing.controlui.timeline.view.SwingTimelineContainerView;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;

public class CCControlApp  {
	
	public static class ExceptionHandler implements Thread.UncaughtExceptionHandler {
		  public void uncaughtException(Thread t, Throwable e) {
		    handle(e);
		  }

		  public void handle(Throwable throwable) {
		    try {
		      // insert your e-mail code here
		    } catch (Throwable t) {
		    	CCLog.info("bla");
		      t.printStackTrace();
		    }
		  }

		  public static void registerExceptionHandler() {
		    Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
		    System.setProperty("sun.awt.exception.handler", ExceptionHandler.class.getName());
		  }
		}

	private Map<String, CCCustomMenu> _myCustomMenues = new HashMap<String, CCCustomMenu>();
	
	private CCControlComponent _myControlComponent;
	
	private CCAnimatorAdapter _myAnimatorListener;
	
	private TimelineContainer _myTimelineContainer;
	private SwingTimelineContainerView _myTimelineView;
	
	private CCPropertyMap _myPropertyMap;

	private JMenuBar _myMenuBar;
	
	private JFrame _myFrame;
	
	@CCProperty(name = "animator")
	private CCAnimator _myAnimator;
	@CCProperty(name = "update timeline")
	private boolean _cUpdate = true;
	
	private boolean _myShowUI;
	
	public static Preferences preferences;
	
	private void saveWindowPosition(String thePath){
		CCControlApp.preferences.put(thePath + "/x" , _myFrame.getX() + "");
		CCControlApp.preferences.put(thePath + "/y" , _myFrame.getY() + "");
		CCControlApp.preferences.put(thePath + "/width" , _myFrame.getWidth() + "");
		CCControlApp.preferences.put(thePath + "/height" , _myFrame.getHeight() + "");
	}
	
	private void init(CCAnimator theAnimator){
		_myPropertyMap = new CCPropertyMap();
		_myTimelineContainer = new TimelineContainer(_myPropertyMap);
		_myTimelineContainer.timelineChangeListener().add(new TimelineChangeListener() {
			
			@Override
			public void resetTimelines() {}
			
			@Override
			public void changeTimeline(TimelineController theController) {
				if(!_myShowUI)return;
				_myTimelineContainer.activeTimeline().view().controller(theController);
//				_myControlsTimelinePane.setRightComponent(((SwingTimelineView)theController.view()).container());
			}

			@Override
			public void addTimeline(String theTimeline) {
			}
		});
		 _myAnimator = theAnimator;
		 _myAnimatorListener = new CCAnimatorAdapter() {
			 @Override
			 public void update(CCAnimator theAnimator) {
				 CCControlApp.this.update(theAnimator.deltaTime());
			 }
		 };
		 _myAnimator.listener().add(_myAnimatorListener);
		if(!_myShowUI)return;

		
		System.setProperty("apple.laf.useScreenMenuBar", "true");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Test");
        
        
        try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // try {
        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        // } catch (Exception e) {
        // System.err.println("Couldn't use system look and feel.");
        // }

        // Create and set up the window.
        _myFrame = new JFrame("Creative Computing Controls");
        _myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		_myTimelineView = new SwingTimelineContainerView(_myFrame);
        
		_myTimelineContainer.view(_myTimelineView);

        _myControlComponent = new CCControlComponent(_myTimelineContainer);

        // Add content to the window.
        _myFrame.add(_myControlComponent);
     		
       
     		
        _myMenuBar = new JMenuBar();
        _myMenuBar.add(_myTimelineView.fileMenu());
        _myMenuBar.add(_myTimelineView.timelineMenu());
        _myFrame.setJMenuBar(_myMenuBar);
        
        // Display the window.
        _myFrame.pack();
        _myFrame.setVisible(true);
        
        InputMap im = (InputMap)UIManager.get("Button.focusInputMap");
        im.put(KeyStroke.getKeyStroke("pressed SPACE"), "none");
        im.put(KeyStroke.getKeyStroke("released SPACE"), "none");
        
        _myFrame.addComponentListener(new ComponentAdapter() {
			
			@Override
			public void componentResized(ComponentEvent e) {
				saveWindowPosition("CCControlApp");
			}
			
			@Override
			public void componentMoved(ComponentEvent e) {
				saveWindowPosition("CCControlApp");
			}
			
		});
		
		if(CCControlApp.preferences.getInt("CCControlApp" + "/x", -1) != -1){
			_myFrame.setLocation(
				CCControlApp.preferences.getInt("CCControlApp" + "/x", -1), 
				CCControlApp.preferences.getInt("CCControlApp" + "/y", -1)
			);
			_myFrame.setSize(
				CCControlApp.preferences.getInt("CCControlApp" + "/width", -1), 
				CCControlApp.preferences.getInt("CCControlApp" + "/height", -1)
			);
		}
	}

	public CCControlApp(Object theRootObject, CCAnimator theAnimator, Class<?> thePrefClass, boolean theShowUI) {
		_myShowUI = theShowUI;
        preferences = Preferences.userNodeForPackage(thePrefClass).node(thePrefClass.getSimpleName());
       
		init(theAnimator);
		
		ExceptionHandler.registerExceptionHandler();
	}
	
	public CCControlApp(Object theRootObject, CCTimelineSynch theSynch, Class<?> thePrefClass, boolean theShowUI) {
		_myShowUI = theShowUI;
        preferences = Preferences.userNodeForPackage(thePrefClass).node(thePrefClass.getSimpleName());
        
		init(theSynch.animator());
		theSynch.timeline(_myTimelineContainer);
	}
	
	public CCControlApp(Object theRootObject, Class<?> thePrefClass, boolean theShowUI) {
		this(theRootObject, new CCAnimator(), thePrefClass, theShowUI);
		_myAnimator.start();
	}
	
	public void update(double theDeltaTime){
		if(!_cUpdate)return;
		try{
			_myTimelineContainer.update(theDeltaTime);
			_myPropertyMap.rootHandle().update(theDeltaTime);
		}catch(Exception e){
			
		}
	}
	
	public void time(double theTime){
		try{
			_myTimelineContainer.time(theTime);
			_myPropertyMap.rootHandle().update(0);
		}catch(Exception e){
			
		}
	}
	
	public void setData(Object theData, String thePresetPath){
		_myPropertyMap.setData(theData, thePresetPath);
		if(_myShowUI)_myControlComponent.setData(_myPropertyMap);
		_myPropertyMap.rootHandle().preset(0);	
	}
	
	public void preset(String thePreset) {
		_myPropertyMap.rootHandle().preset(thePreset);	
	}
	
	public TimelineContainer timeline(){
		return _myTimelineContainer;
	}
	
	public CCAnimatorAdapter animatorListener(){
		return _myAnimatorListener;
	}

	public CCPropertyMap propertyMap(){
		return _myPropertyMap;
	}
	
	public JMenuItem addCustomCommand(final String theMenu, final String theCommand, final ActionListener theActionListener, int theMnemonicKey, int theAccelerator) {
		if(!_myCustomMenues.containsKey(theMenu)) {
			CCCustomMenu myCustomMenu = new CCCustomMenu(theMenu);
			_myMenuBar.add(myCustomMenu);
			_myCustomMenues.put(theMenu, myCustomMenu);
		}
		return _myCustomMenues.get(theMenu).addCommand(theCommand, theActionListener, theMnemonicKey, theAccelerator);
	}
	
	public JCheckBoxMenuItem addCustomCheckBoxCommand(final String theMenu, final String theCommand, final ActionListener theActionListener, int theMnemonicKey, int theAccelerator, boolean theDefault) {
		if(!_myCustomMenues.containsKey(theMenu)) {
			CCCustomMenu myCustomMenu = new CCCustomMenu(theMenu);
			_myMenuBar.add(myCustomMenu);
			_myCustomMenues.put(theMenu, myCustomMenu);
		}
		return _myCustomMenues.get(theMenu).addCheckBoxCommand(theCommand, theActionListener, theMnemonicKey, theAccelerator, theDefault);
	}
}
