package cc.creativecomputing.graphics.scene.state;

import cc.creativecomputing.gl4.GLGraphics;

public abstract class CCGlobalState {
	
	public enum CCGlobalStateType {
		ALPHA, CULL, SHADE, WIREFRAME, DEPTH_BUFFER, OFFSET, STENCIL
	}
	
	public final CCGlobalStateType globalStateType;

	// static Pointer<GlobalState> Default[MAX_STATE];
	
	protected CCGlobalState(CCGlobalStateType theType) {
		globalStateType = theType;
	}
	
	public abstract void draw(GLGraphics g);
}
