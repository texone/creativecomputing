package cc.creativecomputing.demo.kle;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.graphics.camera.CCCameraController;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCTriangleEffectableDemo extends CCGL2Adapter{
	
	public class CCTriangleFront extends CCEffectable{
		public CCTriangleFront(int theId) {
			super(theId);
		}

		private CCColor _myColor = new CCColor();
		
		@Override
		public void apply(double... theValues) {
			_myColor = CCColor.createFromHSB(theValues[0], theValues[1], theValues[2]);
		}
	}
	
	public class CCTriangleElement extends CCEffectable{
		public CCVector3 _myPosition;
		
		public double _myAngle;
		
		public int x;
		
		private List<CCTriangleFront> _myFronts = new ArrayList<>();
		
		public CCTriangleElement(int theID, CCVector3 thePosition, int theX){
			super(theID);
			_myPosition = thePosition;
			x = theX;
			for(int i = 0; i < 3;i++){
			CCTriangleFront myFront = new CCTriangleFront(_myTriangleFronts.size());
			_myFronts.add(myFront);
			_myTriangleFronts.add(myFront);
			}
		}
		
		private CCVector3 point(int i){
			double myAngle = CCMath.map(i, 0, 3, 0, CCMath.TWO_PI) + CCMath.radians(30);
			return new CCVector3(
				CCMath.cos(myAngle) * _cRadius,
				0,
				CCMath.sin(myAngle) * _cRadius
			);
		}
		
		@Override
		public void apply(double...theValues) {
			if(theValues.length == 1){
				_myAngle = theValues[0] * CCMath.PI;
			}else{
				
			}
		}
		
		public void draw(CCGraphics g){
			g.pushMatrix();
			g.translate(_myPosition);
			g.rotateY(CCMath.degrees(_myAngle));
			g.beginShape(CCDrawMode.QUADS);
			for(int i = 0; i < 3;i++){
				CCVector3 myVec0 = point(i);
				CCVector3 myVec1 = point(i + 1);
				
				g.color(_myFronts.get(i)._myColor);
				g.vertex(myVec0.x, myVec0.y - _cSpace, myVec0.z);
				g.vertex(myVec1.x, myVec1.y - _cSpace, myVec1.z);
				g.vertex(myVec1.x, myVec1.y + _cSpace, myVec1.z);
				g.vertex(myVec0.x, myVec0.y + _cSpace, myVec0.z);
			}
			g.endShape();
			g.popMatrix();
		}
	}
	
	@CCProperty(name = "space", min = 1, max = 1000)
	private double _cSpace = 10;
	@CCProperty(name = "radius", min = 1, max = 150)
	private double _cRadius = 10;
	
	@CCProperty(name = "rotation effects")
	private CCEffectManager<CCTriangleElement> _myRotationManager;
	@CCProperty(name = "color effects")
	private CCEffectManager<CCTriangleFront> _myColorManager;
	
	private List<CCTriangleElement> _myTriangleElements = new ArrayList<>();
	private List<CCTriangleFront> _myTriangleFronts = new ArrayList<>();
	
	@CCProperty(name = "camera")
	private CCCameraController _myCameraController;
	
	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		for(int x = 0; x < 30; x++){
			double tx = CCMath.map(x, 0, 30 - 1, -800, 800);
			_myTriangleElements.add(new CCTriangleElement(x, new CCVector3(tx, 0, 0), x));
		}
		
		_myRotationManager = new CCEffectManager<CCTriangleElement>(_myTriangleElements, "r");

		_myRotationManager.updateInfos();
		_myRotationManager.put("offset", new CCOffsetEffect());
		_myRotationManager.put("offset2", new CCOffsetEffect());
		_myRotationManager.put("signal", new CCSignalEffect());
		_myColorManager = new CCEffectManager<CCTriangleFront>(_myTriangleFronts, "h", "s", "b");
		_myColorManager.put("offset", new CCOffsetEffect());
		_myColorManager.updateInfos();
		
		_myCameraController = new CCCameraController(this, g, 100);
		
//		_myAnimator = new CCEffectManager(_myTriangleElements, CCKleChannelType.MOTORS, "a");
//		_myAnimator.put("signal animation", new CCSignalEffect());
//		_myAnimator.put("offset animation", new CCOffsetEffect());
	}
	
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myRotationManager.update(theAnimator);
		_myColorManager.update(theAnimator);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		_myCameraController.camera().draw(g);
		for(CCTriangleElement myElement:_myTriangleElements){
			myElement.draw(g);
		}
	}
	
	public static void main(String[] args) {
		
		
		CCTriangleEffectableDemo demo = new CCTriangleEffectableDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
