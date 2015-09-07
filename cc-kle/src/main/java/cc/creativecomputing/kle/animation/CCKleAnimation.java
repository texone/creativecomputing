package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;

public abstract class CCKleAnimation<Type> {
	
	
	@CCProperty(name = "blend", min = 0, max = 1)
	protected double _cBlend = 0;
	
	@CCProperty(name = "channelblend", min = 0, max = 1)
	protected double _cChannelBlend = 0;
	
	public abstract Type animate(CCSequenceElement theElement);
	
	public void update(final double theDeltaTime){
		
	}
	
	public double channelBlend(){
		return _cChannelBlend;
	}
	
	public double blend(){
		return  _cBlend;
	}
	
	public void blend(double theBlend){
		_cBlend = theBlend;
	}
	
}
