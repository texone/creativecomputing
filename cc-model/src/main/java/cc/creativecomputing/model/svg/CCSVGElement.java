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
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.util.CCTriangulator;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.CCStrokeCap;
import cc.creativecomputing.model.CCStrokeJoin;


public abstract class CCSVGElement {

	public enum CCShapeKind{
		LINE, ELLIPSE, RECT, POLYGON, PATH, GROUP, DEF
	}
	
	protected final CCShapeKind _myKind;
	
	public enum CCShapeFamily{
		GROUP, PRIMITIVE, PATH
	}

	/** The shape type, one of GROUP, PRIMITIVE, PATH, or GEOMETRY. */
	protected final CCShapeFamily family;
	
	protected CCSVGGroup parent;
	
	@CCProperty(name = "name")
	protected String _myName;
	
	@CCProperty(name = "transform")
	protected CCMatrix32 matrix = new CCMatrix32();
	
	// set to false if the object is hidden in the layers palette
	@CCProperty(name = "visible")
	protected boolean visible = true;
	
	@CCProperty(name = "stroke")
	protected boolean stroke;
	@CCProperty(name = "strokeColor")
	protected CCColor strokeColor;
	@CCProperty(name = "stroke weight")
	protected double strokeWeight; // default is 1
	@CCProperty(name = "stroke cap")
	protected CCStrokeCap strokeCap;
	@CCProperty(name = "stroke join")
	protected CCStrokeJoin strokeJoin;
	CCSVGGradient strokeGradient;
	Paint strokeGradientPaint;
	String strokeName; // id of another object, gradients only?

	@CCProperty(name = "fill")
	protected boolean fill;
	@CCProperty(name = "fill color")
	protected CCColor fillColor;
	CCSVGGradient fillGradient;
	Paint fillGradientPaint;
	String fillName; // id of another object
	
	/** True if this is a closed path. */
	protected boolean close;
	
	double opacity;
	double strokeOpacity;
	double fillOpacity;
	
	public CCSVGElement(CCSVGGroup theParent, CCShapeKind theKind, CCShapeFamily theFamily){
		parent = theParent;
		_myKind = theKind;
		family = theFamily;
		
		if (parent == null) {
			// set values to their defaults according to the SVG spec
			stroke = false;
			strokeColor = CCColor.BLACK.clone();
			strokeWeight = 1;
//			strokeCap = PConstants.SQUARE; // equivalent to BUTT in svg spec
//			strokeJoin = PConstants.MITER;
			strokeGradient = null;
			strokeGradientPaint = null;
			strokeName = null;

			fill = true;
			fillColor = CCColor.BLACK.clone();
			fillGradient = null;
			fillGradientPaint = null;
			fillName = null;

			// hasTransform = false;
			// transformation = null; //new double[] { 1, 0, 0, 1, 0, 0 };

			strokeOpacity = 1;
			fillOpacity = 1;
			opacity = 1;

		} else {
			stroke = parent.stroke;
			strokeColor = parent.strokeColor.clone();
			strokeWeight = parent.strokeWeight;
			strokeCap = parent.strokeCap;
			strokeJoin = parent.strokeJoin;
			strokeGradient = parent.strokeGradient;
			strokeGradientPaint = parent.strokeGradientPaint;
			strokeName = parent.strokeName;

			fill = parent.fill;
			fillColor = parent.fillColor.clone();
			fillGradient = parent.fillGradient;
			fillGradientPaint = parent.fillGradientPaint;
			fillName = parent.fillName;

			// hasTransform = parent.hasTransform;
			// transformation = parent.transformation;

			opacity = parent.opacity;
		}
	}
	
	public CCMatrix32 transform(){
		return matrix;
	}
	
	public CCShapeKind kind(){
		return _myKind;
	}

	public void name(String theName) {
		_myName = theName;
	}

	public String name() {
		return _myName;
	}
	
	public void opacity(double theOpacity){
		opacity = theOpacity;
		strokeColor.a = theOpacity;
		fillColor.a = theOpacity;
	}
	
	public void fillOpacity(double theOpacity){
		opacity = theOpacity;
		fillColor.a = theOpacity;
	}
	
	public void strokeOpacity(double theOpacity){
		opacity = theOpacity;
		strokeColor.a = theOpacity;
	}
	
