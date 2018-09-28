package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.draw.CCUIDrawable;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.draw.CCUIStrokeDrawable;
import cc.creativecomputing.ui.draw.CCUITextDrawable;

public class CCUIWidgetStyle {
	
	public static CCUIDrawable OFF = (g,node) ->{};
	
	@CCProperty(name="background")
	protected CCUIDrawable _myBackground = OFF;
	@CCProperty(name="border")
	protected CCUIDrawable _myBorder = OFF;
	@CCProperty(name="foreground")
	protected CCUIDrawable _myForeground = OFF;
	
	public void background(CCUIDrawable theBackground) {
		_myBackground = theBackground;
	}
	
	public void backgroundFill(CCColor theColor) {
		_myBackground = new CCUIFillDrawable(theColor);
	}
	
	public void backgroundStroke(CCColor theColor) {
		_myBackground = new CCUIStrokeDrawable(theColor, 1, 0);
	}
	
	public CCUIDrawable background(){
		return _myBackground;
	}
	
	public void border(CCUIDrawable theBorder) {
		_myBorder = theBorder;
	}
	
	public CCUIDrawable border() {
		return _myBorder;
	}
	
	public void foreground(CCUIDrawable theForeground) {
		_myForeground = theForeground;
	}
	
	public CCUIDrawable foreground() {
		return _myForeground;
	}
	
	protected CCFont<?> _myFont;
	
	public void font(CCFont<?> theFont){
		_myFont = theFont;
		_myForeground = new CCUITextDrawable();
	}
	
	public CCFont<?> font(){
		return _myFont;
	}
	
	public void style(CCUIWidgetStyle theStyle){
		background(theStyle.background());
		foreground(theStyle.foreground());
		border(theStyle.border());
	}
	
	protected CCColor _myItemSelectBackground;
	
	protected CCColor _myItemBackground = new CCColor(0,0);
	
	public void itemSelectBackground(CCColor theSelectBackground){
		_myItemSelectBackground = theSelectBackground;
	}
	
	public CCColor itemSelectBackground(){
		return _myItemSelectBackground;
	}
	
	public void itemBackground(CCColor theItemBackground){
		_myItemBackground = theItemBackground;
	}
	
	public CCColor itemBackground(){
		return _myItemBackground;
	}

	public void drawContent(CCGraphics g, CCUIWidget theWidget) {
		if(_myBackground != null)_myBackground.draw(g, theWidget);
		if(_myBorder != null)_myBorder.draw(g, theWidget);
		if(_myForeground != null)_myForeground.draw(g, theWidget);
	}
}
