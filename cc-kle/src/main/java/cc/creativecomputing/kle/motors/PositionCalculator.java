package cc.creativecomputing.kle.motors;

import cc.creativecomputing.math.CCVector2;

/// <summary>
/// 	  			ab
///	A	+ ----------------- + B
///		 \				   /
///	  al  \				  / br
///		   \			 /
///			\	   e	/
///		   L + ------- +  R
///         f  \	 /  g
///				\   /
///               +  S
/// </summary>
public class PositionCalculator {
	public static final CCVector2 InvalidVector2D = new CCVector2(Double.MAX_VALUE, Double.MAX_VALUE);
	public static final CCVector2[] InvalidRopeConnectionPositions = { InvalidVector2D, InvalidVector2D };
	public static final double[] InvalidRopeLength = { Double.MAX_VALUE, Double.MAX_VALUE };

	public static CCVector2 ComputePosition(
		double leftRope, 
		double rightRope, 
		double distancePulleys,
		double distancePetalConnection, 
		double distanceLeftBarycenter, 
		double distanceRightBarycenter,
		double arcAtABetweenLeftRopeAndXAxis
	) {
		double al = leftRope;
		double br = rightRope;
		double ab = distancePulleys;
		double e = distancePetalConnection;
		double f = distanceLeftBarycenter;
		double g = distanceRightBarycenter;
		double arcA = arcAtABetweenLeftRopeAndXAxis;

		double al_sq = al * al;
		double ab_sq = ab * ab;
		double c = Math.sqrt(ab_sq + al_sq - (2 * ab * al * Math.cos(arcA)));
		double c_sq = (c * c);
		double cos_arc_c_ab = (al_sq - ab_sq - c_sq) / (-2 * ab * c);
		if (cos_arc_c_ab < -1 || cos_arc_c_ab > 1) {
			return InvalidVector2D;
		}
		double e_sq = e * e;
		double br_sq = br * br;

		double cos_arc_c_br = (e_sq - br_sq - c_sq) / (-2 * c * br);
		if (cos_arc_c_br < -1 || cos_arc_c_br > 1) {
			return InvalidVector2D;
		}
		double arc_c_ab = Math.acos(cos_arc_c_ab);
		double arc_c_br = Math.acos(cos_arc_c_br);
		double arc_e_f = Math.acos((g * g - f * f - e_sq) / (-2 * e * f));
		double arc_e_br = Math.acos((c_sq - br_sq - e_sq) / (-2 * e * br));
		double arc_f_x = Math.PI - arc_c_ab - arc_c_br - arc_e_br + arc_e_f;

		return new CCVector2(
			-ab / 2 + al * Math.cos(arcA) + f * Math.cos(arc_f_x),
			0 - al * Math.sin(arcA) - f * Math.sin(arc_f_x)
		);
	}

	public static CCVector2 MinimizePosition(
		double leftRope, 
		double rightRope, 
		double distancePulleys,
		double distancePetalConnection, 
		double distanceLeftBarycenter, 
		double distanceRightBarycenter,
		double precision
	) {
		double divider = 50;
		double arcA = Math.PI;
		double arcPrevA = 0;
		double arcPrevPrevA = 0;
		CCVector2 currentPos = new CCVector2(0, 0);
		CCVector2 prevPos2d = new CCVector2(0, 0);
		CCVector2 prevPrevPos2d = new CCVector2(0, 0);
		CCVector2 minPosition2d = new CCVector2(0, 1);
		
		while (Math.abs(currentPos.y - minPosition2d.y) > precision) {

			// divider = divider*10;
			double currentMaxArcA = arcA;
			arcA = arcPrevPrevA;
			arcPrevA = arcPrevPrevA;
			double delta = (currentMaxArcA - arcA) / divider;
			currentPos = prevPrevPos2d;
			prevPos2d = prevPrevPos2d;
			while (arcA <= currentMaxArcA && currentPos.y <= prevPos2d.y) {
				prevPrevPos2d = prevPos2d;
				prevPos2d = currentPos;
				arcPrevPrevA = arcPrevA;
				arcPrevA = arcA;
				arcA = arcA + delta;
				currentPos = ComputePosition(
					leftRope, 
					rightRope, 
					distancePulleys, 
					distancePetalConnection,
					distanceLeftBarycenter, 
					distanceRightBarycenter, 
					arcA
				);
				if (currentPos == InvalidVector2D || currentPos.y > 0) {
					currentPos = prevPos2d;
				}
			}
			divider = 5;
			if (currentPos.y > prevPos2d.y) {
				minPosition2d = prevPos2d;
			} else {
				minPosition2d = currentPos;
			}
		}
		if (minPosition2d.y == 1)
			return InvalidVector2D;
		return minPosition2d;
	}

