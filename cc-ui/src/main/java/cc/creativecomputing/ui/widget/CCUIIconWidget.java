package cc.creativecomputing.ui.widget;

import cc.creativecomputing.graphics.font.CCEntypoIcon;
import cc.creativecomputing.ui.input.CCUIContext;

public class CCUIIconWidget extends CCUILabelWidget{

	private CCEntypoIcon _myIcon;
	
	private boolean _myIsActive = true;
	
	public CCUIIconWidget(CCEntypoIcon theIcon) {
		super(CCUIContext.ICON_FONT, theIcon.text);
		_myIcon = theIcon;
	}
	
	public boolean active() {
		return _myIsActive;
	}
	
	public void active(boolean theIsActive) {
		_myIsActive = theIsActive;
	}

	public void icon(CCEntypoIcon theIcon) {
		_myTextField.text(theIcon.text);
		_myIcon = theIcon;
	}
	
	public CCEntypoIcon icon() {
		return _myIcon;
	}
}
