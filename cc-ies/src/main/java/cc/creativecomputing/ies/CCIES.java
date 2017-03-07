package cc.creativecomputing.ies;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

public class CCIES {
	/** Maximum label line width */
	public static int IE_MaxLabel = 80;
	/** Maximum non-label line width */
	public static int IE_MaxLine = 130;

	public static double PI = 3.141592654;

	/** Cosine lookup table (five degree increments from 0 to 180 degrees) */
	private static double IE_Cosine[] = { 1.000000, 0.996195, 0.984808, 0.965926, 0.939693, 0.906308, 0.866025,
			0.819152, 0.766044, 0.707107, 0.642788, 0.573576, 0.500000, 0.422618, 0.342020, 0.258819, 0.173648,
			0.087156, 0.000000, -0.087156, -0.173648, -0.258819, -0.342020, -0.422618, -0.500000, -0.573576, -0.642788,
			-0.707107, -0.766044, -0.819152, -0.866025, -0.906308, -0.939693, -0.965926, -0.984808, -0.996195,
			-1.000000 };

	

	private static double[] readFloats(String theLine) {
		String[] myValueStrings = theLine.split(" ");
		double[] myValues = new double[myValueStrings.length];
		for (int i = 0; i < myValues.length; i++) {
			myValues[i] = Double.parseDouble(myValueStrings[i]);
		}
		return myValues;
	}

	/**
	 * Read TILT Data From List
	 * 
	 * @param pdata
	 * @param theFile
	 * @return
	 */
	private static CCIETiltData readTilt(List<String> theFile) {
		CCIETiltData myResult = new CCIETiltData();
		// Get the lamp-to-luminaire geometry value
		myResult.orientation = CCIELampOrientation.fromID(Integer.parseInt(theFile.get(0)));

		// Get the number of angle-multiplying factor pairs value
		myResult.num_pairs = Integer.parseInt(theFile.get(1));

		if (myResult.num_pairs <= 0)
			return myResult;

		// Read in the angle values
		myResult.angles = readFloats(theFile.get(2));
		// Read in the multiplying factor values
		myResult.mult_factors = readFloats(theFile.get(3));

		return myResult;
	}

	/**
	 * Read IESNA-Format Photometric Data File
	 * 
	 * @param thePath
	 * @return CCIESDataObject
	 */
	public static CCIESData read(Path thePath) {
		String myName = CCNIOUtil.fileName(thePath);

		/* Open the IESNA data file */
		List<String> _myLines = CCNIOUtil.loadStrings(thePath);
		if (_myLines == null) {
			throw new RuntimeException("ERROR: could not open file " + thePath);
		}

		int myLineCounter = 0;

		// Read the first line
		CCIESDataFormat myFormat;

		// Determine file format
		switch (_myLines.get(myLineCounter++)) {
		case "IESNA:LM-63-1995":
			// File is LM-63-1995 format
			myFormat = CCIESDataFormat.IESNA_95;
			break;
		case "IESNA91":
			/* File is LM-63-1991 format */
			myFormat = CCIESDataFormat.IESNA_91;
			break;
		default:
			/* File is presumably LM-63-1986 format */
			myFormat = CCIESDataFormat.IESNA_86;
		}

		// Read label lines
		List<String> myLabelLines = new ArrayList<>();

		for (;;) {
			String myLabel = _myLines.get(myLineCounter++);
			// Check for "TILT" keyword indicating end of label lines
			if (myLabel.startsWith("TILT=")) {
				myLineCounter--;
				break;
			}
			myLabelLines.add(myLabel);
		}

		// Save the TILT data file name
		String myTileFileName = _myLines.get(myLineCounter++).substring(5);

		CCLog.info(myTileFileName);

		CCIETiltData myTiltData = null;

		// Check for TILT data
		switch (myTileFileName) {
		case "NONE":
			break;
		case "INCLUDE":
			// Read the TILT data from the IESNA data file
			myTiltData = readTilt(_myLines.subList(myLineCounter, myLineCounter + 4));
			myLineCounter += 4;
			break;
		default:
			// Read the TILT data from the TILT data file
			myTiltData = readTilt(CCNIOUtil.loadStrings(CCNIOUtil.dataPath(myTileFileName)));
		}

		String[] myValueStrings = _myLines.get(myLineCounter).split(" ");

		// Read in next two lines
		CCIESLamp myLamp = new CCIESLamp(Integer.parseInt(myValueStrings[0]), Float.parseFloat(myValueStrings[1]),
				Float.parseFloat(myValueStrings[2]), myTiltData);

		int myNumberOfVerticalAngles = Integer.parseInt(myValueStrings[3]);
		int myNumberOfHorizontalAngles = Integer.parseInt(myValueStrings[4]);
		CCIESGonimeterType myGonimeterType = CCIESGonimeterType.fromID(Integer.parseInt(myValueStrings[5]));

		CCIESMeasurementUnits myUnits = CCIESMeasurementUnits.fromID(Integer.parseInt(myValueStrings[6]));

		CCIESDimensions myDimensions = new CCIESDimensions(
			Float.parseFloat(myValueStrings[7]), // width
			Float.parseFloat(myValueStrings[8]), // length
			Float.parseFloat(myValueStrings[9]) // height
		);

		myValueStrings = _myLines.get(myLineCounter + 1).split(" ");

		CCIESElectricalData myElectricalData = new CCIESElectricalData(
			Float.parseFloat(myValueStrings[0]), // ball_factor
			Float.parseFloat(myValueStrings[1]), // blp_factor
			Float.parseFloat(myValueStrings[2]) // input_watts
		);

		// Read in vertical angles array
		double[] myVerticalAngles = readFloats(_myLines.get(myLineCounter + 2));
		// Read in horizontal angles array
		double[] myHorizontalAngles = readFloats(_myLines.get(myLineCounter + 3));

		// Allocate space for the candela values array pointers
		double[][] myCandelaValues = new double[myNumberOfHorizontalAngles][myNumberOfVerticalAngles];

		// Read in candela values arrays
		for (int i = 0; i < myNumberOfHorizontalAngles; i++) {

			// Read in candela values
			myCandelaValues[i] = readFloats(_myLines.get(myLineCounter + 4 + i));
		}

		CCIESPhotometricData myPhotometricData = new CCIESPhotometricData(
			myVerticalAngles, 
			myHorizontalAngles,
			myCandelaValues, 
			myGonimeterType
		);

		return new CCIESData(myFormat, myUnits, myDimensions, myElectricalData, myPhotometricData, myLamp);
	}
	
}
