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

import java.nio.file.Path;

import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.model.obj.CCOBJParser;

public class CCContent3dIO {
	
	public static enum CCContent3DFormat{
		OBJ;
	}
	
	public static final CCContent3DFormat OBJ = CCContent3DFormat.OBJ;
	
	
	public static CCModel createModel(final Path theFileName){
		final String myFormatExtension = CCNIOUtil.fileExtension(theFileName).toUpperCase();
		CCContent3DFormat myFormat;
		try {
			myFormat = CCContent3DFormat.valueOf(myFormatExtension);
		} catch (RuntimeException e1) {
			throw new RuntimeException("The given format is not supported: " + myFormatExtension, e1);
		}
		
		return createModel(theFileName,myFormat);
	}
	
	public static CCModel createModel(final Path thePath, CCContent3DFormat theFormat){
		final CCModel myModel = new CCModel();
		switch(theFormat){
		case OBJ:
			try {
				new CCOBJParser(thePath, myModel).readFile();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			return myModel;
		}
		
		throw new RuntimeException("The given format is not supported: " + theFormat);
	}
}
