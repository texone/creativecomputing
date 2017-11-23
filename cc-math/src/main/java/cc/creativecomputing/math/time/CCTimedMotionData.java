package cc.creativecomputing.math.time;

import cc.creativecomputing.math.CCVector3;

public class CCTimedMotionData extends CCTimed{
	public double length;
	public double velocity;
	public double acceleration;
	public double jerk;
	
	public  CCVector3 position;
	
	public CCTimedMotionData(CCVector3 thePosition, double theLength, double theVelocitiy, double theAcceleration, double theJerk, double theTimeStep){
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