package cc.creativecomputing.uinano;

import java.util.*;

import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.core.events.CCTriggerEvent;
import cc.creativecomputing.core.events.CCBooleanEvent;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

/**
 * [Normal/Toggle/Radio/Popup] Button widget.
 */
public class Button extends CCWidget {
	/// Flags to specify the button behavior (can be combined with binary OR)
	public enum Flags {
		NormalButton, /// < A normal Button.
		RadioButton, /// < A radio Button.
		PopupButton; /// < A popup Button.
	}

	/**
	 * The available icon positions. 
	 * @author christianr
	 *
	 */
	public enum IconPosition {
		/**
		 * Button icon on the far left.
		 */
		Left, /// < 
		/**
		 * Button icon on the left, centered (depends on caption text length
		 */
		LeftCentered, 
		/**
		 * Button icon on the right, centered (depends on caption text length).
		 */
		RightCentered, 
		/**
		 * Button icon on the far right.
		 */
		Right; 

		
	}

	/// The caption of this Button.
	protected String mCaption;

	/**
	 * The icon of this Button (``0`` means no icon).
	 *
	 * 
	 * The icon to display with this Button. If not ``0``, may either be a
	 * picture icon, or one of the icons enumerated in
	 * :ref:`file_nanogui_entypo.h`. The kind of icon (image or Entypo) is
	 * determined by the functions :func:`nanogui::nvgIsImageIcon` and its
	 * reciprocal counterpart :func:`nanogui::nvgIsFontIcon`.
	 * 
	 */
	protected int mIcon;
	
	public int index = 0;

	/// The position to draw the icon at.
	protected IconPosition mIconPosition;

	/// Whether or not this Button is currently pushed.
	protected boolean mPushed;

	/// The current flags of this button (see \ref nanogui::Button::Flags for
	/// options).
	protected Flags mFlags;

	/// The background color of this Button.
	protected CCColor mBackgroundColor = new CCColor();

	/// The color of the caption text of this Button.
	protected CCColor mTextColor = new CCColor();

	/// The callback issued for all types of buttons.
	public CCListenerManager<CCTriggerEvent> mCallback = CCListenerManager.create(CCTriggerEvent.class);

	/// The callback issued for toggle buttons.
	protected CCListenerManager<CCBooleanEvent> mChangeCallback = CCListenerManager.create(CCBooleanEvent.class);

	/// The button group for radio buttons.
	protected ArrayList<Button> mButtonGroup = new ArrayList<Button>();
	
	public boolean mToggle = false;

	/**
	 * \brief Creates a button attached to the specified parent.
	 *
	 * \param parent The \ref nanogui::Widget this Button will be attached to.
	 *
	 * \param caption The name of the button (default ``"Untitled"``).
	 *
	 * \param icon The icon to display with this Button. See \ref
	 * nanogui::Button::mIcon.
	 */
	public Button(CCWidget parent, String caption) {
		this(parent, caption, 0);
	}

	public Button(CCWidget parent) {
		this(parent, "Untitled", 0);
	}

	// C++ TO JAVA CONVERTER NOTE: Java does not allow default values for
	// parameters. Overloaded methods are inserted above:
	// ORIGINAL LINE: Button(Widget *parent, const String &caption = "Untitled",
	// int icon = 0) : Widget(parent), mCaption(caption), mIcon(icon),
	// mIconPosition(IconPosition::LeftCentered), mPushed(false),
	// mFlags(NormalButton), mBackgroundColor(CCColor(0, 0)),
	// mTextColor(CCColor(0, 0))
	public Button(CCWidget parent, String caption, int icon) {
		super(parent);
		this.mCaption = caption;
		this.mIcon = icon;
		this.mIconPosition = IconPosition.LeftCentered;
		this.mPushed = false;
		this.mFlags = Flags.NormalButton;
		this.mBackgroundColor = new CCColor(0, 0);
		this.mTextColor = new CCColor(0, 0);
	}

	/// Returns the caption of this Button.
	public final String caption() {
		return mCaption;
	}

	/// Sets the caption of this Button.
	public final void setCaption(String caption) {
		mCaption = caption;
	}

	/// Returns the background color of this Button.
	public final CCColor backgroundColor() {
		return mBackgroundColor;
	}

	/// Sets the background color of this Button.
	public final void setBackgroundColor(CCColor backgroundCCColor) {
		mBackgroundColor = backgroundCCColor;
	}

	/// Returns the text color of the caption of this Button.
	public final CCColor textColor() {
		return mTextColor;
	}

	/// Sets the text color of the caption of this Button.
	public final void setTextColor(CCColor textCCColor) {
		mTextColor = textCCColor;
	}

	/// Returns the icon of this Button. See \ref nanogui::Button::mIcon.
	public final int icon() {
		return mIcon;
	}

	/// Sets the icon of this Button. See \ref nanogui::Button::mIcon.
	public final void setIcon(int icon) {
		mIcon = icon;
	}

	/// The current flags of this Button (see \ref nanogui::Button::Flags for
	/// options).
	public final Flags flags() {
		return mFlags;
	}

