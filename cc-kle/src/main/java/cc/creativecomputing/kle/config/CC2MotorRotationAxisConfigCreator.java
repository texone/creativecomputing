package cc.creativecomputing.kle.config;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.motors.CC2MotorRotationAxisBounds;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;

public class CC2MotorRotationAxisConfigCreator {
	
	private CCDataElement _mySculptureXML;
	private CCDataElement _myMappingsXML;

	protected List<CCSequenceElement> _myElements = new ArrayList<>();
	
	public CC2MotorRotationAxisConfigCreator(int xRes, int yRes, double XSpace, double ySpace, double center, double amplitude, double radius, int frameRate){
		
		_mySculptureXML = new CCDataElement("sculpture");
		CCDataElement myElementsXML = _mySculptureXML.createChild("elements");
		
		_myMappingsXML = new CCDataElement("mappings");
		
		CCDataElement myMotorMappingXML = _myMappingsXML.createChild("mapping");
		myMotorMappingXML.addAttribute("name", "motors");
		myMotorMappingXML.addAttribute("columns", xRes);
		myMotorMappingXML.addAttribute("rows", yRes);
		myMotorMappingXML.addAttribute("depth", "2");
		myMotorMappingXML.addAttribute("frameRate", frameRate);
		myMotorMappingXML.addAttribute("bits", 16);
		
		CC2MotorRotationAxisBounds myBounds = new CC2MotorRotationAxisBounds();
		myBounds.radius(radius);
		myBounds.center(center);
		myBounds.amplitude(amplitude);
		
		int id = 0;
		
		for(int x = 0; x < xRes; x++){
			for(int y = 0; y < yRes; y++){
				
				int myID = id++;
				List<CCMotorChannel> myMotorChannels = new ArrayList<>();
				double myX = CCMath.map(x, 0, xRes - 1, -xRes / 2 * XSpace, xRes / 2 * XSpace);
				double myY = CCMath.map(y, 0, yRes - 1, -yRes / 2 * ySpace, yRes / 2 * ySpace);
				
				CCVector3 myPulleyPosition = new CCVector3(0, 0, 0);
				CCVector3 myConnectionPosition = new CCVector3(0, 0, 0);
				CCMotorChannel myMotor0 = new CCMotorChannel(myID, myPulleyPosition, myConnectionPosition);
				myMotorChannels.add(myMotor0);
				CCMotorChannel myMotor1 = new CCMotorChannel(myID, myPulleyPosition, myConnectionPosition);
				myMotorChannels.add(myMotor1);
				
				CCMatrix4x4 myTransform = new CCMatrix4x4();
				myTransform.applyTranslationPost(new CCVector3(myX, myY, 0));
				
				CCSequenceElement myElement = new CCSequenceElement(
					myID, 
					myMotorChannels, 
					null,
					myBounds,
					myConnectionPosition,
					myTransform,
					1
				);
				
				myElementsXML.addChild(myElement.toXML());
				
				CCDataElement myChannel0XML = myMotorMappingXML.createChild("channel");
				myChannel0XML.addAttribute("id", myMotor0.id());
				myChannel0XML.addAttribute("column", x);
				myChannel0XML.addAttribute("row", y);
				myChannel0XML.addAttribute("depth", 0);
				myChannel0XML.addAttribute("min", -amplitude);
				myChannel0XML.addAttribute("max", amplitude);
				
				CCDataElement myChannel1XML = myMotorMappingXML.createChild("channel");
				myChannel1XML.addAttribute("id", myMotor1.id());
				myChannel1XML.addAttribute("column", x);
				myChannel1XML.addAttribute("row", y);
				myChannel1XML.addAttribute("depth", 1);
				myChannel1XML.addAttribute("min", 0);
				myChannel1XML.addAttribute("max", radius);
						
				_myElements.add(myElement);
			}
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
		CC2MotorRotationAxisConfigCreator myConfigCreator = new CC2MotorRotationAxisConfigCreator(5, 5, 100, 100, 0, 180, 40, 5);
		myConfigCreator.saveXML();
	}
}
