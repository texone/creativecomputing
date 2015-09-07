package cc.creativecomputing.core.io.format;

import java.util.List;
import java.util.Map;

public interface CCDataHolder<MapType extends Map<String, Object>, ListType extends List<Object>> {

	public MapType createMap();
	
	public ListType createList();
}
