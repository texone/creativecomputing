package cc.creativecomputing.kle.elements.motors;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.analyze.CCMotionData;
import cc.creativecomputing.kle.elements.CCChannelSetup;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCMotorSetup extends CCChannelSetup<CCMotorChannel>{

	protected List<CCVector3> _myMotorBounds;
	protected List<CCVector3> _myMotorAnimationBounds;
	protected List<CCVector2> _myMotorAnimation2DBounds;
	
	protected CCVector3 _myElementOffset;
	
	protected CCVector3 _myCentroid;
	
	protected CCVector3 _myRelativeOffset;
	
	protected double _myRotateY;
	protected double _myRotateZ;
	
	public CCMotorSetup(List<CCMotorChannel> theChannels, CCVector3 theCentroid){
		super(theChannels);
		_myCentroid = theCentroid;
		_myMotorBounds = new ArrayList<CCVector3>();
		_myMotorAnimationBounds = new ArrayList<CCVector3>();
		_myMotorAnimation2DBounds = new ArrayList<CCVector2>();
		_myElementOffset = new CCVector3();
		_myRelativeOffset = new CCVector3();
	}
	
	public CCVector3 relativeOffset(){
		return _myRelativeOffset;
	}
	
	public List<CCVector3> bounds(){
		return _myMotorBounds;
	}
	
	public List<CCVector3> animationBounds(){
		return _myMotorAnimationBounds;
	}
	
	public List<CCVector2> animation2DBounds(){
		return _myMotorAnimation2DBounds;
	}
	
	public void setByRelativePosition(double...theValues){
		
	}
	
	public void setByPosition(double...theValues){
		
	}
	
	public void setByRopeLength(double...theValues){
		
	}
	
	public void rotateY(double theRotateY){
		_myRotateY = theRotateY;
	}
	
	public double rotateY(){
		return _myRotateY;
	}
	
	public void rotateZ(double theRotateZ){
		_myRotateZ = theRotateZ;
	}
	
	public double rotateZ(){
		return _myRotateZ;
	}
	
	public void drawRopes(CCGraphics g){
	}
	
	public void drawElementBounds(CCGraphics g){
		
	}
	
	public void drawRangeBounds(CCGraphics g){
	}
	
	public CCVector3 elementOffset(){
		return _myElementOffset;
	}
	
	public CCVector3 position(){
		return _myElementOffset;
	}
	
	public CCMotionData _myLastData = null;
	
	public void update(double theDeltaTime){
		if(_myLastData == null){
			_myLastData = new CCMotionData(position(), 0, 0, 0, 0, theDeltaTime);
		}
		
		double myVelocity = (position().distance(_myLastData.position)) / theDeltaTime;
		double myAcceleration = (myVelocity - _myLastData.velocity) / theDeltaTime;
		double myJerk = (myAcceleration - _myLastData.acceleration) / theDeltaTime;
		_myLastData = new CCMotionData(position(), 0, myVelocity, myAcceleration, myJerk, theDeltaTime);
	}
	
	public CCMotionData motionData(){
		if(_myLastData == null){
			_myLastData = new CCMotionData(position(), 0, 0, 0, 0, 0);
		}
		return _myLastData;
	}
	
	private void addPoint(CCXMLElement theParentXML, CCVector3 thePoint){
		CCXMLElement myPointXML = theParentXML.createChild("point");
		myPointXML.addAttribute("x", thePoint.x);
		myPointXML.addAttribute("y", thePoint.y);
		myPointXML.addAttribute("z", thePoint.z);
	}
	
	public CCXMLElement toXML(){
		CCXMLElement myMotorsXML = new CCXMLElement("motors");
		for(CCMotorChannel myChannel:_myChannels){
			myMotorsXML.addChild(myChannel.toXML());
		}
		CCXMLElement myBoundsXML = myMotorsXML.createChild("bounds");
		for(CCVector3 myPoint:_myMotorBounds){
			addPoint(myBoundsXML, myPoint);
		}
		if(_myCentroid != null){
			CCXMLElement myCentroidXML = myMotorsXML.createChild("centroid");
			myCentroidXML.addAttribute("x", _myCentroid.x);
			myCentroidXML.addAttribute("y", _myCentroid.y);
			myCentroidXML.addAttribute("z", _myCentroid.z);
		}
		return myMotorsXML;
	}
}
