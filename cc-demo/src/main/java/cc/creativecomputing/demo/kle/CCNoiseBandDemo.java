package cc.creativecomputing.demo.kle;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.graphics.export.CCScreenCaptureController;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCNoiseBandDemo extends CCGL2Adapter{
	
	private class CCStickEffectable extends CCEffectable{
		
		private CCVector2 _myCenter;
		
		private double _myRotateX;
		private double _myRotateY;
		private double _myRotateZ;
		
		private double _myScale;
		private double _myY;
		
		public CCStickEffectable(int theId, CCVector2 theCenter) {
			super(theId);
			_myCenter = theCenter;
		}
		
		@Override
		public void apply(double...theValues) {
			_myRotateX = theValues[0] * _cRotateXAmp * 180;
			_myRotateY = theValues[1] * _cRotateYAmp * 180;
			_myRotateZ = theValues[2] * _cRotateZAmp * 180;
			
			_myScale = theValues[3];
			
			_myY = theValues[4] * _cHeight; 
		}
		
		
		
		public void draw(CCGraphics g){
			
//			g.color(CCMath.pow( _myAlpha,_cColorPow),CCMath.pow( _myAlpha,_cAlphaPow));
			
			g.pushMatrix();
			g.translate(_myCenter.x * _cWidthScup, _myCenter.y + _myY);
//			g.scale(_myScale);
//			CCLog.info(_myRotateX+ ":" + _myRotateY + ":" + _myRotateZ);
			g.rotateX(_myRotateX);
			g.rotateY(_myRotateY);
			g.rotateZ(_myRotateZ);
			
			double myScale = _myScale * 300;
			
			g.line(0,_cSpace,-myScale,0,_cSpace,myScale);
			g.line(0,-_cSpace,-myScale,0,-_cSpace, myScale);
			g.popMatrix();
		}
	}
	
	
	
	
	
	
	private List<CCStickEffectable> _myCubes = new ArrayList<>();
	
	@CCProperty(name = "rotate x amp", min = 0, max = 2)
	private double _cRotateXAmp = 1;
	@CCProperty(name = "rotate y amp", min = 0, max = 2)
	private double _cRotateYAmp = 1;
	@CCProperty(name = "rotate z amp", min = 0, max = 2)
	private double _cRotateZAmp = 1;
	@CCProperty(name = "space", min = 0, max = 200)
	private double _cSpace = 1;
	@CCProperty(name = "height", min = -500, max = 500)
	private double _cHeight = 1;
	@CCProperty(name = "width_sculp", min = 0, max = 2000)
	private double _cWidthScup = 1;
	
	@CCProperty(name = "effects")
	private CCEffectManager<CCStickEffectable> _myEffectManager;
	
	@CCProperty(name = "screener")
	private CCScreenCaptureController _myScreenCaptureController;
	
	private CCCameraController _myCameraController;
	
	@Override
	public void init( CCGraphics g, CCAnimator theAnimator) {
		int i = 0;
		for(int c = 0; c < 100; c++){
			CCStickEffectable myCube = new CCStickEffectable(i, new CCVector2(CCMath.map(c, 0, 99, -1, 1) ,0));
			myCube.column(c);
			_myCubes.add(myCube);
			i++;
		}
		_myEffectManager = new CCEffectManager<CCStickEffectable>(_myCubes, "x", "y", "z", "scale", "y move");
		
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("offset2", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
		_myEffectManager.put("signal2", new CCSignalEffect());
		
		_myScreenCaptureController = new CCScreenCaptureController(this, theAnimator);
		
		_myCameraController = new CCCameraController(this, g, 100);
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		g.color(1f);
		
		_myCameraController.camera().draw(g);

		for(CCStickEffectable myCube:_myCubes){
			myCube.draw(g);
		}
		
	}
	
	public static void main(String[] args) {
		
		
		CCNoiseBandDemo demo = new CCNoiseBandDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1920, 1080);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
