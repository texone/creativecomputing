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

import java.util.prefs.Preferences;

import cc.creativecomputing.control.handles.CCObjectHandle;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLApplicationManager;
import cc.creativecomputing.gl.app.CCGLTimer;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIContext;
import cc.creativecomputing.ui.widget.CCUIMenu;
import cc.creativecomputing.ui.widget.CCUIMenuBar;
import cc.creativecomputing.ui.widget.CCUIWidgetStyle;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaEdge;
import cc.creativecomputing.yoga.CCYogaNode.CCYogaFlexDirection;

public class CCControlApp extends CCGLApp{
	
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
	
	private CCObjectHandle _myRootHandle;
	
	private CCControlComponent _myControlComponent;

	private CCUIMenuBar _myMenuBar;
	
	@CCProperty(name = "update timeline")
	private boolean _cUpdate = true;
	
	public static Preferences preferences;
	
	private CCUIContext _myContext;
	
	private void init(Object theObject){
        _myContext = new CCUIContext(this, CCYogaFlexDirection.COLUMN);

		_myRootHandle = new CCObjectHandle(theObject, "settings");
        _myControlComponent = new CCControlComponent(this);
        _myControlComponent.setData(_myRootHandle);
        
        CCUIWidgetStyle myMenuStyle = CCUIMenu.createDefaultStyle();
		myMenuStyle.font(CCUIContext.FONT_30);
		myMenuStyle.itemSelectBackground(new CCColor(0.5d));
		myMenuStyle.backgroundFill(new CCColor(0.2d));
		
        _myMenuBar = new CCUIMenuBar(myMenuStyle);
        _myMenuBar.padding(CCYogaEdge.ALL, 10);
//        _myMenuBar.add("file", new CCFileMenu(myMenuStyle, _myFileManager, _myTimelineContainer));
//        _myMenuBar.add("timeline", new CCTimelineMenu(myMenuStyle, _myFileManager, _myTimelineContainer));
        _myMenuBar.margin(CCYogaEdge.BOTTOM, 4);
        _myContext.addChild(_myMenuBar);

		
        
        // Add content to the window.
		_myContext.addChild(_myControlComponent);
        
////        theApp.window().updateEvents.add(timer -> {update(timer.deltaTime());});
        

//        
        // Display the window.
        show();
        
        windowSizeEvents.add(size -> {
			CCControlApp.preferences.put("CCControlApp" + "/width" , size.x + "");
			CCControlApp.preferences.put("CCControlApp" + "/height" , size.y + "");
        });
        positionEvents.add(pos -> {
			CCControlApp.preferences.put("CCControlApp" + "/x" , pos.x + "");
			CCControlApp.preferences.put("CCControlApp" + "/y" ,pos.y + "");
        });
        
		
		if(CCControlApp.preferences.getInt("CCControlApp" + "/x", -1) != -1){
			position(
				CCControlApp.preferences.getInt("CCControlApp" + "/x", -1), 
				CCControlApp.preferences.getInt("CCControlApp" + "/y", -1)
			);
			windowSize(
				CCControlApp.preferences.getInt("CCControlApp" + "/width", -1), 
				CCControlApp.preferences.getInt("CCControlApp" + "/height", -1)
			);
		}
	}
	
	private Object _myRootObject;

	public CCControlApp(CCGLApplicationManager theApp, Object theRootObject, Class<?> thePrefClass) {
		width = 1000;
		height = 400;
		title = "Creative Computing Controls";
		
        // Create and set up the window.
        theApp.add(this);
        
		_myRootObject = theRootObject;
        preferences = Preferences.userNodeForPackage(thePrefClass).node(thePrefClass.getSimpleName());
		
//		
//		ExceptionHandler.registerExceptionHandler();
	}
	
	private CCTexture2D _myTex;
	
	@Override
	public void setup() {
		init(_myRootObject);
	}
	
	public static CCGLApplicationManager appManager;
	
	public CCControlApp(CCGLApplicationManager theApp, Object theRootObject) {
        this(theApp, theRootObject, theRootObject.getClass());
        appManager = theApp;
	}
	
//	public CCControlApp(CCGLApp theApp) {
//        this(theApp, theApp);
//	}
	
	public void setData(Object theObject) {
		if(_myRootHandle != null) {
			_myRootHandle.relink(theObject);
		}
	}
	
//	public CCControlApp(CCGLApp theApp, Object theRootObject, CCTimelineSynch theSynch, Class<?> thePrefClass) {
//        preferences = Preferences.userNodeForPackage(thePrefClass).node(thePrefClass.getSimpleName());
//        
//		init(theApp, theSynch.animator());
//		theSynch.timeline(_myControlComponent.timeline());
//	}

	public CCObjectHandle rootHandle() {
		return _myRootHandle;
	}
	
	@Override
	public void update(final CCGLTimer theTimer) {
		if(!_cUpdate)return;
		if(_myContext == null)return;
		_myContext.update(theTimer);
		try{
//			_myControlComponent.timeline().update(theDeltaTime);
			_myRootHandle.update(theTimer.deltaTime());
		}catch(Exception e){
			
		}
	}
	
	@Override
	public void display(CCGraphics g) {

//		g.debug();
		if(_myContext == null)return;
		g.ortho();
		g.clear();
		g.pushAttribute();
		_myContext.display(g);
		g.popAttribute();
	}
	
	public void time(double theTime){
		try{
//			_myControlComponent.timeline().time(theTime);
			_myRootHandle.update(0);
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
