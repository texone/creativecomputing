package cc.creativecomputing.kle.sequence;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLAdapter;
import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.io.CCFileFilter;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.CCKleChannel;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.kle.CCKleEffectManager;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.CCKleEffectables;
import cc.creativecomputing.kle.CCKleMapping;
import cc.creativecomputing.kle.CCKleSegment;
import cc.creativecomputing.kle.formats.CCKleFormats;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.easing.CCEasing.CCEaseFormular;
import cc.creativecomputing.math.easing.CCEasing.CCEaseMode;

public class CCSequenceRecorder extends CCAnimatorAdapter{
	
	public static interface CCSequenceRecorderListener{
		public void start();
		
		public void end();
	}
	
	public class CCSequenceChannelRecording extends CCSequence{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4476736064113629689L;

		private CCKleMapping<?> _myMapping;
		
		@CCProperty(name = "update steps", min = 1, max = 10)
		public int updateSteps = 5;
		
		@CCProperty(name = "export")
		public boolean export = true;
		
		@CCProperty(name = "start channel")
		private int _cStartChannel = 0;
		@CCProperty(name = "end channel")
		private int _cEndChannel = 0;

		@CCProperty(name = "use start end channel")
		private boolean _cUseStartEndChannel = false;
		
		public CCSequenceChannelRecording(CCKleMapping<?> theMapping){
			super(theMapping.columns(), theMapping.rows(), theMapping.depth());
			_myMapping = theMapping;
			_cEndChannel = theMapping.size();
		}
		
		public void recordFrame(){
			CCMatrix2 myFrame = new CCMatrix2(_myMapping.columns(), _myMapping.rows(), _myMapping.depth());
			for(CCKleChannel myChannel:_myMapping){
				myFrame.data()[myChannel.column()][myChannel.row()][myChannel.depth()] = _cNormalizeValues ? myChannel.normalizedValue() * _cOutputScale : myChannel.value() * _cOutputScale;
			}
			add(myFrame);
		}
		
		
		public void start(){
			clear();
		}
	}
	
	public static class CCSequenceElementRecording extends CCSequence{
		/**
		 * 
		 */
		private static final long serialVersionUID = 4476736064113629689L;
		
		@CCProperty(name = "export")
		public boolean export = true;
		
		@CCProperty(name = "start element")
		private int _cStartElementl = 0;
		@CCProperty(name = "end element")
		private int _cEndElement = 0;

		@CCProperty(name = "use start end element")
		private boolean _cUseStartEndElement = false;
		
		@CCProperty(name = "record absolute x")
		private boolean _cRecordAbsoluteX = true;
		@CCProperty(name = "record absolute y")
		private boolean _cRecordAbsoluteY = true;
		@CCProperty(name = "record absolute z")
		private boolean _cRecordAbsoluteZ = true;
		
		@CCProperty(name = "record rotate y")
		private boolean _cRecordRotateY = false;
		@CCProperty(name = "record rotate z")
		private boolean _cRecordRotateZ = false;

		@CCProperty(name = "record normalized x")
		private boolean _cRecordNormalizedX = false;
		@CCProperty(name = "record normalized y")
		private boolean _cRecordNormalizedY = false;
		@CCProperty(name = "record normalized z")
		private boolean _cRecordNormalizedZ = false;
		
		@CCProperty(name = "record ropelength 0")
		private boolean _cRecordRopeLength0 = false;
		@CCProperty(name = "record ropelength 1")
		private boolean _cRecordRopeLength1 = false;
		@CCProperty(name = "normalized rope length")
		private boolean _cNormalizedRopeLength = false;
		
		private final List<CCKleEffectable> _myElements;
		
		public CCSequenceElementRecording(List<CCKleEffectable> theElements){
			super(theElements.size(), 1, 8);
			_myElements = theElements;
			_cEndElement = _myElements.size();
		}
		
