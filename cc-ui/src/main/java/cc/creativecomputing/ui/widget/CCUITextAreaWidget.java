package cc.creativecomputing.ui.widget;


public class CCUITextAreaWidget extends CCUITextFieldWidget{

	public CCUITextAreaWidget(CCUIWidgetStyle theStyle, String theText) {
		super(theStyle, theText);
		_myTextController.isTextArea(true);
	}

	public CCUITextAreaWidget(String theText) {
		super(theText);
		_myTextController.isTextArea(true);
	}

}
