package cc.creativecomputing.kle;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffect;
import cc.creativecomputing.effects.CCEffectModulation;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.formats.CCSequenceFormats;
import cc.creativecomputing.math.CCMatrix2;

public class CCKleSequenceAnimation extends CCEffect {
	
	@CCProperty(name = "sequence")
	private CCSequenceAsset _mySequenceAsset;
	@CCProperty(name = "amount modulation")
	private CCEffectModulation _cAmountModulation = new CCEffectModulation();
	@CCProperty(name = "offset modulation")
	private CCEffectModulation _cOffsetModulation = new CCEffectModulation();
	

	@CCProperty(name = "group id inverts")
	private Map<String, Boolean> _cGroupIdInverts = new LinkedHashMap<>();
	
	private int _myResultLength = 0;
	
	public CCKleSequenceAnimation(CCSequenceMapping<?> theMapping, final int...theChannels){
		_mySequenceAsset = new CCSequenceAsset(theMapping, CCSequenceFormats.CCA.extension());
	}
	
	public CCKleSequenceAnimation(CCSequenceMapping<?> theMapping){
		this(theMapping, 0, 1);
	}
	
	private CCMatrix2 _myFrame;

	@Override
	public void update(final double theDeltaTime) {
		_myFrame = _mySequenceAsset.frame();
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
		double myOffset = _cOffsetModulation.modulation(theEffectable, -1, 1) * _mySequenceAsset.length();
		double myValue = _mySequenceAsset.value(myOffset, theEffectable.id(), 0, theID) * 2 - 1;
		return myValue * theBLend * _cAmountModulation.modulation(theEffectable, -1, 1);
	}

	public double[] applyTo(CCEffectable theEffectable) {
		double[] myResult = new double[_myResultLength];
		if(_myFrame == null)return myResult;
	
		double myBlend = elementBlend(theEffectable);
		for(int i = 0; i < myResult.length; i++){
			myResult[i] = value(theEffectable, myBlend, i % 2 );
		}
		
		return myResult;
	}
	
	@Override
	public void valueNames(String... theValueNames) {
		_myResultLength = theValueNames.length;
	}
}
