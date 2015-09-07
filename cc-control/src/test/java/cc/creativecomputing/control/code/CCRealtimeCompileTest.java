package cc.creativecomputing.control.code;

import org.junit.Test;

import cc.creativecomputing.core.logging.CCLog;
import static org.junit.Assert.*;

public class CCRealtimeCompileTest {

	@Test
	public void testRealtimeCompileInit(){
		CCRealtimeCompile<CCCompileObject> myCompiler = new CCRealtimeCompile<CCCompileObject>("cc.creativecomputing.CCRealtimeClass", CCCompileObject.class);
		
		assertEquals("CCRealtimeClass", myCompiler.className());
		assertEquals("cc.creativecomputing", myCompiler.packageName());
		
		CCLog.info(myCompiler.codeTemplate());
	}
}
