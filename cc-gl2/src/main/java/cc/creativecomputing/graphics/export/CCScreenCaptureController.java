package cc.creativecomputing.graphics.export;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.nio.ByteBuffer;
import java.nio.file.Path;

import javax.swing.SwingUtilities;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.gl.app.events.CCKeyAdapter;
import cc.creativecomputing.gl.app.events.CCKeyEvent;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.export.CCScreenCapture.PixelStorageModes;
import cc.creativecomputing.image.CCImageIO.CCImageFormats;
import cc.creativecomputing.image.format.CCImageIOFormat;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class CCScreenCaptureController extends CCGL2Adapter{
	
	public interface CCSequenceRecorderListener{
		void start();
		
		void end();
	}
	
	private CCListenerManager<CCSequenceRecorderListener> _myRecordListeners = CCListenerManager.create(CCSequenceRecorderListener.class);

	private int _mySeconds = 100;
	
	
	@CCProperty(name = "format")
	private CCImageFormats _myFormat = CCImageFormats.PNG;
	
	@CCProperty(name = "capture rate", min = 1, max = 60)
	private int _myCaptureRate;
	@CCProperty(name = "quality", min = 0, max = 1)
	private double _cQuality = 1;
	
	@CCProperty(name = "alpha")
	private boolean _cAlpha = false;
	
	@CCProperty(name = "prepend")
	private String _cPrepend = "frame_";
	
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
	
	@CCProperty(name = "record key")
	private boolean _cRecordKey = true;
	
	
	private final CCAnimator _myAnimator;
	
	private CCGL2Adapter _myGLAdapter;
	
	public CCScreenCaptureController(CCGL2Adapter theGLAdapter, CCAnimator theAnimator){
		_myGLAdapter = theGLAdapter;
		_myGLAdapter.glListener().add(this);
		_myGLAdapter.keyListener().add(new CCKeyAdapter() {
			@Override
			public void keyReleased(CCKeyEvent theKeyEvent) {
//				recordFrame();
			}
		});
		_myCaptureRate = 1;
		_myAnimator = theAnimator;
		_myAnimator.listener().add(this);
	}
	
	public CCScreenCaptureController(CCGL2Adapter theGLAdapter){
		this(theGLAdapter, theGLAdapter.animator());
	}
	
	private int _myStep = 0;
	private int _mySequenceSteps = 0;
	private boolean _myIsRecording = false;
	
	private CCTriggerProgress _myProgress;
	
	private int _myThreads = 0;
	

	private CCImageIOFormat _myImageFormat = new CCImageIOFormat();
	
	@Override
	public void display(CCGraphics g) {
		if(_myAdaptSize){
			_cX = 0;
			_cY = 0;
			_cWidth = g.width();
			_cHeight = g.height();
		}
		
		
		
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
		
		if(CCNIOUtil.fileExtension(_myRecordPath) == null)CCNIOUtil.addExtension(_myRecordPath, _myFormat.fileExtension);
		final Path myRecordPath;
		if(_mySequenceSteps > 1){
			myRecordPath = _myRecordPath.resolve(_cPrepend + "_" + CCFormatUtil.nf(_myStep, 5) + "." + _myFormat.fileExtension);
		}else {
			myRecordPath = _myRecordPath;
		}
		
		int x = CCMath.clamp(_cX, 0, g.width());
		int y = CCMath.clamp(_cY, 0, g.height());
		int width = _cWidth == 0 ? g.width() : _cWidth;
		int height = _cHeight == 0 ? g.height() : _cHeight;
		width = CCMath.min(width, g.width() - x);
		height = CCMath.min(height, g.height() - y);
		
		
		String fileSuffix = CCNIOUtil.fileExtension(myRecordPath);
		
		if(fileSuffix == null){
			throw new CCScreenCaptureException("Not able to perform screen capture because of missing file extension.");
		}
		
		if(fileSuffix.equals("tga")){
			CCScreenCapture.writeToTargaFile(myRecordPath, x,y,width, height,_cAlpha);
			return;
		}
		
		boolean alpha = _cAlpha;
		if (alpha && (fileSuffix.equals("jpg") || fileSuffix.equals("jpeg"))) {
			// JPEGs can't deal properly with alpha channels
			alpha = false;
		}
		
		int bufImgType = (alpha ? BufferedImage.TYPE_4BYTE_ABGR : BufferedImage.TYPE_3BYTE_BGR);
		int readbackType = (alpha ? GL2.GL_ABGR_EXT : GL2.GL_BGR);

		if (alpha) {
			CCScreenCapture.checkExtABGR();
		}

		// Allocate necessary storage
		BufferedImage image = new BufferedImage(width, height, bufImgType);

		GL gl = CCGraphics.currentGL();
			
		PixelStorageModes psm = new PixelStorageModes();
		psm.save(gl);
		gl.glReadPixels(x, y, width, height, readbackType, GL.GL_UNSIGNED_BYTE,ByteBuffer.wrap(((DataBufferByte) image.getRaster().getDataBuffer()).getData()));
		psm.restore(gl);

		new Thread(() ->  {
			_myThreads++;
			
			// Must flip BufferedImage vertically for correct results
			CCScreenCapture.flipImageVertically(image);
			
			try{
				_myImageFormat.write(myRecordPath, image, _cQuality);
				if (!_myImageFormat.write(myRecordPath, image, _cQuality)) {
					throw new CCScreenCaptureException("Unsupported file format " + fileSuffix);
				}
			}catch(Exception e){
				throw new CCScreenCaptureException(e);
			}
			_myThreads--;
			
		}).start();
		
		CCLog.info(_myThreads);
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
	
	private CCTransportController _myTransportController;
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
