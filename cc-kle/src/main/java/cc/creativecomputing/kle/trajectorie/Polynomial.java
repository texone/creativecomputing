package cc.creativecomputing.kle.trajectorie;

import cc.creativecomputing.math.CCMath;

/**
 * \brief Class for holding and root finding of polynomials. \author Erik
 * Weitnauer \date 2007
 *
 * Using the laguer algorithm decribed in "Numerical Recipes In C", 2nd edition
 * by Press, Teukolsky, e.a. for finding the roots of a (complex) polynomial,
 * p.371ff.
 */
class Polynomial {

	/*** DEFINES for Polynomial::findRoots ***/
	// fraction for rounding off imaginary part to zero
	public final static double EPS = 2.0e-10;

	/*** DEFINES for Polynomial::laguer ***/
	// estimated fractional roundoff error
	public final static double EPSS = 1.0e-15;
	// try to break (rare) limit cycles with...
	public final static int MR = 10;
	// ...different fractional values, once every...
	public final static int MT = 10;
	// ...steps for...
	public final static int MAXIT = MT * MR;
	// ...total allowed iterations.
	// public final static int FMAX

	private int degree;
	private Complex[] coeff;
	private Complex[] roots;
	private boolean foundRoots;

	/**
	 * Construcor for complex coefficients.
	 * 
	 * @param[in] degree Degree of the polynom, e.g. 2 for \f$x^2-x+1\f$
	 * @param[in] coeff Array containing the complex coeffs [0..deg]
	 *
	 *            In the case the main coefficient(s) are zero, they are
	 *            truncated.
	 */
	public Polynomial(int degree, Complex coeff[]) {
		foundRoots = false;
		while (coeff[degree].abs() == 0)
			degree--;
		this.degree = degree;
		this.coeff = new Complex[degree + 1];
		for (int i = 0; i <= degree; i++)
			this.coeff[i] = coeff[i];
	};

	/**
	 * Construcor for real coefficients.
	 * 
	 * @param degree Degree of the polynom, e.g. 2 for \f$x^2-x+1\f$
	 * @param coeff Array containing the complex coeffs [0..deg]
	 *
	 *            In the case the main coefficient(s) are zero, they are
	 *            truncated.
	 */
	public Polynomial(int degree, double coeff[]) {
		foundRoots = false;
		while (coeff[degree] == 0)
			degree--;
		this.degree = degree;
		this.coeff = new Complex[degree + 1];
		for (int i = 0; i <= degree; i++) {
			this.coeff[i].r = coeff[i];
			this.coeff[i].i = 0.0;
		}
	}

	/**
	 * Implementation of laguer algorithm for finding one root of a given
	 * polynominal.
	 * 
	 * @param a
	 * @param m
	 * @param x
	 * @return
	 */
	public static int laguer(Complex a[], int m, Complex x) {
		int iter, j;
		double abx, abp, abm, err;
		Complex dx, x1, b, d, f, g, h, sq, gp, gm, g2;
		// fractions to break a limit circle:
		double frac[] = { 0.1, 0.5, 0.25, 0.75, 0.13, 0.38, 0.62, 0.88, 1.0 };

		for (iter = 1; iter < MAXIT; iter++) {
			b = a[m];
			err = b.abs();
			d = new Complex(0, 0);
			f = new Complex(0, 0);
			abx = x.abs();
			// efficient computation of the polynomial and its first two
			// derivatives
			for (j = m - 1; j >= 0; j--) {
				f = x.multiply(f).add(d);
				d = x.multiply(d).add(b);
				b = x.multiply(b).add(a[j]);
				err = b.abs() + abx * err;
			}
			err *= EPSS;
			// estimate of roundoff error in evaluating polynomial.
			if (b.abs() <= err)
				return iter; // we are on the root
			g = d.divide(b); // the generic case:
			g2 = g.multiply(g); // use Laguerre's formular
			h = g2.subtract(f.divide(b)).multiply(2);
			sq = (((h.multiply((double) m).subtract(g2))).multiply((double) (m - 1))).sqrt();
			gp = g.add(sq);
			gm = g.subtract(sq);
			abp = gp.abs();
			abm = gm.abs();
			if (abp < abm)
				gp = gm;
			dx = ((abp > 0.0 || abm > 0.0) ? new Complex((double) m, 0.0).divide(gp)
					: new Complex(CCMath.cos(iter), CCMath.sin(iter)).multiply(CCMath.exp(CCMath.log(1 + abx))));
			x1 = x.subtract(dx);
			if (x.r == x1.r && x.i == x1.i)
				return iter; // converged
			if (iter % MT > 0) {
				x.r = x1.r;
				x.i = x1.i;
			} else {
				Complex z = x.subtract(dx.multiply(CCMath.frac(iter / MT)));
				x.r = z.r;
				x.i = z.i;
			}
			// every so ofter we take a fractional step to break any limit
			// circle.
		}
		// too many iterations - very unlikely. Try to start with different
		// starting
		// guess for the root.
		throw new RuntimeException();
	}

