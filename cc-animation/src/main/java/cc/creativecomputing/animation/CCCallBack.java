/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.animation;
// taken from http://stackoverflow.com/questions/443708/callback-functions-in-java

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import cc.creativecomputing.core.logging.CCLog;

public class CCCallBack {
	private String _myMethodName;
	private Object _myScope;
	
	public CCCallBack(Object theScope, String theMethodName) {
		_myMethodName = theMethodName;
		_myScope = theScope;
	}
	public Object invoke(Object... theParameters) throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
		Method myMethod = _myScope.getClass().getMethod(_myMethodName, getParameterClasses(theParameters));
		return myMethod.invoke(_myScope, theParameters);
	}
	private Class<?>[] getParameterClasses(Object... theParameters) {
		Class<?>[] myClasses = new Class<?>[theParameters.length];
		for(int i=0; i < myClasses.length; i++) {
			myClasses[i] = theParameters[i].getClass();
			CCLog.info(theParameters[i].getClass().getName());
		}
		return myClasses;
	}
}
