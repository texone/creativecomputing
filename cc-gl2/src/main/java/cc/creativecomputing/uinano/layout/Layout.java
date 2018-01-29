package cc.creativecomputing.uinano.layout;

import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.uinano.CCWidget;

/**
 * Basic interface of a layout engine.
 */
public abstract class Layout extends Object {
	/**
	 * Performs any and all resizing applicable.
	 *
	 * @param ctx The ``NanoVG`` context being used for drawing.
	 *
	 * @param widget The Widget this layout is controlling sizing for.
	 */
	public abstract void performLayout(CCGraphics g, CCWidget widget);

	/**
	 * The preferred size for this layout.
	 *
	 * @param ctx The ``NanoVG`` context being used for drawing.
	 *
	 * @param widget The Widget this layout's preferred size is considering.
	 *
	 * @return The preferred size, accounting for things such as spacing,
	 * padding for icons, etc.
	 */
	public abstract CCVector2 preferredSize(CCGraphics g, CCWidget widget);

	/// Default destructor (exists for inheritance).
	public void close() {
	}
}