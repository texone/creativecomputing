package cc.creativecomputing.control.code;

public class CCShaderSource {

	protected String _mySourceCode;
	
	protected CCShaderObject _myShaderObject;
	
	public CCShaderSource(CCShaderObject theShaderObject, String theSourceCode){
		_myShaderObject = theShaderObject;
		_mySourceCode = theSourceCode;
	}
	
	public CCShaderObject object(){
		return _myShaderObject;
	}
	
	public String sourceCode(){
		return _mySourceCode;
	}
	
	public void sourceCode(String theSourceCode){
		_mySourceCode = theSourceCode;
		_myShaderObject.update();
	}

	public String errorLog() {
		return _myShaderObject.errorLog();
	}
	
	public void save(){
	}
	
	public static void main(String[] args) {
	}
}
