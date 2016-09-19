package cc.creativecomputing.effects;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

public class CCEffectModulation {
	
	@CCProperty(name = "amounts", min = -1, max = 1)
	private Map<String, Double> _myRelativeAmounts = new HashMap<>();

	@CCProperty(name = "id amounts")
	private Map<String, CCIdSource> _myIdAmounts = new HashMap<>();
	
	private static class CCIdSource{
		
		private String _mySourceName;
		
		@CCProperty(name = "mod", min = 2, max = 48)
		public double mod = 1;
		@CCProperty(name = "mod amount", min = -1, max = 1)
		public double modAmount = 0;
		
		@CCProperty(name = "div", min = 2, max = 8)
		public double div = 1;
		@CCProperty(name = "div amount", min = -1, max = 1)
		public double divAmount = 0;
		
		private CCIdSource(String theSource){
			_mySourceName = theSource;
		}
	}
	
	private final CCEffectables<?> _myEffectables;
	
	public CCEffectModulation(CCEffectables<?> theEffectables){
		_myEffectables = theEffectables;
		
		for(String myRelativeSource:theEffectables.relativeSources()){
			_myRelativeAmounts.put(myRelativeSource, 0d);
		}
		
		for(String myIdSource:theEffectables.idSources()){
			_myIdAmounts.put(myIdSource, new CCIdSource(myIdSource));
		}
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
			return scaleValue(theMin, theMax, (theElement.idSource(theSource._mySourceName) % CCMath.max(1, theStep)) / (double)(theStep - 1), theOffset);
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
		for(String myRelativeSource:_myEffectables.relativeSources()){
			double scaleValue = scaleValue(theMin, theMax, theElement.relativeSource(myRelativeSource), _myRelativeAmounts.get(myRelativeSource));
			myResultPhase += scaleValue;
		}
		
		
		for(String myIdSource:_myEffectables.idSources()){
			CCIdSource myIdSource2 = _myIdAmounts.get(myIdSource);
			
			myResultPhase += _myModBlender.value(theElement, myIdSource2, theMin, theMax, myIdSource2.mod, myIdSource2.modAmount); 
			myResultPhase += _myDivBlender.value(theElement, myIdSource2, theMin, theMax, myIdSource2.div, myIdSource2.divAmount);
		}

		CCLog.info(myResultPhase);
		
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
