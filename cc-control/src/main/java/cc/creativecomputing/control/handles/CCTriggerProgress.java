package cc.creativecomputing.control.handles;

import cc.creativecomputing.core.events.CCListenerManager;

public class CCTriggerProgress {
	
	public static interface CCTriggerProgressListener{
		public void start();
		
		public void progress(double theProgress);
		
		public void end();
		
		public void interrupt();
	}
	
	private CCListenerManager<CCTriggerProgressListener> _myEvents = new CCListenerManager<>(CCTriggerProgressListener.class);
	
	public CCListenerManager<CCTriggerProgressListener> events(){
		return _myEvents;
	}
	
	public void start(){
		_myEvents.proxy().start();
	}

	public void progress(double theProgress){
		_myEvents.proxy().progress(theProgress);
	}

	public void interrupt(){
		_myEvents.proxy().interrupt();
	}
	
	public void end(){
		_myEvents.proxy().end();
	}
}
