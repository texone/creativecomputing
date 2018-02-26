package cc.creativecomputing.ui.widget;

import cc.creativecomputing.core.events.CCEvent;
import cc.creativecomputing.graphics.font.CCFont;
import cc.creativecomputing.ui.layout.CCUIVerticalFlowPane;

public class CCUIMenueWidget extends CCUIVerticalFlowPane{
	
	private CCFont<?> _myFont;

	public CCUIMenueWidget(CCFont<?> theFont) {
		_myFont = theFont;
		
		margin(5);
		space(5);
	}
	
	public void addItem(String theLabel, CCEvent theEvent) {
		CCUILabelWidget myLabel = new CCUILabelWidget(_myFont, theLabel);
		myLabel.mouseReleased.add(event -> {
			theEvent.event();
		});
		addChild(myLabel);
	}
}