	public abstract void drawImplementation(CCGraphics g, boolean theFill);
	
	public void draw(CCGraphics g){
		if(matrix != null){
			g.pushMatrix();
			g.applyMatrix(matrix);
		}
		g.pushAttribute();

		if (fill) {
			// System.out.println("filling " + PApplet.hex(fillColor));
			g.color(fillColor);
			drawImplementation(g, true);
		} 
		if (stroke) {
			g.color(strokeColor);
			g.strokeWeight(strokeWeight);
			drawImplementation(g, false);
		} 
		
		g.popAttribute();
		if(matrix != null){
			g.popMatrix();
		}
	}
	
	private CCTriangulator _myTriangulator = null;
	

	protected void draw(CCGraphics g, CCLinearSpline theSpline, boolean theFill) {
		if(theFill){
			if(_myTriangulator == null){
				_myTriangulator = new CCTriangulator();
				_myTriangulator.beginPolygon();
				for(CCVector3 myPoint:theSpline.points()){
					_myTriangulator.vertex(myPoint);
				}
				_myTriangulator.endPolygon();
			}
			g.beginShape(CCDrawMode.TRIANGLES);
			for(CCVector3 myVertex:_myTriangulator.vertices()){
				g.vertex(myVertex);
			}
			g.endShape();
		}else{
			if(theSpline.isClosed())g.beginShape(CCDrawMode.LINE_LOOP);
			else g.beginShape(CCDrawMode.LINE_STRIP);
			for(CCVector3 myPoint:theSpline.points()){
				g.vertex(myPoint);
			}
			g.endShape();
		}
	}
	

	public List<CCLinearSpline> contours(){
		return contours(1);
	}
	
	public abstract List<CCLinearSpline> contours(double theFlatness);
	
	private void readName(CCDataElement theSVG){
		String myName = theSVG.attribute("id");
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
		_myName = myName;
	}
	
	private void writeName(CCDataElement theSVG){
		theSVG.addAttribute("id", _myName);
	}
	
	private void readOpacity(CCDataElement theSVG) {
		if (!theSVG.hasAttribute("opacity")) return;
		
		opacity(Double.parseDouble(theSVG.attribute("opacity")));
	}
	
	private void writeOpacity(CCDataElement theSVG){
		theSVG.addAttribute("opacity", CCMath.max(opacity, strokeColor.a, fillColor.a));
	}
	
	private void readStroke(CCDataElement theSVG) {
		if (!theSVG.hasAttribute("stroke")) return;
		
		readColor(theSVG.attribute("stroke"), false);
	}
	
	private void writeStroke(CCDataElement theSVG){
		if(!stroke|| strokeColor == null)theSVG.addAttribute("stroke", "none");
		theSVG.addAttribute("stroke", "#" + strokeColor.rgbString());
	}
	
	private void readStrokeOpacity(CCDataElement theSVG) {
		if (!theSVG.hasAttribute("stroke-opacity")) return;
		
		strokeOpacity = theSVG.doubleAttribute("stroke-opacity");
		strokeColor.a = strokeOpacity;
	}
	
	private void writeStrokeOpacity(CCDataElement theSVG){
		if(strokeColor != null && strokeColor.a != 1){
			theSVG.addAttribute("stroke-opacity", strokeColor.a);
		}
	}
	
	private void readStrokeWeight(CCDataElement theSVG){
		if (!theSVG.hasAttribute("stroke-width")) return;
		strokeWeight = CCSVGIO.parseUnitSize(theSVG.attribute("stroke-width"));
	}
	
	private void writeStrokeWeight(CCDataElement theSVG){
		theSVG.addAttribute("stroke-width", strokeWeight);
	}
	
	private void setStrokeJoin(String theJoin){
		switch(theJoin){
		case "inherit":
			// do nothing, will inherit automatically
		case "miter":
			strokeJoin = CCStrokeJoin.MITER;
			break;
		case "round":
			strokeJoin = CCStrokeJoin.ROUND;
			break;
		case "bevel":
			strokeJoin = CCStrokeJoin.BEVEL;
			break;
		}
	}
	
