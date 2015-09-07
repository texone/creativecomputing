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
package cc.creativecomputing.model.obj;

import java.nio.file.Path;

import cc.creativecomputing.math.CCColor;


/**
 * You use a material to set the material reflectance properties of polygons. 
 * The ambient, diffuse, and specular properties affect how these components 
 * of incident light are reflected. Emission is used for materials that appear 
 * to give off their own light. Shininess can vary from 0 to 128, with the higher 
 * values producing a larger specular highlight on the material surface.
 * @author texone
 *
 */
public class CCMaterial {
	
	public static CCMaterial DEFAULT = new CCMaterial(
		new CCColor(0.2f,0.2f,0.2f),
		new CCColor(0.8f,0.8f,0.8f),
		new CCColor(0.1f,0.1f,0.1f),
		null,
		-1
	);
	
	private CCColor _myAmbient;
	private CCColor _myDiffuse;
	private CCColor _mySpecular;
	private CCColor _myEmission;
	
	private float _myAlpha = 1;
	
	private int _myShininess = -1;
	
	
	
	/**
	 * Creates a new material with the given values.
	 * @param theAmbient
	 * @param theDiffuse
	 * @param theSpecular
	 * @param theEmission
	 * @param theShininess
	 */
	public CCMaterial(CCColor theAmbient, CCColor theDiffuse, CCColor theSpecular, CCColor theEmission, int theShininess) {
		_myAmbient = theAmbient;
		_myDiffuse = theDiffuse;
		_mySpecular = theSpecular;
		_myEmission = theEmission;
		_myShininess = theShininess;
	}
	
	/**
	 * Creates a new material where all colors are set to white. This gives
	 * easy control over the lighting by just change the color of the light.
	 */
	public CCMaterial(){
		_myAmbient = new CCColor(1f);
		_myDiffuse = new CCColor(1f);
		_mySpecular = new CCColor(1f);
		_myEmission = new CCColor(0);
	}

	/**
	 * Sets the ambient reflectance for shapes drawn to the screen. 
	 * This is combined with the ambient light component of environment. 
	 * The color components set through the parameters define the reflectance. 
	 * For example setting v1=255, v2=126, v3=0, would cause all the red light 
	 * to reflect and half of the green light to reflect. Used in combination 
	 * with emissive, specular(), and shininess() in setting the material properties of shapes.
	 *
	 * @param theRed
	 * @param theGreen
	 * @param theBlue
	 */
	public void ambient(final float theRed, final float theGreen, final float theBlue){
		_myAmbient.set(theRed, theGreen, theBlue);
	}
	
	public void ambient(final int theRed, final int theGreen, final int theBlue){
		_myAmbient.set(theRed, theGreen, theBlue);
	}
	
	public void ambient(final CCColor theColor){
		_myAmbient.set(theColor);
	}
	
	public CCColor ambient(){
		return _myAmbient;
	}
	
	/**
	 * Sets the diffuse reflectance for shapes drawn to the screen. 
	 * This is combined with the diffuse light component of environment. 
	 * The color components set through the parameters define the reflectance. 
	 * For example setting v1=255, v2=126, v3=0, would cause all the red light 
	 * to reflect and half of the green light to reflect. Used in combination 
	 * with emissive, specular(), and shininess() in setting the material properties of shapes.
	 *
	 * @param theRed
	 * @param theGreen
	 * @param theBlue
	 */
	public void diffuse(final float theRed, final float theGreen, final float theBlue){
		_myDiffuse.set(theRed, theGreen, theBlue);
	}
	
	public void diffuse(final int theRed, final int theGreen, final int theBlue){
		_myDiffuse.set(theRed, theGreen, theBlue);
	}
	
	public void diffuse(final CCColor theColor){
		_myDiffuse.set(theColor);
	}
	
	/**
	 * 
	 * @return
	 */
	public CCColor diffuse(){
		return _myDiffuse;
	}
	
	/**
	 * Sets the specular color of the materials used for shapes drawn to the screen, 
	 * which sets the color of highlights. Specular refers to light which bounces off 
	 * a surface in a preferred direction (rather than bouncing in all directions 
	 * like a diffuse light). Used in combination with emissive, ambient(), and shininess() 
	 * in setting the material properties of shapes.
	 *
	 * @param theRed
	 * @param theGreen
	 * @param theBlue
	 */
	public void specular(final float theRed, final float theGreen, final float theBlue){
		_mySpecular.set(theRed, theGreen, theBlue);
	}
	
	public void specular(final int theRed, final int theGreen, final int theBlue){
		_mySpecular.set(theRed, theGreen, theBlue);
	}
	
	public void specular(final CCColor theColor){
		_mySpecular.set(theColor);
	}
	
	public CCColor specular(){
		return _mySpecular;
	}
	
	/**
	 * Sets the emissive color of the material used for drawing shapes drawn 
	 * to the screen. Used in combination with ambient, specular(), and shininess() 
	 * in setting the material properties of shapes.
	 *
	 * @param theRed
	 * @param theGreen
	 * @param theBlue
	 */
	public void emission(final float theRed, final float theGreen, final float theBlue){
		_myEmission.set(theRed, theGreen, theBlue);
	}
	
	public void emission(final int theRed, final int theGreen, final int theBlue){
		_myEmission.set(theRed, theGreen, theBlue);
	}
	
	public void emission(final CCColor theColor){
		_myEmission.set(theColor);
	}
	
	public CCColor emission(){
		return _myEmission;
	}
	
	/**
	 * Sets the amount of gloss in the surface of shapes. Used in combination with 
	 * ambient, specular(), and emissive() in setting the material properties of shapes.
	 * @param theShininess value between 0 and 128
	 */
	public void shininess(final int theShininess){
		_myShininess = theShininess;
	}
	
	public void shininess(final float theShininess){
		_myShininess = (int)(theShininess * 128);
	}
	
	public int shininess(){
		return _myShininess;
	}
	
	public void alpha(final float theAlpha){
		_myAlpha = theAlpha;
	}
	
	private Path _myTexture;
	
	public Path texture(){
		return _myTexture;
	}
	
	public void texture(Path theTexture){
		_myTexture = theTexture;
	}
}
