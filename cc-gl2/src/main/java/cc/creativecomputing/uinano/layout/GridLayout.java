package cc.creativecomputing.uinano.layout;

import java.util.*;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.uinano.CCWidget;
import cc.creativecomputing.uinano.Orientation;
import cc.creativecomputing.uinano.Window;

/**
 * \class GridLayout layout.h nanogui/layout.h
 *
 * \brief Grid layout.
 *
 * Widgets are arranged in a grid that has a fixed grid resolution \c resolution
 * along one of the axes. The layout orientation indicates the fixed dimension;
 * widgets are also appended on this axis. The spacing between items can be
 * specified per axis. The horizontal/vertical alignment can be specified per
 * row and column.
 */
public class GridLayout extends Layout {
	/// The Orientation defining this GridLayout.
	protected Orientation mOrientation;

	/// The default Alignment for this GridLayout.
	protected Alignment[] mDefaultAlignment = new Alignment[2];

	/// The actual Alignment being used.
	protected ArrayList<Alignment>[] mAlignment = new ArrayList[2];

	/// The number of rows or columns before starting a new one, depending on
	/// the Orientation.
	protected int mResolution;

	/// The spacing used for each dimension.
	protected CCVector2i mSpacing = new CCVector2i();

	/// The margin around this GridLayout.
	protected int mMargin;

	/**
	 * Create a 2-column grid layout by default.
	 *
	 * \param orientation The fixed dimension of this GridLayout.
	 *
	 * \param resolution The number of rows or columns in the grid (depending on
	 * the Orientation).
	 *
	 * \param alignment How widgets should be aligned within each grid cell.
	 *
	 * \param margin The amount of spacing to add around the border of the grid.
	 *
	 * \param spacing The amount of spacing between widgets added to the grid.
	 */
	public GridLayout(Orientation orientation, int resolution, Alignment alignment, int margin) {
		this(orientation, resolution, alignment, margin, 0);
	}

	public GridLayout(Orientation orientation, int resolution, Alignment alignment) {
		this(orientation, resolution, alignment, 0, 0);
	}

	public GridLayout(Orientation orientation, int resolution) {
		this(orientation, resolution, Alignment.Middle, 0, 0);
	}

	public GridLayout(Orientation orientation) {
		this(orientation, 2, Alignment.Middle, 0, 0);
	}

	public GridLayout() {
		this(Orientation.Horizontal, 2, Alignment.Middle, 0, 0);
	}

	public GridLayout(Orientation orientation, int resolution, Alignment alignment, int margin, int spacing) {
		this.mOrientation = orientation;
		this.mResolution = resolution;
		this.mMargin = margin;
		mDefaultAlignment[0] = mDefaultAlignment[1] = alignment;
		mSpacing = new CCVector2i(spacing);
	}

	/// The Orientation of this GridLayout.
	public final Orientation orientation() {
		return mOrientation;
	}

	/// Sets the Orientation of this GridLayout.
	public final void setOrientation(Orientation orientation) {
		mOrientation = orientation;
	}

	/// The number of rows or columns (depending on the Orientation) of this
	/// GridLayout.
	public final int resolution() {
		return mResolution;
	}

	/// Sets the number of rows or columns (depending on the Orientation) of
	/// this GridLayout.
	public final void setResolution(int resolution) {
		mResolution = resolution;
	}

	/// The spacing at the specified axis (row or column number, depending on
	/// the Orientation).
	public final int spacing(int axis) {
		return mSpacing.get(axis);
	}

	/// Sets the spacing for a specific axis.
	public final void setSpacing(int axis, int spacing) {
		mSpacing.set(axis, spacing);
	}

	/// Sets the spacing for all axes.
	public final void setSpacing(int spacing) {

		mSpacing.x = mSpacing.y = spacing;
	}

	/// The margin around this GridLayout.
	public final int margin() {
		return mMargin;
	}

	/// Sets the margin of this GridLayout.
	public final void setMargin(int margin) {
		mMargin = margin;
	}

	/**
	 * The Alignment of the specified axis (row or column number, depending on
	 * the Orientation) at the specified index of that row or column.
	 */
	public final Alignment alignment(int axis, int item) {
		if (item < (int) mAlignment[axis].size()) {
			return mAlignment[axis].get(item);
		} else {
			return mDefaultAlignment[axis];
		}
	}

	/// Sets the Alignment of the columns.
	public final void setColAlignment(Alignment value) {
		mDefaultAlignment[0] = value;
	}

	/// Sets the Alignment of the rows.
	public final void setRowAlignment(Alignment value) {
		mDefaultAlignment[1] = value;
	}

	/// Use this to set variable Alignment for columns.
	public final void setColAlignment(ArrayList<Alignment> value) {
		mAlignment[0] = value;
	}

	/// Use this to set variable Alignment for rows.
	public final void setRowAlignment(ArrayList<Alignment> value) {
		mAlignment[1] = value;
	}

	/* Implementation of the layout interface */
	/// See \ref Layout::preferredSize.
	@Override
	public CCVector2i preferredSize(NanoVG ctx, CCWidget widget) {
		/* Compute minimum row / column sizes */
		int[][] grid = new int[2][];
		computeLayout(ctx, widget, grid);

		int grid0 = 0;
		int grid1 = 0;
		for (int i = 0; i < grid[0].length; i++) {
			grid0 += grid[0][i];
		}
		for (int i = 0; i < grid[1].length; i++) {
			grid1 += grid[1][i];
		}
		CCVector2i size = new CCVector2i(2 * mMargin + grid0 * mSpacing.x,
				2 * mMargin + grid1 + Math.max((int) grid[1].length - 1, 0) * mSpacing.y);

		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			size.y += widget.theme().mWindowHeaderHeight - mMargin / 2;
		}

