package cc.creativecomputing.app.modules;

import cc.creativecomputing.core.events.CCListenerManager;

public interface CCAppModule<ListenerType> {
	
	void start();
	
	void stop();
	
	CCListenerManager<ListenerType> listener();
}
