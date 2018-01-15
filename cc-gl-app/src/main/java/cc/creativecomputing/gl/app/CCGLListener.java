package cc.creativecomputing.gl.app;


public interface CCGLListener <GLGraphicsType extends CCGLGraphics<?>>{
	void reshape(GLGraphicsType theContext);
	
	void init(GLGraphicsType theContext);
	
	void dispose(GLGraphicsType theContext);
	
	void display(GLGraphicsType theContext);
}
