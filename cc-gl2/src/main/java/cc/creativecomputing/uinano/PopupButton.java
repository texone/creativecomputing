package cc.creativecomputing.uinano;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class PopupButton popupbutton.h nanogui/popupbutton.h
 *
 * \brief Button which launches a popup widget.
 *
 * \remark This class overrides \ref nanogui::Widget::mIconExtraScale to be
 * ``0.8f``, which affects all subclasses of this Widget. Subclasses must
 * explicitly set a different value if needed (e.g., in their constructor).
 */
public class PopupButton extends Button {

	protected Popup mPopup;
	protected int mChevronIcon;

	public PopupButton(CCWidget parent, String caption, int buttonIcon) {
		super(parent, caption, buttonIcon);

		mChevronIcon = _myTheme.mPopupChevronRightIcon.id;

		setFlags(Flags.PopupButton);
		mToggle = true;

		Window parentWindow = window();
		mPopup = new Popup(parentWindow.parent(), window());
		mPopup.size(new CCVector2i(320, 250));
		mPopup.setVisible(false);

		_myIconExtraScale = 0.8f;// widget override
	}

	public final void setChevronIcon(int icon) {
		mChevronIcon = icon;
	}

	public final int chevronIcon() {
		return mChevronIcon;
	}

	public void setSide(Popup.Side side) {
		if (mPopup.side() == Popup.Side.Right && mChevronIcon == _myTheme.mPopupChevronRightIcon.id) {
			setChevronIcon(_myTheme.mPopupChevronLeftIcon.id);
		} else if (mPopup.side() == Popup.Side.Left && mChevronIcon == _myTheme.mPopupChevronLeftIcon.id) {
			setChevronIcon(_myTheme.mPopupChevronRightIcon.id);
		}
		mPopup.setSide(side);
	}

	public final Popup.Side side() {
		return mPopup.side();
	}

	public final Popup popup() {
		return mPopup;
	}

	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		return super.preferredSize(ctx).add(new CCVector2i(15, 0));
	}

	public void draw(NanoVG ctx) {
		if (!_myIsEnabled && mPushed) {
			mPushed = false;
		}

		mPopup.setVisible(mPushed);
		super.draw(ctx);

		if (mChevronIcon != 0) {
			// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to
			// implicit typing in Java:
			String icon = utf8(mChevronIcon);
			CCColor textColor = mTextColor.a == 0 ? _myTheme.mTextColor : mTextColor;

			ctx.fontSize((_myFontSize < 0 ? _myTheme.mButtonFontSize : _myFontSize) * icon_scale());
			ctx.fontFace("icons");
			ctx.fillColor(_myIsEnabled ? textColor : _myTheme.mDisabledTextColor);
			ctx.textAlign(NanoVG.ALIGN_LEFT | NanoVG.ALIGN_MIDDLE);

			float iw = ctx.textBounds(0, 0, icon);
			CCVector2 iconPos = new CCVector2(0, _myPosition.y + _mySize.y * 0.5f - 1);

			if (mPopup.side() == Popup.Side.Right) {
				iconPos.x = _myPosition.x + _mySize.x - iw - 8;
			} else {
				iconPos.x = _myPosition.x + 8;
			}

			ctx.text(iconPos.x, iconPos.y, icon);
		}
	}

	public void performLayout(NanoVG ctx) {
		super.performLayout(ctx);

		Window parentWindow = window();

		int posY = absolutePosition().y - parentWindow.position().y + _mySize.y / 2;
		if (mPopup.side() == Popup.Side.Right) {
			mPopup.setAnchorPos(new CCVector2i(parentWindow.width() + 15, posY));
		} else {
			mPopup.setAnchorPos(new CCVector2i(0 - 15, posY));
		}
	}

	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("chevronIcon", mChevronIcon);
	}

	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try{
			mChevronIcon = s.intAttribute("chevronIcon");
		}catch(Exception e){
			return false;
		}
		return true;
	}
}