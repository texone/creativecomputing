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
package cc.creativecomputing.control.handles;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.core.CCEventManager;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class CCPropertyHandle<Type>{
	
	public static String propertyName(CCMember<CCProperty> theMember){
		if(theMember == null)return "";
		return theMember.annotation() == null || theMember.annotation().name().equals("") ? theMember.memberName() : theMember.annotation().name();
	}
	
	protected CCMember<CCProperty> _myMember;
	
	public final CCEventManager<Type> changeEvents = new CCEventManager<>();
	
	protected Type _myValue = null;
	protected Type _myPresetValue = null;
	protected Type _myDefaultValue = null;
	
	protected CCObjectHandle _myParent;
	
	protected boolean _myUpdateMember = false;
	
	private Path _myPath;
	
	private boolean _myReadBack;
	
	protected CCPropertyHandle(CCObjectHandle theParent, CCMember<CCProperty> theMember){
		_myParent = theParent;
		_myMember = theMember;
		if(_myMember == null)return;
		_myValue = (Type)_myMember.value();
		_myPresetValue = (Type)_myMember.value();
		_myDefaultValue = (Type)_myMember.value();
		_myReadBack = readBack();
	}
	
	public void mute(boolean theMute){
		
	}
	
	public void path(Path thePath){
		_myPath = thePath;
	}
	
	public Path path(){
		if(_myPath != null)return _myPath;
		if(_myParent == null)return Paths.get(name());
		else return _myParent.path().resolve(name());
	}
	
	public String name(){
		return propertyName(_myMember);
	}
	
	public String memberName(){
		return _myMember.memberName();
	}
	
	public CCMember member(){
		return _myMember;
	}
	
	public void member(CCMember theMember){
		_myMember = theMember;
		_myUpdateMember = true;
	}
	
	public Type value(){
		if(_myValue != null)return _myValue;
		if(_myMember == null)return null;
		return (Type)_myMember.value();
	}
	
	public void valueCasted(Object theValue, boolean theOverWrite){
		if(theValue == null)return;
		value((Type)theValue, theOverWrite);
	}
	
	public void value(Type theValue, boolean theOverWrite){
		if(theOverWrite){
			_myPresetValue = theValue;
		}
		
		if(_myParent.isSelectable() && _myParent.isSelected()){
			_myParent.valueSiblings(theValue, name());
		}

		if(_myValue != null && _myValue.equals(theValue))return;
		_myValue = theValue;
		_myUpdateMember = true;
	}
	
	protected void directValue(Type theValue){
		if(_myValue == theValue)return;
		_myValue = theValue;
		_myUpdateMember = true;
	}
	
	private boolean readBack(){
		if(
			_myMember == null || 
			_myMember.annotation() == null || 
			!_myMember.annotation().readBack()
		){
			return (
				_myParent != null && 
				_myParent._myMember != null && 
				_myParent._myMember.annotation() != null && 
				_myParent._myMember.annotation().readBack()
			);
		}return true;
	}
	
	public void update(final double theDeltaTime){
		
		if(_myUpdateMember){
			_myMember.value(_myValue);
			_myUpdateMember = false;
			onChange();
		}else{
			if(!_myReadBack)return;
			
			Object myValue = _myMember.value();
			if(myValue != null && !myValue.equals(_myValue)){
				_myValue = (Type)myValue;
				onChange();
			}
		}

		
	}
	
	public void fromDoubleValue(double theValue, boolean theOverWrite){
//		value(convertDoubleValue(theValue), theOverWrite);
	}
	
	public Object dataObject(){
		return value();
	}
	
	public CCObjectHandle parent(){
		return _myParent;
	}
	
	public Class<?> type(){
		return _myMember.type();
	}
	
	public double formatDoubleValue(double theValue){
		return theValue;
	}
	
	public double normalizedValue() {
		return 0;
	}
	
	public String valueString() {
		return _myValue.toString();
	}
	
	public void restorePreset(){
		value(_myPresetValue, true);
	}
	
	public void restoreDefault(){
		value(_myDefaultValue, true);
	}
	
	public boolean isChanged(){
		if(_myDefaultValue == null || _myValue == null)return false;
		return !_myDefaultValue.equals(_myValue);
	}
	
	public void onChange(){
		try{
			changeEvents.event(value());
		}catch(Exception e){
			throw new RuntimeException("Problem with property:" + path() + ":" + name()+":" +value()+":" + value().getClass().getName()+":" + _myMember.value()+":" + _myMember.value().getClass().getName(), e);
		}
	}
	
	private boolean _myIsInEdit = false;
	
	public boolean isInEdit(){
		return _myIsInEdit;
	}
	
	public void beginEdit(){
		_myIsInEdit = true;
	}
	
	public void endEdit(){
		_myIsInEdit = false;
	}
	
	public CCDataObject data(){
		try{
			CCDataObject myResult = new CCDataObject();
			myResult.put("value", dataObject());
			return myResult;
		}catch(Exception e){
			throw new RuntimeException("Problem serializing: " + path(),e);
		}
	}
	
	public void data(CCDataObject theData){
		try{
			value((Type)theData.get("value"), true);
			onChange();
		}catch(Exception e){
			
		}
	}

	public boolean hasSubPreset() {
		return false;
	}
	
	@Override
	public String toString() {
		return name();
	}
}
