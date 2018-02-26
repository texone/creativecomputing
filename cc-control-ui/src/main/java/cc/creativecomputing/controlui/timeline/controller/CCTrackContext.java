/*  
 * Copyright (c) 2012 Christian Riekoff <info@texone.org>  
 *  
 *  This file is free software: you may copy, redistribute and/or modify it  
 *  under the terms of the GNU General Public License as published by the  
 *  Free Software Foundation, either version 2 of the License, or (at your  
 *  option) any later version.  
 *  
 *  This file is distributed in the hope that it will be useful, but  
 *  WITHOUT ANY WARRANTY; without even the implied warranty of  
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU  
 *  General Public License for more details.  
 *  
 *  You should have received a copy of the GNU General Public License  
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.  
 *  
 * This file incorporates work covered by the following copyright and  
 * permission notice:  
 */
package cc.creativecomputing.controlui.timeline.controller;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackDataController;

/**
 * The TrackContext is used to share informations between a number of tracks
 * @author christianriekoff
 *
 */
public class TrackContext implements CCZoomable{
    
    protected double _myLowerBound;
    protected double _myUpperBound;

	protected CCZoomController _myZoomController;

	
	public TrackContext() {
		_myZoomController = new CCZoomController();
		
		_myZoomController.addZoomable(this);

        _myLowerBound = 0;
        _myUpperBound = 1;
	}
	
	private CCTrackDataController _myActiveTrack;
	
	public void activeTrack(CCTrackDataController theActiveTrack){
		if(_myActiveTrack != null && theActiveTrack != _myActiveTrack)_myActiveTrack.deactivate();
		_myActiveTrack = theActiveTrack;
	}
	
	public CCTrackDataController activeTrack(){
		return _myActiveTrack;
	}
	
	public double defaultValue(CCTrackController theTrackController) {
		return 0;
	}
	
	/**
	 * Snaps the time of the given point to the raster of this context. This is called quantization.
	 * @param thePoint
	 * @return
	 */
	public ControlPoint quantize(ControlPoint thePoint) {
        return thePoint;
	}

	public double quantize(double theTime) {
		return theTime;
	}
    
    public double lowerBound() {
    	return _myLowerBound;
    }
    
    public double upperBound() {
    	return _myUpperBound;
    }
    
    public double viewTime() {
    	return _myUpperBound - _myLowerBound;
    }

	/**
	 * Controller for track zooming
	 * @return
	 */
	public CCZoomController zoomController() {
		return _myZoomController;
	}

	@Override
	public void setRange(double theLowerBound, double theUpperBound) {
        if (theLowerBound > theUpperBound) {
            double tmp = theLowerBound;
            theLowerBound = theUpperBound;
            theUpperBound = tmp;
        }
        _myLowerBound = theLowerBound;
        _myUpperBound = theUpperBound;
	}

	
	public void renderInfo(){
		
	}
}
