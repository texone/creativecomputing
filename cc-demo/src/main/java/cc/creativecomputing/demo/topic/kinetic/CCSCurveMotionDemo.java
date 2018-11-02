package cc.creativecomputing.demo.topic.kinetic;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCVector2;

public class CCSCurveMotionDemo extends CCGL2Adapter {
	
	// Givens
	double ta = 3e6;     // acceleration time (microsec)
	double td = 3e6;     // decelleration time (microsec)
	double Vm = 3200;    // steady state velocity (pulse/sec)
	double Pt = 12800;    // total number of pulses for move (1600 steps per rev)

	// Other variables
	double dly;           // stepper pulse delay (microsec)
	double t = td / 9;    // current time (microsec)  -  You need to seed the initial time with something > 0
//	                             so you don't calculate to long of a delay
	double t12;           // time during constant velocity (microsec)

	int count = 0;      // count the number of pulses
	double Perr = 0;       // error in position

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		// Calculate the time at constant velocity
		  t12 = (Pt / (Vm / 1e6)) - 0.5 * (ta + td);
	}
	
	private List<CCVector2> myPositions = new ArrayList<>();

	@Override
	public void update(CCAnimator theAnimator) {
		
		for(int i = 0; i < 100 || t > (ta + t12 + td);i++) {
			// Decide which part of the velocity curve your at
			if (t < ta) {                                     // Acceleration
				CCLog.info ("Acceleration Curve");
			    dly = (ta) / (2 * (Vm / 1e6) * t);
			}
			else if (t >= ta && t < (ta + t12)) {             // Constant velocity
				CCLog.info  ("Constant Velocity");
			    dly = 1 / (2 * (Vm / 1e6));
			}
			else if (t >= (ta + t12) && t < (ta + t12 + td)) { // Deceleration
				CCLog.info  ("Deceleration Curve");
			    dly = 1 / (2 * ((Vm / 1e6) - (Vm / (1e6 * td)) * (t - ta - t12)));
			}
	
			t = t + 2 * dly; // update the current time
	
			  // Move stepper one pulse using delay just calculated
	//		digitalWrite(stepPin, HIGH);
	//		delayMicroseconds(dly);
	//		digitalWrite(stepPin, LOW);
	//		delayMicroseconds(dly);
			count ++;
		}
		CCLog.info ("dly: "+dly+" microsec");
		CCLog.info ("Current time: " + t + " microsec");
		// The move is finished
		if (t > (ta + t12 + td)) {
			CCLog.info ("Move Complete");
			CCLog.info ("Total steps indexed: " + count);
			CCLog.info ("Error: " + Perr);

		    // Correct for any position error due to rounding
//		    Perr = Pt - count;
//		    if (Perr < 0) {
//		      digitalWrite(dirPin, 1 ^ digitalRead(dirPin)); // reverse the stepper direction
//		      delay(50);
//		      Perr = -1 * Perr;
//		    }
//		    for (; Perr > 0;) {
//		      digitalWrite(stepPin, HIGH);
//		      delayMicroseconds(dly);
//		      digitalWrite(stepPin, LOW);
//		      delayMicroseconds(dly);
//		      Perr--;
//		    }

		    count = 0;
		    t = td / 9;

		}
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCSCurveMotionDemo demo = new CCSCurveMotionDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
