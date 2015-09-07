/*
 *  Copyright (c) 2007 - 2008 by Damien Di Fede <ddf@compartmental.net>
 *
 *   This program is free software; you can redistribute it and/or modify
 *   it under the terms of the GNU Library General Public License as published
 *   by the Free Software Foundation; either version 2 of the License, or
 *   (at your option) any later version.
 *
 *   This program is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU Library General Public License for more details.
 *
 *   You should have received a copy of the GNU Library General Public
 *   License along with this program; if not, write to the Free Software
 *   Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

package cc.creativecomputing.sound;

import java.nio.file.Path;

/**
 * <code>AudioMetaData</code> provides information commonly found in ID3 tags.
 * However, other audio formats, such as Ogg, can contain similar information.
 * So rather than refer to this information as ID3Tags or similar, we simply
 * call it metadata. This base class returns the empty string or -1 from all
 * methods and derived classes are expected to simply override the methods that
 * they have information for. This is a little less brittle than using an
 * interface because later on new properties can be added without breaking
 * existing code.
 * 
 * @example Basics/GetMetaData
 */
public class CCAudioMetaData {

	private Path _myFilePath;
	private long _myLength;
	private long _myFrameCount;

	public CCAudioMetaData(Path thePath, long theLength, long theSampleLength) {
		_myFilePath = thePath;
		_myLength = theLength;
		_myFrameCount = theSampleLength;
	}

	/**
	 * The length of the recording in milliseconds.
	 * 
	 * @return int: the length in milliseconds
	 * 
	 * @related AudioMetaData
	 */
	public int length() {
		return (int) _myLength;
	}

	/**
	 * 
	 * How many sample frames are in this recording.
	 * 
	 * @return the number of sample frames
	 */
	public int sampleFrameCount() {
		return (int) _myFrameCount;
	}

	/**
	 * The name of the file / URL of the recording.
	 * 
	 * @return the file name
	 */
	public Path fileName() {
		return _myFilePath;
	}

	/**
	 * The title of the recording.
	 * 
	 * @return the title tag
	 */
	public String title() {
		return "";
	}

	/**
	 * The author or the recording.
	 * 
	 * @return the author tag
	 */
	public String author() {
		return "";
	}

	/**
	 * The album the recording came from.
	 * 
	 * @return the album tab
	 */
	public String album() {
		return "";
	}

	/**
	 * The date the recording was made.
	 * 
	 * @return the date tag
	 */
	public String date() {
		return "";
	}

	/**
	 * The comment field in the file.
	 * 
	 * @return the comment tag
	 */
	public String comment() {
		return "";
	}

	/**
	 * The track number of the recording. This will sometimes be in the form
	 * 3/10, giving you both the track number and total tracks on the album this
	 * track came from.
	 * 
	 * @return the track tag
	 */
	public String track() {
		return "";
	}

	/**
	 * The genre of the recording.
	 * 
	 * @return the genre tag
	 */
	public String genre() {
		return "";
	}

	/**
	 * The copyright of the recording.
	 * 
	 * @return the copyright tag
	 */
	public String copyright() {
		return "";
	}

	/**
	 * The disc number of the recording.
	 * 
	 * @return the disc tag
	 */
	public String disc() {
		return "";
	}

	/**
	 * The composer of the recording.
	 * 
	 * @return the composer tag
	 */
	public String composer() {
		return "";
	}

	/**
	 * The lyrics for the recording, if any.
	 * 
	 * @return the lyrics tag
	 */
	public String lyrics() {
		return "";
	}

	/**
	 * The orchestra that performed the recording.
	 * 
	 * @return the orchestra tag
	 */
	public String orchestra() {
		return "";
	}

	/**
	 * The publisher of the recording.
	 * 
	 * @return the publisher tag
	 */
	public String publisher() {
		return "";
	}

	/**
	 * The software the recording was encoded with.
	 * 
	 * @return the encoded tag
	 */
	public String encoded() {
		return "";
	}
}
