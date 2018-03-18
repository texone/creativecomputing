package cc.creativecomputing.demo.kle.svgled;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMatrix32;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.model.svg.CCSVGElement;
import cc.creativecomputing.model.svg.CCSVGGroup;
import cc.creativecomputing.model.svg.CCSVGPath;

public class CCLedGroup extends ArrayList<CCLed>{
	
	public CCLedGroup(CCSVGGroup theGroup){
		CCLog.info(theGroup.name(), theGroup.transform());
		
		CCMatrix32 myElementMatrix  = theGroup.transform();
		
		for(CCSVGElement myElement:theGroup){
			CCSVGGroup mySubGroup = (CCSVGGroup)myElement;
			CCMatrix32 myLensMatrix  = myElementMatrix.clone();
			if(mySubGroup.transform() == null) {
				CCLog.info(myLensMatrix, mySubGroup.transform(), myElement);
				continue;
			}
			myLensMatrix.apply(mySubGroup.transform());
		
			myElement = mySubGroup.child(0);
			if(myElement.name() == null || !myElement.name().startsWith("Lens"))continue;
			String myName = myElement.name();
			String[] myParts = myName.split("-");
			CCLedType myType = CCLedType.LENS_10;
			double myDiameter = 10;
			switch(myParts[1]){
			case "10mm":
				myType = CCLedType.LENS_10;
				myDiameter = 10;
				break;
			case "14.8mm":
				myType = CCLedType.LENS_14_8;
				myDiameter = 14.8;
				break;
			case "20mm":
				myType = CCLedType.LENS_20;
				myDiameter = 20.0;
				break;
			}
			int myChannel = Integer.parseInt(myParts[2]);
			mySubGroup = (CCSVGGroup)myElement;
			myElement = mySubGroup.child(0);
			CCSVGPath myPath = (CCSVGPath)myElement;
			int myCounter = 0;
			CCVector2 myCenter = new CCVector2();
			for(CCLinearSpline myContours:myPath.contours()){
				for(CCVector3 myVertex:myContours){
					myCenter.addLocal(myVertex.x, myVertex.y);
					myCounter++;
				}
			}
			myCenter.multiplyLocal(1d / myCounter);
			myCenter = myLensMatrix.transform(myCenter);
			add(new CCLed(myCenter, myType, myDiameter, myChannel));
		}
		Collections.sort(this);
		for(CCLed myLed:this) {
			CCLog.info( myLed.channel, myLed.center, myLed.type, myLed.diameter);
		}
	}
}