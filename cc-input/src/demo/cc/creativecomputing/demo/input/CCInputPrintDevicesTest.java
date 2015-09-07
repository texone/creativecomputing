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
package cc.creativecomputing.demo.input;

import cc.creativecomputing.app.CCApplication;
import cc.creativecomputing.app.util.logging.CCLog;
import cc.creativecomputing.input.CCInputDevice;
import cc.creativecomputing.input.CCInputListener;
import cc.creativecomputing.input.CCInputModule;

public class CCInputPrintDevicesTest implements CCInputListener {

	@Override
	public void start(CCInputModule theModule) {
		
		theModule.printDevices();

		for (int i = 0; i < theModule.numberOfDevices(); i++) {
			CCInputDevice device = theModule.device(i);

			CCLog.info(device.name() + " has:");
			CCLog.info(" " + device.numberOfSliders() + " sliders");
			CCLog.info(" " + device.numberOfButtons() + " buttons");
			CCLog.info(" " + device.numberOfSticks() + " sticks");

			device.printSliders();
			device.printButtons();
			device.printSticks();
		}
	}
	
	@Override
	public void stop(CCInputModule theInput) {
		// TODO Auto-generated method stub
		
	}

	public static void main(String[] args) {
		CCApplication manager = new CCApplication(CCInputPrintDevicesTest.class);
		
		CCInputModule myInputModule = new CCInputModule();
		manager.addModule(myInputModule);
		
		manager.start();
	}
}
