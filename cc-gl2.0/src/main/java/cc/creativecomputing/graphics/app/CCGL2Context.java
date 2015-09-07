package cc.creativecomputing.graphics.app;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.gl.app.CCAbstractGLContext;
import cc.creativecomputing.graphics.CCGraphics;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLProfile;

public class CCGL2Context extends CCAbstractGLContext<CCGraphics>{

	
	
	public CCGL2Context() {
		super();
	}

	public CCGL2Context(CCAnimator theAnimator, String theID) {
		super(theAnimator, theID);
	}

	public CCGL2Context(CCAnimator theAnimator) {
		super(theAnimator);
	}

	public CCGL2Context(String theID) {
		super(theID);
	}

	@Override
	public CCGraphics createGraphics(GLAutoDrawable drawable) {
		return new CCGraphics(drawable.getGL().getGL2(), width, height);
	}
	
	@Override
	public GLProfile createProfile() {
		return GLProfile.getMaxFixedFunc(true);
	}

}
