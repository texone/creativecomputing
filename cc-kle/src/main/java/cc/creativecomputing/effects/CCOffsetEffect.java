package cc.creativecomputing.effects;

import cc.creativecomputing.core.logging.CCLog;

public class CCOffsetEffect extends CCEffect{
	
	

	@Override
	public double[] applyTo(CCEffectable theEffectable) {
		
		double myBlend = elementBlend(theEffectable);
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			if(modulation(_myValueNames[i] + " modulation") == null) {
				CCLog.info(_myValueNames[i] + " modulation" );
				continue;
			}
			myResult[i] = modulation(_myValueNames[i] + " modulation").modulation(theEffectable, -1, 1) * myBlend;
		}
		return myResult;
	}

	
}
