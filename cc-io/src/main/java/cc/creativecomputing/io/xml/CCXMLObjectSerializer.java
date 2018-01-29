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
package cc.creativecomputing.io.xml;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.xml.property.CCXMLProperty;
import cc.creativecomputing.io.xml.property.CCXMLPropertyObject;

/**
 * @author christianriekoff
 *
 */
@SuppressWarnings({"rawtypes","hiding","unchecked"})
public class CCXMLObjectSerializer {
	
	private Map<String, Class> _myNodeClassMap;

	public CCXMLObjectSerializer(Map<String,Class> theMap) {
		_myNodeClassMap = theMap;
	}
	
	public CCXMLObjectSerializer() {
		this(new HashMap<String, Class>());
	}
	
	/**
	 * Checks if the given class is existent or if it is defined in the node map or 
	 * as xml attribute.
	 * @param <Type>
	 * @param theClass
	 * @return
	 */
	private <Type> Class<Type> checkClass(CCDataElement theElement, Class<Type> theClass){
		
		if(_myNodeClassMap != null && _myNodeClassMap.containsKey(theElement.name()))return _myNodeClassMap.get(theElement.name());
		if(theElement.hasAttribute("type") && _myNodeClassMap != null) {
			String myType = theElement.attribute("type");
			if(_myNodeClassMap.containsKey(myType))return _myNodeClassMap.get(myType);
		}
		if(theElement.hasAttribute("class")) {
			try {
				return (Class<Type>) ClassLoader.getSystemClassLoader().loadClass(theElement.attribute("class"));
			} catch (ClassNotFoundException e) {
			}
		}
		if(theClass != null) return theClass;
		throw new CCXMLException("Can not create Object because the class for the node: " + theElement.name() + " int line:" + theElement.line() +" is undefined. Pass a class or make sure you add a node class mapping.");
	}
	
	private void getFields(final Class<?> theClass, final List<Field> theFields) {
		if(theClass.getSuperclass() == null) return;
			
		getFields(theClass.getSuperclass(), theFields);
		
		for (Field myField : theClass.getDeclaredFields()) {
			myField.setAccessible(true);

			CCXMLProperty myControl = myField.getAnnotation(CCXMLProperty.class);

			if (myControl != null)theFields.add(myField);
		}
	}
	
	/**
	 * Checks if the given attribute is optional and existent in the XML. If the
	 * attribute is not existent but required an exception is thrown.
	 * @param theAttribute
	 * @return
	 */
	private boolean readAttribute(CCDataElement theElement, CCXMLProperty theAttribute) {
		if(theAttribute == null)return false;
		if(theAttribute.node()) {
			if(theElement.child(theAttribute.name()) == null) {
				if(theAttribute.optional())return false;
				else {
					throw new CCXMLException(
						"\nError reading Attribute from xml element " + theElement.name() +" in line:" + theElement.line() + "\n" +
						"The required attribute: >>" + theAttribute.name() + "<< needs to be defined as node in your xml"
					);
				}
			}
		}else {
			if(!theElement.hasAttribute(theAttribute.name())) {
				if(theAttribute.optional())return false;
				else {
					throw new CCXMLException(
						"\nError reading Attribute from xml element " + theElement.name() +" in line:" + theElement.line() + "\n" +
						"The required attribute: >>" + theAttribute.name() + "<< needs to be defined as attribute in your xml"
					);
				}
			}
		}
		return true;
	}
	
	private boolean isCollection(Class theClass) {
		for(Class myInterface:theClass.getInterfaces()) {
			if(myInterface.equals(Collection.class)) {
				return true;
			}
		}
		return false;
	}
	
	private void setPrimitiveField(CCDataElement theElement, CCXMLProperty theAttribute, Class theClass, Field theField, Object theObject) throws IllegalArgumentException, IllegalAccessException {
		if (theClass == Float.TYPE) {
			if(theAttribute.node())theField.setFloat(theObject,theElement.child(theAttribute.name()).floatContent());
			else theField.setFloat(theObject,theElement.floatAttribute(theAttribute.name()));
		}
		if (theClass == Double.TYPE) {
			if(theAttribute.node())theField.setDouble(theObject,theElement.doubleAttribute(theAttribute.name()));
			else theField.setDouble(theObject,theElement.child(theAttribute.name()).doubleContent());
		}
		if (theClass == Integer.TYPE) {
			if(theAttribute.node())theField.setInt(theObject,theElement.child(theAttribute.name()).intContent());
			else theField.setInt(theObject,theElement.intAttribute(theAttribute.name()));
		}
		if (theClass == Boolean.TYPE) {
			if(theAttribute.node())theField.setBoolean(theObject,theElement.child(theAttribute.name()).booleanContent());
			else theField.setBoolean(theObject,theElement.booleanAttribute(theAttribute.name()));
		}
	}
	
