package cc.creativecomputing.demo.kle.roche;

import java.util.ArrayList;
import java.util.List;

import com.sun.org.omg.SendingContext._CodeBaseImplBase;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.filter.CCChebFilter;
import cc.creativecomputing.math.spline.CCBezierSpline;
import cc.creativecomputing.math.spline.CCLinearSpline;

public class CCRocheData {
	private List<CCVector2> _myPositions = new ArrayList<>();
	private List<CCVector2> _myInterpolatedPositions = new ArrayList<>();
	private List<CCVector2> _myFilteredPositions = new ArrayList<>();
	
	private List<CCVector2> _myVelocites = new ArrayList<>();
	private List<CCVector2> _myAccelrations = new ArrayList<>();
	private List<CCVector2> _myJerks = new ArrayList<>();
	private List<CCVector2> _myDifferences = new ArrayList<>();
	
	@CCProperty(name = "spline")
	private CCBezierSpline _cSpline = new CCBezierSpline();
	
	@CCProperty(name = "use spline")
	private boolean _cUseSpline = false;
	
	@CCProperty(name = "filter")
	private CCChebFilter _cFilter = new CCChebFilter();
	
	@CCProperty(name = "origin color")
	private CCColor _cOriginalPointColor = CCColor.WHITE.clone();

	@CCProperty(name = "filter color")
	private CCColor _cFilterPointColor = CCColor.YELLOW.clone();
	
	@CCProperty(name = "position draw mode")
	private CCDrawMode _cPositionDrawMode = CCDrawMode.LINE_STRIP;
	
	@CCProperty(name = "velocity scale", min = 0, max = 10)
	private double _cVelocityScale = 1.5;
	@CCProperty(name = "acceleration scale", min = 0, max = 10)
	private double _cAccelerationScale = 5;
	@CCProperty(name = "jerk scale", min = 0, max = 10)
	private double _cJerkScale = 2.5;
	@CCProperty(name = "difference scale", min = 0, max = 10)
	private double _cDifferenceScale = 2.5;
	@CCProperty(name = "y scale", min = 0, max = 10)
	private double _cYscale = 2.5;
	
	private static interface CCValueFromVector {
		public double value(CCVector2 theVector);
	}
	
	private static enum CCDrawCurveMode{
		X(theVec -> theVec.x),
		Y(theVec -> theVec.y),
		MAG(theVec -> theVec.length());
		
		final CCValueFromVector func;
		
		private CCDrawCurveMode(CCValueFromVector theFunc) {
			func = theFunc;
		}
	}
	
	@CCProperty(name = "draw curve mode")
	private CCDrawCurveMode _cDrawMode = CCDrawCurveMode.MAG;
	@CCProperty(name = "curve alpha", min = 0, max = 1)
	private double _cCurveAlpha = 1;
	
	@CCProperty(name = "repeat first last")
	private int _cRepeatFirstLastValue = 10;
	@CCProperty(name = "reinterpolate")
	private boolean _cReinterpolate = true;
	
	@CCProperty(name = "start", min = 0, max = 1)
	private double _cStart = 0;

	@CCProperty(name = "end", min = 0, max = 1)
	private double _cEnd = 1;
	
	@CCProperty(name = "position", min = 0, max = 1)
	private double _cPosition = 0;
	

	private double _myPlayPosition = 0;
	private int _myStart = 0;
	private int _myEnd = 0;
	
	private List<Integer> _myKeys = new ArrayList<>();
	
	public CCRocheData() {
		_cFilter.channels(2);
		loadKeys();
	}
	
	private CCVector2 _myLastPosition = new CCVector2();
	private CCVector2 _myLastVelocity = new CCVector2();
	private CCVector2 _myLastAcceleration = new CCVector2();
	
	private void addPoint(double myX, double myY) {
		CCVector2 myPosition = new CCVector2(myX, myY);
		CCVector2 myFilteredPosition  = new CCVector2(
			_cFilter.process(0, myX, 0.2),
			_cFilter.process(1, myY, 0.2)
		);
		CCVector2 myDifference = myPosition.subtract(myFilteredPosition);
		CCVector2 myVelocity = myFilteredPosition.subtract(_myLastPosition).multiplyLocal(5);
		CCVector2 myAcceleration = myVelocity.subtract(_myLastVelocity).multiplyLocal(5);
		CCVector2 myJerk = myAcceleration.subtract(_myLastAcceleration).multiplyLocal(5);
		
		_myFilteredPositions.add(myFilteredPosition);
		_myDifferences.add(myDifference);
		_myVelocites.add(myVelocity);
		_myAccelrations.add(myAcceleration);
		_myJerks.add(myJerk);
		
		_myLastPosition = myFilteredPosition;
		_myLastVelocity = myVelocity;
		_myLastAcceleration = myAcceleration;
	}
	
