package cc.creativecomputing.graphics.scene.controllers;

import cc.creativecomputing.app.modules.CCAnimator;

public abstract class CCController <ObjectType extends CCControlledObject>{
	
	/**
	 * Time management. A controller may use its own time scale, and it
	 * specifies how times are to be mapped to application time. default = CLAMP
	 * @author christianr
	 *
	 */
	public enum CCrepeatTypeType {
		CLAMP, WRAP, CYCLE
	}

	// Member access.
	public CCrepeatTypeType repeatType = CCrepeatTypeType.CLAMP;
	public double minTime = 0f;
	public double maxTime = 0f;
	public double phase = 0.0f;
	public double frequency = 1.0f;
	public boolean active = true;

	/**
	 * The controlled object. This is a regular pointer rather than a
	 * smart pointer to avoid the reference cycle between mObject and
	 * 'this'.
	 */
	protected ObjectType _myObject;

	// The application time in milliseconds.
	protected double _myApplicationTime;

	protected CCController() {
		_myObject = null;
		_myApplicationTime = Float.MIN_VALUE;
	}

	public ObjectType object() {
		return _myObject;
	}

	/**
	 * This function is public because the class ControlledObject needs to set
	 * the object during a call to AttachController. Derived classes that
	 * manage a set of controllers also need to set the objects during a call
	 * to AttachController.
	 * @param object
	 */
	public void object(ObjectType object) {
		_myObject = object;
	}

	public void applicationTime(double theApplicationTime) {
		_myApplicationTime = theApplicationTime;
	}

	public double applicationTime() {
		return _myApplicationTime;
	}

	/**
	 * The animation update. The application time is in seconds.
	 * @param theApplicationTime
	 * @return
	 */
	public boolean update(CCAnimator theAnimator) {
		if (active) {
			_myApplicationTime = theAnimator.time();
			return true;
		}
		return false;
	}

	/**
	 * Conversion from application time units to controller time units.
	 * Derived classes may use this in their update routines.
	 * @param applicationTime
	 * @return
	 */
	protected double controlTime(double applicationTime) {
		double controlTime = frequency * applicationTime + phase;

		if (repeatType == CCrepeatTypeType.CLAMP) {
			// Clamp the time to the [min,max] interval.
			if (controlTime < minTime) {
				return minTime;
			}
			if (controlTime > maxTime) {
				return maxTime;
			}
			return controlTime;
		}

		double timeRange = maxTime - minTime;
		if (timeRange > 0.0) {
			double multiples = (controlTime - minTime) / timeRange;
			double integerTime = Math.floor(multiples);
			double fractionTime = multiples - integerTime;
			if (repeatType == CCrepeatTypeType.WRAP) {
				return minTime + fractionTime * timeRange;
			}

			// repeatType == WM5_RT_CYCLE
			if (((int) integerTime) % 2 == 1) {
				// Go backward in time.
				return maxTime - fractionTime * timeRange;
			} else {
				// Go forward in time.
				return minTime + fractionTime * timeRange;
			}
		}

		// The minimum and maximum times are the same, so return the minimum.
		return minTime;
	}

}