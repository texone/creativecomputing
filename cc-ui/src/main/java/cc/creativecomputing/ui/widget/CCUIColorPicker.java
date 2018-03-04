package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCColor.CCColorEvent;
import cc.creativecomputing.ui.draw.CCUIFillDrawable;
import cc.creativecomputing.ui.widget.CCUILabelWidget;

public class CCUIColorPicker extends CCUILabelWidget{
	
	private CCUIIconWidget _myColorsIcon;
	
	private CCUIColorWheel _myColorWheel;
	
	private CCUIFillDrawable _myFillDrawable;

	public CCListenerManager<CCColorEvent> changeEvents = CCListenerManager.create(CCColorEvent.class);

	public CCUIColorPicker(CCFont<?> theFont, CCColor theColor, double theWidth, double theHeight){
		super(theFont, theColor.toString());
		width(theWidth);
		height(theHeight);
		background(_myFillDrawable = new CCUIFillDrawable(theColor));
		inset(2);
		
		_myColorsIcon = new CCUIIconWidget(CCEntypoIcon.ICON_COLOURS);
		
		_myOverlay = _myColorWheel = new CCUIColorWheel(200);
		_myColorWheel.changeEvents.add(c -> {
			color(c, true);
		});
		_myColorWheel.isActive(false);
		_myColorWheel.background(new CCUIFillDrawable(CCColor.BLUE));
		_myColorWheel.translation().set(-_myColorWheel.width() / 2 + width() / 2, _myColorWheel.height() / 2  - height() / 2);

		_myColorWheel.mouseReleasedOutside.add(event ->{
			_myColorWheel.isActive(false);
		});
		
		mousePressed.add(event -> {
			_myColorWheel.isActive(true);
		});
		
		color(theColor, false);
	}
	
	public void color(CCColor theColor, boolean theSendValue) {
		_myFillDrawable.color().set(theColor);
		text().text(theColor.toString());
		if(theSendValue)changeEvents.proxy().event(theColor);

	}
	
	@Override
	public CCUIWidget overlayWidget() {
		CCLog.info(_myColorWheel.isActive());
		return _myColorWheel.isActive() ? _myColorWheel : null;
	}
	
	@Override
	public void drawContent(CCGraphics g) {
		super.drawContent(g);
		g.color(255);
		_myColorsIcon.text().position().set(width() - _myColorsIcon.width() - 2, - _myColorsIcon.height() + 2, 0);
//		_myColorsIcon.text().draw(g);
		
	}
}
