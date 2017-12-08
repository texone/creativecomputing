package cc.creativecomputing.math.color;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCColorHCGMode implements CCColorMode {

	@Override
	public CCColor toRGB(double... args) {
		double h = args[0];
		double c = args[1];
		double _g = args[2];

		c = c / 100d;
		// double g = _g / 100 * 255;
		double _c = c * 255;

		CCColor myResult = new CCColor(_g, _g, _g);
		if (c != 0) {
			if (h >= 360)
				h -= 360;
			if (h < 0)
				h += 360;
			h /= 60;

			int i = CCMath.floor(h);
			double f = h - i;
			double p = _g * (1 - c);
			double q = p + _c * (1 - f);
			double t = p + _c * f;
			double v = p + _c;

			switch (i) {
			case 0:
				myResult = new CCColor(v, t, p);
				break;
			case 1:
				myResult = new CCColor(q, v, p);
				break;
			case 2:
				myResult = new CCColor(p, v, t);
				break;
			case 3:
				myResult = new CCColor(p, q, v);
				break;
			case 4:
				myResult = new CCColor(t, p, v);
				break;
			case 5:
				myResult = new CCColor(v, p, q);
				break;
			}
		}
		myResult.a = args.length > 3 ? args[3] : 1;
		return myResult;
	}

	@Override
	public double[] fromRGB(CCColor theColor) {
		double min = CCMath.min(theColor.r, theColor.g, theColor.b);
		double max = CCMath.max(theColor.r, theColor.g, theColor.b);
		double delta = max - min;
		double c = delta * 100 / 255;
		double _g = min / (255 - delta) * 100;
		double h = Double.NaN;
		if (delta != 0) {
			if (theColor.r == max)
				h = (theColor.g - theColor.b) / delta;
			if (theColor.g == max)
				h = 2 + (theColor.b - theColor.r) / delta;
			if (theColor.b == max)
				h = 4 + (theColor.r - theColor.g) / delta;
			h *= 60;
			if (h < 0)
				h += 360;
		}
		return new double[] { h, c, _g };
	}

}
