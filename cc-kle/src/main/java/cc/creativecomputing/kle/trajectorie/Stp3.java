package cc.creativecomputing.kle.trajectorie;

import cc.creativecomputing.math.CCMath;

/**
 * Smooth Trajectory Planner, 2nd order (Stp3) \author Robert Haschke, Erik
 * Weitnauer \date 2006, 2007
 *<p>
 * This class is for planning time optimal 2nd order trajectories. Given a start
 * and target position, an inital and maximum value for velocity and a maximum
 * value for acceleration you pass these values to the planFastestProfile
 * method. Afterward the resulting profile can be accessed by various methods,
 * e.g. getTimeArray and getAccArray. To get the movement parameters at specific
 * times, call the move(...) method.
 *<p>
 * \image html 3phases.jpg "typical time optimal 2nd order trajectory" \image
 * latex 3phases.eps "typical time optimal 2nd order trajectory"
 *<p>
 * A time optimal profile can be stretched to any desired duration by calling
 * the scaleToDuration method. ThiscaleToscaleToDurationDurations can be used to
 * synchornise several movements made at the same time.
 * <p>
 * Both planFastestProfile and scaleToDuration throw a logic_error in case they
 * were unable to find a solution. In praxis this should never occour, so you
 * better take it serious if it does and provide some kind of fallback - for
 * example stopping the motion of the joint immedeately.
 *<p>
 * For further information about the theory of the used algorithm \see "On-Line
 * Planning of Time-Optimal, Jerk-Limited Trajectories"; R. Haschke, E.
 * Weitnauer, H. Ritter; 2007
 *<p>
 * Both the time and the acceleration arrays start at index 1 instead
 * of 0 and therefore the arrays are double[4].
 */
public class Stp3 extends StpBase {

	public static enum StpProfile {
		/**
		 * trapezoid shaped profile
		 */
		PROFILE_T,
		/**
		 * wedge shaped profile
		 */
		PROFILE_W,
		/**
		 * fullstop profile
		 */
		PROFILE_STOP
	}

	// ?[0] is start condition, ?[3] is end condition.
	private double[] _x = new double[4];
	private double[] _v = new double[4];
	private double[] _t = new double[4]; // t[i] is point in time, not intervall

	private double[] _a = new double[4]; // a[i] is acceleration between x[i-1]
											// and x[i]
	private double _vmax, _amax; // limit for acceleration and velocity
	private StpProfile _sProfileType;
	private boolean _plannedProfile; // flag indication whether a profile was
										// computed
	private boolean _bIsddec; // flag indicating deceleration in first phase

	public Stp3() {
		_plannedProfile = false;
	}

	// functions for getting information about the calculated profile
	/// returns one of the defined string constants describing the shape of the
	// acceleration graph
	public StpProfile getProfileType() {
		return _sProfileType;
	}

	/// returns true, when both acceleration in first and third phase have same
	/// direction
	public boolean isDoubleDecProfile() {
		return _bIsddec;
	}

	/// returns true, when profile is trapezoid (t(2) is not zero)
	public boolean isTrapezoid() {
		return (_t[2] != _t[1]);
	}

	/// returns time of switch between phase (i) and (i+1) of the profile.
	public double getSwitchTime(int i) {
		if (i < 0 || i > 3)
			throw new IllegalArgumentException("Index for time must be in {0,...,3}!");
		return _t[i];
	}

	/// returns duration of phase (i).
	public double getTimeIntervall(int i) {
		if (i <= 0 || i > 3)
			throw new IllegalArgumentException("Index for time must be in {1,...,3}!");
		return _t[i] - _t[i - 1];
	}

	/// returns total duration of the trajectory.
	public double getDuration() {
		return getSwitchTime(3);
	}

	/// returns index of time intervall the passed times lies in
	public int getPhaseIndex(double t) {
		if (t < _t[1])
			return 0;
		if (t < _t[2])
			return 1;
		if (t < _t[3])
			return 2;
		return 3;
	}

	/// < @param[out] a array of acceleration values
	public void getAccArray(double[] a) {
		for (int i = 0; i < 4; i++)
			a[i] = _a[i];
	}

	/// < @param[out] t array of time points{
	public void getTimeArray(double[] t) {
		for (int i = 0; i < 4; i++)
			t[i] = _t[i];
	}

