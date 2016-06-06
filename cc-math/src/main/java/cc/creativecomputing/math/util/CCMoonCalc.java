package cc.creativecomputing.math.util;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.time.CCDate;

public class CCMoonCalc {
	// http://lexikon.astronomie.info/java/sunmoon/index.html



	// Neu
	static double MAlter;

	// return integer value, closer to 0
	private static int toInt(double x) {
		if (x < 0) {
			return (CCMath.ceil(x));
		} else
			return (CCMath.floor(x));
	}

	private static double mod(double a, double b) {
		return (a - Math.floor(a / b) * b);
	}


	// Modulo PI
	private static double mod2Pi(double x) {
		x = mod(x, 2. * CCMath.PI);
		return (x);
	}

	private static double round1000(double x) {
		return (Math.round(1000. * x) / 1000.);
	}

	private static double round10(double x) {
		return (Math.round(10. * x) / 10.);
	}

	private static String[] signs = new String[] { "Widder", "Stier", "Zwillinge", "Krebs", "Löwe", "Jungfrau",
			"Waage", "SkorCCMath.PIon", "Schütze", "Steinbock", "Wassermann", "Fische" };

	private static String Sign(double lon) {
		return (signs[CCMath.floor(lon * CCMath.RAD_TO_DEG / 30)]);
	}


	// Calculate Julian date: valid only from 1.3.1901 to 28.2.2100
	private static  double CalcJD(int day, int month, int year) {
		// 1.1.1900 - correction of algorithm
		double jd = 2415020.5 - 64;
		if (month <= 2) {
			year--;
			month += 12;
		}
		jd += toInt((year - 1900) * 365.25);
		jd += toInt(30.6001 * (1 + month));
		return (jd + day);
	}

	// Julian Date to Greenwich Mean Sidereal Time
	private static double GMST(double JD) {
		// UT in hours
		double UT = CCMath.frac(JD - 0.5) * 24.;
		// JD at 0 hours UT
		JD = Math.floor(JD - 0.5) + 0.5;
		double T = (JD - 2451545.0) / 36525.0;
		double T0 = 6.697374558 + T * (2400.051336 + T * 0.000025862);
		return (mod(T0 + UT * 1.002737909, 24.));
	}


	// Convert Greenweek mean sidereal time to UT
	private static double GMST2UT(double JD, double gmst) {
		JD = Math.floor(JD - 0.5) + 0.5; // JD at 0 hours UT
		double T = (JD - 2451545.0) / 36525.0;
		double T0 = mod(6.697374558 + T * (2400.051336 + T * 0.000025862), 24.);
		// double UT = 0.9972695663*Mod((gmst-T0), 24.);
		double UT = 0.9972695663 * ((gmst - T0));
		return (UT);
	}


	// Local Mean Sidereal Time, geographical longitude in radians, East is positive
	private static double GMST2LMST(double gmst, double lon) {
		double lmst = mod(gmst + CCMath.RAD_TO_DEG * lon / 15, 24.);
		return (lmst);
	}

	private static class Coor{
		// equatorial coordinates
		double ra;
		double dec;
		
		//ecliptical coordinates
		double lon;
		double lat;
		
		//horizonal coordinates
		//// Azimuth Horizontalwinkel in Bogenmass -180 - 180
		double myaz;
		// Altitude Hoehenwinkel
		double alt;
		
		double radius;
		
		// distance in astronomical units
		double distance;
		
		double distanceTopocentric;
		public double decTopocentric;
		public double raTopocentric;
		
		double x;
		double y;
		double z;
		public double anomalyMean;
		
		// angular diameter in radians
		public double diameter;
		// horizonal parallax
		public double parallax;
		public String sign;
		public double orbitLon;
		public double raGeocentric;
		public double decGeocentric;
		public double moonAge;
		public double phase;
		public String moonPhase;
		
	}

	// Transform ecliptical coordinates (lon/lat) to equatorial coordinates (RA/dec)
	private static Coor Ecl2Equ(Coor coor, double TDT){
		double T = (TDT - 2451545.0) / 36525.; // Epoch 2000 January 1.5
		double eps = (23. + (26 + 21.45 / 60.) / 60. + T * (-46.815 + T * (-0.0006 + T * 0.00181)) / 3600.)
				* CCMath.DEG_TO_RAD;
		double coseps = Math.cos(eps);
		double sineps = Math.sin(eps);

		double sinlon = Math.sin(coor.lon);
		coor.ra = mod2Pi(Math.atan2((sinlon * coseps - Math.tan(coor.lat) * sineps), Math.cos(coor.lon)));
		coor.dec = Math.asin(Math.sin(coor.lat) * coseps + Math.cos(coor.lat) * sineps * sinlon);

		return coor;
	}


