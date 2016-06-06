package cc.creativecomputing.flightXML;

import cc.creativecomputing.geo.CCGeoTrackPoint;
import cc.creativecomputing.io.data.CCDataObject;

/**
 *  It returns an array of positions, with each including the timestamp, longitude, latitude, groundspeed, altitude, altitudestatus, updatetype, and altitudechange. 
 *  Altitude is in hundreds of feet or Flight Level where appropriate, see our FAQ about flight levels. Also included altitude status, update type, and altitude change.

  This happens for VFR flights with flight following, among other things. 
 * @author christianr
 *
 */
public class CCTrackStruct extends CCGeoTrackPoint{
	
	/**
	 * Altitude change is 'C' if the aircraft is climbing (compared to the previous position reported), 
	 * 'D' for descending, and empty if it is level.
	 */
	public final String altitudeChange;	
	
	/**
	 * Altitude status is 'C' when the flight is more than 200 feet away from its ATC-assigned altitude. 
	 * (For example, the aircraft is transitioning to its assigned altitude.)
	 */
	public final String altitudeStatus;	
	public final int groundspeed;	
	
	/**
	 * TP=projected, TO=oceanic, TZ=radar, TA=broadcast
	 */
	public final String updateType;
	
	public CCTrackStruct(CCDataObject theData){
		super(theData.getFloat("longitude"), theData.getFloat("latitude"), theData.getInt("altitude") * 100, theData.getLong("timestamp") * 1000);
		altitudeChange = theData.getString("altitudeChange");
		altitudeStatus = theData.getString("altitudeStatus");

		groundspeed = theData.getInt("groundspeed");
		updateType = theData.getString("updateType");
	}
}
