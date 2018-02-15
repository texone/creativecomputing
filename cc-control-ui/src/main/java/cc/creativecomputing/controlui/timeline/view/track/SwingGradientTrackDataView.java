package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCGradientTrackController;
import cc.creativecomputing.core.logging.CCLog;

public class SwingGradientTrackDataView extends SwingBlendableTrackDataView<CCGradientTrackController> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8117392771151385531L;
	
	public SwingGradientTrackDataView(TimelineController theTimelineController, CCGradientTrackController theTrackController) {
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
