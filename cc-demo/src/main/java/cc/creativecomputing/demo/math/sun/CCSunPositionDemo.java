package cc.creativecomputing.demo.math.sun;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCSpline;
import cc.creativecomputing.math.time.CCDate;
import cc.creativecomputing.math.util.CCMoonCalc;
import cc.creativecomputing.math.util.CCMoonCalc.CCMoonInfo;
import cc.creativecomputing.math.util.CCSunCalc;
import cc.creativecomputing.math.util.CCSunCalc.CCSunInfo;

public class CCSunPositionDemo extends CCGL2Adapter{
	
	
	
	@Override
	public void start(CCAnimator theAnimator) {
	}
	
	private double RADIUS;
	
	private CCDate _myDate;
	private double _myLatitude = 53;
	private double _myLongitude = 11;
	
	@CCProperty(name = "day", min = 1, max = 365)
	private double day = 1;
	
	@CCProperty(name = "time", min = 0, max = 24)
	private double time = 0;
			
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
		if(g.height() > g.width()) {
			this.RADIUS = (g.width() - 80) / 2;
		}else{
			this.RADIUS = (g.height() - 80) / 2;
		}

		_myDate = new CCDate();
	}
	
	
	
	@Override
	public void update(CCAnimator theAnimator) {
//		_myDate = Calendar.getInstance().getTime();
		_myDate.fromDoubleDay(day);
		_myDate.fromDoubleTime(time);
	}
	
	private CCSunInfo getDayInfo(CCDate date) {
		return CCSunCalc.sunInfo(date, _myLatitude, _myLongitude, false);
	}
	
	private CCVector3 getSunPosPoint(CCDate date) {
		CCVector2 pos = CCSunCalc.sunPosition(date, _myLatitude, _myLongitude);
		double angle = Math.PI / 2 + pos.x;
		return new CCVector3(
			RADIUS * CCMath.cos(angle) * CCMath.cos(pos.y),
			RADIUS * CCMath.sin(angle) * CCMath.cos(pos.y),
			pos.y
		);
	}
	
	private void drawCurvePath(CCGraphics g, CCSunInfo di) {
		CCDate start = di.sunrise.start;
		CCDate end = di.sunset.end;
		
		boolean	belowHorizon = true;
		
		CCLog.info(start + ":" + end);
		
		g.beginShape(CCDrawMode.LINE_STRIP);
		for (long time = start.timeInMilliSeconds(); time < end.timeInMilliSeconds(); time += 10 * 60 * 1000) {
			
			CCVector3 posPoint = getSunPosPoint(new CCDate(time));
//			CCLog.info(new CCDate(time) + ":" + posPoint);
			g.vertex(posPoint.x, posPoint.y);
		}
		g.endShape();
	}
	
	
	
	private void drawSectorPath(CCGraphics g, CCDate date1, CCDate date2) {
		CCVector3 p1 = getSunPosPoint(date1);
		CCVector3 p2 = getSunPosPoint(date2);
		
		g.triangle( p1.x, p1.y, 0, 0, p2.x, p2.y);
	}
	
	private void drawPositionPath(CCGraphics g, CCDate date) {
		CCVector3 posPoint = getSunPosPoint(date);
		g.line(0,0, posPoint.x, posPoint.y);
	}
	
	private void drawYearInfo(CCGraphics g) {
		g.color(1d,0.5d);
		CCSunInfo jun21di = getDayInfo(CCDate.longestDay());
		drawCurvePath(g, jun21di);
			
		g.color(1d,0.5d);
		CCSunInfo dec21di = getDayInfo(CCDate.shortestDay());
		drawCurvePath(g, dec21di);
			
		g.color(1d,0.25d);
//		drawSectorPath(g, jun21di.sunrise.start, dec21di.sunrise.start);
//		drawSectorPath(g, jun21di.sunset.start, dec21di.sunset.start);
	}
	
	
	
	
	
	private void drawCurrentDayInfo(CCGraphics g) {
		CCSunInfo di = getDayInfo(_myDate);
		drawPositionPath(g,di.sunrise.start);
		drawPositionPath(g,di.sunset.end);
		drawCurvePath(g, di);
		
		
	}
	
	private void drawCurrentTimeInfo(CCGraphics g) {
		// Sonnenlinie zeichnen (gelb)
		drawPositionPath(g,_myDate);
		
		// Schatten Linie zeichnen (schwarz)
//		this._schattenDir.attr('path', this._getPosPathSchattenStr(this._date));
		
		// Meine Sonne bewegen
		CCVector3 posPoint = getSunPosPoint(_myDate);
		if (posPoint.z < -0.014) { // Nacht war vorher 0,018
			g.color(CCColor.parseFromString("#111111").alpha(0.2));
		} else {
			g.color(CCColor.parseFromString("#FE9A2E").alpha(0.8));
		}
		g.ellipse(posPoint.x, posPoint.y, 20);
	}
	
	private void draw(CCGraphics g) {
		
		drawYearInfo(g);
		drawCurrentDayInfo(g);
		drawCurrentTimeInfo(g);
		
	}
	
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		g.color(1d,0.25d);
		g.ellipse(0, 0, 0, RADIUS * 2, RADIUS * 2, true);
		draw(g);
	}
	
	public static void main(String[] args) {
		
		
		CCSunPositionDemo demo = new CCSunPositionDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