	private void setPrimitiveMethod(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Method myMethod, Object myObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (myClass == Float.TYPE) {
			if(myAttribute.node())myMethod.invoke(myObject, theElement.child(myAttribute.name()).floatContent());
			else myMethod.invoke(myObject, theElement.floatAttribute(myAttribute.name()));
		}
		if (myClass == Double.TYPE) {
			if(myAttribute.node())myMethod.invoke(myObject, theElement.child(myAttribute.name()).doubleContent());
			else myMethod.invoke(myObject, theElement.doubleAttribute(myAttribute.name()));
		}
		if (myClass == Integer.TYPE) {
			if(myAttribute.node())myMethod.invoke(myObject, theElement.child(myAttribute.name()).intContent());
			else myMethod.invoke(myObject, theElement.intAttribute(myAttribute.name()));
		}
		if (myClass == Boolean.TYPE) {
			if(myAttribute.node())myMethod.invoke(myObject, theElement.child(myAttribute.name()).booleanContent());
			else myMethod.invoke(myObject, theElement.booleanAttribute(myAttribute.name()));
		}
	}
	
	private void setArrayField(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Field myField, Object myObject) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Class myArrayType = myClass.getComponentType();
		Method myMethod = null;
		if(myArrayType.isEnum()) {
			myMethod = myArrayType.getMethod("valueOf", new Class<?>[] {String.class});
		}
		
