package cc.creativecomputing.demo;

import cc.creativecomputing.io.CCIOUtil;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;

public class CCXMLDemo {
public static void main(String[] args) {
	for(int i = 0; i < 100;i++) {
		CCDataElement bla = new CCDataElement("bla");
		CCXMLIO.saveXMLElement(bla, CCNIOUtil.dataPath("content/surve.xml"));
	}
}
}