	// Transform equatorial coordinates (RA/Dec) to horizonal coordinates (azimuth/altitude)
	// Refraction is ignored
	private static Coor Equ2Altaz(Coor coor, double TDT, double geolat, double lmst) {
		double cosdec = Math.cos(coor.dec);
		double sindec = Math.sin(coor.dec);
		double lha = lmst - coor.ra;
		double coslha = Math.cos(lha);
		double sinlha = Math.sin(lha);
		double coslat = Math.cos(geolat);
		double sinlat = Math.sin(geolat);

		double N = -cosdec * sinlha;
		double D = sindec * coslat - cosdec * coslha * sinlat;
		// coor.az = Mod2Pi( Math.atan2(N, D) ); // Azimuth Horizontalwinkel
		// Original in Bogenmass 0 - 6.283
		// Azimuth Horizontalwinkel in Bogenmass -180 - 180
		coor.myaz = mod2Pi(Math.atan2(N, D)) - 3.142;
		// Altitude Hoehenwinkel
		coor.alt = Math.asin(sindec * sinlat + cosdec * coslha * coslat);

		return coor;
	}


	// Transform geocentric equatorial coordinates (RA/Dec) to topocentric equatorial coordinates
	private static Coor GeoEqu2TopoEqu(Coor coor, Coor observer, double lmst) {
		double cosdec = Math.cos(coor.dec);
		double sindec = Math.sin(coor.dec);
		double coslst = Math.cos(lmst);
		double sinlst = Math.sin(lmst);
		// we should use geocentric latitude, not geodetic latitude
		double coslat = Math.cos(observer.lat);
		double sinlat = Math.sin(observer.lat);
		// observer-geocenter in Kilometer
		double rho = observer.radius;

		double x = coor.distance * cosdec * Math.cos(coor.ra) - rho * coslat * coslst;
		double y = coor.distance * cosdec * Math.sin(coor.ra) - rho * coslat * sinlst;
		double z = coor.distance * sindec - rho * sinlat;

		coor.distanceTopocentric = CCMath.sqrt(x * x + y * y + z * z);
		coor.decTopocentric = Math.asin(z / coor.distanceTopocentric);
		coor.raTopocentric = mod2Pi(Math.atan2(y, x));

		return coor;
	}


	// Calculate cartesian from polar coordinates
	private static Coor EquPolar2Cart(double lon, double lat, double distance) {
		Coor cart = new Coor();
		double rcd = Math.cos(lat) * distance;
		cart.x = rcd * Math.cos(lon);
		cart.y = rcd * Math.sin(lon);
		cart.z = distance * Math.sin(lat);
		return cart;
	}


	// Calculate observers cartesian equatorial coordinates (x,y,z in celestial frame) 
	// from geodetic coordinates (longitude, latitude, height above WGS84 ellipsoid)
	// Currently only used to calculate distance of a body from the observer
	private static Coor Observer2EquCart(double lon,double lat,double height,double gmst )
	{
		// WGS84 flatening of earth
	  double flat = 298.257223563;        
	  double aearth = 6378.137;           // GRS80/WGS84 semi major axis of earth ellipsoid
	  Coor cart = new Coor();
	  // Calculate geocentric latitude from geodetic latitude
	  double co = Math.cos (lat);
	  double si = Math.sin (lat);
	  double fl = 1.0 - 1.0 / flat;
	  fl = fl * fl;
	  si = si * si;
	  double u = 1.0 / CCMath.sqrt (co * co + fl * si);
	  double a = aearth * u + height;
	  double b = aearth * fl * u + height;
	  double radius = CCMath.sqrt (a * a * co * co + b * b * si); // geocentric distance from earth center
	  cart.y = Math.acos (a * co / radius); // geocentric latitude, rad
	  cart.x = lon; // longitude stays the same
	  if (lat < 0.0) { cart.y = -cart.y; } // adjust sign
	  cart = EquPolar2Cart( cart.x, cart.y, radius ); // convert from geocentric polar to geocentric cartesian, with regard to Greenwich
	  // rotate around earth's polar axis to align coordinate system from Greenwich to vernal equinox
	  double x=cart.x; double y=cart.y;
	  double rotangle = gmst/24*2*CCMath.PI; // sideral time gmst given in hours. Convert to radians
	  cart.x = x*Math.cos(rotangle)-y*Math.sin(rotangle);
	  cart.y = x*Math.sin(rotangle)+y*Math.cos(rotangle);
	  cart.radius = radius;
	  cart.lon = lon;
	  cart.lat = lat;
	  return cart;
	}


