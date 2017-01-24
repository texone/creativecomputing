package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.AbstractAction;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ColorPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.TransportController;
import cc.creativecomputing.controlui.timeline.controller.TransportController.RulerInterval;
import cc.creativecomputing.controlui.timeline.controller.track.ColorTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.CurveTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.EventTrackController;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.timeline.view.SwingConstants;
import cc.creativecomputing.controlui.timeline.view.SwingEventCreatePopup;
import cc.creativecomputing.controlui.timeline.view.SwingEventPopup;
import cc.creativecomputing.controlui.timeline.view.SwingRulerView;
import cc.creativecomputing.controlui.timeline.view.SwingToolChooserPopup;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;


@SuppressWarnings("serial")
public class SwingTrackDataView extends JPanel {
	
	public static interface SwingTrackDataViewListener{
		public void onRender(Graphics2D theG2D);
	}

    private BufferedImage _myRenderBuffer;

    private Color _myLineColor = SwingConstants.LINE_COLOR;
    private Color _myFillColor = SwingConstants.FILL_COLOR;
    private Color _myDotColor = SwingConstants.DOT_COLOR;

    private boolean _myRedraw = true;
    
    private TrackController _myController;
    private TimelineController _myTimelineController;
    private TrackContext _myTrackContext;
    
    private SwingEventPopup _myEventPopup;
    private SwingEventCreatePopup _myCreateEventPopup;
    private SwingToolChooserPopup _myToolChooserPopup;
    private SwingTrackDataRenderer _myTrackDataRenderer;
    
    private CCListenerManager<SwingTrackDataViewListener> _myEvents = CCListenerManager.create(SwingTrackDataViewListener.class);

    public SwingTrackDataView(TimelineController theTimelineController, TrackController theTrackDataController) {
    	this(new SwingToolChooserPopup(theTrackDataController.tool()), null, theTimelineController, theTrackDataController);
    }
    
    public CCListenerManager<SwingTrackDataViewListener> events(){
    	return _myEvents;
    }
    
    private boolean _myIsMousePressed = false;
    
    public SwingTrackDataView(
    	SwingToolChooserPopup theToolChooserPopup, 
    	SwingTrackDataRenderer theDataRenderer,
    	TimelineController theTimelineController, 
    	TrackController theTrackDataController
    ) {
    	_myTimelineController = theTimelineController;
    	_myTrackContext = theTrackDataController.context();
    	_myTrackDataRenderer = theDataRenderer;
        _myController = theTrackDataController;
    	_myToolChooserPopup = theToolChooserPopup;
    	if(_myController instanceof EventTrackController) {
    		_myEventPopup = new SwingEventPopup((EventTrackController)_myController);
    		_myCreateEventPopup = new SwingEventCreatePopup((EventTrackController)_myController);
    	}
        
        setName("timeline" + (int) Math.floor(100 * Math.random()));
//        setBorder(BorderFactory.createLineBorder(Color.gray));
        
        addMouseListener(new MouseAdapter() {
        	
        	@Override
        	public void mouseClicked(MouseEvent theE) {
        	}
        	
        	@Override
        	public void mousePressed(MouseEvent e) {
    			boolean myIsRightClick = e.getButton() == MouseEvent.BUTTON3 || (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1);
    			
        		if(_myController instanceof EventTrackController) {
        			TimedEventPoint myEvent = ((EventTrackController)_myController).clickedPoint(e);
        			
        			
        			if (myIsRightClick) {
        				if(myEvent != null) {
        					_myEventPopup.event(myEvent);
        					_myEventPopup.show(SwingTrackDataView.this, e.getX(), e.getY());
        				}else {
        					_myCreateEventPopup.event(e);
        					_myCreateEventPopup.show(SwingTrackDataView.this, e.getX(), e.getY());
        				}
        				
        			} else if (e.getButton() == MouseEvent.BUTTON1) {
                		_myIsMousePressed = true;
        				_myController.mousePressed(e);
        			}
        		}else if(_myController instanceof ColorTrackController){
        			_myController.mousePressed(e);
        		}else{
        			if (myIsRightClick) {
        				_myToolChooserPopup.trackController(_myController);
        				_myToolChooserPopup.show(SwingTrackDataView.this, e.getX(), e.getY());
    				} else if (e.getButton() == MouseEvent.BUTTON1) {
    	        		_myIsMousePressed = true;
    					_myController.mousePressed(e);
    				}
        		}
        	}
        	
        	@Override
        	public void mouseReleased(MouseEvent e) {
        		
        		if (e.getButton() == MouseEvent.BUTTON1) {
            		_myIsMousePressed = false;
            		repaint();
        			_myController.mouseReleased(e);
        		}

        		requestFocusInWindow();
        	}
        });
        
        addMouseMotionListener(new MouseAdapter() {
        	@Override
        	public void mouseDragged(MouseEvent e) {
        		_myController.mouseDragged(e);
        	}
        	
        	@Override
        	public void mouseMoved(MouseEvent e) {
        		_myController.mouseMoved(e);
        	}
        });
        
        getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("released BACK_SPACE"), "delete event");
        getActionMap().put("delete event",
        	new AbstractAction() {
					
        		@Override
        		public void actionPerformed(ActionEvent e) {
        			if(_myController instanceof EventTrackController) {
        				EventTrackController myEventTrackController = (EventTrackController)_myController;
        				TimedEventPoint myEvent = myEventTrackController.editedEvent();
        				if(myEvent != null && myEvent.isSelected()){
        					myEventTrackController.delete(myEvent);
            				render();
        				}
        			}
        		}
        });
       
    }
    
    public TrackController controller(){
    	return _myController;
    }
    
    public TrackContext context(){
    	return _myTrackContext;
    }

    public void setRedrawFlag(boolean theFlag) {
        _myRedraw = theFlag;
    }

    public boolean getRedrawFlag() {
        return _myRedraw;
    }
    
    public Color fillColor(){
    	return _myFillColor;
    }

    public Color lineColor(){
    	return _myLineColor;
    }

