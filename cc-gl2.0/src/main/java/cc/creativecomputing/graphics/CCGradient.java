package cc.creativecomputing.graphics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.easing.CCEasing;
import cc.creativecomputing.math.easing.CCLinearEasing;

public class CCGradient {

	protected static final class GradPoint implements Comparable<GradPoint> {

		protected double pos;
		protected CCColor color;

		GradPoint(double p, CCColor c) {
			pos = p;
			color = c;
		}

		public int compareTo(GradPoint p) {
			if (Double.compare(p.pos, pos) == 0) {
				return 0;
			} else {
				return pos < p.pos ? -1 : 1;
			}
		}

		public CCColor getColor() {
			return color;
		}

		public double getPosition() {
			return pos;
		}
	}

	protected TreeSet<GradPoint> gradient;

	protected double maxDither;

	protected CCEasing interpolator = new CCLinearEasing();

	/**
	 * Constructs a new empty gradient.
	 */
	public CCGradient() {
		gradient = new TreeSet<GradPoint>();
	}

	/**
	 * Adds a new color at specified position.
	 * 
	 * @param p
	 * @param c
	 */
	public void addColorAt(double p, CCColor c) {
		gradient.add(new GradPoint(p, c));
	}

	public List<CCColor> calcGradient() {
		double start = gradient.first().getPosition();
		return calcGradient(start, (int) (gradient.last().getPosition() - start));
	}

	/**
	 * Calculates the gradient from specified position.
	 * 
	 * @param pos
	 * @param width
	 * @return list of interpolated gradient colors
	 */
	public List<CCColor> calcGradient(double pos, int width) {
		List<CCColor> result = new ArrayList<>();

		if (gradient.size() == 0) {
			return result;
		}

		double frac = 0;
		GradPoint currPoint = null;
		GradPoint nextPoint = null;
		double endPos = pos + width;
		// find 1st color needed, clamp start position to positive values only
		for (GradPoint gp : gradient) {
			if (gp.pos < pos) {
				currPoint = gp;
			}
		}
		boolean isPremature = currPoint == null;
		TreeSet<GradPoint> activeGradient = null;
		if (!isPremature) {
			activeGradient = (TreeSet<GradPoint>) gradient.tailSet(currPoint);
		} else {
			// start position is before 1st gradient color, so use whole
			// gradient
			activeGradient = gradient;
			currPoint = activeGradient.first();
		}
		double currWidth = 0;
		Iterator<GradPoint> iter = activeGradient.iterator();
		if (currPoint != activeGradient.last()) {
			nextPoint = iter.next();
			if (isPremature) {
				double d = currPoint.pos - pos;
				currWidth = CCMath.abs(d) > 0 ? 1f / d : 1;
			} else {
				if (nextPoint.pos - currPoint.pos > 0) {
					currWidth = 1f / (nextPoint.pos - currPoint.pos);
				}
			}
		}
		while (pos < endPos) {
			if (isPremature) {
				frac = 1 - (currPoint.pos - pos) * currWidth;
			} else {
				frac = (pos - currPoint.pos) * currWidth;
			}
			// switch to next color?
			if (frac > 1.0) {
				currPoint = nextPoint;
				isPremature = false;
				if (iter.hasNext()) {
					nextPoint = iter.next();
					if (currPoint != activeGradient.last()) {
						currWidth = 1f / (nextPoint.pos - currPoint.pos);
					} else {
						currWidth = 0;
					}
					frac = (pos - currPoint.pos) * currWidth;
				}
			}
			if (currPoint != activeGradient.last()) {
				double ditheredFrac = CCMath.saturate(frac + CCMath.random() * maxDither);
				ditheredFrac = interpolator.easeIn(ditheredFrac);
				result.add(CCColor.blend(currPoint.color, nextPoint.color, ditheredFrac));
			} else {
				result.add(currPoint.color.clone());
			}
			pos++;
		}
		return result;
	}

	public List<GradPoint> getGradientPoints() {
		return new ArrayList<GradPoint>(gradient);
	}

	/**
	 * @return the interpolator
	 */
	public CCEasing getInterpolator() {
		return interpolator;
	}

	/**
	 * @return the maximum dither amount.
	 */
	public double getMaxDither() {
		return maxDither;
	}

	/**
	 * @param interpolator
	 *            the interpolator to set
	 */
	public void setInterpolator(CCEasing interpolator) {
		this.interpolator = interpolator;
	}

	/**
	 * Sets the maximum dither amount. Setting this to values >0 will jitter the
	 * interpolated colors in the calculated gradient. The value range for this
	 * parameter is 0.0 (off) to 1.0 (100%).
	 * 
	 * @param maxDither
	 */
	public void setMaxDither(double maxDither) {
		this.maxDither = CCMath.saturate(maxDither);
	}
}
