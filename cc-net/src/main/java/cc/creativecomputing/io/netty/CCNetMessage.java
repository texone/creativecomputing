/*
 * Copyright (c) 2017 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
package cc.creativecomputing.io.netty;

import java.net.SocketAddress;

public class CCNetMessage<MessageType> {

	public final MessageType message;
	public final SocketAddress address;
	public final long timeStamp;
	
	public CCNetMessage(MessageType theMessage, SocketAddress theAdress, long theTimeStamp){
		message = theMessage;
		address = theAdress;
		timeStamp = theTimeStamp;
	}
}
