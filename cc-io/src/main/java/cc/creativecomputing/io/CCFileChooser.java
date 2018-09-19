package cc.creativecomputing.io;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.regex.Pattern;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.util.tinyfd.TinyFileDialogs.*;

public class CCFileChooser {
	
	private String[] _myExtensions = new String[0];
	
	public CCFileChooser(String...theExtensions){
		_myCurrentDirectory = Paths.get(".");
		_myExtensions = theExtensions;
	}

	private Path _myCurrentDirectory;
	
	public String[] extensions(){
		return _myExtensions;
	}
	
	public void setDirectory(Path thePath){
		_myCurrentDirectory = thePath;
	}
	
	public static interface CCFileHandler{
		public String handleFile(String theTitle, String thePath, PointerBuffer thePattern, String theFilterDesc);
	}
	
	public static CCFileHandler OPEN_FILES = (theTitle, thePath, thePattern, theFilterDesc) -> {return tinyfd_openFileDialog(theTitle, thePath, thePattern, theFilterDesc, true);};
	
	public static CCFileHandler OPEN_FILE = (theTitle, thePath, thePattern, theFilterDesc) -> {return tinyfd_openFileDialog(theTitle, thePath, thePattern, theFilterDesc, false);};
	
	public static CCFileHandler SAVE_FILE = (theTitle, thePath, thePattern, theFilterDesc) -> {return tinyfd_saveFileDialog(theTitle, thePath, thePattern, theFilterDesc);};
	
	public static CCFileHandler SELECT_FOLDER = (theTitle, thePath, thePattern, theFilterDesc) -> {return tinyfd_selectFolderDialog(theTitle, thePath);};
	
	public Optional<Path[]> handleFiles(final String theText, CCFileHandler theHandler) {
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
            	return Optional.empty();
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
            return Optional.of(myResult);
		}catch(Exception e) {
			e.printStackTrace();
			return Optional.empty();
		}
	}
	
	public Optional<Path[]> openFiles(final String theText) {
		return handleFiles(theText, OPEN_FILES);
	}
	
	public Optional<Path> openFile(final String theText){
		Optional<Path[]> myPaths = handleFiles(theText, OPEN_FILE);
		return !myPaths.isPresent() || myPaths.get().length < 1 ? Optional.empty() : Optional.of(myPaths.get()[0]);
	}
	
	public Optional<Path> saveFile(final String theText){
		Optional<Path[]> myPaths = handleFiles(theText, SAVE_FILE);
		return !myPaths.isPresent() || myPaths.get().length < 1 ? Optional.empty() : Optional.of(myPaths.get()[0]);
	}
	
	public Optional<Path> selectFolder(final String theText){
		Optional<Path[]> myPaths = handleFiles(theText, SELECT_FOLDER);
		return !myPaths.isPresent() || myPaths.get().length < 1 ? Optional.empty() : Optional.of(myPaths.get()[0]);
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
