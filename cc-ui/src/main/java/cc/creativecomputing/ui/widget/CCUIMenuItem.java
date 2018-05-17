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
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.layout.CCUIHorizontalFlowPane;

public class CCUIMenuItem extends CCUIHorizontalFlowPane{

	private String _myToolTip = "";
	
	private CCUIFillDrawable _myBackground;
	
	private CCUIMenuItem(CCUIWidgetStyle theStyle, CCUIWidget theIconWidget, CCUILabelWidget theLabelWidget){
		super(theStyle);
		space(5);
		addChild(theIconWidget);
		addChild(theLabelWidget);
		_myBackground = new CCUIFillDrawable(new CCColor(0,0));
	}
	
	public CCUIMenuItem(CCUIWidgetStyle theStyle, String theText){
		this(theStyle, new CCUIIconWidget(CCEntypoIcon.OFF), new CCUILabelWidget(theStyle, theText));
	}
	
	public CCUIMenuItem(CCEntypoIcon theIcon, CCUIWidgetStyle theStyle, String theText){
		this(theStyle, new CCUIIconWidget(theIcon), new CCUILabelWidget(theStyle, theText));
	}
	
	public CCUIMenuItem(CCUICheckBox theCheckBox, CCUIWidgetStyle theStyle, String theText){
		this(theStyle,theCheckBox, new CCUILabelWidget(theStyle, theText));
	}
	
	public CCUIFillDrawable background(){
		return _myBackground;
	}
	
	public CCUICheckBox checkBox(){
		CCUIWidget myWidget = _myChildren.get(0);
		if(myWidget instanceof CCUICheckBox)return (CCUICheckBox)myWidget;
		return null;
	}
	
	public String text(){
		return ((CCUILabelWidget)_myChildren.get(1)).textField().text();
	}

	public void toolTipText(String theToolTip) {
		_myToolTip = theToolTip;
	}
	
	public String toolTip(){
		return _myToolTip;
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		if(_myBackground != null)_myBackground.draw(g, this);
		if(_myStyle.border() != null)_myStyle.border().draw(g, this);
		if(_myStyle.foreground() != null)_myStyle.foreground().draw(g, this);
		
		if(_myChildren == null) return;
		for(CCUIWidget myChild:_myChildren) {
			g.pushMatrix();
			g.applyMatrix(myChild.localTransform());
			if(_myStyle.border() != null)_myStyle.border().draw(g, myChild);
			if(_myStyle.foreground() != null)_myStyle.foreground().draw(g, myChild);
			g.popMatrix();
		}
	}
}
