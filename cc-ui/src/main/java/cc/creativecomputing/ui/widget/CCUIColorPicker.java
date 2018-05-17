package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;

public class CCUIColorPicker extends CCUILabelWidget{
	
	public static CCUIWidgetStyle createDefaultStyle(){
		CCUIWidgetStyle myResult = CCUILabelWidget.createDefaultStyle();
		return myResult;
	}
	
	private CCUIColorWheel _myColorWheel;
	
	private CCUIFillDrawable _myFillDrawable;

	public CCEventManager<CCColor> changeEvents = new CCEventManager<>();

	public CCUIColorPicker(CCUIWidgetStyle theStyle, CCColor theColor, double theWidth, double theHeight){
		super(theStyle, theColor.toString());
		_myMinWidth = theWidth;
		_myMinHeight = theHeight;
		width(theWidth);
		height(theHeight);
		_myFillDrawable = new CCUIFillDrawable(theColor);
		
		_myOverlay = _myColorWheel = new CCUIColorWheel(200);
		_myColorWheel.changeEvents.add(c -> {
			color(c, true);
		});
		_myColorWheel.isActive(false);

		_myColorWheel.mouseReleasedOutside.add(event ->{
			_myColorWheel.isActive(false);
		});
		_myColorWheel.setFromColor(theColor);
		
		mousePressed.add(event -> {
			_myColorWheel.translation().set(-_myColorWheel.width() / 2 + width() / 2, _myColorWheel.height() / 2  - height() / 2);
			_myColorWheel.isActive(true);
			_myColorWheel.parent(this);
			_myColorWheel.updateMatrices();
		});
		
		color(theColor, false);
	}
	
	public CCUIColorPicker(CCColor theColor, double theWidth, double theHeight){
		this(createDefaultStyle(), theColor, theWidth, theHeight); 
	}
	
	public void color(CCColor theColor, boolean theSendValue) {
		_myFillDrawable.color().set(theColor);
		textField().text(theColor.toString());
		if(theSendValue)changeEvents.event(theColor);

	}
	
	@Override
	public CCUIWidget overlayWidget() {
		return _myColorWheel.isActive() ? _myColorWheel : null;
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		_myFillDrawable.draw(g, this);
		super.drawContent(g);
		
	}
}
