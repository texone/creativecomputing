package cc.creativecomputing.uinano;

import java.util.*;

import cc.creativecomputing.core.events.CCBooleanEvent;
import cc.creativecomputing.core.events.CCIntEvent;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCVector2;
import cc.creativecomputing.math.CCVector2i;
import cc.creativecomputing.uinano.layout.GroupLayout;

/**
 * \class ComboBox combobox.h nanogui/combobox.h
 *
 * \brief Simple combo box widget based on a popup button.
 */
public class ComboBox extends PopupButton {

	/// The items associated with this ComboBox.
	protected ArrayList<String> mItems = new ArrayList<String>();

	/// The short descriptions of items associated with this ComboBox.
	protected ArrayList<String> mItemsShort = new ArrayList<String>();

	/// The callback for this ComboBox.
	protected CCListenerManager<CCIntEvent> mCallback = CCListenerManager.create(CCIntEvent.class);

	/// The current index this ComboBox has selected.
	protected int mSelectedIndex;

	/// Create an empty combo box
	public ComboBox(CCWidget parent) {
		super(parent, "", 0);
		this.mSelectedIndex = 0;
	}

	/// Create a new combo box with the given items
	public ComboBox(CCWidget parent, ArrayList<String> items) {
		super(parent, "", 0);
		this.mSelectedIndex = 0;
		setItems(items);
	}

	/**
	 * \brief Create a new combo box with the given items, providing both short
	 * and long descriptive labels for each item
	 */
	public ComboBox(CCWidget parent, ArrayList<String> items, ArrayList<String> itemsShort) {
		super(parent, "", 0);
		this.mSelectedIndex = 0;
		setItems(items, itemsShort);
	}

	/// The current index this ComboBox has selected.
	public final int selectedIndex() {
		return mSelectedIndex;
	}

	/// Sets the current index this ComboBox has selected.
	public final void setSelectedIndex(int idx) {
		if (mItemsShort.isEmpty()) {
			return;
		}
		ArrayList<CCWidget> children = popup().children();
		((Button) children.get(mSelectedIndex)).setPushed(false);
		((Button) children.get(idx)).setPushed(true);
		mSelectedIndex = idx;
		setCaption(mItemsShort.get(idx));
	}

	/// Sets the items for this ComboBox, providing both short and long
	/// descriptive lables for each item.
	public final void setItems(ArrayList<String> items, ArrayList<String> itemsShort) {
		assert items.size() == itemsShort.size();
		mItems = items;
		mItemsShort = itemsShort;
		if (mSelectedIndex < 0 || mSelectedIndex >= (int) items.size()) {
			mSelectedIndex = 0;
		}
		while (mPopup.childCount() != 0) {
			mPopup.removeChild(mPopup.childCount() - 1);
		}
		mPopup.setLayout(new GroupLayout(10));
		int index = 0;
		for (String str : items) {
			Button button = new Button(mPopup, str);
			button.index = index;
			button.setFlags(Flags.RadioButton);
			button.mCallback.add(() -> {
				mSelectedIndex = button.index;
				setCaption(mItemsShort.get(button.index));
				setPushed(false);
				popup().setVisible(false);

				mCallback.proxy().event(button.index);

			});
			index++;
		}
		setSelectedIndex(mSelectedIndex);
	}

	/// Sets the items for this ComboBox.
	public final void setItems(ArrayList<String> items) {
		setItems(items, items);
	}

	/// The items associated with this ComboBox.
	public final ArrayList<String> items() {
		return mItems;
	}

	/// The short descriptions associated with this ComboBox.
	public final ArrayList<String> itemsShort() {
		return mItemsShort;
	}

	/// Handles mouse scrolling events for this ComboBox.
	@Override
	public boolean scrollEvent(CCVector2i p, CCVector2 rel) {
		if (rel.y < 0) {
			setSelectedIndex(Math.min(mSelectedIndex + 1, (int) (items().size() - 1)));

			mCallback.proxy().event(mSelectedIndex);

			return true;
		} else if (rel.y > 0) {
			setSelectedIndex(Math.max(mSelectedIndex - 1, 0));

			mCallback.proxy().event(mSelectedIndex);

			return true;
		}
		return super.scrollEvent(p, rel);
	}

	/// Saves the state of this ComboBox to the specified Serializer.
	@Override
	public void save(CCDataElement s) {
		super.save(s);
		// s.set("items", mItems);
		// s.set("itemsShort", mItemsShort);
		s.addAttribute("selectedIndex", mSelectedIndex);
	}

	/// Sets the state of this ComboBox from the specified Serializer.
	@Override
	public boolean load(CCDataElement s) {
		if (!super.load(s)) {
			return false;
		}

		return true;
	}

}