	/// Sets the flags of this Button (see \ref nanogui::Button::Flags for
	/// options).
	public final void setFlags(Flags buttonFlags) {
		mFlags = buttonFlags;
	}

	/// The position of the icon for this Button.
	public final IconPosition iconPosition() {
		return mIconPosition;
	}

	/// Sets the position of the icon for this Button.
	public final void setIconPosition(IconPosition iconPosition) {
		mIconPosition = iconPosition;
	}

	/// Whether or not this Button is currently pushed.
	public final boolean pushed() {
		return mPushed;
	}

	/// Sets whether or not this Button is currently pushed.
	public final void setPushed(boolean pushed) {
		mPushed = pushed;
	}

	/// Set the button group (for radio buttons).
	public final void setButtonGroup(ArrayList<Button> buttonGroup) {
		mButtonGroup = buttonGroup;
	}

	/// The current button group (for radio buttons).
	public final ArrayList<Button> buttonGroup() {
		return mButtonGroup;
	}

	/// The preferred size of this Button.
	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		int fontSize = _myFontSize == -1 ? _myTheme.mButtonFontSize : _myFontSize;
		ctx.fontSize(fontSize);
		ctx.fontFace("sans-bold");
		float tw = ctx.textBounds(0, 0, mCaption);
		double iw = 0.0f;
		float ih = fontSize;

