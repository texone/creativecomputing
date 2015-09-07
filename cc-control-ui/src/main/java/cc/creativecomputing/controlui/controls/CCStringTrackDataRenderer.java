package cc.creativecomputing.controlui.controls;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataRenderer;
import cc.creativecomputing.controlui.timeline.view.track.SwingTrackDataView;
import cc.creativecomputing.core.logging.CCLog;

public class CCStringTrackDataRenderer extends SwingTrackDataRenderer{

	
	@Override
	public void renderTimedEvent(TimedEventPoint theTimedEvent, SwingTrackDataView theView, Graphics2D theG2d) {
		if(theTimedEvent.content() == null || theTimedEvent.content().value() == null) {
			return;
		}
		
		
		Point2D myPos = theView.controller().curveToViewSpace(new ControlPoint(theTimedEvent.time(),1));
		Point2D myEndPos = theView.controller().curveToViewSpace(new ControlPoint(theTimedEvent.endTime(),1));
		double width = myEndPos.getX() - myPos.getX();
		theG2d.setColor(new Color(0,0,0,100));
		
		FontMetrics myMetrix = theG2d.getFontMetrics();
		String myString = theTimedEvent.content().value().toString();
		int myIndex = myString.length() - 1;
		StringBuffer myText = new StringBuffer();
		while(myIndex >= 0 && myMetrix.stringWidth(myText.toString() + myString.charAt(myIndex)) < width - 5){
			myText.insert(0, myString.charAt(myIndex));
			myIndex--;
		}
		theG2d.drawString(myText.toString(), (int) myPos.getX() + 5, (int) myPos.getY() + 15);
	}
	
	
}