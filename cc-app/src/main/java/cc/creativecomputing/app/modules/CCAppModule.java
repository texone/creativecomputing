package cc.creativecomputing.app.modules;

import cc.creativecomputing.core.events.CCListenerManager;

public interface CCAppModule<ListenerType> {
	
	public void start();
	
	public void stop();
	
	public Class<ListenerType> listenerInterface();
	
	public CCListenerManager<ListenerType> listener();
}
