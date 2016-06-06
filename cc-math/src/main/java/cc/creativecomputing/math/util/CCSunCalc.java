package cc.creativecomputing.math.util;


import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.time.CCDate;
import cc.creativecomputing.math.time.CCTimeRange;

public class CCSunCalc {
	
	private static int 	J2000 = 2451545;
	
	private static double M0 = 357.5291 * CCMath.DEG_TO_RAD;
	private static double M1 = 0.98560028 * CCMath.DEG_TO_RAD;
	private static double J0 = 0.0009;
	private static double J1 = 0.0053;
	private static double J2 = -0.0069;
	private static double C1 = 1.9148 * CCMath.DEG_TO_RAD;
	private static double C2 = 0.0200 * CCMath.DEG_TO_RAD;
	private static double C3 = 0.0003 * CCMath.DEG_TO_RAD;
	private static double P = 102.9372 * CCMath.DEG_TO_RAD;
	private static double e = 23.45 * CCMath.DEG_TO_RAD;
	private static double th0 = 280.1600 * CCMath.DEG_TO_RAD;
	private static double th1 = 360.9856235 * CCMath.DEG_TO_RAD;
	private static double sunsetAngle = -0.83 * CCMath.DEG_TO_RAD; //sunset angle
	private static double sunDiameter = 0.53 * CCMath.DEG_TO_RAD; //sun diameter
	private static double h1 = -6 * CCMath.DEG_TO_RAD; //nautical twilight angle
	private static double h2 = -12 * CCMath.DEG_TO_RAD; //astronomical twilight angle
	private static double h3 = -18 * CCMath.DEG_TO_RAD; //darkness angle
		
	private static double getJulianCycle(double julianDate, double longitude ) { 
		return CCMath.round(julianDate - J2000 - J0 - longitude/(2 * CCMath.PI)); 
	}
		
	private static double getApproxSolarTransit(double Ht, double longitude, double julianCycle ) { 
		return J2000 + J0 + (Ht + longitude)/(2 * CCMath.PI) + julianCycle; 
	}
		
	private static double getSolarMeanAnomaly(double approxSolarTransit ) { 
		return M0 + M1 * (approxSolarTransit - J2000); 
	}
		
	private static double getEquationOfCenter(double  solarMeanAnomaly ) { 
		return C1 * Math.sin(solarMeanAnomaly) + C2 * Math.sin(2 * solarMeanAnomaly) + C3 * Math.sin(3 * solarMeanAnomaly); 
	}
		
	private static double getEclipticLongitude( double solarMeanAnomaly, double equationOfCenter ) { 
		return solarMeanAnomaly + P + equationOfCenter + Math.PI; 
	}
		
	private static double getSolarTransit( double approxSolarTransit, double solarMeanAnomaly, double eclipticLongitude ) { 
		return approxSolarTransit + (J1 * Math.sin(solarMeanAnomaly)) + (J2 * Math.sin(2 * eclipticLongitude)); 
	}
		
	private static double getSunDeclination( double eclipticLongitude ) { 
		return Math.asin(Math.sin(eclipticLongitude) * Math.sin(e)); 
	}
		
	private static double getRightAscension( double eclipticLongitude ) {
		return Math.atan2(Math.sin(eclipticLongitude) * Math.cos(e), Math.cos(eclipticLongitude));
	}
		
	private static double getSiderealTime( double julianDate, double longitude ) {
		return th0 + th1 * (julianDate - J2000) - longitude;
	}
		
	private static double getAzimuth( double siderealTime, double rightAscension, double latitude, double sunDeclination ) {
		double H = siderealTime - rightAscension;
		return Math.atan2(Math.sin(H), Math.cos(H) * Math.sin(latitude) - Math.tan(sunDeclination) * Math.cos(latitude));
	}
		
	private static double getAltitude( double siderealTime, double rightAscension, double latitude, double sunDeclination ) {
		double H = siderealTime - rightAscension;
		return Math.asin(Math.sin(latitude) * Math.sin(sunDeclination) + Math.cos(latitude) * Math.cos(sunDeclination) * Math.cos(H));
	}
		
