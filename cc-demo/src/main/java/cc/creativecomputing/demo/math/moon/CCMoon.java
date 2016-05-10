package cc.creativecomputing.demo.math.moon;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.time.CCDate;

public class CCMoon {
	// http://lexikon.astronomie.info/java/sunmoon/index.html

	// return integer value, closer to 0
	private static int Int(double x) {
		if (x < 0) {
			return (CCMath.ceil(x));
		} else
			return (CCMath.floor(x));
	}

	private static double frac(double x) {
		return (x - CCMath.floor(x));
	}

	private static double Mod(double a, double b) {
		return (a - CCMath.floor(a / b) * b);
	}

	// Modulo PI
	private static double Mod2Pi(double x) {
		x = Mod(x, 2. * CCMath.PI);
		return (x);
	}

	static String[] signs = new String[] { "Widder", "Stier", "Zwillinge", "Krebs", "Löwe", "Jungfrau", "Waage",
			"Skorpion", "Schütze", "Steinbock", "Wassermann", "Fische" };

	private static String Sign(double lon) {

		return (signs[CCMath.floor(lon * CCMath.RAD_TO_DEG / 30)]);
	}

	// Calculate Julian date: valid only from 1.3.1901 to 28.2.2100
	private static double CalcJD(int day, int month, int year) {
		// 1.1.1900 - correction of algorithm
		double jd = 2415020.5 - 64;
		if (month <= 2) {
			year--;
			month += 12;
		}
		jd += Int((year - 1900) * 365.25);
		jd += Int(30.6001 * (1 + month));
		return (jd + day);
	}

	// Julian Date to Greenwich Mean Sidereal Time
	private static double GMST(double JD) {
		double UT = frac(JD - 0.5) * 24.; // UT in hours
		JD = CCMath.floor(JD - 0.5) + 0.5; // JD at 0 hours UT
		double T = (JD - 2451545.0) / 36525.0;
		double T0 = 6.697374558 + T * (2400.051336 + T * 0.000025862);
		return (Mod(T0 + UT * 1.002737909, 24.));
	}

	// Convert Greenweek mean sidereal time to UT
	private static double GMST2UT(double JD, double gmst) {
		JD = CCMath.floor(JD - 0.5) + 0.5; // JD at 0 hours UT
		double T = (JD - 2451545.0) / 36525.0;
		double T0 = Mod(6.697374558 + T * (2400.051336 + T * 0.000025862), 24.);
		// var UT = 0.9972695663*Mod((gmst-T0), 24.);
		double UT = 0.9972695663 * ((gmst - T0));
		return (UT);
	}

	// Local Mean Sidereal Time, geographical longitude in radians, East is
	// positive
	private static double GMST2LMST(double gmst, double lon) {
		double lmst = Mod(gmst + CCMath.RAD_TO_DEG * lon / 15, 24.);
		return (lmst);
	}

	private static class Coor {

		public double lon;
		public double ra;
		public double lat;
		public double dec;
		public double myaz;
		public double alt;
		public double radius;
		public double distance;
		public double distanceTopocentric;
		public double decTopocentric;
		public double raTopocentric;
		public double x;
		public double y;
		public double z;
		public double anomalyMean;
		public double diameter;
		public double parallax;
		public String sign;
		public double orbitLon;
		public double raGeocentric;
		public double decGeocentric;
		// Age of Moon in radians since New Moon (0) - Full Moon (pi)
		public double moonAge;

		// Moon phase, 0-1
		public double phase;
		public String moonPhase;
		
		/**
		 * Transform ecliptical coordinates (lon/lat) to equatorial coordinates (RA/dec)
		 * @param TDT
		 */
		private void Ecl2Equ(double TDT) {
			double T = (TDT - 2451545.0) / 36525.; // Epoch 2000 January 1.5
			double eps = (23. + (26 + 21.45 / 60.) / 60. + T * (-46.815 + T * (-0.0006 + T * 0.00181)) / 3600.)
					* CCMath.DEG_TO_RAD;
			double coseps = CCMath.cos(eps);
			double sineps = CCMath.sin(eps);

			double sinlon = CCMath.sin(lon);
			ra = Mod2Pi(CCMath.atan2((sinlon * coseps - CCMath.tan(lat) * sineps), CCMath.cos(lon)));
			dec = CCMath.asin(CCMath.sin(lat) * coseps + CCMath.cos(lat) * sineps * sinlon);
		}
		
