package cc.creativecomputing.io.netty.codec.osc;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.control.handles.CCNumberHandle;
import cc.creativecomputing.control.handles.CCObjectHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCPropertyObject;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil.CCDirectMember;
import cc.creativecomputing.io.netty.CCClient;
import cc.creativecomputing.io.netty.CCNetProtocol;
import cc.creativecomputing.io.netty.CCTCPClient;
import cc.creativecomputing.io.netty.CCUDPClient;

public class CCOSCSender {

	private CCClient<CCOSCPacket> _myOSCOut;
	
	@CCProperty(name = "protocol")
	private CCNetProtocol _myProtocol = CCNetProtocol.UDP;
	
	@CCProperty(name = "ip")
	private String _myIP = "127.0.0.1";
	@CCProperty(name = "port")
	private int _myPort = 12345;

	@CCProperty(name = "connect")
	public void connect(boolean theConnect){
		if(theConnect){
			switch(_myProtocol){
			case UDP:
				_myOSCOut = new CCUDPClient<>(new CCOSCCodec());
				break;
			case TCP:
				_myOSCOut = new CCTCPClient<>(new CCOSCCodec());
				break;
			}
		}
	}

	@CCProperty(name = "osc handles", readBack = true)
	private CCObjectHandle _myOSCHandles = new CCObjectHandle(new CCDirectMember( new CCPropertyObject("osc handles", 0, 0)));
	
	
	
	public class CCOSCProperty{
		String _myAddress;
		CCNumberHandle<Double> _myProperty;
		
		private CCOSCProperty(String theAddress, CCNumberHandle<Double> theProperty){
			_myAddress = theAddress;
			_myProperty = theProperty;
			_myProperty.changeEvents.add(theValue ->{
				_myOSCOut.write(new CCOSCMessage(_myAddress, theValue));
			});
		}
	}
	
	private Map<String,CCOSCProperty> _myHandles = new HashMap<>();
	
	@CCProperty(name = "address pattern")
	private String _myAddressPattern = "";
	
	@CCProperty(name = "min")
	private double _myMin = 0;

	@CCProperty(name = "max")
	private double _myMax = 1;
	
	
	@CCProperty(name = "add double property")
	private void addProperty(){
		if(_myHandles.containsKey(_myAddressPattern))return;
		Path myPath = Paths.get(_myAddressPattern);
		CCLog.info(myPath.getNameCount());
		for(int i = 0; i < myPath.getNameCount() - 1;i++){
			CCLog.info(myPath.getName(i));
		}
//		_myOSCHandles.property(myPath, 1);
		
		CCNumberHandle<Double> myProperty = (CCNumberHandle<Double>)_myOSCHandles.createProperty(myPath, Double.class, new CCPropertyObject(myPath.getFileName().toString(), _myMin, _myMax));
		
		_myHandles.put(_myAddressPattern, new CCOSCProperty(_myAddressPattern, myProperty));
		_myOSCHandles.forceChange();
		for(CCPropertyHandle<?> myHandle:_myOSCHandles.children().values()){
			CCLog.info(myHandle.name());
		}
	}
	
	public static void main(String[] args) {
		CCOSCSender _mySender = new CCOSCSender();
		_mySender._myAddressPattern = "ShaderModule/ShaderModule._property";
		_mySender.addProperty();
	}
}
