package cc.creativecomputing.kle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.effects.CCEffectable;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.kle.lights.CCLightBrightnessSetup;
import cc.creativecomputing.kle.lights.CCLightChannel;
import cc.creativecomputing.kle.lights.CCLightRGBSetup;
import cc.creativecomputing.kle.lights.CCLightRGBWSetup;
import cc.creativecomputing.kle.lights.CCLightSetup;
import cc.creativecomputing.kle.motors.CC1Motor1ConnectionBounds;
import cc.creativecomputing.kle.motors.CC1Motor1ConnectionSetup;
import cc.creativecomputing.kle.motors.CC2Motor1ConnectionBounds;
import cc.creativecomputing.kle.motors.CC2Motor1ConnectionSetup;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionCalculations;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionLinearBounds;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionLinearSetup;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionSetup;
import cc.creativecomputing.kle.motors.CC2MotorRotationAxisBounds;
import cc.creativecomputing.kle.motors.CC2MotorRotationAxisSetup;
import cc.creativecomputing.kle.motors.CCMotorCalculations;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.kle.motors.CCMotorSetup;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;

public class CCKleEffectable extends CCEffectable{
	
	private final List<CCKleChannel> _myChannels = new ArrayList<>();

	private final CCMotorSetup _myMotorSetup;
	private final CCLightSetup _myLightSetup;
	
	private Map<String, String> _myAttributes = new HashMap<>();
	
	public static CCKleEffectable createMotorElement(int theID, List<CCMotorChannel> theMotorChannels,CCMotorCalculations<?> theBounds, CCVector3 theCentroid, double theElementRadius){
		return new CCKleEffectable(theID, theMotorChannels, null, theBounds, theCentroid, new CCMatrix4x4(), theElementRadius);
	}
	
	public static CCKleEffectable createLightElement(int theID, List<CCLightChannel> theLightChannels){
		return new CCKleEffectable(theID, null, theLightChannels, null, null, new CCMatrix4x4(), 0);
	}
	
	public CCKleEffectable(
		int theID, 
		
		List<CCMotorChannel> theMotors,
		List<CCLightChannel> theLights,
		
		CCMotorCalculations<?> theBounds,
		
		CCVector3 theCentroid,
		
		CCMatrix4x4 theTransform,
		
		double theElementRadius
	){
		super(theID);
		_myMotorSetup = setMotors(theMotors, theBounds, theCentroid, theElementRadius);
		_myLightSetup = setLights(theLights);
		_myMatrix = theTransform;
	}
	
	public CCKleEffectable(int theID, CCLightSetup theLightSetup){
		super(theID);
		_myMotorSetup = null;
		_myLightSetup = theLightSetup;
	}
	
	public CCKleEffectable(int theID, CCMotorSetup theMotorSetup){
		super(theID);
		_myMotorSetup = theMotorSetup;
		_myLightSetup = null;
	}
	
	public CCKleEffectable(int theID){
		super(theID);
		_myMotorSetup = null;
		_myLightSetup = null;
	}
	
	public void addAttribute(String theKey, String theAttribute){
		_myAttributes.put(theKey, theAttribute);
	}
	
	private CCMotorSetup setMotors(List<CCMotorChannel> theMotors, CCMotorCalculations<?> theBounds, CCVector3 theCentroid, double theElementRadius){
		if(theMotors == null)return null;
		
		_myChannels.addAll(theMotors);
		
		switch(theBounds.type()){
		case SETUP_2_MOTOR_1_CONNECTION:
			return new CC2Motor1ConnectionSetup(theMotors, (CC2Motor1ConnectionBounds)theBounds, theElementRadius);
		case SETUP_2_MOTOR_2_CONNECTION:
			return new CC2Motor2ConnectionSetup(this, theMotors, (CC2Motor2ConnectionCalculations)theBounds, theCentroid,  theElementRadius);
		case SETUP_2_MOTOR_2_CONNECTION_LINEAR:
			return new CC2Motor2ConnectionLinearSetup(theMotors, (CC2Motor2ConnectionLinearBounds)theBounds,  theElementRadius);
		case SETUP_1_MOTOR_1_CONNECTION:
			return new CC1Motor1ConnectionSetup(theMotors, (CC1Motor1ConnectionBounds)theBounds, theCentroid);
		case SETUP_2_MOTOR_ROTATION_AXIS:
			return new CC2MotorRotationAxisSetup(theMotors, (CC2MotorRotationAxisBounds)theBounds, theElementRadius);
		default:
			return new CCMotorSetup(theMotors, theCentroid);
		}
	}
	
