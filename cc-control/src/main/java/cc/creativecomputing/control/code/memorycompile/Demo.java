package cc.creativecomputing.control.code.memorycompile;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.code.memorycompile.test.DynaClass;
import cc.creativecomputing.control.code.memorycompile.test.Test;

public class Demo {


    public static void runToString(final String className) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        final Class<?> theClass = myCompiler.getCompiledClass(className);
        final Object instance = theClass.newInstance();
        System.out.println(instance);
    }

    public static void runMain(final String className, final String[] args) throws IllegalAccessException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException {
        final Class<?> theClass = myCompiler.getCompiledClass(className);
        final Method mainMethod = theClass.getDeclaredMethod("main", String[].class);
        mainMethod.invoke(null, new Object[] { args });
    }
    
    static CCInMemoryCompiler myCompiler;
	
    public static void main(final String[] args) {
    	final CCInMemoryCompilerSourceCode cls1source = new CCInMemoryCompilerSourceCode(DynaClass.class);
        final CCInMemoryCompilerSourceCode cls2source = new CCInMemoryCompilerSourceCode(Test.class);

        final List<CCInMemoryCompilerSourceCode> classSources = new ArrayList<>();
        classSources.add(cls1source);
        classSources.add(cls2source);

        myCompiler = new CCInMemoryCompiler(classSources);
        
    	while(true){
    		if(!myCompiler.needsUpdated())continue;
	        
	        final CCInMemoryCompilerFeedback compilerFeedback = myCompiler.compile();
	        System.out.println("\n\nCOMPILER FEEDBACK: " + compilerFeedback);
	
	        if (compilerFeedback != null && compilerFeedback.success) {
	
	            try {
	                System.out.println("\nTOSTRING DEMO:");
	                runToString(cls1source.className);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	
	            try {
	                System.out.println("\nMAIN DEMO:");
	                runMain(cls1source.className, new String[] { "test1", "test2" });
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        
	        try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}