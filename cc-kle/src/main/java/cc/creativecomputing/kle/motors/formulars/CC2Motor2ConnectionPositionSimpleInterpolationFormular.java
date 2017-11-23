package cc.creativecomputing.kle.motors.formulars;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionSetup;
import cc.creativecomputing.math.CCMath;

public class CC2Motor2ConnectionPositionSimpleInterpolationFormular extends CC2Motor2ConnectionPositionFormular{
	
	@CCProperty(name = "pow center", min = 0, max = 10)
	private double _cPowCenter = 0;
	@CCProperty(name = "pow upper", min = 0, max = 10)
	private double _cPowUpper = 0;
	@CCProperty(name = "pow lower", min = 0, max = 10)
	private double _cPowLower = 0;
	
	@CCProperty(name = "max angle", min = 0, max = 70)
	private double _cMaxAngle = 60;
	
	@CCProperty(name = "upper border", min = 2000, max = 0)
	private double _cUpperBorder = -290;
	@CCProperty(name = "lower border", min = 2000, max = 0)
	private double _cLowerBorderBorder = -2000;

	@Override
	public double rotation(CC2Motor2ConnectionSetup theSetup) {
		double myCenterPulleyDistace = theSetup.pulleyDistance() / 2;
		double myRot = theSetup.elementOffset().x / myCenterPulleyDistace;
		double sign = 1;
		if(myRot < 0){
			myRot *= -1;
			sign = -1;
		}
		double myPow = 1;
		if(-theSetup.elementOffset().y < myCenterPulleyDistace * 2){
			double blend = CCMath.norm(theSetup.elementOffset().y,  _cUpperBorder, -myCenterPulleyDistace * 2);
			myPow = CCMath.blend(_cPowUpper, _cPowCenter, blend);
		}else{
			double blend = CCMath.norm(theSetup.elementOffset().y, -myCenterPulleyDistace * 2, _cLowerBorderBorder);
			myPow = CCMath.blend(_cPowCenter, _cPowLower, blend);
		}
		myRot = CCMath.pow(myRot, myPow);
		return myRot * _cMaxAngle * sign;
	}

}
