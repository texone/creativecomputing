/**
 * Copyright (c) 2008-2012 Ardor Labs, Inc.
 *
 * This file is part of Ardor3D.
 *
 * Ardor3D is free software: you can redistribute it and/or modify it 
 * under the terms of its license which may be found in the accompanying
 * LICENSE file or at <http://www.ardor3d.com/LICENSE>.
 */

package cc.creativecomputing.math;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public abstract class CCLine3Base implements Externalizable {

	protected final CCVector3 _myOrigin = new CCVector3();
	protected final CCVector3 _myDirection = new CCVector3();

	public CCLine3Base(final CCVector3 theOrigin, final CCVector3 theDirection) {
		_myOrigin.set(theOrigin);
		_myDirection.set(theDirection);
	}

	/**
	 * @return this line's origin point as a readable vector
	 */
	public CCVector3 getOrigin() {
		return _myOrigin;
	}

	/**
	 * @return this line's direction as a readable vector
	 */
	public CCVector3 getDirection() {
		return _myDirection;
	}

	/**
	 * Sets the line's origin point to the values of the given vector.
	 * 
	 * @param theOrigin
	 * @throws NullPointerException
	 *             if normal is null.
	 */
	public void setOrigin(final CCVector3 theOrigin) {
		_myOrigin.set(theOrigin);
	}

	/**
	 * Sets the line's direction to the values of the given vector.
	 * 
	 * @param theDirection
	 * @throws NullPointerException
	 *             if direction is null.
	 */
	public void setDirection(final CCVector3 theDirection) {
		_myDirection.set(theDirection);
	}

	/**
	 * @return returns a unique code for this line3base object based on its
	 *         values. If two line3base objects are numerically equal, they will
	 *         return the same hash code value.
	 */
	@Override
	public int hashCode() {
		int result = 17;

		result += 15 * result + _myOrigin.hashCode();
		result += 15 * result + _myDirection.hashCode();

		return result;
	}

	// /////////////////
	// Methods for Externalizable
	// /////////////////

	/**
	 * Used with serialization. Not to be called manually.
	 * 
	 * @param theInput
	 *            ObjectInput
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public void readExternal(final ObjectInput theInput) throws IOException, ClassNotFoundException {
		setOrigin((CCVector3) theInput.readObject());
		setDirection((CCVector3) theInput.readObject());
	}

	/**
	 * Used with serialization. Not to be called manually.
	 * 
	 * @param theOutput
	 *            ObjectOutput
	 * @throws IOException
	 */
	public void writeExternal(final ObjectOutput theOutput) throws IOException {
		theOutput.writeObject(_myOrigin);
		theOutput.writeObject(_myDirection);
	}

}