		public void recordFrame(){
			if(!export)return;
			
			CCMatrix2 myFrame = new CCMatrix2(_myElements.size(), 1, 10);
			for(CCKleEffectable myElement:_myElements){
				
				CCVector3 myElementPosition = myElement.motorSetup().position();
				myFrame.data()[myElement.id()][0][0] = myElementPosition.x;
				myFrame.data()[myElement.id()][0][1] = myElementPosition.y;
				myFrame.data()[myElement.id()][0][2] = -myElementPosition.z;
				myFrame.data()[myElement.id()][0][3] = myElement.motorSetup().rotateY();
				myFrame.data()[myElement.id()][0][4] = -myElement.motorSetup().rotateZ();
				
				CCVector3 myRelativePosition = myElement.motorSetup().relativeOffset();
				myFrame.data()[myElement.id()][0][5] = myRelativePosition.x;
				myFrame.data()[myElement.id()][0][6] = myRelativePosition.y;
				myFrame.data()[myElement.id()][0][7] = myRelativePosition.z;
				
				List<CCMotorChannel> myChannels = myElement.motorSetup().channels();
				if(myChannels.size() > 0){
					myFrame.data()[myElement.id()][0][8] = _cNormalizedRopeLength ? myChannels.get(0).normalizedValue() : myChannels.get(0).value();
				}
				if(myChannels.size() > 1){
					myFrame.data()[myElement.id()][0][9] = _cNormalizedRopeLength ? myChannels.get(1).normalizedValue() : myChannels.get(1).value();
				}
			}
			add(myFrame);
		}
		
		
		public void start(){
			if(!export)return;
			clear();
		}
		
		public void save(Path thePath, CCKleFormats theFormat){
			if(!export)return;
			CCNIOUtil.createDirectories(thePath);
			theFormat.save(thePath, null, this);
		}
		
		public void savePositions(Path thePath, CCKleFormats theFormat){
			if(!export)return;
			CCNIOUtil.createDirectories(thePath);
			boolean[] myExports = new boolean[]{
				_cRecordAbsoluteX, 
				_cRecordAbsoluteY, 
				_cRecordAbsoluteZ, 
				
				_cRecordRotateY, 
				_cRecordRotateZ, 
				
				_cRecordNormalizedX, 
				_cRecordNormalizedY,
				_cRecordNormalizedZ, 
				
				_cRecordRopeLength0, 
				_cRecordRopeLength1
			};
			theFormat.format().savePosition(thePath, this, myExports);
		}
	}
	
	private CCListenerManager<CCSequenceRecorderListener> _myRecordListeners = CCListenerManager.create(CCSequenceRecorderListener.class);

	@CCProperty(name = "seconds", min = 1, max = 1000)
	private int _mySeconds = 100;

	@CCProperty(name = "fade seconds", min = 0, max = 100)
	private int _myFadeSeconds = 10;
	@CCProperty(name = "still frames", min = 0, max = 100)
	private int _cStillFrames = 10;
	
	@CCProperty(name = "fade ease formular")
	private CCEaseFormular _myEaseFormular = CCEaseFormular.SINE;
	
	@CCProperty(name = "fade ease mode")
	private CCEaseMode _myEaseMode = CCEaseMode.IN_OUT;
	
	@CCProperty(name = "output scale", min = 0, max = 20)
	private double _cOutputScale = 1;
	@CCProperty(name = "record positions")
	private boolean _cRecordPositions = false;
	
	@CCProperty(name = "segment name")
	private String _mySegmentName = "";
	
	@CCProperty(name = "recording channels", hide = true)
	private Map<CCKleChannelType, CCSequence> _myRecordings = new HashMap<>();
	
	private Map<CCKleChannelType, CCKleEffectManager> _myEffectManagers = new HashMap<>();
	
	@CCProperty(name = "position recording")
	private CCSequenceElementRecording _myPositionRecording;
	@CCProperty(name = "normalize values")
	private boolean _cNormalizeValues = false;
	
	private final CCAnimator _myAnimator;
	
	private CCGLAdapter<?,?> _myGLAdapter;
	
	private final CCKleEffectables _myElements;
	
	private CCSequenceExporter _myExporter;
	

	private int _myStep = 0;
	private int _mySequenceSteps = 0;
	private int _myFadeSteps = 0;
	private boolean _myIsRecording = false;
	
	private CCTriggerProgress _myProgress;
	
	private CCKleChannelType _myCurrentType;
	private CCSequenceChannelRecording _myCurrentRecording;
	private CCKleEffectManager _myCurrentEffectManager;
	
	private List<CCKleChannelType> _myChannelsToRecord = new ArrayList<>();
	
	private static enum CCRecordMode{
		SEQUENCE, TIMELINE, FRAME
	}
	
	private CCRecordMode _myRecordMode = CCRecordMode.SEQUENCE;
	
	public CCSequenceRecorder(CCGLAdapter<?, ?> theGLAdapter, CCKleEffectables theElements, CCAnimator theAnimator){
		_myGLAdapter = theGLAdapter;
		_myElements = theElements;
		for(CCKleChannelType myKey:theElements.mappings().keySet()){
			CCKleMapping<?> myMapping = theElements.mappings().get(myKey);
			_myRecordings.put(myKey, new CCSequenceChannelRecording(myMapping));
		}
		_myAnimator = theAnimator;
		
		_myPositionRecording = new CCSequenceElementRecording(_myElements);
		
		_myExporter = new CCSequenceExporter(_myElements);
	}
	
