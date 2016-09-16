package cc.creativecomputing.kle.elements.motors;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CC1Motor1ConnectionBounds extends CCMotorBounds{
	
	private double _myElementRadiusScale = 0;
	private double _myTopDistance = 300;
	private double _myBottomDistance = 1600;
	private double _myRopeAngle = 6;
	
	private CCSequenceElements _myElements;
	
	public void setElements(CCSequenceElements theElements){
		_myElements = theElements;
	}
	
	@CCProperty(name = "max velocity")
	private double _cMaxVelocity = 0;
	@CCProperty(name = "max acceleration")
	private double _cMaxAcceleration = 0;
	@CCProperty(name = "max jerk")
	private double _cMaxJerk = 0;
	@CCProperty(name = "apply filter")
	private boolean _cFilter = false;
	
	private double _myDeltaTime;
	
	public boolean applyFilter(){
		return _cFilter;
	}
	
	public void update(double theDeltaTime){
		_myDeltaTime = theDeltaTime;
	}
	
	public double deltaTime(){
		return _myDeltaTime;
	}
	
	public double maxVelocity(){
		return _cMaxVelocity;
	}
	
	public double maxAcceleration(){
		return _cMaxAcceleration;
	}
	
	public double maxJerk(){
		return _cMaxJerk;
	}
	
	public void updateBounds(CC1Motor1ConnectionSetup mySetup){
		mySetup.bounds().clear();
		mySetup.bounds().add(boundPoint(mySetup, _myTopDistance));
		mySetup.bounds().add(boundPoint(mySetup, _myBottomDistance));
		
		CCVector3 animBound0  = mySetup.bounds().get(0).subtract(0, _myElementRadiusScale,0);
		CCVector3 animBound1  = mySetup.bounds().get(1).add(0, _myElementRadiusScale,0);
		
		mySetup.animationBounds().clear();
		mySetup.animationBounds().add(animBound0);
		mySetup.animationBounds().add(animBound1);
	}
	
	private void updateBounds(){
		if(_myElements == null)return;
		
		for(CCSequenceElement myElement:_myElements){
			CCMotorSetup mySetup = myElement.motorSetup();
			if(!(mySetup instanceof CC1Motor1ConnectionSetup))continue;
			updateBounds((CC1Motor1ConnectionSetup)mySetup);
		}
	}

	private CCVector3 boundPoint(CCMotorSetup theSetup, double theDistance){
		CCVector3 myResult = theSetup.channels().get(0)._myPosition.add(0,theDistance,0);
		return myResult;
	}
	
	@CCProperty(name = "top distance", min = 0, max = 2000, defaultValue = 0)
	public void topDistance(double theTopDistance){
		_myTopDistance = theTopDistance;
		updateBounds();
	}
	
	public double topDistance(){
		return _myTopDistance;
	}
	
	@CCProperty(name = "bottom distance", min = 0, max = 2000, defaultValue = 2000)
	public void bottomDistance(double theBottomDistance){
		_myBottomDistance = theBottomDistance;
		updateBounds();
	}
	
	public double bottomDistance(){
		return _myBottomDistance;
	}
	
	public double minRopeAngle(){
		return _myRopeAngle;
	}
	
	@CCProperty(name = "min rope angle", min = 0, max = 30, defaultValue = 6)
	public void minRopeAngle(double theAngle){
		_myRopeAngle = theAngle;
		updateBounds();
	}
}
