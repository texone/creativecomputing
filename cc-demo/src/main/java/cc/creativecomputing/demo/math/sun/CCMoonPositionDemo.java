package cc.creativecomputing.demo.math.sun;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.demo.math.moon.CCMoon;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.time.CCDate;

public class CCMoonPositionDemo extends CCGL2Adapter{
	
	
	
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
	
	private CCVector3 getMoonPosPoint(CCDate date) { // NEU MOND
		return new CCMoon(date, _myLatitude, _myLongitude).position(RADIUS);
	}
	
	private void drawPosPathMoon(CCGraphics g, CCDate date) { // NEU MOND
		CCVector3 posPoint = getMoonPosPoint(date);
		//if (posPoint.altitude < -0.018) { return ''; } // LÃ¶sste den Fehler aus !!!!!!!
		g.line(0, 0, posPoint.x, posPoint.y);
	}
	
	// Linie Mondbahn berechnen
	
	private int CURVE_TIME_INTERVAL = 1000*60*20;
		
		private void drawCurvePathMoon(CCGraphics g, CCDate date) {
			CCDate mydate = new CCDate(date.year(), date.month(), date.day());

			g.beginShape(CCDrawMode.LINE_LOOP);
			for (double x = 0; x < 72; x++) {

				CCVector3 posPoint = getMoonPosPoint(mydate);
				mydate.timeInMilliSeconds(mydate.timeInMilliSeconds() + this.CURVE_TIME_INTERVAL);  // 20 min weiter
				g.vertex(posPoint.x, posPoint.y);
			}
			g.endShape();
		}
	
	private void drawCurrentDayInfo(CCGraphics g) {
		CCMoon pos = new CCMoon(_myDate, _myLatitude, _myLongitude);

		drawPosPathMoon(g, pos.aufgang); // Mondaufgang
		drawPosPathMoon(g, pos.untergang);// Monduntergang
		drawCurvePathMoon(g, _myDate); // Mondbahn
		
//		// Mondschein-Sector berrechnen
//		var sunriseSectorPath = this._getSectorPathStr(this._date);
//		// Mondschein-Sector1 zeichnen
//		this._sunriseSector1.attr('path', sunriseSectorPath.sector1); 
//		this._sunriseSector2.attr('path', sunriseSectorPath.sector2); 
	}
	
	private void drawPosPathStrMoonStrahl(CCGraphics g, CCDate date) { // NEU MOND
		CCVector3 posPoint = getMoonPosPoint(date);
//		if (posPoint.z < -0.010) { return; } 
		g.line(0,0,posPoint.x, posPoint.y);
	}
	
	
	
	private void drawCurrentTimeInfo(CCGraphics g) {
		// Mondlinie zeichnen (gelb)
		drawPosPathStrMoonStrahl(g, _myDate);
		
		// Schatten Linie zeichnen (schwarz)
//		this._schattenDir.attr('path', this._getPosPathSchattenStr(this._date));
//		
//		// Meine Sonne bewegen
//		var di = this._getDayInfo(this._date);
//		var posPoint = this._getMoonPosPoint(this._date);
//		if (posPoint.altitude < -0.010) { // Mond unterm Horizont
//			this._sonne.title = 'Position des Mondes - Unter dem Horizont, nicht sichtbar';
//			this._sonne.style.background = '#111111';
//			this._sonne.style.borderColor = '#000000'; // Schwarz
//			this._sonne.style.opacity = '0.2';
//			this._sonne.filter = 'alpha(opacity=20)';
//		} else if (posPoint.altitude >= -0.010 && this._date > di.sunrise.start && this._date < di.sunset.start) {
//			this._sonne.title = 'Position des Mondes - \u00dcber dem Horizont, nicht sichtbar weil Tageslicht vorhanden ist';
//			this._sonne.style.background = '#F3F781'; // Mond Ã¼berm Horizont und Tag Weissgelb
//			this._sonne.style.borderColor = '#FAFAFA'; // weiss
//			this._sonne.style.opacity = '0.8';
//			this._sonne.filter = 'alpha(opacity=80)';
//		} else {
//			this._sonne.title = 'Position des Mondes - \u00dcber dem Horizont, sichtbar weil es Nacht ist';
//			this._sonne.style.background = '#F3F781'; // Mond Ã¼berm Horizont und Nacht Weissgelb
//			this._sonne.style.borderColor = '#000000'; // schwarz
//			this._sonne.style.opacity = '0.9';
//			this._sonne.filter = 'alpha(opacity=80)';
//		}
//		var ContainerLeft = parseFloat(this._container.style.left); // to Number
//		var ContainerTop = parseFloat(this._container.style.top); // to Number
//		this._sonne.style.left = (posPoint.x + ContainerLeft) + 'px';
//		this._sonne.style.top = (posPoint.y + ContainerTop) + 'px';
	}
	
	private void draw(CCGraphics g) {
		
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
		
		
		CCMoonPositionDemo demo = new CCMoonPositionDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.AUTOMAX;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
