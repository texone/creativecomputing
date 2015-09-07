package cc.creativecomputing.graphics.demo.shape;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.app.modules.CCAnimator.CCAnimationMode;
import cc.creativecomputing.gl.app.CCGL4Adapter;
import cc.creativecomputing.gl.app.CCGL4Application;
import cc.creativecomputing.gl4.GLGraphics;
import cc.creativecomputing.gl4.GLGraphics.GLColorBuffer;
import cc.creativecomputing.gl4.GLGraphics.GLPolygonMode;
import cc.creativecomputing.graphics.scene.CCCamera;
import cc.creativecomputing.graphics.scene.CCRenderer;
import cc.creativecomputing.graphics.scene.CCSpatial;
import cc.creativecomputing.graphics.scene.effect.CCSolidColorEffect;
import cc.creativecomputing.graphics.scene.shape.line.CCEllipse;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;

public class CCEllipseDemo extends CCGL4Adapter{
	
	private CCSpatial _myPrimitive;
	
	private CCCamera _myCamera;
	
	
	private CCRenderer _myRenderer;
	
	@Override
	public void start(CCAnimator theAnimator) {
		_myPrimitive = new CCEllipse(100, 0.7f);
	    
	    _myCamera = new CCCamera();
	    _myCamera.lookAt(new CCVector3(0.0f,0.0f,2.0f), new CCVector3(0.0f,0.0f,0.0f), new CCVector3(0.0f,1.0f,0.0f));
	}
	
	@Override
	public void init(GLGraphics g) {
		
		CCSolidColorEffect myEffect = new CCSolidColorEffect(new CCColor());
	    myEffect.wireState().polygonMode = GLPolygonMode.POINT;
	    myEffect.wireState().enabled = true;
	    myEffect.wireState().lineWidth = 10;
	    myEffect.cullState().enabled = false;
	    myEffect.color().set(1f,0f,0f);
		_myPrimitive.effect(myEffect);
	    
	    _myRenderer = new CCRenderer(_myCamera, g);
	}
	
	float myAngle = 0;
	
	@Override
	public void update(CCAnimator theAnimator) {
		myAngle += theAnimator.deltaTime();
		
		_myPrimitive.localTransform().rotation(myAngle, 0.33f, 0.7f, 0);
		_myPrimitive.updateGeometricState(theAnimator);

	}
	
	@Override
	public void reshape(GLGraphics g) {
		g.viewport(0, 0, g.width(), g.height());
	    _myCamera.perpective(70.0f,g.aspectRatio(), 0.3f, 100.0f);
	}
	
	@Override
	public void display(GLGraphics g) {
		g.clearBufferfv(GLColorBuffer.COLOR, 0, 0f, 0f, 0f, 1f);
		g.clearDepthBuffer(1f);	
		g.depthTest();
	    
	    g.pointSize(3);
		_myPrimitive.draw(_myRenderer);
	}
	
	public static void main(String[] args) {
		CCGL4Application myAppManager = new CCGL4Application(new CCEllipseDemo());
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
