package cc.creativecomputing.uinano;

import java.util.*;

import org.lwjgl.nanovg.NVGTextRow;

import cc.creativecomputing.core.events.CCIntEvent;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.gl.nanovg.NanoVG;
import cc.creativecomputing.math.CCVector2i;

/**
 * \class TabHeader tabheader.h nanogui/tabheader.h
 *
 * \brief A Tab navigable widget.
 */
public class TabHeader extends Widget {
	private CCListenerManager<CCIntEvent> mCallback = CCListenerManager.create(CCIntEvent.class);
	private ArrayList<TabButton> mTabButtons = new ArrayList<TabButton>();
	private int mVisibleStart = 0;
	private int mVisibleEnd = 0;
	private int mActiveTab = 0;
	private boolean mOverflowing = false;

	private String mFont;

	public TabHeader(Widget parent) {
		this(parent, "sans-bold");
	}

	public TabHeader(Widget parent, String font) {
		super(parent);
		this.mFont = font;
	}

	public final void setFont(String font) {
		mFont = font;
	}

	public final String font() {
		return mFont;
	}

	public final boolean overflowing() {
		return mOverflowing;
	}

	public final void setActiveTab(int tabIndex) {
		assert tabIndex < tabCount();
		mActiveTab = tabIndex;
		mCallback.proxy().event(tabIndex);

	}

	public final int activeTab() {
		return mActiveTab;
	}

	public final boolean isTabVisible(int index) {
		return index >= mVisibleStart && index < mVisibleEnd;
	}

	public final int tabCount() {
		return (int) mTabButtons.size();
	}

	/// Inserts a tab at the end of the tabs collection.
	public final void addTab(String label) {
		addTab(tabCount(), label);
	}

	/// Inserts a tab into the tabs collection at the specified index.
	public final void addTab(int index, String label) {
		assert index <= tabCount();
		mTabButtons.add(index, new TabButton(this, label));
		setActiveTab(index);
	}

	/**
	 * Removes the tab with the specified label and returns the index of the
	 * label. Returns -1 if there was no such tab
	 */
	public final int removeTab(String label) {

		TabButton element = mTabButtons.stream().filter((tb) -> {
			return label.equals(tb.label());
		}).findAny().orElse(null);
		if (element == null)
			return -1;
		int index = mTabButtons.indexOf(element);
		mTabButtons.remove(element);
		if (index == -1) {
			return -1;
		}
		if (index == mActiveTab && index != 0) {
			setActiveTab(index - 1);
		}
		return index;
	}

	/// Removes the tab with the specified index.
	public final void removeTab(int index)
	{
		assert index < tabCount();
		mTabButtons.remove(index);
		if (index == mActiveTab && index != 0)
		{
			setActiveTab(index - 1);
		}
	}

	/// Retrieves the label of the tab at a specific index.
	public final String tabLabelAt(int index) {
		assert index < tabCount();
		return mTabButtons.get(index).label();
	}

	/**
	 * Retrieves the index of a specific tab label.
	 * Returns the number of tabs (tabsCount) if there is no such tab.
	 */
	public final int tabIndex(String label)
	{
		TabButton element = mTabButtons.stream().filter((tb) -> {
			return label.equals(tb.label());
		}).findAny().orElse(null);
		if (element == null)
			return -1;
		return mTabButtons.indexOf(element);
	}

