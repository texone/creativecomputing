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

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Arc2D;
import java.awt.geom.GeneralPath;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.creativecomputing.math.CCVector2;


public class CCSVGPathReader {

	static protected double nextFloat(LinkedList<String> l) {
		String s = l.removeFirst();
		return Float.parseFloat(s);
	}
	
	class BuildHistory {
		CCVector2 startPoint = new CCVector2();
		CCVector2 lastPoint = new CCVector2();
		CCVector2 lastKnot = new CCVector2();
	}

	abstract class PathCommand {

		boolean isRelative = false;
		
		int _myNumKnotsAdded;

		/** Creates a new instance of PathCommand */
		public PathCommand(int theNumKnotsAdded) {
			this(theNumKnotsAdded, false);
		}

		public PathCommand(int theNumKnotsAdded, boolean isRelative) {
			this.isRelative = isRelative;
			_myNumKnotsAdded = theNumKnotsAdded;
		}

		abstract public void appendPath(GeneralPath path, BuildHistory hist);

		public int getNumKnotsAdded(){
			return _myNumKnotsAdded;
		}
	}

	class MoveTo extends PathCommand {

		public double x = 0f;
		public double y = 0f;

		/** Creates a new instance of MoveTo */
		public MoveTo() {
			super(2);
		}

		public MoveTo(boolean isRelative, double x, double y) {
			super(2,isRelative);
			this.x = x;
			this.y = y;
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = isRelative ? hist.lastPoint.x : 0f;
			double offy = isRelative ? hist.lastPoint.y : 0f;

			path.moveTo(x + offx, y + offy);
			hist.startPoint.set(x + offx, y + offy);
			hist.lastPoint.set(x + offx, y + offy);
			hist.lastKnot.set(x + offx, y + offy);
		}

		public String toString() {
			return "M " + x + " " + y;
		}

	}

	class LineTo extends PathCommand {

		public double x = 0f;
		public double y = 0f;

		/** Creates a new instance of LineTo */
		public LineTo() {
			super(2);
		}

		public LineTo(boolean isRelative, double x, double y) {
			super(2, isRelative);
			this.x = x;
			this.y = y;
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = isRelative ? hist.lastPoint.x : 0f;
			double offy = isRelative ? hist.lastPoint.y : 0f;

			path.lineTo(x + offx, y + offy);
			hist.lastPoint.set(x + offx, y + offy);
			hist.lastKnot.set(x + offx, y + offy);
		}

		public String toString() {
			return "L " + x + " " + y;
		}
	}

	class Horizontal extends PathCommand {

		public double x = 0f;

		/** Creates a new instance of MoveTo */
		public Horizontal() {
			super(2);
		}

		public String toString() {
			return "H " + x;
		}

		public Horizontal(boolean isRelative, double x) {
			super(2, isRelative);
			this.x = x;
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = isRelative ? hist.lastPoint.x : 0f;
			double offy = hist.lastPoint.y;

			path.lineTo(x + offx, offy);
			hist.lastPoint.set(x + offx, offy);
			hist.lastKnot.set(x + offx, offy);
		}
	}

	class Vertical extends PathCommand {

		public double y = 0f;

		/** Creates a new instance of MoveTo */
		public Vertical() {
			super(2);
		}

		public String toString() {
			return "V " + y;
		}

		public Vertical(boolean isRelative, double y) {
			super(2,isRelative);
			this.y = y;
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = hist.lastPoint.x;
			double offy = isRelative ? hist.lastPoint.y : 0f;

			path.lineTo(offx, y + offy);
			hist.lastPoint.set(offx, y + offy);
			hist.lastKnot.set(offx, y + offy);
		}

		public int getNumKnotsAdded() {
			return 2;
		}
	}

	class Arc extends PathCommand {

		public double rx = 0f;
		public double ry = 0f;
		public double xAxisRot = 0f;
		public boolean largeArc = false;
		public boolean sweep = false;
		public double x = 0f;
		public double y = 0f;

		/** Creates a new instance of MoveTo */
		public Arc() {
			super(6);
		}

