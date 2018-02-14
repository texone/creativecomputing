package cc.creativecomputing.ui.layout;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIVerticalFlowPane extends CCUIPane{

	@Override
	public void addChild(CCUIWidget widget) {
		super.addChild(widget);
		
		double myX = _cMargin;
		double myY = -_cMargin;
		double myMaxWidth = 0;
		for(CCUIWidget myWidget:children()) {
			myWidget.translation().set(myX, myY);
			myY -= myWidget.height();
			myY -= _cSpace;
			myMaxWidth = CCMath.max(myMaxWidth,myWidget.width());
		}
		_myMinSize = new CCVector2(2 * _cMargin + myMaxWidth, -myY - _cSpace + _cMargin);
		updateMatrices();
	}
	
	
}
