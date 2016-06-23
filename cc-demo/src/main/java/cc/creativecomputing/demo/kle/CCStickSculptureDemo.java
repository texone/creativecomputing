package cc.creativecomputing.demo.kle;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCEffectables;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCStickSculptureDemo extends CCGL2Adapter {
	
	private class CCStickEffectable extends CCEffectable{
		
		private double _myX;
		private double _myY;
		private double _myZ;
		

		private double _myXAnimation;
		private double _myYAnimation;
		private double _myZAnimation;

		public CCStickEffectable(int theId, double theX, double theZ) {
			super(theId);
			_myX = theX;
			_myZ = theZ;
		}
		
		public double x(){
			return _myXAnimation + _myX * _cXDistance;
		}
		
		public double y(){
			return _myYAnimation + _myY + 10;
		}
		
		public double z(){
			return _myZAnimation + _myZ * _cZDistance;
		}
		
		@Override
		public void apply(double... theValues) {
			_myXAnimation = (theValues[0] * 2 - 1) * _cXMax;
			_myYAnimation = theValues[1] * _cYMax;
			_myZAnimation = (theValues[2] * 2 - 1) * _cZMax;
		}
	}
	
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	
	@CCProperty(name = "x distance", min = 0, max = 500)
	private double _cXDistance = 400;
	
	@CCProperty(name = "z distance", min = 0, max = 250)
	private double _cZDistance = 100;
	@CCProperty(name = "x max", min = 0, max = 50)
	private double _cXMax = 20;
	@CCProperty(name = "y max", min = 0, max = 200)
	private double _cYMax = 20;
	@CCProperty(name = "z max", min = 0, max = 250)
	private double _cZMax = 20;

	@CCProperty(name = "stick color")
	private CCColor _cStickColor = new CCColor();
	
	@CCProperty(name = "band color")
	private CCColor _cBandColor = new CCColor();
	
	@CCProperty(name = "blend mode")
	private CCBlendMode _cBlendMode = CCBlendMode.BLEND;
	@CCProperty(name = "depth Test")
	private boolean _cDepthTest = true;
	
	private CCEffectables<CCStickEffectable> _mySticks = new CCEffectables<>();
	

	@CCProperty(name = "effects")
	private CCEffectManager<CCStickEffectable> _myEffectManager;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myCameraController = new CCCameraController(this, g, 100);
		
		for(int i = 0; i < 30;i++){
			double myX = CCMath.map(i, 0, 30, -1, 1);
			CCStickEffectable myStick0 = new CCStickEffectable(i * 2, myX, -1);
			myStick0._myXBlend = i / 29d;
			myStick0._myYBlend = 0;
			myStick0.groupIDBlend(i / 29d);
			myStick0.idBlend(i / 29d);
			myStick0.groupBlend(0);
			myStick0.group(0);
			_mySticks.add(myStick0);
			
			myX = CCMath.map(i + 0.5, 0, 30, -1, 1);
			CCStickEffectable myStick1 = new CCStickEffectable(i * 2 + 1, myX, 1);
			myStick1._myXBlend = (i + 0.5) / 29d;
			myStick1._myYBlend = 1;
			myStick1.groupIDBlend(i / 29d);
			myStick1.idBlend(i / 29d);
			myStick1.groupBlend(0);
			myStick1.group(0);
			_mySticks.add(myStick1);
		}
		
		_myEffectManager = new CCEffectManager<CCStickEffectable>(_mySticks, "x","y","z");
		_myEffectManager.put("offset", new CCOffsetEffect());
		_myEffectManager.put("signal", new CCSignalEffect());
	}

	@Override
	public void update(CCAnimator theAnimator) {
		_myEffectManager.update(theAnimator);
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		_myCameraController.camera().draw(g);
		
		g.blend(_cBlendMode);
		if(_cDepthTest){
			g.depthTest();
		}else{
			g.noDepthTest();
		}
		g.color(_cStickColor);
		for(CCStickEffectable myStick:_mySticks){
			g.line(
				myStick.x(), 0, myStick.z(),
				myStick.x(), myStick.y(), myStick.z()
			);
		}
		
		g.color(_cBandColor);
		g.beginShape(CCDrawMode.QUAD_STRIP);
		for(CCStickEffectable myStick:_mySticks){
			g.vertex(myStick.x(), myStick.y() - 10, myStick.z());
			g.vertex(myStick.x(), myStick.y(), myStick.z());
		}
		g.endShape();
		
		g.depthTest();
		g.color(1d);
		g.pushMatrix();
		g.translate(0,40,0);
		g.boxGrid(1000, 80, 500);
		g.popMatrix();
	}

	public static void main(String[] args) {

		CCStickSculptureDemo demo = new CCStickSculptureDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
