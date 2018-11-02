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
package cc.creativecomputing.io;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

import cc.creativecomputing.core.CCSystem;
import cc.creativecomputing.core.CCSystem.CCOS;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.core.util.CCStringUtil;
import cc.creativecomputing.io.data.CCDataException;
import cc.creativecomputing.io.data.CCDataObject;

/**
 * @author christianriekoff
 *
 */
public class CCNIOUtil {
	private static List<String> optionalAssetPaths = new ArrayList<>();
	
	/** Path to sketch folder */
	static public Path applicationPath = Paths.get(System.getProperty("user.dir"));
	
	public static void addAssetPath(String thePath){
		optionalAssetPaths.add(thePath);
	}
	
	public static void addAssetPaths(List<String> thePaths){
		optionalAssetPaths.addAll(thePaths);
	}
	
	/**
	 * Return a full path to an item in the data folder as a Path object. 
	 * See the {@linkplain #dataPath(String)} method for more information.
	 * @param thePath source path for query
	 * @return full path to an item in the data folder as a File object
	 */
	static public Path dataPath(String thePath) {
		// isAbsolute() could throw an access exception, but so will writing
		// to the local disk using the sketch path, so this is safe here.
		Path myPath = Paths.get(thePath);
		if (myPath.isAbsolute())
			return myPath;

		String myJarPath = CCNIOUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
		if (myJarPath.contains("Contents/Resources/Java/")) {
			File containingFolder = new File(myJarPath).getParentFile();
			File dataFolder = new File(containingFolder, "data");
			return Paths.get(dataFolder.getPath(), thePath);
		}

		for(String myAssetPath:optionalAssetPaths){
			myPath = Paths.get(myAssetPath,thePath);
			if(Files.exists(myPath))return myPath;
		}
		
		// Windows, Linux, or when not using a Mac OS X .app file
		return Paths.get("data", thePath);
	}
	
	/**
	 * Prepend the application folder path to the path that is passed in. 
	 * @param thePath
	 * @return
	 */
	static public Path appPath(String thePath){
		if (applicationPath == null){
			throw new RuntimeException("The applet was not inited properly, " + "or security restrictions prevented " + "it from determining its path.");
		}
		
		Path myResult = Paths.get(thePath);
		if(exists(myResult))return myResult;

		return Paths.get(thePath);
	}
	
