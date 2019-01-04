package cc.creativecomputing.opencv;

import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_videoio.VideoWriter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.io.CCNIOUtil;

public class CCVideoWriter {
	
	private VideoWriter _myWriter;
	
	@CCProperty(name = "file")
	private String _myFileName = "capture.avi";

	public CCVideoWriter() {
		_myWriter = new VideoWriter();
		;
	}
	
	private boolean _myIsActive = false;
	
	@CCProperty(name = "start")
	public void start() {
		_myWriter.open(CCNIOUtil.dataPath(_myFileName).toString(), VideoWriter.fourcc((byte)'M', (byte)'J', (byte)'P', (byte)'G'), 25, new Size(1280, 720), true);
	}
	
	public void write(Mat theMat) {
		if(!_myIsActive)return;
		_myWriter.write(theMat);
	}
	
	@CCProperty(name = "end")
	public void end() {
		_myWriter.release();
	}
}