		/**
		 * Transform equatorial coordinates (RA/Dec) to horizonal coordinates (azimuth/altitude) Refraction is ignored
		 * @param TDT
		 * @param geolat
		 * @param lmst
		 */
		private void Equ2Altaz(double TDT, double geolat, double lmst) {
			double cosdec = CCMath.cos(dec);
			double sindec = CCMath.sin(dec);
			double lha = lmst - ra;
			double coslha = CCMath.cos(lha);
			double sinlha = CCMath.sin(lha);
			double coslat = CCMath.cos(geolat);
			double sinlat = CCMath.sin(geolat);

			double N = -cosdec * sinlha;
			double D = sindec * coslat - cosdec * coslha * sinlat;
			// az = Mod2Pi( CCMath.atan2(N, D) ); // Azimuth Horizontalwinkel
			// Original in Bogenmass 0 - 6.283
			myaz = Mod2Pi(CCMath.atan2(N, D)) - 3.142; // Azimuth
															// Horizontalwinkel in
															// Bogenmass -180 - 180
			alt = CCMath.asin(sindec * sinlat + cosdec * coslha * coslat); // Altitude
																				// Hoehenwinkel

			
		}

		/**
		 * Transform geocentric equatorial coordinates (RA/Dec) to topocentric equatorial coordinates
		 * @param observer
		 * @param lmst
		 */
		private void GeoEqu2TopoEqu(Coor observer, double lmst) {
			double cosdec = CCMath.cos(dec);
			double sindec = CCMath.sin(dec);
			double coslst = CCMath.cos(lmst);
			double sinlst = CCMath.sin(lmst);
			// we should use geocentric latitude, not geodetic latitude
			double coslat = CCMath.cos(observer.lat); 
			double sinlat = CCMath.sin(observer.lat);
			double rho = observer.radius; // observer-geocenter in Kilometer

			double x = distance * cosdec * CCMath.cos(ra) - rho * coslat * coslst;
			double y = distance * cosdec * CCMath.sin(ra) - rho * coslat * sinlst;
			double z = distance * sindec - rho * sinlat;

			distanceTopocentric = CCMath.sqrt(x * x + y * y + z * z);
			decTopocentric = CCMath.asin(z / distanceTopocentric);
			raTopocentric = Mod2Pi(CCMath.atan2(y, x));
		}


	}

	

	// Calculate cartesian from polar coordinates
	private static Coor EquPolar2Cart(double lon, double lat, double distance) {
		Coor cart = new Coor();
		double rcd = CCMath.cos(lat) * distance;
		cart.x = rcd * CCMath.cos(lon);
		cart.y = rcd * CCMath.sin(lon);
		cart.z = distance * CCMath.sin(lat);
		return (cart);
	}
	

