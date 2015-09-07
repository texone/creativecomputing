package cc.creativecomputing.graphics.scene.state;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLCompareFunction;

public class CCDepthState extends CCGlobalState {
	
	public static CCDepthState DEFAULT = new CCDepthState();
	
	/**
	 * default: true
	 */
	public boolean enabled = true;
	/**
	 * default: true
	 */
	public boolean writable = true;
	
	/**
	 * default {@linkplain GLCompareFunction#LESS_EQUAL}
	 */
	public GLCompareFunction compare = GLCompareFunction.LESS_EQUAL;

	public CCDepthState() {
		super(CCGlobalStateType.DEPTH_BUFFER);
	}

	@Override
	public void draw(GLGraphics g) {
		if (enabled) {
			g.depthTest();
			g.depthFunc(compare);
		} else {
			g.noDepthTest();
		}

		if (writable) {
			g.depthMask();
		} else {
			g.noDepthMask();
		}
	}

}
