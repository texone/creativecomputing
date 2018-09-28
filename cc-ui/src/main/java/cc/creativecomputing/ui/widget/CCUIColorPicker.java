package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.gl.app.CCGLMouseButton;
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

	public CCUIColorPicker(CCUIWidgetStyle theStyle, CCColor theColor){
		super(theStyle, theColor.toString());
		debugInfo("color picker", theColor);
		_myFillDrawable = new CCUIFillDrawable(theColor);
		
		_myColorWheel = new CCUIColorWheel(200);
		_myColorWheel.changeEvents.add(c -> {
			color(c, true);
		});
		_myColorWheel.isActive(false);
		
		overlay(_myColorWheel);

		_myColorWheel.mouseReleasedOutside.add(event ->{
			_myColorWheel.isActive(false);
			removeChild(_myColorWheel);
		});
		_myColorWheel.setFromColor(theColor);
		
		mousePressed.add(event -> {
			if(event.button != CCGLMouseButton.BUTTON_RIGHT)return;
//			_myColorWheel.translation().set(-_myColorWheel.width() / 2 + width() / 2, _myColorWheel.height() / 2  - height() / 2);
			_myColorWheel.positionType(CCYogaPositionType.ABSOLUTE);
			_myColorWheel.position(CCYogaEdge.LEFT, -_myColorWheel.width() / 2 + width() / 2);
			_myColorWheel.position(CCYogaEdge.TOP,  -_myColorWheel.height() / 2 + height() / 2 );
			_myColorWheel.isActive(true);
			addChild(_myColorWheel);
			root().calculateLayout();
			_myColorWheel.updateMatrices();
		});
		
		color(theColor, false);
	}
	
	@Override
	public boolean isEndNode() {
		return !_myColorWheel.isActive();
	}
	
	public CCUIColorPicker(CCColor theColor) {
		this(createDefaultStyle(), theColor);
	}
	
	public CCUIColorPicker() {
		this(CCColor.RED);
	}
	
	public void color(CCColor theColor, boolean theSendValue) {
		_myFillDrawable.color().set(theColor);
		textField().text(theColor.toString());
		if(theSendValue)changeEvents.event(theColor);

	}
	
//	@Override
//	public CCUIWidget overlayWidget() {
//		return _myColorWheel.isActive() ? _myColorWheel : null;
//	}
	
	@Override
	public void displayContent(CCGraphics g) {
		_myFillDrawable.draw(g, this);
		super.displayContent(g);
		
	}
}
