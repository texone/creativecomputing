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
package cc.creativecomputing.math;

/**
 * Axis aligned bounding rectangle similar to a bounding box but for 2D context.
 * @author christian riekoff
 *
 */
public class CCAABoundingRectangle{

	/**
	 * minimum corner of the rectangle
	 */
	private CCVector2 _myMinCorner;
	
	/**
	 * maximum corner of the rectangle
	 */
	private CCVector2 _myMaxCorner;
	
	/**
	 * Creates a new rectangle using the given color and coordinates.
	 * @param theColor
	 * @param theX1
	 * @param theY1
	 * @param theX2
	 * @param theY2
	 */
	public CCAABoundingRectangle(final double theX1, final double theY1, final double theX2, final double theY2){
		double myMinX = CCMath.min(theX1, theX2);
		double myMinY = CCMath.min(theY1, theY2);

		double myMaxX = CCMath.max(theX1, theX2);
		double myMaxY = CCMath.max(theY1, theY2);
		
		_myMinCorner = new CCVector2(myMinX, myMinY);
		_myMaxCorner = new CCVector2(myMaxX, myMaxY);
	}
	
	/**
	 * Creates a new rectangle using the given coordinates.
	 * @param theColor
	 * @param theMinCorner
	 * @param theMaxCorner
	 */
	public CCAABoundingRectangle(final CCVector2 theMinCorner, final CCVector2 theMaxCorner) {
		this(theMinCorner.x, theMinCorner.y, theMaxCorner.x, theMaxCorner.y);
	}
	
	public CCAABoundingRectangle() {
		this(new CCVector2(), new CCVector2());
	}
	
	public void add(final CCAABoundingRectangle theRectangle){
		_myMinCorner.x = CCMath.min(_myMinCorner.x, theRectangle._myMinCorner.x);
		_myMinCorner.y = CCMath.min(_myMinCorner.y, theRectangle._myMinCorner.y);

		_myMaxCorner.x = CCMath.max(_myMaxCorner.x, theRectangle._myMaxCorner.x);
		_myMaxCorner.y = CCMath.max(_myMaxCorner.y, theRectangle._myMaxCorner.y);
	}
	
	/**
	 * Changes the size of the bounding rect so that the given point is inside of it
	 * @param theX x coord of the point
	 * @param theY y coord of the point
	 */
	public void add(final double theX, final double theY){
		_myMinCorner.x = CCMath.min(_myMinCorner.x, theX);
		_myMinCorner.y = CCMath.min(_myMinCorner.y, theY);

		_myMaxCorner.x = CCMath.max(_myMaxCorner.x, theX);
		_myMaxCorner.y = CCMath.max(_myMaxCorner.y, theY);
	}
	
	/**
	 * Changes the size of the bounding rect so that the given point is inside of it
	 * @param thePoint the point
	 */
	public void add(final CCVector2 thePoint){
		add(thePoint.x, thePoint.y);
	}
	
	/**
	 * Checks if the given position is inside this rectangle
	 * @param thePosition the position to check
	 * @return <code>true</code> if the given position is inside the rectangle, otherwise <code>false</code>
	 */
	public boolean isInside(final CCVector2 thePosition) {
		return isInside(thePosition.x, thePosition.y);
	}
	
	/**
	 * Checks if the given position is inside this rectangle
	 * @param theX x coord of the position to check
	 * @param theY y coord of the position to check
	 * @return <code>true</code> if the given position is inside the rectangle, otherwise <code>false</code>
	 */
	public boolean isInside(final double theX, final double theY) {
		return 
			theX > _myMinCorner.x && 
			theX < _myMaxCorner.x &&
			theY > _myMinCorner.y && 
			theY < _myMaxCorner.y;
	}
	
	/**
	 * Checks if the given bounding rect collides with this bounding rect.
	 * @param theBounds
	 * @return
	 */
	public boolean isColliding(final CCAABoundingRectangle theBounds) {
		return 
			theBounds.min().x < max().x &&
			theBounds.max().x > min().x &&
			theBounds.min().y < max().y &&
			theBounds.max().y > min().y;
			
	}
	
