package cc.creativecomputing.control;

import java.nio.file.Path;

import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;

public class CCPropertyMap {
	
	public static interface CCPropertyMapVisitor{
		
		public void onRoot(CCObjectPropertyHandle theRoot);
		
		public void onFloat(CCNumberPropertyHandle<Float> theHandle);
		
		public void onInt(CCNumberPropertyHandle<Integer> theHandle);
		
		public void onBoolean(CCBooleanPropertyHandle theHandle);
		
		public void onString(CCStringPropertyHandle theHandle);
		
		public void onEnum(CCEnumPropertyHandle theHandle);
		
		public void onColor(CCColorPropertyHandle theHandle);
		
		public void onEnvelope(CCEnvelopeHandle theHandle);
		
		public void onObject(CCObjectPropertyHandle theHandle);
	}
	
	public void visit(CCPropertyMapVisitor theVisitor){
		
		CCObjectPropertyHandle myRootHandle = rootHandle();
		
		theVisitor.onRoot(myRootHandle);
		visit(theVisitor, myRootHandle);
	}
	
	@SuppressWarnings("unchecked")
	private void visit(CCPropertyMapVisitor theVisitor, CCObjectPropertyHandle theObjectHandle){
		for(CCPropertyHandle<?> myPropertyHandle:theObjectHandle.children().values()){
			Class<?> myFieldClass = myPropertyHandle.type();
			
			if(myFieldClass == Float.class || myFieldClass == Float.TYPE){
				theVisitor.onFloat((CCNumberPropertyHandle<Float>)myPropertyHandle);
			}else  if(myFieldClass == Integer.class || myFieldClass == Integer.TYPE){
				theVisitor.onInt((CCNumberPropertyHandle<Integer>)myPropertyHandle);
			}else  if(myFieldClass == Boolean.class || myFieldClass == Boolean.TYPE){
				theVisitor.onBoolean((CCBooleanPropertyHandle)myPropertyHandle);
			}else  if(myFieldClass == String.class){
				theVisitor.onString((CCStringPropertyHandle)myPropertyHandle);
			}else  if(myFieldClass.isEnum()){
				theVisitor.onEnum((CCEnumPropertyHandle)myPropertyHandle);
			}else  if(myFieldClass == CCColor.class){
				theVisitor.onColor((CCColorPropertyHandle)myPropertyHandle);
			}else  if(myFieldClass == CCEnvelope.class){
				theVisitor.onEnvelope((CCEnvelopeHandle)myPropertyHandle);
			}else{
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				theVisitor.onObject(myObjectHandle);
				visit(theVisitor, myObjectHandle);
			}
		}
	}
	
	public static interface CCDoubleConverter<Type extends Number>{
		public Type toType(double theValue);
		
		public Type min();
		
		public Type max();
	}
	
	public static CCDoubleConverter<Float> floatConverter = new CCDoubleConverter<Float>(){
		@Override
		public Float toType(double theValue) {return (float)theValue;}

		@Override
		public Float min() {
			return -Float.MAX_VALUE;
		}

		@Override
		public Float max() {
			return Float.MAX_VALUE;
		}
	};
	
	public static CCDoubleConverter<Double> doubleConverter = new CCDoubleConverter<Double>(){
		@Override
		public Double toType(double theValue) {return theValue;}

		@Override
		public Double min() {
			return -Double.MAX_VALUE;
		}

		@Override
		public Double max() {
			return Double.MAX_VALUE;
		}
	};
	
	public static CCDoubleConverter<Integer> intConverter = new CCDoubleConverter<Integer>(){
		@Override
		public Integer toType(double theValue) {
			return (int)theValue;
		}

		@Override
		public Integer min() {
			return Integer.MIN_VALUE;
		}

		@Override
		public Integer max() {
			return Integer.MAX_VALUE;
		}
	};
	
	
	
	private CCObjectPropertyHandle _myRootHandle;

	public CCPropertyMap(Object theRootObject, String theName){
		_myRootHandle = new CCObjectPropertyHandle(theRootObject);
	}
	
	public CCObjectPropertyHandle rootHandle(){
		return _myRootHandle;
	}
	
	public CCPropertyHandle<?> property(Path thePath){
		if(thePath.getNameCount() == 1)return _myRootHandle;
		return _myRootHandle.property(thePath, 1);
	}
	
	private static class CCTestClass{
		@CCProperty(name = "int test", min = 0, max = 100)
		private int _myInt = 10;
		@CCProperty(name = "float test", min = 0, max = 100)
		private float _myFloat = 10;
	}
	
	public static class CCTestClass2{
		@CCProperty(name = "bool test")
		private boolean _myBoolean = true;
		@CCProperty(name = "object test")
		private CCTestClass _myInnerObject = new CCTestClass();
	}
	
	public static void main(String[] args) {
		CCPropertyMap myHandler = new CCPropertyMap(new CCTestClass2(), "test");
		String myJsonString = (String)CCDataIO.toFormatType(myHandler.rootHandle().data(), CCDataFormats.JSON);
		CCLog.info(myJsonString);
		
		CCDataObject myPropertyData = CCDataIO.parseToObject(myJsonString, CCDataFormats.JSON);
		CCLog.info(CCDataIO.toFormatType(myPropertyData, CCDataFormats.JSON));
	}
}