	/// Searches for all (complex) roots of a given polynomial.
	public static void findRoots(Complex a[], int m, Complex roots[], boolean polish) {
		int i, j, jj;
		Complex x, b, c;
		Complex[] ad = new Complex[m + 1];

		for (j = 0; j <= m; j++)
			(ad[j]) = a[j]; // copy coefficients for deflation
		for (j = m; j >= 1; j--) { // loop over each root to be found
			x = new Complex(0, 0); // start root search at zero...
			laguer(ad, j, x); // ...and find the root
			if (CCMath.abs(x.i) <= 2.0 * EPS * CCMath.abs(x.r))
				x.i = 0.0;
			roots[j - 1] = x;
			b = ad[j];
			for (jj = j - 1; jj >= 0; jj--) { // forward deflation.
				c = ad[jj];
				ad[jj] = b;
				b = x.multiply(b).add(c);
			}
		}
		if (polish) {
			for (j = 1; j <= m; j++) // Polish the roots using undeflated
				laguer(a, m, roots[j - 1]); // coefficients
		}
		for (j = 2; j <= m; j++) { // Sort roots by their real parts by
			x = roots[j - 1]; // staight insertion. All roots without
			for (i = j - 1; i >= 1; i--) { // imaginary part come first.
				if ((roots[i - 1].r <= x.r) && ((roots[i - 1].i == 0.0) || (x.i != 0.0)))
					break;
				roots[i + 1 - 1] = roots[i - 1];
			}
			roots[i + 1 - 1] = x;
		}
	}

	/**
	 * gives back the root with index i
	 * 
	 * @param i
	 * @return
	 */
	public Complex getRoot(int i) {
		if ((i < 0) && (i >= degree))
			throw new RuntimeException();
		mayComputeRoots();
		return roots[i];
	}

	/**
	 * gives back the coefficient with index i
	 * 
	 * @param i
	 * @return
	 */
	public Complex getCoeff(int i) {
		if ((i < 0) && (i > degree))
			throw new RuntimeException();
		return coeff[i];
	}

	/**
	 * calculates the value of the polynomial at x
	 * 
	 * @param x
	 * @return
	 */
	public Complex value(Complex x) {
		Complex result = coeff[degree];
		for (int i = degree - 1; i >= 0; i--) {
			result = result.multiply(x).add(coeff[i]);
		}
		return result;
	}

	public int getDegree() {
		return degree;
	}; /// < returns the degree

	/// get the smallest positive root with no imgaginary part
	public double getSmallestPositiveRealRoot() {
		mayComputeRoots();
		boolean found = false;
		int mini = 0;
		for (int i = 0; i < degree; i++) {
			if (roots[i].r > 0 && roots[i].i == 0) {
				if (!found) {
					mini = i;
					found = true;
				} else if (roots[i].r < roots[mini].r)
					mini = i;
			}
		}
		if (!found)
			throw new RuntimeException("No positive real root!");
		return roots[mini].r;
	}

	/**
	 * get the real root that is closest to zero
	 * 
	 * @return
	 */
	public double getSmallestRealRoot() {
		mayComputeRoots();
		boolean found = false;
		int mini = 0;
		for (int i = 0; i < degree; i++) {
			if (roots[i].i == 0) {
				if (!found) {
					mini = i;
					found = true;
				} else if (CCMath.abs(roots[i].r) < CCMath.abs(roots[mini].r))
					mini = i;
			}
		}
		if (!found)
			throw new RuntimeException("No real root!");
		return roots[mini].r;
	}

	@Override
	public String toString() {
		StringBuffer myResult = new StringBuffer();
		for (int i = degree; i >= 0; i--) {
			myResult.append(coeff[i]);
			myResult.append("*x^");
			myResult.append(i);
			if (i > 0)
				myResult.append(" + ");
		}
		return myResult.toString();
	}

	/**
	 * finds the roots, if they haven't been found before
	 */
	private void mayComputeRoots() {
		if (!foundRoots) {
			roots = new Complex[degree];
			findRoots(coeff, degree, roots, true);
			foundRoots = true;
		}
	}

};