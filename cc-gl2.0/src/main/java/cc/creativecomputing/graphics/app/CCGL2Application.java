package cc.creativecomputing.graphics.app;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCTimelineSynch;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCGLAdapter;
import cc.creativecomputing.graphics.CCGraphics;

public class CCGL2Application {
	
	@CCProperty(name = "animator")
	private CCAnimator _myAnimator;
	@CCProperty(name = "gl context")
	private CCGL2Context _myGLContext;
	@CCProperty(name = "synch")
	private CCTimelineSynch _mySynch;
	
	@CCProperty(name = "app")
	private CCGLAdapter<CCGraphics, CCGL2Context> _myAdapter;

	private CCControlApp _myControlApp;
	
	private boolean _myIsInitialized = false;

	public CCGL2Application(CCGLAdapter<CCGraphics, CCGL2Context> theGLAdapter) {
		_myAdapter = theGLAdapter;
		_myAnimator = new CCAnimator();
		_myAnimator.framerate = 60;
		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		_mySynch = new CCTimelineSynch(_myAnimator);
		
		_myAnimator.listener().add(new CCAnimatorListener() {
			
			@Override
			public void update(CCAnimator theAnimator) {
				if(_myIsInitialized)theGLAdapter.update(theAnimator);
			}
			
			@Override
			public void stop(CCAnimator theAnimator) {
				theGLAdapter.start(theAnimator);
			}
			
			@Override
			public void start(CCAnimator theAnimator) {
				theGLAdapter.start(theAnimator);
			}
		});
		
		_myGLContext = new CCGL2Context(_myAnimator);
		theGLAdapter.glContext(_myGLContext);

		_myGLContext.listener().add(new CCGLAdapter<CCGraphics, CCGL2Context>() {
			@Override
			public void init(CCGraphics theG) {
				_myAdapter.init(theG, _myAnimator);
				_myControlApp = new CCControlApp(CCGL2Application.this, _mySynch);
				_myControlApp.afterInit();
				_mySynch.animator().start();
				theGLAdapter.controlApp(_myControlApp);
				
				_myIsInitialized = true;
			}
		});
		_myGLContext.listener().add(
			new CCGLAdapter<CCGraphics, CCGL2Context>() {
				@Override
				public void display(CCGraphics theG) {
					theG.beginDraw();
				}
			}
		);
		_myGLContext.listener().add(theGLAdapter);
		_myGLContext.listener().add(
			new CCGLAdapter<CCGraphics, CCGL2Context>() {
				@Override

				public void display(CCGraphics theG) {
					theG.endDraw();
				}
			}
		);
		_myGLContext.listener().add(
			new CCGLAdapter<CCGraphics, CCGL2Context>() {
				@Override
				public void dispose(CCGraphics theG) {
					_myAnimator.stop();
				}
			}
		);
	}
	
	public CCGL2Application(){
		this(null);
	}
	
	public CCAnimator animator(){
		return _myAnimator;
	}
	
	public CCGL2Context glcontext(){
		return _myGLContext;
	}
	
	
	public void start(){
		_myGLContext.start();
		_myAnimator.start();
	}
}