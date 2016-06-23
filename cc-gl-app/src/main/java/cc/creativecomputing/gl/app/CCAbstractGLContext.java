/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.gl.app;

import java.awt.Component;
import java.awt.Window;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import javax.swing.WindowConstants;

import cc.creativecomputing.app.modules.CCAbstractAppModule;
import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCPropertyFeedbackObject;
import cc.creativecomputing.control.handles.CCPropertyListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.container.GLContainer;
import cc.creativecomputing.gl.app.container.GLContainerType;
import cc.creativecomputing.gl.app.container.GLJavaComponentContainer;
import cc.creativecomputing.gl.app.container.GLOffsreenContainer;
import cc.creativecomputing.gl.app.container.GLWindowContainer;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.gl.app.events.CCKeyListener;
import cc.creativecomputing.gl.app.events.CCMouseEvent;
import cc.creativecomputing.gl.app.events.CCMouseListener;
import cc.creativecomputing.gl.app.events.CCMouseMotionListener;
import cc.creativecomputing.gl.app.events.CCMouseWheelEvent;
import cc.creativecomputing.gl.app.events.CCMouseWheelListener;
import cc.creativecomputing.math.CCColor;

import com.jogamp.nativewindow.ScalableSurface;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;


@SuppressWarnings({"rawtypes", "unchecked" })
public abstract class CCAbstractGLContext<GLGraphicsType extends CCGLGraphics> extends CCAbstractAppModule<CCGLListener> implements CCPropertyFeedbackObject{
	
	/**
	 * Represents the operations that will happen by default when the user initiates a 
	 * "close" on the application.
	 */
	public static enum CCCloseOperation{
		/**
		 * Don't do anything; require the program to handle the operation in the 
		 * windowClosing method of a registered WindowListener object.
		 */
		DO_NOTHING_ON_CLOSE(WindowConstants.DO_NOTHING_ON_CLOSE),
		
		/**
		 * Automatically hide the frame after invoking any registered WindowListener objects.
		 */
		HIDE_ON_CLOSE(WindowConstants.HIDE_ON_CLOSE),
		
		/**
		 * Automatically hide and dispose the frame after invoking any registered WindowListener objects.
		 */
		DISPOSE_ON_CLOSE(WindowConstants.DISPOSE_ON_CLOSE),
		
		/**
		 * Exit the application using the System exit method. Use this only in applications.
		 */
		EXIT_ON_CLOSE(WindowConstants.EXIT_ON_CLOSE);
		
		private int _myID;
		
		private CCCloseOperation(final int theID) {
			_myID = theID;
		}
		
		public int id() {
			return _myID;
		}
	}
	
	/**
	 * Request a pixel scale for the associated GLContainer, where size_in_pixel_units = pixel_scale * size_in_window_units.
	 * Default pixel scale request for both directions is {@linkplain CCPixelScale#IDENTITY}. In case platform only supports 
	 * uniform pixel scale, i.e. one scale for both directions, either {@linkplain CCPixelScale#AUTOMAX} or the maximum requested
	 * pixel scale component is used.
	 * @author christianr
	 *
	 */
	public static enum CCPixelScale{
		/**
		 * Setting surface-pixel-scale of 1.0f, results in same pixel- and window-units.
		 */
		IDENTITY(ScalableSurface.IDENTITY_PIXELSCALE),
		/**
		 * Setting surface-pixel-scale of 0.0f, results in maximum platform dependent pixel-scale, i.e. pixel-units >> window-units where available.
		 */
		AUTOMAX(ScalableSurface.AUTOMAX_PIXELSCALE);
		
		private float _myID;
		
		private CCPixelScale(float theID){
			_myID = theID;
		}
		
		public float id(){
			return _myID;
		}
	}
	
	@CCProperty(desc = "window width")
	public int width = 400;
	@CCProperty(desc = "window height")
	public int height = 400;
	
	@CCProperty(desc = "x position of the window")
	public int windowX = -1;
	@CCProperty(desc = "y position of the window")
	public int windowY = -1;
	
	@CCProperty(desc = "title of the application")
	public String title = "Creative Computing Application";
	
	@CCProperty(desc = "number of samples used for antialiasing of the application")
	public int antialiasing = 8;
	
	
	
	@CCProperty(desc = "initial background of the window on creation independent from gl clear color")
	public CCColor background = new CCColor();
	
