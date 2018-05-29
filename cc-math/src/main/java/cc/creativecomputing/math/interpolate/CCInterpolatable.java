package cc.creativecomputing.math.interpolate;

/**
 * Implement this interface for interpolation
 * @author christianr
 *
 * @param <ResultType>
 */
public interface CCInterpolatable<ResultType> {

	/**
	 * Interpolates a data struct like gradient, envelope or spline and returns the result of interpolation
	 * @param theValue blend value from 0 to 1
	 * @return result of interpolation
	 */
	public ResultType interpolate(double theValue);

}
