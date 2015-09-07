package cc.creativecomputing.math.signal;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCLinearRandom extends CCSimplexNoise{
	
	@CCProperty(name = "stepsize", min = 0, max = 1)
	private double _cStepSize = 0.1f;

	@Override
	public double[] signalImpl(double theX) {
		
		double myDiv = theX / _cStepSize;
		double myLowerStep = CCMath.floor(myDiv) * _cStepSize;
		double myUpperStep = myLowerStep + _cStepSize;
		double myBlend = CCMath.norm(theX, myLowerStep, myUpperStep);
		//CCLog.info(myBlend);
		
		
		double[] myLowerValues = super.signalImpl(myLowerStep);
		double[] myUpperValues = super.signalImpl(myUpperStep);
		
		double[] myResult = new double[myLowerValues.length];
		
		for(int i = 0; i < myResult.length;i++){
			myResult[i] = CCMath.blend(myLowerValues[i], myUpperValues[i], myBlend);
		}
		
		return myResult;
	}
	
	@Override
	public double[] signalImpl(double theX, double theY) {
		// TODO Auto-generated method stub
		return super.signalImpl(theX, theY);
	}
	
	@Override
	public double[] signalImpl(double theX, double theY, double theZ) {
		// TODO Auto-generated method stub
		return super.signalImpl(theX, theY, theZ);
	}
}
