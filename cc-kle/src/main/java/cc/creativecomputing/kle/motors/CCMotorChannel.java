package cc.creativecomputing.kle.motors;

import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.kle.CCKleChannel;
import cc.creativecomputing.math.CCVector3;

public class CCMotorChannel extends CCKleChannel{
	
	CCVector3 _myPosition;
	CCVector3 _myConnectionPosition;
	protected CCVector3 _myAnimatedConnectionPosition;
	
	private double _myValueOffset = 0;
	
	public CCMotorChannel(CCDataElement theMotorXML){
		super( theMotorXML.intAttribute("id"));
		
		_myValueOffset = theMotorXML.doubleAttribute("offset", 0);
		
		CCDataElement myMotorPositionXML = theMotorXML.child("position");
		_myPosition = new CCVector3(
			myMotorPositionXML.doubleAttribute("x"),
			myMotorPositionXML.doubleAttribute("y"),
			myMotorPositionXML.doubleAttribute("z")
		);
		
		CCDataElement myElementConnectionXML = theMotorXML.child("connectionPosition");
		_myConnectionPosition = new CCVector3(
			myElementConnectionXML.doubleAttribute("x"),
			myElementConnectionXML.doubleAttribute("y"),
			myElementConnectionXML.doubleAttribute("z")
		);
		_myAnimatedConnectionPosition = new CCVector3(_myConnectionPosition);
	}
	
	public CCMotorChannel(int theID, CCVector3 theMotorPosition, CCVector3 theElementConnection){
		super(theID);
		_myPosition = theMotorPosition;
		_myConnectionPosition = theElementConnection;
		_myAnimatedConnectionPosition = new CCVector3(_myConnectionPosition);
	}
	
	public double valueOffset() {
		return _myValueOffset;
	}
	
	public void valueOffset(double theOffset) {
		_myValueOffset = theOffset;
	}
	
	public CCVector3 position(){
		return _myPosition;
	}
	
	public CCVector3 connectionPosition(){
		return _myAnimatedConnectionPosition;
	}
	
	@Override
	public double value() {
		return _myPosition.distance(_myAnimatedConnectionPosition) - _myValueOffset;
	}
	
	@Override
	public CCDataElement toXML(){
		CCDataElement myMotorXML = new CCDataElement("motor");
		myMotorXML.addAttribute("id", id());
		myMotorXML.addAttribute("offset", _myValueOffset);
		CCDataElement myMotorPositionXML = myMotorXML.createChild("position");
		myMotorPositionXML.addAttribute("x", _myPosition.x);
		myMotorPositionXML.addAttribute("y", _myPosition.y);
		myMotorPositionXML.addAttribute("z", _myPosition.z);
		CCDataElement myElementConnectionXML = myMotorXML.createChild("connectionPosition");
		myElementConnectionXML.addAttribute("x", _myConnectionPosition.x);
		myElementConnectionXML.addAttribute("y", _myConnectionPosition.y);
		myElementConnectionXML.addAttribute("z", _myConnectionPosition.z);
		return myMotorXML;
	}
}