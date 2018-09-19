package cc.creativecomputing.demo.kle.roche;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.filter.CCChebFilter;
import cc.creativecomputing.math.spline.CCBezierSpline;

public class CCRocheAnalyzer extends CCGL2Adapter {
	
	
	@CCProperty(name = "camera")
	private CCCameraController _cCameraController;
	@CCProperty(name = "data")
	private CCRocheData _cData;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_cCameraController = new CCCameraController(this, g, 100);
		_cData = new CCRocheData();
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		_cData.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clearColor(255);
		g.clear();
		
		_cCameraController.camera().draw(g);
		_cData.draw(g);
	}

	public static void main(String[] args) {

		CCRocheAnalyzer demo = new CCRocheAnalyzer();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
//		int i = 0;
//		for(String myLine:CCNIOUtil.loadStrings(CCNIOUtil.dataPath("kle/180907_roche_choreoV3_kurven_liste.txt"))){
//			String [] values = myLine.split(",");
//			String myName = values[0].trim();
//			int myKeyFrame = Integer.parseInt(values[1].substring(8, values[1].length()).trim());
//			CCLog.info(myName, myKeyFrame);
//			i++;
//		}
//		CCLog.info(i);
	}
}
