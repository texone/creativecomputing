package cc.creativecomputing.graphics.shape;

import java.util.Arrays;

import cc.creativecomputing.math.CCVector2;

/**
 * The {@code GeneralPath} class represents a geometric path
 * constructed from straight lines, and quadratic and cubic
 * (Bezier) curves.  It can contain multiple subpaths.
 * <p>
 * {@code GeneralPath} is a legacy final class which exactly
 * implements the behavior of its superclass {@link Path2D.Float}.
 * Together with {@link Path2D.Double}, the {@link Path2D} classes
 * provide full implementations of a general geometric path that
 * support all of the functionality of the {@link Shape} and
 * {@link PathIterator} interfaces with the ability to explicitly
 * select different levels of internal coordinate precision.
 * <p>
 * Use {@code Path2D.Float} (or this legacy {@code GeneralPath}
 * subclass) when dealing with data that can be represented
 * and used with floating point precision.  Use {@code Path2D.Double}
 * for data that requires the accuracy or range of double precision.
 *
 * @author Jim Graham
 * @since 1.2
 */
public final class CCPath2D  {
	
	/**
     * An even-odd winding rule for determining the interior of
     * a path.
     *
     * @see PathIterator#WIND_EVEN_ODD
     * @since 1.6
     */
    public static final int WIND_EVEN_ODD = 0;

    /**
     * A non-zero winding rule for determining the interior of a
     * path.
     *
     * @see PathIterator#WIND_NON_ZERO
     * @since 1.6
     */
    public static final int WIND_NON_ZERO = 1;

    // For code simplicity, copy these constants to our namespace
    // and cast them to byte constants for easy storage.
    private static final byte SEG_MOVETO  = (byte) 0;
    private static final byte SEG_LINETO  = (byte) 1;
    private static final byte SEG_QUADTO  = (byte) 2;
    private static final byte SEG_CUBICTO = (byte) 3;
    private static final byte SEG_CLOSE   = (byte) 4;

    transient byte[] pointTypes;
    transient int numTypes;
    transient int numCoords;
    transient int windingRule;

    static final int INIT_SIZE = 20;
    static final int EXPAND_MAX = 500;
    
    double _myCoords[];
    
    /**
     * Constructs a new empty single precision {@code GeneralPath} object
     * with a default winding rule of {@link #WIND_NON_ZERO}.
     *
     * @since 1.2
     */
    public CCPath2D() {
        setWindingRule(WIND_NON_ZERO);
        this.pointTypes = new byte[INIT_SIZE];
    }

    /**
     * Constructs a new <code>GeneralPath</code> object with the specified
     * winding rule to control operations that require the interior of the
     * path to be defined.
     *
     * @param rule the winding rule
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     * @since 1.2
     */
    public CCPath2D(int rule) {
        setWindingRule(rule);
        this.pointTypes = new byte[INIT_SIZE];
    }

    /**
     * Constructs a new <code>GeneralPath</code> object with the specified
     * winding rule and the specified initial capacity to store path
     * coordinates.
     * This number is an initial guess as to how many path segments
     * will be added to the path, but the storage is expanded as
     * needed to store whatever path segments are added.
     *
     * @param rule the winding rule
     * @param initialCapacity the estimate for the number of path segments
     *                        in the path
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     * @since 1.2
     */
    public CCPath2D(int rule, int initialCapacity) {
        setWindingRule(WIND_NON_ZERO);
        this.pointTypes = new byte[INIT_SIZE];
    }

    CCPath2D(int windingRule,
                byte[] pointTypes,
                int numTypes,
                double[] pointCoords,
                int numCoords)
    {
        // used to construct from native

        this.windingRule = windingRule;
        this.pointTypes = pointTypes;
        this.numTypes = numTypes;
        this._myCoords = pointCoords;
        this.numCoords = numCoords;
    }

    void append(double x, double y) {
        _myCoords[numCoords++] = x;
        _myCoords[numCoords++] = y;
    }

    CCVector2 getPoint(int coordindex) {
        return new CCVector2(_myCoords[coordindex],
                                 _myCoords[coordindex+1]);
    }

    void needRoom(boolean needMove, int newCoords) {
        if (needMove && numTypes == 0) {
            throw new RuntimeException("missing initial moveto "+
                                                "in path definition");
        }
        int size = pointTypes.length;
        if (numTypes >= size) {
            int grow = size;
            if (grow > EXPAND_MAX) {
                grow = EXPAND_MAX;
            } else if (grow == 0) {
                grow = 1;
            }
            pointTypes = Arrays.copyOf(pointTypes, size+grow);
        }
        size = _myCoords.length;
        if (numCoords + newCoords > size) {
            int grow = size;
            if (grow > EXPAND_MAX * 2) {
                grow = EXPAND_MAX * 2;
            }
            if (grow < newCoords) {
                grow = newCoords;
            }
            _myCoords = Arrays.copyOf(_myCoords, size+grow);
        }
    }

    /**
     * Adds a point to the path by moving to the specified
     * coordinates specified in double precision.
     * <p>
     * This method provides a single precision variant of
     * the double precision {@code moveTo()} method on the
     * base {@code Path2D} class.
     *
     * @param x the specified X coordinate
     * @param y the specified Y coordinate
     * @see Path2D#moveTo
     * @since 1.6
     */
    public final synchronized void moveTo(double x, double y) {
        if (numTypes > 0 && pointTypes[numTypes - 1] == SEG_MOVETO) {
            _myCoords[numCoords-2] = x;
            _myCoords[numCoords-1] = y;
        } else {
            needRoom(false, 2);
            pointTypes[numTypes++] = SEG_MOVETO;
            _myCoords[numCoords++] = x;
            _myCoords[numCoords++] = y;
        }
    }

