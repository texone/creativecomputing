package cc.creativecomputing.kle.analyze;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCHistoryRenderer<Type extends CCHistoryDataPoint>{

	@CCProperty(name = "history size", min = 1, max = 500) int _cHistorySize = 1;
	@CCProperty(name = "time based") boolean _cTimeBased = false;
	@CCProperty(name = "time scale", min = 1, max = 300) double _cTimeScale = 300;
	@CCProperty(name = "time offset", min = 0, max = 1800)double _cTimeOffset = 0;
	@CCProperty(name = "fade out") boolean _cFadeOut = false;
	@CCProperty(name = "alpha", min = 0, max = 1) float _cAlpha = 1f;
	@CCProperty(name = "draw violations")boolean _cDrawViolations = true;
	@CCProperty(name = "draw turns")boolean _cDrawTurns = true;
	@CCProperty(name = "draw values")boolean _cDrawValues = true;
	@CCProperty(name = "violation point size", min = 1, max = 20)double _cViolationPointSize = 5;
	@CCProperty(name = "draw points")boolean _cDrawPoints = false;
	@CCProperty(name = "point size", min = 1, max = 20)double _cPointSize;
	
	public static class CCAnalyzeRange{
		@CCProperty(name = "min")
		private double _cMin = 0;
		@CCProperty(name = "max")
		private double _cMax = 1;
		
		private CCAnalyzeRange(double theMin, double theMax){
			_cMin = theMin;
			_cMax = theMax;
		}
	}
	
	public double start(){
		return _cTimeBased ? _cTimeOffset : 0;
	}
	
	@CCProperty(name = "ranges")Map<String, CCAnalyzeRange> _cRanges = new LinkedHashMap<>();
	
	public void addRange(String theKey, double theMin, double theMax){
		_cRanges.put(theKey, new CCAnalyzeRange(theMin, theMax));
	}
	
	public static interface CCHistoryValue<Type extends CCHistoryDataPoint>{
		public abstract double value(Type theData);
	}
	
	public static class CCHistoryValueSettings<Type extends CCHistoryDataPoint>{
		@CCProperty(name = "max") 
		protected double _cMax = 0;
		@CCProperty(name = "show") 
		protected boolean _cShow = false;
		@CCProperty(name = "color") 
		protected CCColor _cColor = new CCColor();
		@CCProperty(name = "abs")
		protected boolean _cAbs = false;
		@CCProperty(name = "historgram steps")
		protected int _cHistogramSteps = 10;
		
		protected final CCHistoryValue<Type> _myValue;
		
		private CCHistoryValueSettings(CCHistoryValue<Type> theValue){
			_myValue = theValue;
		}
		
		public double value(Type theData){
			return _myValue.value(theData);
		}
		
		public double normedValue(Type theData){
			return normedValue(value(theData));
		}
		
		public double normedValue(double theData){
			return CCMath.constrain((_cAbs ? CCMath.abs(theData) : theData) /_cMax, -1, 1);
		}
		
		public boolean isOverMax(Type theData){
			return CCMath.abs(value(theData)) > _cMax;
		}
	}

	@CCProperty(name = "values", hide = true)Map<String, CCHistoryValueSettings<Type>> _cValueSettings = new LinkedHashMap<>();
	
	public void addGraph(String theName, CCHistoryValue<Type> theValue){
		_cValueSettings.put(theName, new CCHistoryValueSettings<Type>(theValue));
	}
	
	private void drawCurveGraph(CCGraphics g, List<Type> theData, CCHistoryValueSettings<Type> theSettings, double theHeight, double myStart, double myEnd){
		double myStep = 0;
		g.color(theSettings._cColor.r, theSettings._cColor.g, theSettings._cColor.b, _cAlpha);
		g.pointSize(_cPointSize);
		g.beginShape(_cDrawPoints ? CCDrawMode.POINTS : CCDrawMode.LINE_STRIP);
		for(Type myData:new ArrayList<>(theData)){
			g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2), theSettings.normedValue(myData) * theHeight);
			myStep += _cTimeBased ? myData.timeStep : 1;
		}
		g.endShape();
	}
	
	private void drawCurveViolations(CCGraphics g, List<Type> theData, CCHistoryValueSettings<Type> theSettings, double theHeight, double myStart, double myEnd){
		if(!_cDrawViolations)return;
		
		g.pointSize(_cViolationPointSize);
		double myStep = 0;
		g.beginShape(CCDrawMode.POINTS);
		for(Type myData:new ArrayList<>(theData)){
			if(theSettings.isOverMax(myData)){
				g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theSettings.normedValue(myData) * theHeight);
			}
			myStep += _cTimeBased ? myData.timeStep : 1;
		}
		myStep = 0;
		for(CCAnalyzeRange myRange:_cRanges.values()){
			
		}
		g.endShape();
		
	}
	
	private void drawCurveTurns(CCGraphics g, List<Type> theData, CCHistoryValueSettings<Type> theSettings, double theHeight, double myStart, double myEnd){
		if(!_cDrawTurns)return;
		
		g.pointSize(_cViolationPointSize);
		double	myStep = 0;
		g.beginShape(CCDrawMode.POINTS);
		for(int i = 1; i < theData.size() - 1;i++){
			double myData0 = theSettings.value(theData.get(i - 1));
			double myData1 = theSettings.value(theData.get(i));
			double myData2 = theSettings.value(theData.get(i + 1));
				
			double myDif0 = myData1 - myData0;
			double myDif1 = myData2 - myData1;
				
			myStep = i * (_cTimeBased ? theData.get(i).timeStep : 1);
				
			if(CCMath.sign(myDif0) != CCMath.sign(myDif1)){
				g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theSettings.normedValue(myData1) * theHeight);
			}
		}
		g.endShape();
	}
	
	public void drawCurves(CCGraphics g, List<Type> theData, CCHistoryValueSettings<Type> theSettings, double theHeight){
		if(!theSettings._cShow)return;

		double myStart = start();
		double myEnd = _cTimeBased ? _cTimeScale + _cTimeOffset : CCMath.max(_cHistorySize, theData.size());
		
		drawCurveGraph(g, theData, theSettings, theHeight, myStart, myEnd);
		drawCurveViolations(g, theData, theSettings, theHeight, myStart, myEnd);
		drawCurveTurns(g, theData, theSettings, theHeight, myStart, myEnd);
	}
	
	public void drawCurves(CCGraphics g, List<Type> theData,  double theHeight){
		for(CCHistoryValueSettings<Type> mySettings:_cValueSettings.values()){
			drawCurves(g, theData, mySettings, theHeight);
		}
	}
	
	public void drawSpectogram(CCGraphics g, List<Type> theData, CCHistoryValueSettings<Type> theSettings, double theOffset, double theHeight){
		if(!theSettings._cShow)return;

		double myStart = _cTimeBased ? _cTimeOffset : 0;
		double myEnd = _cTimeBased ? _cTimeScale + _cTimeOffset : CCMath.max(_cHistorySize, theData.size());
		
		int myStep = 0;
		g.beginShape(CCDrawMode.QUAD_STRIP);
		for(Type myData:new ArrayList<>(theData)){
			double myFactor = CCMath.saturate(CCMath.abs(theSettings.value(myData)) / theSettings._cMax);
			g.color(theSettings._cColor.r * myFactor,theSettings._cColor.g * myFactor,theSettings._cColor.b * myFactor, _cAlpha);
			g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theHeight + theOffset);
			g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theOffset);
			myStep += _cTimeBased ? myData.timeStep : 1;
		}
		g.endShape();

		if(!_cDrawViolations)return;
		myStep = 0;
		g.color(1f,_cAlpha);
		g.beginShape(CCDrawMode.LINES);
		for(Type myData:new ArrayList<>(theData)){
			if(CCMath.abs(theSettings.value(myData))>theSettings._cMax){
				g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theHeight + theOffset);
				g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theOffset);
			}
			myStep += _cTimeBased ? myData.timeStep : 1;
		}
		g.endShape();
	}
	
	public void drawSpectogram(CCGraphics g, List<Type> theData, double theOffset, double theHeight){
		for(CCHistoryValueSettings<Type> mySettings:_cValueSettings.values()){
			drawSpectogram(g, theData, mySettings, theOffset, theHeight);
		}
	}
}