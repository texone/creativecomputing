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

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author christianriekoff
 * 
 */
public class CCClipboard implements ClipboardOwner {

	public void lostOwnership(Clipboard theArg0, Transferable theArg1) {

	}

	private Clipboard _myClipboard;
	
	private static CCClipboard instance;
	
	public static  CCClipboard instance() {
		if(instance == null)instance = new CCClipboard();
		return instance;
	}

	private CCClipboard() {
		_myClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * Place a String on the clipboard, and make this class the owner of the Clipboard's contents.
	 */
	public void setData(String theData) {
		StringSelection stringSelection = new StringSelection(theData);
		_myClipboard.setContents(stringSelection, this);
	}

	/**
	 * Get the String residing on the clipboard.
	 * 
	 * @return any text found on the Clipboard; if none found, return an empty String.
	 */
	public String getStringData() {
		String result = "";
		// odd: the Object param of getContents is not currently used
		Transferable myContents = _myClipboard.getContents(this);
		if(myContents == null)return result;
		if(!myContents.isDataFlavorSupported(DataFlavor.stringFlavor))return result;
		
		try {
			result = (String) myContents.getTransferData(DataFlavor.stringFlavor);
		} catch (UnsupportedFlavorException ex) {
			// highly unlikely since we are using a standard DataFlavor
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
			
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<File> getFileData(){
		// odd: the Object param of getContents is not currently used
		Transferable myContents = _myClipboard.getContents(null);
		
		if(myContents == null)return new ArrayList<File>();
		if(!myContents.isDataFlavorSupported(DataFlavor.imageFlavor))return new ArrayList<File>();
		
		try {
			return (List<File>) myContents.getTransferData(DataFlavor.javaFileListFlavor);
		} catch (UnsupportedFlavorException ex) {
			// highly unlikely since we are using a standard DataFlavor
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}
		
		return new ArrayList<File>();
	}
}
