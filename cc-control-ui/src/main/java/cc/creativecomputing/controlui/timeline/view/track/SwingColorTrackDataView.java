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
package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCColorTrackController;
import cc.creativecomputing.math.CCColor;

public class SwingColorTrackDataView extends SwingBlendableTrackDataView<CCColorTrackController> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8117392771151385531L;

	public SwingColorTrackDataView(CCTimelineController theTimelineController, CCColorTrackController theTrackController) {
		super(theTimelineController, theTrackController);
	}

	@Override
	public void renderBlendData(Graphics2D g2d, CCControlPoint myFirstPoint, CCControlPoint mySecondPoint) {
		if (myFirstPoint.equals(mySecondPoint)) {
			return;
		}

		if (mySecondPoint == null) {
			mySecondPoint = new CCControlPoint(_myTrackContext.upperBound(), myFirstPoint.value());
		}

		Point2D p1 = _myController.curveToViewSpace(myFirstPoint);
		Point2D p2 = _myController.curveToViewSpace(mySecondPoint);

		CCColorTrackController myColorTrackController = _myController;

		for (double x = p1.getX(); x <= p2.getX(); x++) {
			double myTime = _myController.viewXToTime((int) x, true);
			CCColor myColor = myColorTrackController.blend(myTime);
			g2d.setColor(myColor.toAWTColor());
			g2d.drawLine((int) x, 0, (int) x, height() / 2);
		}
	}
}
