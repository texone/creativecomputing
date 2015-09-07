/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.math.spline;

import java.util.List;

import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.CCVector4;

/**
 * <p>
 * Non-uniform rational basis spline (NURBS) is a mathematical model commonly 
 * used in computer graphics for generating and representing curves and surfaces 
 * which offers great flexibility and precision for handling both analytic and 
 * freeform shapes.
 * </p>
 * <p>
 * They allow representation of geometrical shapes in a compact form. They can be 
 * efficiently handled by the computer programs and yet allow for easy human interaction. 
 * NURBS surfaces are functions of two parameters mapping to a surface in three-dimensional 
 * space. The shape of the surface is determined by control points.
 * </p>
 * <p>
 * A NURBS curve is defined by its order, a set of weighted control points, and a knot 
 * vector. NURBS curves and surfaces are generalizations of both B-splines and Bezier 
 * curves and surfaces, the primary difference being the weighting of the control points 
 * which makes NURBS curves rational (non-rational B-splines are a special case of 
 * rational B-splines). Whereas Bezier curves evolve into only one parametric direction, 
 * usually called s or u, NURBS surfaces evolve into two parametric directions, called s and 
 * t or u and v.
 * </p>
 * <a href="http://en.wikipedia.org/wiki/Non-uniform_rational_B-spline">nurbs at wikipedia</a>
 * @author christianriekoff
 *
 */
public class CCNurbSpline extends CCSpline{
	
	private static final double KNOTS_MINIMUM_DELTA = 0.0001f;
	
	private List<Double> _myKnots; // knots of NURBS spline
	private double[] _myWeights; // weights of NURBS spline
	private int _myBasisFunctionDegree; // degree of NURBS spline basis function
										// (computed automatically)
	
	/**
	 * Create a NURBS spline. A spline type is automatically set to
	 * SplineType.Nurb. The cycle is set to <b>false</b> by default.
	 * 
	 * @param controlPoints  a list of vector to use as control points of the spline
	 * @param nurbKnots the nurb's spline knots
	 */
	public CCNurbSpline(List<CCVector4> controlPoints, List<Double> nurbKnots) {
		super(CCSplineType.NURB, false);
		// input data control
		for (int i = 0; i < nurbKnots.size() - 1; ++i) {
			if (nurbKnots.get(i) > nurbKnots.get(i + 1)) {
				throw new IllegalArgumentException(
						"The knots values cannot decrease!");
			}
		}

		// storing the data
		_myWeights = new double[controlPoints.size()];
		_myKnots = nurbKnots;
		_myBasisFunctionDegree = nurbKnots.size() - _myWeights.length;
		
		for (int i = 0; i < controlPoints.size(); ++i) {
			CCVector4 controlPoint = controlPoints.get(i);
			_myPoints.add(new CCVector3(controlPoint.x,controlPoint.y, controlPoint.z));
			_myWeights[i] = controlPoint.w;
		}
		prepareNurbsKnots(_myKnots, _myBasisFunctionDegree);
		computeTotalLentgh();
	}
	
	/**
	 * This method prepares the knots to be used. If the knots represent
	 * non-uniform B-splines (first and last knot values are being repeated) it
	 * leads to NaN results during calculations. This method adds a small number
	 * to each of such knots to avoid NaN's.
	 * 
	 * @param knots
	 *            the knots to be prepared to use
	 * @param basisFunctionDegree
	 *            the degree of basis function
	 */
	// TODO: improve this; constant delta may lead to errors if the difference
	// between tha last repeated
	// point and the following one is lower than it
	private void prepareNurbsKnots(List<Double> knots, int basisFunctionDegree) {
		double delta = KNOTS_MINIMUM_DELTA;
		double prevValue = knots.get(0).doubleValue();
		for (int i = 1; i < knots.size(); ++i) {
			double value = knots.get(i).doubleValue();
			if (value <= prevValue) {
				value += delta;
				knots.set(i, Double.valueOf(value));
				delta += KNOTS_MINIMUM_DELTA;
			} else {
				delta = KNOTS_MINIMUM_DELTA;// reset the delta's value
			}

			prevValue = value;
		}
	}

	@Override
	public void computeTotalLengthImpl() {
		// TODO implement this
		
	}
	
	/**
	 * This method computes the base function value for the NURB curve.
	 * 
	 * @param i
	 *            the knot index
	 * @param k
	 *            the base function degree
	 * @param t
	 *            the knot value
	 * @param knots
	 *            the knots' values
	 * @return the base function value
	 */
	private double computeBaseFunctionValue(int i, int k, double t, List<Double> knots) {
		if (k == 1) {
			return knots.get(i) <= t && t < knots.get(i + 1) ? 1.0f : 0.0f;
		} else {
			return 
				(t - knots.get(i)) / (knots.get(i + k - 1) - knots.get(i)) * 
				computeBaseFunctionValue(i, k - 1, t, knots) + 
				(knots.get(i + k) - t) / (knots.get(i + k) - knots.get(i + 1)) * 
				computeBaseFunctionValue(i + 1, k - 1, t, knots);
		}
	}

	@Override
	public CCVector3 interpolate(double value, int currentControlPoint) {
		int controlPointAmount = _myPoints.size();

		CCVector3 store = new CCVector3();
		double delimeter = 0;
		
		for (int i = 0; i < controlPointAmount; ++i) {
			double val = _myWeights[i] * computeBaseFunctionValue(i, _myBasisFunctionDegree, value, _myKnots);
			store.addLocal(_myPoints.get(i).multiply(val));
			delimeter += val;
		}
		return store.multiplyLocal(1f/ delimeter);
	}

	// ////////// NURBS getters /////////////////////

	/**
	 * This method returns the minimum nurb curve knot value. Check the nurb
	 * type before calling this method. It the curve is not of a Nurb type - NPE
	 * will be thrown.
	 * 
	 * @return the minimum nurb curve knot value
	 */
	public double minNurbKnot() {
		return _myKnots.get(_myBasisFunctionDegree - 1);
	}

	/**
	 * This method returns the maximum nurb curve knot value. Check the nurb
	 * type before calling this method. It the curve is not of a Nurb type - NPE
	 * will be thrown.
	 * 
	 * @return the maximum nurb curve knot value
	 */
	public double maxNurbKnot() {
		return _myKnots.get(_myWeights.length);
	}

	/**
	 * This method returns NURBS' spline knots.
	 * 
	 * @return NURBS' spline knots
	 */
	public List<Double> knots() {
		return _myKnots;
	}

	/**
	 * This method returns NURBS' spline weights.
	 * 
	 * @return NURBS' spline weights
	 */
	public double[] weights() {
		return _myWeights;
	}

	/**
	 * This method returns NURBS' spline basis function degree.
	 * 
	 * @return NURBS' spline basis function degree
	 */
	public int basisFunctionDegree() {
		return _myBasisFunctionDegree;
	}
}