	@CCProperty(desc = "what to do on aplication close")
	public CCCloseOperation closeOperation = CCCloseOperation.HIDE_ON_CLOSE;
	@CCProperty(desc = "display mode for the application")
	public CCDisplayMode displayMode = CCDisplayMode.WINDOW;
	@CCProperty(desc = "gl container for the application")
	public GLContainerType containerType = GLContainerType.NEWT;
	
	@CCProperty(desc = "optional path for loading assets")
	public String assetPaths = "";
	
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
	@CCProperty(desc = "define the pixelscale of the gl container eiter IDENTITY or AUTOMAX default is AUTOMAX")
	public CCPixelScale pixelScale = CCPixelScale.AUTOMAX;
	
	protected GLContainer _myContainer;
	
	private GLGraphicsType _myGraphics;
	
	protected CCAnimator _myAnimatorModule;
	
	@CCProperty(name = "device setup")
	private CCGraphicDeviceSetup _mySetup = new CCGraphicDeviceSetup();
	
	protected CCListenerManager<CCMouseListener> _myMouseListener;
	protected CCListenerManager<CCMouseMotionListener> _myMouseMotionListener;
	protected CCListenerManager<CCMouseWheelListener> _myMouseWheelListener;
	protected CCListenerManager<CCKeyListener> _myKeyListener;
	
	private Queue<CCMouseEvent> _myMouseEventQueue = new LinkedList<CCMouseEvent>();
	private Queue<CCMouseWheelEvent> _myMouseWheelEventQueue = new LinkedList<CCMouseWheelEvent>();
	private Queue<CCKeyEvent> _myKeyEventQueue = new LinkedList<CCKeyEvent>();
	
	/**
	 * Creates a new manager from the class object of your application
	 * @param theClass class object of your application
	 * @example basics.CCAppExample
	 */
	public CCAbstractGLContext(String theID){
		super(CCGLListener.class, theID);

		_myMouseListener = CCListenerManager.create(CCMouseListener.class);
		_myMouseMotionListener = CCListenerManager.create(CCMouseMotionListener.class);
		_myMouseWheelListener = CCListenerManager.create(CCMouseWheelListener.class);
		_myKeyListener = CCListenerManager.create(CCKeyListener.class);
	}
	
	@Override
	public Map<String, CCPropertyListener<?>> propertyListener() {
		return _myListenerMap;
	}
	
	public CCAbstractGLContext(){
		this("gl");
	}
	
	public CCAbstractGLContext(CCAnimator theAnimator, String theID){
		this(theID);
		_myAnimatorModule = theAnimator;
	}
	
	public CCAbstractGLContext(CCAnimator theAnimator){
		this(theAnimator, "gl");
	}
	
	public void updateDisplay(boolean theUpdateDisplay){
		_myContainer.updateDisplay(theUpdateDisplay);
	}
	
	public boolean updateDisplay(){
		return _myContainer.updateDisplay();
	}
	
	private Map<String, CCPropertyListener<?>> _myListenerMap = new HashMap<>();
	
	private boolean _myUpdateVsync = false;
	
	private void createListener(){
		_myListenerMap.put("fullscreen", new CCPropertyListener<Boolean>() {
			@Override
			public void onChange(Boolean theValue){
				_myContainer.fullScreen(theValue);
			}
		});
		
		_myListenerMap.put("visible", new CCPropertyListener<Boolean>() {
			@Override
			public void onChange(Boolean theValue){
				_myContainer.setVisible(theValue);
			}
		});
		
		_myListenerMap.put("width", new CCPropertyListener<Double>() {
			@Override
			public void onChange(Double theValue){
				_myContainer.size(width, height);
			}
		});
		
		_myListenerMap.put("height", new CCPropertyListener<Double>() {
			@Override
			public void onChange(Double theValue){
				_myContainer.size(width, height);
			}
		});
		
		_myListenerMap.put("windowX", new CCPropertyListener<Double>() {
			@Override
			public void onChange(Double theValue){
				_myContainer.position(windowX, windowY);
			}
		});
		
		_myListenerMap.put("windowY", new CCPropertyListener<Double>() {
			@Override
			public void onChange(Double theValue){
				_myContainer.position(windowX, windowY);
			}
		});
		
		_myListenerMap.put("title", new CCPropertyListener<String>() {
			@Override
			public void onChange(String theValue){
				_myContainer.title(theValue);
			}
		});
		
		_myListenerMap.put("inVsync", new CCPropertyListener<Boolean>() {
			@Override
			public void onChange(Boolean theInVsync){
				_myUpdateVsync = true;
			}
		});
		
		_myListenerMap.put("undecorated", new CCPropertyListener<Boolean>() {
			@Override
			public void onChange(Boolean theIsUndecorated){
				_myContainer.undecorated(theIsUndecorated);
			}
		});
		
		_myListenerMap.put("pixelScale", new CCPropertyListener<CCPixelScale>() {
			@Override
			public void onChange(CCPixelScale thePixelScale){
				_myContainer.pixelScale(thePixelScale);
			}
		});
	}
	
