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
package cc.creativecomputing.video;

import java.io.File;
import java.net.URL;

import org.gstreamer.Gst;
import org.gstreamer.Registry;
import org.gstreamer.elements.PlayBin2;

import cc.creativecomputing.core.CCSystem;
import cc.creativecomputing.core.CCSystem.CCOS;
import cc.creativecomputing.core.logging.CCLog;

import com.sun.jna.Platform;

/**
 * This class contains some basic functions used by the rest of the classes in this library.
 * originally adapted from the Processing project - http://processing.org
 * @author Andres Colubri
 * @author christianr
 */
public class CCGStreamer {
	
//	private static String OSX_GLOBAL_PATH = "/System/Library/Frameworks/GStreamer.framework/Versions/Current/lib";
	private static String LINUX_GLOBAL_PATH = "/usr/lib/x86_64-linux-gnu";

	private String _myGstreamerBinPath = "";
	private String _myGstreamerPluginsPath = "";

	private boolean defaultGLibContext = false;


	// Direct buffer pass enabled by default.
	public static boolean passDirectBuffer = true;

//	private String _myGlobalPluginsFolder = "gstreamer-0.10";

	private int _myBitsJVM = Integer.parseInt(System.getProperty("sun.arch.data.model"));

	// Default location of the local install of gstreamer. Suggested by Charles Bourasseau.
	// When it is left as empty string, GSVideo will attempt to use the path from GSLibraryPath.get(),
	// otherwise it will use it as the path to the folder where the libgstreamer.dylib and other
	// files are located.
	private String localPluginsFolder = "plugins";

	// Some constants to identify AUDIO, VIDEO and RAW streams.
	static public final int AUDIO = 0;
	static public final int VIDEO = 1;
	static public final int RAW = 2;

	private CCGStreamer() {
		
		switch(CCSystem.os) {
		case LINUX:
			setupGlobal(LINUX_GLOBAL_PATH);
			break;
		case MACOSX:
			setupLocal();
			break;
		case WINDOWS:
			setupLocal();
			break;
		default:
		}
		if (!_myGstreamerBinPath.equals("")) {
			System.setProperty("jna.library.path", _myGstreamerBinPath);
		}
		CCGStreamerLibraryLoader loader = CCGStreamerLibraryLoader.instance();
		if (loader == null) {
			CCLog.error("Cannot load local version of GStreamer libraries.");
		}
		
		String[] myArguments = { "" };
		Gst.setUseDefaultContext(defaultGLibContext);
		Gst.init("cc video", myArguments);

		addPlugins();
	}

//	static public void restart() {
//		removePlugins();
//		Gst.deinit();
//		initImpl();
//	}
	
	private boolean isGStreamerPresent(String theDirectory, String theLibName) {
		File myLibFolder = new File(theDirectory);
		String[] myFiles = myLibFolder.list();
		if (myFiles == null) return false;
			
		for (String myFile:myFiles) {
			if (myFile.indexOf(theLibName) >= 0) {
				return true;
			}
		}
		return false;
	}
	
	private void setupGlobal(String theGlobalPath) {
		if (isGStreamerPresent(theGlobalPath, "libgstreamer")) {
			_myGstreamerBinPath = "";
			_myGstreamerPluginsPath = "";	
			return;
		}
		
		CCLog.error("Loading local version of GStreamer not supported in Linux at this time.");
	}
	
	private String buildGStreamerBinPath(String base, String os) {
		File myPath = new File(base + os);
		if (myPath.exists()) {
			return base + os;
		} else {
			return base;
		}
	}
	
	/**
	 * This method returns the folder inside which the gstreamer library folder should be located.
	 * @return
	 */
	private String localLibraryPath() {
		if (CCSystem.os == CCOS.LINUX)return "";
		
		URL myUrl = PlayBin2.class.getResource("PlayBin2.class");
		if(myUrl == null)return "";
		
		// Convert URL to string, taking care of spaces represented by the "%20"
		// string.
		String myPath = myUrl.toString().replace("%20", " ");
		int n0 = myPath.indexOf('/');
		int n1 = myPath.indexOf("/gstreamer-java.jar!");
			/*
			 * if (Platform.isWindows()) { n1 = path.indexOf("/lib/video.jar"); // location of video.jar in // exported
			 * apps. if (n1 == -1) n1 = path.indexOf("/video.jar"); // location of video.jar in // library folder.
			 * 
			 * // In Windows, path string starts with "jar file/C:/..." // so the substring up to the first / is
			 * removed. n0++; } else if (Platform.isMac()) { // In Mac, getting the index of video.jar is enough in the
			 * case of sketches running from the PDE // as well as exported applications. n1 =
			 * path.indexOf("video.jar");
			 */

		// In Windows, path string starts with "jar file/C:/..."
		// so the substring up to the first / is removed.
		if (Platform.isWindows())n0++;

		if ((n0 >= 0) && (n1 >= 0)) return myPath.substring(n0, n1);
			
		return "";
	}
	
	private void setupLocal() {
//		String locPath = localLibraryPath();
		_myGstreamerBinPath = "target/lib/natives";//System.getProperty("java.library.path");//buildGStreamerBinPath(locPath, File.separator + CCSystem.os.name().toLowerCase() + _myBitsJVM);
		_myGstreamerPluginsPath = _myGstreamerBinPath + File.separator + localPluginsFolder;
	}

	private void addPlugins() {
		if (!_myGstreamerPluginsPath.equals("")) {
			Registry myRegistry = Registry.getDefault();
			boolean res;
			res = myRegistry.scanPath(_myGstreamerPluginsPath);
			if (!res) {
				CCLog.error("Cannot load GStreamer plugins from " + _myGstreamerPluginsPath);
			}
		}
		
	}

//	private void removePlugins() {
//		Registry myRegistry = Registry.getDefault();
//		List<Plugin> myPluginList = myRegistry.getPluginList();
//		for (Plugin plg : myPluginList) {
//			myRegistry.removePlugin(plg);
//		}
//	}

	private static CCGStreamer _mySetup;

	/**
	 * Initialize GStreamer
	 */
	static public void init() {
		if (_mySetup == null) {
			_mySetup = new CCGStreamer();
		}
	}
	
	public static void main(String[] args) {
		init();
	}
}
