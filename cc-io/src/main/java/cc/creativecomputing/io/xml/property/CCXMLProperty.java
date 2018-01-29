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
package cc.creativecomputing.io.xml.property;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Use this annotation to mark attributes of a class for xml serialization
 * @author christianriekoff
 *
 */
@Target({FIELD,METHOD})
@Retention(RUNTIME)
public @interface CCXMLProperty {
	/**
	 * name that is used inside the xml to store the attribute
	 * @return
	 */
	String name() default "";
	
	/**
	 * defines if the class attribute is stored as xml attribute or node.
	 * The default value is node.
	 * @return
	 */
	boolean node() default true;
	
	
	boolean optional() default false;
}
