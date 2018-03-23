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
package cc.creativecomputing.controlui.timeline.view.transport;


import java.text.DecimalFormat;

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.control.timeline.point.CCMarkerPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.controlui.CCUIConstants;
import cc.creativecomputing.controlui.timeline.controller.CCTimelineController;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController.CCRulerInterval;
import cc.creativecomputing.controlui.timeline.view.track.CCAbstractTrackView;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.graphics.CCDrawMode;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;

public class CCRulerView extends CCAbstractTrackView {
	
	public static final int MAX_RULER_LABELS = 10;
	public static final double MIN_RULER_INTERVAL = 0.25;
	
	private CCTimelineController _myTimelineController;
	private CCTransportController _myTransportController;
	
	private CCRulerPopUp _myRulerPopUp;
	
	public CCRulerView(CCTimelineController theTimelineController) {
		super();
		if(theTimelineController != null){
			_myTimelineController = theTimelineController;
			_myTransportController = theTimelineController.transportController();
		    _myRulerPopUp = new CCRulerPopUp(this, theTimelineController);
		}
	    
		mouseReleased.add(e -> {
			if(_myTransportController == null)return;
			_myTransportController.mouseReleased(e);
		});
			
			
		mousePressed.add(e -> {
			if(_myTransportController == null)return;
				
			boolean myIsRightClick = e.button == CCGLMouseButton.BUTTON_3 || (e.isControlDown() && e.button== CCGLMouseButton.BUTTON_1);
				
			if (myIsRightClick) {
//					new CCTextInputDialog(
//						"Insert Time", 
//						"Specify the time to insert in seconds.", 
//						"insert",
//						input ->{
//							double myTime = 0;
//							try {
//								myTime = new ExpressionBuilder(input).build().evaluate();
//							} catch (Exception ex) {
//							}
//							_myTimelineController.insertTime(_myTransportController.time(),myTime);
//						}
//					)
//					.location(e.getXOnScreen(), e.getYOnScreen())
//					.size(400,200)
//					.open();
					
				CCLog.info("show pop");
				try {
					_myRulerPopUp.show( e);
				}catch(Exception ex) {
					ex.printStackTrace();
				}
			} else if (e.button== CCGLMouseButton.BUTTON_1) {
				_myTransportController.mousePressed(e);
			}
		});
		
		mouseClicked.add(e ->{_myTransportController.mousePressed(e);});
		
		mouseDragged.add(p -> {
			if (_myTransportController == null)return;
			
			_myTransportController.mouseDragged(p);
			updateUI();
		});
			
		mouseMoved.add(p->{
			if (_myTransportController == null)return;
			
			_myTransportController.mouseMoved(p);
		});	
	}
	
	
	
	private void updateUI() {
	}

	public void timelineController(CCTimelineController theTimelineController){
		_myTimelineController = theTimelineController;
		_myTransportController = theTimelineController.transportController();
	    _myRulerPopUp = new CCRulerPopUp(this, theTimelineController);
	}
	
	public void changeValue(double theValue) {
		if (_myTransportController == null)return;
		_myTransportController.speed(theValue);	
	}
	
	public void render() {
		updateUI();
	}
	
	public void setViewWidth(int theViewWidth) {
		updateUI();
	}
	
	public static final double THIN_STROKE = 1;
	public static final double THICK_STROKE = 2;
	
	public static final CCColor TEXT_COLOR = new CCColor(100);
	public static final CCColor STEP_COLOR = new CCColor(0.8f, 0.8f, 0.8f);
	public static final CCColor SUB_STEP_COLOR = new CCColor(0.9f, 0.9f, 0.9f);
	
