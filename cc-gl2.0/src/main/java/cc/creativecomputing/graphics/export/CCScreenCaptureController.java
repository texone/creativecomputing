package cc.creativecomputing.graphics.export;

import java.nio.file.Path;

import javax.swing.SwingUtilities;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.controlui.timeline.controller.TransportController;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.image.CCImageIO.CCImageFormats;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCScreenCaptureController extends CCGL2Adapter{
	
	public static interface CCSequenceRecorderListener{
		public void start();
		
		public void end();
	}
	
	private CCListenerManager<CCSequenceRecorderListener> _myRecordListeners = CCListenerManager.create(CCSequenceRecorderListener.class);

	private int _mySeconds = 100;
	
	
	@CCProperty(name = "format")
	private CCImageFormats _myFormat = CCImageFormats.PNG;
	
	@CCProperty(name = "capture rate", min = 1, max = 60)
	private int _myCaptureRate;
	
	@CCProperty(name = "alpha")
	private boolean _cAlpha = false;
	
	@CCProperty(name = "x", readBack = true)
	private int _cX = 0;
	@CCProperty(name = "y", readBack = true)
	private int _cY = 0;
	@CCProperty(name = "width", readBack = true)
	private int _cWidth = 0;
	@CCProperty(name = "height", readBack = true)
	private int _cHeight = 0;
	
	private boolean _myAdaptSize = false;
	
	@CCProperty(name = "full window")
	public void fullWindow(){
		_myAdaptSize = true;
	}
	
	@CCProperty(name = "draw bounds")
	private boolean _cDrawCaptureBounds = false;
	
	
	private final CCAnimator _myAnimator;
	
	private CCGL2Adapter _myGLAdapter;
	
	public CCScreenCaptureController(CCGL2Adapter theGLAdapter, CCAnimator theAnimator){
		_myGLAdapter = theGLAdapter;
		_myGLAdapter.glListener().add(this);
		_myCaptureRate = 1;
		_myAnimator = theAnimator;
		_myAnimator.listener().add(this);
	}
	
	private int _myStep = 0;
	private int _mySequenceSteps = 0;
	private boolean _myIsRecording = false;
	
	private CCTriggerProgress _myProgress;
	
	
	@Override
	public void display(CCGraphics g) {
		if(_myAdaptSize){
			_cX = 0;
			_cY = 0;
			_cWidth = g.width();
			_cHeight = g.height();
		}
		
		int myCaptureX = CCMath.clamp(_cX, 0, g.width());
		int myCaptureY = CCMath.clamp(_cY, 0, g.height());
		int myCaptureWidth = _cWidth == 0 ? g.width() : _cWidth;
		int myCaptureHeight = _cHeight == 0 ? g.height() : _cHeight;
		myCaptureWidth = CCMath.min(myCaptureWidth, g.width() - myCaptureX);
		myCaptureHeight = CCMath.min(myCaptureHeight, g.height() - myCaptureY);
		
		if(!_myIsRecording){
			if(_cDrawCaptureBounds){
				g.beginOrtho2D();
				g.beginShape(CCDrawMode.LINE_LOOP);
				g.vertex(_cX, _cY);
				g.vertex(_cX + _cWidth, _cY);
				g.vertex(_cX + _cWidth, _cY + _cHeight);
				g.vertex(_cX, _cY + _cHeight);
				g.endShape();
				g.endOrtho2D();
			}
			return;
		}
		
		CCLog.info(_myRecordPath);
		if(_mySequenceSteps == 1){
			CCScreenCapture.capture(_myRecordPath, myCaptureX, myCaptureY, myCaptureWidth, myCaptureHeight, _cAlpha);
		}else{
			CCScreenCapture.capture(_myRecordPath.resolve("frame_" + CCFormatUtil.nf(_myStep, 5) + "." + _myFormat.fileExtension), myCaptureX, myCaptureY, myCaptureWidth, myCaptureHeight, _cAlpha);
		}
		_myStep++;
		_myAnimator.fixedUpdateTime = 1f / (_myCaptureRate);
		if(_myRecordTimeline){
			_myTimelineTime += _myAnimator.deltaTime();
			_myTransportController.time(_myTimelineTime);
		}
		double myProgress = (double)_myStep / (double)_mySequenceSteps;
		if(_myProgress != null)_myProgress.progress(myProgress);
		if(myProgress >= 1)end();
	}
	
	public CCListenerManager<CCSequenceRecorderListener> events(){
		return _myRecordListeners;
	}
	
	private CCAnimationMode _myAnimationMode;
	private boolean _myFixUpdateTime = false;
	private double _myFixedUpdateTime = 0;
	
	private TransportController _myTransportController;
	private double _myTimelineTime = 0;
	
	public void startRecord(){
		_myAnimationMode = _myAnimator.animationMode;
		_myFixedUpdateTime = _myAnimator.fixedUpdateTime;
		_myFixUpdateTime = _myAnimator.fixUpdateTime;
		
		_myStep = 0;
		_myAnimator.animationMode = CCAnimationMode.AS_FAST_AS_POSSIBLE;
		_myAnimator.fixedUpdateTime = 1d / (_myCaptureRate);
		_myAnimator.fixUpdateTime = true;
	
		_myIsRecording = true;
		_myRecordListeners.proxy().start();
	}
	
	private Path _myRecordPath = null;
	
	public void end(){
		_myRecordListeners.proxy().end();
		_myAnimator.fixedUpdateTime = _myFixedUpdateTime;
		_myAnimator.fixUpdateTime = _myFixUpdateTime;
		_myAnimator.animationMode = _myAnimationMode;
		
		if(_myProgress != null)_myProgress.end();
		_myIsRecording = false;
	}
	
	private boolean _myRecordTimeline = false;
	
	@CCProperty(name = "record sequence")
	public void recordAndSaveSequence(CCTriggerProgress theProgress) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				_myRecordPath = CCNIOUtil.selectFolder();
				if(_myRecordPath == null)return;
				
				_myRecordTimeline = false;
				_myProgress = theProgress;
				_myProgress.start();

				_mySequenceSteps = _mySeconds * _myCaptureRate;

				startRecord();
			}
		});
	}
	
	@CCProperty(name = "record frame")
	public void recordFrame(){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				_myRecordPath = CCNIOUtil.selectOutput();
				if(_myRecordPath == null)return;
				_myRecordTimeline = false;
				_myProgress = null;
				startRecord();
				_mySequenceSteps = 1;
			}
		});
	}
	
	@CCProperty(name = "record timeline loop")
	public void recordFromTimeline(CCTriggerProgress theProgress){
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				_myRecordPath = CCNIOUtil.selectFolder();
				if(_myRecordPath == null)return;
				
				_myTransportController = _myGLAdapter.timeline().activeTimeline().transportController();
				_myTransportController.stop();
				_myTimelineTime = _myTransportController.loopStart();
				_myTransportController.time(_myTransportController.loopStart());
				_mySequenceSteps = CCMath.floor(_myTransportController.loopRange().length()  * _myCaptureRate);
				
				_myRecordTimeline = true;
				_myProgress = theProgress;
				_myProgress.start();
				startRecord();}
		});
	}
	
	public void recordAndSaveSequence(Path thePath){
		_myRecordPath = thePath;
		if(_myRecordPath == null)return;
		startRecord();
	}
	
	@CCProperty(name = "seconds", min = 1, max = 1000, defaultValue = 100)
	public void seconds(int theSeconds){
		_mySeconds = theSeconds;
	}
	
	public void format(CCImageFormats theFormat){
		_myFormat = theFormat;
	}
}
