/*  
 * Copyright (c) 2009  Christian Riekoff <info@texone.org>  
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

import java.awt.event.MouseEvent;

import cc.creativecomputing.control.timeline.TimeRange;
import cc.creativecomputing.controlui.timeline.view.TimedContentView;
import cc.creativecomputing.math.CCMath;

/**
 * @author christianriekoff
 *
 */
public class TimeRangeController {
	
	public static enum TimeRangeEditFunction{
		MOVE_BOTH, MOVE_START, MOVE_END, ON_OFF
	}
	
	protected TrackContext _myTrackContext;

	protected TimeRange _myTimeRange;
	protected TimedContentView _myTransportView;
	protected TimeRangeEditFunction _myLoopAction;
	
	private int _myStartClickX = 0;
	private double _myStartTime;
	private double _myLastTime;
	protected double _myLoopStart = 0;
	protected double _myLoopEnd = 0;

	protected double _myNewLoopStart;
	protected double _myNewLoopEnd;
	
	public TimeRangeController(
		TrackContext theTrackContext,
		TimeRange theTimeRange, 
		TimedContentView theView
	) {
		_myTrackContext = theTrackContext;
		_myTimeRange = theTimeRange;
		_myTransportView = theView;
	}
	
	protected TimeRangeController(TrackContext theTrackContext) {
		_myTrackContext = theTrackContext;
	}
	
	public void mousePressed(MouseEvent e) {
		double myCurveX = _myTransportView.viewXToTime(e.getX(), true);
			
		int myLoopStart = _myTransportView.timeToViewX(_myTimeRange.start());
		int myLoopEnd = _myTransportView.timeToViewX(_myTimeRange.end());
		_myLoopAction = TimeRangeEditFunction.ON_OFF;
			
		if(CCMath.abs(myLoopStart - e.getX()) < 5) {
			_myLoopAction = TimeRangeEditFunction.MOVE_START;
		}else if(CCMath.abs(myLoopEnd - e.getX()) < 5) {
			_myLoopAction = TimeRangeEditFunction.MOVE_END;
		}else if(myCurveX > _myTimeRange.start() && myCurveX < _myTimeRange.end()){
			_myLoopAction = TimeRangeEditFunction.MOVE_BOTH;
		}
			
		_myStartClickX = e.getX();
		_myStartTime = myCurveX;
		_myLastTime = myCurveX;
		
		_myLoopStart = _myTimeRange.start();
		_myLoopEnd = _myTimeRange.end();
	}
	
	public void mouseDragged(MouseEvent e) {
		_myLoopStart = _myTimeRange.start();
		_myLoopEnd = _myTimeRange.end();
		double myCurveX = _myTransportView.viewXToTime(e.getX(), true);
//		myCurveX = _myTimelineController.snapToRaster(myCurveX);

		switch (_myLoopAction) {
		case MOVE_BOTH:
			double myCurveMove = _myTransportView.viewXToTime(e.getX(), true) - _myLastTime;
			_myNewLoopStart = _myLoopStart + myCurveMove;
			_myNewLoopEnd = _myLoopEnd + myCurveMove;
			break;
		case MOVE_START:
			double myCurveMoveStart = _myTransportView.viewXToTime(e.getX(), true);
			_myNewLoopStart = _myTrackContext.quantize(myCurveMoveStart);
			_myNewLoopEnd = _myLoopEnd;
			break;
		case MOVE_END:
			double myCurveMoveEnd = _myTransportView.viewXToTime(e.getX(), true);
			_myNewLoopStart = _myLoopStart;
			_myNewLoopEnd = _myTrackContext.quantize(myCurveMoveEnd);
			break;
		default:
			if (e.getX() < _myStartClickX) {
				_myNewLoopStart = _myTrackContext.quantize(_myTransportView.viewXToTime(e.getX(), true));
				_myNewLoopEnd = _myTrackContext.quantize(_myStartTime);
			} else {
				_myNewLoopStart = _myTrackContext.quantize(_myStartTime);
				_myNewLoopEnd = _myTrackContext.quantize(_myTransportView.viewXToTime(e.getX(), true));
			}
			break;
		}

		_myTimeRange.range(_myNewLoopStart, _myNewLoopEnd);
		_myLastTime = myCurveX;
		
		_myTrackContext.render();
	}
	
	public void mouseReleased(MouseEvent e) {
		if(e.getX() == _myStartClickX && _myLoopAction == TimeRangeEditFunction.ON_OFF) {
			_myTimeRange.range(_myStartTime, _myStartTime);
		}
	}
}
