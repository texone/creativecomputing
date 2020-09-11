package cc.creativecomputing.math.util;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCAvoidRectangleOverlap {
	
	private static class CCAvoidInfo{
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
		
		
		private  void rotate() {

			double theta = CCMath.random() * CCMath.TWO_PI;
			
			CCVector2 delta = delta();
			double new_left = origin.x + delta.x * CCMath.cos(theta) - delta.y * CCMath.sin(theta);
			double new_top  = origin.y + delta.x * CCMath.sin(theta) + delta.y * CCMath.cos(theta);

			rect.position(new_left,new_top);
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
	
	

	public static void Annealing(List<CCAABoundingRectangle> theRectangles) {

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
				rect.rect.position(
					rect.min().x + CCMath.random(15) * (CCMath.random() - 0.5),
					rect.min().y + CCMath.random(15) * (CCMath.random() - 0.5)
				);
			} else {
				rect.rotate();
			}

			double newe = energy(theRectangles,i);

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
}
