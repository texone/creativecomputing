package cc.creativecomputing.ies;

/** Calculated photometric data */
public class CCIECalc {

	/**
	 * Candlepower distribution NOTE: The candlepower distribution array is
	 * ordered as follows:
	 * 
	 * Horizontal: { 0.0, 22.5, 45.0, 67.5, 90.0, 112.5, 135.0, 157.5, 180.0 }
	 * Vertical: { 0, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50, 55, 60, 65, 70, 75,
	 * 80, 85, 90, 95, 100, 105, 110, 115, 120, 125, 130, 135, 140, 145, 150,
	 * 155, 160, 165, 170, 175, 180 }
	 */
	long[][] candela = new long[CCIES.IE_HORZ][CCIES.IE_VERT_CAND];

	/**
	 * Valid horizontal angle index array
	 */
	int[] h_angle = new int[CCIES.IE_HORZ];
	/** Valid vertical angle index array */
	int[] v_angle = new int[CCIES.IE_VERT_CAND];
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
	long[] flux = new long[CCIES.IE_VERT_FLUX];

	/**
	 * Zonal lumens NOTE: The zonal lumens, percent lamp lumens and percent
	 * fixture lumens arrays are ordered as follows:
	 * 
	 * { 0-30, 0-40, 0-60, 0-90, 90-120, 90-130, 90-150, 90-180, 0-180
	 **/
	long[] zonal_lm = new long[CCIES.IE_ZONES];

	/** Percent lamp lumens */
	int[] lamp_pct = new int[CCIES.IE_ZONES];

	/** Percent fixture lumens */
	int[] fixt_pct = new int[CCIES.IE_ZONES];

	/** Luminaire efficiency */
	double efficiency;

	/** Total lamp lumens */
	double total_lm;

	/**
	 * CIE luminaire type classification
	 */
	int cie_type;

	
	/** Coefficient of utilization */
	double cu;
	
	/** Wall luminous exitance coefficient */
	double wec;
	
	/** Ceiling cavity luminous exitance coefficient */
	double ccec;
	
	/** Wall direct radiation coefficient */
	double wdrc;

	/** Coefficients of Utilization array */
	int[][] IE_CU_Array = new int[CCIES.IE_CU_ROWS][CCIES.IE_CU_COLS];

}
