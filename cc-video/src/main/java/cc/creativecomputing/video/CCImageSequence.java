package cc.creativecomputing.video;

import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.image.CCImage;
import cc.creativecomputing.image.CCImageIO;
import cc.creativecomputing.math.CCMath;

public class CCImageSequence extends CCVideo {
	
	private boolean _myIsRunning = false;

	private final List<Path> _myImagePaths;
	private double _myFrameRate = 30;

	private double _myPlayTime = 0;
	private double _myTime = 0;

	private double _myFrameTime = 1 / _myFrameRate;
	private int _myCurrentFrame = 0;
	private int _myBufferedFrame = 0;
		
	private int _myLoopStartFrame;
	private int _myLoopEndFrame;

	public CCImageSequence(final CCAnimator theAnimator, final Path thePath) {
		super(theAnimator);
//		super(theParent, thePath);

		_myImagePaths = CCImageIO.listImages(thePath);

		_myLoopStartFrame = 0;
		_myLoopEndFrame = _myImagePaths.size();
		
		sendTexture();
	}
		
	public void frameRate(final double theFrameRate){
		_myFrameRate = theFrameRate;
		_myFrameTime = 1 / _myFrameRate;
	}
		
	public Path currentImagePath() {
		return _myImagePaths.get(_myCurrentFrame % _myImagePaths.size());
	}
	
	public CCImage currentImage(){
		return _myCurrentImage;
	}

	public double duration() {
		return _myImagePaths.size() / _myFrameRate;
	}

	public void start(boolean theRestart) {
		_myIsRunning = true;
		_myDoRepeat = theRestart;
		_myCurrentFrame = 0;
		_myTime = 0;
		_myPlayTime = 0;
		
		sendTexture();
	}

	public double time() {
		return _myPlayTime;
	}

	public void time(double theNewtime) {
		_myTime = 0;
		_myPlayTime = CCMath.max(theNewtime,0);
		_myCurrentFrame = (int) (_myPlayTime / _myFrameTime);
		
		sendTexture();
	}
	
	private boolean _myDoRepeat;

	public void goToBeginning() {
		goToFrame(0);
	}
		
	public void goToFrame(final int theFrame){
		_myCurrentFrame = theFrame;
		_myBufferedFrame = theFrame;
		_myTime = _myFrameTime;
		_myPlayTime = theFrame * _myFrameTime;
		
		sendTexture();
	}

	public void loop(final int theStartFrame, final int theEndFrame){
		if(theStartFrame == _myLoopStartFrame && theEndFrame == _myLoopEndFrame)return;
		_myLoopStartFrame = theStartFrame;
		_myLoopEndFrame = theEndFrame;
		goToFrame(theStartFrame);
			
		_myDoRepeat = true;
	}
		
	public int bufferedFrame(){
		return _myBufferedFrame;
	}
		
	public int currentFrame(){
		return _myCurrentFrame;
	}
	
	private CCImage _myCurrentImage = null;
	
	private int _myLastFrame = -1;
	
	private void sendTexture(){
		if(_myCurrentImage != null && _myCurrentFrame == _myLastFrame){
			return;
		}
		_myLastFrame = _myCurrentFrame;
		_myCurrentImage = CCImageIO.newImage(currentImagePath());
		if (_myIsFirstFrame) {
			_myIsFirstFrame = false;
			_myListener.proxy().onInit(_myCurrentImage);
		} else {
			_myListener.proxy().onUpdate(_myCurrentImage);
		}
	}
	
	
	
	@Override
	public void addListener(CCVideoTextureDataListener theListener) {
		_myListener.add(theListener);
		theListener.onInit(_myCurrentImage);
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(!_myIsRunning)return;
	
		_myTime += theAnimator.deltaTime();
		_myPlayTime += theAnimator.deltaTime();
			
		if(_myTime >= _myFrameTime){
			_myTime -= _myFrameTime;
		//	updateImage(_myTextureData[_myCurrentFrame % BUFFER_LENGTH]);
			_myCurrentFrame++;
			if(_myCurrentFrame > _myLoopEndFrame){
				if(_myDoRepeat){
					goToFrame(_myLoopStartFrame);
				}else{
					_myIsRunning = false;
				}
			}
		}
		sendTexture();
		
	}
}
