package cc.creativecomputing.graphics.shader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class CCShaderSourceTemplate {
	
	private static class CCShaderSourceInsert{
		private String _myInsert = "";
		
		@Override
		public String toString() {
			return _myInsert;
		}
	}
	
	private Map<String, CCShaderSourceInsert> _mySourceDefineMap;
	private Map<String, CCShaderSourceInsert> _mySourceApplyMap;
	
	private List<Object> _myParts;

	public CCShaderSourceTemplate(){
		_myParts = new ArrayList<>();
		_mySourceDefineMap = new HashMap<>();
		_mySourceApplyMap = new HashMap<>();
	}
	
	public void setDefine(String theKey, String theSource){
		if(!_mySourceDefineMap.containsKey(theKey))return;
		_mySourceDefineMap.get(theKey)._myInsert = theSource;
	}
	
	public void setApply(String theKey, String theSource){
		if(!_mySourceApplyMap.containsKey(theKey))return;
		_mySourceApplyMap.get(theKey)._myInsert = theSource;
	}
	
	public void addLine(String theLine){
		if(theLine.trim().startsWith("@")){
			CCShaderSourceInsert myInsert = new CCShaderSourceInsert();
			String[] myParts = theLine.trim().split(Pattern.quote(" "));
			if(myParts.length < 2)return;
			String myKey = myParts[1];
			switch(myParts[0]){
			case "@define":
				_mySourceDefineMap.put(myKey, myInsert);
				break;
			case "@apply":
				_mySourceApplyMap.put(myKey, myInsert);
				break;
			}
			_myParts.add(myInsert);
		}else{
			_myParts.add(theLine + "\n");
		}
	}
	
	public String source(){
		StringBuffer myResult = new StringBuffer();
		for(Object myPart:_myParts){
			myResult.append(myPart.toString());
		}
		return myResult.toString();
	}
}
