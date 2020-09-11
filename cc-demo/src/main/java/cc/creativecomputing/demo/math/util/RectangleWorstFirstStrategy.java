package cc.creativecomputing.demo.math.util;

import java.util.List;

import cc.creativecomputing.math.CCMath;

public class RectangleWorstFirstStrategy implements RectangleStrategy {

	public List<Rectangle> rectangles;

	public RectangleWorstFirstStrategy(List<Rectangle> _rectangles) {
		rectangles = _rectangles;

	}
	
	private boolean appliedMove() {
		double maxOverlap = 0;
		Rectangle r1 = null, r2 = null;

		for (int i = 0; i < rectangles.size(); i++) {
			Rectangle ri = rectangles.get(i);
			for (int j = i + 1; j < rectangles.size(); j++) {
				Rectangle rj = rectangles.get(j);
				double overlapArea = ri.overlapx(rj) * ri.overlapy(rj);

				if (overlapArea > maxOverlap) {
					r1 = ri;
					r2 = rj;
					maxOverlap = overlapArea;
				}
			}
		}

		if (r1 == null || r2 == null)
			return false;

		double overlapx = r1.overlapx(r2);
		double overlapy = r1.overlapy(r2);

		// See which distance is bigger (x or y overlap) and move one of
		// the rectangles so they no longer overlap
		if (overlapx > overlapy) {
			if (CCMath.abs(r1.bottom() - r2.top) < CCMath.abs(r1.top - r2.bottom())) {
				r2.top = r1.bottom();
			} else {
				r1.top = r2.bottom();
			}
		} else {
			if (CCMath.abs(r1.left - r2.right()) < CCMath.abs(r1.right() - r2.left)) {
				r1.left = r2.right();
			} else {
				r2.left = r1.right();
			}
		}
		return true;
	}

	@Override
	public void step() {
		while(appliedMove()) {
			
		}
	}

}
