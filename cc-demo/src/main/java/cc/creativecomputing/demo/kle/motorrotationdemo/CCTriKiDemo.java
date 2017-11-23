package cc.creativecomputing.demo.kle.motorrotationdemo;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCOffsetEffect;
import cc.creativecomputing.effects.CCSignalEffect;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.kle.CCKleChannelType;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCTriKiDemo extends CCGL2Adapter{
	
	
	public class CCTriangleElement{
		public CCVector3 _myPosition;
		
		public double _myAngle = CCMath.random(CCMath.TWO_PI);
		
		public int x;
		public int y;
		
		public CCTriangleElement(CCVector3 thePosition, int theX, int theY){
			_myPosition = thePosition;
			x = theX;
			y = theY;
		}
		
		private CCVector3 point(int i){
			double myAngle = CCMath.map(i, 0, 3, 0, CCMath.TWO_PI);
			return new CCVector3(
				CCMath.cos(myAngle) * _cRadius,
				0,
				CCMath.sin(myAngle) * _cRadius
			);
		}
		
		public void draw(CCGraphics g){
			g.pushMatrix();
			g.translate(_myPosition);
			g.rotateY(CCMath.degrees(_myAngle));
			g.beginShape(CCDrawMode.QUADS);
			for(int i = 0; i < 3;i++){
				CCVector3 myVec0 = point(i);
				CCVector3 myVec1 = point(i + 1);
				
				switch(i){
				case 0:
					g.color(255,0,0);
					break;
				case 1:
					g.color(0,255,0);
					break;
				case 2:
					g.color(0,0,255);
					break;
				}
				g.vertex(myVec0.x, myVec0.y - _cSpace, myVec0.z);
				g.vertex(myVec1.x, myVec1.y - _cSpace, myVec1.z);
				g.vertex(myVec1.x, myVec1.y + _cSpace, myVec1.z);
				g.vertex(myVec0.x, myVec0.y + _cSpace, myVec0.z);
			}
			g.endShape();
			g.popMatrix();
		}
	}
	
	@CCProperty(name = "space", min = 1, max = 150)
	private double _cSpace = 10;
	@CCProperty(name = "radius", min = 1, max = 150)
	private double _cRadius = 10;
	
	@CCProperty(name = "animator")
	private CCEffectManager _myAnimator;
	
	private List<CCTriangleElement> _myTriangleElements = new ArrayList<>();
	
	
	@Override
	public void start(CCAnimator theAnimator) {
		for(int x = 0; x < 10; x++){
			for(int y = 0; y < 10; y++){
				double tx = CCMath.map(x, 0, 10 - 1, -800, 800);
				double ty = CCMath.map(y, 0, 10 - 1, -400, 400);
				_myTriangleElements.add(new CCTriangleElement(new CCVector3(tx, ty, 0), x, y));
			}
		}
//		_myAnimator = new CCEffectManager(_myTriangleElements, CCKleChannelType.MOTORS, "a");
//		_myAnimator.put("signal animation", new CCSignalEffect());
//		_myAnimator.put("offset animation", new CCOffsetEffect());
	}
	
	@Override
	public void init(CCGraphics g) {
		
	}
	
	
	@Override
	public void update(CCAnimator theAnimator) {
		_myAnimator.update(theAnimator);
	}
	
	@Override
	public void display(CCGraphics g) {
		g.clear();
		
		for(CCTriangleElement myElement:_myTriangleElements){
			myElement.draw(g);
		}
	}
	
	public static void main(String[] args) {
		
		
		CCTriKiDemo demo = new CCTriKiDemo();
		
		
		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1900, 1000);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}