		public Arc(boolean isRelative, double rx, double ry, double xAxisRot, boolean largeArc, boolean sweep, double x, double y) {
			super(6,isRelative);
			this.rx = rx;
			this.ry = ry;
			this.xAxisRot = xAxisRot;
			this.largeArc = largeArc;
			this.sweep = sweep;
			this.x = x;
			this.y = y;
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = isRelative ? hist.lastPoint.x : 0f;
			double offy = isRelative ? hist.lastPoint.y : 0f;

			arcTo(path, rx, ry, xAxisRot, largeArc, sweep, x + offx, y + offy, hist.lastPoint.x, hist.lastPoint.y);
			// path.lineTo(x + offx, y + offy);
			// hist.setPoint(x + offx, y + offy);
			hist.lastPoint.set(x + offx, y + offy);
			hist.lastKnot.set(x + offx, y + offy);
		}

		/**
		 * Adds an elliptical arc, defined by two radii, an angle from the
		 * x-axis, a flag to choose the large arc or not, a flag to indicate if
		 * we increase or decrease the angles and the final point of the arc.
		 * 
		 * @param rx
		 *            the x radius of the ellipse
		 * @param ry
		 *            the y radius of the ellipse
		 * 
		 * @param angle
		 *            the angle from the x-axis of the current coordinate system
		 *            to the x-axis of the ellipse in degrees.
		 * 
		 * @param largeArcFlag
		 *            the large arc flag. If true the arc spanning less than or
		 *            equal to 180 degrees is chosen, otherwise the arc spanning
		 *            greater than 180 degrees is chosen
		 * 
		 * @param sweepFlag
		 *            the sweep flag. If true the line joining center to arc
		 *            sweeps through decreasing angles otherwise it sweeps
		 *            through increasing angles
		 * 
		 * @param x
		 *            the absolute x coordinate of the final point of the arc.
		 * @param y
		 *            the absolute y coordinate of the final point of the arc.
		 * @param x0
		 *            - The absolute x coordinate of the initial point of the
		 *            arc.
		 * @param y0
		 *            - The absolute y coordinate of the initial point of the
		 *            arc.
		 */
		public void arcTo(GeneralPath path, double rx, double ry, double angle,
				boolean largeArcFlag, boolean sweepFlag, double x, double y,
				double x0, double y0) {

			// Ensure radii are valid
			if (rx == 0 || ry == 0) {
				path.lineTo((double) x, (double) y);
				return;
			}

			if (x0 == x && y0 == y) {
				// If the endpoints (x, y) and (x0, y0) are identical, then this
				// is equivalent to omitting the elliptical arc segment
				// entirely.
				return;
			}

			Arc2D arc = computeArc(x0, y0, rx, ry, angle, largeArcFlag,
					sweepFlag, x, y);
			if (arc == null)
				return;

			AffineTransform t = AffineTransform.getRotateInstance(Math.toRadians(angle), arc.getCenterX(), arc.getCenterY());
			Shape s = t.createTransformedShape(arc);
			path.append(s, true);
		}

