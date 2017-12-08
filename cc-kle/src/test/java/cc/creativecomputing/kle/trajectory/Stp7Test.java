package cc.creativecomputing.kle.trajectory;

import cc.creativecomputing.kle.trajectorie.Stp3;
import cc.creativecomputing.kle.trajectorie.Stp7;
import cc.creativecomputing.kle.trajectorie.Stp7.Stp7Profile;
import cc.creativecomputing.kle.trajectorie.StpData;
import cc.creativecomputing.math.CCMath;

import static org.junit.Assert.*;
import org.junit.Test;

public class Stp7Test {
	private double calcFullstopPosition(double x0, double v0, double a0, double amax, double jmax) {
		Stp3 stp = new Stp3();
		stp.planFastestProfile(v0, 0, a0, amax, jmax);
		return stp.pos(stp.getDuration());
	}

	private double calcZeroCruisePosition(int dir, double x0, double v0, double vmax, double a0, double amax,
			double jmax) {
		double[] t = new double[8], j = new double[8];
		double xStop, v_dummy, a_dummy;
		// position change just from acc and dec phase:
		Stp3 stp3Acc = new Stp3(), stp3Dec = new Stp3();
		stp3Acc.planFastestProfile(v0, dir * vmax, a0, amax, jmax);
		stp3Dec.planFastestProfile(dir * vmax, 0, 0, amax, jmax);
		// position change:
		stp3Acc.getTimeArray(t);
		stp3Acc.getAccArray(j);
		stp3Dec.getTimeArray(t);
		stp3Dec.getAccArray(j);
		t[4] = 0;
		j[4] = 0;
		for (int i = 4; i < 8; i++)
			t[i] += t[3];
		StpData myData = new StpData();
		Stp7.calcjTracks(t, j, 7, x0, v0, a0, myData);
		return myData.position;
	}

	/// most likely a numerical problem in the stp7Formulars.
	/// The Stp7::MAX_STRETCH_FACTOR must be around 10 or smaller to avoid.
	@Test
	public void testProblem() {
		Stp7 stp = new Stp7();

		stp.planFastestProfile(-1.18682, -1.18681975, 0., 0.4, 0., 0.523599, 0.02);
		try {
			stp.scaleToDuration(1);
		} catch (Exception le) {
			le.printStackTrace();
			System.out.println("Falling back to 2nd order trajectory...");
			Stp3 stp3 = new Stp3();
			stp3.planFastestProfile(-1.18682, -1.18681975, 0., 0.4, 0.523599);
			stp3.scaleToDuration(1);
		}
	}

	/// test some simple basic behavior
//	@Test
	public void testBasics() {
		Stp7 stp = new Stp7();
		assertNotEquals(stp.toString(), "");
		stp.planFastestProfile(0, 30, 0, 6, 0, 4, 2);
		assertNotEquals(stp.toString(), "");
		assertEquals(stp.getDuration(), 8.46, 0.01);
		assertEquals(stp.pos(8.46), 30, 0.05);
		assertEquals(stp.vel(8.46), 0, 0.1);
		assertEquals(stp.acc(8.46), 0, 0.1);
		stp.testProfile();
		stp.setT(1, 10);
		// TS_ASSERT_THROWS_ANYTHING(stp.testProfile());
	};