		if(myAttribute.node()) {
			List<CCDataElement> myArrayElements = theElement.child(myAttribute.name()).children();

			Object myArrayObject = Array.newInstance(myArrayType, myArrayElements.size());
			
			int myIndex = 0;
			
			for(CCDataElement myArrayElement:myArrayElements) {
				if(myArrayType == Float.TYPE) {
					Array.setFloat(myArrayObject, myIndex++, myArrayElement.floatContent());
				}else if(myArrayType == Boolean.TYPE) {
					Array.setBoolean(myArrayObject, myIndex++, myArrayElement.booleanContent());
				}else if(myArrayType == Integer.TYPE) {
					Array.setInt(myArrayObject, myIndex++, myArrayElement.intContent());
				}else if(myArrayType == Double.TYPE) {
					Array.setDouble(myArrayObject, myIndex++, myArrayElement.doubleContent());
				}else if(myArrayType == String.class) {
					Array.set(myArrayObject, myIndex++, myArrayElement.content());
				}else if(myArrayType.isEnum()) {
					Array.set(myArrayObject, myIndex++,myMethod.invoke(null, new Object[] {myArrayElement.content()}));
				}else {
					Array.set(myArrayObject, myIndex++, toObject(myArrayElement));
				}
			}
			myField.set(myObject, myArrayObject);
		}else {
			if(myArrayType.isPrimitive() || myArrayType.isEnum() || myArrayType == String.class) {
				String myValueString = theElement.attribute(myAttribute.name());
				String[] myValues = myValueString.split(",");
				Object myArrayObject = Array.newInstance(myArrayType, myValues.length);
				int myIndex = 0;
				
				for(String myValue:myValues) {
					if(myArrayType == Float.TYPE) {
						Array.setFloat(myArrayObject, myIndex++, Float.parseFloat(myValue));
					}
					if(myArrayType == Boolean.TYPE) {
						Array.setBoolean(myArrayObject, myIndex++, Boolean.parseBoolean(myValue));
					}
					if(myArrayType == Integer.TYPE) {
						Array.setInt(myArrayObject, myIndex++, Integer.parseInt(myValue));
					}
					if(myArrayType == Double.TYPE) {
						Array.setDouble(myArrayObject, myIndex++, Double.parseDouble(myValue));
					}
					if(myArrayType == String.class) {
						Array.set(myArrayObject, myIndex++, myValue);
					}
					if(myArrayType.isEnum()) {
						Array.set(myArrayObject, myIndex++,myMethod.invoke(null, new Object[] {theElement.attribute(myAttribute.name())}));
					}
				}
				myField.set(myObject, myArrayObject);
			}else {
				throw new CCXMLException(
					"To store arrays in xml attributes is only allowed for primitive types" +
					"Strings and enums. Your attribute: " + myAttribute.name() +
					" is of the array type: " + myArrayType.getName()
				);
			}
		}
	}
	
	private void setCollectionField(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Field myField, Object myObject) throws ArrayIndexOutOfBoundsException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {
		Type myGenericType = myField.getGenericType();
		Class myCollectionGenericType = Object.class;
		
		if(myGenericType instanceof ParameterizedType) {
			ParameterizedType genericFieldType = (ParameterizedType)myField.getGenericType();
			myGenericType = genericFieldType.getActualTypeArguments()[0];
			
			if(myGenericType instanceof Class) {
				myCollectionGenericType = (Class)myGenericType;
			}
		}
		
		Method myMethod = null;
		if(myCollectionGenericType.isEnum()) {
			myMethod = myCollectionGenericType.getMethod("valueOf", new Class<?>[] {String.class});
		}
		
		Collection myCollection;
		if(myField.getType() == List.class) {
			myCollection = new ArrayList();
		}else {
			myCollection = (Collection)createObject(myField.getType());
		}
		
		if(myAttribute.node()) {
			List<CCDataElement> myArrayElements = theElement.child(myAttribute.name()).children();
			
			for(CCDataElement myArrayElement:myArrayElements) {
				if(myCollectionGenericType == Float.class) {
					myCollection.add(myArrayElement.floatContent());
				}else if(myCollectionGenericType == Boolean.class) {
					myCollection.add(myArrayElement.booleanContent());
				}else if(myCollectionGenericType == Integer.class) {
					myCollection.add(myArrayElement.intContent());
				}else if(myCollectionGenericType == Double.class) {
					myCollection.add(myArrayElement.doubleContent());
				}else if(myCollectionGenericType == String.class) {
					myCollection.add(myArrayElement.content());
				}else if(myCollectionGenericType.isEnum()) {
					myCollection.add(myMethod.invoke(null, new Object[] {myArrayElement.content()}));
				}else {
					myCollection.add(toObject(myArrayElement));
				}
			}
			myField.set(myObject, myCollection);
		}else {
			if(
				myCollectionGenericType == Float.class || 
				myCollectionGenericType == Integer.class || 
				myCollectionGenericType == Boolean.class || 
				myCollectionGenericType == Double.class || 
				myCollectionGenericType.isEnum() || 
				myCollectionGenericType == String.class
			) {
				String myValueString = theElement.attribute(myAttribute.name());
				String[] myValues = myValueString.split(",");
				
				for(String myValue:myValues) {
					if(myCollectionGenericType == Float.class) {
						myCollection.add(Float.parseFloat(myValue));
					}
					if(myCollectionGenericType == Boolean.class) {
						myCollection.add(Boolean.parseBoolean(myValue));
					}
					if(myCollectionGenericType == Integer.class) {
						myCollection.add(Integer.parseInt(myValue));
					}
					if(myCollectionGenericType == Double.class) {
						myCollection.add(Double.parseDouble(myValue));
					}
					if(myCollectionGenericType == String.class) {
						myCollection.add(myValue);
					}
					if(myCollectionGenericType.isEnum()) {
						myCollection.add(myMethod.invoke(null, new Object[] {theElement.attribute(myAttribute.name())}));
					}
				}
				myField.set(myObject, myCollection);
			}else {
				throw new CCXMLException(
					"To store arrays in xml attributes is only allowed for primitive types" +
					"Strings and enums. Your attribute: " + myAttribute.name() +
					" is of the array type: " + myCollectionGenericType.getName()
				);
			}
		}
	}
		
	
	private void setEnumField(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Field myField, Object myObject) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method myMethod = myClass.getMethod("valueOf", new Class<?>[] {String.class});
		if(myAttribute.node()) {
			Object bla = myMethod.invoke(null, new Object[] {theElement.child(myAttribute.name()).content()});
			myField.set(myObject, bla);
		}else {
			Object bla = myMethod.invoke(null, new Object[] {theElement.attribute(myAttribute.name())});
			myField.set(myObject, bla);
		}
	}
	
	private void setEnumMethod(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Method myMethod, Object myObject) throws SecurityException, NoSuchMethodException, IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method myValueOfMethod = myClass.getMethod("valueOf", new Class<?>[] {String.class});
		if(myAttribute.node()) {
			Object bla = myValueOfMethod.invoke(null, new Object[] {theElement.child(myAttribute.name()).content()});
			myMethod.invoke(myObject, bla);
		}else {
			Object bla = myValueOfMethod.invoke(null, new Object[] {theElement.attribute(myAttribute.name())});
			myMethod.invoke(myObject, bla);
		}
	}
	
	private void setStringField(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Field myField, Object myObject) throws IllegalArgumentException, IllegalAccessException {
		if(myAttribute.node())myField.set(myObject,theElement.child(myAttribute.name()).content());
		else myField.set(myObject,theElement.attribute(myAttribute.name()));
	}
	
	private void setStringMethod(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Method myMethod, Object myObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if(myAttribute.node())myMethod.invoke(myObject, theElement.child(myAttribute.name()).content());
		else myMethod.invoke(myObject, theElement.attribute(myAttribute.name()));
	}
	
	private void setObjectField(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Field myField, Object myObject) throws IllegalArgumentException, IllegalAccessException {
		CCDataElement myChild = theElement.child(myAttribute.name());
		
		if(myChild == null) {
			throw new CCXMLException(
				"\nError reading field value from xml element " + theElement.name() +" in line:" + theElement.line() + "\n" +
				"The required attribute: >>" + myAttribute.name() + "<< needs to be defined as node in your xml"
			);
		}
		
		myField.set(myObject, toObject(myChild, myClass));
	}
	
	private void setObjectMethod(CCDataElement theElement, CCXMLProperty myAttribute, Class myClass, Method myMethod, Object myObject) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException{
		CCDataElement myChild = theElement.child(myAttribute.name());
		
		if(myChild == null) {
			throw new CCXMLException(
				"\nError reading field value from xml element " + theElement.name() +" in line:" + theElement.line() + "\n" +
				"The required attribute: >>" + myAttribute.name() + "<< needs to be defined as node in your xml"
			);
		}
		
		myMethod.invoke(myObject, toObject(theElement.child(myAttribute.name()),myClass));
	}
	
	private <ObjectType> ObjectType createObject(Class<ObjectType> theClass){
		try {
			Constructor<ObjectType> myConstructor = theClass.getDeclaredConstructor();
			myConstructor.setAccessible(true);
			return myConstructor.newInstance();
		} catch (Exception e) {
			throw new CCXMLException("The Object could not be created because it has no default constructor with no parameters", e);
		}
	}
	
	/**
	 * Creates an object from the given xml
	 * @param <Type>
	 * @param theElement
	 * @param theClass
	 * @return
	 */
	public <Type> Type toObject(CCDataElement theElement, Class<Type> theClass){
		theClass = checkClass(theElement, theClass);
		
//		CCXMLPropertyObject myXLNode = theClass.getAnnotation(CCXMLPropertyObject.class);
//		if(myXLNode == null) {
//			throw new CCXMLException("The given class: " +theClass.getName() +" does not support xml serialization. It must be taged with the CCXMLNode annotation.");
//		}
		
		Type myObject = createObject(theClass);
		
		List<Field> myFields = new ArrayList<Field>();
		getFields(theClass, myFields);
		
		for (Field myField : myFields) {
			myField.setAccessible(true);

			CCXMLProperty myAttribute = myField.getAnnotation(CCXMLProperty.class);
			
			if(!readAttribute(theElement, myAttribute))continue;
			CCLog.info(theElement, myAttribute.name());
			Class<?> myClass = myField.getType();
			try {
				if(myClass.isPrimitive()) {
					setPrimitiveField(theElement, myAttribute, myClass, myField, myObject);
				}else if(myClass.isArray()){
					setArrayField(theElement, myAttribute, myClass, myField, myObject);
				}else if(myClass.isEnum()){
					setEnumField(theElement, myAttribute, myClass, myField, myObject);
				}else if(myClass.equals(String.class)){
					setStringField(theElement, myAttribute, myClass, myField, myObject);
				}else if(isCollection(myClass)){
					setCollectionField(theElement, myAttribute, myClass, myField, myObject);
				}else {
					setObjectField(theElement, myAttribute, myClass, myField, myObject);
				}
			}catch(Exception e) {
				throw new CCXMLException("Not able to build object from xml.",e);
			}
		}
		
		for (Method myMethod : theClass.getDeclaredMethods()) {
			myMethod.setAccessible(true);
			CCXMLProperty myAttribute = myMethod.getAnnotation(CCXMLProperty.class);

			if(!readAttribute(theElement, myAttribute))continue;

			Class<?>[] _myParameters = myMethod.getParameterTypes();
			if(_myParameters.length != 1)continue;
			
			Class<?> myClass = _myParameters[0];

			try {
				if(myClass.isPrimitive()) {
					setPrimitiveMethod(theElement, myAttribute, myClass, myMethod, myObject);
				}else if(myClass.isEnum()){
					setEnumMethod(theElement, myAttribute, myClass, myMethod, myObject);
				}else if(myClass.equals(String.class)){
					setStringMethod(theElement, myAttribute, myClass, myMethod, myObject);
				}else {
					setObjectMethod(theElement, myAttribute, myClass, myMethod, myObject);
				}
			}catch(Exception iae) {
				throw new RuntimeException("Not able to set xml attribute.",iae);
			}
		}
		
		return myObject;
	}
	
	/**
	 * Returns a list with all Objects of the given type.
	 * @param <Type>
	 * @param theClass
	 * @return
	 */
	public <Type> List<Type> getObjects(CCDataElement theElement, Class<Type> theClass){
		List<Type> myResult = new ArrayList<Type>();
		
		CCXMLPropertyObject myXMLNode = theClass.getAnnotation(CCXMLPropertyObject.class);
		if(myXMLNode == null)return myResult;
		
		String myNodeName = myXMLNode.name();
		
		for(CCDataElement myChild:theElement) {
			if(!myChild.name().equals(myNodeName))continue;
			
			myResult.add(toObject(myChild,theClass));
		}
		
		return myResult;
	}
	
	public Object toObject(CCDataElement theElement) {
		return toObject(theElement,null);
	}
	
	
	private void addChildToXml(CCDataElement theElement, Object theObject) {
		
		List<Field> myFields = new ArrayList<Field>();
		getFields(theObject.getClass(), myFields);
		
		for (Field myField : myFields) {
			myField.setAccessible(true);

			CCXMLProperty myAttribute = myField.getAnnotation(CCXMLProperty.class);
			
			if (myAttribute == null)
				continue;

			Class<?> myClass = myField.getType();

			try {
				if(myClass.isPrimitive()) {
					if (myClass == Float.TYPE) {
						if(myAttribute.node())theElement.createChild(myAttribute.name(), myField.getFloat(theObject));
						else theElement.addAttribute(myAttribute.name(), myField.getFloat(theObject));
					}
					if (myClass == Double.TYPE) {
						if(myAttribute.node())theElement.createChild(myAttribute.name(), myField.getDouble(theObject));
						else theElement.addAttribute(myAttribute.name(), myField.getDouble(theObject));
					}
					if (myClass == Integer.TYPE) {
						if(myAttribute.node())theElement.createChild(myAttribute.name(), myField.getInt(theObject));
						else theElement.addAttribute(myAttribute.name(), myField.getInt(theObject));
					}
					if (myClass == Boolean.TYPE) {
						if(myAttribute.node())theElement.createChild(myAttribute.name(), myField.getBoolean(theObject));
						else theElement.addAttribute(myAttribute.name(), myField.getBoolean(theObject));
					}
				}else if(myClass.isEnum()){
					if(myAttribute.node())theElement.createChild(myAttribute.name(), myField.get(theObject).toString());
					else theElement.addAttribute(myAttribute.name(), myField.get(theObject).toString());
				}else if(myClass.equals(String.class)){
					if(myAttribute.node())theElement.createChild(myAttribute.name(), myField.get(theObject).toString());
					else theElement.addAttribute(myAttribute.name(), myField.get(theObject).toString());
				}else {
					Object myObject = myField.get(theObject);
					CCXMLPropertyObject myXMLNode = myObject.getClass().getAnnotation(CCXMLPropertyObject.class);
					
					if(myXMLNode == null)continue;
					
					CCDataElement myFieldXML = new CCDataElement(myAttribute.name());
					myFieldXML.addAttribute("type", myXMLNode.name());
					myFieldXML.addAttribute("class", myObject.getClass().getName());
					theElement.addChild(myFieldXML);
					addChild(myFieldXML, myObject);
				}
			}catch(IllegalAccessException iae) {
				throw new RuntimeException("Not able to set xml attribute.",iae);
			}
		}
		
		for (Method myMethod : theObject.getClass().getDeclaredMethods()) {
			myMethod.setAccessible(true);
			CCXMLProperty myAttribute = myMethod.getAnnotation(CCXMLProperty.class);

			if (myAttribute == null)
				continue;

			Class<?>[] _myParameters = myMethod.getParameterTypes();
			if(_myParameters.length > 0)continue;
			
			Class<?> myClass = myMethod.getReturnType();

			try {
				if(myClass.isPrimitive()) {
					if (myClass == Float.TYPE) {
						if(myAttribute.node())theElement.createChild(myAttribute.name(), (Float)myMethod.invoke(theObject));
						else theElement.addAttribute(myAttribute.name(), (Float)myMethod.invoke(theObject));
					}
					if (myClass == Double.TYPE) {
						if(myAttribute.node())theElement.createChild(myAttribute.name(), (Double)myMethod.invoke(theObject));
						else theElement.addAttribute(myAttribute.name(), (Double)myMethod.invoke(theObject));
					}
					if (myClass == Integer.TYPE) {
						if(myAttribute.node())theElement.createChild(myAttribute.name(), (Integer)myMethod.invoke(theObject));
						else theElement.addAttribute(myAttribute.name(), (Integer)myMethod.invoke(theObject));
					}
					if (myClass == Boolean.TYPE) {
						if(myAttribute.node())theElement.createChild(myAttribute.name(), (Boolean)myMethod.invoke(theObject));
						else theElement.addAttribute(myAttribute.name(), (Boolean)myMethod.invoke(theObject));
					}
				}else if(myClass.isEnum()){
					if(myAttribute.node())theElement.createChild(myAttribute.name(), myMethod.invoke(theObject).toString());
					else theElement.addAttribute(myAttribute.name(), myMethod.invoke(theObject).toString());
				}else if(myClass.equals(String.class)){
					if(myAttribute.node())theElement.createChild(myAttribute.name(), myMethod.invoke(theObject).toString());
					else theElement.addAttribute(myAttribute.name(), myMethod.invoke(theObject).toString());
				}else {
					Object myObject = myMethod.invoke(theObject);
					CCXMLPropertyObject myXMLNode = myObject.getClass().getAnnotation(CCXMLPropertyObject.class);
					
					if(myXMLNode == null)continue;
					
					CCDataElement myFieldXML = new CCDataElement(myAttribute.name());
					myFieldXML.addAttribute("type", myXMLNode.name());
					myFieldXML.addAttribute("class", myObject.getClass().getName());
					theElement.addChild(myFieldXML);
					addChildToXml(myFieldXML, myObject);
				}
			}catch(Exception iae) {
				throw new RuntimeException("Not able to set xml attribute.",iae);
			}
		}
	}
	
	/**
	 * Adds an object to the xml element, object is serialized using reflection, to
	 * save an object to xml it must be marked with {@linkplain CCXMLPropertyObject} annotation.
	 * @param theObject the object to add to the xml
	 */
	public void addChild(CCDataElement theElement, Object theObject) {
		CCXMLPropertyObject myXMLNode = theObject.getClass().getAnnotation(CCXMLPropertyObject.class);
		if(myXMLNode == null)return;
		
		CCDataElement myElement = new CCDataElement(myXMLNode.name());
		myElement.addAttribute("class", theObject.getClass().getName());
		theElement.addChild(myElement);
		addChildToXml(myElement, theObject);
	}
}