	/**
	 * Recalculate the visible range of tabs so that the tab with the specified
	 * index is visible. The tab with the specified index will either be the
	 * first or last visible one depending on the position relative to the
	 * old visible range.
	 */
	public final void ensureTabVisible(int index)
	{
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto visibleArea = visibleButtonArea();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto visibleWidth = visibleArea.second.x - visibleArea.first.x;
		int allowedVisibleWidth = mSize.x - 2 * theme().mTabControlWidth;
		assert allowedVisibleWidth >= visibleWidth;
		assert index >= 0 && index < (int) mTabButtons.size();

//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto first = visibleBegin();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto last = visibleEnd();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto goal = tabIterator(index);

		// Reach the goal tab with the visible range.
		if (goal < first)
		{
			do
			{
				--first;
				visibleWidth += first.size().x;
			} while (goal < first);
			while (allowedVisibleWidth < visibleWidth)
			{
				--last;
				visibleWidth -= last.size().x;
			}
		}
		else if (goal >= last)
		{
			do
			{
				visibleWidth += last.size().x;
				++last;
			} while (goal >= last);
			while (allowedVisibleWidth < visibleWidth)
			{
				visibleWidth -= first.size().x;
				++first;
			}
		}

		// Check if it is possible to expand the visible range on either side.
		while (first != mTabButtons.begin() && std::next(first, -1).size().x < allowedVisibleWidth - visibleWidth)
		{
			--first;
			visibleWidth += first.size().x;
		}
		while (last != mTabButtons.end() && last.size().x < allowedVisibleWidth - visibleWidth)
		{
			visibleWidth += last.size().x;
			++last;
		}

		mVisibleStart = (int) std::distance(mTabButtons.begin(), first);
		mVisibleEnd = (int) std::distance(mTabButtons.begin(), last);
	}

	/**
	 * Returns a pair of Vectors describing the top left (pair.first) and the
	 * bottom right (pair.second) positions of the rectangle containing the visible tab buttons.
	 */
	public final CCVector2i[] visibleButtonArea()
	{
		if (mVisibleStart == mVisibleEnd)
		{
			return {CCVector2i.Zero(), CCVector2i.Zero()};
		}
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto topLeft = mPos + CCVector2i(theme().mTabControlWidth, 0);
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto width = std::accumulate(visibleBegin(), visibleEnd(), theme().mTabControlWidth, (int acc, TabButton tb) ->
		{
			return acc + tb.size().x;
		});
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto bottomRight = mPos + CCVector2i(width, mSize.y);
		return {topLeft, bottomRight};
	}

	/**
	 * Returns a pair of Vectors describing the top left (pair.first) and the
	 * bottom right (pair.second) positions of the rectangle containing the active tab button.
	 * Returns two zero vectors if the active button is not visible.
	 */
//C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
//ORIGINAL LINE: tangible.Pair<CCVector2i, CCVector2i> activeButtonArea() const
	public final tangible.Pair<CCVector2i, CCVector2i> activeButtonArea()
	{
		if (mVisibleStart == mVisibleEnd || mActiveTab < mVisibleStart || mActiveTab >= mVisibleEnd)
		{
			return {CCVector2i.Zero(), CCVector2i.Zero()};
		}
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto width = std::accumulate(visibleBegin(), activeIterator(), theme().mTabControlWidth, (int acc, TabButton tb) ->
		{
			return acc + tb.size().x;
		});
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto topLeft = mPos + CCVector2i(width, 0);
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto bottomRight = mPos + CCVector2i(width + activeIterator().size().x, mSize.y);
		return {topLeft, bottomRight};
	}

	@Override
	public void performLayout(NanoVG ctx) {
		Widget.performLayout(ctx);

		CCVector2i currentPosition = CCVector2i.Zero();
		// Place the tab buttons relative to the beginning of the tab header.
		// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit
		// typing in Java:
		for (auto tab : mTabButtons) {
			// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to
			// implicit typing in Java:
			auto tabPreferred = tab.preferredSize(ctx);
			if (tabPreferred.x < theme().mTabMinButtonWidth) {
				tabPreferred.x = theme().mTabMinButtonWidth;
			} else if (tabPreferred.x > theme().mTabMaxButtonWidth) {
				tabPreferred.x = theme().mTabMaxButtonWidth;
			}
			tab.setSize(tabPreferred);
			tab.calculateVisibleString(ctx);
			currentPosition.x += tabPreferred.x;
		}
		calculateVisibleEnd();
		if (mVisibleStart != 0 || mVisibleEnd != tabCount()) {
			mOverflowing = true;
		}
	}

	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: virtual CCVector2i preferredSize(NanoVG* ctx) const
	// override
	@Override
	public CCVector2i preferredSize(NanoVG ctx) {
		// Set up the nvg context for measuring the text inside the tab buttons.
		nvgFontFace(ctx, mFont);
		nvgFontSize(ctx, fontSize());
		nvgTextAlign(ctx, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);
		CCVector2i size = new CCVector2i(2 * theme().mTabControlWidth, 0);
		// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit
		// typing in Java:
		for (auto tab : mTabButtons) {
			// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to
			// implicit typing in Java:
			auto tabPreferred = tab.preferredSize(ctx);
			if (tabPreferred.x < theme().mTabMinButtonWidth) {
				tabPreferred.x = theme().mTabMinButtonWidth;
			} else if (tabPreferred.x > theme().mTabMaxButtonWidth) {
				tabPreferred.x = theme().mTabMaxButtonWidth;
			}
			size.x += tabPreferred.x;
			size.y = Math.max(size.y, tabPreferred.y);
		}
		return size;
	}

