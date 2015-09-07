package cc.creativecomputing.controlui.timeline.view;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.geom.Point2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.DecimalFormat;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.MarkerPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.controlui.timeline.controller.TimelineController;
import cc.creativecomputing.controlui.timeline.controller.TransportController;
import cc.creativecomputing.controlui.timeline.controller.TransportController.RulerInterval;
import cc.creativecomputing.controlui.timeline.view.SwingDraggableValueBox.ChangeValueListener;
import cc.creativecomputing.controlui.timeline.view.track.SwingAbstractTrackView;

@SuppressWarnings("serial")
public class SwingRulerView extends SwingAbstractTrackView implements ChangeValueListener{
	
	private class InsertTimeDialog extends JDialog implements ActionListener, PropertyChangeListener {
		private String typedText = null;
		private JTextField _myTextField;

		private JOptionPane optionPane;

		private String btnString1 = "insert";
		private String btnString2 = "Cancel";

		/** Creates the reusable dialog. */
		public InsertTimeDialog(String aWord) {
			super();

			setTitle("Insert Time");

			_myTextField = new JTextField(10);

			// Create an array of the text and components to be displayed.
			String msgString1 = "Specify the time to insert in seconds.";
			Object[] array = { msgString1, _myTextField };

			// Create an array specifying the number of dialog buttons
			// and their text.
			Object[] options = { btnString1, btnString2 };

			// Create the JOptionPane.
			optionPane = new JOptionPane(
				array, 
				JOptionPane.QUESTION_MESSAGE,
				JOptionPane.YES_NO_OPTION, 
				null, 
				options, 
				options[0]
			);

			// Make this dialog display it.
			setContentPane(optionPane);

			// Handle window closing correctly.
			setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
			addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent we) {
					/*
					 * Instead of directly closing the window, we're going to
					 * change the JOptionPane's value property.
					 */
					optionPane.setValue(new Integer(JOptionPane.CLOSED_OPTION));
				}
			});

			// Ensure the text field always gets the first focus.
			addComponentListener(new ComponentAdapter() {
				public void componentShown(ComponentEvent ce) {
					_myTextField.requestFocusInWindow();
				}
			});

			// Register an event handler that puts the text into the option
			// pane.
			_myTextField.addActionListener(this);

			// Register an event handler that reacts to option pane state
			// changes.
			optionPane.addPropertyChangeListener(this);
		}

		/** This method handles events for the text field. */
		public void actionPerformed(ActionEvent e) {
			optionPane.setValue(btnString1);
		}

		/** This method reacts to state changes in the option pane. */
		public void propertyChange(PropertyChangeEvent e) {
			String prop = e.getPropertyName();

			if(
				isVisible() && 
				(e.getSource() == optionPane) && 
				(JOptionPane.VALUE_PROPERTY.equals(prop) || JOptionPane.INPUT_VALUE_PROPERTY.equals(prop))
			) {
				Object value = optionPane.getValue();

				if (value == JOptionPane.UNINITIALIZED_VALUE) {
					// ignore reset
					return;
				}

				// Reset the JOptionPane's value.
				// If you don't do this, then if the user
				// presses the same button next time, no
				// property change event will be fired.
				optionPane.setValue(JOptionPane.UNINITIALIZED_VALUE);

				if (btnString1.equals(value)) {
					typedText = _myTextField.getText();
					double myTime = Double.parseDouble(typedText);
					_myTimelineController.insertTime(_myTransportController.time(),myTime);
					
					clearAndHide();
					
				} else { 
					typedText = null;
					clearAndHide();
				}
			}
		}

		/** This method clears the dialog and hides it. */
		public void clearAndHide() {
			_myTextField.setText(null);
			setVisible(false);
		}
	}
	
	
	
	public static final int MAX_RULER_LABELS = 10;
	public static final double MIN_RULER_INTERVAL = 0.25;
	
	private TimelineController _myTimelineController;
	private TransportController _myTransportController;
	
	private SwingRulerMarkerDialog _myMarkerFrame;
	private InsertTimeDialog _myInsertTimeFrame;
	
	public SwingRulerView(JFrame theMainFrame, TimelineController theTimelineController) {
		super(theMainFrame);
		if(theTimelineController != null){
			_myTimelineController = theTimelineController;
			_myTransportController = theTimelineController.transportController();
		}
		
		_myMarkerFrame = new SwingRulerMarkerDialog(this, "MARKER");
	    _myMarkerFrame.setSize( 300, 200 ); 
	    
	    _myInsertTimeFrame = new InsertTimeDialog("Insert Time");
	    _myInsertTimeFrame.setSize( 300, 200 ); 
		
		addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(_myTransportController == null)return;
				
				_myTransportController.mouseReleased(e);
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(_myTransportController == null)return;
				
				if (e.getButton() == MouseEvent.BUTTON3) {
					_myInsertTimeFrame.setLocation(e.getXOnScreen(), e.getYOnScreen());
					_myInsertTimeFrame.setVisible(true);
    			} else if (e.getButton() == MouseEvent.BUTTON1) {
					_myMarkerFrame.setLocation(e.getXOnScreen(), e.getYOnScreen());
    			}
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
		});
		
		setLayout(new FlowLayout(FlowLayout.LEFT));
	}
	
	public void timelineController(TimelineController theTimelineController){
		_myTimelineController = theTimelineController;
		_myTransportController = theTimelineController.transportController();
	}
	
	public void changeValue(double theValue) {
		if (_myTransportController == null)return;
		_myTransportController.speed(theValue);	
	}
	
	public void showMarkerDialog(MarkerPoint theMarker) {
		_myMarkerFrame.marker(theMarker);
		_myMarkerFrame.setVisible(true);
		
	}
	
	public void render() {
		updateUI();
	}
	
	public Dimension getMinimumSize() {
		return new Dimension(0, 20);
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(0, 20);
	}
	
	public Dimension getMaximumSize() {
		return new Dimension(5000, 20);
	}
	
	public void setViewWidth(int theViewWidth) {
		updateUI();
	}
	
	private Stroke _myThinStroke = new BasicStroke(1);
	private Stroke _myThickStroke = new BasicStroke(2);
	
	private Color _myTextColor = new Color(100);
	private Color _myStepColor = new Color(0.8f, 0.8f, 0.8f);
	private Color _mySubStepColor = new Color(0.9f, 0.9f, 0.9f);
	
	private String timeToString(double theTime) {
		long myTime = (long)(theTime * 1000);
		long myMillis = myTime % 1000;
		myTime /= 1000;
		long mySeconds = myTime % 60;
		myTime /= 60;
		long myMinutes = myTime % 60;
		myTime /= 60;
		long myHours = myTime;
		
		StringBuffer myResult = new StringBuffer();
		if(myHours != 0) {
			myResult.append(myHours);
			myResult.append("h ");
		}
		if(myMinutes != 0) {
			myResult.append(myMinutes);
			myResult.append("min ");
		}
		if(mySeconds != 0) {
			myResult.append(mySeconds);
			myResult.append("s ");
		}
		if(myMillis != 0) {
			myResult.append(myMillis);
			myResult.append("ms ");
		}
		return myResult.toString();
	}
	
	@Override
	public void paintComponent(Graphics g) {
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
			
			g.setColor(_myStepColor);
			myG2.setStroke(_myThickStroke);
			g.drawLine(myX, 0, myX, getHeight());
			
			int myTimeX = myX;
			
			g.setColor(_mySubStepColor);
			myG2.setStroke(_myThinStroke);
			
			for(int i = 1; i < _myTimelineController.drawRaster();i++) {
				myX = _myTransportController.timeToViewX(step + ri.interval() * i / _myTimelineController.drawRaster());
				g.drawLine(myX, 0, myX, getHeight() / 10);
			}
			
			String myTimeString = timeToString(step);
			
			g.setFont(SwingGuiConstants.ARIAL_BOLD_10);
	        g.setColor(_myTextColor);
			g.drawString(myTimeString, myTimeX + 5, 11);
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
	public void mute(boolean theMute) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void color(Color theColor) {
		// TODO Auto-generated method stub
		
	}

}
