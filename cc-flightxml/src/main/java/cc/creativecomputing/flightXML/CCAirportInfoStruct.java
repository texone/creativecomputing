package cc.creativecomputing.flightXML;

import cc.creativecomputing.io.data.CCDataObject;

public class CCAirportInfoStruct {
	public final float latitude;
	public final String location;
	public final float longitude;
	public final String name;
	public final String timezone;
	
	public CCAirportInfoStruct(CCDataObject theData){
		latitude = theData.getFloat("latitude");
		longitude = theData.getFloat("longitude");
		location = theData.getString("location");
		name = theData.getString("name");
		timezone = theData.getString("timezone").replace(":", "");
	}

}
