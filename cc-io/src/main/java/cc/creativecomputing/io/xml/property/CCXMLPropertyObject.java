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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import cc.creativecomputing.io.xml.CCDataElement;


/**
 * <p>
 * Use this annotation to mark an object for xml serialization. Objects marked
 * with this annotation can be passed to {@linkplain CCDataElement#addChild(Object)}
 * method.
 * </p>
 * <p>
 * Use the {@linkplain CCXMLProperty} annotation to mark class attributes for XML 
 * serialization.
 * </p>
 * @author christianriekoff
 *
 */
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface CCXMLPropertyObject{
	/**
	 * Defines the name of the node in the xml document
	 * @return the name of the node
	 */
	String name() default "";
}
