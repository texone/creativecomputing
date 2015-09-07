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
package cc.creativecomputing.image;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

import cc.creativecomputing.image.filter.CCGaussianBlur;

public class CCImageGraphics{
	
	private CCImage _myImage;
	private BufferedImage _myRenderImage;
	private Graphics2D _myGraphics2D;

	public CCImageGraphics(final int theWidth, final int theHeight, final boolean theIsMipMap) {
		_myRenderImage = new BufferedImage(theWidth, theHeight, BufferedImage.TYPE_INT_ARGB);
		_myImage = CCImageIO.newImage(_myRenderImage);
		_myGraphics2D = _myRenderImage.createGraphics();
		_myGraphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		_myGraphics2D.setBackground(new Color(0f,0f,0f, 0f));
	}
	
	public CCImage image(){
		return _myImage;
	}
	
	/**
	 * After drawing the changes have to be applied to the image. Use this method
	 * to apply the changes.
	 */
	public void update(){
		_myImage = CCImageIO.newImage(_myRenderImage);
	}
	
	public void blur(final float theBlurSize){
		CCGaussianBlur myBlur = new CCGaussianBlur(theBlurSize);
		_myRenderImage = myBlur.filter(_myRenderImage);
	}
	
	public void color(final float theRed, final float theGreen, final float theBlue, final float theAlpha){
		_myGraphics2D.setColor(new Color(theRed, theGreen,theBlue,theAlpha));
	}
	
	public void rect(final float theX, final float theY, final float theWidth, final float theHeight){
		_myGraphics2D.fillRect((int)theX, (int)theY, (int)theWidth, (int)theHeight);
	}
	
	public void roundRect(
		final float theX, final float theY, 
		final float theWidth, final float theHeight,
		final float theCornerRadius
	){
		_myGraphics2D.fillRoundRect(
			(int)theX, (int)theY, 
			(int)theWidth, (int)theHeight, 
			(int)(theCornerRadius*2), (int)(theCornerRadius*2)
		);
	}

//	@Override
//	protected BufferedImage createTexture(BufferedImage theImage, BufferedImage theTargetImage) {
//		/* Create a rescale filter op that makes the image 50% opaque */
//		float[] scales = { 0f, 0f, 0f, 1f };
//		float[] offsets = new float[4];
//		RescaleOp rop = new RescaleOp(scales, offsets, null);
//
//		Graphics2D myGraphics = (Graphics2D) theTargetImage.getGraphics();
//		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, _myIsAntialiased);
//		myGraphics.setBackground(new Color(1f, 1f, 1f, 0f));
//
//		/* Draw the image, applying the filter */
//		myGraphics.drawImage(theImage, rop, -(int) _myOffset.x(), (int) _myOffset.y());
//
//		CCGaussianBlur myBlur = new CCGaussianBlur(_myShadowSize);
//		theTargetImage = myBlur.filter(theTargetImage);
//
//		myGraphics = (Graphics2D) theTargetImage.getGraphics();
//		myGraphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, _myIsAntialiased);
//		myGraphics.drawImage(theImage, -(int) _myOffset.x(), (int) _myOffset.y(), null);
//
//		return theTargetImage;
//
//	}


	public boolean mustFlipVertically() {
		return _myImage.mustFlipVertically();
	}


}
