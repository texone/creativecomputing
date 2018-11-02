package cc.creativecomputing.graphics.shader;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

public class CCShaderBufferDebugger {

	private CCTexture2D _myBuffer;
	
	@CCProperty(name = "shader")
	private CCGLProgram _myDebugProgram;
	@CCProperty(name = "active")
	private boolean _cActive = true;
	
	
	@CCProperty(name = "X")
	private double _cX = 0;
	@CCProperty(name = "Y")
	private double _cY = 0;
	
	public CCShaderBufferDebugger(CCTexture2D theBuffer) {
		_myBuffer = theBuffer;
		
		_myDebugProgram = new CCGLProgram(CCNIOUtil.classPath(this, "buffer_debug_vertex.glsl"), CCNIOUtil.classPath(this, "buffer_debug_fragment.glsl"));
	}
	
	public CCShaderBufferDebugger(CCShaderBuffer theBuffer) {
		this(theBuffer.attachment(0));
	}
	
	public void display(CCGraphics g) {
		if(!_cActive)return;
		g.pushMatrix();
		g.ortho();
		g.translate(_cX,_cY);
		g.texture(0, _myBuffer);
		_myDebugProgram.start();
		_myDebugProgram.uniform1i("buffer", 0);
		g.beginShape(CCDrawMode.QUADS);
		g.vertex(0,0);
		g.vertex(_myBuffer.width(),0);
		g.vertex(_myBuffer.width(),_myBuffer.height());
		g.vertex(0,_myBuffer.height());
		g.endShape();
		_myDebugProgram.end();
		g.noTexture();
		g.popMatrix();
	}
}
