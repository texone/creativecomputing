package cc.creativecomputing.graphics.scene;

import cc.creativecomputing.gl4.GLGraphics;

public class CCRenderer extends CCNode{
	
	private CCCamera _myCamera;
	
	private GLGraphics _myGraphics;
	
	public CCRenderer(CCCamera theCamera, GLGraphics theGraphics){
		_myCamera = theCamera;
		_myGraphics = theGraphics;
	}
	
	public CCCamera camera(){
		return _myCamera;
	}

	public GLGraphics graphics(){
		return _myGraphics;
	}
}
