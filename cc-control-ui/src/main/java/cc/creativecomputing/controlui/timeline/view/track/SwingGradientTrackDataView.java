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
import java.awt.image.BufferedImage;

import com.sun.prism.paint.Color;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGradientTrackController;
import cc.creativecomputing.core.logging.CCLog;

public class SwingGradientTrackDataView extends SwingBlendableTrackDataView<CCGradientTrackController> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8117392771151385531L;
	
	public SwingGradientTrackDataView(CCTimelineController theTimelineController, CCGradientTrackController theTrackController) {
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

		CCGradientTrackController myGradientTrackController = _myController;

		try {
		for (double x = p1.getX(); x <= p2.getX() && x < _myRenderBuffer.getWidth(); x++) {
			double myTime = _myController.viewXToTime((int) x, true);
			CCGradient myGradient = myGradientTrackController.blend(myTime);
			for(double y = 0; y < height() / 2;y++) {
				double myBlend = y / (height() / 2);
				_myRenderBuffer.setRGB((int)x, (int)y, myGradient.color(myBlend).toAWTColor().getRGB());
			}
		}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
