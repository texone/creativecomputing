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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * As data sets become larger and more complex, they become harder to manipulate within a single container. 
 * One approach to managing this complexity is to divide the data into smaller pieces organized by some criteria. 
 * These modular pieces can then be stored in separate resources as libraries.
 * @author christianriekoff
 *
 */
public class CCColladaLibrary <ElementType extends CCColladaElement> implements Iterable<ElementType>{

	protected HashMap<String, ElementType> _myElementMap = new HashMap<String, ElementType>();
	protected List<ElementType> _myElementList = new ArrayList<ElementType>();
	
	public ElementType element(String theID) {
		return _myElementMap.get(theID);
	}
	
	public ElementType element(int theIndex) {
		return _myElementList.get(theIndex);
	}
	
	public List<ElementType> elements(){
		return _myElementList;
	}

	@Override
	public Iterator<ElementType> iterator() {
		return _myElementList.iterator();
	}
}
