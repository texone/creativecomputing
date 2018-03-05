
package cc.creativecomputing.kle.out;

import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.kle.CCKleEffectable;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.protocol.serial.dmx.CCDMX;

public class CCKLEDMXSender extends CCKLESender{

	@CCProperty(name = "dmx", hide = true)
	private CCDMX _myDMX;

	public CCKLEDMXSender(List<CCKleEffectable> theElements) {
		super(theElements);
		_myDMX = new CCDMX();
	}

	@Override
	public void send() {
		for(int i = 0; i < _myDMX.universeSize();i++){
		}
		CCKLEInterface myInterface = _myInterfaces.get(0);
		
		CCKLEUniverse myUniverse = myInterface.universes.get(0);
		for(int i = 0; i < myUniverse.channels.length;i++){
			if(myUniverse.channels[i] == null)continue;
			CCLog.info(myUniverse.channels[i].value());
			_myDMX.setDMXChannel(i, (byte) (CCMath.saturate(myUniverse.channels[i].value()) * 255));
		}
		_myDMX.send();
	}

}
