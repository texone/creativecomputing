package cc.creativecomputing.effects;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.modulation.CCConstantSource;
import cc.creativecomputing.effects.modulation.CCIDSource;
import cc.creativecomputing.effects.modulation.CCModulationSource;
import cc.creativecomputing.effects.modulation.CCRandomSource;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.filter.CCFilter;

public class CCEffectManager<Type extends CCEffectable> extends LinkedHashMap<String, CCEffect> implements CCAnimatorListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -785875230663549514L;
	
	
	protected final List<Type> _myEffectables;
	
	private final String[] _myValueNames;

	@CCProperty(name = "normalize")
	private boolean _cNormalize = false;
	@CCProperty(name = "bypass amount")
	private boolean _cBypassAmount = false;
	@CCProperty(name = "end scale")
	private double _scale = 1;
	@CCProperty(name = "end add")
	private double _add = 0;
	
	@CCProperty(name = "scales", min = 0, max = 3, defaultValue = 1, hide = true)
	private final Map<String, Double> _cScales = new LinkedHashMap<>();
	@CCProperty(name = "defaults", hide = true)
	private final Map<String, Double> _cDefaults = new LinkedHashMap<>();
	
	protected double[] _myDefaultValues;
	
	@CCProperty(name = "filters")
	private final Map<String, CCFilter> _cFilters = new LinkedHashMap<>();
	@CCProperty(name = "modulation sources")
	private Map<String, CCModulationSource> _myRelativeSources = new LinkedHashMap<>();
	private Set<String> _myIdSources = new HashSet<>();

	@CCProperty(name = "animation blender")
	protected final CCEffectBlender _myEffectBlender;
	@CCProperty(name = "combine")
	protected CCEffectCombiner _myCombiner = new CCEffectBlendCombiner();
	
	@CCProperty(name = "amount animations")
	private final Map<String, CCEffect> _cAmountEffects = new LinkedHashMap<>();
	
	
	private Map<String, Integer> _myMaxIds = new HashMap<>();
	
	
	public CCEffectManager(List<Type> theEffectables, String...theValueNames){
		_myEffectables = theEffectables;
		_myValueNames = theValueNames;
		_cScales.put("global scale", 1.0);
		for(String myValueName:_myValueNames){
			_cScales.put(myValueName + " scale", 1.0);
			_cDefaults.put(myValueName + " default", 1.0);
		}
		_myDefaultValues = new double[theValueNames.length];

		_myEffectBlender = new CCEffectBlender(this);
		addRelativeSources(new CCConstantSource(), new CCRandomSource());
		if(theEffectables.size() < 0) {
			return;
		}
		Type myFirstEffectable = theEffectables.get(0);
		for(String myIDSource:myFirstEffectable._myIdBasedSources.keySet()){
			addIdSources(myIDSource);
		}
	}
	
	public void combiner(CCEffectCombiner theCombiner) {
		_myCombiner = theCombiner;
	}
	
	public CCEffectBlender blender() {
		return _myEffectBlender;
	}
	
	public List<Type> effectables(){
		return _myEffectables;
	}
	
	public int groups(){
		return idMax(CCEffectable.GROUP_SOURCE);
	}
	
	public int columns(){
		return idMax(CCEffectable.COLUMN_SOURCE);
	}
	
	public int rows(){
		return idMax(CCEffectable.ROW_SOURCE);
	}
	
	@Override
	public CCEffect put(String theKey, CCEffect theEffect) {
		theEffect.addGroupBlends(groups());
		theEffect.valueNames(this, _myValueNames);
		return super.put(theKey, theEffect);
	}
	
	public CCEffect putAmountEffect(String theKey, CCEffect theEffect) {
		theEffect.addGroupBlends(groups());
		theEffect.valueNames(this, "amount");
		return _cAmountEffects.put(theKey, theEffect);
	}
	
	public void addFilter(String theKey, CCFilter theFilter){
		theFilter.channels(_myValueNames.length * _myEffectables.size());
		_cFilters.put(theKey, theFilter);
	}
	
	public void addRelativeSources(CCModulationSource...theRelativeSources){
		for(CCModulationSource mySource:theRelativeSources){
			_myRelativeSources.put(mySource.name(), mySource);
			_myEffectBlender.modulation().addRelativeSource(mySource.name());
		}
	}
	
	public void addIdSources(String...theIdSources){
		for(String mySource:theIdSources){
			_myIdSources.add(mySource);
			_myEffectBlender.modulation().addIdSource(mySource);
			addRelativeSources(new CCIDSource(mySource));
		}
		updateMaxIds();
	}
	
	private void updateMaxIds(CCEffectable theEffectable){
		for(String myIdSource:_myIdSources){
			if(!_myMaxIds.containsKey(myIdSource)){
				_myMaxIds.put(myIdSource, 0);
			}
			int myLastMax = _myMaxIds.get(myIdSource);
			
			_myMaxIds.put(myIdSource, CCMath.max(theEffectable.idSource(myIdSource), myLastMax));
		}
	}
	
	private void updateMaxIds(){
		for(CCEffectable myEffectable:_myEffectables){
			updateMaxIds(myEffectable);
		}
	}
	
	public void updateInfos(){
		for(Type myElement:_myEffectables){
			for(String myIdSource:_myIdSources){
				if(!_myMaxIds.containsKey(myIdSource)){
					_myMaxIds.put(myIdSource, 0);
				}
				int myLastMax = _myMaxIds.get(myIdSource);
				_myMaxIds.put(myIdSource, CCMath.max(myElement.idSource(myIdSource), myLastMax));
			}
		}
	}
	
	public Set<String> relativeSources(){
		return _myRelativeSources.keySet();
	}
	
	public Set<String> idSources(){
		return _myIdSources;
	}
	
	public int idMax(String theSource){
		Integer result = _myMaxIds.get(theSource);
		return result == null ? 0 : result;
	}
	
	public void apply(Type theEffectable, double[]theValues){
		theEffectable.apply(theValues);
	}

	@Override
	public void update(CCAnimator theAnimator){
		
		for(int i = 0; i < _myValueNames.length;i++){
			_myDefaultValues[i] = _cDefaults.get(_myValueNames[i] + " default");
		}
		for(CCModulationSource mySource:_myRelativeSources.values()){
			mySource.update(theAnimator, this);
			if(!mySource.isUpdated())continue;
			for(CCEffectable myEffectable:_myEffectables){
				mySource.updateModulation(this, myEffectable);
			}
			mySource.isUpdated(false);
		}
		
		double myBlendSumA = 0;
		double myBlendSumB = 0;
		
		for(CCEffect myEffect:values()){
			myEffect.update(theAnimator.deltaTime());
			myBlendSumA += myEffect.blend() * (1 - myEffect.channelBlend());
			myBlendSumB += myEffect.blend() * myEffect.channelBlend();
		}
//		double myCenter = 0;
		int index = 0;
		
		double[] myBlends = new double[_myEffectables.size()];
		double[][] myValuesA = new double[_myEffectables.size()][_myValueNames.length];
		double[][] myValuesB = new double[_myEffectables.size()][_myValueNames.length];
		
		for(Type myEffectable:_myEffectables){
			myEffectable.update(theAnimator);
			myEffectable.parameters(_myValueNames);
			for(CCEffect myEffect:values()){
				if(myEffect._cBlend == 0)continue;
				double[] myValues = myEffect.applyTo(myEffectable);
				
				for(int i = 0; i < myValues.length;i++){
					double myValue = myValues[i];
					if(Double.isNaN(myValue))continue;
					
					myValuesA[myEffectable.id()][i] += myValue * (1 - myEffect.channelBlend());
					myValuesB[myEffectable.id()][i] += myValue * (myEffect.channelBlend());
				}
			}
			
			if(_cNormalize){
				for(int i = 0; i < _myValueNames.length;i++){
					myValuesA[myEffectable.id()][i] = myBlendSumA == 0 ? 1 : myValuesA[myEffectable.id()][i] / myBlendSumA;
					myValuesB[myEffectable.id()][i] = myBlendSumB == 0 ? 1 : myValuesB[myEffectable.id()][i] / myBlendSumB;
				}
			}
			
			myBlends[myEffectable.id()] = _myEffectBlender.blend(myEffectable);
		}
		
		double[][] myValues = _myCombiner.combine(myBlends, myValuesA, myValuesB);
		
		for(Type myEffectable:_myEffectables){
			double myAmountValue = _cAmountEffects.size() == 0 || _cBypassAmount ? 1 : 0;
			if(!_cBypassAmount){
				for(CCEffect myAnimation:_cAmountEffects.values()){
					double[] myAmountValues = myAnimation.applyTo(myEffectable);
					
					double myValue = myAmountValues[0];
					if(Double.isNaN(myValue))continue;
						
					myAmountValue+= myValue;
				}
			}
			
//			myElement.motorSetup().rotateZ(CCMath.sign(myTranslation.x) * CCMath.pow(CCMath.abs(myTranslation.x), _cRotationPow) * _cRotationAngle);
			double myGlobalScale = _cScales.get("global scale");
			
			double[] myEffectableValues = myValues[myEffectable.id()];
			
			for(int i = 0; i < _myValueNames.length;i++){
				String myValueName = _myValueNames[i];
				
				myEffectableValues[i] = myEffectableValues[i] * _cScales.get(myValueName + " scale") * myAmountValue * _scale * myGlobalScale + _add;
				for(CCFilter myFilter:_cFilters.values()){
					myEffectableValues[i] = myFilter.process(index, myEffectableValues[i], theAnimator.deltaTime());
				}
				index++;
			}
			
			apply(myEffectable, myEffectableValues);
		}
	}
	
	@Override
	public void start(CCAnimator theAnimator) {}
	
	@Override
	public void stop(CCAnimator theAnimator) {}
}
