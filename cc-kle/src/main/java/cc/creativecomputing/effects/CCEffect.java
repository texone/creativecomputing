package cc.creativecomputing.effects;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;

public abstract class CCEffect {
	
	@CCProperty(name = "blend", min = 0, max = 1)
	protected double _cBlend = 0;
	
	@CCProperty(name = "channelblend", min = 0, max = 1)
	protected double _cChannelBlend = 0;

	@CCProperty(name = "group blends", min = 0, max = 1)
	private Map<String, Double> _cGroupBlends = new LinkedHashMap<>();
	
	@CCProperty(name = "modulations")
	protected Map<String, CCEffectModulation> _cModulations = null;
	
	protected String[] _myValueNames = new String[0];
	
	public abstract double[] applyTo(CCEffectable theEffectable);
	
	public void valueNames(CCEffectables<?> theEffectables, String... theValueNames) {
		_cModulations = new LinkedHashMap<>();
		_myValueNames = new String[theValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			_myValueNames[i] = theValueNames[i] + " modulation";
			createModulation(_myValueNames[i], theEffectables);
		}
	}
	
	protected void createModulation(String theKey,CCEffectables<?> theEffectables){
		_cModulations.put(theKey, new CCEffectModulation(theEffectables));
	}
	
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
	
	public String groupKey(int theGroup){
		return "group_" + theGroup;
	}
	
	public double elementBlend(CCEffectable theEffectable){
		String myGroupKey = groupKey(theEffectable.group());
		Double myGroupBlend = _cGroupBlends.get(myGroupKey);
		if(myGroupBlend == null)myGroupBlend = 1d;
		return _cBlend;// * myGroupBlend;
	}
	
	public void blend(double theBlend){
		_cBlend = theBlend;
	}
	
}
