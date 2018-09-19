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
package cc.creativecomputing.controlui.timeline.controller.track;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.timeline.CCTimeRange;
import cc.creativecomputing.control.timeline.CCTrackData;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.CCTrackContext;
import cc.creativecomputing.controlui.timeline.tools.CCTimedContentView;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackView;
import cc.creativecomputing.core.CCEventManager.CCEvent;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christianriekoff
 *
 */
public abstract class CCTrackDataController implements CCTimedContentView {

	protected CCAbstractTrackView _myTrackView;

	protected CCTrackContext _myTrackContext;

	protected CCTimelineController _myTimelineController;

	protected CCPropertyHandle<?> _myProperty;

	public CCTrackDataController(CCTrackContext theTrackContext, CCPropertyHandle<?> theProperty) {
		_myTrackContext = theTrackContext;
		_myProperty = theProperty;
		if (_myTrackContext instanceof CCTimelineController)
			_myTimelineController = (CCTimelineController) theTrackContext;
	}

	public CCTrackContext context() {
		return _myTrackContext;
	}

	public void view(CCAbstractTrackView theView) {
		_myTrackView = theView;
	}

	public CCAbstractTrackView view() {
		return _myTrackView;
	}

	public abstract CCTrackData trackData();

	public CCPropertyHandle<?> property() {
		return _myProperty;
	}

	public void reset() {
		_mySelectedPoints.clear();
		trackData().clear();
	}

	

	public void paste(List<CCControlPoint> thePoints, double theTime) {
		if (thePoints == null)
			return;
		if (thePoints.size() < 1)
			return;
		CCControlPoint myFirstPoint = thePoints.get(0);
		for (CCControlPoint myPoint : thePoints) {
			CCControlPoint myCopy = myPoint.clone();
			myCopy.time(myCopy.time() - myFirstPoint.time() + theTime);
			trackData().add(myCopy);
		}
	}

	@Override
	public double viewXToTime(double theViewX) {
		try {
			return viewWidthToTime(theViewX) + _myTrackContext.lowerBound();
		} catch (Exception e) {
			return 0;
		}
	}
	
	@Override
	public double viewWidthToTime(double theViewWidth) {
		try {
			return theViewWidth / (double) _myTrackView.width() * (_myTrackContext.viewTime());
		} catch (Exception e) {
			return 0;
		}
	}

	@Override
	public double timeToViewX(double theCurveX) {
		return  (theCurveX - _myTrackContext.lowerBound()) / (_myTrackContext.viewTime()) * _myTrackView.width();
	}

	// public abstract double viewXToTime(int theViewX);
	//
	// public abstract int timeToViewX(double theCurveX);

	public abstract CCVector2 curveToViewSpace(CCControlPoint thePoint);

	public abstract CCControlPoint viewToCurveSpace(CCVector2 thePoint, boolean theGetPos);

	public void setRange(CCTimeRange theRange) {
	}

	public CCEvent<CCTimeRange> zoomEvent = this::setRange;

	protected List<CCControlPoint> _mySelectedPoints = new ArrayList<>();

	public void deactivate() {
		for (CCControlPoint myPoint : _mySelectedPoints) {
			myPoint.setSelected(false);
		}
		_mySelectedPoints.clear();
	}

	public List<CCControlPoint> selectedPoints() {
		return _mySelectedPoints;
	}

	// public double distance(ControlPoint theNearest, CCVector2 theViewCoords) {
	// if(this instanceof EventTrackController || this instanceof
	// CCBlendableTrackController)
	// return Math.abs(curveToViewSpace(theNearest).getX() -
	// theViewCoords.getX());
	// else
	// return curveToViewSpace(theNearest).distance(theViewCoords);
	// }

	public double distance(CCControlPoint theNearest, CCVector2 theViewCoords) {
		if (this instanceof CCEventTrackController || this instanceof CCBlendableTrackController)
			return CCMath.abs(curveToViewSpace(theNearest).x - theViewCoords.y);
		else
			return curveToViewSpace(theNearest).distance(theViewCoords);
	}
}