	private void readStrokeJoin(CCDataElement theSVG){
		if (!theSVG.hasAttribute("stroke-linejoin")) return;
		
		setStrokeJoin(theSVG.attribute("stroke-linejoin"));
	}
	
	private void setStrokeCap(String theStrokeCap){
		switch(theStrokeCap){
		case "inherit":
			// do nothing, will inherit automatically
		case "butt":
			strokeCap = CCStrokeCap.SQUARE;
			break;
		case "round":
			strokeCap = CCStrokeCap.ROUND;
			break;
		case "square":
			strokeCap = CCStrokeCap.PROJECT;
			break;
		}
	}
	
	private void readStrokeCap(CCDataElement theSVG){
		if (!theSVG.hasAttribute("stroke-linecap")) return;
		
		setStrokeCap(theSVG.attribute("stroke-linecap"));
	}
	

	private void readFill(CCDataElement theSVG) {
		if (!theSVG.hasAttribute("fill")) return;
		
		String myFillText = theSVG.attribute("fill");
		readColor(myFillText, true);
	}
	
	private void writeFill(CCDataElement theSVG){
		if(!fill|| fillColor == null)theSVG.addAttribute("fill", "none");
		theSVG.addAttribute("fill", "#" + fillColor.rgbString());
	}
	
	private void readFillOpacity(CCDataElement theSVG) {
		if (!theSVG.hasAttribute("fill-opacity")) return;
		
		fillOpacity(theSVG.doubleAttribute("fill-opacity"));
	}
	
	private void writeFillOpacity(CCDataElement theSVG){
		if(fillColor != null && fillColor.a != 1){
			theSVG.addAttribute("fill-opacity", fillColor.a);
		}
	}
	
	private CCColor readRGB(String theColorText) {
		int leftParen = theColorText.indexOf('(') + 1;
		int rightParen = theColorText.indexOf(')');
		String sub = theColorText.substring(leftParen, rightParen);
		String[] values = CCStringUtil.splitTokens(sub, ", ");
		return new CCColor(
			Integer.parseInt(values[0]),
			Integer.parseInt(values[1]),
			Integer.parseInt(values[2])
		);
	}
	
	private class LinearGradientPaint implements Paint {
		double x1, y1, x2, y2;
		double[] offset;
		int[] color;
		int count;
		double opacity;

