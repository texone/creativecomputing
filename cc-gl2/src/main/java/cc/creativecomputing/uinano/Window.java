package cc.creativecomputing.uinano;

import java.util.*;

import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.gl.app.CCGLAction;
import cc.creativecomputing.gl.app.CCGLMouseButton;
import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.gl.nanovg.NanoVG.NVGwinding;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.uinano.layout.Alignment;
import cc.creativecomputing.uinano.layout.BoxLayout;

/**
 * Top-level window widget.
 */
public class Window extends CCWidget {

	protected String mTitle;
	protected CCWidget mButtonPanel;
	protected boolean mModal;
	protected boolean mDrag;

	public Window(CCWidget parent, String title) {
		super(parent);
		mTitle = title;
		mButtonPanel = null;
		mModal = false;
		mDrag = false;
	}

	/// Return the window title
	public final String title() {
		return mTitle;
	}

	/// Set the window title
	public final void setTitle(String title) {
		mTitle = title;
	}

	/// Is this a model dialog?
	public final boolean modal() {
		return mModal;
	}

	/// Set whether or not this is a modal dialog
	public final void setModal(boolean modal) {
		mModal = modal;
	}

	/// Return the panel used to house window buttons
	public CCWidget buttonPanel() {
		if (mButtonPanel == null) {
			mButtonPanel = new CCWidget(this);
			mButtonPanel.setLayout(new BoxLayout(Orientation.Horizontal, Alignment.Middle, 0, 4));
		}
		return mButtonPanel;
	}

	/// Dispose the window
	public void dispose() {
		CCWidget widget = this;
		while (widget.parent() != null)
			widget = widget.parent();
		((Screen) widget).disposeWindow(this);
	}

	/// Center the window in the current \ref Screen
	public void center() {
		CCWidget widget = this;
		while (widget.parent() != null)
			widget = widget.parent();
		((Screen) widget).centerWindow(this);
	}

	/// Draw the window
	@Override
	public void draw(NanoVG ctx) {
		int ds = _myTheme.mWindowDropShadowSize, cr = _myTheme.mWindowCornerRadius;
		int hh = _myTheme.mWindowHeaderHeight;

		/* Draw window */
		ctx.save();
		ctx.beginPath();
		ctx.roundedRect(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y, cr);

		ctx.fillColor(_myMouseFocus ? _myTheme.mWindowFillFocused : _myTheme.mWindowFillUnfocused);
		ctx.fill();

		/* Draw a drop shadow */
		NVGPaint shadowPaint = ctx.boxGradient(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y, cr * 2, ds * 2, _myTheme.mDropShadow,
				_myTheme.mTransparent);

		ctx.save();
		ctx.resetScissor();
		ctx.beginPath();
		ctx.rect(_myPosition.x - ds, _myPosition.y - ds, _mySize.x + 2 * ds, _mySize.y + 2 * ds);
		ctx.roundedRect(_myPosition.x, _myPosition.y, _mySize.x, _mySize.y, cr);
		ctx.pathWinding(NVGwinding.CW);
		ctx.fillPaint(shadowPaint);
		ctx.fill();
		ctx.restore();

		if (!mTitle.isEmpty()) {
			/* Draw header */
			NVGPaint headerPaint = ctx.linearGradient(_myPosition.x, _myPosition.y, _myPosition.x, _myPosition.y + hh,
					_myTheme.mWindowHeaderGradientTop, _myTheme.mWindowHeaderGradientBot);

			ctx.beginPath();
			ctx.roundedRect(_myPosition.x, _myPosition.y, _mySize.x, hh, cr);

			ctx.fillPaint(headerPaint);
			ctx.fill();

			ctx.beginPath();
			ctx.roundedRect(_myPosition.x, _myPosition.y, _mySize.x, hh, cr);
			ctx.strokeColor(_myTheme.mWindowHeaderSepTop);

			ctx.save();
			ctx.intersectScissor(_myPosition.x, _myPosition.y, _mySize.x, 0.5f);
			ctx.stroke();
			ctx.restore();

			ctx.beginPath();
			ctx.moveTo(_myPosition.x + 0.5f, _myPosition.y + hh - 1.5f);
			ctx.lineTo(_myPosition.x + _mySize.x - 0.5f, _myPosition.y + hh - 1.5);
			ctx.strokeColor(_myTheme.mWindowHeaderSepBot);
			ctx.stroke();

			ctx.fontSize(18.0f);
			ctx.fontFace("sans-bold");
			ctx.textAlign(NanoVG.ALIGN_CENTER | NanoVG.ALIGN_MIDDLE);

			ctx.fontBlur(2);
			ctx.fillColor(_myTheme.mDropShadow);
			ctx.text(_myPosition.x + _mySize.x / 2, _myPosition.y + hh / 2, mTitle);

			ctx.fontBlur(0);
			ctx.fillColor(_myIsFocused ? _myTheme.mWindowTitleFocused : _myTheme.mWindowTitleUnfocused);
			ctx.text(_myPosition.x + _mySize.x / 2, _myPosition.y + hh / 2 - 1, mTitle);
		}

		ctx.restore();
		super.draw(ctx);
	}

