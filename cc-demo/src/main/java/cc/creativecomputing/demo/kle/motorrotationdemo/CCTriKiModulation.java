package cc.creativecomputing.demo.kle.motorrotationdemo;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.kle.motorrotationdemo.CCTriKiDemo.CCTriangleElement;
import cc.creativecomputing.math.CCMath;

public class CCTriKiModulation {
	@CCProperty(name = "x offset", min = 0, max = 1)
	private double _cXOffset = 0;
	@CCProperty(name = "y offset", min = 0, max = 1)
	private double _cYOffset = 0;
	@CCProperty(name = "offset", min = 0, max = 1)
	private double _cOffset = 0;
	@CCProperty(name = "random offset", min = 0, max = 1)
	private double _cRandomOffset = 0;
	
	@CCProperty(name = "mod", min = 2, max = 8)
	private int _cXMod = 1;
	@CCProperty(name = "mod offset", min = 0, max = 1)
	private double _cXModOffset = 0;
	
	@CCProperty(name = "group mod", min = 2, max = 6)
	private int _cYMod = 1;
	@CCProperty(name = "group mod offset", min = 0, max = 1)
	private double _cYModOffset = 0;
	
	private double[] _myRandoms = new double[1000];
	{
		for(int i = 0; i < _myRandoms.length; i++){
			_myRandoms[i] = CCMath.random(); 
		}
	}
	
	public double modulation(CCTriangleElement theElement) {
		return modulation(theElement, 0, 1);
	}
	
	private double scaleValue(double theMin, double theMax, double theValue, double theOffset){
		return CCMath.blend(theMin * theOffset, theMax * theOffset, theValue);
	}
	
	public double offsetSum(){
		return _cXOffset + _cYOffset + _cRandomOffset + _cXOffset + _cXModOffset + _cYModOffset + _cOffset;
	}
	
	public double modulation(CCTriangleElement theElement, double theMin, double theMax) {
		double myXPhase = scaleValue(theMin, theMax, theElement.x / (double) (10 - 1), _cXOffset);
		double myYPhase = scaleValue(theMin, theMax, theElement.y / ((double) 10 - 1), _cYOffset);
		double myRandomPhase = scaleValue(theMin, theMax, _myRandoms[theElement.x * 10 + theElement.y], _cRandomOffset); 
		double myModXPhase = scaleValue(theMin, theMax, (theElement.x % _cXMod) / (double)(_cXMod - 1), _cXModOffset); 
		double myModYPhase = scaleValue(theMin, theMax, (theElement.y % _cYMod) / (double)(_cYMod - 1), _cYModOffset); 
		double myConstOffset = scaleValue(theMin, theMax, 1f, _cOffset); 
		
		return myXPhase + myYPhase + myRandomPhase + myModXPhase + myModYPhase + myConstOffset;
	}
}
