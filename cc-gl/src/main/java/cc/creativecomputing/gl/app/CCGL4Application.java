package cc.creativecomputing.gl.app;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLAdapter;
import cc.creativecomputing.gl4.GLGraphics;

public class CCGL4Application {
	
	@CCProperty(name = "animator")
	private CCAnimator _myAnimator;
	@CCProperty(name = "gl context")
	private CCGL4Context _myGLContext;
	@CCProperty(name = "app")
	private CCGLAdapter<GLGraphics, CCGL4Context> _myAdapter;
	
	private CCControlApp _myControlApp;

	public CCGL4Application(CCGLAdapter<GLGraphics, CCGL4Context> theGLAdapter) {
		_myAdapter = theGLAdapter;
		_myAnimator = new CCAnimator();
		_myAnimator.framerate = 60;
		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		_myAnimator.listener().add(theGLAdapter);
	
		_myGLContext = new CCGL4Context(_myAnimator);
		theGLAdapter.glContext(_myGLContext);

		_myGLContext.listener().add(new CCGLAdapter<GLGraphics, CCGL4Context>() {
			@Override
			public void init(GLGraphics theG) {
				_myAdapter.init(theG, _myAnimator);
				_myControlApp = new CCControlApp(CCGL4Application.this);
			}
		});
		_myGLContext.listener().add(
			new CCGLAdapter<GLGraphics, CCGL4Context>() {
				@Override
				public void display(GLGraphics theG) {
					theG.beginDraw();
				}
			}
		);
		_myGLContext.listener().add(theGLAdapter);
		_myGLContext.listener().add(
			new CCGLAdapter<GLGraphics, CCGL4Context>() {
				@Override

				public void display(GLGraphics theG) {
					theG.endDraw();
				}
			}
		);
		_myGLContext.listener().add(
			new CCGLAdapter<GLGraphics, CCGL4Context>() {
				@Override
				public void dispose(GLGraphics theG) {
					_myAnimator.stop();
				}
			}
		);
	}
	
	public CCGL4Application(){
		this(null);
	}
	
	public CCAnimator animator(){
		return _myAnimator;
	}
	
	public CCGL4Context glcontext(){
		return _myGLContext;
	}
	
	
	public void start(){
		_myGLContext.start();
		_myAnimator.start();
	}
}