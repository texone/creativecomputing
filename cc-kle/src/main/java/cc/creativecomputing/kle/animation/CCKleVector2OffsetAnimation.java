package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCVector2;

public class CCKleVector2OffsetAnimation extends CCKleAnimation<CCVector2> {

	@CCProperty(name = "x amount", min = 0, max = 1)
	private double _cXAmount = 0;
	@CCProperty(name = "y amount", min = 0, max = 1)
	private double _cYAmount = 0;

	@CCProperty(name = "x modulation")
	private CCKleModulation _cXModulation = new CCKleModulation();
	@CCProperty(name = "y modulation")
	private CCKleModulation _cYModulation = new CCKleModulation();
	

	@CCProperty(name = "x amount modulation")
	private CCKleModulation _cXAmountModulation = new CCKleModulation();
	@CCProperty(name = "y amount modulation")
	private CCKleModulation _cYAmountModulation = new CCKleModulation();

	public void update(final double theDeltaTime) {
	}

	public CCVector2 animate(CCSequenceElement theElement) {
		double myX = _cXModulation.modulation(theElement, -1, 1) * _cXAmount * _cXAmountModulation.modulation(theElement, -1, 1);
		double myY = _cYModulation.modulation(theElement, -1, 1) * _cYAmount * _cYAmountModulation.modulation(theElement, -1, 1);

		return new CCVector2(myX * _cBlend, myY * _cBlend);
	}
}
