package cc.creativecomputing.controlui.patch;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.math.CCVector2i;

public class CCPatchElementView implements MouseListener, MouseMotionListener{
	public CCVector2i position;
	public int width;
	public int height;
	
	public CCPatchElementView(int theX, int theY, int theWidth, int theHeight){
		position = new CCVector2i(theX, theY);
		width = theWidth;
		height = theHeight;
	}
	

	public boolean isOver(int theX, int theY){
		CCLog.info(theX + " : " + position.x + " : " + (position.x + width) + " : " + theY + " : " + position.y + " : " + (position.y + height));
		return theX > position.x && theX < position.x + width && theY > position.y && theY < position.y + height;
	}

	
	@Override
	public void mouseEntered(MouseEvent e) {}
	
	@Override
	public void mouseExited(MouseEvent e) {}
	
	public void mousePressed(MouseEvent e){}
	
	public void mouseReleased(MouseEvent e){}
	
	@Override
	public void mouseClicked(MouseEvent e) {}

	public void mouseDragged(MouseEvent e){}

	public void mouseMoved(MouseEvent e){}
}
