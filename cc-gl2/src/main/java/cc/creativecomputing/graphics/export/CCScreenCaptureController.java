package cc.creativecomputing.graphics.export;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_PACK_ALIGNMENT;
import static org.lwjgl.opengl.GL11.GL_PACK_ROW_LENGTH;
import static org.lwjgl.opengl.GL11.GL_PACK_SKIP_PIXELS;
import static org.lwjgl.opengl.GL11.GL_PACK_SKIP_ROWS;
import static org.lwjgl.opengl.GL11.GL_PACK_SWAP_BYTES;
import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_UNSIGNED_BYTE;
import static org.lwjgl.opengl.GL11.glGetInteger;
import static org.lwjgl.opengl.GL11.glPixelStorei;
import static org.lwjgl.opengl.GL11.glReadPixels;
import static org.lwjgl.stb.STBImageWrite.stbi_write_bmp;
import static org.lwjgl.stb.STBImageWrite.stbi_write_hdr;
import static org.lwjgl.stb.STBImageWrite.stbi_write_jpg;
import static org.lwjgl.stb.STBImageWrite.stbi_write_png;
import static org.lwjgl.stb.STBImageWrite.stbi_write_tga;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.file.Path;

import org.lwjgl.BufferUtils;

import cc.creativecomputing.control.handles.CCTriggerProgress;
import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.gl.app.CCGLApp;
import cc.creativecomputing.gl.app.CCGLKey;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.image.CCImageIO.CCImageFormats;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.time.CCDate;

public class CCScreenCaptureController{
	
	static class PixelStorageModes {
		int packAlignment;
		int packRowLength;
		int packSkipRows;
		int packSkipPixels;
		int packSwapBytes;

		void save() {
			packAlignment = glGetInteger(GL_PACK_ALIGNMENT);
			packRowLength = glGetInteger(GL_PACK_ROW_LENGTH);
			packSkipRows = glGetInteger(GL_PACK_SKIP_ROWS);
			packSkipPixels = glGetInteger(GL_PACK_SKIP_PIXELS);
			packSwapBytes = glGetInteger(GL_PACK_SWAP_BYTES);

			glPixelStorei(GL_PACK_ALIGNMENT, 1);
			glPixelStorei(GL_PACK_ROW_LENGTH, 0);
			glPixelStorei(GL_PACK_SKIP_ROWS, 0);
			glPixelStorei(GL_PACK_SKIP_PIXELS, 0);
			glPixelStorei(GL_PACK_SWAP_BYTES, 0);
		}

		void restore() {
			glPixelStorei(GL_PACK_ALIGNMENT, packAlignment);
			glPixelStorei(GL_PACK_ROW_LENGTH, packRowLength);
			glPixelStorei(GL_PACK_SKIP_ROWS, packSkipRows);
			glPixelStorei(GL_PACK_SKIP_PIXELS, packSkipPixels);
			glPixelStorei(GL_PACK_SWAP_BYTES, packSwapBytes);
		}
	}
	
	public CCEventManager<CCEvent<Object>> startEvents = new CCEventManager<>();
	public CCEventManager<CCEvent<Object>> endEvents = new CCEventManager<>();

	private int _mySeconds = 100;
	
	
	@CCProperty(name = "format")
	private CCImageFormats _myFormat = CCImageFormats.PNG;
	
	@CCProperty(name = "capture rate", min = 1, max = 60)
	private int _myCaptureRate;
	@CCProperty(name = "quality", min = 0, max = 1)
	private double _cQuality = 1;
	
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
	
	private CCGLApp _myApp;
	
	public CCScreenCaptureController(CCGLApp theApp){
		_myApp = theApp;
		_myApp.keyReleaseEvents.add(e ->  {
			if(!(e.isAltDown() && e.key == CCGLKey.KEY_S))return;
			
			_myRecordPath = CCNIOUtil.appPath("export/screencaps/"+new CCDate().toString("yyyy-MM-dd'T'HH-mm-ss-SSS")+".png");
			if(_myRecordPath == null)return;
			_myRecordTimeline = false;
			_myProgress = null;
			startRecord();
			_mySequenceSteps = 1;
		});
		_myApp.updateEvents.add(t ->{
			
		});
		_myApp.drawEvents.add(g -> display(g));
		
		_myCaptureRate = 30;
//		_myAnimator = theAnimator;
//		_myAnimator.listener().add(this);
	}
	
	private int _myStep = 0;
	private int _mySequenceSteps = 0;
	private boolean _myIsRecording = false;
	
	private CCTriggerProgress _myProgress;
	
	private int _myThreads = 0;
	
