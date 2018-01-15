package cc.creativecomputing.core.io.format;

import java.util.Map;

public interface CCDataSerializable{

	void data(Map<String, Object> theData);
	
	Map<String,Object> data();
	
}