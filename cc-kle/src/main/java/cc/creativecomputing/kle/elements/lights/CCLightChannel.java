package cc.creativecomputing.kle.elements.lights;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.elements.CCSequenceChannel;

public class CCLightChannel extends CCSequenceChannel{

	public CCLightChannel(CCXMLElement theLightXML) {
		super(theLightXML.intAttribute("id"));
	}
	
	public CCLightChannel(int theID){
		super(theID);
	}
	

	@Override
	public CCXMLElement toXML() {
		CCXMLElement myLightXML = new CCXMLElement("light");
		myLightXML.addAttribute("id", id());
		return myLightXML;
	}

}
