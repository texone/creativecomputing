package cc.creativecomputing.kle.trajectorie;

/**
 * Trajectory Planner, 1st order (Stp1) \author Robert Haschke \date 2009
 *
 * This class is for planning simple linear trajectories with constant velocity.
 * Given a start and target position, and a maximum value for velocity you pass
 * these values to the planFastestProfile method. To get the movement parameters
 * at specific times, call the move(...) method.
 *
 * A profile can be stretched to any desired duration by calling the
 * scaleToDuration method. This method can be used to synchronise several
 * movements made at the same time.
 * 
 * Both planFastestProfile and scaleToDuration throw a logic_error in case they
 * were unable to find a solution. In praxis this should never occour, so you
 * better take it serious if it does and provide some kind of fallback - for
 * example stopping the motion of the joint immediately.
 *
 * For further information about the theory of the used algorithm \see "On-Line
 * Planning of Time-Optimal, Jerk-Limited Trajectories"; R. Haschke, E.
 * Weitnauer, H. Ritter; 2007
 *
 */
public class Stp1 extends StpBase {

	private double[] _x = new double[2]; // initial and target position
	private double _v, _T; // (signed) velocity and duration of motion

	public Stp1() {
		_v = 0;
		_T = 0;
		_x[0] = 0;
		_x[1] = 0;
	}

	@Override
	public double getDuration() {
		return _T;
	}

	@Override
	public double getEndOfCruisingTime() {
		return _T;
	}

	/// get the position, velocity and acceleration at passed time >= 0
	@Override
	public void move(double t, StpData theData) {
		theData.velocity = vel(t);
		theData.position = pos(t);
	}

	/// < get the position at passed time >= 0
	@Override
	public double pos(double t) {
		if (t >= _T)
			return _x[1];
		return _x[0] + _v * t;
	}

	/// < get the velocity at passed time >= 0
	@Override
	public double vel(double t) {
		if (t >= _T)
			return 0;
		return _v;
	};

	/// Function for calculating the profile.
	
	public double planFastestProfile(double x0, double xtarget, double vmax) {
		_x[0] = x0;
		_x[1] = xtarget;

		// check, whether vmax and amax are greater than zero
		if (vmax < 0)
			throw new IllegalArgumentException("vmax must be positive");

		_v = xtarget < x0 ? -vmax : vmax;
		_T = (xtarget - x0) / _v;

		return _T;
	}

	/// scale a planned profile to a longer duration
	@Override
	public double scaleToDuration(double newDuration) {
		if (newDuration <= _T)
			return _T; // only enlarge duration
		_T = newDuration;
		_v = (_x[1] - _x[0]) / _T;
		return _T;
	}
	
	@Override
	public String toString() {
		return "1st order trajectory: v=" + _v + " T=" + _T;
	}

};