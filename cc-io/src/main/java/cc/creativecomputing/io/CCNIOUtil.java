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
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.file.attribute.BasicFileAttributes;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.FileAttribute;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFileChooser;

/**
 * @author christianriekoff
 *
 */
public class CCNIOUtil {
	private static List<String> optionalAssetPaths = new ArrayList<>();
	
	/** Path to sketch folder */
	static public String applicationPath = System.getProperty("user.dir");
	
	public static void addAssetPath(String thePath){
		optionalAssetPaths.add(thePath);
	}
	
	public static void addAssetPaths(List<String> thePaths){
		optionalAssetPaths.addAll(thePaths);
	}
	
	static public Path applicationPath(String thePath){
		return Paths.get(applicationPath, thePath);
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
		return Paths.get(applicationPath, "data", thePath);
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

		return Paths.get(applicationPath).resolve(thePath);
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
		URL myResult = theClass.getResource(thePath);
		if(myResult == null) {
			throw new CCIOException("The given Resource is not available:" + theClass.getResource("") + thePath);
		}
		String myPath = myResult.getPath().replaceAll("%20", " ");
//		if(myPath.startsWith("/"))myPath = myPath.substring(1);
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
		} catch (IOException e) {
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
		CCFileInputChannel myFileChannel = new CCFileInputChannel(thePath);
		return myFileChannel.read();
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
	 * Gets the extension of the given file.
	 * @param thePath path of the file to check the extension
	 * @return the extension of the file
	 */
	public static String fileExtension(final Path thePath) {
		String myPathString = thePath.getFileName().toString();
		if(myPathString.lastIndexOf(".") < 0)return null;
		return myPathString.substring(myPathString.lastIndexOf(".") + 1 , myPathString.length());
	}
	
	/**
	 * Gets the name of the given file without an extension.
	 * @param theFile path to get the name
	 * @return name of the given file without an extension.
	 */
	public static String fileName(final String theFile){
		final int myIndex = theFile.lastIndexOf('.');
		final int mySeperator = theFile.lastIndexOf(FileSystems.getDefault().getSeparator());
		return theFile.substring(Math.max(0, mySeperator),myIndex);
	}
	
	public static String fileName(final Path thePath){
		return fileName(thePath.toString());
	}
	
	public static Path filePath(final Path thePath){
		return Paths.get(fileName(thePath));
	}
	
	/**
	 * Returns an array of files in the given folder
	 * @param theFolder
	 * @return array of files in the given folder
	 */
	static public List<Path> list(final Path theFolder){
		return listImplementation(theFolder, null);
	}
	
	static private class FileExtensionFilter implements DirectoryStream.Filter<Path> {
		private final String[] _myExtensions;
		
		private FileExtensionFilter(final String ... theExtensions){
			_myExtensions = new String[theExtensions.length];
			
			for(int i = 0; i < _myExtensions.length;i++){
				_myExtensions[i] = "." + theExtensions[i];
			}
		}
		
		public boolean accept(final Path thePath) {
			for(String myExtension:_myExtensions){
				if(thePath.getFileName().toString().toLowerCase().endsWith(myExtension))return true;
			}
			return false;
		}
	}
	
	static public List<Path> list(final Path theFolder, final String...theExtensions){
		return listImplementation(theFolder, new FileExtensionFilter(theExtensions));
	}
	
	/**
	 * Returns an array of files in the given folder
	 * 
	 * @param theFolder
	 * @param theExtension
	 * @return
	 */
	static public List<Path> list(final Path theFolder, final DirectoryStream.Filter<Path> theFilter){
		return listImplementation(theFolder, theFilter);
	}

	static public List<Path> listImplementation(Path theFolder, final DirectoryStream.Filter<Path> theFilter) {

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
				myResult.add(path);
			}
		} catch (IOException ex) {
		}
		return myResult;

	}
	
	// ////////////////////////////////////////////////////////////
	// FILE/FOLDER SELECTION

	private static CCFileChooser fileChooser = new CCFileChooser();
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
	 * @param prompt message you want the user to see in the file chooser
	 * @return full path to the selected file, or null if canceled.
	 * 
	 * @see #selectOutput(String)
	 * @see #selectFolder(String)
	 */
	static public Path selectInput(String prompt) {
		return selectInput(prompt, null);
	}

	static public Path selectInput(String prompt, Path theFolder) {
		fileChooser.setFileFilter(new CCFileFilter(""));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		
		if (theFolder != null) {
			fileChooser.setCurrentDirectory(theFolder.toFile());
		}

		selectedPath = fileChooser.chosePath(prompt);
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
		fileChooser.setFileFilter(new CCFileFilter("", theExtensions));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);

		selectedPath = fileChooser.chosePath("Select a file...");
		return selectedPath;
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

	static public Path selectOutput(String theMessage, final String theFolder) {
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		if (theFolder != null)
			fileChooser.setCurrentDirectory(new File(theFolder));

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
		fileChooser.setDialogTitle(theMessage);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if (theFolder != null)
			fileChooser.setCurrentDirectory(new File(theFolder));

		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		
		selectedPath = fileChooser.chosePath(theMessage);
		return selectedPath;
	}
}
