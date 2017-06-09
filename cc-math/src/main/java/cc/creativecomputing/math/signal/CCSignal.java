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
public abstract class CCSignal extends CCSignalSettings{
	
	protected double _myOffsetX = 0;
	protected double _myOffsetY = 0;
	protected double _myOffsetZ = 0;
	
	protected CCSignalSettings _mySettings;
	
	public CCSignal(CCSignalSettings theSettings){
		_mySettings = theSettings == null ? this : theSettings;
	}
	
	public CCSignal(){
		this(null);
	}
	
	protected void settings(CCSignalSettings theSettings){
		_mySettings = theSettings;
	}
	
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
		double myScale = _mySettings.scale();
		double myFallOff = _mySettings.gain();
		
		int myOctaves = CCMath.floor(_mySettings.octaves());
		double[] myResult = null;
		double myAmp = 0;
		
		for(int i = 0; i < myOctaves;i++){
			double[] myValues = signalImpl(theX * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _mySettings.gain();
			myScale *= _mySettings.lacunarity();
		}
		double myBlend = _mySettings.octaves() - myOctaves;
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
		double myScale = _mySettings.scale();
		double myFallOff = _mySettings.gain();
		
		int myOctaves = CCMath.floor(_mySettings.octaves());
		double[] myResult = null;
		double myAmp = 0;
		
		for(int i = 0; i < myOctaves;i++){
			double[] myValues = signalImpl(theX * myScale, theY * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _mySettings.gain();
			myScale *= _mySettings.lacunarity();
		}
		double myBlend = _mySettings.octaves() - myOctaves;
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
		double myScale = _mySettings.scale();
		double myFallOff = _mySettings.gain();
		
		int myOctaves = CCMath.floor(_mySettings.octaves());
		double[] myResult = null;
		double myAmp = 0;
		
		for(int i = 0; i < myOctaves;i++){
			double[] myValues = signalImpl(theX * myScale, theY * myScale, theZ * myScale);
			if(myResult == null)myResult = new double[myValues.length];
			for(int j = 0; j < myResult.length;j++) {
				myResult[j] += myValues[j] * myFallOff;
			}
			myAmp += myFallOff;
			myFallOff *= _mySettings.gain();
			myScale *= _mySettings.lacunarity();
		}
		double myBlend = _mySettings.octaves() - myOctaves;
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
	
	
	public void offset(final double theX, final double theY, final double theZ){
		_myOffsetX = theX;
		_myOffsetY = theY;
		_myOffsetZ = theZ;
	}
}
