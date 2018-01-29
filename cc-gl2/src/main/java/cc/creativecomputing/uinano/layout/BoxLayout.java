package cc.creativecomputing.uinano.layout;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.uinano.CCWidget;
import cc.creativecomputing.uinano.Orientation;
import cc.creativecomputing.uinano.Window;

/**
 * Simple horizontal/vertical box layout
 *
 * This widget stacks up a bunch of widgets horizontally or vertically. It adds
 * margins around the entire container and a custom spacing between adjacent
 * widgets.
 */
public class BoxLayout extends Layout {

	/// The Orientation of this BoxLayout.
	protected Orientation mOrientation;

	/// The Alignment of this BoxLayout.
	protected Alignment mAlignment;

	/// The margin of this BoxLayout.
	protected int mMargin;

	/// The spacing between widgets of this BoxLayout.
	protected int mSpacing;

	/**
	 * Construct a box layout which packs widgets in the given \c Orientation
	 *
	 * @param orientation The Orientation this BoxLayout expands along
	 *
	 * @param alignment Widget alignment perpendicular to the chosen orientation
	 *
	 * @param margin Margin around the layout container
	 *
	 * @param spacing Extra spacing placed between widgets
	 */
	public BoxLayout(Orientation orientation, Alignment alignment, int margin) {
		this(orientation, alignment, margin, 0);
	}

	public BoxLayout(Orientation orientation, Alignment alignment) {
		this(orientation, alignment, 0, 0);
	}

	public BoxLayout(Orientation orientation) {
		this(orientation, Alignment.Middle, 0, 0);
	}

	public BoxLayout(Orientation orientation, Alignment alignment, int margin, int spacing) {
		this.mOrientation = orientation;
		this.mAlignment = alignment;
		this.mMargin = margin;
		this.mSpacing = spacing;
	}

	/// The Orientation this BoxLayout is using.
	public final Orientation orientation() {
		return mOrientation;
	}

	/// Sets the Orientation of this BoxLayout.
	public final void setOrientation(Orientation orientation) {
		mOrientation = orientation;
	}

	/// The Alignment of this BoxLayout.
	public final Alignment alignment() {
		return mAlignment;
	}

	/// Sets the Alignment of this BoxLayout.
	public final void setAlignment(Alignment alignment) {
		mAlignment = alignment;
	}

	/// The margin of this BoxLayout.
	public final int margin() {
		return mMargin;
	}

	/// Sets the margin of this BoxLayout.
	public final void setMargin(int margin) {
		mMargin = margin;
	}

	/// The spacing this BoxLayout is using to pad in between widgets.
	public final int spacing() {
		return mSpacing;
	}

	/// Sets the spacing of this BoxLayout.
	public final void setSpacing(int spacing) {
		mSpacing = spacing;
	}

	/* Implementation of the layout interface */
	@Override
	public CCVector2i preferredSize(NanoVG ctx, CCWidget widget) {
		CCVector2i size = new CCVector2i(2 * mMargin);

		int yOffset = 0;
		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			if (mOrientation == Orientation.Vertical) {
				size.y += widget.theme().mWindowHeaderHeight - mMargin / 2;
			} else {
				yOffset = widget.theme().mWindowHeaderHeight;
			}
		}

		boolean first = true;
		int axis1 = mOrientation.ordinal();
		int axis2 = (mOrientation.ordinal() + 1) % 2;
		for (CCWidget w : widget.children()) {
			if (!w.visible()) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				size.set(axis1, size.get(axis1) + mSpacing);
			}

			CCVector2i ps = w.preferredSize(ctx);
			CCVector2i fs = w.fixedSize();
			CCVector2i targetSize = new CCVector2i(fs.x != 0 ? fs.x : ps.x, fs.y != 0 ? fs.y : ps.y);

			size.set(axis1, size.get(axis1) + targetSize.get(axis1));
			size.set(axis2, Math.max(size.get(axis2), targetSize.get(axis2) + 2 * mMargin));
			first = false;
		}
		return size.add(new CCVector2i(0, yOffset));
	}

	@Override
	public void performLayout(NanoVG ctx, CCWidget widget) {
		CCVector2i fs_w = widget.fixedSize();
		CCVector2i containerSize = new CCVector2i(fs_w.x != 0 ? fs_w.x : widget.width(),
				fs_w.y != 0 ? fs_w.y : widget.height());

		int axis1 = mOrientation.ordinal();
		int axis2 = (mOrientation.ordinal() + 1) % 2;
		int position = mMargin;
		int yOffset = 0;

		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			if (mOrientation == Orientation.Vertical) {
				position += widget.theme().mWindowHeaderHeight - mMargin / 2;
			} else {
				yOffset = widget.theme().mWindowHeaderHeight;
				containerSize.y -= yOffset;
			}
		}

		boolean first = true;
		for (CCWidget w : widget.children()) {
			if (!w.visible()) {
				continue;
			}
			if (first) {
				first = false;
			} else {
				position += mSpacing;
			}

			CCVector2i ps = w.preferredSize(ctx);
			CCVector2i fs = w.fixedSize();
			CCVector2i targetSize = new CCVector2i(fs.x != 0 ? fs.x : ps.x, fs.y != 0 ? fs.y : ps.y);
			CCVector2i pos = new CCVector2i(0, yOffset);

			pos.set(axis1, position);

			switch (mAlignment) {
			case Minimum:
				pos.add(axis2, mMargin);
				break;
			case Middle:
				pos.add(axis2, (containerSize.get(axis2) - targetSize.get(axis2)) / 2);
				break;
			case Maximum:
				pos.add(axis2, containerSize.get(axis2) - targetSize.get(axis2) - mMargin * 2);
				break;
			case Fill:
				pos.add(axis2, mMargin);
				targetSize.set(axis2, fs.get(axis2) != 0 ? fs.get(axis2) : (containerSize.get(axis2) - mMargin * 2));
				break;
			}

			w.position(pos);
			w.size(targetSize);
			w.performLayout(ctx);
			position += targetSize.get(axis1);
		}
	}
}