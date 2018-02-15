package cc.creativecomputing.kle.motors;

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CC2MotorRotationAxisSetup extends CCMotorSetup{

	protected final CCMotorChannel motor0;
	protected final CCMotorChannel motor1;
	
	protected final double _myElementRadius;
	
	private CC2MotorRotationAxisBounds _myBounds;
	
	public CC2MotorRotationAxisSetup(List<CCMotorChannel> theChannels, CC2MotorRotationAxisBounds theBounds, double theElementRadius){
		super(theChannels, null);

		_myElementRadius = theElementRadius;
		
		motor0 = _myChannels.get(0);
		motor1 = _myChannels.get(1);
		
		_myCentroid = motor0.connectionPosition().clone();
		
		_myBounds = theBounds;
		_myBounds.updateBounds(this);
	}
	
	public double elementRadius(){
		return _myElementRadius;
	}
	
	
	private CCVector3 animationPosition(double theX, double theY){
		double myAngle = theX * CCMath.radians(_myBounds.amplitude()) - CCMath.HALF_PI + CCMath.radians(_myBounds.center());
		return new CCVector3(
			CCMath.cos(myAngle) * _myBounds.radius() * theY,
			CCMath.sin(myAngle) * _myBounds.radius() * theY
		);
	}
	
	@Override
	public void setByRelativePosition(double... theValues) {
		double myX = theValues != null && theValues.length > 0 ? theValues[0] : 0.5f;
		double myY = theValues != null && theValues.length > 1 ? theValues[1] : 0.5f;
		
		_myElementOffset.set(animationPosition(myX, myY));
		
		motor0._myAnimatedConnectionPosition = _myElementOffset.clone();
		motor1._myAnimatedConnectionPosition = _myElementOffset.clone();
		
	}
	
	@Override
	public void setByRopeLength(double... theValues) {
//		double motionValue0 = theValues[0];
//		double motionValue1 = theValues[1];
//		
//			
//		double a = motionValue0;
//		double b = motionValue1;
//		double c = _myMotorDistance;
//			        
//		double beta = CCMath.acos ((a * a + c * c - b * b) / (2.0 * a * c));
//		double x = a * CCMath.cos(beta);
//		double h = a * CCMath.sin(beta);
//		
//		_myElementOffset = CCVector3.blend(motor0.position(), motor1.position(), x / c);
//		_myElementOffset.y += h;
//		
//		motor0._myAnimatedConnectionPosition = _myElementOffset.clone();
//		motor1._myAnimatedConnectionPosition = _myElementOffset.clone();
	}
	
	@Override
	public void drawRopes(CCGraphics g){

//		g.line(motor0._myPosition, motor0._myAnimatedConnectionPosition); 
//		g.line(motor1._myPosition, motor1._myAnimatedConnectionPosition);
	
	}
	
	public void drawElementBounds(CCGraphics g){
//		g.beginShape(CCDrawMode.LINE_LOOP);
//		for(int i = 0; i < 100;i++){
//			double angle = CCMath.blend(0, CCMath.TWO_PI, i / 100f);
//			double x = CCMath.sin(angle) * _myElementRadius * _myPlaneDirection.x + _myElementOffset.x;
//			double y = CCMath.cos(angle) * _myElementRadius + _myElementOffset.y;
//			double z = CCMath.sin(angle) * _myElementRadius * _myPlaneDirection.z + _myElementOffset.z;
//			g.vertex(x,y,z);
//		}
//		g.endShape();
	}
	
	public void drawRangeBounds(CCGraphics g){

//		g.beginShape(CCDrawMode.LINE_LOOP);
//		for(CCVector3 myBound:_myMotorBounds){
//			g.vertex(myBound);
//		}
//		g.endShape();
//		
//		g.beginShape(CCDrawMode.LINE_LOOP);
//		for(CCVector3 myBound:_myMotorAnimationBounds){
//			g.vertex(myBound);
//		}
//		g.endShape();
	}
}
