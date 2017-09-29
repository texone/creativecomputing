package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.BasicStroke;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.tools.CCEventTrackTool.EventAction;
import cc.creativecomputing.controlui.timeline.controller.track.CCEventTrackController;
import cc.creativecomputing.controlui.timeline.view.SwingEventCreatePopup;
import cc.creativecomputing.controlui.timeline.view.SwingEventPopup;
import cc.creativecomputing.math.CCMath;

public class SwingEventTrackDataView extends SwingAbstractTrackDataView<CCEventTrackController>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7428010925887801272L;
	private SwingEventPopup _myEventPopup;
    private SwingEventCreatePopup _myCreateEventPopup;
    protected SwingTrackDataRenderer _myTrackDataRenderer;

	public SwingEventTrackDataView(SwingTrackDataRenderer theDataRenderer, TimelineController theTimelineController, CCEventTrackController theTrackController) {
		super(theTimelineController, theTrackController);
		
		_myEventPopup = new SwingEventPopup((CCEventTrackController)_myController);
		_myCreateEventPopup = new SwingEventCreatePopup((CCEventTrackController)_myController);
    	_myTrackDataRenderer = theDataRenderer;
		
		getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released BACK_SPACE"), "delete event");
        getActionMap().put("delete event",
        	
        	new AbstractAction() {
					
        		/**
        		 * 
        		 */
        		private static final long serialVersionUID = 6852365663996197271L;

				@Override
        		public void actionPerformed(ActionEvent e) {
        			TimedEventPoint myEvent = _myController.editedEvent();
        			if(myEvent != null && myEvent.isSelected()){
        				_myController.delete(myEvent);
        				render();
        			}			
        		}
        	}
       );
    
	}

	@Override
	public void showPopUp(MouseEvent theEvent) {
		TimedEventPoint myEvent = ((CCEventTrackController)_myController).clickedPoint(theEvent);
		
		if(myEvent != null) {
			_myEventPopup.event(myEvent);
			_myEventPopup.show(SwingEventTrackDataView.this, theEvent.getX(), theEvent.getY());
		}else {
			_myCreateEventPopup.event(theEvent);
			_myCreateEventPopup.show(SwingEventTrackDataView.this, theEvent.getX(), theEvent.getY());
		}
	}
	
	@Override
	public void renderData(Graphics2D g2d) {
    	BasicStroke myThinStroke = new BasicStroke(0.5f);
		g2d.setStroke(myThinStroke);
		g2d.setColor(_myDotColor);
		ControlPoint myCurrentPoint = _myController.trackData().floor(new ControlPoint(_myTrackContext.lowerBound(),0));
		if(myCurrentPoint == null) {
			myCurrentPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.lowerBound(),0));
		}
		
		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound()) {
				break;
			}

			if (myCurrentPoint.getType() == ControlPointType.TIMED_EVENT) {
				TimedEventPoint myPoint = (TimedEventPoint) myCurrentPoint;
				double myLowerBound = CCMath.max(myCurrentPoint.time(), context().lowerBound());
	        	double myUpperBound = CCMath.min(myPoint.endPoint().time(), context().upperBound());
				Point2D myLowerCorner = _myController.curveToViewSpace(new ControlPoint(myLowerBound, 1));
				Point2D myUpperCorner = _myController.curveToViewSpace(new ControlPoint(myUpperBound,0));

				if(myPoint.isSelected()){
					g2d.setColor(_myFillColor.darker());
				}else{
					g2d.setColor(_myFillColor);
				}
				g2d.fillRect(
					(int) myLowerCorner.getX(), 0,
					(int) myUpperCorner.getX() - (int) myLowerCorner.getX(), getHeight()
				);
				g2d.setColor(_myDotColor);
				g2d.drawLine((int) myLowerCorner.getX(), getHeight(), (int) myLowerCorner.getX(), 0);
				g2d.drawLine((int) myUpperCorner.getX(), getHeight(), (int) myUpperCorner.getX(), 0);
				Shape myClip = g2d.getClip();
				g2d.setClip(
					(int) myLowerCorner.getX(), 0,
					(int) myUpperCorner.getX() - (int) myLowerCorner.getX(), getHeight()
				);
				if(_myTrackDataRenderer != null) {
					_myTrackDataRenderer.renderTimedEvent(myPoint, this, g2d);
				}

				Point2D myPos = _myController.curveToViewSpace(new ControlPoint(myPoint.time(),1));
				g2d.drawString(myPoint.contentOffset() + "", (int) myPos.getX() + 5, (int) myPos.getY() + 35);
				((CCEventTrackController)_myController).renderTimedEvent(myPoint, myLowerCorner, myUpperCorner, myLowerBound, myUpperBound, g2d);
				g2d.setClip(myClip);
			}
			
			myCurrentPoint = myCurrentPoint.getNext();
		}
    }
	
	private void drawArrow(Graphics g, int theStart, int theEnd, boolean flip){
		int myOffset = (getHeight() / 16); 
		if(theStart > theEnd) myOffset = -myOffset;
		theStart =  theEnd - 3 * myOffset;
		int myArrowOffset = theEnd - myOffset;
		
//		g.drawLine(theStart, getHeight() / 2, theEnd, getHeight() / 2);
		if(flip){
			int tmp = theEnd;
			theEnd = myArrowOffset;
			myArrowOffset = tmp;
		}
		g.drawLine(myArrowOffset, getHeight() / 2 - myOffset, theEnd, getHeight() / 2);
		g.drawLine(myArrowOffset, getHeight() / 2 + myOffset, theEnd, getHeight() / 2);
	}
	
	@Override
	public void drawTimelineInfos(Graphics g) {
		EventAction myDragAction = ((CCEventTrackController)_myController).dragAction();
		
		if(myDragAction == null)return;
	
		ControlPoint myDraggedPoint = _myController.draggedPoint();
		
		if(myDraggedPoint == null)return;
		
		Graphics2D g2 = (Graphics2D) g;
		g2.setStroke(new BasicStroke(2));
		
		Point2D myPoint = _myController.curveToViewSpace(myDraggedPoint);
		
		if(_myMouseEvent == null){
			return;
		}
		
		switch(myDragAction){
			case DRAG_END_OFFSET:
			case DRAG_START:
				g.fillRect((int)myPoint.getX() - 6 , getHeight() / 2 - getHeight() / 8, 12, getHeight() / 4);
				break;
				
			case DRAG_START_OFFSET:
			case DRAG_END:
				g.drawLine((int)myPoint.getX(), 0, (int)myPoint.getX(), getHeight());
				break;
				
			case DRAG_BLOCK:
				drawArrow(g, _myMouseEvent.getX() - 50, _myMouseEvent.getX() - 10, true);
				drawArrow(g, _myMouseEvent.getX() + 50, _myMouseEvent.getX() + 10, true);
				g.fillRect(_myMouseEvent.getX() - 6 , getHeight() / 2 - getHeight() / 8, 12, getHeight() / 4);
				break;

			case DRAG_CONTENT:
				drawArrow(g, _myMouseEvent.getX() - 50, _myMouseEvent.getX() - 10, true);
				drawArrow(g, _myMouseEvent.getX() + 50, _myMouseEvent.getX() + 10, true);
				g.drawLine(_myMouseEvent.getX(), 0, _myMouseEvent.getX(), getHeight());
				break;
		}
		
		switch(myDragAction){
		case DRAG_START:
		case DRAG_END:
		case DRAG_END_OFFSET:
		case DRAG_START_OFFSET:
			drawArrow(g, (int)myPoint.getX() - 50, (int)myPoint.getX() - 10, false);
			drawArrow(g, (int)myPoint.getX() + 50, (int)myPoint.getX() + 10, false);
			break;
		default:
			break;
		}
	}
}
