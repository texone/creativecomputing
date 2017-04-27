package cc.creativecomputing.kle.analyze;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public abstract class CCHistoryGraph<Type extends CCHistoryData>{
	@CCProperty(name = "max") 
	protected double _cMax = 0;
	@CCProperty(name = "show") 
	protected boolean _cShow = false;
	@CCProperty(name = "color") 
	protected CCColor _cColor = new CCColor();
	@CCProperty(name = "abs")
	protected boolean _cAbs = false;
	
	protected CCAnalyzeSettings _mySettings;
	
	public CCHistoryGraph(CCAnalyzeSettings theSettings){
		_mySettings = theSettings;
	}
	
	protected CCHistoryGraph(){
	}
	
	public abstract double value(Type theData);
	
	public void drawCurves(CCGraphics g, List<Type> theData, double theHeight){
		if(!_cShow)return;

		double myStart = _mySettings._cTimeBased ? _mySettings._cTimeOffset : 0;
		double myEnd = _mySettings._cTimeBased ? _mySettings._cTimeScale + _mySettings._cTimeOffset : CCMath.max(_mySettings._cHistorySize, theData.size());
		
		double myStep = 0;
		g.color(_cColor.r,_cColor.g,_cColor.b, _mySettings._cAlpha);
		g.pointSize(_mySettings._cPointSize);
		g.beginShape(_mySettings._cDrawPoints ? CCDrawMode.POINTS : CCDrawMode.LINE_STRIP);
		for(Type myData:new ArrayList<>(theData)){
			g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),CCMath.constrain((_cAbs ? CCMath.abs(value(myData)) : value(myData)) /_cMax, -1, 1) * theHeight);
			myStep += _mySettings._cTimeBased ? myData.timeStep : 1;
		}
		g.endShape();
		
		if(_mySettings._cDrawViolations){
			g.pointSize(_mySettings._cViolationPointSize);
			myStep = 0;
			g.beginShape(CCDrawMode.POINTS);
			for(Type myData:new ArrayList<>(theData)){
				if(CCMath.abs(value(myData)) > _cMax){
					g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),CCMath.constrain((_cAbs ? CCMath.abs(value(myData)) : value(myData)) /_cMax, -1, 1) * theHeight);
				}
				myStep += _mySettings._cTimeBased ? myData.timeStep : 1;
			}
			g.endShape();
		}
		if(_mySettings._cDrawTurns){
			g.pointSize(_mySettings._cViolationPointSize);
			myStep = 0;
			g.beginShape(CCDrawMode.POINTS);
			for(int i = 1; i < theData.size() - 1;i++){
				double myData0 = value(theData.get(i - 1));
				double myData1 = value(theData.get(i));
				double myData2 = value(theData.get(i + 1));
				
				double myDif0 = myData1 - myData0;
				double myDif1 = myData2 - myData1;
				
				myStep = i * (_mySettings._cTimeBased ? theData.get(i).timeStep : 1);
				
				if(CCMath.sign(myDif0) != CCMath.sign(myDif1)){
					g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),CCMath.constrain((_cAbs ? CCMath.abs(myData1) : myData1) /_cMax, -1, 1) * theHeight);
				}
			}
			g.endShape();
		}
	}
	
	public void drawSpectogram(CCGraphics g, List<Type> theData, double theOffset, double theHeight){
		if(!_cShow)return;

		double myStart = _mySettings._cTimeBased ? _mySettings._cTimeOffset : 0;
		double myEnd = _mySettings._cTimeBased ? _mySettings._cTimeScale + _mySettings._cTimeOffset : CCMath.max(_mySettings._cHistorySize, theData.size());
		
		int myStep = 0;
		g.beginShape(CCDrawMode.QUAD_STRIP);
		for(Type myData:new ArrayList<>(theData)){
			double myFactor = CCMath.saturate(CCMath.abs(value(myData))/_cMax);
			g.color(_cColor.r * myFactor,_cColor.g * myFactor,_cColor.b * myFactor, _mySettings._cAlpha);
			g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theHeight + theOffset);
			g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theOffset);
			myStep += _mySettings._cTimeBased ? myData.timeStep : 1;
		}
		g.endShape();

		if(!_mySettings._cDrawViolations)return;
		myStep = 0;
		g.color(1f,_mySettings._cAlpha);
		g.beginShape(CCDrawMode.LINES);
		for(Type myData:new ArrayList<>(theData)){
			if(CCMath.abs(value(myData))>_cMax){
				g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theHeight + theOffset);
				g.vertex(CCMath.map(myStep, myStart, myEnd, -g.width() / 2, g.width() / 2),theOffset);
			}
			myStep += _mySettings._cTimeBased ? myData.timeStep : 1;
		}
		g.endShape();
	}
}