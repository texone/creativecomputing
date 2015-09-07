package cc.creativecomputing.kle.elements.lights;

import java.util.List;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.elements.CCChannelSetup;
import cc.creativecomputing.math.CCColor;

public class CCLightSetup extends CCChannelSetup<CCLightChannel>{
	
	protected CCColor _myColor;
	
	public CCLightSetup(List<CCLightChannel> theChannels){
		super(theChannels);
		_myColor = new CCColor();
	}
	
	public void setByRelativePosition(double...theValues){
		
	}
	
	public CCColor color(){
		return _myColor;
	}
	
	public CCXMLElement toXML(){
		CCXMLElement myLightsXML = new CCXMLElement("lights");
		for(CCLightChannel myChannel:_myChannels){
			myLightsXML.addChild(myChannel.toXML());
		}
		return myLightsXML;
	}
}
