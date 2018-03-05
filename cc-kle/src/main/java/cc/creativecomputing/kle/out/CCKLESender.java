
package cc.creativecomputing.kle.out;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.kle.CCKleChannel;
import cc.creativecomputing.kle.CCKleEffectable;

public abstract class CCKLESender  {

	public static class CCKLEUniverse {
		public final CCKleChannel[] channels = new CCKleChannel[512];
		public final int universe;

		CCKLEUniverse(int theUniverse) {
			universe = theUniverse;
		}
	}

	public static class CCKLEInterface {
		public final List<CCKLEUniverse> universes = new ArrayList<>();

		public final String name;

		public CCKLEInterface(String theName) {
			name = theName;
		}
	}

	protected List<CCKLEInterface> _myInterfaces = new ArrayList<>();

	public CCKLESender(List<CCKleEffectable> theElements) {

		Map<String, Map<Integer, CCKLEUniverse>> myInterfaceMap = new HashMap<>();
		
		String myInterfaceID = "default";

		for (CCKleEffectable myElement : theElements) {
			for (CCKleChannel myChannel : myElement.channels()) {
				if (myChannel.universe() < 0)
					continue;
				if (myChannel.channel() < 0)
					continue;
				if (myChannel.interfaceName() != null)
					myInterfaceID = myChannel.interfaceName();

				if (!myInterfaceMap.containsKey(myInterfaceID)) {
					myInterfaceMap.put(myInterfaceID, new HashMap<>());
				}
				Map<Integer, CCKLEUniverse> myUniverseMap = myInterfaceMap.get(myInterfaceID);
				if (!myUniverseMap.containsKey(myChannel.universe())) {
					myUniverseMap.put(myChannel.universe(), new CCKLEUniverse(myChannel.universe()));
				}
				myUniverseMap.get(myChannel.universe()).channels[myChannel.channel()] = myChannel;
			}
		}

		for (String myInterfaceName : myInterfaceMap.keySet()) {
			Map<Integer, CCKLEUniverse> myUniverseMap = myInterfaceMap.get(myInterfaceName);
			CCKLEInterface myInterface = new CCKLEInterface(myInterfaceName);
			myInterface.universes.addAll(myUniverseMap.values());
			Collections.sort(myInterface.universes, (a, b) -> {
				return Integer.compare(a.universe, b.universe);
			});
			_myInterfaces.add(myInterface);
		}

		Collections.sort(_myInterfaces, (a, b) -> {
			return a.name.compareTo(b.name);
		});

//		for (CCKLEInterface myInterface : _myInterfaces) {
//			CCLog.info(myInterface._myName);
//			for (CCKLEUniverse myUniverse : myInterface._myUniverses) {
//				CCLog.info(myUniverse._myUniverse);
//				for (int i = 0; i < myUniverse._myChannels.length; i++) {
//					if (myUniverse._myChannels[i] != null)
//						CCLog.info(myInterface._myName + " : " + myUniverse._myUniverse + " : " + " : " + i);
//				}
//			}
//		}
	}

	public void start() {

	}
	
	public abstract void send();

	public void update(CCAnimator theAnimator) {
		send();
	}

}
