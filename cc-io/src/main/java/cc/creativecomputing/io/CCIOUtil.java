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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.jar.JarFile;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.ZipInputStream;

import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;


@Deprecated
public class CCIOUtil{
	
	/** Path to sketch folder */
	static public String applicationPath = System.getProperty("user.dir");
	
	/**
	 * Gets the extension of the given file, you can pass the file as String or as a java File object.
	 * @param theFile
	 * @return the extension of the file
	 */
	public static String fileExtension(final File theFile) {
		return fileExtension(theFile.getName());
	}

	public static String fileExtension(final String theFileName) {
		int i = theFileName.lastIndexOf('.');
		
		if(i < 0)
			return null;
		
		if(i >= theFileName.length() - 1)
			return null;
		
		
		return theFileName.substring(i + 1).toLowerCase();
	}
	
	/**
	 * Gets the name of the given file without an extension. You can pass the file as a a String or a
	 * java file.
	 * @param theFile
	 * @return
	 */
	public static String fileName(final String theFile){
		final int myIndex = theFile.lastIndexOf('.');
		final int mySeperator = theFile.lastIndexOf(File.separatorChar);
		return theFile.substring(Math.max(0, mySeperator),myIndex);
	}
	
	public static String fileName(final File theFile){
		return fileName(theFile.getName());
	}
	
	public static boolean exists(final String theFile){
		return new File(dataPath(theFile)).exists();
	}
	
	/**
	 * Checks if the given file A is newer than the given file B
	 * @param theA file A can be a java File or String defining its path
	 * @param theB file B can be a java File or String defining its path
	 * @return <code>true</code> if A is newer than B otherwise <code>false</code>
	 */
	public static boolean isNewer(final String theA, final String theB){
		return isNewer(new File(theA), new File(theB));
	}
	
	public static boolean isNewer(final File theA, final File theB){
		if(theA.exists() && !theB.exists())return true;
		return theA.lastModified() > theB.lastModified();
	}

	static public InputStream openStream(final String theFileName){
		InputStream myResult = createStream(theFileName);
		
		if(myResult == null)throw new CCIOException("Could not open the given file:" + theFileName);
		String myExtension = fileExtension(theFileName);
		try {
			if(myExtension.equals("zip")){
				return new ZipInputStream(myResult);
			}
			if(myExtension.equals("gz"))
				return new GZIPInputStream(myResult);
				
			return myResult;
		} catch (Exception e) {
			throw new RuntimeException("Could not open the given file:" + theFileName,e);
		}
	}

	/**
	 * Creates a BufferedReader from a given path.
	 * @param theFilename
	 * @return
	 */
	static public BufferedReader createReader(final String theFilename){
		try{
			InputStream is = openStream(theFilename);
			if (is == null){
				throw new CCIOException(theFilename + " does not exist or could not be read");
			}
			return createReader(is);

		}catch (Exception e){
			if (theFilename == null){
				throw new CCIOException("Filename passed to reader() was null", e);
			}else{
				throw new CCIOException("Couldn't create a reader for " + theFilename, e);
			}
		}
	}

	/**
	 * Creates a BufferedReader from a file.
	 * @param theFile
	 * @return
	 */
	static public BufferedReader createReader(final File theFile){
		try{
			return createReader(new FileInputStream(theFile));

		}catch (Exception e){
			if (theFile == null){
				throw new CCIOException("File passed to reader() was null");
			}else{
				e.printStackTrace();
				throw new CCIOException("Couldn't create a reader for " + theFile.getAbsolutePath());
			}
		}
		//return null;
	}

	/**
	 * Creates a BufferedReader from an inputstream
	 * @param theInputStream
	 * @return
	 */
	static public BufferedReader createReader(final InputStream theInputStream){
		return new BufferedReader(new InputStreamReader(theInputStream));
	}
	
