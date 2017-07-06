package cc.creativecomputing.kle.elements.lights;

import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.kle.elements.CCSequenceChannel;

public class CCLightChannel extends CCSequenceChannel{

	public CCLightChannel(CCDataElement theLightXML) {
		super(theLightXML.intAttribute("id"));
	}
	
	public CCLightChannel(int theID){
		super(theID);
	}
	

	@Override
	public CCDataElement toXML() {
		CCDataElement myLightXML = new CCDataElement("light");
		myLightXML.addAttribute("id", id());
		return myLightXML;
	}

}
