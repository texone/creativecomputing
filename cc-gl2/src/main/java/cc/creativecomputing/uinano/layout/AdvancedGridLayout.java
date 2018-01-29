package cc.creativecomputing.uinano.layout;

import java.util.*;
import java.util.Map.Entry;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.uinano.CCWidget;
import cc.creativecomputing.uinano.Window;

/**
 * \class AdvancedGridLayout layout.h nanogui/layout.h
 *
 * \brief Advanced Grid layout.
 *
 * The is a fancier grid layout with support for items that span multiple rows
 * or columns, and per-widget alignment flags. Each row and column additionally
 * stores a stretch factor that controls how additional space is redistributed.
 * The downside of this flexibility is that a layout anchor data structure must
 * be provided for each widget.
 *
 * An example:
 *
 * \rst .. code-block:: cpp
 *
 * using AdvancedGridLayout::Anchor; Label *label = new Label(window, "A
 * label"); // Add a centered label at grid position (1, 5), which spans two
 * horizontal cells layout->setAnchor(label, Anchor(1, 5, 2, 1,
 * Alignment::Middle, Alignment::Middle));
 *
 * \endrst
 *
 * The grid is initialized with user-specified column and row size vectors
 * (which can be expanded later on if desired). If a size value of zero is
 * specified for a column or row, the size is set to the maximum preferred size
 * of any widgets contained in the same row or column. Any remaining space is
 * redistributed according to the row and column stretch factors.
 *
 * The high level usage somewhat resembles the classic HIG layout:
 *
 * - https:
 * //web.archive.org/web/20070813221705/http://www.autel.cz/dmi/tutorial.html -
 * https: //github.com/jaapgeurts/higlayout
 */
public class AdvancedGridLayout extends Layout {
	/**
	 * \struct Anchor layout.h nanogui/layout.h
	 *
	 * \brief Helper struct to coordinate anchor points for the layout.
	 */
	public static class Anchor {
		public int[] pos = new int[2]; /// < The ``(x, y)`` position.
		public int[] size = new int[2]; /// < The ``(x, y)`` size.
		public Alignment[] align = new Alignment[2]; /// < The ``(x, y)``
														/// Alignment.

		/// Creates a ``0`` Anchor.
		public Anchor() {
		}

		/// Create an Anchor at position ``(x, y)`` with specified Alignment.
		public Anchor(int x, int y, Alignment horiz) {
			this(x, y, horiz, Alignment.Fill);
		}

		public Anchor(int x, int y) {
			this(x, y, Alignment.Fill, Alignment.Fill);
		}

		public Anchor(int x, int y, Alignment horiz, Alignment vert) {
			pos[0] = x;
			pos[1] = y;
			size[0] = size[1] = 1;
			align[0] = horiz;
			align[1] = vert;
		}

		/// Create an Anchor at position ``(x, y)`` of size ``(w, h)`` with
		/// specified alignments.
		public Anchor(int x, int y, int w, int h, Alignment horiz) {
			this(x, y, w, h, horiz, Alignment.Fill);
		}

		public Anchor(int x, int y, int w, int h) {
			this(x, y, w, h, Alignment.Fill, Alignment.Fill);
		}

		public Anchor(int x, int y, int w, int h, Alignment horiz, Alignment vert) {
			pos[0] = x;
			pos[1] = y;
			size[0] = w;
			size[1] = h;
			align[0] = horiz;
			align[1] = vert;
		}

		/// Allows for printing out Anchor position, size, and alignment.
		public String toString() {
			return String.format("Format[pos=(%i, %i), size=(%i, %i), align=(%i, %i)]", pos[0], pos[1], size[0],
					size[1], align[0], align[1]);
		}
	}

	/// The columns of this AdvancedGridLayout.
	protected ArrayList<Integer> mCols = new ArrayList<Integer>();

	/// The rows of this AdvancedGridLayout.
	protected ArrayList<Integer> mRows = new ArrayList<Integer>();

	/// The stretch for each column of this AdvancedGridLayout.
	protected ArrayList<Float> mColStretch = new ArrayList<Float>();