		/**
		 * This constructs an unrotated Arc2D from the SVG specification of an
		 * Elliptical arc. To get the final arc you need to apply a rotation
		 * transform such as:
		 * 
		 * AffineTransform.getRotateInstance (angle,
		 * arc.getX()+arc.getWidth()/2, arc.getY()+arc.getHeight()/2);
		 */
		public Arc2D computeArc(double x0, double y0, double rx, double ry,
				double angle, boolean largeArcFlag, boolean sweepFlag,
				double x, double y) {
			//
			// Elliptical arc implementation based on the SVG specification
			// notes
			//

			// Compute the half distance between the current and the final point
			double dx2 = (x0 - x) / 2.0;
			double dy2 = (y0 - y) / 2.0;
			// Convert angle from degrees to radians
			angle = Math.toRadians(angle % 360.0);
			double cosAngle = Math.cos(angle);
			double sinAngle = Math.sin(angle);

			//
			// Step 1 : Compute (x1, y1)
			//
			double x1 = (cosAngle * dx2 + sinAngle * dy2);
			double y1 = (-sinAngle * dx2 + cosAngle * dy2);
			// Ensure radii are large enough
			rx = Math.abs(rx);
			ry = Math.abs(ry);
			double Prx = rx * rx;
			double Pry = ry * ry;
			double Px1 = x1 * x1;
			double Py1 = y1 * y1;
			// check that radii are large enough
			double radiiCheck = Px1 / Prx + Py1 / Pry;
			if (radiiCheck > 1) {
				rx = Math.sqrt(radiiCheck) * rx;
				ry = Math.sqrt(radiiCheck) * ry;
				Prx = rx * rx;
				Pry = ry * ry;
			}

			//
			// Step 2 : Compute (cx1, cy1)
			//
			double sign = (largeArcFlag == sweepFlag) ? -1 : 1;
			double sq = ((Prx * Pry) - (Prx * Py1) - (Pry * Px1))
					/ ((Prx * Py1) + (Pry * Px1));
			sq = (sq < 0) ? 0 : sq;
			double coef = (sign * Math.sqrt(sq));
			double cx1 = coef * ((rx * y1) / ry);
			double cy1 = coef * -((ry * x1) / rx);

			//
			// Step 3 : Compute (cx, cy) from (cx1, cy1)
			//
			double sx2 = (x0 + x) / 2.0;
			double sy2 = (y0 + y) / 2.0;
			double cx = sx2 + (cosAngle * cx1 - sinAngle * cy1);
			double cy = sy2 + (sinAngle * cx1 + cosAngle * cy1);

			//
			// Step 4 : Compute the angleStart (angle1) and the angleExtent
			// (dangle)
			//
			double ux = (x1 - cx1) / rx;
			double uy = (y1 - cy1) / ry;
			double vx = (-x1 - cx1) / rx;
			double vy = (-y1 - cy1) / ry;
			double p, n;
			// Compute the angle start
			n = Math.sqrt((ux * ux) + (uy * uy));
			p = ux; // (1 * ux) + (0 * uy)
			sign = (uy < 0) ? -1d : 1d;
			double angleStart = Math.toDegrees(sign * Math.acos(p / n));

			// Compute the angle extent
			n = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
			p = ux * vx + uy * vy;
			sign = (ux * vy - uy * vx < 0) ? -1d : 1d;
			double angleExtent = Math.toDegrees(sign * Math.acos(p / n));
			if (!sweepFlag && angleExtent > 0) {
				angleExtent -= 360f;
			} else if (sweepFlag && angleExtent < 0) {
				angleExtent += 360f;
			}
			angleExtent %= 360f;
			angleStart %= 360f;

			//
			// We can now build the resulting Arc2D in double precision
			//
			Arc2D.Double arc = new Arc2D.Double();
			arc.x = cx - rx;
			arc.y = cy - ry;
			arc.width = rx * 2.0;
			arc.height = ry * 2.0;
			arc.start = -angleStart;
			arc.extent = -angleExtent;

			return arc;
		}

		public String toString() {
			return "A " + rx + " " + ry + " " + xAxisRot + " " + largeArc + " "
					+ sweep + " " + x + " " + y;
		}
	}

	class Quadratic extends PathCommand {

		public double kx = 0f;
		public double ky = 0f;
		public double x = 0f;
		public double y = 0f;

		/** Creates a new instance of MoveTo */
		public Quadratic() {
			super(4);
		}

		public String toString() {
			return "Q " + kx + " " + ky + " " + x + " " + y;
		}

		public Quadratic(boolean isRelative, double kx, double ky, double x, double y) {
			super(4, isRelative);
			this.kx = kx;
			this.ky = ky;
			this.x = x;
			this.y = y;
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = isRelative ? hist.lastPoint.x : 0f;
			double offy = isRelative ? hist.lastPoint.y : 0f;

			path.quadTo(kx + offx, ky + offy, x + offx, y + offy);
			hist.lastPoint.set(x + offx, y + offy);
			hist.lastKnot.set(kx + offx, ky + offy);
		}

