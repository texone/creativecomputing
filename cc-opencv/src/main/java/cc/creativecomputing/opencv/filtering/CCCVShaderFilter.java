package cc.creativecomputing.opencv.filtering;

import java.nio.file.Path;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.opencv.CCCVTexture;
import cc.creativecomputing.opencv.CCImageProcessor;

public class CCCVShaderFilter extends CCImageProcessor{
	
	@CCProperty(name = "shader")
	private CCGLProgram _cShader;
	
	private CCShaderBuffer _myShaderBuffer;
	private CCCVTexture _myTexture;
	private Mat _myCurrentMat;
	private Mat _myCurrentOutput;
	
	public CCCVShaderFilter(Path theVertexShader, Path theFragmentShader) {
		_cShader = new CCGLProgram(theVertexShader, theFragmentShader);
		_myTexture = new CCCVTexture();
		_myCurrentMat = null;
	}
	

	@Override
	public Mat implementation(Mat theSource) {
		_myCurrentMat = theSource.clone();
		if(_myShaderBuffer == null)return theSource;
		if(_myCurrentOutput == null)return theSource;
		
		return _myCurrentOutput;
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		if(_myCurrentMat == null)return;
		if(_myCurrentOutput == null) {
			_myCurrentOutput = _myCurrentMat.clone();
		}
//		_myCurrentOutput = _myCurrentMat.clone();
		_myTexture.image(_myCurrentMat);
		
		boolean myAllocateData = _myShaderBuffer == null || _myCurrentMat.cols() != _myShaderBuffer.width() || _myCurrentMat.rows() != _myShaderBuffer.height();
		
		if(myAllocateData) {
			_myShaderBuffer = new CCShaderBuffer(_myCurrentMat.cols(), _myCurrentMat.rows());
		}
		
		g.texture(_myTexture);
		_cShader.start();
		_cShader.uniform1i("texture", 0);
		_cShader.uniform2f("resolution",_myTexture.width(), _myTexture.height());
		_myShaderBuffer.draw(g);
		_cShader.end();
		g.noTexture();
		
//		_myShaderBuffer.attachment(0).getTexImage();
try {
		_myCurrentOutput.getByteBuffer().rewind();
		_myCurrentOutput.getByteBuffer().put(_myShaderBuffer.attachment(0).getTexImage());
		_myCurrentOutput.getByteBuffer().rewind();
}catch(Exception e) {
//	e.printStackTrace();
}
	}
	
	public CCTexture2D inputTexture() {
		return _myTexture;
	}
	
	public CCTexture2D outputTexture() {
		return _myShaderBuffer.attachment(0);
	}

}
