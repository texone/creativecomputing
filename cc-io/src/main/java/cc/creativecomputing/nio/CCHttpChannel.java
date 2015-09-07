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
package cc.creativecomputing.nio;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.URI;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.CharBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import cc.creativecomputing.io.CCIOException;

public class CCHttpChannel {

	private SocketChannel _myChannel;
	private URI _myURI;

	private String _myPath;

	public CCHttpChannel(String theUrl) {
		open(theUrl);
	}

	public void open(String theUrl) {
		// Parse the URL. Note we use the new java.net.URI, not URL here.
		try {
			_myURI = new URI(theUrl);

			// Now query and verify the various parts of the URI
			String myScheme = _myURI.getScheme();
			if (myScheme == null || !myScheme.equals("http"))
				throw new IllegalArgumentException("Must use 'http:' protocol");

			int port = _myURI.getPort();
			if (port == -1)
				port = 80; // Use default port if none specified

			_myPath = _myURI.getRawPath();
			if (_myPath == null || _myPath.length() == 0)
				_myPath = "/";

			String query = _myURI.getRawQuery();
			query = (query == null) ? "" : '?' + query;

			// Combine the hostname and port into a single address object.
			// java.net.SocketAddress and InetSocketAddress are new in Java 1.4
			SocketAddress serverAddress = new InetSocketAddress(_myURI.getHost(), port);

			// Open a SocketChannel to the server
			System.out.println(_myURI);
			System.out.println(_myURI.getHost()+":" +port);
			_myChannel = SocketChannel.open(serverAddress);
			query(query);
		} catch (Exception e) {
			throw new CCIOException(e);
		}

	}

	public void query(String theQuery) {
		try {
		// Put together the HTTP request we'll send to the server.
		String myRequest = "GET " + _myPath + theQuery + " HTTP/1.1\r\n" + // The request
				"Host: " + _myURI.getHost() + "\r\n" + // Required in HTTP 1.1
				"Connection: close\r\n" + // Don't keep connection open
				"User-Agent: " + CCHttpChannel.class.getName() + "\r\n" + "\r\n"; // Blank

		// line
		// indicates
		// end of
		// request
		// headers

		// Now wrap a CharBuffer around that request string
		CharBuffer requestChars = CharBuffer.wrap(myRequest);

		// Get a Charset object to encode the char buffer into bytes
		Charset charset = Charset.forName("ISO-8859-1");

		// Use the charset to encode the request into a byte buffer
		ByteBuffer requestBytes = charset.encode(requestChars);

		// Finally, we can send this HTTP request to the server.
		_myChannel.write(requestBytes);

		// Allocate a 32 Kilobyte byte buffer for reading the response.
		// Hopefully we'll get a low-level "direct" buffer
		ByteBuffer data = ByteBuffer.allocateDirect(32 * 1024);

		// Have we discarded the HTTP response headers yet?
		boolean skippedHeaders = false;
		// The code sent by the server
		int responseCode = -1;

		// Now loop, reading data from the server channel and writing it
		// to the destination channel until the server indicates that it
		// has no more data.
		while (_myChannel.read(data) != -1) { // Read data, and check for end
			data.flip(); // Prepare to extract data from buffer

			// All HTTP reponses begin with a set of HTTP headers, which
			// we need to discard. The headers end with the string
			// "\r\n\r\n", or the bytes 13,10,13,10. If we haven't already
			// skipped them then do so now.
			if (!skippedHeaders) {
				// First, though, read the HTTP response code.
				// Assume that we get the complete first line of the
				// response when the first read() call returns. Assume also
				// that the first 9 bytes are the ASCII characters
				// "HTTP/1.1 ", and that the response code is the ASCII
				// characters in the following three bytes.
				if (responseCode == -1) {
					responseCode = 100 * (data.get(9) - '0') + 10 * (data.get(10) - '0') + 1 * (data.get(11) - '0');
					System.out.println("responseCode:" + responseCode);
					// If there was an error, report it and quit
					// Note that we do not handle redirect responses.
					if (responseCode < 200 || responseCode >= 300) {
						System.err.println("HTTP Error: " + responseCode);
						System.exit(1);
					}
				}

				// Now skip the rest of the headers.
				try {
					for (;;) {
						if ((data.get() == 13) && (data.get() == 10) && (data.get() == 13) && (data.get() == 10)) {
							skippedHeaders = true;
							break;
						}
					}
				} catch (BufferUnderflowException e) {
					// If we arrive here, it means we reached the end of
					// the buffer and didn't find the end of the headers.
					// There is a chance that the last 1, 2, or 3 bytes in
					// the buffer were the beginning of the \r\n\r\n
					// sequence, so back up a bit.
					data.position(data.position() - 3);
					// Now discard the headers we have read
					data.compact();
					// And go read more data from the server.
					continue;
				}
			}
			return;
		}
		} catch (Exception e) {
			throw new CCIOException(e);
		}
	}

	public int read(ByteBuffer theBuffer) {
		try {
			return _myChannel.read(theBuffer);
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

	public void close() {
		try {
			if (_myChannel != null && _myChannel.isOpen())
				_myChannel.close();
		} catch (IOException e) {
		}
	}
	
	public static void main(String[] args) {
		CCHttpChannel myChannel = new CCHttpChannel("http://192.168.56.2/api/v1/ingoing?lat0=13&lon0=52&lat1=14&lon1=53");
		
		ByteBuffer myByteBuffer = ByteBuffer.allocate(4 + 4 + 4);

		myByteBuffer.order(ByteOrder.LITTLE_ENDIAN);
		int myEntryCounter = 0;
		
		while(myChannel.read(myByteBuffer) > 0) {
			myByteBuffer.rewind();
			float myTime = myByteBuffer.getFloat();
			float myLatitude = myByteBuffer.getFloat();
			float myLongitude = myByteBuffer.getFloat();
			myByteBuffer.rewind();
			
			myEntryCounter++;
			
			System.out.println(myEntryCounter+" ; " +myTime+" ; " +myLatitude+" ; " +myLongitude);
		}
	}
}