	// Calculate observers cartesian equatorial coordinates (x,y,z in celestial
	// frame)
	// from geodetic coordinates (longitude, latitude, height above WGS84
	// ellipsoid)
	// Currently only used to calculate distance of a body from the observer
	private static Coor Observer2EquCart(double lon, double lat, double height, double gmst) {
		double flat = 298.257223563; // WGS84 flatening of earth
		double aearth = 6378.137; // GRS80/WGS84 semi major axis of earth
									// ellipsoid
		Coor cart = new Coor();
		// Calculate geocentric latitude from geodetic latitude
		double co = CCMath.cos(lat);
		double si = CCMath.sin(lat);
		double fl = 1.0 - 1.0 / flat;
		fl = fl * fl;
		si = si * si;
		double u = 1.0 / CCMath.sqrt(co * co + fl * si);
		double a = aearth * u + height;
		double b = aearth * fl * u + height;
		double radius = CCMath.sqrt(a * a * co * co + b * b * si); // geocentric
																	// distance
																	// from
																	// earth
																	// center
		cart.y = CCMath.acos(a * co / radius); // geocentric latitude, rad
		cart.x = lon; // longitude stays the same
		if (lat < 0.0) {
			cart.y = -cart.y;
		} // adjust sign
		cart = EquPolar2Cart(cart.x, cart.y, radius); // convert from geocentric
														// polar to geocentric
														// cartesian, with
														// regard to Greenwich
		// rotate around earth's polar axis to align coordinate system from
		// Greenwich to vernal equinox
		double x = cart.x;
		double y = cart.y;
		double rotangle = gmst / 24 * 2 * CCMath.PI; // sideral time gmst given
														// in hours. Convert to
														// radians
		cart.x = x * CCMath.cos(rotangle) - y * CCMath.sin(rotangle);
		cart.y = x * CCMath.sin(rotangle) + y * CCMath.cos(rotangle);
		cart.radius = radius;
		cart.lon = lon;
		cart.lat = lat;
		return (cart);
	}

	private static Coor SunPosition(double TDT) {
		double D = TDT - 2447891.5;

		double eg = 279.403303 * CCMath.DEG_TO_RAD;
		double wg = 282.768422 * CCMath.DEG_TO_RAD;
		double e = 0.016713;
		double a = 149598500; // km
		double diameter0 = 0.533128 * CCMath.DEG_TO_RAD; // angular diameter of
															// Moon at a
															// distance

		double MSun = 360 * CCMath.DEG_TO_RAD / 365.242191 * D + eg - wg;
		double nu = MSun + 360. * CCMath.DEG_TO_RAD / CCMath.PI * e * CCMath.sin(MSun);

		Coor sunCoor = new Coor();
		sunCoor.lon = Mod2Pi(nu + wg);
		sunCoor.lat = 0;
		sunCoor.anomalyMean = MSun;

		sunCoor.distance = (1 - CCMath.sq(e)) / (1 + e * CCMath.cos(nu)); // distance
																		// in
																		// astronomical
																		// units
		sunCoor.diameter = diameter0 / sunCoor.distance; // angular diameter in
															// radians
		sunCoor.distance *= a; // distance in km
		sunCoor.parallax = 6378.137 / sunCoor.distance; // horizonal parallax

		sunCoor.Ecl2Equ(TDT);

		sunCoor.sign = Sign(sunCoor.lon);
		return sunCoor;

	}

	// Calculate coordinates for Sun
	// Coordinates are accurate to about 10s (right ascension)
	// and a few minutes of arc (declination)
	private static Coor SunPosition(double TDT, double geolat, double lmst) {

		Coor sunCoor = SunPosition(TDT);

		// Calculate horizonal coordinates of sun, if geographic positions is
		// given
		sunCoor.Equ2Altaz(TDT, geolat, lmst);
		return sunCoor;
	}

