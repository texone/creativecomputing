package cc.creativecomputing.demo.topic.geometry;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;

public class CCGPXFixer extends CCGL2Adapter {

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

//		CCGPXFixer demo = new CCGPXFixer();
//
//		CCGL2Application myAppManager = new CCGL2Application(demo);
//		myAppManager.glcontext().size(1200, 600);
//		myAppManager.animator().framerate = 30;
//		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
//		myAppManager.start();
		
		CCDataElement myXML = CCXMLIO.createXMLElement(CCNIOUtil.dataPath("data/One_more_quick_loop_.gpx"));
		
	}
}