	@Override
	public boolean mouseButtonEvent(CCVector2i p, int button, boolean down, int modifiers)
	{
		Widget.mouseButtonEvent(p, button, down, modifiers);
		if (button == GLFW_MOUSE_BUTTON_1 && down)
		{
			switch (locateClick(p))
			{
			case ClickLocation.LeftControls:
				onArrowLeft();
				return true;
			case ClickLocation.RightControls:
				onArrowRight();
				return true;
			case ClickLocation.TabButtons:
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				auto first = visibleBegin();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				auto last = visibleEnd();
				int currentPosition = theme().mTabControlWidth;
				int endPosition = p.x;
//C++ TO JAVA CONVERTER TODO TASK: Only lambda expressions having all locals passed by reference can be converted to Java:
//ORIGINAL LINE: auto firstInvisible = std::find_if(first, last, [&currentPosition, endPosition](const TabButton& tb)
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
				auto firstInvisible = std::find_if(first, last, (TabButton tb) ->
				{
					currentPosition += tb.size().x;
					return currentPosition > endPosition;
				});

				// Did not click on any of the tab buttons
				if (firstInvisible == last)
				{
					return true;
				}

				// Update the active tab and invoke the callback.
				setActiveTab((int) std::distance(mTabButtons.begin(), firstInvisible));
				return true;
			}
		}
		return false;
	}

	@Override
	public void draw(NanoVG ctx)
	{
		// Draw controls.
		Widget.draw(ctx);
		if (mOverflowing)
		{
			drawControls(ctx);
		}

		// Set up common text drawing settings.
		nvgFontFace(ctx, mFont);
		nvgFontSize(ctx, fontSize());
		nvgTextAlign(ctx, NVG_ALIGN_LEFT | NVG_ALIGN_TOP);

//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto current = visibleBegin();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto last = visibleEnd();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto active = std::next(mTabButtons.begin(), mActiveTab);
		CCVector2i currentPosition = mPos + CCVector2i(theme().mTabControlWidth, 0);

		// Flag to draw the active tab last. Looks a little bit better.
		boolean drawActive = false;
		CCVector2i activePosition = CCVector2i.Zero();

		// Draw inactive visible buttons.
		while (current != last)
		{
			if (current == active)
			{
				drawActive = true;
//C++ TO JAVA CONVERTER WARNING: The following line was determined to be a copy assignment (rather than a reference assignment) - this should be verified and a 'copyFrom' method should be created if it does not yet exist:
//ORIGINAL LINE: activePosition = currentPosition;
				activePosition.copyFrom(currentPosition);
			}
			else
			{
				current.drawAtPosition(ctx, currentPosition, false);
			}
			currentPosition.x += current.size().x;
			++current;
		}

		// Draw active visible button.
		if (drawActive)
		{
			active.drawAtPosition(ctx, activePosition, true);
		}
	}

	/**
	 * \class TabButton tabheader.h
	 *
	 * \brief Implementation class of the actual tab buttons.
	 */
	private static class TabButton {
		public static final String dots = "...";

		public TabButton(TabHeader header, String label) {
			this.mHeader = header;
			this.mLabel = label;
		}

		public final void setLabel(String label) {
			mLabel = label;
		}

		// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
		// Java:
		// ORIGINAL LINE: const String& label() const
		public final String label() {
			return mLabel;
		}

		public final void setSize(CCVector2i size) {
			mSize = size;
		}

		// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
		// Java:
		// ORIGINAL LINE: const CCVector2i& size() const
		public final CCVector2i size() {
			return mSize;
		}

		// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in
		// Java:
		// ORIGINAL LINE: CCVector2i preferredSize(NanoVG *ctx) const
		public final CCVector2i preferredSize(NanoVG ctx) {
			// No need to call nvg font related functions since this is done by
			// the tab header implementation
			float[] bounds = new float[4];
			int labelWidth = nvgTextBounds(ctx, 0, 0, mLabel, null, bounds);
			int buttonWidth = labelWidth + 2 * mHeader.theme().mTabButtonHorizontalPadding;
			float buttonHeight = bounds[3] - bounds[1] + 2 * mHeader.theme().mTabButtonVerticalPadding;
			return new CCVector2i(buttonWidth, buttonHeight);
		}

		public final void calculateVisibleString(NanoVG ctx) {
			// The size must have been set in by the enclosing tab header.
			NVGTextRow displayedText = NVGTextRow.create();
			nvgTextBreakLines(ctx, mLabel, null, mSize.x, displayedText, 1);

			// Check to see if the text need to be truncated.
			if (displayedText.next[0]) {
				// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to
				// implicit typing in Java:
				auto truncatedWidth = nvgTextBounds(ctx, 0.0f, 0.0f, displayedText.start, displayedText.end, null);
				// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to
				// implicit typing in Java:
				auto dotsWidth = nvgTextBounds(ctx, 0.0f, 0.0f, dots, null, null);
				while ((truncatedWidth + dotsWidth + mHeader.theme().mTabButtonHorizontalPadding) > mSize.x
						&& displayedText.end != displayedText.start) {
					--displayedText.end;
					truncatedWidth = nvgTextBounds(ctx, 0.0f, 0.0f, displayedText.start, displayedText.end, null);
				}

				// Remember the truncated width to know where to display the
				// dots.
				mVisibleWidth = truncatedWidth;
				mVisibleText.last = displayedText.end;
			} else {
				mVisibleText.last = null;
				mVisibleWidth = 0;
			}
			mVisibleText.first = displayedText.start;
		}

		public final void drawAtPosition(NanoVG ctx, CCVector2i position, boolean active) {
			int xPos = position.x;
			int yPos = position.y;
			int width = mSize.x;
			int height = mSize.y;
			// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to
			// implicit typing in Java:
			auto theme = mHeader.theme();

			nvgSave(ctx);
			nvgIntersectScissor(ctx, xPos, yPos, width + 1, height);
			if (!active) {
				// Background gradients
				NVGcolor gradTop = theme.mButtonGradientTopPushed;
				NVGcolor gradBot = theme.mButtonGradientBotPushed;

				// Draw the background.
				nvgBeginPath(ctx);
				nvgRoundedRect(ctx, xPos + 1, yPos + 1, width - 1, height + 1, theme.mButtonCornerRadius);
				NVGpaint backgroundColor = nvgLinearGradient(ctx, xPos, yPos, xPos, yPos + height, gradTop, gradBot);
				nvgFillPaint(ctx, backgroundColor);
				nvgFill(ctx);
			}

			if (active) {
				nvgBeginPath(ctx);
				nvgStrokeWidth(ctx, 1.0f);
				nvgRoundedRect(ctx, xPos + 0.5f, yPos + 1.5f, width, height + 1, theme.mButtonCornerRadius);
				nvgStrokeColor(ctx, theme.mBorderLight);
				nvgStroke(ctx);

				nvgBeginPath(ctx);
				nvgRoundedRect(ctx, xPos + 0.5f, yPos + 0.5f, width, height + 1, theme.mButtonCornerRadius);
				nvgStrokeColor(ctx, theme.mBorderDark);
				nvgStroke(ctx);
			} else {
				nvgBeginPath(ctx);
				nvgRoundedRect(ctx, xPos + 0.5f, yPos + 1.5f, width, height, theme.mButtonCornerRadius);
				nvgStrokeColor(ctx, theme.mBorderDark);
				nvgStroke(ctx);
			}
			nvgResetScissor(ctx);
			nvgRestore(ctx);

			// Draw the text with some padding
			int textX = xPos + mHeader.theme().mTabButtonHorizontalPadding;
			int textY = yPos + mHeader.theme().mTabButtonVerticalPadding;
			NVGcolor textColor = mHeader.theme().mTextColor;
			nvgBeginPath(ctx);
			nvgFillColor(ctx, textColor);
			nvgText(ctx, textX, textY, mVisibleText.first, mVisibleText.last);
			if (mVisibleText.last != null) {
				nvgText(ctx, textX + mVisibleWidth, textY, dots, null);
			}
		}

