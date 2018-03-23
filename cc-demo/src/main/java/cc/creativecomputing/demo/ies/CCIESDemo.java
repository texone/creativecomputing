package cc.creativecomputing.demo.ies;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.ies.CCIES;
import cc.creativecomputing.ies.CCIESData;
import cc.creativecomputing.io.CCNIOUtil;

public class CCIESDemo extends CCGL2Adapter {
	
	private CCIESData _myCiesData;
	
	

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCiesData = CCIES.read(CCNIOUtil.dataPath("ies/TEST.IES"));
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		for(int h = 0; h < _myCiesData.photometricalData().horizontalAngles().length;h++){
			for(int v = 0; v < _myCiesData.photometricalData().verticalAngles().length;v++){
				double myHorizontalAngle = _myCiesData.photometricalData().horizontalAngles()[h];
				double myVerticalAngle = _myCiesData.photometricalData().horizontalAngles()[h];
			}
		}
	}

	public static void main(String[] args) {

		CCIESDemo demo = new CCIESDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

