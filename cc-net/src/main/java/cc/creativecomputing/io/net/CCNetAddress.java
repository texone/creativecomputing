package cc.creativecomputing.io.net;

import java.net.InetSocketAddress;

import cc.creativecomputing.core.CCProperty;

public abstract class CCNetAddress {
	
	@CCProperty(name = "port")
	private int _myPort = 0;
	
	private String _myLastIP = "none";
	private int _myLastPort = -1;
	
	private InetSocketAddress _myAdress = null;
	
	public abstract String ip();
	
	public void port(int thePort){
		_myPort = thePort;
	}
	
	public int port(){
		return _myPort;
	}
	
	public InetSocketAddress getAddress(){
		if(ip().equals(_myLastIP) && _myPort == _myLastPort && _myAdress != null){
			return _myAdress;
		}
		_myLastIP = ip();
		_myLastPort = _myPort;
		return _myAdress = new InetSocketAddress(ip(), _myPort);
	}
}
