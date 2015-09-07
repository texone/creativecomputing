package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCKleVector2RotationAnimation {

	@CCProperty(name = "x amount", min = 0, max = 1000)
	private double _cXAmount = 0;
	@CCProperty(name = "y amount", min = 0, max = 1000)
	private double _cYAmount = 0;

	@CCProperty(name = "modulation")
	private CCKleModulation _cModulation = new CCKleModulation();

	public void update(final double theDeltaTime) {
	}
	
	public void animation(CCSequenceElement theElement, CCVector2 theCenter){
		double myAngle = _cModulation.modulation(theElement, -1, 1) * CCMath.PI;
		
//		theElement.translation().subtractLocal(theCenter);
//		theElement.translation().rotateAroundOriginLocal(myAngle, false);
//		theElement.translation().addLocal(theCenter);
	}
}
