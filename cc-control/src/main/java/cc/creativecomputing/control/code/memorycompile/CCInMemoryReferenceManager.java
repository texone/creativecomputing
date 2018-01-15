package cc.creativecomputing.control.code.memorycompile;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil;

public class CCInMemoryReferenceManager {

	public static void copy(Object theSourceObject, Object theTargetObject) {
		Map<String,Field> mySourceFields = collectFields(theSourceObject.getClass(), new HashMap<>());
		Map<String,Field> myTargetFields = collectFields(theTargetObject.getClass(), new HashMap<>());
		
		for (String myFieldKey : mySourceFields.keySet()) {
			Field mySourceField = mySourceFields.get(myFieldKey);
			Field myTargetField = myTargetFields.get(myFieldKey);
			
//			if (mySourceField.getName().equals(myTargetField.getName()) && mySourceField.getType().equals(myTargetField.getType())) {
//				myTargetField.set(theTargetObject, mySourceField.get(theSourceObject));
//				continue;
//			}
//			
//			if(myTargetField.getType().getName().startsWith("recompile")){
//				CCLog.info(myTargetField.getType().getName());
//			}
//			
		}
	}

	private static Map<String,Field> collectFields(Class<?> c, Map<String, Field> theResult) {
		if(c.getSuperclass() == null)return theResult;
		
		for (Field myField : c.getDeclaredFields()) {
			myField.setAccessible(true);
			theResult.put(myField.getName(), myField);
		} 
		
		collectFields(c.getSuperclass(), theResult);
		return theResult;
	}

	private static Field findAndRemove(Field field, List<Field> fields) {
		for (Field actual:new ArrayList<>(fields)) {
			if (field.getName().equals(actual.getName()) && field.getType().equals(actual.getType())) {
				fields.remove(actual);
				return actual;
			}else{
				CCLog.info("not found", field.getName(), actual.getName(), field.getType(), actual.getType());
			}
		}
		return null;
	}
}
