package cc.creativecomputing.kle.trajectorie;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

/**
 * \brief Represents complex numbers and offers basic operators for them.
 * \author Erik Weitnauer \date 2007
 */
class Complex {

	public double r; /// < Real part.
	public double i; /// < Imaginary part.

	public Complex() {
		r = 0;
		i = 0;
	}

	public Complex(double real) {
		r = real;
		i = 0;
	}

	public Complex(double real, double img) {
		r = real;
		i = img;
	}

	/**
	 * return conjugate-complex of the number
	 * 
	 * @return
	 */
	public Complex conjg() {
		return new Complex(r, -i);
	}

	/**
	 * return square root of the number
	 * 
	 * @return
	 */
	public Complex sqrt() {
		Complex c = new Complex();
		double x, y, w, v;
		if ((r == 0) && (i == 0)) {
			c.r = 0.;
			c.i = 0.;
		} else {
			x = CCMath.abs(r);
			y = CCMath.abs(i);
			if (x >= y) {
				v = y / x;
				w = CCMath.sqrt(x) * CCMath.sqrt(0.5 * (1.0 + CCMath.sqrt(1.0 + v * v)));
			} else {
				v = x / y;
				w = CCMath.sqrt(y) * CCMath.sqrt(0.5 * (v + CCMath.sqrt(1.0 + v * v)));
			}
			if (r >= 0.0) {
				c.r = w;
				c.i = i / (2.0 * w);
			} else {
				c.i = (i >= 0) ? w : -w;
				c.r = i / (2.0 * c.i);
			}
		}
		return c;
	}

	/**
	 * return absolute value of the number
	 * 
	 * @return
	 */
	public double abs() {
		double x, y, ans, temp;
		x = CCMath.abs(r);
		y = CCMath.abs(i);
		if (x == 0)
			ans = y;
		else if (y == 0)
			ans = x;
		else if (x > y) {
			temp = y / x;
			ans = x * CCMath.sqrt(1.0 + temp * temp);
		} else {
			temp = x / y;
			ans = y * CCMath.sqrt(1.0 + temp * temp);
		}
		return ans;
	}

	/**
	 * Add complex numbers
	 * 
	 * @param b
	 * @return
	 */
	public Complex add(Complex b) {
		return new Complex(r + b.r, i + b.i);
	}

	public Complex subtract(Complex b) {
		return new Complex(r - b.r, i - b.i);
	}

	public Complex multiply(Complex b) {
		return new Complex(r * b.r - i * b.i, r * b.i + i * b.r);
	}

	public Complex multiply(double x) {
		return new Complex(r * x, i * x);
	}

	public Complex divide(Complex b) {
		Complex c = new Complex();
		double x, den;
		if (CCMath.abs(b.r) >= CCMath.abs(b.i)) {
			x = b.i / b.r;
			den = b.r + x * b.i;
			c.r = (r + x * i) / den;
			c.i = (i - x * r) / den;
		} else {
			x = b.r / b.i;
			den = b.i + x * b.r;
			c.r = (r * x + i) / den;
			c.i = (i * x - r) / den;
		}
		return c;
	}

	public Complex set(Complex b) {
		r = b.r;
		i = b.i;
		return this;
	}

	/**
	 * @return the string representation of this vector.
	 */
	@Override
	public String toString() {
		return getClass().getName() + " [R=" + r + ", I=" + i + "]";
	}

};