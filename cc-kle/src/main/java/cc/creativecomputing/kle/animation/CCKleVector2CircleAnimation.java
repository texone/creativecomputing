package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCKleVector2CircleAnimation extends CCKleAnimation<CCVector2> {

	@CCProperty(name = "x amount", min = 0, max = 1)
	private double _cXAmount = 0;
	@CCProperty(name = "y amount", min = 0, max = 1)
	private double _cYAmount = 0;

	@CCProperty(name = "modulation")
	private CCKleModulation _cModulation = new CCKleModulation();

	@CCProperty(name = "amount modulation")
	private CCKleModulation _cAmountModulation = new CCKleModulation();

	@CCProperty(name = "phase speed", min = 0, max = 0.1)
	private double _cSpeed = 0;

	private double _myPhase = 0;

	public void update(final double theDeltaTime) {
		_myPhase += theDeltaTime * _cSpeed;
	}

	@Override
	public CCVector2 animate(CCSequenceElement theElement) {
		double myPhase = _myPhase;
		double myAngle = (myPhase + _cModulation.modulation(theElement, -0.5f, 0.5f)) * CCMath.TWO_PI;
		double myAmount = _cAmountModulation.modulation(theElement, -1, 1);
		double myX = CCMath.cos(myAngle) * _cXAmount * myAmount;
		double myY = CCMath.sin(myAngle) * _cYAmount * myAmount;
		double myBlend = elementBlend(theElement);
		return new CCVector2(myX * myBlend, myY * myBlend);
	}
	
	@CCProperty(name = "reset phase")
	public void resetPhase(){
		_myPhase = 0;
	}
}
