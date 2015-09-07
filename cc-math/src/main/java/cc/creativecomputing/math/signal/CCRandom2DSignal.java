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
package cc.creativecomputing.math.signal;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

/**
 * @author max goettner
 *
 */
public class CCRandom2DSignal extends CCSignal{


	@CCProperty(name="nSteps", min=1, max=1000)
	int _cNSteps = 0;
	
	@CCProperty(name="speed", min=0, max=1)
	double R = 0.1f;
	
	@CCProperty(name="direction change min", min=0, max=1)
	double dMin = 0.1f;
	
	@CCProperty(name="direction change max", min=0, max=1)
	double dMax = 0.5f;
	
	
	int currentStep = 0;
	double phi = 0;
	
	CCVector3 step = new CCVector3();
	CCVector3 nextPosition    = new CCVector3();
	CCVector3 currentPosition = new CCVector3();

	
	private void getNewRandomPos() {
		//nextPosition = new CCVector3(currentPosition);
		phi += 2 * CCMath.PI * CCMath.random(dMin, dMax);

		step.x = R*CCMath.cos(phi) / (double)_cNSteps; //(nextPosition.x - currentPosition.x) / (double)_cNSteps;
		step.y = R*CCMath.sin(phi) / (double)_cNSteps; //(nextPosition.y - currentPosition.y) / (double)_cNSteps;
		step.z = 0; // (nextPosition.z - currentPosition.z) / (double)_cNSteps;
	}
	
	private void nextStep() {
		
		if (currentPosition.x + step.x > 1 || currentPosition.x + step.x < -1) step.x = 0; //-step.x;
		if (currentPosition.y + step.y > 1 || currentPosition.y + step.y < -1) step.y = 0; // -step.y;
		
		currentPosition.add(step);
		currentStep += 1;
		if (currentStep == _cNSteps) {
			currentStep = 0;
			getNewRandomPos();
		}
	}
	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		nextStep();
		return new double[]{currentPosition.x, currentPosition.y, currentPosition.z};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double, double)
	 */
	@Override
	public double[] signalImpl(double theX, double theY) {
		nextStep();
		return new double[]{currentPosition.x, currentPosition.y};
	}
	
	/* (non-Javadoc)
	 * @see cc.creativecomputing.math.signal.CCSignal#signalImpl(double)
	 */
	@Override
	public double[] signalImpl(double theX) {
		nextStep();
		return new double[]{currentPosition.x};
	}

}
