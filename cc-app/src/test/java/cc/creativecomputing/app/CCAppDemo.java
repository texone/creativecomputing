package cc.creativecomputing.app;

import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.app.modules.CCBasicAppListener;
import cc.creativecomputing.core.logging.CCLog;

public class CCAppDemo implements CCAnimatorListener, CCBasicAppListener{
	
	@Override
	public void start() {
		CCLog.info("START APP");
	}

	@Override
	public void start(CCAnimator theAnimator) {
		CCLog.info("START ANIMATOR");
	}

	@Override
	public void update(CCAnimator theAnimator) {
		CCLog.info(theAnimator.frames() + " : " + theAnimator.frameRate()+" : " + theAnimator.deltaTime());
	}

	@Override
	public void stop(CCAnimator theAnimator) {
		CCLog.info("STOP ANIMATOR");
	}
	
	@Override
	public void stop() {
		CCLog.info("STOP APP");
	}
	
	public static void main(String[] args) {
		
		CCAppDemo myDemo = new CCAppDemo();
		
		CCAnimator myAnimator = new CCAnimator();
		myAnimator.framerate = 1;
		myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAnimator.addListener(myDemo);
		myAnimator.start();
	}
}
