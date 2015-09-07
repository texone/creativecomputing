package cc.creativecomputing.kle.elements.motors;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.elements.CCSequenceChannel;
import cc.creativecomputing.math.CCVector3;

public class CCMotorChannel extends CCSequenceChannel{
	
	CCVector3 _myPosition;
	CCVector3 _myConnectionPosition;
	protected CCVector3 _myAnimatedConnectionPosition;
	
	public CCMotorChannel(CCXMLElement theMotorXML){
		super( theMotorXML.intAttribute("id"));
		
		CCXMLElement myMotorPositionXML = theMotorXML.child("position");
		_myPosition = new CCVector3(
			myMotorPositionXML.doubleAttribute("x"),
			myMotorPositionXML.doubleAttribute("y"),
			myMotorPositionXML.doubleAttribute("z")
		);
		
		CCXMLElement myElementConnectionXML = theMotorXML.child("connectionPosition");
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
	}
	
	public CCVector3 position(){
		return _myPosition;
	}
	
	public CCVector3 connectionPosition(){
		return _myAnimatedConnectionPosition;
	}
	
	@Override
	public double value() {
		return _myPosition.distance(_myAnimatedConnectionPosition);
	}
	
	@Override
	public CCXMLElement toXML(){
		CCXMLElement myMotorXML = new CCXMLElement("motor");
		myMotorXML.addAttribute("id", id());
		CCXMLElement myMotorPositionXML = myMotorXML.createChild("position");
		myMotorPositionXML.addAttribute("x", _myPosition.x);
		myMotorPositionXML.addAttribute("y", _myPosition.y);
		myMotorPositionXML.addAttribute("z", _myPosition.z);
		CCXMLElement myElementConnectionXML = myMotorXML.createChild("connectionPosition");
		myElementConnectionXML.addAttribute("x", _myConnectionPosition.x);
		myElementConnectionXML.addAttribute("y", _myConnectionPosition.y);
		myElementConnectionXML.addAttribute("z", _myConnectionPosition.z);
		return myMotorXML;
	}
}