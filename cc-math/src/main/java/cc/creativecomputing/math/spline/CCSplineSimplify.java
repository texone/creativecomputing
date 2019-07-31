package cc.creativecomputing.math.spline;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCSplineSimplify {
	public static List<CCVector3> simplify(List<CCVector3> points, double tolerance) {
		double sqTolerance = tolerance * tolerance;

		return simplify(points, sqTolerance, true);
	}

	public static List<CCVector3> simplify(List<CCVector3> points, double tolerance, boolean highestQuality) {
		double sqTolerance = tolerance * tolerance;

		if (!highestQuality)
			return simplifyRadialDistance(points, sqTolerance);

		return simplifyDouglasPeucker(points, sqTolerance);
	}

	// distance-based simplification
	private static List<CCVector3> simplifyRadialDistance(List<CCVector3> thePoints, double theSquareTolerance) {

		CCVector3 point = new CCVector3();
		CCVector3 prevPoint = thePoints.get(0);

		List<CCVector3> newPoints = new ArrayList<>();
		newPoints.add(prevPoint);

		for (int i = 1; i < thePoints.size(); i++) {
			point = thePoints.get(i);

			if (point.distanceSquared(prevPoint) <= theSquareTolerance) continue;
			
			newPoints.add(point);
			prevPoint = point;
		}

		if (!prevPoint.equals(point)) {
			newPoints.add(point);
		}

		return newPoints;
	}

	// simplification using optimized Douglas-Peucker algorithm with recursion
	// elimination
	private static List<CCVector3> simplifyDouglasPeucker(List<CCVector3> thePoints, double theSquareTolerance) {
		
		int[] markers = new int[thePoints.size()];

		int first = 0;
		int last = thePoints.size() - 1;

		double maxSqDist;
		double sqDist;
		int index = 0;

		List<Integer> firstStack = new ArrayList<>();
		List<Integer> lastStack = new ArrayList<>();

		List<CCVector3> newPoints = new ArrayList<>();

		markers[first] = markers[last] = 1;

		while (last != -1) {
			maxSqDist = 0;

			for (int i = first + 1; i < last; i++) {
				sqDist = getSquareSegmentDistance(thePoints.get(i), thePoints.get(first), thePoints.get(last));

				if (sqDist > maxSqDist) {
					index = i;
					maxSqDist = sqDist;
				}
			}

			if (maxSqDist > theSquareTolerance) {
				markers[index] = 1;

				firstStack.add(first);
				lastStack.add(index);

				firstStack.add(index);
				lastStack.add(last);
			}

			if (firstStack.size() > 1)
				first = firstStack.remove(firstStack.size() - 1);
			else
				first = -1;

			if (lastStack.size() > 1)
				last = lastStack.remove(lastStack.size() - 1);
			else
				last = -1;
		}

		for (int i = 0; i < thePoints.size(); i++) {
			if (markers[i] != -1)
				newPoints.add(thePoints.get(i));
		}

		return newPoints;
	}

	// square distance from a point to a segment
	private static double getSquareSegmentDistance(CCVector3 theP0, CCVector3 theP1, CCVector3 theP2) {
		double x = theP1.x;
		double y = theP1.y;
		double z = theP1.z;

		double dx = theP2.x - x;
		double dy = theP2.y - y;
		double dz = theP2.z - z;

		double t;

		if (dx != 0 || dy != 0 || dz != 0) {
			t = ((theP0.x - x) * dx + (theP0.y - y) * dy) + (theP0.z - z) * dz / (dx * dx + dy * dy + dz * dz);

			if (t > 1) {
				x = theP2.x;
				y = theP2.y;
				z = theP2.z;

			} else if (t > 0) {
				x += dx * t;
				y += dy * t;
				z += dz * t;
			}
		}

		dx = theP0.x - x;
		dy = theP0.y - y;
		dz = theP0.z - z;

		return dx * dx + dy * dy + dz * dz;
	}
}