		public LinearGradientPaint(double x1, double y1, double x2, double y2,
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

		public PaintContext createContext(ColorModel cm,
				Rectangle deviceBounds, Rectangle2D userBounds,
				AffineTransform xform, RenderingHints hints) {
			Point2D t1 = xform.transform(new Point2D.Double(x1, y1), null);
			Point2D t2 = xform.transform(new Point2D.Double(x2, y2), null);
			return new LinearGradientContext(t1.getX(),
                    t1.getY(), t2.getX(), t2.getY());
		}

		public int getTransparency() {
			return TRANSLUCENT; // why not.. rather than checking each color
		}

		public class LinearGradientContext implements PaintContext {
			int ACCURACY = 2;
			double tx1, ty1, tx2, ty2;

			public LinearGradientContext(double tx1, double ty1, double tx2, double ty2) {
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
				WritableRaster raster = getColorModel()
						.createCompatibleWritableRaster(w, h);

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

	class RadialGradientPaint implements Paint {
		double cx, cy, radius;
		double[] offset;
		int[] color;
		int count;
		double opacity;

		public RadialGradientPaint(double cx, double cy, double radius, double[] offset, int[] color, int count, double opacity) {
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
			return new RadialGradientContext();
		}

		public int getTransparency() {
			return TRANSLUCENT;
		}

		public class RadialGradientContext implements PaintContext {
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
	
	private Paint calcGradientPaint(CCSVGGradient gradient, double theOpacity) {
		if (gradient instanceof CCSVGLinearGradient) {
			CCSVGLinearGradient grad = (CCSVGLinearGradient) gradient;
			return new LinearGradientPaint(grad.x1, grad.y1, grad.x2, grad.y2, grad.offset, grad.color, grad.count, theOpacity);

		} else if (gradient instanceof CCSVGRadialGradient) {
			CCSVGRadialGradient grad = (CCSVGRadialGradient) gradient;
			return new RadialGradientPaint(grad.cx, grad.cy, grad.r, grad.offset, grad.color, grad.count, theOpacity);
		}
		return null;
	}
	
	private void readColor(String theColorText, boolean isFill) {
		double myAlpha = fillColor.a;
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
//			CCSVGElement myElement = _myDocument.findChild(name);
//			// PApplet.println("found " + fillObject);
//			if (myElement instanceof CCSVGGradient) {
//				myGradient = (CCSVGGradient) myElement;
//				myPaint = calcGradientPaint(myGradient, myAlpha);
//				// PApplet.println("got filla " + fillObject);
//			} else {
//				// visible = false;
//				System.err.println("url " + name + " refers to unexpected data: " + myElement);
//			}
		}
		if (isFill) {
			fill = myIsColorVisible;
			fillColor = myColor;
			fillName = name;
			fillGradient = myGradient;
			fillGradientPaint = myPaint;
		} else {
			stroke = myIsColorVisible;
			strokeColor = myColor;
			strokeName = name;
			strokeGradient = myGradient;
			strokeGradientPaint = myPaint;
		}
	}
	
	private void writeColors(CCDataElement theSVG) {
		writeOpacity(theSVG);
		writeStroke(theSVG);
		writeStrokeOpacity(theSVG);
		writeStrokeWeight(theSVG);
//		writeStrokeJoin(theSVG);
//		writeStrokeCap(theSVG);
		writeFill(theSVG);
		writeFillOpacity(theSVG);
	}
	
	private void readColors(CCDataElement theSVG) {
		readOpacity(theSVG);
		readStroke(theSVG);
		readStrokeOpacity(theSVG);
		readStrokeWeight(theSVG);
		readStrokeJoin(theSVG);
		readStrokeCap(theSVG);
		readFill(theSVG);
		readFillOpacity(theSVG);


		if (theSVG.hasAttribute("style")) {
			String styleText = theSVG.attribute("style");
			String[] styleTokens = CCStringUtil.splitTokens(styleText, ";");

			// PApplet.println(styleTokens);
			for (int i = 0; i < styleTokens.length; i++) {
				String[] tokens = CCStringUtil.splitTokens(styleTokens[i], ":");
				// PApplet.println(tokens);

				tokens[0] = CCStringUtil.trim(tokens[0]);

				if (tokens[0].equals("fill")) {
					readColor(tokens[1], true);

				} else if (tokens[0].equals("fill-opacity")) {
					fillOpacity(Double.parseDouble(tokens[1]));
				} else if (tokens[0].equals("stroke")) {
					readColor(tokens[1], false);

				} else if (tokens[0].equals("stroke-width")) {
					strokeWeight = CCSVGIO.parseUnitSize(tokens[1]);
				} else if (tokens[0].equals("stroke-linecap")) {
					setStrokeCap(tokens[1]);
				} else if (tokens[0].equals("stroke-linejoin")) {
					setStrokeJoin(tokens[1]);
				} else if (tokens[0].equals("stroke-opacity")) {
					strokeOpacity(Double.parseDouble(tokens[1]));
				} else if (tokens[0].equals("opacity")) {
					opacity(Double.parseDouble(tokens[1]));
				}
			}
		}
	}
	
	public abstract String svgTag();
	
	private void readVisible(CCDataElement theSVG){
		String displayStr = theSVG.attribute("display", "inline");
		visible = !displayStr.equals("none");
	}
	
	private void writeVisible(CCDataElement theSVG){
		theSVG.addAttribute("display", visible ? "inline" : "none");
	}
	
	private void readTransform(CCDataElement theSVG){
		String transformStr = theSVG.attribute("transform");
		if (transformStr != null) {
			matrix = CCSVGIO.parseTransform(transformStr);
		}
	}
	
	private void writeTransform(CCDataElement theSVG){
		if(matrix != null)theSVG.addAttribute("transform", CCSVGIO.writeMatrix(matrix));
	}
	
	public void read(CCDataElement theSVG){
		readName(theSVG);
		readColors(theSVG);
		readVisible(theSVG);
		readTransform(theSVG);
	}
	
	public CCDataElement write(){
		CCDataElement myResult = new CCDataElement(svgTag());
		writeName(myResult);
		writeColors(myResult);
		writeVisible(myResult);
		writeTransform(myResult);
		return myResult;
	}
}
