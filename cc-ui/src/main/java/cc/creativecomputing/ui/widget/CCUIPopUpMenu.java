package cc.creativecomputing.ui.widget;

public class CCUIPopUpMenu extends CCUIMenu{

	public CCUIPopUpMenu(CCUIWidgetStyle theStyle, CCUIWidget theParentWidget, double theX, double theY) {
		super(theStyle);
		
		
//		translation().set(theX, theY);
		isActive(true);
		reset();
		theParentWidget.overlay(this);
		theParentWidget.addChild(this);
		positionType(CCYogaPositionType.ABSOLUTE);
		position(CCYogaEdge.LEFT, 0);
		position(CCYogaEdge.TOP, 0);
		addElements();
		root().calculateLayout();
		updateMatrices();
	}

	public CCUIPopUpMenu(CCUIWidget theParentWidget, double theX, double theY) {
		this(createDefaultStyle(), theParentWidget, theX, theY);
	}
	
	public void addElements() {
		
	}
}