    /**
     * Adds a point to the path by drawing a straight line from the
     * current coordinates to the new specified coordinates
     * specified in double precision.
     * <p>
     * This method provides a single precision variant of
     * the double precision {@code lineTo()} method on the
     * base {@code Path2D} class.
     *
     * @param x the specified X coordinate
     * @param y the specified Y coordinate
     * @see Path2D#lineTo
     * @since 1.6
     */
    public final synchronized void lineTo(double x, double y) {
        needRoom(true, 2);
        pointTypes[numTypes++] = SEG_LINETO;
        _myCoords[numCoords++] = x;
        _myCoords[numCoords++] = y;
    }

    /**
     * Adds a curved segment, defined by two new points, to the path by
     * drawing a Quadratic curve that intersects both the current
     * coordinates and the specified coordinates {@code (x2,y2)},
     * using the specified point {@code (x1,y1)} as a quadratic
     * parametric control point.
     * All coordinates are specified in double precision.
     * <p>
     * This method provides a single precision variant of
     * the double precision {@code quadTo()} method on the
     * base {@code Path2D} class.
     *
     * @param x1 the X coordinate of the quadratic control point
     * @param y1 the Y coordinate of the quadratic control point
     * @param x2 the X coordinate of the final end point
     * @param y2 the Y coordinate of the final end point
     * @see Path2D#quadTo
     * @since 1.6
     */
    public final synchronized void quadTo(double x1, double y1,
                                          double x2, double y2)
    {
        needRoom(true, 4);
        pointTypes[numTypes++] = SEG_QUADTO;
        _myCoords[numCoords++] = x1;
        _myCoords[numCoords++] = y1;
        _myCoords[numCoords++] = x2;
        _myCoords[numCoords++] = y2;
    }

    /**
     * Adds a curved segment, defined by three new points, to the path by
     * drawing a Bezier curve that intersects both the current
     * coordinates and the specified coordinates {@code (x3,y3)},
     * using the specified points {@code (x1,y1)} and {@code (x2,y2)} as
     * Bezier control points.
     * All coordinates are specified in double precision.
     * <p>
     * This method provides a single precision variant of
     * the double precision {@code curveTo()} method on the
     * base {@code Path2D} class.
     *
     * @param x1 the X coordinate of the first Bezier control point
     * @param y1 the Y coordinate of the first Bezier control point
     * @param x2 the X coordinate of the second Bezier control point
     * @param y2 the Y coordinate of the second Bezier control point
     * @param x3 the X coordinate of the final end point
     * @param y3 the Y coordinate of the final end point
     * @see Path2D#curveTo
     * @since 1.6
     */
    public final synchronized void curveTo(double x1, double y1,
                                           double x2, double y2,
                                           double x3, double y3)
    {
        needRoom(true, 6);
        pointTypes[numTypes++] = SEG_CUBICTO;
        _myCoords[numCoords++] = x1;
        _myCoords[numCoords++] = y1;
        _myCoords[numCoords++] = x2;
        _myCoords[numCoords++] = y2;
        _myCoords[numCoords++] = x3;
        _myCoords[numCoords++] = y3;
    }
    
    /**
     * Closes the current subpath by drawing a straight line back to
     * the coordinates of the last {@code moveTo}.  If the path is already
     * closed then this method has no effect.
     *
     * @since 1.6
     */
    public final synchronized void closePath() {
        if (numTypes == 0 || pointTypes[numTypes - 1] != SEG_CLOSE) {
            needRoom(true, 0);
            pointTypes[numTypes++] = SEG_CLOSE;
        }
    }
    
    /**
     * Returns the fill style winding rule.
     *
     * @return an integer representing the current winding rule.
     * @see #WIND_EVEN_ODD
     * @see #WIND_NON_ZERO
     * @see #setWindingRule
     * @since 1.6
     */
    public final synchronized int getWindingRule() {
        return windingRule;
    }

    /**
     * Sets the winding rule for this path to the specified value.
     *
     * @param rule an integer representing the specified
     *             winding rule
     * @exception IllegalArgumentException if
     *          {@code rule} is not either
     *          {@link #WIND_EVEN_ODD} or
     *          {@link #WIND_NON_ZERO}
     * @see #getWindingRule
     * @since 1.6
     */
    public final void setWindingRule(int rule) {
        if (rule != WIND_EVEN_ODD && rule != WIND_NON_ZERO) {
            throw new IllegalArgumentException("winding rule must be "+
                                               "WIND_EVEN_ODD or "+
                                               "WIND_NON_ZERO");
        }
        windingRule = rule;
    }
    
    /**
     * Returns the coordinates most recently added to the end of the path
     * as a {@link CCVector2} object.
     *
     * @return a {@code CCVector2} object containing the ending coordinates of
     *         the path or {@code null} if there are no points in the path.
     * @since 1.6
     */
    public CCVector2 getCurrentPoint() {
        int index = numCoords;
        if (numTypes < 1 || index < 1) {
            return null;
        }
        if (pointTypes[numTypes - 1] == SEG_CLOSE) {
        loop:
            for (int i = numTypes - 2; i > 0; i--) {
                switch (pointTypes[i]) {
                case SEG_MOVETO:
                    break loop;
                case SEG_LINETO:
                    index -= 2;
                    break;
                case SEG_QUADTO:
                    index -= 4;
                    break;
                case SEG_CUBICTO:
                    index -= 6;
                    break;
                case SEG_CLOSE:
                    break;
                }
            }
        }
        return getPoint(index - 2);
    }

    /**
     * Resets the path to empty.  The append position is set back to the
     * beginning of the path and all coordinates and point types are
     * forgotten.
     *
     * @since 1.6
     */
    public final synchronized void reset() {
        numTypes = numCoords = 0;
    }
}