		public int getNumKnotsAdded() {
			return 4;
		}
	}

	class QuadraticSmooth extends PathCommand {

		public double x = 0f;
		public double y = 0f;

		/** Creates a new instance of MoveTo */
		public QuadraticSmooth() {
			super(4);
		}

		public String toString() {
			return "T " + x + " " + y;
		}

		public QuadraticSmooth(boolean isRelative, double x, double y) {
			super(4,isRelative);
			this.x = x;
			this.y = y;
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = isRelative ? hist.lastPoint.x : 0f;
			double offy = isRelative ? hist.lastPoint.y : 0f;

			double oldKx = hist.lastKnot.x;
			double oldKy = hist.lastKnot.y;
			double oldX = hist.lastPoint.x;
			double oldY = hist.lastPoint.y;
			// Calc knot as reflection of old knot
			double kx = oldX * 2f - oldKx;
			double ky = oldY * 2f - oldKy;

			path.quadTo(kx, ky, x + offx, y + offy);
			hist.lastPoint.set(x + offx, y + offy);
			hist.lastKnot.set(kx, ky);
		}
	}

	class Cubic extends PathCommand {

		public double k1x = 0f;
		public double k1y = 0f;
		public double k2x = 0f;
		public double k2y = 0f;
		public double x = 0f;
		public double y = 0f;

		/** Creates a new instance of MoveTo */
		public Cubic() {
			super(6);
		}

		public String toString() {
			return "C " + k1x + " " + k1y + " " + k2x + " " + k2y + " " + x + " " + y;
		}

		public Cubic(boolean isRelative, double k1x, double k1y, double k2x, double k2y, double x, double y) {
			super(6, isRelative);
			this.k1x = k1x;
			this.k1y = k1y;
			this.k2x = k2x;
			this.k2y = k2y;
			this.x = x;
			this.y = y;
		}

		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = isRelative ? hist.lastPoint.x : 0f;
			double offy = isRelative ? hist.lastPoint.y : 0f;