//    public Dimension getPreferredSize() {
//        return new Dimension(Integer.MAX_VALUE, 150);
//    }
//
//    public Dimension getMaximumSize() {
//        return new Dimension(Integer.MAX_VALUE, 500);
//    }
    
    private Color brighter(Color theColor, float theScale, int theAlpha) {
        float myRed = (255 - theColor.getRed()) * theScale;
        float myGreen = (255 - theColor.getGreen()) * theScale;
        float myBlue = (255 - theColor.getBlue()) * theScale;
        
        return new Color(
            255 - (int)myRed,
            255 - (int)myGreen,
            255 - (int)myBlue,
            theAlpha
        );
    }
    
    //EMIL
    public void color(Color theColor) {
        _myDotColor = theColor;
        _myLineColor = brighter(theColor,0.5f, 125);
        _myFillColor = brighter(theColor,0.25f, 125);
        render();
    }

    private void drawCurve(Graphics g) {
        if (_myController.trackData().size() == 0) {
        	GeneralPath myPath = new GeneralPath();
        	Point2D p1 = _myController.curveToViewSpace(new ControlPoint(0,_myController.value(0)));
            myPath.moveTo(0, p1.getY());
            myPath.lineTo(getWidth(), p1.getY());
            
            g2d.setColor(_myFillColor);
            g2d.draw(myPath);
            return;
        }
        
        ControlPoint myMinPoint = _myController.trackData().floor(new ControlPoint(_myTrackContext.lowerBound(), 0));
		if(myMinPoint == null){
			myMinPoint = new ControlPoint(
				_myTrackContext.lowerBound(), 
				_myController.trackData().value(_myTrackContext.lowerBound())
			);
		}
        Point2D p1 = _myController.curveToViewSpace(myMinPoint);
		
        GeneralPath myPath = new GeneralPath();
        myPath.moveTo(p1.getX(), p1.getY());
        
        ControlPoint myMaxPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.upperBound(), 0));
		
		if(myMaxPoint == null){
			myMaxPoint = new ControlPoint(
				_myTrackContext.upperBound(), 
				_myController.trackData().value(_myTrackContext.upperBound())
			);
		}
		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);
        
        ControlPoint myCurrentPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.lowerBound(), 0));
        ControlPoint myLastPoint = myMinPoint;
        while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
        	drawCurvePiece(myLastPoint, myCurrentPoint, myPath);
        	myLastPoint = myCurrentPoint;
        	myCurrentPoint = myCurrentPoint.getNext();
		}
        drawCurvePiece(myLastPoint, myMaxPoint, myPath);
        
        myPath.lineTo(this.getWidth(), this.getHeight());
        myPath.lineTo(0, this.getHeight());
        myPath.closePath();

	   
        if(!_myController.track().mute()){
	        g2d.setPaint(new GradientPaint(0, 0, _myFillColor, 0, getHeight(), _myLineColor));
        }else{
	        g2d.setPaint(new GradientPaint(0, 0, _myFillColor.brighter(), 0, getHeight(), _myLineColor.brighter()));
        }
        g2d.fill(myPath);
    	
        
        g2d.setColor(_myLineColor);
        g2d.draw(myPath);
    }

    private void drawCurvePiece(ControlPoint myFirstPoint, ControlPoint mySecondPoint, GeneralPath thePath) {
        if (myFirstPoint.equals(mySecondPoint)) {
            return;
        }

        if (mySecondPoint == null) {
            mySecondPoint = new ControlPoint(_myTrackContext.upperBound(), myFirstPoint.value());
        }
        
        boolean myIsBezier = false;
        Point2D p1 = _myController.curveToViewSpace(myFirstPoint);
        Point2D p2 = _myController.curveToViewSpace(mySecondPoint);
        double myA1X = p1.getX();
        double myA1Y = p1.getY();
        double myA2X = p2.getX();
        double myA2Y = p2.getY();
        double myX = p2.getX();
        double myY = p2.getY();
        
        if(mySecondPoint.getType() == ControlPointType.STEP){
        	thePath.lineTo(myA2X, myA1Y);
        	thePath.lineTo(myA2X, myA2Y);
        	return;
        }
        
        if(mySecondPoint.getType() == ControlPointType.BEZIER){
        	myIsBezier = true;
        	BezierControlPoint myBezier2Point = (BezierControlPoint)mySecondPoint;
        	Point2D myHandle = _myController.curveToViewSpace(myBezier2Point.inHandle());
        	myA2X = myHandle.getX();
        	myA2Y = myHandle.getY();
        	
        }
        if(myFirstPoint.getType() == ControlPointType.BEZIER){
        	myIsBezier = true;
        	BezierControlPoint myBezier1Point = (BezierControlPoint)myFirstPoint;
        	Point2D myHandle = _myController.curveToViewSpace(myBezier1Point.outHandle());
        	myA1X = myHandle.getX();
        	myA1Y = myHandle.getY();
    	}
        if(myIsBezier){
        	thePath.curveTo(myA1X, myA1Y, myA2X, myA2Y, myX, myY);
        	return;
        }
        
        if(mySecondPoint.getType() == ControlPointType.LINEAR){
        	thePath.lineTo(myX, myY);
        	return;
        }

//        if(mySecondPoint.getType() == ControlPointType.CUBIC && mySecondPoint.hasNext()){
//        	ControlPoint myNextPoint = mySecondPoint.getNext();
//        	Point2D myp2 = _myController.curveToViewSpace(myNextPoint);
//        	thePath.quadTo(myX, myY, myp2.getX(), myp2.getY());
//        	return;
//        }
        
//        if(theDrawInterval){
	        double myInterval = SwingTrackView.GRID_INTERVAL / getWidth() * (_myTrackContext.viewTime());
	        double myStart = myInterval * Math.floor(myFirstPoint.time() / myInterval);
	
	        for (double step = myStart + myInterval; step < mySecondPoint.time(); step = step + myInterval) {
	            double myValue = _myController.trackData().value(step);
	            p1 = _myController.curveToViewSpace(new ControlPoint(step, myValue));
	            thePath.lineTo(p1.getX(), p1.getY());
	        }
//        }
        
        thePath.lineTo(p2.getX(), p2.getY());     
    }

