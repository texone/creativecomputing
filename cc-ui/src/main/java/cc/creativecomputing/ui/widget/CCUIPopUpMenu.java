package cc.creativecomputing.ui.widget;

public class CCUIPopUpMenu extends CCUIMenu{

	public CCUIPopUpMenu(CCUIWidgetStyle theStyle, CCUIWidget theParentWidget, double theX, double theY) {
		super(theStyle);
		
		translation().set(theX, theY);
		parent(theParentWidget);
		isActive(true);
		updateMatrices();
		reset();
		theParentWidget.overlayWidget(this);
	}

	public CCUIPopUpMenu(CCUIWidget theParentWidget, double theX, double theY) {
		this(createDefaultStyle(), theParentWidget, theX, theY);
	}
}
