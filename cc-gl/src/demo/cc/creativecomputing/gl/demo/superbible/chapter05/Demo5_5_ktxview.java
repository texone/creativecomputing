package cc.creativecomputing.gl.demo.superbible.chapter05;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl4.GLBuffer;
import cc.creativecomputing.gl4.GLBuffer.GLBufferTarget;
import cc.creativecomputing.gl4.GLDrawMode;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLSampler.GLTextureMagFilter;
import cc.creativecomputing.gl4.GLSampler.GLTextureMinFilter;
import cc.creativecomputing.gl4.GLShaderObject;
import cc.creativecomputing.gl4.GLShaderObject.GLShaderType;
import cc.creativecomputing.gl4.GLShaderProgram;
import cc.creativecomputing.gl4.GLShaderProgram.GLUniformInfo;
import cc.creativecomputing.gl4.GLUniform;
import cc.creativecomputing.gl4.GLUniformParameters;
import cc.creativecomputing.gl4.GLVertexArray;
import cc.creativecomputing.gl4.texture.GLKTXLoader;
import cc.creativecomputing.gl4.texture.GLTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMath;

public class Demo5_5_ktxview extends CCGL4Adapter{
	
	private GLShaderProgram _myRenderProgram;
	
	private GLVertexArray _myVAO;
	
	private GLUniformParameters _myParameters;
	
	@GLUniform(name = "exposure")
	private double _myExposure = 0;
	@GLUniform(name = "s")
	private GLTexture2D _myTexture;
	
	@Override
	public void init(GLGraphics g) {
		_myTexture = (GLTexture2D)GLKTXLoader.load(CCNIOUtil.classPath(this, "tex/Tree.ktx"));
		_myTexture.minFilter(GLTextureMinFilter.LINEAR);
		_myTexture.magFilter(GLTextureMagFilter.LINEAR);
		
		_myRenderProgram = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this,"ktxview_vert.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this,"ktxview_frag.glsl"))
		);
		_myParameters = _myRenderProgram.createUniformParameters(this);
		
		for(GLUniformInfo myInfo:_myRenderProgram.uniforms()){
			CCLog.info(myInfo);
		}
		
		GLBuffer myVertices = new GLBuffer(GLBufferTarget.ARRAY);
	    myVertices.bind();
	    myVertices.datavf(
	    	-1f, -1f, 0f,
	    	-1f,  1f, 0f, 
	    	 1f, -1f, 0f,
	    	 
	    	-1f,  1f, 0f, 
	    	 1f,  1f, 0f, 
	    	 1f, -1f, 0f
	    );
	    
	    GLBuffer myTexCoords = new GLBuffer(GLBufferTarget.ARRAY);
	    myTexCoords.bind();
	    myTexCoords.datavf(
	    	0f, 0f,
	    	0f, 1f, 
	    	1f, 0f,
	    	 
	    	0f, 1f, 
	    	1f, 1f, 
	    	1f, 0f
	    );
	    
	    int myVertexPositionLocation = _myRenderProgram.attribLocation("position");
	    int myTexCoordLocation = _myRenderProgram.attribLocation("texcoord");
	    
	    // Set up the vertex attributes
	    _myVAO = new GLVertexArray();
	    _myVAO.bind();
	    
	    myVertices.bind();
	    _myVAO.attributes(myVertexPositionLocation, 3);
	    _myVAO.enableVertexAttribArray(myVertexPositionLocation);
	    
	    myTexCoords.bind();
	    _myVAO.attributes(myTexCoordLocation, 2);
	    _myVAO.enableVertexAttribArray(myTexCoordLocation);
	    
	    _myVAO.unbind();
	    
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myExposure = CCMath.sin(theAnimator.time()) * 16f + 16f;
	}
	
	@Override
	public void reshape(GLGraphics g) {
        g.viewport(0, 0, g.width(), g.height());
	}
	
	@Override
	public void display(GLGraphics g) {

        g.clearBufferfv(GLColorBuffer.COLOR, 0.0f, 0.25f, 0.0f, 1.0f);
        
        _myRenderProgram.use();
        _myParameters.apply(_myRenderProgram);
        
        _myVAO.bind();
        _myVAO.drawArrays(GLDrawMode.TRIANGLES, 0, 6);
        
	}

	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo5_5_ktxview());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		myAppManager.glcontext().title = "OpenGL SuperBible - KTX Viewer";
		myAppManager.glcontext().size(400, 400);
		myAppManager.start();
	}
}
