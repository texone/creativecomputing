package cc.creativecomputing.gl.generator;


import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.CCIOUtil;

public class CCEnumGenerator {
	
	private static final String PACKAGE = "cc.creativecomputing.gl.enums";
	private static final String FOLDER = "cc/creativecomputing/gl/enums/";
	
	private static String getClassName(String theFileName){
		String[] myParts = theFileName.substring(0, theFileName.indexOf(".")).split("_");
		StringBuffer myResult = new StringBuffer();
		myResult.append("GL");
		for(String myPart:myParts){
			myResult.append(myPart.substring(0, 1).toUpperCase());
			myResult.append(myPart.substring(1));
		}
		return myResult.toString();
	}
	
	private static String getEnumLine(String theLine){
		if(theLine.contains(":"))theLine = theLine.substring(0, theLine.indexOf(":"));
		return "	" + theLine + "(GL4.GL_"+theLine+"),\n";
	}
	
	private static String getSwitchLine(String theLine){
		if(theLine.contains(":"))theLine = theLine.substring(0, theLine.indexOf(":"));
		return "		case GL4.GL_"+theLine+":return " + theLine + ";\n";
	}
	
	public static void main(String[] args) {
		String[] myEnumFiles = CCIOUtil.list("sources/enums");
		for(String myEnumFile:myEnumFiles){
			String[] myLines = CCIOUtil.loadStrings("sources/enums/" + myEnumFile);
			
			String myClassName = getClassName(myEnumFile);
			StringBuffer myJavaCode = new StringBuffer();
			
			myJavaCode.append("package " + PACKAGE + ";\n");
			myJavaCode.append("\n");
			myJavaCode.append("import javax.media.opengl.GL4;\n");
			myJavaCode.append("\n");
			myJavaCode.append("public enum "+myClassName+" {\n");
			
			for(String myLine:myLines){
				if(myLine.startsWith("#"))continue;
				if(myLine.trim().length() == 0)continue;
				myJavaCode.append(getEnumLine(myLine));
			}
			myJavaCode.deleteCharAt(myJavaCode.length()-1);
			myJavaCode.deleteCharAt(myJavaCode.length()-1);
			myJavaCode.append(";\n");
			myJavaCode.append("	\n");
			myJavaCode.append("	private int _myGLID;\n");
			myJavaCode.append("	\n");
			myJavaCode.append("	private " + myClassName + "(int theGLID){\n");
			myJavaCode.append("		_myGLID = theGLID;\n");
			myJavaCode.append("	}\n");
			myJavaCode.append("	\n");
			myJavaCode.append("	public int glID(){\n");
			myJavaCode.append("		return _myGLID;\n");
			myJavaCode.append("	}\n");
			myJavaCode.append("	\n");
			myJavaCode.append("	public static "+myClassName+" fromGLID(int theGLID){\n");
			myJavaCode.append("		switch(theGLID){\n");
			
			for(String myLine:myLines){
				if(myLine.startsWith("#"))continue;
				if(myLine.trim().length() == 0)continue;
				myJavaCode.append(getSwitchLine(myLine));
			}
			
			myJavaCode.append("		}\n");
			myJavaCode.append("		return null;\n");
			myJavaCode.append("	}\n");
			
			myJavaCode.append("}\n");
			CCIOUtil.saveStrings(CCIOUtil.appPath("src/"+FOLDER+myClassName+".java"), new String[]{myJavaCode.toString()});
			
			CCLog.info(myJavaCode);
		}
	}
}
