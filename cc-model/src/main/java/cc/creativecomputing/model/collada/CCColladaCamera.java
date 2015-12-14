/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.model.collada;

import cc.creativecomputing.graphics.CCCamera;
import cc.creativecomputing.io.xml.CCXMLElement;
import cc.creativecomputing.math.CCMath;

public class CCColladaCamera extends CCColladaElement{

	private CCCamera _myCamera;
	
	private float _myFov;
	private float _myAspect;
	
	private float _myZnear;
	private float _myZfar;
	
	CCColladaCamera(CCXMLElement theCameraXML) {
		super(theCameraXML);
		
		for(CCXMLElement myChildXML:theCameraXML){
			switch(myChildXML.name()){
			case "optics":
				readOptics(myChildXML);
				break;
			}
		}
		
		_myCamera = new CCCamera(_myFov * CCMath.DEG_TO_RAD, 1f / _myAspect, _myZnear, _myZfar * 100);
	}
	
	private void readOptics(CCXMLElement theOpticsXML){
		CCXMLElement myTechniquesXML = theOpticsXML.child("technique_common");
		for(CCXMLElement myChildXML:myTechniquesXML){
			switch(myChildXML.name()){
			case "perspective":
				float myXfov = 0;
				float myYfov = 0;
				float myAspect = 0;
				CCXMLElement myXfovXML = myChildXML.child("xfov");
				CCXMLElement myYfovXML = myChildXML.child("yfov");
				CCXMLElement myAspectXML = myChildXML.child("aspect_ratio");
				
				if(myXfovXML != null && myYfovXML != null){
					myXfov = myXfovXML.floatContent();
					myYfov = myYfovXML.floatContent();
					myAspect = myXfov / myYfov;
				}
				if(myXfovXML != null && myAspectXML != null){
					myXfov = myXfovXML.floatContent();
					myAspect = myAspectXML.floatContent();
					myYfov = myXfov / myAspect;
				}
				if(myYfovXML != null && myAspectXML != null){
					myYfov = myYfovXML.floatContent();
					myAspect = myAspectXML.floatContent();
					myXfov =  myAspect * myYfov;
				}
				
				_myFov = myXfov;
				_myAspect = myAspect;
				
				CCXMLElement myZnearXML = myChildXML.child("znear");
				CCXMLElement myZFarXML = myChildXML.child("zfar");
				
				_myZnear = myZnearXML.floatContent();
				_myZfar = myZFarXML.floatContent();
				
				break;
				
			}
		}
	}

	public CCCamera camera(){
		return _myCamera;
	}
}
