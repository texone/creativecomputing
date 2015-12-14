package cc.creativecomputing.app.modules;

import cc.creativecomputing.core.events.CCListenerManager;

public abstract class CCAbstractAppModule<ListenerType> implements CCAppModule<ListenerType>{
	
	protected final CCListenerManager<ListenerType> _myListeners;
	
	protected String _myID;
	
	protected CCAbstractAppModule(Class<ListenerType> theListenerInterface, String theID){
		_myListeners = CCListenerManager.create(theListenerInterface);
		
		_myID = theID;
	}	
	
	/**
	 * Returns the Listener manager containing the animator Listeners
	 * @return
	 */
	public CCListenerManager<ListenerType> listener() {
		return _myListeners;
	}

}
