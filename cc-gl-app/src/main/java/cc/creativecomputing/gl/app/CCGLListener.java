package cc.creativecomputing.gl.app;


public interface CCGLListener <GLGraphicsType extends CCGLGraphics>{
	public void reshape(GLGraphicsType theContext);
	
	public void init(GLGraphicsType theContext);
	
	public void dispose(GLGraphicsType theContext);
	
	public void display(GLGraphicsType theContext);
}
