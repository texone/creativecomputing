package cc.creativecomputing.ies;

/**
 * indicates the orientation of the lamp within the luminaire
 * 
 * @author christianr
 *
 */
public enum CCIELampOrientation {
	/**
	 * Lamp base is either vertical base up or vertical base down when the
	 * luminaire is aimed straight down.
	 */
	VERTICAL(1),
	/**
	 * Lamp is horizontal and remains horizontal when the luminaire is aimed
	 * straight down or rotated about the zero-degree horizontal plane.
	 */
	HORIZONTAL(2),
	/**
	 * Lamp is horizontal when the luminaire is pointed straight down, but
	 * does not remains horizontal when the luminaire is rotated about the
	 * zero-degree horizontal plane.
	 */
	TILTED(3);

	int id;

	CCIELampOrientation(int theID) {
		id = theID;
	}
	
	public static CCIELampOrientation fromID(int id){
		switch(id){
		case 1:
			return CCIELampOrientation.VERTICAL;
		case 2:
			return CCIELampOrientation.HORIZONTAL;
		case 3:
			return CCIELampOrientation.TILTED;
		}
		return CCIELampOrientation.TILTED;
	}
}