	public void display(CCGraphics g) {
		if(_myAdaptSize){
			_cX = 0;
			_cY = 0;
			_cWidth = g.width();
			_cHeight = g.height();
		}
		
		if(!_myIsRecording){
			if(_cDrawCaptureBounds){
				g.beginOrtho();
				g.beginShape(CCDrawMode.LINE_LOOP);
				g.vertex(_cX, _cY);
				g.vertex(_cX + _cWidth, _cY);
				g.vertex(_cX + _cWidth, _cY + _cHeight);
				g.vertex(_cX, _cY + _cHeight);
				g.endShape();
				g.endOrtho();
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
		PixelStorageModes psm = new PixelStorageModes();
		psm.save();
		switch(fileSuffix) {
		case "png":
			ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 3);
			glReadPixels(x, y, width, height, GL_RGB, GL_UNSIGNED_BYTE,buffer);
			stbi_write_png(_myRecordPath.toString(), width, height, 3, buffer, 4);//int stride_in_bytes);
			break;
		case "bmp":
			buffer = BufferUtils.createByteBuffer(width * height * 3);
			glReadPixels(x, y, width, height, GL_RGB, GL_UNSIGNED_BYTE,buffer);
			stbi_write_bmp(_myRecordPath.toString(), width, height, 3, buffer);
	     	break;
		case "tga":
			buffer = BufferUtils.createByteBuffer(width * height * 3);
			glReadPixels(x, y, width, height, GL_RGB, GL_UNSIGNED_BYTE,buffer);
			stbi_write_tga(_myRecordPath.toString(), width, height, 3, buffer);
			break;
		case "jpg":
			FloatBuffer fBuffer = BufferUtils.createFloatBuffer(width * height * 3);
			glReadPixels(x, y, width, height, GL_RGB, GL_FLOAT, fBuffer);
			stbi_write_jpg(_myRecordPath.toString(), width, height, 3, fBuffer, (int)(_cQuality * 100));
	     	break;
		case "hdr":
			fBuffer = BufferUtils.createFloatBuffer(width * height * 3);
			glReadPixels(x, y, width, height, GL_RGB, GL_FLOAT, fBuffer);
			stbi_write_hdr(_myRecordPath.toString(), width, height, 3, fBuffer);
	    	break;
	    default:
			psm.restore();
			throw new CCScreenCaptureException("Not able to perform screen capture because of missing file extension.");
		}
		psm.restore();
		


//		new Thread(() ->  {
//			_myThreads++;
//			
//			// Must flip BufferedImage vertically for correct results
//			CCScreenCapture.flipImageVertically(image);
//			
//			try{
//				_myImageFormat.write(myRecordPath, image, _cQuality);
//				if (!_myImageFormat.write(myRecordPath, image, _cQuality)) {
//					throw new CCScreenCaptureException("Unsupported file format " + fileSuffix);
//				}
//			}catch(Exception e){
//				throw new CCScreenCaptureException(e);
//			}
//			_myThreads--;
//			
//		}).start();
		
		CCLog.info(_myThreads);
		_myStep++;
		_myApp.t.fixedUpdateTime = 1f / (_myCaptureRate);
		if(_myRecordTimeline){
			_myTimelineTime += _myApp.t.deltaTime();
			//_myTransportController.time(_myTimelineTime);
		}
		double myProgress = (double)_myStep / (double)_mySequenceSteps;
		if(_myProgress != null)_myProgress.progress(myProgress);
		if(myProgress >= 1)end();
	}
	
//	private CCAnimationMode _myAnimationMode;
	private boolean _myFixUpdateTime = false;
	private double _myFixedUpdateTime = 0;
	
	//private CCTransportController _myTransportController;
	private double _myTimelineTime = 0;
	
	public void startRecord(){
//		_myAnimationMode = _myApp.t.animationMode;
		_myFixedUpdateTime = _myApp.t.fixedUpdateTime;
		_myFixUpdateTime = _myApp.t.fixUpdateTime;
		
		_myStep = 0;
//		_myApp.t.animationMode = CCAnimationMode.AS_FAST_AS_POSSIBLE;
		_myApp.t.fixedUpdateTime = 1d / (_myCaptureRate);
		_myApp.t.fixUpdateTime = true;
	
		_myIsRecording = true;
		startEvents.event();
	}
	
	private Path _myRecordPath = null;
	
	public void end(){
		endEvents.event();
		_myApp.t.fixedUpdateTime = _myFixedUpdateTime;
		_myApp.t.fixUpdateTime = _myFixUpdateTime;
//		_myApp.t.animationMode = _myAnimationMode;
		
		if(_myProgress != null)_myProgress.end();
		_myIsRecording = false;
	}
	
	private boolean _myRecordTimeline = false;
	
	@CCProperty(name = "record sequence")
	public void recordAndSaveSequence(CCTriggerProgress theProgress) {
//		SwingUtilities.invokeLater(new Runnable() {
//
//			@Override
//			public void run() {
				CCNIOUtil.selectFolder().ifPresent(p -> {
					_myRecordPath = p;
					_myRecordTimeline = false;
					_myProgress = theProgress;
					_myProgress.start();

					_mySequenceSteps = _mySeconds * _myCaptureRate;

					startRecord();
				});
//			}
//		});
	}
	
	@CCProperty(name = "record frame")
	public void recordFrame(){
//		SwingUtilities.invokeLater(new Runnable() {
//			
//			@Override
//			public void run() {
				CCNIOUtil.selectOutput().ifPresent(p->{
				_myRecordPath = p;
				_myRecordTimeline = false;
				_myProgress = null;
				startRecord();
				_mySequenceSteps = 1;
			});
//			}
//		});
	}
	
//	@CCProperty(name = "record timeline loop")
//	public void recordFromTimeline(CCTriggerProgress theProgress){
//		SwingUtilities.invokeLater(new Runnable() {
//			
//			@Override
//			public void run() {
//				_myRecordPath = CCNIOUtil.selectFolder();
//				if(_myRecordPath == null)return;
//				
//				_myTransportController = _myGLAdapter.timeline().activeTimeline().transportController();
//				_myTransportController.stop();
//				_myTimelineTime = _myTransportController.loopStart();
//				_myTransportController.time(_myTransportController.loopStart());
//				_mySequenceSteps = CCMath.floor(_myTransportController.loopRange().length()  * _myCaptureRate);
//				
//				_myRecordTimeline = true;
//				_myProgress = theProgress;
//				_myProgress.start();
//				startRecord();}
//		});
//	}
	
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
