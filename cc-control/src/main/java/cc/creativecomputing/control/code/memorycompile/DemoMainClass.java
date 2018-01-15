package cc.creativecomputing.control.code.memorycompile;

import cc.creativecomputing.control.code.memorycompile.test.DynaClass;

public class DemoMainClass {

	
	
    public static void main(final String[] args) {
    	
    	CCInMemoryExecutionManager myExecutionManager = new CCInMemoryExecutionManager(DynaClass.class, null);
    	
    	while(true){

    		myExecutionManager.update();
    		System.out.println(myExecutionManager.runMethod("toString"));
	        
    		try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}
    }
}