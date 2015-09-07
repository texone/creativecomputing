package cc.creativecomputing.app.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CCCommandLineParser {
    
	public static class CCCommandlineParserException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = 6199580651929508241L;

		public CCCommandlineParserException() {
			super();
		}

		public CCCommandlineParserException(String theMessage, Throwable theCause, boolean theEnableSuppression, boolean theWritableStackTrace) {
			super(theMessage, theCause, theEnableSuppression, theWritableStackTrace);
		}

		public CCCommandlineParserException(String theMessage, Throwable theCause) {
			super(theMessage, theCause);
		}

		public CCCommandlineParserException(String theMessage) {
			super(theMessage);
		}

		public CCCommandlineParserException(Throwable theCause) {
			super(theCause);
		}
		
	}
	
    public static class CCCommandlineOption{
    	private String _myCommandlineOption;
    	private List<String> _myValues;
    	private boolean _myHasArguments;
    	
    	private CCCommandlineOption(String theCommandlineOption){
    		_myCommandlineOption = theCommandlineOption;
    		_myValues = new ArrayList<>();
    	}
    	
    	public boolean hasArguments(){
    		return _myHasArguments;
    	}
    	
    	public String name(){
    		return _myCommandlineOption;
    	}
    	
    	public String value(){
    		if(_myValues.size() <= 0)return null;
    		return _myValues.get(0);
    	}
    	
    	public List<String> values(){
    		return _myValues;
    	}
    }
    
    public static class CCCommandlineParameter{
    	private String _myName;
    	private boolean _myHasArguments;
    	private String _myDescription;
    	
    	public CCCommandlineParameter(String theName, boolean theHasArguments, String theDescription){
    		_myName = theName;
    		_myHasArguments = theHasArguments;
    		_myDescription = theDescription;
    	}
    	
    	public String name(){
    		return _myName;
    	}
    	
    	public String description(){
    		return _myDescription;
    	}
    }
    
    private Map<String, CCCommandlineOption> _mySetOptions = new HashMap<>();
    private Map<String, CCCommandlineOption> _myCustomOptions = new HashMap<>();
    private Map<String, CCCommandlineParameter> _myParameters = new HashMap<>();
	private String _myAppName;
	
	public CCCommandLineParser(String theAppName){
		_myAppName = theAppName; 
	}
	
	public String appName(){
		return _myAppName;
	}
	
	public List<CCCommandlineOption> options(){
		return new ArrayList<>(_mySetOptions.values());
	}
	
	public List<CCCommandlineOption> customOptions(){
		return new ArrayList<>(_myCustomOptions.values());
	}
	
	public void parse(String[] theArguments){
		_mySetOptions.clear();
		CCCommandlineOption myOption = null;
		for(String myArgument:theArguments){
			if(myArgument.startsWith("-")){
				String myOptionName = myArgument.replaceAll("-", "");
				myOption = new CCCommandlineOption(myOptionName);
				CCCommandlineParameter myParameter = _myParameters.get(myOptionName);
				if(myParameter != null){
					_mySetOptions.put(myOptionName, myOption);
					myOption._myHasArguments = myParameter._myHasArguments;
				}else{
					myOption._myHasArguments = true;
					_myCustomOptions.put(myOptionName, myOption);
				}
				continue;
			}else{
				if(myOption == null)continue;
				if(!myOption._myHasArguments){
					throw new CCCommandlineParserException(myOption.name() +" is not allowed to have arguments!");
				}
				myOption._myValues.add(myArgument);
			}
		}
	}
	
//	public void printHelp(){
//		int myMaxLength = 0;
//		for(CCCommandlineParameter myParameter:_myParameters.values()){
//			myParameter._myName
//		}
//	}
	
	/**
	 * Creates an Option using the specified parameters.
	 * @param theOption  short representation of the option
	 * @param theHasArg  specifies whether the Option takes an argument or not
	 * @param theDescription describes the function of the option
	 */
	public void addOption(String theName, boolean theHasArguments, String theDescription){
		_myParameters.put(theName, new CCCommandlineParameter(theName, theHasArguments, theDescription));
	}
	
	/**
	 * Creates an Option using the specified parameters. The option does not take an argument.
	 * @param theOption  short representation of the option
	 * @param theDescription describes the function of the option
	 */
	public void addOption(String theOption, String theDescription){
		addOption(theOption, false, theDescription);
	}
	
	public String optionValue(String theOption){
		if(!_mySetOptions.containsKey(theOption))return null;
		return _mySetOptions.get(theOption).value();
	}
	
	public List<String> optionValues(String theOption){
		if(!_mySetOptions.containsKey(theOption))return null;
		return _mySetOptions.get(theOption).values();
	}
	
	public String valueList(String theOption){
		if(!_mySetOptions.containsKey(theOption))return null;
		List<String> myValues = _mySetOptions.get(theOption).values();
		if(myValues.size()== 0)return "";
		StringBuffer myResult = new StringBuffer(myValues.get(0));
		for(int i = 1; i < myValues.size();i++){
			myResult.append(",");
			myResult.append(myValues.get(i));
		}
		return myResult.toString();
	}
	
	public boolean hasOption(String theOption){
		return _mySetOptions.containsKey(theOption);
	}
	
	public boolean hasMultipleValues(String theOption){
		if(!hasOption(theOption))return false;
		return _mySetOptions.get(theOption).values().size() > 1;
	}
	
	public String customOptionValue(String theOption){
		if(!_myCustomOptions.containsKey(theOption))return null;
		return _myCustomOptions.get(theOption).value();
	}
	
	public List<String> customOptionValues(String theOption){
		if(!_myCustomOptions.containsKey(theOption))return null;
		return _myCustomOptions.get(theOption).values();
	}
	
	public boolean hasCustomOption(String theOption){
		return _myCustomOptions.containsKey(theOption);
	}
}
