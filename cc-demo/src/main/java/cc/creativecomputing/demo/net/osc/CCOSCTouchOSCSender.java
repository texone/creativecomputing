package cc.creativecomputing.demo.net.osc;

import java.nio.file.Paths;
import java.util.Base64;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCEventTriggerHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCPropertyObject;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil.CCDirectMember;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.app.CCGL2Adapter;
import cc.creativecomputing.graphics.app.CCGL2Application;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.net.CCUDPOut;
import cc.creativecomputing.io.net.codec.osc.CCOSCMessage;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacket;
import cc.creativecomputing.io.net.codec.osc.CCOSCPacketCodec;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;

public class CCOSCTouchOSCSender extends CCGL2Adapter {
	
	@CCProperty(name = "attributes", hide = true)
	private CCObjectPropertyHandle _myAttributeHandles = new CCObjectPropertyHandle(new CCDirectMember( new CCPropertyObject("attributes", 0, 0)));

	
	private CCNumberPropertyHandle<Double> createHandle(CCObjectPropertyHandle theParent, String theName, double theMin, double theMax){
		CCNumberPropertyHandle<Double> myResult = new CCNumberPropertyHandle<Double>(
			theParent, 
			new CCDirectMember<CCProperty>(new Double(0), new CCPropertyObject(theName, theMin, theMax)),
			CCPropertyMap.doubleConverter
		);
		theParent.children().put(theName, myResult);
		return myResult;
	}
	
	private CCBooleanPropertyHandle createTriggerHandle(CCObjectPropertyHandle theParent, String theName){
		CCBooleanPropertyHandle myResult = new CCBooleanPropertyHandle(
			theParent, 
			new CCDirectMember<CCProperty>(new Boolean(false), new CCPropertyObject(theName))
		);
		theParent.children().put(theName, myResult);
		return myResult;
	}
	
	private CCUDPOut<CCOSCPacket> _myOut;

	@Override
	public void init(CCGraphics g, CCAnimator theAnimator) {
		_myOut = new CCUDPOut<>(new CCOSCPacketCodec(), "localhost", 17670);
		_myOut.connect(true);
		CCDataElement myTouchOSC = CCXMLIO.createXMLElement(CCNIOUtil.dataPath("net/index.xml"));
		CCLog.info(myTouchOSC);
		for(CCDataElement myTabElement:myTouchOSC) {
			String myName = myTabElement.attribute("name");
			for(CCDataElement myControllerElement:myTabElement) {
				String myType = myControllerElement.attribute("type");
				String myAdressPattern = myControllerElement.attribute("osc_cs");
				if(myAdressPattern == null)continue;
				final String myAdressPattern2 = new String(Base64.getDecoder().decode(myAdressPattern));
				
				String myCName = Paths.get(myAdressPattern2).getFileName().toString();
				String myGroup = Paths.get(myAdressPattern2).getName(1).toString();
				if(_myAttributeHandles.property(myGroup) == null) {
					_myAttributeHandles.children().put(myGroup, new CCObjectPropertyHandle(new CCDirectMember( new CCPropertyObject(myGroup, 0, 0))));
				}
				CCObjectPropertyHandle myParent = (CCObjectPropertyHandle)_myAttributeHandles.property(myGroup);
				
				switch(myType) {
				case "faderh":
					double myMin = myControllerElement.doubleAttribute("scalef");
					double myMax = myControllerElement.doubleAttribute("scalet");
					
					
					CCNumberPropertyHandle<Double> myHandle = createHandle(myParent, myCName, myMin, myMax);
					myHandle.events().add(theValue ->{
						CCLog.info(myAdressPattern2, theValue, _myOut);
						if(_myOut == null)return;
						if(!_myOut.isConnected())return;
						CCLog.info("send");
						_myOut.send(new CCOSCMessage(myAdressPattern2, theValue));
					});
					break;
				case "push":
					myMin = myControllerElement.doubleAttribute("scalef");
					final double myMax2 = myControllerElement.doubleAttribute("scalet");
					CCLog.info(myAdressPattern2, myMax2);
					CCBooleanPropertyHandle myBoolHandle = createTriggerHandle(myParent, myCName + (int)myMax2);
					myBoolHandle.events().add(theValue ->{
						if(_myOut == null)return;
						if(!_myOut.isConnected())return;
						_myOut.send(new CCOSCMessage(myAdressPattern2, myMax2));
					});
					break;
				
				}
			}
		}
	}

	@Override
	public void update(CCAnimator theAnimator) {
		if(_myOut == null)return;
		if(!_myOut.isConnected())return;
		_myOut.send(new CCOSCMessage("/ping", 0));
	}

	@Override
	public void display(CCGraphics g) {
	}

	public static void main(String[] args) {

		CCOSCTouchOSCSender demo = new CCOSCTouchOSCSender();

		CCGL2Application myAppManager = new CCGL2Application(demo);
		myAppManager.glcontext().size(1200, 600);
		myAppManager.animator().framerate = 30;
		myAppManager.animator().animationMode = CCAnimator.CCAnimationMode.FRAMERATE_PRECISE;
		myAppManager.start();
		
		
	}
}

