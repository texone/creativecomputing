package cc.creativecomputing.demo.control;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.container.GLContainerType;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;

public class PSEMouseDemo extends CCGL2Adapter {

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		
//		mousePressed().add(e -> {CCLog.info(e.button(), e.clickCount(),e.eventType(),e.isAltDown(),e.isAltGraphDown(),e.isCtrlDown(), e.isShiftDown(), e.isMetaDown());});
//		mouseReleased().add(e -> {CCLog.info(e.button(), e.clickCount(),e.eventType(),e.isAltDown(),e.isAltGraphDown(),e.isCtrlDown(), e.isShiftDown(), e.isMetaDown());});
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		PSEMouseDemo demo = new PSEMouseDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
