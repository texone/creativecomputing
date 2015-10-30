package cc.creativecomputing.kinect.graphics;

import KinectPV2.Device.KDataListener;
import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.control.CCRemoteControllerModule;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.CCGLAdapter;
import cc.creativecomputing.gl.CCGLContext;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.kinect.CCKinectModule;

public class CCKinectGraphicsApplication {
	
	private CCAnimator _myAnimator;
	private CCGLContext _myGLContext;
	private CCRemoteControllerModule _myRemoteControllerModule;
	private CCKinectModule _myKinectModule = new CCKinectModule();
	
	

	public CCKinectGraphicsApplication(CCKinectGLAdapter theAdapter) {

		_myAnimator = new CCAnimator();
		_myAnimator.framerate = 1;
		_myAnimator.animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		_myAnimator.listener().add(theAdapter);
		
		_myRemoteControllerModule = new CCRemoteControllerModule ();
		_myRemoteControllerModule.addControllable(theAdapter, "demo");

		_myGLContext = new CCGLContext(_myAnimator);
		_myGLContext.listener().add(theAdapter);
		_myGLContext.listener().add(
			new CCGLAdapter() {
				@Override
				public void dispose(GLGraphics theG) {
					_myAnimator.stop();
					_myKinectModule.stop();
					_myRemoteControllerModule.stop();
				}
			}
		);
		
		_myKinectModule = new CCKinectModule(_myAnimator);
		_myKinectModule.listener().add(theAdapter);
	}
	
	public CCKinectGraphicsApplication(){
		this(null);
	}
	
	public CCAnimator animator(){
		return _myAnimator;
	}
	
	public CCGLContext glcontext(){
		return _myGLContext;
	}
	
	public CCRemoteControllerModule remoteController(){
		return _myRemoteControllerModule;
	}
	
	public void start(){
		_myKinectModule.start();
		_myGLContext.start();
		_myRemoteControllerModule.start();
		_myAnimator.start();
	}

}
