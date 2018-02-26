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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.CCVector2;

import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.tools.CCTimelineTools;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCMath;

public class SwingCurveTrackDataView extends CCAbstractTrackDataView<CCCurveTrackController>{
	
    /**
	 * 
	 */
	private static final long serialVersionUID = 2769109052305237545L;
	private SwingCurveTrackPopup _myToolChooserPopup;

	public SwingCurveTrackDataView(CCTimelineController theTimelineController, CCCurveTrackController theTrackController) {
		super(theTimelineController, theTrackController);
		_myToolChooserPopup = new SwingCurveTrackPopup(theTrackController, theTimelineController);
	}
	
	@Override
	public void showPopUp(MouseEvent theEvent) {
		_myToolChooserPopup.show(SwingCurveTrackDataView.this, theEvent);
	}
	
	private void drawCurve(CCGraphics g) {
        if (_myController.trackData().size() == 0) {
        	GeneralPath myPath = new GeneralPath();
        	CCVector2 p1 = _myController.curveToViewSpace(new CCControlPoint(0,_myController.value(0)));
            myPath.moveTo(0, p1.getY());
            myPath.lineTo(width(), p1.getY());
            
            g2d.setColor(_myFillColor);
            g2d.draw(myPath);
            return;
        }
        
        CCControlPoint myMinPoint = _myController.trackData().floor(new CCControlPoint(_myTrackContext.lowerBound(), 0));
		if(myMinPoint == null){
			myMinPoint = new CCControlPoint(
				_myTrackContext.lowerBound(), 
				_myController.trackData().value(_myTrackContext.lowerBound())
			);
		}
        CCVector2 p1 = _myController.curveToViewSpace(myMinPoint);
		
        GeneralPath myPath = new GeneralPath();
        myPath.moveTo(p1.x, p1.getY());
        
        CCControlPoint myMaxPoint = _myController.trackData().ceiling(new CCControlPoint(_myTrackContext.upperBound(), 0));
		
		if(myMaxPoint == null){
			myMaxPoint = new CCControlPoint(
				_myTrackContext.upperBound(), 
				_myController.trackData().value(_myTrackContext.upperBound())
			);
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);
        
        CCControlPoint myCurrentPoint = _myController.trackData().ceiling(new CCControlPoint(_myTrackContext.lowerBound(), 0));
        CCControlPoint myLastPoint = myMinPoint;
        while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
	        	drawCurvePiece(myLastPoint, myCurrentPoint, myPath, false);
	        	myLastPoint = myCurrentPoint;
	        	myCurrentPoint = myCurrentPoint.next();
		}
        drawCurvePiece(myLastPoint, myMaxPoint, myPath, false);
        
        myPath.lineTo(this.width(), this.height());
        myPath.lineTo(0, this.height());
        myPath.closePath();

	   
        if(!_myController.track().mute()){
	        g2d.setPaint(new GradientPaint(0, 0, _myFillColor, 0, height(), _myLineColor));
        }else{
	        g2d.setPaint(new GradientPaint(0, 0, _myFillColor.brighter(), 0, height(), _myLineColor.brighter()));
        }
        g2d.fill(myPath);
    	
        
        g2d.setColor(_myLineColor);
        try {
        		g2d.draw(myPath);
        }catch(Exception e) {
        	
        }
    }
	
	@Override
	public void renderData(CCGraphics g) {
		drawCurve(g);

		// paint curve points
		BasicStroke myThinStroke = new BasicStroke(0.5f);
		g2d.setStroke(myThinStroke);
		g2d.setColor(_myDotColor);
		CCControlPoint myCurrentPoint = _myController.trackData().getFirstPointAt(_myTrackContext.lowerBound());

		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound()) {
				break;
			}
			CCVector2 myUserPoint = _myController.curveToViewSpace(myCurrentPoint);
			if(myCurrentPoint.isSelected()){
				g2d.setColor(Color.red);
			}else{
				g2d.setColor(_myDotColor);
			}
			point(myUserPoint);
					
			//g2d.drawString(myCurrentPoint.value() +"", myUserPoint.x, myUserPoint.getY());

			if (myCurrentPoint.type() == CCControlPointType.BEZIER && (_myController.activeTool() == CCTimelineTools.BEZIER_POINT || _myController.activeTool() == CCTimelineTools.CURVE)) {
				CCBezierControlPoint myBezierPoint = (CCBezierControlPoint) myCurrentPoint;
					
				CCVector2 myUserHandle = _myController.curveToViewSpace(myBezierPoint.inHandle());
				line(myUserPoint, myUserHandle);
				point(myUserHandle);

				myUserHandle = _myController.curveToViewSpace(myBezierPoint.outHandle());
				line(myUserPoint, myUserHandle);
				point(myUserHandle);
			}
			myCurrentPoint = myCurrentPoint.next();
		}
	}
	
	@Override
	public void drawTimelineInfos(CCGraphics g) {
		
		double myTime = _myTimelineController.transportController().time();
		int myViewX = _myController.timeToViewX(myTime) ;

		if (myViewX >= 0 && myViewX <= width()) {
			
			CCControlPoint myDraggedPoint = _myController.draggedPoint();
				
			if(_myIsMousePressed && myDraggedPoint != null){
				double myValue = myDraggedPoint.value();
				cc.creativecomputing.math.CCVector2 myPoint = _myController.curveToViewSpace(myDraggedPoint);
				g.drawString(
					CCFormatUtil.formatTime(	myDraggedPoint.time()) + " : " + _myController.property().valueString(),
					myPoint.x + 10, 
					 _myController.curveToViewSpace(new CCControlPoint(myTime, myValue * (1 - 12f/height()))).getY()
				);
			}else{
				double myValue = _myController.trackData().value(myTime);
				g.drawString(
					_myController.property().valueString(), 
					myViewX + 10, 
					 _myController.curveToViewSpace(new CCControlPoint(myTime, myValue * (1 - 12f/height()))).getY()
				);
			}
		}
		
		if(_myIsMousePressed){
			CCControlPoint myDraggedPoint = _myController.draggedPoint();
			if(myDraggedPoint != null){
				CCVector2 myPoint = _myController.curveToViewSpace(myDraggedPoint);
				g.line(0, myPoint.getY(), width(), myPoint.getY());
				g.line(myPoint.x, 0, myPoint.x, height());
			}
			if(_myController.selectionStart() != null){
				double x = CCMath.min(_myController.selectionStart().x,_myController.selectionEnd().x);
				double y = CCMath.min(_myController.selectionStart().getY(),_myController.selectionEnd().getY());
				double width = CCMath.abs(_myController.selectionEnd().x - _myController.selectionStart().x);
				double height = CCMath.abs(_myController.selectionEnd().getY() - _myController.selectionStart().getY());
				g.setColor(new Color(0.15f, 0.15f, 0.15f, 0.05f));
				g.fillRect(x, y, width, height);
				g.setColor(Color.red);
				g.drawRect(x, y, width, height);
			}
		}
	}
}