//    private void drawGridLines(Graphics g) {
//        double myNumberOfLines = (_myController.viewTime()) / TrackView.GRID_INTERVAL;
//        int myIntervalFactor = 1;
//        if (myNumberOfLines > MAX_GRID_LINES) {
//            myIntervalFactor = (int) (myNumberOfLines / MAX_GRID_LINES + 1);
//        }
//        double myStart = TrackView.GRID_INTERVAL * (Math.floor(_myController.lowerBound() / TrackView.GRID_INTERVAL));
//        for (double step = myStart; step <= _myController.upperBound(); step = step + myIntervalFactor * TrackView.GRID_INTERVAL) {
//            double myX = (step - _myController.lowerBound()) / (_myController.viewTime()) * getWidth();
//            g.setColor(new Color(0.9f, 0.9f, 0.9f));
//            g.drawLine((int) myX, 0, (int) myX, this.getHeight());
//        }
//    }

    public void render() {
    	try{
	        // checks if the current time-line is visible in the parent containers,
	        // so only visible time-line panels are rendered i.e. when zooming
	        Rectangle myVisibleRect = new Rectangle();
	        computeVisibleRect(myVisibleRect);
	
	        if (!myVisibleRect.isEmpty()) {
	        	renderImplementation();
	        	_myRedraw = false;
	        }else{
	        	_myRedraw = true;
	        }
	        
	        events().proxy().onRender(g2d);
    	}catch(NullPointerException e){
    		
    	}
    }
    
    private Graphics2D g2d = null;
    
    private void createContext(){
    	try {
	    	_myRenderBuffer = new BufferedImage(getWidth(), getHeight(),BufferedImage.TYPE_INT_ARGB);
			Graphics g = _myRenderBuffer.getGraphics();
	
			RenderingHints myRenderingHints = new RenderingHints(
				RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON
			);
			myRenderingHints.put(
				RenderingHints.KEY_RENDERING,
				RenderingHints.VALUE_RENDER_QUALITY
			);
			
			
			g2d = (Graphics2D) g;
			g2d.setRenderingHints(myRenderingHints);
    	}catch(NegativeArraySizeException e) {
    		
    	}
    }
    
    private int _myLastWidth = 0;
    private int _myLastHeight = 0;
    
    private void point(Point2D thePoint){
    	g2d.fillOval(
			(int) thePoint.getX() - SwingConstants.CURVE_POINT_SIZE / 2,
			(int) thePoint.getY() - SwingConstants.CURVE_POINT_SIZE / 2, 
			SwingConstants.CURVE_POINT_SIZE, SwingConstants.CURVE_POINT_SIZE
		);
    }
    
    private void line(Point2D thePoint1, Point2D thePoint2){
    	g2d.drawLine(
    		(int) thePoint1.getX(), (int) thePoint1.getY(), 
    		(int) thePoint2.getX(), (int) thePoint2.getY()
    	);
    }
    
    private void renderTimedEvent() {
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
				((EventTrackController)_myController).renderTimedEvent(myPoint, myLowerCorner, myUpperCorner, myLowerBound, myUpperBound, g2d);
				g2d.setClip(myClip);
			}
			
			myCurrentPoint = myCurrentPoint.getNext();
		}
    }
    
    private void renderAutomization() {
    	drawCurve(g2d);

		// paint curve points
		BasicStroke myThinStroke = new BasicStroke(0.5f);
		g2d.setStroke(myThinStroke);
		g2d.setColor(_myDotColor);
		ControlPoint myCurrentPoint = _myController.trackData().getFirstPointAt(_myTrackContext.lowerBound());

		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound()) {
				break;
			}
			Point2D myUserPoint = _myController.curveToViewSpace(myCurrentPoint);
			point(myUserPoint);
			//g2d.drawString(myCurrentPoint.value() +"", (int)myUserPoint.getX(), (int)myUserPoint.getY());

			if (myCurrentPoint.getType() == ControlPointType.BEZIER) {
				BezierControlPoint myBezierPoint = (BezierControlPoint) myCurrentPoint;
				
				Point2D myUserHandle = _myController.curveToViewSpace(myBezierPoint.inHandle());
				line(myUserPoint, myUserHandle);
				point(myUserHandle);

				myUserHandle = _myController.curveToViewSpace(myBezierPoint.outHandle());
				line(myUserPoint, myUserHandle);
				point(myUserHandle);
			}
			myCurrentPoint = myCurrentPoint.getNext();
		}
    }
    
    private void drawGradient(ControlPoint myFirstPoint, ControlPoint mySecondPoint) {
    	if (myFirstPoint.equals(mySecondPoint)) {
    		return;
        }

        if (mySecondPoint == null) {
            mySecondPoint = new ControlPoint(_myTrackContext.upperBound(), myFirstPoint.value());
        }
        
        Point2D p1 = _myController.curveToViewSpace(myFirstPoint);
        Point2D p2 = _myController.curveToViewSpace(mySecondPoint);
        
        ColorTrackController myColorTrackController = (ColorTrackController)_myController;
        
        for(double x = p1.getX(); x <= p2.getX();x++){
        	double myTime = _myController.viewXToTime((int)x, true);
        	CCColor myColor = myColorTrackController.color(myTime);
        	g2d.setColor(new Color((float)myColor.r, (float)myColor.g, (float)myColor.b, (float)myColor.a));
        	g2d.drawLine((int)x, 0, (int)x, getHeight());
        }
        
//       
//        
//      
//
////        if(mySecondPoint.getType() == ControlPointType.CUBIC && mySecondPoint.hasNext()){
////        	ControlPoint myNextPoint = mySecondPoint.getNext();
////        	Point2D myp2 = _myController.curveToViewSpace(myNextPoint);
////        	thePath.quadTo(myX, myY, myp2.getX(), myp2.getY());
////        	return;
////        }
//        
////        if(theDrawInterval){
//        double myInterval = SwingTrackView.GRID_INTERVAL / getWidth() * (_myTrackContext.viewTime());
//        double myStart = myInterval * Math.floor(myFirstPoint.time() / myInterval);
//	
//        for (double step = myStart + myInterval; step < mySecondPoint.time(); step = step + myInterval) {
//        	double myValue = _myController.trackData().value(step);
//        	p1 = _myController.curveToViewSpace(new ControlPoint(step, myValue));
//        	thePath.lineTo(p1.getX(), p1.getY());
//        }
////        }
//        
//        thePath.lineTo(p2.getX(), p2.getY());     
    }
    
    private void renderColor() {
    	if (_myController.trackData().size() == 0) {
    		return;
    	}
         
    	ControlPoint myMinPoint = _myController.trackData().floor(new ControlPoint(_myTrackContext.lowerBound(), 0));
 		if(myMinPoint == null){
 			myMinPoint = new ControlPoint(
 				_myTrackContext.lowerBound(), 
 				_myController.trackData().value(_myTrackContext.lowerBound())
 			);
 		}
         
 		ControlPoint myMaxPoint = _myController.trackData().ceiling(new ControlPoint(_myTrackContext.upperBound(), 0));
 		
 		if(myMaxPoint == null){
 			myMaxPoint = new ControlPoint(
 				_myTrackContext.upperBound(), 
 				_myController.trackData().value(_myTrackContext.upperBound())
 			);
 		}
 		myMaxPoint = _myController.trackData().getLastOnSamePosition(myMaxPoint);
         
 		ColorPoint myCurrentPoint = (ColorPoint)_myController.trackData().ceiling(new ControlPoint(_myTrackContext.lowerBound(), 0));
 		ControlPoint myLastPoint = myMinPoint;
 		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
 			drawGradient(myLastPoint, myCurrentPoint);
         	myLastPoint = myCurrentPoint;
         	myCurrentPoint = (ColorPoint)myCurrentPoint.getNext();
 		}
 		drawGradient(myLastPoint, myMaxPoint);

 	// paint curve points
 		BasicStroke myThinStroke = new BasicStroke(1.0f);
 		BasicStroke myThickStroke = new BasicStroke(3.0f);
 		myCurrentPoint = (ColorPoint)_myController.trackData().getFirstPointAt(_myTrackContext.lowerBound());

 		while (myCurrentPoint != null) {
 			if (myCurrentPoint.time() > _myTrackContext.upperBound()) {
 				break;
 			}

        	CCColor myColor = myCurrentPoint.color().invert();
        	Color myAWTColor = new Color((float)myColor.r, (float)myColor.g, (float)myColor.b);
 			int myX = _myController.timeToViewX(myCurrentPoint.time());
 	 		g2d.setColor(myAWTColor);
 	 		if(myCurrentPoint.isSelected())
 	 			g2d.setStroke(myThickStroke);
 	 		else
 	 			g2d.setStroke(myThinStroke);
 			g2d.drawLine(myX, 0, myX, getHeight());
 			
 			myX = _myController.timeToViewX(myCurrentPoint.endTime());
 			g2d.drawLine(myX, getHeight()/ 2 - 5, myX, getHeight()/ 2 + 5);
 			g2d.drawLine(myX - 5, getHeight() / 2, myX + 5, getHeight() / 2);
 	 		
 				//g2d.drawString(myCurrentPoint.value() +"", (int)myUserPoint.getX(), (int)myUserPoint.getY());
 			
// 			if (myCurrentPoint.getType() == ControlPointType.BEZIER) {
// 					BezierControlPoint myBezierPoint = (BezierControlPoint) myCurrentPoint;
// 					
// 					Point2D myUserHandle = _myController.curveToViewSpace(myBezierPoint.inHandle());
// 					line(myUserPoint, myUserHandle);
// 					point(myUserHandle);
//
// 					myUserHandle = _myController.curveToViewSpace(myBezierPoint.outHandle());
// 					line(myUserPoint, myUserHandle);
// 					point(myUserHandle);
// 				}
 			myCurrentPoint = (ColorPoint)myCurrentPoint.getNext();
 		}
    }
    
    public Graphics2D g2d() {
    	return g2d;
    }

    // does a full rendering of the function. we only need to do that if we're visible and we edit points or
    // zoom in and out...
	public void renderImplementation() {
		if (getWidth() <= 0 || getHeight() <= 0)
			return;
		
		if(!_myController.isParentOpen())return;

		if(getWidth() != _myLastWidth || getHeight() != _myLastHeight || g2d == null){
			createContext();
			_myLastWidth = getWidth();
			_myLastHeight = getHeight();
		}
		
		if(g2d == null)return;

		// paint background
		g2d.setComposite(AlphaComposite.Clear); 
		g2d.setColor(new Color(255, 255, 255, 0));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setComposite(AlphaComposite.SrcOver);
		
		// paint curve
		BasicStroke myThickStroke = new BasicStroke(1.5f);
		g2d.setStroke(myThickStroke);
		if (_myController instanceof CurveTrackController) {
			renderAutomization();
		}else if (_myController instanceof ColorTrackController) {
			renderColor();
		}else {
			renderTimedEvent();
		}

		updateUI();
	}
	
	private void drawTimelineBack(Graphics g){
		if(_myTimelineController == null){
			return;
		}
		Graphics2D myG2 = (Graphics2D)g;
		TransportController myTransportController = _myTimelineController.transportController();
		RulerInterval ri = myTransportController.rulerInterval();
		
		double myStart = ri.interval() * (Math.floor(myTransportController.lowerBound() / ri.interval()) ) ;
		
		for (double step = myStart; step <= myTransportController.upperBound(); step = step + ri.interval()) {
			
	        int myX = myTransportController.timeToViewX(step) ;
	        if(myX < 0)continue;
			
			g.setColor(SwingRulerView.STEP_COLOR);
			myG2.setStroke(SwingRulerView.THIN_STROKE);
			g.drawLine(myX, 0, myX, getHeight());
			
			g.setColor(SwingRulerView.SUB_STEP_COLOR);
			myG2.setStroke(SwingRulerView.THIN_STROKE);
			
			for(int i = 1; i < _myTimelineController.drawRaster();i++) {
				myX = myTransportController.timeToViewX(step + ri.interval() * i / _myTimelineController.drawRaster()) ;
				g.drawLine(myX, 0, myX, getHeight());
			}
			
		}

		 ControlPoint myCurrentPoint = _myTimelineController.transportController().trackData().getFirstPointAt(_myTrackContext.lowerBound());
		
		 while (myCurrentPoint != null) {
			 if (myCurrentPoint.time() > _myTrackContext.upperBound())break;
		 
			 int myMarkerX = _myController.timeToViewX(myCurrentPoint.time()) ;
		
			 g.setColor(new Color(1f, 0f,0f));
			 g.drawLine(myMarkerX, 0, myMarkerX, getHeight());
		
			 myCurrentPoint = myCurrentPoint.getNext();
		 }
	}
	
	private void drawTimelineInfos(Graphics g) {

		if (_myTimelineController == null)
			return;
		
		// draw loop if existent
		if (_myTimelineController != null && _myTimelineController.transportController().doLoop()) {
			Point2D myLowerCorner = _myController.curveToViewSpace(new ControlPoint(_myTimelineController.transportController().loopStart(), 1));
			Point2D myUpperCorner = _myController.curveToViewSpace(new ControlPoint(_myTimelineController.transportController().loopEnd(),0));
			
			g.setColor(new Color(0.15f, 0.15f, 0.15f, 0.05f));
			g.fillRect(
				(int) myLowerCorner.getX() , (int) myLowerCorner.getY(),
				(int) myUpperCorner.getX() - (int) myLowerCorner.getX(), (int) myUpperCorner.getY()
			);
			g.setColor(new Color(0.8f, 0.8f, 0.8f));
			g.drawLine((int) myLowerCorner.getX() , getHeight() + 1, (int) myLowerCorner.getX() , 0);
			g.drawLine((int) myUpperCorner.getX() , getHeight() + 1, (int) myUpperCorner.getX() , 0);
		}

		double myTime = _myTimelineController.transportController().time();
		int myViewX = _myController.timeToViewX(myTime) ;

		if (myViewX >= 0 && myViewX <= getWidth()) {
			g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
			g.drawLine(myViewX, 0, myViewX, getHeight());
			if (_myController instanceof CurveTrackController) {
				ControlPoint myDraggedPoint = _myController.draggedPoint();
				
				if(_myIsMousePressed && myDraggedPoint != null){
					double myValue = myDraggedPoint.value();
					String myValueString = CCFormatUtil.nd(myValue, 4);
					Point2D myPoint = _myController.curveToViewSpace(myDraggedPoint);
					g.drawString(
						myValueString, 
						(int)myPoint.getX() + 10, 
						(int) _myController.curveToViewSpace(new ControlPoint(myTime, myValue * (1 - 12f/getHeight()))).getY()
					);
				}else{
					double myValue = _myController.value(myTime);
					g.drawString(
						_myController.property().valueString(), 
						myViewX + 10, 
						(int) _myController.curveToViewSpace(new ControlPoint(myTime, myValue * (1 - 12f/getHeight()))).getY()
					);
				}
			}
		}
		
		if(_myIsMousePressed){

			ControlPoint myDraggedPoint = _myController.draggedPoint();
			if(myDraggedPoint != null){
				Point2D myPoint = _myController.curveToViewSpace(myDraggedPoint);
				g.drawLine(0, (int)myPoint.getY(), getWidth(), (int)myPoint.getY());
				g.drawLine((int)myPoint.getX(), 0, (int)myPoint.getX(), getHeight());
			}
		}
	}

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        drawTimelineBack(g);
        
        if (_myRedraw) {
            renderImplementation();
            _myRedraw = false;
        }

        g.drawImage(_myRenderBuffer, 0, 0, null);

        drawTimelineInfos(g);

        // paint selection
        if (_myController.selection() != null) {
    		
    		Point2D myLowerCorner = _myController.curveToViewSpace(new ControlPoint(_myController.selection().start(), 1));
    		Point2D myUpperCorner = _myController.curveToViewSpace(new ControlPoint(_myController.selection().end(), 0));
    	
    		g.setColor(SwingConstants.SELECTION_COLOR);
    		g.fillRect(
    			(int)myLowerCorner.getX(), 
    			(int)myLowerCorner.getY(), 
    			(int)myUpperCorner.getX()-(int)myLowerCorner.getX(), 
    			(int)myUpperCorner.getY()
    		);
    		
    		g.setColor(SwingConstants.SELECTION_BORDER_COLOR);
    		g.drawLine((int)myLowerCorner.getX(), getHeight(), (int)myLowerCorner.getX(), 0);
    		g.drawLine((int)myUpperCorner.getX(), (int)myUpperCorner.getY(), (int)myUpperCorner.getX(), 0);
        }
    }

    public void update() {
        updateUI();
    }

    public int height() {
        return getHeight();
    }
    
    public int width() {
    	return getWidth();
    }

    @Override
    public void finalize() {
    }
}
