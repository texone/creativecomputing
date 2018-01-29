package cc.creativecomputing.controlui.patch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.List;

public class CCConnectionView implements MouseListener, MouseMotionListener {

	private List<CCNodeView> _myNodes = new ArrayList<>();
	
	public CCConnectionView(List<CCNodeView> theNodes){
		_myNodes = theNodes;
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(_myPressedNodeView == null)return;
		_myEndX = e.getX();
		_myEndY = e.getY();
		
		
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	private CCNodeView _myPressedNodeView;
	private CCNodePinView _myPressedInput;
	private CCNodePinView _myPressedOutput;
	
	private int _myStartX = 0;
	private int _myStartY = 0;
	private int _myEndX = 0;
	private int _myEndY = 0;
	
	private class CCConnectionLineView{
		CCNodeView _myInView;
		CCNodeView _myOutView;
		CCNodePinView _myInput;
		CCNodePinView _myOutput;
		
		public void draw(Graphics g){
			g.drawLine(
				_myInView.position.x + _myInput.position.x + 4, _myInView.position.y + _myInput.position.y + 4, 
				_myOutView.position.x + _myOutput.position.x + 4, _myOutView.position.y + _myOutput.position.y + 5
			);
		}
	}
	
	private List<CCConnectionLineView> _myConnectionViews = new ArrayList<>();

	@Override
	public void mousePressed(MouseEvent e) {
		for(CCNodeView myNodeView:_myNodes){
			CCNodePinView myIn = myNodeView.getTouchedInput(e);
			if(myIn != null){
				_myPressedNodeView = myNodeView;
				_myPressedInput = myIn;
				_myStartX = _myEndX = myNodeView.position.x + myIn.position.x + 4;
				_myStartY = _myEndY = myNodeView.position.y + myIn.position.y + 4;
				return;
			}
			CCNodePinView myOut = myNodeView.getTouchedOutput(e);
			if(myOut != null){
				_myPressedNodeView = myNodeView;
				_myPressedOutput = myOut;
				_myStartX = _myEndX = myNodeView.position.x + myOut.position.x + 4;
				_myStartY = _myEndY = myNodeView.position.y + myOut.position.y + 4;
				return;
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(_myPressedInput == null && _myPressedOutput == null)return;
		
		for(CCNodeView myNodeView:_myNodes){
			if(_myPressedInput != null){
				CCNodePinView myOut = myNodeView.getTouchedOutput(e);
				
				if(myOut != null){
					CCConnectionLineView myView = new CCConnectionLineView();
					myView._myInView = _myPressedNodeView;
					myView._myOutView = myNodeView;
					myView._myInput = _myPressedInput;
					myView._myOutput = myOut;
					_myConnectionViews.add(myView);
					break;
				}
			}

			if(_myPressedOutput != null){
				CCNodePinView myIn = myNodeView.getTouchedInput(e);
				
				if(myIn != null){
					CCConnectionLineView myView = new CCConnectionLineView();
					myView._myInView = myNodeView;
					myView._myOutView = _myPressedNodeView;
					myView._myInput = myIn;
					myView._myOutput = _myPressedOutput;
					_myConnectionViews.add(myView);
					break;
				}
			}
		}
		_myPressedNodeView = null;
		_myPressedInput = null;
		_myPressedOutput = null;
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	
	public void draw(Graphics g){
		g.setColor(new Color(0,0,0));
		if(_myPressedNodeView != null)g.drawLine(_myStartX, _myStartY, _myEndX, _myEndY);
		for(CCConnectionLineView myView:_myConnectionViews){
			myView.draw(g);
		}
	}
}
