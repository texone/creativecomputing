package cc.creativecomputing.io.xml;

import java.nio.file.Path;
import java.util.List;

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
}