	@Override
	public void start() {
		try {
			if(displayMode == CCDisplayMode.UPDATE_ONLY){
				startUpdate();
				return;
			}
			
			createListener();
			
			GLProfile myProfile = createProfile();
			
			_myCapabilities = new GLCapabilities(myProfile);
			_myCapabilities.setSampleBuffers(antialiasing > 0);
			_myCapabilities.setNumSamples(antialiasing);
			_myCapabilities.setStencilBits(8);
			
			GLAutoDrawable myAutoDrawable = null;
			Component myComponent = null;
			
			if(displayMode == CCDisplayMode.OFFSCREEN){
				_myContainer = new GLOffsreenContainer(this);
			}else{
				switch(containerType){
				case FRAME:
				case DIALOG:
					GLCanvas myGLCanvas = new GLCanvas(_myCapabilities);
//					if(_mySettings.appContext().isShared())myGLCanvas.setSharedContext(_mySettings.appContext().glContext());
					myGLCanvas.setSurfaceScale(new float[]{pixelScale._myID, pixelScale._myID});
					myAutoDrawable = myGLCanvas;
					myComponent = myGLCanvas;
					_myContainer = new GLJavaComponentContainer(this, myAutoDrawable, myComponent);
					break;
				case NEWT:
					_myContainer = new GLWindowContainer(this);
					break;
				}
			}
			
			
			_myContainer.glAutoDrawable().addGLEventListener(new GLEventListener() {
				
				@Override
				public void reshape(GLAutoDrawable drawable, int theX, int theY, int theWidth, int theHeight) {
//					width = theWidth;
//					height = theHeight;
//					windowX = theX;
//					windowY = theY;
//					drawable.getSurfaceWidth()
					
					_myGraphics.reshape(theX, theY, theWidth, theHeight);
					_myListeners.proxy().reshape(_myGraphics);
				}
				
				@Override
				public void init(GLAutoDrawable drawable) {
					_myGraphics = createGraphics(drawable);
					_myGraphics.clear();
					_myListeners.proxy().init(_myGraphics);
					drawable.getGL().setSwapInterval(0);
				}
				
				@Override
				public void dispose(GLAutoDrawable drawable) {
					_myListeners.proxy().dispose(_myGraphics);
				}
				
				@Override
				public void display(GLAutoDrawable drawable) {
					dequeueKeyEvents();
					dequeueMouseEvents();
					dequeueMouseWheelEvents();
					_myListeners.proxy().display(_myGraphics);
					
					if(_myUpdateVsync){
						drawable.getGL().setSwapInterval(inVsync ? 1 : 0);
					}
				}
			});
			if(_myAnimatorModule != null)_myContainer.handleAddUpdates(_myAnimatorModule);
		} catch (Exception e) {
			throw new RuntimeException("COULD NOT START APPLICATION:",e);
		}
		
		
	}
	
	public abstract GLGraphicsType createGraphics(GLAutoDrawable drawable);
	
	//GLProfile.getMaximum(true)
	public abstract GLProfile createProfile();
	
	@Override
	public void stop() {
		if(_myAnimatorModule != null)_myContainer.handleRemoveUpdates(_myAnimatorModule);
		_myAnimatorModule = null;
		_myContainer.close();
	}
	
	/**
	 * Returns the mouse listener manager to register to mouse events.
	 * @see CCMouseListener
	 */
	public CCListenerManager<CCMouseListener> mouseListener() {
		return _myMouseListener;
	}

	/**
	 * Returns the mouse motion listener manager to register to mouse motion events.
	 * @see CCMouseMotionListener
	 */
	public CCListenerManager<CCMouseMotionListener> mouseMotionListener() {
		return _myMouseMotionListener;
	}

	/**
	 * Returns the mouse motion listener manager to register to mouse motion events.
	 * @see CCMouseMotionListener
	 */
	public CCListenerManager<CCMouseWheelListener> mouseWheelListener() {
		return _myMouseWheelListener;
	}
	
