package cc.creativecomputing.kle.motors;

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor2ConnectionLinearSetup extends CCMotorSetup{

	protected final CCMotorChannel motor0;
	protected final CCMotorChannel motor1;
	
	protected final CCVector3 _myPlaneDirection;
	
	protected final double _myElementRadius;
	
	protected final double _myMotorDistance;
	
	private CC2Motor2ConnectionLinearBounds _myBounds;
	
	private double _myJoint0X = -77;
	private double _myJoint0Y = -162.5; 
	
	private double _myJoint21Dist = 90;
	private double _myJoint03Dist = 13;
	
	private double _myRotationRadius;
	
	public CC2Motor2ConnectionLinearSetup(List<CCMotorChannel> theChannels, CC2Motor2ConnectionLinearBounds theBounds, CCVector3 theCentroid, double theElementRadius){
		super(theChannels, null);

		_myElementRadius = theElementRadius;
		
		motor0 = _myChannels.get(0);
		motor1 = _myChannels.get(1);
		
		_myJoint0X = theCentroid.x;
		_myJoint0Y = theCentroid.y;
		
		_myJoint21Dist = motor0.connectionPosition().distance(motor1.connectionPosition());
		_myJoint03Dist = theCentroid.distance(motor1.position());
		
		_myCentroid = theCentroid;
		
		_myMotorDistance = motor0.position().distance(motor1.position());
		
		_myPlaneDirection = motor0._myPosition.subtract(motor1._myPosition).normalize();
		_myRotateY = new CCVector2(_myPlaneDirection.x, _myPlaneDirection.z).getPolarAngle() + CCMath.PI;
		
		theBounds.updateBounds(this);
		
		_myBounds = theBounds;
	}
	
	public double elementRadius(){
		return _myElementRadius;
	}
	
	public CCVector3 planeDirection(){
		return _myPlaneDirection;
	}
	
	private CCVector3 animationPosition(double theLift){
		return CCVector3.blend(animationBounds().get(0), animationBounds().get(1), theLift);
	}
	
	@Override
	public void setByRelativePosition(double... theValues) {
		double myRotation = ((theValues != null && theValues.length > 0 ? theValues[0] : 0.5f) * 2 - 1) * _myBounds.maxRotation();
		double myLift = (theValues != null && theValues.length > 1 ? theValues[1] : 0.5f) * _myBounds.maxLift();
		
		_myRotateZ = myRotation;
		
		double myAngle = CCMath.radians(myRotation);
		
		double myJoint1X = 0;
		double myJoint1Y = myLift;
		
		double myJointY = CCMath.sin(myAngle) * _myJoint21Dist + myJoint1Y;
		if(myJointY < 0) {
			myAngle = CCMath.asin(-myJoint1Y / _myJoint21Dist);
		}
		
		if(myJointY > _myBounds.maxLift()) {
			myAngle = CCMath.asin(-(myJoint1Y - 100) / _myJoint21Dist);
			//CCLog.info(myAngle);
		}
		
		_myRotateZ = CCMath.degrees(myAngle);
		
		double myJoint2X = CCMath.cos(myAngle) * _myJoint21Dist + myJoint1X;
		double myJoint2Y = CCMath.sin(myAngle) * _myJoint21Dist + myJoint1Y;
		
		double myJoint02Dist = CCMath.dist(_myJoint0X, _myJoint0Y, myJoint2X, myJoint2Y);
		double myJoint23Dist = CCMath.sqrt(CCMath.sq(myJoint02Dist) - CCMath.sq(_myJoint03Dist));
		
		double myAngle1 = CCMath.asin(_myJoint03Dist / myJoint02Dist);
		
		CCVector2 myDirection = new CCVector2(_myJoint0X - myJoint2X, _myJoint0Y - myJoint2Y).normalizeLocal();
	
		CCVector2 myJoint3 = myDirection.rotate(-myAngle1);
		myJoint3.multiplyLocal(myJoint23Dist);
		myJoint3.addLocal(myJoint2X, myJoint2Y);
		
//		if(motor0.column() == 0) {
//			CCLog.info(myJoint3);
//		}
		
		_myElementOffset.set(animationPosition(myLift));
		
//		double myAngle = 0; //myRotation * CCMath.radians(_myBounds.maxRotation());
		CCVector2 myRotation0 = CCVector2.circlePoint(myAngle, _myMotorDistance / 2, 0, 0);
		CCVector2 myRotation1 = CCVector2.circlePoint(-myAngle, _myMotorDistance / 2, 0, 0);
		
		motor0._myAnimatedConnectionPosition.set(myJoint1X, myJoint1Y, 0);
		motor1._myAnimatedConnectionPosition.set(myJoint2X, myJoint2Y, 0);
		motor1._myPosition.set(myJoint3);
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
