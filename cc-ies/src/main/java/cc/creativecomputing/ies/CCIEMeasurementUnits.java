package cc.creativecomputing.ies;

/**
 * the units used for the dimensions of the luminous opening in the
 * luminaire
 * 
 * @author christianr
 *
 */
public enum CCIEMeasurementUnits {
	/** Imperial */
	FEET(1),
	/** Standard Internationale */
	METERS(2);
	int id;

	private CCIEMeasurementUnits(int theID) {
		id = theID;
	}
	
	public static CCIEMeasurementUnits fromID(int id){
		switch(id){
		case 1:
			return CCIEMeasurementUnits.FEET;
		case 2:
			return CCIEMeasurementUnits.METERS;
		}
		return CCIEMeasurementUnits.METERS;
	}
}