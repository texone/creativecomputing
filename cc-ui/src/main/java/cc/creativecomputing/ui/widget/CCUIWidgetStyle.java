package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.CCUIHorizontalAlignment;
import cc.creativecomputing.ui.CCUIVerticalAlignment;
import cc.creativecomputing.ui.draw.CCUIDrawable;
import cc.creativecomputing.ui.draw.CCUITextDrawable;

public class CCUIWidgetStyle {
	
	@CCProperty(name="horizontal_alignment")
	private CCUIHorizontalAlignment _myHorizontalAlignment = CCUIHorizontalAlignment.LEFT;
	@CCProperty(name="vertical_alignment")
	private CCUIVerticalAlignment _myVerticalAlignment = CCUIVerticalAlignment.TOP;
	
	public CCUIHorizontalAlignment horizontalAlignment() {
		return _myHorizontalAlignment;
	}

	public void horizontalAlignment(CCUIHorizontalAlignment theHorizontalAlignment) {
		_myHorizontalAlignment = theHorizontalAlignment;
	}

	public CCUIVerticalAlignment verticalAlignment() {
		return _myVerticalAlignment;
	}

	public void verticalAlignment(CCUIVerticalAlignment theVerticalAlignment) {
		_myVerticalAlignment = theVerticalAlignment;
	}
	
	@CCProperty(name="background")
	protected CCUIDrawable _myBackground;
	@CCProperty(name="border")
	protected CCUIDrawable _myBorder;
	@CCProperty(name="foreground")
	protected CCUIDrawable _myForeground;
	
	public void background(CCUIDrawable theBackground) {
		_myBackground = theBackground;
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
	
	@CCProperty(name = "left inset")
	protected double _myLeftInset = 0;
	@CCProperty(name = "right inset")
	protected double _myRightInset = 0;
	@CCProperty(name = "top inset")
	protected double _myTopInset = 0;
	@CCProperty(name = "bottom inset")
	protected double _myBottomInset = 0;
	
	public double leftInset() {
		return _myLeftInset;
	}
	
	public void leftInset(double theInset) {
		_myLeftInset = theInset;
	}
	
	public double rightInset() {
		return _myRightInset;
	}
	
	public void rightInset(double theInset) {
		_myRightInset = theInset;
	}
	
	public double topInset() {
		return _myTopInset;
	}
	
	public void topInset(double theInset) {
		_myTopInset = theInset;
	}
	
	public double bottomInset() {
		return _myBottomInset;
	}
	
	public void bottomInset(double theInset) {
		_myBottomInset = theInset;
	}
	
	public void inset(double theInset) {
		_myLeftInset = theInset;
		_myRightInset = theInset;
		_myTopInset = theInset;
		_myBottomInset = theInset;
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
		
		horizontalAlignment(theStyle.horizontalAlignment());
		verticalAlignment(theStyle.verticalAlignment());
		
		leftInset(theStyle.leftInset());
		rightInset(theStyle.rightInset());
		topInset(theStyle.topInset());
		bottomInset(theStyle.bottomInset());
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

	private CCUISeparatorStyle _mySeparatorStyle = new CCUISeparatorStyle();
	

	public CCUISeparatorStyle separatorStyle(){
		return _mySeparatorStyle;
	}

	public void drawContent(CCGraphics g, CCUIWidget theWidget) {
		if(_myBackground != null)_myBackground.draw(g, theWidget);
		if(_myBorder != null)_myBorder.draw(g, theWidget);
		if(_myForeground != null)_myForeground.draw(g, theWidget);
	}
}