	// Calculate coordinates for Sun
	// Coordinates are accurate to about 10s (right ascension) 
	// and a few minutes of arc (declination)
	private static Coor SunPosition(double TDT, double geolat, double lmst) {
		double D = TDT - 2447891.5;

		double eg = 279.403303 * CCMath.DEG_TO_RAD;
		double wg = 282.768422 * CCMath.DEG_TO_RAD;
		double e = 0.016713;
		double a = 149598500; // km
		// angular diameter of Moon at a distance
		double diameter0 = 0.533128 * CCMath.DEG_TO_RAD;

		double MSun = 360 * CCMath.DEG_TO_RAD / 365.242191 * D + eg - wg;
		double nu = MSun + 360. * CCMath.DEG_TO_RAD / CCMath.PI * e * Math.sin(MSun);

		Coor sunCoor = new Coor();
		sunCoor.lon = mod2Pi(nu + wg);
		sunCoor.lat = 0;
		sunCoor.anomalyMean = MSun;
		// distance in astronomical units
		sunCoor.distance = (1 - CCMath.sq(e)) / (1 + e * Math.cos(nu));
		// angular diameter in radians
		sunCoor.diameter = diameter0 / sunCoor.distance;
		// distance in km
		sunCoor.distance *= a;
		// horizonal parallax
		sunCoor.parallax = 6378.137 / sunCoor.distance;

		sunCoor = Ecl2Equ(sunCoor, TDT);

		// Calculate horizonal coordinates of sun, if geographic positions is
		// given
		if (geolat != Double.NaN && lmst != Double.NaN) {
			sunCoor = Equ2Altaz(sunCoor, TDT, geolat, lmst);
		}

		sunCoor.sign = Sign(sunCoor.lon);
		return sunCoor;
	}
	
	private static Coor SunPosition(double TDT){
		return SunPosition(TDT, Double.NaN, Double.NaN);
	}
	
	private static String[] phases = new String[]{"Neumond", "Zunehmende Sichel", "Erstes Viertel", "Zunehmender Mond", 
			   "Vollmond", "Abnehmender Mond", "Letztes Viertel", "Abnehmende Sichel", "Neumond"};


	// Calculate data and coordinates for the Moon
	// Coordinates are accurate to about 1/5 degree (in ecliptic coordinates)
	private static Coor MoonPosition(Coor sunCoor,double  TDT, Coor observer,double lmst)
	{
	  double D = TDT-2447891.5;
	  
	  // Mean Moon orbit elements as of 1990.0
	  double l0 = 318.351648*CCMath.DEG_TO_RAD;
	  double P0 =  36.340410*CCMath.DEG_TO_RAD;
	  double N0 = 318.510107*CCMath.DEG_TO_RAD;
	  double i  = 5.145396*CCMath.DEG_TO_RAD;
	  double e  = 0.054900;
	  double a  = 384401; // km
	  double diameter0 = 0.5181*CCMath.DEG_TO_RAD; // angular diameter of Moon at a distance
	  double parallax0 = 0.9507*CCMath.DEG_TO_RAD; // parallax at distance a
	  
	  double l = 13.1763966*CCMath.DEG_TO_RAD*D+l0;
	  double MMoon = l-0.1114041*CCMath.DEG_TO_RAD*D-P0; // Moon's mean anomaly M
	  double N = N0-0.0529539*CCMath.DEG_TO_RAD*D;       // Moon's mean ascending node longitude
	  double C = l-sunCoor.lon;
	  double Ev = 1.2739*CCMath.DEG_TO_RAD*Math.sin(2*C-MMoon);
	  double Ae = 0.1858*CCMath.DEG_TO_RAD*Math.sin(sunCoor.anomalyMean);
	  double A3 = 0.37*CCMath.DEG_TO_RAD*Math.sin(sunCoor.anomalyMean);
	  double MMoon2 = MMoon+Ev-Ae-A3;  // corrected Moon anomaly
	  double Ec = 6.2886*CCMath.DEG_TO_RAD*Math.sin(MMoon2);  // equation of centre
	  double A4 = 0.214*CCMath.DEG_TO_RAD*Math.sin(2*MMoon2);
	  double l2 = l+Ev+Ec-Ae+A4; // corrected Moon's longitude
	  double V = 0.6583*CCMath.DEG_TO_RAD*Math.sin(2*(l2-sunCoor.lon));
	  double l3 = l2+V; // true orbital longitude;

	  double N2 = N-0.16*CCMath.DEG_TO_RAD*Math.sin(sunCoor.anomalyMean);
	  
	  Coor moonCoor = new Coor();  
	  moonCoor.lon = mod2Pi( N2 + Math.atan2( Math.sin(l3-N2)*Math.cos(i), Math.cos(l3-N2) ) );
	  moonCoor.lat = Math.asin( Math.sin(l3-N2)*Math.sin(i) );
	  moonCoor.orbitLon = l3;
	  
	  moonCoor = Ecl2Equ(moonCoor, TDT);
	  // relative distance to semi mayor axis of lunar oribt
	  moonCoor.distance = (1-CCMath.sq(e)) / (1+e*Math.cos(MMoon2+Ec) );
	  moonCoor.diameter = diameter0/moonCoor.distance; // angular diameter in radians
	  moonCoor.parallax = parallax0/moonCoor.distance; // horizontal parallax in radians
	  moonCoor.distance *= a; // distance in km

	  // Calculate horizonal coordinates of sun, if geographic positions is given
	  if (observer!=null && lmst!=Double.NaN) {
	    // transform geocentric coordinates into topocentric (==observer based) coordinates
		moonCoor = GeoEqu2TopoEqu(moonCoor, observer, lmst);
		moonCoor.raGeocentric = moonCoor.ra; // backup geocentric coordinates
		moonCoor.decGeocentric = moonCoor.dec;
		moonCoor.ra=moonCoor.raTopocentric;
		moonCoor.dec=moonCoor.decTopocentric;
	    moonCoor = Equ2Altaz(moonCoor, TDT, observer.lat, lmst); // now ra and dec are topocentric
	  }
	  
	  // Age of Moon in radians since New Moon (0) - Full Moon (CCMath.PI)
	  moonCoor.moonAge = mod2Pi(l3-sunCoor.lon);   
	  moonCoor.phase   = 0.5*(1-Math.cos(moonCoor.moonAge)); // Moon phase, 0-1
	  
	  
	  double mainPhase = 1./29.53*360*CCMath.DEG_TO_RAD; // show 'Newmoon, 'Quarter' for +/-1 day arond the actual event
	  double p = mod(moonCoor.moonAge, 90.*CCMath.DEG_TO_RAD);
	  if (p < mainPhase || p > 90*CCMath.DEG_TO_RAD-mainPhase) p = 2*Math.round(moonCoor.moonAge / (90.*CCMath.DEG_TO_RAD));
	  else p = 2*Math.floor(moonCoor.moonAge / (90.*CCMath.DEG_TO_RAD))+1;
	  moonCoor.moonPhase = phases[(int)p];
	  
	  moonCoor.sign = Sign(moonCoor.lon);

	  return moonCoor;
	}
	