	/**
	 * Every implementation of the Java platform is required to support the following standard charsets.
	 * @author christianriekoff
	 *
	 */
	public enum CCCharSet{
		/**
		 * Seven-bit ASCII, a.k.a. ISO646-US, a.k.a. the Basic Latin block of the Unicode character set
		 */
		US_ASCII("US-ASCII"),
		
		/**
		 * ISO Latin Alphabet No. 1, a.k.a. ISO-LATIN-1
		 */
		ISO_8859_1("ISO-8859-1"),
		
		/**
		 * Eight-bit UCS Transformation Format
		 */
		UTF_8("UTF-8"),
		
		/**
		 * Sixteen-bit UCS Transformation Format, big-endian byte order
		 */
		UTF_16BE("UTF-16BE"),
		
		/**
		 * Sixteen-bit UCS Transformation Format, little-endian byte order
		 */
		UTF_16LE("UTF-16LE"),
		
		/**
		 * Sixteen-bit UCS Transformation Format, byte order identified by an optional byte-order mark
		 */
		UTF_16("UTF-16");
		
		String _myValue;
		
		CCCharSet(final String theValue) {
			_myValue = theValue;
		}
	}
	
	/**
	 * I want to read lines from a stream. If I have to type the
	 * following lines any more I'm gonna send Sun my medical bills.
	 */
	static public BufferedReader createReader(final InputStream theInputStream, final CCCharSet theCharSet){
		InputStreamReader myInputStreamReader;
		try {
			myInputStreamReader = new InputStreamReader(theInputStream, theCharSet._myValue);
			return new BufferedReader(myInputStreamReader);
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException();
		}
	}

	/**
	 * Creates a Scanner from the given filename
	 * @param theFilename
	 * @return
	 */
	static public Scanner createScanner(final String theFilename){
		try{
			InputStream is = openStream(theFilename);
			if (is == null){
				throw new CCIOException(theFilename + " does not exist or could not be read");
			}
			return createScanner(is);

		}catch (Exception e){
			if (theFilename == null){
				throw new CCIOException("Filename passed to reader() was null", e);
			}else{
				throw new CCIOException("Couldn't create a reader for " + theFilename, e);
			}
		}
	}

	/**
	 * Creates a scanner object for the given file.
	 * @param theFile
	 * @return
	 */
	static public Scanner createScanner(final File theFile){
		try{
			return createScanner(new FileInputStream(theFile));

		}catch (Exception e){
			if (theFile == null){
				throw new CCIOException("File passed to reader() was null");
			}else{
				e.printStackTrace();
				throw new CCIOException("Couldn't create a scanner for " + theFile.getAbsolutePath());
			}
		}
		//return null;
	}

	/**
	 * Creates a scanner from the given inputstream
	 * @param theInputStream
	 * @return
	 */
	static public Scanner createScanner(final InputStream theInputStream){
		return new Scanner(theInputStream);
	}

	/**
	 * decode a gzip input stream
	 */
	static public InputStream gzipInput(InputStream input){
		try{
			return new GZIPInputStream(input);
		}catch (IOException e){
			e.printStackTrace();
			throw new RuntimeException("Problem with gzip input");
		}
		//return null;
	}

	/**
	 * decode a bzip input stream
	 */
	static public InputStream zipInput(InputStream input){
		return new ZipInputStream(input);
	}

	/**
	 * decode a gzip output stream
	 */
	static public OutputStream gzipOutput(OutputStream output){
		try{
			return new GZIPOutputStream(output);
		}catch (IOException e){
			e.printStackTrace();
			throw new RuntimeException("Problem with gzip output");
		}
		//return null;
	}

	/**
	 * Creates a new file in the application folder, and a PrintWriter object to write to it. 
	 * For the file to be made correctly, it should be flushed and must be closed with 
	 * its flush() and close() methods. All files saved use UTF-8 encoding.
	 * @param theFilename Name of the file to be created
	 * @return
	 */
	static public PrintWriter createWriter(String theFilename){
		try{
			return createWriter(new FileOutputStream(savePath(theFilename)));

		}catch (Exception e){
			if (theFilename == null){
				throw new RuntimeException("Filename passed to writer() was null", e);
			}else{
				throw new RuntimeException("Couldn't create a writer for " + theFilename, e);
			}
		}
	}