		if (mIcon > 0) {
			if (nvgIsFontIcon(mIcon)) {
				ih *= icon_scale();
				ctx.fontFace("icons");
				ctx.fontSize(ih);
				iw = ctx.textBounds(0, 0, utf8(mIcon)) + _mySize.y * 0.15f;
			} else {
				ih *= 0.9f;
				CCVector2i imageSize = ctx.imageSize(mIcon);
				iw = imageSize.x * ih / imageSize.y;
			}
		}
		return new CCVector2i((int) (tw + iw) + 20, fontSize + 10);
	}
	
	private void push(boolean thePush){
		mPushed = thePush;
		mChangeCallback.proxy().event(thePush);
	}

	/// The callback that is called when any type of mouse button event is
	/// issued to this Button.
	@Override
	public boolean mouseButtonEvent(CCVector2 p, CCGLMouseEvent theMouseEvent) {
		super.mouseButtonEvent(p, theMouseEvent);

		if(!_myIsEnabled)return false;
		if (theMouseEvent.button != CCGLMouseButton.BUTTON_1) return false;
		
		boolean pushedBackup = mPushed;
		if (theMouseEvent.action == CCGLAction.PRESS) {
			switch (mFlags) {
			case RadioButton:
				if (mButtonGroup.isEmpty()) {
					for (CCWidget widget : parent().children()) {
						Button b = (Button) ((widget instanceof Button) ? widget : null);
						if (b != this && b != null && (b.flags() == Flags.RadioButton) && b.mPushed) {
							b.push(false);
						}
					}
				} else {
					for (Button b : mButtonGroup) {
						if (b != this && (b.flags() == Flags.RadioButton) && b.mPushed) {
							b.push(false);
						}
					}
					}
				break;
			case PopupButton:
				for (CCWidget widget : parent().children()) {
					Button b = (Button) ((widget instanceof Button) ? widget : null);
					if (b != this && b != null && (b.flags() == Flags.PopupButton) && b.mPushed) {
						b.push(false);
					}
				}
				break;
			
			}
			if(mToggle){
				mPushed = !mPushed;
			}else{
				mPushed = true;
			}
		} else if (mPushed) {
			if (contains(p)) {
				mCallback.proxy().event();
			}
			if ((mFlags == Flags.NormalButton)) {
				mPushed = false;
			}
		}
		if (pushedBackup != mPushed) {
			mChangeCallback.proxy().event(mPushed);
		}

		return true;
	}

	/// Responsible for drawing the Button.
	@Override
	public void draw(NanoVG ctx) {
		super.draw(ctx);

		CCColor gradTop = _myTheme.mButtonGradientTopUnfocused;
		CCColor gradBot = _myTheme.mButtonGradientBotUnfocused;

		if (mPushed) {
			gradTop = _myTheme.mButtonGradientTopPushed;
			gradBot = _myTheme.mButtonGradientBotPushed;
		} else if (_myMouseFocus && _myIsEnabled) {
			gradTop = _myTheme.mButtonGradientTopFocused;
			gradBot = _myTheme.mButtonGradientBotFocused;
		}

		ctx.beginPath();

		ctx.roundedRect(_myPosition.x + 1, _myPosition.y + 1.0f, _mySize.x - 2, _mySize.y - 2, _myTheme.mButtonCornerRadius - 1);

		if (mBackgroundColor.a != 0) {
			ctx.fillColor(new CCColor(mBackgroundColor.r, mBackgroundColor.g, mBackgroundColor.b, 1.0f));
			ctx.fill();
			if (mPushed) {
				gradTop.a = gradBot.a = 0.8f;
			} else {
				double v = 1 - mBackgroundColor.a;
				gradTop.a = gradBot.a = _myIsEnabled ? v : v * .5f + .5f;
			}
		}

		NVGPaint bg = ctx.linearGradient(_myPosition.x, _myPosition.y, _myPosition.x, _myPosition.y + _mySize.y, gradTop, gradBot);

		ctx.fillPaint(bg);
		ctx.fill();

		ctx.beginPath();
		ctx.strokeWidth(1.0f);
		ctx.roundedRect(
			_myPosition.x + 0.5f, 
			_myPosition.y + (mPushed ? 0.5f : 1.5f), 
			_mySize.x - 1,
			_mySize.y - 1 - (mPushed ? 0.0f : 1.0f), 
			_myTheme.mButtonCornerRadius
		);
		ctx.strokeColor(_myTheme.mBorderLight);
		ctx.stroke();

		ctx.beginPath();
		ctx.roundedRect(_myPosition.x + 0.5f, _myPosition.y + 0.5f, _mySize.x - 1, _mySize.y - 2, _myTheme.mButtonCornerRadius);
		ctx.strokeColor(_myTheme.mBorderDark);
		ctx.stroke();

		int fontSize = _myFontSize == -1 ? _myTheme.mButtonFontSize : _myFontSize;
		ctx.fontSize(fontSize);
		ctx.fontFace("sans-bold");
		float tw = ctx.textBounds(0, 0, mCaption);

		CCVector2 center = new CCVector2(_myPosition.x + _mySize.x * 0.5, _myPosition.y + _mySize.y * 0.5);
		CCVector2 textPos = new CCVector2(center.x - tw * 0.5f, center.y - 1);
		CCColor textCCColor = mTextColor.a == 0 ? _myTheme.mTextColor : mTextColor;
		if (!_myIsEnabled) {
			textCCColor = _myTheme.mDisabledTextColor;
		}

		if (mIcon != 0) {
			String icon = utf8(mIcon);

			float iw;
			float ih = fontSize;
			if (nvgIsFontIcon(mIcon)) {
				ih *= icon_scale();
				ctx.fontSize(ih);
				ctx.fontFace("icons");
				iw = ctx.textBounds(0, 0, icon);
			} else {
				ih *= 0.9f;
				CCVector2i imageSize = ctx.imageSize(mIcon);
				iw = imageSize.x * ih / imageSize.y;
			}
			if (!mCaption.equals("")) {
				iw += _mySize.y * 0.15f;
			}
			ctx.fillColor(textCCColor);
			ctx.textAlign(NanoVG.ALIGN_LEFT | NanoVG.ALIGN_MIDDLE);

			CCVector2 iconPos = new CCVector2(center);
			iconPos.y -= 1;

			if (mIconPosition == IconPosition.LeftCentered) {
				iconPos.x -= (tw + iw) * 0.5f;
				textPos.x += iw * 0.5f;
			} else if (mIconPosition == IconPosition.RightCentered) {
				textPos.x -= iw * 0.5f;
				iconPos.x += tw * 0.5f;
			} else if (mIconPosition == IconPosition.Left) {
				iconPos.x = _myPosition.x + 8;
			} else if (mIconPosition == IconPosition.Right) {
				iconPos.x = _myPosition.x + _mySize.x - iw - 8;
			}

			if (nvgIsFontIcon(mIcon)) {
				ctx.text(iconPos.x, iconPos.y + 1, icon);
			} else {
				NVGPaint imgPaint = ctx.imagePattern(iconPos.x, iconPos.y - ih / 2, iw, ih, 0, mIcon, _myIsEnabled ? 0.5f : 0.25f);
				ctx.fillPaint(imgPaint);
				ctx.fill();
			}
		}

		ctx.fontSize(fontSize);
		ctx.fontFace("sans-bold");
		ctx.textAlign(NanoVG.ALIGN_LEFT | NanoVG.ALIGN_MIDDLE);
		ctx.fillColor(_myTheme.mTextColorShadow);
		ctx.text(textPos.x, textPos.y, mCaption);
		ctx.fillColor(textCCColor);
		ctx.text(textPos.x, textPos.y + 1, mCaption);
	}

	/// Saves the state of this Button provided the given Serializer.
	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("caption", mCaption);
		s.addAttribute("icon", mIcon);
		s.addAttribute("iconPosition", mIconPosition.toString());
		s.addAttribute("pushed", mPushed);
		s.addAttribute("flags", mFlags.toString());
		s.add("backgroundCCColor", mBackgroundColor);
		s.add("textCCColor", mTextColor);
	}

	/// Sets the state of this Button provided the given Serializer.
	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try{
			mCaption = s.attribute("caption");
			mIcon = s.intAttribute("icon");
			mIconPosition = IconPosition.valueOf(s.attribute("iconPosition"));
			mPushed = s.booleanAttribute("pushed");
			mFlags = Flags.valueOf(s.attribute("flags"));
			mBackgroundColor = s.color("backgroundCCColor");
			mTextColor = s.color("textCCColor");
		}catch(Exception e){
			return false;
		}
		return true;
	}

}