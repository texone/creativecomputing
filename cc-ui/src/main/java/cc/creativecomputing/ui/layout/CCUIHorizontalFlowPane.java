package cc.creativecomputing.ui.layout;

import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.ui.widget.CCUIWidget;

public class CCUIHorizontalFlowPane extends CCUIPane{

	public CCUIHorizontalFlowPane() {
		super();
	}

	public CCUIHorizontalFlowPane(double theWidth, double theHeight) {
		super(theWidth, theHeight);
	}

	@Override
	public void addChild(CCUIWidget widget) {
		super.addChild(widget);
		
		double myX = _cMargin;
		double myY = -_cMargin;
		double myMaxHeight = 0;
		for(CCUIWidget myWidget:children()) {
			myWidget.translation().set(myX, myY);
			myX += myWidget.width();
			myX += _cSpace;
			myMaxHeight = CCMath.max(myMaxHeight,myWidget.height());
		}
		_myMinSize = new CCVector2(myX - _cSpace + _cMargin, 2 * _cMargin + myMaxHeight);

		updateMatrices();
	}
	
	
}
