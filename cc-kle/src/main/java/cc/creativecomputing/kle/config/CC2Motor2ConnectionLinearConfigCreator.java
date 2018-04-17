package cc.creativecomputing.kle.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.lights.CCLightChannel;
import cc.creativecomputing.kle.motors.CC1Motor1ConnectionBounds;
import cc.creativecomputing.kle.motors.CC2Motor2ConnectionLinearBounds;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

public class CC2Motor2ConnectionLinearConfigCreator {
	
	private CCDataElement _mySculptureXML;
	private CCDataElement _myMappingsXML;

	protected List<CCKleEffectable> _myElements = new ArrayList<>();
	
	public CC2Motor2ConnectionLinearConfigCreator(int theElements, double theRadius, double theConnectionDistance, double theOffset, double top, double bottom, int frameRate){
		
		_mySculptureXML = new CCDataElement("sculpture");
		CCDataElement myElementsXML = _mySculptureXML.createChild("elements");
		
		_myMappingsXML = new CCDataElement("mappings");
		
		CCDataElement myMotorMappingXML = _myMappingsXML.createChild("mapping");
		myMotorMappingXML.addAttribute("name", "motors");
		myMotorMappingXML.addAttribute("columns", theElements);
		myMotorMappingXML.addAttribute("rows", 2);
		myMotorMappingXML.addAttribute("depth", "1");
		myMotorMappingXML.addAttribute("frameRate", frameRate);
		myMotorMappingXML.addAttribute("bits", 16);
		
		CCDataElement myLightMappingXML = _myMappingsXML.createChild("mapping");
		myLightMappingXML.addAttribute("name", "lights");
		myLightMappingXML.addAttribute("columns", theElements);
		myLightMappingXML.addAttribute("rows", 1);
		myLightMappingXML.addAttribute("depth", 3);
		myLightMappingXML.addAttribute("frameRate", "20");
		myLightMappingXML.addAttribute("bits", 16);
		
		CC2Motor2ConnectionLinearBounds myBounds = new CC2Motor2ConnectionLinearBounds();
		myBounds.topDistance(top);
		myBounds.bottomDistance(bottom);
		
		
		double height = bottom - top;
		
		for(int i = 0; i < theElements;i++) {
			double myAngle = CCMath.map(i, 0, theElements, 0, CCMath.TWO_PI);
			CCVector2 pos = CCVector2.circlePoint(myAngle, theRadius + theConnectionDistance / 2, 0, 0);
			List<CCMotorChannel> myMotorChannels = new ArrayList<>();
			
			CCVector3 myConnectionPosition0 = new CCVector3(0, 0, 0);
			CCVector3 myConnectionPosition1 = new CCVector3(90, 0, 0);
			CCVector3 myPulleyPosition0 = new CCVector3(0, -162.5, 0);
			CCVector3 myPulleyPosition1 = new CCVector3(90, -162.5, 0);
			CCMotorChannel myMotor0 = new CCMotorChannel(i * 2, myPulleyPosition0, myConnectionPosition0);
			myMotor0.valueOffset(theOffset);
			CCMotorChannel myMotor1 = new CCMotorChannel(i * 2 + 1, myPulleyPosition1, myConnectionPosition1);
			myMotor1.valueOffset(theOffset);
			myMotorChannels.add(myMotor0);
			myMotorChannels.add(myMotor1);
			
			CCMatrix4x4 myTransform = new CCMatrix4x4();
			myTransform.applyTranslationPost(new CCVector3(pos.x, 0, pos.y));
			myTransform.applyRotationY(-myAngle);
			
			CCVector3 myCentroid = new CCVector3(77, -162.5, 0);
			
			List<CCLightChannel> myLightChannels = new ArrayList<>();
			for(int j = 0; j < 3;j++){
				myLightChannels.add(new CCLightChannel(i * 3 + j));
			}
			
			CCKleEffectable myElement = new CCKleEffectable(
				i, 
				myMotorChannels, 
				myLightChannels,
				myBounds,
				myCentroid,
				myTransform,
				1
			);

			double min = top;
			double max = bottom;
			
			myElementsXML.addChild(myElement.toXML());
			
			CCDataElement myChannel0XML = myMotorMappingXML.createChild("channel");
			myChannel0XML.addAttribute("id", myMotor0.id());
			myChannel0XML.addAttribute("column", i);
			myChannel0XML.addAttribute("row", 0);
			myChannel0XML.addAttribute("depth", 0);
			myChannel0XML.addAttribute("min", min);
			myChannel0XML.addAttribute("max", max);

			
			CCDataElement myChannel1XML = myMotorMappingXML.createChild("channel");
			myChannel1XML.addAttribute("id", myMotor1.id());
			myChannel1XML.addAttribute("column", i);
			myChannel1XML.addAttribute("row", 1);
			myChannel1XML.addAttribute("depth", 0);
			myChannel1XML.addAttribute("min", min);
			myChannel1XML.addAttribute("max", max);
			
			for(int j = 0; j < 3;j++){
				CCDataElement myLightChannelXML = myLightMappingXML.createChild("channel");
				myLightChannelXML.addAttribute("id", i * 3 + j);
				myLightChannelXML.addAttribute("column", i);
				myLightChannelXML.addAttribute("row", 0);
				myLightChannelXML.addAttribute("depth", j);
				myLightChannelXML.addAttribute("min", 0);
				myLightChannelXML.addAttribute("max", 1);
			}
					
			_myElements.add(myElement);
		}
		
		
	}
	
	public void saveXML(){
		saveXML("config");
	}
	
	public void saveXML(String folder){
		CCXMLIO.saveXMLElement(_mySculptureXML, CCNIOUtil.dataPath(folder + "/sculpture.xml"));
		CCXMLIO.saveXMLElement(_myMappingsXML, CCNIOUtil.dataPath(folder + "/mapping.xml"));
	}
	
	public static void main(String[] args) {
//		CC2Motor2ConnectionLinearConfigCreator myConfigCreator = new CC2Motor2ConnectionLinearConfigCreator(12, 14, 18, 18, 380);
//		myConfigCreator.saveXML();
	}
}
