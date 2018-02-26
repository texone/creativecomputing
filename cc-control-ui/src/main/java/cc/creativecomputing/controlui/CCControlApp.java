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
package cc.creativecomputing.controlui;

import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineContainer;
import cc.creativecomputing.controlui.timeline.view.transport.CCTransportView;
import cc.creativecomputing.controlui.view.menu.CCFileMenu;
import cc.creativecomputing.controlui.view.menu.CCTimelineMenu;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCEvent;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.gl.app.CCGLWindow;
import cc.creativecomputing.graphics.font.CCCharSet;
import cc.creativecomputing.graphics.font.CCTextureMapFont;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.layout.CCUIVerticalFlowPane;
import cc.creativecomputing.ui.widget.CCUIMenu;
import cc.creativecomputing.ui.widget.CCUIMenuBar;
import cc.creativecomputing.ui.widget.CCUIMenuItem;

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
	
	private CCPropertyMap _myPropertyMap;

	private CCTimelineContainer _myTimelineContainer;
	
//	private CCControlComponent _myControlComponent;
	
	private CCTransportView _myTransport;
	
	private CCUIMenu _myTimelineMenue;
	private CCUIMenu _myFileMenue;

	private CCUIMenuBar _myMenuBar;
	
	private CCGLWindow _myWindow;
	
	@CCProperty(name = "update timeline")
	private boolean _cUpdate = true;
	
	public static Preferences preferences;
	
	private CCTextureMapFont _myFont;
	
	private CCUIContext _myContext;
	
	private void init(CCGLApp theApp){
		
		_myFont = new CCTextureMapFont(CCCharSet.REDUCED, CCNIOUtil.dataPath("Lato/Lato-Regular.ttf"), 20, 2, 2);

        // Create and set up the window.
        _myWindow = theApp.createWindow(400,400,"Creative Computing Controls");

		_myPropertyMap = new CCPropertyMap();
		
		_myTimelineContainer = new CCTimelineContainer(_myPropertyMap);

//        _myControlComponent = new CCControlComponent(_myWindow);
        
        CCUIVerticalFlowPane myVerticalPane = new CCUIVerticalFlowPane();
		myVerticalPane.inset(5);
		myVerticalPane.space(50);
		myVerticalPane.translation().set(-_myWindow.framebufferSize().x / 2, _myWindow.framebufferSize().y / 2);
		myVerticalPane.updateMatrices();
        _myContext = new CCUIContext(_myWindow, myVerticalPane);
//        
//////		_myTransport = new CCTransportView(_myTimelineContainer);
		_myFileMenue = new CCFileMenu(_myFont, _myTimelineContainer);
		_myTimelineMenue = new CCTimelineMenu(_myFont, _myTimelineContainer);
//
        _myMenuBar = new CCUIMenuBar(_myFont);
        _myMenuBar.add("file", _myFileMenue);
        _myMenuBar.add("timeline", _myTimelineMenue);
        _myContext.widget().addChild(_myMenuBar);
//        
//        // Add content to the window.
////		_myContext.widget().addChild(_myControlComponent);
//        
////        theApp.window().updateEvents.add(timer -> {update(timer.deltaTime());});
//        
        _myWindow.drawEvents.add( g -> {
        	g.clearColor(0,0d,0);
        	g.clear();
        	_myContext.widget().draw(g);
        });
        _myWindow.updateEvents.add( t -> {_myContext.widget().update(t);});
//        
        // Display the window.
        _myWindow.show();
//        
//        _myWindow.windowSizeEvents.add((window, width, height) -> {
//			CCControlApp.preferences.put("CCControlApp" + "/width" , width + "");
//			CCControlApp.preferences.put("CCControlApp" + "/height" , height + "");
//        });
//        _myWindow.positionEvents.add((window, x, y) -> {
//			CCControlApp.preferences.put("CCControlApp" + "/x" , x + "");
//			CCControlApp.preferences.put("CCControlApp" + "/y" ,y + "");
//        });
//        
//		
//		if(CCControlApp.preferences.getInt("CCControlApp" + "/x", -1) != -1){
//			CCLog.info(CCControlApp.preferences.getInt("CCControlApp" + "/x", -1), CCControlApp.preferences.getInt("CCControlApp" + "/y", -1));
//			_myWindow.position(
//				CCControlApp.preferences.getInt("CCControlApp" + "/x", -1), 
//				CCControlApp.preferences.getInt("CCControlApp" + "/y", -1)
//			);
//			_myWindow.windowSize(
//				CCControlApp.preferences.getInt("CCControlApp" + "/width", -1), 
//				CCControlApp.preferences.getInt("CCControlApp" + "/height", -1)
//			);
//		}
	}

	public CCControlApp(CCGLApp theApp, Object theRootObject, Class<?> thePrefClass) {
        preferences = Preferences.userNodeForPackage(thePrefClass).node(thePrefClass.getSimpleName());
       
		init(theApp);
//		
//		ExceptionHandler.registerExceptionHandler();
	}
	
	public CCControlApp(CCGLApp theApp, Object theRootObject) {
        this(theApp, theRootObject, theRootObject.getClass());
	}
	
	public CCControlApp(CCGLApp theApp) {
        this(theApp, theApp);
	}
	
//	public CCControlApp(CCGLApp theApp, Object theRootObject, CCTimelineSynch theSynch, Class<?> thePrefClass) {
//        preferences = Preferences.userNodeForPackage(thePrefClass).node(thePrefClass.getSimpleName());
//        
//		init(theApp, theSynch.animator());
//		theSynch.timeline(_myControlComponent.timeline());
//	}

	public CCObjectPropertyHandle rootHandle() {
		return _myPropertyMap.rootHandle();
	}

	public CCPropertyMap propertyMap() {
		return _myPropertyMap;
	}
	
	public void update(double theDeltaTime){
		if(!_cUpdate)return;
		try{
//			_myControlComponent.timeline().update(theDeltaTime);
			_myPropertyMap.rootHandle().update(theDeltaTime);
		}catch(Exception e){
			
		}
	}
	
	public void time(double theTime){
		try{
//			_myControlComponent.timeline().time(theTime);
			_myPropertyMap.rootHandle().update(0);
		}catch(Exception e){
			
		}
	}
	
//	
//	
//	public void setData(Object theData, String thePresetPath){
//		_myControlComponent.setData(theData, thePresetPath);
//	}
//	
//	public CCTimelineContainer timeline(){
//		return _myControlComponent.timeline();
//	}
	
//	public CCUIMenuItem addCustomCommand(final String theMenu, final String theCommand, final CCEvent theActionListener) {
//		if(!_myCustomMenues.containsKey(theMenu)) {
//			CCCustomMenu myCustomMenu = new CCCustomMenu(theMenu);
//			_myMenuBar.add(myCustomMenu);
//			_myCustomMenues.put(theMenu, myCustomMenu);
//		}
//		return _myCustomMenues.get(theMenu).addCommand(theCommand, theActionListener, theMnemonicKey, theAccelerator);
//	}
//	
//	public CCUIMenuItem addCustomCheckBoxCommand(final String theMenu, final String theCommand, final CCEvent theActionListener, boolean theDefault) {
//		if(!_myCustomMenues.containsKey(theMenu)) {
//			CCCustomMenu myCustomMenu = new CCCustomMenu(theMenu);
//			_myMenuBar.add(myCustomMenu);
//			_myCustomMenues.put(theMenu, myCustomMenu);
//		}
//		return _myCustomMenues.get(theMenu).addCheckBoxCommand(theCommand, theActionListener, theMnemonicKey, theAccelerator, theDefault);
//	}
}
