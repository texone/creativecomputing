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

import cc.creativecomputing.control.handles.CCBooleanHandle;
import cc.creativecomputing.control.handles.CCColorHandle;
import cc.creativecomputing.control.handles.CCControlMatrixHandle;
import cc.creativecomputing.control.handles.CCEnumHandle;
import cc.creativecomputing.control.handles.CCEnvelopeHandle;
import cc.creativecomputing.control.handles.CCGradientHandle;
import cc.creativecomputing.control.handles.CCNumberHandle;
import cc.creativecomputing.control.handles.CCObjectHandle;
import cc.creativecomputing.control.handles.CCPropertyHandle;
import cc.creativecomputing.control.handles.CCStringHandle;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.io.data.CCDataIO.CCDataFormats;
import cc.creativecomputing.math.CCColor;

public class CCPropertyMap {
	
	public interface CCPropertyMapVisitor{
		
		void onRoot(CCObjectHandle theRoot);
		
		void onFloat(CCNumberHandle<Float> theHandle);
		
		void onInt(CCNumberHandle<Integer> theHandle);
		
		void onBoolean(CCBooleanHandle theHandle);
		
		void onString(CCStringHandle theHandle);
		
		void onEnum(CCEnumHandle theHandle);
		
		void onColor(CCColorHandle theHandle);
		
		void onGradient(CCGradientHandle theHandle);
		
		void onEnvelope(CCEnvelopeHandle theHandle);
		
		void onControlMatrix(CCControlMatrixHandle theHandle);
		
		void onObject(CCObjectHandle theHandle);
	}
	
	public void visit(CCPropertyMapVisitor theVisitor){
		
		CCObjectHandle myRootHandle = rootHandle();
		
		theVisitor.onRoot(myRootHandle);
		visit(theVisitor, myRootHandle);
	}
	
	@SuppressWarnings("unchecked")
	private void visit(CCPropertyMapVisitor theVisitor, CCObjectHandle theObjectHandle){
		for(CCPropertyHandle<?> myPropertyHandle:theObjectHandle.children().values()){
			Class<?> myClass = myPropertyHandle.type();
			
			if(myClass == Float.class || myClass == Float.TYPE){
				theVisitor.onFloat((CCNumberHandle<Float>)myPropertyHandle);
			}else  if(myClass == Integer.class || myClass == Integer.TYPE){
				theVisitor.onInt((CCNumberHandle<Integer>)myPropertyHandle);
			}else  if(myClass == Boolean.class || myClass == Boolean.TYPE){
				theVisitor.onBoolean((CCBooleanHandle)myPropertyHandle);
			}else  if(myClass == String.class){
				theVisitor.onString((CCStringHandle)myPropertyHandle);
			}else  if(myClass.isEnum()){
				theVisitor.onEnum((CCEnumHandle)myPropertyHandle);
			}else  if(myClass == CCColor.class){
				theVisitor.onColor((CCColorHandle)myPropertyHandle);
			}else  if(myClass == CCGradient.class){
				theVisitor.onGradient((CCGradientHandle)myPropertyHandle);
			}else  if(myClass == CCEnvelope.class){
				theVisitor.onEnvelope((CCEnvelopeHandle)myPropertyHandle);
			}else  if(myClass == CCControlMatrix.class){
				theVisitor.onControlMatrix((CCControlMatrixHandle)myPropertyHandle);
			}else{
				CCObjectHandle myObjectHandle = (CCObjectHandle)myPropertyHandle;
				theVisitor.onObject(myObjectHandle);
				visit(theVisitor, myObjectHandle);
			}
		}
	}
	
	private CCObjectHandle _myRootHandle;

	public CCPropertyMap(){
	}
	
	public void setData(Object theRootObject, String thePresetPath){
		if(_myRootHandle != null){
			_myRootHandle.relink(theRootObject);
			return;
		}
		_myRootHandle = new CCObjectHandle(theRootObject, thePresetPath);
	}
	
	public CCObjectHandle rootHandle(){
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