	public static Coor MoonPosition(Coor sunCoor, double TDT){
		return MoonPosition(sunCoor, TDT, null, Double.NaN);
	}


	// Rough refraction formula using standard atmosphere: 1015 mbar and 10°C
	// Input true altitude in radians, Output: increase in altitude in degrees
	private static double Refraction(double alt) {
		double altdeg = alt * CCMath.RAD_TO_DEG;
		if (altdeg < -2 || altdeg >= 90)
			return (0);

		double pressure = 1015;
		double temperature = 10;
		if (altdeg > 15)
			return (0.00452 * pressure / ((273 + temperature) * Math.tan(alt)));

		double y = alt;
		double D = 0.0;
		double P = (pressure - 80.) / 930.;
		double Q = 0.0048 * (temperature - 10.);
		double y0 = y;
		double D0 = D;

		for (int i = 0; i < 3; i++) {
			double N = y + (7.31 / (y + 4.4));
			N = 1. / Math.tan(N * CCMath.DEG_TO_RAD);
			D = N * P / (60. + Q * (N + 39.));
			N = y - y0;
			y0 = D - D0 - N;
			if ((N != 0.) && (y0 != 0.)) {
				N = y - N * (alt + D - y) / y0;
			} else {
				N = alt + D;
			}
			y0 = y;
			D0 = D;
			y = N;
		}
		return D; // Hebung durch Refraktion in radians
	}

	private static class RiseSet{

		public double transit;
		public double rise;
		public double set;
		public double cicilTwilightMorning;
		public double cicilTwilightEvening;
		public double nauticalTwilightMorning;
		public double nauticalTwilightEvening;
		public double astronomicalTwilightMorning;
		public double astronomicalTwilightEvening;
		
