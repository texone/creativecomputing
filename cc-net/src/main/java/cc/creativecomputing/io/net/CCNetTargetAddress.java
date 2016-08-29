package cc.creativecomputing.io.net;

import cc.creativecomputing.core.CCProperty;

public class CCNetTargetAddress extends CCNetAddress{

	@CCProperty(name = "ip")
	private String _myIp = "0.0.0.0";
	
	@Override
	public String ip() {
		return _myIp;
	}
	
	public void ip(String theIP){
		_myIp = theIP;
	}
}
