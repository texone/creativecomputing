package cc.creativecomputing.kle.elements.motors;

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector3;

public class CC1Motor1ConnectionSetup extends CCMotorSetup{

	protected final CCMotorChannel motor;
	
	public CC1Motor1ConnectionSetup(List<CCMotorChannel> theChannels, CC1Motor1ConnectionBounds theBounds, CCVector3 theCentroid){
		super(theChannels, theCentroid);
		
		motor = _myChannels.get(0);
		
		theBounds.updateBounds(this);
		
	}
	
	private CCVector3 animationPosition(double theX){
		return CCVector3.blend(animationBounds().get(0), animationBounds().get(1), theX);
	}
	
	@Override
	public void setByRelativePosition(double... theValues) {
		double myLength = theValues != null && theValues.length > 0 ? theValues[0] : 0.5f;
		_myElementOffset.set(animationPosition(myLength));
		motor._myAnimatedConnectionPosition = _myElementOffset.clone();
		
	}
	
	@Override
	public void setByRopeLength(double... theValues) {
		double motionValue = theValues[0];
		_myElementOffset = motor.position().add(0,motionValue,0);
		motor._myAnimatedConnectionPosition = _myElementOffset.clone();
	}
	
	@Override
	public void drawRopes(CCGraphics g){
		g.line(motor._myPosition, motor._myAnimatedConnectionPosition); 
	}
	
	@Override
	public void drawElementBounds(CCGraphics g){
//		g.beginShape(CCDrawMode.LINE_LOOP);
//		for(int i = 0; i < 100;i++){
//			double angle = CCMath.blend(0, CCMath.TWO_PI, i / 100f);
//			double x = CCMath.sin(angle) * _myElementRadius * _myPlaneDirection.x + _myElementOffset.x + _myAnimationCenter.x;
//			double y = CCMath.cos(angle) * _myElementRadius + _myElementOffset.y + _myAnimationCenter.y;
//			double z = CCMath.sin(angle) * _myElementRadius * _myPlaneDirection.z + _myElementOffset.z + _myAnimationCenter.z;
//			g.vertex(x,y,z);
//		}
//		g.endShape();
	}
	
	@Override
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
