package cc.creativecomputing.app.modules;

public interface CCAppModule<ListenerType> {
	
	public void start();
	
	public void stop();
	
	public Class<ListenerType> listenerInterface();
	
	public void addListener(ListenerType theObject);
}
