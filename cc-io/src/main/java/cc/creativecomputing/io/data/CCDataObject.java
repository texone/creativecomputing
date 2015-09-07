package cc.creativecomputing.io.data;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;


public class CCDataObject extends HashMap<String, Object> implements Iterable<CCDataObject>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4352488576023550512L;

	/**
	 * Construct an empty {@linkplain CCDataObject}.
	 */
	public CCDataObject() {
		super();
	}

	/**
	 * Construct a {@linkplain CCDataObject} from a subset of another
	 * {@linkplain CCDataObject}. An array of strings is used to identify the
	 * keys that should be copied. Missing keys are ignored.
	 *
	 * @param theObject A {@linkplain CCDataObject}.
	 * @param theNames An array of strings.
	 * @throws CCDataException
	 * @exception CCDataException If a value is a non-finite number or if a name
	 *                is duplicated.
	 */
	public CCDataObject(CCDataObject theObject, String... theNames) {
		this();
		for (int i = 0; i < theNames.length; i++) {
			try {
				putIfAbsent(theNames[i], theObject.get(theNames[i]));
			} catch (Exception ignore) {
			}
		}
	}

	/**
	 * Construct a {@linkplain CCDataObject} from a Map.
	 *
	 * @param theMap A map object that can be used to initialize the contents of
	 *            the {@linkplain CCDataObject}.
	 * @throws CCDataException
	 */
	public CCDataObject(Map<String, Object> theMap) {
		this();
		if (theMap == null)
			return;

		for (String myKey : theMap.keySet()) {
			Object myValue = theMap.get(myKey);
			if (myValue == null)
				continue;
			put(myKey, CCDataUtil.wrap(myValue));
		}
	}

	/**
	 * Construct a {@linkplain CCDataObject} from a ResourceBundle.
	 *
	 * @param theBaseName The ResourceBundle base name.
	 * @param theLocale The Locale to load the ResourceBundle for.
	 * @throws CCDataException If any JSONExceptions are detected.
	 */
	public CCDataObject(String theBaseName, Locale theLocale) throws CCDataException {
		this();
		ResourceBundle bundle = ResourceBundle.getBundle(theBaseName, theLocale, Thread.currentThread()
				.getContextClassLoader());

		// Iterate through the keys in the bundle.

		Enumeration<String> keys = bundle.getKeys();
		while (keys.hasMoreElements()) {
			Object key = keys.nextElement();
			if (key != null) {

				// Go through the path, ensuring that there is a nested
				// {@linkplain CCDataObject} for each
				// segment except the last. Add the value using the last
				// segment's name into
				// the deepest nested {@linkplain CCDataObject}.

				String[] path = ((String) key).split("\\.");
				int last = path.length - 1;
				CCDataObject target = this;
				for (int i = 0; i < last; i += 1) {
					String segment = path[i];
					CCDataObject nextTarget = target.getObject(segment);
					if (nextTarget == null) {
						nextTarget = new CCDataObject();
						target.put(segment, nextTarget);
					}
					target = nextTarget;
				}
				target.put(path[last], bundle.getString((String) key));
			}
		}
	}

	/**
	 * Accumulate values under a key. It is similar to the put method except
	 * that if there is already an object stored under the key then a
	 * {@linkplain CCDataArray} is stored under the key to hold all of the
	 * accumulated values. If there is already a {@linkplain CCDataArray}, then
	 * the new value is appended to it. In contrast, the put method replaces the
	 * previous value.
	 *
	 * If only one value is accumulated that is not a {@linkplain CCDataArray},
	 * then the result will be the same as using {@linkplain #put(String, Object)}. But if multiple values are
	 * accumulated, then the result will be like {@linkplain #append(String, Object)}.
	 *
	 * @param theKey A key string.
	 * @param theValue An object to be accumulated under the key.
	 * @return
	 * @throws CCDataException If the value is an invalid number or if the key
	 *             is null.
	 */
	public CCDataObject accumulate(String theKey, Object theValue) throws CCDataException {
		CCDataUtil.testValidity(theValue);
		Object object = get(theKey);
		if (object == null) {
			put(theKey, theValue instanceof CCDataArray ? new CCDataArray().add(theValue) : theValue);
		} else if (object instanceof CCDataArray) {
			((CCDataArray) object).add(theValue);
		} else {
			CCDataArray myArray = new CCDataArray();
			myArray.add(object);
			myArray.add(theValue);
			put(theKey, myArray);
		}
		return this;
	}

	/**
	 * Append values to the array under a key. If the key does not exist in the
	 * {@linkplain CCDataObject}, then the key is put in the
	 * {@linkplain CCDataObject} with its value being a {@linkplain CCDataArray}
	 * containing the value parameter. If the key was already associated with a
	 * {@linkplain CCDataArray}, then the value parameter is appended to it.
	 *
	 * @param theKey A key string.
	 * @param theValue An object to be accumulated under the key.
	 * @return
	 * @throws CCDataException If the key is null or if the current value
	 *             associated with the key is not a {@linkplain CCDataArray}.
	 */
	public CCDataObject append(String theKey, Object theValue) throws CCDataException {
		CCDataUtil.testValidity(theValue);
		Object object = get(theKey);
		if (object == null) {
			put(theKey, new CCDataArray().add(theValue));
		} else if (object instanceof CCDataArray) {
			put(theKey, ((CCDataArray) object).add(theValue));
		} else {
			throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + theKey + "] is not a " + CCDataArray.class.getSimpleName() + ".");
		}
		return this;
	}

	/**
	 * Get the boolean value associated with a key.
	 *
	 * @param theKey A key string.
	 * @return The truth.
	 * @throws CCDataException if the value is not a Boolean or the String
	 *             "true" or "false".
	 */
	public boolean getBoolean(String theKey) throws CCDataException {
		try {
			return CCDataUtil.booleanValue(get(theKey));
		} catch (CCDataException e) {
			throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(theKey) + "] is not a Boolean.");
		}
	}

	/**
	 * Get an optional boolean associated with a key. It returns the
	 * defaultValue if there is no such key, or if it is not a Boolean or the
	 * String "true" or "false" (case insensitive).
	 *
	 * @param theKey A key string.
	 * @param theDefaultValue The default.
	 * @return The truth.
	 */
	public boolean getBoolean(String theKey, boolean theDefaultValue) {
		try {
			return getBoolean(theKey);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the double value associated with a key.
	 *
	 * @param theKey A key string.
	 * @return The numeric value.
	 * @throws CCDataException if the key is not found or if the value is not a
	 *             Number object and cannot be converted to a number.
	 */
	public double getDouble(String theKey) throws CCDataException {
		Object object = get(theKey);
		try {
			return object instanceof Number ? ((Number) object).doubleValue() : Double.parseDouble((String) object);
		} catch (Exception e) {
			throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(theKey) + "] is not a number.");
		}
	}

	/**
	 * Get an optional double associated with a key, or the defaultValue if
	 * there is no such key or if its value is not a number. If the value is a
	 * string, an attempt will be made to evaluate it as a number.
	 *
	 * @param theKey A key string.
	 * @param theDefaultValue The default.
	 * @return An object which is the value.
	 */
	public double getDouble(String theKey, double theDefaultValue) {
		try {
			return getDouble(theKey);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the float value associated with a key.
	 *
	 * @param theKey A key string.
	 * @return The numeric value.
	 * @throws CCDataException if the key is not found or if the value is not a
	 *             Number object and cannot be converted to a number.
	 */
	public float getFloat(String theKey) throws CCDataException {
		Object object = get(theKey);
		try {
			return object instanceof Number ? ((Number) object).floatValue() : Float.parseFloat((String) object);
		} catch (Exception e) {
			throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(theKey) + "] is not a number.");
		}
	}

	/**
	 * Get an optional double associated with a key, or the defaultValue if
	 * there is no such key or if its value is not a number. If the value is a
	 * string, an attempt will be made to evaluate it as a number.
	 *
	 * @param theKey A key string.
	 * @param theDefaultValue The default.
	 * @return An object which is the value.
	 */
	public float getFloat(String theKey, float theDefaultValue) {
		try {
			return getFloat(theKey);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the short value associated with a key.
	 *
	 * @param theKey A key string.
	 * @return The short value.
	 * @throws CCDataException if the key is not found or if the value cannot be
	 *             converted to a short.
	 */
	public short getShort(String theKey) throws CCDataException {
		Object object = get(theKey);
		try {
			return object instanceof Number ? ((Number) object).shortValue() : Short.parseShort((String) object);
		} catch (Exception e) {
			throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(theKey) + "] is not an int.");
		}
	}

	/**
	 * Get the int value associated with a key.
	 *
	 * @param theKey A key string.
	 * @return The integer value.
	 * @throws CCDataException if the key is not found or if the value cannot be
	 *             converted to an integer.
	 */
	public int getInt(String theKey) throws CCDataException {
		Object object = get(theKey);
		try {
			return object instanceof Number ? ((Number) object).intValue() : Integer.parseInt((String) object);
		} catch (Exception e) {
			throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(theKey) + "] is not an int.");
		}
	}

	/**
	 * Get an optional int value associated with a key, or the default if there
	 * is no such key or if the value is not a number. If the value is a string,
	 * an attempt will be made to evaluate it as a number.
	 *
	 * @param theKey A key string.
	 * @param theDefaultValue The default.
	 * @return An object which is the value.
	 */
	public int getInt(String theKey, int theDefaultValue) {
		try {
			return getInt(theKey);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the long value associated with a key.
	 *
	 * @param theKey A key string.
	 * @return The long value.
	 * @throws CCDataException if the key is not found or if the value cannot be
	 *             converted to a long.
	 */
	public long getLong(String theKey) throws CCDataException {
		Object object = get(theKey);
		try {
			return object instanceof Number ? ((Number) object).longValue() : Long.parseLong((String) object);
		} catch (Exception e) {
			throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(theKey) + "] is not a long.");
		}
	}

	/**
	 * Get an optional long value associated with a key, or the default if there
	 * is no such key or if the value is not a number. If the value is a string,
	 * an attempt will be made to evaluate it as a number.
	 *
	 * @param theKey A key string.
	 * @param theDefaultValue The default.
	 * @return An object which is the value.
	 */
	public long getLong(String theKey, long theDefaultValue) {
		try {
			return getLong(theKey);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the {@linkplain CCDataArray} value associated with a key.
	 *
	 * @param key A key string.
	 * @return A {@linkplain CCDataArray} which is the value.
	 * @throws CCDataException if the key is not found or if the value is not a
	 *             {@linkplain CCDataArray}.
	 */
	public CCDataArray getArray(String key) throws CCDataException {
		Object object = get(key);
		if (object instanceof CCDataArray) {
			return (CCDataArray) object;
		}
		throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(key)+ "] is not a " + CCDataArray.class.getSimpleName() + ".");
	}

	/**
	 * Get the {@linkplain CCDataObject} value associated with a key.
	 *
	 * @param key A key string.
	 * @return A {@linkplain CCDataObject} which is the value.
	 * @throws CCDataException if the key is not found or if the value is not a
	 *             {@linkplain CCDataObject}.
	 */
	public CCDataObject getObject(String key) throws CCDataException {
		Object object = get(key);
		if (object instanceof CCDataObject) {
			return (CCDataObject) object;
		}else{
			return null;
		}
//		throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(key)+ "] is not a " + CCDataObject.class.getSimpleName());
	}

	/**
	 * Get the string associated with a key.
	 *
	 * @param theKey A key string.
	 * @return A string which is the value.
	 * @throws CCDataException if there is no string value for the key.
	 */
	public String getString(String theKey) throws CCDataException {
		Object object = get(theKey);
		if(object == null)return null;
		if (object instanceof String) {
			return (String) object;
		}else{
			return object.toString();
		}
//		throw new CCDataException(CCDataObject.class.getSimpleName() + "[" + CCDataUtil.quote(theKey) + "] not a string.");
	}

	/**
	 * Get an optional string associated with a key. It returns the defaultValue
	 * if there is no such key.
	 *
	 * @param theKey A key string.
	 * @param theDefaultValue The default.
	 * @return A string which is the value.
	 */
	public String getString(String theKey, String theDefaultValue) {
		Object object = get(theKey);
		return object == null ? theDefaultValue : object.toString();
	}

	/**
	 * Determine if the value associated with the key is null or if there is no
	 * value.
	 *
	 * @param theKey A key string.
	 * @return true if there is no value associated with the key or if the value
	 *         is the {@linkplain CCDataObject}.NULL object.
	 */
	public boolean isNull(String theKey) {
		return get(theKey) == null;
	}

	/**
	 * Put a key/value pair in the {@linkplain CCDataObject}. If the value is
	 * null, then the key will be removed from the {@linkplain CCDataObject} if
	 * it is present.
	 *
	 * @param theKey A key string.
	 * @param value An object which is the value. It should be of one of these
	 *            types: Boolean, Double, Integer, {@linkplain CCDataArray},
	 *            {@linkplain CCDataObject}, Long, String, or the
	 *            {@linkplain CCDataObject}.NULL object.
	 * @return
	 * @throws CCDataException If the value is non-finite number or if the key
	 *             is null.
	 */
	public Object put(String theKey, Object value) throws CCDataException {
		if (theKey == null) {
			throw new NullPointerException("Null key.");
		}
		if (value != null) {
			CCDataUtil.testValidity(value);
			return super.put(theKey, value);
		} else {
			return remove(theKey);
		}
	}
	
	public CCDataArray createArray(String theKey){
		CCDataArray myResult = new CCDataArray();
		put(theKey, myResult);
		return myResult;
	}
	
	public CCDataObject createObject(String theKey){
		CCDataObject myResult = new CCDataObject();
		put(theKey, myResult);
		return myResult;
	}

	/**
	 * Put a key/value pair in the {@linkplain CCDataObject}, but only if the
	 * key and the value are both non-null.
	 *
	 * @param key A key string.
	 * @param value An object which is the value. It should be of one of these
	 *            types: Boolean, Double, Integer, {@linkplain CCDataArray},
	 *            {@linkplain CCDataObject}, Long, String, or the
	 *            {@linkplain CCDataObject}.NULL object.
	 * @return
	 * @throws CCDataException If the value is a non-finite number.
	 */
	public Object putOpt(String key, Object value) throws CCDataException {
		if (key != null && value != null) {
			return put(key, value);
		}
		return null;
	}

	/**
	 * Determine if two JSONObjects are similar. They must contain the same set
	 * of names which must be associated with similar values.
	 *
	 * @param other The other {@linkplain CCDataObject}
	 * @return true if they are equal
	 */
	public boolean similar(Object other) {
		try {
			if (!(other instanceof CCDataObject)) {
				return false;
			}
			Set<String> set = keySet();
			if (!set.equals(((CCDataObject) other).keySet())) {
				return false;
			}
			Iterator<String> iterator = set.iterator();
			while (iterator.hasNext()) {
				String name = iterator.next();
				Object valueThis = get(name);
				Object valueOther = ((CCDataObject) other).get(name);
				if (valueThis instanceof CCDataObject) {
					if (!((CCDataObject) valueThis).similar(valueOther)) {
						return false;
					}
				} else if (valueThis instanceof CCDataArray) {
					if (!((CCDataArray) valueThis).similar(valueOther)) {
						return false;
					}
				} else if (!valueThis.equals(valueOther)) {
					return false;
				}
			}
			return true;
		} catch (Throwable exception) {
			return false;
		}
	}

	/**
	 * Produce a {@linkplain CCDataArray} containing the values of the members
	 * of this {@linkplain CCDataObject}.
	 *
	 * @param names A {@linkplain CCDataArray} containing a list of key strings.
	 *            This determines the sequence of the values in the result.
	 * @return A {@linkplain CCDataArray} of values.
	 * @throws CCDataException If any of the values are non-finite numbers.
	 */
	public CCDataArray toJSONArray(CCDataArray names) throws CCDataException {
		if (names == null || names.size() == 0) {
			return null;
		}
		CCDataArray ja = new CCDataArray();
		for (int i = 0; i < names.size(); i += 1) {
			ja.add(get(names.getString(i)));
		}
		return ja;
	}

	@Override
	public Iterator<CCDataObject> iterator() {
		return new Iterator<CCDataObject>() {
			
			private List<String> _myKeys = new ArrayList<>(keySet());
			private int i = 0;

			@Override
			public boolean hasNext() {
				for(int j = i;j < _myKeys.size();j++){
					Object myValue = get(_myKeys.get(j));
					if(myValue instanceof CCDataObject){
						return true;
					}
				}
				return false;
			}

			@Override
			public CCDataObject next() {
				for(;i < _myKeys.size();i++){
					Object myValue = get(_myKeys.get(i));
					if(myValue instanceof CCDataObject){
						return (CCDataObject)myValue;
					}
				}
				return null;
			}
		};
	}

}
