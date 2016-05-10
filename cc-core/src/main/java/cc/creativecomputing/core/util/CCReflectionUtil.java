package cc.creativecomputing.core.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.logging.CCLog;


public class CCReflectionUtil {
	
	public static class CCReflectionException extends RuntimeException{

		/**
		 * 
		 */
		private static final long serialVersionUID = -1063278621698200509L;

		public CCReflectionException() {
			super();
		}

		public CCReflectionException(String theMessage, Throwable theCause) {
			super(theMessage, theCause);
		}

		public CCReflectionException(String theMessage) {
			super(theMessage);
		}

		public CCReflectionException(Throwable theCause) {
			super(theCause);
		}
		
	}
	
	public static abstract class CCMember<Type extends Annotation>{

		protected Object _myParent;
		private Type _myAnnotation;
		
		private Member _myMember;
		
		public CCMember(Member theMember, Object theParent, Class<Type> theAnnotation){
			_myParent = theParent;
			if(theMember instanceof Field){
				_myAnnotation = theAnnotation == null ? null :((Field) theMember).getAnnotation(theAnnotation);
			}
			if(theMember instanceof Method){
				_myAnnotation = theAnnotation == null ? null :((Method) theMember).getAnnotation(theAnnotation);
			}
			_myMember = theMember;
		}
		
		public Type annotation(){
			return _myAnnotation;
		}
		
		public String name(){
			return _myMember.getName();
		}
		
		
		public String memberName(){
			return _myMember.getName();
		}
		
		public abstract Object value();
		
		public abstract void value(Object theObject);
		
		public abstract Class<?> type();
	}
	
	public static class CCField<Type extends Annotation> extends CCMember<Type> {
		private Field _myField;
		
		public CCField(Field theField, Object theParent, Class<Type> theAnnotation){
			super(theField, theParent, theAnnotation);
			_myField = theField;
		}
		
		public Object value(){
			try {
				return _myField.get(_myParent);
			} catch (Exception e) {
				throw new CCReflectionException(e);
			}
		}
		
		public Float floatValue(){
			try {
				return _myField.getFloat(_myParent);
			} catch (Exception e) {
				throw new CCReflectionException(e);
			}
		}
		
		@Override
		public void value(Object theObject){
			try {
				_myField.set(_myParent, theObject);
			}catch(IllegalArgumentException iae){
				
			}catch (Exception e) {
				throw new CCReflectionException(e);
			}
		}
		
		@Override
		public Class<?> type() {
			return _myField.getType();
		}
		
		public Field field(){
			return _myField;
		}
	}
	
	@SuppressWarnings("rawtypes")
	public static class CCMapEntry extends CCMember{

		private Map<Object, Object> _myMap;
		private Object _myKey;
		
		@SuppressWarnings("unchecked")
		public CCMapEntry(Map<Object, Object> theMap, Object theKey) {
			super(null, null, null);
			_myMap = theMap;
			_myKey = theKey;
		}
		
		@Override
		public String name() {
			return _myKey.toString();
		}
		
		@Override
		public String memberName() {
			return _myKey.toString();
		}

		@Override
		public Object value() {
			return _myMap.get(_myKey);
		}

		@Override
		public void value(Object theObject) {
			_myMap.put(_myKey, theObject);
		}

		@Override
		public Class type() {
			return _myMap.get(_myKey).getClass();
		}
		
	}
	
