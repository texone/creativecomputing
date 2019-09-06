package cc.creativecomputing.controlui.timeline.view.track;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Transparency;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.GeneralPath;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;

import javax.swing.JPanel;

import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController;
import cc.creativecomputing.controlui.timeline.controller.CCTransportController.RulerInterval;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.TrackContext;
import cc.creativecomputing.controlui.timeline.controller.track.CCTrackController;
import cc.creativecomputing.controlui.timeline.view.SwingConstants;
import cc.creativecomputing.controlui.timeline.view.transport.SwingRulerView;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCMath;

@SuppressWarnings("serial")
public abstract class SwingAbstractTrackDataView<ControllerType extends CCTrackController> extends JPanel {

	public interface SwingTrackDataViewListener {
		void onRender(Graphics2D theG2D);
	}

	protected BufferedImage _myRenderBuffer;

	protected Color _myLineColor = SwingConstants.LINE_COLOR;
	protected Color _myFillColor = SwingConstants.FILL_COLOR;
	protected Color _myDotColor = SwingConstants.DOT_COLOR;

	private boolean _myRedraw = true;

	protected ControllerType _myController;
	protected TimelineController _myTimelineController;
	protected TrackContext _myTrackContext;

	private CCListenerManager<SwingTrackDataViewListener> _myEvents = CCListenerManager.create(SwingTrackDataViewListener.class);

	public CCListenerManager<SwingTrackDataViewListener> events() {
		return _myEvents;
	}

	protected boolean _myIsMousePressed = false;

	protected MouseEvent _myMouseEvent = null;

	private SwingTrackPopup<ControllerType> _myToolChooserPopup;

	public SwingAbstractTrackDataView(TimelineController theTimelineController, ControllerType theTrackController) {
		_myTimelineController = theTimelineController;
		_myTrackContext = theTrackController.context();
		_myController = theTrackController;

		setName("timeline" + (int) Math.floor(100 * Math.random()));
		// setBorder(BorderFactory.createLineBorder(Color.gray));

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent theE) {
			}

			@Override
			public void mousePressed(MouseEvent e) {
				_myMouseEvent = e;
				boolean myIsRightClick = e.getButton() == MouseEvent.BUTTON3 || (e.isControlDown() && e.getButton() == MouseEvent.BUTTON1);

				if (myIsRightClick) {
					showPopUp(e);
				} else if (e.getButton() == MouseEvent.BUTTON1) {
					_myIsMousePressed = true;
					_myController.mousePressed(e);
				}
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				_myMouseEvent = e;
				if (e.getButton() == MouseEvent.BUTTON1) {
					_myIsMousePressed = false;
					repaint();
					_myController.mouseReleased(e);
				}
				requestFocusInWindow();
			}

