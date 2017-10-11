package cc.creativecomputing.controlui.timeline.view.transport;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.text.DecimalFormat;

import javax.swing.JFrame;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.MarkerPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.CCNumberBox.CCChangeValueBoxListener;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController.RulerInterval;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.view.SwingGuiConstants;
import cc.creativecomputing.controlui.timeline.view.track.SwingAbstractTrackView;
import cc.creativecomputing.core.logging.CCLog;

@SuppressWarnings("serial")
public class SwingRulerView extends SwingAbstractTrackView implements CCChangeValueBoxListener{
	
	public static final int MAX_RULER_LABELS = 10;
	public static final double MIN_RULER_INTERVAL = 0.25;
	
	private TimelineController _myTimelineController;
	private CCTransportController _myTransportController;
	
	private SwingRulerPopUp _myRulerPopUp;
	
	public SwingRulerView(JFrame theMainFrame, TimelineController theTimelineController) {
		super(theMainFrame);
		if(theTimelineController != null){
			_myTimelineController = theTimelineController;
			_myTransportController = theTimelineController.transportController();
		    _myRulerPopUp = new SwingRulerPopUp(this, theTimelineController);
		}
	    
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(_myTransportController == null)return;
				
				_myTransportController.mouseReleased(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(_myTransportController == null)return;
				
				boolean myIsRightClick = e.getButton() == MouseEvent.BUTTON3 || (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1);
				
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
						_myRulerPopUp.show(SwingRulerView.this, e);
					}catch(Exception ex) {
						ex.printStackTrace();
					}
    				} else if (e.getButton() == MouseEvent.BUTTON1) {
    					_myTransportController.mousePressed(e);
    				}
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				_myTransportController.mousePressed(e);
			}
		});
		
		addMouseMotionListener(new MouseAdapter() {
			
			@Override
			public void mouseDragged(MouseEvent e) {
				
				if (_myTransportController == null)return;
				
				_myTransportController.mouseDragged(e);
				updateUI();
			}
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if (_myTransportController == null)return;
				
				_myTransportController.mouseMoved(e);
			}
		});
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	
	public void timelineController(TimelineController theTimelineController){
		_myTimelineController = theTimelineController;
		_myTransportController = theTimelineController.transportController();
	    _myRulerPopUp = new SwingRulerPopUp(this, theTimelineController);
	}
	
	public void changeValue(double theValue) {
		if (_myTransportController == null)return;
		_myTransportController.speed(theValue);	
	}
	
	public void render() {
		updateUI();
	}
	
	public Dimension getMinimumSize() {
		return new Dimension(0, 20 * SwingGuiConstants.SCALE);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(0, 20 * SwingGuiConstants.SCALE);
	}
	
	public Dimension getMaximumSize() {
		return new Dimension(5000, 20 * SwingGuiConstants.SCALE);
	}
	
	public void setViewWidth(int theViewWidth) {
		updateUI();
	}
	
	public static final Stroke THIN_STROKE = new BasicStroke(1);
	public static final Stroke THICK_STROKE = new BasicStroke(2);
	
	public static final Color TEXT_COLOR = new Color(100);
	public static final Color STEP_COLOR = new Color(0.8f, 0.8f, 0.8f);
	public static final Color SUB_STEP_COLOR = new Color(0.9f, 0.9f, 0.9f);
	
	@Override
	public void paintComponent(Graphics g) {
		g.translate(0, 0);
		Graphics2D myG2 = (Graphics2D)g;
		g.setColor(new Color(255,255,255));
		g.fillRect(0, 0, getWidth(), getHeight() );
		
		if(_myTimelineController == null)return;
		if(_myTransportController == null)return;
		
		RulerInterval ri = _myTransportController.rulerInterval();

		DecimalFormat myFormat = new DecimalFormat();
        myFormat.applyPattern("#0.##");
		
		double myStart = ri.interval() * (Math.floor(_myTransportController.lowerBound() / ri.interval()) ) ;
		
		for (double step = myStart; step <= _myTransportController.upperBound(); step = step + ri.interval()) {
			
	        int myX = _myTransportController.timeToViewX(step);
	        if(myX < 0)continue;

			myG2.setStroke(THICK_STROKE);
			g.setColor(SUB_STEP_COLOR);
			g.drawLine(0, getHeight() / 2, getWidth(), getHeight() / 2);
			g.setColor(STEP_COLOR);
			g.drawLine(myX, 0, myX, getHeight());
			
			int myTimeX = myX;
			
			g.setColor(SUB_STEP_COLOR);
			myG2.setStroke(THIN_STROKE);
			
			for(int i = 1; i < _myTimelineController.drawRaster();i++) {
				myX = _myTransportController.timeToViewX(step + ri.interval() * i / _myTimelineController.drawRaster());
				g.drawLine(myX, getHeight(), myX, getHeight() / 4 * 3);
			}
			
			
			String myTimeString = _myTransportController.timeToString(step);
			
			g.setFont(SwingGuiConstants.ARIAL_BOLD_10);
	        g.setColor(TEXT_COLOR);
			g.drawString(myTimeString, myTimeX + 5, 11 * SwingGuiConstants.SCALE);
		}
		ControlPoint myCurrentPoint = _myTransportController.trackData().ceiling(new ControlPoint(_myTransportController.lowerBound(),0));
//		if(myCurrentPoint == null) {
//			myCurrentPoint = _myTransportController.trackData().ceiling(new ControlPoint(_myTransportController.lowerBound(),0));
//		}
        
        while (myCurrentPoint != null) {
            if (myCurrentPoint.time() > _myTransportController.upperBound()) {
                break;
            }

            if(myCurrentPoint instanceof MarkerPoint) {
	            Point2D myUserPoint = _myTransportController.curveToViewSpace(myCurrentPoint);
	            MarkerPoint myMarker = (MarkerPoint)myCurrentPoint;
	            int myMarkerX = (int)myUserPoint.getX();
	            
	            g.setColor(new Color(1f, 0f,0f));
				g.drawLine(myMarkerX, getHeight()/2, myMarkerX, getHeight());
				g.drawString(myMarker.name(), myMarkerX + 5, getHeight()/2 + 15);
				
				Polygon myPolygon = new Polygon();
				myPolygon.addPoint(myMarkerX, getHeight()/2 + 5);
				myPolygon.addPoint(myMarkerX - 5, getHeight()/2);
				myPolygon.addPoint(myMarkerX + 5, getHeight()/2);
				g.fillPolygon(myPolygon);
            }
            
            if(myCurrentPoint.getType() == ControlPointType.TIMED_EVENT) {
            	BasicStroke myThinStroke = new BasicStroke(0.5f);
            	myG2.setStroke(myThinStroke);
            	myG2.setColor(new Color(100,100,255,50));
            	
            	TimedEventPoint myPoint = (TimedEventPoint) myCurrentPoint;
        		Point2D myLowerCorner = _myTransportController.curveToViewSpace(new ControlPoint(myCurrentPoint.time(), 1));
        		Point2D myUpperCorner = _myTransportController.curveToViewSpace(new ControlPoint(myPoint.endPoint().time(),0));

        		myG2.fillRect(
        			(int) myLowerCorner.getX(), getHeight() * 3 / 4,
        			(int) myUpperCorner.getX() - (int) myLowerCorner.getX(), getHeight()/4
        		);
        		myG2.setColor(new Color(0,0,255));
        		myG2.drawLine((int) myLowerCorner.getX(), getHeight() * 3 / 4, (int) myLowerCorner.getX(), getHeight());
        		myG2.drawLine((int) myUpperCorner.getX(), getHeight() * 3 / 4, (int) myUpperCorner.getX(), getHeight());
            }
            
			
            myCurrentPoint = myCurrentPoint.getNext();
        }
		
		int myTransportX = Math.max(0, _myTransportController.timeToViewX(_myTransportController.time()));
		Polygon myPolygon = new Polygon();
		myPolygon.addPoint(myTransportX - 5, getHeight()/2);
		myPolygon.addPoint(myTransportX - 5, getHeight() - 5);
		myPolygon.addPoint(myTransportX, getHeight());
		myPolygon.addPoint(myTransportX + 5, getHeight() - 5);
		myPolygon.addPoint(myTransportX + 5, getHeight()/2);
		g.setColor(new Color(0.6f, 0.6f, 0.6f));
		g.fillPolygon(myPolygon);
		
		g.setColor(new Color(0.8f, 0.8f, 0.8f));
		g.drawLine(myTransportX, getHeight()/2, myTransportX, getHeight());
		
		int myLoopStartX = Math.max(0,_myTransportController.timeToViewX(_myTransportController.loopStart()));
		int myLoopEndX = Math.max(0,_myTransportController.timeToViewX(_myTransportController.loopEnd()));
		
		if(myLoopStartX == myLoopEndX)return;

		if(_myTransportController.doLoop()) {
			g.setColor(new Color(1f, 0.0f,0.0f,0.5f));
			g.drawRect(myLoopStartX, 0, myLoopEndX - myLoopStartX, getHeight()/2);
			g.setColor(new Color(1f, 0.0f,0.0f,0.25f));
			g.fillRect(myLoopStartX, 0, myLoopEndX - myLoopStartX, getHeight()/2);
		}else {
			g.setColor(new Color(0.7f, 0.7f,0.7f,0.5f));
			g.drawRect(myLoopStartX, 0, myLoopEndX - myLoopStartX, getHeight()/2);
			g.setColor(new Color(0.7f, 0.7f,0.7f,0.25f));
			g.fillRect(myLoopStartX, 0, myLoopEndX - myLoopStartX, getHeight()/2);
		}
	}

	public int height() {
		return getHeight();
	}
	
	public int width() {
		return getWidth();
	}

	@Override
	public void mute(boolean theMute) {}
	
	@Override
	public void min(double theMin) {}
	
	@Override
	public void max(double theMax) {}

	@Override
	public void color(Color theColor) {}

}
