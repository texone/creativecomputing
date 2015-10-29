package cc.creativecomputing.app.modules;

import cc.creativecomputing.core.events.CCListenerManager;

public abstract class CCAbstractAppModule<ListenerType> implements CCAppModule<ListenerType>{
	
	protected final CCListenerManager<ListenerType> _myListeners;
	
	protected Class<ListenerType> _myListenerClass;
	
	protected String _myID;
	
	protected CCAbstractAppModule(Class<ListenerType> theListenerInterface, String theID){
		_myListeners = CCListenerManager.create(theListenerInterface);
		_myListenerClass = theListenerInterface;
		
		_myID = theID;
	}

	@Override
	public Class<ListenerType> listenerInterface() {
		return _myListenerClass;
	}	
	
	/**
	 * Returns the Listener manager containing the animator Listeners
	 * @return
	 */
	public CCListenerManager<ListenerType> listener() {
		return _myListeners;
	}

}
