package cc.creativecomputing.kle.elements.motors;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCFastMath;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor2ConnectionSetup extends CCMotorSetup{
	
	private static class CCSetupInfo{
		CCVector3 myConnection0;
		CCVector3 myConnection1;
	}

	protected final CCMotorChannel motor0;
	protected final CCMotorChannel motor1;
	
	protected final double _myElementRadius;
	
	protected final double _myPulleyDistance;
	protected final double _myConnectionPointDistance;
	protected final double _myLeftConnectionCentroidDistance;
	protected final double _myRightConnectionCentroidDistance;
	
//	protected CCTwoPointMatrices _myTwoPointMatrices;
	
	protected CC2Motor2ConnectionCalculations _myCalculations;
	
	protected CCSequenceElement _myElement;
	
	protected final CCVector3 _myCentroidConnection0;
	protected final CCVector3 _myCentroidConnection1;
	
	public CC2Motor2ConnectionSetup(CCSequenceElement theElement, List<CCMotorChannel> theChannels, CC2Motor2ConnectionCalculations theBounds, CCVector3 theCentroid, double theElementRadius){
		super(theChannels, theCentroid);
		_myElement = theElement;
		_myCalculations = theBounds;
		_myElementRadius = theElementRadius;
		
		motor0 = _myChannels.get(0);
		motor1 = _myChannels.get(1);
		
		_myPulleyDistance = motor0.position().distance(motor1.position());
		_myConnectionPointDistance = motor0.connectionPosition().distance(motor1.connectionPosition());
		
		_myCentroidConnection0 = motor0.connectionPosition().subtract(_myCentroid);
		_myCentroidConnection1 = motor1.connectionPosition().subtract(_myCentroid);
		 
		_myLeftConnectionCentroidDistance = _myCentroidConnection0.length();
		_myRightConnectionCentroidDistance = _myCentroidConnection1.length();
		
		theBounds.updateBounds(this);
	}
	
	public int id(){
		return _myElement.id();
	}
	
	public double pulleyDistance(){
		return _myPulleyDistance;
	}
	
	public double connectionDistance(){
		return _myConnectionPointDistance;
	}
	
	public double centroidConnection0Distance(){
		return _myLeftConnectionCentroidDistance;
	}
	
	public double centroidConnection1Distance(){
		return _myRightConnectionCentroidDistance;
	}
	
	private CCSetupInfo calculateRopeConnectPositions(CCVector3 position, double arcAtSBetweenXAxisAndF) {
		CCSetupInfo result = new CCSetupInfo();
		CCVector3 l = new CCVector3(
			position.x - _myLeftConnectionCentroidDistance * CCMath.cos(arcAtSBetweenXAxisAndF),
			position.y + _myLeftConnectionCentroidDistance * CCMath.sin(arcAtSBetweenXAxisAndF)
		);
		result.myConnection0 = l;
		double asin = _myConnectionPointDistance / 2 / _myLeftConnectionCentroidDistance;
		if (asin < -1 || asin > 1)
			return null;
			
		// TODO:check if this holds for all elements
		double arcAtSBetweenFAndG = 2 * CCMath.asin(_myConnectionPointDistance / 2 / _myLeftConnectionCentroidDistance);
		CCVector3 r = new CCVector3(
			position.x - _myLeftConnectionCentroidDistance * CCMath.cos(arcAtSBetweenXAxisAndF + arcAtSBetweenFAndG),
			position.y + _myLeftConnectionCentroidDistance * CCMath.sin(arcAtSBetweenXAxisAndF + arcAtSBetweenFAndG)
		);
		result.myConnection1 = r;
		return result;
	}
	
	@SuppressWarnings("unused")
	private CCSetupInfo updateValues(CCVector3 position, double precision){
		double currentValue = 0;
		double targetValue = (position.x + _myPulleyDistance / 2) / (_myPulleyDistance / 2 - position.x);
		
		double divider = 100;
		double currentArc = Math.PI/2;
		double prevValue = 0;
		double prevArc = 0;
		
		CCSetupInfo myResult = null;
	
		while (CCMath.abs(currentValue - targetValue) > precision){
			double maxArc = currentArc;
			currentArc = prevArc;
			currentValue = prevValue;
			double currentDelta = (maxArc-currentArc) / divider;
			
			while (currentArc < maxArc && (currentValue < targetValue)){
				prevArc = currentArc;
				currentArc += currentDelta;
				prevValue = currentValue;
				myResult = calculateRopeConnectPositions(
					position,
					currentArc
				);
				if (myResult == null) return myResult;
				
				double tanAlpha = CCMath.abs(myResult.myConnection0.x - motor0.position().x) / (myResult.myConnection0.y - motor0.position().y);
				double tanBeta = CCMath.abs(myResult.myConnection1.x - motor1.position().x) / (myResult.myConnection1.y - motor1.position().y);
				
				currentValue = tanAlpha / tanBeta;
			}        
			divider = 10;
		}
		return myResult;
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
	
	private CCVector3 animationPosition(double theX, double theY){
		return CCVector3.lerp(
			CCVector3.lerp(animationBounds().get(0), animationBounds().get(1), theX), 
			CCVector3.lerp(animationBounds().get(3), animationBounds().get(2), theX), 
			theY
		);
	}
	
	
	@Override
	public void setByRelativePosition(double... theValues) {
		double myX = CCMath.saturate(theValues != null && theValues.length > 0 ? theValues[0] : 0.5);
		double myY = CCMath.saturate(theValues != null && theValues.length > 1 ? theValues[1] : 0.5);
		
		_myRelativeOffset.set(myX, myY,0);
		_myElementOffset.set(animationPosition(myX, myY));
		
		_myRotateZ = CCMath.radians(_myCalculations.rotation(this));
		
		motor0._myAnimatedConnectionPosition = _myCentroidConnection0.rotate(0, 0, 1, _myRotateZ).add(_myElementOffset);
		motor1._myAnimatedConnectionPosition = _myCentroidConnection1.rotate(0, 0, 1, _myRotateZ).add(_myElementOffset);
		
		if(_myCalculations._cCalcBack){
			CCVector2 myCalcPos = _myCalculations.position(this);
			
			if(_myElement.id() < 8){
//				CCLog.info(_myElementOffset.x + " : " + myCalcPos.x + " : " + _myElementOffset.y + ":" + myCalcPos.y);
	//			CCLog.info(_myElement.id() + ":" + length0 + " ; " +  length1 + " ; " +  _myElementOffset + " ; " +  myCalcPos);
			}
			_myElementOffset.set(myCalcPos.x, myCalcPos.y, 0);
			
	
			motor0._myAnimatedConnectionPosition = _myCentroidConnection0.rotate(0, 0, 1, _myRotateZ).add(_myElementOffset);
			motor1._myAnimatedConnectionPosition = _myCentroidConnection1.rotate(0, 0, 1, _myRotateZ).add(_myElementOffset);
		}
	}
	
	@Override
	public void setByRopeLength(double... theValues) {
		
		CCVector2 myCalcPos = _myCalculations.position(this, theValues[0], theValues[1]);
		
		if(_myElement.id() < 8){
			CCLog.info(_myElementOffset.x + " : " + myCalcPos.x + " : " + _myElementOffset.y + ":" + myCalcPos.y);
//			CCLog.info(_myElement.id() + ":" + length0 + " ; " +  length1 + " ; " +  _myElementOffset + " ; " +  myCalcPos);
		}
		_myElementOffset.set(myCalcPos.x, myCalcPos.y, 0);
		

		motor0._myAnimatedConnectionPosition = _myCentroidConnection0.rotate(0, 0, 1, _myRotateZ).add(_myElementOffset);
		motor1._myAnimatedConnectionPosition = _myCentroidConnection1.rotate(0, 0, 1, _myRotateZ).add(_myElementOffset);
	}
	
	public CCVector2 ropeLengthRelative(double theLength0, double theLength1){
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
			double x = CCFastMath.sin(angle) * _myElementRadius + _myElementOffset.x;
			double y = CCFastMath.cos(angle) * _myElementRadius + _myElementOffset.y;
			double z = 0;
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
