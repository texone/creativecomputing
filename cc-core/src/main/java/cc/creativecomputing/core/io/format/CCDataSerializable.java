package cc.creativecomputing.core.io.format;

import java.util.Map;

public interface CCDataSerializable{
	public Map<String, Object> toDataObject(CCDataHolder<?, ?> theHolder);
	
}