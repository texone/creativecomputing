package cc.creativecomputing.uinano;


import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.gl.nanovg.NanoVG.NVGwinding;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class Popup popup.h nanogui/popup.h
 *
 * \brief Popup window for combo boxes, popup buttons, nested dialogs etc.
 *
 * Usually the Popup instance is constructed by another widget (e.g. \ref
 * PopupButton) and does not need to be created by hand.
 */
public class Popup extends Window {
	public enum Side {
		Left, Right;
	}

	/// Create a new popup parented to a screen (first argument) and a parent
	/// window
	public Popup(CCWidget parent, Window parentWindow) {
		super(parent, "");
		this.mParentWindow = parentWindow;
		this.mAnchorPos = new CCVector2i();
		this.mAnchorHeight = 30;
		this.mSide = Side.Right;
	}

	/// Return the anchor position in the parent window; the placement of the
	/// popup is relative to it
	public final void setAnchorPos(CCVector2i anchorPos) {
		mAnchorPos = anchorPos;
	}

	/// Set the anchor position in the parent window; the placement of the popup
	/// is relative to it
	public final CCVector2i anchorPos() {
		return mAnchorPos;
	}

	/// Set the anchor height; this determines the vertical shift relative to
	/// the anchor position
	public final void setAnchorHeight(int anchorHeight) {
		mAnchorHeight = anchorHeight;
	}

	/// Return the anchor height; this determines the vertical shift relative to
	/// the anchor position
	public final int anchorHeight() {
		return mAnchorHeight;
	}

	/// Set the side of the parent window at which popup will appear
	public final void setSide(Side popupSide) {
		mSide = popupSide;
	}

	/// Return the side of the parent window at which popup will appear
	public final Side side() {
		return mSide;
	}

	/// Return the parent window of the popup
	public final Window parentWindow() {
		return mParentWindow;
	}

	/// Invoke the associated layout generator to properly place child widgets,
	/// if any
	@Override
	public void performLayout(NanoVG ctx) {
		if (_myLayout != null || _myChildren.size() != 1) {
			super.performLayout(ctx);
		} else {
			_myChildren.get(0).position(new CCVector2i());
			_myChildren.get(0).size(_mySize);
			_myChildren.get(0).performLayout(ctx);
		}
		if (mSide == Side.Left) {
			mAnchorPos.x -= size().x;
		}
	}

	/// Draw the popup window
	@Override
	public void draw(NanoVG ctx) {
		refreshRelativePlacement();

		if (!_myIsVisible) {
			return;
		}

		int ds = _myTheme.mWindowDropShadowSize;
		int cr = _myTheme.mWindowCornerRadius;

		ctx.save();
		ctx.resetScissor();

		/* Draw a drop shadow */
		NVGPaint shadowPaint = ctx.boxGradient(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y, cr * 2, ds * 2, _myTheme.mDropShadow,
				_myTheme.mTransparent);

		ctx.beginPath();
		ctx.rect(_myPosition.x - ds, _myPosition.y - ds, _mySize.x + 2 * ds, _mySize.y + 2 * ds);
		ctx.roundedRect(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y, cr);
		ctx.pathWinding(NVGwinding.CW);
		ctx.fillPaint(shadowPaint);
		ctx.fill();

		/* Draw window */
		ctx.beginPath();
		ctx.roundedRect(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y, cr);

		CCVector2i base = _myPosition.add(new CCVector2i(0, mAnchorHeight));
		int sign = -1;
		if (mSide == Side.Left) {
			base.x += _mySize.x;
			sign = 1;
		}

		ctx.moveTo(base.x + 15 * sign, base.y);
		ctx.lineTo(base.x - 1 * sign, base.y - 15);
		ctx.lineTo(base.x - 1 * sign, base.y + 15);

		ctx.fillColor(_myTheme.mWindowPopup);
		ctx.fill();
		ctx.restore();

		super.draw(ctx);
	}

	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.add("anchorPos", mAnchorPos);
		s.addAttribute("anchorHeight", mAnchorHeight);
		s.addAttribute("side", mSide.toString());
	}

	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try{
			mAnchorPos = s.vector2i("anchorPos");
			mAnchorHeight = s.intAttribute("anchorHeight");
			mSide = Side.valueOf(s.attribute("side"));
		}catch(Exception e){
			return false;
		}

		return true;
	}

	/// Internal helper function to maintain nested window position values
	@Override
	protected void refreshRelativePlacement() {
		mParentWindow.refreshRelativePlacement();
		_myIsVisible &= mParentWindow.visibleRecursive();
		_myPosition = mParentWindow.position() .add( mAnchorPos.subtract(new CCVector2i(0, mAnchorHeight)));
	}

	protected Window mParentWindow;
	protected CCVector2i mAnchorPos = new CCVector2i();
	protected int mAnchorHeight;
	protected Side mSide;
}