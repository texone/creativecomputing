package cc.creativecomputing.kle.animation;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;

public abstract class CCKleAnimation<Type> {
	
	
	@CCProperty(name = "blend", min = 0, max = 1)
	protected double _cBlend = 0;
	
	@CCProperty(name = "channelblend", min = 0, max = 1)
	protected double _cChannelBlend = 0;

	@CCProperty(name = "group blends", min = 0, max = 1)
	private Map<String, Double> _cGroupBlends = new LinkedHashMap<>();
	
	public abstract Type animate(CCSequenceElement theElement);
	
	public void update(final double theDeltaTime){
		
	}
	
	public void addGroupBlends(int theGroups){
		for(int i = 0; i <= theGroups;i++){
			_cGroupBlends.put(groupKey(i), 1d);
		}
	}
	
	public double channelBlend(){
		return _cChannelBlend;
	}
	
	public double blend(){
		return  _cBlend;
	}
	
	private String groupKey(int theGroup){
		return "group_" + theGroup;
	}
	
	public double elementBlend(CCSequenceElement theElement){
		String myGroupKey =groupKey(theElement.group());
		Double myGroupBlend = _cGroupBlends.get(myGroupKey);
		if(myGroupBlend == null)myGroupBlend = 1d;
		return _cBlend * myGroupBlend;
	}
	
	public void blend(double theBlend){
		_cBlend = theBlend;
	}
	
}
