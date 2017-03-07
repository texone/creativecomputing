package cc.creativecomputing.ies;

/** Calculated photometric data */
/*
 *************************************************************************
 *
 * IE_CalcData - Calculate Photometric Data
 *
 * Purpose: To calculate the luminaire photometric data from the IES
 * Standard File data.
 *
 * Setup: BOOL IE_CalcData ( IE_DATA *pdata, IE_CALC *pcalc )
 *
 * Where: pdata is a pointer to an IE_DATA data structure. pcalc is a
 * pointer to an IE_CALC data structure.
 *
 * Return: true if successful; otherwise false (insufficient photometric
 * data).
 *
 * Note: The following calculations are in accordance with:
 *
 * "IES Recommended Procedure for Calculating Coefficients of Utilization,
 * Wall and Ceiling Cavity Exitance", IES Publication LM-57
 *
 * The candela and lamp lumen values are multiplied by the value of
 * "multiplier".
 *
 * This function requires photometric measurements at vertical angle
 * increments of 5.0 degrees (e.g., 0.0 degrees, 5.0 degrees, 10.0 degrees,
 * ... ).
 *
 *************************************************************************
 */
public class CCIECalc {

	/** Invalid candela array index */
	private static int IE_INDEX_NONE = -1;

	/** Vertical angle increment */
	private static double IE_V_ANGLE = 5.0;
	
	/** Horizontal angle increment */
	private static double IE_H_ANGLE = 22.5;
	
	/** Number of horizontal angles (0-180) */
	private static int IE_HORZ = 9;
	
	/** Number of vertical angles (candela) */
	private static int IE_VERT_CAND = 37;

	private static double PI = 3.141592654;

	/** Number of vertical angles (flux) */
	private static int IE_VERT_FLUX = 18;
	
	/** Number of zones */
	private static int IE_ZONES = 9;
	
	/** 90-degree horizontal angle */
	private static int IE_HORZ_90 = IE_HORZ / 2;
	/** 90-degree vertical angle */
	private static int IE_VERT_90 = IE_VERT_CAND / 2;
	/** 180-degree vertical angle */
	private static int IE_VERT_180 = IE_VERT_CAND - 1;
	
	/** Cosine lookup table (five degree increments from 0 to 180 degrees) */
	private static double IE_Cosine[] = { 
		1.000000, 0.996195, 0.984808, 0.965926, 0.939693, 
		0.906308, 0.866025, 0.819152, 0.766044, 0.707107, 
		0.642788, 0.573576, 0.500000, 0.422618, 0.342020, 
		0.258819, 0.173648, 0.087156, 0.000000, 
		-0.087156, -0.173648, -0.258819, -0.342020, -0.422618, 
		-0.500000, -0.573576, -0.642788, -0.707107, -0.766044, 
		-0.819152, -0.866025, -0.906308, -0.939693, -0.965926, 
		-0.984808, -0.996195, -1.000000 
	};
	
	/* Zonal multiplier equation constants */
	private static double IE_A[] = { 0.000, 0.041, 0.070, 0.100, 0.136, 0.190, 0.315, 0.640, 2.100 };
	private static double IE_B[] = { 0.00, 0.98, 1.05, 1.12, 1.16, 1.25, 1.25, 1.25, 0.80 };

	/* Coefficients of utilization array dimensions */
	/** Room cavity ratios */
	public static int IE_CU_ROWS = 11;
	/** Ceiling/wall reflectances */
	public static int IE_CU_COLS = 18;

	/**
	 * Candlepower distribution NOTE: The candlepower distribution array is
	 * ordered as follows:
	 * 
	 * Horizontal: { 0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5, 180.0 }
	 * Vertical: { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75,
	 * 80, 85, 90, 95, 100, 105, 110, 115, 120, 125, 130, 135, 140, 145, 150,
	 * 155, 160, 165, 170, 175, 180 }
	 */
	long[][] candela = new long[IE_HORZ][IE_VERT_CAND];

