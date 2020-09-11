package cc.creativecomputing.math.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCAvoidRectangleOverlap {

	private static class CCAvoidInfo {
		CCAABoundingRectangle rect;
		CCVector2 origin;

		public CCAvoidInfo(CCAABoundingRectangle theRect) {
			rect = theRect;
			origin = theRect.min().clone();
		}

		public CCVector2 delta() {
			return origin.subtract(rect.min());
		}

		public CCVector2 min() {
			return rect.min();
		}

		private void rotate() {

			double theta = CCMath.random() * CCMath.TWO_PI;

			CCVector2 delta = delta();
			double new_left = origin.x + delta.x * CCMath.cos(theta) - delta.y * CCMath.sin(theta);
			double new_top = origin.y + delta.x * CCMath.sin(theta) + delta.y * CCMath.cos(theta);

			rect.position(new_left, new_top);
		}
	}

	// Energy measure is the total area of overlap of this rectangle with others
	private static double energy(List<CCAABoundingRectangle> theRectangles, int idx) {

		double overlap = 0;

		// TODO incorporate distance from original position?
		for (int i = 0; i < theRectangles.size(); i++) {
			if (i == idx)
				continue;
			CCVector2 rectOverlap = theRectangles.get(i).overlap(theRectangles.get(idx));
			overlap += rectOverlap.x * rectOverlap.y;
		}
		return overlap;
	}

	public static void annealing(List<CCAABoundingRectangle> theRectangles) {

		List<CCAvoidInfo> myInfos = new ArrayList<CCAvoidRectangleOverlap.CCAvoidInfo>();
		theRectangles.forEach(r -> myInfos.add(new CCAvoidInfo(r)));

		double temperature = 1.0;

		// TODO how to set/configure these parameters in a smarter way
		double magnitude = 10;
		double rounds = 5000;

		if (temperature <= 0)
			return;

		int tries = 0;
		while (tries < 10000) {
			// TODO make smarter random choice here in some way?
			int i = CCMath.random(0, myInfos.size());
			double e = energy(theRectangles, i);

			if (e < 0) {
				tries++;
				continue;
			}

			// Now, randomly translate, but remember old position
			CCAvoidInfo rect = myInfos.get(i);
			double old_top = rect.min().y;
			double old_left = rect.min().x;

			// TODO incorporate rotational moves as well?
			if (CCMath.random() > 0.5) {
				rect.rect.position(rect.min().x + CCMath.random(15) * (CCMath.random() - 0.5),
						rect.min().y + CCMath.random(15) * (CCMath.random() - 0.5));
			} else {
				rect.rotate();
			}

			double newe = energy(theRectangles, i);

			// Acceptance check compares new energy with previous w/temperature
			boolean accept = false;
			if (newe < e)
				accept = true;
			else
				accept = CCMath.random() < CCMath.exp((e - newe) / temperature);

			if (accept)
				break;
			else {
				rect.min().y = old_top;
				rect.min().x = old_left;
			}
			tries++;
		}

		temperature -= 1 / rounds;
	}

	/**
	 * This strategy looks for the largest overlaps (by area) in each step, moves
	 * one of the overlapping rectangles along the X or Y axis to eliminate the
	 * overlap, then repeats
	 * 
	 * @param theRectangles
	 */
	public static void worstFirst(List<CCAABoundingRectangle> theRectangles) {

		double maxOverlap = 0;
		CCAABoundingRectangle r1 = null, r2 = null;

		for (int i = 0; i < theRectangles.size(); i++) {
			CCAABoundingRectangle ri = theRectangles.get(i);
			for (int j = i + 1; j < theRectangles.size(); j++) {
				CCAABoundingRectangle rj = theRectangles.get(j);
				CCVector2 overlap = ri.overlap(rj);
				double overlapArea = overlap.x * overlap.y;

				if (overlapArea > maxOverlap) {
					r1 = ri;
					r2 = rj;
					maxOverlap = overlapArea;
				}
			}
		}

		if (r1 == null || r2 == null)
			return;

		CCVector2 overlap = r1.overlap(r2);

		// See which distance is bigger (x or y overlap) and move one of
		// the rectangles so they no longer overlap
		if (overlap.x > overlap.y) {
			if (CCMath.abs(r1.max().y - r2.min().y) < CCMath.abs(r1.min().y - r2.max().y)) {
				r2.min().y = r1.max().y;
			} else {
				r1.min().y = r2.max().y;
			}
		} else {
			if (CCMath.abs(r1.min().x - r2.max().x) < CCMath.abs(r1.max().x - r2.min().x)) {
				r1.min().x = r2.max().x;
			} else {
				r2.min().x = r1.max().x;
			}
		}
	}

	private static boolean overlaps_previous(List<CCAABoundingRectangle> theRectangles, int index) {
		CCAABoundingRectangle r = theRectangles.get(index);
		for (int i = 0; i < index; i++) {
			CCAABoundingRectangle rect = theRectangles.get(i);
			if (r.isColliding(rect))
				return true;
		}
		return false;
	}

	/**
	 * This strategy starts at the top, works it's way down, shifting rectangles
	 * downward as needed so they don't overlap previous rectangles above
	 * 
	 * @param theRectangles
	 */
	public static void topDown(List<CCAABoundingRectangle> theRectangles) {
		Collections.sort(theRectangles, (r1, r2) -> Double.compare(r2.min().y, r1.min().y));

		int index = 1;

		for(int i = 0; i < theRectangles.size();i++) {
			CCAABoundingRectangle r1 = theRectangles.get(i);
			for(int j = i + 1; j < theRectangles.size();j++) {
				CCAABoundingRectangle r2 = theRectangles.get(j);
				
				//CCLog.info(i,j);
				if(r1.isColliding(r2)) {
					CCLog.info("JO");
					r2.position(r2.min().x, r1.max().y);
					break;
				}
			}
		}

	}
}
