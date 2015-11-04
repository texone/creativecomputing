package cc.creativecomputing.kle.elements;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.elements.lights.CCLightBrightnessSetup;
import cc.creativecomputing.kle.elements.lights.CCLightChannel;
import cc.creativecomputing.kle.elements.lights.CCLightRGBSetup;
import cc.creativecomputing.kle.elements.lights.CCLightSetup;
import cc.creativecomputing.kle.elements.motors.CC2Motor1ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC2Motor1ConnectionSetup;
import cc.creativecomputing.kle.elements.motors.CCMotorBounds;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.kle.elements.motors.CCMotorSetup;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CC2Motor2ConnectionSetup;

public class CCSequenceElement  {

	private final int _myID;
	
	private final List<CCSequenceChannel> _myChannels = new ArrayList<>();

	private final CCMotorSetup _myMotorSetup;
	private final CCLightSetup _myLightSetup;
	
	private double _myIDBlend;
	private double _myGroupIDBlend;
	private double _myGroupBlend;
	private int _myGroup;

	public double _myXBlend;
	public double _myYBlend;
	
	public CCSequenceElement(
		int theID, 
		
		List<CCMotorChannel> theMotors,
		List<CCLightChannel> theLights,
		
		CCMotorBounds theBounds,
		
		double theElementRadius
	){
		_myID = theID;
		_myMotorSetup = setMotors(theMotors, theBounds, theElementRadius);
		_myLightSetup = setLights(theLights);
	}
	
	public CCSequenceElement(int theID, List<CCLightChannel> theLights){
		this(theID, null, theLights, null, 0);
	}
	
	public CCSequenceElement(int theID, CCLightSetup theLightSetup){
		_myID = theID;
		_myMotorSetup = null;
		_myLightSetup = theLightSetup;
	}
	
	private CCMotorSetup setMotors(List<CCMotorChannel> theMotors, CCMotorBounds theBounds, double theElementRadius){
		if(theMotors == null)return new CCMotorSetup(theMotors);
		_myChannels.addAll(theMotors);
		
		switch(theMotors.size()){
		case 2:
			if(theMotors.get(0).connectionPosition().equals(theMotors.get(1).connectionPosition())){
				return new CC2Motor1ConnectionSetup(theMotors, (CC2Motor1ConnectionBounds)theBounds, theElementRadius);
			}else{
				return new CC2Motor2ConnectionSetup(theMotors, (CC2Motor2ConnectionBounds)theBounds, theElementRadius);
			}
		default:
			return new CCMotorSetup(theMotors);
		}
	}
	
	private CCLightSetup setLights(List<CCLightChannel> theLights){
		if(theLights == null)
		_myChannels.addAll(theLights);
		switch(theLights.size()){
		case 1:
			return new CCLightBrightnessSetup(theLights);
		case 3:
			return new CCLightRGBSetup(theLights);
		default:
			return new CCLightSetup(theLights);
		}
	}
	
	public double groupIDBlend(){
		return _myGroupIDBlend;
	}
	
	public void groupIDBlend(double theGroupIDBlend){
		_myGroupIDBlend = theGroupIDBlend;
	}
	
	public double groupBlend(){
		return _myGroupBlend;
	}
	
	public void groupBlend(double theGroupBlend){
		_myGroupBlend = theGroupBlend;
	}
	
	public int group(){
		return _myGroup;
	}
	
	public void group(int theGroup){
		_myGroup = theGroup;
	}
	
	public double xBlend(){
		return _myXBlend;
	}
	
	public void xBlend(double theXBlend){
		_myXBlend = theXBlend;
	}
	
	public double yBlend(){
		return _myYBlend;
	}
	
	public void yBlend(double theYBlend){
		_myYBlend = theYBlend;
	}
	
	public int id(){
		return _myID;
	}
	
	public double idBlend(){
		return _myIDBlend;
	}
	
	public void idBlend(double theIDBlend){
		_myIDBlend = theIDBlend;
	}
	
	public CCMotorSetup motorSetup(){
		return _myMotorSetup;
	}
	
	public CCLightSetup lightSetup(){
		return _myLightSetup;
	}
	
	public CCChannelSetup<?> setup(CCKleChannelType theChannelType){
		switch(theChannelType){
		case LIGHTS:
			return _myLightSetup;
		case MOTORS:
			return _myMotorSetup;
		}
		return _myMotorSetup;
	}
	
	public void update(double theDeltaTime){
		if(_myMotorSetup != null)_myMotorSetup.update(theDeltaTime);
	}
	
	public List<CCSequenceChannel> channels(){
		return _myChannels;
	}
	
	public CCXMLElement toXML(){
		CCXMLElement myResult = new CCXMLElement("element");
		myResult.addAttribute("id", _myID);
		myResult.addChild(_myMotorSetup.toXML());
		myResult.addChild(_myLightSetup.toXML());
		
		return myResult;
	}
}
