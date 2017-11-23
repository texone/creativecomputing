package cc.creativecomputing.io.xml;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCNIOUtil;

public class CCCSVIO {
	
	public static String[] readHeader(String theLine, String theSeparator){
		String[] myResult = theLine.split(theSeparator);
		for(int i = 0; i < myResult.length;i++){
			if(myResult[i].trim().length() == 0){
				myResult[i] = null;
			}
		}
		return myResult;
	}

	public static CCDataElement createDataElement(Path thePath, String theHeader, String theSeparator){
		if(theSeparator == null)theSeparator = ";";
		CCDataElement myResult = new CCDataElement("table");
		List<String> myLines = CCNIOUtil.loadStrings(thePath);
		int myStart = 0;
		String[] myAttributes;
		if(theHeader != null){
			myAttributes = readHeader(theHeader, theSeparator);
		}else{
			myAttributes = readHeader(myLines.get(0), theSeparator);
			myStart = 1;
		}
		for(int i = myStart; i < myLines.size();i++){
			String myLine = myLines.get(i);
			if(myLine.trim().startsWith("#"))continue;
			String[] myValues = myLine.split(theSeparator);
			CCDataElement myEntryData = myResult.createChild("entry");
			for(int j = 0; j < myValues.length;j++){
				if(j >= myAttributes.length)continue;
				if(myAttributes[j] == null)continue;
				myEntryData.addAttribute(myAttributes[j], myValues[j]);
			}
		}
		return myResult;
	}
	
	/**
	 * Saves the XMLElement to a given filename.
	 * 
	 * @param theDataElement the element to be saved
	 * @param thePath path to save the XMLElement as XML File 
	 * @param theEncoding used for the XML File 
	 */
	public static void saveDataElement(final CCDataElement theDataElement, Path thePath, String theEncoding){
		try{
			if(theDataElement == null || theDataElement.children().size() <= 0)return;
			CCDataElement myFirstElement = theDataElement.child(0);
			
			CCNIOUtil.createDirectories(thePath);
			PrintStream myWriter = new PrintStream(Files.newOutputStream(thePath), true, theEncoding);
			for(String myAttribute:myFirstElement.attributes()){
				myWriter.print(myAttribute + ";");
			}
			myWriter.println();
			for(CCDataElement myEntry:theDataElement){
				for(String myAttribute:myFirstElement.attributes()){
					myWriter.print(myEntry.attribute(myAttribute, "") + ";");
				}
				myWriter.println();
			}
			myWriter.flush();
			myWriter.close();
		}catch (Exception e){
			e.printStackTrace();
			CCLog.error("You cannot write to this destination. Make sure destionation is a valid path");
		}
	}
	
	/**
	 * Saves the XMLElement to a given filename using standard encoding "ISO-8859-1".
	 * 
	 * @param theDataElement the element to be saved
	 * @param thePath path to save the XMLElement as XML File 
	 */
	public static void saveDataElement(final CCDataElement theDataElement, Path thePath){
		saveDataElement(theDataElement, thePath, "ISO-8859-1");
	}
}
