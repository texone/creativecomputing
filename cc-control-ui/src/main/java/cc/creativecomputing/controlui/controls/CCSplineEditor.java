package cc.creativecomputing.controlui.controls;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.GeneralPath;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;
import cc.creativecomputing.math.spline.CCBezierSpline;
import cc.creativecomputing.math.spline.CCCatmulRomSpline;
import cc.creativecomputing.math.spline.CCLinearSpline;
import cc.creativecomputing.math.spline.CCSpline;

public class CCSplineEditor extends JFrame {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7258875972598552365L;


	private CCSpline _mySpline;
	
	
	private CCVector3 _mySelectedPoint = null;
	private CCVector3 _myHighlightedPoint = null;
	CurvePane _myPane;
	
	public CCSplineEditor(String theName) {
		super(theName);
		JPanel myPanel = new JPanel(new BorderLayout());
		_myPane = new CurvePane();
		myPanel.add(_myPane, "Center");

		_myPane.addMouseListener(new MouseListener() {
			
			@Override
			public void mouseReleased(MouseEvent e) {
				if(_mySpline == null)return;
				_mySelectedPoint = null;
			}
			
			@Override
			public void mousePressed(MouseEvent e) {
				if(_mySpline == null)return;
				
				_mySelectedPoint = selectPoint(e);
				_myPane.repaint();
			}
			
			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void mouseClicked(MouseEvent e) {
				if(_mySpline == null)return;
				
				if(e.getClickCount() == 2){
					CCVector3 myPoint = selectPoint(e);
					if(myPoint != null){
						if(_mySpline instanceof CCBezierSpline){
							int myIndex = _mySpline.points().indexOf(myPoint);
							switch(myIndex % 3){
							case 0:
								if(myIndex > 0){
									_mySpline.points().remove(myIndex - 1);
									_mySpline.points().remove(myIndex - 1);
									_mySpline.points().remove(myIndex - 1);
								}else{
									_mySpline.points().remove(0);
									_mySpline.points().remove(0);
									_mySpline.points().remove(0);
								}
								break;
							case 1:
								myPoint.set(_mySpline.points().get(myIndex - 1));
								break;
							case 2:
								myPoint.set(_mySpline.points().get(myIndex + 1));
								break;
							}
						}else{
							_mySpline.beginEditSpline();
							_mySpline.points().remove(myPoint);
							_mySpline.endEditSpline();
						}
						_myPane.repaint();
						return;
					}
					
					_mySpline.beginEditSpline();
					_mySpline.addPoint(new CCVector3(mouseToRelative(e)));
					_mySpline.endEditSpline();
					_myPane.repaint();
				}
			}
		});

		_myPane.addMouseMotionListener(new MouseMotionListener() {
			
			@Override
			public void mouseMoved(MouseEvent e) {
				if(_mySpline == null)return;
				
				_myHighlightedPoint = selectPoint(e);
				_myPane.repaint();
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				if(_mySpline == null)return;
				if(_mySelectedPoint == null)return;
				
				CCVector3 myNewPos = mouseToRelative(e);
				CCVector3 myMotion = myNewPos.subtract(_mySelectedPoint);
				_mySelectedPoint.set(myNewPos);
				
				if(_mySpline instanceof CCBezierSpline){
					int myIndex = _mySpline.points().indexOf(_mySelectedPoint);
					switch(myIndex % 3){
					case 0:
						if(myIndex > 0){
							_mySpline.points().get(myIndex - 1).addLocal(myMotion);
						}
						if(myIndex < _mySpline.points().size() - 1){
							_mySpline.points().get(myIndex + 1).addLocal(myMotion);
						}
						break;
					case 1:
						if(e.isShiftDown() && myIndex > 1){
							CCVector3 myAnchor = _mySpline.points().get(myIndex - 1);
							CCVector3 myControl2 = _mySpline.points().get(myIndex - 2);
							CCVector3 myDifference = _mySelectedPoint.subtract(myAnchor);
							myControl2.set(myAnchor.subtract(myDifference));
						}
						break;
					case 2:
						if(e.isShiftDown() && myIndex <  _mySpline.points().size() - 2){
							CCVector3 myAnchor = _mySpline.points().get(myIndex + 1);
							CCVector3 myControl2 = _mySpline.points().get(myIndex + 2);
							CCVector3 myDifference = _mySelectedPoint.subtract(myAnchor);
							myControl2.set(myAnchor.subtract(myDifference));
						}
						break;
					}
				}
				_myPane.repaint();
			}
		});

		getContentPane().add(myPanel);

		setSize(200, 200);
	}
	
	private CCVector3 mouseToRelative(MouseEvent e){
		return new CCVector3(
			CCMath.saturate(CCMath.norm(e.getX(), 10, _myPane.getWidth() - 10)),
			CCMath.saturate(CCMath.norm(e.getY(), 10, _myPane.getHeight() - 10))
		);
	}
	
