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
package cc.creativecomputing.graphics.shader.imaging;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCRenderBuffer;
import cc.creativecomputing.graphics.texture.CCFrameBufferObjectAttributes;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.math.CCMath;

public class CCGPUSeperateGaussianBlur extends CCGPUConvolutionShader{
	
	private int _myRows;
	private int _myIntRadius;
	private double[] _myMatrix;
	
	private CCRenderBuffer _myTexture1;
	private CCRenderBuffer _myTexture2;
	
	private int _myWidth;
	private int _myHeight;
	
	private int _myRes = 1;

	/**
	 * Creates a new GaussianBlur. Instead of applying a full size two dimensional kernel.
	 * The blur is applied using a separate vertical and horizontal kernel. This way the
	 * amount of calculations per pixel is reduced from <code>kernelSize * kernelSize</code>
	 * to <code>kernelSize + kernelSize</code>. The radius of the blur is variable. You have to
	 * however define a maximum radius because the size of the array keeping the kernel on the
	 * shaderside can not be changed at runtime.
	 * @param theMaximumRadius the maximum possible blur radius
	 * @param theWidth width for the render texture used by the blur
	 * @param theHeight height for the render texture used by the blur
	 */
	public CCGPUSeperateGaussianBlur(double theMaximumRadius, final int theWidth, final int theHeight, final int theRes) {
		super();
		_myRes = theRes;

		_myWidth = theWidth / _myRes;
		_myHeight = theHeight / _myRes;
		
		_myIntRadius = CCMath.ceil(theMaximumRadius);
		_myRows = _myIntRadius * 2+1;
		_myMatrix = new double[_myRows];
		
		setKernel(calculateKernel(theMaximumRadius), _myRows, 1);
		
	}
	
	public CCGPUSeperateGaussianBlur(double theMaximumRadius, final int theWidth, final int theHeight) {
		this(theMaximumRadius, theWidth, theHeight, 1);
	}
	
	public double maxRadius(){
		return _myIntRadius;
	}
	
	/**
	 * Make a Gaussian blur kernel.
     * @param radius the blur radius
     * @return the kernel
	 */
	private List<Double> calculateKernel(double theRadius){
		theRadius = CCMath.max(theRadius, 1);
		double sigma = theRadius/3;
		double sigma22 = 2*sigma*sigma;
		double sigmaPi2 = CCMath.TWO_PI * sigma;
		double sqrtSigmaPi2 = CCMath.sqrt(sigmaPi2);
		
		double radius2 = theRadius * theRadius;
		double total = 0;
		int index = 0;
		
		for (int row = -_myIntRadius; row <= _myIntRadius; row++) {
			double distance = row * row;
			
			if (distance > radius2)
				_myMatrix[index] = 0;
			else
				_myMatrix[index] = (double)Math.exp(-(distance)/sigma22) / sqrtSigmaPi2;
			
			total += _myMatrix[index];
			index++;
		}
		
		List<Double> myKernel = new ArrayList<>();
		
		for (int i = 0; i < _myRows; i++) 
			myKernel.add(_myMatrix[i] / total);
		
		return myKernel;
	}

	/**
	 * Changes the blur radius. The radius is clamped to the maximum blur value
	 * passed to the constructor. Be aware you have to define a maximum radius because
	 * the size of the array holding the kernel on the shaderside can not be changed
	 * at runtime. For a smaller radius kernel cells not needed will be filled with 0.
	 * @param theRadius
	 */
	public void radius(final double theRadius){
		updateKernel(calculateKernel(theRadius));
	}
	
	/**
	 * Call this method to start the blur. Everything drawn between the 
	 * <code>beginDraw()</code> and <code>endDraw()</code>
	 * will be blurred. 
	 */
	public void beginDraw(CCGraphics g){
		if(_myTexture1 == null){
			CCFrameBufferObjectAttributes myAtts = new CCFrameBufferObjectAttributes();
			myAtts.samples(8);
 			_myTexture1 = new CCRenderBuffer(myAtts, _myWidth, _myHeight);
			_myTexture2 = new CCRenderBuffer(myAtts, _myWidth, _myHeight);
			texture(_myTexture1.attachment(0));
		}
		_myTexture1.beginDraw(g);
		g.clear();
		g.pushMatrix();
		g.scale(1f/_myRes);
	}
	
	/**
	 * Call this method to end the first pass of the blur, this allows
	 * you too call the second pass while drawing into another texture.
	 * @param g
	 */
	public void endFirstPass(CCGraphics g) {
		g.popMatrix();
		_myTexture1.endDraw(g);
		
		_myTexture2.beginDraw(g);
		g.clear();
		start();
		
		g.image(_myTexture1.attachment(0), - _myWidth/2, -_myHeight/2);
		end();
		flipKernel();
		_myTexture2.endDraw(g);
	}
	
	/**
	 * Call this method to end the second pass of the blur, this allows
	 * you too call the second pass while drawing into another texture.
	 * @param g
	 */
	public void endSecondPass(CCGraphics g) {
		_myTexture1.beginDraw(g);
		g.clear();
		start();
		g.image(_myTexture2.attachment(0), - _myWidth/2, -_myHeight/2);
		end();
		flipKernel();
		_myTexture1.endDraw(g);
	}
	
	/**
	 * Call this method to end the blur. Everything drawn between the 
	 * <code>beginDraw()</code> and <code>endDraw()</code>
	 * will be blurred. 
	 */
	public void endDraw(CCGraphics g){
		endFirstPass(g);
		endSecondPass(g);
		g.image(_myTexture1.attachment(0), - _myWidth/2 * _myRes, -_myHeight/2 * _myRes, _myWidth * _myRes, _myHeight * _myRes);
	}
	
	public CCTexture2D blurredTexture() {
		return _myTexture1.attachment(0);
	}
}
