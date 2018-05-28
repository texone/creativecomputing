package cc.creativecomputing.demo.kle.svgled;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.kle.lights.CCLightChannel;
import cc.creativecomputing.kle.motors.CC1Motor1ConnectionBounds;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCBezierSpline;
import cc.creativecomputing.model.svg.CCSVGDocument;
import cc.creativecomputing.model.svg.CCSVGElement;
import cc.creativecomputing.model.svg.CCSVGGroup;
import cc.creativecomputing.model.svg.CCSVGIO;

public class CCBrightnessLightConfigCreator {
	
	private CCDataElement _mySculptureXML;
	private CCDataElement _myMappingsXML;

	protected List<CCKleEffectable> _myElements = new ArrayList<>();
	
	public CCBrightnessLightConfigCreator(){
		
		CCSVGDocument myDocument = CCSVGIO.newSVG(CCNIOUtil.dataPath("demo/leds.svg"));
		
		int myElements = 0;
		List<CCLedGroup> myGroups = new ArrayList<>();
		for(CCSVGElement myElement:myDocument){
			CCLedGroup myGroup = new CCLedGroup((CCSVGGroup)myElement);
			myGroups.add(myGroup);
			myElements += myGroup.size();
		}
		
		_mySculptureXML = new CCDataElement("sculpture");
		CCDataElement myElementsXML = _mySculptureXML.createChild("elements");
		
		_myMappingsXML = new CCDataElement("mappings");
		
		CCDataElement myLightMappingXML = _myMappingsXML.createChild("mapping");
		myLightMappingXML.addAttribute("name", "lights");
		myLightMappingXML.addAttribute("columns", myElements);
		myLightMappingXML.addAttribute("rows", "1");
		myLightMappingXML.addAttribute("depth", "1");
		myLightMappingXML.addAttribute("frameRate", 30);
		myLightMappingXML.addAttribute("bits", 16);
		
		int myID = 0;
		int myGroupID = 0;
		for(CCLedGroup myGroup:myGroups) {
			int myGroupSubID = 0;
			for(CCLed myLed:myGroup) {
				double myX = myLed.center.x;
				double myY = myLed.center.y;
				List<CCLightChannel> myLightChannels = new ArrayList<>();
				CCLightChannel myLight = new CCLightChannel(myID);
				myLightChannels.add(myLight);
				
				CCMatrix4x4 myTransform = new CCMatrix4x4();
				myTransform.applyTranslationPost(new CCVector3(myX, myY, 0));
				
				CCKleEffectable myElement = new CCKleEffectable(
					myID, 
					null, 
					myLightChannels,
					null,
					null,
					null,
					myTransform,
					1
				);
				myElement.lightSetup().position().set(myX, myY, 0);
				myElement.addIdBasedSource("group", myGroupID);
				myElement.addIdBasedSource("group_id", myGroupSubID);
				myElement.addRelativeSource("led10", myLed.type == CCLedType.LENS_10 ? 1 : 0);
				myElement.addRelativeSource("led14", myLed.type == CCLedType.LENS_14_8 ? 1 : 0);
				myElement.addRelativeSource("led20", myLed.type == CCLedType.LENS_20 ? 1 : 0);
				
				myElementsXML.addChild(myElement.toXML());
				
				CCDataElement myChannelXML = myLightMappingXML.createChild("channel");
				myChannelXML.addAttribute("id", myID);
				myChannelXML.addAttribute("dmx_interface", "");
				myChannelXML.addAttribute("dmx_universe", 0);
				myChannelXML.addAttribute("dmx_channel", myLed.channel);
				myChannelXML.addAttribute("column", myID);
				myChannelXML.addAttribute("row", 0);
				myChannelXML.addAttribute("depth", 0);
				myChannelXML.addAttribute("min", 0);
				myChannelXML.addAttribute("max", 255);
				myID++;
				myGroupSubID++;
			}
			myGroupID++;
		}
		
		
	}
	
	public void saveXML(){
		saveXML("config_leds");
	}
	
	public void saveXML(String folder){
		CCXMLIO.saveXMLElement(_mySculptureXML, CCNIOUtil.dataPath(folder + "/sculpture.xml"));
		CCXMLIO.saveXMLElement(_myMappingsXML, CCNIOUtil.dataPath(folder + "/mapping.xml"));
	}
	
	public static void main(String[] args) {
		CCBrightnessLightConfigCreator myConfigCreator = new CCBrightnessLightConfigCreator();
		myConfigCreator.saveXML();
	}
}
