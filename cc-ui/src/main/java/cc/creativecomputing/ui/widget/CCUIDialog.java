/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.ui.widget;

import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_beep;
import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_inputBox;
import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_messageBox;
import static org.lwjgl.util.tinyfd.TinyFileDialogs.tinyfd_notifyPopup;

public class CCUIDialog {
	
	public static void beep(){
		tinyfd_beep();
	}
	
	public static enum CCUIDialogIcon{
		INFO("info"),
		WARNING("warning"),
		ERROR("error"),
		QUESTION("question");
		
		public String text;
		
		private CCUIDialogIcon(String theText){
			text = theText;
		}
	}
	
	/**
	 * Opens a notification popup
	 * @param theTitle null or string 
	 * @param theMessage null or string may contain \n \t
	 * @param theIcon
	 */
	public static void notify(String theTitle, String theMessage, CCUIDialogIcon theIcon){
		tinyfd_notifyPopup(theTitle, theMessage, theIcon.text);
	}
	
	public static void notify(String theTitle, String theMessage){
		notify(theTitle, theMessage, CCUIDialogIcon.INFO);
	}
	
	public static enum CCUIMessageType{
		OK("ok"),
		OK_CANCEL("okcancel"),
		YES_NO("yesno");
		//YES_NO_CANCEL("yesnocancel");
		
		public String text;
		
		private CCUIMessageType(String theText){
			text = theText;
		}
	}
	
	/**
	 * Opens a message dialog
	 * @param theTitle null or string 
	 * @param theMessage null or string may contain \n \t
	 * @param theType
	 * @param theIcon
	 * @return <code>true</code> if yes or ok is pressed otherwise <code>false</code>
	 */
	public static boolean message(String theTitle, String theMessage, CCUIMessageType theType, CCUIDialogIcon theIcon){
		return tinyfd_messageBox(theTitle, theMessage, theType.text, theIcon.text, true);
	}
	
	public static boolean message(String theTitle, String theMessage, CCUIMessageType theType){
		return message(theTitle, theMessage, theType, CCUIDialogIcon.INFO);
	}
	
	/**
	 * 
	 * @param theTitle null or string 
	 * @param theMessage null or string may contain \n \t
	 * @param theDefault string if null its a password
	 * @return
	 */
	public static String input(String theTitle, String theMessage, String theDefault){
		return tinyfd_inputBox(theTitle, theMessage, theDefault);
	}
	
	public static String input(String theTitle, String theMessage){
		return tinyfd_inputBox(theTitle, theMessage, "");
	}

//case GLFW_KEY_1:
//    System.out.println("\nOpening message dialog...");
//    System.out.println(tinyfd_messageBox("Please read...", "...this message.", "okcancel", "info", true) ? "OK" : "Cancel");
//    break;
//case GLFW_KEY_2:
//    System.out.println("\nOpening input box dialog...");
//    System.out.println(tinyfd_inputBox("Input Value", "How old are you?", "30"));
	
	public static void main(String[] args) {
//		beep();
		System.out.println(input("Please read...", "...this message.", ""));
	}
}
