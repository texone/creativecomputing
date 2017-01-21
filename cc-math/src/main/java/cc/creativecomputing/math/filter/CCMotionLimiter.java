package cc.creativecomputing.math.filter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

public class CCMotionLimiter extends CCFilter{

	@CCProperty(name = "lookAhead", min = 0, max = 10)
	private double _cLookAhead = 1;
	
	@CCProperty(name = "threshold", min = 0, max = 10)
	private double _cThreshold = 1;
	
	@CCProperty(name = "max jerk", min = 0, max = 5000)
	private double _cMaxJerk = 600;
	
	@CCProperty(name = "max acc", min = 0, max = 1000)
	private double _cMaxAcc = 800;
	
	@CCProperty(name = "max vel", min = 0, max = 1000)
	public double _cMaxVel = 600;
	
	@CCProperty(name = "apply scale", min = 0, max = 1)
	private double _cApplyScale = 1;
	
	@CCProperty(name = "draw targets")
	public boolean _cDrawTargets = true;
	
	@CCProperty(name = "draw future points")
	public boolean _cDrawFuturePoints = true;
	
	@CCProperty(name = "constrain 1", min = 0, max = 1)
	public double _cConstrain1 = 0;
	
	@CCProperty(name = "constrain 2", min = 0, max = 1)
	public double _cConstrain2 = 1;
	@CCProperty(name = "move start")
	public double _cMoveStart = 0;
	@CCProperty(name = "move range")
	public double _cMoveRange = 1;
	
	private class CCMotionData{
		private double _myTarget;
		private double _myPosition = Double.NaN;
		private double _myVelocity;
		private double _myAcceleration;
		
		private double _myLastTarget = Double.NaN;
		
		private double process(double theData, double theDeltaTime){
			if(CCMath.abs(theDeltaTime) >= 1 || _myLastTarget == Double.NaN){
				
				_myLastTarget = theData;
				_myTarget = theData;
				
				if(theDeltaTime == 0 && _myPosition != Double.NaN)return _myPosition;
		
				_myPosition = theData;
				_myVelocity = 0;
				_myAcceleration = 0;
				return _myPosition;
			}
			
			
			_myLastTarget = _myTarget;
			_myTarget = theData;
			
			double myTargetVelocity = (_myTarget - _myLastTarget) / theDeltaTime;
			myTargetVelocity *= _cLookAhead;
			double myFutureTarget = _myTarget + myTargetVelocity;
			
			double myMin = CCMath.min(_cConstrain1, _cConstrain2);
			double myMax = CCMath.max(_cConstrain1, _cConstrain2);
			
			myMin = CCMath.blend(_cMoveStart, _cMoveStart + _cMoveRange, myMin);
			myMax = CCMath.blend(_cMoveStart, _cMoveStart + _cMoveRange, myMax);
				
			myFutureTarget = CCMath.constrain(myFutureTarget, myMin, myMax);
			
			double myVelocity = myFutureTarget - _myPosition;
			myVelocity = CCMath.constrain(myVelocity, -_cMaxVel, _cMaxVel);
			
			double myAcceleration = (myVelocity - _myVelocity) / theDeltaTime;
			myAcceleration = CCMath.constrain(myAcceleration, -_cMaxAcc, _cMaxAcc);
			
			double myJerk = (myAcceleration - _myAcceleration) / theDeltaTime;
			myJerk = CCMath.constrain(myJerk, -_cMaxJerk, _cMaxJerk);
			
			_myAcceleration += myJerk * theDeltaTime;
			_myVelocity += _myAcceleration * theDeltaTime;
			_myPosition += _myVelocity * theDeltaTime;
			
			return CCMath.blend(theData, _myPosition, _cApplyScale);
		}
	}
	
	private CCMotionData[] _myData = new CCMotionData[1];
	
	public CCMotionLimiter(int theChannels) {
		_myChannels = theChannels;
	}
	
	public CCMotionLimiter() {
		this(1);
	}
	
	
	@Override
	public double process(int theChannel, double theData, double theDeltaTime) {
		if(_myBypass)return theData;
		
		if(_myChannels != _myData.length){
			_myData = new CCMotionData[_myChannels];
			for(int i = 0; i < _myChannels;i++){
				_myData[i] = new CCMotionData();
			}
		}
		if(_myData[theChannel] == null){
			_myData[theChannel] = new CCMotionData();
		}
		
		try{
			CCLog.info(_myData[theChannel]);
			return _myData[theChannel].process(theData, theDeltaTime);
		}catch(Exception e){
			return theData;
		}
	}
}
