 /*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.model.svg;

import java.nio.file.Path;
import java.util.HashMap;

import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32;

public class CCSVGIO {
	
	/**
	 * Parse a size that may have a suffix for its units. Ignoring cases where
	 * this could also be a percentage. The <A
	 * HREF="http://www.w3.org/TR/SVG/coords.html#Units">units</A> spec:
	 * <UL>
	 * <LI>"1pt" equals "1.25px" (and therefore 1.25 user units)
	 * <LI>"1pc" equals "15px" (and therefore 15 user units)
	 * <LI>"1mm" would be "3.543307px" (3.543307 user units)
	 * <LI>"1cm" equals "35.43307px" (and therefore 35.43307 user units)
	 * <LI>"1in" equals "90px" (and therefore 90 user units)
	 * </UL>
	 */
	static protected double parseUnitSize(String text) {
		int len = text.length() - 2;

		if (text.endsWith("pt")) {
			return Double.parseDouble(text.substring(0, len)) * 1.25f;
		} else if (text.endsWith("pc")) {
			return Double.parseDouble(text.substring(0, len)) * 15;
		} else if (text.endsWith("mm")) {
			return Double.parseDouble(text.substring(0, len)) * 3.543307f;
		} else if (text.endsWith("cm")) {
			return Double.parseDouble(text.substring(0, len)) * 35.43307f;
		} else if (text.endsWith("in")) {
			return Double.parseDouble(text.substring(0, len)) * 90;
		} else if (text.endsWith("px")) {
			return Double.parseDouble(text.substring(0, len));
		} else if (text.endsWith("%")) {
			return Double.parseDouble(text.substring(0, text.length() - 1));
		} else {
			return Double.parseDouble(text);
		}
	}
	
	
	/**
	 * Used in place of element.getDoubleAttribute(a) because we can have a unit
	 * suffix (length or coordinate).
	 * 
	 * @param element
	 *            what to parse
	 * @param attribute
	 *            name of the attribute to get
	 * @return unit-parsed version of the data
	 */
	static protected double getDoubleWithUnit(CCDataElement element, String attribute) {
		String val = element.attribute(attribute);
		return (val == null) ? 0 : parseUnitSize(val);
	}
	
	static protected HashMap<String, String> parseStyleAttributes(String style) {
		HashMap<String, String> table = new HashMap<String, String>();
		String[] pieces = style.split(";");
		for (int i = 0; i < pieces.length; i++) {
			String[] parts = pieces[i].split(":");
			table.put(parts[0], parts[1]);
		}
		return table;
	}
	
	static protected CCMatrix32 parseSingleTransform(String matrixStr) {
		// String[] pieces = PApplet.match(matrixStr,
		// "^\\s*(\\w+)\\((.*)\\)\\s*$");
		String[] pieces = CCStringUtil.match(matrixStr, "[,\\s]*(\\w+)\\((.*)\\)");
		if (pieces == null) {
			System.err.println("Could not parse transform " + matrixStr);
			return null;
		}
		String[] m = CCStringUtil.splitTokens(pieces[2], ", ");
		if (pieces[1].equals("matrix")) {
			return new CCMatrix32(
				Double.parseDouble(m[0]), 
				Double.parseDouble(m[2]), 
				Double.parseDouble(m[4]), 
				Double.parseDouble(m[1]), 
				Double.parseDouble(m[3]), 
				Double.parseDouble(m[5])
			);
		} else if (pieces[1].equals("translate")) {
			double tx = Double.parseDouble(m[0]);
			double ty = (m.length == 2) ? Double.parseDouble(m[1]) : Double.parseDouble(m[0]);
			// return new double[] { 1, 0, tx, 0, 1, ty };
			return new CCMatrix32(1, 0, tx, 0, 1, ty);

		} else if (pieces[1].equals("scale")) {
			double sx = Double.parseDouble(m[0]);
			double sy = (m.length == 2) ? Double.parseDouble(m[1]) : Double.parseDouble(m[0]);
			// return new double[] { sx, 0, 0, 0, sy, 0 };
			return new CCMatrix32(sx, 0, 0, 0, sy, 0);

		} else if (pieces[1].equals("rotate")) {
			double angle = Double.parseDouble(m[0]);

			if (m.length == 1) {
				double c = CCMath.cos(angle);
				double s = CCMath.sin(angle);
				// SVG version is cos(a) sin(a) -sin(a) cos(a) 0 0
				return new CCMatrix32(c, -s, 0, s, c, 0);

			} else if (m.length == 3) {
				CCMatrix32 mat = new CCMatrix32(0, 1, Double.parseDouble(m[1]), 1, 0, Double.parseDouble(m[2]));
				mat.rotate(Double.parseDouble(m[0]));
				mat.translate(-Double.parseDouble(m[1]), -Double.parseDouble(m[2]));
				return mat; // .get(null);
			}

		} else if (pieces[1].equals("skewX")) {
			return new CCMatrix32(1, 0, 1, CCMath.tan(Double.parseDouble(m[0])), 0, 0);

		} else if (pieces[1].equals("skewY")) {
			return new CCMatrix32(1, 0, 1, 0, CCMath.tan(Double.parseDouble(m[0])), 0);
		}
		return null;
	}

	/**
	 * Parse the specified SVG matrix into a CCMatrix32. Note that CCMatrix32 is
	 * rotated relative to the SVG definition, so parameters are rearranged
	 * here. More about the transformation matrices in <a
	 * href="http://www.w3.org/TR/SVG/coords.html#TransformAttribute">this
	 * section</a> of the SVG documentation.
	 * 
	 * @param matrixStr
	 *            text of the matrix param.
	 * @return a good old-fashioned CCMatrix32
	 */
	static protected CCMatrix32 parseTransform(String matrixStr) {
		matrixStr = matrixStr.trim();
		CCMatrix32 outgoing = null;
		int start = 0;
		int stop = -1;
		while ((stop = matrixStr.indexOf(')', start)) != -1) {
			CCMatrix32 m = parseSingleTransform(matrixStr.substring(start, stop + 1));
			if (outgoing == null) {
				outgoing = m;
			} else {
				outgoing.apply(m);
			}
			start = stop + 1;
		}
		return outgoing;
	}

	static protected String writeMatrix(CCMatrix32 theMatrix) {
		StringBuffer myResult = new StringBuffer("matrix(");
		myResult.append(theMatrix.m00 + " ");
		myResult.append(theMatrix.m10 + " ");
		myResult.append(theMatrix.m01 + " ");
		myResult.append(theMatrix.m11 + " ");
		myResult.append(theMatrix.m02 + " ");
		myResult.append(theMatrix.m12 + ")");
		return myResult.toString();
	}
	
	
	private CCSVGDocument _myDocument;

	private CCSVGDocument readSVG(CCDataElement theSVG){
		if (!theSVG.name().equals("svg")) {
			throw new RuntimeException("root is not <svg>, it's <" + theSVG.name() + ">");
		}
		
		_myDocument = new CCSVGDocument();
		_myDocument.read(theSVG);

		return _myDocument;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
	

	
	
	
	
	private static CCSVGIO _myReader;
	
	public static CCSVGDocument newSVG(final Path theSVG){
		if(_myReader == null){
			_myReader = new CCSVGIO();
		}
		return _myReader.readSVG(CCXMLIO.createXMLElement(theSVG));
	}
}
