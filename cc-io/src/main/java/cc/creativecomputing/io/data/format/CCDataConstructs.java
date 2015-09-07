package cc.creativecomputing.io.data.format;

import cc.creativecomputing.core.io.format.CCDataHolder;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataObject;

public class CCDataConstructs implements CCDataHolder<CCDataObject, CCDataArray>{

	@Override
	public CCDataObject createMap() {
		return new CCDataObject();
	}

	@Override
	public CCDataArray createList() {
		return new CCDataArray();
	}

}
