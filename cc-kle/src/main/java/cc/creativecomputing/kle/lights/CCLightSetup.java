package cc.creativecomputing.kle.lights;

import java.util.List;

import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.kle.CCKleChannelSetup;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector3;

public abstract class CCLightSetup extends CCKleChannelSetup<CCLightChannel>{
	
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
	
	public CCDataElement toXML(){
		CCDataElement myLightsXML = new CCDataElement("lights");
		if(_myChannels == null)return myLightsXML;
		for(CCLightChannel myChannel:_myChannels){
			if(myChannel == null)continue;
			myLightsXML.addChild(myChannel.toXML());
		}
		return myLightsXML;
	}
}
