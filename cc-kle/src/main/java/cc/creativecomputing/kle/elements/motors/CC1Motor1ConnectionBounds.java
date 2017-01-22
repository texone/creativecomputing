package cc.creativecomputing.kle.elements.motors;

import cc.creativecomputing.math.CCVector3;

public class CC1Motor1ConnectionBounds extends CCMotorCalculations<CC1Motor1ConnectionSetup>{

	@Override
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

	private CCVector3 boundPoint(CCMotorSetup theSetup, double theDistance){
		CCVector3 myResult = theSetup.channels().get(0)._myPosition.add(0,theDistance,0);
		return myResult;
	}

}
