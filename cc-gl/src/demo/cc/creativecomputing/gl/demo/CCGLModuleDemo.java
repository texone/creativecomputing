package cc.creativecomputing.gl.demo;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGL4Context;
import cc.creativecomputing.gl.app.CCGLListener;
import cc.creativecomputing.gl4.GLGraphics;

public class CCGLModuleDemo implements CCAnimatorListener, CCGLListener<GLGraphics>{

	@Override
	public void start(CCAnimator theAnimator) {
		CCLog.info("START ANIMATOR");
	}

	@Override
	public void init(GLGraphics g) {
		CCLog.info("INIT GL CONTEXT");
		g.clearColor(0, 0, 0, 0);
	}
	
	@Override
	public void reshape(GLGraphics g) {
		CCLog.info("RESHAPE GL CONTEXT");
	}

	@Override
	public void update(CCAnimator theAnimator) {
		CCLog.info(theAnimator.frames() + " : " + theAnimator.frameRate()+" : " + theAnimator.deltaTime());
	}

	@Override
	public void display(GLGraphics g) {
		CCLog.info("DISPLAY GL CONTEXT");
		g.clear();
	}

	@Override
	public void stop(CCAnimator theAnimator) {
		CCLog.info("STOP ANIMATOR");
	}

	@Override
	public void dispose(GLGraphics g) {
		CCLog.info("DISPOSE GL CONTEXT");
	}
	
	public static void main(String[] args) {
		
		CCGLModuleDemo myDemo = new CCGLModuleDemo();
		
		CCAnimator myAnimator = new CCAnimator();
		myAnimator.addListener(myDemo);
		myAnimator.framerate = 1;
		myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		CCGL4Context myGLModule = new CCGL4Context(myAnimator);
		myGLModule.addListener(myDemo);
		
		myGLModule.start();
		myAnimator.start();
	}

	
}
