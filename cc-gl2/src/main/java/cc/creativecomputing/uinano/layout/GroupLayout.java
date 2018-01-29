package cc.creativecomputing.uinano.layout;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.uinano.CCUILabel;
import cc.creativecomputing.uinano.CCWidget;
import cc.creativecomputing.uinano.Window;

/**
 * \class GroupLayout layout.h nanogui/layout.h
 *
 * \brief Special layout for widgets grouped by labels.
 *
 * This widget resembles a box layout in that it arranges a set of widgets
 * vertically. All widgets are indented on the horizontal axis except for \ref
 * Label widgets, which are not indented.
 *
 * This creates a pleasing layout where a number of widgets are grouped under
 * some high-level heading.
 */
public class GroupLayout extends Layout {
	/// The margin of this GroupLayout.
	protected int mMargin;

	/// The spacing between widgets of this GroupLayout.
	protected int mSpacing;

	/// The spacing between groups of this GroupLayout.
	protected int mGroupSpacing;

	/// The indent amount of a group under its defining Label of this
	/// GroupLayout.
	protected int mGroupIndent;

	/**
	 * Creates a GroupLayout.
	 *
	 * \param margin The margin around the widgets added.
	 *
	 * \param spacing The spacing between widgets added.
	 *
	 * \param groupSpacing The spacing between groups (groups are defined by
	 * each Label added).
	 *
	 * \param groupIndent The amount to indent widgets in a group (underneath a
	 * Label).
	 */
	public GroupLayout(int margin, int spacing, int groupSpacing) {
		this(margin, spacing, groupSpacing, 20);
	}

	public GroupLayout(int margin, int spacing) {
		this(margin, spacing, 14, 20);
	}

	public GroupLayout(int margin) {
		this(margin, 6, 14, 20);
	}

	public GroupLayout() {
		this(15, 6, 14, 20);
	}

	public GroupLayout(int margin, int spacing, int groupSpacing, int groupIndent) {
		this.mMargin = margin;
		this.mSpacing = spacing;
		this.mGroupSpacing = groupSpacing;
		this.mGroupIndent = groupIndent;
	}

	/// The margin of this GroupLayout.
	public final int margin() {
		return mMargin;
	}

	/// Sets the margin of this GroupLayout.
	public final void setMargin(int margin) {
		mMargin = margin;
	}

	/// The spacing between widgets of this GroupLayout.
	public final int spacing() {
		return mSpacing;
	}

	/// Sets the spacing between widgets of this GroupLayout.
	public final void setSpacing(int spacing) {
		mSpacing = spacing;
	}

	/// The indent of widgets in a group (underneath a Label) of this
	/// GroupLayout.
	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: int groupIndent() const
	public final int groupIndent() {
		return mGroupIndent;
	}

	/// Sets the indent of widgets in a group (underneath a Label) of this
	/// GroupLayout.
	public final void setGroupIndent(int groupIndent) {
		mGroupIndent = groupIndent;
	}

	/// The spacing between groups of this GroupLayout.
	public final int groupSpacing() {
		return mGroupSpacing;
	}

	/// Sets the spacing between groups of this GroupLayout.
	public final void setGroupSpacing(int groupSpacing) {
		mGroupSpacing = groupSpacing;
	}

	/* Implementation of the layout interface */
	/// See \ref Layout::preferredSize.
	@Override
	public CCVector2 preferredSize(CCGraphics g, CCWidget widget) {
		int height = mMargin;
		double width = 2 * mMargin;

		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			height += widget.theme().mWindowHeaderHeight - mMargin / 2;
		}

		boolean first = true;
		boolean indent = false;
		for (CCWidget c : widget.children()) {
			if (!c.visible()) {
				continue;
			}
			CCUILabel label = (CCUILabel) ((c instanceof CCUILabel) ? c : null);
			if (!first) {
				height += (label == null) ? mSpacing : mGroupSpacing;
			}
			first = false;

			CCVector2 ps = c.preferredSize(g);
			CCVector2 fs = c.fixedSize();
			CCVector2 targetSize = new CCVector2(fs.x != 0 ? fs.x : ps.x, fs.y != 0 ? fs.y : ps.y);

			boolean indentCur = indent && label == null;
			height += targetSize.y;
			width = Math.max(width, targetSize.x + 2 * mMargin + (indentCur ? mGroupIndent : 0));

			if (label != null) {
				indent = !label.caption().isEmpty();
			}
		}
		height += mMargin;
		return new CCVector2(width, height);
	}

	/// See \ref Layout::performLayout.
	@Override
	public void performLayout(CCGraphics g, CCWidget widget) {
		int height = mMargin;
		double availableWidth = (widget.fixedWidth() != 0 ? widget.fixedWidth() : widget.width()) - 2 * mMargin;

		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			height += widget.theme().mWindowHeaderHeight - mMargin / 2;
		}

		boolean first = true;
		boolean indent = false;

		for (CCWidget c : widget.children()) {
			if (!c.visible()) {
				continue;
			}
			CCUILabel label = (CCUILabel) ((c instanceof CCUILabel) ? c : null);
			if (!first) {
				height += (label == null) ? mSpacing : mGroupSpacing;
			}
			first = false;

			boolean indentCur = indent && label == null;
			CCVector2 ps = new CCVector2(availableWidth - (indentCur ? mGroupIndent : 0), c.preferredSize(g).y);
			CCVector2 fs = c.fixedSize();

			CCVector2 targetSize = new CCVector2(fs.x != 0 ? fs.x : ps.x, fs.y != 0 ? fs.y : ps.y);

			c.position(new CCVector2(mMargin + (indentCur ? mGroupIndent : 0), height));
			c.size(targetSize);
			c.performLayout(g);

			height += targetSize.y;

			if (label != null) {
				indent = !label.caption().isEmpty();
			}
		}
	}

}