	/**
	 * Valid horizontal angle index array
	 */
	int[] h_angle;
	boolean[] horz_flag;
	/** Valid vertical angle index array */
	int[] v_angle;
	boolean[] vert_flag;
	/** Number of horizontal angles */
	int horz_num;
	/** Number of vertical angles */
	int vert_num;

	/**
	 * Lumen distribution NOTE: The flux distribution arrays are ordered as
	 * follows:
	 * 
	 * { 5, 15, 25, 35, 45, 55, 65, 75, 85, 95, 105, 115, 125, 135, 145, 155,
	 * 165, 175 }
	 **/
	private long[] flux = new long[IE_VERT_FLUX];

	/**
	 * Zonal lumens NOTE: The zonal lumens, percent lamp lumens and percent
	 * fixture lumens arrays are ordered as follows:
	 * 
	 * { 0-30, 0-40, 0-60, 0-90, 90-120, 90-130, 90-150, 90-180, 0-180
	 **/
	long[] zonal_lm = new long[IE_ZONES];

	/** Percent lamp lumens */
	int[] lamp_pct = new int[IE_ZONES];

	/** Percent fixture lumens */
	int[] fixt_pct = new int[IE_ZONES];

	/** Luminaire efficiency */
	double efficiency;

	/** Total lamp lumens */
	private double total_lm;
	private long emit_lm = 0L; /* Initialize emitted lumens */

	/**
	 * CIE luminaire type classification
	 */
	CCIESType cie_type;

	
	/** Coefficient of utilization */
	double cu;
	
	/** Wall luminous exitance coefficient */
	double wec;
	
	/** Ceiling cavity luminous exitance coefficient */
	double ccec;
	
	/** Wall direct radiation coefficient */
	double wdrc;

	/** Coefficients of Utilization array */
	int[][] CU_Array = new int[IE_CU_ROWS][IE_CU_COLS];

	public CCIECalc(CCIESData theData){
		searchHorizontalAngles(theData);
		searchVerticalAngles(theData);
		calculateCandelaValues(theData);
		total_lm = calculateTotalLumens(theData);
		calculateZonalFluxValuesAndEfficiency();
		calculateZonalLumenSummary();
		calculateCuArray();
	}
	
	private double calculateTotalLumens(CCIESData theData){
		return theData.lamp().numberOfLamps() * theData.lamp().lumensPerLamp();
	}
	
	private void searchHorizontalAngles(CCIESData theData){
		/* Search for valid horizontal angles */
		/* Initialize the horizontal and vertical angle flags array */
		horz_flag = new boolean[IE_HORZ]; /* Valid horz angle flags */
		h_angle = new int[IE_HORZ];
		
		for (int i = 0; i < IE_HORZ; i++){
			horz_flag[i] = false;
			h_angle[i] = IE_INDEX_NONE;
		}
		
		horz_num = 0;

		for (int i = 0; i < IE_HORZ; i++){
			for (int j = 0; j < theData.photometricalData().horizontalAngles().length; j++){
				if (Math.abs(((double) i * IE_H_ANGLE) - (double) theData.photometricalData().horizontalAngles()[j]) < 1.0) {
					horz_num++;
					h_angle[i] = j;
					horz_flag[i] = true;
				}
			}
		}
	}
	
	private void searchVerticalAngles(CCIESData theData){
		vert_flag = new boolean[IE_VERT_CAND];
		v_angle = new int[IE_VERT_CAND];
		
		for (int i = 0; i < IE_VERT_CAND; i++){
			vert_flag[i] = false;
			v_angle[i] = IE_INDEX_NONE;
		}
		
		for (int i = 0; i < IE_VERT_CAND; i++){
			for (int j = 0; j < theData.photometricalData().verticalAngles().length; j++){
				if (Math.abs((i * IE_V_ANGLE) - (double) theData.photometricalData().verticalAngles()[j]) < 1.0) {
					v_angle[i] = j;
					vert_flag[i] = true;
				}
			}
		}
		
		/* Determine whether vertical angles exist at 5 degree increments */
		/* over range of 0 to 90 degrees */
		boolean vva_flag = true;
		for (int i = 0; i <= IE_VERT_90; i++) {
			if (vert_flag[i] == false) {
				vva_flag = false;
				break;
			}
		}

		if (vva_flag == false) {
			/* Determine whether vertical angles exist at 5 degree increments */
			/* over range of 90 to 180 degrees */
			vva_flag = true;
			for (int i = IE_VERT_90; i <= IE_VERT_180; i++){
				if (vert_flag[i] == false)
					return; /* Insufficient photometric data */
			}
		}
	}
	
