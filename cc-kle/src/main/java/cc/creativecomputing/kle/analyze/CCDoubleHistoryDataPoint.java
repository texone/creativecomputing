package cc.creativecomputing.kle.analyze;

public class CCDoubleHistoryDataPoint extends CCHistoryDataPoint{

	final double value;
	
	public CCDoubleHistoryDataPoint(double theValue, double theTimeStep){
		super(theTimeStep);
		value = theValue;
	}
}
