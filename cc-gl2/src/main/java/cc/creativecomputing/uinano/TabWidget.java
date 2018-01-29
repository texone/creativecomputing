package cc.creativecomputing.uinano;
import java.util.*;



/**
 * \class TabWidget tabwidget.h nanogui/tabwidget.h
 *
 * \brief A wrapper around the widgets TabHeader and StackedWidget which hooks
 *        the two classes together.
 */
public class TabWidget extends Widget
{
	public TabWidget(Widget parent)
	{
		super(parent);
		this.mHeader = new TabHeader(this);
		this.mContent = new StackedWidget(this);
//C++ TO JAVA CONVERTER TODO TASK: Only lambda expressions having all locals passed by reference can be converted to Java:
//ORIGINAL LINE: mHeader->setCallback([this](int i)
		mHeader.setCallback((int i) ->
		{
			mContent.setSelectedIndex(i);
			if (mCallback)
			{
				mCallback(i);
			}
		});
	}

	public final void setActiveTab(int tabIndex)
	{
		mHeader.setActiveTab(tabIndex);
		mContent.setSelectedIndex(tabIndex);
	}
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: int activeTab() const
	public final int activeTab()
	{
		assert mHeader.activeTab() == mContent.selectedIndex();
		return mContent.selectedIndex();
	}
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: int tabCount() const
	public final int tabCount()
	{
		assert mContent.childCount() == mHeader.tabCount();
		return mHeader.tabCount();
	}

	/**
	 * Sets the callable objects which is invoked when a tab is changed.
	 * The argument provided to the callback is the index of the new active tab.
	 */
	public final void setCallback(tangible.Action1Param<Integer> callback)
	{
		mCallback = callback;
	}
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: const System.Action<int> &callback() const
	public final tangible.Action1Param<Integer> callback()
	{
		return mCallback;
	}

	/// Creates a new tab with the specified name and returns a pointer to the layer.
	public final Widget createTab(String label)
	{
		return createTab(tabCount(), label);
	}
	public final Widget createTab(int index, String label)
	{
		Widget tab = new Widget(null);
		addTab(index, label, tab);
		return tab;
	}

	/// Inserts a tab at the end of the tabs collection and associates it with the provided widget.
	public final void addTab(String name, Widget tab)
	{
		addTab(tabCount(), name, tab);
	}

	/// Inserts a tab into the tabs collection at the specified index and associates it with the provided widget.
	public final void addTab(int index, String label, Widget tab)
	{
		assert index <= tabCount();
		// It is important to add the content first since the callback
		// of the header will automatically fire when a new tab is added.
		mContent.addChild(index, tab);
		mHeader.addTab(index, label);
		assert mHeader.tabCount() == mContent.childCount();
	}

	/**
	 * Removes the tab with the specified label and returns the index of the label.
	 * Returns whether the removal was successful.
	 */
	public final boolean removeTab(String tabName)
	{
		int index = mHeader.removeTab(tabName);
		if (index == -1)
		{
			return false;
		}
		mContent.removeChild(index);
		return true;
	}

	/// Removes the tab with the specified index.
	public final void removeTab(int index)
	{
		assert mContent.childCount() < index;
		mHeader.removeTab(index);
		mContent.removeChild(index);
		if (activeTab() == index)
		{
			setActiveTab(index == (index - 1) ? index - 1 : 0);
		}
	}

	/// Retrieves the label of the tab at a specific index.
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: const String &tabLabelAt(int index) const
	public final String tabLabelAt(int index)
	{
		return mHeader.tabLabelAt(index);
	}

	/**
	 * Retrieves the index of a specific tab using its tab label.
	 * Returns -1 if there is no such tab.
	 */
	public final int tabLabelIndex(String label)
	{
		return mHeader.tabIndex(label);
	}

	/**
	 * Retrieves the index of a specific tab using a widget pointer.
	 * Returns -1 if there is no such tab.
	 */
	public final int tabIndex(Widget tab)
	{
		return mContent.childIndex(tab);
	}

	/**
	 * This function can be invoked to ensure that the tab with the provided
	 * index the is visible, i.e to track the given tab. Forwards to the tab
	 * header widget. This function should be used whenever the client wishes
	 * to make the tab header follow a newly added tab, as the content of the
	 * new tab is made visible but the tab header does not track it by default.
	 */
	public final void ensureTabVisible(int index)
	{
		if (!mHeader.isTabVisible(index))
		{
			mHeader.ensureTabVisible(index);
		}
	}

