package cc.creativecomputing.kle.elements.motors.formulars;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionSetup;
import cc.creativecomputing.math.CCMath;

public class CC2Motor2ConnectionPositionHermiteInterpolationFormular extends CC2Motor2ConnectionPositionFormular{
	
	private static class CCHermiteBlend{
		@CCProperty(name = "v0" , min = -1, max = 3)
		private double _cV0 = 0.5;
		@CCProperty(name = "v3" , min = -1, max = 3)
		private double _cV3 = 0.5;
		@CCProperty(name = "bias" , min = 0, max = 1)
		private double _cBias = 0;
		@CCProperty(name = "tension" , min = -1, max = 2)
		private double _cTension = 0;
		
		public double blend(double theBlend){
			return CCMath.hermiteBlend(_cV0, 0, 1, _cV3, theBlend, _cTension, _cBias);
		}
	}
	
	@CCProperty(name = "center hermite")
	private CCHermiteBlend _cCenterBlend = new CCHermiteBlend();
	@CCProperty(name = "upper hermite")
	private CCHermiteBlend _cUpperBlend = new CCHermiteBlend();
	@CCProperty(name = "lower hermite")
	private CCHermiteBlend _cLowerBlend = new CCHermiteBlend();
	
	@CCProperty(name = "upper border", min = 2000, max = 0)
	private double _cUpperBorder = -290;
	@CCProperty(name = "lower border", min = 2000, max = 0)
	private double _cLowerBorderBorder = -2000;
	
	@CCProperty(name = "max angle", min = 0, max = 70)
	private double _cMaxAngle = 60;
	
	@Override
	public double rotation(CC2Motor2ConnectionSetup theSetup){
		double myCenterPulleyDistace = theSetup.pulleyDistance() / 2;
		double myRot = theSetup.elementOffset().x / myCenterPulleyDistace;
		double sign = 1;
		if(myRot < 0){
			myRot *= -1;
			sign = -1;
		}
		if(-theSetup.elementOffset().y < myCenterPulleyDistace * 2){
			double blend = CCMath.norm(theSetup.elementOffset().y,  _cUpperBorder, -myCenterPulleyDistace * 2);
			myRot = CCMath.blend(_cUpperBlend.blend(myRot), _cCenterBlend.blend(myRot), blend);
		}else{
			double blend = CCMath.norm(theSetup.elementOffset().y, -myCenterPulleyDistace * 2, _cLowerBorderBorder);
			myRot = CCMath.blend(_cCenterBlend.blend(myRot), _cLowerBlend.blend(myRot), blend);
		}
		return myRot * _cMaxAngle * sign;
	}
}
