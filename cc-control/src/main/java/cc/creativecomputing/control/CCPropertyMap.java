/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.control;

import java.nio.file.Path;

import cc.creativecomputing.control.handles.CCBooleanPropertyHandle;
import cc.creativecomputing.control.handles.CCColorPropertyHandle;
import cc.creativecomputing.control.handles.CCControlMatrixHandle;
import cc.creativecomputing.control.handles.CCEnumPropertyHandle;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCGradientPropertyHandle;
import cc.creativecomputing.control.handles.CCNumberPropertyHandle;
import cc.creativecomputing.control.handles.CCObjectPropertyHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCStringPropertyHandle;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCFormatUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.math.CCColor;

public class CCPropertyMap {
	
	public interface CCPropertyMapVisitor{
		
		void onRoot(CCObjectPropertyHandle theRoot);
		
		void onFloat(CCNumberPropertyHandle<Float> theHandle);
		
		void onInt(CCNumberPropertyHandle<Integer> theHandle);
		
		void onBoolean(CCBooleanPropertyHandle theHandle);
		
		void onString(CCStringPropertyHandle theHandle);
		
		void onEnum(CCEnumPropertyHandle theHandle);
		
		void onColor(CCColorPropertyHandle theHandle);
		
		void onGradient(CCGradientPropertyHandle theHandle);
		
		void onEnvelope(CCEnvelopeHandle theHandle);
		
		void onControlMatrix(CCControlMatrixHandle theHandle);
		
		void onObject(CCObjectPropertyHandle theHandle);
	}
	
	public void visit(CCPropertyMapVisitor theVisitor){
		
		CCObjectPropertyHandle myRootHandle = rootHandle();
		
		theVisitor.onRoot(myRootHandle);
		visit(theVisitor, myRootHandle);
	}
	
	@SuppressWarnings("unchecked")
	private void visit(CCPropertyMapVisitor theVisitor, CCObjectPropertyHandle theObjectHandle){
		for(CCPropertyHandle<?> myPropertyHandle:theObjectHandle.children().values()){
			Class<?> myClass = myPropertyHandle.type();
			
			if(myClass == Float.class || myClass == Float.TYPE){
				theVisitor.onFloat((CCNumberPropertyHandle<Float>)myPropertyHandle);
			}else  if(myClass == Integer.class || myClass == Integer.TYPE){
				theVisitor.onInt((CCNumberPropertyHandle<Integer>)myPropertyHandle);
			}else  if(myClass == Boolean.class || myClass == Boolean.TYPE){
				theVisitor.onBoolean((CCBooleanPropertyHandle)myPropertyHandle);
			}else  if(myClass == String.class){
				theVisitor.onString((CCStringPropertyHandle)myPropertyHandle);
			}else  if(myClass.isEnum()){
				theVisitor.onEnum((CCEnumPropertyHandle)myPropertyHandle);
			}else  if(myClass == CCColor.class){
				theVisitor.onColor((CCColorPropertyHandle)myPropertyHandle);
			}else  if(myClass == CCGradient.class){
				theVisitor.onGradient((CCGradientPropertyHandle)myPropertyHandle);
			}else  if(myClass == CCEnvelope.class){
				theVisitor.onEnvelope((CCEnvelopeHandle)myPropertyHandle);
			}else  if(myClass == CCControlMatrix.class){
				theVisitor.onControlMatrix((CCControlMatrixHandle)myPropertyHandle);
			}else{
				CCObjectPropertyHandle myObjectHandle = (CCObjectPropertyHandle)myPropertyHandle;
				theVisitor.onObject(myObjectHandle);
				visit(theVisitor, myObjectHandle);
			}
		}
	}
	
	public interface CCDoubleConverter<Type extends Number>{
		Type toType(double theValue);
		
		Type min();
		
		Type max();
		
		String toString(Number theValue);
		
		Class<Type> type();
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

		@Override
		public String toString(Number theValue) {
			return CCFormatUtil.nd(theValue.doubleValue(), 4);
		}

		@Override
		public Class<Float> type() {
			return Float.class;
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

		@Override
		public String toString(Number theValue) {
			return CCFormatUtil.nd(theValue.doubleValue(), 4);
		}

		@Override
		public Class<Double> type() {
			return Double.class;
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

		@Override
		public String toString(Number theValue) {
			return theValue.intValue() + "";
		}

		@Override
		public Class<Integer> type() {
			return Integer.class;
		}
	};
	
	
	
	private CCObjectPropertyHandle _myRootHandle;

	public CCPropertyMap(){
	}
	
	public void setData(Object theRootObject, String thePresetPath){
		if(_myRootHandle != null){
			_myRootHandle.relink(theRootObject);
			return;
		}
		_myRootHandle = new CCObjectPropertyHandle(theRootObject, thePresetPath);
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
		CCPropertyMap myHandler = new CCPropertyMap();
		myHandler.setData(new CCTestClass2(), "settings");
		String myJsonString = (String)CCDataIO.toFormatType(myHandler.rootHandle().data(), CCDataFormats.JSON);
		CCLog.info(myJsonString);
		
		CCDataObject myPropertyData = CCDataIO.parseToObject(myJsonString, CCDataFormats.JSON);
		CCLog.info(CCDataIO.toFormatType(myPropertyData, CCDataFormats.JSON));
	}
}
