package cc.creativecomputing.core.io.format;

import java.util.Map;

public interface CCDataSerializable{

	public void data(Map<String,Object> theData);
	
	public Map<String,Object> data();
	
}