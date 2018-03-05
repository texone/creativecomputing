package cc.creativecomputing.graphics.app;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.core.CCProperty;

public class CCAnimatorApplication {
	
	@CCProperty(name = "animator")
	private CCAnimator _myAnimator;
	@CCProperty(name = "app")
	private CCAnimationAdapter _myAdapter;
	
	private CCControlApp _myControlApp;
	
	public String presetPath = null;
	
	private boolean _myUseUI;
	
	private boolean _myIsUpdated = false;

	public CCAnimatorApplication(CCAnimationAdapter theListener, boolean useUI) {
		_myUseUI = useUI;
		_myAdapter = theListener;
		_myAnimator = new CCAnimator();
		_myAnimator.framerate = 60;
		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		_myAnimator.listener().add(new CCAnimatorListener() {
			
			@Override
			public void update(CCAnimator theAnimator) {
				try{
				_myAdapter.update(theAnimator);
				_myIsUpdated = true;
				}catch(Exception e){
					e.printStackTrace();
				}
			}
			
			@Override
			public void stop(CCAnimator theAnimator) {
				_myAdapter.stop(theAnimator);
			}
			
			@Override
			public void start(CCAnimator theAnimator) {
				_myAdapter.start(theAnimator);
				_myControlApp = new CCControlApp(theAnimator, _myAnimator, theAnimator.getClass(),_myUseUI);
				_myControlApp.setData(CCAnimatorApplication.this, presetPath);
				_myControlApp.update(0);
				_myAdapter.setupControls(_myControlApp);
			}
		});
		
		if(presetPath == null){
			presetPath = "settings/" + _myAdapter.getClass().getName() + "/";
		}
		
	}
	
	public CCAnimatorApplication(CCAnimationAdapter theGLAdapter){
		this(theGLAdapter, true);
	}
	
	public CCAnimatorApplication(){
		this(null, true);
	}
	
	public CCAnimator animator(){
		return _myAnimator;
	}
	
	public void start(){
		_myAnimator.start();
	}
}