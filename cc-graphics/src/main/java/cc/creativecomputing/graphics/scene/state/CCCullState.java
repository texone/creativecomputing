package cc.creativecomputing.graphics.scene.state;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLCullFace;
import cc.creativecomputing.gl4.GLGraphics.GLFrontFace;

public class CCCullState extends CCGlobalState{
	
	public static CCCullState DEFAULT = new CCCullState();
	
	public GLFrontFace frontFace = GLFrontFace.COUNTER_CLOCK_WISE;
	
	public GLCullFace cullFace = GLCullFace.BACK;
	
	public boolean enabled = false;

	public CCCullState() {
		super(CCGlobalStateType.CULL);
	}
	
	@Override
	public void draw(GLGraphics g) {
		if (enabled) {
			g.cullFace(cullFace);
			g.frontFace(frontFace);

		} else {
			g.noCullFace();
		}
	}

}
