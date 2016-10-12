package cc.creativecomputing.effects;

public class CCOffsetEffect extends CCEffect{

	@Override
	public double[] applyTo(CCEffectable theEffectable) {
		
		double myBlend = elementBlend(theEffectable);
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			myResult[i] = modulation(_myValueNames[i]).modulation(theEffectable, -1, 1) * myBlend;
		}
		return myResult;
	}

	
}
