package cc.creativecomputing.graphics.shader.imaging;

import java.nio.file.Path;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture.CCTextureTarget;
import cc.creativecomputing.graphics.texture.CCTexture2D;

public class CCSimpleImageFilter extends CCImageFilter{

	private CCShaderBuffer _myOutput;
	
	@CCProperty(name = "shader")
	private CCGLProgram _myShader;
	
	public CCSimpleImageFilter(int theWidth, int theHeight, Path theFragmentShader) {
		super();
		_myOutput = new CCShaderBuffer(theWidth, theHeight, CCTextureTarget.TEXTURE_2D);
		CCLog.info(theFragmentShader);
		_myShader = new CCGLProgram(null, theFragmentShader);
	}

	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}

	@Override
	public void display(CCGraphics g) {
		boolean myUseTexture = false;
		for(int i = 0; i < _myInputChannel.length;i++){
			if(_myInputChannel[i] != null){
				g.texture(i, _myInputChannel[i]);
				myUseTexture = true;
			}
		}
		_myShader.start();
		_myShader.uniform2f("iResolution", _myOutput.width(), _myOutput.height());
		for(int i = 0; i < _myInputChannel.length;i++){
			if(_myInputChannel[i] != null){
				_myShader.uniform1i("channel"+i, i);
			}
		}
		_myOutput.draw(g);
		_myShader.end();
		
		if(myUseTexture)g.noTexture();
	}

}
