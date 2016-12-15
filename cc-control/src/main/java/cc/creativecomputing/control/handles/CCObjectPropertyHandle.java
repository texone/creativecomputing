package cc.creativecomputing.control.handles;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.CCGradient;
import cc.creativecomputing.control.CCPropertyFeedbackObject;
import cc.creativecomputing.control.CCPropertyMap;
import cc.creativecomputing.control.CCSelection;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.control.code.CCShaderObject;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil;
import cc.creativecomputing.core.util.CCReflectionUtil.CCField;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMapEntry;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMethod;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMethodParameter;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.CCDataIO;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.spline.CCSpline;

@SuppressWarnings({"rawtypes","unchecked"})
public class CCObjectPropertyHandle extends CCPropertyHandle<Object>{
	
	private static interface CCHandleCreator{
		public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember);
	}
	
	private static Map<Class<?>, CCHandleCreator> creatorMap = new HashMap<>();
	static{
		CCHandleCreator myFloatCreator = new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCNumberPropertyHandle<Float>(theParent, theMember, CCPropertyMap.floatConverter);}
		};
		creatorMap.put(Float.class, myFloatCreator);
		creatorMap.put(Float.TYPE, myFloatCreator);

		CCHandleCreator myDoubleCreator = new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCNumberPropertyHandle<Double>(theParent, theMember, CCPropertyMap.doubleConverter);}
		};
		creatorMap.put(Double.class, myDoubleCreator);
		creatorMap.put(Double.TYPE, myDoubleCreator);

		CCHandleCreator myIntegerCreator = new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCNumberPropertyHandle<Integer>(theParent, theMember, CCPropertyMap.intConverter);}
		};
		creatorMap.put(Integer.class, myIntegerCreator);
		creatorMap.put(Integer.TYPE, myIntegerCreator);

		CCHandleCreator myBooleanCreator = new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCBooleanPropertyHandle(theParent, theMember);}
		};
		creatorMap.put(Boolean.class, myBooleanCreator);
		creatorMap.put(Boolean.TYPE, myBooleanCreator);
		
		creatorMap.put(CCColor.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCColorPropertyHandle(theParent, theMember);}
		});
		creatorMap.put(CCGradient.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCGradientPropertyHandle(theParent, theMember);}
		});
		creatorMap.put(String.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCStringPropertyHandle(theParent, theMember);}
		});
		creatorMap.put(CCEnvelope.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCEnvelopeHandle(theParent, theMember);}
		});
		creatorMap.put(CCSpline.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCSplineHandle(theParent, theMember);}
		});
		creatorMap.put(Path.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCPathHandle(theParent, theMember);}
		});
		creatorMap.put(CCSelection.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCSelectionPropertyHandle(theParent, theMember);}
		});
		creatorMap.put(CCRealtimeCompile.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {return new CCRealtimeCompileHandle(theParent, theMember);}
		});
		creatorMap.put(CCShaderObject.class, new CCHandleCreator(){
			@Override
			public CCPropertyHandle create(CCObjectPropertyHandle theParent, CCMember theMember) {
				return new CCShaderCompileHandle(theParent, theMember);}
		});
	}
	
	private Map<String,CCPropertyHandle> _myChildHandles = new LinkedHashMap<>();
	
	private Object _myRootObject = null;
	
	private final Path _myPresetPath;
	
	private String _myPreset = null;
	
	private final String _mySettingsPath;

	protected CCObjectPropertyHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember, String theSettingsPath) {
		super(theParent, theMember);
		_mySettingsPath = theSettingsPath;
		if(theMember instanceof CCMethod){
			_myChildHandles = linkMethod((CCMethod)theMember);
		}else{
			_myChildHandles = link(theMember.value());
		}
		_myPresetPath = createPresetPath();
	}

	public CCObjectPropertyHandle(Object theObject, String theSettingsPath){
		super(null, null);
		_mySettingsPath = theSettingsPath;
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
		if(_myMember instanceof CCMethod){
			return Object.class;
		}
		return super.type();
	}
	
	private Path createPresetPath(){
		Path myPresetPath = CCNIOUtil.dataPath(_mySettingsPath);
			
		String[] myTypeParts = type().getName().split("\\.");
		for(String myPart:myTypeParts){
			myPresetPath = myPresetPath.resolve(Paths.get(myPart));
		}
		return myPresetPath;
	}
	
	public Path presetPath(){
		return _myPresetPath;
	}
	
	private Map<String,CCPropertyHandle> linkMethod(CCMethod theObject){
		Map<String,CCPropertyHandle> myResult = new LinkedHashMap<>();
	
		for(Object myParameter:theObject.parameter()){
			CCMethodParameter<CCProperty> myMethodParameter = (CCMethodParameter<CCProperty>)myParameter;
			Class<?> myClass = myMethodParameter.type();
			CCPropertyHandle myProperty;
			if(creatorMap.containsKey(myClass)){
				myProperty = creatorMap.get(myClass).create(this, myMethodParameter);
			}else  if(myClass.isEnum()){
				myProperty = new CCEnumPropertyHandle(this, myMethodParameter);
			}else{
				if(myMethodParameter.value() == null)continue;
				myProperty = new CCObjectPropertyHandle(this, myMethodParameter, _mySettingsPath);
			}
			myResult.put(myProperty.name(), myProperty);
		}
		return myResult;
	}
	
	private Map<String,CCPropertyHandle> link(Object theObject){
		
		Map<String,CCPropertyHandle> myResult = new LinkedHashMap<>();
		
		List<CCField<CCProperty>> myFields = CCReflectionUtil.getFields(theObject, CCProperty.class);
		for(CCField<CCProperty> myField:myFields){
			Class<?> myClass = myField.type();
			CCPropertyHandle myProperty = null;
			if(creatorMap.containsKey(myClass)){
				myProperty = creatorMap.get(myClass).create(this, myField);
			}else  if(myClass.isEnum()){
				myProperty = new CCEnumPropertyHandle(this, myField);
			}else{
				if(myField.value() == null)continue;
				if(myField.value() instanceof Map && ((Map)myField.value()).size() <= 0){
					continue;
				}
				if(myField.annotation().hide()){
					Map<String,CCPropertyHandle> myHandles = link(myField.value());
					for(String myKey:myHandles.keySet()){
						myResult.put(myKey, myHandles.get(myKey));
					}
				}else{
					myProperty = new CCObjectPropertyHandle(this, myField, _mySettingsPath);
				}
			}
			if(myProperty != null)myResult.put(myProperty.name(), myProperty);
		}
		
		List<CCMethod<CCProperty>> myMethods = CCReflectionUtil.getMethods(theObject, CCProperty.class);
		for(CCMethod<CCProperty> myMethod:myMethods){

			CCPropertyHandle myProperty = null;
			if(myMethod.method().getParameterCount() > 1){
				myProperty = new CCObjectPropertyHandle(this, myMethod, _mySettingsPath);
			}else{
				Class<?> myClass = myMethod.type();
				if(creatorMap.containsKey(myClass)){
					myProperty = creatorMap.get(myClass).create(this, myMethod);
				}else if((myClass == null) || myClass == CCTriggerProgress.class){
					myProperty = new CCEventTriggerHandle(this, myMethod);
				}else  if(myClass.isEnum()){
					myProperty = new CCEnumPropertyHandle(this, myMethod);
				}else{
	//				if(myField.value() == null)continue;
	//				myProperty = new CCObjectPropertyHandle(this, myField);
				}
			}
			if(myProperty != null)myResult.put(myProperty.name(), myProperty);
		}
		
		if((theObject instanceof Map)){
			Map<Object,Object> myMap = (Map<Object, Object>)theObject;
			for(Object myKey:myMap.keySet()){
				CCMapEntry myEntry = new CCMapEntry(myMap, myKey);
				Class<?> myClass = myEntry.type();
				CCPropertyHandle myProperty;
				if(creatorMap.containsKey(myClass)){
					myProperty = creatorMap.get(myClass).create(this, myEntry);
				}else  if(myClass.isEnum()){
					myProperty = new CCEnumPropertyHandle(this, myEntry);
				}else{
					myProperty = new CCObjectPropertyHandle(this, myEntry, _mySettingsPath);
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
		myResult.put("value", myData);
		
		return myResult;
	}
	
	@Override
	public CCDataObject data() {
		if(_myMember == null)return dataObject();
		return super.data();
	}
	
	@Override
	public void restorePreset() {
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
			if(myData == null)return;
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