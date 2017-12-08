package cc.creativecomputing.kle.trajectorie;

import cc.creativecomputing.math.CCMath;

/**
 * Smooth Trajectory Planner, 3rd order (Stp7) \author Erik Weitnauer \date 2007
 *
 * This class is for planning time optimal 3rd order trajectories. Given a start
 * and target position, an inital and maximum value for velocity and
 * acceleration and a maximum value for jerk (change of acceleration) you pass
 * these values to the planFastestProfile method. Afterward the resulting
 * profile can be accessed by various methods, e.g. getTimeArray and
 * getJerkArray. To get the movement parameters at specific times, call the
 * move(...) method.
 *
 * \image html 3phases.jpg "typical time optimal 2nd order trajectory" \image
 * latex 3phases.eps "typical time optimal 2nd order trajectory"
 *
 * A time optimal profile can be stretched to any desired duration by calling
 * the scaleToDuration method. This can be used to synchornize several movements
 * made at the same time.
 * 
 * Both planFastestProfile and scaleToDuration throw a std::logic_error in case
 * they were unable to find a solution. In praxis could occour for some
 * numerically unstable cases, e.g. when extremely stretching a movement. There
 * should be some kind of fallback behaviour if this occours. For example the
 * much simpler and therefore more stable 2nd order Trajectory planner could be
 * used.
 * 
 * However, the only numerically problematic cases where arising when trying to
 * stretch a very short movement to a long time - which in praxis makes no
 * sence, since the movement itself is so small that it is barely recognisable
 * and therefore must not be considered when synchronizing movements of several
 * joints. To avoid these numerical extreme cases, the stretch algorithm is
 * slightly modified: In case the algorithm does not lead to an solution and the
 * movement should be stretched to a time more than MAX_STRETCH_FACTOR times of
 * the optimal time, the original movement is returned unaltered. This accords
 * to doing the movement at once and just waiting after finshing it.
 * 
 * For further information about the theory of the used algorithm \see "On-Line
 * Planning of Time-Optimal, Jerk-Limited Trajectories"; R. Haschke, E.
 * Weitnauer, H. Ritter; 2007
 *
 * \warning Both the time and the jerk arrays start at index 1 instead of 0 and
 * therefore the arrays are double[8].
 */

public class Stp7 extends StpBase {

	// string constants for different profile types

	public static enum Stp7Profile {
		PROFILE_TT, /// < trapezoid - trapezoid shaped acceleration profile
		PROFILE_WT, /// < wedge- trapezoid shaped acceleration profile
		PROFILE_TW, /// < trapezoid - wedge shaped acceleration profile
		PROFILE_WW, /// < wedge - wedge shaped acceleration profile
		PROFILE_STOP; /// < stop profile
	}

	// ?[0] is start condition, ?[7] is end condition.
	private double[] _x = new double[8], _v = new double[8], _a = new double[8], _t = new double[8]; // t[i]
																										// is
																										// point
																										// in
																										// time,
																										// not
																										// intervall
	private double[] _j = new double[8]; // j[i] is jerk between x[i-1] and x[i]
	private double _vmax, _amax, _jmax;
	private Stp7Profile _sProfileType;
	private boolean _bIsddec;
	private boolean _bHasCruise;
	private boolean _plannedProfile;

	/**
	 * Errors when attempting to stretch a movement to a time more than this
	 * factor times the original duration are not thrown. Instead the profile is
	 * returned unchanged. Default value is 10.
	 */
	public static final double MAX_STRETCH_FACTOR = 10;

	// constructor
	public Stp7() {
		_plannedProfile = false;
	};

	// functions for getting information about the calculated profile
	public boolean isDoubleDecProfile() {
		return _bIsddec;
	}

	public boolean hasCruisingPhase() {
		return _bHasCruise;
	}

	public Stp7Profile getProfileType() {
		return _sProfileType;
	}

	public String getDetailedProfileType() {
		String result = "";
		if (isDoubleDecProfile())
			result = "ddec ";
		else
			result = "cano ";
		if (hasCruisingPhase())
			result += "c";
		result += getProfileType();
		return result;
	}

	/// 1 <= i <= 7. Returns the time of switch between phase (i) and (i+1) of
	/// the profile.
	public double getSwitchTime(int i) {
		if (i < 0 || i > 7)
			throw new RuntimeException("Index for time must be in {0,...,7}!");
		return _t[i];
	}

	/// 1 <= i <= 7. Returns the time length of phase (i).
	public double getTimeIntervall(int i) {
		if (i <= 0 || i > 7)
			throw new RuntimeException("Index for time must be in {1,...,7}!");
		return _t[i] - _t[i - 1];
	}

	@Override
	public double getDuration() {
		return getSwitchTime(7);
	}

	/// gives back in which time intervall the passed time lies inside. For
	/// t = 0 return 1, for t >= duration returns 7.
	public int getPhaseIndex(double t) {
		if (t <= _t[1])
			return 0;
		if (t <= _t[2])
			return 1;
		if (t <= _t[3])
			return 2;
		if (t <= _t[4])
			return 3;
		if (t <= _t[5])
			return 4;
		if (t <= _t[6])
			return 5;
		if (t <= _t[7])
			return 6;
		return 7;
	}

	// getters - they return a copy of the arrays.
	public void getJerkArray(double[] j) {
		for (int i = 0; i < 8; i++)
			j[i] = _j[i];
	}

	public void getTimeArray(double[] t) {
		for (int i = 0; i < 8; i++)
			t[i] = _t[i];
	}

	public void getTimeIntArray(double[] t) {
		t[0] = 0;
		t[1] = _t[1];
		for (int i = 2; i < 8; i++)
			t[i] = _t[i] - _t[i - 1];
	}

