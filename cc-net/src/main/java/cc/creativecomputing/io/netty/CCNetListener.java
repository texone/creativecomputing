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

/**
 * The {@linkplain CCNetListener} interface is used to register a client to a
 * {@linkplain CCNetIn} object which will be notified when an incoming
 * message was received.
 * <p>
 * Note that these methods are typically called from the {@linkplain CCNetIn} thread
 * which is not the regular OPENGL thread. You may often want to
 * defer actual code to the event thread. You can do this by adding the received
 * messages to a list and invoking the actual code using
 * {@linkplain EventQueue#invokeLater(Runnable)}. This is particularly required when
 * dealing with render processes which require methods to be called in the event
 * thread.
 * 
 * @author christian riekoff
 * 
 * @see CCNetIn
 * @see java.awt.EventQueue#invokeLater(Runnable )
 */
public interface CCNetListener<MessageType> {
	/**
	 * Called when a new message arrived at the receiving local socket.
	 * 
	 * @param theMessage
	 *            the newly arrived and decoded message
	 * @param theSender
	 *            who sent the message
	 * @param theTime
	 *            the time if no time tag was specified or the
	 *            message is expected to be processed immediately
	 */
	public void messageReceived(CCNetMessage<MessageType> theMessage);
}
