package cc.creativecomputing.graphics.scene.state;

import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLPolygonMode;

public class CCWireFrameState extends CCGlobalState {
	
	public static final CCWireFrameState DEFAULT = new CCWireFrameState();

	public boolean enabled = false;
	
	public float lineWidth = 1;
	
	public float pointSize = 1;
	
	public GLPolygonMode polygonMode = GLPolygonMode.LINE;

	public CCWireFrameState() {
		super(CCGlobalStateType.WIREFRAME);
	}

	@Override
	public void draw(GLGraphics g) {

		if (enabled) {
			if(polygonMode == GLPolygonMode.LINE){
				g.lineWidth(100);
			}
			if(polygonMode == GLPolygonMode.POINT)g.pointSize(pointSize);
			g.polygonMode(polygonMode);
		} else {
			g.polygonMode(GLPolygonMode.FILL);
		}
	}
}
