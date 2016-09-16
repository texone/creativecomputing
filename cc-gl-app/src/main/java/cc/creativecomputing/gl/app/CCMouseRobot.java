package cc.creativecomputing.gl.app;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;

public class CCMouseRobot implements CCAnimatorListener{
	
	private CCAbstractGLContext<?> _myContext;
	
	public CCMouseRobot(CCAbstractGLContext<?> theContext){
		_myContext = theContext;
	}

	public void update(CCAnimator theAnimator){
		
	}

	@Override
	public void start(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void stop(CCAnimator theAnimator) {
		// TODO Auto-generated method stub
		
	}
}
