package cc.creativecomputing.kle.elements;

import java.util.List;

import cc.creativecomputing.io.xml.CCXMLElement;

public abstract class CCChannelSetup <ChannelType extends CCSequenceChannel>{
	
	protected final List<ChannelType> _myChannels;
	
	public CCChannelSetup(List<ChannelType> theChannels){
		_myChannels = theChannels;
	}
	
	public List<ChannelType> channels(){
		return _myChannels;
	}
	
	public abstract void setByRelativePosition(double...theValues);
	
	
	public abstract CCXMLElement toXML();
}