	/// Handle window drag events
	@Override
	public boolean mouseDragEvent(CCVector2i p, CCVector2i rel, CCGLMouseEvent theEvent) {
		if (mDrag && (theEvent.button == CCGLMouseButton.BUTTON_1)) {
			_myPosition.addLocal(rel);
			_myPosition = _myPosition.cwiseMax(new CCVector2i());
			_myPosition = _myPosition.cwiseMin(parent().size().subtract(_mySize));
			return true;
		}
		return false;
	}

	/// Handle mouse events recursively and bring the current window to the top
	@Override
	public boolean mouseButtonEvent(CCVector2i p, CCGLMouseEvent theEvent) {
		if (super.mouseButtonEvent(p, theEvent)) {
			return true;
		}
		if (theEvent.button == CCGLMouseButton.BUTTON_1) {
			mDrag = theEvent.action == CCGLAction.PRESS && (p.y - _myPosition.y) < _myTheme.mWindowHeaderHeight;
			return true;
		}
		return false;
	}

	/// Accept scroll events and propagate them to the widget under the mouse
	/// cursor
	@Override
	public boolean scrollEvent(CCVector2i p, CCVector2 rel) {
		super.scrollEvent(p, rel);
		return true;
	}

	/// Compute the preferred size of the widget
	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		if (mButtonPanel != null) {
			mButtonPanel.setVisible(false);
		}
		CCVector2i result = super.preferredSize(ctx);
		if (mButtonPanel != null) {
			mButtonPanel.setVisible(true);
		}

		ctx.fontSize(18.0f);
		ctx.fontFace("sans-bold");
		float[] bounds = new float[4];
		ctx.textBounds(0, 0, mTitle, bounds);

		return result.cwiseMax(new CCVector2i((int) (bounds[2] - bounds[0] + 20), (int) (bounds[3] - bounds[1])));
	}

	/// Invoke the associated layout generator to properly place child widgets,
	/// if any
	@Override
	public void performLayout(NanoVG ctx) {
		if (mButtonPanel == null) {
			super.performLayout(ctx);
		} else {
			mButtonPanel.setVisible(false);
			super.performLayout(ctx);
			for (CCWidget w : mButtonPanel.children()) {
				w.setFixedSize(new CCVector2i(22, 22));
				w.setFontSize(15);
			}
			mButtonPanel.setVisible(true);
			mButtonPanel.size(new CCVector2i(width(), 22));
			mButtonPanel.position(new CCVector2i(width() - (mButtonPanel.preferredSize(ctx).x + 5), 3));
			mButtonPanel.performLayout(ctx);
		}
	}

	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("title", mTitle);
		s.addAttribute("modal", mModal);
	}

	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try {
			mTitle = s.attribute("title");
			mModal = s.booleanAttribute("modal");
		} catch (Exception e) {
			return false;
		}

		mDrag = false;
		return true;
	}

	/// Internal helper function to maintain nested window position values;
	/// overridden in \ref Popup
	/// Internal helper function to maintain nested window position values;
	/// overridden in \ref Popup
	protected void refreshRelativePlacement() {
		/* Overridden in \ref Popup */
	}
}