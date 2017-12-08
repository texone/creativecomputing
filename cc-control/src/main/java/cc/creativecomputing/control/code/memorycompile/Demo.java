package cc.creativecomputing.control.code.memorycompile;

import java.util.ArrayList;
import java.util.List;

public class Demo {

	
	
    public static void main(final String[] args) {
    	
    	while(true){

	        final CCInMemoryCompiler.IMCSourceCode cls1source = new CCInMemoryCompiler.IMCSourceCode(DynaClass.class);
	        final CCInMemoryCompiler.IMCSourceCode cls2source = new CCInMemoryCompiler.IMCSourceCode(Test.class);
	
	        final List<CCInMemoryCompiler.IMCSourceCode> classSources = new ArrayList<>();
	        classSources.add(cls1source);
	        classSources.add(cls2source);
	
	        final CCInMemoryCompiler uCompiler = new CCInMemoryCompiler(classSources);
	        final CompilerFeedback compilerFeedback = uCompiler.compile();
	        System.out.println("\n\nCOMPILER FEEDBACK: " + compilerFeedback);
	
	        if (compilerFeedback != null && compilerFeedback.success) {
	
	            try {
	                System.out.println("\nTOSTRING DEMO:");
	                uCompiler.runToString(cls1source.className);
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	
	            try {
	                System.out.println("\nMAIN DEMO:");
	                uCompiler.runMain(cls1source.className, new String[] { "test1", "test2" });
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
    	}
    }
}