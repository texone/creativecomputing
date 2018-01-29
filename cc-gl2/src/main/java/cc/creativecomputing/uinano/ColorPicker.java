package cc.creativecomputing.uinano;

import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCColor.CCColorEvent;
import cc.creativecomputing.uinano.layout.GroupLayout;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class ColorPicker colorpicker.h nanogui/colorpicker.h
 *
 * \brief Push button with a popup to tweak a color value. This widget was
 * contributed by Christian Schueller.
 */
public class ColorPicker extends PopupButton {

	/// The "fast" callback executed when the ColorWheel has changed.
	protected final CCListenerManager<CCColorEvent> mCallback = CCListenerManager.create(CCColorEvent.class);

	/**
	 * The callback to execute when a new CCColor is selected on the ColorWheel
	 * **and** the user clicks the \ref nanogui::ColorPicker::mPickButton or
	 * \ref nanogui::ColorPicker::mResetButton.
	 */
	protected final CCListenerManager<CCColorEvent> mFinalCallback = CCListenerManager.create(CCColorEvent.class);

	/// The ColorWheel for this ColorPicker (the actual widget allowing
	/// selection).
	protected ColorWheel mColorWheel;

	/**
	 * The Button used to signal that the current value on the ColorWheel is the
	 * desired color to be chosen. The default value for the caption of this
	 * Button is ``"Pick"``. You can change it using \ref
	 * nanogui::ColorPicker::setPickButtonCaption if you need.
	 *
	 * The color of this Button will not affect \ref nanogui::ColorPicker::color
	 * until the user has actively selected by clicking this pick button.
	 * Similarly, the \ref nanogui::ColorPicker::mCallback function is only
	 * called when a user selects a new CCColor using by clicking this Button.
	 */
	protected Button mPickButton;

	/**
	 * Remains the CCColor of the active color selection, until the user picks a
	 * new CCColor on the ColorWheel **and** selects the \ref
	 * nanogui::ColorPicker::mPickButton. The default value for the caption of
	 * this Button is ``"Reset"``. You can change it using \ref
	 * nanogui::ColorPicker::setResetButtonCaption if you need.
	 */
	protected Button mResetButton;

	/**
	 * Attaches a ColorPicker to the specified parent.
	 *
	 * \param parent The Widget to add this ColorPicker to.
	 *
	 * \param color The color initially selected by this ColorPicker (default:
	 * Red).
	 */
	public ColorPicker(CCWidget parent) {
		this(parent, new CCColor(1.0f, 0.0f, 0.0f, 1.0f));
	}

	public ColorPicker(CCWidget parent, CCColor color) {
		super(parent, "", 0);
		setBackgroundColor(color);
		Popup popup = this.popup();
		popup.setLayout(new GroupLayout());

		// set the color wheel to the specified color
		mColorWheel = new ColorWheel(popup, color);

		// set the pick button to the specified color
		mPickButton = new Button(popup, "Pick");
		mPickButton.setBackgroundColor(color);
		mPickButton.setTextColor(color.brightness() > 0.5 ? CCColor.BLACK : CCColor.WHITE);
		mPickButton.setFixedSize(new CCVector2i(100, 20));

		// set the reset button to the specified color
		mResetButton = new Button(popup, "Reset");
		mResetButton.setBackgroundColor(color);
		mResetButton.setTextColor(color.brightness() > 0.5 ? CCColor.BLACK : CCColor.WHITE);
		mResetButton.setFixedSize(new CCVector2i(100, 20));

		super.mChangeCallback.add(bool -> {
			if (this.mPickButton.pushed()) {
				setColor(backgroundColor());
				mFinalCallback.proxy().event(backgroundColor());
			}
		});

		mColorWheel.mCallback.add(value -> {
			mPickButton.setBackgroundColor(value);
			mPickButton.setTextColor(color.brightness() > 0.5 ? CCColor.BLACK : CCColor.WHITE);
			mCallback.proxy().event(value);
		});

		mPickButton.mCallback.add(() -> {
			if (mPushed) {
				CCColor value = mColorWheel.color();
				setPushed(false);
				setColor(value);
				mFinalCallback.proxy().event(value);
			}
		});

		mResetButton.mCallback.add(() -> {
			CCColor bg = this.mResetButton.backgroundColor();
			CCColor fg = this.mResetButton.textColor();

			mColorWheel.setColor(bg);
			mPickButton.setBackgroundColor(bg);
			mPickButton.setTextColor(fg);

			mCallback.proxy().event(bg);
			mFinalCallback.proxy().event(bg);
		});
	}

	/// Get the current CCColor selected for this ColorPicker.
	public final CCColor color() {
		return backgroundColor();
	}

	/// Set the current CCColor selected for this ColorPicker.
	public final void setColor(CCColor color) {
		/* Ignore setColor() calls when the user is currently editing */
		if (!mPushed) {
			CCColor fg = color.brightness() > 0.5 ? CCColor.BLACK : CCColor.WHITE;
			setBackgroundColor(color);
			setTextColor(fg);
			mColorWheel.setColor(color);

			mPickButton.setBackgroundColor(color);
			mPickButton.setTextColor(fg);

			mResetButton.setBackgroundColor(color);
			mResetButton.setTextColor(fg);
		}
	}

	/// The current caption of the \ref nanogui::ColorPicker::mPickButton.
	public final String pickButtonCaption() {
		return mPickButton.caption();
	}

	/// Sets the current caption of the \ref nanogui::ColorPicker::mPickButton.
	public final void setPickButtonCaption(String caption) {
		mPickButton.setCaption(caption);
	}

	/// The current caption of the \ref nanogui::ColorPicker::mResetButton.
	public final String resetButtonCaption() {
		return mResetButton.caption();
	}

	/// Sets the current caption of the \ref nanogui::ColorPicker::mResetButton.
	public final void setResetButtonCaption(String caption) {
		mResetButton.setCaption(caption);
	}

}