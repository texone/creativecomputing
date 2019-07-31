package cc.creativecomputing.demo;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCCSVIO;
import cc.creativecomputing.io.xml.CCDataElement;

public class CCCSVAnalyszer extends CCGL2Adapter {

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

//		CCCSVAnalyszer demo = new CCCSVAnalyszer();
//
//		CCGL2Application myAppManager = new CCGL2Application(demo);
//		myAppManager.glcontext().size(1200, 600);
//		myAppManager.animator().framerate = 30;
//		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
//		myAppManager.start();
		

		for(CCDataElement myElement:CCCSVIO.createDataElement(CCNIOUtil.dataPath("ablauf01.csv"), "0,1,2,3,4,5,6,7,8,9,10,11,12", ",")) {
			CCLog.info(myElement);
		}
	}
}
