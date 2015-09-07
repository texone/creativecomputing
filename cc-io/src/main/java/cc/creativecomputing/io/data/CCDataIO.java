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
package cc.creativecomputing.io.data;

import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;
import cc.creativecomputing.io.data.format.CCDataFormat;
import cc.creativecomputing.io.data.format.CCJsonFormat;
import cc.creativecomputing.io.data.format.CCXMLFormat;



/**
 * <p>
 * Use CCDataIO for simple loading and saving of data files.
 * </p>
 * 
 * @see CCDataObject
 */
public class CCDataIO{
	
	static public enum CCDataFormats{
		JSON("json"),
		XML("xml");
		
		public final String fileExtension;
		
		private CCDataFormats(final String theFileExtension){
			fileExtension = theFileExtension;
		}
	}
	
	private static Map<CCDataFormats, CCDataFormat<?>> formatMap = new HashMap<>();
	
	static{
		formatMap.put(CCDataFormats.JSON, new CCJsonFormat());
		formatMap.put(CCDataFormats.XML, new CCXMLFormat());
	}
	
	
	/**
	 * Returns a {@linkplain CCDataObject} containing the data document as graph structure.
	 * @param theDocumentPath
	 * @return data for the given url
	 */
	public static CCDataObject createDataObject(final Path theDocumentPath, final boolean theIgnoreLineFeed, final CCDataFormats theFormat, OpenOption...theOptions){
		CCDataFormat<?> myFormat = formatMap.get(theFormat);
		return myFormat.loadAsDataObject(theDocumentPath, theIgnoreLineFeed, theOptions);
	}
	
	public static Map<String, Object> createMap(final Path theDocumentPath, final boolean theIgnoreLineFeed, final CCDataFormats theFormat, OpenOption...theOptions){
		return createDataObject(theDocumentPath, theIgnoreLineFeed, theFormat, theOptions);
	}
	
	public static CCDataObject createDataObject(final Path theDocumentPath){
		String myExtension = CCNIOUtil.fileExtension(theDocumentPath);
		CCDataFormats myFormat = CCDataFormats.XML;
		switch(myExtension){
		case "xml":
			myFormat = CCDataFormats.XML;
			break;
		case "json":
			myFormat = CCDataFormats.JSON;
			break;
		}
		return createDataObject(theDocumentPath, true, myFormat);
	}
	
	public static CCDataObject createDataObject(final Path theDocumentPath, final CCDataFormats theFormat){
		return createDataObject(theDocumentPath, true, theFormat);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static CCDataObject parseToObject(Object theDocument, final CCDataFormats theFormat){
		CCDataFormat myFormat = formatMap.get(theFormat);
		return myFormat.parseAsDataObject(theDocument);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static Map<String, Object> parseToMap(Object theDocument, final CCDataFormats theFormat){
		CCDataFormat myFormat = formatMap.get(theFormat);
		return myFormat.parseAsDataObject(theDocument);
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked"})
	public static Object toFormatType(Map<String, Object> theObject, final CCDataFormats theFormat){
		CCDataFormat myFormat = formatMap.get(theFormat);
		return myFormat.toFormatType(theObject);
	}

	/**
	 * Saves the XMLElement to a given filename.
	 * 
	 * @param theDataObject the element to be saved
	 * @param theDocumentPath path to save the XMLElement as XML File 
	 */
	public static void saveDataObject(final CCDataObject theDataObject, Path theDocumentPath, final CCDataFormats theFormat){
		CCDataFormat<?> myFormat = formatMap.get(theFormat);
		myFormat.save(theDataObject, theDocumentPath);
	}
	
	public static void saveDataObject(final CCDataObject theDataObject, Path theDocumentPath){
		String myExtension = CCNIOUtil.fileExtension(theDocumentPath);
		CCLog.info(myExtension);
		if(myExtension == null)myExtension = "json";
		theDocumentPath = Paths.get(theDocumentPath.toString() + ".json");
		CCDataFormats myFormat = CCDataFormats.XML;
		switch(myExtension){
		case "xml":
			myFormat = CCDataFormats.XML;
			break;
		case "json":
			myFormat = CCDataFormats.JSON;
			break;
		}
		saveDataObject(theDataObject, theDocumentPath, myFormat);
	}

}
