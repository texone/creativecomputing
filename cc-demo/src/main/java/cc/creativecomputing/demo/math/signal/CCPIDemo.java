package cc.creativecomputing.demo.math.signal;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCPIDemo extends CCGL2Adapter {

	/**
	 * A proportional–integral–derivative controller (PID controller or three
	 * term controller) is a control loop feedback mechanism widely used in
	 * industrial control systems and a variety of other applications requiring
	 * continuously modulated control. A PID controller continuously calculates
	 * an error value {\displaystyle e(t)} e(t) as the difference between a
	 * desired setpoint (SP) and a measured process variable (PV) and applies a
	 * correction based on proportional, integral, and derivative terms (denoted
	 * P, I, and D respectively) which give the controller its name.
	 * <p>
	 * In practical terms it automatically applies accurate and responsive
	 * correction to a control function. An everyday example is the cruise
	 * control on a road vehicle; where external influences such as gradients
	 * would cause speed changes, and the driver has the ability to alter the
	 * desired set speed. The PID algorithm restores the actual speed to the
	 * desired speed in the optimum way, without delay or overshoot, by
	 * controlling the power output of the vehicle's engine.
	 * 
	 * @author christianr
	 *
	 */
	private class CCPID {
		/* working variables */

		long lastTime;

		double Input;
		double Output;
		double Setpoint;

		double errSum;
		double lastErr;

		@CCProperty(name = "kp", min = 0, max = 1)
		double kp;
		@CCProperty(name = "ki", min = 0, max = 1)
		double ki;
		@CCProperty(name = "kd", min = 0, max = 1)
		double kd;

		@CCProperty(name = "sample time", min = 0, max = 1000)
		int SampleTime = 1000; // 1 sec

		void compute() {
			/* How long since we last calculated */
			long now = System.currentTimeMillis();
			int timeChange = (int) (now - lastTime);
			if (timeChange >= SampleTime) {

				/* Compute all the working error variables */
				double error = Setpoint - Input;
				errSum += error;
				double dErr = (error - lastErr);

				/* Compute PID Output */
				Output = kp * error + ki * errSum + kd * dErr;

				/* Remember some variables for next time */
				lastErr = error;
				lastTime = now;
			}
		}

		public void SetTunings(double Kp, double Ki, double Kd) {
			double SampleTimeInSec = ((double)SampleTime)/1000;
			   kp = Kp;
			   ki = Ki * SampleTimeInSec;
			   kd = Kd / SampleTimeInSec;
		}
		
		void SetSampleTime(int NewSampleTime)
		{
			
		   if (NewSampleTime <= 0)return;
		   
		      double ratio  = (double)NewSampleTime / (double)SampleTime;
		      ki *= ratio;
		      kd /= ratio;
		      SampleTime = NewSampleTime;
		   
		}
	}

	@CCProperty(name = "signal")
	private CCMixSignal _cMixSignal = new CCMixSignal();
	
	@CCProperty(name = "scale", min = 0, max = 200)
	private double _cscale = 100;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.ortho();
		g.clear();
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(int x = 0; x < g.width();x++){
			g.vertex(x, _cMixSignal.value(x / 100d) * _cscale + 200);
		}
		g.endShape();
	}

	public static void main(String[] args) {

		CCPIDemo demo = new CCPIDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
