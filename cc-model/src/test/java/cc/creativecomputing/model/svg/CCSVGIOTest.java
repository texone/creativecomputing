package cc.creativecomputing.model.svg;

import org.junit.Test;
import static org.junit.Assert.*;

public class CCSVGIOTest {

    @Test
    public void testParseUnitSize() {
    	assertEquals(CCSVGIONew.parseUnitSize("1"), 1f, 0f);
    }
    
    
}