package cc.creativecomputing.opencv.filtering;

import java.nio.file.Path;

import org.bytedeco.javacpp.opencv_core.Mat;

import cc.creativecomputing.core.CCProperty;
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
	
	public CCCVShaderFilter(Path theVertexShader, Path theFragmentShader) {
		_cShader = new CCGLProgram(theVertexShader, theFragmentShader);
		_myTexture = new CCCVTexture();
		_myCurrentMat = null;
	}
	

	@Override
	public void implementation(Mat theSource) {
		_myCurrentMat = theSource.clone();
		if(_myShaderBuffer == null)return;
		theSource.getByteBuffer().rewind();
		theSource.getByteBuffer().put(_myShaderBuffer.attachment(0).getTexImage());
		theSource.getByteBuffer().rewind();
	}
	
	@Override
	public void preDisplay(CCGraphics g) {
		if(_myCurrentMat == null)return;
			
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
		
		
	}
	
	public CCTexture2D inputTexture() {
		return _myTexture;
	}
	
	public CCTexture2D outputTexture() {
		return _myShaderBuffer.attachment(0);
	}

}
