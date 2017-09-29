package cc.creativecomputing.io.data;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import cc.creativecomputing.core.io.format.CCDataSerializable;
import cc.creativecomputing.core.logging.CCLog;


public class CCDataArray extends ArrayList<Object> {
	
	private class TypedIterator<Type> implements Iterator<Type>, Iterable<Type>{

		private int _myCounter = 0;
		

		@Override
		public boolean hasNext() {
			return _myCounter < size() - 1;
		}

		@SuppressWarnings("unchecked")
		@Override
		public Type next() {
			return (Type)get(_myCounter++);
		}

		@Override
		public Iterator<Type> iterator() {
			return this;
		}
		
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -5961129861018298564L;

	/**
	 * Construct an empty {@linkplain CCDataArray}.
	 */
	public CCDataArray() {
		super();
	}

	/**
	 * Construct a {@linkplain CCDataArray} from a Collection.
	 *
	 * @param theCollection A Collection.
	 */
	public CCDataArray(Collection<Object> theCollection) {
		this();

		if (theCollection == null)
			return;

		for (Object myObject : theCollection) {
			if (myObject instanceof CCDataSerializable) {
				CCDataObject myObjectData = new CCDataObject();
				((CCDataSerializable) myObject).data(myObjectData);
				add (myObject);
			}
			else {
				add(CCDataUtil.wrap(myObject));
			}
		}
	}

	/**
	 * Construct a {@linkplain CCDataArray} from an array
	 *
	 * @throws CCDataException If not an array.
	 */
	public CCDataArray(Object theArray) throws CCDataException {
		this();
		if (!theArray.getClass().isArray())
			throw new CCDataException(CCDataArray.class.getSimpleName() + " initial value should be a string or collection or array.");

		int size = Array.getLength(theArray);
		for (int i = 0; i < size; i++) {
			add(CCDataUtil.wrap(Array.get(theArray, i)));
		}
	}
	
	public CCDataObject createObject(){
		CCDataObject myResult = new CCDataObject();
		add(myResult);
		return myResult;
	}

	/**
	 * Get the boolean value associated with an index. The string values "true"
	 * and "false" are converted to boolean.
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @return The truth.
	 * @throws CCDataException If there is no value for the index or if the
	 *             value is not convertible to boolean.
	 */
	public boolean getBoolean(int theIndex) throws CCDataException {
		try {
			return CCDataUtil.booleanValue(get(theIndex));
		} catch (Exception e) {
			throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] is not a boolean.");
		}
	}

