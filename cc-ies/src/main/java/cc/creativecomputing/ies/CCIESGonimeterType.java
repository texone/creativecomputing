package cc.creativecomputing.ies;

/**
 * Photometric goniometer type
 * <p>
 * Luminaires are photometered by locating the luminaire at the center of an
 * imaginary sphere and measuring the light intensity (candela) values at
 * grid points (the "photometric web') on the sphere's surface. The
 * orientation of the luminaire's axes relative to that of the sphere
 * determines the photometric type.
 * <p>
 * It is important to note that these photometric types are *not* clearly
 * defined in IES LM-63. All three versions refer the reader to the IES
 * Lighting Handbook for descriptions of Type A and Type B photometry, and
 * to CIE 27-1973 ("Photometry of Luminaires for Street Lighting") and CIE
 * 43-1979 ("Photometry of Floodlights") for a description of Type C
 * photometry. It then says that "Type C is the form in common use in the
 * United States (although it was formerly referred to as Type A)."
 * <p>
 * This is in contrast to CIE Publication 102-1993, "Recommended File format
 * for Electronic Transfer of Luminaire Photometric Data," which clearly and
 * unambiguously defines three types of photometry: A (alpha), B (beta), and
 * C (gamma). The diagrams in CIE 102-1993 leave no doubt as to how their
 * photometric webs are oriented with respect to the luminaire.
 * <p>
 * Unfortunately, the IES LM-63 Type A photometry is equivalent to the CIE
 * 102-1993 Type C photometry, and the IES LM-63 Type C photometry is
 * equivalent to the CIE 102-1993 Type A photometry.
 * 
 * @author christianr
 *
 */
public enum CCIESGonimeterType {
	/**
	 * Type A photometry is normally used for automotive headlights and
	 * signal lights. The polar axis of the luminaire coincides with the
	 * major axis (length) of the luminaire, and the 0-180 degree
	 * photometric plane coinicides with the luminaire's vertical axis.
	 */
	TYPE_A(3),
	/**
	 * Type B photometry is normally used for adjustable outdoor area and
	 * sports lighting luminaires. The polar axis of the luminaire coincides
	 * with the minor axis (width) of the luminaire, and the 0-180 degree
	 * photometric plane coinicides with the luminaire's vertical axis.
	 */
	TYPE_B(2),
	/**
	 * Type C photometry is normally used for architectural and roadway
	 * luminaires. The polar axis of the photometric web coincides with the
	 * vertical axis of the luminaire, and the 0-180 degree photometric
	 * plane coincides with the luminaire's major axis (length).
	 */
	TYPE_C(1);

	int id;

	CCIESGonimeterType(int theID) {
		id = theID;
	}
	
	public static CCIESGonimeterType fromID(int id){
		switch(id){
		case 1:
			return CCIESGonimeterType.TYPE_C;
		case 2:
			return CCIESGonimeterType.TYPE_B;
		case 3:
			return CCIESGonimeterType.TYPE_A;
		}
		return CCIESGonimeterType.TYPE_C;
	}
}