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

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.track.CCCurveTrackController;
import cc.creativecomputing.controlui.timeline.tools.CCTimelineTools;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCCurveTrackDataView extends CCAbstractTrackDataView<CCCurveTrackController>{
	
	private SwingCurveTrackPopup _myToolChooserPopup;

	public CCCurveTrackDataView(CCTimelineController theTimelineController, CCCurveTrackController theTrackController) {
		super(theTimelineController, theTrackController);
		_myToolChooserPopup = new SwingCurveTrackPopup(theTrackController, theTimelineController);
	}
	
//	@Override
//	public void showPopUp(MouseEvent theEvent) {
//		_myToolChooserPopup.show(SwingCurveTrackDataView.this, theEvent);
//	}
	
	private void drawCurve(CCGraphics g) {
        if (_myController.trackData().size() == 0) {
        	CCVector2 p1 = _myController.curveToViewSpace(new CCControlPoint(0,_myController.value(0)));
            g.color(_myFillColor);
            g.line(0, p1.y,width(), p1.y);
            return;
        }
        
        CCControlPoint myMinPoint = _myController.trackData().floor(new CCControlPoint(_myTrackContext.lowerBound(), 0));
		if(myMinPoint == null){
			myMinPoint = new CCControlPoint(
				_myTrackContext.lowerBound(), 
				_myController.trackData().value(_myTrackContext.lowerBound())
			);
		}
		
        List<CCVector2> myPath = new ArrayList<>();
        myPath.add(_myController.curveToViewSpace(myMinPoint));
        
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
	        	drawCurvePiece(myLastPoint, myCurrentPoint, myPath);
	        	myLastPoint = myCurrentPoint;
	        	myCurrentPoint = myCurrentPoint.next();
		}
        drawCurvePiece(myLastPoint, myMaxPoint, myPath);
        	   
        if(!_myController.track().mute()){
	        g.color(_myFillColor);
        }else{
	        g.color(_myFillColor.brighter());
        }
        g.beginShape(CCDrawMode.TRIANGLE_STRIP);
        for(CCVector2 myPoint:myPath){
        	g.vertex(myPoint.x, myPoint.y);
        	g.vertex(myPoint.x, height());
        }
        g.endShape();
    	
        
        g.color(_myLineColor);
        g.beginShape(CCDrawMode.LINE_STRIP);
        for(CCVector2 myPoint:myPath){
        	g.vertex(myPoint.x, myPoint.y);
        }
        g.endShape();
    }
	
	@Override
	public void renderData(CCGraphics g) {
		drawCurve(g);

		// paint curve points
		g.strokeWeight(0.5);
		g.color(_myDotColor);
		CCControlPoint myCurrentPoint = _myController.trackData().getFirstPointAfter(_myTrackContext.lowerBound());

		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound()) {
				break;
			}
			CCVector2 myUserPoint = _myController.curveToViewSpace(myCurrentPoint);
			if(myCurrentPoint.isSelected()){
				g.color(CCColor.RED);
			}else{
				g.color(_myDotColor);
			}
			g.point(myUserPoint);
					
			//g.drawString(myCurrentPoint.value() +"", myUserPoint.x, myUserPoint.y);

			if (myCurrentPoint.type() == CCControlPointType.BEZIER && (_myController.activeTool() == CCTimelineTools.BEZIER_POINT || _myController.activeTool() == CCTimelineTools.CURVE)) {
				CCBezierControlPoint myBezierPoint = (CCBezierControlPoint) myCurrentPoint;
					
				CCVector2 myUserHandle = _myController.curveToViewSpace(myBezierPoint.inHandle());
				g.line(myUserPoint, myUserHandle);
				g.point(myUserHandle);

				myUserHandle = _myController.curveToViewSpace(myBezierPoint.outHandle());
				g.line(myUserPoint, myUserHandle);
				g.point(myUserHandle);
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
//				g.drawString(
//					CCFormatUtil.formatTime(myDraggedPoint.time()) + " : " + _myController.property().valueString(),
//					myPoint.x + 10, 
//					 _myController.curveToViewSpace(new CCControlPoint(myTime, myValue * (1 - 12f/height()))).y
//				);
			}else{
				double myValue = _myController.trackData().value(myTime);
//				g.drawString(
//					_myController.property().valueString(), 
//					myViewX + 10, 
//					 _myController.curveToViewSpace(new CCControlPoint(myTime, myValue * (1 - 12f/height()))).y
//				);
			}
		}
		
		if(_myIsMousePressed){
			CCControlPoint myDraggedPoint = _myController.draggedPoint();
			if(myDraggedPoint != null){
				CCVector2 myPoint = _myController.curveToViewSpace(myDraggedPoint);
				g.line(0, myPoint.y, width(), myPoint.y);
				g.line(myPoint.x, 0, myPoint.x, height());
			}
			if(_myController.selectionStart() != null){
				double x = CCMath.min(_myController.selectionStart().x,_myController.selectionEnd().x);
				double y = CCMath.min(_myController.selectionStart().y,_myController.selectionEnd().y);
				double width = CCMath.abs(_myController.selectionEnd().x - _myController.selectionStart().x);
				double height = CCMath.abs(_myController.selectionEnd().y - _myController.selectionStart().y);
				g.color(new CCColor(0.15f, 0.15f, 0.15f, 0.05f));
				g.rect(x, y, width, height);
				g.color(CCColor.RED);
				g.rect(x, y, width, height, true);
			}
		}
	}
}
