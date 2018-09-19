package cc.creativecomputing.gl.app;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.CCPropertyFeedbackObject;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;

public class CCGLApp extends CCGLWindow implements CCPropertyFeedbackObject{
	
	@CCProperty(desc = "window width", readBack = true)
	public int width = 800;
	@CCProperty(desc = "window height", readBack = true)
	public int height = 800;
	@CCProperty(desc = "window title")
	public String title = "cc app";
	@CCProperty(desc = "number of samples used for antialiasing of the application")
	public int antialiasing = 8;
	@CCProperty(desc = "flag to make a window undecorated default false")
	public boolean undecorated = false;
	@CCProperty(desc = "flag to make a window fullscreen default false")
	public boolean fullscreen = false;
	@CCProperty(desc = "flag to make a window resizable default true")
	public boolean resizable = true;
	@CCProperty(desc = "flag to let the app run in vsync")
	public boolean inVsync = false;
	@CCProperty(desc = "flag to define if the window should be shown")
	public boolean visible = true;
	@CCProperty(desc = "flag to define if the window should be shown on top")
	public boolean alwaysOnTop = false;
	
	public void init(CCGLFWMonitor theMonitor, CCGLWindow theWindow){
		super.init(width, height, title, theMonitor, theWindow);
		setupEvents.add(e -> {setup();});
		updateEvents.add(this::update);
		drawEvents.add(this::display);
		
		windowSizeEvents.add(e -> {width = e.x; height = e.y;});
	}
	
	public void size(int theWidth, int theHeight) {
		width = theWidth;
		height = theHeight;
	}
	
	public void setup(){
		
	}
	
	public void update(CCGLTimer theTimer){
		
	}
	
	public void display(CCGraphics g) {
		
	}

	@Override
	public Map<String, CCEvent<?>> propertyListener() {
		Map<String, CCEvent<?>> myResult = new HashMap<>();
		myResult.put("width", e ->{windowSize(width, height);});
		myResult.put("height", e ->{windowSize(width, height);});
		myResult.put("title", e -> title(title));
		myResult.put("visible", e -> {if(visible)show(); else hide();});
		
//		@CCProperty(desc = "flag to make a window undecorated default false")
//		public boolean undecorated = false;
//		@CCProperty(desc = "flag to make a window fullscreen default false")
//		public boolean fullscreen = false;
//		@CCProperty(desc = "flag to make a window resizable default true")
//		public boolean resizable = true;
//		@CCProperty(desc = "flag to let the app run in vsync")
//		public boolean inVsync = false;
//
//		@CCProperty(desc = "flag to define if the window should be shown on top")
//		public boolean alwaysOnTop = false;
		return myResult;
	}

}