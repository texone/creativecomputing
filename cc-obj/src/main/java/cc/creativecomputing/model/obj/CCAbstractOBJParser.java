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
package cc.creativecomputing.model.obj;

import java.io.Reader;

import cc.creativecomputing.io.CCAbstractFileParser;


public abstract class CCAbstractOBJParser extends CCAbstractFileParser {
	

	// ObjectFileParser constructor
	protected CCAbstractOBJParser(Reader r) throws CCOBJParsingException{
		super(r);
		setup();
	}
	
	public abstract void readFile()throws CCOBJParsingException;

	@Override
	/**
	 * Sets up StreamTokenizer for reading ViewPoint .obj file format.
	 */
	public void setup() {
		// keep setup of the file parser
		super.setup();

		// Comment from ! to end of line
		commentChar('!');

		// These characters returned as tokens
		ordinaryChar('#');
		ordinaryChar('/');
	}
}