	/**
	 * I want to print lines to a file. I have RSI from typing these
	 * eight lines of code so many times.
	 */
	static public PrintWriter createWriter(File file){
		try{
			return createWriter(new FileOutputStream(file));

		}catch (Exception e){
			if (file == null){
				throw new RuntimeException("File passed to writer() was null");
			}else{
				e.printStackTrace();
				throw new RuntimeException("Couldn't create a writer for " + file.getAbsolutePath());
			}
		}
		//return null;
	}

	/**
	 * I want to print lines to a file. Why am I always explaining myself?
	 * It's the JavaSoft API engineers who need to explain themselves.
	 */
	static public PrintWriter createWriter(OutputStream output){
		OutputStreamWriter osw = new OutputStreamWriter(output);
		return new PrintWriter(osw);
	}

	static public InputStream openStream(File file){
		try{
			return new FileInputStream(file);

		}catch (IOException e){
			if (file == null){
				throw new RuntimeException("File passed to openStream() was null");

			}else{
				e.printStackTrace();
				throw new RuntimeException("Couldn't openStream() for " + file.getAbsolutePath());
			}
		}
	}

	/**
	 * Simplified method to open a Java InputStream.
	 * <p>
	 * This method is useful if you want to easily open things from the data folder or from a URL, but want an
	 * InputStream object so that you can use other Java methods to take more control of how the stream is read.
	 * </p>
	 * <p>
	 * If the requested item doesn't exist, null is returned. This will also check to see if the user is asking for a
	 * file whose name isn't properly capitalized. It is strongly recommended that libraries use this method to open
	 * data files, so that the loading sequence is handled in the same way.
	 * </p>
	 * 
	 * @param theFilename The filename passed in can be:
	 *        <ul>
	 *        <li>An URL, for instance openStream("http://creativecomputing.cc/");</li>
	 *        <li>A file in the application's data folder</li>
	 *        <li>Another file to be opened locally</li>
	 *        </ul>
	 */
	static public InputStream createStream(String theFilename){
		InputStream stream = null;
		
		// check if the filename makes sense
		if (theFilename == null || theFilename.length() == 0)
			return null;

		// safe to check for this as a url first. this will prevent online
		try{
			URL urlObject = new URL(theFilename);
			URLConnection con = urlObject.openConnection();
			// try to be a browser some sources do not like bots
			con.setRequestProperty( "User-Agent", "Mozilla/4.0 (compatible; MSIE 5.5; Windows NT 5.0; H010818)" );
			return con.getInputStream();
		}catch (MalformedURLException mfue){
			// not a url, that's fine
		}catch (FileNotFoundException fnfe){
			// Java 1.5 likes to throw this when URL not available.

		}catch (IOException e){
			e.printStackTrace();
			return null;
		}
		
		// load resource from jar using the path with getResourceAsStream
		if(theFilename.contains(".jar!")) {
			String[] myParts = theFilename.split("!");
			String myJarPath = myParts[0];
			if(myJarPath.startsWith("file:")) {
				myJarPath = myJarPath.substring(5);
			}
			
			String myFilePath = myParts[1];
			if(myFilePath.startsWith("/")) {
				myFilePath = myFilePath.substring(1);
			}
			try {
				@SuppressWarnings("resource")
				JarFile myJarFile = new JarFile(myJarPath);
		        return myJarFile.getInputStream(myJarFile.getEntry(myFilePath));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		// handle case sensitivity check
		try{
			// first see if it's in a data folder
			File file = dataFile(theFilename);
			if (!file.exists()){
				// next see if it's just in this folder
				file = new File(applicationPath, theFilename);
			}
			if (file.exists()){
				try{
					String filePath = file.getCanonicalPath();
					String filenameActual = new File(filePath).getName();
					// make sure there isn't a subfolder prepended to the name
					String filenameShort = new File(theFilename).getName();
					// if the actual filename is the same, but capitalized
					// differently, warn the user.
					//if (filenameActual.equalsIgnoreCase(filenameShort) &&
					//!filenameActual.equals(filenameShort)) {
					if (!filenameActual.equals(filenameShort)){
						throw new RuntimeException("This file is named " + filenameActual + " not " + theFilename + ". Re-name it " + "or change your code.");
					}
				}catch (IOException e){
				}
			}

			// if this file is ok, may as well just load it
			stream = new FileInputStream(file);
			if (stream != null)
				return stream;

			// have to break these out because a general Exception might
			// catch the RuntimeException being thrown above
		}catch (IOException ioe){
		}catch (SecurityException se){
		}

		try{
			// attempt to load from a local file, used when running as
			// an application, or as a signed applet
			try{ // first try to catch any security exceptions
				try{
					stream = new FileInputStream(dataPath(theFilename));
					if (stream != null)
						return stream;
				}catch (IOException e2){
				}

				try{
					stream = new FileInputStream(appPath(theFilename));
					if (stream != null)
						return stream;
				}catch (Exception e){
				} // ignored

				try{
					stream = new FileInputStream(theFilename);
					if (stream != null)
						return stream;
				}catch (IOException e1){
				}

			}catch (SecurityException se){
			} // online, whups

		}catch (Exception e){
			//die(e.getMessage(), e);
			e.printStackTrace();
		}
		return null;
	}
	
	static public FileOutputStream createOutputStream(final String theFileName) {
		createPath(theFileName);
		try {
			FileOutputStream fos = new FileOutputStream(new File(theFileName));
			return fos;
		} catch (FileNotFoundException e) {
			throw new CCIOException(e);
		}
	}
	
	static public FileInputStream createInputStream(final String theFileName) {
		try {
			FileInputStream fis = new FileInputStream(new File(theFileName));
			return fis;
		} catch (FileNotFoundException e) {
			throw new CCIOException(e);
		}
	}

	static public byte[] loadBytes(String filename){
		InputStream is = openStream(filename);
		if (is != null)
			return loadBytes(is);

		throw new CCIOException("The file \"" + filename + "\" " + "is missing or inaccessible, make sure " + "it's been added to your app and is readable.");
	}

	static public byte[] loadBytes(InputStream input){
		try{
			BufferedInputStream bis = new BufferedInputStream(input);
			ByteArrayOutputStream out = new ByteArrayOutputStream();

			int c = bis.read();
			while (c != -1){
				out.write(c);
				c = bis.read();
			}
			return out.toByteArray();

		}catch (IOException e){
			e.printStackTrace();
			//throw new RuntimeException("Couldn't load bytes from stream");
		}
		return null;
	}

	static public String[] loadStrings(File file){
		InputStream is = openStream(file);
		if (is != null)
			return loadStrings(is);
		return null;
	}

	public static String[] loadStrings(String filename){
		InputStream is = openStream(filename);
		if (is != null)
			return loadStrings(is);

		throw new CCIOException("The file \"" + filename + "\" " + "is missing or inaccessible, make sure " + "it's been added to your app and is readable.");
	}

	static public String[] loadStrings(InputStream input){
		try{
			BufferedReader reader = new BufferedReader(new InputStreamReader(input));

			String lines[] = new String[100];
			int lineCount = 0;
			String line = null;
			while ((line = reader.readLine()) != null){
				if (lineCount == lines.length){
					String temp[] = new String[lineCount << 1];
					System.arraycopy(lines, 0, temp, 0, lineCount);
					lines = temp;
				}
				lines[lineCount++] = line;
			}
			reader.close();

			if (lineCount == lines.length){
				return lines;
			}

			// resize array to appropriate amount for these lines
			String output[] = new String[lineCount];
			System.arraycopy(lines, 0, output, 0, lineCount);
			return output;

		}catch (IOException e){
			e.printStackTrace();
			//throw new RuntimeException("Error inside loadStrings()");
		}
		return null;
	}

	//////////////////////////////////////////////////////////////
	//
	// FILE OUTPUT
	//
	//////////////////////////////////////////////////////////////

	/**
	 * 
	 * Save the contents of a stream to a file in the app folder.
	 * 
	 * @param theFileName
	 * @param theStream
	 */
	public void saveStream(String theFileName, String theStream){
		saveBytes(theFileName, loadBytes(theStream));
	}

	/**
	 * Identical to the other saveStream(), but writes to a File
	 * object, for greater control over the file location.
	 * @param file
	 * @param stream
	 */
	public void saveStream(File file, String stream){
		saveBytes(file, loadBytes(stream));
	}

	/**
	 * Saves bytes to a file to inside the app folder.
	 * The filename can be a relative path, i.e. "poo/bytefun.txt"
	 * would save to a file named "bytefun.txt" to a sub folder
	 * called 'poo' inside the app folder. If the in-between
	 * sub folders don't exist, they'll be created.
	 * @param theFilename name of file to write to
	 * @param theBuffer array of bytes to be written
	 */
	static public void saveBytes(String theFilename, byte[] theBuffer){
		try{
			String location = savePath(theFilename);
			FileOutputStream fos = new FileOutputStream(location);
			saveBytes(fos, theBuffer);
			fos.close();

		}catch (IOException e){
			throw new CCIOException("error saving bytes to " + theFilename);
		}
	}
	
	

	/**
	 * Saves bytes to a specific File location specified by the user.
	 */
	static public void saveBytes(File file, byte buffer[]){
		try{
			String filename = file.getAbsolutePath();
			createPath(filename);
			FileOutputStream fos = new FileOutputStream(file);
			saveBytes(fos, buffer);
			fos.close();

		}catch (IOException e){
			throw new CCIOException("error saving bytes to " + file);
		}
	}

	/**
	 * Spews a buffer of bytes to an OutputStream.
	 */
	static public void saveBytes(OutputStream output, byte buffer[]){
		try{
			//BufferedOutputStream bos = new BufferedOutputStream(output);
			output.write(buffer);
			output.flush();

		}catch (IOException e){
			e.printStackTrace();
			throw new RuntimeException("Couldn't save bytes");
		}
	}

	//

	static public void saveStrings(String filename, String strings[]){
		try{
			String location = savePath(filename);
			FileOutputStream fos = new FileOutputStream(location);
			saveStrings(fos, strings);
			fos.close();

		}catch (IOException e){
			e.printStackTrace();
			throw new RuntimeException("saveStrings() failed: " + e.getMessage());
		}
	}

	static public void saveStrings(File file, String strings[]){
		try{
			String location = file.getAbsolutePath();
			createPath(location);
			FileOutputStream fos = new FileOutputStream(location);
			saveStrings(fos, strings);
			fos.close();

		}catch (IOException e){
			throw new CCIOException("error while saving strings");
		}
	}

	static public void saveStrings(OutputStream output, String strings[]){
		PrintWriter writer = new PrintWriter(new OutputStreamWriter(output));
		for (int i = 0; i < strings.length; i++){
			writer.println(strings[i]);
		}
		writer.flush();
	}
	
	//////////////////////////////////////////////////////////////
	static public void copy(final String theSource, final String thDestination) {
		try {
			copy(new FileInputStream(theSource), new FileOutputStream(thDestination));
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	static public void copy(final File theSource, final File thDestination) {
		try {
			copy(new FileInputStream(theSource), new FileOutputStream(thDestination));
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}

	static public void copy( InputStream fis, OutputStream fos ){
	    try
	    {
	      byte  buffer[] = new byte[0xffff];
	      int   nbytes;

	      while ( (nbytes = fis.read(buffer)) != -1 )
	        fos.write( buffer, 0, nbytes );
	    }
	    catch( IOException e ) {
	    	throw new CCIOException( e );
	    }
	    finally {
	      if ( fis != null )
	        try {
	          fis.close();
	        } catch ( IOException e ) {}

	      try {
	        if ( fos != null )
	          fos.close();
	      } catch ( IOException e ) {}
	    }
	  }
	// ////////////////////////////////////////////////////////////

	/**
	 * Prepend the application folder path to the filename (or path) that is
	 * passed in. External libraries should use this function to save to
	 * the application folder.
	 * <p/>
	 * Note that when running as an applet inside a web browser,
	 * the application path will be set to null, because security restrictions
	 * prevent applets from accessing that information.
	 * <p/>
	 */
	static public String appPath(String thePath){
		if (applicationPath == null){
			throw new RuntimeException("The applet was not inited properly, " + "or security restrictions prevented " + "it from determining its path.");
		}
		
		if(isPathExisting(thePath))return thePath;

		return applicationPath + File.separator + thePath;
	}

	/**
	 * Returns a path inside the application folder to save to,
	 * just like {@linkplain #appPath(String)}, but also creates any in-between
	 * folders so that things save properly.
	 * <p/>
	 * All saveXxxx() functions use the path to the app folder, rather than
	 * its data folder.
	 */
	static public String savePath(String thePath){
		String filename = appPath(thePath);
		createPath(filename);
		return filename;
	}
	
	/**
	 * Checks if the given path is absolute and at least contains existing folders.
	 * @param thePath
	 * @return true if the path is absolute and contains folders that exist
	 */
	static boolean isPathExisting(final String thePath) {
		File myFile = new File(thePath);
		while(!myFile.exists()) {
			String myParent = myFile.getParent();
			if(myParent == null || myParent.equals("/"))return false;
			myFile = new File(myParent);
		}
		return true;
	}

	private static List<String> optionalAssetPaths = new ArrayList<>();
	
	public static void addAssetPath(String thePath){
		optionalAssetPaths.add(thePath);
	}
	
	public static void addAssetPaths(List<String> thePaths){
		optionalAssetPaths.addAll(thePaths);
	}
	
	/**
	 * Return a full path to an item in the data folder.
	 * @param thePath source path for query
	 * @return full path to an item in the data folder
	 */
	static public String dataPath(String thePath){
		return dataFile(thePath).getAbsolutePath();
	}
	
	/**
	 * Return a full path to an item in the data folder as a File object. 
	 * See the {@linkplain #dataPath(String)} method for more information.
	 * @param thePath source path for query
	 * @return full path to an item in the data folder as a File object
	 */
	static public File dataFile(String thePath) {
		// isAbsolute() could throw an access exception, but so will writing
		// to the local disk using the sketch path, so this is safe here.
		File myFile = new File(thePath);
		if (myFile.isAbsolute())
			return myFile;

		String myJarPath = CCIOUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath().replaceAll("%20", " ");
		if (myJarPath.contains("Contents/Resources/Java/")) {
			File containingFolder = new File(myJarPath).getParentFile();
			File dataFolder = new File(containingFolder, "data");
			return new File(dataFolder, thePath);
		}

		for(String myAssetPath:optionalAssetPaths){
			myFile = new File(myAssetPath + File.separator + thePath);
			if(myFile.exists())return myFile;
		}
		
		// Windows, Linux, or when not using a Mac OS X .app file
		return new File(applicationPath + File.separator + "data" + File.separator + thePath);
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
	static public String classPath(Class<?> theClass, String thePath) {
		URL myResult = theClass.getResource(thePath);
		if(myResult == null) {
			throw new CCIOException("The given Resource is not available:" + theClass.getResource("") + thePath);
		}
//		CCLog.info("CLASSPATH:" + myResult.getPath().replaceAll("%20", " "));
		return myResult.getPath().replaceAll("%20", " ");
	}
	
	/**
	 * Shortcut to {@linkplain #classPath(Class, String)} by taking the class from the object.
	 * @param theObject object to take the class from to look for a resource
	 * @param thePath path inside the class folder
	 * @return path based on the class of the given object
	 */
	static public String classPath(Object theObject, String thePath) {
		return classPath(theObject.getClass(), thePath);
	}
	
	static private class FileExtensionFilter implements FilenameFilter {
		private final String[] _myExtensions;
		
		private FileExtensionFilter(final String ... theExtensions){
			_myExtensions = new String[theExtensions.length];
			
			for(int i = 0; i < _myExtensions.length;i++){
				_myExtensions[i] = "." + theExtensions[i];
			}
		}
		
		public boolean accept(final File theFile, final String theFileName) {
			for(String myExtension:_myExtensions){
				if(theFileName.toLowerCase().endsWith(myExtension))return true;
			}
			return false;
		}
	}
	
	static private class CCFileExtensionFilter extends FileFilter {
		private final String[] _myExtensions;
		
		private CCFileExtensionFilter(final String ... theExtensions){
			_myExtensions = new String[theExtensions.length];
			
			for(int i = 0; i < _myExtensions.length;i++){
				_myExtensions[i] = "." + theExtensions[i];
			}
		}
		
		public boolean accept(final File theFile) {
			int myDotIndex = theFile.getName().lastIndexOf(".");
			String myExtension;
			if (myDotIndex > 0) {
				myExtension = theFile.getName().substring(myDotIndex + 1);
			}else {
				return false;
			}
			for(String myFilterExtension:_myExtensions){
				if(myFilterExtension.equals(myExtension))return true;
			}
			return false;
		}

		@Override
		public String getDescription() {
			return null;
		}
	}
	
	static public String[] list(){
		return list("");
	}
	
	/**
	 * Returns an array of files in the given folder
	 * @param theFolder
	 * @return array of files in the given folder
	 */
	static public String[] list(final String theFolder){
		return listImplementation(theFolder, null);
	}
	
	static public String[] list(final String theFolder, final String...theExtensions){
		return listImplementation(theFolder, new FileExtensionFilter(theExtensions));
	}
	
	/**
	 * Returns an array of files in the given folder
	 * 
	 * @param theFolder
	 * @param theExtension
	 * @return
	 */
	static public String[] list(final String theFolder, final FilenameFilter theFilter){
		return listImplementation(theFolder, theFilter);
	}
	
	static private String[] listImplementation(final String theFolder, final FilenameFilter theFilter) {
		File myFile = new File(theFolder);
		
		if(!myFile.exists()){
			myFile = new File(dataPath(theFolder));
		}
		
		if(!myFile.exists()){
			throw new CCIOException("The given folder: " + theFolder + " does not exist.");
		}
		
		if(!myFile.isDirectory()){
			throw new CCIOException("The given path: " + theFolder + " is not a folder.");
		}
		
		return myFile.list(theFilter);
	}

	/**
	 * Takes a path and creates any in-between folders if they don't
	 * already exist. Useful when trying to save to a subfolder that
	 * may not actually exist.
	 * @param theFileName theFileNameToCheck
	 */
	static public void createPath(String theFileName){
		File file = new File(theFileName);
		String parent = file.getParent();
		if (parent != null){
			File unit = new File(parent);
			if (!unit.exists())
				unit.mkdirs();
		}
	}

	//////////////////////////////////////////////////////////////
	// FILE/FOLDER SELECTION

	private static JFileChooser fileChooser = new JFileChooser();
	private static File selectedFile;
	
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
	static public String selectInput() {
		return selectInput("Select a file...");
	}
	
	/**
	 * Opens a platform-specific file chooser dialog to select a file for input. This function returns the full path to
	 * the selected file as a <b>String</b>, or <b>null</b> if no selection.
	 * 
	 * @webref input:files
	 * @param prompt message you want the user to see in the file chooser
	 * @return full path to the selected file, or null if canceled.
	 * 
	 * @see #selectOutput(String)
	 * @see #selectFolder(String)
	 */
	static public String selectInput(String prompt) {
		return selectInput(prompt, null);
	}

	
	static public String selectInput(String prompt, String theFolder) {
		fileChooser.setDialogTitle(prompt);
		if(theFolder != null) {
			fileChooser.setCurrentDirectory(new File(theFolder));
		}

		int returned = fileChooser.showOpenDialog(null);
		if (returned == JFileChooser.CANCEL_OPTION) {
			selectedFile = null;
		} else {
			selectedFile = fileChooser.getSelectedFile();
		}
		return (selectedFile == null) ? null : selectedFile.getAbsolutePath();
	}
	
	/**
	 * Opens a platform-specific file chooser dialog to select a file for input. This function returns the full path to
	 * the selected file as a <b>String</b>, or <b>null</b> if no selection. Files are filtered according to the given
	 * file extensions.
	 * 
	 * @webref input:files
	 * @param theExtensions file extensions for filtering
	 * @return full path to the selected file, or null if canceled.
	 * 
	 * @see #selectOutput(String)
	 * @see #selectFolder(String)
	 */
	static public String selectFilteredInput(String...theExtensions) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setFileFilter(new CCFileExtensionFilter(theExtensions));
		fileChooser.setDialogTitle("Select a file...");

		int returned = fileChooser.showOpenDialog(null);
		if (returned == JFileChooser.CANCEL_OPTION) {
			selectedFile = null;
		} else {
			selectedFile = fileChooser.getSelectedFile();
		}
		return (selectedFile == null) ? null : selectedFile.getAbsolutePath();
	}

	/**
	 * Open a platform-specific file save dialog to select a file for output.
	 * 
	 * @return full path to the file entered, or null if canceled.
	 */
	static public String selectOutput() {
		return selectOutput("Save as...");
	}

	/**
	 * Open a platform-specific file save dialog to create of select a file for output. This function returns the full
	 * path to the selected file as a <b>String</b>, or <b>null</b> if no selection. If you select an existing file,
	 * that file will be replaced. Alternatively, you can navigate to a folder and create a new file to write to.
	 * 
	 * @param prompt message you want the user to see in the file chooser
	 * @return full path to the file entered, or null if canceled.
	 * 
	 * @webref input:files
	 * @see #selectInput(String)
	 * @see #selectFolder(String)
	 */
	static public String selectOutput(String prompt) {
		return selectOutput(prompt, null);
	}
	
	static public String selectOutput(String prompt, final String theFolder) {
		fileChooser.setDialogTitle(prompt);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		if(theFolder != null)fileChooser.setCurrentDirectory(new File(theFolder));

		int returned = fileChooser.showSaveDialog(null);
		if (returned == JFileChooser.CANCEL_OPTION) {
			selectedFile = null;
		} else {
			selectedFile = fileChooser.getSelectedFile();
		}
		return (selectedFile == null) ? null : selectedFile.getAbsolutePath();
	}

	static public String selectFolder() {
		return selectFolder("Select a folder...");
	}
	
	/**
	 * Opens a platform-specific file chooser dialog to select a folder for input. This function returns the full path
	 * to the selected folder as a <b>String</b>, or <b>null</b> if no selection.
	 * 
	 * @param prompt message you want the user to see in the file chooser
	 * @return full path to the selected folder, or null if no selection.
	 * 
	 * @see #selectOutput(CCApp, String)
	 * @see #selectFolder(CCApp, String)
	 */
	static public String selectFolder(final String prompt) {
		return selectFolder(prompt, null);
	}
	static public String selectFolder(final String prompt, String theFolder) {

		fileChooser = new JFileChooser();
		fileChooser.setDialogTitle(prompt);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		if(theFolder != null)fileChooser.setCurrentDirectory(new File(theFolder));

		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		int returned = fileChooser.showSaveDialog(null);

		if (returned == JFileChooser.CANCEL_OPTION) {
			selectedFile = null;
		} else {
			selectedFile = fileChooser.getSelectedFile();
		}
		return (selectedFile == null) ? null : selectedFile.getAbsolutePath();
	}
	
}
