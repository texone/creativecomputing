/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.core.events;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * An event manager can be used to manage a list of event listeners of a particular type. The class
 * provides {@link #add(Object)} and {@link #remove(Object)} methods for registering listeners, as well
 * as a {@link #proxy()} method for firing events to the listeners.
 * 
 * <p/>
 * To use this class, suppose you want to support ActionEvents. You would do: <code><pre>
 * public class MyActionEventSource
 * {
 *   private CCEventManager<ActionListener> actionListeners = 
 *       CCEventManager.create(ActionListener.class);
 * 
 *   public void someMethodThatFiresAction()
 *   {
 *     ActionEvent e = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "somethingCool");
 *     actionListeners.proxy().actionPerformed(e);
 *   }
 * }
 * </pre></code>
 * 
 * @param <ListenerType> the type of event listener that is supported by this proxy.
 * 
 * @since 3.0
 * @version $Id: EventListenerSupport.java 978864 2010-07-24 12:49:38Z jcarman $
 */
public class CCListenerManager<ListenerType> implements Iterable<ListenerType>{
	
	/**
	 * The list used to hold the registered listeners. This list is intentionally a thread-safe copy-on-write-array so
	 * that traversals over the list of listeners will be atomic.
	 */
	private final List<ListenerType> _myListeners = new ArrayList<ListenerType>();

	/**
	 * The proxy representing the collection of listeners. Calls to this proxy object will sent to all registered
	 * listeners.
	 */
	private ListenerType _myProxy;

	/**
	 * Creates an EventListenerSupport object which supports the specified listener type.
	 * 
	 * @param listenerInterface the type of listener interface that will receive events posted using this class.
	 * 
	 * @return an EventListenerSupport object which supports the specified listener type.
	 * 
	 * @throws NullPointerException if <code>listenerInterface</code> is <code>null</code>.
	 * @throws IllegalArgumentException if <code>listenerInterface</code> is not an interface.
	 */
	public static <T> CCListenerManager<T> create(Class<T> listenerInterface) {
		return new CCListenerManager<T>(listenerInterface);
	}

	/**
	 * Creates an EventListenerSupport object which supports the provided listener interface.
	 * 
	 * @param listenerInterface the type of listener interface that will receive events posted using this class.
	 * 
	 * @throws NullPointerException if <code>listenerInterface</code> is <code>null</code>.
	 * @throws IllegalArgumentException if <code>listenerInterface</code> is not an interface.
	 */
	public CCListenerManager(Class<ListenerType> listenerInterface) {
		_myProxy = listenerInterface.cast(Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[] { listenerInterface }, new ProxyInvocationHandler()));
	}

	/**
	 * Returns a proxy object which can be used to call listener methods on all of the registered event listeners. All
	 * calls made to this proxy will be forwarded to all registered listeners.
	 * 
	 * @return a proxy object which can be used to call listener methods on all of the registered event listeners
	 */
	public ListenerType proxy() {
		return _myProxy;
	}

	// **********************************************************************************************************************
	// Other Methods
	// **********************************************************************************************************************

	/**
	 * Registers an event listener.
	 * 
	 * @param theListener the event listener (may not be <code>null</code>).
	 * 
	 * @throws NullPointerException if <code>listener</code> is <code>null</code>.
	 */
	public void add(ListenerType theListener) {
		synchronized (_myListeners) {
			_myListeners.add(theListener);
		}
	}

	/**
	 * Returns the number of registered listeners.
	 * 
	 * @return the number of registered listeners.
	 */
	public int size() {
		return _myListeners.size();
	}

	/**
	 * Unregisters an event listener.
	 * 
	 * @param theListener the event listener (may not be <code>null</code>).
	 * 
	 * @throws NullPointerException if <code>listener</code> is <code>null</code>.
	 */
	public void remove(ListenerType theListener) {
		synchronized (_myListeners) {
			_myListeners.remove(theListener);
		}
	}
	
	public void clear() {
		synchronized (_myListeners) {
			_myListeners.clear();
		}
	}

	/**
	 * An invocation handler used to dispatch the event(s) to all the listeners.
	 */
	private class ProxyInvocationHandler implements InvocationHandler {
		/**
		 * Propagates the method call to all registered listeners in place of the proxy listener object.
		 * 
		 * @param proxy the proxy object representing a listener on which the invocation was called.
		 * @param method the listener method that will be called on all of the listeners.
		 * @param args event arguments to propogate to the listeners.
		 */
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			for (ListenerType listener : new ArrayList<ListenerType>(_myListeners)) {
				method.invoke(listener, args);
			}
			return null;
		}
	}

	@Override
	public Iterator<ListenerType> iterator() {
		return new ArrayList<ListenerType>(_myListeners).iterator();
	}
}