	/**
	 * Runs 6174 tests covering all possible cases for the planning of time
	 * optimal profiles, both ddec and canonical ones.
	 * 
	 * The method choses the target position around the end positions for the
	 * zero-cruise profiles (exactly reach vmax, but immediately slow down
	 * afterwards). 21 Positions are chosen.
	 *
	 * For the other values, the method iterates through the following
	 * combinations: - amax = 1.07 - vmax = 0.92 - x0 = 0 - a0 =
	 * {-1.2,-1.07,-0.7,0.0,0.7,1.07,1.2} - v0 =
	 * {-1.2,-0.92,-0.7,0.0,0.7,0.92,1.2} - jmax = {0.23, 0.79, 1.0, 1.68, 5.1,
	 * 101}
	 */
//	@Test
	public void testAutomatedFastestProfileTest() {
		Stp7 stp = new Stp7();

		int count = 0;
		double amax = 1.07;
		double vmax = 0.92;
		double xtarget;

		double x0 = 0.;
		double[] a0 = { -1.2, -1.07, -0.7, 0.0, 0.7, 1.07, 1.2 };
		double[] v0 = { -1.2, -0.92, -0.7, 0.0, 0.7, 0.92, 1.2 };
		double[] jmaxs = { 0.23, 0.79, 1.0, 1.68, 5.1, 101 };

		String testResult;

		for (int j = 0; j < 6; j++) {
			double jmax = jmaxs[j];
			for (int i_a = 0; i_a < 7; i_a++) {
				for (int i_v = 0; i_v < 7; i_v++) {
					double x_fullstop = calcFullstopPosition(x0, v0[i_v], a0[i_a], amax, jmax);
					stp.planFastestProfile(x0, x_fullstop, v0[i_v], vmax, a0[i_a], amax, jmax);
					testResult = "";
					try {
						stp.testProfile();
					} catch (Exception e) {
						testResult = e.getMessage();
					}
					if (testResult != "") {
						System.out.println(stp.toString());
						System.out.println("Testing planFastestProfile(" + x0 + ", " + x_fullstop + ", " + v0[i_v]
								+ ", " + vmax + ", " + a0[i_a] + ", " + amax + ", " + jmax + ")");
					}
					assertEquals(testResult, "");
					count++;
					double x_neg = calcZeroCruisePosition(-1, x0, v0[i_v], vmax, a0[i_a], amax, jmax);
					double x_pos = calcZeroCruisePosition(1, x0, v0[i_v], vmax, a0[i_a], amax, jmax);
					double dp = CCMath.abs((x_neg - x_pos) / 9);
					if (x_pos < x_neg)
						x_neg = x_pos;
					for (int i_x = -5; i_x < 15; i_x++) {
						xtarget = x_neg + i_x * dp;
						stp.planFastestProfile(x0, xtarget, v0[i_v], vmax, a0[i_a], amax, jmax);
						testResult = "";
						try {
							stp.testProfile();
						} catch (Exception e) {
							testResult = e.getMessage();
						}
						if (testResult != "") {
							System.out.println(stp.toString());
							System.out.println("Testing planFastestProfile(" + x0 + ", " + xtarget + ", " + v0[i_v]
									+ ", " + vmax + ", " + a0[i_a] + ", " + amax + ", " + jmax + ")");
						}
						assertEquals(testResult, "");
						// System.out.println( stp.getDetailedProfileType();
						count++;
					}
				}
			}
		}
		System.out.println("Calculated time optimal profile for " + count + " different start conditions.");
	}

