package cc.creativecomputing.math.interpolate;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCVector4;

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
	
	public static void main(String[] args) {
		CCInterpolatable<CCVector4> myCheck = e -> {return new CCVector4();};
		
		CCLog.info(myCheck.getClass().getGenericInterfaces());
	}
}
