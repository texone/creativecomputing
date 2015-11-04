package cc.creativecomputing.control.handles;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCPropertyFeedbackObject;
import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCReflectionUtil;
import cc.creativecomputing.core.util.CCReflectionUtil.CCField;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMapEntry;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMethod;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;

@SuppressWarnings({"rawtypes","unchecked"})
public class CCObjectPropertyHandle extends CCPropertyHandle<Object>{
	
	private Map<String,CCPropertyHandle> _myChildHandles = new LinkedHashMap<>();
	
	private Object _myRootObject = null;
	
	private final Path _myPresetPath;
	
	private String _myPreset = null;

	protected CCObjectPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
		_myChildHandles = link(theMember.value());
		_myPresetPath = createPresetPath();
	}

	public CCObjectPropertyHandle(Object theObject){
		super(null, null);
		_myRootObject = theObject;
		_myChildHandles = link(theObject);
		_myPresetPath = createPresetPath();
	}
	
	public CCPropertyHandle<?> property(Path thePath, int theStart){
		Path myName = thePath.getName(theStart);
		
		CCPropertyHandle<?> myProperty = _myChildHandles.get(myName.toString());
		if(thePath.getNameCount() == theStart + 1){
			
			return myProperty;
		}
		
		if(!(myProperty instanceof CCObjectPropertyHandle)){
			return null;
		}
		
		CCObjectPropertyHandle myChild = (CCObjectPropertyHandle)myProperty;
		return myChild.property(thePath, theStart + 1);
	}
	
	public CCPropertyHandle<?> property(String theID){
		return _myChildHandles.get(theID);
	}
	
	
	@Override
	public String name() {
		if(_myRootObject != null)return "app";
		return super.name();
	}
	
	@Override
	public Class<?> type() {
		if(_myRootObject != null)return _myRootObject.getClass();
		return super.type();
	}
	
	private Path createPresetPath(){
		Path myPresetPath = CCNIOUtil.dataPath("settings");
			
		String[] myTypeParts = type().getName().split("\\.");
		for(String myPart:myTypeParts){
			myPresetPath = myPresetPath.resolve(Paths.get(myPart));
		}
			
		return myPresetPath;
	}
	
	public Path presetPath(){
		return _myPresetPath;
	}
	
	private Map<String,CCPropertyHandle> link(Object theObject){
		
		Map<String,CCPropertyHandle> myResult = new LinkedHashMap<>();
		
		List<CCField<CCProperty>> myFields = CCReflectionUtil.getFields(theObject, CCProperty.class);
		for(CCField<CCProperty> myField:myFields){
			Class<?> myClass = myField.type();
			CCPropertyHandle myProperty;
			if(myClass == Float.class || myClass == Float.TYPE){
				myProperty = new CCNumberPropertyHandle<Float>(this, myField, CCPropertyMap.floatConverter);
			}else  if(myClass == Double.class || myClass == Double.TYPE){
				myProperty = new CCNumberPropertyHandle<Double>(this, myField, CCPropertyMap.doubleConverter);
			}else  if(myClass == Integer.class || myClass == Integer.TYPE){
				myProperty = new CCNumberPropertyHandle<Integer>(this, myField, CCPropertyMap.intConverter);
			}else  if(myClass == Boolean.class || myClass == Boolean.TYPE){
				myProperty = new CCBooleanPropertyHandle(this, myField);
			}else  if(myClass.isEnum()){
				myProperty = new CCEnumPropertyHandle(this, myField);
			}else  if(myClass == CCColor.class){
				myProperty = new CCColorPropertyHandle(this, myField);
			}else  if(myClass == String.class){
				myProperty = new CCStringPropertyHandle(this, myField);
			}else  if(myClass == CCEnvelope.class){
				myProperty = new CCEnvelopeHandle(this, myField);
			}else  if(myClass == Path.class){
				myProperty = new CCPathHandle(this, myField);
			}else  if(myClass == CCSelection.class){
				myProperty = new CCSelectionPropertyHandle(this, myField);
			}else  if(myClass == CCRealtimeCompile.class){
				myProperty = new CCRealtimeCompileHandle(this, myField);
			}else{
				if(myField.value() == null)continue;
				myProperty = new CCObjectPropertyHandle(this, myField);
			}
			myResult.put(myProperty.name(), myProperty);
		}
		
		List<CCMethod<CCProperty>> myMethods = CCReflectionUtil.getMethods(theObject, CCProperty.class);
		for(CCMethod<CCProperty> myMethod:myMethods){
			Class<?> myClass = myMethod.type();
			
			CCPropertyHandle myProperty = null;
			if((myClass == null) || myClass == CCTriggerProgress.class){
				myProperty = new CCEventTriggerHandle(this, myMethod);
			}else if(myClass == Float.class || myClass == Float.TYPE){
				myProperty = new CCNumberPropertyHandle<Float>(this, myMethod, CCPropertyMap.floatConverter);
			}else if(myClass == Double.class || myClass == Double.TYPE){
				myProperty = new CCNumberPropertyHandle<Double>(this, myMethod, CCPropertyMap.doubleConverter);
			}else  if(myClass == Integer.class || myClass == Integer.TYPE){
				myProperty = new CCNumberPropertyHandle<Integer>(this, myMethod, CCPropertyMap.intConverter);
			}else  if(myClass == Boolean.class || myClass == Boolean.TYPE){
				myProperty = new CCBooleanPropertyHandle(this, myMethod);
			}else  if(myClass.isEnum()){
				myProperty = new CCEnumPropertyHandle(this, myMethod);
			}else  if(myClass == CCColor.class){
				myProperty = new CCColorPropertyHandle(this, myMethod);
			}else  if(myClass == String.class){
				myProperty = new CCStringPropertyHandle(this, myMethod);
			}else  if(myClass == Path.class){
				myProperty = new CCPathHandle(this, myMethod);
			}else{
//				myResult.put(myMethod.name(), new CCObjectPropertyHandle(this, myMethod));
			}
			if(myProperty != null)myResult.put(myProperty.name(), myProperty);
		}
		
		if((theObject instanceof Map)){
		
			Map<String,Object> myMap = (Map<String, Object>)theObject;
			for(String myKey:myMap.keySet()){
				CCMapEntry myEntry = new CCMapEntry(myMap, myKey);
				
				Class<?> myClass = myEntry.type();
				CCPropertyHandle myProperty;
				if(myClass == Float.class || myClass == Float.TYPE){
					myProperty = new CCNumberPropertyHandle<Float>(this, myEntry, CCPropertyMap.floatConverter);
				}else  if(myClass == Double.class || myClass == Double.TYPE){
					myProperty = new CCNumberPropertyHandle<Double>(this, myEntry, CCPropertyMap.doubleConverter);
				}else  if(myClass == Integer.class || myClass == Integer.TYPE){
					myProperty = new CCNumberPropertyHandle<Integer>(this, myEntry, CCPropertyMap.intConverter);
				}else  if(myClass == Boolean.class || myClass == Boolean.TYPE){
					myProperty = new CCBooleanPropertyHandle(this, myEntry);
				}else  if(myClass.isEnum()){
					myProperty = new CCEnumPropertyHandle(this, myEntry);
				}else  if(myClass == CCColor.class){
					myProperty = new CCColorPropertyHandle(this, myEntry);
				}else  if(myClass == String.class){
					myProperty = new CCStringPropertyHandle(this, myEntry);
				}else{
					myProperty = new CCObjectPropertyHandle(this, myEntry);
				}
				myResult.put(myProperty.name(), myProperty);
			}
		}
		if(theObject instanceof CCPropertyFeedbackObject){
			CCPropertyFeedbackObject myFeedBackObject = (CCPropertyFeedbackObject)theObject;
			for(String myKey:myFeedBackObject.propertyListener().keySet()){
				if(myResult.containsKey(myKey)){
					myResult.get(myKey).events().add(myFeedBackObject.propertyListener().get(myKey));
				}
			}
		}
		return myResult;
	}
	
	public double formatNormalizedValue(double theValue){
		return theValue;
	}
	
	public Map<String,CCPropertyHandle> children(){
		return _myChildHandles;
	}
	
	@Override
	public CCDataObject dataObject(){
		if(_myPreset != null){
			CCDataObject myResult = new CCDataObject();
			myResult.put("preset", _myPreset);
			return myResult;
		}
		CCDataObject myData = new CCDataObject();
		for(String myKey:_myChildHandles.keySet()){
			CCPropertyHandle myHandle = _myChildHandles.get(myKey);
			myData.put(myHandle.name(), myHandle.data());
		}
		return myData;
	}
	
	public CCDataObject presetData(){
		CCDataObject myData = new CCDataObject();
		for(String myKey:_myChildHandles.keySet()){
			CCPropertyHandle myHandle = _myChildHandles.get(myKey);
			myData.put(myHandle.name(), myHandle.data());
		}
		
		CCDataObject myResult = new CCDataObject();
		CCLog.info(name());
		myResult.put("value", myData);
		
		return myResult;
	}
	
	@Override
	public CCDataObject data() {
		if(_myMember == null)return dataObject();
		return super.data();
	}
	
	@Override
	public void restore() {
		data(_myLastData);
	}
	
	private CCDataObject _myLastData;
	
	@Override
	public void data(CCDataObject theData) {
	
		if(theData == null){
			return;
		}
		
		if(!theData.containsKey("timeline")){
			_myLastData = theData;
		}
		
		if(_myRootObject == null || theData.containsKey("value")){
			theData = theData.getObject("value");
		}

		if(theData == null)return;
		if(theData.containsKey("preset")){
			preset(theData.getString("preset"));
			return;
		}
		
		for(String myKey:theData.keySet()){
			CCDataObject myData = theData.getObject(myKey);
			String myName = myData.getString("name");
			CCPropertyHandle myHandle = _myChildHandles.get(myName);

			if(myHandle == null){
				continue;
			}
			try{
				myHandle.data(myData);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public void preset(String thePreset){
		try{
			Path myPresetPath = _myPresetPath.resolve(Paths.get(thePreset + ".json"));
			if(!CCNIOUtil.exists(myPresetPath))return;
			data(CCDataIO.createDataObject(myPresetPath));
			_myPreset = thePreset;
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public String preset(){
		return _myPreset;
	}
	
	@Override
	public void update(double theDeltaTime) {
		for(CCPropertyHandle<?> myHandle:_myChildHandles.values()){
			myHandle.update(theDeltaTime);
		}
	}

	@Override
	public Object convertNormalizedValue(double theValue) {
		return null;
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return null;
	}
	
	@Override
	public void value(Object theValue, boolean theOverWrite){
//		if(theOverWrite){
//			_myOriginalValue = theValue;
//		}
//		_myValue = theValue;
//		_myMember.value(theValue);
//		onChange();
	}
}