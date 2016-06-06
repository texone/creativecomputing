package cc.creativecomputing.flightXML;

import cc.creativecomputing.io.data.CCDataObject;

/**
 * <pre>
 * {
 * 	aircrafttype=A321, 
 * 	ident=BAW1305, 
 * 	filed_airspeed_kts=247, 
 * 	origin=EGPD, 
 * 	destination=EGLL, 
 * 	faFlightID=BAW1305-1464766289-airline-0137, 
 * 	filed_departuretime=1464942600, 
 * 	destinationCity=London, England GB, 
 * 	diverted=, 
 * 	route=, 
 * 	actualdeparturetime=0, 
 * 	estimatedarrivaltime=1464948300, 
 * 	destinationName=London Heathrow, 
 * 	filed_ete=01:25:00, 
 * 	originCity=Aberdeen, Scotland GB, 
 * 	filed_altitude=0, 
 * 	filed_time=1464766289, 
 * 	filed_airspeed_mach=, 
 * 	actualarrivaltime=0, 
 * 	originName=Aberdeen
 * }
 * </pre>
 * @author christianr
 *
 */
public class CCFlightInfoExStruct extends CCArrivalFlightStruct{
	
	
	public final String diverted;
	public final long estimatedarrivaltime;
	public final String faFlightID;
	public final int filed_airspeed_kts;
	public final String filed_airspeed_mach;
	public final int filed_altitude;
	public final long filed_departuretime;
	public final String filed_ete;
	public final long filed_time;
	
	public final String route;

	public CCFlightInfoExStruct(CCDataObject theData) {
		super(theData);
		

		diverted = theData.getString("diverted");
		estimatedarrivaltime = theData.getLong("estimatedarrivaltime") * 1000;
		faFlightID = theData.getString("faFlightID");
		filed_airspeed_kts = theData.getInt("filed_airspeed_kts");
		filed_airspeed_mach = theData.getString("filed_airspeed_mach");
		filed_altitude = theData.getInt("filed_altitude");
		filed_departuretime = theData.getLong("filed_departuretime") * 1000;
		filed_ete = theData.getString("filed_ete");
		filed_time = theData.getLong("filed_time") * 1000;
		route = theData.getString("route");
		
	}
	


}