	private CCVector3 relativeToMouse(CCVector3 theRelative){
		return new CCVector3(
			CCMath.blend(10, _myPane.getWidth() - 10, theRelative.x), 
			CCMath.blend(10, _myPane.getHeight() - 10, theRelative.y)
		);
	}
	
	public void spline(CCSpline theSpline){
		_mySpline = theSpline;
	}
	
	private CCVector3 selectPoint(MouseEvent e){
		for(CCVector3 myPoint:_mySpline.points()){
			CCVector3 myMouseCoord = relativeToMouse(myPoint);
			double x0 = myMouseCoord.x - CURVE_POINT_SIZE / 2;
			double y0 = myMouseCoord.y - CURVE_POINT_SIZE / 2;
			
			if(
				e.getX() >= x0 && 
				e.getX() <= x0 + CURVE_POINT_SIZE && 
				e.getY() >= y0 && 
				e.getY() <= y0 + CURVE_POINT_SIZE
			){
				return myPoint;
			}
		}
		
		return null;
	}
	public static final int CURVE_POINT_SIZE = 5;

	@SuppressWarnings("serial")
	class CurvePane extends JComponent {
		public CurvePane() {
			
		}
		
		private void point(Graphics2D g2d, CCVector3 thePoint){
			CCVector3 mouseCoord = relativeToMouse(thePoint);
			g2d.fillOval(
				(int) (mouseCoord.x) - CURVE_POINT_SIZE / 2,
				(int) (mouseCoord.y) - CURVE_POINT_SIZE / 2, 
				CURVE_POINT_SIZE, CURVE_POINT_SIZE
			);
		}
		
		private CCVector3 mouseSplinePoint(int i){
			return relativeToMouse(_mySpline.points().get(i));
		}

		public void paint(Graphics g) {
			if(_mySpline == null)return;
			if(_mySpline.points().size() <= 0)return;

			Graphics2D g2d = (Graphics2D)g;

			g2d.setColor(Color.LIGHT_GRAY);
			
			GeneralPath myPath = new GeneralPath();
			GeneralPath myBezierPath = new GeneralPath();
			CCVector3 myPoint = mouseSplinePoint(0);
			myPath.moveTo(myPoint.x, myPoint.y);

			if(_mySpline instanceof CCLinearSpline){
				for(int i = 1; i < _mySpline.points().size();i++){
					myPoint = mouseSplinePoint(i);
					myPath.lineTo(myPoint.x, myPoint.y);
				}
			}else if(_mySpline instanceof CCCatmulRomSpline){
				for(double i = 0; i <= _mySpline.points().size() * 100;i++){
					double myBlend = i / (_mySpline.points().size() * 100);
					myPoint = relativeToMouse(_mySpline.interpolate(myBlend));
					myPath.lineTo(myPoint.x, myPoint.y);
				}
			}else if(_mySpline instanceof CCBezierSpline){
				
				for(int i = 1; i < _mySpline.points().size();i+=3){
					myPoint = mouseSplinePoint(i);
					CCVector3 myPoint1 = mouseSplinePoint(i + 1);
					CCVector3 myPoint2 = mouseSplinePoint(i + 2);
					myPath.curveTo(
						myPoint.x, myPoint.y, 
						myPoint1.x, myPoint1.y, 
						myPoint2.x, myPoint2.y
					);
				}
				for(int i = 1; i < _mySpline.points().size();i+=3){
					myPoint = mouseSplinePoint(i-1);
					CCVector3 myPoint1 = mouseSplinePoint(i);
					CCVector3 myPoint2 = mouseSplinePoint(i + 1);
					CCVector3 myPoint3 = mouseSplinePoint(i + 2);
					myBezierPath.moveTo(myPoint.x, myPoint.y); 
					myBezierPath.lineTo(myPoint1.x, myPoint1.y);
					myBezierPath.moveTo(myPoint2.x, myPoint2.y);
					myBezierPath.lineTo(myPoint3.x, myPoint3.y);
					
				}
			}
			g2d.setColor(Color.LIGHT_GRAY);
			g2d.draw(myPath);
			g2d.setColor(Color.GRAY);
			g2d.draw(myBezierPath);
			
			g2d.setColor(Color.BLACK);
			
			for(CCVector3 myPoint0:_mySpline.points()){
				point(g2d, myPoint0);
			}

			g2d.setColor(Color.RED);
			if(_myHighlightedPoint != null)point(g2d, _myHighlightedPoint);
		}
	}

	

	public static void main(String[] args) {
		CCSplineEditor myEditor = new CCSplineEditor("check");
		myEditor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		myEditor.spline(new CCBezierSpline(false));
		myEditor.setVisible(true);
	}
}