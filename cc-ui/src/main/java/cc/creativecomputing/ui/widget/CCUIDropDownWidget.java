package cc.creativecomputing.ui.widget;

import cc.creativecomputing.graphics.font.CCFont;

public class CCUIDropDownWidget extends CCUILabelWidget{
	
	private CCUIMenueWidget _myMenue;

	public CCUIDropDownWidget(CCFont<?> theFont) {
		super(theFont, "...");
		_myMenue = new CCUIMenueWidget(theFont);
	}

	public void addItem(String theLabel) {
		_myMenue.addItem(theLabel, () -> {});
	}
}
