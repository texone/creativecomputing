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
package cc.creativecomputing.graphics.shader.imaging.filter;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderBuffer;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;

/**
 * This filter implements the Horn-Schunck algorithm to calculate the optical flow between 2 input images I(k), I(k-1).
 * 
 * In the first step it estimates the spatial partial derivatives Ix(k) and Iy(k) with a sobel kernel (output values are written
 * to the r and g channel) and the time derivative It(x,y,k) = sum [I(x+i, y+j, k) - I(x+i, y+j, k-1)] as a weighted 
 * sum of neighborhood pixel differences.
 * 
 * In the second step and optimal flow is approximated 
 * 
 * The third shader stage applies thresholding and an offset to the output.
 *
 * @author max goettner
 * @demo cc.creativecomputing.demo.cc.creativecomputing.demo.graphics.shader.filter.CCOpticalFlowDemo
 */


public class CCOpticalFlowFilter extends CCImageFilter{
	
	CCGLProgram   _myShaderStage1;
	CCGLProgram   _myShaderStage2;
	CCGLProgram   _myShaderStage3;
	CCBlurFilter   _myBlurInputStage;
	
	CCShaderBuffer _myOutputStage1;
	CCShaderBuffer _myOutputStage2;
	CCShaderBuffer _myOutputStage3;
	
	CCShaderBuffer outTmp;
	CCShaderBuffer bufTmp;
	CCShaderBuffer _myLastInput;
	CCShaderBuffer _myPreBlurInput;

	@CCProperty(name = "threshold", min = 0f, max = 0.1f)
	private float _cThresh;
	
	@CCProperty(name = "offset", min = 0f, max = 1f)
	private float _cOffset;
	
	/*
	@CCProperty(name = "blur radius", min = 0f, max = 10f)
	private float _cBlurRadius = 1f;
	*/
	@CCProperty(name = "gain", min = 0f, max = 10f)
	private float _cGain = 1f;
	
	private int nSteps = 4;
	
	public CCOpticalFlowFilter(CCGraphics g, CCTexture2D theInput) {
		super(theInput);
		
		//_myBlurInputStage = new CCBlurFilter (theGraphics, theInput, 10);
		//_myBlurInputStage.setRadius(2f);
		
		//_myInput = _myBlurInputStage.output();
		_myOutputStage1 = new CCShaderBuffer(theInput.width(), theInput.height());
		_myOutputStage1.clear(g);
		
		_myOutputStage2 = new CCShaderBuffer(theInput.width(), theInput.height());
		_myOutputStage2.clear(g);
		
		_myOutputStage3 = new CCShaderBuffer(theInput.width(), theInput.height());
		_myOutputStage3.clear(g);
		
		_myShaderStage1 = new CCGLProgram (CCNIOUtil.classPath(this, "shader/partialDerivatives_vp.glsl"), CCNIOUtil.classPath(this, "shader/partialDerivatives_fp.glsl"));
		_myShaderStage2 = new CCGLProgram (CCNIOUtil.classPath(this, "shader/hornSchunck_vp.glsl"), CCNIOUtil.classPath(this, "shader/hornSchunck_fp.glsl"));
		_myShaderStage3 = new CCGLProgram (CCNIOUtil.classPath(this, "shader/offset_vp.glsl"), CCNIOUtil.classPath(this, "shader/offset_fp.glsl"));
		
		outTmp = new CCShaderBuffer(_myOutputStage1.width(), _myOutputStage1.height());
		bufTmp = new CCShaderBuffer(_myOutputStage1.width(), _myOutputStage1.height());
		_myLastInput = new CCShaderBuffer(_myInput.width(), _myInput.height());
		_myLastInput.clear(g);
		
		outTmp.clear(g);
		bufTmp.clear(g);
	}

	@Override
	public CCTexture2D output() {
		return _myOutputStage3.attachment(0);
	}
	
	public CCTexture2D outputUnshifted() {
		return _myOutputStage2.attachment(0);
	}
	

	public CCTexture2D input() {
		return _myInput;
	}
	
	public void setGain(float theGain) {
		_cGain = theGain;
	}
	

	@Override
	public void display(CCGraphics g) {

		// init step, calculate Ex, Ey, Et from current and last input frame
		//_myBlurInputStage.update(theDeltaTime);
		
		g.clear();
		
		_myShaderStage1.start();
		g.texture (0, _myInput);	
		g.texture (1, _myLastInput.attachment(0));	
		_myShaderStage1.uniform1i ("IN0", 0);
		_myShaderStage1.uniform1i ("IN1", 1);
		_myShaderStage1.uniform1f ("gain", _cGain);
		
		_myOutputStage1.draw(g);
		
		_myShaderStage1.end();
		g.noTexture();

		// initalize output to zeros
		outTmp.clear(g);
		
		// iterate to find v, u
		for (int i=0; i<nSteps; i++) {

			g.clear();
			_myShaderStage2.start();
			
			g.texture (0, outTmp.attachment(0));	
			g.texture (1, _myOutputStage1.attachment(0));	
			_myShaderStage2.uniform1i ("UV", 0);
			_myShaderStage2.uniform1i ("E_xyt", 1);
			_myOutputStage2.draw(g);
			
			_myShaderStage2.end();
			g.noTexture();
			
			outTmp.beginDraw(g);
			g.image(_myOutputStage2.attachment(0), 0, 0);
			outTmp.endDraw(g);
		}
		
		_myShaderStage3.start();
		g.texture (0, _myOutputStage2.attachment(0));	
		_myShaderStage3.uniform1i ("IN0", 0);
		_myShaderStage3.uniform1f ("offset", 0.5f);
		_myShaderStage3.uniform1f ("gain", 0.5f);
		_myOutputStage3.draw(g);
		_myShaderStage3.end();
		g.noTexture();
		
		// keep input for next update call
		_myLastInput.beginDraw(g);
		g.image (_myInput, 0, 0);
		_myLastInput.endDraw(g);
	}
}
