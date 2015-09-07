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
package cc.creativecomputing.io.net;

import java.net.InetSocketAddress;
import java.nio.channels.SelectableChannel;

import cc.creativecomputing.io.net.codec.CCNetPacketCodec;

/**
 * An interface describing common functionality in bidirectional network communicators.
 * 
 * @author Christian Riekoff
 */
public abstract class CCNetBidirectionalChannel<ChannelType extends SelectableChannel, MessageType> extends CCNetChannel<ChannelType, MessageType> {
	
	public CCNetBidirectionalChannel(CCNetPacketCodec<MessageType> theCodec, InetSocketAddress theLocalAddress) {
		super(theCodec, theLocalAddress);
	}
	
	/**
	 * Starts the communicator.
	 */
	public abstract void start();

	/**
	 * Checks whether the communicator is active (was started) or not (is stopped).
	 * 
	 * @return <code>true</code> if the communicator is active, <code>false</code> otherwise
	 */
	public abstract boolean isActive();

	/**
	 * Stops the communicator.
	 */
	public abstract void stop();
}