	private static double getHourAngle( double h, double latitude, double sunDeclination ) { 
			return CCMath.acos((Math.sin(h) - CCMath.sin(latitude) * CCMath.sin(sunDeclination)) / (CCMath.cos(latitude) * CCMath.cos(sunDeclination))); 
	}
		
	private static double getSunsetJulianDate( double w0, double solarMeanAnomaly, double eclipticLongitude, double longitude, double julianCycle ) { 
		return getSolarTransit(getApproxSolarTransit(w0, longitude, julianCycle), solarMeanAnomaly, eclipticLongitude); 
	}
		
	private static double getSunriseJulianDate( double solarTransit, double julianSunset ) { 
		return solarTransit - (julianSunset - solarTransit); 
	}
		
	private static CCVector2 getSunPosition(double julianDate, double longitude, double latitude ) {
		double solarMeanAnomaly = getSolarMeanAnomaly(julianDate);
		double equationOfCenter = getEquationOfCenter(solarMeanAnomaly);
		double eclipticLongitude = getEclipticLongitude(solarMeanAnomaly, equationOfCenter);
		double sunDeclination = getSunDeclination(eclipticLongitude);
		double rightAscension = getRightAscension(eclipticLongitude);
		double siderealTime = getSiderealTime(julianDate, longitude);
				
		return new CCVector2(
			getAzimuth( siderealTime, rightAscension, latitude, sunDeclination ),
			getAltitude( siderealTime, rightAscension, latitude, sunDeclination )
		);
	}
	
	public static CCVector2 sunPosition(CCDate date, double lat, double lng ) {
		return getSunPosition(date.toJulianDate(), -lng * CCMath.DEG_TO_RAD, lat * CCMath.DEG_TO_RAD );
	}
	
	public static CCVector3 sunPosition3D(CCDate date, double lat, double lng){
		CCVector2 pos = CCSunCalc.sunPosition(date, lat, lng);
		double angle = Math.PI / 2 + pos.x;
		return new CCVector3(
			CCMath.cos(angle) * CCMath.cos(pos.y),
			CCMath.sin(angle) * CCMath.cos(pos.y),
			CCMath.sin(pos.y)
		);
	}
	
	public static class CCSunInfo{
		
		public CCDate dawn;
		public CCTimeRange sunrise;
		public CCDate transit;
		public CCTimeRange sunset;
		public CCDate dusk;
		
	}
	
	public static class CCTwilight{
		public CCTimeRange astronomical;
		public CCTimeRange nautical;
		public CCTimeRange civil;
	}
	
	public static class CCSunDetailedInfo extends CCSunInfo{
		CCTwilight morningTwilight = new CCTwilight();
		CCTwilight nightTwilight = new CCTwilight();
	}
	
