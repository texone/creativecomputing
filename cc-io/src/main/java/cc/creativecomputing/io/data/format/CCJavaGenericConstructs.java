package cc.creativecomputing.io.data.format;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.io.format.CCDataHolder;

public class CCJavaGenericConstructs implements CCDataHolder<Map<String,Object>, List<Object>>{

	@Override
	public Map<String, Object> createMap() {
		return new HashMap<String, Object>();
	}

	@Override
	public List<Object> createList() {
		return new ArrayList<Object>();
	}

}