	/// test 4 basic cases for standard profiles with cruising phase
	//@Test
	public void testCruiseProfilesStandard() {
		Stp7 stp = new Stp7();
		// TT profile
		stp.planFastestProfile(-5, 5, 0, 3, 0, 2, 2);
		assertEquals(stp.getDuration(), 5.8333, 0.0001);
		assertEquals(stp.pos(100), 5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TT);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// TW profile
		stp.planFastestProfile(-5, 6, -1, 3, 1.5, 2, 1.25);
		assertEquals(stp.getDuration(), 6.6834, 0.0001);
		assertEquals(stp.pos(100), 6, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// WT profile
		stp.planFastestProfile(-5, 5, 2, 3, 0, 2, 2);
		assertEquals(stp.getDuration(), 4.8190, 0.0001);
		assertEquals(stp.pos(100), 5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WT);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// WW profile
		stp.planFastestProfile(-4, 4, 0, 2, 0, 2, 1);
		assertEquals(stp.getDuration(), 6.8284, 0.0001);
		assertEquals(stp.pos(100), 4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());
	}

	/// test 3 cases for standard profiles with cruising phase with a0 > amax
	//@Test
	public void testCruiseProfilesSpecial() {
		Stp7 stp = new Stp7();
		// a0>amax, double deceleration, TT
		stp.planFastestProfile(-3, 3, -2, 2, 3, 1.5, 2);
		assertEquals(stp.getDuration(), 6.0990, 0.0001);
		assertEquals(stp.pos(100), 3, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TT);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// a(1.5)>amax, double deceleration, WW
		stp.planFastestProfile(-4, 4, 1.5, 2, 1.5, 2, 1.5);
		assertEquals(stp.getDuration(), 5.1037, 0.0001);
		assertEquals(stp.pos(100), 4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// a0>amax, double deceleration, WW
		stp.planFastestProfile(-4, 4, 1, 2, 2.5, 2, 2);
		assertEquals(stp.getDuration(), 4.8248, 0.0001);
		assertEquals(stp.pos(100), 4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());
	}

	/// test 4 basic cases for standard profiles without cruising phase
	//@Test
	public void testNoCruiseProfilesStandard() {
		Stp7 stp = new Stp7();

		// WW profile
		stp.planFastestProfile(4, -4, 0, 3, 0, 2, 1);
		assertEquals(stp.getDuration(), 6.3496, 0.0001);
		assertEquals(stp.pos(100), -4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// TW profile
		stp.planFastestProfile(0, 10, 15, 12, -5, 8, 4);
		assertEquals(stp.getDuration(), 5.291, 0.0001);
		assertEquals(stp.pos(100), 10, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TW);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// TT profile
		stp.planFastestProfile(-4, 4, 0, 4, 0, 3, 4);
		// calc7st(4,4,3,4,0,0,-4,true);
		assertEquals(stp.getDuration(), 4.1010, 0.0001);
		assertEquals(stp.pos(100), 4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TT);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// WT profile
		stp.planFastestProfile(-10, 4, 4.5, 5, 0.2, 2, 1);
		assertEquals(stp.getDuration(), 5.1591, 0.0001);
		assertEquals(stp.pos(100), 4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WT);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());
	}

	/// test 4 cases for double deceleration profiles without cruising phase
	//@Test
	public void testNoCruiseProfileDoubleDec() {
		Stp7 stp = new Stp7();

		// WW profile
		// calc7st(4.228053, 0.857550, 1.727873, 3.105438, -0.2, 5,
		// -10.239185,true);
		stp.planFastestProfile(-10.239185, 4.228053, 5, 3.105438, -0.2, 1.727873, 0.857550);
		assertEquals(stp.getDuration(), 5.7485, 1e-4);
		assertEquals(stp.pos(100), 4.228053, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// TW profile
		// calc7st(4, 3,2,1, 4,0.5, -3, true);
		stp.planFastestProfile(-3, 4, 0.5, 1, 4, 2, 3);
		assertEquals(stp.getDuration(), 3.9662, 1e-4);
		assertEquals(stp.pos(100), 4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TW);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// WT profile
		// calc7st(4.228053, 0.857550, 1.727873, 3.105438, 0.2, 5,
		// -10.239185,true)
		stp.planFastestProfile(-10.239185, 4.228053, 5, 3.105438, 0.2, 1.727873, 0.857550);
		assertEquals(stp.getDuration(), 5.42855, 1e-4);
		assertEquals(stp.pos(100), 4.228053, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WT);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// TT profile
		// calc7st(3.7, 3,2,1, 4,0.5, -3, true);
		stp.planFastestProfile(-3, 3.7, 0.5, 1, 4, 2, 3);
		assertEquals(stp.getDuration(), 3.6613, 1e-4);
		assertEquals(stp.pos(100), 3.7, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TT);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());
	}

	/// test the stretching of 4 profiles with cruising phase
	//@Test
	public void testCruiseProfileStretched() {
		Stp7 stp = new Stp7();

		// TcT ==> TcT
		// [t,j] = calc7st(5,2,2,3,0,0,-5);
		// stretch7st(t,j,6.5,5,2,2,3,0,0,-5,true);
		stp.planFastestProfile(-5, 5, 0, 3, 0, 2, 2);
		stp.scaleToDuration(6.5);
		assertEquals(stp.getDuration(), 6.5, 1e-6);
		assertEquals(stp.pos(100), 5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TT);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// TcT ==> WcW
		// [t,j] = calc7st(5,2,2,3,0,0,-5);
		// stretch7st(t,j,10,5,2,2,3,0,0,-5,true);
		stp.planFastestProfile(-5, 5, 0, 3, 0, 2, 2);
		stp.scaleToDuration(10);
		assertEquals(stp.getDuration(), 10, 1e-6);
		assertEquals(stp.pos(100), 5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// TcT, a0 ==> TcW, a0
		// [t,j] = calc7st(3,2,1.5,2,3,-2,-3);
		// stretch7st(t,j,10,3,2,1.5,2,3,-2,-3,true);
		stp.planFastestProfile(-3, 3, -2, 2, 3, 1.5, 2);
		stp.scaleToDuration(10);
		assertEquals(stp.getDuration(), 10, 1e-6);
		assertEquals(stp.pos(100), 3, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// WcT ==> WcT
		// [t,j] = calc7st(5,2,2,3,0,2,-5);
		// stretch7st(t,j,5,5,2,2,3,0,2,-5,true);
		stp.planFastestProfile(-5, 5, 2, 3, 0, 2, 2);
		stp.scaleToDuration(5);
		assertEquals(stp.getDuration(), 5, 1e-6);
		assertEquals(stp.pos(100), 5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WT);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());
	}

	/// test the stretching of 3 profiles without a cruising phase
	//@Test
	public void testNoCruiseProfileStretched() {
		Stp7 stp = new Stp7();

		// WW ==> WcW
		// [t,j] = calc7st(4,1,2,3,0,0,-4);
		// stretch7st(t,j,7,4,1,2,3,0,0,-4,true);
		stp.planFastestProfile(-4, 4, 0, 3, 0, 2, 1);
		stp.scaleToDuration(7);
		assertEquals(stp.getDuration(), 7, 1e-6);
		assertEquals(stp.pos(100), 4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// TW, v0 ==> TcW, v0
		// [t,j] = calc7st(10, 4, 8, 12, -5, 15, 0, true);
		// stretch7st(t, j, 5.5, 10, 4, 8, 12, -5, 15, 0, true);
		stp.planFastestProfile(0, 10, 15, 12, -5, 8, 4);
		stp.scaleToDuration(5.5);
		assertEquals(stp.getDuration(), 5.5, 1e-6);
		assertEquals(stp.pos(100), 10, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());

		// TT ==> WcW
		// [t,j] = calc7st(4,4,3,4,0,0,-4);
		// stretch7st(t,j,6,4,4,3,4,0,0,-4,bPlot);
		stp.planFastestProfile(-4, 4, 0, 4, 0, 3, 4);
		stp.scaleToDuration(6);
		assertEquals(stp.getDuration(), 6, 1e-6);
		assertEquals(stp.pos(100), 4, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(!stp.isDoubleDecProfile());
	}

	/// tests the stretching of 9 double deceleration profiles
	void testDoubleDecProfileStretched() {
		Stp7 stp = new Stp7();

		// TcT, a0 ==> ddec WcW
		// [t,j] = calc7st(3,2,1.5,2,3,-2,-3);
		// stretch7st(t,j,30,3,2,1.5,2,3,-2,-3,bPlot);
		stp.planFastestProfile(-3, 3, -2, 2, 3, 1.5, 2);
		stp.scaleToDuration(30);
		assertEquals(stp.getDuration(), 30, 1e-6);
		assertEquals(stp.pos(100), 3, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// WW, ddec ==> WW, ddec
		// [t,j] = calc7st(4.5, 0.8, 1.7, 3, 0.2, 4.5, -10);
		// stretch7st(t,j,6,4.5, 0.8, 1.7, 3, 0.2, 4.5, -10, bPlot);
		stp.planFastestProfile(-10, 4.5, 4.5, 3, 0.2, 1.7, 0.8);
		stp.scaleToDuration(6);
		assertEquals(stp.getDuration(), 6, 1e-6);
		assertEquals(stp.pos(100), 4.5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// WW, ddec ==> WcW, ddec
		// [t,j] = calc7st(4.5, 0.8, 1.7, 3, 0.2, 4.5, -10);
		// stretch7st(t,j,7,4.5, 0.8, 1.7, 3, 0.2, 4.5, -10, bPlot);
		stp.planFastestProfile(-10, 4.5, 4.5, 3, 0.2, 1.7, 0.8);
		stp.scaleToDuration(7);
		assertEquals(stp.getDuration(), 7, 1e-6);
		assertEquals(stp.pos(100), 4.5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// TT, ddec ==> TcW, ddec
		// [t,j] = calc7st(10, 0.8, 1.5, 3, -0.1, 6.5, -10);
		// stretch7st(t,j,7.5,10, 0.8, 1.5, 3, -0.1, 6.5, -10, bPlot);
		stp.planFastestProfile(-10, 10, 6.5, 3, -0.1, 1.5, 0.8);
		stp.scaleToDuration(7.5);
		assertEquals(stp.getDuration(), 7.5, 1e-6);
		assertEquals(stp.pos(100), 10, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TW);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// WT, ddec ==> TW, ddec
		// [t,j] = calc7st(4.5, 0.8, 1.7, 3, 0.2, 5, -10);
		// stretch7st(t,j,6,4.5, 0.8, 1.7, 3, 0.2, 5, -10, bPlot);
		stp.planFastestProfile(-10, 4.5, 5, 3, 0.2, 1.7, 0.8);
		stp.scaleToDuration(6);
		assertEquals(stp.getDuration(), 6, 1e-6);
		assertEquals(stp.pos(100), 4.5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TW);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// WT, ddec ==> WcT, ddec
		// [t,j] = calc7st(8, 0.8, 1.4, 3.5, -0.1, 5, -10);
		// stretch7st(t,j,7,8, 0.8, 1.4, 3.5, -0.1, 5, -10,bPlot);
		stp.planFastestProfile(-10, 8, 5, 3.5, -0.1, 1.4, 0.8);
		stp.scaleToDuration(7);
		assertEquals(stp.getDuration(), 7, 1e-6);
		assertEquals(stp.pos(100), 8, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WT);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// WT, ddec ==> WT, ddec
		// [t,j] = calc7st(4.5, 0.8, 1.7, 3, 0.2, 5, -10);
		// stretch7st(t,j,5.5,4.5, 0.8, 1.7, 3, 0.2, 5, -10, bPlot);
		stp.planFastestProfile(-10, 4.5, 5, 3, 0.2, 1.7, 0.8);
		stp.scaleToDuration(5.5);
		assertEquals(stp.getDuration(), 5.5, 1e-6);
		assertEquals(stp.pos(100), 4.5, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_WT);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// TT, ddec ==> TcT, ddec
		// [t,j] = calc7st(15, 0.8, 1.4, 3.5, -0.1, 6.5, -10);
		// stretch7st(t,j,8.2,15, 0.8, 1.4, 3.5, -0.1, 6.5, -10,bPlot);
		stp.planFastestProfile(-10, 15, 6.5, 3.5, -0.1, 1.4, 0.8);
		stp.scaleToDuration(8.2);
		assertEquals(stp.getDuration(), 8.2, 1e-6);
		assertEquals(stp.pos(100), 15, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TT);
		assertTrue(stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());

		// TT, ddec ==> TT, ddec
		// [t,j] = calc7st(10, 0.8, 1.5, 3, -0.1, 6.5, -10);
		// stretch7st(t,j,6.3,10, 0.8, 1.5, 3, -0.1, 6.5, -10, bPlot);
		stp.planFastestProfile(-10, 10, 6.5, 3, -0.1, 1.5, 0.8);
		stp.scaleToDuration(6.3);
		assertEquals(stp.getDuration(), 6.3, 1e-6);
		assertEquals(stp.pos(100), 10, 1e-6);
		assertEquals(stp.getProfileType(), Stp7Profile.PROFILE_TT);
		assertTrue(!stp.hasCruisingPhase());
		assertTrue(stp.isDoubleDecProfile());
	}

	/**
	 * Runs 53214 tests covering all possible cases for the planning of
	 * stretched profiles, both ddec and canonical ones.
	 *
	 * The method choses the target position around the end positions for the
	 * zero-cruise profiles (exactly reach vmax, but immediately slow down
	 * afterwards). 21 Positions are chosen.
	 *
	 * For the other values, the method iterates through the following
	 * combinations: - amax = 1.07 - vmax = 0.92 - x0 = 0 - a0 =
	 * {-1.2,-1.07,-0.7,0.0,0.7,1.07,1.2} - v0 =
	 * {-1.2,-0.92,-0.7,0.0,0.7,0.92,1.2} - jmax = {0.23, 0.79, 1.0, 1.68, 5.1,
	 * 101} - dt = {1.0001, 1.125, 1.25, 1.375, 1.5, 1.6667, 1.83333, 2.0, 3.0,
	 * 10.0}
	 *
	 * The new duration is calcutated by multiplying the dt value with the time
	 * optimal duration.
	 *
	 * \bug last unsolved problem case!!! stp. planFastestProfile(0, 12.084, 1,
	 * 2, -1, 1.07, 1); stp.scaleToDuration(24.54);
	 */
	//@Test
	public void testAutomatedStretchedProfileTest() {
		Stp7 stp = new Stp7();

		double amax = 1.07;
		double vmax = 0.92;
		double xtarget;

		double x0 = 0.;
		double[] a0 = { -1.2, -1.07, -0.7, 0.0, 0.7, 1.07, 1.2 };
		double[] v0 = { -1.2, -0.92, -0.7, 0.0, 0.7, 0.92, 1.2 };
		double[] dt = { 1.0001, 1.125, 1.25, 1.375, 1.5, 1.6667, 1.83333, 2.0, 3.0, 10.0 };
		double[] jmaxs = { 0.23, 0.79, 1.0, 1.68, 5.1, 101 };

		String testResult;
		for (int j = 0; j < 6; j++) {
			int count = 0;
			int error_counter = 0;
			int problem_counter = 0;
			double jmax = jmaxs[j];

			for (int i_a = 0; i_a < 7; i_a++) {
				for (int i_v = 0; i_v < 7; i_v++) {
					double x_fullstop = calcFullstopPosition(x0, v0[i_v], a0[i_a], amax, jmax);
					stp.planFastestProfile(x0, x_fullstop, v0[i_v], vmax, a0[i_a], amax, jmax);
					testResult = "";
					try {
						stp.testProfile();
					} catch (Exception e) {
						testResult = e.getMessage();
					}
					if (testResult != "") {
						System.out.println("yellowworld");
						System.out.println(stp.toString());
						System.out.println("Testing planFastestProfile(" + x0 + ", " + x_fullstop + ", " + v0[i_v]
								+ ", " + vmax + ", " + a0[i_a] + ", " + amax + ", " + jmax + ")");
					}
					assertEquals(testResult, "");
					count++;
					double x_neg = calcZeroCruisePosition(-1, x0, v0[i_v], vmax, a0[i_a], amax, jmax);
					double x_pos = calcZeroCruisePosition(1, x0, v0[i_v], vmax, a0[i_a], amax, jmax);
					double dp = CCMath.abs((x_neg - x_pos) / 9);
					if (x_pos < x_neg)
						x_neg = x_pos;
					for (int i_x = -5; i_x < 15; i_x++) {
						xtarget = x_neg + i_x * dp;
						stp.planFastestProfile(x0, xtarget, v0[i_v], vmax, a0[i_a], amax, jmax);
						double sum_t = stp.getDuration();
						for (int i_t = 1; i_t < 10; i_t++) {
							double Tnew = dt[i_t] * sum_t;
							// stp.scaleToDuration(Tnew));
							stp.planFastestProfile(x0, xtarget, v0[i_v], vmax, a0[i_a], amax, jmax);
							try {
								stp.scaleToDuration(Tnew);
								testResult = "";
								try {
									stp.testProfile();
								} catch (Exception e) {
									testResult = e.getMessage();
								}
								if (testResult != "") {
									System.out.println(stp.toString());
									System.out.println("Testing planFastestProfile(" + x0 + ", " + xtarget + ", "
											+ v0[i_v] + ", " + vmax + ", " + a0[i_a] + ", " + amax + ", " + jmax
											+ ") stretched to " + "a duration of " + Tnew + ".");
									problem_counter++;
								}
								assertEquals(testResult, "");
								// System.out.println(
								// stp.getDetailedProfileType();
								count++;
							} catch (Exception e) {
								System.out.println(e.getMessage());
								System.out.println(stp.toString());
								System.out.println("Testing planFastestProfile(" + x0 + ", " + xtarget + ", " + v0[i_v]
										+ ", " + vmax + ", " + a0[i_a] + ", " + amax + ", " + jmax + ") stretched to "
										+ "a duration of " + Tnew + ".");
								error_counter++;
							}
						}
					}
				}
			}
			System.out.println("Calculated stretched profile for " + count + " different start conditions.");
			if (error_counter > 0)
				System.out.println("I couldn't solve " + error_counter + " profiles at all.");
			if (problem_counter > 0)
				System.out.println("I couldn't solve " + problem_counter + " profiles correctly.");
			assertEquals(error_counter, 0);
			assertEquals(problem_counter, 0);
		}

	}
}