	/**
	 * Moves the rectangle to the given position
	 * @param theX 
	 * @param theY
	 */
	public void position(final double theX, final double theY) {
		double myWidth = width();
		double myHeight = height();
		_myMinCorner.set(theX, theY);
		_myMaxCorner.set(theX, theY);
		_myMaxCorner.add(myWidth, myHeight);
	}
	
	/**
	 * Moves the rectangle to the given position
	 * @param theX 
	 * @param theY
	 */
	public void position(final CCVector2 thePosition) {
		position(thePosition.x, thePosition.y);
	}
	
	/**
	 * Returns the x coord of the position.
	 * @return x coord of the position.
	 */
	public double x() {
		return _myMinCorner.x;
	}
	
	/**
	 * Returns the y coord of the position.
	 * @return y coord of the position.
	 */
	public double y() {
		return _myMinCorner.y;
	}
	
	/**
	 * Returns the width of the rectangle
	 * @return
	 */
	public double width() {
		return _myMaxCorner.x - _myMinCorner.x;
	}
	
	/**
	 * Sets the width of the rectangle. Changing the width of the
	 * rectangle causes a change of the maximum (upper right) corner.
	 * @param theWidth the new width
	 */
	public void width(final double theWidth){
		_myMaxCorner.x = _myMinCorner.x;
		_myMaxCorner.addLocal(theWidth,0);
	}
	
	/**
	 * Returns the height of the rectangle
	 * @return
	 */
	public double height() {
		return _myMaxCorner.y - _myMinCorner.y;
	}
	
	/**
	 * Sets the height of the rectangle. Changing the height of the
	 * rectangle causes a change of the maximum (upper right) corner.
	 * @param theHeight the new height
	 */
	public void height(final double theHeight){
		_myMaxCorner.y = _myMinCorner.y;
		_myMaxCorner.addLocal(0, theHeight);
	}
	
	/**
	 * Returns the width and height of the rectangle as vector.
	 * @return width and height of the rectangle
	 */
	public CCVector2 size() {
		return _myMaxCorner.subtract(_myMinCorner);
	}
	
	/**
	 * Returns the minimum corner (lower left) of the rectangle.
	 * @return minimum corner
	 */
	public CCVector2 min() {
		return _myMinCorner;
	}
	
	/**
	 * Returns the maximum corner (upper right) of the rectangle.
	 * @return maximum corner
	 */
	public CCVector2 max() {
		return _myMaxCorner;
	}
	
	/**
	 * Returns the center of the rectangle
	 * @return center of the rectangle
	 */
	public CCVector2 center(){
		return _myMinCorner.add(_myMaxCorner).multiplyLocal(0.5f);
	}
	
	/**
	 * Returns a copy of the rectangle
	 */
	public CCAABoundingRectangle clone() {
		return new CCAABoundingRectangle(_myMinCorner.clone(), _myMaxCorner.clone());
	}
	
	/**
	 * Returns a string representation of the rectangle
	 */
	public String toString() {
		StringBuilder myStringBuilder = new StringBuilder("CCRectangle:\n");
		myStringBuilder.append("minCorner:");
		myStringBuilder.append(_myMinCorner.toString());
		myStringBuilder.append("\n");
		myStringBuilder.append("maxCorner:");
		myStringBuilder.append(_myMaxCorner.toString());
		myStringBuilder.append("\n");
		return myStringBuilder.toString();
	}

	/**
	 * moves 
	 */
	public void translate(double theX, double theY) {
		_myMinCorner.addLocal(theX, theY);
		_myMaxCorner.addLocal(theX, theY);
	}
	
	public void scale(double theXscale, double theYscale) {
		_myMinCorner.multiplyLocal(theXscale, theYscale);
		_myMaxCorner.multiplyLocal(theXscale, theYscale);
	}
}
