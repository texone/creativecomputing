package cc.creativecomputing.io.data;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;


public class CCDataUtil {
//	/**
//     * Construct a JSONObject from an Object using bean getters. It reflects on
//     * all of the public methods of the object. For each of the methods with no
//     * parameters and a name starting with <code>"get"</code> or
//     * <code>"is"</code> followed by an uppercase letter, the method is invoked,
//     * and a key and the value returned from the getter method are put into the
//     * new JSONObject.
//     *
//     * The key is formed by removing the <code>"get"</code> or <code>"is"</code>
//     * prefix. If the second remaining character is not upper case, then the
//     * first character is converted to lower case.
//     *
//     * For example, if an object has a method named <code>"getName"</code>, and
//     * if the result of calling <code>object.getName()</code> is
//     * <code>"Larry Fine"</code>, then the JSONObject will contain
//     * <code>"name": "Larry Fine"</code>.
//     *
//     * @param bean
//     *            An object that has getter methods that should be used to make
//     *            a JSONObject.
//     */
//    public CCDataObject(Object bean) {
//        this();
//        this.populateMap(bean);
//    }
//
//    /**
//     * Construct a JSONObject from an Object, using reflection to find the
//     * public members. The resulting JSONObject's keys will be the strings from
//     * the names array, and the values will be the field values associated with
//     * those keys in the object. If a key is not found or not visible, then it
//     * will not be copied into the new JSONObject.
//     *
//     * @param object
//     *            An object that has fields that should be used to make a
//     *            JSONObject.
//     * @param names
//     *            An array of strings, the names of the fields to be obtained
//     *            from the object.
//     */
//    public CCDataObject(Object object, String names[]) {
//        this();
//        Class c = object.getClass();
//        for (int i = 0; i < names.length; i += 1) {
//            String name = names[i];
//            try {
//                this.putOpt(name, c.getField(name).get(object));
//            } catch (Exception ignore) {
//            }
//        }
//    }
	
	private static void populateMap(CCDataObject theObject, Object bean) {
		Class<?> klass = bean.getClass();

		// If klass is a System class then set includeSuperClass to false.

		boolean includeSuperClass = klass.getClassLoader() != null;

		Method[] methods = includeSuperClass ? klass.getMethods() : klass
				.getDeclaredMethods();
		for (int i = 0; i < methods.length; i += 1) {
			try {
				Method method = methods[i];
				if (Modifier.isPublic(method.getModifiers())) {
					String name = method.getName();
					String key = "";
					if (name.startsWith("get")) {
						if ("getClass".equals(name) || "getDeclaringClass".equals(name)) {
							key = "";
						} else {
							key = name.substring(3);
						}
					} else if (name.startsWith("is")) {
						key = name.substring(2);
					}
					if (key.length() > 0 && Character.isUpperCase(key.charAt(0)) && method.getParameterTypes().length == 0) {
						if (key.length() == 1) {
							key = key.toLowerCase();
						} else if (!Character.isUpperCase(key.charAt(1))) {
							key = key.substring(0, 1).toLowerCase() + key.substring(1);
						}

						Object result = method.invoke(bean, (Object[]) null);
						if (result != null) {
							theObject.put(key, CCDataUtil.wrap(result));
						}
					}
				}
			} catch (Exception ignore) {
			}
		}
	}
    
	/**
     * Wrap an object, if necessary. If the object is null, return the NULL
     * object. If it is an array or collection, wrap it in a JSONArray. If it is
     * a map, wrap it in a JSONObject. If it is a standard property (Double,
     * String, et al) then it is already wrapped. Otherwise, if it comes from
     * one of the java packages, turn it into a string. And if it doesn't, try
     * to wrap it in a JSONObject. If the wrapping fails, then null is returned.
     *
     * @param theObject
     *            The object to wrap
     * @return The wrapped value
     */
    @SuppressWarnings("unchecked")
	public static Object wrap(Object theObject) {
        try {
            if (theObject == null) {
                return null;
            }
            if (theObject instanceof CCDataObject || 
            	theObject instanceof CCDataArray || 
            	theObject instanceof Byte || 
            	theObject instanceof Character || 
            	theObject instanceof Short || 
            	theObject instanceof Integer || 
            	theObject instanceof Long || 
            	theObject instanceof Boolean || 
            	theObject instanceof Float || 
            	theObject instanceof Double || 
            	theObject instanceof String
            ) {
                return theObject;
            }
            

            if (theObject instanceof Collection) {
                return new CCDataArray((Collection<Object>) theObject);
            }
            if (theObject.getClass().isArray()) {
                return new CCDataArray(theObject);
            }
            if (theObject instanceof Map) {
                return new CCDataObject((Map<String, Object>) theObject);
            }
            Package objectPackage = theObject.getClass().getPackage();
            String objectPackageName = objectPackage != null ? objectPackage.getName() : "";
            if (objectPackageName.startsWith("java.")
                    || objectPackageName.startsWith("javax.")
                    || theObject.getClass().getClassLoader() == null) {
                return theObject.toString();
            }
            CCDataObject myResult = new CCDataObject();
            populateMap(myResult, theObject);
            return myResult;
        } catch (Exception exception) {
            return null;
        }
    }
    
