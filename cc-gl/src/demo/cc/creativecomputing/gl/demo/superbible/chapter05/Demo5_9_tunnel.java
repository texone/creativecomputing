package cc.creativecomputing.gl.demo.superbible.chapter05;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
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
import cc.creativecomputing.gl4.GLVertexArray;
import cc.creativecomputing.gl4.texture.GLKTXLoader;
import cc.creativecomputing.gl4.texture.GLTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCMatrix4x4;

public class Demo5_9_tunnel extends CCGL4Adapter{
	
	private float aspect;
	
	private GLShaderProgram _myRenderProgram;
	
	private GLTexture2D _myWallTexture;
	private GLTexture2D _myCeilingTexture;
	private GLTexture2D _myFloorTexture;
	
	private GLVertexArray _myVAO;
	
	private CCMatrix4x4 _myProjectionMatrix;
	
	@Override
	public void init(GLGraphics g) {

	    _myProjectionMatrix = new CCMatrix4x4();
	    
		_myRenderProgram = new GLShaderProgram(
			new GLShaderObject(GLShaderType.VERTEX, CCNIOUtil.classPath(this,"tunnel_vert.glsl")),
			new GLShaderObject(GLShaderType.FRAGMENT, CCNIOUtil.classPath(this,"tunnel_frag.glsl"))
		);
		_myRenderProgram.use();
		
		_myWallTexture = (GLTexture2D)GLKTXLoader.load(CCNIOUtil.classPath(this, "tex/brick.ktx"));
		_myWallTexture.minFilter(GLTextureMinFilter.LINEAR_MIPMAP_LINEAR);
		_myWallTexture.magFilter(GLTextureMagFilter.LINEAR);
		
		_myCeilingTexture = (GLTexture2D)GLKTXLoader.load(CCNIOUtil.classPath(this, "tex/ceiling.ktx"));
		_myCeilingTexture.minFilter(GLTextureMinFilter.LINEAR_MIPMAP_LINEAR);
		_myCeilingTexture.magFilter(GLTextureMagFilter.LINEAR);
		
		_myFloorTexture = (GLTexture2D)GLKTXLoader.load(CCNIOUtil.classPath(this, "tex/floor.ktx"));
		_myFloorTexture.minFilter(GLTextureMinFilter.LINEAR_MIPMAP_LINEAR);
		_myFloorTexture.magFilter(GLTextureMagFilter.LINEAR);
	    
	    
		GLBuffer myVertices = new GLBuffer(GLBufferTarget.ARRAY);
	    myVertices.bind();
	    myVertices.datavf(
	    	-1f, -1f, -1f,
	    	 1f, -1f, -1f, 
	    	 1f, -1f,  1f

//	    	-1f, -1f, -1f,
//	    	 1f, -1f,  1f,
//	    	-1f, -1f,  1f,
//
//	    	-1f,  1f, -1f,
//	    	 1f,  1f, -1f, 
//	    	 1f,  1f,  1f,
//
//	    	-1f,  1f, -1f,
//	    	 1f,  1f,  1f,
//	    	-1f,  1f,  1f
	    );
	    
	    GLBuffer myTexCoords = new GLBuffer(GLBufferTarget.ARRAY);
	    myTexCoords.bind();
	    myTexCoords.datavf(
	    	0f, 0f,
	    	0f, 1f, 
	    	1f, 0f
	    	 
//	    	0f, 1f, 
//	    	1f, 1f, 
//	    	1f, 0f,
//	    	
//	    	0f, 0f,
//	    	0f, 1f, 
//	    	1f, 0f,
//	    	 
//	    	0f, 1f, 
//	    	1f, 1f, 
//	    	1f, 0f
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
	
	private float _myTime;
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myTime += theAnimator.deltaTime() * 0.1f;
	}
	
	@Override
	public void reshape(GLGraphics g) {
		g.viewport(0, 0, g.width(), g.height());
		_myProjectionMatrix = CCMatrix4x4.createPerspective(60.0f,g.aspectRatio(), 0.1f, 100.0f);
	}
	
	@Override
	public void display(GLGraphics g) {
       

		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);
//		g.cullFace();
		g.depthTest();

		_myRenderProgram.use();
		_myRenderProgram.uniform1f("offset", _myTime * 0.003f);
		_myRenderProgram.uniform1i("tex", 0);
		
		GLTexture2D textures[] = { _myWallTexture, _myFloorTexture, _myWallTexture, _myCeilingTexture };

		 CCMatrix4x4 myModelMatrix = new CCMatrix4x4();
//		myModelMatrix.applyRotationZ(i * CCMath.HALF_PI);
//		myModelMatrix.applyTranslationPost(-0.5f, 0.0f, -1.0f);
//		myModelMatrix.applyRotationY(CCMath.HALF_PI);
		myModelMatrix.scale(0.05f, 0.05f, 0.15f, myModelMatrix);
		_myRenderProgram.uniformMatrix4f("mvp", myModelMatrix.multiply( _myProjectionMatrix));//
		 for (int i = 0; i < 4; i++){
			 
//	            vmath::mat4 mv_matrix = vmath::rotate(90.0f * (float)i, vmath::vec3(0.0f, 0.0f, 1.0f)) *
//	                                    vmath::translate() *
//	                                    vmath::rotate(90.0f, 0.0f, 1.0f, 0.0f) *
//	                                    vmath::scale(30.0f, 1.0f, 1.0f);
//	            vmath::mat4 mvp = proj_matrix * mv_matrix;
	            

//			g.activeTexture(0);
//			textures[i].bind();
			
		 }

		 _myVAO.bind();
			_myVAO.drawArrays(GLDrawMode.TRIANGLES, 0, 3);
	}

	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new Demo5_9_tunnel());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		
		myAppManager.glcontext().title = "OpenGL SuperBible - Tunnel";
		myAppManager.start();
	}
}
