package cc.creativecomputing.geo;


public class CCGeoTrackPoint extends CCGeoLocation{
	
	public final long timeStamp;

	public CCGeoTrackPoint(float theLongitude, float theLatitude, int theAltitude, long theTimeStamp) {
		super(theLongitude, theLatitude, theAltitude);
		timeStamp = theTimeStamp;
	}

}
