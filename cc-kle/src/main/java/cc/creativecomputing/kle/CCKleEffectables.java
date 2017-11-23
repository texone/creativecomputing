package cc.creativecomputing.kle;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.kle.lights.CCLightChannel;
import cc.creativecomputing.kle.lights.CCLightRGBSetup;
import cc.creativecomputing.kle.motors.CCMotorCalculations;
import cc.creativecomputing.kle.motors.CCMotorChannel;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;

public class CCKleEffectables extends ArrayList<CCKleEffectable>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1200033076720174639L;

	public static CCDataElement mapping(CCKleEffectables theElements){
		CCDataElement myMappingsXML = new CCDataElement("mappings");
		for(CCKleChannelType myKey:theElements.mappings().keySet()){
			CCKleMapping<?> myMapping = theElements.mappings().get(myKey);
			myMappingsXML.addChild(myMapping.toXML());
		}
		return myMappingsXML;
	}
	
	public static CCDataElement sculpture(CCKleEffectables theElements){
		CCDataElement myResult = new CCDataElement("sculpture");
		CCDataElement myElementsXML = myResult.createChild("elements");
		
		for(CCKleEffectable myElement:theElements){
			myElementsXML.addChild(myElement.toXML());
		}
		
		return myResult;
	}
	
	public static CCKleEffectables createLightMatrix(int theColumns, int theRows){
		CCKleEffectables myResult = new CCKleEffectables();
		int i = 0;
		for(int c = 0; c < theColumns; c++){
			for(int r = 0; r < theRows; r++){
				CCKleEffectable myElement = new CCKleEffectable(i, new CCLightRGBSetup(i * 3, i * 3 + 1, i * 3 + 2));
				myElement.column(c);;
				myElement.row(r);
				myResult.add(myElement);
				i++;
			}
		}
		
		return myResult;
	}
	
	private Map<CCKleChannelType,CCKleMapping<?>> _myMappings = new HashMap<>();
	
	public CCKleEffectables(Path theKlePath, CCMotorCalculations<?> theMotorBounds, float theElementRadius){
		this(
			CCXMLIO.createXMLElement(theKlePath.resolve("mapping.xml")),
			CCXMLIO.createXMLElement(theKlePath.resolve("sculpture.xml")),
			theMotorBounds,
			theElementRadius
		);
	}
	
	public CCKleEffectables(){
		super();
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public CCKleEffectables(CCDataElement theMappingsXML, CCDataElement mySculptureXML, CCMotorCalculations theMotorBounds, float theElementRadius){
		super();
		for(CCDataElement myMappingXML:theMappingsXML){
			_myMappings.put(CCKleChannelType.valueOf(myMappingXML.attribute("name").toUpperCase()), new CCKleMapping(myMappingXML));
		}
		
		CCDataElement myElementsXML = mySculptureXML.child("elements");
		for(CCDataElement myElementXML:myElementsXML){
			int myID = myElementXML.intAttribute("id");
			
			List<CCMotorChannel> myMotorChannels = null;
			List<CCLightChannel> myLightChannels = null;
			
			CCVector3 myCentroid = null;
			CCVector3 myLightPosition = null;
			
			for(CCKleChannelType myKey:_myMappings.keySet()){
				CCKleMapping myMapping = _myMappings.get(myKey);
				switch(myKey){
				case MOTORS:
					myMotorChannels = new ArrayList<>();
					CCDataElement myMotorsXML = myElementXML.child("motors");
					for(CCDataElement myMotorXML:myMotorsXML.children("motor")){
						CCMotorChannel myMotorChannel = new CCMotorChannel(myMotorXML);
						
						myMotorChannels.add(myMotorChannel);
						myMapping.add(myMotorChannel);
					}
					CCDataElement myCentroidXML = myMotorsXML.child("centroid");
					if(myCentroidXML != null){
						myCentroid = new CCVector3(
							myCentroidXML.doubleAttribute("x"),
							myCentroidXML.doubleAttribute("y"),
							myCentroidXML.doubleAttribute("z")	
						);
					}
					break;
				case LIGHTS:
					myLightChannels = new ArrayList<>();
					CCDataElement myLightsXML = myElementXML.child("lights");
					for(CCDataElement myLightXML:myLightsXML.children("light")){
						CCLightChannel myLightChannel = new CCLightChannel(myLightXML);
						
						myLightChannels.add(myLightChannel);
						myMapping.add(myLightChannel);
						
						
						CCDataElement myLightPositionXML = myLightXML.child("position");
						if(myLightPositionXML != null){
							myLightPosition = new CCVector3(
								myLightPositionXML.doubleAttribute("x"),
								myLightPositionXML.doubleAttribute("y"),
								myLightPositionXML.doubleAttribute("z")
							);
						}
					}
					break;
				}
			}
			
			CCDataElement myMatrixXML = myElementXML.child("matrix");
			
			CCMatrix4x4 myMatrix = new CCMatrix4x4();
			myMatrix.m00 = myMatrixXML.doubleAttribute("m00");
			myMatrix.m01 = myMatrixXML.doubleAttribute("m01");
			myMatrix.m02 = myMatrixXML.doubleAttribute("m02");
			myMatrix.m03 = myMatrixXML.doubleAttribute("m03");

			myMatrix.m10 = myMatrixXML.doubleAttribute("m10");
			myMatrix.m11 = myMatrixXML.doubleAttribute("m11");
			myMatrix.m12 = myMatrixXML.doubleAttribute("m12");
			myMatrix.m13 = myMatrixXML.doubleAttribute("m13");

			myMatrix.m20 = myMatrixXML.doubleAttribute("m20");
			myMatrix.m21 = myMatrixXML.doubleAttribute("m21");
			myMatrix.m22 = myMatrixXML.doubleAttribute("m22");
			myMatrix.m23 = myMatrixXML.doubleAttribute("m23");

			myMatrix.m30 = myMatrixXML.doubleAttribute("m30");
			myMatrix.m31 = myMatrixXML.doubleAttribute("m31");
			myMatrix.m32 = myMatrixXML.doubleAttribute("m32");
			myMatrix.m33 = myMatrixXML.doubleAttribute("m33");
			
			CCKleEffectable myElement = new CCKleEffectable(
				myID, 
				myMotorChannels,
				myLightChannels,
				theMotorBounds,
				myCentroid,
				myMatrix,
				theElementRadius
			);
			for(String myKey:myElementXML.attributes()){
				myElement.addAttribute(myKey, myElementXML.attribute(myKey));
			}
			CCDataElement myIdSourceData = myElementXML.child("id_sources");
			if(myIdSourceData != null){
				for(String myKey:myIdSourceData.attributes()){
					myElement.addIdBasedSource(myKey, myIdSourceData.intAttribute(myKey));
				}
			}
			if(myLightPosition != null)myElement.lightSetup().position().set(myLightPosition);
			add(myElement);
			
			if(theMotorBounds != null)theMotorBounds.setElements(this);
		}
	}

	public Map<CCKleChannelType, CCKleMapping<?>> mappings() {
		return _myMappings;
	}
	
	
}
