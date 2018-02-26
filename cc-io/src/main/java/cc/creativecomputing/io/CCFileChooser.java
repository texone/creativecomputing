package cc.creativecomputing.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Pattern;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

public class CCFileChooser {
	
	private String[] _myExtensions = new String[0];
	
	public CCFileChooser(){
		super();
		_myCurrentDirectory = Paths.get(".");
	}
	
	public CCFileChooser(String...theExtensions){
		this();
		_myExtensions = theExtensions;
		
	}

	private Path _myCurrentDirectory;
	
	public String[] extensions(){
		return _myExtensions;
	}
	
	public void setDirectory(Path thePath){
		_myCurrentDirectory = thePath;
	}
	
	private static interface CCFileHandler{
		public String handleFile(String theTitle, String thePath, PointerBuffer thePattern, String theFilterDesc);
	}
	
	private static CCFileHandler openFiles = (theTitle, thePath, thePattern, theFilterDesc) -> {return tinyfd_openFileDialog(theTitle, thePath, thePattern, theFilterDesc, true);};
	
	private static CCFileHandler openFile = (theTitle, thePath, thePattern, theFilterDesc) -> {return tinyfd_openFileDialog(theTitle, thePath, thePattern, theFilterDesc, false);};
	
	private static CCFileHandler saveFile = (theTitle, thePath, thePattern, theFilterDesc) -> {return tinyfd_saveFileDialog(theTitle, thePath, thePattern, theFilterDesc);};
	
	private static CCFileHandler selectFolder = (theTitle, thePath, thePattern, theFilterDesc) -> {return tinyfd_selectFolderDialog(theTitle, thePath);};
	
	private Path[] handleFiles(final String theText, CCFileHandler theHandler) {
		try (MemoryStack stack = stackPush()) {
            PointerBuffer myFilterPatterns = stack.mallocPointer(_myExtensions.length);
            
            StringBuffer myFilterDesc = new StringBuffer("files (");

            for(String myExtension:_myExtensions){
                myFilterPatterns.put(stack.UTF8("*."+myExtension));
                myFilterDesc.append("*."+myExtension + ", ");
            }
            myFilterDesc.append(")");

            myFilterPatterns.flip();
            String myFile = theHandler.handleFile(theText, _myCurrentDirectory.toAbsolutePath().toString() + "/", myFilterPatterns, myFilterDesc.toString());
            if(myFile == null){
            	return null;
            }
            String[] myFiles = myFile.split(Pattern.quote("|"));
            
            Path[] myResult = new Path[myFiles.length];
            
            for(int i = 0; i < myFiles.length;i++){
            	myResult[i] = Paths.get(myFiles[i]);
				_myCurrentDirectory = myResult[i].getParent();
            	if(myResult[i].startsWith(CCNIOUtil.applicationPath)){
            		myResult[i] = CCNIOUtil.applicationPath.relativize(myResult[i]);
				}
            }
            return myResult;
		}
	}
	
	public Path[] openFiles(final String theText) {
		return handleFiles(theText, openFiles);
	}
	
	public Path openFile(final String theText){
		Path[] myPaths = handleFiles(theText, openFile);
		return myPaths == null || myPaths.length < 1 ? null : myPaths[0];
	}
	
	public Path saveFile(final String theText){
		Path[] myPaths = handleFiles(theText, saveFile);
		return myPaths == null || myPaths.length < 1 ? null : myPaths[0];
	}
	
	public Path selectFolder(final String theText){
		Path[] myPaths = handleFiles(theText, selectFolder);
		return myPaths == null || myPaths.length < 1 ? null : myPaths[0];
	}

	public void resetPath() {
		_myCurrentDirectory = Paths.get(".");
	}
	
//	public static void main(String[] args) {
//		CCFileChooser myChooser = new CCFileChooser();
//		CCLog.info(myChooser.openFiles("open files"));
//		CCLog.info(myChooser.openFile("open file"));
//		CCLog.info(myChooser.saveFile("save file"));
//		CCLog.info(myChooser.selectFolder("select Folderbla"));
//	}
}
