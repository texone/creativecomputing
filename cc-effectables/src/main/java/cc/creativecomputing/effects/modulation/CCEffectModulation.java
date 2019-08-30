package cc.creativecomputing.effects.modulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.math.CCMath;

public class CCEffectModulation {
	
	@CCProperty(name = "amounts", min = -1, max = 1)
	private Map<String, Double> _myRelativeAmounts = new HashMap<>();

	@CCProperty(name = "id amounts")
	private Map<String, CCIdSource> _myIdAmounts = new HashMap<>();
	
	@CCProperty(name = "combiner")
	private CCModulationCombiner _myCombiner = CCModulationCombiner.ADD;
	
	private static class CCIdSource{
		
		private String _mySourceName;
		
		@CCProperty(name = "mod", min = 2, max = 48)
		public double mod = 1;
		@CCProperty(name = "mod amount", min = -1, max = 1)
		public double modAmount = 0;
		@CCProperty(name = "mod flip")
		public boolean modFlip = false;
		
		@CCProperty(name = "div", min = 2, max = 8)
		public double div = 1;
		@CCProperty(name = "div amount", min = -1, max = 1)
		public double divAmount = 0;
		@CCProperty(name = "random amount", min = -1, max = 1)
		public double random = 0;
		
		public List<Double> randoms = new ArrayList<>();
		
		private CCIdSource(String theSource){
			_mySourceName = theSource;
		}
		
		public double random(int theID) {
			while(theID >= randoms.size()) {
				randoms.add(CCMath.random());
			}
			return randoms.get(theID);
		}
	}
	
	private final CCEffectManager<?> _myEffectManager;
	
	public CCEffectModulation(CCEffectManager<?> theEffectManager){
		_myEffectManager = theEffectManager;
		
		for(String myRelativeSource:theEffectManager.relativeSources()){
			_myRelativeAmounts.put(myRelativeSource, 0d);
		}
		
		for(String myIdSource:theEffectManager.idSources()){
			_myIdAmounts.put(myIdSource, new CCIdSource(myIdSource));
		}
	}
	
	public void addRelativeSource(String theSource) {
		_myRelativeAmounts.put(theSource, 0d);
	}
	
	public void addIdSource(String theSource) {
		_myIdAmounts.put(theSource, new CCIdSource(theSource));
	}
	
	public double modulation(CCEffectable theElement) {
		return modulation(theElement, 0, 1);
	}
	
	private static double scaleValue(double theMin, double theMax, double theValue, double theOffset){
		if(theOffset < 0){
			theOffset = -theOffset;
			return CCMath.blend(theMax * theOffset, theMin * theOffset, theValue);
		}
		return CCMath.blend(theMin * theOffset, theMax * theOffset, theValue);
	}
	
	public double offsetSum(){
		double myResult = 0;
		for(Double myAmount:_myRelativeAmounts.values()){
			myResult += myAmount;
		}
		for(CCIdSource mySource:_myIdAmounts.values()){
			myResult += mySource.divAmount;
			myResult += mySource.modAmount;
			myResult += mySource.random;
		}
		return myResult;
	}
	
	private static abstract class CCStepBlender{
		
		public abstract double valueFormular(CCEffectable theElement, CCIdSource theSource, double theMin, double theMax, int theStep, double theOffset);
		
		private double value(CCEffectable theElement, CCIdSource theSource, double theMin, double theMax, double theMod, double theOffset){
			int lowerMod = CCMath.floor(theMod);
			int upperMod = CCMath.ceil(theMod);
			double modBlend = theMod - lowerMod;
			
			double myLowerModPhase = valueFormular(theElement, theSource, theMin, theMax, lowerMod, theOffset);
			double myUpperModPhase = valueFormular(theElement, theSource, theMin, theMax, upperMod, theOffset);
			return CCMath.blend(myLowerModPhase, myUpperModPhase, modBlend); 
		}
	}
	
	private CCStepBlender _myModBlender = new CCStepBlender() {
		
		@Override
		public double valueFormular(CCEffectable theElement, CCIdSource theSource, double theMin, double theMax, int theStep, double theOffset) {
			int flipDiv = (theElement.idSource(theSource._mySourceName) / CCMath.max(1, theStep));
			boolean flip = flipDiv % 2 == 1 && theSource.modFlip;
			double modBlend = (theElement.idSource(theSource._mySourceName) % CCMath.max(1, theStep)) / (double)(theStep - 1);
			if(flip)modBlend = 1 - modBlend;
			return scaleValue(theMin, theMax, modBlend, theOffset);
		}
	};
	
	private CCStepBlender _myDivBlender = new CCStepBlender() {
		
		@Override
		public double valueFormular(CCEffectable theElement, CCIdSource theSource, double theMin, double theMax, int theStep, double theOffset) {
			return scaleValue(theMin, theMax, (int)((theElement.relativeSource(theSource._mySourceName) - 0.00001)   * theStep) / (double)(theStep - 1), theOffset); 
		}
	};
	
	
	public double modulation(CCEffectable theElement, double theMin, double theMax) {
		double myResultPhase = 0;
		switch(_myCombiner) {
		case ADD:
			myResultPhase = 0;
			break;
		case MAX:
			myResultPhase = -1;
			break;
		}
		for(String myRelativeSource:_myEffectManager.relativeSources()){
			double scaleValue = scaleValue(theMin, theMax, theElement.relativeSource(myRelativeSource), _myRelativeAmounts.get(myRelativeSource));
			if(Double.isNaN(scaleValue))continue;
			switch(_myCombiner) {
			case ADD:
				myResultPhase += scaleValue;
				break;
			case MAX:
				myResultPhase = CCMath.max(myResultPhase, scaleValue);
				break;
			}
		}
		
		
		for(String myIdSource:_myEffectManager.idSources()){
			CCIdSource myIdSource2 = _myIdAmounts.get(myIdSource);
			double modBlend = _myModBlender.value(theElement, myIdSource2, theMin, theMax, myIdSource2.mod, myIdSource2.modAmount);
			double divBlend = _myDivBlender.value(theElement, myIdSource2, theMin, theMax, myIdSource2.div, myIdSource2.divAmount);
			int myID = theElement.idSource(myIdSource2._mySourceName);
			double myRandomBlend = myIdSource2.random(myID) * myIdSource2.random;
			
//			if(myIdSource.equals("row"))CCLog.info(myIdSource2._mySourceName, myID, myIdSource2.random(myID), myIdSource2.random, myIdSource2);
			
			if(Double.isNaN(modBlend) || Double.isNaN(divBlend))continue;
			
			switch(_myCombiner) {
			case ADD:
				myResultPhase += modBlend;
				myResultPhase += divBlend;
				myResultPhase += myRandomBlend;
				
				break;
			case MAX:
				myResultPhase = CCMath.max(myResultPhase, modBlend);
				myResultPhase = CCMath.max(myResultPhase, divBlend);
				break;
			}
		}
		
		return myResultPhase;
	}
	
	public static void main(String[] args) {
		for(int i = 0; i < 16;i++){
			double groupblend = i / 15f;
			int div = 5;
			System.out.println(scaleValue(-1,1,(int)((groupblend - 0.00001)   * div) / (double)(div - 1),1));
		}
	}
}