			path.curveTo(k1x + offx, k1y + offy, k2x + offx, k2y + offy, x + offx, y + offy);
			hist.lastPoint.set(x + offx, y + offy);
			hist.lastKnot.set(k2x + offx, k2y + offy);
		}
	}

	class CubicSmooth extends PathCommand {

		public double x = 0f;
		public double y = 0f;
		public double k2x = 0f;
		public double k2y = 0f;

		/** Creates a new instance of MoveTo */
		public CubicSmooth() {
			super(6);
		}

		public CubicSmooth(boolean isRelative, double k2x, double k2y, double x, double y) {
			super(6,isRelative);
			this.k2x = k2x;
			this.k2y = k2y;
			this.x = x;
			this.y = y;
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			double offx = isRelative ? hist.lastPoint.x : 0f;
			double offy = isRelative ? hist.lastPoint.y : 0f;

			double oldKx = hist.lastKnot.x;
			double oldKy = hist.lastKnot.y;
			double oldX = hist.lastPoint.x;
			double oldY = hist.lastPoint.y;
			// Calc knot as reflection of old knot
			double k1x = oldX * 2f - oldKx;
			double k1y = oldY * 2f - oldKy;

			path.curveTo(k1x, k1y, k2x + offx, k2y + offy, x + offx, y + offy);
			hist.lastPoint.set(x + offx, y + offy);
			hist.lastKnot.set(k2x + offx, k2y + offy);
		}

		public String toString() {
			return "S " + k2x + " " + k2y + " " + x + " " + y;
		}
	}

	class Terminal extends PathCommand {

		public Terminal() {
			super(0);
		}

		public String toString() {
			return "Z";
		}

		// public void appendPath(ExtendedGeneralPath path, BuildHistory hist)
		public void appendPath(GeneralPath path, BuildHistory hist) {
			path.closePath();
			hist.lastPoint.set(hist.startPoint.x, hist.startPoint.y);
			hist.lastKnot.set(hist.startPoint.x, hist.startPoint.y);
		}
	}
	
	private Pattern _myPattern;
	
	public CCSVGPathReader() {
		_myPattern = Pattern.compile("([MmLlHhVvAaQqTtCcSsZz])|([-+]?((\\d*\\.\\d+)|(\\d+))([eE][-+]?\\d+)?)");
	}
	

	protected PathCommand[] parsePathList(String list) {
		final Matcher matchPathCmd = _myPattern.matcher(list);

		// Tokenize
		LinkedList<String> tokens = new LinkedList<>();
		while (matchPathCmd.find()) {
			tokens.addLast(matchPathCmd.group());
		}

		LinkedList<PathCommand> cmdList = new LinkedList<>();
		char curCmd = 'Z';
		while (tokens.size() != 0) {
			String curToken = (String) tokens.removeFirst();
			char initChar = curToken.charAt(0);
			if ((initChar >= 'A' && initChar <= 'Z') || (initChar >= 'a' && initChar <= 'z')) {
				curCmd = initChar;
			} else {
				tokens.addFirst(curToken);
			}

			PathCommand cmd = null;

			switch (curCmd) {
			case 'M':
				cmd = new MoveTo(false, nextFloat(tokens), nextFloat(tokens));
				curCmd = 'L';
				break;
			case 'm':
				cmd = new MoveTo(true, nextFloat(tokens), nextFloat(tokens));
				curCmd = 'l';
				break;
			case 'L':
				cmd = new LineTo(false, nextFloat(tokens), nextFloat(tokens));
				break;
			case 'l':
				cmd = new LineTo(true, nextFloat(tokens), nextFloat(tokens));
				break;
			case 'H':
				cmd = new Horizontal(false, nextFloat(tokens));
				break;
			case 'h':
				cmd = new Horizontal(true, nextFloat(tokens));
				break;
			case 'V':
				cmd = new Vertical(false, nextFloat(tokens));
				break;
			case 'v':
				cmd = new Vertical(true, nextFloat(tokens));
				break;
			case 'A':
				cmd = new Arc(false, nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens) == 1f, nextFloat(tokens) == 1f, nextFloat(tokens), nextFloat(tokens));
				break;
			case 'a':
				cmd = new Arc(true, nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens) == 1f, nextFloat(tokens) == 1f, nextFloat(tokens), nextFloat(tokens));
				break;
			case 'Q':
				cmd = new Quadratic(false, nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
				break;
			case 'q':
				cmd = new Quadratic(true, nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
				break;
			case 'T':
				cmd = new QuadraticSmooth(false, nextFloat(tokens), nextFloat(tokens));
				break;
			case 't':
				cmd = new QuadraticSmooth(true, nextFloat(tokens), nextFloat(tokens));
				break;
			case 'C':
				cmd = new Cubic(false, nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
				break;
			case 'c':
				cmd = new Cubic(true, nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
				break;
			case 'S':
				cmd = new CubicSmooth(false, nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
				break;
			case 's':
				cmd = new CubicSmooth(true, nextFloat(tokens), nextFloat(tokens), nextFloat(tokens), nextFloat(tokens));
				break;
			case 'Z':
			case 'z':
				cmd = new Terminal();
				break;
			default:
				throw new RuntimeException("Invalid path element");
			}

			cmdList.add(cmd);
		}

		PathCommand[] retArr = new PathCommand[cmdList.size()];
		cmdList.toArray(retArr);
		return retArr;
	}
	
	 protected GeneralPath buildPath(String text, int windingRule){
        PathCommand[] commands = parsePathList(text);
        
        int numKnots = 2;
        for (int i = 0; i < commands.length; i++){
            numKnots += commands[i].getNumKnotsAdded();
        }
        
        GeneralPath path = new GeneralPath(windingRule, numKnots);
        
        BuildHistory hist = new BuildHistory();
        
        for (int i = 0; i < commands.length; i++){
            PathCommand cmd = commands[i];
            cmd.appendPath(path, hist);
        }
        
        return path;
    }
}