	private void calculateCandelaValues(CCIESData theData){
		/* Clear the candela values array */
		for (int i = 0; i < IE_HORZ; i++)
			for (int j = 0; j < IE_VERT_CAND; j++)
				candela[i][j] = 0L;

		
		/* Add the candela values for valid angles */
		double[][] pcandela = theData.photometricalData().candelaValues();

		for (int i = 0; i < IE_HORZ; i++)
			if (horz_flag[i])
				for (int j = 0; j < IE_VERT_CAND; j++)
					if (vert_flag[j])
						candela[i][j] += (long) (pcandela[h_angle[i]][v_angle[j]] * theData.lamp().multiplier());
	}
	
	private long[] calculateCandelaAverages(){
		/* Calculate the average candela values */
		long[] avg_candela = new long[IE_VERT_CAND]; 
		for (int i = 0; i < IE_VERT_CAND; i++){
			if (v_angle[i] != IE_INDEX_NONE) {
				avg_candela[i] = 0L;

				for (int j = 0; j < IE_HORZ; j++)
					if (h_angle[j] != IE_INDEX_NONE)
						avg_candela[i] += candela[j][i];

				avg_candela[i] /= (long) horz_num;
			}
		}
		return avg_candela;
	}
	
	private void calculateZonalFluxValuesAndEfficiency(){
		emit_lm = 0L; /* Initialize emitted lumens */
		long[] avg_candela = calculateCandelaAverages();
		/* Calculate the zonal flux values */
		for (int i = 0; i < IE_VERT_FLUX; i++) {
			int j = 2 * i + 1;

			if (v_angle[j] != IE_INDEX_NONE) {
				flux[i] = (long) (2.0 * PI * avg_candela[j] * (IE_Cosine[j - 1] - IE_Cosine[j + 1]));

				emit_lm += flux[i]; /* Update emitted lumens */
			} else
				flux[i] = 0L;
		}

		/* Calculate the luminaire efficiency */
		efficiency = (double) (emit_lm * 100L) / total_lm;
	}
	
	private void calculateZonalLumenSummary(){
		/* Calculate the zonal lumen summary */

		/* 0-30 degree zone */
		zonal_lm[0] = flux[0] + flux[1] + flux[2];
		/* 0-40 degree zone */
		zonal_lm[1] = zonal_lm[0] + flux[3];
		/* 0-60 degree zone */
		zonal_lm[2] = zonal_lm[1] + flux[4] + flux[5];
		/* 0-90 degree zone */
		zonal_lm[3] = zonal_lm[2] + flux[6] + flux[7] + flux[8];
		/* 90-120 degree zone */
		zonal_lm[4] = flux[9] + flux[10] + flux[11];
		/* 90-130 degree zone */
		zonal_lm[5] = zonal_lm[4] + flux[12];
		/* 90-150 degree zone */
		zonal_lm[6] = zonal_lm[5] + flux[13] + flux[14];
		/* 90-180 degree zone */
		zonal_lm[7] = zonal_lm[6] + flux[15] + flux[16] + flux[17];
		/* 0 - 180 degree zone */
		zonal_lm[8] = zonal_lm[3] + zonal_lm[7];
		
		for(int i = 0; i < IE_ZONES;i++){
			lamp_pct[i] = (int) ((zonal_lm[i] * 100L) / total_lm);
			fixt_pct[i] = (int) ((zonal_lm[i] * 100L) / emit_lm);
		}
	}
	