	/// The stretch for each row of this AdvancedGridLayout.
	protected ArrayList<Float> mRowStretch = new ArrayList<Float>();

	/// The mapping of widgets to their specified anchor points.
	protected final HashMap<CCWidget, Anchor> mAnchor = new HashMap<CCWidget, Anchor>();

	/// The margin around this AdvancedGridLayout.
	protected int mMargin;

	/// Creates an AdvancedGridLayout with specified columns, rows, and margin.
	public AdvancedGridLayout(ArrayList<Integer> cols, ArrayList<Integer> rows) {
		this(cols, rows, 0);
	}

	public AdvancedGridLayout(ArrayList<Integer> cols) {
		this(cols, new ArrayList<>(), 0);
	}

	public AdvancedGridLayout() {
		this(new ArrayList<>(), new ArrayList<>(), 0);
	}

	public AdvancedGridLayout(ArrayList<Integer> cols, ArrayList<Integer> rows, int margin) {
		this.mCols = cols;
		this.mRows = rows;
		this.mMargin = margin;
		for (int i = 0; i < mCols.size(); i++) {
			mColStretch.add(0f);
		}
		for (int i = 0; i < mRows.size(); i++) {
			mRowStretch.add(0f);
		}
	}

	/// The margin of this AdvancedGridLayout.
	public final int margin() {
		return mMargin;
	}

	/// Sets the margin of this AdvancedGridLayout.
	public final void setMargin(int margin) {
		mMargin = margin;
	}

	/// Return the number of cols
	public final int colCount() {
		return mCols.size();
	}

	/// Return the number of rows
	public final int rowCount() {
		return mRows.size();
	}

	/// Append a row of the given size (and stretch factor)
	public final void appendRow(int size) {
		appendRow(size, 0.0f);
	}

	public final void appendRow(int size, float stretch) {
		mRows.add(size);
		mRowStretch.add(stretch);
	}

	/// Append a column of the given size (and stretch factor)
	public final void appendCol(int size) {
		appendCol(size, 0.0f);
	}

	public final void appendCol(int size, float stretch) {
		mCols.add(size);
		mColStretch.add(stretch);
	}

	/// Set the stretch factor of a given row
	public final void setRowStretch(int index, float stretch) {
		mRowStretch.set(index, stretch);
	}

	/// Set the stretch factor of a given column
	public final void setColStretch(int index, float stretch) {
		mColStretch.set(index, stretch);
	}

	/// Specify the anchor data structure for a given widget
	public final void setAnchor(CCWidget widget, Anchor anchor) {
		mAnchor.put(widget, anchor);
	}

	/// Retrieve the anchor data structure for a given widget
	public final Anchor anchor(CCWidget widget) {
		Anchor myResult = mAnchor.get(widget);
		if (myResult == null) {
			throw new RuntimeException("Widget was not registered with the grid layout!");
		}
		return myResult;
	}

	/* Implementation of the layout interface */
	@Override
	public CCVector2i preferredSize(NanoVG ctx, CCWidget widget) {
		/* Compute minimum row / column sizes */
		ArrayList<Integer>[] grid = new ArrayList[2];
		computeLayout(ctx, widget, grid);

		int x = 0;
		int y = 0;
		for (int i : grid[0]) {
			x += i;
		}
		for (int i : grid[1]) {
			y += i;
		}
		CCVector2i size = new CCVector2i(x, y);

		CCVector2i extra = new CCVector2i(2 * mMargin);
		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			extra.y += widget.theme().mWindowHeaderHeight - mMargin / 2;
		}

