package cc.creativecomputing.io.net;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.core.CCProperty;

public class CCNetLocalAddress extends CCNetAddress{
	
	private static List<String> ips = new ArrayList<>();
	
	private static void checkIPList(){
		ips.clear();
		try {
			Enumeration<NetworkInterface> myInterfaces = NetworkInterface.getNetworkInterfaces();
			while(myInterfaces.hasMoreElements()){
			    NetworkInterface myInterface = myInterfaces.nextElement();
			 
			    Enumeration<InetAddress> myAddresses = myInterface.getInetAddresses();
			    while (myAddresses.hasMoreElements()){
			        InetAddress i = myAddresses.nextElement();
			        if(i.getHostAddress().contains(":"))continue;
			        ips.add(i.getHostAddress());
			        System.out.println(i.getHostName() + " " + i.getHostAddress());
			    }
			}
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@CCProperty(name = "ip")
	private CCSelection _myIPSelection = new CCSelection();
	
	public CCNetLocalAddress(){
		if(ips.size() == 0){
			checkIPList();
		}
		for(String myIP:ips){
			_myIPSelection.add(myIP);
		}
	}
	
	@Override
	public String ip() {
		return _myIPSelection.value();
	}
	
	public String broadcast(){
		return ip().substring(0, ip().indexOf(".")) + ".255.255.255";
	}
	
	public void ip(String theIP){
		_myIPSelection.value(theIP);
	}
	
	@CCProperty(name = "refresh ip list")
	public void refreshIpList(){
		_myIPSelection.values().clear();
		checkIPList();
		for(String myIP:ips){
			_myIPSelection.add(myIP);
		}
	}
}