	/// < @param[out] t array of time intervalls
	public void getTimeIntArray(double[] t) {
		t[0] = 0;
		t[1] = _t[1];
		t[2] = _t[2] - t[1];
		t[3] = _t[3] - _t[2];
		t[4] = _t[4] - _t[3];
	}

	/// get the position, velocity and acceleration at passed time >= 0
	public void move(double t, StpData theData) {
		int i = getPhaseIndex(t);
		if (i == 3) {
			theData.position = _x[3];
			theData.velocity = theData.acceleration = 0;
		} else {
			calcaTrack(t - _t[i], _x[i], _v[i], _a[i + 1], theData);
			theData.acceleration = _a[i + 1];
		}
	}

	@Override
	public double pos(double t) {
		StpData myData = new StpData();
		move(t, myData);
		return myData.position;
	}

	@Override
	public double vel(double t) {
		StpData myData = new StpData();
		move(t, myData);
		return myData.velocity;
	}

	@Override
	public double acc(double t) {
		StpData myData = new StpData();
		move(t, myData);
		return myData.acceleration;
	}

	/// Function for calculating the time optimal profile. Read the results with
	/// e.g. getTimeArray(..) or move(...).
	public double planFastestProfile(double x0, double xtarget, double v0, double vmax, double amax) {
		// check, whether vmax and amax are greater than zero
		if (vmax < 0 || amax < 0)
			throw new IllegalArgumentException("vmax and amax must be positive!");

		// first set object fields
		_vmax = vmax;
		_amax = amax;
		_x[0] = x0;
		_x[3] = xtarget;
		_v[0] = v0;
		_a[0] = 0;
		_t[0] = 0;

		// Do the planning algorithm --> we get back the jerks and time points.
		planProfile();
		// check if we have valid times, if not, throw a logic error
		if (!isValidMovement())
			throw new IllegalArgumentException("Invalid solution.");

		// calculate the missing x and v values
		for (int i = 1; i < 4; i++) {
			// calc x,v values for next switch point
			StpData myData = new StpData();
			calcaTrack(_t[i] - _t[i - 1], _x[i - 1], _v[i - 1], _a[i], myData);
			_x[i] = myData.position;
			_v[i] = myData.velocity;
		}

		_plannedProfile = true;

		return _t[3];
	}

	/// scale a planned profile to a longer duration
	@Override
	public double scaleToDuration(double newDuration) {
		if (!_plannedProfile)
			return 0;

		if (newDuration <= _t[3])
			return _t[3]; // only enlarge duration

		double A, B, tcruise, deltaT, diff, stopT, stop, dir;

		tcruise = _t[2] - _t[1]; // old cruising time
		A = CCMath.abs(_v[1]) * (newDuration - _t[3]) / _amax;
		B = newDuration - _t[3] + tcruise;

		/* compute time delta to steel from acc + decl phase */
		deltaT = -B / 2. + CCMath.sqrt(B * B / 4. + A); /* > 0 */

		if (!_bIsddec && (_t[1] - deltaT >= 0)) {
			_t[1] -= deltaT;
			_t[2] = newDuration - (_t[3] - _t[2]) + deltaT;
			_t[3] = newDuration;

			_v[1] = _v[0] + _a[1] * _t[1];
		} else {
			/* compute time needed for full stop */
			dir = -CCMath.sign(_v[0]); // direction of acceleration to stop
			stopT = CCMath.abs(_v[0] / _amax);
			/* compute final position after full stop */
			stop = _x[0] + stopT * (_v[0] + dir * _amax / 2. * stopT);

			/* cruising speed: */
			_v[1] = (_x[3] - stop) / (newDuration - stopT);
			/* turn acceleration into deceleration: */
			if (!_bIsddec) {
				_a[1] = -_a[1];
				_bIsddec = true;
			}
			/* time to reach cruising speed: */
			_t[1] = CCMath.abs(_v[1] - _v[0]) / _amax;
			_t[2] = newDuration - (stopT - _t[1]);
			_t[3] = newDuration;
		}

		StpData myData = new StpData();
		// calculate the missing x and v values
		for (int i = 1; i < 4; i++) {
			// calc x,v values for next switch point
			calcaTrack(_t[i] - _t[i - 1], _x[i - 1], _v[i - 1], _a[i], myData);
			_x[i] = myData.position;
			_v[i] = myData.velocity;
		}

		// check if we have valid times, if not, throw a logic error
		if (!isValidMovement())
			throw new RuntimeException("No solution found for stretched 3stp profile.");

		return _t[3];
	}

