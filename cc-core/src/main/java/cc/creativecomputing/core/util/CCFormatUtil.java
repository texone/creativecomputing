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
package cc.creativecomputing.core.util;

import java.text.NumberFormat;
import java.util.Locale;

public class CCFormatUtil {

	//////////////////////////////////////////////////////////////
	//
	// INTEGER NUMBER FORMATTING
	//
	//////////////////////////////////////////////////////////////
	
	private static NumberFormat _myIntFormat = NumberFormat.getInstance(Locale.ENGLISH);

	/**
	 * Utility function for formatting numbers into strings.
	 * @param theNumbers the numbers to format
	 * @param theDigits number of digits to pad with zeroes
	 * @return String array of the formated numbers
	 */
	static public String[] nf(final int[] theNumbers, int theDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nf(theNumbers[i], theDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings.
	 * @param theNumber the number to format
	 * @param theDigits number of digits to pad with zeroes
	 * @return formated String presentation of the number
	 */
	static public String nf(final int theNumber, final int theDigits) {
		_myIntFormat.setGroupingUsed(false);
		_myIntFormat.setMinimumIntegerDigits(theDigits);
		return _myIntFormat.format(theNumber);
	}

	/**
	 * Utility function for formatting numbers into strings and 
	 * placing appropriate commas to mark units of 1000. 
	 * @param theNumbers the numbers to format
	 * @return String array of the formated numbers
	 */
	static public String[] nfc(final int[] theNumbers) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nfc(theNumbers[i]);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings and 
	 * placing appropriate commas to mark units of 1000. 
	 * @param theNumber the number to format
	 * @return formated String presentation of the number
	 */
	static public String nfc(final int theNumber) {
		_myIntFormat.setGroupingUsed(true);
		_myIntFormat.setMinimumIntegerDigits(0);
		return _myIntFormat.format(theNumber);
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(int, int)} but 
	 * leaves a blank space in front of positive numbers so they align with negative numbers in spite of 
	 * the minus symbol. 
	 * @param theNumber the number to format
	 * @param theDigits number of digits to pad with spaces
	 * @return String presentation of the number
	 */
	static public String nfs(final int theNumber, final int theDigits) {
		return (theNumber < 0) ? nf(theNumber, theDigits) : (' ' + nf(theNumber, theDigits));
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(int[], int)} but 
	 * leaves a blank space in front of positive numbers so they align with negative numbers in spite of 
	 * the minus symbol. 
	 * @param theNumbers the numbers to format
	 * @param theDigits number of digits to pad with zeroes
	 * @return String array of the formated numbers
	 */
	static public String[] nfs(final int[] theNumbers, int theDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nfs(theNumbers[i], theDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to 
	 * {@link #nf(int, int)} but puts a "+" in front of positive numbers and a "-" in front 
	 * of negative numbers.
	 * @param theNumber number to format
	 * @param theDigits digits to pad with zeroes
	 * @return String presentation of the number
	 */
	static public String nfp(final int theNumber, final int theDigits) {
		return (theNumber < 0) ? nf(theNumber, theDigits) : ('+' + nf(theNumber, theDigits));
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to 
	 * {@link #nf(int[], int)} but puts a "+" in front of positive numbers and a "-" in front 
	 * @param theNumbers the numbers to format
	 * @param theDigits digits to pad with zeroes
	 * @return String array of the formated numbers
	 */
	static public String[] nfp(final int[] theNumbers, final int theDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nfp(theNumbers[i], theDigits);
		}
		return formatted;
	}

	//////////////////////////////////////////////////////////////
	//
	// FLOAT NUMBER FORMATTING
	//
	//////////////////////////////////////////////////////////////

	static private NumberFormat _myFloatFormat = NumberFormat.getInstance(Locale.ENGLISH);

	/**
	 * Utility function for formatting numbers into strings.
	 * @param theNumbers the numbers to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String[] nf(final float[] theNumbers, final int theLeftDigits, final int theRightDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nf(theNumbers[i], theLeftDigits, theRightDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings.
	 * @param theNumber the number to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String presentation of the number
	 */
	static public String nf(final float theNumber, final int theLeftDigits, final int theRightDigits) {
		_myFloatFormat.setGroupingUsed(false);

		if (theLeftDigits != 0)
			_myFloatFormat.setMinimumIntegerDigits(theLeftDigits);
		if (theRightDigits != 0) {
			_myFloatFormat.setMinimumFractionDigits(theRightDigits);
			_myFloatFormat.setMaximumFractionDigits(theRightDigits);
		}
		return _myFloatFormat.format(theNumber);
	}

	/**
	 * Utility function for formatting numbers into strings and 
	 * placing appropriate commas to mark units of 1000.
	 * @param theNumbers theNumbers to format
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String[] nfc(final float[] theNumbers, final int theRightDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nfc(theNumbers[i], theRightDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings and 
	 * placing appropriate commas to mark units of 1000.
	 * @param theNumber the number to format
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String presentation of the number
	 */
	static public String nfc(final float theNumber, final int theRightDigits) {
		_myFloatFormat.setGroupingUsed(true);
		_myFloatFormat.setMinimumFractionDigits(theRightDigits);
		_myFloatFormat.setMaximumFractionDigits(theRightDigits);
		return _myFloatFormat.format(theNumber);
	}
	
	/**
	 * Utility function for formatting numbers into strings and 
	 * placing appropriate commas to mark units of 1000.
	 * @param theNumber the number to format
	 * @param theMinRightDigits
	 * @param theMaxRightDigits
	 * @return String presentation of the number
	 */
	static public String nfc(final float theNumber, final int theMinRightDigits, final int theMaxRightDigits) {
		_myFloatFormat.setGroupingUsed(true);
		_myFloatFormat.setMinimumFractionDigits(theMinRightDigits);
		_myFloatFormat.setMaximumFractionDigits(theMaxRightDigits);
		
		return _myFloatFormat.format(theNumber);
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(float[], int, int)} but 
	 * leaves a blank space in front of positive numbers so they align with negative numbers in spite of the minus symbol. 
	 * @param theNumbers the numbers to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String[] nfs(final float[] theNumbers, final int theLeftDigits, final int theRightDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nfs(theNumbers[i], theLeftDigits, theRightDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(float, int, int)} but 
	 * leaves a blank space in front of positive numbers so they align with negative numbers in spite of the minus symbol. 
	 * @param theNumber the number to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String nfs(final float theNumber, final int theLeftDigits, final int theRightDigits) {
		return (theNumber < 0) ? nf(theNumber, theLeftDigits, theRightDigits) : (' ' + nf(theNumber, theLeftDigits, theRightDigits));
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(float[], int, int)}
	 * but puts a "+" in front of positive numbers and a "-" in front of negative numbers.
	 * @param theNumbers the numbers to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String[] nfp(float[] theNumbers, final int theLeftDigits, final int theRightDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nfp(theNumbers[i], theLeftDigits, theRightDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(float[], int, int)}
	 * but puts a "+" in front of positive numbers and a "-" in front of negative numbers.
	 * @param theNumber the number to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String nfp(final float theNumber, final int theLeftDigits, final int theRightDigits) {
		return (theNumber < 0) ? nf(theNumber, theLeftDigits, theRightDigits) : ('+' + nf(theNumber, theLeftDigits, theRightDigits));
	}

	//////////////////////////////////////////////////////////////
	//
	// Double NUMBER FORMATTING
	//
	//////////////////////////////////////////////////////////////

	static private NumberFormat _myDoubleFormat = NumberFormat.getInstance(Locale.ENGLISH);

	/**
	 * Utility function for formatting numbers into strings.
	 * @param theNumbers the numbers to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String[] nd(final double[] theNumbers, final int theLeftDigits, final int theRightDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nd(theNumbers[i], theLeftDigits, theRightDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings.
	 * @param theNumber the number to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String presentation of the number
	 */
	static public String nd(final double theNumber, final int theLeftDigits, final int theRightDigits) {
		_myDoubleFormat.setGroupingUsed(false);

		if (theLeftDigits != 0)
			_myDoubleFormat.setMinimumIntegerDigits(theLeftDigits);
		if (theRightDigits != 0) {
			_myDoubleFormat.setMinimumFractionDigits(theRightDigits);
			_myDoubleFormat.setMaximumFractionDigits(theRightDigits);
		}
		return _myDoubleFormat.format(theNumber);
	}

	/**
	 * Utility function for formatting numbers into strings.
	 * @param theNumber the number to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String presentation of the number
	 */
	static public String nd(final double theNumber, final int theRightDigits) {
		return nd(theNumber, 0, theRightDigits);
	}

	/**
	 * Utility function for formatting numbers into strings and 
	 * placing appropriate commas to mark units of 1000.
	 * @param theNumbers theNumbers to format
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String[] ndc(final double[] theNumbers, final int theRightDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = ndc(theNumbers[i], theRightDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings and 
	 * placing appropriate commas to mark units of 1000.
	 * @param theNumber the number to format
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String presentation of the number
	 */
	static public String ndc(final double theNumber, final int theRightDigits) {
		_myDoubleFormat.setGroupingUsed(true);
		_myDoubleFormat.setMinimumFractionDigits(theRightDigits);
		_myDoubleFormat.setMaximumFractionDigits(theRightDigits);
		return _myDoubleFormat.format(theNumber);
	}
	
	/**
	 * Utility function for formatting numbers into strings and 
	 * placing appropriate commas to mark units of 1000.
	 * @param theNumber the number to format
	 * @param theMinRightDigits
	 * @param theMaxRightDigits
	 * @return String presentation of the number
	 */
	static public String ndc(final double theNumber, final int theMinRightDigits, final int theMaxRightDigits) {
		_myDoubleFormat.setGroupingUsed(true);
		_myDoubleFormat.setMinimumFractionDigits(theMinRightDigits);
		_myDoubleFormat.setMaximumFractionDigits(theMaxRightDigits);
		
		return _myDoubleFormat.format(theNumber);
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(double[], int, int)} but 
	 * leaves a blank space in front of positive numbers so they align with negative numbers in spite of the minus symbol. 
	 * @param theNumbers the numbers to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String[] nds(final double[] theNumbers, final int theLeftDigits, final int theRightDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = nds(theNumbers[i], theLeftDigits, theRightDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(double, int, int)} but 
	 * leaves a blank space in front of positive numbers so they align with negative numbers in spite of the minus symbol. 
	 * @param theNumber the number to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String nds(final double theNumber, final int theLeftDigits, final int theRightDigits) {
		return (theNumber < 0) ? nd(theNumber, theLeftDigits, theRightDigits) : (' ' + nd(theNumber, theLeftDigits, theRightDigits));
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(double[], int, int)}
	 * but puts a "+" in front of positive numbers and a "-" in front of negative numbers.
	 * @param theNumbers the numbers to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String[] ndp(double[] theNumbers, final int theLeftDigits, final int theRightDigits) {
		String[] formatted = new String[theNumbers.length];
		for (int i = 0; i < formatted.length; i++) {
			formatted[i] = ndp(theNumbers[i], theLeftDigits, theRightDigits);
		}
		return formatted;
	}

	/**
	 * Utility function for formatting numbers into strings. Similar to {@link #nf(double[], int, int)}
	 * but puts a "+" in front of positive numbers and a "-" in front of negative numbers.
	 * @param theNumber the number to format
	 * @param theLeftDigits number of digits to the left of the decimal point
	 * @param theRightDigits number of digits to the right of the decimal point
	 * @return String array of the formated numbers
	 */
	static public String ndp(final double theNumber, final int theLeftDigits, final int theRightDigits) {
		return (theNumber < 0) ? nd(theNumber, theLeftDigits, theRightDigits) : ('+' + nd(theNumber, theLeftDigits, theRightDigits));
	}

	/**
	 * Formats the given time into a more readable string for printing, debugging, etc.
	 * @param theTime time to format in seconds.
	 * @return a more readable String representation of the given time.
	 */
	public static String formatTime(final double theTime) {
		int hours = (int)(theTime / 3600);
		int minutes = (int) (theTime / 60) % 60;
		int seconds = (int) theTime % 60;
		int milli = (int) (theTime * 1000) % 1000;

		return nf(hours,2) + ":" + nf(minutes, 2) + ":" + nf(seconds, 2) + "." + nf(milli, 3);
	}
	
	/**
	 * Formats the given time into a more readable string for printing, debugging, etc.
	 * @param theTime time to format in seconds.
	 * @return a more readable String representation of the given time in format hh:mm.
	 */
	public static String formatTimeHM(final double theTime) {
		int hours = (int)(theTime / 3600);
		int minutes = (int) (theTime / 60) % 60;

		return nf(hours,2) + ":" + nf(minutes, 2);
	}
}
