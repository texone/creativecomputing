package cc.creativecomputing.control.handles;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class CCPropertyHandle<Type>{
	
	public static String propertyName(CCMember<CCProperty> theMember){
		if(theMember == null)return "";
		return theMember.annotation() == null || theMember.annotation().name().equals("") ? theMember.memberName() : theMember.annotation().name();
	}
	
	protected CCMember<CCProperty> _myMember;
	
	protected final CCListenerManager<CCPropertyListener> _myEvents = CCListenerManager.create(CCPropertyListener.class);
	
	protected Type _myValue = null;
	protected Type _myPresetValue = null;
	protected Type _myDefaultValue = null;
	
	protected CCObjectPropertyHandle _myParent;
	
	protected boolean _myUpdateMember = false;
	
	private Path _myPath;
	
	private boolean _myReadBack;
	
	protected CCPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember){
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
	
	public CCListenerManager<CCPropertyListener> events(){
		return _myEvents;
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
		
		if(_myValue == theValue)return;
		_myValue = theValue;
		_myUpdateMember = true;
//		_myMember.value(theValue);
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
	
	public CCObjectPropertyHandle parent(){
		return _myParent;
	}
	
	public Class<?> type(){
		return _myMember.type();
	}
	
	public double formatDoubleValue(double theValue){
		return theValue;
	}
	
	public abstract double normalizedValue();
	
	public abstract String valueString();
	
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
			_myEvents.proxy().onChange(value());
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
			myResult.put("name", name());
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