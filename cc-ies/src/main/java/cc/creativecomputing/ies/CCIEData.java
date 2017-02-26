package cc.creativecomputing.ies;

public class CCIEData {

	String name;

	CCIEDataFormat format;

	/** lamp data */
	CCIELamp lamp;

	/**
	 * the units used for the dimensions of the luminous opening in the
	 * luminaire
	 */
	CCIEMeasurementUnits units;

	/** Luminous cavity dimensions */
	CCIEDimensions dim;

	/** Electrical data */
	CCIEElectricalData elec;

	/** Photometric data */
	CCIEPhotometricData photo;

	public CCIEData() {
		/* Initialize the photometric data structure */
		// plline = null;
		lamp.tilt_fname = null;
		lamp.tilt.angles = null;
		lamp.tilt.mult_factors = null;
		photo.vert_angles = null;
		photo.horz_angles = null;
		photo.pcandela = null;
	}
}
