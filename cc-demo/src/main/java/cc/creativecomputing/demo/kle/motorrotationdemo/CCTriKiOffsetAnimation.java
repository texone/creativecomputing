package cc.creativecomputing.demo.kle.motorrotationdemo;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.kle.motorrotationdemo.CCTriKiDemo.CCTriangleElement;

public class CCTriKiOffsetAnimation extends CCTriKiAnimation<CCTriKiOffsetAnimation> {

	@CCProperty(name = "x amount", min = 0, max = 1000)
	private double _cXAmount = 0;
	@CCProperty(name = "y amount", min = 0, max = 1000)
	private double _cYAmount = 0;

	@CCProperty(name = "x modulation")
	private CCTriKiModulation _cXModulation = new CCTriKiModulation();

	public void update(final double theDeltaTime) {
	}

	public double animate(CCTriangleElement theElement) {
		double myX = _cXModulation.modulation(theElement, -1, 1) * _cXAmount;

		return myX * _cBlend;
	}

	@Override
	public CCTriKiOffsetAnimation createAnimation() {
		return new CCTriKiOffsetAnimation();
	}
}
