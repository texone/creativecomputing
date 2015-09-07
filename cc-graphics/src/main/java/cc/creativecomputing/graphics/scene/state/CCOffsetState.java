package cc.creativecomputing.graphics.scene.state;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLPolygonMode;

/**
 * When {@linkplain #fillEnabled}, {@linkplain #lineEnabled}, or
 * {@linkplain #pointEnabled()} is true, each fragment's depth value
 * will be offset after it is interpolated from the depth values of the
 * appropriate vertices. The value of the offset is
 * <code>factor * DZ + r * units</code>, where DZ is a measurement of the change
 * in depth relative to the screen area of the polygon, and r is the smallest
 * value that is guaranteed to produce a resolvable offset for a given
 * implementation. The offset is added before the depth test is performed and
 * before the value is written into the depth buffer.
 * <p>
 * CCOffsetState is useful for rendering hidden-line images, for applying decals
 * to surfaces, and for rendering solids with highlighted edges.
 * 
 * @author christianr
 * 
 */
public class CCOffsetState extends CCGlobalState {
	
	public static final CCOffsetState DEFAULT = new CCOffsetState();

	/**
	 * If true, and if the polygon is rendered in {@linkplain GLPolygonMode#FILL} mode, 
	 * an offset is added to depth values of a polygon's fragments before the depth comparison is performed.
	 */
	public boolean fillEnabled = false;
	/**
	 * If true, and if the polygon is rendered in {@linkplain GLPolygonMode#LINE} mode, 
	 * an offset is added to depth values of a polygon's fragments before the depth comparison is performed.
	 */
	public boolean lineEnabled = false;
	/**
	 * If true, and if the polygon is rendered in {@linkplain GLPolygonMode#POINT} mode, 
	 * an offset is added to depth values of a polygon's fragments before the depth comparison is performed.
	 */
	public boolean pointEnabled = false;

	//
	/**
	 * The offset is scale * dZ + bias*r where dZ is the change in depth
	 * relative to the screen space area of the poly, and r is the smallest
	 * resolvable depth difference. Negative values move polygons closer to the
	 * eye. default: 0
	 */
	public float scale = 0; //

	/**
	 * @see #scale
	 */
	public float bias = 0;

	// Construction and destruction.
	public CCOffsetState() {
		super(CCGlobalStateType.OFFSET);
	}

	@Override
	public void draw(GLGraphics g) {

		if (fillEnabled) {
			g.polygonOffsetFill();
		} else {
			g.noPolygonOffsetFill();
		}

		if (lineEnabled) {
			g.polygonOffsetLine();
		} else {
			g.noPolygonOffsetLINE();
		}

		if (pointEnabled) {
			g.polygonOffsetPoint();
		} else {
			g.noPolygonOffsetPoint();
		}

		g.polygonOffset(scale, bias);
	}
}
