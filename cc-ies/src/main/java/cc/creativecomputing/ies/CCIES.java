package cc.creativecomputing.ies;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.CCNIOUtil;

public class CCIES {
	/** Maximum label line width */
	public static int IE_MaxLabel = 80;
	/** Maximum non-label line width */
	public static int IE_MaxLine = 130;

	/*
	 * Calculated photometric data
	 */

	/** Invalid candela array index */
	public static int IE_INDEX_NONE = -1;
	/** Number of horizontal angles (0-180) */
	public static int IE_HORZ = 9;
	/** 90-degree horizontal angle */
	public static int IE_HORZ_90 = IE_HORZ / 2;
	/** Number of vertical angles (candela) */
	public static int IE_VERT_CAND = 37;
	/** 90-degree vertical angle */
	public static int IE_VERT_90 = IE_VERT_CAND / 2;
	/** 180-degree vertical angle */
	public static int IE_VERT_180 = IE_VERT_CAND - 1;
	/** Number of vertical angles (flux) */
	public static int IE_VERT_FLUX = 18;
	/** Vertical angle increment */
	public static double IE_V_ANGLE = 5.0;
	/** Horizontal angle increment */
	public static double IE_H_ANGLE = 22.5;
	/** Number of zones */
	public static int IE_ZONES = 9;

	/* Coefficients of utilization array dimensions */
	/** Room cavity ratios */
	public static int IE_CU_ROWS = 11;
	/** Ceiling/wall reflectances */
	public static int IE_CU_COLS = 18;

	private static float[] readFloats(String theLine) {
		String[] myValueStrings = theLine.split(" ");
		float[] myValues = new float[myValueStrings.length];
		for (int i = 0; i < myValues.length; i++) {
			myValues[i] = Float.parseFloat(myValueStrings[i]);
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
	 * @param fname
	 * @return
	 */
	public static CCIEData read(Path fname) {

		CCIEData pdata = new CCIEData();
		/* Save file name */
		if ((pdata.name = CCNIOUtil.fileName(fname)) == null) {
			throw new RuntimeException("Report memory allocation error");
		}

		/* Open the IESNA data file */
		List<String> _myLines = CCNIOUtil.loadStrings(fname);
		if (_myLines == null) {
			throw new RuntimeException("ERROR: could not open file " + fname);
		}

		int myLineCounter = 0;

		// Read the first line
		String myFormat = _myLines.get(myLineCounter++);

		// Determine file format
		switch (myFormat) {
		case "IESNA:LM-63-1995":
			// File is LM-63-1995 format
			pdata.format = CCIEDataFormat.IESNA_95;
			break;
		case "IESNA91":
			/* File is LM-63-1991 format */
			pdata.format = CCIEDataFormat.IESNA_91;
			break;
		default:
			/* File is presumably LM-63-1986 format */
			pdata.format = CCIEDataFormat.IESNA_86;
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
		pdata.lamp.tilt_fname = _myLines.get(myLineCounter++).substring(5);

		// Check for TILT data
		switch (pdata.lamp.tilt_fname) {
		case "NONE":
			break;
		case "INCLUDE":
			// Read the TILT data from the IESNA data file
			pdata.lamp.tilt = readTilt(_myLines.subList(myLineCounter, myLineCounter + 4));
			break;
		default:
			// Read the TILT data from the TILT data file
			pdata.lamp.tilt = readTilt(CCNIOUtil.loadStrings(CCNIOUtil.dataPath(pdata.lamp.tilt_fname)));
		}

		String[] myValueStrings = _myLines.get(myLineCounter + 5).split(" ");
		// Read in next two lines
		pdata.lamp.num_lamps = Integer.parseInt(myValueStrings[0]);
		pdata.lamp.lumens_lamp = Float.parseFloat(myValueStrings[1]);
		pdata.lamp.multiplier = Float.parseFloat(myValueStrings[2]);

		pdata.photo.num_vert_angles = Integer.parseInt(myValueStrings[3]);
		pdata.photo.num_horz_angles = Integer.parseInt(myValueStrings[4]);
		pdata.photo.gonio_type = CCIEGonimeterType.fromID(Integer.parseInt(myValueStrings[5]));
		pdata.units = CCIEMeasurementUnits.fromID(Integer.parseInt(myValueStrings[6]));
		pdata.dim.width = Float.parseFloat(myValueStrings[7]);
		pdata.dim.length = Float.parseFloat(myValueStrings[8]);
		pdata.dim.height = Float.parseFloat(myValueStrings[9]);

		myValueStrings = _myLines.get(myLineCounter + 6).split(" ");
		pdata.elec.ball_factor = Float.parseFloat(myValueStrings[0]);
		pdata.elec.blp_factor = Float.parseFloat(myValueStrings[1]);
		pdata.elec.input_watts = Float.parseFloat(myValueStrings[2]);

		// Read in vertical angles array
		pdata.photo.vert_angles = readFloats(_myLines.get(12));
		// Read in horizontal angles array
		pdata.photo.horz_angles = readFloats(_myLines.get(13));

		// Allocate space for the candela values array pointers
		pdata.photo.pcandela = new float[pdata.photo.num_horz_angles][pdata.photo.num_vert_angles];

		// Read in candela values arrays
		for (int i = 0; i < pdata.photo.num_horz_angles; i++) {

			// Read in candela values
			pdata.photo.pcandela[i] = readFloats(_myLines.get(13 + i));
		}

		return pdata;
	}
}
