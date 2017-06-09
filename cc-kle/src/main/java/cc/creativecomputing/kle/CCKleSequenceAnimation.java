package cc.creativecomputing.kle;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffect;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.formats.CCSequenceFormats;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.interpolate.CCInterpolators;

public class CCKleSequenceAnimation extends CCEffect {
	
	@CCProperty(name = "sequence")
	private CCSequenceAsset _mySequenceAsset;

	@CCProperty(name = "interpolator")
	private CCInterpolators _myInterpolator = CCInterpolators.LINEAR;
	

	@CCProperty(name = "curve tension", min = -1, max = 2)
	private double _curveTension = 0;
	@CCProperty(name = "range min", min = 0, max = 1)
	private double _cRangeMin = 0;
	@CCProperty(name = "range max", min = 0, max = 1)
	private double _cRangeMax = 1;
	

	@CCProperty(name = "group id inverts")
	private Map<String, Boolean> _cGroupIdInverts = new LinkedHashMap<>();
	
	private int _myResultLength = 0;
	
	public CCKleSequenceAnimation(CCSequenceMapping<?> theMapping, final int...theChannels){
		_mySequenceAsset = new CCSequenceAsset(theMapping, CCSequenceFormats.CCA.extension(), CCSequenceFormats.XML.extension());
	}
	
	public CCKleSequenceAnimation(CCSequenceMapping<?> theMapping){
		this(theMapping, 0, 1);
	}
	
	@Override
	public void update(final double theDeltaTime) {
	}
	
	public void addGroupBlends(int theGroups){
		super.addGroupBlends(theGroups);
		for(int i = 0; i <= theGroups;i++){
			_cGroupIdInverts.put(groupKey(i), false);
		}
	}
	
	public CCSequenceAsset sequence(){
		return _mySequenceAsset;
	}
	
	private double value(CCEffectable theEffectable, double theBLend, int theID){
		double myOffset = modulation("offset").modulation(theEffectable, -1, 1) * _mySequenceAsset.length();
		double myValue = CCMath.blend(_cRangeMin, _cRangeMax, _mySequenceAsset.value(_myInterpolator, myOffset, theEffectable, theID)) * 2 - 1;
		return myValue * theBLend * modulation("amount").modulation(theEffectable, -1, 1);
	}

	public double[] applyTo(CCEffectable theEffectable) {
		double[] myResult = new double[_myResultLength];
	
		double myBlend = elementBlend(theEffectable);
		for(int i = 0; i < myResult.length; i++){
			myResult[i] = value(theEffectable, myBlend, i % 2 );
		}
		
		return myResult;
	}
	
	@Override
	public void valueNames(CCEffectManager<?> theEffectables, String... theValueNames) {
		_myResultLength = theValueNames.length;
		super.valueNames(theEffectables, "offset", "amount");
	}
}