	/**
	 * Returns the path to a resource based on the given class, this looks for files in the package
	 * of the given class and allows to place files that are needed to work with the code for example
	 * shaders to placed with the source code. This is still experimental and need to be checked if things
	 * are put together in jar for example.
	 * @param theClass class to look for a resource
	 * @param thePath path inside the class folder
	 * @return path based on the given class
	 */
	static public Path classPath(Class<?> theClass, String thePath) {
		
//		URL myResult = theClass.getResource(thePath);
		URL myResult = theClass.getResource("");
		if(myResult == null) {
			throw new CCIOException("The given Resource is not available:" + theClass.getResource("") + thePath);
		}
		String myPath = myResult.getPath();
		myPath = myPath.replaceAll("file:", "");
		myPath = myPath + "/" + thePath;
		if(CCSystem.os == CCOS.WINDOWS)if(myPath.startsWith("/"))myPath = myPath.substring(1);
		
		if(myPath.contains(".jar!")){
			Path myJarPath = Paths.get(myPath.substring(0, myPath.indexOf(".jar!")+4));
			String myFilePart = myPath.substring(myPath.indexOf(".jar!")+5);
			
			try {
				Path path = File.createTempFile(myFilePart, "tmp").toPath();
				Files.copy(theClass.getResourceAsStream(thePath), path, StandardCopyOption.REPLACE_EXISTING);
				return path;
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
		}

		myPath = myPath.replaceAll("%20", " ");
		return Paths.get(myPath);

	}
	
	/**
	 * Shortcut to {@linkplain #classPath(Class, String)} by taking the class from the object.
	 * @param theObject object to take the class from to look for a resource
	 * @param thePath path inside the class folder
	 * @return path based on the class of the given object
	 */
	static public Path classPath(Object theObject, String thePath) {
		return classPath(theObject.getClass(), thePath);
	}
	
	static public void createDirectories(Path thePath, FileAttribute<?>...theAttributes){
		if(thePath == null)return;
		
		if(fileExtension(thePath) != null){
			thePath = thePath.getParent();
			if(thePath == null)return;
		}
		
		try {
			Files.createDirectories(thePath, theAttributes);
		} catch(FileAlreadyExistsException e){
			
		}catch (IOException e) {
		
			throw new RuntimeException(e);
		}
	}
	
	public static BufferedWriter createWriter(Path thePath, FileAttribute<?>...theAttributes){
		try{
			createDirectories(thePath, theAttributes);
			return Files.newBufferedWriter(thePath);
		}catch (IOException e){
			throw new RuntimeException(e);
		}
	}

	static public String loadString(Path thePath){
		try{
			
	        return new String(Files.readAllBytes(thePath));
		}catch (IOException e){
			throw new RuntimeException("Error inside loadStrings()", e);
		}
	}
	
	static public String loadString(URL theDocumentURL, String theUser, String theKey) {
		try {

			URLConnection myUrlConnection = theDocumentURL.openConnection();

			if (theUser != null && theKey != null) {
				String userpass = theUser + ":" + theKey;
					String basicAuth = "Basic " + CCStringUtil.printBase64Binary(userpass.getBytes());

					myUrlConnection.setRequestProperty("Authorization", basicAuth);
				}
				BufferedReader reader = new BufferedReader(new InputStreamReader(myUrlConnection.getInputStream()));
				
				StringBuffer myResult = new StringBuffer();
				String line = null;
				while ((line = reader.readLine()) != null){
					CCLog.info(line);
					myResult.append(line);
				}
				reader.close();

				return myResult.toString();
			} catch (IOException e) {
				throw new RuntimeException("Error inside loadStrings()", e);
			}
	}
	
	static public List<String> loadStrings(Path thePath){
		try{
			BufferedReader reader = Files.newBufferedReader(thePath);
			List<String> myResult = new ArrayList<>();
			String line = null;
			while ((line = reader.readLine()) != null){
				myResult.add(line);
			}
			reader.close();

			return myResult;

		}catch (IOException e){
			throw new RuntimeException("Error inside loadStrings()", e);
		}
	}
	
	static public void saveString(Path thePath, String theString, OpenOption...theOptions){
		try{
			createDirectories(thePath.getParent());
			Files.write(thePath, theString.getBytes(), theOptions);
		}catch (IOException e){
			throw new RuntimeException("", e);
		}
	}
	
	static public ByteBuffer loadBytes(Path thePath){
//		CCFileInputChannel myFileChannel = new CCFileInputChannel(thePath);
//		return myFileChannel.read();
		ByteBuffer buffer = null;
		try{
	        if (Files.isReadable(thePath)) {
	            try (SeekableByteChannel fc = Files.newByteChannel(thePath)) {
	                buffer = ByteBuffer.allocateDirect((int)fc.size() + 1).order(ByteOrder.nativeOrder());
	                while (fc.read(buffer) != -1) {
	                    ;
	                }
	            }
	        } 
	        buffer.flip();
	        return buffer.slice();
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	
	static public void saveBytes(Path thePath, ByteBuffer theBuffer){
		CCFileOutputChannel myFileChannel = new CCFileOutputChannel(thePath);
		myFileChannel.write(theBuffer);
		myFileChannel.close();
	}
	
	static public boolean exists(Path thePath, LinkOption...theLinkOptions){
		return Files.exists(thePath, theLinkOptions);
	}
	
	static public void delete(Path thePath){
		try{
			Files.walkFileTree(thePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.delete(dir);
					return FileVisitResult.CONTINUE;
				}

			});
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	static public void deleteIfExists(Path thePath){
		try{
			Files.walkFileTree(thePath, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					Files.deleteIfExists(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
					Files.deleteIfExists(dir);
					return FileVisitResult.CONTINUE;
				}

			});
		}catch(Exception e){
			throw new RuntimeException(e);
		}
	}
	
	/**
    * Returns a file's last modified time.
    *
    * <p> The {@code options} array may be used to indicate how symbolic links
    * are handled for the case that the file is a symbolic link. By default,
    * symbolic links are followed and the file attribute of the final target
    * of the link is read. If the option {@link LinkOption#NOFOLLOW_LINKS
    * NOFOLLOW_LINKS} is present then symbolic links are not followed.
    *
    * @param   thePath
    *          the path to the file
    * @param   theOptions
    *          options indicating how symbolic links are handled
    *
    * @return  the millis representing the time the file was last
    *          modified, or an implementation specific default when a time
    *          stamp to indicate the time of last modification is not supported
    *          by the file system
    *
    * @see BasicFileAttributes#lastModifiedTime
    */

	public static long lastModified(Path thePath, LinkOption...theOptions){
		try {
			return Files.getLastModifiedTime(thePath, theOptions).toMillis();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Gets the extension of the given file.
	 * @param thePath path of the file to check the extension
	 * @return the extension of the file
	 */
	public static String fileExtension(final Path thePath) {
		if(thePath == null)return null;
		if(thePath.getFileName() == null)return null;
		String myPathString = thePath.getFileName().toString();
		if(myPathString.lastIndexOf(".") < 0)return null;
		return myPathString.substring(myPathString.lastIndexOf(".") + 1 , myPathString.length());
	}
	
	public static Path addExtension(Path thePath, String theExtension){
		return thePath.resolveSibling(thePath.getFileName() + "." + theExtension);
	}
	
	/**
	 * Gets the name of the given file without an extension.
	 * @param theFile path to get the name
	 * @return name of the given file without an extension.
	 */
	public static String fileName(final String theFile){
		int myIndex = theFile.lastIndexOf('.');
		final int mySeperator = theFile.lastIndexOf(FileSystems.getDefault().getSeparator());
		if(myIndex < mySeperator)myIndex = -1;
		if(myIndex < 0)return theFile.substring(Math.max(0, mySeperator));
		return theFile.substring(Math.max(0, mySeperator),myIndex);
	}
	
	public static String fileName(final Path thePath){
		return fileName(thePath.getFileName().toString());
	}
	
	public static Path filePath(final Path thePath){
		return Paths.get(fileName(thePath));
	}
	
	static public List<Path> list(final Path theFolder, boolean theRecursive){
		return listImplementation(theFolder, entry -> {return true;}, theRecursive);
	}
	
	/**
	 * Returns an array of files in the given folder
	 * @param theFolder
	 * @return array of files in the given folder
	 */
	static public List<Path> list(final Path theFolder){
		return listImplementation(theFolder, entry -> {return true;}, false);
	}
	
	/**
	 * Returns an array of files in the given folder
	 * @param theFolder
	 * @return array of files in the given folder
	 */
	static public List<Path> listFolder(final Path theFolder, boolean theRecursive){
		return listImplementation(theFolder, entry -> {return entry.toFile().isDirectory();}, theRecursive);
	}
	
	static public List<Path> listFolder(final Path theFolder){
		return listFolder(theFolder, false);
	}
	
	static private class FileExtensionFilter implements DirectoryStream.Filter<Path> {
		private final String[] _myExtensions;
		private final boolean _myRecursive;
		private FileExtensionFilter(final boolean theRecursive, final String ... theExtensions){
			_myExtensions = new String[theExtensions.length];
			
			for(int i = 0; i < _myExtensions.length;i++){
				_myExtensions[i] = "." + theExtensions[i];
			}
			
			_myRecursive = theRecursive;
		}
		
		public boolean accept(final Path thePath) {
			if(_myRecursive && Files.isDirectory(thePath))return true;
			for(String myExtension:_myExtensions){
				if(thePath.getFileName().toString().toLowerCase().endsWith(myExtension))return true;
			}
			return false;
		}
	}
	
	static public List<Path> list(final Path theFolder, final String...theExtensions){
		return listImplementation(theFolder, new FileExtensionFilter(false, theExtensions), false);
	}
	
	static public List<Path> list(final Path theFolder, boolean theRecursive, final String...theExtensions){
		return listImplementation(theFolder, new FileExtensionFilter(true, theExtensions), theRecursive);
	}
	
	/**
	 * Returns an array of files in the given folder
	 * 
	 * @param theFolder
	 * @param theExtension
	 * @return
	 */
	static public List<Path> list(final Path theFolder, final DirectoryStream.Filter<Path> theFilter){
		return listImplementation(theFolder, theFilter, false);
	}

	static public List<Path> listImplementation(Path theFolder, final DirectoryStream.Filter<Path> theFilter, boolean theIsRecursive) {
		if (!exists(theFolder)) {
			theFolder = dataPath(theFolder.toString());
		}

		if (!exists(theFolder)) {
			throw new CCIOException("The given folder: " + theFolder + " does not exist.");
		}

		if (!Files.isDirectory(theFolder)) {
			throw new CCIOException("The given path: " + theFolder + " is not a folder.");
		}

		List<Path> myResult = new ArrayList<>();
		try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(theFolder, theFilter)) {
			for (Path path : directoryStream) {
				if(!Files.isDirectory(path))myResult.add(path);
				if(theIsRecursive && Files.isDirectory(path))myResult.addAll(listImplementation(path, theFilter, theIsRecursive));
			}
		} catch (IOException ex) {
		}
		return myResult;

	}
	
	// ////////////////////////////////////////////////////////////
	// FILE/FOLDER SELECTION

	private static Path selectedPath;

	static {
		System.setProperty("apple.awt.fileDialogForDirectories", "true");
	}

	/**
	 * Open a platform-specific file chooser dialog to select a file for input.
	 * 
	 * @return full path to the selected file, or null if no selection.
	 * 
	 * @see #selectOutput()
	 * @see #selectFolder()
	 */
	static public Path selectInput() {
		return selectInput("Select a file...");
	}

	/**
	 * Opens a platform-specific file chooser dialog to select a file for input.
	 * This function returns the full path to the selected file as a
	 * <b>String</b>, or <b>null</b> if no selection.
	 * 
	 * @param theMessage message you want the user to see in the file chooser
	 * @return full path to the selected file, or null if canceled.
	 * 
	 * @see #selectOutput(String)
	 * @see #selectFolder(String)
	 */
	static public Path selectInput(String theMessage) {
		return selectInput(theMessage, null);
	}

	static public Path selectInput(String theMessage, Path theFolder,String... theExtensions) {
		CCFileChooser fileChooser = new CCFileChooser();
		if(theExtensions != null && theExtensions.length > 0){
			fileChooser.setAcceptAllFileFilterUsed(false);
			for(String myExtension:theExtensions){
				fileChooser.addChoosableFileFilter(new CCFileFilter(myExtension));
			}
		}
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		
		if (theFolder != null) {
			fileChooser.setCurrentDirectory(theFolder.toFile());
		}
		
		if (theFolder != null)
			fileChooser.setCurrentDirectory(theFolder.toFile());
		else if(selectedPath != null){

			Path myChosenPath = selectedPath;
			if(!CCNIOUtil.exists(selectedPath)){
				myChosenPath = selectedPath.getParent();
			}
			fileChooser.setDirectory(myChosenPath);
		}

		selectedPath = fileChooser.chosePath(theMessage);

		fileChooser.setFileFilter(null);
		return selectedPath;
	}

	/**
	 * Opens a platform-specific file chooser dialog to select a file for input.
	 * This function returns the full path to the selected file as a
	 * <b>String</b>, or <b>null</b> if no selection. Files are filtered
	 * according to the given file extensions.
	 * 
	 * @param theExtensions file extensions for filtering
	 * @return full path to the selected file, or null if canceled.
	 * 
	 * @see #selectOutput(String)
	 * @see #selectFolder(String)
	 */
	static public Path selectFilteredInput(String... theExtensions) {
		return selectInput("Select a file...", null, theExtensions);
	}

	/**
	 * Open a platform-specific file save dialog to select a file for output.
	 * 
	 * @return full path to the file entered, or null if canceled.
	 */
	static public Path selectOutput() {
		return selectOutput("Save as...");
	}

	/**
	 * Open a platform-specific file save dialog to create of select a file for
	 * output. This function returns the full path to the selected file as a
	 * <b>String</b>, or <b>null</b> if no selection. If you select an existing
	 * file, that file will be replaced. Alternatively, you can navigate to a
	 * folder and create a new file to write to.
	 * 
	 * @param theMessage message you want the user to see in the file chooser
	 * @return full path to the file entered, or null if canceled.
	 * 
	 * @webref input:files
	 * @see #selectInput(String)
	 * @see #selectFolder(String)
	 */
	static public Path selectOutput(String theMessage) {
		return selectOutput(theMessage, null);
	}

	static public Path selectOutput(String theMessage, final String theFolder, String ... theExtensions) {
		CCFileChooser fileChooser = new CCFileChooser();
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		if (theFolder != null)
			fileChooser.setCurrentDirectory(new File(theFolder));
		else if(selectedPath != null){

			Path myChosenPath = selectedPath;
			if(!CCNIOUtil.exists(selectedPath)){
				myChosenPath = selectedPath.getParent();
			}
			fileChooser.setDirectory(myChosenPath);
		}
	
		selectedPath = fileChooser.chosePath(theMessage);
		return selectedPath;
	}

	static public Path selectFolder() {
		return selectFolder("Select a folder...");
	}

	/**
	 * Opens a platform-specific file chooser dialog to select a folder for
	 * input. This function returns the full path to the selected folder as a
	 * <b>String</b>, or <b>null</b> if no selection.
	 * 
	 * @param theMessage message you want the user to see in the file chooser
	 * @return full path to the selected folder, or null if no selection.
	 * 
	 * @see #selectOutput(CCApp, String)
	 * @see #selectFolder(CCApp, String)
	 */
	static public Path selectFolder(final String theMessage) {
		return selectFolder(theMessage, null);
	}

	static public Path selectFolder(final String theMessage, String theFolder) {
		CCFileChooser fileChooser = new CCFileChooser();
		fileChooser.setDialogTitle(theMessage);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (theFolder != null)
			fileChooser.setCurrentDirectory(new File(theFolder));

		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		
		selectedPath = fileChooser.chosePath(theMessage);
		return selectedPath;
	}

	
	public static void main(String[] args) {
//		selectOutput("yo", null, "bin");
		CCLog.info(dataPath("YP"));
	}
}