		//  with coordinates coor.ra/coor.dec
		// at geographic position lon/lat (all values in radians)
		// Correction for refraction and semi-diameter/parallax of body is taken care of in private double RiseSet
		// 
		
		
		/**
		 * returns Greenwich sidereal time (hours) of time of rise and set of object
		 * @param coor
		 * @param lon
		 * @param lat
		 * @param h h is used to calculate the twilights. It gives the required elevation of the disk center of the sun
		 */
		private RiseSet(Coor coor, double lon, double lat, double h) {
			if(h == Double.NaN){
				h = 0;
			}
			// double tagbogen = Math.acos(-Math.tan(lat)*Math.tan(coor.dec)); //
			// simple formula if twilight is not required
			double tagbogen = Math.acos((Math.sin(h) - Math.sin(lat) * Math.sin(coor.dec))
					/ (Math.cos(lat) * Math.cos(coor.dec)));

			transit = CCMath.RAD_TO_DEG / 15 * (+coor.ra - lon);
			// calculate GMST of rise of object
			rise = 24. + CCMath.RAD_TO_DEG / 15 * (-tagbogen + coor.ra - lon);
			// calculate GMST of set of object
			set = CCMath.RAD_TO_DEG / 15 * (+tagbogen + coor.ra - lon);

			// using the modulo private double Mod, the day number goes missing.
			// This may get a problem for the moon
			transit %= 24;
			rise %= 24;
			set %= 24;
		}
		
		private RiseSet(){
			
		}
		
		@Override
		public String toString() {
			return "RISE SET: transit: " + transit + " rise: " + rise + " set: " + set;
		}
	}

	


	// Find GMST of rise/set of object from the two calculates 
	// (start)points (day 1 and 2) and at midnight UT(0)
	private static double InterpolateGMST(double gmst0, double gmst1, double gmst2, double timefactor) {
		return ((timefactor * 24.07 * gmst1 - gmst0 * (gmst2 - gmst1)) / (timefactor * 24.07 + gmst1 - gmst2));
	}


	// JD is the Julian Date of 0h UTC time (midnight)
	private static RiseSet RiseSet(double jd0UT, Coor coor1, Coor coor2, double lon, double lat, double timeinterval, double altitude) {
		// altitude of sun center: semi-diameter, horizontal parallax and
		// (standard) refraction of 34'
		double alt = 0.; // calculate

		// true height of sun center for sunrise and set calculation. Is kept 0
		// for twilight (ie. altitude given):
		if (altitude != 0)
			alt = 0.5 * coor1.diameter - coor1.parallax + 34. / 60 * CCMath.DEG_TO_RAD;

		RiseSet rise1 = new RiseSet(coor1, lon, lat, altitude);
		RiseSet rise2 = new RiseSet(coor2, lon, lat, altitude);

		// unwrap GMST in case we move across 24h -> 0h
		if (rise1.transit > rise2.transit && Math.abs(rise1.transit - rise2.transit) > 18)
			rise2.transit += 24;
		if (rise1.rise > rise2.rise && Math.abs(rise1.rise - rise2.rise) > 18)
			rise2.rise += 24;
		if (rise1.set > rise2.set && Math.abs(rise1.set - rise2.set) > 18)
			rise2.set += 24;
		double T0 = GMST(jd0UT);
		// double T02 = T0-zone*1.002738; // Greenwich sidereal time at 0h time
		// zone (zone: hours)

		// Greenwich sidereal time for 0h at selected longitude
		double T02 = T0 - lon * CCMath.RAD_TO_DEG / 15 * 1.002738;
		if (T02 < 0)
			T02 += 24;

		if (rise1.transit < T02) {
			rise1.transit += 24;
			rise2.transit += 24;
		}
		if (rise1.rise < T02) {
			rise1.rise += 24;
			rise2.rise += 24;
		}
		if (rise1.set < T02) {
			rise1.set += 24;
			rise2.set += 24;
		}

		// Refraction and Parallax correction
		double decMean = 0.5 * (coor1.dec + coor2.dec);
		double psi = Math.acos(Math.sin(lat) / Math.cos(decMean));
		double y = Math.asin(Math.sin(alt) / Math.sin(psi));
		
		// time correction due to refraction parallax
		double dt = 240 * CCMath.RAD_TO_DEG * y / Math.cos(decMean) / 3600; 

		RiseSet rise = new RiseSet();
		rise.transit = GMST2UT(jd0UT, InterpolateGMST(T0, rise1.transit, rise2.transit, timeinterval));
		rise.rise = GMST2UT(jd0UT, InterpolateGMST(T0, rise1.rise, rise2.rise, timeinterval) - dt);
		rise.set = GMST2UT(jd0UT, InterpolateGMST(T0, rise1.set, rise2.set, timeinterval) + dt);
		
		return rise;
	}
	
	private static RiseSet RiseSet(double jd0UT, Coor coor1, Coor coor2, double lon, double lat, double timeinterval){
		return RiseSet(jd0UT, coor1, coor2, lon, lat, timeinterval, 0);
	}


