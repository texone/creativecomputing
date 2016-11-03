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
package cc.creativecomputing.math.signal;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector3;

/**
 * This is the base signal class that handles the basic setup, like scale and offset.
 * Here you can also set how fractal values should be calculated. Values are always in the 
 * range 0 to 1.
 * @author christianriekoff
 *
 */
public abstract class CCSignal {
	protected double _myScale = 1;
	
	protected double _myOffsetX = 0;
	protected double _myOffsetY = 0;
	protected double _myOffsetZ = 0;
	
	protected double _myOctaves = 1;
	protected double _myGain = 0.5f;
	protected double _myLacunarity = 2;
	
	/**
	 * Override this method to define how the 3d is calculated
	 * @param theX
	 * @param theY
	 * @param theZ
	 * @return the calculated value
	 */
	public abstract double[] signalImpl(final double theX, final double theY, final double theZ);
	
	public double[] signalImpl(final double theX, final double theY) {
		return signalImpl(theX,theY,0);
	}
	
	public double[] signalImpl(final double theX) {
		return signalImpl(theX,0);
	}
	
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theX x coord for the noise
	 * @return multiple values
	 */
	public final double[] values(final double theX){
		double myScale = _myScale;
		double myFallOff = _myGain;
		
		int myOctaves = CCMath.floor(_myOctaves);
		double[] myResult = null;
		double myAmp = 0;
		
		for(int i = 0; i < myOctaves;i++){
			double[] myValues = signalImpl(theX * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _myGain;
			myScale *= _myLacunarity;
		}
		double myBlend = _myOctaves - myOctaves;
		if(myBlend > 0) {
			double[] myValues = signalImpl(theX * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff * myBlend;
			}
			myAmp += myFallOff * myBlend;
		}
		if(myAmp > 0){
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] /= myAmp;
			}
		}
		return myResult;
	}
	
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theX x coord for the noise
	 * @return the value for the given coordinates.
	 */
	public double value(final double theX) {
		return values(theX)[0];
	}
	
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @return multiple values
	 */
	public final double[] values(final double theX, final double theY){
		double myScale = _myScale;
		double myFallOff = _myGain;
		
		int myOctaves = CCMath.floor(_myOctaves);
		double[] myResult = null;
		double myAmp = 0;
		
		for(int i = 0; i < myOctaves;i++){
			double[] myValues = signalImpl(theX * myScale, theY * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _myGain;
			myScale *= _myLacunarity;
		}
		double myBlend = _myOctaves - myOctaves;
		if(myBlend > 0) {
			double[] myValues = signalImpl(theX * myScale, theY * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff * myBlend;
			}
			myAmp += myFallOff * myBlend;
		}
		if(myAmp > 0){
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] /= myAmp;
			}
		}
		return myResult;
	}
	
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theVector coordinates for the noise
	 * @return multiple values
	 */
	public double[] values(final CCVector2 theVector) {
		return values(theVector.x, theVector.y);
	}
	
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @return the value for the given coordinates.
	 */
	public double value(final double theX, final double theY) {
		return values(theX, theY)[0];
	}
	
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theVector coordinates for the noise
	 * @return the value for the given coordinates.
	 */
	public double value(final CCVector2 theVector) {
		return values(theVector)[0];
	}
	
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @param theZ z coord for the noise
	 * @return multiple values
	 */
	public final double[] values(final double theX, final double theY, final double theZ) {
		double myScale = _myScale;
		double myFallOff = _myGain;
		
		int myOctaves = CCMath.floor(_myOctaves);
		double[] myResult = null;
		double myAmp = 0;
		
		for(int i = 0; i < myOctaves;i++){
			double[] myValues = signalImpl(theX * myScale, theY * myScale, theZ * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _myGain;
			myScale *= _myLacunarity;
		}
		double myBlend = _myOctaves - myOctaves;
		if(myBlend > 0) {
			double[] myValues = signalImpl(theX * myScale, theY * myScale, theZ * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff * myBlend;
			}
			myAmp += myFallOff * myBlend;
		}
		if(myAmp > 0){
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] /= myAmp;
			}
		}
		return myResult;
	}
	
	/**
	 * Returns multiple values for 3d coordinates, this is useful to get derivatives or 
	 * multiple output values like in case of the worley noise
	 * @param theVector coordinates for the noise
	 * @return multiple values
	 */
	public double[] values(final CCVector3 theVector) {
		return values(theVector.x, theVector.y, theVector.z);
	}
	
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theX x coord for the noise
	 * @param theY y coord for the noise
	 * @param theZ z coord for the noise
	 * @return the value for the given coordinates.
	 */
	public double value(final double theX, final double theY, final double theZ) {
		return values(theX, theY, theZ)[0];
	}
	
	/**
	 * Returns the value for the given coordinates. By default only one band is calculated.
	 * @param theVector coordinates for the noise
	 * @return the value for the given coordinates.
	 */
	public double value(final CCVector3 theVector) {
		return values(theVector)[0];
	}
	
	public double[] noiseImpl(final double theX) {
		return signalImpl(theX,0);
	}
	
	@CCProperty(name = "scale", min = 0, max = 1, defaultValue = 1, digits = 4)
	public void scale(final double theNoiseScale){
		scaleImplementation(theNoiseScale);
	}
	
	protected void scaleImplementation(final double theNoiseScale){
		_myScale = theNoiseScale;
	}
	
	public void offset(final double theX, final double theY, final double theZ){
		_myOffsetX = theX;
		_myOffsetY = theY;
		_myOffsetZ = theZ;
	}
	
	public double scale(){
		return _myScale;
	}
	
	/**
	 * The minimum value is one. You can also set floating numbers to blend
	 * between the result of 2 or three bands.
	 * @param theBands
	 */
	@CCProperty(name = "octaves", min = 1, max = 10, defaultValue = 2)
	public void octaves(final double theBands) {
		octavesImplementation(theBands);
	}
	
	protected void octavesImplementation(final double theBands){
		_myOctaves = CCMath.max(1.0f,theBands);
	}
	
	public double octaves() {
		return _myOctaves;
	}
	
	/**
	 * Controls amplitude change between each band. The default gain
	 * is 0.5 meaning that the influence of every higher band is half as
	 * high as the one from the previous.
	 * @param theGain amplitude change between each band
	 */
	@CCProperty(name = "gain", min = 0, max = 1, defaultValue = 0.5)
	public void gain(final double theGain) {
		gainImplementation(theGain);
	}
	
	protected void gainImplementation(final double theGain) {
		_myGain = theGain;
	}
	
	/**
	 * Returns the amplitude change between each band
	 * @return the amplitude change between each band
	 */
	public double gain() {
		return _myGain;
	}
	
	/**
	 * Lacunarity controls frequency change between each band. The default value
	 * is 2.0 meaning the frequency of every band is twice as high as the previous
	 * @param theLacunarity frequency change between each band
	 */
	@CCProperty(name = "lacunarity", min = 0, max = 10, defaultValue = 2)
	public void lacunarity(final double theLacunarity) {
		lacunarityImplementation(theLacunarity);
	}
	
	protected void lacunarityImplementation(final double theLacunarity) {
		_myLacunarity = theLacunarity;
	}
	
	/**
	 * Returns the frequency change between each band
	 * @return the frequency change between each band
	 */
	public double lacunarity() {
		return _myLacunarity;
	}
}