	/**
	 * Get the optional boolean value associated with an index. It returns the
	 * defaultValue if there is no value at that index or if it is not a Boolean
	 * or the String "true" or "false" (case insensitive).
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @param theDefaultValue A boolean default.
	 * @return The truth.
	 */
	public boolean getBoolean(int theIndex, boolean theDefaultValue) {
		try {
			return getBoolean(theIndex);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the double value associated with an index.
	 *
	 * @param index The index must be between 0 and size() - 1.
	 * @return The value.
	 * @throws CCDataException If the key is not found or if the value cannot be
	 *             converted to a number.
	 */
	public double getDouble(int theIndex) throws CCDataException {
		try {
			return CCDataUtil.doubleValue(get(theIndex));
		} catch (CCDataException e) {
			throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] is not a number.");
		}
	}

	/**
	 * Get the optional double value associated with an index. The defaultValue
	 * is returned if there is no value for the index, or if the value is not a
	 * number and cannot be converted to a number.
	 *
	 * @param theIndex subscript
	 * @param theDefaultValue The default value.
	 * @return The value.
	 */
	public double getDouble(int theIndex, double theDefaultValue) {
		try {
			return getDouble(theIndex);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the float value associated with an index.
	 *
	 * @param index The index must be between 0 and size() - 1.
	 * @return The value.
	 * @throws CCDataException If the key is not found or if the value cannot be
	 *             converted to a number.
	 */
	public float getFloat(int theIndex) throws CCDataException {
		try {
			return CCDataUtil.floatValue(get(theIndex));
		} catch (CCDataException e) {
			throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] is not a number.");
		}
	}

	/**
	 * Get the optional float value associated with an index. The defaultValue
	 * is returned if there is no value for the index, or if the value is not a
	 * number and cannot be converted to a number.
	 *
	 * @param theIndex subscript
	 * @param theDefaultValue The default value.
	 * @return The value.
	 */
	public float getFloat(int theIndex, float theDefaultValue) {
		try {
			return getFloat(theIndex);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the long value associated with an index.
	 *
	 * @param index The index must be between 0 and size() - 1.
	 * @return The value.
	 * @throws CCDataException If the key is not found or if the value cannot be
	 *             converted to a number.
	 */
	public long getLong(int theIndex) throws CCDataException {
		try {
			return CCDataUtil.longValue(get(theIndex));
		} catch (CCDataException e) {
			throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] is not a number.");
		}
	}

	/**
	 * Get the optional long value associated with an index. The defaultValue is
	 * returned if there is no value for the index, or if the value is not a
	 * number and cannot be converted to a number.
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @param theDefaultValue The default value.
	 * @return The value.
	 */
	public long getLong(int theIndex, long theDefaultValue) {
		try {
			return getLong(theIndex);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the int value associated with an index.
	 *
	 * @param index The index must be between 0 and size() - 1.
	 * @return The value.
	 * @throws CCDataException If the key is not found or if the value cannot be
	 *             converted to a number.
	 */
	public int getInt(int theIndex) throws CCDataException {
		try {
			return CCDataUtil.intValue(get(theIndex));
		} catch (CCDataException e) {
			throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] is not a number.");
		}
	}

	/**
	 * Get the optional int value associated with an index. The defaultValue is
	 * returned if there is no value for the index, or if the value is not a
	 * number and cannot be converted to a number.
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @param theDefaultValue The default value.
	 * @return The value.
	 */
	public int getInt(int theIndex, int theDefaultValue) {
		try {
			return getInt(theIndex);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the short value associated with an index.
	 *
	 * @param index The index must be between 0 and size() - 1.
	 * @return The value.
	 * @throws CCDataException If the key is not found or if the value cannot be
	 *             converted to a number.
	 */
	public short getShort(int theIndex) throws CCDataException {
		try {
			return CCDataUtil.shortValue(get(theIndex));
		} catch (CCDataException e) {
			throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] is not a number.");
		}
	}

	/**
	 * Get the optional short value associated with an index. The defaultValue
	 * is returned if there is no value for the index, or if the value is not a
	 * number and cannot be converted to a number.
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @param theDefaultValue The default value.
	 * @return The value.
	 */
	public short getShort(int theIndex, short theDefaultValue) {
		try {
			return getShort(theIndex);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Get the {@linkplain CCDataArray} associated with an index.
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @return A {@linkplain CCDataArray} value.
	 * @throws CCDataException If there is no value for the index. or if the
	 *             value is not a {@linkplain CCDataArray}
	 */
	public CCDataArray getArray(int theIndex) throws CCDataException {
		Object myResult = get(theIndex);
		if (myResult instanceof CCDataArray) {
			return (CCDataArray) myResult;
		}
		throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] is not a " + CCDataArray.class.getSimpleName());
	}

	/**
	 * Get the {@linkplain CCDataObject} associated with an index.
	 *
	 * @param theIndex subscript
	 * @return A {@linkplain CCDataObject} value.
	 * @throws CCDataException If there is no value for the index or if the
	 *             value is not a {@linkplain CCDataObject}
	 */
	public CCDataObject getObject(int theIndex) throws CCDataException {
		Object myResult = get(theIndex);
		if (myResult instanceof CCDataObject) {
			return (CCDataObject) myResult;
		}
		throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] is not a " + CCDataObject.class.getSimpleName());
	}
	
	public Iterable<CCDataObject> objects(){
		return new TypedIterator<CCDataObject>();
	}

	/**
	 * Get the string associated with an index.
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @return A string value.
	 * @throws CCDataException If there is no string value for the index.
	 */
	public String getString(int theIndex) throws CCDataException {
		Object myResult = get(theIndex);
		if (myResult instanceof String) {
			return (String) myResult;
		}
		throw new CCDataException(CCDataArray.class.getSimpleName() + "[" + theIndex + "] not a string.");
	}

	/**
	 * Get the optional string associated with an index. The defaultValue is
	 * returned if the key is not found.
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @param theDefaultValue The default value.
	 * @return A String value.
	 */
	public String getString(int theIndex, String theDefaultValue) {
		try {
			return getString(theIndex);
		} catch (Exception e) {
			return theDefaultValue;
		}
	}

	/**
	 * Determine if the value is null.
	 *
	 * @param theIndex The index must be between 0 and size() - 1.
	 * @return true if the value at the index is null, or if there is no value.
	 */
	public boolean isNull(int theIndex) {
		return get(theIndex) == null;
	}

	/**
	 * Make a string from the contents of this {@linkplain CCDataArray}. The
	 * <code>separator</code> string is inserted between each element. Warning:
	 * This method assumes that the data structure is acyclical.
	 *
	 * @param theSeparator A string that will be inserted between the elements.
	 * @return a string.
	 * @throws CCDataException If the array contains an invalid number.
	 */
	public String join(String theSeparator) throws CCDataException {
		int myLength = size();
		StringBuilder myResult = new StringBuilder();

		for (int i = 0; i < myLength; i++) {
			if (i > 0) {
				myResult.append(theSeparator);
			}
			myResult.append(CCDataUtil.valueToString(get(i)));
		}
		return myResult.toString();
	}

	/**
	 * Determine if two JSONArrays are similar. They must contain similar
	 * sequences.
	 *
	 * @param other The other {@linkplain CCDataArray}
	 * @return true if they are equal
	 */
	public boolean similar(Object other) {
		if (!(other instanceof CCDataArray)) {
			return false;
		}
		int len = size();
		if (len != ((CCDataArray) other).size()) {
			return false;
		}
		for (int i = 0; i < len; i += 1) {
			Object valueThis = get(i);
			Object valueOther = ((CCDataArray) other).get(i);
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
	}

	/**
	 * Produce a {@linkplain CCDataObject} by combining a
	 * {@linkplain CCDataArray} of names with the values of this
	 * {@linkplain CCDataArray}.
	 *
	 * @param names A {@linkplain CCDataArray} containing a list of key strings.
	 *            These will be paired with the values.
	 * @return A {@linkplain CCDataObject}, or null if there are no names or if
	 *         this {@linkplain CCDataArray} has no values.
	 * @throws CCDataException If any of the names are null.
	 */
	public CCDataObject toJSONObject(CCDataArray names) throws CCDataException {
		if (names == null || names.size() == 0 || size() == 0) {
			return null;
		}
		CCDataObject jo = new CCDataObject();
		for (int i = 0; i < names.size(); i += 1) {
			jo.put(names.getString(i), get(i));
		}
		return jo;
	}

	public static void main(String[] args) {
		CCDataArray myArray = new CCDataArray();
		myArray.add(0.0);
		CCLog.info(myArray.getDouble(0));
	}

}
