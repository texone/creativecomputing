package cc.creativecomputing.demo.topic.simulation;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.texture.CCGLSwapBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.io.CCNIOUtil;

public class CCDLADemo extends CCGL2Adapter {
	
	@CCProperty(name = "buffer program")
	private CCGLProgram _myBufferProgram;
	
	private CCGLProgram _myImageProgram;
	
	private CCGLSwapBuffer _mySwapBuffer;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myBufferProgram = new CCGLProgram(
			null,
			CCNIOUtil.classPath(this, "dla_buffer_fragment.glsl")
		);
		
		_mySwapBuffer = new CCGLSwapBuffer(g.width(), g.height(), CCTextureTarget.TEXTURE_2D);
		
//		_myImageProgram = new CCGLProgram(
//			null,
//			CCNIOUtil.classPath(this, "dla_image_fragment.glsl")
//		);
	}
	

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		CCLog.info(animator().frames());
		_myBufferProgram.start();
		_myBufferProgram.uniform1i("iChannel0", 0);
		_myBufferProgram.uniform2f("iChannelResolution", _mySwapBuffer.width(), _mySwapBuffer.height());
		_myBufferProgram.uniform1i("iFrame", 0);//animator().frames());
		_myBufferProgram.uniform1f("iGlobalTime", animator().time());
		_mySwapBuffer.draw();
		_myBufferProgram.end();
		_mySwapBuffer.swap();
		
		g.clear();
		g.image(_mySwapBuffer.currentBuffer().attachment(0),-g.width()/2, -g.height()/2);
	}

	public static void main(String[] args) {

		CCDLADemo demo = new CCDLADemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