		return size.add(extra);
	}

	/// See \ref Layout::performLayout.
	// TODO check this
	@Override
	public void performLayout(NanoVG ctx, CCWidget widget) {
		ArrayList<Integer>[] grid = new ArrayList[2];
		computeLayout(ctx, widget, grid);

		grid[0].add(mMargin);
		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			grid[1].add(widget.theme().mWindowHeaderHeight + mMargin / 2);
		} else {
			grid[1].add(mMargin);
		}

		for (int axis = 0; axis < 2; ++axis) {
			for (int i = 1; i < grid[axis].size(); ++i) {
				grid[axis].set(i, grid[axis].get(i) + grid[axis].get(i - 1));
			}

			for (CCWidget w : widget.children()) {
				if (!w.visible()) {
					continue;
				}
				Anchor anchor = this.anchor(w);

				int itemPos = grid[axis].get(anchor.pos[axis]);
				int cellSize = grid[axis].get(anchor.pos[axis] + anchor.size[axis]) - itemPos;
				int ps = w.preferredSize(ctx).get(axis);
				int fs = w.fixedSize().get(axis);
				int targetSize = fs != 0 ? fs : ps;

				switch (anchor.align[axis]) {
				case Minimum:
					break;
				case Middle:
					itemPos += (cellSize - targetSize) / 2;
					break;
				case Maximum:
					itemPos += cellSize - targetSize;
					break;
				case Fill:
					targetSize = fs != 0 ? fs : cellSize;
					break;
				}

				CCVector2i pos = w.position();
				CCVector2i size = w.size();
				pos.set(axis, itemPos);
				size.set(axis, targetSize);
				w.position(pos);
				w.size(size);
				w.performLayout(ctx);
			}
		}
	}

	/// Computes the layout
	protected final void computeLayout(NanoVG ctx, CCWidget widget, ArrayList<Integer>[] _grid) {
		CCVector2i fs_w = widget.fixedSize();
		CCVector2i containerSize = new CCVector2i(fs_w.x != 0 ? fs_w.x : widget.width(),
				fs_w.y != 0 ? fs_w.y : widget.height());

		CCVector2i extra = new CCVector2i(2 * mMargin);
		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			extra.y += widget.theme().mWindowHeaderHeight - mMargin / 2;
		}

		containerSize.subtractLocal(extra);

		for (int axis = 0; axis < 2; ++axis) {
			ArrayList<Integer> grid = _grid[axis];
			final ArrayList<Integer> sizes = axis == 0 ? mCols : mRows;
			final ArrayList<Float> stretch = axis == 0 ? mColStretch : mRowStretch;
			grid = sizes;

			for (int phase = 0; phase < 2; ++phase) {
				for (Entry<CCWidget, Anchor> pair : mAnchor.entrySet()) {
					CCWidget w = pair.getKey();
					if (!w.visible()) {
						continue;
					}
					final Anchor anchor = pair.getValue();
					if ((anchor.size[axis] == 1) != (phase == 0)) {
						continue;
					}
					int ps = w.preferredSize(ctx).get(axis);
					int fs = w.fixedSize().get(axis);
					int targetSize = fs != 0 ? fs : ps;

					if (anchor.pos[axis] + anchor.size[axis] > (int) grid.size()) {
						throw new RuntimeException("Advanced grid layout: widget is out of bounds: " + anchor);
					}

					int currentSize = 0;
					float totalStretch = 0F;
					for (int i = anchor.pos[axis]; i < anchor.pos[axis] + anchor.size[axis]; ++i) {
						if (sizes.get(i) == 0 && anchor.size[axis] == 1) {
							grid.set(i, Math.max(grid.get(i), targetSize));
						}
						currentSize += grid.get(i);
						totalStretch += stretch.get(i);
					}
					if (targetSize <= currentSize) {
						continue;
					}
					if (totalStretch == 0F) {
						throw new RuntimeException("Advanced grid layout: no space to place widget: " + anchor);
					}
					float amt = (targetSize - currentSize) / totalStretch;
					for (int i = anchor.pos[axis]; i < anchor.pos[axis] + anchor.size[axis]; ++i) {
						grid.set(i, grid.get(i) + (int) Math.round(amt * stretch.get(i)));
					}
				}
			}
			int currentSize = 0;
			for (int i : grid) {
				currentSize += i;
			}
			float totalStretch = 0;
			for (float f : stretch) {
				totalStretch += f;
			}
			if (currentSize >= containerSize.get(axis) || totalStretch == 0F) {
				continue;
			}
			float amt = (containerSize.get(axis) - currentSize) / totalStretch;
			for (int i = 0; i < grid.size(); ++i) {
				grid.set(i, grid.get(i) + (int) Math.round(amt * stretch.get(i)));
			}
		}
	}

}