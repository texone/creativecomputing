package cc.creativecomputing.kle.analyze;

import cc.creativecomputing.math.CCVector3;

public class CCMotionData extends CCHistoryData{
	public final double length;
	public final double velocity;
	public final double acceleration;
	public final double jerk;
	
	public final CCVector3 position;
	
	public CCMotionData(CCVector3 thePosition, double theLength, double theVelocitiy, double theAcceleration, double theJerk, double theTimeStep){
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