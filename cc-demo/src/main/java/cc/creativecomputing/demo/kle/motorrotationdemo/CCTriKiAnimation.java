package cc.creativecomputing.demo.kle.motorrotationdemo;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.kle.motorrotationdemo.CCTriKiDemo.CCTriangleElement;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.math.CCVector2;

public abstract class CCTriKiAnimation<AnimationType extends CCTriKiAnimation> {
	@CCProperty(name = "blend", min = 0, max = 1)
	protected double _cBlend = 0;
	
	@CCProperty(name = "channelblend", min = 0, max = 1)
	protected double _cChannelBlend = 0;
	
	public abstract double animate(CCTriangleElement theElement);
	
	public void update(final double theDeltaTime){
		
	}
	
	public double channelBlend(){
		return _cChannelBlend;
	}
	
	public abstract AnimationType createAnimation();
}
