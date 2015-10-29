package cc.creativecomputing.gl.app;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;

public abstract class CCGLApplication<GLGraphicsType extends CCGLGraphics, GLContextType extends CCAbstractGLContext<GLGraphicsType>> {
	
	private CCAnimator _myAnimator;
	private GLContextType _myGLContext;

	@SuppressWarnings("rawtypes")
	public CCGLApplication(CCGLAdapter<GLGraphicsType, GLContextType> theGLAdapter, GLContextType theContext) {

		_myAnimator = new CCAnimator();
		_myAnimator.framerate = 1;
		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		_myAnimator.listener().add(theGLAdapter);

		_myGLContext = theContext;
		_myGLContext.listener().add(theGLAdapter);
		_myGLContext.listener().add(
			new CCGLAdapter() {
				@Override
				public void dispose(CCGLGraphics theG) {
					_myAnimator.stop();
				}
			}
		);
	}
	
	public CCAnimator animator(){
		return _myAnimator;
	}
	
	public GLContextType glcontext(){
		return _myGLContext;
	}
	
	public void start(){
		_myGLContext.start();
		_myAnimator.start();
	}

}
