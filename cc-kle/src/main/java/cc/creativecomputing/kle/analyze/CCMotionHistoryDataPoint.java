package cc.creativecomputing.kle.analyze;

import cc.creativecomputing.math.CCVector3;

public class CCMotionHistoryDataPoint extends CCHistoryDataPoint{
	public  double length;
	public  double velocity;
	public  double acceleration;
	public  double jerk;
	
	public  CCVector3 position;
	
	public CCMotionHistoryDataPoint(CCVector3 thePosition, double theLength, double theVelocitiy, double theAcceleration, double theJerk, double theTimeStep){
		super(theTimeStep);
		position = new CCVector3(thePosition);
		length = theLength;
		velocity = theVelocitiy;
		acceleration = theAcceleration;
		jerk = theJerk;
	}
	
	public String toString(){
		return position + " : " +length + " : " +velocity + " : " +acceleration + " : " +jerk;
	}
}