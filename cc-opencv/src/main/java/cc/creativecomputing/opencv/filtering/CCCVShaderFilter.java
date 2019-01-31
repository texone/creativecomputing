package cc.creativecomputing.opencv.filtering;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

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
	
	private static class CCCVTextureAddon{
		CCTexture2D texture;
		int channel;
		String uniform;
	}
	
	private List<CCCVTextureAddon> _myTextures = new ArrayList<>();
	
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
	
	public void addTexture(CCTexture2D theTexture, int theChannel, String theUniform) {
		if(theTexture == null)return;
		CCCVTextureAddon myCVTex = new CCCVTextureAddon();
		myCVTex.texture = theTexture;
		myCVTex.channel = theChannel;
		myCVTex.uniform = theUniform;
		_myTextures.add(myCVTex);
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
		for(CCCVTextureAddon myAddon:_myTextures) {
			g.texture(myAddon.channel, myAddon.texture);
		}
		_cShader.start();
		_cShader.uniform1i("texture", 0);
		for(CCCVTextureAddon myAddon:_myTextures) {
			_cShader.uniform1i(myAddon.uniform, myAddon.channel);
		}
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
