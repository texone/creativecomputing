package cc.creativecomputing.kle;

import java.util.List;

import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCVector3;

public abstract class CCKleChannelSetup <ChannelType extends CCKleChannel>{
	
	protected final List<ChannelType> _myChannels;
	
	public CCKleChannelSetup(List<ChannelType> theChannels){
		_myChannels = theChannels;
	}
	
	public List<ChannelType> channels(){
		return _myChannels;
	}
	
	public abstract void setByRelativePosition(double...theValues);
	
	public abstract CCVector3 position();
	
	
	public abstract CCDataElement toXML();
}
