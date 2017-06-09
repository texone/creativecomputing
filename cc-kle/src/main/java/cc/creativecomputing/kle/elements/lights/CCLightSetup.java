package cc.creativecomputing.kle.elements.lights;

import java.util.List;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.kle.elements.CCChannelSetup;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;

public abstract class CCLightSetup extends CCChannelSetup<CCLightChannel>{
	
	protected CCColor _myColor;
	
	protected CCVector3 _myPosition = new CCVector3();
	
	public CCLightSetup(List<CCLightChannel> theChannels){
		super(theChannels);
		_myColor = new CCColor();
	}
	
	public CCColor color(){
		return _myColor;
	}
	
	@Override
	public CCVector3 position() {
		return _myPosition;
	}
	
	public CCXMLElement toXML(){
		CCXMLElement myLightsXML = new CCXMLElement("lights");
		if(_myChannels == null)return myLightsXML;
		for(CCLightChannel myChannel:_myChannels){
			if(myChannel == null)continue;
			myLightsXML.addChild(myChannel.toXML());
		}
		return myLightsXML;
	}
}
