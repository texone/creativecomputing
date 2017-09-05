package cc.creativecomputing.effects;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.modulation.CCEffectModulation;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;
import cc.creativecomputing.math.filter.CCFilter;

public class CCEffectBlender {
	@CCProperty(name = "filters")
	private final Map<String, CCFilter> _cFilters = new LinkedHashMap<>();

	@CCProperty(name = "modulation")
	private CCEffectModulation _cModulation;

	
	@CCProperty(name = "blend", min = 0, max = 1)
	private double _cBlend = 0;
	@CCProperty(name = "apply blend", min = 0, max = 1)
	private double _cApplyBlendRange = 0f;
	@CCProperty(name = "blend range", min = 0, max = 1)
	private double _cBlendRange = 0f;
	
	@CCProperty(name = "reverse")
	private boolean _cReverse = true;
	
	private CCEffectManager<?> _myManager;
	
	public CCEffectBlender(CCEffectManager<?> theEffectManager){
		_myManager  = theEffectManager;
		_cModulation = new CCEffectModulation(theEffectManager);
	}
	
	public CCEffectModulation modulation(){
		return _cModulation;
	}
	
	public void addFilter(String theKey, CCFilter theFilter){
		theFilter.channels(_myManager.effectables().size());
		_cFilters.put(theKey, theFilter);
	}
	
	public double blend(CCEffectable theEffectable){
		double myOffsetSum = _cModulation.offsetSum();
		double myBlendRadius = (myOffsetSum + _cBlendRange) * ((!_cReverse) ? 1 -_cBlend : _cBlend);
		
		double myModulation = myOffsetSum == 0 ? 0 : _cModulation.modulation(theEffectable);
		
		double myBlend = CCMath.saturate((myBlendRadius - myModulation)  / (_cBlendRange == 0 ? 1 : _cBlendRange));
		
		if(!_cReverse) {
			myBlend = 1 - myBlend;
		}
	
		myBlend = CCEaseFormular.SINE.easing().easeInOut(CCMath.blend(_cBlend, myBlend, _cApplyBlendRange));
		
		for(CCFilter myFilter:_cFilters.values()){
			myBlend = myFilter.process(theEffectable.id(), myBlend, 0);
		}
		
		return myBlend;
	}
}
