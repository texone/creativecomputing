package cc.creativecomputing.gl.demo.superbible.chapter02;

import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl.app.container.GLContainerType;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;

public class Demo01SimpleClear extends CCGL4Adapter{
	
	@Override
	public void init(GLGraphics theG) {
		CCLog.info("INIT GL");
	}
	
	@Override
	public void reshape(GLGraphics theG) {
		CCLog.info("RESHAPE GL");
	}
	
	@Override
	public void display(GLGraphics g) {
        g.clearBufferfv(GLColorBuffer.COLOR,  0, 1.0f, 0.0f, 0.0f, 1.0f);
	}
	
	@Override
	public void dispose(GLGraphics g) {
		CCLog.info("DISPOSE GL");
	}
	
	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo01SimpleClear());
		
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		myAppManager.glcontext().title = "OpenGL SuperBible - Simple Clear";
		myAppManager.glcontext().containerType = GLContainerType.NEWT;
		myAppManager.glcontext().size(800, 800);
		
		myAppManager.start();
	}
}
