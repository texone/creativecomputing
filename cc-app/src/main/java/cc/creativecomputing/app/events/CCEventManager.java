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
package cc.creativecomputing.app.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * This is the object which maintains the list of registered events and their
 * listeners.
 * <p>
 * This is a many-to-many relationship, as both one listener can be configured
 * to process multiple event types and of course multiple listeners can be
 * registered to each event type.
 * <p>
 * The interface to this construct uses smart pointer wrapped objects, the
 * purpose being to ensure that no object that the registry is referring to is
 * destroyed before it is removed from the registry AND to allow for the
 * registry to be the only place where this list is kept ... the application
 * code does not need to maintain a second list.
 * <p>
 * Simply tearing down the registry (e.g.: destroying it) will automatically
 * clean up all pointed-to objects (so long as there are no other outstanding
 * references, of course).
 * 
 * @author christianr
 * 
 */
public class CCEventManager {

	static final int EVENTMANAGER_NUM_QUEUES = 2;

	private Map<Class<CCEvent>, List<CCEventListener<?>>> _myListener = new HashMap<Class<CCEvent>, List<CCEventListener<?>>>();
	private List<List<CCEvent>> _myQueues;
	
	// index of actively processing queue; events enque to the opposing queue
	private int _myActiveQueue; 

	private Queue<CCEvent> _myRealtimeEventQueue;

	private static CCEventManager instance = null;

	public CCEventManager() {
		_myActiveQueue = 0;
	}
	
	/**
	 * Registers a listener that will get called when the event type is triggered. 
	 * @param theListener
	 * @return Returns true if successful, false if not.
	 */
	@SuppressWarnings("unchecked")
	public <EventType extends CCEvent> boolean addListener(CCEventListener<?> theListener) {
//		CCLog.info("Events , Attempting to add delegate function for event type: " + theListener.eventType().getName());

		if(!_myListener.containsKey(theListener.eventType())){
			_myListener.put((Class<CCEvent>) theListener.eventType(), new ArrayList<CCEventListener<?>>());
		}
		List<CCEventListener<?>> eventListenerList = _myListener.get( theListener.eventType()); // this will find or create the entry

		eventListenerList.add(theListener);
//		CCLog.info("Events Successfully added delegate for event type: " + theListener.eventType().getName());

		return true;
	}
	
	public boolean addListener(List<CCEventListener<?>> theListener) {

		for(CCEventListener<?> myListener:theListener){
			addListener(myListener);
		}

		return false;
	}

	/**
	 * Removes a listener from the internal tables.
	 * @param theListener
	 * @return false if the pairing was not found.
	 */
	public <EventType extends CCEvent> boolean removeListener(CCEventListener<EventType> theListener) {
//		CCLog.info("Events ,  Attempting to remove delegate function from event type: " + theListener.eventType().getName());

		List<CCEventListener<?>> eventListenerList = _myListener.get(theListener.eventType());

        return eventListenerList.remove(theListener);

    }
	
	/**
	 * Removes the listeners from the internal tables.
	 * @param theListener
	 * @return false if the pairing was not found.
	 */
	public boolean removeListener(List<CCEventListener<?>> theListener) {

		for(CCEventListener<?> myListener:theListener){
			removeListener(myListener);
		}

		return false;
	}

