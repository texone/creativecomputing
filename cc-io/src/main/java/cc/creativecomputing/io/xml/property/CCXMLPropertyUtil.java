/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.io.xml.property;

import java.lang.reflect.Field;

/**
 * @author christianriekoff
 *
 */
public class CCXMLPropertyUtil {
	
	public static CCXMLProperty propertyAnnotation(Field theField, String theProperty) {
		CCXMLProperty myProperty = theField.getAnnotation(CCXMLProperty.class);
		if(myProperty == null)return null;
		if(!myProperty.name().equals(theProperty))return null;
		return myProperty;
	}
	
	@SuppressWarnings("unchecked")
	public static <Type> Type property(String theProperty, Object theObject) {
		String[] myProperties = theProperty.split("\\.");
		
//		CCPropertyObject myPropertyObject = theObject.getClass().getAnnotation(CCPropertyObject.class);
//		if(myPropertyObject == null) {
//			return null;
//		}
		
		Object myObject = theObject;
		Class<?> myClass = theObject.getClass();
		
		try {
		for(int i = 0; i < myProperties.length;i++) {
			String myPropertyName = myProperties[i];
			boolean myHasClass = false;
			
			for(Field myField:myClass.getDeclaredFields()) {
				myField.setAccessible(true);
				
				CCXMLProperty myProperty = propertyAnnotation(myField,myPropertyName);
				if(myProperty == null)continue;
				
				Type myObject2 = (Type)myField.get(myObject);
				if(myObject2 == null)return null;
				
				myClass = myObject2.getClass();
				myObject = myObject2;
				myHasClass = true;
				
				if(i == myProperties.length - 1) {
					return myObject2;
				}
			}
			
			if(myHasClass)continue;
			
			
		}
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	
}
