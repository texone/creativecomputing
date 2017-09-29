package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCColorTrackController;
import cc.creativecomputing.math.CCColor;

public class SwingColorTrackDataView extends SwingBlendableTrackDataView<CCColorTrackController> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8117392771151385531L;

	public SwingColorTrackDataView(TimelineController theTimelineController, CCColorTrackController theTrackController) {
		super(theTimelineController, theTrackController);
	}

	@Override
	public void renderBlendData(Graphics2D g2d, ControlPoint myFirstPoint, ControlPoint mySecondPoint) {
		if (myFirstPoint.equals(mySecondPoint)) {
			return;
		}

		if (mySecondPoint == null) {
			mySecondPoint = new ControlPoint(_myTrackContext.upperBound(), myFirstPoint.value());
		}

		Point2D p1 = _myController.curveToViewSpace(myFirstPoint);
		Point2D p2 = _myController.curveToViewSpace(mySecondPoint);

		CCColorTrackController myColorTrackController = (CCColorTrackController) _myController;

		for (double x = p1.getX(); x <= p2.getX(); x++) {
			double myTime = _myController.viewXToTime((int) x, true);
			CCColor myColor = myColorTrackController.blend(myTime);
			g2d.setColor(myColor.toAWTColor());
			g2d.drawLine((int) x, 0, (int) x, height() / 2);
		}
	}
}
