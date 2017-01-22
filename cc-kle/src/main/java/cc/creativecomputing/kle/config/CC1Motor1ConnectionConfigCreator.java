package cc.creativecomputing.kle.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.kle.elements.motors.CC1Motor1ConnectionBounds;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.model.obj.CCAABB;

public class CC1Motor1ConnectionConfigCreator {
	
	protected CCAABB _myAABB;
	
	private CCXMLElement _mySculptureXML;
	private CCXMLElement _myMappingsXML;

	protected List<CCSequenceElement> _myElements = new ArrayList<>();
	
	public CC1Motor1ConnectionConfigCreator(int xRes, int zRes, double XSpace, double zSpace, double height){
		
		_mySculptureXML = new CCXMLElement("sculpture");
		CCXMLElement myElementsXML = _mySculptureXML.createChild("elements");
		
		_myMappingsXML = new CCXMLElement("mappings");
		
		CCXMLElement myMotorMappingXML = _myMappingsXML.createChild("mapping");
		myMotorMappingXML.addAttribute("name", "motors");
		myMotorMappingXML.addAttribute("columns", xRes);
		myMotorMappingXML.addAttribute("rows", zRes);
		myMotorMappingXML.addAttribute("depth", "1");
		myMotorMappingXML.addAttribute("frameRate", "5");
		myMotorMappingXML.addAttribute("bits", 16);
		
		CC1Motor1ConnectionBounds myBounds = new CC1Motor1ConnectionBounds();
		myBounds.topDistance(0);
		myBounds.bottomDistance(height);
		
		int id = 0;
		
		for(int x = 0; x < xRes; x++){
			for(int z = 0; z < zRes; z++){
				
				int myID = id++;
				List<CCMotorChannel> myMotorChannels = new ArrayList<>();
				double myX = CCMath.map(x, 0, xRes - 1, -xRes / 2 * XSpace, xRes / 2 * XSpace);
				double myZ = CCMath.map(z, 0, zRes - 1, -zRes / 2 * zSpace, zRes / 2 * zSpace);
				
				CCVector3 myPulleyPosition = new CCVector3(0, 0, 0);
				CCVector3 myConnectionPosition = new CCVector3(0, height / 2, 0);
				CCMotorChannel myMotor = new CCMotorChannel(myID, myPulleyPosition, myConnectionPosition);
				myMotorChannels.add(myMotor);
				
				CCMatrix4x4 myTransform = new CCMatrix4x4();
				myTransform.applyTranslationPost(new CCVector3(myX, 0, myZ));
				
				CCSequenceElement myElement = new CCSequenceElement(
					myID, 
					myMotorChannels, 
					null,
					myBounds,
					myConnectionPosition,
					myTransform,
					1
				);

				double min = 0;
				double max = height;
				
				myElementsXML.addChild(myElement.toXML());
				
				CCXMLElement myChannel0XML = myMotorMappingXML.createChild("channel");
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
	
	
	public void saveXML(){
		CCXMLIO.saveXMLElement(_mySculptureXML, CCNIOUtil.dataPath("config/sculpture.xml"));
		CCXMLIO.saveXMLElement(_myMappingsXML, CCNIOUtil.dataPath("config/mapping.xml"));
	}
	
	protected CCVector3 lineToVector(String theVector) {
		String[] myCoords = theVector.split("\t");
		return new CCVector3 (Float.parseFloat(myCoords[1]), Float.parseFloat(myCoords[2]), -Float.parseFloat(myCoords[3]));
	}
	
	protected List<CCVector3> loadVectorList(Path theDocument) {
		List<CCVector3> myVectors = new ArrayList<>();
		for (String myLine : CCNIOUtil.loadString(theDocument).split("\\n")) {
			try {
				myVectors.add(lineToVector(myLine));
			} catch (Exception e) {
//				e.printStackTrace();
			}
		}
		return myVectors;
	}
	
	public static void main(String[] args) {
		CC1Motor1ConnectionConfigCreator myConfigCreator = new CC1Motor1ConnectionConfigCreator(12, 14, 18, 18, 380);
		myConfigCreator.saveXML();
	}
}