	/// Returns at which time the cruising phase ends.
	@Override
	public double getEndOfCruisingTime() {
		return _t[2];
	}

	/// convert to string
	@Override
	public String toString() {
		// if (_plannedProfile) {
		// if (_bIsddec) oss << "double decceleration ";
		// else oss << "canonical ";
		// oss << getProfileType() << " (t=";
		// writedArrayToStream(oss, _t, 1,3);
		// oss << ", a=";
		// writedArrayToStream(oss, _a, 1,3);
		// oss << ", x0 = " << _x[0] << ", xTarget = " << _x[3] << ", v0 = ";
		// oss << _v[0] << ", vmax = " << _vmax << ", amax = " << _amax << ")";
		// } else {
		// oss << "unplanned profile";
		// }
		return super.toString();
	}

	/// returns true if all time intervalls are non negative and all
	/// acceleration
	/// values are inside the limits.
	protected boolean isValidMovement() {
		if (_t[0] < 0)
			return false;
		for (int i = 1; i < 4; i++)
			if (_t[i] < _t[i - 1])
				return false;
		for (int i = 1; i < 4; i++)
			if (CCMath.abs(_a[i]) > _amax)
				return false;
		return true;
	}

	private void calcaTrack(double dt, double x0, double v0, double a, StpData theData) {
		theData.position = x0 + v0 * dt + 0.5 * a * dt * dt;
		theData.velocity = v0 + a * dt;
	}

	private void planProfile() {
		double dir, stop, deltaP, deltaT, w;
		double target = _x[3];

		_a[2] = 0;
		_bIsddec = false;

		/* compute time needed for full stop */
		dir = -CCMath.sign(_v[0]); // direction of acceleration to stop
		stop = CCMath.abs(_v[0]) / _amax;

		/* compute final position after full stop */
		stop = _x[0] + stop * (_v[0] + dir * _amax / 2. * stop);

		if (target == stop) { // after full stop, we are already at the goal
			_t[1] = _t[2] = 0; // no acceleration, no cruising phase
			_t[3] = CCMath.abs(_v[0]) / _amax;
			_a[1] = -dir * _amax;
			_a[3] = dir * _amax;
			return;
		} else {
			/* direction of cruising phase */
			dir = CCMath.sign(target - stop);

			/* (typical) direction of acceleration / deceleration */
			_a[1] = dir * _amax;
			_a[3] = -dir * _amax;

			/* time to reach cruising speed dir * _vmax (clipping to zero?) */
			_t[1] = (dir * _vmax - _v[0]) / _a[1];
			if (_t[1] < 0) {
				// deceleration to lower max speed than current speed needed
				_a[1] = -_a[1];
				_t[1] = -_t[1];
				_bIsddec = true;
			}

			/* time to stop from cruising */
			_t[2] = _vmax / _amax;

			/* pos change from acceleration and deceleration only: */
			deltaP = _t[1] * (_v[0] + _a[1] / 2. * _t[1]);
			deltaP += _t[2] * (dir * _vmax + _a[3] / 2. * _t[2]);

			/* time in cruising phase: */
			deltaT = (target - _x[0] - deltaP) / (dir * _vmax);
			if (deltaT >= 0.0) { // plan a complete (trapezoidal) profile:
				_t[3] = _t[1] + deltaT + _t[2]; // duration
				_t[2] = _t[3] - _t[2];
			} else { // plan an incomplete (triangular) profile:
				/*
				 * w - speed at switching between acceleration and deceleration
				 */
				w = dir * CCMath.sqrt(dir * _amax * (target - _x[0]) + _v[0] * _v[0] / 2.);
				_t[1] = (w - _v[0]) / _a[1];
				_t[2] = _t[1];
				_t[3] = _t[1] + CCMath.abs(w / _a[3]); // duration
			}
		}
	}
};