	private void determine(){
		/* Determine the CIE luminaire type */
		if (fixt_pct[7] < 10)
			cie_type = CCIESType.IE_CIE_1; /* Direct */
		else if (fixt_pct[7] < 40)
			cie_type = CCIESType.IE_CIE_2; /* Semi-direct */
		else if (fixt_pct[7] < 60)
			cie_type = CCIESType.IE_CIE_3; /* General diffuse */
		else if (fixt_pct[7] < 90)
			cie_type = CCIESType.IE_CIE_4; /* Semi-indirect */
		else
			cie_type = CCIESType.IE_CIE_5; /* Indirect */
	}
	
	/*
	 *************************************************************************
	 *
	 * IE_CalcCU - Calculate Luminaire Coefficient of Utilization
	 *
	 * Purpose: To calculate the zonal cavity coefficient of utilization for a
	 * luminaire.
	 *
	 * Setup: double IE_CalcCU ( IE_CALC *double g, double p1, double p2,
	 * double p3 )
	 *
	 * Where: pcalc is a pointer to an IE_CALC data structure. g is the room
	 * cavity ratio. p1 is the wall cavity reflectance (0.001 to 0.999). p2 is
	 * the effective ceiling cavity reflectance (0.000 to 0.999). p3 is the
	 * effective floor cavity reflectance (0.000 to 0.999).
	 *
	 * Return: The calculated coefficient of utilization if the input parameters
	 * are within range; otherwise 0.0.
	 *
	 * Note: The following calculations are in accordance with:
	 *
	 * "IES Recommended Procedure for Calculating Coefficients of Utilization,
	 * Wall and Ceiling Cavity Exitance", IES Publication LM-57.
	 *
	 *************************************************************************
	 */

	private double IE_CalcCU(double g, double p1, double p2, double p3) {
		
		/* Check for conditions which could cause a divide-by-zero error */
		if (p1 > 0.999 || p2 > 0.999 || p3 > 0.999)
			return 0.0;

		/* Calculate the flux functions */
		double phi_d = 0.0; /* Total downward luminaire flux */
		for (int n = 0; n < IE_VERT_FLUX / 2; n++)
			phi_d += (double) flux[n];

		double phi_u = 0.0; /* Total upward luminaire flux */
		for (int n = IE_VERT_FLUX / 2; n < IE_VERT_FLUX; n++)
			phi_u += (double) flux[n];

		phi_d /= (double) total_lm;
		phi_u /= (double) total_lm;

		if (g < 0.001) {
			/* Calculate the coefficient of utilization */
			return (phi_d + p2 * phi_u) / (1.0 - p2 * p3);
		} 
		
		/* Calculate the luminaire direct ratio */
		double Dg = 0.0; /* Luminaire direct ratio */
		for (int n = 0; n < IE_VERT_FLUX / 2; n++)
			Dg += Math.exp(-IE_A[n] * Math.pow(g, IE_B[n])) * (double) flux[n];

		if (phi_d > 0.001)
			Dg /= (phi_d * (double) total_lm);

		/* Calculate the form factor approximation */
		double f23 = 0.026 + 0.503 * Math.exp(-0.270 * g) + 0.470 * Math.exp(-0.119 * g);

		/* Calculate the intermediate calculation parameters */
		double C1 = (1.0 - p1) * (1.0 - f23 * f23) * g / (2.5 * p1 * (1.0 - f23 * f23) + g * f23 * (1.0 - p1));
		double C2 = (1.0 - p2) * (1.0 + f23) / (1.0 + p2 * f23);
		double C3 = (1.0 - p3) * (1.0 + f23) / (1.0 + p3 * f23);
		double C0 = C1 + C2 + C3;

		/* Calculate the coefficient of utilization */
		return 2.5 * p1 * C1 * C3 * (1.0 - Dg) * phi_d / (g * (1.0 - p1) * (1.0 - p3) * C0)
				+ p2 * C2 * C3 * phi_u / ((1.0 - p2) * (1.0 - p3) * C0)
				+ (1.0 - p3 * C3 * (C1 + C2) / ((1.0 - p3) * C0)) * Dg * phi_d / (1.0 - p3);
		
	}

