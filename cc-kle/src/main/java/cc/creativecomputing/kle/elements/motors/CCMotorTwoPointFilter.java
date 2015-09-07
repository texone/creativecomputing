package cc.creativecomputing.kle.elements.motors;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCMotorTwoPointFilter {
	
	

	
	private CC2Motor2ConnectionBounds _mySettings;
	
	public CCMotorTwoPointFilter(CC2Motor2ConnectionBounds theSettings){
		_mySettings = theSettings;
	}
	
	private double _myLastVelocity = 0;
	private double _myLastAcceleration = 0;
	
	public CCVector3 filterOffset(CCVector3 theLastPosition, CCVector3 theNewPosition){
		if(!_mySettings.applyFilter())return theNewPosition;
		if(_mySettings.deltaTime() <= 0)return theNewPosition;
		
		CCVector3 myDirection = theNewPosition.subtract(theLastPosition);
		double myVelocity = myDirection.length() / _mySettings.deltaTime();
		double myAcceleration = (myVelocity - _myLastVelocity) / _mySettings.deltaTime();
		double myJerk = (myAcceleration - _myLastAcceleration) / _mySettings.deltaTime();
//		
		double myClampedJerk = CCMath.constrain(myJerk, -_mySettings.maxJerk(), _mySettings.maxJerk());
		double myClampedAcceleration = CCMath.constrain(_myLastAcceleration + myClampedJerk * _mySettings.deltaTime(), -_mySettings.maxAcceleration(), _mySettings.maxAcceleration());
		double myClampedVelocity = CCMath.constrain(_myLastVelocity + myClampedAcceleration * _mySettings.deltaTime(), -_mySettings.maxVelocity(), _mySettings.maxVelocity());
		
//		double myClampedAcceleration = CCMath.constrain(myAcceleration, -_mySettings.maxAcceleration(), _mySettings.maxAcceleration());
////		double myClampedVelocity = CCMath.constrain(myVelocity, -_mySettings.maxVelocity(), _mySettings.maxVelocity());
//		double myClampedVelocity = CCMath.constrain(_myLastVelocity + myClampedAcceleration * _mySettings.deltaTime(), -_mySettings.maxVelocity(), _mySettings.maxVelocity());
		
		_myLastAcceleration =  (myClampedVelocity - _myLastVelocity) / _mySettings.deltaTime();
		_myLastVelocity = myClampedVelocity;
		
		
		myDirection.normalizeLocal();
		myDirection.multiplyLocal(myClampedVelocity * _mySettings.deltaTime());
		
		return theLastPosition.add(myDirection);
	}
	
//	public CCVector3 filterOffset(CCVector3 theLastPosition, CCVector3 theNewPosition){
//		if(!_mySettings.applyFilter())return theNewPosition;
//		if(_mySettings.deltaTime() <= 0)return theNewPosition;
//		
//		CCVector3 myDirection = theNewPosition.subtract(theLastPosition);
//		double myAcceleration = CCMath.constrain(myDirection.length() / _mySettings.deltaTime(), -_mySettings.maxAcceleration(), _mySettings.maxAcceleration());
//		
//		_myLastVelocity += myAcceleration * _mySettings.deltaTime();
//		_myLastVelocity = CCMath.constrain(_myLastVelocity, -_mySettings.maxVelocity(), _mySettings.maxVelocity());
//		
//		myDirection.normalizeLocal();
//		myDirection.multiplyLocal(_myLastVelocity * _mySettings.deltaTime());
//		
//		return theLastPosition.add(myDirection);
//	}
}