	/**
	 * Fire off event NOW. This bypasses the queue entirely and immediately calls all listeners registered for the event.
	 * @param theEvent
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <EventType extends CCEvent> boolean trigger(EventType theEvent) {
//		CCLog.info("Events , Attempting to trigger event " + theEvent.name());

		List<CCEventListener<?>> eventListenerList = _myListener.get(theEvent.getClass());
		if (eventListenerList == null)
			return false;

		for (CCEventListener<?> listener : new ArrayList<>(eventListenerList)) {
//			CCLog.info("Events , Sending Event " + theEvent.name() + " to delegate.");
			((CCEventListener<EventType>)listener).handleEvent(theEvent); // call the delegate
		}

		return true;
	}

	/**
	 * Fire off event. This uses the queue and will call the listener on the next call to VTick(), assuming there's enough time.
	 * @param theEvent
	 * @return
	 */
	public <EventType extends CCEvent> boolean queueEvent(EventType theEvent) {
		assert (_myActiveQueue >= 0);
		assert (_myActiveQueue < EVENTMANAGER_NUM_QUEUES);

//		CCLog.info("Events , Attempting to queue event: " + theEvent.name());
		List<CCEventListener<?>> eventListenerList = _myListener.get(theEvent.getClass());
		if (eventListenerList != null) {
			_myQueues.get(_myActiveQueue).add(theEvent);
//			CCLog.info("Events , Successfully queued event: " + theEvent.name());
			return true;
		} else {
//			CCLog.info("Events , Skipping event since there are no delegates registered to receive it: " + theEvent.name());
			return false;
		}
	}

	public boolean threadSafeQueueEvent(CCEvent theEvent) {
		_myRealtimeEventQueue.add(theEvent);
		return true;
	}

	/**
	 * Find the next-available instance of the named event type and remove it from the processing queue. This
	 * may be done up to the point that it is actively being processed ... e.g.: is safe to happen during event
	 * processing itself.
	 * @param theType
	 * @param theAbortAll
	 * @return true if the event was found and removed, false otherwise
	 */
	public <EventType extends CCEvent> boolean abortEvent(EventType theType, boolean theAbortAll) {
		assert (_myActiveQueue >= 0);
		assert (_myActiveQueue < EVENTMANAGER_NUM_QUEUES);

		boolean success = false;
		List<CCEventListener<?>> eventListenerList = _myListener.get(theType);

		if (eventListenerList != null) {
			List<CCEvent> eventQueue = _myQueues.get(_myActiveQueue);

			for (int i = 0; i < eventQueue.size();) {
				if (eventQueue.get(i).getClass().equals(theType)) {
					eventQueue.remove(i);
					success = true;
					if (!theAbortAll)
						break;
				} else {
					i++;
				}
			}
		}

		return success;
	}

	/**
	 * Allow for processing of any queued messages, optionally specify a
	 * processing time limit so that the event processing does not take too
	 * long. Note the danger of using this artificial limiter is that all
	 * messages may not in fact get processed.
	 * 
	 * @param theDeltaTime
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void update(float theDeltaTime) {

		// This section added to handle events from other threads. Check out
		// Chapter 20.
		while (_myRealtimeEventQueue.size() > 0) {
			queueEvent(_myRealtimeEventQueue.poll());
		}

		// swap active queues and clear the new queue after the swap
		int queueToProcess = _myActiveQueue;
		_myActiveQueue = (_myActiveQueue + 1) % EVENTMANAGER_NUM_QUEUES;
		_myQueues.get(_myActiveQueue).clear();

//		CCLog.info("EventLoop , Processing Event Queue " + queueToProcess + "; " + _myQueues.get(queueToProcess).size() + " events to process");

		// Process the queue
		while (!_myQueues.get(queueToProcess).isEmpty()) {
			// pop the front of the queue
			CCEvent theEvent = _myQueues.get(queueToProcess).remove(0);

			

			// find all the delegate functions registered for this event
			List<CCEventListener<?>> findIt = _myListener.get(theEvent.getClass());
			if (findIt != null) {

				// call each listener
				for (CCEventListener listener : findIt) {
					listener.handleEvent(theEvent);
				}
			}

		}

	}

	/**
	 * Getter for the main global event manager. This is the event manager that
	 * is used by the majority of the engine, though you are free to define your
	 * own as long as you instantiate it with setAsGlobal set to false. It is
	 * not valid to have more than one global event manager.
	 * 
	 * @return
	 */
	public static CCEventManager instance() {
		if (instance == null)
			instance = new CCEventManager();
		return instance;
	}

}
