package cc.creativecomputing.effects;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.effects.modulation.CCEffectModulation;

public abstract class CCEffect {
	
	@CCProperty(name = "blend", min = 0, max = 1)
	protected double _cBlend = 0;
	
	@CCProperty(name = "channelblend", min = 0, max = 1)
	protected double _cChannelBlend = 0;

	@CCProperty(name = "group blends", min = 0, max = 1)
	private Map<String, Double> _cGroupBlends = new LinkedHashMap<>();
	
	@CCProperty(name = "modulations", hide = true)
	private Map<String, CCEffectModulation> _cModulations = null;
//	@CCProperty(name = "modulation")
//	private CCControlMatrix _cModulationMatrix;
	
	protected String[] _myValueNames = new String[0];
	
	public abstract double[] applyTo(CCEffectable theEffectable);
	
	public String[] modulationSources(String[]theValueNames) {
		String[] _myModulationSources = new String[theValueNames.length];
		for(int i = 0; i < _myModulationSources.length; i++) {
			_myModulationSources[i] = theValueNames[i] + " modulation"; 
		}
		return _myModulationSources;
	}
	
	public void valueNames(CCEffectManager<?> theEffectManager, String... theValueNames) {
		_cModulations = new LinkedHashMap<>();
		_myValueNames = theValueNames;
		
//		String[] myOutputs = new String[_myModulations.length * theValueNames.length];
//		String[] myInputs = new String[theEffectManager.relativeSources().size()];
//		
//		int s = 0;
//		for(String mySource:theEffectManager.relativeSources()) {
//			myInputs[s] = mySource;
//			s++;
//		}
		
		for(String myModulation:modulationSources(theValueNames)){
			createModulation(myModulation, theEffectManager);
		}
		
//		_cModulationMatrix = new CCControlMatrix(myInputs, myOutputs);
	}
	
	public String[] modulations() {
		return _myValueNames;
	}
	
	public CCEffectModulation modulation(String theValue){
		return _cModulations.get(theValue);
	}
	
	protected void createModulation(String theKey,CCEffectManager<?> theEffectManager){
		_cModulations.put(theKey, new CCEffectModulation(theEffectManager));
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
		return _cBlend * myGroupBlend;
	}
	
	public void blend(double theBlend){
		_cBlend = theBlend;
	}
	
}
