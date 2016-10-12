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
import cc.creativecomputing.math.CCMath;

public class CCGPUGaussianBlur extends CCGPUConvolutionShader{

	public CCGPUGaussianBlur(boolean theUseRect, CCGraphics theGraphics, double theRadius) {
		super(theUseRect);
		makeKernel(theRadius);
	}
	
	public CCGPUGaussianBlur(CCGraphics theGraphics, double theRadius) {
		this(false, theGraphics, theRadius);
	}

	/**
	 * Make a Gaussian blur kernel.
     * @param theRadius the blur radius
     * @return the kernel
	 */
	private void makeKernel(final double theRadius) {
		int r = (int)Math.ceil(theRadius);
		int rows = r*2+1;
		
		double[] matrix = new double[rows * rows ];
		double sigma = theRadius/3;
		double sigma22 = 2 * sigma * sigma;
		double sigmaPi2 = 2 * CCMath.PI * sigma;
		double radius2 = theRadius * theRadius;
		double total = 0;
		int index = 0;
		
		for (int xrow = -r; xrow <= r; xrow++) {
			for (int yrow = -r; yrow <= r; yrow++) {
			double distance = xrow * xrow + yrow * yrow;
			if (distance > radius2)
				matrix[index] = 0;
			else
				matrix[index] = CCMath.exp(-(distance)/sigma22) / sigmaPi2;
			total += matrix[index];
			index++;
			}
		}
		
		List<Double> myResult = new ArrayList<>();
		
		for (int i = 0; i < rows * rows; i++)
			myResult.add(matrix[i] / total);

		setKernel(myResult, rows, rows);
	}
}