	private void process(String myLine) {
		String[] myPosition = myLine.split(" ");
		addPoint(
			Double.parseDouble(myPosition[1]),
			Double.parseDouble(myPosition[2])
		);
	}
	
	private void createDataFromSpline() {
		int i = 0;
		int count = 300;
		CCVector3 myLastPos = null;
		for(int myLineIndex = 0; myLineIndex <= count; myLineIndex++){
			CCVector3 myPos = _cSpline.interpolate(CCMath.norm(myLineIndex, 0, count));
			myPos.x *= 2000;
			myPos.x -= 1000;
			myPos.y *= 2000;
			//myPos.y -= 1000;
			if(i == 0) {
				_myLastPosition = new CCVector2(myPos.x,  myPos.y);
			}
			for(;i< _cRepeatFirstLastValue;i++) {
				addPoint(myPos.x,myPos.y);
			}
			addPoint(myPos.x,myPos.y);
			myLastPos = myPos;
		}
		for(i = 0;i< _cRepeatFirstLastValue;i++) {
			addPoint(myLastPos.x, myLastPos.y);
		}
		_myKeys = new ArrayList<>();
	}
	
	private void loadKeys() {
		_myKeys = new ArrayList<>();
		int myLastKey = 0;
		for(String myLine:CCNIOUtil.loadStrings(CCNIOUtil.dataPath("kle/180913_roche_choreoV6_kurven_liste.txt"))){
			String [] values = myLine.split(",");
			String myName = values[0].trim();
			int myKeyFrame = Integer.parseInt(values[1].substring(8, values[1].length()).trim());
			int myBrake = Integer.parseInt(values[2].trim().substring(7, values[2].length()-1));
			if(myBrake != 0) {
				_myKeys.add(myLastKey + myBrake);
				CCLog.info(myBrake);
			}
			_myKeys.add(myKeyFrame);
			myLastKey = myKeyFrame;
		}
	}
	
	private void readPointsFromFile() {
		
		String myLastLine = "";
		List<String> myLines = CCNIOUtil.loadStrings(CCNIOUtil.dataPath("kle/e000_180913_roche_choreoV6.txt"));
		
		
		List<CCVector2> myPoints = new ArrayList<>();
		
		for(String myLine:myLines) {
			String[] myPosition = myLine.split(" ");
			_myPositions.add(
				new CCVector2(
					Double.parseDouble(myPosition[1]),
					Double.parseDouble(myPosition[2])
				)
			);
		}
	}
	
	public void insertStartAndEnd() {
		if(_cRepeatFirstLastValue <= 0)return;
		
		CCVector2 myFirst = _myPositions.get(0);
		CCVector2 myLast = _myPositions.get(_myPositions.size() - 1);
		
		for(int i = 0; i < _cRepeatFirstLastValue;i++) {
			_myPositions.add(0, myFirst.clone());
			_myPositions.add(myLast.clone());
		}
		
		for(int i = 0; i < _myKeys.size();i++) {
			_myKeys.set(i, _myKeys.get(i) + _cRepeatFirstLastValue);
		}
		
		_myKeys.add(0,0);
		_myKeys.add(_myPositions.size());
	}
	
	public void reinterpolate() {
		if(!_cReinterpolate) {
			_myInterpolatedPositions.addAll(_myPositions);
			return;
		}
		int myLastKey = -1;
		
		for(int myKey:_myKeys) {
			if(myLastKey < 0) {
				myLastKey = myKey;
				continue;
			}
			
			int myKeys = myKey - myLastKey;

			CCLinearSpline mySpline = new CCLinearSpline();
			for(int i = myLastKey; i < myKey;i++) {
				CCVector2 myPosition = _myPositions.get(i);
				mySpline.addPoint(new CCVector3(myPosition));
				
				
			}
			mySpline.endEditSpline();
			
			for(int i = 0; i < myKeys;i++) {
				double myBlend = i /(double)(myKeys - 1);
				myBlend = CCMath.smoothStep(0, 1, myBlend);
				CCVector3 myPoint = mySpline.interpolate(myBlend);
				_myInterpolatedPositions.add(new CCVector2(myPoint.x,myPoint.y));
			}
			
			myLastKey = myKey;
		}
	}
	
	public void filter() {
		int i = 0;
		for(CCVector2 myPoint:_myInterpolatedPositions){
			if(i == 0) {
				_myLastPosition = myPoint.clone();
			}
			addPoint(myPoint.x, myPoint.y);
			i++;
		}
	}
	
