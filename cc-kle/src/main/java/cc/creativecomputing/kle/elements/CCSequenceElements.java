package cc.creativecomputing.kle.elements;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.elements.lights.CCLightChannel;
import cc.creativecomputing.kle.elements.motors.CCMotorBounds;
import cc.creativecomputing.kle.elements.motors.CCMotorChannel;

public class CCSequenceElements extends ArrayList<CCSequenceElement>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1200033076720174639L;

	public static CCXMLElement mapping(CCSequenceElements theElements){
		CCXMLElement myMappingsXML = new CCXMLElement("mappings");
		for(CCKleChannelType myKey:theElements.mappings().keySet()){
			CCSequenceMapping myMapping = theElements.mappings().get(myKey);
			myMappingsXML.addChild(myMapping.toXML());
		}
		return myMappingsXML;
	}
	
	public static CCXMLElement sculpture(CCSequenceElements theElements){
		CCXMLElement myResult = new CCXMLElement("sculpture");
		CCXMLElement myElementsXML = myResult.createChild("elements");
		
		for(CCSequenceElement myElement:theElements){
			myElementsXML.addChild(myElement.toXML());
		}
		
		return myResult;
	}
	
	private Map<CCKleChannelType,CCSequenceMapping<?>> _myMappings = new HashMap<>();
	
	public CCSequenceElements(Path theKlePath, CCMotorBounds theMotorBounds, float theElementRadius){
		this(
			CCXMLIO.createXMLElement(theKlePath.resolve("mapping.xml")),
			CCXMLIO.createXMLElement(theKlePath.resolve("sculpture.xml")),
			theMotorBounds,
			theElementRadius
		);
	}
	
	public CCSequenceElements(CCXMLElement theMappingsXML, CCXMLElement mySculptureXML, CCMotorBounds theMotorBounds, float theElementRadius){
		super();
		for(CCXMLElement myMappingXML:theMappingsXML){
			_myMappings.put(CCKleChannelType.valueOf(myMappingXML.attribute("name").toUpperCase()), new CCSequenceMapping(myMappingXML));
		}
		
		CCXMLElement myElementsXML = mySculptureXML.child("elements");
		for(CCXMLElement myElementXML:myElementsXML){
			int myID = myElementXML.intAttribute("id");
			
			
			List<CCMotorChannel> myMotorChannels = new ArrayList<>();
			List<CCLightChannel> myLightChannels = new ArrayList<>();
			
			for(CCKleChannelType myKey:_myMappings.keySet()){
				CCSequenceMapping myMapping = _myMappings.get(myKey);
				switch(myKey){
				case MOTORS:
					CCXMLElement myMotorsXML = myElementXML.child("motors");
					for(CCXMLElement myMotorXML:myMotorsXML.children("motor")){
						CCMotorChannel myMotorChannel = new CCMotorChannel(myMotorXML);
						
						myMotorChannels.add(myMotorChannel);
						myMapping.add(myMotorChannel);
					}
					break;
				case LIGHTS:
					CCXMLElement myLightsXML = myElementXML.child("lights");
					for(CCXMLElement myLightXML:myLightsXML.children("light")){
						CCLightChannel myLightChannel = new CCLightChannel(myLightXML);
						
						myLightChannels.add(myLightChannel);
						myMapping.add(myLightChannel);
					}
					break;
				}
			}
			
			CCSequenceElement myElement = new CCSequenceElement(
				myID, 
				myMotorChannels,
				myLightChannels,
				theMotorBounds,
				theElementRadius
			);
			
			add(myElement);
		}
	}

	public Map<CCKleChannelType, CCSequenceMapping<?>> mappings() {
		return _myMappings;
	}
	
	public static void main(String[] args) {
		
//		CCLog.info(mapping(new CCTestSequenceElements()));
	}
}
