package cc.creativecomputing.demo.math.util;

import java.util.List;

import cc.creativecomputing.math.CCAABoundingRectangle;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class RectangleAnnealingStrategy implements RectangleStrategy {

	public List<Rectangle> rectangles;

	public double temperature;
	public double magnitude;

	public int rounds;

	public RectangleAnnealingStrategy(List<Rectangle> _rectangles) {
		rectangles = _rectangles;

		temperature = 1.0;

		// TODO how to set/configure these parameters in a smarter way
		magnitude = 10;
		rounds = 5000;

	}

	// Energy measure is the total area of overlap of this rectangle with others
	private double energy( int idx) {
		Rectangle rect = rectangles.get(idx);
		double overlap = 0;

		// TODO incorporate distance from original position?
		for (int i = 0; i < rectangles.size(); i++) {
			if (i == idx)
				continue;
			
			Rectangle r2 = rectangles.get(i);
			overlap += r2.overlapx(rect) * r2.overlapy(rect);
		}
		return overlap;
	}

	@Override
	public void step() {
		if (temperature <= 0)
            return;

        int tries = 0;
        while (tries < 10000){
            // TODO make smarter random choice here in some way?
            int i = CCMath.random(0, rectangles.size() - 1);
            double e = energy(i);

            if (e > 0) {
                // Now, randomly translate, but remember old position
                Rectangle rect = rectangles.get(i);
                double old_top = rect.top;
                double old_left = rect.left;

                // TODO incorporate rotational moves as well?
                if (CCMath.random() > 0.5) {
                    rect.top += (CCMath.random() * 15) * (CCMath.random() - 0.5);
                    rect.left += (CCMath.random() * 15) * (CCMath.random() - 0.5);
                }else {
                    double theta = CCMath.random() * 2 * 3.1416;
                    rect.rotate(theta);
                }

                double newe = energy(i);

                // Acceptance check compares new energy with previous w/temperature
                boolean accept = false;
                if (newe < e ) {
                    accept = true;
                }else {
                        accept = CCMath.random() < CCMath.exp((e - newe) / temperature);
                }
                if (accept)
                    break;
                else {
                    rect.top = old_top;
                    rect.left = old_left;
                }
            }
            tries += 1;
        }
        temperature -= 1d / rounds;

	}
}