	@Override
	public void drawContent(CCGraphics g) {
		g.translate(0, 0);
		g.color(1d);
		g.rect(0, 0, width(), height() );
		
		if(_myTimelineController == null)return;
		if(_myTransportController == null)return;
		
		CCRulerInterval ri = _myTransportController.rulerInterval();

		DecimalFormat myFormat = new DecimalFormat();
        myFormat.applyPattern("#0.##");
		
		double myStart = ri.interval() * (Math.floor(_myTransportController.lowerBound() / ri.interval()) ) ;
		
		for (double step = myStart; step <= _myTransportController.upperBound(); step = step + ri.interval()) {
			
	        int myX = _myTransportController.timeToViewX(step);
	        if(myX < 0)continue;

			g.strokeWeight(THICK_STROKE);
			g.color(SUB_STEP_COLOR);
			g.line(0, height() / 2, width(), height() / 2);
			g.color(STEP_COLOR);
			g.line(myX, 0, myX, height());
			
			int myTimeX = myX;
			
			g.color(SUB_STEP_COLOR);
			g.strokeWeight(THIN_STROKE);
			
			for(int i = 1; i < _myTimelineController.drawRaster();i++) {
				myX = _myTransportController.timeToViewX(step + ri.interval() * i / _myTimelineController.drawRaster());
				g.line(myX, height(), myX, height() / 4 * 3);
			}
			
			
			String myTimeString = _myTransportController.timeToString(step);
			
			g.textFont(CCUIConstants.DEFAULT_FONT);
	        g.color(TEXT_COLOR);
			g.text(myTimeString, myTimeX + 5, 11 * CCUIConstants.SCALE);
		}
		CCControlPoint myCurrentPoint = _myTransportController.trackData().ceiling(new CCControlPoint(_myTransportController.lowerBound(),0));
//		if(myCurrentPoint == null) {
//			myCurrentPoint = _myTransportController.trackData().ceiling(new ControlPoint(_myTransportController.lowerBound(),0));
//		}
        
        while (myCurrentPoint != null) {
            if (myCurrentPoint.time() > _myTransportController.upperBound()) {
                break;
            }

            if(myCurrentPoint instanceof CCMarkerPoint) {
	            CCVector2 myUserPoint = _myTransportController.curveToViewSpace(myCurrentPoint);
	            CCMarkerPoint myMarker = (CCMarkerPoint)myCurrentPoint;
	            int myMarkerX = (int)myUserPoint.x;
	            
	            g.color(new CCColor(1f, 0f,0f));
				g.line(myMarkerX, height()/2, myMarkerX, height());
				g.text(myMarker.name(), myMarkerX + 5, height()/2 + 15);
				
				g.beginShape(CCDrawMode.POLYGON);
				g.vertex(myMarkerX, height()/2 + 5);
				g.vertex(myMarkerX - 5, height()/2);
				g.vertex(myMarkerX + 5, height()/2);
				g.endShape();
            }
            
            if(myCurrentPoint.type() == CCControlPointType.TIMED_EVENT) {
            	g.strokeWeight(0.5);
            	g.color(new CCColor(100,100,255,50));
            	
            	CCTimedEventPoint myPoint = (CCTimedEventPoint) myCurrentPoint;
        		CCVector2 myLowerCorner = _myTransportController.curveToViewSpace(new CCControlPoint(myCurrentPoint.time(), 1));
        		CCVector2 myUpperCorner = _myTransportController.curveToViewSpace(new CCControlPoint(myPoint.endPoint().time(),0));

        		g.rect(
        			myLowerCorner.x, height() * 3 / 4,
        			myUpperCorner.x - myLowerCorner.x, height()/4
        		);
        		g.color(new CCColor(0,0,255));
        		g.line(myLowerCorner.x, height() * 3 / 4, myLowerCorner.x, height());
        		g.line(myUpperCorner.x, height() * 3 / 4, myUpperCorner.x, height());
            }
            
			
            myCurrentPoint = myCurrentPoint.next();
        }
		
		int myTransportX = Math.max(0, _myTransportController.timeToViewX(_myTransportController.time()));

		g.color(0.6f);
		g.beginShape(CCDrawMode.POLYGON);
		g.vertex(myTransportX - 5, height()/2);
		g.vertex(myTransportX - 5, height() - 5);
		g.vertex(myTransportX, height());
		g.vertex(myTransportX + 5, height() - 5);
		g.vertex(myTransportX + 5, height()/2);
		g.endShape();
		
		g.color(new CCColor(0.8f, 0.8f, 0.8f));
		g.line(myTransportX, height()/2, myTransportX, height());
		
		int myLoopStartX = Math.max(0,_myTransportController.timeToViewX(_myTransportController.loopStart()));
		int myLoopEndX = Math.max(0,_myTransportController.timeToViewX(_myTransportController.loopEnd()));
		
		if(myLoopStartX == myLoopEndX)return;

		if(_myTransportController.doLoop()) {
			g.color(new CCColor(1f, 0.0f,0.0f,0.5f));
			g.rect(myLoopStartX, 0, myLoopEndX - myLoopStartX, height()/2, true);
			g.color(new CCColor(1f, 0.0f,0.0f,0.25f));
			g.rect(myLoopStartX, 0, myLoopEndX - myLoopStartX, height()/2);
		}else {
			g.color(new CCColor(0.7f, 0.7f,0.7f,0.5f));
			g.rect(myLoopStartX, 0, myLoopEndX - myLoopStartX, height()/2, true);
			g.color(new CCColor(0.7f, 0.7f,0.7f,0.25f));
			g.rect(myLoopStartX, 0, myLoopEndX - myLoopStartX, height()/2);
		}
	}

	@Override
	public void mute(boolean theMute) {}
	
	@Override
	public void min(double theMin) {}
	
	@Override
	public void max(double theMax) {}

	@Override
	public void color(CCColor theColor) {}

}
