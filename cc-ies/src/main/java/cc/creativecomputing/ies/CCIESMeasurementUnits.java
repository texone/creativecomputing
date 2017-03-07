package cc.creativecomputing.ies;

/**
 * the units used for the dimensions of the luminous opening in the
 * luminaire
 * 
 * @author christianr
 *
 */
public enum CCIESMeasurementUnits {
	/** Imperial */
	FEET(1),
	/** Standard Internationale */
	METERS(2);
	int id;

	private CCIESMeasurementUnits(int theID) {
		id = theID;
	}
	
	public static CCIESMeasurementUnits fromID(int id){
		switch(id){
		case 1:
			return CCIESMeasurementUnits.FEET;
		case 2:
			return CCIESMeasurementUnits.METERS;
		}
		return CCIESMeasurementUnits.METERS;
	}
}