	public static CCVector2[] CalculateRopeConnectPositions(
		CCVector2 position, 
		double distancePetalConnection,
		double distanceLeftBarycenter, 
		double distanceRightBarycenter, 
		double arcAtSBetweenXAxisAndF
	) {
		CCVector2[] result = new CCVector2[2];
		CCVector2 l = new CCVector2(
			position.x - distanceLeftBarycenter * Math.cos(arcAtSBetweenXAxisAndF),
			position.y + distanceLeftBarycenter * Math.sin(arcAtSBetweenXAxisAndF)
		);
		result[0] = l;
		double asin = distancePetalConnection / 2 / distanceLeftBarycenter;
		if (asin < -1 || asin > 1)
			return InvalidRopeConnectionPositions;
		
		// TODO:check if this holds for all elements
		double arcAtSBetweenFAndG = 2 * Math.asin(distancePetalConnection / 2 / distanceLeftBarycenter);
		CCVector2 r = new CCVector2(
			position.x - distanceLeftBarycenter * Math.cos(arcAtSBetweenXAxisAndF + arcAtSBetweenFAndG),
			position.y + distanceLeftBarycenter * Math.sin(arcAtSBetweenXAxisAndF + arcAtSBetweenFAndG)
		);
		result[1] = r;
		return result;
	}

	public static double[] FindRopeLength(
		CCVector2 position, 
		double distancePulleys,
		double distancePetalConnection, 
		double distanceLeftBarycenter,
		double distanceRightBarycenter, 
		double precision
	){
		double currentValue = 0;
		double targetValue = (position.x + distancePulleys/2)/(distancePulleys/2 - position.x);
		CCVector2 a = new CCVector2(-distancePulleys/2, 0);
		CCVector2 b = new CCVector2(distancePulleys/2, 0);
		double divider = 100;
		double currentArc = Math.PI/2;
		double prevValue = 0;
		double prevArc = 0;
		CCVector2 l = new CCVector2(0,0);
		CCVector2 r = new CCVector2(0,0);
		while (Math.abs(currentValue-targetValue) > precision){
			double maxArc = currentArc;
			currentArc = prevArc;
			currentValue = prevValue;
			double currentDelta = (maxArc-currentArc) / divider;
			while (currentArc < maxArc && (currentValue < targetValue)){
                    prevArc = currentArc;
                    currentArc += currentDelta;
                    prevValue = currentValue;
                    CCVector2[] lrVectors = CalculateRopeConnectPositions(
                    	position,
                        distancePetalConnection, 
                        distanceLeftBarycenter, 
                        distanceRightBarycenter, 
                        currentArc
                    );
                    if (lrVectors == InvalidRopeConnectionPositions) return new double[] {0.0, 0.0};
                    l = lrVectors[0];
                    r = lrVectors[1];
                    double tanAlpha = Math.abs(l.x - a.x)/(l.y - a.y);
                    double tanBeta = Math.abs(r.x - b.x)/(r.y - b.y);
                    currentValue = tanAlpha/tanBeta;
                }
                
                divider = 10;
            }
            return new double[] {l.distance(a), r.distance(b)};
        }