	public void addEffectManager(CCKleEffectManager theEffectManager, CCKleChannelType theType){
		_myEffectManagers.put(theType, theEffectManager);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		if(!_myIsRecording)return;
		
		if(_myCurrentType == null){
			for(CCKleChannelType myChannelType:_myRecordings.keySet()){
				CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get(myChannelType);
				_myCurrentEffectManager = _myEffectManagers.get(myChannelType);
				_myCurrentEffectManager.isInRecord = true;
				if(_myCurrentEffectManager == null)continue;
				if(myRecording.export)_myChannelsToRecord.add(myChannelType);
			}
			if(_myChannelsToRecord.size() <= 0)_myIsRecording = false;
			_myCurrentType = _myChannelsToRecord.remove(0);
			_myCurrentRecording = (CCSequenceChannelRecording)_myRecordings.get(_myCurrentType);
			_myCurrentEffectManager = _myEffectManagers.get(_myCurrentType);
			CCLog.info(_myCurrentType + ":" + _myRecordMode);
			startRecord(_myCurrentType);
		}
		
		for(int i = 0; i < _myCurrentRecording.updateSteps;i++){
			double myUpdateTime = (1d / _myCurrentRecording._myMapping.frameRate());
			if(_myRecordMode == CCRecordMode.TIMELINE)myUpdateTime *= _myTransportController.speed();
			double myFade = 1.0;
			if(_myFadeSteps > 0){
				if(_myStep < _myFadeSteps){
					myFade = _myEaseFormular.easing().ease(_myEaseMode, _myStep / (double)_myFadeSteps);
				}else if(_myStep > _mySequenceSteps - _myFadeSteps){
					myFade = (_mySequenceSteps - _myStep) /  (double)_myFadeSteps;
					myFade = _myEaseFormular.easing().ease(_myEaseMode, myFade);
				}
				myUpdateTime =  myFade == 0 ? 0 : (1d / _myCurrentRecording._myMapping.frameRate()) * myFade * _myTransportController.speed();
				_myAnimator.fixedUpdateTime = myUpdateTime;
			}
			
			_myStep++;
			if(_myRecordMode == CCRecordMode.TIMELINE){
	//			_myAnimator.fixedUpdateTime = 1f / (_myCurrentRecording._myMapping.frameRate() * _myTransportController.speed());
				_myTimelineTime += myUpdateTime;
				_myTransportController.time(_myTimelineTime);
			}
	
			for(CCKleEffectManager myManager:_myEffectManagers.values()){
				if(_myRecordMode != CCRecordMode.FRAME)myManager.updateRecord(_myAnimator);
			}
			if(_myCurrentType == CCKleChannelType.MOTORS)_myPositionRecording.recordFrame();
			_myCurrentRecording.recordFrame();
	
			CCLog.info(
				_myCurrentType + 
					" : step " + _myStep + 
					" : sequencestep " + _mySequenceSteps + 
					" : fade step " + _myFadeSteps + 
					" : fade seconds " + _myFadeSeconds + 
					" : fade " + myFade + 
					" : update time " + myUpdateTime + 
					" : timelinetime " + _myTimelineTime);
	//			CCLog.info(_myCurrentType + ":" + _myStep + ":" + _mySequenceSteps + " : " + _myTransportController.time() + " : ");
			double myProgress = (double)_myStep / (double)_mySequenceSteps;
			if(_myProgress != null)_myProgress.progress(myProgress);
			if(myProgress >= 1){
				if(_myChannelsToRecord.size() <= 0){
					for(CCKleChannelType myChannelType:_myRecordings.keySet()){
						_myCurrentEffectManager = _myEffectManagers.get(myChannelType);
						_myCurrentEffectManager.isInRecord = false;
					}
					save(_myRecordPath);
					return;
				}
				_myCurrentType = _myChannelsToRecord.remove(0);
				_myCurrentRecording = (CCSequenceChannelRecording)_myRecordings.get(_myCurrentType);
				_myCurrentEffectManager = _myEffectManagers.get(_myCurrentType);
				startRecord(_myCurrentType);
			}
		}
		_myAnimator.fixedUpdateTime = 1d / _myCurrentRecording._myMapping.frameRate();
	}
	
