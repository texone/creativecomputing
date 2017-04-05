package cc.creativecomputing.control.code;

import cc.creativecomputing.core.events.CCListenerManager;

public class CCShaderObject {
	
	
	
	public static abstract class CCShaderObjectInterface{
		
		private String _myShaderSource = "";
		
		public void sourceCode(String theSource){
			_myShaderSource = theSource;
		}
		
		public String sourceCode(){
			return _myShaderSource;
		}
		
		public abstract String errorLog();
		
	}
	
	private CCListenerManager<CCShaderCompileListener> _myEvents = CCListenerManager.create(CCShaderCompileListener.class);
	
	private final CCShaderObjectInterface _myInterface;
	
	public static interface CCShaderCompileListener{
		public void onRecompile(CCShaderObject theCompiler);
	}
	
	public CCListenerManager<CCShaderCompileListener> events(){
		return _myEvents;
	}
	
	public CCShaderObject(CCShaderObjectInterface theInterface){
		_myInterface = theInterface;
	}

	
	
	public void sourceCode(String theSource){
		_myInterface.sourceCode(theSource);
	}
	
	public String sourceCode(){
		return _myInterface.sourceCode();
	}
	
	public String errorLog(){
		return _myInterface.errorLog();
	}
}
