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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

/**
 * @author christianriekoff
 *
 */
public class CCFileInputChannel {

	private Path _myPath;
	
	private ByteBuffer _my8ByteBuffer = ByteBuffer.allocate(8);
	private ByteBuffer _my4ByteBuffer = ByteBuffer.allocate(4);
//	private ByteBuffer _my2ByteBuffer = ByteBuffer.allocate(2);
	
	private SeekableByteChannel _myFileChannel;
	
	
	public CCFileInputChannel(Path thePath) {
		_myPath = thePath;
		open();
	}
	
	public void open() {
		if(_myFileChannel != null && _myFileChannel.isOpen())return;
		
		try {
			_myFileChannel = Files.newByteChannel(_myPath, StandardOpenOption.READ);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public int read(ByteBuffer theBuffer) {
		try {
			return _myFileChannel.read(theBuffer);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void read(byte[] theBytes) {
		ByteBuffer myBuffer = ByteBuffer.allocate(theBytes.length);
		read(myBuffer);
		myBuffer.flip();
		myBuffer.get(theBytes);
	}
	
	public ByteBuffer read(){
		try {
			ByteBuffer myBuffer = ByteBuffer.allocate((int)_myFileChannel.size());
			_myFileChannel.read(myBuffer);
			myBuffer.rewind();
			return myBuffer;
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public IntBuffer readInts(int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocate(theSize * 4);
		read(myBuffer);
		myBuffer.rewind();
		return myBuffer.asIntBuffer();
	}
	
	public FloatBuffer readFloats(int theSize) {
		ByteBuffer myBuffer = ByteBuffer.allocate(theSize * 4);
		read(myBuffer);
		myBuffer.position(0);
		return myBuffer.asFloatBuffer();
	}
	
	public long position() {
		try {
			return _myFileChannel.position();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public boolean isFinished(){
		try {
			return _myFileChannel.position() >= _myFileChannel.size();
		} catch (IOException e) {
			return false;
		}
	}
	
	public void close() {
		try {
			_myFileChannel.close();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public long size() {
		try {
			return _myFileChannel.size();
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public int readInt() {
		_my4ByteBuffer.position(0);
		read(_my4ByteBuffer);
		return _my4ByteBuffer.getInt(0);
	}
	
	public float readFloat() {
		_my4ByteBuffer.position(0);
		read(_my4ByteBuffer);
		return _my4ByteBuffer.getFloat(0);
	}
	
	public double readDouble() {
		_my8ByteBuffer.position(0);
		read(_my8ByteBuffer);
		return _my8ByteBuffer.getDouble(0);
	}
}