	public CCSequence sequence(CCKleChannelType theKey){
		return _myRecordings.get(theKey.id());
	}
	
	public CCSequenceChannelRecording recording(CCKleChannelType theKey){
		return (CCSequenceChannelRecording)_myRecordings.get(theKey.id());
	}
	
	public CCListenerManager<CCSequenceRecorderListener> events(){
		return _myRecordListeners;
	}
	
	private CCAnimator.CCAnimationMode _myAnimationMode;
	private boolean _myFixUpdateTime = false;
	private double _myFixedUpdateTime = 0;
	
	private CCTransportController _myTransportController;
	private double _myTimelineTime = 0;
	

	
	public void startRecord(CCKleChannelType theKey){
		getSegments();
		
		CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get(theKey);
		myRecording.start();
		CCKleMapping<?> myMapping = myRecording._myMapping;
		
		_myPositionRecording.start();

		_myAnimationMode = _myAnimator.animationMode;
		_myFixedUpdateTime = _myAnimator.fixedUpdateTime;
		_myFixUpdateTime = _myAnimator.fixUpdateTime;
		
		_myStep = 0;
		switch(_myRecordMode){
		case TIMELINE:
			_myTransportController = _myGLAdapter.timeline().activeTimeline().transportController();
			_myTransportController.stop();
			_myTimelineTime = _myTransportController.loopStart();
			_myTransportController.time(_myTransportController.loopStart());
			_myTransportController.doLoop(false);
			_myTransportController.play();
			
			_mySequenceSteps = CCMath.ceil(_myTransportController.loopRange().length() / _myTransportController.speed())  * myMapping.frameRate();
			_myFadeSteps = _myFadeSeconds * myMapping.frameRate();
			_myAnimator.animationMode = CCAnimator.CCAnimationMode.AS_FAST_AS_POSSIBLE;
			_myAnimator.fixedUpdateTime = (1f / (myMapping.frameRate())) * _myTransportController.speed();
			_myAnimator.fixUpdateTime = true;
			if(_myFadeSteps > 0)_myAnimator.fixedUpdateTime = 0;
			break;
		case SEQUENCE:
			_mySequenceSteps = (_mySeconds + 2 * _myFadeSeconds)  * myMapping.frameRate();
			_myFadeSteps = _myFadeSeconds * myMapping.frameRate();
			_myAnimator.animationMode = CCAnimator.CCAnimationMode.AS_FAST_AS_POSSIBLE;
			_myAnimator.fixedUpdateTime = 1f / (myMapping.frameRate());
			_myAnimator.fixUpdateTime = true;
			if(_myFadeSteps > 0)_myAnimator.fixedUpdateTime = 0;
			break;
		case FRAME:
			_mySequenceSteps = _cStillFrames;
			_myFadeSteps = 0;
			_myAnimator.animationMode = CCAnimator.CCAnimationMode.AS_FAST_AS_POSSIBLE;
			_myAnimator.fixedUpdateTime = 0;
			_myAnimator.fixUpdateTime = true;
			break;
		}
		
		
		_myIsRecording = true;
		_myRecordListeners.proxy().start();
	}
	
	private Path _myRecordPath = null;
	
	public static void main(String[] args) {
		CCFileChooser _myFileChooser = new CCFileChooser();
		_myFileChooser.setAcceptAllFileFilterUsed(false);
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("KLE_1"));
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("KLE_2"));
		for(CCKleFormats myFormat:CCKleFormats.values()){
			_myFileChooser.addChoosableFileFilter(new CCFileFilter(myFormat.name()));
		}
		
		int myRetVal = _myFileChooser.show("");
		if (myRetVal == JFileChooser.APPROVE_OPTION) {
			try {
				Path myChoosenPath = _myFileChooser.path();
				String myExtension = _myFileChooser.extension();
				
				CCLog.info(myChoosenPath);
				
				switch(myExtension){
				case "KLE_1":
					myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, "kle");
//					Path myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
//					
//					if(myPath == null)return;bla
//		
//					CCSequenceKLE1Container myKLE1Container = new CCSequenceKLE1Container();
//					CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get("motors");
//					if(myRecording != null){
//						myKLE1Container.useStartEndChannels(myRecording._cUseStartEndChannel);
//						myKLE1Container.startChannel(myRecording._cStartChannel);
//						myKLE1Container.endChannel(myRecording._cEndChannel);
//					}
//					myKLE1Container.save(myPath, _myElements, _myRecordings);
					break;
				case "KLE_2":
					myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, "kle");
//					myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
//					if(myPath == null)return;
//		
//					CCSequenceKLE2Container myKLEContainer = new CCSequenceKLE2Container();
//					myKLEContainer.save(myPath, _myElements, _myRecordings);
					break;
				default:
					CCKleFormats myFormat = CCKleFormats.valueOf(myExtension);
//					String myEnteredExtension = CCNIOUtil.fileExtension(myChoosenPath);
					if(!myFormat.isFolder()){
						myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, myFormat.extension());
					}