			@Override
			public void mouseExited(MouseEvent e) {
				_myMouseEvent = null;
				renderInfo();
			}
		});

		addMouseMotionListener(new MouseAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				_myMouseEvent = e;
				if (e.isAltDown() && !_myIsEnvelope) {
					_myTrackContext.zoomController().performDrag(new Point2D.Double(e.getX(), e.getY()), width());
					return;
				}
				_myController.mouseDragged(e);
			}

			@Override
			public void mouseMoved(MouseEvent e) {
				_myMouseEvent = e;
				_myController.mouseMoved(e);
				renderInfo();
			}
		});

		addKeyListener(new KeyAdapter() {
			
			@Override
			public void keyPressed(KeyEvent e) {
				_myController.keyPressed(e);
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
				_myController.keyReleased(e);
			}
		});
		
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				//_myController.context().zoomController().updateZoomables();
				render();
			}
			
			
			
			@Override
			public void componentShown(ComponentEvent e) {
				render();
			}
		});
		
	}

	public void showPopUp(MouseEvent theEvent) {
		_myToolChooserPopup.show(SwingAbstractTrackDataView.this, theEvent);
	}

	public CCTrackController controller() {
		return _myController;
	}

	public TrackContext context() {
		return _myTrackContext;
	}

	public Color fillColor() {
		return _myFillColor;
	}

	public Color lineColor() {
		return _myLineColor;
	}

	// public Dimension getPreferredSize() {
	// return new Dimension(Integer.MAX_VALUE, 150);
	// }
	//
	// public Dimension getMaximumSize() {
	// return new Dimension(Integer.MAX_VALUE, 500);
	// }

	private Color brighter(Color theColor, float theScale, int theAlpha) {
		float myRed = (255 - theColor.getRed()) * theScale;
		float myGreen = (255 - theColor.getGreen()) * theScale;
		float myBlue = (255 - theColor.getBlue()) * theScale;

		return new Color(255 - (int) myRed, 255 - (int) myGreen, 255 - (int) myBlue, theAlpha);
	}

	public void color(Color theColor) {
		_myDotColor = theColor;
		_myLineColor = brighter(theColor, 0.5f, 125);
		_myFillColor = brighter(theColor, 0.25f, 125);
		render();
	}

	public void drawCurvePiece(ControlPoint myFirstPoint, ControlPoint mySecondPoint, GeneralPath thePath, boolean theToFirst) {
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

		if (theToFirst) {
			thePath.lineTo(myA1X, myA1Y);
		}

		if (mySecondPoint.getType() == ControlPointType.STEP) {
			thePath.lineTo(myA2X, myA1Y);
			thePath.lineTo(myA2X, myA2Y);
			return;
		}

		if (mySecondPoint.getType() == ControlPointType.BEZIER) {
			myIsBezier = true;
			BezierControlPoint myBezier2Point = (BezierControlPoint) mySecondPoint;
			Point2D myHandle = _myController.curveToViewSpace(myBezier2Point.inHandle());
			myA2X = myHandle.getX();
			myA2Y = myHandle.getY();

		}
		if (myFirstPoint.getType() == ControlPointType.BEZIER) {
			myIsBezier = true;
			BezierControlPoint myBezier1Point = (BezierControlPoint) myFirstPoint;
			Point2D myHandle = _myController.curveToViewSpace(myBezier1Point.outHandle());
			myA1X = myHandle.getX();
			myA1Y = myHandle.getY();
		}
		if (myIsBezier) {
			thePath.curveTo(myA1X, myA1Y, myA2X, myA2Y, myX, myY);
			return;
		}

		if (mySecondPoint.getType() == ControlPointType.LINEAR) {
			thePath.lineTo(myX, myY);
			return;
		}

		// if(mySecondPoint.getType() == ControlPointType.CUBIC &&
		// mySecondPoint.hasNext()){
		// ControlPoint myNextPoint = mySecondPoint.getNext();
		// Point2D myp2 = _myController.curveToViewSpace(myNextPoint);
		// thePath.quadTo(myX, myY, myp2.getX(), myp2.getY());
		// return;
		// }

		// if(theDrawInterval){
		double myInterval = SwingTrackView.GRID_INTERVAL / getWidth() * (_myTrackContext.viewTime());
		double myStart = myInterval * Math.floor(myFirstPoint.time() / myInterval);

		for (double step = myStart + myInterval; step < mySecondPoint.time(); step = step + myInterval) {
			double myValue = _myController.trackData().value(step);
			p1 = _myController.curveToViewSpace(new ControlPoint(step, myValue));
			thePath.lineTo(p1.getX(), p1.getY());
		}
		// }

		thePath.lineTo(p2.getX(), p2.getY());
	}

	// private void drawGridLines(Graphics g) {
	// double myNumberOfLines = (_myController.viewTime()) /
	// TrackView.GRID_INTERVAL;
	// int myIntervalFactor = 1;
	// if (myNumberOfLines > MAX_GRID_LINES) {
	// myIntervalFactor = (int) (myNumberOfLines / MAX_GRID_LINES + 1);
	// }
	// double myStart = TrackView.GRID_INTERVAL *
	// (Math.floor(_myController.lowerBound() / TrackView.GRID_INTERVAL));
	// for (double step = myStart; step <= _myController.upperBound(); step =
	// step + myIntervalFactor * TrackView.GRID_INTERVAL) {
	// double myX = (step - _myController.lowerBound()) /
	// (_myController.viewTime()) * getWidth();
	// g.setColor(new Color(0.9f, 0.9f, 0.9f));
	// g.drawLine((int) myX, 0, (int) myX, this.getHeight());
	// }
	// }

	public void render() {
		try {
			// checks if the current time-line is visible in the parent
			// containers,
			// so only visible time-line panels are rendered i.e. when zooming
			Rectangle myVisibleRect = new Rectangle();
			computeVisibleRect(myVisibleRect);
			if (!myVisibleRect.isEmpty()) {
				renderImplementation();
				_myRedraw = false;
			} else {
				_myRedraw = true;
			}

			events().proxy().onRender(g2d);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
		update();
	}

	public void renderInfo() {
		// _myRedraw = false;
		update();
	}

	private boolean _myIsEnvelope = false;

	public void isEnvelope(boolean theDrawGrid) {
		_myIsEnvelope = theDrawGrid;
	}

	public void point(Point2D thePoint) {
		g2d.fillOval(
			(int) thePoint.getX() - SwingConstants.CURVE_POINT_SIZE / 2,
			(int) thePoint.getY() - SwingConstants.CURVE_POINT_SIZE / 2, 
			SwingConstants.CURVE_POINT_SIZE,
			SwingConstants.CURVE_POINT_SIZE
		);
	}

	public void line(Point2D thePoint1, Point2D thePoint2) {
		g2d.drawLine((int) thePoint1.getX(), (int) thePoint1.getY(), (int) thePoint2.getX(), (int) thePoint2.getY());
	}

	public Graphics2D g2d() {
		return g2d;
	}

	public abstract void renderData(Graphics2D g2d);

	protected Graphics2D g2d = null;

	private void createContext() {
		try {
			// obtain the current system graphical settings
		    GraphicsConfiguration gfx_config = GraphicsEnvironment.
		        getLocalGraphicsEnvironment().getDefaultScreenDevice().
		        getDefaultConfiguration();
		    
		    _myRenderBuffer = gfx_config.createCompatibleImage(getWidth(), getHeight(),Transparency.TRANSLUCENT);

//			_myRenderBuffer = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics g = _myRenderBuffer.getGraphics();

			RenderingHints myRenderingHints = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			myRenderingHints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

			g2d = (Graphics2D) g;
			g2d.setRenderingHints(myRenderingHints);
		} catch (NegativeArraySizeException e) {

		}
	}

	private int _myLastWidth = 0;
	private int _myLastHeight = 0;

	// does a full rendering of the function. we only need to do that if we're
	// visible and we edit points or
	// zoom in and out...
	private void renderImplementation() {

		if (getWidth() <= 0 || getHeight() <= 0)
			return;

		if (!_myController.isParentOpen())
			return;

		if (getWidth() != _myLastWidth || getHeight() != _myLastHeight || g2d == null) {
			createContext();
			_myLastWidth = getWidth();
			_myLastHeight = getHeight();
		}

		if (g2d == null)
			return;
		
		// paint background
		g2d.setComposite(AlphaComposite.Clear);
		g2d.setColor(new Color(255, 255, 255, 0));
		g2d.fillRect(0, 0, getWidth(), getHeight());
		g2d.setComposite(AlphaComposite.SrcOver);

		// paint curve
		BasicStroke myThickStroke = new BasicStroke(1.5f);
		g2d.setStroke(myThickStroke);

		renderData(g2d);
	}

	private void drawTransportInfos(Graphics g) {
		// draw loop if existent
		if (_myTimelineController == null)
			return;

		if (_myTimelineController.transportController().doLoop()) {
			Point2D myLowerCorner = _myController.curveToViewSpace(new ControlPoint(_myTimelineController.transportController().loopStart(), 1));
			Point2D myUpperCorner = _myController.curveToViewSpace(new ControlPoint(_myTimelineController.transportController().loopEnd(), 0));

			g.setColor(new Color(0.15f, 0.15f, 0.15f, 0.05f));
			g.fillRect((int) myLowerCorner.getX(), (int) myLowerCorner.getY(), (int) myUpperCorner.getX() - (int) myLowerCorner.getX(), (int) myUpperCorner.getY());
			g.setColor(new Color(0.8f, 0.8f, 0.8f));
			g.drawLine((int) myLowerCorner.getX(), getHeight() + 1, (int) myLowerCorner.getX(), 0);
			g.drawLine((int) myUpperCorner.getX(), getHeight() + 1, (int) myUpperCorner.getX(), 0);
		}

		double myTime = _myTimelineController.transportController().time();
		int myViewX = _myController.timeToViewX(myTime);

		if (myViewX >= 0 && myViewX <= getWidth()) {
			g.setColor(new Color(0.1f, 0.1f, 0.1f, 0.5f));
			g.drawLine(myViewX, 0, myViewX, getHeight());
		}
	}

	public abstract void drawTimelineInfos(Graphics g);

	private void drawTimelineBack(Graphics g) {
		if (_myTimelineController == null) {
			return;
		}
		Graphics2D myG2 = (Graphics2D) g;
		CCTransportController myTransportController = _myTimelineController.transportController();
		RulerInterval ri = myTransportController.rulerInterval();

		double myStart = ri.interval() * (Math.floor(myTransportController.lowerBound() / ri.interval()));

		for (double step = myStart; step <= myTransportController.upperBound(); step = step + ri.interval()) {

			int myX = myTransportController.timeToViewX(step);
			if (myX < 0)
				continue;

			g.setColor(SwingRulerView.STEP_COLOR);
			myG2.setStroke(SwingRulerView.THIN_STROKE);
			g.drawLine(myX, 0, myX, getHeight());

			g.setColor(SwingRulerView.SUB_STEP_COLOR);
			myG2.setStroke(SwingRulerView.THIN_STROKE);

			for (int i = 1; i < _myTimelineController.drawRaster(); i++) {
				myX = myTransportController.timeToViewX(step + ri.interval() * i / _myTimelineController.drawRaster());
				g.drawLine(myX, 0, myX, getHeight());
			}

		}

		ControlPoint myCurrentPoint = _myTimelineController.transportController().trackData().getFirstPointAt(_myTrackContext.lowerBound());

		while (myCurrentPoint != null) {
			if (myCurrentPoint.time() > _myTrackContext.upperBound())
				break;

			int myMarkerX = _myController.timeToViewX(myCurrentPoint.time());

			g.setColor(new Color(1f, 0f, 0f));
			g.drawLine(myMarkerX, 0, myMarkerX, getHeight());

			myCurrentPoint = myCurrentPoint.getNext();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		drawTimelineBack(g);

		if (_myIsEnvelope) {
			Graphics2D myG2 = (Graphics2D) g;

			for (int i = 0; i <= 8; i++) {
				int myX = (int) CCMath.map(i, 0, 8, 0, width());
				int myY = (int) CCMath.map(i, 0, 8, 0, height());

				if (i % 2 == 0)
					g.setColor(Color.gray);
				else
					g.setColor(SwingRulerView.STEP_COLOR);

				myG2.setStroke(SwingRulerView.THIN_STROKE);
				g.drawLine(myX, 0, myX, getHeight());
				g.drawLine(0, myY, getWidth(), myY);
			}
		}

		if (_myRedraw) {
			renderImplementation();
			_myRedraw = false;
		}

		g.drawImage(_myRenderBuffer, 0, 0, null);

		drawTransportInfos(g);
		if (_myTimelineController != null) {
			drawTimelineInfos(g);
		}

		// paint selection
		if (_myController.selection() != null) {

			Point2D myLowerCorner = _myController.curveToViewSpace(new ControlPoint(_myController.selection().start(), 1));
			Point2D myUpperCorner = _myController.curveToViewSpace(new ControlPoint(_myController.selection().end(), 0));

			g.setColor(SwingConstants.SELECTION_COLOR);
			g.fillRect((int) myLowerCorner.getX(), (int) myLowerCorner.getY(), (int) myUpperCorner.getX() - (int) myLowerCorner.getX(), (int) myUpperCorner.getY());

			g.setColor(SwingConstants.SELECTION_BORDER_COLOR);
			g.drawLine((int) myLowerCorner.getX(), getHeight(), (int) myLowerCorner.getX(), 0);
			g.drawLine((int) myUpperCorner.getX(), (int) myUpperCorner.getY(), (int) myUpperCorner.getX(), 0);
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
