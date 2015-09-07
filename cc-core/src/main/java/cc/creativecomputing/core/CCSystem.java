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
package cc.creativecomputing.core;

import java.io.IOException;

/**
 * 
 * @author texone
 * 
 */
public class CCSystem {

	public static enum CCOS {
		WINDOWS, MACOSX, LINUX, OTHER;
	}

	/**
	 * Current platform in use, one of WINDOWS, MACOSX, LINUX or OTHER.
	 */
	static public CCOS os;

	static {
		String myOsName = System.getProperty("os.name");

		if (myOsName.indexOf("Mac") != -1) {
			os = CCOS.MACOSX;

		} else if (myOsName.indexOf("Windows") != -1) {
			os = CCOS.WINDOWS;

		} else if (myOsName.equals("Linux")) { // true for the ibm vm
			os = CCOS.LINUX;

		} else {
			os = CCOS.OTHER;
		}
	}

	/**
	 * Current platform in use.
	 * <P>
	 * Equivalent to System.getProperty("os.name"), just used internally.
	 */
	static public String platformName = System.getProperty("os.name");

	/**
	 * Attempt to open a file using the platform's shell.
	 */
	static public void open(String filename) {
		if (os == CCOS.WINDOWS) {
			// just launching the .html file via the shell works
			// but make sure to chmod +x the .html files first
			// also place quotes around it in case there's a space
			// in the user.dir part of the url
			try {
				Runtime.getRuntime().exec("cmd /c \"" + filename + "\"");
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not open " + filename);
			}

		} else if (os == CCOS.MACOSX) {
			// osx fix contributed by chandler for rev 0113
			try {
				// Java on OS X doesn't like to exec commands inside quotes
				// for some reason.. escape spaces with slashes just in case
				if (filename.indexOf(' ') != -1) {
					StringBuffer sb = new StringBuffer();
					char c[] = filename.toCharArray();
					for (int i = 0; i < c.length; i++) {
						if (c[i] == ' ') {
							sb.append("\\\\ ");
						} else {
							sb.append(c[i]);
						}
					}
					filename = sb.toString();
				}
				Runtime.getRuntime().exec("open " + filename);

			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Could not open " + filename);
			}

		} else { // give up and just pass it to Runtime.exec()
			open(new String[] { filename });
		}
	}

	/**
	 * Launch a process using a platforms shell, and an array of args passed on the command line.
	 */
	static public Process open(String args[]) {
		try {
			return Runtime.getRuntime().exec(args);
		} catch (Exception e) {
			throw new RuntimeException("Could not open " + args, e);
		}
	}
	
	public static enum CCEndianess{
		LITTLE_ENDIAN, BIG_ENDIAN
	}
	
	static public CCEndianess endianess;
	
	static {
		if(System.getProperty("sun.cpu.endian").equals("big")) {
			endianess = CCEndianess.BIG_ENDIAN;
		}else {
			endianess = CCEndianess.LITTLE_ENDIAN;
		}
	}

	/**
	 * Returns the total amount of memory in the Java virtual machine. The value returned by this method may vary over
	 * time, depending on the host environment. Note that the amount of memory required to hold an object of any given
	 * type may be implementation-dependent.
	 * 
	 * @return the total amount of memory currently available for current and future objects, measured in bytes.
	 */
	public static long memoryTotal() {
		return Runtime.getRuntime().totalMemory();
	}

	/**
	 * Returns the amount of free memory in the Java Virtual Machine. Calling the gc method may result in increasing the
	 * value returned by freeMemory.
	 * 
	 * @return an approximation to the total amount of memory currently available for future allocated objects, measured
	 *         in bytes.
	 */
	public static long memoryFree() {
		return Runtime.getRuntime().freeMemory();
	}

	/**
	 * Returns the memory currently used by the program.
	 * 
	 * @return
	 */
	public static long memoryInUse() {
		return memoryTotal() - memoryFree();
	}
}
