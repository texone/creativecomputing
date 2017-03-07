package cc.creativecomputing.ies;

/**
 * File Format Identifier
 * 
 * IES LM-63-1991 and LM-63-1995 photometric data files begin with a unique
 * file format identifier line, namely "IESNA91" or "IESNA:LM-63-1995".
 * 
 * IES LM-63-1986 does not have a file format identifier line.
 * 
 * @author christianr
 *
 */
public enum CCIEDataFormat {
	/** LM-63-1986 */
	IESNA_86,
	/** LM-63-1991 */
	IESNA_91,
	/** LM-63-1995 */
	IESNA_95
}