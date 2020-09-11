package cc.creativecomputing.demo.math.util;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class Rectangle {

	public double left;
	public double top;
	public double width;
	public double height;

	public double original_left;
	public double original_top;

	public Rectangle(double _left, double _top, double _width, double _height) {
		left = _left;
		top = _top;
		width = _width;
		height = _height;

		// So we can track how far the rectangle moved from original position
		original_left = _left;
		original_top = _top;

	}

	public double right() {
		return left + width;
	}

	public double bottom() {
		return top + height;
	}
	
	public double deltax() {
        return original_left - left;
	}

    public double deltay() {
        return original_top - top;
    }

	public boolean overlap(Rectangle other) {
		if (left >= other.right() || other.left >= right())
			return false;
		if (top >= other.bottom() || other.top >= bottom())
			return false;
		return true;
	}
	
	public double overlapx(Rectangle other) {
        return CCMath.max(0, CCMath.min(right(), other.right()) - CCMath.max(left, other.left));
	}

    public double overlapy(Rectangle other) {
        return CCMath.max(0, CCMath.min(bottom(), other.bottom()) - CCMath.max(top, other.top));
    }

    public Rectangle overlap_rect(Rectangle other) {
        left = CCMath.max(left, other.left);
        top = CCMath.max(top, other.top);
        return new Rectangle(left, top, overlapx(other), overlapy(other));
    }

	public void rotate(double theta) {
		double new_left = original_left + deltax() * CCMath.cos(theta) - deltay() * CCMath.sin(theta);
		double new_top = original_top + deltax() * CCMath.sin(theta) + deltay() * CCMath.cos(theta);
		left = new_left;top =  new_top;
		
	}

}
