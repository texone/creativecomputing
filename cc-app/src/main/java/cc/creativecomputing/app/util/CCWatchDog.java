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
package cc.creativecomputing.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;

/**
 * @author info
 * 
 */
public class CCWatchDog extends Thread{
	
	private interface CCWatchDogCommandHandler{
		public void onCommand(final String theValue);
	}
	
	private class CCWatchDogFrameRateRestart implements CCWatchDogCommandHandler{
		
		private float _myMinFrameRate;
		
		private CCWatchDogFrameRateRestart(final float theMinFrameRate) {
			_myMinFrameRate = theMinFrameRate;
		}

		/* (non-Javadoc)
		 * @see cc.creativecomputing.app.CCWatchDog.CCWatchDogCommandHandler#onCommand(java.lang.String)
		 */
		public void onCommand(String theValue) {
			try {
				float myFrameRate = Float.parseFloat(theValue);
				if(myFrameRate < _myMinFrameRate)endProcess();
			} catch (NumberFormatException e) {
				CCLog.error("# COULD NOT READ FRAMERATE:"+theValue);
			}
		}
		
	}
	
	private int _myTime;
	private int _myStartTime;
	private int _myRestartTime;
	private int _myWaitTime;
	private boolean _myStartedProcess;
	
	private int _myOffTime = 0;
	private int _myMaxOffTime = -1;
	
	private String _myClassName;
	
	private List<String> _myLibraries = new ArrayList<String>();
	private List<String> _myVirtualMachineOptions = new ArrayList<String>();
	
	private Map<String, List<CCWatchDogCommandHandler>> _myCommandHandlerMap = new HashMap<String, List<CCWatchDogCommandHandler>>();
	
	private ProcessBuilder _myProcessBuilder;
	private Process _myProcess;
	
	public CCWatchDog() {
		super();
		loadSettings("process.xml");
		
		List<String> myArgumentList = new ArrayList<String>();
		myArgumentList.add("java");
		myArgumentList.add("-cp");
		myArgumentList.addAll(_myLibraries);
		myArgumentList.addAll(_myVirtualMachineOptions);
		myArgumentList.add(_myClassName);
		
		_myProcessBuilder = new ProcessBuilder(myArgumentList);
		_myStartedProcess = false;
		_myWaitTime = _myStartTime;
	}
	
	private void loadSettings(final String theFile) {
		CCDataElement myProcessXML = CCXMLIO.createXMLElement(CCNIOUtil.dataPath(theFile));
		System.out.println(CCNIOUtil.dataPath(theFile));
		CCDataElement myStartTimeXML = myProcessXML.child("starttime");
		if(myStartTimeXML != null)_myStartTime = myStartTimeXML.intContent(0);

		CCDataElement myRestartTimeXML = myProcessXML.child("restarttime");
		if(myRestartTimeXML != null)_myRestartTime = myRestartTimeXML.intContent(0);
		
		CCDataElement myClassNameXML = myProcessXML.child("class");
		if(myClassNameXML == null)throw new RuntimeException("You have to define a class to start inside the process.xml!");
		_myClassName = myClassNameXML.content();
		
		CCDataElement myClassPathXML = myProcessXML.child("classpath");
		if(myClassPathXML == null)throw new RuntimeException("You have to define a classpath inside the process.xml!");
		
		for(CCDataElement myLibXML:myClassPathXML) {
			_myLibraries.add(myLibXML.content());
		}
		
		CCDataElement myVMParametersXML = myProcessXML.child("vm_options");
		if(myVMParametersXML != null) {
			for(CCDataElement myVMParameterXML:myVMParametersXML) {
				_myVirtualMachineOptions.add(myVMParameterXML.content());
			}
		}
		
		loadRestartOptions(myProcessXML.child("restart"));
		
	}
	
	private void loadRestartOptions(CCDataElement theRestartOptionsXML){
		if(theRestartOptionsXML == null) return;
		
		CCDataElement myMinFrameRateXML = theRestartOptionsXML.child("minFrameRate");
		if(myMinFrameRateXML != null) {
			addCommandHandler("-frameRate", new CCWatchDogFrameRateRestart(myMinFrameRateXML.floatContent()));
		}
		CCDataElement myMaxOffTimeXML = theRestartOptionsXML.child("maxOffTime");
		if(myMaxOffTimeXML != null) {
			_myMaxOffTime = myMaxOffTimeXML.intContent();
		}
	}
	
	private void addCommandHandler(final String theCommand, final CCWatchDogCommandHandler theCommandHandler) {
		if(!_myCommandHandlerMap.containsKey(theCommand)) {
			_myCommandHandlerMap.put(theCommand, new ArrayList<CCWatchDogCommandHandler>());
		}
		_myCommandHandlerMap.get(theCommand).add(theCommandHandler);
	}
	
	private void parseTextLine(final String theLine) {
		if(!theLine.startsWith("-")) {
			CCLog.error("Can not parse arguments:" + theLine);
			return;
		}
		
		String[] myCommandArray = theLine.split(" ",2);
		String myCommand = myCommandArray[0];
		String myValue = "";
		if(myCommandArray.length > 1) {
			myValue = myCommandArray[0];
		}
		
		List<CCWatchDogCommandHandler> myCommandHandlers = _myCommandHandlerMap.get(myCommand);
		if(myCommandHandlers != null) {
			for(CCWatchDogCommandHandler myCommandHandler:myCommandHandlers) {
				myCommandHandler.onCommand(myValue);
			}
		}
	}
	
	/**
	 * Ends the active process and sets the parameters so that the process will be restarted
	 */
	private void endProcess() {
		_myProcess.destroy();
	}
	
	public void run() {
		try {
			while (true) {
				CCLog.info(_myTime);
				long myTime = System.currentTimeMillis();
				if(!_myStartedProcess && _myTime >= _myWaitTime * 1000) {
//					System.out.println(CCIOUtil.dataPath(thePath));
					_myProcess = _myProcessBuilder.start();
					_myStartedProcess = true;
				}
				
				if(_myStartedProcess) {
					try {
						CCLog.info(_myProcess.exitValue());
						CCLog.info("RESTART");
						_myWaitTime = _myRestartTime;
						_myStartedProcess = false;
						_myTime = 0;
					} catch (IllegalThreadStateException e) {
						// ignore this process is still running which is exactly what we want
					}

					Scanner scanner = new Scanner(_myProcess.getInputStream());
					while (scanner.hasNextLine()) {
						_myOffTime = 0;
						parseTextLine(scanner.nextLine());
					}
					scanner.close();
					
					scanner = new Scanner(_myProcess.getErrorStream());
					while (scanner.hasNextLine())
						CCLog.info(scanner.nextLine());
					scanner.close();

				}
				Thread.sleep(10);
				

				_myTime += System.currentTimeMillis() - myTime;
				_myOffTime += System.currentTimeMillis() - myTime;
				
				if(_myMaxOffTime > 0 && _myOffTime > _myMaxOffTime) {
					endProcess();
				}
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		CCWatchDog myWatchDog = new CCWatchDog();
		myWatchDog.start();
		
		while(true) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
