/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/

package cc.creativecomputing.ui.widget;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.yoga.CCYogaNode;

/**
 * @author christianriekoff
 *
 */
public class CCUIWidget extends CCYogaNode{

	protected CCUIWidgetStyle _myStyle;
	
	public CCUIWidget(CCUIWidgetStyle theStyle) {
		_myStyle = theStyle;
	}
	
	public CCUIWidget() {
		_myStyle = new CCUIWidgetStyle();
	}
	
	public void style(CCUIWidgetStyle theStyle){
		_myStyle = theStyle;
	}
	
	public CCUIWidgetStyle style(){
		return _myStyle;
	}
	
	public void display(CCGraphics g) {
		g.pushMatrix();
		g.applyMatrix(_myLocalMatrix);
		if(_myIsOverlay)g.translate(0,0,1);
		displayContent(g);
		for(CCYogaNode myChild:this) {
			myChild.display(g);
		}
		g.popMatrix();
	}
	
	public void displayContent(CCGraphics g) {
		_myStyle.drawContent(g,this);
		
		
//		g.color(255,0,0);
//		g.rect(0, 0, width(), height(), true);
	}
}
