package cc.creativecomputing.kle.simple.util;

import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCKle1Kle2Converter {

	public static void convertMapping(Path theKle1Path, Path theKle2Path){
		CCXMLElement theKle1Mapping = CCXMLIO.createXMLElement(theKle1Path);
		CCXMLElement theKle2Mappings = new CCXMLElement("mappings");
		CCXMLElement theKle2Mapping = theKle2Mappings.createChild("mapping");
		theKle2Mapping.addAttribute("name", "motors");
		int myColumns = 0;
		int myRows = 0;
		for(CCXMLElement myChannelXML:theKle1Mapping){
			myChannelXML.addAttribute("depth", 0);
			theKle2Mapping.addChild(myChannelXML);
			myColumns = CCMath.max(myChannelXML.intAttribute("column"), myColumns);
			myRows = CCMath.max(myChannelXML.intAttribute("row"), myRows);
		}
		theKle2Mapping.addAttribute("columns", myColumns + 1);
		theKle2Mapping.addAttribute("rows", myRows + 1);
		theKle2Mapping.addAttribute("depth", 1);
		theKle2Mapping.addAttribute("frameRate", 5);
		theKle2Mapping.addAttribute("bits", 16);
		
		CCXMLIO.saveXMLElement(theKle2Mappings, theKle2Path);
	}
	
	public static void convertSculpture(Path theKle1Path, Path theKle2Path){
		CCXMLElement theKle1Sculpture = CCXMLIO.createXMLElement(theKle1Path);
		CCLog.info(theKle1Sculpture);
		CCXMLElement theKle1Elements = theKle1Sculpture.child("elements");
		CCXMLElement theKle2Sculpture = new CCXMLElement("sculpture");
		CCXMLElement theKle2Elements = theKle2Sculpture.createChild("elements");
		
		for(CCXMLElement myKle1Element:theKle1Elements){
			CCXMLElement myKle2Element = theKle2Elements.createChild("element");
			myKle2Element.addAttribute("id", myKle1Element.intAttribute("id"));
			
			CCXMLElement myKle1Motors = myKle1Element.child("motors");
			CCXMLElement myKle1Motors0 = myKle1Motors.child(0);
			CCXMLElement myKle1Motors1 = myKle1Motors.child(1);
			
			CCXMLElement myKle2Motors = myKle2Element.createChild("motors");
			
			CCXMLElement myKle2Motors0 = myKle2Motors.createChild("motor");
			CCVector3 myMotor0 = new CCVector3(
				myKle1Motors0.doubleAttribute("x"),
				myKle1Motors0.doubleAttribute("y"),
				myKle1Motors0.doubleAttribute("z")
			);
			myKle2Motors0.addAttribute("id", myKle1Motors0.intAttribute("id"));
			CCXMLElement myKle2Position0 = myKle2Motors0.createChild("position");
			myKle2Position0.addAttribute("x", myKle1Motors0.doubleAttribute("x"));
			myKle2Position0.addAttribute("y", myKle1Motors0.doubleAttribute("y"));
			myKle2Position0.addAttribute("z", myKle1Motors0.doubleAttribute("z"));
			
			CCXMLElement myKle2Motors1 = myKle2Motors.createChild("motor");
			CCVector3 myMotor1 = new CCVector3(
				myKle1Motors1.doubleAttribute("x"),
				myKle1Motors1.doubleAttribute("y"),
				myKle1Motors1.doubleAttribute("z")
			);
			myKle2Motors1.addAttribute("id", myKle1Motors1.intAttribute("id"));
			CCXMLElement myKle2Position1= myKle2Motors1.createChild("position");
			myKle2Position1.addAttribute("x", myKle1Motors1.doubleAttribute("x"));
			myKle2Position1.addAttribute("y", myKle1Motors1.doubleAttribute("y"));
			myKle2Position1.addAttribute("z", myKle1Motors1.doubleAttribute("z"));
			
			CCVector3 myCenter = myMotor0.add(myMotor1).multiply(0.5);

			CCXMLElement myKle2Connection0 = myKle2Motors0.createChild("connectionPosition");
			myKle2Connection0.addAttribute("x", myCenter.x);
			myKle2Connection0.addAttribute("y", myCenter.y);
			myKle2Connection0.addAttribute("z", myCenter.z);
			
			CCXMLElement myKle2Connection1 = myKle2Motors1.createChild("connectionPosition");
			myKle2Connection1.addAttribute("x", myCenter.x);
			myKle2Connection1.addAttribute("y", myCenter.y);
			myKle2Connection1.addAttribute("z", myCenter.z);
		}
		
		
		
		CCXMLIO.saveXMLElement(theKle2Sculpture, theKle2Path);
	}
	
	public static void main(String[] args) {
		CCKle1Kle2Converter.convertMapping(CCNIOUtil.dataPath("manila/setup.xml"), CCNIOUtil.dataPath("manila_kle2/mapping.xml"));
		CCKle1Kle2Converter.convertSculpture(CCNIOUtil.dataPath("manila/sculpture.xml"), CCNIOUtil.dataPath("manila_kle2/sculpture.xml"));
	}
}
