package cc.creativecomputing.gl.app;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.gl.app.CCAbstractGLContext;
import cc.creativecomputing.gl4.GLGraphics;

import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLProfile;

public class CCGL4Context extends CCAbstractGLContext<GLGraphics>{

	
	
	public CCGL4Context() {
		super();
	}

	public CCGL4Context(CCAnimator theAnimator, String theID) {
		super(theAnimator, theID);
	}

	public CCGL4Context(CCAnimator theAnimator) {
		super(theAnimator);
	}

	public CCGL4Context(String theID) {
		super(theID);
	}

	@Override
	public GLGraphics createGraphics(GLAutoDrawable drawable) {
		return new GLGraphics(drawable.getGL().getGL4(), width, height);
	}
	
	@Override
	public GLProfile createProfile() {
		return GLProfile.getMaxProgrammable(true);
	}

}