	private static Coor MoonPosition(Coor sunCoor, double TDT) {
		double D = TDT - 2447891.5;

		// Mean Moon orbit elements as of 1990.0
		double l0 = 318.351648 * CCMath.DEG_TO_RAD;
		double P0 = 36.340410 * CCMath.DEG_TO_RAD;
		double N0 = 318.510107 * CCMath.DEG_TO_RAD;
		double i = 5.145396 * CCMath.DEG_TO_RAD;
		double e = 0.054900;
		double a = 384401; // km
		double diameter0 = 0.5181 * CCMath.DEG_TO_RAD; // angular diameter of
														// Moon at a distance
		double parallax0 = 0.9507 * CCMath.DEG_TO_RAD; // parallax at distance a

		double l = 13.1763966 * CCMath.DEG_TO_RAD * D + l0;
		double MMoon = l - 0.1114041 * CCMath.DEG_TO_RAD * D - P0; // Moon's
																	// mean
																	// anomaly M
		double N = N0 - 0.0529539 * CCMath.DEG_TO_RAD * D; // Moon's mean
															// ascending node
															// longitude
		double C = l - sunCoor.lon;
		double Ev = 1.2739 * CCMath.DEG_TO_RAD * CCMath.sin(2 * C - MMoon);
		double Ae = 0.1858 * CCMath.DEG_TO_RAD * CCMath.sin(sunCoor.anomalyMean);
		double A3 = 0.37 * CCMath.DEG_TO_RAD * CCMath.sin(sunCoor.anomalyMean);
		double MMoon2 = MMoon + Ev - Ae - A3; // corrected Moon anomaly
		double Ec = 6.2886 * CCMath.DEG_TO_RAD * CCMath.sin(MMoon2); // equation
																	// of centre
		double A4 = 0.214 * CCMath.DEG_TO_RAD * CCMath.sin(2 * MMoon2);
		double l2 = l + Ev + Ec - Ae + A4; // corrected Moon's longitude
		double V = 0.6583 * CCMath.DEG_TO_RAD * CCMath.sin(2 * (l2 - sunCoor.lon));
		double l3 = l2 + V; // true orbital longitude;

		double N2 = N - 0.16 * CCMath.DEG_TO_RAD * CCMath.sin(sunCoor.anomalyMean);

		Coor moonCoor = new Coor();
		moonCoor.lon = Mod2Pi(N2 + CCMath.atan2(CCMath.sin(l3 - N2) * CCMath.cos(i), CCMath.cos(l3 - N2)));
		moonCoor.lat = CCMath.asin(CCMath.sin(l3 - N2) * CCMath.sin(i));
		moonCoor.orbitLon = l3;

		moonCoor.Ecl2Equ(TDT);
		// relative distance to semi mayor axis of lunar oribt
		moonCoor.distance = (1 - CCMath.sq(e)) / (1 + e * CCMath.cos(MMoon2 + Ec));
		moonCoor.diameter = diameter0 / moonCoor.distance; // angular diameter
															// in radians
		moonCoor.parallax = parallax0 / moonCoor.distance; // horizontal
															// parallax in
															// radians
		moonCoor.distance *= a; // distance in km

		// Age of Moon in radians since New Moon (0) - Full Moon (pi)
		moonCoor.moonAge = Mod2Pi(l3 - sunCoor.lon);
		moonCoor.phase = 0.5 * (1 - CCMath.cos(moonCoor.moonAge)); // Moon phase,
																	// 0-1

		double mainPhase = 1. / 29.53 * 360 * CCMath.DEG_TO_RAD; // show
																	// 'Newmoon,
																	// 'Quarter'
																	// for +/-1
																	// day arond
																	// the
																	// actual
																	// event
		double p = Mod(moonCoor.moonAge, 90. * CCMath.DEG_TO_RAD);
		if (p < mainPhase || p > 90 * CCMath.DEG_TO_RAD - mainPhase)
			p = 2 * CCMath.round(moonCoor.moonAge / (90. * CCMath.DEG_TO_RAD));
		else
			p = 2 * CCMath.floor(moonCoor.moonAge / (90. * CCMath.DEG_TO_RAD)) + 1;
		moonCoor.moonPhase = phases[(int) p];

		moonCoor.sign = Sign(moonCoor.lon);

		return (moonCoor);
	}