//					if(_myFormat == CCSequenceFormats.NONE)return;
//					if(!myFormat.savePosition()){
//						for(String myKey:_myRecordings.keySet()){
//							myRecording = (CCSequenceChannelRecording)_myRecordings.get(myKey);
//							if(!myRecording.export)continue;
//							if(myFormat.isFolder()){
//								myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
//							}else{
//								myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
//							}
//							if(myPath == null)return;
//							myRecording.save(myPath);
//						}
//					}else{
//						if(myFormat.isFolder()){
//							myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
//						}else{
//							myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
//						}
//						if(myPath == null)return;
//						_myElementRecording.save(myPath);
//					}
//					
				}
				CCLog.info(myChoosenPath);
				
				
			} catch (RuntimeException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	private List<CCKleSegment> getSegments(){
		
		for(CCTrackController myTrack:_myGLAdapter.timeline().activeTimeline().trackController()){
			if(myTrack.property().path().toString().endsWith("/record/segment name")){

				CCLog.info(myTrack.property().path().toString());
				List<CCKleSegment> myResult = new ArrayList<>();
				for(ControlPoint myPoint:myTrack.trackData()){
					TimedEventPoint myEventPoint = (TimedEventPoint)myPoint;
					CCLog.info(myEventPoint.content().value() + ":" + myEventPoint.time() + ":" + myEventPoint.endTime());
					CCKleSegment mySegment = new CCKleSegment(
						myEventPoint.content().value().toString(), 
						myEventPoint.time(),
						myEventPoint.endTime()
					);
					myResult.add(mySegment);
				}
				return myResult;
			}
		}
		return null;
	}
	
	public void save(Path theRecordPath){
		_myRecordListeners.proxy().end();
		_myAnimator.fixedUpdateTime = _myFixedUpdateTime;
		_myAnimator.fixUpdateTime = _myFixUpdateTime;
		_myAnimator.animationMode = _myAnimationMode;
		
		if(_myProgress != null)_myProgress.end();
		_myIsRecording = false;
		_myCurrentType = null;
		
		
		if(_cRecordPositions)_myExporter.savePositions(theRecordPath, _myPositionRecording);
		else{
//			if(_cNormalizeValues){
//				for(CCSequence mySequence:_myRecordings.values()){
//					CCVector2 myMinMax = mySequence.minMax();
//					for(CCMatrix2 myFrame:mySequence){
//						for (int c = 0; c < mySequence.columns(); c++) {
//							for (int r = 0; r < mySequence.rows(); r++) {
//								for (int d = 0; d < mySequence.depth(); d++) {
//									myFrame.data()[c][r][d] = CCMath.norm(myFrame.data()[c][r][d], myMinMax.x, myMinMax.y);
//								}
//							}
//						}
//					}
//				}
//			}
			_myExporter.save(theRecordPath, _myRecordings, getSegments());
		}
	}
	
	@CCProperty(name = "record sequence")
	public void recordAndSaveSequence(CCTriggerProgress theProgress){
		_myRecordMode = CCRecordMode.SEQUENCE;
		_myRecordPath = null;
		_myProgress = theProgress;
		_myProgress.start();
		_myIsRecording = true;
	}
	
	@CCProperty(name = "record timeline loop")
	public void recordFromTimeline(CCTriggerProgress theProgress){
		_myRecordMode = CCRecordMode.TIMELINE;
		_myRecordPath = null;
		_myProgress = theProgress;
		_myProgress.start();
		_myIsRecording = true;
	}
	
	@CCProperty(name = "record frame")
	public void recordFrame(CCTriggerProgress theProgress){
		_myRecordMode = CCRecordMode.FRAME;
		_myRecordPath = null;
		_myProgress = theProgress;
		_myProgress.start();
		_myIsRecording = true;
	}
	
	public void recordAndSaveSequence(Path thePath){
		_myRecordPath = thePath;
		_myIsRecording = true;
	}
	
	public void seconds(int theSeconds){
		_mySeconds = theSeconds;
	}
	
	public void fadeSeconds(int theFadeSeconds){
		_myFadeSeconds = theFadeSeconds;
	}

}
