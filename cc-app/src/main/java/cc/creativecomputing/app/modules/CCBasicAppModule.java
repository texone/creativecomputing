package cc.creativecomputing.app.modules;


public class CCBasicAppModule extends CCAbstractAppModule<CCBasicAppListener>{
	
	public CCBasicAppModule() {
		super(CCBasicAppListener.class, "app");
	}
	
	@Override
	public void start() {
		_myListeners.proxy().start();
	}
	
	@Override
	public void stop() {
		_myListeners.proxy().stop();
	}

}
