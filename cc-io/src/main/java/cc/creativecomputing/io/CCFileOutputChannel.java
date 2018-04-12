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
import java.nio.ByteOrder;
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
public class CCFileOutputChannel {

	private Path _myPath;
	
	private ByteBuffer _my8ByteBuffer = ByteBuffer.allocate(8);
	private ByteBuffer _my4ByteBuffer = ByteBuffer.allocate(4);
	private ByteBuffer _my2ByteBuffer = ByteBuffer.allocate(2);
	private ByteBuffer _my1ByteBuffer = ByteBuffer.allocate(1);
	
	private SeekableByteChannel _myFileChannel;
	
	private ByteOrder _myOrder = ByteOrder.BIG_ENDIAN;
	
	public CCFileOutputChannel(Path thePath) {
		_myPath = thePath;
		open();
	}
	
	public void order(ByteOrder theOrder) {
		_myOrder = theOrder;
		_my8ByteBuffer.order(theOrder);
		_my4ByteBuffer.order(theOrder);
		_my2ByteBuffer.order(theOrder);
		_my1ByteBuffer.order(theOrder);
	}
	
	public void open() {
		if(_myFileChannel != null && _myFileChannel.isOpen())return;
		
		CCNIOUtil.createDirectories(_myPath);
		try {
			_myFileChannel = Files.newByteChannel(_myPath, StandardOpenOption.CREATE, StandardOpenOption.WRITE);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void write(String theString) {
		write(theString.getBytes());
	}
	
	public void write(ByteBuffer theBuffer) {
		try {
			_myFileChannel.write(theBuffer);
		} catch (IOException e) {
			throw new CCIOException(e);
		}
	}
	
	public void write(byte...theBytes) {
		ByteBuffer myBuffer = ByteBuffer.wrap(theBytes);
		myBuffer.order(_myOrder);
		write(myBuffer);
	}
	
	public void write(float[] theData) {
		ByteBuffer myBuffer = ByteBuffer.allocateDirect(theData.length * 4); //4 bytes per float
		myBuffer.order(_myOrder);
		myBuffer.asFloatBuffer().put(theData);
		myBuffer.position(0);
		write(myBuffer);
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
	
	public void write(long theLong) {
		_my8ByteBuffer.putLong(0,theLong);
		_my8ByteBuffer.position(0);
		write(_my8ByteBuffer);
	}
	
	public long readLong() {
		_my8ByteBuffer.position(0);
		read(_my8ByteBuffer);
		return _my8ByteBuffer.getLong(0);
	}
	
	public void write(byte theByte) {
		_my1ByteBuffer.put(0,theByte);
		_my1ByteBuffer.position(0);
		write(_my1ByteBuffer);
	}
	
	public void write(short theShort) {
		_my2ByteBuffer.putShort(0,theShort);
		_my4ByteBuffer.position(0);
		write(_my2ByteBuffer);
	}
	
	public void write(int theInt) {
		_my4ByteBuffer.putInt(0,theInt);
		_my4ByteBuffer.position(0);
		write(_my4ByteBuffer);
	}
	
	public void write(float theFloat) {
		_my4ByteBuffer.putFloat(0,theFloat);
		_my4ByteBuffer.position(0);
		write(_my4ByteBuffer);
	}
	
	public void write(double theDouble) {
		_my8ByteBuffer.putDouble(0,theDouble);
		_my8ByteBuffer.position(0);
		write(_my8ByteBuffer);
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
