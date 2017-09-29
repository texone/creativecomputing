package cc.creativecomputing.model.svg;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;

public class CCMain {
	public static void main(String[] args) {
//		CCDataObject myObject = CCDataIO.createDataObject(CCNIOUtil.dataPath("bot1.svg"), CCDataFormats.XML);
//		CCLog.info(myObject.containsKey("svg"));
//		/*
//		if (!theSVG.name().equals("svg")) {
//			throw new RuntimeException("root is not <svg>, it's <" + theSVG.name() + ">");
//		}*/
//		CCLog.info(myObject);
		
		CCSVGIONew.newSVG(CCNIOUtil.dataPath("bot1.svg"));
	}
}
