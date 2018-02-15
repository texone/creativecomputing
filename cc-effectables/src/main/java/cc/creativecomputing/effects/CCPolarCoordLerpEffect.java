package cc.creativecomputing.effects;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;

public class CCPolarCoordLerpEffect extends CCEffect{
	
	@CCProperty(name = "min")
	private double _cMin = -1;
	@CCProperty(name = "max")
	private double _cMax = 1;

	@Override
	public double[] applyTo(CCEffectable theEffectable) {
		
		double myBlend = elementBlend(theEffectable);
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			if(modulation(_myValueNames[i] + " modulation") == null) {
				CCLog.info(_myValueNames[i] + " modulation" );
				continue;
			}
			myResult[i] = modulation(_myValueNames[i] + " modulation").modulation(theEffectable, _cMin, _cMax) * myBlend;
		}
		return myResult;
	}

	
}