	// Calculate data and coordinates for the Moon
	// Coordinates are accurate to about 1/5 degree (in ecliptic coordinates)
	private static Coor MoonPosition(Coor sunCoor, double TDT, Coor observer, double lmst) {

		Coor moonCoor = MoonPosition(sunCoor, TDT);

		// Calculate horizonal coordinates of sun, if geographic positions is
		// given
		// transform geocentric coordinates into topocentric (==observer based)
		// coordinates
		moonCoor.GeoEqu2TopoEqu(observer, lmst);
		moonCoor.raGeocentric = moonCoor.ra; // backup geocentric coordinates
		moonCoor.decGeocentric = moonCoor.dec;
		moonCoor.ra = moonCoor.raTopocentric;
		moonCoor.dec = moonCoor.decTopocentric;
		moonCoor.Equ2Altaz(TDT, observer.lat, lmst); // now ra and
																	// dec are
																	// topocentric

		return (moonCoor);
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
			return (0.00452 * pressure / ((273 + temperature) * CCMath.tan(alt)));

		double y = alt;
		double D = 0.0;
		double P = (pressure - 80.) / 930.;
		double Q = 0.0048 * (temperature - 10.);
		double y0 = y;
		double D0 = D;

		for (int i = 0; i < 3; i++) {
			double N = y + (7.31 / (y + 4.4));
			N = 1. / CCMath.tan(N * CCMath.DEG_TO_RAD);
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
		return (D); // Hebung durch Refraktion in radians
	}

	private static class RiseSet {

		public double transit;
		public double rise;
		public double set;
		public double cicilTwilightMorning;
		public double cicilTwilightEvening;
		public double nauticalTwilightMorning;
		public double nauticalTwilightEvening;
		public double astronomicalTwilightMorning;
		public double astronomicalTwilightEvening;

	}

	
	
	/**
	 * returns Greenwich sidereal time (hours) of time of rise
	 * and set of object with coordinates coor.ra/coor.dec
	 * at geographic position lon/lat (all values in radians)
	 * Correction for refraction and semi-diameter/parallax of body is taken
	 * care of in function RiseSet
	 * h is used to calculate the twilights. It gives the required elevation of
	 * the disk center of the sun
	 * @param coor
	 * @param lon
	 * @param lat
	 * @param h
	 * @return
	 */
	private static RiseSet GMSTRiseSet(Coor coor, double lon, double lat, double h) {
		h = (h == Double.NaN) ? 0. : h; // set default value
		RiseSet riseset = new RiseSet();
		// var tagbogen = CCMath.acos(-CCMath.tan(lat)*CCMath.tan(coor.dec)); //
		// simple formula if twilight is not required
		double tagbogen = CCMath.acos((CCMath.sin(h) - CCMath.sin(lat) * CCMath.sin(coor.dec)) / (CCMath.cos(lat) * CCMath.cos(coor.dec)));

		riseset.transit = CCMath.RAD_TO_DEG / 15 * (+coor.ra - lon);
		// calculate GMST of rise of object
		riseset.rise = 24. + CCMath.RAD_TO_DEG / 15 * (-tagbogen + coor.ra - lon); 
		// calculate GMST of set of object
		riseset.set = CCMath.RAD_TO_DEG / 15 * (+tagbogen + coor.ra - lon); 

		// using the modulo function Mod, the day number goes missing. This may
		// get a problem for the moon
		riseset.transit = Mod(riseset.transit, 24);
		riseset.rise = Mod(riseset.rise, 24);
		riseset.set = Mod(riseset.set, 24);

		return (riseset);
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
		if (altitude == 0)
			alt = 0.5 * coor1.diameter - coor1.parallax + 34. / 60 * CCMath.DEG_TO_RAD;

		RiseSet rise1 = GMSTRiseSet(coor1, lon, lat, altitude);
		RiseSet rise2 = GMSTRiseSet(coor2, lon, lat, altitude);

		RiseSet rise = new RiseSet();

		// unwrap GMST in case we move across 24h -> 0h
		if (rise1.transit > rise2.transit && CCMath.abs(rise1.transit - rise2.transit) > 18)
			rise2.transit += 24;
		if (rise1.rise > rise2.rise && CCMath.abs(rise1.rise - rise2.rise) > 18)
			rise2.rise += 24;
		if (rise1.set > rise2.set && CCMath.abs(rise1.set - rise2.set) > 18)
			rise2.set += 24;
		double T0 = GMST(jd0UT);
		// var T02 = T0-zone*1.002738; // Greenwich sidereal time at 0h time
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
		double psi = CCMath.acos(CCMath.sin(lat) / CCMath.cos(decMean));
		double y = CCMath.asin(CCMath.sin(alt) / CCMath.sin(psi));
		double dt = 240 * CCMath.RAD_TO_DEG * y / CCMath.cos(decMean) / 3600; // time
																			// correction
																			// due
																			// to
																			// refraction,
																			// parallax

		rise.transit = GMST2UT(jd0UT, InterpolateGMST(T0, rise1.transit, rise2.transit, timeinterval));
		rise.rise = GMST2UT(jd0UT, InterpolateGMST(T0, rise1.rise, rise2.rise, timeinterval) - dt);
		rise.set = GMST2UT(jd0UT, InterpolateGMST(T0, rise1.set, rise2.set, timeinterval) + dt);

		return (rise);
	}
	
	/**
	 * Find local time of moonrise and moonset
	 * @param JD is the Julian Date of 0h local time (midnight)
	 * @param deltaT
	 * @param lon
	 * @param lat
	 * @param zone
	 * @param recursive if <code>true</code> calculate rise/set in UTC otherwise find rise/set on the current local day (set could also be first)
	 * @return
	 */
	private static RiseSet MoonRise(double JD, double deltaT, double lon, double lat, double zone, boolean recursive) {
		double timeinterval = 0.5;

		double jd0UT = CCMath.floor(JD - 0.5) + 0.5; // JD at 0 hours UT
		Coor suncoor1 = SunPosition(jd0UT + deltaT / 24. / 3600.);
		Coor coor1 = MoonPosition(suncoor1, jd0UT + deltaT / 24. / 3600.);

		Coor suncoor2 = SunPosition(jd0UT + timeinterval + deltaT / 24. / 3600.); // calculations
																					// for
																					// noon
		// calculations for next day's midnight
		Coor coor2 = MoonPosition(suncoor2, jd0UT + timeinterval + deltaT / 24. / 3600.);

		// rise/set time in UTC, time zone corrected later.
		// Taking into account refraction, semi-diameter and parallax
		RiseSet rise = RiseSet(jd0UT, coor1, coor2, lon, lat, timeinterval, 0);

		// check and adjust to have rise/set time on local calendar day
		if (!recursive) { 
			if (zone > 0) {
				// recursive call to MoonRise returns events in UTC
				RiseSet riseprev = MoonRise(JD - 1., deltaT, lon, lat, zone, true);

				if (rise.transit >= 24. - zone || rise.transit < -zone) { // transit
																			// time
																			// is
																			// tomorrow
																			// local
																			// time
					if (riseprev.transit < 24. - zone)
						rise.transit = Double.NaN; // there is no moontransit
													// today
					else
						rise.transit = riseprev.transit;
				}

				if (rise.rise >= 24. - zone || rise.rise < -zone) { // transit
																	// time is
																	// tomorrow
																	// local
																	// time
					if (riseprev.rise < 24. - zone)
						rise.rise = Double.NaN; // there is no moontransit today
					else
						rise.rise = riseprev.rise;
				}

				// transit time is tomorrow local time
				if (rise.set >= 24. - zone || rise.set < -zone) { 
					if (riseprev.set < 24. - zone)
						rise.set = Double.NaN; // there is no moontransit today
					else
						rise.set = riseprev.set;
				}

			} else if (zone < 0) {
				// rise/set time was tomorrow local time -> calculate rise time
				// for former UTC day
				if (rise.rise < -zone || rise.set < -zone || rise.transit < -zone) {
					RiseSet risetemp = MoonRise(JD + 1., deltaT, lon, lat, zone, true);

					if (rise.rise < -zone) {
						if (risetemp.rise > -zone)
							rise.rise = Double.NaN; // there is no moonrise
													// today
						else
							rise.rise = risetemp.rise;
					}

					if (rise.transit < -zone) {
						if (risetemp.transit > -zone)
							// there is no moonset today
							rise.transit = Double.NaN; 
						else
							rise.transit = risetemp.transit;
					}

					if (rise.set < -zone) {
						if (risetemp.set > -zone)
							rise.set = Double.NaN; // there is no moonset today
						else
							rise.set = risetemp.set;
					}

				}
			}

			if (rise.rise != Double.NaN)
				rise.rise = Mod(rise.rise + zone, 24.); // correct for time
														// zone, if time is
														// valid
			if (rise.transit != Double.NaN)
				rise.transit = Mod(rise.transit + zone, 24.); // correct for
																// time zone, if
																// time is valid
			if (rise.set != Double.NaN)
				rise.set = Mod(rise.set + zone, 24.); // correct for time zone,
														// if time is valid
		}
		return (rise);
	}

	private static String[] phases = new String[] { "Neumond", "Zunehmende Sichel", "Erstes Viertel",
			"Zunehmender Mond", "Vollmond", "Abnehmender Mond", "Letztes Viertel", "Abnehmende Sichel", "Neumond" };

		public double altitude;
		public double azimuth;
		// Entfernung Erdmittelpunkt zum Mond in km
		public double distance;
		// Name der Mondphase
		public String phase;
		// Beleuchtung des Mondes 0 - 1, http://de.wikipedia.org/wiki/Mondphase
		public double fraction;
		public CCDate aufgang;
		public CCDate untergang;
		// Hoechsstand
		public CCDate kulmination;

		/**
		 * age in days
		 */
		public double ageInDays;

		/**
		 * age in degrees
		 */
		public double ageInDegrees;

		public CCMoon(CCDate theDate, double theLatitude, double theLongitude) {

			CCDate now = new CCDate();
			double diffTimeZone = -now.timezoneOffset() / 60.;
			double DeltaT = 65; // deltaT - difference among 'earth center'
								// versus 'observered' time (TDT-UT), in seconds

			double JD0 = CalcJD(theDate.day(), theDate.month() + 1, theDate.year());
			double JD = JD0 + (theDate.hours() - diffTimeZone + theDate.minutes() / 60. + theDate.seconds() / 3600.) / 24.;
			double TDT = JD + DeltaT / 24. / 3600.;

			// geodetic latitude of observer on WGS84
			double lat = theLatitude * CCMath.DEG_TO_RAD;
			// latitude of observer
			double lon = theLongitude * CCMath.DEG_TO_RAD;
			// altiude of observer in meters above WGS84 ellipsoid (and
			// converted to kilometers)
			double height = 0 * 0.001;

			double gmst = GMST(JD);
			double lmst = GMST2LMST(gmst, lon);

			// geocentric cartesian coordinates of observer
			Coor observerCart = Observer2EquCart(lon, lat, height, gmst);

			// Calculate data for the Sun at given time
			Coor sunCoor = SunPosition(TDT, lat, lmst * 15. * CCMath.DEG_TO_RAD);
			// Calculate data for the Moon at given time
			Coor moonCoor = MoonPosition(sunCoor, TDT, observerCart, lmst * 15. * CCMath.DEG_TO_RAD);
			RiseSet moonRise = MoonRise(JD0, DeltaT, lon, lat, diffTimeZone, false);
			// including refraction in Bogenmass (Radians)
			altitude = (moonCoor.alt * CCMath.RAD_TO_DEG + Refraction(moonCoor.alt)) * CCMath.DEG_TO_RAD;
			// in Bogenmass (Radians)
			azimuth = (moonCoor.myaz);
			distance = CCMath.round(moonCoor.distance, 1);
			phase = moonCoor.moonPhase;
			fraction = CCMath.round(moonCoor.phase,3);
			aufgang = theDate.clone().fromDoubleTime(moonRise.rise);
			untergang = theDate.clone().fromDoubleTime(moonRise.set);
			kulmination = theDate.clone().fromDoubleTime(moonRise.transit);
			ageInDegrees = CCMath.round(moonCoor.moonAge * CCMath.RAD_TO_DEG, 3);
			ageInDays = 29.5 / 360 * ageInDegrees;
		}

		public CCVector3 position(double theRadius) {
			double angle = CCMath.PI / 2 + azimuth;
			return new CCVector3(
				theRadius * CCMath.cos(angle) * CCMath.cos(altitude), 
				theRadius * CCMath.sin(angle) * CCMath.cos(altitude), 
				altitude
			);
		}
	

}
