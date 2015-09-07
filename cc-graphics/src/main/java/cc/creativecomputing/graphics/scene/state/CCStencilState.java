package cc.creativecomputing.graphics.scene.state;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLCompareFunction;
import cc.creativecomputing.gl4.GLGraphics.GLStencilOperation;

/**
 * <p>
 * Stenciling, like depth-buffering, enables and disables drawing on a per-pixel
 * basis. Stencil planes are first drawn into using GL drawing primitives, then
 * geometry and images are rendered using the stencil planes to mask out
 * portions of the screen. Stenciling is typically used in multipass rendering
 * algorithms to achieve special effects, such as decals, outlining, and
 * constructive solid geometry rendering.
 * </p>
 * <p>
 * The stencil test conditionally eliminates a pixel based on the outcome of a
 * comparison between the reference value and the value in the stencil buffer.
 * To enable and disable the test, call {@linkplain #stencilTest()} and
 * {@linkplain #noStencilTest()}. To specify actions based on the outcome of the
 * stencil test, call glStencilOp or glStencilOpSeparate.
 * </p>
 * <p>
 * There can be two separate sets of func, ref, and mask parameters; one affects
 * back-facing polygons, and the other affects front-facing polygons as well as
 * other non-polygon primitives. glStencilFunc sets both front and back stencil
 * state to the same values. Use glStencilFuncSeparate to set front and back
 * stencil state to different values.
 * </p>
 * <p>
 * func is a symbolic constant that determines the stencil comparison function.
 * It accepts one of eight values, shown in the following list. ref is an
 * integer reference value that is used in the stencil comparison. It is clamped
 * to the range 0 2 n - 1 , where n is the number of bitplanes in the stencil
 * buffer. mask is bitwise ANDed with both the reference value and the stored
 * stencil value, with the ANDed values participating in the comparison.
 * </p>
 * <p>
 * If stencil represents the value stored in the corresponding stencil buffer
 * location, the following list shows the effect of each comparison function
 * that can be specified by func. Only if the comparison succeeds is the pixel
 * passed through to the next stage in the rasterization process (see
 * glStencilOp). All tests treat stencil values as unsigned integers in the
 * range 0 2 n - 1 , where n is the number of bitplanes in the stencil buffer.
 * </p>
 * 
 * @param theFunc
 *            Specifies the test function. Eight symbolic constants are valid:
 * @param theRef
 *            Specifies the reference value for the stencil test. ref is clamped
 *            to the range 0 2 n - 1 , where n is the number of bitplanes in the
 *            stencil buffer. The initial value is 0.
 * @param theMask
 *            Specifies a mask that is ANDed with both the reference value and
 *            the stored stencil value when the test is done. The initial value
 *            is all 1's.
 * 
 * @author christianr
 * 
 */
public class CCStencilState extends CCGlobalState {
	
	public static final CCStencilState DEFAULT = new CCStencilState();
	/**
	 * default: false
	 */
	public boolean enabled = false; // default: false

	/**
	 * default: {@linkplain GLCompareFunction#NEVER}
	 */
	public GLCompareFunction compare = GLCompareFunction.NEVER;

	/**
	 * default: 0
	 */
	public int reference;

	/**
	 * default: 0xFFFFFFFF
	 */
	public int mask = 0xFFFFFFFF;

	/**
	 * default: 0xFFFFFFFF
	 */
	public int writeMask = 0xFFFFFFFF;

	/**
	 * default: {@linkplain GLStencilOperation#KEEP}
	 */
	public GLStencilOperation onFail = GLStencilOperation.KEEP;

	/**
	 * default: {@linkplain GLStencilOperation#KEEP}
	 */
	public GLStencilOperation onZFail = GLStencilOperation.KEEP;

	/**
	 * default: {@linkplain GLStencilOperation#KEEP}
	 */
	public GLStencilOperation onZPass = GLStencilOperation.KEEP;

	public CCStencilState() {
		super(CCGlobalStateType.STENCIL);
	}

	@Override
	public void draw(GLGraphics g) {
		if (enabled) {
			g.stencilTest();

			g.stencilFunc(compare, reference, mask);
			g.stencilMask(writeMask);
			g.stencilOp(onFail, onZFail, onZPass);
		} else {
			g.noStencilTest();
		}
	}
}
