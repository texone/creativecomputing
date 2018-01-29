package cc.creativecomputing.controlui.patch;

import java.awt.Graphics;

public class CCNodePinView extends CCPatchElementView{

	public CCNodePinView(int theX, int theY, int theWidth, int theHeight) {
		super(theX - 2, theY - 2, 9, 9);
	}

	public void draw(Graphics g){
		g.fillOval(position.x + 2, position.y + 2, 5, 5);
	}
}
