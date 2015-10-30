package cc.creativecomputing.control.handles;

import java.nio.file.Path;
import java.nio.file.Paths;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataObject;

@SuppressWarnings({ "unchecked", "rawtypes" })
public abstract class CCPropertyHandle<Type>{
	protected final CCMember<CCProperty> _myMember;
	
	protected final CCListenerManager<CCPropertyListener> _myEvents = CCListenerManager.create(CCPropertyListener.class);
	protected final CCListenerManager<CCPropertyEditListener> _myEditEvents = CCListenerManager.create(CCPropertyEditListener.class);
	
	protected Type _myValue = null;
	protected Type _myOriginalValue = null;
	
	protected final CCObjectPropertyHandle _myParent;
	
	protected boolean _myUpdateMember = false;
	
	private Path _myPath;
	
	private boolean _myReadBack;
	
	protected CCPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember){
		_myParent = theParent;
		_myMember = theMember;
		if(_myMember == null)return;
		_myValue = (Type)_myMember.value();
		_myOriginalValue = (Type)_myMember.value();
		_myReadBack = readBack();
	}
	
	public CCListenerManager<CCPropertyListener> events(){
		return _myEvents;
	}
	
	public CCListenerManager<CCPropertyEditListener> editEvents(){
		return _myEditEvents;
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
		if(_myMember == null)return "";
		return _myMember.annotation() == null || _myMember.annotation().name().equals("") ? memberName() : _myMember.annotation().name();
	}
	
	public String memberName(){
		return _myMember.memberName();
	}
	
	public CCMember member(){
		return _myMember;
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
			_myOriginalValue = theValue;
		}

		
		if(_myValue == theValue)return;
		_myValue = theValue;
		_myUpdateMember = true;
//		_myMember.value(theValue);
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
	
	public void fromNormalizedValue(double theValue, boolean theOverWrite){
		value(convertNormalizedValue(theValue), theOverWrite);
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
	
	public double formatNormalizedValue(double theValue){
		return theValue;
	}
	
	public abstract double normalizedValue();
	
	public abstract Type convertNormalizedValue(double theValue);
	
	public abstract String valueString();
	
	public void restore(){
		value(_myOriginalValue, true);
	}
	
	public void onChange(){
		_myEvents.proxy().onChange(value());
	}
	
	public void beginEdit(){
		_myEditEvents.proxy().beginEdit(this);
	}
	
	public void endEdit(){
		_myEditEvents.proxy().endEdit(this);
	}
	
	public CCDataObject data(){
		CCDataObject myResult = new CCDataObject();
		myResult.put("name", name());
		myResult.put("value", dataObject());
		return myResult;
	}
	
	public void data(CCDataObject theData){
		try{
			value((Type)theData.get("value"), true);
		}catch(Exception e){
			
		}
	}
}