package cc.creativecomputing.kle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFileChooser;
import javax.swing.SwingUtilities;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimatorAdapter;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLAdapter;
import cc.creativecomputing.io.CCFileChooser;
import cc.creativecomputing.io.CCFileFilter;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.kle.elements.CCKleChannelType;
import cc.creativecomputing.kle.elements.CCSequenceChannel;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.CCSequenceElements;
import cc.creativecomputing.kle.elements.CCSequenceMapping;
import cc.creativecomputing.kle.elements.CCSequenceSegment;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.kle.formats.CCSequenceFormats;
import cc.creativecomputing.kle.formats.CCSequenceKLE1Container;
import cc.creativecomputing.kle.formats.CCSequenceKLE2Container;
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
		
		public CCSequenceChannelRecording(CCSequenceMapping<?> theMapping){
			super(theMapping.columns(), theMapping.rows(), theMapping.depth());
			_myMapping = theMapping;
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
		
		public void save(Path thePath, CCSequenceFormats theFormat){
			if(!export)return;
			CCNIOUtil.createDirectories(thePath);
			theFormat.save(thePath, _myMapping, this);
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
		
		private final CCSequenceElements _myElements;
		
		public CCSequenceElementRecording(CCSequenceElements theElements){
			super(theElements.size(), 1, 8);
			_myElements = theElements;
			_cEndElement = _myElements.size();
		}
		
		public void recordFrame(){
			if(!export)return;
			
			CCMatrix2 myFrame = new CCMatrix2(_myElements.size(), 1, 10);
			for(CCSequenceElement myElement:_myElements){
				
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
					myFrame.data()[myElement.id()][0][8] = myChannels.get(0).value();
				}
				if(myChannels.size() > 1){
					myFrame.data()[myElement.id()][0][9] = myChannels.get(1).value();
				}
			}
			add(myFrame);
		}
		
		
		public void start(){
			if(!export)return;
			clear();
		}
		
		public void save(Path thePath, CCSequenceFormats theFormat){
			if(!export)return;
			CCNIOUtil.createDirectories(thePath);
			theFormat.save(thePath, null, this);
		}
		
		public void savePositions(Path thePath, CCSequenceFormats theFormat){
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

	@CCProperty(name = "seconds", min = 1, max = 1000, defaultValue = 100)
	private int _mySeconds = 100;

	@CCProperty(name = "fade seconds", min = 0, max = 100, defaultValue = 10)
	private int _myFadeSeconds = 100;
	
	@CCProperty(name = "fade ease formular")
	private CCEaseFormular _myEaseFormular = CCEaseFormular.SINE;
	
	@CCProperty(name = "fade ease mode")
	private CCEaseMode _myEaseMode = CCEaseMode.IN_OUT;
	
	@CCProperty(name = "output scale", min = 0, max = 20)
	private double _cOutputScale = 1;
	@CCProperty(name = "record positions")
	private boolean _cRecordPositions = false;
	
	private int _myBaseRate;
	
	@CCProperty(name = "recording channels")
	private Map<CCKleChannelType, CCSequence> _myRecordings = new HashMap<>();
	@CCProperty(name = "segment name")
	private String _mySegmentName = "";
	
	@CCProperty(name = "position recording")
	private CCSequenceElementRecording _myPositionRecording;
	
	private final CCAnimator _myAnimator;
	
	private CCGLAdapter<?,?> _myGLAdapter;
	
	private final CCSequenceElements _myElements;
	
	private CCFileChooser _myFileChooser = new CCFileChooser();
	
	public CCSequenceRecorder(CCGLAdapter<?, ?> theGLAdapter, CCSequenceElements theElements, CCAnimator theAnimator){
		_myGLAdapter = theGLAdapter;
		_myElements = theElements;
		_myBaseRate = 1;
		for(CCKleChannelType myKey:theElements.mappings().keySet()){
			CCSequenceMapping<?> myMapping = theElements.mappings().get(myKey);
			_myBaseRate = CCMath.leastCommonMultiple(_myBaseRate, myMapping.frameRate());
			_myRecordings.put(myKey, new CCSequenceChannelRecording(myMapping));
		}
		_myAnimator = theAnimator;
		_myAnimator.listener().add(this);
		
		_myPositionRecording = new CCSequenceElementRecording(_myElements);
		
		_myFileChooser = new CCFileChooser();
		_myFileChooser.setAcceptAllFileFilterUsed(false);
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("KLE_1", "kle"));
		_myFileChooser.addChoosableFileFilter(new CCFileFilter("KLE_2", "kle"));
		for(CCSequenceFormats myFormat:CCSequenceFormats.values()){
			if(myFormat == CCSequenceFormats.NONE)continue;
			_myFileChooser.addChoosableFileFilter(new CCFileFilter(myFormat.name()));
		}
	}
	
	private int _myStep = 0;
	private int _mySequenceSteps = 0;
	private int _myFadeSteps = 0;
	private boolean _myIsRecording = false;
	
	private CCTriggerProgress _myProgress;
	
	@Override
	public void update(CCAnimator theAnimator) {
		if(!_myIsRecording)return;
		
		_myStep++;
		if(_myRecordTimeline){
			_myAnimator.fixedUpdateTime = 1f / (_myBaseRate) * _myTransportController.speed();
			_myTimelineTime += _myAnimator.deltaTime() * _myTransportController.speed();
			_myTransportController.time(_myTimelineTime);
			recordFrame();
		}else{
			if(_myFadeSteps > 0){
				if(_myStep < _myFadeSteps){
					_myAnimator.fixedUpdateTime = 1f / (_myBaseRate) * _myEaseFormular.easing().ease(_myEaseMode, _myStep / (double)_myFadeSteps);
				}else if(_myStep > _mySequenceSteps - _myFadeSteps){
					double myFade = (_mySequenceSteps - _myStep) / (double)_myFadeSteps;
					_myAnimator.fixedUpdateTime = 1f / (_myBaseRate) * _myEaseFormular.easing().ease(_myEaseMode, myFade);
				}else{
					_myAnimator.fixedUpdateTime = 1f / (_myBaseRate);
				}
			}
			recordFrame();
		}
		CCLog.info(_myStep + ":" + _mySequenceSteps);
		double myProgress = (double)_myStep / (double)_mySequenceSteps;
		if(_myProgress != null)_myProgress.progress(myProgress);
		if(myProgress >= 1)save(_myRecordPath);
	}
	
	public void recordFrame(){
		_myPositionRecording.recordFrame();
		for(CCKleChannelType myKey:_myRecordings.keySet()){
			CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get(myKey);
			myRecording.recordFrame();
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
	
	private CCAnimator.CCAnimationMode _myAnimationMode;
	private boolean _myFixUpdateTime = false;
	private double _myFixedUpdateTime = 0;
	
	private CCTransportController _myTransportController;
	private double _myTimelineTime = 0;
	

	
	public void startRecord(){
		_myBaseRate = 1;
		getSegments();
		for(CCKleChannelType myKey:_myRecordings.keySet()){
				
			CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)_myRecordings.get(myKey);
			if(!myRecording.export)continue;
	
			CCSequenceMapping<?> myMapping = myRecording._myMapping;
			_myBaseRate = CCMath.leastCommonMultiple(_myBaseRate, myMapping.frameRate() * myRecording.updateSteps);
		}
		for(CCSequence mySequence:_myRecordings.values()){
			CCSequenceChannelRecording myRecording = (CCSequenceChannelRecording)mySequence;
			myRecording.start();
		}
		_myPositionRecording.start();

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
			int mySteps2 = CCMath.ceil(_myTransportController.loopRange().length())  * _myBaseRate;
			CCLog.info(_mySequenceSteps + ":" + mySteps2);
			_myFadeSteps = _myFadeSeconds * _myBaseRate;
			_myAnimator.animationMode = CCAnimator.CCAnimationMode.AS_FAST_AS_POSSIBLE;
			_myAnimator.fixedUpdateTime = 1f / (_myBaseRate) * _myTransportController.speed();
			_myAnimator.fixUpdateTime = true;
			if(_myFadeSteps > 0)_myAnimator.fixedUpdateTime = 0;
		}else{
			_mySequenceSteps = (_mySeconds + 2 * _myFadeSeconds)  * _myBaseRate;
			_myFadeSteps = _myFadeSeconds * _myBaseRate;
			_myAnimator.animationMode = CCAnimator.CCAnimationMode.AS_FAST_AS_POSSIBLE;
			_myAnimator.fixedUpdateTime = 1f / (_myBaseRate);
			_myAnimator.fixUpdateTime = true;
			if(_myFadeSteps > 0)_myAnimator.fixedUpdateTime = 0;
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
		for(CCSequenceFormats myFormat:CCSequenceFormats.values()){
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
					CCSequenceFormats myFormat = CCSequenceFormats.valueOf(myExtension);
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
	
	private List<CCSequenceSegment> getSegments(){
		
		for(TrackController myTrack:_myGLAdapter.timeline().activeTimeline().trackController()){
			if(myTrack.property().path().toString().endsWith("/record/segment name")){

				CCLog.info(myTrack.property().path().toString());
				List<CCSequenceSegment> myResult = new ArrayList<>();
				for(ControlPoint myPoint:myTrack.trackData()){
					TimedEventPoint myEventPoint = (TimedEventPoint)myPoint;
					CCLog.info(myEventPoint.content().value() + ":" + myEventPoint.time() + ":" + myEventPoint.endTime());
					CCSequenceSegment mySegment = new CCSequenceSegment(
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
		
		SwingUtilities.invokeLater(new Runnable() {
			
			@Override
			public void run() {
				
				int myRetVal = _myFileChooser.show("");
				if (myRetVal == JFileChooser.APPROVE_OPTION) {
					try {
						Path myChoosenPath = _myFileChooser.path();
						String myExtension = _myFileChooser.extension();
						
						switch(myExtension){
						case "KLE_1":
							myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, "kle");
							Path myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
							
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
						case "KLE_2":
							myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, "kle");
							myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
							if(myPath == null)return;
				
							CCSequenceKLE2Container myKLEContainer = new CCSequenceKLE2Container();
							myKLEContainer.save(myPath, _myElements, _myRecordings, getSegments());
							break;
						default:
							CCSequenceFormats myFormat = CCSequenceFormats.valueOf(myExtension);
							if(!myFormat.isFolder()){
								myChoosenPath = CCNIOUtil.addExtension(myChoosenPath, myFormat.extension());
							}
							if(myFormat == CCSequenceFormats.NONE)return;
							if(!myFormat.savePosition()){
								if(_cRecordPositions){
									if(myFormat.isFolder()){
										myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
									}else{
										myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
									}
									if(myPath == null)return;

									_myPositionRecording.savePositions(myPath, myFormat);
								}else{
									for(CCKleChannelType myKey:_myRecordings.keySet()){
										myRecording = (CCSequenceChannelRecording)_myRecordings.get(myKey);
										if(!myRecording.export)continue;
										if(myFormat.isFolder()){
											myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
										}else{
											myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
										}
										
										myRecording.save(myPath, myFormat);
									}
								}
							}else{
								if(myFormat.isFolder()){
									myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
								}else{
									myPath = theRecordPath == null ? myChoosenPath : theRecordPath;
								}
								if(myPath == null)return;
								_myPositionRecording.save(myPath, myFormat);
							}
						}
					} catch (RuntimeException ex) {
						ex.printStackTrace();
					}
				}
			};
		});
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
	public void recordFromTimeline(CCTriggerProgress theProgress){
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
	
	public void seconds(int theSeconds){
		_mySeconds = theSeconds;
	}
	
	public void fadeSeconds(int theFadeSeconds){
		_myFadeSeconds = theFadeSeconds;
	}

}
