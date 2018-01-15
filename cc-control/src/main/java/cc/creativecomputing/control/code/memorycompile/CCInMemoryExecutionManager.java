package cc.creativecomputing.control.code.memorycompile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil;

public class CCInMemoryExecutionManager {
	
	private CCInMemoryCompiler _myCompiler;
	
	private Class<?> _myMainClass;
	private Object _myMainObjectInstance;

	public CCInMemoryExecutionManager(Class<?> theMainClass, Object theMainInstance){
		_myCompiler = new CCInMemoryCompiler(theMainClass);
		_myMainObjectInstance = theMainInstance;
	}
	
    public Object runMethod(final String theMethod, Object...theParameter) {
    	Class<?>[] myTypes = new Class[theParameter.length];
    	for(int i = 0; i < theParameter.length;i++){
    		myTypes[i] = theParameter[i].getClass();
    	}
    	if(_myMainClass == null)return null;
    	try {
	    	Method myMethod = _myMainClass.getDeclaredMethod(theMethod, myTypes);
	    	myMethod.setAccessible(true);
	    	return myMethod.invoke(_myMainObjectInstance, theParameter);
    	} catch (Exception e) {
			throw new CCInMemoryExecutionException(e);
		}
    }
	
	public boolean update(){
		try {
			if(!_myCompiler.needsUpdated())return false;
	        
	        final CCInMemoryCompilerFeedback compilerFeedback = _myCompiler.compile();
	        System.out.println("\n\nCOMPILER FEEDBACK: " + compilerFeedback);
	
	        if (compilerFeedback != null && compilerFeedback.success) {
	        		
	        	_myMainClass = _myCompiler.getCompiledMainClass();
				
	        	Object myNewMainObjectInstance = _myMainClass.newInstance();
	        	CCLog.info(_myMainObjectInstance);
//	        	if(_myMainObjectInstance != null)CCInMemoryReferenceManager.copy(_myMainObjectInstance, myNewMainObjectInstance);
	        	_myMainObjectInstance = myNewMainObjectInstance;
	        }
	        return true;
		} catch (Exception e) {
			throw new CCInMemoryExecutionException(e);
		}
	}
	
	public Object mainObject(){
		return _myMainObjectInstance;
	}
}
