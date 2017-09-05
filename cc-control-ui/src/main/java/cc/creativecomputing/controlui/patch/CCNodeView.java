package cc.creativecomputing.controlui.patch;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCMath;

public class CCNodeView extends CCPatchElementView{
	
	private CCNode _myNode;
	private CCNodePinView _myOutputView;
	private List<CCNodePinView> _myInputs = new ArrayList<>(); 
	
	public CCNodeView(CCNode theNode, int theX, int theY){
		super(theX, theY + 4, 100, 17);
		_myNode = theNode;
		_myOutputView = new CCNodePinView(-2, height + 2, 5, 5);
		
		int i = 0;
		for(CCNodeInput myInput:theNode.inputs){
			double myX = i == 0 ? -2 : CCMath.map(i, 0, theNode.inputs.size() - 1, -2, width - 2);
			i++;
			_myInputs.add(new CCNodePinView((int)myX, -6, 5, 5));
		}
	}
	
	private CCNodeInput _myPressedConnection;
	private CCNodeOutput _myPressedOutput;
	private int _myStartX;
	private int _myStartY;
	private int _myEndX;
	private int _myEndY;
	
	public CCNodePinView getTouchedInput(MouseEvent e){
		int mouseX = e.getX() - position.x;
		int mouseY = e.getY() - position.y;
		for(CCNodePinView myInput:_myInputs){
			if(myInput.isOver(mouseX, mouseY))return myInput;
		}
		return null;
	}
	
	public CCNodePinView getTouchedOutput(MouseEvent e){
		int mouseX = e.getX() - position.x;
		int mouseY = e.getY() - position.y;
		if(_myOutputView.isOver(mouseX, mouseY))return _myOutputView;
		return null;
	}
	
	protected boolean _myIsPressed;
	

	private MouseEvent _myLastEvent;
	
	@Override
	public void mousePressed(MouseEvent e) {
		_myIsPressed = isOver(e.getX(), e.getY());
		CCLog.info(_myIsPressed);
		_myLastEvent = e;
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(!_myIsPressed)return;
		int moveX = e.getX() - _myLastEvent.getX();
		int moveY = e.getY() - _myLastEvent.getY();
		_myLastEvent = e;
		
		position.x += moveX;
		position.y += moveY;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		super.mouseReleased(e);
		if(_myPressedConnection != null){
			
		}
		_myPressedConnection = null;
		_myPressedOutput = null;
	}

	public void draw(Graphics g){
		g.translate(position.x, position.y);
		g.setColor(new Color(200,200,200));
		g.fillRect(0, -4, width, height + 8);
		g.setColor(new Color(150,150,150));
		g.drawRect(0, -4, width, height + 8);
		_myOutputView.draw(g);
		for(CCNodePinView myInput:_myInputs){
			myInput.draw(g);
		}
		
		g.setColor(new Color(0,0,0));
		g.drawString(_myNode.name, 5, 13);
		g.translate(-position.x, -position.y);
		
		if(_myPressedConnection != null || _myPressedOutput != null){
			g.drawLine(_myStartX, _myStartY, _myEndX, _myEndY);
		}
	}

}
