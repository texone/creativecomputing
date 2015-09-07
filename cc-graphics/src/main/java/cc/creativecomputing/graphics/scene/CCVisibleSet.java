package cc.creativecomputing.graphics.scene;

import java.util.ArrayList;
import java.util.List;

public class CCVisibleSet {

	private List<CCSpatial> mVisible;

	public CCVisibleSet() {
		mVisible = new ArrayList<>();
	}

	/**
	 * Access to the elements of the visible set.
	 * @return
	 */
	public int GetNumVisible() {
		return mVisible.size();
	}

	public List<CCSpatial> GetAllVisible() {
		return mVisible;
	}

	public CCSpatial GetVisible(int i) {
		if (i < 0 && i >= mVisible.size())
			throw new ArrayIndexOutOfBoundsException("Invalid index to GetVisible");
		return mVisible.get(i);
	}

	/**
	 * Insert a visible object into the set.
	 * @param visible
	 */
	public void Insert(CCSpatial visible) {

	}

	/**
	 * Set the number of elements to zero.
	 */
	public void Clear() {
		mVisible.clear();
	}

}
