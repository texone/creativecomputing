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

import java.awt.Paint;
import java.awt.PaintContext;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCArrayUtil;
import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.CCStrokeCap;
import cc.creativecomputing.model.CCStrokeJoin;
import cc.creativecomputing.model.svg.CCSVGElement.CCShapeFamily;
import cc.creativecomputing.model.svg.CCSVGElement.CCShapeKind;

public class CCSVGIONew {
	
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
	static protected double parseUnitSize(String theText) {
		int myLength = theText.length() - 2;

		if (theText.endsWith("pt")) {
			return Double.parseDouble(theText.substring(0, myLength)) * 1.25f;
		} else if (theText.endsWith("pc")) {
			return Double.parseDouble(theText.substring(0, myLength)) * 15;
		} else if (theText.endsWith("mm")) {
			return Double.parseDouble(theText.substring(0, myLength)) * 3.543307f;
		} else if (theText.endsWith("cm")) {
			return Double.parseDouble(theText.substring(0, myLength)) * 35.43307f;
		} else if (theText.endsWith("in")) {
			return Double.parseDouble(theText.substring(0, myLength)) * 90;
		} else if (theText.endsWith("px")) {
			return Double.parseDouble(theText.substring(0, myLength));
		} else {
			return Double.parseDouble(theText);
		}
	}
	
	
	/**
	 * Used in place of element.getDoubleAttribute(a) because we can have a unit
	 * suffix (length or coordinate).
	 * 
	 * @param theDataObject
	 *            what to parse
	 * @param theKey
	 *            name of the attribute to get
	 * @return unit-parsed version of the data
	 */
	static protected double getDoubleWithUnit(CCDataObject theDataObject, String theKey) {
		String myValue = theDataObject.getString(theKey);
		return (myValue == null) ? 0 : parseUnitSize(myValue);
	}
	
	static protected HashMap<String, String> parseStyleAttributes(String theStyle) {
		HashMap<String, String> myResult = new HashMap<String, String>();
		String[] myPieces = theStyle.split(";");
		for (String myPiece:myPieces) {
			String[] parts = myPiece.split(":");
			myResult.put(parts[0], parts[1]);
		}
		return myResult;
	}
	
