package cc.creativecomputing.kle.motors;

import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor1ConnectionSetup extends CCMotorSetup{

	protected final CCMotorChannel motor0;
	protected final CCMotorChannel motor1;
	
	protected final CCVector3 _myPlaneDirection;
	
	protected final double _myElementRadius;
	
	protected final double _myMotorDistance;
	
	public CC2Motor1ConnectionSetup(List<CCMotorChannel> theChannels, CC2Motor1ConnectionBounds theBounds, double theElementRadius){
		super(theChannels, null);

		_myElementRadius = theElementRadius;
		
		motor0 = _myChannels.get(0);
		motor1 = _myChannels.get(1);
		
		_myCentroid = motor0.connectionPosition().clone();
		
		_myMotorDistance = motor0.position().distance(motor1.position());
		
		_myPlaneDirection = motor0._myPosition.subtract(motor1._myPosition).normalize();
		_myRotateY = new CCVector2(_myPlaneDirection.x, _myPlaneDirection.z).getPolarAngle() + CCMath.PI;
		
		theBounds.updateBounds(this);
	}
	
	public double elementRadius(){
		return _myElementRadius;
	}
	
	public CCVector3 planeDirection(){
		return _myPlaneDirection;
	}
	
	private CCVector3 animationPosition(double theX, double theY){
		return CCVector3.blend(
			CCVector3.blend(animationBounds().get(0), animationBounds().get(1), theX), 
			CCVector3.blend(animationBounds().get(3), animationBounds().get(2), theX), 
			theY
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
		double motionValue0 = theValues[0];
		double motionValue1 = theValues[1];
		
			
		double a = motionValue0;
		double b = motionValue1;
		double c = _myMotorDistance;
			        
		double beta = CCMath.acos ((a * a + c * c - b * b) / (2.0 * a * c));
		double x = a * CCMath.cos(beta);
		double h = a * CCMath.sin(beta);
		
		_myElementOffset = CCVector3.blend(motor0.position(), motor1.position(), x / c);
		_myElementOffset.y += h;
		
		motor0._myAnimatedConnectionPosition = _myElementOffset.clone();
		motor1._myAnimatedConnectionPosition = _myElementOffset.clone();
	}
	
	@Override
	public void drawRopes(CCGraphics g){

		g.line(motor0._myPosition, motor0._myAnimatedConnectionPosition); 
		g.line(motor1._myPosition, motor1._myAnimatedConnectionPosition);
	
	}
	
	public void drawElementBounds(CCGraphics g){
		g.beginShape(CCDrawMode.LINE_LOOP);
		for(int i = 0; i < 100;i++){
			double angle = CCMath.blend(0, CCMath.TWO_PI, i / 100f);
			double x = CCMath.sin(angle) * _myElementRadius * _myPlaneDirection.x + _myElementOffset.x;
			double y = CCMath.cos(angle) * _myElementRadius + _myElementOffset.y;
			double z = CCMath.sin(angle) * _myElementRadius * _myPlaneDirection.z + _myElementOffset.z;
			g.vertex(x,y,z);
		}
		g.endShape();
	}
	
	public void drawRangeBounds(CCGraphics g){

		g.beginShape(CCDrawMode.LINE_LOOP);
		for(CCVector3 myBound:_myMotorBounds){
			g.vertex(myBound);
		}
		g.endShape();
		
		g.beginShape(CCDrawMode.LINE_LOOP);
		for(CCVector3 myBound:_myMotorAnimationBounds){
			g.vertex(myBound);
		}
		g.endShape();
	}
}