	// Find (local) time of sunrise and sunset, and twilights
	// JD is the Julian Date of 0h local time (midnight)
	// Accurate to about 1-2 minutes
	// recursive: 1 - calculate rise/set in UTC in a second run
	// recursive: 0 - find rise/set on the current local day. This is set when doing the first call to this private double
	private RiseSet SunRise(double JD, double deltaT,double  lon, double lat, double zone, boolean recursive)
	{
	  double jd0UT = Math.floor(JD-0.5)+0.5;   // JD at 0 hours UT
	  Coor coor1 = SunPosition(jd0UT+  deltaT/24./3600.);
	  Coor coor2 = SunPosition(jd0UT+1.+deltaT/24./3600.); // calculations for next day's UTC midnight
	  
	  RiseSet risetemp = new RiseSet ();
	  RiseSet rise = new RiseSet();
	  // rise/set time in UTC. 
	  rise = RiseSet(jd0UT, coor1, coor2, lon, lat, 1); 
	  if (!recursive) { // check and adjust to have rise/set time on local calendar day
	    if (zone>0) {
	      // rise time was yesterday local time -> calculate rise time for next UTC day
	      if (rise.rise>=24-zone || rise.transit>=24-zone || rise.set>=24-zone) {
	        risetemp = SunRise(JD+1, deltaT, lon, lat, zone, true);
	        if (rise.rise>=24-zone) rise.rise = risetemp.rise;
	        if (rise.transit >=24-zone) rise.transit = risetemp.transit;
	        if (rise.set >=24-zone) rise.set  = risetemp.set;
	      }
	    }
	    else if (zone<0) {
	      // rise time was yesterday local time -> calculate rise time for next UTC day
	      if (rise.rise<-zone || rise.transit<-zone || rise.set<-zone) {
	        risetemp = SunRise(JD-1, deltaT, lon, lat, zone, true);
	        if (rise.rise<-zone) rise.rise = risetemp.rise;
	        if (rise.transit<-zone) rise.transit = risetemp.transit;
	        if (rise.set <-zone) rise.set  = risetemp.set;
	      }
	    }
		
	    rise.transit = mod(rise.transit+zone, 24.);
	    rise.rise    = mod(rise.rise   +zone, 24.);
	    rise.set     = mod(rise.set    +zone, 24.);

		// Twilight calculation
		// civil twilight time in UTC. 
		risetemp = RiseSet(jd0UT, coor1, coor2, lon, lat, 1, -6.*CCMath.DEG_TO_RAD);
		rise.cicilTwilightMorning = mod(risetemp.rise +zone, 24.);
		rise.cicilTwilightEvening = mod(risetemp.set  +zone, 24.);

		// nautical twilight time in UTC. 
		risetemp = RiseSet(jd0UT, coor1, coor2, lon, lat, 1, -12.*CCMath.DEG_TO_RAD);
		rise.nauticalTwilightMorning = mod(risetemp.rise +zone, 24.);
		rise.nauticalTwilightEvening = mod(risetemp.set  +zone, 24.);

		// astronomical twilight time in UTC. 
		risetemp = RiseSet(jd0UT, coor1, coor2, lon, lat, 1, -18.*CCMath.DEG_TO_RAD);
		rise.astronomicalTwilightMorning = mod(risetemp.rise +zone, 24.);
		rise.astronomicalTwilightEvening = mod(risetemp.set  +zone, 24.);
	  }
	  return( rise );  
	}
	