		return size;
	}

	/// See \ref Layout::performLayout.
	@Override
	public void performLayout(NanoVG ctx, CCWidget widget) {
		CCVector2i fs_w = widget.fixedSize();
		CCVector2i containerSize = new CCVector2i(fs_w.x != 0 ? fs_w.x : widget.width(),
				fs_w.y != 0 ? fs_w.y : widget.height());

		/* Compute minimum row / column sizes */
		int[][] grid = new int[2][];
		computeLayout(ctx, widget, grid);
		int[] dim = { grid[0].length, grid[1].length };

		CCVector2i extra = new CCVector2i();
		Window window = (Window) ((widget instanceof Window) ? widget : null);
		if (window != null && !window.title().isEmpty()) {
			extra.y += widget.theme().mWindowHeaderHeight - mMargin / 2;
		}

		/* Strech to size provided by \c widget */
		for (int i = 0; i < 2; i++) {
			int gridSize = 2 * mMargin + extra.get(i);
			for (int s : grid[i]) {
				gridSize += s;
				if (i + 1 < dim[i]) {
					gridSize += mSpacing.get(i);
				}
			}

			if (gridSize < containerSize.get(i)) {
				/* Re-distribute remaining space evenly */
				int gap = containerSize.get(i) - gridSize;
				int g = gap / dim[i];
				int rest = gap - g * dim[i];
				for (int j = 0; j < dim[i]; ++j) {
					grid[i][j] = grid[i][j] + g;
				}
				for (int j = 0; rest > 0 && j < dim[i]; --rest, ++j) {
					grid[i][j] = grid[i][j] + 1;
				}
			}
		}

		int axis1 = mOrientation.ordinal();
		int axis2 = (axis1 + 1) % 2;
		CCVector2i start = new CCVector2i(mMargin).add(extra);

		int numChildren = widget.children().size();
		int child = 0;

		CCVector2i pos = new CCVector2i(start);
		for (int i2 = 0; i2 < dim[axis2]; i2++) {
			pos.set(axis1, start.get(axis1));
			for (int i1 = 0; i1 < dim[axis1]; i1++) {
				CCWidget w = null;
				do {
					if (child >= numChildren) {
						return;
					}
					w = widget.children().get(child++);
				} while (!w.visible());

				CCVector2i ps = w.preferredSize(ctx);
				CCVector2i fs = w.fixedSize();
				CCVector2i targetSize = new CCVector2i(fs.x != 0 ? fs.x : ps.x, fs.y != 0 ? fs.y : ps.y);

				CCVector2i itemPos = new CCVector2i(pos);
				for (int j = 0; j < 2; j++) {
					int axis = (axis1 + j) % 2;
					int item = j == 0 ? i1 : i2;
					Alignment align = alignment(axis, item);

					switch (align) {
					case Minimum:
						break;
					case Middle:
						itemPos.add(axis, (grid[axis][item] - targetSize.get(axis)) / 2);
						break;
					case Maximum:
						itemPos.add(axis, grid[axis][item] - targetSize.get(axis));
						break;
					case Fill:
						targetSize.set(axis, fs.get(axis) != 0 ? fs.get(axis) : grid[axis][item]);
						break;
					}
				}
				w.position(itemPos);
				w.size(targetSize);
				w.performLayout(ctx);
				pos.add(axis1, grid[axis1][i1] + mSpacing.get(axis1));
			}
			pos.add(axis2, grid[axis2][i2] + mSpacing.get(axis2));
		}
	}

	/// Compute the maximum row and column sizes
	protected final void computeLayout(NanoVG ctx, CCWidget widget, int[][] grid) {
		int axis1 = mOrientation.ordinal();
		int axis2 = (axis1 + 1) % 2;
		int numChildren = widget.children().size();
		int visibleChildren = 0;

		for (CCWidget w : widget.children()) {
			visibleChildren += w.visible() ? 1 : 0;
		}

		CCVector2i dim = new CCVector2i();
		dim.set(axis1, mResolution);
		dim.set(axis2, (int) ((visibleChildren + mResolution - 1) / mResolution));

		grid[axis1] = new int[dim.get(axis1)];
		grid[axis2] = new int[dim.get(axis2)];

		int child = 0;
		for (int i2 = 0; i2 < dim.get(axis2); i2++) {
			for (int i1 = 0; i1 < dim.get(axis1); i1++) {
				CCWidget w = null;
				do {
					if (child >= numChildren) {
						return;
					}
					w = widget.children().get(child++);
				} while (!w.visible());

				CCVector2i ps = w.preferredSize(ctx);
				CCVector2i fs = w.fixedSize();
				CCVector2i targetSize = new CCVector2i(fs.x != 0 ? fs.x : ps.x, fs.y != 0 ? fs.y : ps.y);

				grid[axis1][i1] = Math.max(grid[axis1][i1], targetSize.get(axis1));
				grid[axis2][i2] = Math.max(grid[axis2][i2], targetSize.get(axis2));
			}
		}
	}

}