	/*
	 *************************************************************************
	 *
	 * IE_CalcCU_Array - Calculate Coefficients of Utilization Array
	 *
	 * Purpose: To calculate the coefficients of utilization for the selected
	 * product.
	 *
	 * Setup: static void IE_CalcCU_Array ( IE_CALC *pcalc )
	 *
	 * Where: pcalc is a pointer to an IE_CALC data structure.
	 *
	 *************************************************************************
	 */

	private void calculateCuArray() {

		for (int i = 0; i < IE_CU_ROWS; i++) {
			CU_Array[i][0] = (int) (IE_CalcCU((double) i, 0.70, 0.80, 0.20) * 100.0);
			CU_Array[i][1] = (int) (IE_CalcCU((double) i, 0.50, 0.80, 0.20) * 100.0);
			CU_Array[i][2] = (int) (IE_CalcCU((double) i, 0.30, 0.80, 0.20) * 100.0);
			CU_Array[i][3] = (int) (IE_CalcCU((double) i, 0.10, 0.80, 0.20) * 100.0);
			CU_Array[i][4] = (int) (IE_CalcCU((double) i, 0.70, 0.70, 0.20) * 100.0);
			CU_Array[i][5] = (int) (IE_CalcCU((double) i, 0.50, 0.70, 0.20) * 100.0);
			CU_Array[i][6] = (int) (IE_CalcCU((double) i, 0.30, 0.70, 0.20) * 100.0);
			CU_Array[i][7] = (int) (IE_CalcCU((double) i, 0.10, 0.70, 0.20) * 100.0);
			CU_Array[i][8] = (int) (IE_CalcCU((double) i, 0.50, 0.50, 0.20) * 100.0);
			CU_Array[i][9] = (int) (IE_CalcCU((double) i, 0.30, 0.50, 0.20) * 100.0);
			CU_Array[i][10] = (int) (IE_CalcCU((double) i, 0.10, 0.50, 0.20) * 100.0);
			CU_Array[i][11] = (int) (IE_CalcCU((double) i, 0.50, 0.30, 0.20) * 100.0);
			CU_Array[i][12] = (int) (IE_CalcCU((double) i, 0.30, 0.30, 0.20) * 100.0);
			CU_Array[i][13] = (int) (IE_CalcCU((double) i, 0.10, 0.30, 0.20) * 100.0);
			CU_Array[i][14] = (int) (IE_CalcCU((double) i, 0.50, 0.10, 0.20) * 100.0);
			CU_Array[i][15] = (int) (IE_CalcCU((double) i, 0.30, 0.10, 0.20) * 100.0);
			CU_Array[i][16] = (int) (IE_CalcCU((double) i, 0.10, 0.10, 0.20) * 100.0);
			CU_Array[i][17] = (int) (IE_CalcCU((double) i, 0.0, 0.0, 0.20) * 100.0);
		}
	}
	/*
	 *************************************************************************
	 *
	 *  IE_CalcCoeff - Calculate Zonal Cavity Luminaire Coefficients
	 *
	 *  Purpose:    To calculate the zonal cavity coefficients of a luminaire.
	 *
	 *  Setup:      BOOL IE_CalcCoeff
	 *              (
	 *                IE_CALC *pcalc,
	 *                double g,
	 *                double p1,
	 *                double p2,
	 *                double p3
	 *              )
	 *
	 *  Where:      pcalc is a pointer to an IE_CALC data structure.
	 *              g .
	 *              p1 is the 
	 *              p2 is the 
	 *              p3 is the 
	 *
	 *  Return:     TRUE if successful; otherwise FALSE (input parameters out
	 *              of range).
	 *
	 *  Result:     The structure pointed to by "pcalc" is modified.
	 *
	 *  Note:       The following calculations are in accordance with:
	 *
	 *                "IES Recommended Procedure for Calculating Coefficients
	 *                of Utilization, Wall and Ceiling Cavity Exitance", IES
	 *                Publication LM-57.
	 *
	 *************************************************************************
	 */
	