	/**
	 * Find local time of moonrise and moonset
	 * @param JD is the Julian Date of 0h local time (midnight) Accurate to about 5 minutes or better
	 * @param deltaT
	 * @param lon
	 * @param lat
	 * @param zone
	 * @param recursive true calculate rise/set in UTC false find rise/set on the current local day (set could also be first)
	 * @return null for moonrise/set does not occur on selected day
	 */
	private static RiseSet MoonRise(double JD, double deltaT, double lon, double lat, double zone, boolean recursive) {
		double timeinterval = 0.5;

		double jd0UT = Math.floor(JD - 0.5) + 0.5; // JD at 0 hours UT
		Coor suncoor1 = SunPosition(jd0UT + deltaT / 24. / 3600.);
		Coor coor1 = MoonPosition(suncoor1, jd0UT + deltaT / 24. / 3600.);

		// calculations for noon
		Coor suncoor2 = SunPosition(jd0UT + timeinterval + deltaT / 24. / 3600.); 
		
		// calculations for next day's midnight
		Coor coor2 = MoonPosition(suncoor2, jd0UT + timeinterval + deltaT / 24. / 3600.);

		// rise/set time in UTC, time zone corrected later.
		// Taking into account refraction, semi-diameter and parallax
		RiseSet rise = RiseSet(jd0UT, coor1, coor2, lon, lat, timeinterval);
		
		

		// check and adjust to have rise/set time on local calendar day
		if (!recursive) {
			if (zone > 0) {
				// recursive call to MoonRise returns events in UTC
				RiseSet riseprev = MoonRise(JD - 1., deltaT, lon, lat, zone, true);

				// recursive call to MoonRise returns events in UTC
				RiseSet risenext = MoonRise(JD+1, deltaT, lon, lat, zone, true);

//				  CCLog.info("yesterday="+riseprev.transit+"  today="+rise.transit+" tomorrow="+risenext.transit);
//				  CCLog.info("yesterday="+riseprev.rise+"  today="+rise.rise+" tomorrow="+risenext.rise);
//				  CCLog.info("yesterday="+riseprev.set+"  today="+rise.set+" tomorrow="+risenext.set);

				// transit time is tomorrow local time
				if (rise.transit >= 24. - zone || rise.transit < -zone) {
					// there is no moontransit today
					if (riseprev.transit < 24. - zone)
						rise.transit = Double.NaN;
					else
						rise.transit = riseprev.transit;
				}
				// transit time is tomorrow local time
				if (rise.rise >= 24. - zone || rise.rise < -zone) {
					// there is no moontransit today
					if (riseprev.rise < 24. - zone)
						rise.rise = Double.NaN;
					else
						rise.rise = riseprev.rise;
				}
				// transit time is tomorrow local time
				if (rise.set >= 24. - zone || rise.set < -zone) {
					// there is no moontransit today
					if (riseprev.set < 24. - zone)
						rise.set = Double.NaN;
					else
						rise.set = riseprev.set;
				}

			} else if (zone < 0) {
				// rise/set time was tomorrow local time -> calculate rise time
				// for former UTC day
				if (rise.rise < -zone || rise.set < -zone || rise.transit < -zone) {
					RiseSet risetemp = MoonRise(JD + 1., deltaT, lon, lat, zone, true);

					if (rise.rise < -zone) {
						// there is no moonrise today
						if (risetemp.rise > -zone)
							rise.rise = Double.NaN;
						else
							rise.rise = risetemp.rise;
					}

					if (rise.transit < -zone) {
						// there is no moonset today
						if (risetemp.transit > -zone)
							rise.transit = Double.NaN;
						else
							rise.transit = risetemp.transit;
					}

					if (rise.set < -zone) {
						// there is no moonset today
						if (risetemp.set > -zone)
							rise.set = Double.NaN;
						else
							rise.set = risetemp.set;
					}

				}
			}
			// correct for time zone, if time is valid
			if (rise.rise != Double.NaN)
				rise.rise = mod(rise.rise + zone, 24.);
			// correct for time zone, if time is valid
			if (rise.transit != Double.NaN)
				rise.transit = mod(rise.transit + zone, 24.);
			// correct for time zone, if time is valid
			if (rise.set != Double.NaN)
				rise.set = mod(rise.set + zone, 24.);
		}
		return rise;
	}

	// Meins
	
	public static class CCMoonInfo{

		public double altitude;
		public double azimuth;
		public String distance;
		public String phase;
		public double fraction;
		public CCDate rise;
		public CCDate set;
		public CCDate kulmination;
		public double alterday;
		public double altergrad;
		
	}

