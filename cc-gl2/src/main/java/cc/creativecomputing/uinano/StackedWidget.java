package cc.creativecomputing.uinano;


import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class StackedWidget stackedwidget.h nanogui/stackedwidget.h
 *
 * \brief A stack widget.
 */
public class StackedWidget extends CCWidget {
	private int mSelectedIndex = -1;

	public StackedWidget(CCWidget parent) {
		super(parent);
	}

	public final void setSelectedIndex(int index) {
		assert index < childCount();
		if (mSelectedIndex >= 0) {
			_myChildren.get(mSelectedIndex).setVisible(false);
		}
		mSelectedIndex = index;
		_myChildren.get(mSelectedIndex).setVisible(true);
	}

	public final int selectedIndex() {
		return mSelectedIndex;
	}

	@Override
	public void performLayout(NanoVG ctx) {
		for (CCWidget child : _myChildren) {
			child.position(new CCVector2i());
			child.size(_mySize);
			child.performLayout(ctx);
		}
	}

	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		CCVector2i size = new CCVector2i();
		for (CCWidget child : _myChildren) {
			size = size.cwiseMax(child.preferredSize(ctx));
		}
		return size;
	}

	@Override
	public void addChild(int index, CCWidget widget) {
		if (mSelectedIndex >= 0) {
			_myChildren.get(mSelectedIndex).setVisible(false);
		}
		super.addChild(index, widget);
		widget.setVisible(true);
		setSelectedIndex(index);
	}

}
