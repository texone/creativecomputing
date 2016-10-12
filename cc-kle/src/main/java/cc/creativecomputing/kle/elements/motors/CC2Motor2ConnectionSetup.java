package cc.creativecomputing.kle.elements.motors;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCFastMath;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor2ConnectionSetup extends CCMotorSetup{

	protected final CCMotorChannel motor0;
	protected final CCMotorChannel motor1;
	
	protected final CCVector3 _myPlaneDirection;
	
	protected final double _myElementRadius;
	
	protected final double _myMotorDistance;
	
	protected CCTwoPointMatrices _myTwoPointMatrices;
	
	protected CC2Motor2ConnectionBounds _myBounds;
	
	protected CCSequenceElement _myElement;
	
	public CC2Motor2ConnectionSetup(CCSequenceElement theElement, List<CCMotorChannel> theChannels, CC2Motor2ConnectionBounds theBounds, double theElementRadius){
		super(theChannels);
		_myElement = theElement;
		_myBounds = theBounds;

		_myElementRadius = theElementRadius;
		
		motor0 = _myChannels.get(0);
		motor1 = _myChannels.get(1);
		
		_myMotorDistance = motor0.position().distance(motor1.position());
		
		_myPlaneDirection = motor0._myPosition.subtract(motor1._myPosition).normalize();
		_myRotateY = new CCVector2(_myPlaneDirection.x, _myPlaneDirection.z).getPolarAngle() + CCMath.PI;
		
		theBounds.updateBounds(this);
		
		_myAnimationCenter = animationPosition(0.5f, 0.5f);
		
		_myTwoPointMatrices = new CCTwoPointMatrices(
			0,
			motor0._myPosition, motor0._myConnectionPosition,
			motor1._myPosition, motor1._myConnectionPosition,
			theBounds.topDistance(),
			theBounds.bottomDistance(),
			theBounds.minRopeAngle()
		);
	}
	
	public CCMotorChannel motor0(){
		return motor0;
	}
	
	public CCMotorChannel motor1(){
		return motor1;
	}
	
	public double elementRadius(){
		return _myElementRadius;
	}
	
	public CCVector3 planeDirection(){
		return _myPlaneDirection;
	}
	
	public CCTwoPointMatrices twoPointMatrices(){
		return _myTwoPointMatrices;
	}
	
	private CCVector3 animationPosition(double theX, double theY){
		return CCVector3.lerp(
			CCVector3.lerp(animationBounds().get(0), animationBounds().get(1), theX), 
			CCVector3.lerp(animationBounds().get(3), animationBounds().get(2), theX), 
			theY
		).subtractLocal(_myAnimationCenter);
	}
	
	@Override
	public void setByRelativePosition(double... theValues) {
		double myX = theValues != null && theValues.length > 0 ? theValues[0] : 0.5f;
		double myY = theValues != null && theValues.length > 1 ? theValues[1] : 0.5f;
		
		_myRelativeOffset.set(myX, myY,0);
		_myElementOffset.set(animationPosition(myX, myY));
		
		double myDistance = new CCVector2(motor0._myPosition.x, motor0._myPosition.z).distance(_myElementOffset.x + _myAnimationCenter.x,_myElementOffset.z + _myAnimationCenter.z);
		myDistance /= _myMotorDistance;
		_myElementOffset2D.set(
			CCMath.blend(-400, 400, myDistance),
			 _myElementOffset.y + _myAnimationCenter.y - motor0.position().y
		);
		
		
		CCPositionRopeLengthAngle myData = _myTwoPointMatrices.dataByPosition(_myElementOffset2D.x, _myElementOffset2D.y);
		double myAngle =  -(myX - 0.5) * CCMath.radians(60) + 0.5;
		
		if(myData != null){
			_myRotateZ = -myAngle + _myTwoPointMatrices.centerAngle();
			motor0._myAnimatedConnectionPosition = _myTwoPointMatrices.leftConnection(_myElementOffset.add(_myAnimationCenter), _myPlaneDirection, myAngle);
			motor1._myAnimatedConnectionPosition = _myTwoPointMatrices.rightConnection(_myElementOffset.add(_myAnimationCenter), _myPlaneDirection, myAngle);
		}
			
//		motor0._myAnimatedConnectionPosition =  motor0._myConnectionPosition.add(_myElementOffset).addLocal(0, _myAnimationCenter.y, 0); 
//		motor1._myAnimatedConnectionPosition =  motor1._myConnectionPosition.add(_myElementOffset).addLocal(0, _myAnimationCenter.y, 0);
		
		
	}
	
	@Override
	public void setByRopeLength(double... theValues) {
		CCPositionRopeLengthAngle myData = _myTwoPointMatrices.dataByRopeLength(theValues[0], theValues[1]);
		
		if(myData != null){
			_myRotateZ = -myData.angle() + _myTwoPointMatrices.centerAngle();
			motor0._myAnimatedConnectionPosition = _myTwoPointMatrices.leftConnection(_myElementOffset.add(_myAnimationCenter), _myPlaneDirection, myData.angle());
			motor1._myAnimatedConnectionPosition = _myTwoPointMatrices.rightConnection(_myElementOffset.add(_myAnimationCenter), _myPlaneDirection, myData.angle());
		}
	}
	
	public CCVector2 ropeLengthRelative(double theLength0, double theLength1){
		CCPositionRopeLengthAngle myData = _myTwoPointMatrices.dataByRopeLength(theLength0, theLength1);
		return new CCVector2();
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
			double x = CCFastMath.sin(angle) * _myElementRadius * _myPlaneDirection.x + _myElementOffset.x + _myAnimationCenter.x;
			double y = CCFastMath.cos(angle) * _myElementRadius + _myElementOffset.y + _myAnimationCenter.y;
			double z = CCFastMath.sin(angle) * _myElementRadius * _myPlaneDirection.z + _myElementOffset.z + _myAnimationCenter.z;
			g.vertex(x,y,z);
		}
		g.endShape();
	}
	
	public void drawRangeBounds(CCGraphics g){

		g.beginShape(CCDrawMode.LINE_LOOP);
		for(CCVector3 myBound:new ArrayList<>(_myMotorBounds)){
			if(myBound == null)continue;
			g.vertex(myBound);
		}
		g.endShape();
		
		g.beginShape(CCDrawMode.LINE_LOOP);
		for(CCVector3 myBound:new ArrayList<>(_myMotorAnimationBounds)){
			if(myBound == null)continue;
			g.vertex(myBound);
		}
		g.endShape();
	}
}