	private CCLightSetup setLights(List<CCLightChannel> theLights){
		if(theLights == null)return null;
			
		_myChannels.addAll(theLights);
		
		switch(theLights.size()){
		case 1:
			return new CCLightBrightnessSetup(theLights);
		case 3:
			return new CCLightRGBSetup(theLights);
		case 4:
			return new CCLightRGBWSetup(theLights);
		default:
			return null;
		}
	}
	
	public CCMotorSetup motorSetup(){
		return _myMotorSetup;
	}
	
	public CCLightSetup lightSetup(){
		return _myLightSetup;
	}
	
	public CCKleChannelSetup<?> setup(CCKleChannelType theChannelType){
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
	
	public List<CCKleChannel> channels(){
		return _myChannels;
	}
	
//	@Override
//	public CCVector3 position() {
//		if(_myMotorSetup == null)return _myLightSetup.position();
//		return _myMotorSetup.position();
//	}
	
	public CCDataElement toXML(){
		CCDataElement myResult = new CCDataElement("element");
		myResult.addAttribute("id", _myID);
		
		if(_myMotorSetup != null)myResult.addChild(_myMotorSetup.toXML());
		if(_myLightSetup != null)myResult.addChild(_myLightSetup.toXML());
		
		CCDataElement myMatrixXML = myResult.createChild("matrix");
		myMatrixXML.addAttribute("m00", _myMatrix.m00);
		myMatrixXML.addAttribute("m01", _myMatrix.m01);
		myMatrixXML.addAttribute("m02", _myMatrix.m02);
		myMatrixXML.addAttribute("m03", _myMatrix.m03);

		myMatrixXML.addAttribute("m10", _myMatrix.m10);
		myMatrixXML.addAttribute("m11", _myMatrix.m11);
		myMatrixXML.addAttribute("m12", _myMatrix.m12);
		myMatrixXML.addAttribute("m13", _myMatrix.m13);

		myMatrixXML.addAttribute("m20", _myMatrix.m20);
		myMatrixXML.addAttribute("m21", _myMatrix.m21);
		myMatrixXML.addAttribute("m22", _myMatrix.m22);
		myMatrixXML.addAttribute("m23", _myMatrix.m23);

		myMatrixXML.addAttribute("m30", _myMatrix.m30);
		myMatrixXML.addAttribute("m31", _myMatrix.m31);
		myMatrixXML.addAttribute("m32", _myMatrix.m32);
		myMatrixXML.addAttribute("m33", _myMatrix.m33);
		
		for(String myKey:_myAttributes.keySet()){
			myResult.addAttribute(myKey, _myAttributes.get(myKey));
		}
		
		CCDataElement myIDSources = myResult.createChild("id_sources");
		for(String myKey:_myIdBasedSources.keySet()){
			if(myKey.equals("id"))continue;
			myIDSources.addAttribute(myKey, _myIdBasedSources.get(myKey));
		}
		
		return myResult;
	}
	
	@Override
	public CCVector3 position() {
		if(_myMotorSetup == null)return super.position();
		return super.position().add(_myMotorSetup.elementOffset());
	}

	@Override
	public CCVector3 normedPosition() {
		if(_myMotorSetup == null)return super.normedPosition();
		return _myMotorSetup.relativeOffset();
	}
}