    /**
     * Get the boolean value of the given object if possible. The string values "true"
     * and "false" are converted to boolean.
     *
     * @param theObject the object to check
     * @return The truth.
     * @throws CCDataException
     *             If there is no value for the index or if the value is not
     *             convertible to boolean.
     */
    public static boolean booleanValue(Object theObject){
        if (theObject.equals(Boolean.FALSE) || (theObject instanceof String && ((String) theObject).equalsIgnoreCase("false"))) {
            return false;
        } else if (theObject.equals(Boolean.TRUE) || (theObject instanceof String && ((String) theObject).equalsIgnoreCase("true"))) {
            return true;
        }
        throw new CCDataException(theObject.getClass().getSimpleName() +" is not a boolean.");
    }
    
    /**
     * Get the double value of the given object if possible.
     *
     * @param theObject the object to check
     * @return The value.
     * @throws CCDataException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public static double doubleValue(Object theObject){
    	try {
            return theObject instanceof Number ? ((Number) theObject).doubleValue() : Double.parseDouble((String) theObject);
        } catch (Exception e) {
            throw new CCDataException(theObject.getClass().getSimpleName() +" is not a number.");
        }
    }
    
    /**
     * Get the float value of the given object if possible.
     *
     * @param theObject the object to check
     * @return The value.
     * @throws CCDataException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public static float floatValue(Object theObject){
    	try {
            return theObject instanceof Number ? ((Number) theObject).floatValue() : Float.parseFloat((String) theObject);
        } catch (Exception e) {
            throw new CCDataException(theObject.getClass().getSimpleName() +" is not a number.");
        }
    }
    
    /**
     * Get the long value of the given object if possible.
     *
     * @param theObject the object to check
     * @return The value.
     * @throws CCDataException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public static long longValue(Object theObject){
    	try {
            return theObject instanceof Number ? ((Number) theObject).longValue() : Long.parseLong((String) theObject);
        } catch (Exception e) {
            throw new CCDataException(theObject.getClass().getSimpleName() +" is not a number.");
        }
    }
    
    /**
     * Get the int value of the given object if possible.
     *
     * @param theObject the object to check
     * @return The value.
     * @throws CCDataException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public static int intValue(Object theObject){
    	try {
            return theObject instanceof Number ? ((Number) theObject).intValue() : Integer.parseInt((String) theObject);
        } catch (Exception e) {
            throw new CCDataException(theObject.getClass().getSimpleName() +" is not a number.");
        }
    }
    
    /**
     * Get the short value of the given object if possible.
     *
     * @param theObject the object to check
     * @return The value.
     * @throws CCDataException
     *             If the key is not found or if the value cannot be converted
     *             to a number.
     */
    public static short shortValue(Object theObject){
    	try {
            return theObject instanceof Number ? ((Number) theObject).shortValue() : Short.parseShort((String) theObject);
        } catch (Exception e) {
            throw new CCDataException(theObject.getClass().getSimpleName() +" is not a number.");
        }
    }
    
    /**
     * Try to convert a string into a number, boolean, or null. If the string
     * can't be converted, return the string.
     *
     * @param string
     *            A String.
     * @return A simple JSON value.
     */
    public static Object stringToValue(String string) {
        if (string.equals("")) {
            return string;
        }
        if (string.equalsIgnoreCase("true")) {
            return Boolean.TRUE;
        }
        if (string.equalsIgnoreCase("false")) {
            return Boolean.FALSE;
        }
        if (string.equalsIgnoreCase("null")) {
            return null;
        }

        /*
         * If it might be a number, try converting it. If a number cannot be
         * produced, then the value will just be a string.
         */

        char b = string.charAt(0);
        if ((b >= '0' && b <= '9') || b == '-') {
            try {
                if (string.indexOf('.') > -1 || string.indexOf('e') > -1 || string.indexOf('E') > -1) {
                   Double d = Double.valueOf(string);
                    if (!d.isInfinite() && !d.isNaN()) {
                        return d;
                    }
                } else {
                    Long myLong = new Long(string);
                    if (string.equals(myLong.toString())) {
                        if (myLong == myLong.intValue()) {
                            return myLong.intValue();
                        } else {
                            return myLong;
                        }
                    }
                }
            } catch (Exception ignore) {
            }
        }
        return string;
    }

    

