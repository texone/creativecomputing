package cc.creativecomputing.kle.motors;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor2ConnectionLinearBounds extends CCMotorCalculations<CC2Motor2ConnectionLinearSetup>{

	@CCProperty(name = "max rotation", min = 0, max = 45)
	private double _cRotation = 0;
	
	public double maxRotation() {
		return _cRotation;
	}
	
//	@Override
//	public void updateBounds(CC2Motor2ConnectionLinearSetup mySetup){
//		mySetup.bounds().clear();
//		mySetup.bounds().add(boundPoint(mySetup, 0, 1, _myTopDistance));
//		mySetup.bounds().add(boundPoint(mySetup, 1, 0, _myTopDistance));
//		mySetup.bounds().add(boundPoint(mySetup, 1, 0, _myBottomDistance));
//		mySetup.bounds().add(boundPoint(mySetup, 0, 1, _myBottomDistance));
//		
//		CCVector3 myPlaneDirection = mySetup.planeDirection();
//		CCVector3 animBound0  = mySetup.bounds().get(0).subtract(myPlaneDirection.multiply(mySetup.elementRadius() * _myElementRadiusScale));
//		animBound0.y -= mySetup.elementRadius() * _myElementRadiusScale;
//		CCVector3 animBound1  = mySetup.bounds().get(1).add(myPlaneDirection.multiply(mySetup.elementRadius() * _myElementRadiusScale));
//		animBound1.y -= mySetup.elementRadius() * _myElementRadiusScale;
//		CCVector3 animBound2 = mySetup.bounds().get(2).add(myPlaneDirection.multiply(mySetup.elementRadius() * _myElementRadiusScale));
//		animBound2.y += mySetup.elementRadius() * _myElementRadiusScale;
//		CCVector3 animBound3 = mySetup.bounds().get(3).subtract(myPlaneDirection.multiply(mySetup.elementRadius() * _myElementRadiusScale));
//		animBound3.y += mySetup.elementRadius() * _myElementRadiusScale;
//		
//		mySetup.animationBounds().clear();
//		mySetup.animationBounds().add(animBound0);
//		mySetup.animationBounds().add(animBound1);
//		mySetup.animationBounds().add(animBound2);
//		mySetup.animationBounds().add(animBound3);
//	}
	
	@Override
	public void updateBounds(CC2Motor2ConnectionLinearSetup mySetup){
		mySetup.bounds().clear();
		mySetup.bounds().add(boundPoint(mySetup, _myTopDistance));
		mySetup.bounds().add(boundPoint(mySetup, _myBottomDistance));
		
		CCVector3 animBound0  = mySetup.bounds().get(0);//.subtract(0, _myElementRadiusScale,0);
		CCVector3 animBound1  = mySetup.bounds().get(1);//.add(0, _myElementRadiusScale,0);
		
		mySetup.animationBounds().clear();
		mySetup.animationBounds().add(animBound0);
		mySetup.animationBounds().add(animBound1);
	}

//	private CCVector3 boundPoint(CCMotorSetup theSetup, int theID0, int theID1, double theTopDistance){
//		CCVector3 myMotorPos0 = theSetup.channels().get(theID0)._myPosition;
//		CCVector3 myMotorPos1 = theSetup.channels().get(theID1)._myPosition;
//		CCVector3 myCenter = myMotorPos0.add(myMotorPos1).multiply(0.5f);
//		CCVector3 myDirection = myMotorPos0.subtract(myCenter).normalizeLocal();
//		CCVector3 myResult = myMotorPos0.subtract(myDirection.multiply(CCMath.tan(CCMath.radians(_myRopeAngle)) * theTopDistance));
//		myResult.y -= theTopDistance;
//		return myResult;
//	}
	
	private CCVector3 boundPoint(CC2Motor2ConnectionLinearSetup theSetup, double theDistance){
		CCVector3 myResult = theSetup.channels().get(0)._myPosition.add(theSetup.channels().get(1)._myPosition);
		myResult.multiplyLocal(0.5);
		myResult.addLocal(0,theDistance,0);
		return myResult;
	}

	@Override
	public CCMotorSetupTypes type() {
		return CCMotorSetupTypes.SETUP_2_MOTOR_2_CONNECTION_LINEAR;
	}
}
