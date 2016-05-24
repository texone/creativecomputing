package cc.creativecomputing.demo;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.gl.app.CCAbstractGLContext.CCPixelScale;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix2;
import cc.creativecomputing.math.interpolate.CCInterpolators;

public class CCMatrix2InterpolationDemo extends CCGL2Adapter{
	
	private CCMatrix2 _myMatrix;

	@CCProperty(name = "camera controller")
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "scale", min = 0, max = 500)
	private double _cScale = 0;
	
	@CCProperty(name = "use cubic")
	private CCInterpolators _cInterpolator = CCInterpolators.LINEAR;
	
	@Override
	public void init(CCGraphics g) {
		
		_myMatrix = new CCMatrix2(10, 10, 1);

		_myCameraController = new CCCameraController(this, g, 100);
		buildMap();
	}
	
	@CCProperty(name = "build map")
	private void buildMap(){
		_myMatrix.randomize(0, 1);
	}
	
	@Override
	public void update(CCAnimator theAnimator) {
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();

		_myCameraController.camera().draw(g);
		
		g.beginShape(CCDrawMode.POINTS);
		for(int x = 0; x < 500;x++){
			double myMatrixX = CCMath.map(x, 0, 499, 0, _myMatrix.columns() - 1);
			double myVertexX = CCMath.map(x, 0, 499, -250, 250);
			for(int y = 0; y < 500;y++){
				double myMatrixY = CCMath.map(y, 0, 499, 0, _myMatrix.rows() - 1);
				double myVertexY = CCMath.map(y, 0, 499, -250, 250);
				double myZ = _myMatrix.get(_cInterpolator, myMatrixX, myMatrixY)[0] * _cScale;
				g.vertex(myVertexX, myVertexY, myZ);
			}
		}
		g.endShape();
	}
	
	public static void main(String[] args) {
		
		
		CCMatrix2InterpolationDemo demo = new CCMatrix2InterpolationDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1000, 500);
		myAppManager.glcontext().pixelScale = CCPixelScale.IDENTITY;
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
