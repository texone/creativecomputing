package cc.creativecomputing.kle.simple.util;

import java.nio.file.Path;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.io.xml.CCXMLIO;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class CCKle1Kle2Converter {

	public static void convertMapping(Path theKle1Path, Path theKle2Path){
		CCDataElement theKle1Mapping = CCXMLIO.createXMLElement(theKle1Path);
		CCDataElement theKle2Mappings = new CCDataElement("mappings");
		CCDataElement theKle2Mapping = theKle2Mappings.createChild("mapping");
		theKle2Mapping.addAttribute("name", "motors");
		int myColumns = 0;
		int myRows = 0;
		for(CCDataElement myChannelXML:theKle1Mapping){
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
		CCDataElement theKle1Sculpture = CCXMLIO.createXMLElement(theKle1Path);
		CCLog.info(theKle1Sculpture);
		CCDataElement theKle1Elements = theKle1Sculpture.child("elements");
		CCDataElement theKle2Sculpture = new CCDataElement("sculpture");
		CCDataElement theKle2Elements = theKle2Sculpture.createChild("elements");
		
		for(CCDataElement myKle1Element:theKle1Elements){
			CCDataElement myKle2Element = theKle2Elements.createChild("element");
			myKle2Element.addAttribute("id", myKle1Element.intAttribute("id"));
			
			CCDataElement myKle1Motors = myKle1Element.child("motors");
			CCDataElement myKle1Motors0 = myKle1Motors.child(0);
			CCDataElement myKle1Motors1 = myKle1Motors.child(1);
			
			CCDataElement myKle2Motors = myKle2Element.createChild("motors");
			
			CCDataElement myKle2Motors0 = myKle2Motors.createChild("motor");
			CCVector3 myMotor0 = new CCVector3(
				myKle1Motors0.doubleAttribute("x"),
				myKle1Motors0.doubleAttribute("y"),
				myKle1Motors0.doubleAttribute("z")
			);
			myKle2Motors0.addAttribute("id", myKle1Motors0.intAttribute("id"));
			CCDataElement myKle2Position0 = myKle2Motors0.createChild("position");
			myKle2Position0.addAttribute("x", myKle1Motors0.doubleAttribute("x"));
			myKle2Position0.addAttribute("y", myKle1Motors0.doubleAttribute("y"));
			myKle2Position0.addAttribute("z", myKle1Motors0.doubleAttribute("z"));
			
			CCDataElement myKle2Motors1 = myKle2Motors.createChild("motor");
			CCVector3 myMotor1 = new CCVector3(
				myKle1Motors1.doubleAttribute("x"),
				myKle1Motors1.doubleAttribute("y"),
				myKle1Motors1.doubleAttribute("z")
			);
			myKle2Motors1.addAttribute("id", myKle1Motors1.intAttribute("id"));
			CCDataElement myKle2Position1= myKle2Motors1.createChild("position");
			myKle2Position1.addAttribute("x", myKle1Motors1.doubleAttribute("x"));
			myKle2Position1.addAttribute("y", myKle1Motors1.doubleAttribute("y"));
			myKle2Position1.addAttribute("z", myKle1Motors1.doubleAttribute("z"));
			
			CCVector3 myCenter = myMotor0.add(myMotor1).multiply(0.5);

			CCDataElement myKle2Connection0 = myKle2Motors0.createChild("connectionPosition");
			myKle2Connection0.addAttribute("x", myCenter.x);
			myKle2Connection0.addAttribute("y", myCenter.y);
			myKle2Connection0.addAttribute("z", myCenter.z);
			
			CCDataElement myKle2Connection1 = myKle2Motors1.createChild("connectionPosition");
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
