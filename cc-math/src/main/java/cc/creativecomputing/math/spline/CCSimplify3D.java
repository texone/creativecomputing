package cc.creativecomputing.math.spline;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import cc.creativecomputing.math.CCVector3;

/**
 * Simplification of a 3D-polyline.
 *
 * @author hgoebl
 * @since 06.07.13
 */
public class CCSimplify3D {

	static public double getSquareSegmentDistance(CCVector3 p0, CCVector3 p1, CCVector3 p2) {

		double x1 = p1.x;
		double y1 = p1.y;
		double z1 = p1.z;
		double x2 = p2.x;
		double y2 = p2.y;
		double z2 = p2.z;
		double x0 = p0.x;
		double y0 = p0.y;
		double z0 = p0.z;

		double dx = x2 - x1;
		double dy = y2 - y1;
		double dz = z2 - z1;

		if (dx != 0.0d || dy != 0.0d || dz != 0.0d) {
			double t = ((x0 - x1) * dx + (y0 - y1) * dy + (z0 - z1) * dz) / (dx * dx + dy * dy + dz * dz);

			if (t > 1.0d) {
				x1 = x2;
				y1 = y2;
				z1 = z2;
			} else if (t > 0.0d) {
				x1 += dx * t;
				y1 += dy * t;
				z1 += dz * t;
			}
		}

		dx = x0 - x1;
		dy = y0 - y1;
		dz = z0 - z1;

		return dx * dx + dy * dy + dz * dz;
	}

	/**
	 * Simplifies a list of points to a shorter list of points.
	 * 
	 * @param points         original list of points
	 * @param tolerance      tolerance in the same measurement as the point
	 *                       coordinates
	 * @param highestQuality <tt>true</tt> for using Douglas-Peucker only,
	 *                       <tt>false</tt> for using Radial-Distance algorithm
	 *                       before applying Douglas-Peucker (should be a bit
	 *                       faster)
	 * @return simplified list of points
	 */
	public static List<CCVector3> simplify(List<CCVector3> points, double tolerance, boolean highestQuality) {

		if (points == null || points.size() <= 2) {
			return points;
		}

		double sqTolerance = tolerance * tolerance;

		if (!highestQuality) {
			points = simplifyRadialDistance(points, sqTolerance);
		}

		points = simplifyDouglasPeucker(points, sqTolerance);

		return points;
	}

	static public List<CCVector3> simplifyRadialDistance(List<CCVector3> points, double sqTolerance) {
		CCVector3 point = null;
		CCVector3 prevPoint = points.get(0);

		List<CCVector3> newPoints = new ArrayList<>();
		newPoints.add(prevPoint);

		for (int i = 1; i < points.size(); ++i) {
			point = points.get(i);

			if (point.distanceSquared(prevPoint) > sqTolerance) {
				newPoints.add(point);
				prevPoint = point;
			}
		}

		if (prevPoint != point) {
			newPoints.add(point);
		}

		return newPoints;
	}

	static class Range {
		private Range(int first, int last) {
			this.first = first;
			this.last = last;
		}

		int first;
		int last;
	}

	static public List<CCVector3> simplifyDouglasPeucker(List<CCVector3> points, double sqTolerance) {

		BitSet bitSet = new BitSet(points.size());
		bitSet.set(0);
		bitSet.set(points.size() - 1);

		List<Range> stack = new ArrayList<Range>();
		stack.add(new Range(0, points.size() - 1));

		while (!stack.isEmpty()) {
			Range range = stack.remove(stack.size() - 1);

			int index = -1;
			double maxSqDist = 0f;

			// find index of point with maximum square distance from first and last point
			for (int i = range.first + 1; i < range.last; ++i) {
				double sqDist = getSquareSegmentDistance(points.get(i), points.get(range.first),
						points.get(range.last));

				if (sqDist > maxSqDist) {
					index = i;
					maxSqDist = sqDist;
				}
			}

			if (maxSqDist > sqTolerance) {
				bitSet.set(index);

				stack.add(new Range(range.first, index));
				stack.add(new Range(index, range.last));
			}
		}

		List<CCVector3> newPoints = new ArrayList<>(bitSet.cardinality());
		for (int index = bitSet.nextSetBit(0); index >= 0; index = bitSet.nextSetBit(index + 1)) {
			newPoints.add(points.get(index));
		}

		return newPoints;
	}

}