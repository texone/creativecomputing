package cc.creativecomputing.flightXML;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.geo.CCGeoTrack;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCMath;

public class CCFlightXML {
	
	private final static String FLIGHT_XML_URL = "http://flightxml.flightaware.com/json/FlightXML2/";
	
	private String _myUser;
	private String _myKey;
	
	private int _myQueries = 0;
	private double _myCosts = 0;

	public CCFlightXML(String theUser, String theKey){
		_myUser = theUser;
		_myKey = theKey;
		
//		try{
//			String myCosts = CCNIOUtil.loadString(CCNIOUtil.dataPath("flightxml/datacosts.txt"));
//			String[] myCostParts = myCosts.split(",");
//			_myQueries = Integer.parseInt(myCostParts[0]);
//			_myCosts = Double.parseDouble(myCostParts[1]);
//		}catch(Exception e){
//			
//		}
	}
	
	private void addCost(int theClass, int theSize){
		_myQueries++;
		switch(theClass){
		
		}
		CCNIOUtil.saveString(CCNIOUtil.dataPath("flightxml/datacosts.txt"), _myQueries + "," + _myCosts);
	}
	
	private void cache(String theQuery, CCDataObject theObject){
		String[] myQueryParts = theQuery.split(Pattern.quote("?"));
		CCNIOUtil.createDirectories(CCNIOUtil.dataPath("flightxml/" + myQueryParts[0]));
		CCDataIO.saveDataObject(theObject, CCNIOUtil.dataPath("flightxml/" + myQueryParts[0]).resolve(myQueryParts[1]));

//		CCLog.info("CACHE:" + theQuery + ":" + theObject + ":" + CCNIOUtil.dataPath(myQueryParts[0]).resolve(myQueryParts[1]));
	}
	
	private CCDataObject checkCached(String theQuery){
		String myPath = theQuery.replace("?", "/");
		try{
			return CCDataIO.createDataObject(CCNIOUtil.dataPath("flightxml/" + myPath + ".json"));
		}catch(Exception e){
			return null;
		}
	}
	