	private static boolean IsInside(CCVector2 pos, CCVector2 upL, CCVector2 loL, CCVector2 upR, CCVector2 loR) {
		if (pos.y > upL.y || pos.y < loL.y || pos.x < upL.x || pos.x > upR.x)
			return false;
		if (pos.x < loL.x && IsCheckLinear(pos, loL, upL, LowerCheck))
			return false;
		if (pos.x > loR.x && IsCheckLinear(pos, loR, upR, UpperCheck))
			return false;
		return true;
	}
	
	private static interface Check{
		public boolean check(double xPos, double checkPos);
	}

	private static boolean IsCheckLinear(CCVector2 pos, CCVector2 loL, CCVector2 upL, Check check){
		double currentVal = pos.y - loL.y;
		double fraction = (upL.x - loL.x) / (upL.y - loL.y);
		double val = loL.x + currentVal * fraction;
		return check.check(pos.x, val);
	}

	private static Check LowerCheck = new Check(){

		@Override
		public boolean check(double xPos, double checkPos) {
			return xPos < checkPos;
		}
		
	};
	
	private static Check UpperCheck = new Check(){

		@Override
		public boolean check(double xPos, double checkPos) {
			return xPos > checkPos;
		}
		
	};

	public static double GetMinRopeLengthToFitTrapez(
		double leftRopeLength, 
		double rightRopeLength,
		double minRopeLength, 
		CCVector2 a, 
		CCVector2 b, 
		CCVector2 c, 
		CCVector2 d, 
		double precision,
		int distancePulleys, 
		int distancePetalConnection, 
		int distanceBarycenter
	) {
		double newRightLen = rightRopeLength - (rightRopeLength - minRopeLength) / 2;

		if (rightRopeLength - newRightLen < precision)
			return rightRopeLength;

		CCVector2 curPos = MinimizePosition(leftRopeLength, newRightLen, distancePulleys, distancePetalConnection,
				distanceBarycenter, distanceBarycenter, precision);
		if (curPos != InvalidVector2D && IsInside(curPos, a, b, c, d)) {
			return GetMinRopeLengthToFitTrapez(leftRopeLength, newRightLen, minRopeLength, a, b, c, d, precision, distancePulleys, distancePetalConnection, distanceBarycenter);
		}
		return GetMinRopeLengthToFitTrapez(leftRopeLength, rightRopeLength, newRightLen, a, b, c, d, precision, distancePulleys, distancePetalConnection, distanceBarycenter);
	}

	public static double GetMaxRopeLengthToFitTrapez(
		double leftRopeLength, 
		double rightRopeLength,
		double maxRopeLength, 
		CCVector2 a, 
		CCVector2 b, 
		CCVector2 c, 
		CCVector2 d, 
		double precision,
		int distancePulleys, 
		int distancePetalConnection, 
		int distanceBarycenter
	) {
		double newRightLen = rightRopeLength + (maxRopeLength - rightRopeLength) / 2;

		if (newRightLen - rightRopeLength < precision)
			return rightRopeLength;

		CCVector2 curPos = MinimizePosition(leftRopeLength, newRightLen, distancePulleys, distancePetalConnection,
				distanceBarycenter, distanceBarycenter, precision);
		if (curPos != InvalidVector2D && IsInside(curPos, a, b, c, d)) {
			return GetMaxRopeLengthToFitTrapez(leftRopeLength, newRightLen, maxRopeLength, a, b, c, d, precision,
					distancePulleys, distancePetalConnection, distanceBarycenter);
		}
		return GetMaxRopeLengthToFitTrapez(leftRopeLength, rightRopeLength, newRightLen, a, b, c, d, precision,
				distancePulleys, distancePetalConnection, distanceBarycenter);
	}
}