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

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.shader.CCGLProgram;
import cc.creativecomputing.graphics.shader.CCShaderException;
import cc.creativecomputing.graphics.texture.CCTexture2D;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.math.CCVector2;

public class CCGPUConvolutionShader extends CCGLProgram{
	
	private double _myPixelWidth;
	private double _myPixelHeight;
	
	protected int _myKernelWidth;
	protected int _myKernelHeight;
	protected int _myKernelSize;
	
	protected List<Double> _myKernel;
	protected List<Double> _myOffsets;
	
	public CCGPUConvolutionShader() {
		super(
			CCNIOUtil.dataPath("shader/convolution.vp"), 
			CCNIOUtil.dataPath("shader/convolution.fp")
		);
	}
	
	public CCGPUConvolutionShader(final CCGraphics theGraphics, final Path theShader) {
		super(null, theShader);
	}
	
	public CCGPUConvolutionShader(final int theKernelWidth, final int theKernelHeight) {
		super(
			CCNIOUtil.dataPath("shader/convolution.vp"), 
			CCNIOUtil.dataPath("shader/convolution.fp")
		);
		
		_myKernelWidth = theKernelWidth;
		_myKernelHeight = theKernelHeight;
		_myKernelSize = _myKernelWidth * _myKernelHeight;
	}
	
	public CCGPUConvolutionShader(final List<Double> theKernel, final int theKernelWidth, final int theKernelHeight) {
		this(theKernelWidth,theKernelHeight);
		initKernel(theKernel);
	}
	
	protected void setKernel(final List<Double> theKernel, final int theKernelWidth, final int theKernelHeight) {
		_myKernelWidth = theKernelWidth;
		_myKernelHeight = theKernelHeight;
		_myKernelSize = _myKernelWidth * _myKernelHeight;
		
		initKernel(theKernel);
	}
	
	protected void initKernel(final List<Double> theKernel) {
		if(_myKernelSize != theKernel.size()) {
			throw new CCShaderException("The given Kernel of the size "+theKernel.size()+" does not match the given width and height.");
		}
		_myKernel = theKernel;
	}
	
	protected void updateKernel(final List<Double> theKernel){
		_myKernel = theKernel;
	}
	
	public void texture(final CCTexture2D theTexture) {
		_myPixelWidth = 1f/theTexture.width();
		_myPixelHeight = 1f/theTexture.height();
	
		updateOffsets();
	}
	
	public void flipKernel() {
		int temp = _myKernelWidth;
		_myKernelWidth = _myKernelHeight;
		_myKernelHeight = temp;
		
		updateOffsets();
	}
	
	public void updateOffsets() {
		_myOffsets = new ArrayList<>();
		
		int xStart = -_myKernelWidth / 2;
		int yStart = -_myKernelHeight / 2;
		
		for(int x = 0; x < _myKernelWidth;x++) {
			for(int y = 0; y < _myKernelHeight; y++) {
				_myOffsets.add((xStart + x) * _myPixelWidth);
				_myOffsets.add((yStart + y) * _myPixelHeight);
			}
		}
	}
	
	@Override
	public void start() {
		super.start();
		uniform1i("decal", 0);
		uniform2f("pixelScale", _myPixelWidth, _myPixelHeight);
		uniform1i("kernelWidth", _myKernelWidth);
		uniform1i("kernelHeight", _myKernelHeight);
		uniform1fv("kernelValue", _myKernel);
		
	}
}
