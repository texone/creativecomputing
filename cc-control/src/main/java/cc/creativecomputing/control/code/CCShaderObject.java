package cc.creativecomputing.control.code;

import cc.creativecomputing.core.events.CCListenerManager;

public abstract class CCShaderObject {
	
	private String _myShaderSource = "";
	
	private CCListenerManager<CCShaderCompileListener> _myEvents = CCListenerManager.create(CCShaderCompileListener.class);
	
	public static interface CCShaderCompileListener{
		public void onRecompile(CCShaderObject theCompiler);
	}
	
	public CCListenerManager<CCShaderCompileListener> events(){
		return _myEvents;
	}
	
	public void sourceCode(String theSource){
		_myShaderSource = theSource;
	}
	
	public String sourceCode(){
		return _myShaderSource;
	}
	
	public abstract String errorLog();
}
