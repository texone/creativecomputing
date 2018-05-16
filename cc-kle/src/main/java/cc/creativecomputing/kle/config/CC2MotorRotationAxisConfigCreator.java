package cc.creativecomputing.kle.config;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.lights.CCLightCalculations;
import cc.creativecomputing.kle.motors.CC2MotorRotationAxisBounds;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;

public class CC2MotorRotationAxisConfigCreator {
	
	private CCDataElement _mySculptureXML;
	private CCDataElement _myMappingsXML;

	protected List<CCKleEffectable> _myElements = new ArrayList<>();
	
	public CC2MotorRotationAxisConfigCreator(int res, double space, double center, double amplitude, double radius, int frameRate){
		
		_mySculptureXML = new CCDataElement("sculpture");
		CCDataElement myElementsXML = _mySculptureXML.createChild("elements");
		
		_myMappingsXML = new CCDataElement("mappings");
		
		CCDataElement myMotorMappingXML = _myMappingsXML.createChild("mapping");
		myMotorMappingXML.addAttribute("name", "motors");
		myMotorMappingXML.addAttribute("columns", res);
		myMotorMappingXML.addAttribute("rows", 2);
		myMotorMappingXML.addAttribute("depth", 1);
		myMotorMappingXML.addAttribute("frameRate", frameRate);
		myMotorMappingXML.addAttribute("bits", 16);
		
		CC2MotorRotationAxisBounds myBounds = new CC2MotorRotationAxisBounds();
		myBounds.radius(radius);
		myBounds.center(center);
		myBounds.amplitude(amplitude);
		
		CCLightCalculations myLightCalculations = new CCLightCalculations();
		
		int id = 0;
		
		for(int z = 0; z < res; z++){	
			int myID = id++;
			List<CCMotorChannel> myMotorChannels = new ArrayList<>();
			double myZ = CCMath.map(z, 0, res - 1, -res / 2 * space, res / 2 * space);
				
			CCVector3 myPulleyPosition = new CCVector3(0, 0, 0);
			CCVector3 myConnectionPosition = new CCVector3(0, 0, 0);
			CCMotorChannel myMotor0 = new CCMotorChannel(myID, myPulleyPosition, myConnectionPosition);
			myMotorChannels.add(myMotor0);
			CCMotorChannel myMotor1 = new CCMotorChannel(myID, myPulleyPosition, myConnectionPosition);
			myMotorChannels.add(myMotor1);
				
			CCMatrix4x4 myTransform = new CCMatrix4x4();
			myTransform.applyTranslationPost(new CCVector3(0, 0, myZ));
				
			CCKleEffectable myElement = new CCKleEffectable(
				myID, 
				myMotorChannels, 
				null,
				myBounds,
				myLightCalculations,
				myConnectionPosition,
				myTransform,
				1
			);
				
			myElementsXML.addChild(myElement.toXML());
				
			CCDataElement myChannel0XML = myMotorMappingXML.createChild("channel");
			myChannel0XML.addAttribute("id", myMotor0.id());
			myChannel0XML.addAttribute("column", z);
			myChannel0XML.addAttribute("row", 0);
			myChannel0XML.addAttribute("depth", 0);
			myChannel0XML.addAttribute("min", -amplitude);
			myChannel0XML.addAttribute("max", amplitude);
				
			CCDataElement myChannel1XML = myMotorMappingXML.createChild("channel");
			myChannel1XML.addAttribute("id", myMotor1.id());
			myChannel1XML.addAttribute("column", z);
			myChannel1XML.addAttribute("row", 1);
			myChannel1XML.addAttribute("depth", 0);
			myChannel1XML.addAttribute("min", 0);
			myChannel1XML.addAttribute("max", radius);
						
			_myElements.add(myElement);
		}
	}
	
	public void saveXML(){
		saveXML("config");
	}
	
	public void saveXML(String folder){
		CCLog.info(CCNIOUtil.dataPath(folder + "/sculpture.xml"));
		CCLog.info(CCNIOUtil.dataPath(folder + "/mapping.xml"));
		CCXMLIO.saveXMLElement(_mySculptureXML, CCNIOUtil.dataPath(folder + "/sculpture.xml"));
		CCXMLIO.saveXMLElement(_myMappingsXML, CCNIOUtil.dataPath(folder + "/mapping.xml"));
	}
	
	public static void main(String[] args) {
		CC2MotorRotationAxisConfigCreator myConfigCreator = new CC2MotorRotationAxisConfigCreator(5, 100, 0, 180, 40, 5);
		myConfigCreator.saveXML();
	}
}
