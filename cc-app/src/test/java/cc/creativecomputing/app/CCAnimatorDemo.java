package cc.creativecomputing.app;

import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;

public class CCAnimatorDemo {
	
	public static void main(String[] args) {
		
		CCAnimator myAnimator = new CCAnimator();
		myAnimator.framerate = 60;
		myAnimator.animationMode = CCAnimationMode.FRAMERATE_PERFORMANT;
		myAnimator.listener().add(new CCAnimatorListener() {
			double _myFrameRate = 0;
			
			@Override
			public void start(CCAnimator theAnimator){
				
			}
			
			@Override
			public void update(CCAnimator theAnimator) {
				CCLog.info(theAnimator.frames() + " : " + theAnimator.frameRate()+" : " + theAnimator.deltaTime());
				if(_myFrameRate == 0)_myFrameRate = theAnimator.frameRate();
				else _myFrameRate = _myFrameRate * 0.99f + theAnimator.frameRate() * 0.01f;
				
				CCLog.info(_myFrameRate);
			}
			
			@Override
			public void stop(CCAnimator theAnimator) {
				// TODO Auto-generated method stub
				
			}
		});
		myAnimator.start();
		
		CCLog.info("STOP");
		
		CCApplication manager = new CCApplication();
		manager.start();
	}
}
