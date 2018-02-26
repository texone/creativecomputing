package cc.creativecomputing.ui.widget;

import cc.creativecomputing.graphics.font.CCEntypoIcon;

public class CCUICheckBoxWidget extends CCUIIconWidget{
	
	private boolean _myIsSelected = true;

	public CCUICheckBoxWidget() {
		super(CCEntypoIcon.ICON_CHECK);
		
		mouseReleased.add(event -> {
			_myIsSelected = !_myIsSelected;
			_myTextField.text(_myIsSelected ? CCEntypoIcon.ICON_CHECK.text : "");
		});
		
		inset(2);
	}

	
}
