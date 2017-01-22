package cc.creativecomputing.demo.gl2.postprocess;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector2;

/**
 * Deferred shading is a technique where a 3D scene's geometry data is rendered
 * into screen space and shading is deferred until a second pass when lights are
 * drawn.
 * <p>
 * This scene is rendered into a frame buffer with multiple attachments
 * (G-buffer). Uniform buffer objects are used to store a database of material
 * and light data on the GPU; reducing drawing overhead. Shadow casters are
 * rendered into a shadow map FBO. The buffers are read while drawing light
 * volumes into the light buffer (L-buffer) to create the shaded scene. Then
 * shadows are subtracted from the image.
 * <p>
 * An ambient occlusion (AO) pass provides extra shading detail. Volumetric
 * light scattering broadcasts rays from our primary light. Lights are
 * accumulated to leave subtle trails, then bloomed to appear that they are
 * glowing. We follow these with some post-processing passes, including depth of
 * field to mimic camera focus, color tweaks, and anti-aliasing.
 * 
 * @author christianr
 *
 */
public class CCDeferred {

	public void draw(CCGraphics g) {
		double f = g.camera().far();
		double n = g.camera().near();
		CCVector2 projectionParams = new CCVector2(f / (f - n), (-f * n) / (f - n));
		CCMatrix4x4 projMatrixInverse = g.camera().projectionMatrix().invert();
	}
}
