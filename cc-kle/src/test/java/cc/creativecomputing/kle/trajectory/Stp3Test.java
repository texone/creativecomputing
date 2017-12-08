package cc.creativecomputing.kle.trajectory;

import static org.junit.Assert.*;
import org.junit.Test;

import cc.creativecomputing.kle.trajectorie.Stp3;

public class Stp3Test {
	@Test
	public void testBasics() {
		Stp3 stp3 = new Stp3();
		assertNotEquals(stp3.toString(), "");
		stp3.planFastestProfile(0, 10, 0, 6, 4);
		assertNotEquals(stp3.toString(), "");
		assertEquals(stp3.getDuration(), 3.16, 0.01);
		assertEquals(stp3.pos(3.16), 10, 0.1);
		assertEquals(stp3.vel(3.16), 0, 0.1);
	}

	@Test
	public void testScale() {
		Stp3 stp = new Stp3();
		// canonical
		assertNotEquals(stp.toString(), "");
		stp.planFastestProfile(0, 30, 0, 6, 4);
		assertEquals(stp.getDuration(), 6.5, 0.0001);
		assertEquals(stp.pos(8), 30, 0.0001);
		assertNotEquals(stp.toString(), "");

		stp.scaleToDuration(5);
		assertEquals(stp.getDuration(), 6.5, 0.0001);
		assertEquals(stp.pos(8), 30, 0.0001);

		stp.scaleToDuration(7);
		assertEquals(stp.getDuration(), 7, 0.0001);
		assertEquals(stp.pos(8), 30, 0.0001);

		// double dec
		stp.planFastestProfile(0, 30, 9, 6, 4);
		assertEquals(stp.getDuration(), 5.5625, 0.001);
		assertEquals(stp.pos(8), 30, 0.0001);

		stp.scaleToDuration(6);
		assertEquals(stp.getDuration(), 6, 0.001);
		assertEquals(stp.pos(8), 30, 0.0001);
	}
}
