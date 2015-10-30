package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCMath;

public class CCKleDoubleOffsetAnimation extends CCKleAnimation<Double> {

	@CCProperty(name = "amount", min = 0, max = 1)
	private double _cAmount = 0;

	@CCProperty(name = "modulation")
	private CCKleModulation _cModulation = new CCKleModulation();

	public void update(final double theDeltaTime) {
	}

	@Override
	public Double animate(CCSequenceElement theElement) {
		double myBlend = elementBlend(theElement);
		return CCMath.blend(-_cAmount * myBlend, _cAmount * myBlend, _cModulation.modulation(theElement));
	}
}
