package cc.creativecomputing.kle.elements.motors;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor1ConnectionBounds extends CCMotorCalculations<CC2Motor1ConnectionSetup>{

	private double _myRopeAngle = 6;
	
	@Override
	public void updateBounds(CC2Motor1ConnectionSetup mySetup){
		mySetup.bounds().clear();
		mySetup.bounds().add(boundPoint(mySetup, 0, 1, _myTopDistance));
		mySetup.bounds().add(boundPoint(mySetup, 1, 0, _myTopDistance));
		mySetup.bounds().add(boundPoint(mySetup, 1, 0, _myBottomDistance));
		mySetup.bounds().add(boundPoint(mySetup, 0, 1, _myBottomDistance));
		
		CCVector3 myPlaneDirection = mySetup.planeDirection();
		CCVector3 animBound0  = mySetup.bounds().get(0).subtract(myPlaneDirection.multiply(mySetup.elementRadius() * _myElementRadiusScale));
		animBound0.y -= mySetup.elementRadius() * _myElementRadiusScale;
		CCVector3 animBound1  = mySetup.bounds().get(1).add(myPlaneDirection.multiply(mySetup.elementRadius() * _myElementRadiusScale));
		animBound1.y -= mySetup.elementRadius() * _myElementRadiusScale;
		CCVector3 animBound2 = mySetup.bounds().get(2).add(myPlaneDirection.multiply(mySetup.elementRadius() * _myElementRadiusScale));
		animBound2.y += mySetup.elementRadius() * _myElementRadiusScale;
		CCVector3 animBound3 = mySetup.bounds().get(3).subtract(myPlaneDirection.multiply(mySetup.elementRadius() * _myElementRadiusScale));
		animBound3.y += mySetup.elementRadius() * _myElementRadiusScale;
		
		mySetup.animationBounds().clear();
		mySetup.animationBounds().add(animBound0);
		mySetup.animationBounds().add(animBound1);
		mySetup.animationBounds().add(animBound2);
		mySetup.animationBounds().add(animBound3);
	}

	private CCVector3 boundPoint(CCMotorSetup theSetup, int theID0, int theID1, double theTopDistance){
		CCVector3 myMotorPos0 = theSetup.channels().get(theID0)._myPosition;
		CCVector3 myMotorPos1 = theSetup.channels().get(theID1)._myPosition;
		CCVector3 myCenter = myMotorPos0.add(myMotorPos1).multiply(0.5f);
		CCVector3 myDirection = myMotorPos0.subtract(myCenter).normalizeLocal();
		CCVector3 myResult = myMotorPos0.subtract(myDirection.multiply(CCMath.tan(CCMath.radians(_myRopeAngle)) * theTopDistance));
		myResult.y -= theTopDistance;
		return myResult;
	}
	
	public double minRopeAngle(){
		return _myRopeAngle;
	}
	
	@CCProperty(name = "min rope angle", min = 0, max = 30, defaultValue = 6)
	public void minRopeAngle(double theAngle){
		_myRopeAngle = theAngle;
		updateBounds();
	}

	@Override
	public CCMotorSetupTypes type() {
		return CCMotorSetupTypes.SETUP_2_MOTOR_1_CONNECTION;
	}
}
