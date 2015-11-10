package cc.creativecomputing.kle.animation;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.motors.CCMotorSetup;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.random.CCRandom;

public class CCKleMotionModulation {
	@CCProperty(name = "x offset", min = -1, max = 1)
	private double _cXOffset = 0;
	@CCProperty(name = "y offset", min = -1, max = 1)
	private double _cYOffset = 0;
	@CCProperty(name = "z offset", min = -1, max = 1)
	private double _cZOffset = 0;
	@CCProperty(name = "velocity offset", min = -1, max = 1)
	private double _cVelocityOffset = 0;
	@CCProperty(name = "acceleration offset", min = -1, max = 1)
	private double _cAccelerationOffset = 0;
	@CCProperty(name = "jerk offset", min = -1, max = 1)
	private double _cJerkOffset = 0;
	
	@CCProperty(name = "max velocity")
	private double _cMaxVelocity = 0;
	@CCProperty(name = "max acceleration")
	private double _cMaxAcceleration = 0;
	@CCProperty(name = "max jerk")
	private double _cMaxJerk = 0;
	
	public CCKleMotionModulation(){
	}
	
	public double modulation(CCSequenceElement theElement) {
		return modulation(theElement, 0, 1);
	}
	
	private double scaleValue(double theMin, double theMax, double theValue, double theOffset){
		if(theOffset < 0){
			theOffset = -theOffset;
			return CCMath.blend(theMin * theOffset, theMax * theOffset, theValue);
		}
		return CCMath.blend(theMax * theOffset, theMin * theOffset, theValue);
	}
	
	public double offsetSum(){
		return  
			_cXOffset + 
			_cYOffset + 
			_cZOffset + 
			_cVelocityOffset +
			_cAccelerationOffset + 
			_cJerkOffset;
	}
	
	public double modulation(CCSequenceElement theElement, double theMin, double theMax) {
		if(theElement.motorSetup() == null)return 0;
		
		CCMotorSetup mySetup = theElement.motorSetup();
//		T4ElementInfo myInfo = theElement.elementInfo();
		double myXPhase = scaleValue(theMin, theMax, mySetup.relativeOffset().x, _cXOffset);
		double myYPhase = scaleValue(theMin, theMax, mySetup.relativeOffset().y, _cYOffset);
		double myZPhase = scaleValue(theMin, theMax, mySetup.relativeOffset().z, _cZOffset);
		
		double myVelPhase = scaleValue(theMin, theMax, CCMath.abs(mySetup.motionData().velocity / _cMaxVelocity), _cVelocityOffset); 
		double myAccPhase = scaleValue(theMin, theMax, CCMath.abs(mySetup.motionData().acceleration / _cMaxAcceleration), _cAccelerationOffset); 
		double myJerkPhase = scaleValue(theMin, theMax, CCMath.abs(mySetup.motionData().jerk / _cMaxJerk), _cJerkOffset); 
		
		return 
			myXPhase + 
			myYPhase + 
			myZPhase + 
			myVelPhase + 
			myAccPhase + 
			myJerkPhase;
	}
}
