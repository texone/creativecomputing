package cc.creativecomputing.kle.animation;

import cc.creativecomputing.kle.elements.CCSequenceElement;

public class CCKleOffsetAnimation extends CCKleAnimation{

	@Override
	public double[] animate(CCSequenceElement theElement) {
		
		double myBlend = elementBlend(theElement);
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			myResult[i] = _cModulations.get(_myValueNames[i]).modulation(theElement, -1, 1) * myBlend;
		}
		return myResult;
	}

	
}