	public static CCMoonInfo moonInfo (CCDate date, double Lat, double Lng) {

		/*
		if (eval(form.Year.value)<=1900 || eval(form.Year.value)>=2100 ) {
			alert("Dies Script erlaubt nur Berechnungen"+
	        "in der Zeitperiode 1901-2099. Angezeigte Resultat sind ungültig.");
			return;
		}
		*/
		CCDate now = new CCDate();
		double diffTimeZone = -now.timezoneOffset()/60.;
		double DeltaT  = 65; // deltaT - difference among 'earth center' versus 'observered' time (TDT-UT), in seconds

		CCDate myDate0 = date.clone();
		myDate0.hours(0);
		myDate0.minutes(0);
		myDate0.seconds(0);
		
		double JD0 = myDate0.toJulianDate();
		double JD  = date.toJulianDate();
		double TDT = JD+DeltaT/24./3600.;
	  
		double lat      = Lat * CCMath.DEG_TO_RAD; // geodetic latitude of observer on WGS84
		double lon      = Lng * CCMath.DEG_TO_RAD; // latitude of observer
		double height   = 0 * 0.001; // altiude of observer in meters above WGS84 ellipsoid (and converted to kilometers)

		double gmst = GMST(JD);
		double lmst = GMST2LMST(gmst, lon);
	  
		Coor observerCart = Observer2EquCart(lon, lat, height, gmst); // geocentric cartesian coordinates of observer
	 
		Coor sunCoor  = SunPosition(TDT, lat, lmst*15.*CCMath.DEG_TO_RAD);   // Calculate data for the Sun at given time
		Coor moonCoor = MoonPosition(sunCoor, TDT, observerCart, lmst*15.*CCMath.DEG_TO_RAD);    // Calculate data for the Moon at given time
		RiseSet moonRise = MoonRise(JD0, DeltaT, lon, lat, diffTimeZone, false);
	  
	 
		String Distance = round10(moonCoor.distance) +""; // Entfernung Erdmittelpunkt zum Mond in km
		double Fraction = round1000(moonCoor.phase); // Beleuchtung des Mondes 0 - 1, http://de.wikipedia.org/wiki/Mondphase

	  
	  
		CCMoonInfo result = new CCMoonInfo();

		result.altitude = (moonCoor.alt*CCMath.RAD_TO_DEG+Refraction(moonCoor.alt))*CCMath.DEG_TO_RAD;  // including refraction in Bogenmass (Radians);
		result.azimuth = (moonCoor.myaz); // in Bogenmass (Radians);
		result.distance = Distance;
		result.phase = moonCoor.moonPhase; // Name der Mondphase
		result.fraction = Fraction;
		result.rise = date.clone().fromDoubleTime(moonRise.rise);
		result.set = date.clone().fromDoubleTime(moonRise.set);
	  
	  
		result.kulmination = date.clone().fromDoubleTime(moonRise.transit);
		result.altergrad = round1000(moonCoor.moonAge*CCMath.RAD_TO_DEG); // Mondalter in Grad, brauche aber Tage 29,5 Tage ist 360°;
		result.alterday = 29.5 / 360 * result.altergrad;
	    
	  // Neu
	  MAlter = result.altergrad;
	  return result;
	}



	// Zeichne Mondphase
	// http://jsfiddle.net/raphaeljs/RbB4k/
	// http://e-shop.mablog.eu/moon/phases/calendar
	// http://jsfiddle.net/tGmDh/8/
//	private double drawMoon (Object opts) {
//	    opts = opts || {};
//		
//		document.getElementById(opts.el).innerHTML = "";
//
//	    // set defaults (using ternery if/else statements)
//	    opts.r = typeof opts.r === "number" ? opts.r : 100;
//	    opts.phase = typeof opts.phase === "number" ? opts.phase : 0.25;
//	    opts.x = typeof opts.x === "number" ? opts.x : 0;
//	    opts.y = typeof opts.y === "number" ? opts.y : 0;
//
//	    if (opts.el && typeof opts.el === "string") {
//	        double paper = Raphael(opts.el, opts.r * 2, opts.r * 2);
//	    } else if (opts.el && typeof opts.el === "object" && opts.el.canvas) {
//	        double paper = opts.el;
//	    } else {
//	        double paper = Raphael(0, 0, opts.r * 2, opts.r * 2);
//	    }
//		
//		// Zeichne dunklen Mondhintergrund
//		double shadow = paper.circle(opts.r + opts.x, opts.r + opts.y, opts.r).attr({
//	        'stroke-width': 0,
//			'stroke': "#000000", // #111000
//	        fill: '#999', // #hellgrau
//			//opacity: 0.99
//		});
//		
//		// Zeichne beleuchteten Mondsektor
//		double orb = paper.path(shape(opts.phase)).attr({
//	        'stroke-width': 0,
//	        'stroke': "#999", // #00
//	        fill: "#FF9", // #hellgelb
//			//opacity: 0.9
//		}).transform("T" + opts.x + "," + opts.y);
//		
//		// Berechne leuchtenden Mondsektor
//		private double shape(phase) {
//	        double sweep = [];
//			double mag;
//			if (phase <= 0.25) {
//				sweep = [ 1, 0 ];
//				mag = 20 - 20 * phase * 4
//			} else 
//			if (phase <= 0.50) { 
//				sweep = [ 0, 0 ];
//				mag = 20 * (phase - 0.25) * 4
//			} else
//			if (phase <= 0.75) {
//				sweep = [ 1, 1 ];
//				mag = 20 - 20 * (phase - 0.50) * 4
//			} else
//			if (phase <= 1) {
//				sweep = [ 0, 1 ];
//				mag = 20 * (phase - 0.75) * 4
//			} else { 
//				exit; 
//			}
//		
//			double d = "m" + opts.r + ",0 ";
//			d = d + "a" + mag + ",20 0 0," + sweep[0] + " 0," + opts.r*2 + " ";
//			d = d + "a20,20 0 0," + sweep[1] + " 0,-" + opts.r*2;
//
//			return d;
//	    }
//
//		
//		
//		// Gebe das gezeichnete aus
//	    return {
//	        setPhase: private double(new_phase) {
//	            orb.attr("path", shape(new_phase));
//	        }
//	    }
//	}

	
	

}
