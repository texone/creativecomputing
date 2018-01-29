package cc.creativecomputing.uinano;

import cc.creativecomputing.core.events.CCIntEvent;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.uinano.layout.Alignment;
import cc.creativecomputing.uinano.layout.BoxLayout;

/**
 * \class MessageDialog messagedialog.h nanogui/messagedialog.h
 *
 * \brief Simple "OK" or "Yes/No"-style modal dialogs.
 */
public class MessageDialog extends Window {

	protected CCListenerManager<CCIntEvent> mCallback = CCListenerManager.create(CCIntEvent.class);
	protected CCUILabel mMessageLabel;

	/// Classification of the type of message this MessageDialog represents.
	public enum Type {
		Information, Question, Warning;
	}

	public MessageDialog(CCWidget parent, Type type, String title, String message, String buttonText,
			String altButtonText) {
		this(parent, type, title, message, buttonText, altButtonText, false);
	}

	public MessageDialog(CCWidget parent, Type type, String title, String message, String buttonText) {
		this(parent, type, title, message, buttonText, "Cancel", false);
	}

	public MessageDialog(CCWidget parent, Type type, String title, String message) {
		this(parent, type, title, message, "OK", "Cancel", false);
	}

	public MessageDialog(CCWidget parent, Type type, String title) {
		this(parent, type, title, "Message", "OK", "Cancel", false);
	}

	public MessageDialog(CCWidget parent, Type type) {
		this(parent, type, "Untitled", "Message", "OK", "Cancel", false);
	}

	public MessageDialog(CCWidget parent, Type type, String title, String message, String buttonText,
			String altButtonText, boolean altButton) {
		super(parent, title);
		setLayout(new BoxLayout(Orientation.Vertical, Alignment.Middle, 10, 10));
		setModal(true);

		CCWidget panel1 = new CCWidget(this);
		panel1.setLayout(new BoxLayout(Orientation.Horizontal, Alignment.Middle, 10, 15));
		TypoIcon icon = null;
		switch (type) {
		case Information:
			icon = _myTheme.mMessageInformationIcon;
			break;
		case Question:
			icon = _myTheme.mMessageQuestionIcon;
			break;
		case Warning:
			icon = _myTheme.mMessageWarningIcon;
			break;
		}
		CCUILabel iconLabel = new CCUILabel(panel1, utf8(icon.id), "icons");
		iconLabel.setFontSize(50);
		mMessageLabel = new CCUILabel(panel1, message);
		mMessageLabel.setFixedWidth(200);
		CCWidget panel2 = new CCWidget(this);
		panel2.setLayout(new BoxLayout(Orientation.Horizontal, Alignment.Middle, 0, 15));

		if (altButton) {
			Button button = new Button(panel2, altButtonText, _myTheme.mMessageAltButtonIcon.id);
			button.mCallback.add(() -> {

				mCallback.proxy().event(1);
				dispose();
			});
		}
		Button button = new Button(panel2, buttonText, _myTheme.mMessagePrimaryButtonIcon.id);
		button.mCallback.add(() -> {

			mCallback.proxy().event(0);

			dispose();
		});
		center();
		requestFocus();
	}

	public final CCUILabel messageLabel() {
		return mMessageLabel;
	}

}