	/**
	 * Returns the key listener manager to register to key events.
	 * @see CCKeyListener
	 */
	public CCListenerManager<CCKeyListener> keyListener() {
		return _myKeyListener;
	}


	public void enqueueMouseEvent(CCMouseEvent theEvent) {
		synchronized (_myMouseEventQueue){
			_myMouseEventQueue.add(theEvent);
		}
	}

	protected void dequeueMouseEvents() {
		synchronized (_myMouseEventQueue){
			while(!_myMouseEventQueue.isEmpty()) {
				handleMouseEvent(_myMouseEventQueue.poll());
			}
		}
	}

	public void enqueueMouseWheelEvent(CCMouseWheelEvent theEvent) {
		synchronized (_myMouseWheelEventQueue){
			_myMouseWheelEventQueue.add(theEvent);
		}
	}

	protected void dequeueMouseWheelEvents() {
		synchronized (_myMouseWheelEventQueue){
			while(!_myMouseWheelEventQueue.isEmpty()) {
				_myMouseWheelListener.proxy().mouseWheelMoved(_myMouseWheelEventQueue.poll());
			}
		}
	}
	
	private void handleMouseEvent(CCMouseEvent theEvent) {

		switch (theEvent.eventType()){
			case MOUSE_PRESSED:
				_myMouseListener.proxy().mousePressed(theEvent);
				break;
			case MOUSE_RELEASED:
				_myMouseListener.proxy().mouseReleased(theEvent);
				break;
			case MOUSE_ENTERED:
				_myMouseListener.proxy().mouseEntered(theEvent);
				break;
			case MOUSE_EXITED:
				_myMouseListener.proxy().mouseExited(theEvent);
				break;
			case MOUSE_DRAGGED:
				_myMouseMotionListener.proxy().mouseDragged(theEvent);
				break;
			case MOUSE_MOVED:
				_myMouseMotionListener.proxy().mouseMoved(theEvent);
				break;
			case MOUSE_CLICKED:
				_myMouseListener.proxy().mouseClicked(theEvent);
				break;
			case MOUSE_WHEEL:
				break;
		}
	}

	public void enqueueKeyEvent(CCKeyEvent theEvent) {
		synchronized (_myKeyEventQueue){
			_myKeyEventQueue.add(theEvent);
		}
	}

	protected void dequeueKeyEvents() {
		synchronized (_myKeyEventQueue){
			while(!_myKeyEventQueue.isEmpty()) {
				handleKeyEvent(_myKeyEventQueue.poll());
			}
		}
	}

	private void handleKeyEvent(CCKeyEvent theEvent) {
		switch (theEvent.type()){
			case PRESSED:
				_myKeyListener.proxy().keyPressed(theEvent);
				break;
			case RELEASED:
				_myKeyListener.proxy().keyReleased(theEvent);
				break;
			case TYPED:
				_myKeyListener.proxy().keyTyped(theEvent);
				break;
		}
	}
	
	public CCGraphicDeviceSetup deviceSetup(){
		return _mySetup;
	}
	
	private void startUpdate(){
//		try{
//			final Class<?> myArguments[] = new Class[]{};
//			final Constructor<?> myContructor = _myClass.getConstructor( myArguments );
//			
//			_myApplication = (CCAbstractGraphicsApp<?>)myContructor.newInstance();
//			_myContainer = new CCUpdateContainer();
//			
//			CCUpdateAnimator myUpdateAnimator = new CCUpdateAnimator(new CCUpdateListener() {
//				
//				@Override
//				public void update(float theDeltaTime) {
//					_myApplication.updateEvents().proxy().update(theDeltaTime);
//					_myApplication.update(theDeltaTime);
//				}
//			}, 30);
//			_mySettings.appContext(new CCAppContextUpdate(myUpdateAnimator));
//			_myApplication.makeSettings(_mySettings, _myContainer);
//			_myApplication.setup();
//			myUpdateAnimator.start();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
	}
	
	private GLCapabilities _myCapabilities;
	
	/**
	 * Returns a reference to the capabilities for this gl context
	 * @return reference to the capabilities
	 */
	public GLCapabilities glCapabilities(){
		return _myCapabilities;
	}
	
	private Window _myDialogOwner;
	
	public void dialog(final Window theOwner){
//		container(GLContainerType.DIALOG);
		_myDialogOwner = theOwner;
	}
	
	public Window dialogOwner() {
		return _myDialogOwner;
	}

	public void size(int theWidth, int theHeight) {
		width = theWidth;
		height = theHeight;
	}
}