	public static CCSunInfo sunInfo(CCDate date, double lat, double lng, boolean detailed ) {
		double longitude = -lng * CCMath.DEG_TO_RAD;
		double latitude = lat * CCMath.DEG_TO_RAD;
		double julianDate = date.toJulianDate();
					
		double myJulianCycle = getJulianCycle(julianDate, longitude);
		double approxSolarTransit = getApproxSolarTransit(0, longitude, myJulianCycle);
		double solarMeanAnomaly = getSolarMeanAnomaly(approxSolarTransit);
		double equationOfCenter = getEquationOfCenter(solarMeanAnomaly);
		double eclipticLongitude = getEclipticLongitude(solarMeanAnomaly, equationOfCenter);
		double sunDeclination = getSunDeclination(eclipticLongitude);
		double solarTransit = getSolarTransit(approxSolarTransit, solarMeanAnomaly, eclipticLongitude);
		double w0 = getHourAngle(sunsetAngle, latitude, sunDeclination);
		double w1 = getHourAngle(sunsetAngle + sunDiameter, latitude, sunDeclination);
		double julianSunset = getSunsetJulianDate(w0, solarMeanAnomaly, eclipticLongitude, longitude, myJulianCycle);
		double julianSunsetStart = getSunsetJulianDate(w1, solarMeanAnomaly, eclipticLongitude, longitude, myJulianCycle);
		double julianSunrise = getSunriseJulianDate(solarTransit, julianSunset);
		double julianSunriseEnd = getSunriseJulianDate(solarTransit, julianSunsetStart);
		double w2 = getHourAngle(h1, latitude, sunDeclination);
		double julianDusk = getSunsetJulianDate(w2, solarMeanAnomaly, eclipticLongitude, longitude, myJulianCycle);
		double julianDawn = getSunriseJulianDate(solarTransit, julianDusk);
		
		CCSunDetailedInfo myResult = new CCSunDetailedInfo();
		myResult.dawn = CCDate.createFromJulianDate(julianDawn);
		myResult.sunrise = new CCTimeRange(
			CCDate.createFromJulianDate(julianSunrise),
			CCDate.createFromJulianDate(julianSunriseEnd)
		);
		myResult.transit = CCDate.createFromJulianDate(solarTransit);
		myResult.sunset = new CCTimeRange(
			CCDate.createFromJulianDate(julianSunsetStart),
			CCDate.createFromJulianDate(julianSunset)
		);
		myResult.dusk = CCDate.createFromJulianDate(julianDusk);
		
		if(!detailed)return myResult;
		
		double w3 = getHourAngle(h2, latitude, sunDeclination);
		double w4 = getHourAngle(h3, latitude, sunDeclination);
		double Jastro = getSunsetJulianDate(w3, solarMeanAnomaly, eclipticLongitude, longitude, myJulianCycle);
		double Jdark = getSunsetJulianDate(w4, solarMeanAnomaly, eclipticLongitude, longitude, myJulianCycle);
		double Jnau2 = getSunriseJulianDate(solarTransit, Jastro);
		double Jastro2 = getSunriseJulianDate(solarTransit, Jdark);
					
		myResult.morningTwilight.astronomical = new CCTimeRange(
			CCDate.createFromJulianDate(Jastro2),
			CCDate.createFromJulianDate(Jnau2)
		);
		myResult.morningTwilight.nautical = new CCTimeRange(
			CCDate.createFromJulianDate(Jnau2),
			CCDate.createFromJulianDate(julianDawn)
		);
		myResult.morningTwilight.civil = new CCTimeRange(
			CCDate.createFromJulianDate(julianDawn),
			CCDate.createFromJulianDate(julianSunrise)
		);
		myResult.nightTwilight.civil = new CCTimeRange(
			CCDate.createFromJulianDate(julianSunset),
			CCDate.createFromJulianDate(julianDusk)
		);
		myResult.nightTwilight.nautical = new CCTimeRange(
			CCDate.createFromJulianDate(julianDusk),
			CCDate.createFromJulianDate(Jastro)
		);
		myResult.nightTwilight.astronomical = new CCTimeRange(
			CCDate.createFromJulianDate(Jastro),
			CCDate.createFromJulianDate(Jdark)
		);
				
		return myResult;		
	}
	
	public static double lightBlend(CCDate theDate, double theLatitude, double theLongitude){
		CCSunInfo mySunInfo = CCSunCalc.sunInfo(theDate, theLatitude, theLongitude, false);
		double mySunRiseStart = mySunInfo.dawn.dayProgress();
		double mySunRiseEnd = mySunInfo.sunrise.end.dayProgress();
		double mySunSetStart = mySunInfo.sunset.start.dayProgress();
		double mySunSetEnd = mySunInfo.dusk.dayProgress();
		
		double myDay = theDate.dayProgress();
		if(myDay < mySunRiseStart)return 0;
		if(myDay < mySunRiseEnd)return CCMath.norm(myDay, mySunRiseStart, mySunRiseEnd);
		if(myDay < mySunSetStart)return 1;
		if(myDay < mySunSetEnd)return CCMath.norm(myDay, mySunSetEnd, mySunSetStart);
		return 0;
		
	}
}
