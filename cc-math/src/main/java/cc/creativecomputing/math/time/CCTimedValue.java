package cc.creativecomputing.math.time;

public class CCTimedValue extends CCTimed{

	final double value;
	
	public CCTimedValue(double theValue, double theTimeStep){
		super(theTimeStep);
		value = theValue;
	}
}
