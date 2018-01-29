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
package cc.creativecomputing.graphics.font;


import cc.creativecomputing.graphics.CCTesselator;


public class CCVectorFontTesselator extends CCTesselator{
	
	private CCVectorChar _myVectorChar;

	public CCVectorFontTesselator() {
		super();
	}

	@Override
	public void begin(int theMode) {
	}

	@Override
	public void combineData(
		double[] theCoords, Object[] theInputData,
		float[] theWeight, Object[] theOutputData, Object theUserData
	) {
	}

	@Override
	public void edgeFlagData(final boolean theArg0, Object theData) {
	}

	@Override
	public void end() {
		_myVectorChar.end();
	}

	@Override
	public void errorData(final int theErrorNumber, Object theUserData) {
	}

	@Override
	public void vertex(final Object theVertexData) {
		if (theVertexData instanceof double[]) {
	        double[] d = (double[]) theVertexData;
			_myVectorChar.addVertex(d[0],d[1]);
		}
	}

	public void beginPolygon(final CCVectorChar theVectorChar) {
		_myVectorChar = theVectorChar;
		_myTesselator.gluTessBeginPolygon(null);
	}
	
	

}
