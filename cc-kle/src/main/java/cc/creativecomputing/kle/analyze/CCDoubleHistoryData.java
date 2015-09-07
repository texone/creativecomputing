package cc.creativecomputing.kle.analyze;

public class CCDoubleHistoryData extends CCHistoryData{

	final double value;
	
	public CCDoubleHistoryData(double theValue, double theTimeStep){
		super(theTimeStep);
		value = theValue;
	}
}
