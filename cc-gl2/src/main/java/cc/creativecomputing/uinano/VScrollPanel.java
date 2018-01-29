package cc.creativecomputing.uinano;

import org.lwjgl.nanovg.NVGPaint;

import cc.creativecomputing.gl.app.CCGLMouseEvent;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class VScrollPanel vscrollpanel.h nanogui/vscrollpanel.h
 *
 * \brief Adds a vertical scrollbar around a widget that is too big to fit into
 * a certain area.
 */
public class VScrollPanel extends CCWidget {

	protected int mChildPreferredHeight;
	protected double mScroll;
	protected boolean mUpdateLayout;

	public VScrollPanel(CCWidget parent) {
		super(parent);
		this.mChildPreferredHeight = 0;
		this.mScroll = 0.0f;
		this.mUpdateLayout = false;
	}

	/// Return the current scroll amount as a value between 0 and 1. 0 means
	/// scrolled to the top and 1 to the bottom.
	public final double scroll() {
		return mScroll;
	}

	/// Set the scroll amount to a value between 0 and 1. 0 means scrolled to
	/// the top and 1 to the bottom.
	public final void setScroll(double scroll) {
		mScroll = scroll;
	}

	@Override
	public void performLayout(NanoVG ctx) {
		super.performLayout(ctx);

		if (_myChildren.isEmpty()) {
			return;
		}
		if (_myChildren.size() > 1) {
			throw new RuntimeException("VScrollPanel should have one child.");
		}

		CCWidget child = _myChildren.get(0);
		mChildPreferredHeight = child.preferredSize(ctx).y;

		if (mChildPreferredHeight > _mySize.y) {
			child.position(new CCVector2i(0, -mScroll * (mChildPreferredHeight - _mySize.y)));
			child.size(new CCVector2i(_mySize.x - 12, mChildPreferredHeight));
		} else {
			child.position(new CCVector2i());
			child.size(_mySize);
			mScroll = 0;
		}
		child.performLayout(ctx);
	}

	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		if (_myChildren.isEmpty()) {
			return new CCVector2i();
		}
		return _myChildren.get(0).preferredSize(ctx).add(new CCVector2i(12, 0));
	}

	@Override
	public boolean mouseDragEvent(CCVector2i p, CCVector2i rel, CCGLMouseEvent theEvent) {
		if (!_myChildren.isEmpty() && mChildPreferredHeight > _mySize.y) {
			double scrollh = height() * Math.min(1.0f, height() / (double) mChildPreferredHeight);

			mScroll = Math.max((double) 0.0f,
					Math.min((double) 1.0f, mScroll + rel.y / (double) (_mySize.y - 8 - scrollh)));
			mUpdateLayout = true;
			return true;
		} else {
			return super.mouseDragEvent(p, rel, theEvent);
		}
	}

	@Override
	public boolean scrollEvent(CCVector2i p, CCVector2 rel) {
		if (!_myChildren.isEmpty() && mChildPreferredHeight > _mySize.y) {
			double scrollAmount = rel.y * (_mySize.y / 20.0f);
			double scrollh = height() * Math.min(1.0f, height() / (double) mChildPreferredHeight);

			mScroll = Math.max((double) 0.0f,
					Math.min((double) 1.0f, mScroll - scrollAmount / (double) (_mySize.y - 8 - scrollh)));
			mUpdateLayout = true;
			return true;
		} else {
			return super.scrollEvent(p, rel);
		}
	}

	@Override
	public void draw(NanoVG ctx) {
		if (_myChildren.isEmpty()) {
			return;
		}
		CCWidget child = _myChildren.get(0);
		child.position(new CCVector2i(0, -mScroll * (mChildPreferredHeight - _mySize.y)));
		mChildPreferredHeight = child.preferredSize(ctx).y;
		double scrollh = height() * Math.min(1.0f, height() / (double) mChildPreferredHeight);

		if (mUpdateLayout) {
			child.performLayout(ctx);
		}

		ctx.save();
		ctx.translate(_myPosition.x, _myPosition.y);
		ctx.intersectScissor(0, 0, _mySize.x, _mySize.y);
		if (child.visible()) {
			child.draw(ctx);
		}
		ctx.restore();

		if (mChildPreferredHeight <= _mySize.y) {
			return;
		}

		NVGPaint paint = ctx.boxGradient(_myPosition.x + _mySize.x - 12 + 1, _myPosition.y + 4 + 1, 8, _mySize.y - 8, 3, 4,
				new CCColor(0, 32), new CCColor(0, 92));
		ctx.beginPath();
		ctx.roundedRect(_myPosition.x + _mySize.x - 12, _myPosition.y + 4, 8, _mySize.y - 8, 3);
		ctx.fillPaint(paint);
		ctx.fill();

		paint = ctx.boxGradient(_myPosition.x + _mySize.x - 12 - 1, _myPosition.y + 4 + (_mySize.y - 8 - scrollh) * mScroll - 1, 8,
				scrollh, 3, 4, new CCColor(220, 100), new CCColor(128, 100));

		ctx.beginPath();
		ctx.roundedRect(_myPosition.x + _mySize.x - 12 + 1, _myPosition.y + 4 + 1 + (_mySize.y - 8 - scrollh) * mScroll, 8 - 2,
				scrollh - 2, 2);
		ctx.fillPaint(paint);
		ctx.fill();
	}

	@Override
	public void save(CCDataElement s) {
		super.save(s);
		s.addAttribute("childPreferredHeight", mChildPreferredHeight);
		s.addAttribute("scroll", mScroll);
	}

	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}
		try {
			mChildPreferredHeight = s.intAttribute("childPreferredHeight");
			mScroll = s.doubleAttribute("scroll");
		} catch (Exception e) {
			return false;
		}
		return true;
	}
}