	public static Field getField(final Class<?> theClass, final String theFieldName) {
		try{
			return theClass.getDeclaredField(theFieldName);
		}catch(Exception e){
			if(theClass.getSuperclass() == null) return null;
			
			return getField(theClass.getSuperclass(), theFieldName);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static CCField<?> getField(Object theObject, final String theFieldName){
		Field myField = CCReflectionUtil.getField(theObject.getClass(), theFieldName);
		if(myField == null)return null;
		
		return new CCField(myField, theObject, null);
	}
	
	public static void getFields(final Class<?> theClass, final List<Field> theFields) {
		if(theClass.getSuperclass() == null) return;
			
		getFields(theClass.getSuperclass(), theFields);
		
		for (Field myField : theClass.getDeclaredFields()) {
			myField.setAccessible(true);
			theFields.add(myField);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<CCField<?>> getFields(Object theObject){
		List<Field> myFields = new ArrayList<>();
		CCReflectionUtil.getFields(theObject.getClass(), myFields);
		
		List<CCField<?>> myResult = new ArrayList<>();
		for(Field myField:myFields){
			try {
				//Object myValue = myField.get(theObject);
				myResult.add(new CCField(myField, theObject, null));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} 
		}
		return myResult;
	}
	
	public static void getFields(final Class<?> theClass, final Class<? extends Annotation>theAnnotation, final List<Field> theFields) {
		if(theClass.getSuperclass() == null) return;
			
		getFields(theClass.getSuperclass(), theAnnotation, theFields);
		
		for (Field myField : theClass.getDeclaredFields()) {
			myField.setAccessible(true);
			Annotation myAnnotation = myField.getAnnotation(theAnnotation);
			if (myAnnotation != null)theFields.add(myField);
		}
	}
	
	public static <Type extends Annotation>List<CCField<Type>> getFields(Object theObject, Class<Type> theAnnotation){
		List<Field> myFields = new ArrayList<>();
		CCReflectionUtil.getFields(theObject.getClass(), theAnnotation, myFields);
		
		List<CCField<Type>> myResult = new ArrayList<>();
		for(Field myField:myFields){
			try {
				myResult.add(new CCField<Type>(myField, theObject, theAnnotation));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return myResult;
	}
	
	public static class CCMethod<Type extends Annotation> extends CCMember<Type> {
		private Method _mySetMethod;
		private Method _myGetMethod;
		private Object _myValue;
		
		public CCMethod(Method theSetMethod, Method theGetMethod, Object theParent, Class<Type> theAnnotation){
			super(theSetMethod == null ? theGetMethod : theSetMethod, theParent, theAnnotation);
			_mySetMethod = theSetMethod;
			_myGetMethod = theGetMethod;
			
//			if(_mySetMethod != null && _myGetMethod != null && )
		}
		
//		public Object value(){
//			try {
//				return _myField.get(_myParent);
//			} catch (Exception e) {
//				throw new CCReflectionException(e);
//			}
//		}
		
		@Override
		public void value(Object theObject){
			_myValue = theObject;
			if(_mySetMethod == null)return;
			try {
				_mySetMethod.invoke(_myParent, theObject);
			} catch (Exception e) {
				Class<?>[] myParameters = _mySetMethod.getParameterTypes();
				for(Class<?> myParameter:myParameters){
					CCLog.info(myParameter.getName());
				}
				throw new CCReflectionException("Could not call: " + _myParent.getClass().getName() + "." + _mySetMethod.getName(),e);
			}
		}
		
		public void trigger(Object...theObjects){
			try {
				_mySetMethod.invoke(_myParent, theObjects);
			} catch (Exception e) {
				throw new CCReflectionException(e);
			}
		}
		
		public Method method(){
			return _mySetMethod == null ? _myGetMethod : _mySetMethod;
		}

		@Override
		public Object value() {
			if(_myGetMethod != null){
				try {
					return _myGetMethod.invoke(_myParent);
				} catch (Exception e) {
					throw new CCReflectionException(e);
				}
			}
			if(_myValue == null)return null;
			return _myValue;
		}

		@Override
		public Class<?> type() {
			if(_myGetMethod != null)return _myGetMethod.getReturnType();
			if(_mySetMethod.getParameterCount() <= 0)return null;
			return _mySetMethod.getParameterTypes()[0];
		}
		
	}
	
	public static void getMethods(final Class<?> theClass, final Class<? extends Annotation>theAnnotation, final List<Method> theMethods) {
		if(theClass.getSuperclass() == null) return;
			
		getMethods(theClass.getSuperclass(), theAnnotation, theMethods);
		
		for (Method myMethod : theClass.getDeclaredMethods()) {
			myMethod.setAccessible(true);
			
			Annotation myAnnotation = myMethod.getAnnotation(theAnnotation);
			if (myAnnotation != null)theMethods.add(myMethod);
		}
	}
	
	public static void getMethods(final Class<?> theClass, final List<Method> theMethods) {
		if(theClass.getSuperclass() == null) return;
			
		getMethods(theClass.getSuperclass(), theMethods);
		
		for (Method myMethod : theClass.getDeclaredMethods()) {
			myMethod.setAccessible(true);
			theMethods.add(myMethod);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static List<CCMethod<?>> getMethods(Object theObject){
		List<Method> myMethods = new ArrayList<>();
		getMethods(theObject.getClass(), myMethods);
		
		List<CCMethod<?>> myResult = new ArrayList<>();
		for(Method myMethod:myMethods){
			try {
				//Object myValue = myMethod.get(theObject);
				myResult.add(new CCMethod(myMethod, null,theObject, null));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} 
		}
		return myResult;
	}
	
	public static <Type extends Annotation>List<CCMethod<Type>> getMethods(Object theObject, Class<Type> theAnnotation){
		List<Method> myMethods = new ArrayList<>();
		getMethods(theObject.getClass(), theAnnotation, myMethods);
		
		List<CCMethod<Type>> myResult = new ArrayList<>();
		for(Method myMethod:myMethods){
			String myName = myMethod.getName();
			Method myGetter = null;
			Method mySetter = null;
			if(myMethod.getReturnType().equals(Void.TYPE)){
				mySetter = myMethod;
			}else{
				myGetter = myMethod;
			}
			for(Method myMethod2:myMethods){
				if(myMethod == myMethod2)continue;
				if(myMethod2.getName().equals(myName)){
					if(myMethod2.getReturnType().equals(Void.TYPE)){
						mySetter = myMethod2;
					}else{
						myGetter = myMethod2;
					}
				}
			}
			try {
				myResult.add(new CCMethod<Type>(mySetter,myGetter, theObject, theAnnotation));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		return myResult;
	}
	
	public static boolean isSubclassOf(Class<?> theTestClass, Class<?> theClass){
		
		while(theTestClass != Object.class){
			if(theTestClass == theClass){
				return true;
			}
			theTestClass = theTestClass.getSuperclass();
		}
		
		return false;
	}
}
