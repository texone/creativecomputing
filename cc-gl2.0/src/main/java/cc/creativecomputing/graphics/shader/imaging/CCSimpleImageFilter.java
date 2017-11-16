package cc.creativecomputing.graphics.shader.imaging;

import java.nio.file.Path;

import cc.creativecomputing.core.CCProperty;
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
		_myShader = new CCGLProgram(null, theFragmentShader);
	}

	@Override
	public CCTexture2D output() {
		return _myOutput.attachment(0);
	}

	@Override
	public void display(CCGraphics g) {
		_myShader.start();
		_myOutput.draw(g);
		_myShader.end();
	}

}
