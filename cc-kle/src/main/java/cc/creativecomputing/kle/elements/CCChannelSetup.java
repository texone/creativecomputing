package cc.creativecomputing.kle.elements;

import java.util.List;

import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.math.CCVector3;

public abstract class CCChannelSetup <ChannelType extends CCSequenceChannel>{
	
	protected final List<ChannelType> _myChannels;
	
	public CCChannelSetup(List<ChannelType> theChannels){
		_myChannels = theChannels;
	}
	
	public List<ChannelType> channels(){
		return _myChannels;
	}
	
	public abstract void setByRelativePosition(double...theValues);
	
	public abstract CCVector3 position();
	
	
	public abstract CCXMLElement toXML();
}