	// function for getting the pos/vel/acc/jerk at different times >= 0
	@Override
	public void move(double t, StpData theData) {
		int i = getPhaseIndex(t);
		if (i == 7) {
			theData.position = _x[7];
			theData.velocity = theData.acceleration = theData.jerk = 0;
		} else {
			calcjTrack(t - _t[i], _x[i], _v[i], _a[i], _j[i + 1], theData);
			theData.jerk = _j[i + 1];
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

	public double jer(double t) {
		StpData myData = new StpData();
		move(t, myData);
		return myData.jerk;
	}

	/// Function for calculating the time optimal profile. Returns the duration.
	/// @throws std::logic_error if no solution could be found.
	public double planFastestProfile(double x0, double xtarget, double v0, double vmax, double a0, double amax,
			double jmax) {
		// check, whether vmax, amax and jmax are greater than zero
		if (vmax < 0 || amax < 0 || jmax < 0)
			throw new RuntimeException("vmax, amax and jmax must be positive!");

		// first set object fields
		_vmax = vmax;
		_amax = amax;
		_jmax = jmax;
		_x[0] = x0;
		_x[7] = xtarget;
		_v[0] = v0;
		_a[0] = a0;
		_j[0] = 0;
		_t[0] = 0;

		// Do the planning algorithm --> we get back the jerks and times.
		planProfile();

		StpData myData = new StpData();
		for (int i = 1; i < 8; i++) {
			// calc x,v,a values for next switch point
			calcjTrack(_t[i] - _t[i - 1], _x[i - 1], _v[i - 1], _a[i - 1], _j[i], myData);
			_x[i] = myData.position;
			_v[i] = myData.velocity;
			_a[i] = myData.acceleration;
		}

		_plannedProfile = true;

		// test if solution is valid, if not the test method will throw an
		// exception
		testProfile(xtarget);

		return _t[7];
	}

	/**
	 * Function for scaling the profile to the given duration. Gives back the
	 * new duration. In case no solution could be found there are two
	 * possibilities: If the movement should be stretched to more than 10 time
	 * of the original duration, it is returned unchanged. Otherwise a
	 * std::logic_error is thrown.
	 * 
	 * @throws std::logic_error
	 */
	@Override
	public double scaleToDuration(double newDuration) {
		if (!_plannedProfile)
			return 0;
		// if (newDuration < getDuration()) return getDuration();
		if ((newDuration - getDuration()) < 0)
			return getDuration();

		convertTimePointsToIntervalls();

		// we can't really stretch a full-stop profile, so check for it first
		if ((isZero(_t[5])) && (isZero(_t[6])) && (isZero(_t[7]))) {
			_t[4] = newDuration - _t[1] - _t[2] - _t[3] - _t[4];
		} else {
			splitNoCruiseProfileTimeInt(_t, _j, _a[0]);
			// for the case that v(0) = vmax the jerk vector might be filled
			// with zeros
			// only in the first part --> we need to correct that.
			if (_j[1] == 0) {
				_j[1] = -CCMath.sign(_v[0]) * _jmax;
				_j[3] = -_j[1];
			}

			StpData myData = new StpData();
			calcjTracksTimeInt(_t, _j, 3, _x[0], _v[0], _a[0], myData);
			double dir = CCMath.sign(myData.velocity); // TODO: what happens
														// when dir=0?

			// check whether we must use the double deceleration branch
			boolean useDDec = false;
			double[] t_orig = new double[8];
			for (int i = 0; i < 8; i++)
				t_orig[i] = _t[i];
			double[] j_orig = new double[8];
			for (int i = 0; i < 8; i++)
				j_orig[i] = _j[i];
			double oldDuration = getDuration();
			{
				// Tests for a given profile, whether stretching it to the time
				// T would
				// nead a double deceleration profile.

				// folgenden einfachen algorithmus verwenden:
				// Profil erzeugen: erst a auf null, dann v auf null
				// ist dieses profil zu langsam --> double deceleration, sonst
				// normal

				if (CCMath.sign(_j[3]) != CCMath.sign(_j[5])) {
					// that was easy - it already is a double dec. profile :)
					if ((CCMath.abs(_a[0]) > _amax) && (CCMath.sign(_a[0]) == CCMath.sign(_j[5])))
						_j[1] = _j[3];
					useDDec = true;
				} else {
					// If the velocity change within the deceleration part is
					// larger in magnitude than
					// the velocity change within acceleration part (starting
					// from max velocity,
					// i.e. at a=0, the cutting process in findProfileNormal may
					// lead to the
					// situation, where no more area can be cut from the
					// acceleration part,
					// although the desired duration T is not yet reached. In
					// this case we have
					// to switch to the double deceleration branch as well.
					// We compute a profile, which immediately decreases
					// acceleration to zero
					// in the first (acceleration) phase and subsequently does a
					// full stop to zero
					// velocity in the second phase. In between an appropriate
					// cruising phase is
					// inserted to reach the final position. If this profile
					// still is to short,
					// we need to switch to a double deceleration profile.
					double j1, t1;
					double[] tdec = new double[4], jdec = new double[4];
					if (_a[0] == 0) {
						t1 = 0;
						// profile to reach zero velocity, starting from a0=0,
						// v0
						Stp3 stp3Dec = new Stp3();
						stp3Dec.planFastestProfile(_v[0], 0, _a[0], _amax, _jmax);
						stp3Dec.getTimeIntArray(tdec);
						stp3Dec.getAccArray(jdec);
					} else {
						// jerk to decrease acceleration to zero
						j1 = -CCMath.sign(_a[0]) * _jmax;
						// time needed to reach zero acceleration
						t1 = CCMath.abs(_a[0] / _jmax);
						// position and velocity reached after this time
						// double a1, v1, x1;
						calcjTrack(t1, _x[0], _v[0], _a[0], j1, myData); // a1
																			// ==
																			// 0
						// profile to reach zero velocity, starting from v1
						Stp3 stp3Dec = new Stp3();
						stp3Dec.planFastestProfile(myData.velocity, 0, myData.acceleration, _amax, _jmax);
						stp3Dec.getTimeIntArray(tdec);
						stp3Dec.getAccArray(jdec);
					}

					// If the a(t) profile in deceleration part has the same
					// direction as before,
					// we may need to switch to a double deceleration profile.
					// Otherwise, the velocity change in deceleration phase was
					// smaller than
					// in acceleration phase, hence no switch is neccessary.
					if (CCMath.sign(jdec[1]) == CCMath.sign(_j[5])) { // we may
																		// need
																		// to
																		// switch
						if (CCMath.sign(_a[0]) == CCMath.sign(_j[5])) {
							_t[1] = 0;
							_t[2] = 0;
							_t[3] = t1;
							_t[4] = 0;
							_t[5] = tdec[1];
							_t[6] = tdec[2];
							_t[7] = tdec[3];
							_j[1] = jdec[1];
							_j[2] = jdec[2];
							_j[3] = jdec[3];
							_j[4] = 0;
							_j[5] = jdec[1];
							_j[6] = jdec[2];
							_j[7] = jdec[3];
						} else {
							_t[1] = t1;
							_t[2] = 0;
							_t[3] = 0;
							_t[4] = 0;
							_t[5] = tdec[1];
							_t[6] = tdec[2];
							_t[7] = tdec[3];
							_j[1] = jdec[1];
							_j[2] = jdec[2];
							_j[3] = jdec[3];
							_j[4] = 0;
							_j[5] = jdec[1];
							_j[6] = jdec[2];
							_j[7] = jdec[3];
						}
						// for the case that we need to reduce the acceleration
						// first,
						// because its over the limit, we need to rearrage the
						// jerks a
						// bit...
						if ((CCMath.abs(_a[0]) > _amax) && (CCMath.sign(_a[0]) == CCMath.sign(_j[5]))) {
							double t1a = (CCMath.abs(_a[0]) - _amax) / _jmax;
							_t[1] = t1a;
							_t[2] = 0;
							_t[3] = t1 - t1a;
							_t[4] = 0;
							_t[5] = tdec[1];
							_t[6] = tdec[2];
							_t[7] = tdec[3];
							_j[1] = -jdec[1];
							_j[2] = jdec[2];
							_j[3] = jdec[3];
							_j[4] = 0;
							_j[5] = jdec[1];
							_j[6] = jdec[2];
							_j[7] = jdec[3];
						}
						// insert cruising phase, such that the target is still
						// reached
						adaptProfile(_t, _j, _x[7], _x[0], _v[0], _a[0]);
						useDDec = stillTooShort(_t, newDuration);
					} else {
						useDDec = false;
					}
				}
			}
			if (!useDDec) {
				for (int i = 0; i < 8; i++) {
					_t[i] = t_orig[i];
					_j[i] = j_orig[i];
				}
			}

			double da;
			da = _j[1] == _j[3] ? -1 : 1;

			try {
				if (useDDec) {
					// double deceleration branch
					planProfileStretchDoubleDec(newDuration, dir, da);
					_bHasCruise = (_t[4] != 0);
				} else {
					// normal profile branch
					_bIsddec = false;
					findProfileTypeStretchCanonical(newDuration);
					_sProfileType = getProfileString(_t);
					_bHasCruise = (_t[4] != 0);
					Stp7Formulars.solveProfile(_t, _sProfileType, _bHasCruise, _bIsddec, da == -1, dir == 1, _x[0],
							_x[7], _v[0], _vmax, _a[0], _amax, _jmax, newDuration);
				}
			} catch (Exception le) {
				// in case no solution was found and the stetching was less than
				// factor 10, throw the exception again
				if (newDuration < MAX_STRETCH_FACTOR * oldDuration)
					throw le;
				// otherwise return the old movement unchanged
				for (int i = 0; i < 8; i++) {
					_t[i] = t_orig[i];
					_j[i] = j_orig[i];
				}
			}
		}
		convertTimeIntervallsToPoints();

		StpData myData = new StpData();
		for (int i = 1; i < 8; i++) {
			// calc x,v,a values for next switch point
			calcjTrack(_t[i] - _t[i - 1], _x[i - 1], _v[i - 1], _a[i - 1], _j[i], myData);
			_x[i] = myData.position;
			_v[i] = myData.velocity;
			_a[i] = myData.acceleration;
		}

		// test if solution is valid, if not the test method will throw an
		// exception
		testProfile();

		return getDuration();
	}

	/// Returns at which time the cruising phase ends.
	@Override
	public double getEndOfCruisingTime() {
		return _t[4];
	}

	/**
	 * The function throws a logic_error exception, if the limits for jerk, acc
	 * or vel are broken, or if there are any incorrect time intervalls. If
	 * everything is correct, it just returns doing nothing .* @throws
	 * std::logic_error
	 */
	public void testProfile() {
		// test whether time intervalls are all positive
		for (int i = 1; i < 8; i++) {
			if (!isPositive(_t[i] - _t[i - 1]))
				throw new RuntimeException("Negative Time Intervalls");
		}

		// test for jerk, acc and vel limits at switching points
		double j, a, v, x;
		StpData myData = new StpData();
		for (int i = 1; i < 8; i++) {
			if (isZero(_t[i]))
				continue;

			move(_t[i], myData);
			x = myData.position;
			v = myData.velocity;
			a = myData.acceleration;
			j = myData.jerk;
			if ((!isZero(CCMath.abs(j) - _jmax)) && (!isZero(j)))
				throw new RuntimeException("Wrong jerk value!");
			if (!isNegative(CCMath.abs(a) - _amax))
				throw new RuntimeException("Broke acc limit!");
			if (!isNegative(CCMath.abs(v) - _vmax)) {
				if ((CCMath.sign(a) == CCMath.sign(v)))
					throw new RuntimeException("Broke vel limit!");
				// the only 'excuse' for braking the vec limit if v0>vmax or
				// v1>vmax
				// because of a unappropriately starting acceleration.
				if ((!isPositive(CCMath.abs(_v[0]) - _vmax)) && (!isNegative(
						_vmax - CCMath.abs(0.5 * _a[0] * _a[0] * (double) CCMath.sign(_a[0]) / _jmax + _v[0]))))
					throw new RuntimeException("Broke vel limit!");
			}
		}
		// everything seems to be allright :)
	}

	/// As testProfile(), but also checking, if the target position was reached.
	/// @throws std::logic_error
	public void testProfile(double xtarget) {
//		if (!isZero(xtarget - _x[7]))
//			throw new RuntimeException("Didn't reach the target position");
		testProfile();
	}

	public static void calcjTrack(double dt, double x0, double v0, double a0, double j, StpData theData) {
		double dt2 = dt * dt;
		theData.position = x0 + v0 * dt + (1. / 2.) * a0 * dt2 + (1. / 6.) * j * dt2 * dt;
		theData.velocity = v0 + a0 * dt + (1. / 2.) * j * dt2;
		theData.acceleration = a0 + j * dt;
	}

	/**
	 * Calculates a given 3rd order profile and writes the resulting position,
	 * velocity and acceleration in the passed variables &x, &v, &a. The t[]
	 * array must contain the time points (not intervalls) and indicies go from
	 * t[1] to t[length].
	 */
	public static void calcjTracks(double t[], double j[], int length, double x0, double v0, double a0,
			StpData theData) {
		theData.position = x0;
		theData.velocity = v0;
		theData.acceleration = a0;
		for (int i = 1; i <= length; i++) {
			calcjTrack(t[i] - t[i - 1], theData.position, theData.velocity, theData.acceleration, j[i], theData);
		}
	}

	/// same as calcjTracks but with time intervalls instead of time points.
	public static void calcjTracksTimeInt(double t[], double j[], int length, double x0, double v0, double a0,
			StpData theData) {
		theData.position = x0;
		theData.velocity = v0;
		theData.acceleration = a0;
		for (int i = 1; i <= length; i++) {
			calcjTrack(t[i], theData.position, theData.velocity, theData.acceleration, j[i], theData);
		}
	}

	/// Manuelly set a time value in the time array describing the movement.
	/// Don't use it unless you are feeling adventurous - its just intended for
	/// testing ;)
	public void setT(int i, double t) {
		_t[i] = t;
	}

	private void planProfile() {
		/*
		 * Calculates the time optimal third-order trajectory to reach the
		 * target position with the given start conditions and according to the
		 * limitations for jerk, acc and vel. There are two arrays filled up
		 * with the correct values: The array holding the jerk impulses and the
		 * one describing the time intervalls for these impulses. Both have a
		 * fixed length of 7 entries, however, several entries in the t-array
		 * might be zero - indicating that this paricular phase is not needed in
		 * the profil. The elements of the jerk array are the jerk values (NOT:
		 * either of the three values -1, 0, 1. NOT: Multiplication with the
		 * max-jerk gives the actual jerk-value.)
		 */

		int dir = 0;
		double xTarget = _x[7];
		double a_dummy, v_dummy;
		double xStop;

		// (1) Calculation of the direction flag (direction of potential
		// cruising
		// phase) by comparing the position we reach at an immideate halt to the
		// desired target position.
		Stp3 stp3Stop = new Stp3();
		stp3Stop.planFastestProfile(_v[0], 0, _a[0], _amax, _jmax);
		stp3Stop.getTimeArray(_t);
		stp3Stop.getAccArray(_j);
		StpData myData = new StpData();
		calcjTracks(_t, _j, 3, _x[0], _v[0], _a[0], myData);
		xStop = myData.position;
		dir = CCMath.sign(xTarget - xStop);
		if (isZero(xTarget - xStop)) {
			for (int i = 4; i < 8; i++) {
				_t[i] = _t[3];
				_j[i] = 0;
			}
			_sProfileType = Stp7Profile.PROFILE_STOP;
			_bIsddec = false;
			_bHasCruise = false;
			return;
		} else {
			// position change just from acc and dec phase:
			Stp3 stp3Acc = new Stp3(), stp3Dec = new Stp3();
			stp3Acc.planFastestProfile(_v[0], dir * _vmax, _a[0], _amax, _jmax);
			stp3Dec.planFastestProfile(dir * _vmax, 0, 0, _amax, _jmax);
			// position change:
			stp3Acc.getTimeArray(_t);
			stp3Acc.getAccArray(_j);
			stp3Dec.getTimeArray((_t));
			stp3Dec.getAccArray((_j));
			_t[4] = 0;
			_j[4] = 0;
			for (int i = 4; i < 8; i++)
				_t[i] += _t[3];
			calcjTracks(_t, _j, 7, _x[0], _v[0], _a[0], myData);
			xStop = myData.position;
			// distance we need to go in cruising phase:
			double xDelta = (xTarget - xStop);
			double tDelta = xDelta / (dir * _vmax);

			// case differentiation: Do we have a cruising phase?
			if (tDelta >= 0) {
				// with cruising phase, insert t_delta as cruising phase (t[4])
				for (int i = 4; i < 8; i++)
					_t[i] += tDelta;
				_bIsddec = false;
				_bHasCruise = true;
				if (stp3Acc.isTrapezoid())
					_sProfileType = (stp3Dec.isTrapezoid()) ? Stp7Profile.PROFILE_TT : Stp7Profile.PROFILE_TW;
				else
					_sProfileType = (stp3Dec.isTrapezoid()) ? Stp7Profile.PROFILE_WT : Stp7Profile.PROFILE_WW;
				_bIsddec = (_j[1] == _j[5]);
			} else {
				// without cruising phase
				planProfileNoCruise(dir);
				_bHasCruise = false;
			}
		}
	}

	private void planProfileNoCruise(int dir) {
		// The function must be called with a valid 7-phases profile stored in
		// _t and _j arrays, which is overshooting the target. It will then
		// first do
		// a case distinction to check, which kind of profile we currently have.
		// Next step is to cut out / shift parts of the acc-profile to shorten
		// the
		// profile until it is not overshooting the target anymore. At this time
		// we
		// know which profile-type the final solution will have and can call the
		// appropriate function to find the final solution.

		// (0) Check whether a normal profile has to switch into double
		// deceleration
		// (1) Check whether we have a double deceleration profile.
		// (2) Case distinction: TT / TW / WT / WW

		// its easier to calculate in time intervalls than time points, so we
		// convert the time array into intervalls first. In the end we just
		// convert it back.
		_bHasCruise = false;
		convertTimePointsToIntervalls();

		StpData myData = new StpData();

		// (0)
		if (CCMath.sign(_j[3]) == CCMath.sign(_j[5]) && _t[3] < _t[1]) {
			double tAcc[] = { 0, _t[3], _t[2], _t[3] };
			double tDec[] = { 0, _t[5], _t[6], _t[7] };
			double deltaAcc, deltaDec;
			calcjTracksTimeInt(tAcc, _j, 3, 0, 0, 0, myData);
			deltaAcc = myData.velocity;

			calcjTracksTimeInt(tDec, _j, 3, 0, 0, 0, myData);
			deltaDec = myData.velocity;
			deltaAcc = CCMath.abs(deltaAcc);
			deltaDec = CCMath.abs(deltaDec);
			if (deltaAcc < deltaDec) {
				tAcc[1] = tAcc[2] = 0;
				tAcc[3] = _t[1] - _t[3];
				removeAreaTimeInt(tDec, deltaAcc, _amax, _jmax);
				double jNew[] = { 0, _j[3], 0, _j[1], 0, _j[5], _j[6], _j[7] };
				double[] tNew = new double[8];
				for (int i = 0; i < 8; i++)
					tNew[i] = (i < 4) ? tAcc[i] : tDec[i - 4];
				// If we still overshoot after putting as much area under the
				// acc
				// graph from the front to the back, the profile becomes double
				// deceleration.
				if (stillOvershootsTimeInt(tNew, jNew, dir, _x[0], _x[7], _v[0], _a[0]))
					for (int i = 0; i < 8; i++) {
						_t[i] = tNew[i];
						_j[i] = jNew[i];
					}
			}
		}

		double da;
		da = _j[1] == _j[3] ? -1 : 1;

		// (1)
		// check if we have a double deceleration profile
		if (CCMath.sign(_j[3]) != CCMath.sign(_j[5])) {
			// _sProfileType = Stp7Profile.PROFILE_WW;
			if (_t[6] == 0) {
				// second part is currently wedge, may become trapez
				// calculate maximal shift from first to second deceleration
				// phase
				// in order to reach the W-T-border case
				double a2, v2;
				calcjTracksTimeInt(_t, _j, 2, _x[0], _v[0], _a[0], myData);
				v2 = myData.velocity;
				a2 = myData.acceleration;
				StpShiftTime myTime = new StpShiftTime();
				calc7st_opt_shiftTimeInt(_t, _j, dir, _amax, _jmax, v2, a2, myTime);
				if (myTime.tDelta < 0)
					throw new RuntimeException("DeltaT negative at opt_shift!");
				if (myTime.tDelta < _t[3]) {
					// adapt profile by shortening t[3]
					double tNew[] = { 0, _t[1], _t[2], _t[3] - myTime.tDelta, _t[4], myTime.t5, 0, myTime.t7 };
					// if we still overshoot, the profile becomes trapezoidal
					if (stillOvershootsTimeInt(tNew, _j, dir, _x[0], _x[7], _v[0], _a[0])) {
						for (int i = 0; i < 8; i++)
							_t[i] = tNew[i];
						// allow trapez in second part when generating formulas:
						_t[6] = 1;
						// _sProfileType = PROFILE_TT;
					}
				} else {
					// velocity delta in phase 3 is not enough to extend
					// wedge-shaped second decleration phase to trapezoidal
					// shape
					// so we stay at a triangular profile
				}
			}

			_bIsddec = true;
			if (_t[6] == 0)
				_sProfileType = Stp7Profile.PROFILE_WW;
			else
				_sProfileType = Stp7Profile.PROFILE_TT;

			// Calculate exact phase duration from given profile t, j
			Stp7Formulars.solveProfile(_t, _sProfileType, _bHasCruise, _bIsddec, da == -1, dir == 1, _x[0], _x[7],
					_v[0], _vmax, _a[0], _amax, _jmax, 0);
			_sProfileType = getProfileString(_t);

			convertTimeIntervallsToPoints();
			return;
		}

		// (2)
		// we don't have double deceleration --> cut out instead of merging
		// find correct profile by cutting pieces and descending to shorter
		// profiles
		_bIsddec = false;
		_sProfileType = findProfileTimeInt(_t, _j, dir, _x[0], _x[7], _v[0], _a[0], _amax, _jmax);

		// Calculate exact phase duration for choosen profile t, j
		// j[0..7] are already the correct values!
		Stp7Formulars.solveProfile(_t, _sProfileType, _bHasCruise, _bIsddec, da == -1, dir == 1, _x[0], _x[7], _v[0],
				_vmax, _a[0], _amax, _jmax, 0);

		convertTimeIntervallsToPoints();
		return;
	}

	private void convertTimePointsToIntervalls() {
		for (int i = 7; i > 0; i--) {
			_t[i] = _t[i] - _t[i - 1];
		}
	}

	private void convertTimeIntervallsToPoints() {
		for (int i = 1; i < 8; i++) {
			_t[i] = _t[i] + _t[i - 1];
		}
	}

	private static boolean stillOvershootsTimeInt(double[] t, double[] j, int dir, double x0, double xTarget, double v0,
			double a0) {

		StpData myData = new StpData();
		calcjTracksTimeInt(t, j, 7, x0, v0, a0, myData);
		double xEnd = myData.position;
		return (CCMath.sign(xEnd - xTarget) * dir == 1);
	}

	Stp7Profile findProfileTimeInt(double[] t, double[] j, int dir, double x0, double xTarget, double v0, double a0,
			double amax, double jmax) {
		// find correct profile by cutting pieces and descending to shorter
		// profiles
		// uses the values t[1..7] and j[1..7] and changes them accodingly to
		// the
		// new profile type.
		Stp7Profile type = getProfileString(t);

		double[] tOld = new double[8];
		for (int i = 0; i < 8; i++)
			tOld[i] = t[i];

		if (type == Stp7Profile.PROFILE_TT) {
			// cut out smaller a=const. part
			double dt = CCMath.min(t[2], t[6]);
			t[2] = t[2] - dt;
			t[6] = t[6] - dt;
			if (stillOvershootsTimeInt(t, j, dir, x0, xTarget, v0, a0)) {
				// recursively calling this function even cuts further
				type = findProfileTimeInt(t, j, dir, x0, xTarget, v0, a0, amax, jmax);
			} else {
				// now we stop before the target, hence profile stays TT
				for (int i = 0; i < 8; i++)
					t[i] = tOld[i];
			}
			return type;
		}

		if (type == Stp7Profile.PROFILE_WW) {
			// nothing to do, WW stays WW anytime
			return type;
		}

		if (type == Stp7Profile.PROFILE_WT) {
			double a1 = a0 + j[1] * t[1];
			// double dt_w = CCMath.min(t[1],t[3]);
			// double area_w_max = CCMath.abs(dt_w * (2*a1 - dt_w*j[1]));

			double area_w_max = t[3] * t[3] * jmax;
			if (t[1] < t[3])
				area_w_max -= 0.5 * a0 * a0 / jmax;

			double area_t_max = t[6] * amax;
			if (area_w_max > area_t_max) {
				// we will cut out the whole t[6] WT -> WW
				t[6] = 0;
				double dt = (CCMath.abs(a1) - CCMath.sqrt(a1 * a1 - area_t_max * jmax)) / jmax;
				t[1] = t[1] - dt;
				t[3] = t[3] - dt;
				if (stillOvershootsTimeInt(t, j, dir, x0, xTarget, v0, a0)) {
					type = Stp7Profile.PROFILE_WW; // type switches to WW
				} else {
					// now we stop before the target, hence profile stays WT
					for (int i = 0; i < 8; i++)
						t[i] = tOld[i];
				}
			} else
				; // nothing to cut out, stays WT
			return type;
		}

		if (type == Stp7Profile.PROFILE_TW) {
			double a5 = j[5] * t[5];
			double area_w_max = CCMath.abs(t[5] * a5);
			double area_t_max = t[2] * amax;
			if (area_w_max > area_t_max) {
				// we will cut out the whole t[2]
				t[2] = 0;
				t[5] = CCMath.sqrt((area_w_max - area_t_max) / CCMath.abs(j[5]));
				t[7] = t[5];
				if (stillOvershootsTimeInt(t, j, dir, x0, xTarget, v0, a0)) {
					type = Stp7Profile.PROFILE_WW;
				} else {
					// now we stop before the target, hence profile stays TW
					for (int i = 0; i < 8; i++)
						t[i] = tOld[i];
				}
			}
			return type;
		}

		return type;
	}

	class StpShiftTime {
		double tDelta;
		double t5;
		double t7;
	}

	private static void calc7st_opt_shiftTimeInt(double[] t, double[] j, int dir, double amax, double jmax, double v2,
			double a2, StpShiftTime theTime) {
		// Given a deceleration - deceleration profile with wedge-shaped second
		// part,
		// compute the period DeltaT which must be cut from third phase (and
		// inserted in second part), such that the second part becomes a
		// triangular
		// profile exactly hitting -d*amax.
		double v3, a3, v6, a6;
		// compute a3 and a6
		StpData myData = new StpData();
		calcjTrack(t[3], 0., v2, a2, j[3], myData);
		v3 = myData.velocity;
		a3 = myData.acceleration;
		calcjTracksTimeInt(t, j, 3, 0, v3, a3, myData);
		v6 = myData.velocity;
		a6 = myData.acceleration;

		// compute discriminant of quadratic polynomial solution
		double diskriminant = 4 * amax * amax + 2 * jmax * jmax * (t[7] * t[7] - t[5] * t[5])
				+ 4 * dir * jmax * (a6 * t[7] + a3 * t[5]) + 2 * a3 * a3;
		if (isZero(diskriminant))
			diskriminant = 0;
		double root = CCMath.sqrt(diskriminant);

		// compute T5
		if (dir < 0)
			theTime.t5 = dir * amax + root / 2;
		else
			theTime.t5 = dir * amax - root / 2;
		theTime.t5 = theTime.t5 / (dir * jmax);

		// compute DeltaT and T7
		theTime.tDelta = (a3 + dir * amax) / (dir * jmax) - theTime.t5;
		if (isZero(theTime.tDelta))
			theTime.tDelta = 0;
		theTime.t7 = amax / jmax;
	}

	Stp7Profile getProfileString(double[] t) {
		if (t[2] != 0) { // T? profile
			if (t[6] != 0)
				return Stp7Profile.PROFILE_TT;
			else
				return Stp7Profile.PROFILE_TW;
		} else { // W? profile
			if (t[6] != 0)
				return Stp7Profile.PROFILE_WT;
			else
				return Stp7Profile.PROFILE_WW;
		}
	}

	private static void adaptProfile(double[] t, double[] j, double xtarget, double a0, double v0, double x0) {
		// Given a profile (t,j) where acceleration and deceleration phase was
		// already changed (cutted or shifted) such that velocity v(3) is
		// smaller
		// in magnitude than before, this function extends the cruising phase
		// (or
		// inserts one), such that the target is reach again.
		// This is a simple linear equation...
		double xend, v3new;
		StpData myData = new StpData();
		calcjTracksTimeInt(t, j, 7, x0, v0, a0, myData);
		xend = myData.position;
		calcjTracksTimeInt(t, j, 3, x0, v0, a0, myData);
		v3new = myData.velocity;
		// enlarge cruising time, such that area below velocity profile equals
		// dp again
		t[4] = t[4] + (xtarget - xend) / v3new;
	}

	private static boolean stillTooShort(double[] t, double newDuration) {
		return (t[1] + t[2] + t[3] + t[4] + t[5] + t[6] + t[7] < newDuration);
	}

	static void shiftDoubleDecArea(double[] t, double[] j, double newDuration, double x0, double xTarget, double v0,
			double vmax, double a0, double amax, double jmax) {
		// Compute current velocity decrease achieved during first and second
		// part
		double curFirst, curLast;
		double a3, a7;
		StpData myData = new StpData();
		calcjTracksTimeInt(t, j, 3, 0, 0, a0, myData); // a3=0
		curFirst = myData.velocity;
		a3 = myData.acceleration;
		calcjTracksTimeInt(t, j, 3, 0., 0., 0., myData); // a7=0
		curLast = myData.velocity;
		a7 = myData.acceleration;
		//////////////// WARUM nicht j[4] ???
		curFirst = CCMath.abs(curFirst);
		curLast = CCMath.abs(curLast);

		double wedgeMax = amax * amax / jmax;
		double deltaFirst, deltaLast, deltaV;
		double[] tacc = new double[4];

		while (true) {
			// area needed to extend first part to full wedge
			deltaFirst = wedgeMax - curFirst;
			if (t[2] == 0 && !isZero(deltaFirst)) { // first part is not yet
													// full wedge
				if (t[6] == 0) {
					deltaLast = curLast; // area available in second part
					// if last part has not enough area to extend first one to
					// full
					// triangle, the profile will keep WW shape
					if (deltaFirst >= deltaLast)
						return;
				} else {
					deltaLast = t[6] * amax; // area below const-trapezoidal
												// part
				}
				deltaV = CCMath.min(deltaFirst, deltaLast);
			} else {
				if (t[2] == 0)
					t[2] = 1; // allow const-part in trapez
				if (t[6] == 0)
					return; // profile will keep TW shape
				deltaV = t[6] * amax; // area below const-trapezoidal part
			}

			addAreaTimeInt(curFirst + deltaV - wedgeMax, amax, jmax, tacc);
			double[] tdec = new double[4];
			tdec[0] = 0;
			tdec[1] = t[5];
			tdec[2] = t[6];
			tdec[3] = t[7];
			removeAreaTimeInt(tdec, deltaV, amax, jmax);
			double[] tn = new double[8];
			tn[0] = 0;
			tn[1] = tacc[1];
			tn[2] = tacc[2];
			tn[3] = tacc[3];
			tn[4] = t[4];
			tn[5] = tdec[1];
			tn[6] = tdec[2];
			tn[7] = tdec[3];
			adaptProfile(tn, j, xTarget, x0, v0, a0);
			// if we overshoot in time, t contains the correct profile
			if (!stillTooShort(tn, newDuration))
				return;
			// otherwise continue probing with adapted profile
			for (int i = 0; i < 7; i++)
				t[i] = tn[i]; // use adapted profile for further checkst = tn;
			curFirst = curFirst + deltaV;
			curLast = curLast - deltaV;
		}
	}

	static void addAreaTimeInt(double deltaV, double amax, double jmax, double[] t) {
		// Compute a profile [t1 t2 t3] such that its area is wedgeMax + deltaV.
		// The result is written in t[1..3].
		double tmax = amax / jmax;
		if (deltaV >= 0) { // full wedge + const trapezoidal part
			t[1] = tmax;
			t[2] = deltaV / amax;
			t[3] = tmax;
		} else {
			double deltaT = tmax - CCMath.sqrt(tmax * tmax + deltaV / jmax);
			t[1] = tmax - deltaT;
			t[2] = 0;
			t[3] = tmax - deltaT;
		}
	}

	static void removeAreaTimeInt(double[] t, double deltaV, double amax, double jmax) {
		// Takes an array t[1..3] and deletes the passed deltaV from the area
		// under
		// the acceleration graph. The elements of t are altered accordingly.

		// we only decrease the area...
		deltaV = CCMath.abs(deltaV);
		double A_now = t[1] * t[1] * jmax + amax * t[2];
		if (A_now < deltaV)
			return; // not enough to cut out...
		double Aw_max = amax * amax / jmax;

		if (isZero(t[2]) || (Aw_max > A_now - deltaV)) {
			// result wedge shaped
			t[1] = CCMath.sqrt(A_now / jmax - deltaV / jmax);
			t[3] = t[1];
			t[2] = 0;
		} else {
			// result trapezoid shaped
			t[2] = t[2] - deltaV / amax;
		}
	}

	static void splitNoCruiseProfileTimeInt(double[] t, double[] j, double a0) {
		// In case of a profile without cruising phase, the time intervalls
		// t(3) and t(5) might be joined together into one of them so the other
		// one is zero. This can only occour if j(3) and j(5) have the same
		// CCMath.sign.
		// For the stretching algorithm, we need to split the time intervall up
		// so the acc-graph reaches zero after t(3).
		if (t[4] != 0)
			return;
		if (j[3] != j[5])
			return;
		double tsum = t[3] + t[5];
		t[3] = CCMath.abs((a0 + j[1] * t[1]) / j[3]); // = -a2/j(3)
		t[5] = tsum - t[3];
	}

	void findProfileTypeStretchCanonical(double newDuration) {
		// find correct profile by cutting pieces and descending to shorter
		// profiles

		_sProfileType = getProfileString(_t);

		// if profile type does not change, we just insert cruising phase into t
		double[] t_orig = new double[8];
		for (int i = 0; i < 8; i++)
			t_orig[i] = _t[i];
		if (_t[4] == 0)
			t_orig[4] = 1;

		if (_sProfileType == Stp7Profile.PROFILE_TT) {
			// cut out smaller a=const. part
			double dt = CCMath.min(_t[2], _t[6]);
			_t[2] = _t[2] - dt;
			_t[6] = _t[6] - dt;
			adaptProfile(_t, _j, _x[7], _x[0], _v[0], _a[0]);
			if (stillTooShort(_t, newDuration)) {
				// recursively calling this function even cuts further
				findProfileTypeStretchCanonical(newDuration);
			} else {
				// now we stop after duration time newDuration, hence profile
				// stays TT
				for (int i = 0; i < 8; i++)
					_t[i] = t_orig[i]; // allow for a cruising phase
			}
			return;
		}

		if (_sProfileType == Stp7Profile.PROFILE_WW) {
			for (int i = 0; i < 8; i++)
				_t[i] = t_orig[i]; // allow for a cruising phase
			return; // nothing to do, WW stays WW anytime
		}

		if (_sProfileType == Stp7Profile.PROFILE_WT) {
			double a1 = _a[0] + _j[1] * _t[1];
			// double dt_w = CCMath.min(_t[1],_t[3]);
			// double area_w_max = 0.5*dt_w*dt_w*_jmax;
			// if (_t[1] > _t[3]) area_w_max += CCMath.abs(2.*_a[0]*dt_w);
			double area_w_max = _t[3] * _t[3] * _jmax;
			if (_t[1] < _t[3])
				area_w_max -= 0.5 * _a[0] * _a[0] / _jmax;

			double area_t_max = _t[6] * _amax;
			if (area_w_max > area_t_max) {
				// we will cut out the whole t(6) WT -> WW
				_t[6] = 0;
				double dt = (CCMath.abs(a1) - CCMath.sqrt(a1 * a1 - area_t_max * _jmax)) / _jmax;
				_t[1] = _t[1] - dt;
				_t[3] = _t[3] - dt;
				adaptProfile(_t, _j, _x[7], _x[0], _v[0], _a[0]);
				if (stillTooShort(_t, newDuration)) {
					_sProfileType = Stp7Profile.PROFILE_WW; // type switches to
															// WW
				} else {
					// now we stop after duration time newDuration, hence
					// profile stays WT
					for (int i = 0; i < 8; i++)
						_t[i] = t_orig[i]; // allow for a cruising phase
				}
			} else {
				// nothing to cut out, stays at WT
				for (int i = 0; i < 8; i++)
					_t[i] = t_orig[i]; // allow for a cruising phase
			}
			return;
		}

		if (_sProfileType == Stp7Profile.PROFILE_TW) {
			double a5 = _j[5] * _t[5];
			double area_w_max = CCMath.abs(_t[5] * a5);
			double area_t_max = _t[2] * _amax;
			if (area_w_max > area_t_max) {
				// we will cut out the whole t(2)
				_t[2] = 0;
				_t[5] = CCMath.sqrt((area_w_max - area_t_max) / CCMath.abs(_j[5]));
				_t[7] = _t[5];
				// for the case the t area and the second wedge are exactly
				// same,
				// the result is a fullstop. In this case we stay a TW profile.
				if (isZero(_t[7])) {
					for (int i = 0; i < 8; i++)
						_t[i] = t_orig[i]; // allow for a cruising phase
				} else {
					adaptProfile(_t, _j, _x[7], _x[0], _v[0], _a[0]);
					// t(4) might get smaller than zero, when due to the area
					// switching, the direction flag of the motion changes. In
					// that case, we stay a TW profile.
					if ((_t[4] >= 0) && (stillTooShort(_t, newDuration))) {
						_sProfileType = Stp7Profile.PROFILE_WW; // type switches
																// to WW
					} else {
						// now we stop after duration time newDuration, hence
						// profile stays TW
						for (int i = 0; i < 8; i++)
							_t[i] = t_orig[i]; // allow for a cruising phase
					}
				}
			} else {
				// nothing to cut out, stays at WT
				for (int i = 0; i < 8; i++)
					_t[i] = t_orig[i]; // allow for a cruising phase
			}
			return;
		}

		return;
	}

	void planProfileStretchDoubleDec(double newDuration, double dir, double da) {
		// find correct double deceleration profile by shifting area from second
		// deceleration to first deceleration part.
		// We can get two type of deceleration profiles here:
		// 1) The time-optimal profile was already double deceleraton, leading
		// to a(3) ~= 0
		// 2) all other profiles: a(3) == 0, there might be profile [0 0 t3] /
		// [t1 0 0]

		double a3;
		StpData myData = new StpData();
		calcjTracksTimeInt(_t, _j, 3, _x[0], _v[0], _a[0], myData);
		a3 = myData.acceleration;

		if (isZero(a3)) {
			// We need to differentiate between two cases:
			// Either j(1) and j(3) have different CCMath.sign, in that case,
			// the first part of
			// the profile will resemble a wedge or a trapezoid, respectively.
			// When they have the same CCMath.sign (in case a0 > amax), it will
			// have the form
			// of a slope or a stair, respectively.
			if (CCMath.sign(_j[1]) == CCMath.sign(_j[3])) {
				_bHasCruise = true;
				da = -1;
				if (_t[6] == 0) {
					_t[2] = 1;
				} else {
					// move all the area from second trapezoid part to the first
					_t[2] = _t[2] + _t[6];
					_t[6] = 0;
					double[] tn = new double[8];
					for (int i = 0; i < 8; i++)
						tn[i] = _t[i];
					adaptProfile(tn, _j, _x[7], _x[0], _v[0], _a[0]);
					// if we overshoot in time, t contains the correct profile
					if (stillTooShort(tn, newDuration)) {
						// we will need to transfer even more area to the first
						// part
						// ==> no trapezoid second part
						// we need to allow t(2) to be different from zero
						_t[2] = 1;
					} else {
						// otherwise the second part will stay trapezoid
						_t[6] = 1;
						_t[2] = 1;
					}
				}
			} else {
				double t0;
				if (CCMath.sign(_a[0]) != CCMath.sign(_j[5])) {
					// In the shifting process, we may only consider the
					// velocity change *after*
					// reaching zero acceleration. Hence, we compute new initial
					// conditions, reached
					// after this initial acceleration decrease.
					t0 = CCMath.abs(_a[0] / _jmax);
				} else {
					// To ease computation during shifting, we extend the first
					// phase
					// to an full profile, starting at zero acceleration
					t0 = -CCMath.abs(_a[0] / _jmax);
				}
				// compute initial position at zero acceleration
				double x0, v0, a0;
				calcjTrack(t0, _x[0], _v[0], _a[0], _j[1], myData);
				x0 = myData.position;
				v0 = myData.velocity;
				a0 = myData.acceleration;
				_t[1] = _t[1] - t0;

				shiftDoubleDecArea(_t, _j, newDuration - t0, x0, _x[7], v0, _vmax, a0, _amax, _jmax);
				_t[1] = _t[1] + t0;
			}
			// did we find a correct profile? if not, we need to enter the a3 !=
			// 0
			// case
			if (_t[4] >= 0) {
				// extend simple profile to wedge-shaped profile
				_t[1] = 1;
				_t[3] = 1;
				_sProfileType = getProfileString(_t);
				_bIsddec = true;
				// TODO
				// this is a very ugly solution for the problem that we can't
				// detect
				// cases in which a ddec profile with cruising phase switches
				// into a
				// profile without cruising phase.
				// We will just set t(4) to zero and try.
				try { // try the case with cruising phase first
					Stp7Formulars.solveProfile(_t, _sProfileType, true, _bIsddec, da == -1, dir == 1, _x[0], _x[7],
							_v[0], _vmax, _a[0], _amax, _jmax, newDuration);
					return;
				} catch (Exception e) {
				}
				// it didnt work --> try the one without cruising phase
				Stp7Formulars.solveProfile(_t, _sProfileType, false, _bIsddec, da == -1, dir == 1, _x[0], _x[7], _v[0],
						_vmax, _a[0], _amax, _jmax, newDuration);
				return;
			}
		}

		// We now have to stretch a double deceleration profile without a
		// cruising phase, which is the most complex possible case.
		// It turns out, that we can't distinguish between the 8 profile types
		// without actually trying to compute them and see whether we get a
		// correct solution or not.
		// However - since we will shift area under the acc-graph
		// from the second part of the movement to the first, the unstretched
		// profile type already limits the possible outcomes:
		// TT==>{TT,TW}, TW==>{TW}, WT==>{WT,WW,TW,TT}, WW==>{WW,TW}
		// In each of this cases, t4 could either be zero or not.

		// First put all profiles to test as columns into a matrix:
		Stp7Profile[] profilesToTest = new Stp7Profile[4];
		double length = 0;

		if ((_t[2] != 0) && (_t[6] != 0)) { // TT
			length = 2;
			profilesToTest[0] = Stp7Profile.PROFILE_TT;
			profilesToTest[1] = Stp7Profile.PROFILE_TW;
		} else if ((_t[2] != 0) && (_t[6] == 0)) { // TW
			length = 1;
			profilesToTest[0] = Stp7Profile.PROFILE_TW;
		} else if ((_t[2] == 0) && (_t[6] != 0)) { // WT
			length = 4;
			profilesToTest[0] = Stp7Profile.PROFILE_TT;
			profilesToTest[1] = Stp7Profile.PROFILE_TW;
			profilesToTest[2] = Stp7Profile.PROFILE_WT;
			profilesToTest[3] = Stp7Profile.PROFILE_WW;
		} else if ((_t[2] == 0) && (_t[6] == 0)) { // WW
			length = 2;
			profilesToTest[0] = Stp7Profile.PROFILE_TW;
			profilesToTest[1] = Stp7Profile.PROFILE_WW;
		}

		// now test all profiles in until we found the right one:
		_bIsddec = true;
		int cCalcs = 0;
		for (int i = 0; i < length; i++) {
			_sProfileType = profilesToTest[i];
			try {
				// with cruising phase
				Stp7Formulars.solveProfile(_t, _sProfileType, true, _bIsddec, da == -1, dir == 1, _x[0], _x[7], _v[0],
						_vmax, _a[0], _amax, _jmax, newDuration);
				// cout << "Stretch DDec: Calculated " << cCalcs << " profiles
				// before finding the right one." << endl;
				// in some cases we might get an additional solution that is
				// oszillating in the acceleration. So we need to check, whether
				// a3 has the same CCMath.sign as -dir.
				a3 = _a[0] + _t[1] * _j[1] + _t[2] * _j[2] + _t[3] * _j[3];
				if ((isZero(a3)) || (CCMath.sign(a3) != CCMath.sign(dir))) {
					// no oszillation, we found the correct profile
					return;
				}
				// oszillation, we need to continue the search
			} catch (Exception e) {
				cCalcs++;
			}
			try {
				// without cruising phase
				Stp7Formulars.solveProfile(_t, _sProfileType, false, _bIsddec, da == -1, dir == 1, _x[0], _x[7], _v[0],
						_vmax, _a[0], _amax, _jmax, newDuration);
				// cout << "Stretch DDec: Calculated " << cCalcs << " profiles
				// before finding the right one." << endl;
				// in some cases we might get an additional solution that is
				// oszillating in the acceleration. So we need to check, whether
				// a3 has the same CCMath.sign as -dir.
				a3 = _a[0] + _t[1] * _j[1] + _t[2] * _j[2] + _t[3] * _j[3];
				if ((isZero(a3)) || (CCMath.sign(a3) != CCMath.sign(dir))) {
					// no oszillation, we found the correct profile
					return;
				}
				// oszillation, we need to continue the search
			} catch (Exception e) {
				cCalcs++;
			}
		}
		throw new RuntimeException("No solution found for stretched double dec 3rd order profile.");
	}

}