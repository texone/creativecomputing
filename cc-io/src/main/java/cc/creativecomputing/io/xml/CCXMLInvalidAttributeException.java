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
package cc.creativecomputing.io.xml;

/**
 * An InvalidAttributeException occurs when a XMLElement does not have the requested 
 * attribute, or when you use getIntAttribute() or getFloatAttribute() for Attributes 
 * that are not numeric. Another reason could be that you try to add an attribute to a PCDATA 
 * section.
 */

public class CCXMLInvalidAttributeException extends RuntimeException{

	/**
	 * 
	 */
	private static final long serialVersionUID = 904551115681531931L;

	public CCXMLInvalidAttributeException(String attributeName){
		super("You can't add the attribute " + attributeName + " to a PCDATA section.");
	}

	public CCXMLInvalidAttributeException(String elementName, String attributeName){
		super("The XMLElement " + elementName + " has no attribute " + attributeName + "!");
	}

	public CCXMLInvalidAttributeException(String elementName, String attributeName, String type){
		super("The XMLElement " + elementName + " has no attribute " + attributeName + " of the type " + type + "!");
	}

	public CCXMLInvalidAttributeException(String elementName, String attributeName, String type, Exception e){
		super("The XMLElement " + elementName + " has no attribute " + attributeName + " of the type " + type + "!",e);
	}

}