	/**
	 * \brief Returns a ``const`` pointer to the Widget associated with the
	 *        specified label.
	 *
	 * \param label
	 *     The label used to create the tab.
	 *
	 * \return
	 *     The Widget associated with this label, or ``nullptr`` if not found.
	 */
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: const Widget *tab(const String &tabName) const
	public final Widget tab(String tabName)
	{
		int index = mHeader.tabIndex(tabName);
		if (index == -1 || index == mContent.childCount())
		{
			return null;
		}
		return mContent.children()[index];
	}

	/**
	 * \brief Returns a pointer to the Widget associated with the specified label.
	 *
	 * \param label
	 *     The label used to create the tab.
	 *
	 * \return
	 *     The Widget associated with this label, or ``nullptr`` if not found.
	 */
	public final Widget tab(String tabName)
	{
		int index = mHeader.tabIndex(tabName);
		if (index == -1 || index == mContent.childCount())
		{
			return null;
		}
		return mContent.children()[index];
	}

	/**
	 * \brief Returns a ``const`` pointer to the Widget associated with the
	 *        specified index.
	 *
	 * \param index
	 *     The current index of the desired Widget.
	 *
	 * \return
	 *     The Widget at the specified index, or ``nullptr`` if ``index`` is not
	 *     a valid index.
	 */
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: const Widget *tab(int index) const
	public final Widget tab(int index)
	{
		if (index < 0 || index >= mContent.childCount())
		{
			return null;
		}
		return mContent.children()[index];
	}

	/**
	 * \brief Returns a pointer to the Widget associated with the specified index.
	 *
	 * \param index
	 *     The current index of the desired Widget.
	 *
	 * \return
	 *     The Widget at the specified index, or ``nullptr`` if ``index`` is not
	 *     a valid index.
	 */
	public final Widget tab(int index)
	{
		if (index < 0 || index >= mContent.childCount())
		{
			return null;
		}
		return mContent.children()[index];
	}

	@Override
	public void performLayout(NVGcontext ctx)
	{
		int headerHeight = mHeader.preferredSize(ctx).y();
		int margin = mTheme.mTabInnerMargin;
		mHeader.setPosition({0, 0});
		mHeader.setSize({mSize.x(), headerHeight});
		mHeader.performLayout(ctx);
		mContent.setPosition({margin, headerHeight + margin});
		mContent.setSize({mSize.x() - 2 * margin, mSize.y() - 2 * margin - headerHeight});
		mContent.performLayout(ctx);
	}
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: virtual Vector2i preferredSize(NVGcontext* ctx) const override
	@Override
	public Vector2i preferredSize(NVGcontext ctx)
	{
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto contentSize = mContent.preferredSize(ctx);
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto headerSize = mHeader.preferredSize(ctx);
		int margin = mTheme.mTabInnerMargin;
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto borderSize = Vector2i(2 * margin, 2 * margin);
		Vector2i tabPreferredSize = contentSize + borderSize + Vector2i(0, headerSize.y());
		return tabPreferredSize;
	}
	@Override
	public void draw(NVGcontext ctx)
	{
		int tabHeight = mHeader.preferredSize(ctx).y();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto activeArea = mHeader.activeButtonArea();


		for (int i = 0; i < 3; ++i)
		{
			nvgSave(ctx);
			if (i == 0)
			{
				nvgIntersectScissor(ctx, mPos.x(), mPos.y(), activeArea.first.x() + 1, mSize.y());
			}
			else if (i == 1)
			{
				nvgIntersectScissor(ctx, mPos.x() + activeArea.second.x(), mPos.y(), mSize.x() - activeArea.second.x(), mSize.y());
			}
			else
			{
				nvgIntersectScissor(ctx, mPos.x(), mPos.y() + tabHeight + 2, mSize.x(), mSize.y());
			}

			nvgBeginPath(ctx);
			nvgStrokeWidth(ctx, 1.0f);
			nvgRoundedRect(ctx, mPos.x() + 0.5f, mPos.y() + tabHeight + 1.5f, mSize.x() - 1, mSize.y() - tabHeight - 2, mTheme.mButtonCornerRadius);
			nvgStrokeColor(ctx, mTheme.mBorderLight);
			nvgStroke(ctx);

			nvgBeginPath(ctx);
			nvgRoundedRect(ctx, mPos.x() + 0.5f, mPos.y() + tabHeight + 0.5f, mSize.x() - 1, mSize.y() - tabHeight - 2, mTheme.mButtonCornerRadius);
			nvgStrokeColor(ctx, mTheme.mBorderDark);
			nvgStroke(ctx);
			nvgRestore(ctx);
		}

		Widget.draw(ctx);
	}

	private TabHeader mHeader;
	private StackedWidget mContent;
	private tangible.Action1Param<Integer> mCallback;
//C++ TO JAVA CONVERTER TODO TASK: The following statement was not recognized, possibly due to an unrecognized macro:
	EIGEN_MAKE_ALIGNED_OPERATOR_NEW
}