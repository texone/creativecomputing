package cc.creativecomputing.demo.kle.motor1connection1;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CC2Motor2ConnectionSetupDemo extends CCGL2Adapter {
	@CCProperty(name = "lift", min = 0, max = 100)
	private double _cLift = 50;
	
	@CCProperty(name = "rotation", min = -35, max = 35)
	private double _cRotation = 50;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
	}

	@Override
	public void update(CCAnimator theAnimator) {
	}

	@Override
	public void display(CCGraphics g) {
		g.clear();

		double myJoint0X = 77;
		double myJoint0Y = -162.5;
		
		double myJoint21Dist = 90;
		double myJoint03Dist = 13;
		
		double myAngle = CCMath.radians(_cRotation);
		
		double myJoint1X = 0;
		double myJoint1Y = _cLift;
		

		double myJointY = CCMath.sin(myAngle) * myJoint21Dist + myJoint1Y;
		if(myJointY < 0) {
			myAngle = CCMath.asin(-myJoint1Y / myJoint21Dist);
		}
		
		if(myJointY > 100) {
			myAngle = CCMath.asin(-(myJoint1Y - 100) / myJoint21Dist);
		}

		double myJoint2X = CCMath.cos(myAngle) * myJoint21Dist + myJoint1X;
		double myJoint2Y = CCMath.sin(myAngle) * myJoint21Dist + myJoint1Y;
		
		double myJoint02Dist = CCMath.dist(myJoint0X, myJoint0Y, myJoint2X, myJoint2Y);
		double myJoint23Dist = CCMath.sqrt(CCMath.sq(myJoint02Dist) - CCMath.sq(myJoint03Dist));

		double myAngle1 = CCMath.asin(myJoint03Dist / myJoint02Dist);
		
		CCVector2 myDirection = new CCVector2(myJoint0X - myJoint2X, myJoint0Y - myJoint2Y).normalizeLocal();
	
		CCVector2 myJoint3 = myDirection.rotate(-myAngle1);
		myJoint3.multiplyLocal(myJoint23Dist);
		myJoint3.addLocal(myJoint2X, myJoint2Y);

		CCLog.info(myJoint23Dist);
		
		g.pointSize(3);
		g.beginShape(CCDrawMode.POINTS);
		g.vertex(myJoint0X,myJoint0Y);
		g.vertex(myJoint1X,myJoint1Y);
		g.vertex(myJoint2X,myJoint2Y);
		g.vertex(myJoint3.x,myJoint3.y);
		g.endShape();
		
		g.line(myJoint0X, myJoint0Y, 0, myJoint0Y);
		g.line(myJoint0X, myJoint0Y, myJoint2X, myJoint2Y);
		g.line(myJoint0X, myJoint0Y, myJoint3.x, myJoint3.y);
		g.line(myJoint2X, myJoint2Y, myJoint3.x, myJoint3.y);
		
		g.line(0,0,myJoint1X,myJoint1Y);
		g.line(myJoint1X,myJoint1Y,myJoint2X,myJoint2Y);
		
		g.color(255);
		
	}

	public static void main(String[] args) {

		CC2Motor2ConnectionSetupDemo demo = new CC2Motor2ConnectionSetupDemo();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(600, 1200);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
	}
}