	static protected CCMatrix32 parseSingleTransform(String theMatrixString) {
		// String[] pieces = PApplet.match(matrixStr,
		// "^\\s*(\\w+)\\((.*)\\)\\s*$");
		String[] pieces = CCStringUtil.match(theMatrixString, "[,\\s]*(\\w+)\\((.*)\\)");
		if (pieces == null) {
			System.err.println("Could not parse transform " + theMatrixString);
			return null;
		}
		String[] m = CCStringUtil.splitTokens(pieces[2], ", ");
		switch(pieces[1]){
		case "matrix":
			return new CCMatrix32(
				Double.parseDouble(m[0]), 
				Double.parseDouble(m[2]), 
				Double.parseDouble(m[4]), 
				Double.parseDouble(m[1]), 
				Double.parseDouble(m[3]), 
				Double.parseDouble(m[5])
			);
		case "translate":
			double tx = Double.parseDouble(m[0]);
			double ty = (m.length == 2) ? Double.parseDouble(m[1]) : Double.parseDouble(m[0]);
			// return new double[] { 1, 0, tx, 0, 1, ty };
			return new CCMatrix32(1, 0, tx, 0, 1, ty);

		case "scale":
			double sx = Double.parseDouble(m[0]);
			double sy = (m.length == 2) ? Double.parseDouble(m[1]) : Double.parseDouble(m[0]);
			// return new double[] { sx, 0, 0, 0, sy, 0 };
			return new CCMatrix32(sx, 0, 0, 0, sy, 0);

		case "rotate":
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

		case "skewX":
			return new CCMatrix32(1, 0, 1, CCMath.tan(Double.parseDouble(m[0])), 0, 0);

		case "skewY":
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
	 * @param theMatrixString
	 *            text of the matrix param.
	 * @return a good old-fashioned CCMatrix32
	 */
	static protected CCMatrix32 parseTransform(String theMatrixString) {
		theMatrixString = theMatrixString.trim();
		CCMatrix32 outgoing = null;
		int start = 0;
		int stop = -1;
		while ((stop = theMatrixString.indexOf(')', start)) != -1) {
			CCMatrix32 m = parseSingleTransform(theMatrixString.substring(start,
					stop + 1));
			if (outgoing == null) {
				outgoing = m;
			} else {
				outgoing.apply(m);
			}
			start = stop + 1;
		}
		return outgoing;
	}
	
	

	class CCSVGLinearGradientPaint implements Paint {
		double x1, y1, x2, y2;
		double[] offset;
		int[] color;
		int count;
		double opacity;

		public CCSVGLinearGradientPaint(double x1, double y1, double x2, double y2,
				double[] offset, int[] color, int count, double opacity) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
			this.offset = offset;
			this.color = color;
			this.count = count;
			this.opacity = opacity;
		}

		public PaintContext createContext(
			ColorModel cm,
			Rectangle deviceBounds, Rectangle2D userBounds,
			AffineTransform xform, RenderingHints hints
		) {
			Point2D t1 = xform.transform(new Point2D.Double(x1, y1), null);
			Point2D t2 = xform.transform(new Point2D.Double(x2, y2), null);
			return new CCSVGLinearGradientContext(
                    t1.getX(), t1.getY(),
                    t2.getX(), t2.getY()
			);
		}

		public int getTransparency() {
			return TRANSLUCENT; // why not.. rather than checking each color
		}

		public class CCSVGLinearGradientContext implements PaintContext {
			int ACCURACY = 2;
			double tx1, ty1, tx2, ty2;

			public CCSVGLinearGradientContext(double tx1, double ty1, double tx2, double ty2) {
				this.tx1 = tx1;
				this.ty1 = ty1;
				this.tx2 = tx2;
				this.ty2 = ty2;
			}

			public void dispose() {
			}

			public ColorModel getColorModel() {
				return ColorModel.getRGBdefault();
			}

			public Raster getRaster(int x, int y, int w, int h) {
				WritableRaster raster = getColorModel().createCompatibleWritableRaster(w, h);

				int[] data = new int[w * h * 4];

				// make normalized version of base vector
				double nx = tx2 - tx1;
				double ny = ty2 - ty1;
				double len = Math.sqrt(nx * nx + ny * ny);
				if (len != 0) {
					nx /= len;
					ny /= len;
				}

				int span = (int) CCMath.dist(tx1, ty1, tx2, ty2) * ACCURACY;
				if (span <= 0) {
					// System.err.println("span is too small");
					// annoying edge case where the gradient isn't legit
					int index = 0;
					for (int j = 0; j < h; j++) {
						for (int i = 0; i < w; i++) {
							data[index++] = 0;
							data[index++] = 0;
							data[index++] = 0;
							data[index++] = 255;
						}
					}

				} else {
					int[][] interp = new int[span][4];
					int prev = 0;
					for (int i = 1; i < count; i++) {
						int c0 = color[i - 1];
						int c1 = color[i];
						int last = (int) (offset[i] * (span - 1));
						// System.out.println("last is " + last);
						for (int j = prev; j <= last; j++) {
							double btwn = CCMath.norm(j, prev, last);
							interp[j][0] = (int) CCMath.blend((c0 >> 16) & 0xff, (c1 >> 16) & 0xff, btwn);
							interp[j][1] = (int) CCMath.blend((c0 >> 8) & 0xff, (c1 >> 8) & 0xff, btwn);
							interp[j][2] = (int) CCMath.blend(c0 & 0xff, c1 & 0xff, btwn);
							interp[j][3] = (int) (CCMath.blend((c0 >> 24) & 0xff, (c1 >> 24) & 0xff, btwn) * opacity);
							// System.out.println(j + " " + interp[j][0] + " " +
							// interp[j][1] + " " + interp[j][2]);
						}
						prev = last;
					}

					int index = 0;
					for (int j = 0; j < h; j++) {
						for (int i = 0; i < w; i++) {
							// double distance = 0; //PApplet.dist(cx, cy, x + i,
							// y + j);
							// int which = PApplet.min((int) (distance *
							// ACCURACY), interp.length-1);
							double px = (x + i) - tx1;
							double py = (y + j) - ty1;
							// distance up the line is the dot product of the
							// normalized
							// vector of the gradient start/stop by the point
							// being tested
							int which = (int) ((px * nx + py * ny) * ACCURACY);
							if (which < 0)
								which = 0;
							if (which > interp.length - 1)
								which = interp.length - 1;
							// if (which > 138) System.out.println("grabbing " +
							// which);

							data[index++] = interp[which][0];
							data[index++] = interp[which][1];
							data[index++] = interp[which][2];
							data[index++] = interp[which][3];
						}
					}
				}
				raster.setPixels(0, 0, w, h, data);

				return raster;
			}
		}
	}

	class CCSVGRadialGradientPaint implements Paint {
		double cx, cy, radius;
		double[] offset;
		int[] color;
		int count;
		double opacity;

		public CCSVGRadialGradientPaint(double cx, double cy, double radius, double[] offset, int[] color, int count, double opacity) {
			this.cx = cx;
			this.cy = cy;
			this.radius = radius;
			this.offset = offset;
			this.color = color;
			this.count = count;
			this.opacity = opacity;
		}

		public PaintContext createContext(ColorModel cm,
				Rectangle deviceBounds, Rectangle2D userBounds,
				AffineTransform xform, RenderingHints hints) {
			return new CCSVGRadialGradientContext();
		}

		public int getTransparency() {
			return TRANSLUCENT;
		}

		public class CCSVGRadialGradientContext implements PaintContext {
			int ACCURACY = 5;

			public void dispose() {
			}

			public ColorModel getColorModel() {
				return ColorModel.getRGBdefault();
			}

			public Raster getRaster(int x, int y, int w, int h) {
				WritableRaster raster = getColorModel()
						.createCompatibleWritableRaster(w, h);

				int span = (int) radius * ACCURACY;
				int[][] interp = new int[span][4];
				int prev = 0;
				for (int i = 1; i < count; i++) {
					int c0 = color[i - 1];
					int c1 = color[i];
					int last = (int) (offset[i] * (span - 1));
					for (int j = prev; j <= last; j++) {
						double btwn = CCMath.norm(j, prev, last);
						interp[j][0] = (int) CCMath.blend((c0 >> 16) & 0xff,(c1 >> 16) & 0xff, btwn);
						interp[j][1] = (int) CCMath.blend((c0 >> 8) & 0xff,
								(c1 >> 8) & 0xff, btwn);
						interp[j][2] = (int) CCMath.blend(c0 & 0xff, c1 & 0xff, btwn);
						interp[j][3] = (int) (CCMath.blend((c0 >> 24) & 0xff,(c1 >> 24) & 0xff, btwn) * opacity);
					}
					prev = last;
				}

				int[] data = new int[w * h * 4];
				int index = 0;
				for (int j = 0; j < h; j++) {
					for (int i = 0; i < w; i++) {
						double distance = CCMath.dist(cx, cy, x + i, y + j);
						int which = CCMath.min((int) (distance * ACCURACY), interp.length - 1);

						data[index++] = interp[which][0];
						data[index++] = interp[which][1];
						data[index++] = interp[which][2];
						data[index++] = interp[which][3];
					}
				}
				raster.setPixels(0, 0, w, h, data);

				return raster;
			}
		}
	}
	
	private CCSVGDocument _myDocument;

	private CCSVGDocument readSVG(CCDataObject theSVG){
		if (!theSVG.containsKey("svg")) {
			throw new RuntimeException("root is not <svg>");
		}
		
		_myDocument = new CCSVGDocument();

		CCDataObject mySVG = theSVG.getObject("svg");
		readDocumentSize(_myDocument, mySVG);
		readElement(_myDocument, mySVG);
		readChildren(_myDocument, mySVG);

		return _myDocument;
	}
	
	private void readDocumentSize(CCSVGDocument theDocument, CCDataObject theSVG){
		// not proper parsing of the viewBox, but will cover us for cases where
		// the width and height of the object is not specified
		
		String viewBoxStr = theSVG.getString("viewBox");
		if (viewBoxStr != null) {
			String[] viewBox = CCStringUtil.splitTokens(viewBoxStr);
			theDocument.width = Double.parseDouble(viewBox[2]);
			theDocument.height = Double.parseDouble(viewBox[3]);
		}

		// TODO if viewbox is not same as width/height, then use it to scale
		// the original objects. for now, viewbox only used when width/height
		// are empty values (which by the spec means w/h of "100%"
		CCLog.info(theSVG);
		String unitWidth = theSVG.getString("width");
		String unitHeight = theSVG.getString("height");
		if (unitWidth != null) {
			theDocument.width = parseUnitSize(unitWidth);
			theDocument.height = parseUnitSize(unitHeight);
		} else {
			if ((_myDocument.width == 0) || (_myDocument.height == 0)) {
				// throw new RuntimeException("width/height not specified");
				CCLog.warn("The width and/or height is not readable in the <svg> tag of this file.");
				// For the spec, the default is 100% and 100%. For purposes
				// here, insert a dummy value because this is prolly just a
				// font or something for which the w/h doesn't matter.
				theDocument.width = 1;
				theDocument.height = 1;
			}
		}
	}
	
	private void readName(CCSVGElement theElement, CCDataObject theSVG){
		String myName = theSVG.getString("id");
		// @#$(* adobe illustrator mangles names of objects when re-saving
		if (myName != null) {
			while (true) {
				String[] m = CCStringUtil.match(myName, "_x([A-Za-z0-9]{2})_");
				if (m == null)
					break;
				char repair = (char) Integer.parseInt(m[1],16);
				myName = myName.replace(m[0], "" + repair);
			}
		}
		theElement._myName = myName;
	}
	
	private void readOpacity(CCSVGElement theElement, CCDataObject theSVG) {
		if (!theSVG.containsKey("opacity")) return;
		
		String opacityText = theSVG.getString("opacity");
		theElement.opacity(Double.parseDouble(opacityText));
	}
	
	private CCColor readRGB(String theColorText) {
		int leftParen = theColorText.indexOf('(') + 1;
		int rightParen = theColorText.indexOf(')');
		String sub = theColorText.substring(leftParen, rightParen);
		String[] values = CCStringUtil.splitTokens(sub, ", ");
		return new CCColor(
			Double.parseDouble(values[0]),
			Double.parseDouble(values[1]),
			Double.parseDouble(values[2])
		);
	}
	
	private Paint calcGradientPaint(CCSVGGradient gradient, double theOpacity) {
		if (gradient instanceof CCSVGLinearGradient) {
			CCSVGLinearGradient grad = (CCSVGLinearGradient) gradient;
			return new CCSVGLinearGradientPaint(grad.x1, grad.y1, grad.x2, grad.y2, grad.offset, grad.color, grad.count, theOpacity);

		} else if (gradient instanceof CCSVGRadialGradient) {
			CCSVGRadialGradient grad = (CCSVGRadialGradient) gradient;
			return new CCSVGRadialGradientPaint(grad.cx, grad.cy, grad.r, grad.offset, grad.color, grad.count, theOpacity);
		}
		return null;
	}
	
	private void readColor(CCSVGElement theElement, String theColorText, boolean isFill) {
		double myAlpha = theElement.fillColor.a;
		boolean myIsColorVisible = true;
		CCColor myColor = new CCColor();
		String name = "";
		CCSVGGradient myGradient = null;
		Paint myPaint = null;
		
		if (theColorText.equals("none")) {
			myIsColorVisible = false;
		} else if (theColorText.equals("black")) {
			myColor = CCColor.BLACK.clone();
			myColor.a = myAlpha;
		} else if (theColorText.equals("white")) {
			myColor = CCColor.WHITE.clone();
			myColor.a = myAlpha;
		} else if (theColorText.startsWith("#")) {
			if (theColorText.length() == 4) {
				// Short form: #ABC, transform to long form #AABBCC
				theColorText = theColorText.replaceAll("^#(.)(.)(.)$", "#$1$1$2$2$3$3");
			}
			myColor = CCColor.parseFromString(theColorText);
			myColor.a = myAlpha;
			// System.out.println("hex for fill is " + PApplet.hex(fillColor));
		} else if (theColorText.startsWith("rgb")) {
			myColor = readRGB(theColorText);
			myColor.a = myAlpha;
		} else if (theColorText.startsWith("url(#")) {
			name = theColorText.substring(5, theColorText.length() - 1);
			// PApplet.println("looking for " + name);
			CCSVGElement myElement = _myDocument.findChild(name);
			// PApplet.println("found " + fillObject);
			if (myElement instanceof CCSVGGradient) {
				myGradient = (CCSVGGradient) myElement;
				myPaint = calcGradientPaint(myGradient, myAlpha);
				// PApplet.println("got filla " + fillObject);
			} else {
				// visible = false;
				System.err.println("url " + name + " refers to unexpected data: " + myElement);
			}
		}
		if (isFill) {
			theElement.fill = myIsColorVisible;
			theElement.fillColor = myColor;
			theElement.fillName = name;
			theElement.fillGradient = myGradient;
			theElement.fillGradientPaint = myPaint;
		} else {
			theElement.stroke = myIsColorVisible;
			theElement.strokeColor = myColor;
			theElement.strokeName = name;
			theElement.strokeGradient = myGradient;
			theElement.strokeGradientPaint = myPaint;
		}
	}
	
	private void readStroke(CCSVGElement theElement, CCDataObject theSVG) {
		if (!theSVG.containsKey("stroke")) return;
		
		String strokeText = theSVG.getString("stroke");
		readColor(theElement, strokeText, false);
	}
	
	private void readStrokeOpacity(CCSVGElement theElement, CCDataObject theSVG) {
		if (!theSVG.containsKey("stroke-opacity")) return;
		
		theElement.strokeOpacity = theSVG.getDouble("stroke-opacity");
		theElement.strokeColor.a = theElement.strokeOpacity;
	}
	
	private void readStrokeWeight(CCSVGElement theElement, CCDataObject theSVG){
		if (!theSVG.containsKey("stroke-width")) return;
		theElement.strokeWeight = parseUnitSize(theSVG.getString("stroke-width"));
	}
	
	private void setStrokeJoin(CCSVGElement theElement, String theStrokeJoin){
		if (theStrokeJoin.equals("inherit")) {
			// do nothing, will inherit automatically
		} else if (theStrokeJoin.equals("miter")) {
			theElement.strokeJoin = CCStrokeJoin.MITER;
		} else if (theStrokeJoin.equals("round")) {
			theElement.strokeJoin = CCStrokeJoin.ROUND;
		} else if (theStrokeJoin.equals("bevel")) {
			theElement.strokeJoin = CCStrokeJoin.BEVEL;
		}
	}
	
	private void readStrokeJoin(CCSVGElement theElement, CCDataObject theSVG){
		if (!theSVG.containsKey("stroke-linejoin")) return;
			
		setStrokeJoin(theElement, theSVG.getString("stroke-linejoin"));
	}
	
	private void setStrokeCap(CCSVGElement theElement, String theStrokeCap){
		if (theStrokeCap.equals("inherit")) {
			// do nothing, will inherit automatically
		} else if (theStrokeCap.equals("butt")) {
			theElement.strokeCap = CCStrokeCap.SQUARE;
		} else if (theStrokeCap.equals("round")) {
			theElement.strokeCap = CCStrokeCap.ROUND;
		} else if (theStrokeCap.equals("square")) {
			theElement.strokeCap = CCStrokeCap.PROJECT;
		}
	}
	
	private void readStrokeCap(CCSVGElement theElement, CCDataObject theSVG){
		if (!theSVG.containsKey("stroke-linecap")) return;
			
		setStrokeCap(theElement, theSVG.getString("stroke-linecap"));
	}
	
	private void readFill(CCSVGElement theElement, CCDataObject theSVG) {
		if (!theSVG.containsKey("fill")) return;
		
		String myFillText = theSVG.getString("fill");
		readColor(theElement, myFillText, true);
	}
	
	private void readFillOpacity(CCSVGElement theElement, CCDataObject theSVG) {
		if (!theSVG.containsKey("fill-opacity")) return;
		
		theElement.fillOpacity(theSVG.getDouble("fill-opacity"));
	}
	
	private void readColors(CCSVGElement theElement, CCDataObject theSVG) {
		readOpacity(theElement, theSVG);
		readStroke(theElement, theSVG);
		readStrokeOpacity(theElement, theSVG);
		readStrokeWeight(theElement, theSVG);
		readStrokeJoin(theElement, theSVG);
		readStrokeCap(theElement, theSVG);
		readFill(theElement, theSVG);
		readFillOpacity(theElement, theSVG);


		if (theSVG.containsKey("style")) {
			String styleText = theSVG.getString("style");
			String[] styleTokens = CCStringUtil.splitTokens(styleText, ";");

			// PApplet.println(styleTokens);
			for (int i = 0; i < styleTokens.length; i++) {
				String[] tokens = CCStringUtil.splitTokens(styleTokens[i], ":");
				// PApplet.println(tokens);

				tokens[0] = CCStringUtil.trim(tokens[0]);

				if (tokens[0].equals("fill")) {
					readColor(theElement, tokens[1], true);

				} else if (tokens[0].equals("fill-opacity")) {
					theElement.fillOpacity(Double.parseDouble(tokens[1]));
				} else if (tokens[0].equals("stroke")) {
					readColor(theElement, tokens[1], false);

				} else if (tokens[0].equals("stroke-width")) {
					theElement.strokeWeight = parseUnitSize(tokens[1]);
				} else if (tokens[0].equals("stroke-linecap")) {
					setStrokeCap(theElement, tokens[1]);
				} else if (tokens[0].equals("stroke-linejoin")) {
					setStrokeJoin(theElement,tokens[1]);
				} else if (tokens[0].equals("stroke-opacity")) {
					theElement.strokeOpacity(Double.parseDouble(tokens[1]));
				} else if (tokens[0].equals("opacity")) {
					theElement.opacity(Double.parseDouble(tokens[1]));
				}
			}
		}
	}
	
	private void readVisible(CCSVGElement theElement, CCDataObject theSVG){
		String displayStr = theSVG.getString("display", "inline");
		theElement.visible = !displayStr.equals("none");
	}
	
	private void readTransform(CCSVGElement theElement, CCDataObject theSVG){
		String transformStr = theSVG.getString("transform");
		if (transformStr != null) {
			theElement.matrix = parseTransform(transformStr);
		}
	}
	
	private void readElement(CCSVGElement theElement, CCDataObject theSVG){
		for(String myKey:theSVG.keySet()){
			CCLog.info(myKey);
		}
		readName(theElement, theSVG);
		readColors(theElement, theSVG);
		readVisible(theElement, theSVG);
		readTransform(theElement, theSVG);
	}
	
	private void readGroup(CCSVGGroup theGroup, CCDataObject theSVG){
		readElement(theGroup, theSVG);
		readChildren(theGroup, theSVG);
	}
	
	private void readDefs(CCSVGGroup theGroup, CCDataObject theSVG){
		readChildren(theGroup, theSVG);
	}
	
	private void readLine(CCSVGLine theLine, CCDataObject theSVG){
		readElement(theLine, theSVG);
		theLine.a().set(
			getDoubleWithUnit(theSVG, "x1"),
			getDoubleWithUnit(theSVG, "y1")
		);
		theLine.b().set(
			getDoubleWithUnit(theSVG, "x2"),
			getDoubleWithUnit(theSVG, "y2")
		);
	}
	
	private void readEllipse(CCSVGEllipse theEllipse, CCDataObject theSVG, boolean theIsCircle){
		readElement(theEllipse, theSVG);

		theEllipse.center().set(
			getDoubleWithUnit(theSVG, "cx"),
			getDoubleWithUnit(theSVG, "cy")
		);

		double rx, ry;
		if (theIsCircle) {
			rx = ry = getDoubleWithUnit(theSVG, "r");
		} else {
			rx = getDoubleWithUnit(theSVG, "rx");
			ry = getDoubleWithUnit(theSVG, "ry");
		}
		theEllipse.radius().set(rx, ry);
	}
	
	private void readRect(CCSVGRectangle theRectangle, CCDataObject theSVG) {
		readElement(theRectangle, theSVG);
		
		theRectangle.center().set(
			getDoubleWithUnit(theSVG, "x"),
			getDoubleWithUnit(theSVG, "y")
		);
		theRectangle.dimension().set(
			getDoubleWithUnit(theSVG, "width"),
			getDoubleWithUnit(theSVG, "height")
		);
	}
	
	/**
	 * Parse a polyline or polygon from an SVG file.
	 * 
	 * @param close
	 *            true if shape is closed (polygon), false if not (polyline)
	 */
	private void readPoly(CCSVGPoly thePath, CCDataObject theSVG) {
		readElement(thePath, theSVG);

		String pointsAttr = theSVG.getString("points");
		if (pointsAttr != null) {
			String[] pointsBuffer = CCStringUtil.splitTokens(pointsAttr);
			thePath.spline().beginEditSpline();
			for (int i = 0; i < pointsBuffer.length; i++) {
				String pb[] = CCStringUtil.split(pointsBuffer[i], ',');
				thePath.spline().addPoint(new CCVector3(
					Double.valueOf(pb[0]),
					Double.valueOf(pb[1])
				));
			}
			thePath.spline().endEditSpline();
		}

	}
	
	
	
	
	private void readChildren(String theName, CCSVGGroup theGroup, CCDataArray theDataArray){
		
	}
	
	private void readChildren(String theName, CCSVGGroup theGroup, CCDataObject theObject){
//		switch(theName){
//		case "g":
//			CCSVGGroup myGroup = new CCSVGGroup(theGroup);
//			myGroup.kind(CCShapeKind.GROUP);
//			CCLog.info(theSVG.get("g").getClass());
//			readGroup(myGroup, mySVG);
//			theGroup.addChild(myGroup);
//			break;
//		case "defs":
//			// generally this will contain gradient info, so may
//			// as well just throw it into a group element for parsing
//			// return new BaseObject(this, elem);
//			if(mySVG == null)continue;
//			CCSVGGroup myDefGroup = new CCSVGGroup(theGroup);
//			readDefs(myDefGroup, mySVG);
//			myDefGroup.kind(CCShapeKind.DEF);
//			myDefGroup.addChild(myDefGroup);
//		case "line":
//			CCSVGLine myLine = new CCSVGLine(theGroup);
//			readLine(myLine, mySVG);
//			theGroup.addChild(myLine);
//		case "circle":
//			CCSVGEllipse myCircle = new CCSVGEllipse(theGroup);
//			readEllipse(myCircle, mySVG, true);
//			theGroup.addChild(myCircle);
//		case "ellipse":
//			CCSVGEllipse myEllipe = new CCSVGEllipse(theGroup);
//			readEllipse(myEllipe, mySVG, false);
//			theGroup.addChild(myEllipe);
//		case "rect":
//			CCSVGRectangle myRect = new CCSVGRectangle(theGroup);
//			readRect(myRect, mySVG);
//			theGroup.addChild(myRect);
//		case "polygon":
//			CCSVGPoly myPoly = new CCSVGPoly(theGroup, true);
//			readPoly(myPoly, mySVG);
//			theGroup.addChild(myPoly);
//		case "polyline":
//			CCSVGPoly myPolyline = new CCSVGPoly(theGroup, false);
//			readPoly(myPolyline, mySVG);
//			theGroup.addChild(myPolyline);
//		case "path":
//			CCSVGPath myPath = new CCSVGPath(theGroup);
//			readPath(myPath, mySVG);
//			theGroup.addChild(myPath);
//			// return new BaseObject(this, elem, PATH);
////			shape = new PShapeSVG(this, mySVG, true);
////			shape.parsePath();
//
//		case "radialGradient":
//			theGroup.addChild(new CCSVGRadialGradient(theGroup, mySVG));
//		case "linearGradient":
//			theGroup.addChild(new CCSVGLinearGradient(theGroup, mySVG));
//		case "font":
////			return new Font(this, mySVG);
//
//			// } else if (myName.equals("font-face")) {
//			// return new FontFace(this, elem);
//
//			// } else if (myName.equals("glyph") || myName.equals("missing-glyph"))
//			// {
//			// return new FontGlyph(this, elem);
//
//		case "metadata":
//
//		case "text": // || myName.equals("font")) {
//			CCLog.warn("Text and fonts in SVG files "
//					+ "are not currently supported, "
//					+ "convert text to outlines instead.");
//
//		case "filter":
//			CCLog.warn("Filters are not supported.");
//		case "mask":
//			CCLog.warn("Masks are not supported.");
//		case "pattern":
//			CCLog.warn("Patterns are not supported.");
//		case "stop":
//			// stop tag is handled by gradient parser, so don't warn about it
//		case "sodipodi:namedview":
//			// these are always in Inkscape files, the warnings get tedious
//		default:
//			if (!myName.startsWith("#")) {
//				CCLog.warn("Ignoring <" + myName + "> tag.");
//			}
//		}
	}
	
	private void readChildren(CCSVGGroup theGroup, CCDataObject theSVG) {
		theGroup._myChildren = new ArrayList<>();
		 
		for (String myName : theSVG.keySet()) {
			
			if (myName == null) {
				continue;
			}
			
			
			Object mySVG = theSVG.get(myName);
				
			
		}
	}
	
	private static CCSVGIONew _myReader;
	
	public static CCSVGDocument newSVG(final Path theSVG){
		if(_myReader == null){
			_myReader = new CCSVGIONew();
		}
		return _myReader.readSVG(CCDataIO.createDataObject(theSVG, CCDataFormats.XML));
	}
}
