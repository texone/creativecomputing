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
package cc.creativecomputing.app;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAppModule;
import cc.creativecomputing.app.modules.CCBasicAppModule;
import cc.creativecomputing.core.logging.CCLog;



/**
 * <p>The Application Manager handles the setup and start off an application.
 * Here you can make all settings like Window size, window position etc.
 * To define the settings you can use various methods of the CCSettings class.</p>
 * @author texone
 *
 * @param <CCAppType>
 * @see CCApp
 * @see CCPropertyMap
 */
public class CCApplication{
	
	public enum CCApplicationProgress{
		CREATED, CALLED_TO_START, RUNNING, CALLED_TO_STOP, STOPPED
	}
	
	/**
	 * Class name of the Application to start
	 */
	private final Object _myApp;
	
	private List<CCAppModule<?>> _myModules = new ArrayList<>();
	private List<CCAppModule<?>> _myInitializedModules = new ArrayList<>();
	
	private CCApplicationProgress _myProgress = CCApplicationProgress.CREATED;
	
	/**
	 * Creates a new manager from the class object of your application
	 * @param theClass class object of your application
	 * @example basics.CCAppExample
	 */
	public CCApplication(final Object theApp){
		_myApp = theApp;
		
		addModule(new CCBasicAppModule());
	}
	
	public CCApplication(){
		this(null);
	}
	
//	private void addInterfaces(Class<?> theClass, CCAppModule<?> theModule){
//		if(theClass == Object.class)return;
//		for(Class<?> myInterface:theClass.getInterfaces()){
//			if(theModule.listenerInterface() == myInterface){
//				theModule.addListener(_myApp);
//			}
//		}
//		addInterfaces(theClass.getSuperclass(), theModule);
//	}
	
	public void addModule(CCAppModule<?> theModule){
//		if(_myApp != null)addInterfaces(_myApp.getClass(), theModule);
		
//		theModule.init(this);
		_myModules.add(theModule);
	}
	
	public void isInitialized(CCAppModule<?> theModule){
		_myInitializedModules.add(theModule);
		
		switch(_myProgress){
		case CREATED:
			return;
		case CALLED_TO_START:
			if(_myInitializedModules.size() == _myModules.size()){
				startModules();
			}
			return;
		default:
			break;
		}
	}
	
	public void isStoped(CCAppModule<?> theModule){
		_myInitializedModules.remove(theModule);
		
		if(_myInitializedModules.size() > 0) return;
		
		System.exit(0);
	}

	private void startModules(){
		if(_myProgress != CCApplicationProgress.CALLED_TO_START)return;
		
		for(CCAppModule<?> myModule:_myModules){
			myModule.start();
		}
		
		_myProgress = CCApplicationProgress.RUNNING;
	}
	
	/**
	 * Starts your Application with the settings you made in the manager.
	 */
	public void start(){
		CCLog.info("call to start "+ _myInitializedModules.size() + ":" + _myModules.size());
		try {
			
			if(_myInitializedModules.size() == _myModules.size()){
				_myProgress = CCApplicationProgress.CALLED_TO_START;
				startModules();
			}else{
				_myProgress = CCApplicationProgress.CALLED_TO_START;
			}
			
		} catch (Exception e) {
			throw new RuntimeException("COULD NOT START APPLICATION:",e);
		}
	}
	
	/**
	 * Stops the application that is currently running.
	 */
	public void stop() {
		for(CCAppModule<?> myModule:_myModules){
			myModule.stop();
		}
	}
	
	/**
	 * Use this method to get a reference to the running application. Is only working after
	 * you have started the application. This can be useful if you start your application
	 * from inside another program and need access to it.
	 * @return a reference to your application
	 */
	public Object app(){
		return _myApp;
	}
	
	
}