	private CCDataObject query(String theQuery){   
		try {
//			CCLog.info(theQuery);
			CCDataObject myObject = checkCached(theQuery);
			if(myObject != null){
//				CCLog.info("FOUND CACHE:"+theQuery);
				myObject.put("cached", true);
				return myObject;
			}
			myObject = CCDataIO.createDataObject(new URL( FLIGHT_XML_URL + theQuery), true, CCDataFormats.JSON, _myUser, _myKey);
			myObject.put("cached", false);
			cache(theQuery, myObject);
			return myObject;
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Modifies the maximum result count returned by other FlightXML methods. 
	 * Many FlightXML methods that return lists limit the number of results to 15 records for performance reasons, 
	 * even if you specify a larger number to its "howMany" argument. Using this method, you can raise the limit 
	 * allowed for the "howMany" argument for all subsequent FlightXML methods invoked using your account. 
	 * Once invoked, the last specified max_size is remembered for your account until the next call to SetMaximumResultSize; 
	 * it is not necessary to call this function repeatedly.
	 * <p>
	 * Any request that has a "howMany" argument and returns more than 15 records will be billed at a rate equivalent to the 
	 * actual number of results returned divided by 15, rounded up. For example, if you call SetMaximumResultSize with a max_size 
	 * of 100, then call FlightInfo with howMany of 45, but it only returns 35 records, you will be charged the equivalent of 
	 * calling FlightInfo a total of 3 times, or 1+int(35/15).
	 * @param theMaxSize
	 */
	public void setMaximumResultSize(int theMaxSize){
		query("SetMaximumResultSize?max_size=" + theMaxSize);
	}
	
	/**
	 *  Returns information about flights that have recently arrived for the specified airport and 
	 *  maximum number of flights to be returned. Flights are returned from most to least recent. 
	 *  Only flights that arrived within the last 24 hours are considered.
	 *  Times returned are seconds since 1970 (UNIX epoch seconds).
	 *  @param theAirport the ICAO airport ID (e.g., KLAX, KSFO, KIAH, KHOU, KJFK, KEWR, KORD, KATL, etc.)
	 * @return
	 */
	public List<CCArrivalFlightStruct> arrived(String theAirport){
		CCDataArray myResultObject = query("Arrived?airport=" + theAirport+"&howMany=5000").getObject("ArrivedResult").getArray("arrivals");
		List<CCArrivalFlightStruct> myResult = new ArrayList<>();
		
		for(int i = 0; i < myResultObject.size();i++){
			myResult.add(new CCArrivalFlightStruct(myResultObject.getObject(i)));
		}
		
		return myResult;
	}
	
	/**
	 * Returns information about an airport given an ICAO airport code such as KLAX, KSFO, KORD, KIAH, O07, etc. 
	 * Data returned includes name (Houston Intercontinental Airport), location (typically city and state), 
	 * latitude and longitude, and timezone (:America/Chicago).
	 * @param theAirport
	 * @return
	 */
	public CCAirportInfoStruct aipportInfo(String theAirport){
		CCDataObject myResultObject = query("AirportInfo?airportCode=" + theAirport).getObject("AirportInfoResult");
		if(myResultObject == null)return null;
		return new CCAirportInfoStruct(myResultObject);
	}
	
	/**
	 *  Returns information about already departed flights for a specified airport and maximum number 
	 *  of flights to be returned. Departed flights are returned in order from most recently to least 
	 *  recently departed. Only flights that have departed within the last 24 hours are considered.

Times returned are seconds since 1970 (UNIX epoch seconds).
	 *  @param theAirport the ICAO airport ID (e.g., KLAX, KSFO, KIAH, KHOU, KJFK, KEWR, KORD, KATL, etc.)
	 * @return
	 */
	public List<CCArrivalFlightStruct> departed(String theAirport){
		CCDataArray myResultObject = query("Departed?airport=" + theAirport+"&howMany=5000").getObject("ArrivedResult").getArray("arrivals");
		List<CCArrivalFlightStruct> myResult = new ArrayList<>();
		
		for(int i = 0; i < myResultObject.size();i++){
			myResult.add(new CCArrivalFlightStruct(myResultObject.getObject(i)));
		}
		
		return myResult;
	}
	
	/**
	 * GetLastTrack looks up a flight's track log by specific tail number (e.g., N12345) or 
	 * ICAO airline and flight number (e.g., SWA2558). It returns the track log from the current IFR flight or, 
	 * if the aircraft is not airborne, the most recent IFR flight. It returns an array of positions, with each 
	 * including the timestamp, longitude, latitude, groundspeed, altitude, altitudestatus, updatetype, and altitudechange. 
	 * <p>
	 * Altitude is in hundreds of feet or Flight Level where appropriate, see our FAQ about flight levels. 
	 * Also included altitude status, update type, and altitude change.
	 * <p>
	 * Altitude status is 'C' when the flight is more than 200 feet away from its ATC-assigned altitude. 
	 * (For example, the aircraft is transitioning to its assigned altitude.) Altitude change is 'C' if the aircraft 
	 * is climbing (compared to the previous position reported), 'D' for descending, and empty if it is level. 
	 * This happens for VFR flights with flight following, among other things. 
	 * Timestamp is integer seconds since 1970 (UNIX epoch time).
	 * <p>
	 * This function only returns tracks for recent flights within approximately the last 24 hours. Use the 
	 * GetHistoricalTrack function to look up a specific past flight rather than just the most recent one. 
	 * Codeshares and alternate idents are automatically searched.
	 * @param theIdent requested tail number
	 */
	public CCGeoTrack<CCTrackStruct> lastTrack(String theIdent){
		CCGeoTrack<CCTrackStruct> myResult = new CCGeoTrack<>();
		CCDataObject myRes = query("GetLastTrack?ident=" + theIdent);
		if(myRes.containsKey("error")){
			return myResult;
		}
		CCDataArray myResultObject = myRes.getObject("GetLastTrackResult").getArray("data");
		
		for(int i = 0; i < myResultObject.size();i++){
			myResult.add(new CCTrackStruct(myResultObject.getObject(i)));
		}
		
		return myResult;
	}
	
	/**
	 * looks up a past flight's track log by its unique identifier.
	 * Use the GetLastTrack function to look up just the most recent flight rather than a specific historical one.
	 * @param thefaFlightID unique identifier assigned by FlightAware for the desired flight (or use "ident@departureTime")
	 * @return
	 */
	public List<CCTrackStruct> historicalTrack(String thefaFlightID){
		List<CCTrackStruct> myResult = new ArrayList<>();
		CCDataObject myRes = query("GetHistoricalTrack?faFlightID=" + thefaFlightID);
		if(myRes.containsKey("error")){
			return myResult;
		}
		
		CCDataArray myResultObject = myRes.getObject("GetHistoricalTrackResult").getArray("data");
		
		for(int i = 0; i < myResultObject.size();i++){
			myResult.add(new CCTrackStruct(myResultObject.getObject(i)));
		}
		
		return myResult;
	}
	
	/**
	 * returns information about flights for a specific tail number (e.g., N12345), or an ident 
	 * (typically an ICAO airline with flight number, e.g., SWA2558), or a FlightAware-assigned 
	 * unique flight identifier (e.g. faFlightID returned by another FlightXML function).
	 * <p>
	 * When a tail number 
	 * or ident is specified and multiple flights are available, the results will be returned from newest to oldest. 
	 * The oldest flights searched by this function are about 2 weeks in the past.
	 * <p>
	 * When specifying an airline with flight number, either an ICAO or IATA code may be used to 
	 * designate the airline, however ambiguous or conflicting IATA code assignments do exist so
	 * use of ICAO codes is strongly recommended.
	 * <p>
	 * Codeshares and alternate idents are automatically searched, which may cause the actual identifier 
	 * of the primary operator of the flight to be returned instead of the originally requested identifier.
	 * <p>
	 * When a FlightAware-assigned unique flight identifier is supplied, at most a single result will be returned.
	 * <p>
	 * Times are in integer seconds since 1970 (UNIX epoch time), except for estimated time enroute, which is in hours and minutes.
	 * @param theIdent
	 */
	public List<CCFlightInfoExStruct> flightInfoEx(String theIdent){
		List<CCFlightInfoExStruct> myResult = new ArrayList<>();
		CCDataObject myRes = query("FlightInfoEx?ident=" + theIdent);
		
		if(myRes.containsKey("error")){
			return myResult;
		}
		//{FlightInfoExResult={flights=[{aircrafttype=A321, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1464766289-airline-0137, filed_departuretime=1464942600, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=0, estimatedarrivaltime=1464948300, destinationName=London Heathrow, filed_ete=01:25:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1464766289, filed_airspeed_mach=, actualarrivaltime=0, originName=Aberdeen}, {aircrafttype=A320, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1464672413-airline-0286, filed_departuretime=1464856200, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=0, estimatedarrivaltime=1464862020, destinationName=London Heathrow, filed_ete=01:27:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1464672413, filed_airspeed_mach=, actualarrivaltime=0, originName=Aberdeen}, {aircrafttype=A320, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1464586017-airline-0195:0, filed_departuretime=1464769800, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1464770640, estimatedarrivaltime=1464775320, destinationName=London Heathrow, filed_ete=01:18:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1464701340, filed_airspeed_mach=, actualarrivaltime=1464775302, originName=Aberdeen}, {aircrafttype=A319, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1464499582-airline-0053:0, filed_departuretime=1464683400, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1464684791, estimatedarrivaltime=1464690120, destinationName=London Heathrow, filed_ete=01:23:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1464635717, filed_airspeed_mach=, actualarrivaltime=1464690126, originName=Aberdeen}, {aircrafttype=A320, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1464413557-airline-0119:0, filed_departuretime=1464597000, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1464596603, estimatedarrivaltime=1464601260, destinationName=London Heathrow, filed_ete=01:34:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1464507788, filed_airspeed_mach=, actualarrivaltime=1464601260, originName=Aberdeen}, {aircrafttype=A319, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1464154073-airline-0086:0, filed_departuretime=1464337800, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1464338484, estimatedarrivaltime=1464343620, destinationName=London Heathrow, filed_ete=01:18:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1464238443, filed_airspeed_mach=, actualarrivaltime=1464343638, originName=Aberdeen}, {aircrafttype=A320, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1464067596-airline-0164:0, filed_departuretime=1464251400, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1464251561, estimatedarrivaltime=1464255720, destinationName=London Heathrow, filed_ete=01:26:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1464151682, filed_airspeed_mach=, actualarrivaltime=1464255756, originName=Aberdeen}, {aircrafttype=A320, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1463981209-airline-0224:0, filed_departuretime=1464165000, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1464165360, estimatedarrivaltime=1464170700, destinationName=London Heathrow, filed_ete=01:22:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1464065532, filed_airspeed_mach=, actualarrivaltime=1464170727, originName=Aberdeen}, {aircrafttype=A319, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1463894837-airline-0132:0, filed_departuretime=1464078600, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1464078480, estimatedarrivaltime=1464083940, destinationName=London Heathrow, filed_ete=01:27:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1463983075, filed_airspeed_mach=, actualarrivaltime=1464083940, originName=Aberdeen}, {aircrafttype=A319, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1463808445-airline-0088:0, filed_departuretime=1463992200, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1463992440, estimatedarrivaltime=1463997120, destinationName=London Heathrow, filed_ete=01:18:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1463898186, filed_airspeed_mach=, actualarrivaltime=1463997151, originName=Aberdeen}, {aircrafttype=A320, ident=BAW1305, filed_airspeed_kts=247, origin=EGPD, destination=EGLL, faFlightID=BAW1305-1463549226-airline-0039:0, filed_departuretime=1463733000, destinationCity=London, England GB, diverted=, route=, actualdeparturetime=1463733377, estimatedarrivaltime=1463737980, destinationName=London Heathrow, filed_ete=01:24:00, originCity=Aberdeen, Scotland GB, filed_altitude=0, filed_time=1463668015, filed_airspeed_mach=, actualarrivaltime=1463738020, originName=Aberdeen}], next_offset=-1}}

		
		CCDataArray myResultObject = myRes.getObject("FlightInfoExResult").getArray("flights");
		
		for(int i = 0; i < myResultObject.size();i++){
			myResult.add(new CCFlightInfoExStruct(myResultObject.getObject(i)));
		}
		
		return myResult;
	}
	
	public static void main(String[] args) {
		String myUser = "user";
		String myKey = "key";
		
		CCFlightXML myFlightXML = new CCFlightXML(myUser, myKey);
		myFlightXML.setMaximumResultSize(5000);
		
//		myFlightXML.historicalTrack("DLH1198-1461735014-airline-0136:2");
		List<CCArrivalFlightStruct> myArrivedFlights = myFlightXML.arrived("LSZH");//LSZH//EDDF
		for(CCArrivalFlightStruct myFlight:myArrivedFlights){
			int mySize = myFlightXML.lastTrack(myFlight.ident).size();
			if(mySize > 0){
//				mySize = myFlightXML.historicalTrack(myFlight.ident).size();
//				myFlightXML.flightInfoEx(myFlight.ident);
//				CCLog.info(myFlight.ident + ":" + mySize + ":" + myFlight.originCity);
			}
		}
		int flightID = 0;
		for(CCArrivalFlightStruct myFlight:myArrivedFlights){
			List<CCTrackStruct> myTracks = myFlightXML.lastTrack(myFlight.ident);
//			fixTrack(myTracks);
			int mySize = myTracks.size();
			if(mySize > 0){
				for(int i = 0; i < myTracks.size() - 1; i++){
					CCTrackStruct myPoint0 = myTracks.get(i);
					CCTrackStruct myPoint1 = myTracks.get(i+1);

					double difLon = CCMath.abs(myPoint1.longitude - myPoint0.longitude);
					double difLat = CCMath.abs(myPoint1.latitude - myPoint0.latitude);
					boolean projected = myPoint0.updateType.equals("TP") || myPoint1.updateType.equals("TP");
//					if((difLat > 1 || difLon > 1) && projected)CCLog.info(flightID + ":" + i + ":" + myFlight.ident + ":" + myFlight.originName + ":" + difLat + ":" + difLon);
					if(myPoint0.updateType.equals("TP") && myPoint1.updateType.equals("TP")){
//						myPoint.altitude = myLastAltitude;
					}
				}
//				CCLog.info(myFlight.ident + ":" + mySize + ":" + myFlight.originCity);
				flightID++;
			}
		}
//		CCLog.info(myArrivedFlights.size());
	}
}