		public final void drawActiveBorderAt(NanoVG ctx, CCVector2i position, float offset, Color color) {
			int xPos = position.x;
			int yPos = position.y;
			int width = mSize.x;
			int height = mSize.y;
			nvgBeginPath(ctx);
			nvgLineJoin(ctx, NVG_ROUND);
			nvgMoveTo(ctx, xPos + offset, yPos + height + offset);
			nvgLineTo(ctx, xPos + offset, yPos + offset);
			nvgLineTo(ctx, xPos + width - offset, yPos + offset);
			nvgLineTo(ctx, xPos + width - offset, yPos + height + offset);
			nvgStrokeColor(ctx, color);
			nvgStrokeWidth(ctx, mHeader.theme().mTabBorderWidth);
			nvgStroke(ctx);
		}

		public final void drawInactiveBorderAt(NanoVG ctx, CCVector2i position, float offset, Color color) {
			int xPos = position.x;
			int yPos = position.y;
			int width = mSize.x;
			int height = mSize.y;
			nvgBeginPath(ctx);
			nvgRoundedRect(ctx, xPos + offset, yPos + offset, width - offset, height - offset,
					mHeader.theme().mButtonCornerRadius);
			nvgStrokeColor(ctx, color);
			nvgStroke(ctx);
		}

		private TabHeader mHeader;
		private String mLabel;
		private CCVector2i mSize = new CCVector2i();

		/**
		 * \struct StringView tabheader.h nanogui/tabheader.h
		 *
		 * \brief Helper struct to represent the TabButton.
		 */
		private static class StringView {
			public final String first = null;
			public final String last = null;
		}

		private StringView mVisibleText = new StringView();
		private int mVisibleWidth = 0;
	}

	/// The location in which the Widget will be facing.
	private enum ClickLocation {
		LeftControls, RightControls, TabButtons;

		public static final int SIZE = java.lang.Integer.SIZE;

		public int getValue() {
			return this.ordinal();
		}

		public static ClickLocation forValue(int value) {
			return values()[value];
		}
	}

	private Iterator<TabButton> visibleBegin()
	{
		return std::next(mTabButtons.begin(), mVisibleStart);
	}

	private Iterator<TabButton> visibleEnd()
	{
		return std::next(mTabButtons.begin(), mVisibleEnd);
	}

	private Iterator<TabButton> activeIterator()
	{
		return std::next(mTabButtons.begin(), mActiveTab);
	}

	private Iterator<TabButton> tabIterator(int index)
	{
		return std::next(mTabButtons.begin(), index);
	}

	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: ClassicVectorIterator<TabButton> visibleBegin() const
	private Iterator<TabButton> visibleBegin()
	{
		return std::next(mTabButtons.begin(), mVisibleStart);
	}

	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: ClassicVectorIterator<TabButton> visibleEnd() const
	private Iterator<TabButton> visibleEnd()
	{
		return std::next(mTabButtons.begin(), mVisibleEnd);
	}

	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: ClassicVectorIterator<TabButton> activeIterator() const
	private Iterator<TabButton> activeIterator()
	{
		return std::next(mTabButtons.begin(), mActiveTab);
	}

	// C++ TO JAVA CONVERTER WARNING: 'const' methods are not available in Java:
	// ORIGINAL LINE: ClassicVectorIterator<TabButton> tabIterator(int index)
	// const
	private Iterator<TabButton> tabIterator(int index)
	{
		return std::next(mTabButtons.begin(), index);
	}

	/// Given the beginning of the visible tabs, calculate the end.
	private void calculateVisibleEnd()
	{
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto first = visibleBegin();
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto last = mTabButtons.end();
		int currentPosition = theme().mTabControlWidth;
		int lastPosition = mSize.x - theme().mTabControlWidth;
//C++ TO JAVA CONVERTER TODO TASK: Only lambda expressions having all locals passed by reference can be converted to Java:
//ORIGINAL LINE: auto firstInvisible = std::find_if(first, last, [&currentPosition, lastPosition](const TabButton& tb)
//C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit typing in Java:
		auto firstInvisible = std::find_if(first, last, (TabButton tb) ->
		{
			currentPosition += tb.size().x;
			return currentPosition > lastPosition;
		});
		mVisibleEnd = (int) std::distance(mTabButtons.begin(), firstInvisible);
	}

