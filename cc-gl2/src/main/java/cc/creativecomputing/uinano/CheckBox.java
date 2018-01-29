package cc.creativecomputing.uinano;

import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.core.events.CCBooleanEvent;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2i;

/**
 *
 * \brief Two-state check box widget.
 *
 * \remark This class overrides \ref nanogui::Widget::mIconExtraScale to be
 * ``1.2f``, which affects all subclasses of this Widget. Subclasses must
 * explicitly set a different value if needed (e.g., in their constructor).
 */
public class CheckBox extends CCWidget {

	/// The caption text of this CheckBox.
	protected String mCaption;

	/**
	 * Internal tracking variable to distinguish between mouse click and
	 * release. \ref nanogui::CheckBox::mCallback is only called upon release.
	 * See \ref nanogui::CheckBox::mouseButtonEvent for specific conditions.
	 */
	protected boolean mPushed;

	/// Whether or not this CheckBox is currently checked or unchecked.
	protected boolean mChecked;

	/// The function to execute when \ref nanogui::CheckBox::mChecked is
	/// changed.
	protected CCListenerManager<CCBooleanEvent> mCallback = CCListenerManager.create(CCBooleanEvent.class);

	/**
	 * Adds a CheckBox to the specified ``parent``.
	 *
	 * \param parent The Widget to add this CheckBox to.
	 *
	 * \param caption The caption text of the CheckBox (default ``"Untitled"``).
	 *
	 */

	public CheckBox(CCWidget parent) {
		this(parent, "Untitled");
	}

	public CheckBox(CCWidget parent, String caption) {
		super(parent);
		this.mCaption = caption;
		this.mPushed = false;
		this.mChecked = false;

		_myIconExtraScale = 1.2f; // widget override
	}

	/// The caption of this CheckBox.
	public final String caption() {
		return mCaption;
	}

	/// Sets the caption of this CheckBox.
	public final void setCaption(String caption) {
		mCaption = caption;
	}

	/// Whether or not this CheckBox is currently checked.
	public final boolean checked() {
		return mChecked;
	}

	/// Sets whether or not this CheckBox is currently checked.
	public final void setChecked(boolean checked) {
		mChecked = checked;
	}

	/// Whether or not this CheckBox is currently pushed.
	public final boolean pushed() {
		return mPushed;
	}

	/// Sets whether or not this CheckBox is currently pushed.
	public final void setPushed(boolean pushed) {
		mPushed = pushed;
	}

	/**
	 * The mouse button callback will return ``true`` when all three conditions
	 * are met:
	 *
	 * 1. This CheckBox is "enabled" (see \ref nanogui::Widget::mEnabled). 2.
	 * ``p`` is inside this CheckBox. 3. ``button`` is ``GLFW_MOUSE_BUTTON_1``
	 * (left mouse click).
	 *
	 * Since a mouse button event is issued for both when the mouse is pressed,
	 * as well as released, this function sets \ref nanogui::CheckBox::mPushed
	 * to ``true`` when parameter ``down == true``. When the second event
	 * (``down == false``) is fired, \ref nanogui::CheckBox::mChecked is
	 * inverted and \ref nanogui::CheckBox::mCallback is called.
	 *
	 * That is, the callback provided is only called when the mouse button is
	 * released, **and** the click location remains within the CheckBox
	 * boundaries. If the user clicks on the CheckBox and releases away from the
	 * bounds of the CheckBox, \ref nanogui::CheckBox::mPushed is simply set
	 * back to ``false``.
	 */
	@Override
	public boolean mouseButtonEvent(CCVector2i p, CCGLMouseEvent theEvent) {
		super.mouseButtonEvent(p, theEvent);
		if (!_myIsEnabled) {
			return false;
		}

		if (theEvent.button == CCGLMouseButton.BUTTON_1) {
			if (theEvent.action == CCGLAction.PRESS) {
				mPushed = true;
			} else if (mPushed) {
				if (contains(p)) {
					mChecked = !mChecked;
					mCallback.proxy().event(mChecked);
				}
				mPushed = false;
			}
			return true;
		}
		return false;
	}

	/// The preferred size of this CheckBox.
	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		if (!_myFixedSize.isZero()) {
			return _myFixedSize;
		}
		ctx.fontSize(fontSize());
		ctx.fontFace("sans");
		return new CCVector2i((int) (ctx.textBounds(0, 0, mCaption) + 1.8f * fontSize()), (int) (fontSize() * 1.3f));
	}

	/// Draws this CheckBox.
	@Override
	public void draw(NanoVG ctx) {
		super.draw(ctx);

		ctx.fontSize(fontSize());
		ctx.fontFace("sans");
		ctx.fillColor(_myIsEnabled ? _myTheme.mTextColor : _myTheme.mDisabledTextColor);
		ctx.textAlign(NanoVG.ALIGN_LEFT | NanoVG.ALIGN_MIDDLE);
		ctx.text(_myPosition.x + 1.6f * fontSize(), _myPosition.y + _mySize.y * 0.5f, mCaption);

		NVGPaint bg = ctx.boxGradient(_myPosition.x + 1.5f, _myPosition.y + 1.5f, _mySize.y - 2.0f, _mySize.y - 2.0f, 3, 3,
				mPushed ? new CCColor(0, 100) : new CCColor(0, 32), new CCColor(0, 0, 0, 180));

		ctx.beginPath();
		ctx.roundedRect(_myPosition.x + 1.0f, _myPosition.y + 1.0f, _mySize.y - 2.0f, _mySize.y - 2.0f, 3);
		ctx.fillPaint(bg);
		ctx.fill();

		if (mChecked) {
			ctx.fontSize(_mySize.y * icon_scale());
			ctx.fontFace("icons");
			ctx.fillColor(_myIsEnabled ? _myTheme.mIconColor : _myTheme.mDisabledTextColor);
			ctx.textAlign(NanoVG.ALIGN_CENTER | NanoVG.ALIGN_MIDDLE);
			ctx.text(_myPosition.x + _mySize.y * 0.5f + 1, _myPosition.y + _mySize.y * 0.5f, utf8(_myTheme.mCheckBoxIcon.id));
		}
	}

	/// Saves this CheckBox to the specified Serializer.
	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("caption", mCaption);
		s.addAttribute("pushed", mPushed);
		s.addAttribute("checked", mChecked);
	}

	/// Loads the state of the specified Serializer to this CheckBox.
	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try {
			mCaption = s.attribute("caption");
			mPushed = s.booleanAttribute("pushed");
			mChecked = s.booleanAttribute("checked");
		} catch (Exception e) {
			return false;
		}

		return true;
	}

}