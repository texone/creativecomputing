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

import java.util.HashMap;
import java.util.List;

import cc.creativecomputing.io.xml.CCDataElement;


/**
 * <p>
 * Lucerne University of Applied Sciences and Arts <a href="http://www.hslu.ch">http://www.hslu.ch</a>
 * </p>
 * 
 * <p>
 * This source is free; you can redistribute it and/or modify it under the terms of the GNU General Public License and
 * by nameing of the originally author
 * </p>
 * 
 * <p>
 * this class maps the effect-tag
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 1.0
 */
class CCColladaEffect extends CCColladaElement{

	private CCColladaImage _myImage;
	private float[] _myColor;
	private boolean _myHasTexture = false;

	// add support for your chosen effect here
	CCColladaEffect(CCDataElement theEffectXML, CCColladaImages theImages) {
		super(theEffectXML);
		CCDataElement myDiffuse = null;

		// Get the effect type. Common examples are lambert and Phong
		CCDataElement myTechniqueXML = theEffectXML.child("profile_COMMON/technique");
		String myEffectType = myTechniqueXML.child(0).name().toString();

		if (myEffectType.equals("lambert")) {
			// get lambert -> diffuse if possible
			myDiffuse = myTechniqueXML.child("lambert/diffuse");
			// if lambert -> diffuse does not exist then take the transparency value as the diffuse value

			if (myDiffuse == null) // not sure about this constant/transparent...havent seen this in collada file before
									// but leaving it in case
				myDiffuse = myTechniqueXML.child("constant/transparent");

			if (myDiffuse == null) // in case the transparent above doesnt catch the transparent value when a diffuse is
									// not present
				myDiffuse = myTechniqueXML.child("lambert/transparent");
		}

		if (myEffectType.equals("phong")) {
			// get Phong -> diffuse if possible
			myDiffuse = myTechniqueXML.child("phong/diffuse");

			// if Phong -> diffuse does not exist then take the transparency value as the diffuse value
			if (myDiffuse == null)
				myDiffuse = myTechniqueXML.child("phong/transparent");
		}

		// give user error message to tell them where to look to add support for their shader listed in the collada file
		if (myDiffuse == null)
			System.out.print("Unable to find diffuse value. Try adding support for your chosen effect in the Effect.java");

		// if diffuse is null an error will be thrown here
		if (myDiffuse.children().get(0).name().equals("texture")) {
			_myHasTexture = true;
			// read Paramtags
			HashMap<String, CCColladaNewParam> myParameterMap = new HashMap<String, CCColladaNewParam>();
			List<CCDataElement> myNewParams = theEffectXML.children("profile_COMMON/newparam");
			for (CCDataElement myParam : myNewParams) {
				CCColladaNewParam p = new CCColladaNewParam(myParam);
				myParameterMap.put(p.id(), p);
			}
			// set Imagevariable
			String myImageID = myParameterMap.get(myParameterMap.get(myDiffuse.child("texture").attribute("texture")).source()).source();
			_myImage = theImages.image(myImageID);
		}
		if (myDiffuse.children().get(0).name().equals("color")) {
			_myHasTexture = false;
			String[] color = myDiffuse.child("color").content().split(" +");
			Float.parseFloat(color[0]);
			_myColor = new float[] { 
				Float.parseFloat(color[0]), 
				Float.parseFloat(color[1]), 
				Float.parseFloat(color[2]), 
				Float.parseFloat(color[3]) 
			};
		}
	}

	/**
	 * gives the answer if the effect contains a color or an image
	 * 
	 * @return false on color, true on image
	 */
	boolean hasTexture() {
		return _myHasTexture;
	}

	/**
	 * returns the image of the texture
	 * 
	 * @return the image
	 */
	CCColladaImage image() {
		return _myImage;
	}

	/**
	 * returns an Array in the Format [red,green,blue,transparent]
	 * 
	 * @return the color
	 */
	float[] color() {
		return _myColor;
	}

	@Override
	public String toString() {
		String s = "";
		s += "Effect ID '" + _myID + "' contains"
				+ ((_myHasTexture) ? " an Image with " + _myImage : "Colors: Red = " + _myColor[0] + " green = " + _myColor[1] + " blue = " + _myColor[2] + " transparence = " + _myColor[3]);
		return s;
	}

}
