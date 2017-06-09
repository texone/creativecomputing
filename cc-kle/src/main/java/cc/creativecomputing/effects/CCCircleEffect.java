package cc.creativecomputing.effects;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;

public class CCCircleEffect extends CCEffect {

	@CCProperty(name = "x amount", min = 0, max = 1)
	private double _cXAmount = 0;
	@CCProperty(name = "y amount", min = 0, max = 1)
	private double _cYAmount = 0;

	

	@CCProperty(name = "phase speed", min = 0, max = 0.1)
	private double _cSpeed = 0;
	
	@CCProperty(name = "angle max", min = 1, max = 10)
	private double _cAngleMax= 0;

	private double _myPhase = 0;
	
	private int _myResultLength = 0;

	public void update(final double theDeltaTime) {
		_myPhase += theDeltaTime * _cSpeed;
	}

	@Override
	public double[] applyTo(CCEffectable theEffectable) {
		double myAngle = (_myPhase + modulation("angle").modulation(theEffectable, -1f, 1f)) * CCMath.PI * _cAngleMax;
		double myAmount = modulation("amount").modulation(theEffectable, -1, 1);
		double myBlend = elementBlend(theEffectable);
		double myX = CCMath.cos(myAngle) * _cXAmount * myAmount * myBlend;
		double myY = CCMath.sin(myAngle) * _cYAmount * myAmount * myBlend;
		
		double[] myResult = new double[_myResultLength];
		for(int i = 0; i < myResult.length;i++){
			if(i % 2 == 0){
				myResult[i] = myX;
			}else{
				myResult[i] = myY;
			}
		}
		return myResult;
	}
	
	@CCProperty(name = "reset phase")
	public void resetPhase(){
		_myPhase = 0;
	}

	@Override
	public void valueNames(CCEffectManager<?> theEffectManager, String... theValueNames) {
		_myResultLength = theValueNames.length;
		super.valueNames(theEffectManager, "angle", "amount");
	}
}
