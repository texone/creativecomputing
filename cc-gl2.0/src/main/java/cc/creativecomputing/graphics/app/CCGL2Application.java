package cc.creativecomputing.graphics.app;

import java.nio.file.Path;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.code.memorycompile.CCInMemoryExecutionManager;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.controlui.CCControlApp;
import cc.creativecomputing.controlui.CCTimelineSynch;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLAdapter;
import cc.creativecomputing.gl.app.CCGLListener;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.CCNIOUtil;

public class CCGL2Application {
	
	@CCProperty(name = "animator")
	private CCAnimator _myAnimator;
	@CCProperty(name = "gl context")
	private CCGL2Context _myGLContext;
	@CCProperty(name = "synch")
	private CCTimelineSynch _mySynch;
	@CCProperty(name = "app")
	private CCGLAdapter<CCGraphics, CCGL2Context> _myAdapter;
	@CCProperty(name = "real compile")
	private boolean _cRealCompile = false;
	
	private CCControlApp _myControlApp;
	
	public String presetPath = null;
	
	private boolean _myIsInitialized = false;
	
	private boolean _myUseUI;
	
	private CCInMemoryExecutionManager _myExecutionManager;
	private CCGLAdapter<CCGraphics, CCGL2Context> _myOriginalCopy;
	
	private boolean _myUpdateUI = false;

	public CCGL2Application(CCGLAdapter<CCGraphics, CCGL2Context> theGLAdapter, boolean useUI) {
		_myUseUI = useUI;
		_myAdapter = theGLAdapter;
		_myOriginalCopy = theGLAdapter;
		_myAnimator = new CCAnimator();
		_myAnimator.framerate = 60;
		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		_mySynch = new CCTimelineSynch(_myAnimator);
		
		
		_myAnimator.listener().add(new CCAnimatorListener() {
			
			@Override
			public void update(CCAnimator theAnimator) {
				if(!_myIsInitialized)return;
				
				if(_cRealCompile){

					if(_myExecutionManager == null)_myExecutionManager = new CCInMemoryExecutionManager(theGLAdapter.getClass());
					if(_myExecutionManager.update()){
						if(_myExecutionManager.mainObject()!= null){
							_myUpdateUI = true;
						}
					}
				}else{
					_myAdapter = _myOriginalCopy;
				}
				_myAdapter.update(theAnimator);
			}
			
			@Override
			public void stop(CCAnimator theAnimator) {
				_myAdapter.start(theAnimator);
			}
			
			@Override
			public void start(CCAnimator theAnimator) {
				_myAdapter.start(theAnimator);
			}
		});
		
		_myGLContext = new CCGL2Context(_myAnimator);
		_myAdapter.glContext(_myGLContext);
		if(presetPath == null){
			presetPath = "settings/" + _myAdapter.getClass().getName() + "/";
		}
		CCGLAdapter<CCGraphics, CCGL2Context> myGLAdapter = new CCGLAdapter<CCGraphics, CCGL2Context>() {
			
			
			@Override
			public void init(CCGraphics theG) {
				_myAdapter.init(theG, _myAnimator);
				if(_myUseUI){
					_myControlApp.setData(CCGL2Application.this, presetPath);
					theGLAdapter.controlApp(_myControlApp);
					_myControlApp.update(0);
					_myAdapter.setupControls(_myControlApp);
				}else{
					CCPropertyMap myProps = new CCPropertyMap();
					CCLog.info(presetPath);
					myProps.setData(CCGL2Application.this, presetPath);
					
					Path myPresetsPath = myProps.rootHandle().presetPath();
					CCNIOUtil.createDirectories(myPresetsPath);
					for(Path myPath:CCNIOUtil.list(myPresetsPath, "json")){
						CCLog.info(myPath.getFileName().toString());
						myProps.rootHandle().preset(CCNIOUtil.fileName(myPath.getFileName().toString()));
						break;
					}
					myProps.rootHandle().update(0);
				}
				_mySynch.animator().start();
				
				_myIsInitialized = true;
			}
		};
		
		
		if(_myUseUI){
			_myControlApp = new CCControlApp(myGLAdapter, _mySynch, theGLAdapter.getClass());
			myGLAdapter.controlApp(_myControlApp);
		}

		_myGLContext.listener().add(myGLAdapter);
		_myGLContext.listener().add(
			new CCGLAdapter<CCGraphics, CCGL2Context>() {
				@Override
				public void display(CCGraphics theG) {
					theG.beginDraw();
				}
			}
		);
		_myGLContext.listener().add(new CCGLListener<CCGraphics>() {

			@Override
			public void reshape(CCGraphics g) {
				_myAdapter.reshape(g);
			}

			@Override
			public void init(CCGraphics g) {
				_myAdapter.init(g);
			}

			@Override
			public void dispose(CCGraphics g) {
				_myAdapter.dispose(g);
			}

			@Override
			public void display(CCGraphics g) {
				_myAdapter.display(g);
				if(_myUpdateUI){
					_myUpdateUI = false;
					_myAdapter = (CCGLAdapter<CCGraphics, CCGL2Context>)_myExecutionManager.mainObject();
					_myAdapter.glContext(_myGLContext);
					CCObjectPropertyHandle myProperty = (CCObjectPropertyHandle)_myControlApp.propertyMap().rootHandle().property("app");
					myProperty.value(_myAdapter, true);
				}
			}
		});
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
	
	public CCGL2Application(CCGLAdapter<CCGraphics, CCGL2Context> theGLAdapter){
		this(theGLAdapter, true);
	}
	
	public CCGL2Application(){
		this(null, true);
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