package cc.creativecomputing.kle;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.controlui.timeline.controller.TransportController;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLAdapter;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceChannel;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.formats.CCSequenceContainers;
import cc.creativecomputing.kle.formats.CCSequenceFormats;
import cc.creativecomputing.kle.formats.CCSequenceKLE1Container;
import cc.creativecomputing.kle.formats.CCSequenceKLE2Container;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;
import cc.creativecomputing.math.CCVector3;

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

		private CCSequenceMapping<?> _myMapping;
		
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
		
		private int _myRateBrake;
		
		private CCKleChannelType _myMappingType;
		
		public CCSequenceChannelRecording(CCKleChannelType theMappingType, CCSequenceMapping<?> theMapping){
			super(theMapping.columns(), theMapping.rows(), theMapping.depth());
			_myMapping = theMapping;
			_myMappingType = theMappingType;
			_myRateBrake = _myBaseRate / (theMapping.frameRate() * updateSteps);
			_cEndChannel = theMapping.size();
		}
		
		public void recordFrame(){
			if(!export)return;
			if(_myStep % _myRateBrake != 0)return;
			
			CCMatrix2 myFrame = new CCMatrix2(_myMapping.columns(), _myMapping.rows(), _myMapping.depth());
			for(CCSequenceChannel myChannel:_myMapping){
				myFrame.data()[myChannel.column()][myChannel.row()][myChannel.depth()] = myChannel.value() * _cOutputScale;
			}
			add(myFrame);
		}
		
		
		public void start(){
			_myRateBrake = _myBaseRate / (_myMapping.frameRate());
			if(!export)return;
			clear();
		}
		
		public void save(Path thePath){
			if(!export)return;
			CCNIOUtil.createDirectories(thePath);
			_myFormat.save(thePath, _myMapping, this);
		}
	}
	
	public class CCSequenceElementRecording extends CCSequence{
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
		
		private int _myFrameRate;
		
		public CCSequenceElementRecording(int theFrameRate){
			super(_myElements.size(), 1, 5);
			_myFrameRate = theFrameRate;
			_cEndElement = _myElements.size();
		}
		
		public void recordFrame(){
			if(!export)return;
			
			CCMatrix2 myFrame = new CCMatrix2(_myElements.size(), 1, 5);
			for(CCSequenceElement myElement:_myElements){
				CCVector3 myElementPosition = myElement.motorSetup().position();
				myFrame.data()[myElement.id()][0][0] = myElementPosition.x;
				myFrame.data()[myElement.id()][0][1] = myElementPosition.y;
				myFrame.data()[myElement.id()][0][2] = -myElementPosition.z;
				myFrame.data()[myElement.id()][0][3] = myElement.motorSetup().rotateY();
				myFrame.data()[myElement.id()][0][4] = -myElement.motorSetup().rotateZ();
			}
			add(myFrame);
		}
		
		
		public void start(){
			if(!export)return;
			clear();
		}
		
		public void save(Path thePath){
			if(!export)return;
			CCNIOUtil.createDirectories(thePath);
			_myFormat.save(thePath, null, this);
		}
	}
	
	private CCListenerManager<CCSequenceRecorderListener> _myRecordListeners = CCListenerManager.create(CCSequenceRecorderListener.class);

	private int _mySeconds = 100;

	private int _myFadeSeconds = 100;
	
	
	@CCProperty(name = "format")
	private CCSequenceFormats _myFormat = CCSequenceFormats.NONE;
	
	@CCProperty(name = "output scale", min = 0, max = 20)
	private double _cOutputScale = 1;
	
	
	
	@CCProperty(name = "container")
	private CCSequenceContainers _myContainers = CCSequenceContainers.INDIVIDUAL;
	
	private int _myBaseRate;
	
	@CCProperty(name = "recording channels")
	private Map<String, CCSequence> _myRecordings = new HashMap<>();
	
	private CCSequenceElementRecording _myElementRecording;
	
	private final CCAnimator _myAnimator;
	
	private CCGLAdapter _myGLAdapter;
	
	private CCSequenceElements _myElements;
	
	public CCSequenceRecorder(CCGLAdapter<?, ?> theGLAdapter, CCSequenceElements theElements, CCAnimator theAnimator){
		_myGLAdapter = theGLAdapter;
		_myElements = theElements;
		_myBaseRate = 1;
		for(CCKleChannelType myKey:theElements.mappings().keySet()){
			CCSequenceMapping myMapping = theElements.mappings().get(myKey);
			_myBaseRate = CCMath.leastCommonMultiple(_myBaseRate, myMapping.frameRate());
			_myRecordings.put(myKey.id(), new CCSequenceChannelRecording(myKey, myMapping));
		}
		_myAnimator = theAnimator;
		_myAnimator.listener().add(this);
		
		_myElementRecording = new CCSequenceElementRecording(30);
		
	}
	
	private int _myStep = 0;
	private int _mySequenceSteps = 0;
	private int _myFadeSteps = 0;
	private boolean _myIsRecording = false;
	
	private CCTriggerProgress _myProgress;
	
	@Override
	public void update(CCAnimator theAnimator) {
		if(!_myIsRecording)return;
		
		recordFrame();
		_myStep++;
		if(_myRecordTimeline){
			_myAnimator.fixedUpdateTime = 1f / (_myBaseRate);
			_myTimelineTime += _myAnimator.deltaTime();
			_myTransportController.time(_myTimelineTime);
			CCLog.info(_myTimelineTime);
		}else{
			if(_myFadeSteps > 0){
				if(_myStep < _myFadeSteps){
					_myAnimator.fixedUpdateTime = 1f / (_myBaseRate) * (_myStep / (double)_myFadeSteps);
				}else if(_myStep > _mySequenceSteps - _myFadeSteps){
					double myFade = (_mySequenceSteps - _myStep) / (double)_myFadeSteps;
					_myAnimator.fixedUpdateTime = 1f / (_myBaseRate) * myFade;
				}else{
					_myAnimator.fixedUpdateTime = 1f / (_myBaseRate);
				}
			}
		}
		double myProgress = (double)_myStep / (double)_mySequenceSteps;
		if(_myProgress != null)_myProgress.progress(myProgress);
		if(myProgress >= 1)save(_myRecordPath);
	}
	
	public void recordFrame(){
		if(_myFormat.savePosition()){
			_myElementRecording.recordFrame();
		}else{
			for(String myKey:_myRecordings.keySet()){
				CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get(myKey);
				myRecording.recordFrame();
			}
		}
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
	
	private CCAnimationMode _myAnimationMode;
	private boolean _myFixUpdateTime = false;
	private double _myFixedUpdateTime = 0;
	
	private TransportController _myTransportController;
	private double _myTimelineTime = 0;
	
	public void startRecord(){
		_myBaseRate = 1;
		
		if(!_myFormat.savePosition()){
			for(String myKey:_myRecordings.keySet()){
				
				CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get(myKey);
				if(!myRecording.export)continue;
	
				CCSequenceMapping myMapping = myRecording._myMapping;
				_myBaseRate = CCMath.leastCommonMultiple(_myBaseRate, myMapping.frameRate() * myRecording.updateSteps);
			}
			for(CCSequence mySequence:_myRecordings.values()){
				CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)mySequence;
				myRecording.start();
			}
		}else{
			_myElementRecording.start();
			_myBaseRate = _myElementRecording._myFrameRate;
		}

		_myAnimationMode = _myAnimator.animationMode;
		_myFixedUpdateTime = _myAnimator.fixedUpdateTime;
		_myFixUpdateTime = _myAnimator.fixUpdateTime;
		
		_myStep = 0;
		if(_myRecordTimeline){
			_myTransportController = _myGLAdapter.timeline().activeTimeline().transportController();
			_myTransportController.stop();
			_myTimelineTime = _myTransportController.loopStart();
			_myTransportController.time(_myTransportController.loopStart());
			_myTransportController.play();
			
			_mySequenceSteps = CCMath.ceil(_myTransportController.loopRange().length())  * _myBaseRate;
			_myFadeSteps = _myFadeSeconds * _myBaseRate;
			_myAnimator.animationMode = CCAnimationMode.AS_FAST_AS_POSSIBLE;
			_myAnimator.fixedUpdateTime = 1f / (_myBaseRate);
			_myAnimator.fixUpdateTime = true;
			if(_myFadeSteps > 0)_myAnimator.fixedUpdateTime = 0;
		}else{
			_mySequenceSteps = (_mySeconds + 2 * _myFadeSeconds)  * _myBaseRate;
			_myFadeSteps = _myFadeSeconds * _myBaseRate;
			_myAnimator.animationMode = CCAnimationMode.AS_FAST_AS_POSSIBLE;
			_myAnimator.fixedUpdateTime = 1f / (_myBaseRate);
			_myAnimator.fixUpdateTime = true;
			if(_myFadeSteps > 0)_myAnimator.fixedUpdateTime = 0;
		}
		
		_myIsRecording = true;
		_myRecordListeners.proxy().start();
	}
	
	private Path _myRecordPath = null;
	
	public void save(Path theRecordPath){
		_myRecordListeners.proxy().end();
		_myAnimator.fixedUpdateTime = _myFixedUpdateTime;
		_myAnimator.fixUpdateTime = _myFixUpdateTime;
		_myAnimator.animationMode = _myAnimationMode;
		
		if(_myProgress != null)_myProgress.end();
		_myIsRecording = false;
		
		
		switch(_myContainers){
		case INDIVIDUAL:
			if(_myFormat == CCSequenceFormats.NONE)return;
			if(!_myFormat.savePosition()){
				for(String myKey:_myRecordings.keySet()){
					Path myPath;
					CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get(myKey);
					if(_myFormat.isFolder()){
						myPath = theRecordPath == null ? CCNIOUtil.selectFolder() : theRecordPath;
					}else{
						myPath = theRecordPath == null ? CCNIOUtil.selectOutput() : theRecordPath;
					}
					if(myPath == null)return;
					myRecording.save(myPath);
				}
			}else{
				Path myPath;
				if(_myFormat.isFolder()){
					myPath = theRecordPath == null ? CCNIOUtil.selectFolder() : theRecordPath;
				}else{
					myPath = theRecordPath == null ? CCNIOUtil.selectOutput() : theRecordPath;
				}
				if(myPath == null)return;
				_myElementRecording.save(myPath);
			}
			break;
		case KLE_1:
			Path myPath = theRecordPath == null ? CCNIOUtil.selectOutput() : theRecordPath;
			
			if(myPath == null)return;

			CCSequenceKLE1Container myKLE1Container = new CCSequenceKLE1Container();
			CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get("motors");
			if(myRecording != null){
				myKLE1Container.useStartEndChannels(myRecording._cUseStartEndChannel);
				myKLE1Container.startChannel(myRecording._cStartChannel);
				myKLE1Container.endChannel(myRecording._cEndChannel);
			}
			myKLE1Container.save(myPath, _myElements, _myRecordings);
			break;
		case KLE_2:
			myPath = theRecordPath == null ? CCNIOUtil.selectOutput() : theRecordPath;
			if(myPath == null)return;

			CCSequenceKLE2Container myKLEContainer = new CCSequenceKLE2Container();
			myKLEContainer.save(myPath, _myElements, _myRecordings);
			break;
			
		}
	}
	
	private boolean _myRecordTimeline = false;
	
	@CCProperty(name = "record sequence")
	public void recordAndSaveSequence(CCTriggerProgress theProgress){
		_myRecordTimeline = false;
		_myRecordPath = null;
		_myProgress = theProgress;
		_myProgress.start();
		startRecord();
	}
	
	@CCProperty(name = "record timeline loop")
	public void recordFromTimelien(CCTriggerProgress theProgress){
		_myRecordTimeline = true;
		_myRecordPath = null;
		_myProgress = theProgress;
		_myProgress.start();
		startRecord();
	}
	
	public void recordAndSaveSequence(Path thePath){
		_myRecordPath = thePath;
		startRecord();
	}
	
	@CCProperty(name = "seconds", min = 1, max = 1000, defaultValue = 100)
	public void seconds(int theSeconds){
		_mySeconds = theSeconds;
	}
	
	@CCProperty(name = "fade seconds", min = 0, max = 100, defaultValue = 10)
	public void fadeSeconds(int theFadeSeconds){
		_myFadeSeconds = theFadeSeconds;
	}
	
	public void format(CCSequenceFormats theFormat){
		_myFormat = theFormat;
	}
	
	public void container(CCSequenceContainers theContainer){
		_myContainers = theContainer;
	}
}
