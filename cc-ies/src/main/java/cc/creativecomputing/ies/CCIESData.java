package cc.creativecomputing.ies;

public class CCIESData {

	private final CCIESDataFormat _myFormat;
	private final CCIESMeasurementUnits _myUnits;

	private final CCIESDimensions _myDimensions;
	
	private final CCIESElectricalData _myElectricalData;

	private final CCIESPhotometricData _myPhotometricData;

	private final CCIESLamp _myLamp;

	

	/** Luminous cavity dimensions */


	/** Photometric data */

	CCIESData(
		CCIESDataFormat theFormat,
		CCIESMeasurementUnits theUnits,
		CCIESDimensions theDimensions,
		CCIESElectricalData theElectricalData,
		CCIESPhotometricData thePhotometricData,
		CCIESLamp theLamp
	) {
		_myFormat = theFormat;
		_myUnits = theUnits;
		_myDimensions = theDimensions;
		_myElectricalData = theElectricalData;
		_myPhotometricData = thePhotometricData;
		_myLamp = theLamp;
	}
	
	/**
	 * 
	 * @return
	 */
	public CCIESDataFormat format(){
		return _myFormat;
	}

	/**
	 * the units used for the dimensions of the luminous opening in the
	 * luminaire
	 * @return
	 */
	public CCIESMeasurementUnits units(){
		return _myUnits;
	}
	
	public CCIESDimensions dimensions(){
		return _myDimensions;
	}
	
	public CCIESElectricalData electricalData(){
		return _myElectricalData;
	}
	
	public CCIESPhotometricData photometricalData(){
		return _myPhotometricData;
	}
	
	public CCIESLamp lamp(){
		return _myLamp;
	}
}
