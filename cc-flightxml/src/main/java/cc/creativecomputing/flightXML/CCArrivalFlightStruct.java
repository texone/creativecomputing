package cc.creativecomputing.flightXML;

import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.time.CCDate;

public class CCArrivalFlightStruct {
	/**
	 * filed time of departure (seconds since 1970)
	 */
	public final CCDate actualarrivaltime;	
	/**
	 * actual time of departure (seconds since 1970)
	 */
	public final CCDate actualdeparturetime;
	/**
	 * aircraft type ID
	 */
	public final String aircrafttype;	
	/**
	 * the destination ICAO airport ID
	 */
	public final String destination;	
	public final String destinationCity;	
	public final String destinationName;	
	/**
	 * flight ident or tail number
	 */
	public final String ident;	
	/**
	 * the origin ICAO airport ID
	 */
	public final String origin;	
	public final String originCity;	
	public final String originName;
	
	public CCArrivalFlightStruct(CCDataObject theData){
		actualarrivaltime = new CCDate(theData.getLong("actualarrivaltime"));
		actualdeparturetime = new CCDate(theData.getLong("actualdeparturetime"));
		
		aircrafttype = theData.getString("aircrafttype");

		destination = theData.getString("destination");
		destinationCity = theData.getString("destinationCity");
		destinationName = theData.getString("destinationName");

		ident = theData.getString("ident");

		origin = theData.getString("origin");
		originCity = theData.getString("originCity");
		originName = theData.getString("originName");
	}
}
