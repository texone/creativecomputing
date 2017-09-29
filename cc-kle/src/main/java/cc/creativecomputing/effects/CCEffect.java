package cc.creativecomputing.effects;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.control.CCControlMatrix;
import cc.creativecomputing.core.CCProperty;
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
	@CCProperty(name = "modulation")
	private CCControlMatrix _cModulationMatrix;
	
	protected String[] _myValueNames = new String[0];
	
	public abstract double[] applyTo(CCEffectable theEffectable);
	
	protected final String[] _myModulations;
	
	public CCEffect(String...theModulations){
		_myModulations = theModulations;
	}
	
	public CCEffect(){
		this("modulation");
	}
	
	public void valueNames(CCEffectManager<?> theEffectManager, String... theValueNames) {
		_cModulations = new LinkedHashMap<>();
		_myValueNames = theValueNames;
		
		String[] myOutputs = new String[_myModulations.length * theValueNames.length];
		String[] myInputs = new String[theEffectManager.relativeSources().size()];
		
		int s = 0;
		for(String mySource:theEffectManager.relativeSources()) {
			myInputs[s] = mySource;
			s++;
		}
		
		int j = 0;
		for(String myModulation:_myModulations){
			for(int i = 0; i < _myValueNames.length;i++){
				String myModulationName = _myValueNames[i] + " " + myModulation;
				createModulation(myModulationName, theEffectManager);
				myOutputs[j * _myValueNames.length + i] = _myValueNames[i];
			}
			j++;
		}
		
		_cModulationMatrix = new CCControlMatrix(myInputs, myOutputs);
	}
	
	public CCEffectModulation modulation(String theModulation, String theValue){
		CCEffectModulation myResult = _cModulations.get(theValue + " " + theModulation);
		if(myResult == null)throw new RuntimeException("No modulation " + theModulation + " " + theValue);
		return myResult;
	}
	
	public CCEffectModulation modulation(String theValue){
		return _cModulations.get(theValue + " modulation");
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
