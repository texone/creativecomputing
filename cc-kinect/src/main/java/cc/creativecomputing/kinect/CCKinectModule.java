package cc.creativecomputing.kinect;

import java.nio.ByteOrder;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import KinectPV2.Device;
import KinectPV2.Device.KDataListener;
import cc.creativecomputing.app.modules.CCAbstractAppModule;
import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorListener;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCPixelFormat;
import cc.creativecomputing.image.CCPixelInternalFormat;
import cc.creativecomputing.image.CCPixelType;
import cc.creativecomputing.nativeutil.CCNativeLibUtil;

public class CCKinectModule extends CCAbstractAppModule<CCKinectListener>{

	private CCAnimator _myAnimator;
	private Device _myDevice;
	private boolean _myHasOwnAnimator = false;
	
	public CCKinectModule(CCAnimator theAnimator) {
		super(CCKinectListener.class, "kinect");
		_myAnimator = theAnimator;
		
		_myAnimator.listener().add(new CCAnimatorListener() {
			
			@Override
			public void update(CCAnimator theAnimator) {
				
				boolean result = _myDevice.update();
//				CCLog.info("_myDevice:" + result);
				if(!result){
					CCLog.info("Error updating Kinect EXIT");
				}
						
			}
			
			@Override
			public void stop(CCAnimator theAnimator) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void start(CCAnimator theAnimator) {
				// TODO Auto-generated method stub
				
			}
		});
	}
	
	public CCKinectModule(){
		this(new CCAnimator());
		_myHasOwnAnimator = true;
	}
	
	

	@Override
	public void start() {
//		CCNativeLibUtil.prepareLibraryForLoading(Device.class, "Kinect20.Face");
		_myDevice = new Device();
		
		_myListeners.proxy().start(this);
		_myDevice.start();
		if(_myHasOwnAnimator)_myAnimator.start();
	}

	@Override
	public void stop() {
		_myListeners.proxy().stop(this);
		if(_myHasOwnAnimator)_myAnimator.stop();
		_myDevice.stop();
	}
	
	public Device device(){
		return _myDevice;
	}

	public CCKinectImage depthImage(){
		CCKinectImage myDepthImage = new CCKinectImage(
			Device.DEPTH_WIDTH, 
			Device.DEPTH_HEIGHT, 
			CCPixelInternalFormat.RGBA, 
			CCPixelFormat.RGBA, 
			CCPixelType.UNSIGNED_BYTE
		);
		_myDevice.addDepthListener(myDepthImage);
		return myDepthImage;
	}
	
	public CCKinectImage infraredImage(){
		CCKinectImage myInfraredImage = new CCKinectImage(
			Device.DEPTH_WIDTH, 
			Device.DEPTH_HEIGHT, 
			CCPixelInternalFormat.RGBA,
			CCPixelFormat.RGBA, 
			CCPixelType.UNSIGNED_BYTE
		);
		_myDevice.addInfraredListener(myInfraredImage);
		return myInfraredImage;
	}
	
	public CCKinectImage colorImage(){
		CCKinectImage mColorImage = new CCKinectImage(
			Device.COLOR_WIDTH, 
			Device.COLOR_HEIGHT, 
			CCPixelInternalFormat.RGBA,
			CCPixelFormat.RGBA, 
			CCPixelType.UNSIGNED_BYTE
		);
		_myDevice.addColorListener(mColorImage);
		return mColorImage;
	}
	
	public CCKinectImage depthPositionImage(){
		CCKinectImage myPointCloud = new CCKinectImage(
			Device.DEPTH_WIDTH, 
			Device.DEPTH_HEIGHT, 
			CCPixelInternalFormat.RGB,
			CCPixelFormat.RGB, 
			CCPixelType.FLOAT
		);
		_myDevice.addPointCloudPosListener(myPointCloud);
		return myPointCloud;
	}

	public CCKinectImage colorPositionImage(){
		CCKinectImage myPointCloud = new CCKinectImage(
			Device.COLOR_WIDTH, 
			Device.COLOR_HEIGHT, 
			CCPixelInternalFormat.RGB,
			CCPixelFormat.RGB, 
			CCPixelType.FLOAT
		);
		_myDevice.addPointCloudColorListener(myPointCloud);
		return myPointCloud;
	}
	
	


	public static void main(String[] args) {
		CCKinectModule myKinectModule = new CCKinectModule();
		CCKinectImage myImage;
		myKinectModule.listener().add(new CCKinectListener() {
			
			@Override
			public void stop(CCKinectModule theKinectModule) {
				
			}
			
			@Override
			public void start(CCKinectModule theKinectModule) {
				theKinectModule.device().addDepthListener(new KDataListener() {
					
					@Override
					public void onData(int[] arg0) {
						CCImage myImage = new CCImage();
						CCLog.info(arg0.length);
					}
				});
				
				theKinectModule.depthImage();
			}
		});
		
		myKinectModule.start();
	}
	
}