	public void updateData() {
		_myPositions.clear();
		_myInterpolatedPositions.clear();
		_myFilteredPositions.clear();
		_myDifferences.clear();
		_myVelocites.clear();
		_myAccelrations.clear();
		_myJerks.clear();
		
		_myLastPosition = new CCVector2();
		_myLastVelocity = new CCVector2();
		_myLastAcceleration = new CCVector2();
		
		if(_cUseSpline) {
			createDataFromSpline();
		}else {
			loadKeys();
			readPointsFromFile();
			insertStartAndEnd();
			reinterpolate();
			filter();
		}
	}
	
	public void update(CCAnimator theAnimator) {
		updateData() ;

		int myStart = (int)(_cStart * _myPositions.size());
		int myEnd = (int)(_cEnd * _myPositions.size());
		_myPlayPosition = CCMath.blend(0, _myPositions.size(), _cPosition);
	}
	
	private void drawPoints(CCGraphics g, List<CCVector2> thePoints, CCColor theColor) {
		g.color(theColor);
		g.beginShape(_cPositionDrawMode);
		for(CCVector2 myPoint:thePoints) {
			g.vertex(myPoint);
		}
		g.endShape();
	}
	
	private void drawKeys(CCGraphics g, CCColor theColor) {
		g.color(theColor);
		g.pushAttribute();
		g.pointSize(5);
		g.beginShape(CCDrawMode.POINTS);
		for(int myKey:_myKeys) {
			myKey+= _cRepeatFirstLastValue;
			if(myKey > 0 && myKey < _myPositions.size())g.vertex(_myPositions.get(myKey));
		}
		g.endShape();
		g.popAttribute();
	}
	
	private void drawGraph(CCGraphics g, CCColor theColor, List<CCVector2> theData, CCValueFromVector theFunc, double theScale) {
		g.color(theColor);
		int i = 0;
		g.beginShape(CCDrawMode.LINE_STRIP);
		for(CCVector2 myPoint:theData) {
			g.vertex(CCMath.map(i, 0, theData.size() - 1, 0, g.width()) - g.width()/2, theFunc.value(myPoint) * theScale);
			i++;
		}
		g.endShape();
	}
	
	private void drawInfo(CCGraphics g) {
//		g.color(CCColor.RED);
//		CCVector2 myPosition = _myPositions.get((int)_myPlayPosition);
//		g.ellipse(myPosition, 100);
//		
//		g.color(CCColor.WHITE);
//		double myX = CCMath.map(_myPlayPosition, 0, _myPositions.size() - 1, 0, g.width()) - g.width()/2;
//		g.line(myX, -200, myX, 200);
		
		g.color(0, 120);
		g.pushAttribute();
		g.pointSize(5);
		g.beginShape(CCDrawMode.LINES);
		for(int myKey:_myKeys) {
			myKey+= _cRepeatFirstLastValue;
			double myX = CCMath.map(myKey, 0, _myPositions.size(), - g.width()/2, g.width()/2);
			g.vertex(myX, -100);
			g.vertex(myX,  100);
//			if(myKey > 0 && myKey < _myPositions.size())g.vertex(_myPositions.get(myKey));
		}
		g.endShape();
		g.popAttribute();
	}
	
	public void draw(CCGraphics g) {
		drawPoints(g, new ArrayList<>(_myFilteredPositions), _cFilterPointColor);
		drawKeys(g, CCColor.RED);
		drawPoints(g, _myPositions, _cOriginalPointColor);
		drawInfo(g);
		
		drawGraph(g, new CCColor(CCColor.RED, _cCurveAlpha), _myVelocites, _cDrawMode.func, _cVelocityScale);
		drawGraph(g, new CCColor(CCColor.GREEN, _cCurveAlpha), _myAccelrations, _cDrawMode.func,  _cAccelerationScale);
		drawGraph(g, new CCColor(CCColor.BLUE, _cCurveAlpha), _myJerks, _cDrawMode.func, _cJerkScale);
		drawGraph(g, new CCColor(CCColor.MAGENTA, _cCurveAlpha), _myDifferences, _cDrawMode.func, _cDifferenceScale);
		
	}
	
	@CCProperty(name = "export")
	public void export() {
		int i = 0;
		StringBuffer myData = new StringBuffer();
		for(CCVector2 myPosition:_myFilteredPositions) {
			myData.append(i);
			myData.append(" ");
			myData.append(CCFormatUtil.nd(myPosition.x, 10));
			myData.append(" ");
			myData.append(CCFormatUtil.nd(myPosition.y, 10));
			myData.append("\n");
			i++;
		}
		CCNIOUtil.saveString(CCNIOUtil.dataPath("kle/e000_180913_roche_choreoV6_filter025.txt"), myData.toString());
	}
}
