package cc.creativecomputing.geo;

public class CCGeoLocation {

	public int altitude;
	public final float latitude;	
	public final float longitude;	
	
	public CCGeoLocation(float theLongitude, float theLatitude, int theAltitude){
		longitude = theLongitude;
		latitude = theLatitude;
		altitude = theAltitude;
	}
}
