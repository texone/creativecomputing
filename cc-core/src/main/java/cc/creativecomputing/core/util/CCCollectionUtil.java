package cc.creativecomputing.core.util;

import java.util.ArrayList;
import java.util.List;

public class CCCollectionUtil {
	
	static public<Type> List<Type> createList(Type...theArgs){
		List<Type> myResult = new ArrayList<>();
		for(Type myVal:theArgs) {
			myResult.add(myVal);
		}
		return myResult;
	}
}