	/**
	 * Calculate the zonal cavity coefficients of a luminaire.
	 * @param g is the room cavity ratio
	 * @param p1 wall cavity reflectance (0.001 to 0.999).
	 * @param p2 effective ceiling cavity reflectance (0.000 to 0.999).
	 * @param p3 effective floor cavity reflectance (0.000 to 0.999).
	 */
	public void calculateCoefficients(double g, double p1, double p2, double p3) {

		/* Check for conditions which could cause a divide-by-zero error */
		if (p1 > 0.999 || p2 > 0.999 || p3 > 0.999) {
			cu = 0.0;
			ccec = 0.0;
			wec = 0.0;
			wdrc = 0.0;
			return;
		}

		/* Calculate the flux functions */
		double phi_d = 0.0; /* Total downward luminaire flux */
		for (int n = 0; n < IE_VERT_FLUX / 2; n++)
			phi_d += (double) flux[n];

		double phi_u = 0.0; /* Total upward luminaire flux */
		for (int n = IE_VERT_FLUX / 2; n < IE_VERT_FLUX; n++)
			phi_u += (double) flux[n];
		phi_d /= (double) total_lm;
		phi_u /= (double) total_lm;

		if (g < 0.001) {
			/* Calculate the coefficient of utilization */
			cu = (phi_d + p2 * phi_u) / (1.0 - p2 * p3);

			/* Calculate the ceiling cavity luminous exitance coefficient */
			ccec = p2 * (phi_u + p3 * phi_d) / (1.0 - p2 * p3);

			wec = 0.0;
			wdrc = 0.0;
			return;
		}

		/* Calculate the luminaire direct ratio */
		double Dg = 0.0; /* Luminaire direct ratio */
		for (int n = 0; n < IE_VERT_FLUX / 2; n++)
			Dg += Math.exp(-IE_A[n] * Math.pow(g, IE_B[n])) * (double) flux[n];

		if (phi_d > 0.001)
			Dg /= (phi_d * (double) total_lm);

		/* Calculate the form factor approximation */
		double f23 = 0.026 + 0.503 * Math.exp(-0.270 * g) + 0.470 * Math.exp(-0.119 * g);

		/* Calculate the intermediate calculation parameters */
		double C1 = (1.0 - p1) * (1.0 - f23 * f23) * g / (2.5 * p1 * (1.0 - f23 * f23) + g * f23 * (1.0 - p1));
		double C2 = (1.0 - p2) * (1.0 + f23) / (1.0 + p2 * f23);
		double C3 = (1.0 - p3) * (1.0 + f23) / (1.0 + p3 * f23);
		double C0 = C1 + C2 + C3;

		/* Calculate the coefficient of utilization */
		cu = 2.5 * p1 * C1 * C3 * (1.0 - Dg) * phi_d / (g * (1.0 - p1) * (1.0 - p3) * C0)
				+ p2 * C2 * C3 * phi_u / ((1.0 - p2) * (1.0 - p3) * C0)
				+ (1.0 - p3 * C3 * (C1 + C2) / ((1.0 - p3) * C0)) * Dg * phi_d / (1.0 - p3);

		/* Calculate the ceiling cavity luminous exitance coefficient */
		ccec = 2.5 * p1 * p2 * C1 * C2 * (1.0 - Dg) * phi_d / (g * (1.0 - p1) * (1.0 - p2) * C0)
				+ (p2 * phi_u / (1.0 - p2)) * (1.0 - p2 * C2 * (C1 + C3) / ((1.0 - p2) * C0))
				+ p2 * p3 * C2 * C3 * Dg * phi_d / ((1.0 - p2) * (1.0 - p3) * C0);

		/* Calculate the wall luminous exitance coefficient */
		wec = 2.5 / g
				* (p1 * (1.0 - Dg) * phi_d / (1.0 - p1) * (1.0 - 2.5 * p1 * C1 * (C2 + C3) / (g * (1.0 - p1) * C0))
						+ p1 * p2 * C1 * C2 * phi_u / ((1.0 - p1) * (1.0 - p2) * C0)
						+ p1 * p3 * C1 * C3 * Dg * phi_d / ((1.0 - p1) * (1.0 - p3) * C0));

		/* Calculate the wall direct radiation coefficient */
		wdrc = 2.5 * phi_d * (1.0 - Dg) / g;

	}
	
	public CCIESType cieType(){
		return cie_type;
	}
}