	private void drawControls(NanoVG ctx) {
		// Left button.
		boolean active = mVisibleStart != 0;

		// Draw the arrow.
		nvgBeginPath(ctx);
		// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit
		// typing in Java:
		auto iconLeft = utf8(mTheme.mTabHeaderLeftIcon);
		int fontSize = mFontSize == -1 ? mTheme.mButtonFontSize : mFontSize;
		float ih = fontSize;
		ih *= icon_scale();
		nvgFontSize(ctx, ih);
		nvgFontFace(ctx, "icons");
		NVGcolor arrowColor = new NVGcolor();
		if (active) {
			arrowColor = mTheme.mTextColor;
		} else {
			arrowColor = mTheme.mButtonGradientBotPushed;
		}
		nvgFillColor(ctx, arrowColor);
		nvgTextAlign(ctx, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		float yScaleLeft = 0.5f;
		float xScaleLeft = 0.2f;
		Vector2f leftIconPos = mPos.<Float> cast()
				+ Vector2f(xScaleLeft * theme().mTabControlWidth, yScaleLeft * mSize.<Float> cast().y);
		nvgText(ctx, leftIconPos.x, leftIconPos.y + 1, iconLeft.data(), null);

		// Right button.
		active = mVisibleEnd != tabCount();
		// Draw the arrow.
		nvgBeginPath(ctx);
		// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit
		// typing in Java:
		auto iconRight = utf8(mTheme.mTabHeaderRightIcon);
		fontSize = mFontSize == -1 ? mTheme.mButtonFontSize : mFontSize;
		ih = fontSize;
		ih *= icon_scale();
		nvgFontSize(ctx, ih);
		nvgFontFace(ctx, "icons");
		float rightWidth = nvgTextBounds(ctx, 0, 0, iconRight.data(), null, null);
		if (active) {
			arrowColor = mTheme.mTextColor;
		} else {
			arrowColor = mTheme.mButtonGradientBotPushed;
		}
		nvgFillColor(ctx, arrowColor);
		nvgTextAlign(ctx, NVG_ALIGN_LEFT | NVG_ALIGN_MIDDLE);
		float yScaleRight = 0.5f;
		float xScaleRight = 1.0f - xScaleLeft - rightWidth / theme().mTabControlWidth;
		Vector2f rightIconPos = mPos.<Float> cast()
				+ Vector2f(mSize.<Float> cast().x, mSize.<Float> cast().y * yScaleRight)
				- Vector2f(xScaleRight * theme().mTabControlWidth + rightWidth, 0);

		nvgText(ctx, rightIconPos.x, rightIconPos.y + 1, iconRight.data(), null);
	}

	private TabHeader.ClickLocation locateClick(CCVector2i p) {
		// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit
		// typing in Java:
		auto leftDistance = (p - mPos).array;
		boolean hitLeft = (leftDistance >= 0).all()
				&& (leftDistance < CCVector2i(theme().mTabControlWidth, mSize.y).array).all();
		if (hitLeft) {
			return ClickLocation.LeftControls;
		}
		// C++ TO JAVA CONVERTER TODO TASK: There is no equivalent to implicit
		// typing in Java:
		auto rightDistance = (p - (mPos + CCVector2i(mSize.x - theme().mTabControlWidth, 0))).array;
		boolean hitRight = (rightDistance >= 0).all()
				&& (rightDistance < CCVector2i(theme().mTabControlWidth, mSize.y).array).all();
		if (hitRight) {
			return ClickLocation.RightControls;
		}
		return ClickLocation.TabButtons;
	}

	private void onArrowLeft() {
		if (mVisibleStart == 0) {
			return;
		}
		--mVisibleStart;
		calculateVisibleEnd();
	}

	private void onArrowRight() {
		if (mVisibleEnd == tabCount()) {
			return;
		}
		++mVisibleStart;
		calculateVisibleEnd();
	}

}