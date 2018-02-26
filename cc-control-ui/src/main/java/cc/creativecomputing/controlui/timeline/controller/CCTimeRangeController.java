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
package cc.creativecomputing.controlui.timeline.controller;

import cc.creativecomputing.control.timeline.CCTimeRange;
import cc.creativecomputing.controlui.timeline.view.CCTimedContentView;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christianriekoff
 *
 */
public class CCTimeRangeController {
	
	public enum CCTimeRangeEditFunction{
		MOVE_BOTH, MOVE_START, MOVE_END, ON_OFF
	}
	
	protected CCTrackContext _myTrackContext;

	protected CCTimeRange _myTimeRange;
	protected CCTimedContentView _myTransportView;
	protected CCTimeRangeEditFunction _myLoopAction;
	
	private double _myStartClickX = 0;
	private double _myStartTime;
	private double _myLastTime;
	protected double _myLoopStart = 0;
	protected double _myLoopEnd = 0;

	protected double _myNewLoopStart;
	protected double _myNewLoopEnd;
	
	public CCTimeRangeController(
		CCTrackContext theTrackContext,
		CCTimeRange theTimeRange, 
		CCTimedContentView theView
	) {
		_myTrackContext = theTrackContext;
		_myTimeRange = theTimeRange;
		_myTransportView = theView;
	}
	
	protected CCTimeRangeController(CCTrackContext theTrackContext) {
		_myTrackContext = theTrackContext;
	}
	
	public void mousePressed(CCGLMouseEvent e) {
		double myCurveX = _myTransportView.viewXToTime(e.x, true);
			
		int myLoopStart = _myTransportView.timeToViewX(_myTimeRange.start());
		int myLoopEnd = _myTransportView.timeToViewX(_myTimeRange.end());
		_myLoopAction = CCTimeRangeEditFunction.ON_OFF;
			
		if(CCMath.abs(myLoopStart - e.x) < 5) {
			_myLoopAction = CCTimeRangeEditFunction.MOVE_START;
		}else if(CCMath.abs(myLoopEnd - e.x) < 5) {
			_myLoopAction = CCTimeRangeEditFunction.MOVE_END;
		}else if(myCurveX > _myTimeRange.start() && myCurveX < _myTimeRange.end()){
			_myLoopAction = CCTimeRangeEditFunction.MOVE_BOTH;
		}
			
		_myStartClickX = e.x;
		_myStartTime = myCurveX;
		_myLastTime = myCurveX;
		
		_myLoopStart = _myTimeRange.start();
		_myLoopEnd = _myTimeRange.end();
	}
	
	public CCTimeRangeEditFunction action(CCVector2 e) {
		double myCurveX = _myTransportView.viewXToTime(e.x, true);
		int myLoopStart = _myTransportView.timeToViewX(_myTimeRange.start());
		int myLoopEnd = _myTransportView.timeToViewX(_myTimeRange.end());
		
		if(CCMath.abs(myLoopStart - e.x) < 5) {
			return CCTimeRangeEditFunction.MOVE_START;
		}else if(CCMath.abs(myLoopEnd - e.x) < 5) {
			return CCTimeRangeEditFunction.MOVE_END;
		}else if(myCurveX > _myTimeRange.start() && myCurveX < _myTimeRange.end()){
			return CCTimeRangeEditFunction.MOVE_BOTH;
		}
		return CCTimeRangeEditFunction.ON_OFF;
	}
	
	public CCTimeRangeEditFunction loopAction() {
		return _myLoopAction;
	}
	
	public void mouseDragged(CCVector2 e) {
		double myCurveX = _myTransportView.viewXToTime(e.x, true);
//		myCurveX = _myTimelineController.snapToRaster(myCurveX);

		switch (_myLoopAction) {
		case MOVE_BOTH:
			double myCurveMove = myCurveX - _myLastTime;
			_myNewLoopStart = _myTrackContext.quantize(_myLoopStart + myCurveMove);
			_myNewLoopEnd = _myTrackContext.quantize(_myLoopEnd + myCurveMove);
			break;
		case MOVE_START:
			double myCurveMoveStart = myCurveX;
			_myNewLoopStart = _myTrackContext.quantize(myCurveMoveStart);
			_myNewLoopEnd = _myTimeRange.end();
			break;
		case MOVE_END:
			double myCurveMoveEnd = myCurveX;
			_myNewLoopStart = _myTimeRange.start();
			_myNewLoopEnd = _myTrackContext.quantize(myCurveMoveEnd);
			break;
		default:
			if (e.x < _myStartClickX) {
				_myNewLoopStart = _myTrackContext.quantize(myCurveX);
				_myNewLoopEnd = _myTrackContext.quantize(_myStartTime);
			} else {
				_myNewLoopStart = _myTrackContext.quantize(_myStartTime);
				_myNewLoopEnd = _myTrackContext.quantize(myCurveX);
			}
			break;
		}

		_myTimeRange.range(_myNewLoopStart, _myNewLoopEnd);
		
		_myTrackContext.renderInfo();
	}
	
	public void mouseReleased(CCGLMouseEvent e) {
		if(e.x == _myStartClickX && _myLoopAction == CCTimeRangeEditFunction.ON_OFF) {
			_myTimeRange.range(_myStartTime, _myStartTime);
		}
	}
}
