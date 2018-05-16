package cc.creativecomputing.kle.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.lights.CCLightCalculations;
import cc.creativecomputing.kle.motors.CC1Motor1ConnectionBounds;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;

public class CC1Motor1ConnectionConfigCreator {
	
	private CCDataElement _mySculptureXML;
	private CCDataElement _myMappingsXML;

	protected List<CCKleEffectable> _myElements = new ArrayList<>();
	
	public CC1Motor1ConnectionConfigCreator(int xRes, int zRes, double XSpace, double zSpace, double top, double bottom, int frameRate){
		
		_mySculptureXML = new CCDataElement("sculpture");
		CCDataElement myElementsXML = _mySculptureXML.createChild("elements");
		
		_myMappingsXML = new CCDataElement("mappings");
		
		CCDataElement myMotorMappingXML = _myMappingsXML.createChild("mapping");
		myMotorMappingXML.addAttribute("name", "motors");
		myMotorMappingXML.addAttribute("columns", xRes);
		myMotorMappingXML.addAttribute("rows", zRes);
		myMotorMappingXML.addAttribute("depth", "1");
		myMotorMappingXML.addAttribute("frameRate", frameRate);
		myMotorMappingXML.addAttribute("bits", 16);
		
		CC1Motor1ConnectionBounds myBounds = new CC1Motor1ConnectionBounds();
		myBounds.topDistance(top);
		myBounds.bottomDistance(bottom);

		CCLightCalculations myLightCalculations = new CCLightCalculations();
		
		int id = 0;
		
		double height = bottom - top;
		
		for(int x = 0; x < xRes; x++){
			for(int z = 0; z < zRes; z++){
				
				int myID = id++;
				List<CCMotorChannel> myMotorChannels = new ArrayList<>();
				double myX = CCMath.map(x, 0, xRes - 1, -xRes / 2 * XSpace, xRes / 2 * XSpace);
				double myZ = CCMath.map(z, 0, zRes - 1, -zRes / 2 * zSpace, zRes / 2 * zSpace);
				
				CCVector3 myPulleyPosition = new CCVector3(0, 0, 0);
				CCVector3 myConnectionPosition = new CCVector3(0, top + height / 2, 0);
				CCMotorChannel myMotor = new CCMotorChannel(myID, myPulleyPosition, myConnectionPosition);
				myMotorChannels.add(myMotor);
				
				CCMatrix4x4 myTransform = new CCMatrix4x4();
				myTransform.applyTranslationPost(new CCVector3(myX, 0, myZ));
				
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

				double min = top;
				double max = bottom;
				
				myElementsXML.addChild(myElement.toXML());
				
				CCDataElement myChannel0XML = myMotorMappingXML.createChild("channel");
				myChannel0XML.addAttribute("id", myMotor.id());
				myChannel0XML.addAttribute("column", x);
				myChannel0XML.addAttribute("row", z);
				myChannel0XML.addAttribute("depth", 0);
				myChannel0XML.addAttribute("min", min);
				myChannel0XML.addAttribute("max", max);
						
				_myElements.add(myElement);
			}
		}
		
	}
	
	public CC1Motor1ConnectionConfigCreator(int xRes, int zRes, double XSpace, double zSpace, double height){
		this(xRes, zRes, XSpace, zSpace, 0, height, 5);
	}
	
	public void saveXML(){
		saveXML("config");
	}
	
	public void saveXML(String folder){
		CCXMLIO.saveXMLElement(_mySculptureXML, CCNIOUtil.dataPath(folder + "/sculpture.xml"));
		CCXMLIO.saveXMLElement(_myMappingsXML, CCNIOUtil.dataPath(folder + "/mapping.xml"));
	}
	
	public static void main(String[] args) {
		CC1Motor1ConnectionConfigCreator myConfigCreator = new CC1Motor1ConnectionConfigCreator(12, 14, 18, 18, 380);
		myConfigCreator.saveXML();
	}
}