    /**
     * Make a JSON text of an Object value. If the object has an
     * value.toJSONString() method, then that method will be used to produce the
     * JSON text. The method is required to produce a strictly conforming text.
     * If the object does not contain a toJSONString method (which is the most
     * common case), then a text will be produced by other means. If the value
     * is an array or Collection, then a JSONArray will be made from it and its
     * toJSONString method will be called. If the value is a MAP, then a
     * JSONObject will be made from it and its toJSONString method will be
     * called. Otherwise, the value's toString method will be called, and the
     * result will be quoted.
     *
     * <p>
     * Warning: This method assumes that the data structure is acyclical.
     *
     * @param value
     *            The value to be serialized.
     * @return a printable, displayable, transmittable representation of the
     *         object, beginning with <code>{</code>&nbsp;<small>(left
     *         brace)</small> and ending with <code>}</code>&nbsp;<small>(right
     *         brace)</small>.
     * @throws CCDataException
     *             If the value is or contains an invalid number.
     */
    @SuppressWarnings("unchecked")
	public static String valueToString(Object value) throws CCDataException {
        if (value == null || value.equals(null)) {
            return "null";
        }
       
        if (value instanceof Number) {
            return CCDataUtil.numberToString((Number) value);
        }
        if (value instanceof Boolean || value instanceof CCDataObject
                || value instanceof CCDataArray) {
            return value.toString();
        }
        if (value instanceof Map) {
            return new CCDataObject((Map<String, Object>)value).toString();
        }
        if (value instanceof Collection) {
            return new CCDataArray((Collection<Object>) value).toString();
        }
        if (value.getClass().isArray()) {
            return new CCDataArray(value).toString();
        }
        return CCDataUtil.quote(value.toString());
    }

    /**
     * Produce a string from a double. The string "null" will be returned if the
     * number is not finite.
     *
     * @param d
     *            A double.
     * @return A String.
     */
    public static String doubleToString(double d) {
        if (Double.isInfinite(d) || Double.isNaN(d)) {
            return "null";
        }

// Shave off trailing zeros and decimal point, if possible.

        String string = Double.toString(d);
        if (string.indexOf('.') > 0 && string.indexOf('e') < 0
                && string.indexOf('E') < 0) {
            while (string.endsWith("0")) {
                string = string.substring(0, string.length() - 1);
            }
            if (string.endsWith(".")) {
                string = string.substring(0, string.length() - 1);
            }
        }
        return string;
    }
    
    /**
     * Throw an exception if the object is a NaN or infinite number.
     *
     * @param o
     *            The object to test.
     * @throws CCDataException
     *             If o is a non-finite number.
     */
    public static void testValidity(Object o) throws CCDataException {
        if (o != null) {
            if (o instanceof Double) {
                if (((Double) o).isInfinite() || ((Double) o).isNaN()) {
                    throw new CCDataException("JSON does not allow non-finite numbers.");
                }
            } else if (o instanceof Float) {
                if (((Float) o).isInfinite() || ((Float) o).isNaN()) {
                    throw new CCDataException("JSON does not allow non-finite numbers.");
                }
            }
        }
    }

    /**
     * Produce a string from a Number.
     *
     * @param number
     *            A Number
     * @return A String.
     * @throws CCDataException
     *             If n is a non-finite number.
     */
    public static String numberToString(Number number) throws CCDataException {
        if (number == null) {
            throw new CCDataException("Null pointer");
        }
        testValidity(number);

// Shave off trailing zeros and decimal point, if possible.

        String myResult = number.toString();
        if (myResult.indexOf('.') > 0 && myResult.indexOf('e') < 0 && myResult.indexOf('E') < 0) {
            while (myResult.endsWith("0")) {
                myResult = myResult.substring(0, myResult.length() - 1);
            }
            if (myResult.endsWith(".")) {
                myResult = myResult.substring(0, myResult.length() - 1);
            }
        }
        return myResult;
    }
    
    /**
     * Produce a string in double quotes with backslash sequences in all the
     * right places. A backslash will be inserted within </, producing <\/,
     * allowing JSON text to be delivered in HTML. In JSON text, a string cannot
     * contain a control character or an unescaped quote or backslash.
     *
     * @param theString
     *            A String
     * @return A String correctly formatted for insertion in a JSON text.
     */
    public static String quote(String theString) {
        StringWriter sw = new StringWriter();
        synchronized (sw.getBuffer()) {
            try {
                return quote(theString, sw).toString();
            } catch (IOException ignored) {
                // will never happen - we are writing to a string writer
                return "";
            }
        }
    }

    public static Writer quote(String string, Writer w) throws IOException {
        if (string == null || string.length() == 0) {
            w.write("\"\"");
            return w;
        }

        char b;
        char c = 0;
        String hhhh;
        int i;
        int len = string.length();

        w.write('"');
        for (i = 0; i < len; i += 1) {
            b = c;
            c = string.charAt(i);
            switch (c) {
            case '\\':
            case '"':
                w.write('\\');
                w.write(c);
                break;
            case '/':
                if (b == '<') {
                    w.write('\\');
                }
                w.write(c);
                break;
            case '\b':
                w.write("\\b");
                break;
            case '\t':
                w.write("\\t");
                break;
            case '\n':
                w.write("\\n");
                break;
            case '\f':
                w.write("\\f");
                break;
            case '\r':
                w.write("\\r");
                break;
            default:
                if (c < ' ' || (c >= '\u0080' && c < '\u00a0')
                        || (c >= '\u2000' && c < '\u2100')) {
                    w.write("\\u");
                    hhhh = Integer.toHexString(c);
                    w.write("0000", 0, 4 - hhhh.length());
                    w.write(hhhh);
                } else {
                    w.write(c);
                }
            }
        }
        w.write('"');
        return w;
    }
}
