/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui.timeline.controller.quantize;

import cc.creativecomputing.controlui.timeline.controller.CCTransportController;

public class CCSubStepQuantizer implements CCQuantizer{
	private int _myRaster;
	
	public CCSubStepQuantizer(int theRaster){
		_myRaster = theRaster;
	}

	@Override
	public double quantize(CCTransportController theTransport, double theTime) {
		if(_myRaster <= 0)return 0;
		return theTransport.rulerInterval().quantize(theTime, _myRaster);
	}

	@Override
	public int drawRaster(CCTransportController theTransport) {
		return _myRaster;
	}

}
