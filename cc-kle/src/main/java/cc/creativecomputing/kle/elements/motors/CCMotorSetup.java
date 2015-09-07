package cc.creativecomputing.kle.elements.motors;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.elements.CCChannelSetup;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CCMotorSetup extends CCChannelSetup<CCMotorChannel>{

	protected List<CCVector3> _myMotorBounds;
	protected List<CCVector3> _myMotorAnimationBounds;
	protected List<CCVector2> _myMotorAnimation2DBounds;
	
	protected CCVector3 _myAnimationCenter;
	
	protected CCVector3 _myElementOffset;
	
	protected CCVector2 _myElementOffset2D;
	
	protected CCVector3 _myRelativeOffset;
	
	protected double _myRotateY;
	protected double _myRotateZ;
	
	public CCMotorSetup(List<CCMotorChannel> theChannels){
		super(theChannels);
		_myMotorBounds = new ArrayList<CCVector3>();
		_myMotorAnimationBounds = new ArrayList<CCVector3>();
		_myMotorAnimation2DBounds = new ArrayList<CCVector2>();
		_myElementOffset = new CCVector3();
		_myElementOffset2D = new CCVector2();
		_myRelativeOffset = new CCVector3();
		_myAnimationCenter = new CCVector3();
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
	
	public CCVector2 elementOffset2D(){
		return _myElementOffset2D;
	}
	
	public CCVector3 animationCenter(){
		return _myAnimationCenter;
	}
	
	public CCVector3 position(){
		return _myElementOffset.add(_myAnimationCenter);
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
		return myMotorsXML;
	}
}
