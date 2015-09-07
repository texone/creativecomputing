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
package cc.creativecomputing.model.collada;

/**
 * <p>
 * Lucerne University of Applied Sciences and Arts <a href="http://www.hslu.ch">http://www.hslu.ch</a>
 * </p>
 * 
 * <p>
 * This source is free; you can redistribute it and/or modify it under the terms of the GNU General Public License and
 * by nameing of the originally author
 * </p>
 * 
 * <p>
 * Description: some xml-Subtags have common behavior. Such Helper-Classes implement the following Methods
 * </p>
 * 
 * @author Markus Zimmermann <a href="http://www.die-seite.ch">http://www.die-seite.ch</a>
 * @version 1.0
 */
abstract class CCColladaSubTag {

	/**
	 * links to the next sub-tag (if exists)to get more informations. Otherwise it returns the ID
	 * 
	 * @return the next Tag-ID or source, whatever
	 */
	abstract String source();

	/**
	 * returns the ID of this xml-tag
	 * 
	 * @return the tag-ID
	 */
	abstract String id();

}
