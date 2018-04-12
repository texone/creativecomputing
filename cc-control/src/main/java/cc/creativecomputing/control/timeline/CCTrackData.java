/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.control.timeline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import cc.creativecomputing.control.timeline.point.CCBezierControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.control.timeline.point.CCControlPoint.CCControlPointType;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.control.timeline.point.CCLinearControlPoint;
import cc.creativecomputing.control.timeline.point.CCMarkerPoint;
import cc.creativecomputing.control.timeline.point.CCStepControlPoint;
import cc.creativecomputing.control.timeline.point.CCTimedEventPoint;
import cc.creativecomputing.math.CCMath;

public class CCTrackData extends TreeSet<CCControlPoint>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6993915141725326263L;

	private static class TimelineComparator implements Comparator<CCControlPoint> {
		public int compare(CCControlPoint p1, CCControlPoint p2) {
			if (p1.time() < p2.time()) {
				return -1;
			} else if (p1.time() == p2.time()) {
				return 0;
			}
			return 1;
		}
	}

	private int _mySize = 0;
	protected boolean _myDirtyFlag = false;

	public CCTrackData() {
		super(new TimelineComparator());
	}
	
	public void accumulate() {
		
	}
	
	public boolean isDirty() {
		return _myDirtyFlag;
	}
	
	public void setDirty(boolean theFlag) {
		_myDirtyFlag = theFlag;
	}
	
	/**
	 * Reverses the data of the given range
	 */
	public void reverse(double theStartTime, double theEndTime){
		List<CCControlPoint> myRangeCopy = copyRange(theStartTime, theEndTime);
		removeAll(theStartTime, theEndTime);
		
		for(CCControlPoint myPoint:myRangeCopy){
			double myNewTime = theEndTime - myPoint.time() + theStartTime;
			myPoint.time(myNewTime);
			add(myPoint);
		}
	}

	@Override
	/**
	 * @param thePoint
	 */
	public boolean add(CCControlPoint thePoint) {

		_mySize += 1;
		_myDirtyFlag = true;
		
		if (contains(thePoint)) {
			CCControlPoint myTreeLeaf = floor(thePoint);
			if (myTreeLeaf.equals(thePoint)) {
				_mySize -=1;
				_myDirtyFlag = false;
				return false;
			}
			if (thePoint.isPrevious(myTreeLeaf)) {
				insertBefore(myTreeLeaf, thePoint);
			} else {
				myTreeLeaf = getLastOnSamePosition(myTreeLeaf);
				// do not insert into the tree, just update the double linked list
				myTreeLeaf.append(thePoint);
			}
		} else { // new leaf
			super.add(thePoint);
			thePoint.cutLoose();
			CCControlPoint myLower = lower(thePoint);
			if (myLower != null) {
				myLower = getLastOnSamePosition(myLower);
				myLower.append(thePoint);
			}
			CCControlPoint myHigher = higher(thePoint);
			if (myHigher != null) {
				myHigher.prepend(thePoint);
			}
		}
		return true;
	}

	public CCControlPoint createSamplePoint(double theTime) {
		return new CCControlPoint(theTime, 0);
	}

	/**
	 * Returns the least control point with a time greater than or equal to the 
	 * given time, or null if there is no control point after the given time. 
	 * @param theTime
	 * @return
	 */
	public CCControlPoint getFirstPointAfter(double theTime) {
		return ceiling(new CCControlPoint(theTime, 0));
	}
	
	/**
	 * Returns the least control point with a time greater than or equal to the 
	 * given time, or null if there is no control point after the given time. 
	 * @param theTime
	 * @return
	 */
	public CCControlPoint getLastPointBefore(double theTime) {
		return floor(new CCControlPoint(theTime, 0));
	}

	public CCControlPoint getLastOnSamePosition(CCControlPoint thePoint) {
		if (!contains(thePoint)) {
			return thePoint;
		} else {
			CCControlPoint myTreeLeaf = floor(thePoint);
			if(myTreeLeaf == null)return thePoint;
			while (myTreeLeaf.hasNext()) {
				if (myTreeLeaf.next().time() != thePoint.time()) {
					return myTreeLeaf;
				} else {
					myTreeLeaf = myTreeLeaf.next();
				}
			}
			return myTreeLeaf;
		}
	}

//	public CCControlPoint getLastPointAt(double theTime) {
//		return getLastOnSamePosition(ceiling(new CCControlPoint(theTime, 0)));
//	}

	public CCControlPoint getLastPoint() {
		try {
			CCControlPoint myLast = last();
			return getLastOnSamePosition(myLast);
		} catch (NoSuchElementException e) {
			return null;
		}
	}
	
	/**
	 * Returns the time of the last point in the dataset
	 * @return
	 */
	public double getLastTime(){
		CCControlPoint myLastPoint = getLastPoint();
		if (myLastPoint != null) {
			return myLastPoint.time();
		}
		return 0;
	}

	public double value(double theTime) {
		if (size() == 0) {
			return 0.0;
		}
		
		CCControlPoint mySample = createSamplePoint(theTime);
		CCControlPoint myLower = lower(mySample);
		CCControlPoint myCeiling = ceiling(mySample);

		if (myLower == null) {
			if (myCeiling != null) {
				return myCeiling.value();
			}
			return 0.0;
		}

		myLower = getLastOnSamePosition(myLower);

		if (myCeiling != null) {
			return myCeiling.interpolateValue(theTime, this);
		} else {
			if (myLower != null) {
				return myLower.value();
			}
		}
		return 0.0;
	}

	private void insertBefore(CCControlPoint theLocation, CCControlPoint theInsertion) {
		theLocation.prepend(theInsertion);
		// the first element in the double linked list at the same x position
		// becomes the new tree leaf
		if (theLocation.time() == theInsertion.time()) {
			super.remove(theLocation);
			super.add(theInsertion);
		}
	}

	/**
	 * Returns a list of all points in the given range
	 * @param theStartTime
	 * @param theEndTime
	 * @return
	 */
	public ArrayList<CCControlPoint> rangeList(double theStartTime, double theEndTime) {
		ArrayList<CCControlPoint> myRange = new ArrayList<CCControlPoint>();
		
		CCControlPoint myMinPoint = ceiling(new CCControlPoint(theStartTime, 0));

		if (myMinPoint == null || myMinPoint.time() > theEndTime) {
			return myRange;
		}
		CCControlPoint myMaxPoint = floor(new CCControlPoint(theEndTime, 0));

		myMaxPoint = getLastOnSamePosition(myMaxPoint);
		CCControlPoint myCurrentPoint = myMinPoint;

		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
			myRange.add(myCurrentPoint);
			myCurrentPoint = myCurrentPoint.next();
		}
		myRange.add(myMaxPoint);
		return myRange;
	}
	
	

	/**
	 * Returns a list of copied control points inside the given range
	 * @param theStartTime
	 * @param theEndTime
	 * @return
	 */	
	public ArrayList<CCControlPoint> copyRange(double theStartTime, double theEndTime) {
		ArrayList<CCControlPoint> myRange = rangeList(theStartTime, theEndTime);
		ArrayList<CCControlPoint> myCopy = new ArrayList<CCControlPoint>();
		Iterator<CCControlPoint> it = myRange.iterator();

		CCControlPoint myControlPoint = null;
		while (it.hasNext()) {
			CCControlPoint myNext = it.next().clone();
			myNext.cutLoose();
			if (myControlPoint != null) {
				myControlPoint.next(myNext);
				myNext.previous(myControlPoint);
			}
			myCopy.add(myNext);
			myControlPoint = myNext;
		}

		return myCopy;

	}

	public ArrayList<CCControlPoint> rangeList(double theMinValue) {
		ArrayList<CCControlPoint> myRange = new ArrayList<CCControlPoint>();
		CCControlPoint myMinPoint = ceiling(new CCControlPoint(theMinValue, 0));

		if (myMinPoint == null) {
			return myRange;
		}

		CCControlPoint myCurrentPoint = myMinPoint;

		while (myCurrentPoint != null) {
			myRange.add(myCurrentPoint);
			myCurrentPoint = myCurrentPoint.next();
		}
		return myRange;
	}

	
	public boolean remove(CCControlPoint thePoint) {
		boolean myResult = false;
		if (contains(thePoint)) { // we have one ore more points at the location
			CCControlPoint myTreeLeaf = floor(thePoint); // get the tree leaf
			if (myTreeLeaf == thePoint) { // we are removing the leaf
				
				if (myTreeLeaf.hasNext()) { // maybe add a new leaf
					super.remove(myTreeLeaf);
					super.add(myTreeLeaf.next()); // this will replace the old leaf
				} else {
					super.remove(myTreeLeaf);
				}
			}
			myResult = true;
		}
		// update double linked list
		if (thePoint.hasPrevious()) {
			thePoint.previous().next(thePoint.next());
		}
		if (thePoint.hasNext()) {
			thePoint.next().previous(thePoint.previous());
		}
		_mySize -= 1;
		_myDirtyFlag = true;
		assert (_mySize >= 0);
		return myResult;
	}

	public void move(CCControlPoint thePoint, CCControlPoint theTargetLocation) {
		boolean rebuild_search_tree = true;//thePoint.getTime() != theTargetLocation.getTime();
		if (rebuild_search_tree) {
			remove(thePoint);
		}
		thePoint.time(theTargetLocation.time());
		thePoint.value(theTargetLocation.value());
		
		if (rebuild_search_tree) {
			add(thePoint);
		}
	}

	/**
	 * Removes all points in the given range
	 * @param theMinValue
	 * @param theMaxValue
	 */
	public void removeAll(double theMinValue, double theMaxValue) {
		ArrayList<CCControlPoint> myRange = rangeList(theMinValue, theMaxValue);
		if (myRange.size() == 0) {
			return;
		}
		// connect the point before
		CCControlPoint myLower = lower(myRange.get(0));
		CCControlPoint myHigher = higher(myRange.get(myRange.size() - 1));
		if (myLower != null) {
			myLower = getLastOnSamePosition(myLower);
			myLower.next(myHigher);
			if (myHigher != null) {
				myHigher.previous(myLower);
			}
		} else if (myHigher != null) {
			myHigher.previous(null);
		}
		for (CCControlPoint myCurrentPoint : myRange) {
			remove(myCurrentPoint);
		}
	}

	public void replaceAll(double theKey, double theRange, ArrayList<CCControlPoint> theArray) {
		if (theArray.size() == 0) {
			return;
		}
		
		removeAll(theKey, theKey + theRange);
		for (CCControlPoint myPoint : theArray) {
			myPoint.cutLoose();
			CCControlPoint myClone = myPoint.clone();
			myClone.time(myPoint.time() + theKey);
			add(myClone);
		}
	}

	public void insertAll(double theInsertTime, double theRange, ArrayList<CCControlPoint> theArray) {
		if (theArray.size() == 0) {
			return;
		}
		
		insertTime(theInsertTime, theRange);
		replaceAll(theInsertTime, theRange, theArray);
	}
	
	/**
	 * Inserts time into the trackdata, by moving the the points between the
	 * insert time and the time + the range, by the given time
	 * @param theInsertTime
	 * @param theTime
	 * @param theAddRangePoints if this is true points are added to 
	 */
	public void insertTime(double theInsertTime, double theTime, boolean theAddRangePoints) {
		CCControlPoint myCurrentPoint = ceiling(new CCControlPoint(theInsertTime, 0));
		ArrayList<CCControlPoint> myTmpList = new ArrayList<CCControlPoint>();
		while (myCurrentPoint != null) {
			myCurrentPoint.time(myCurrentPoint.time() + theTime);
			myTmpList.add(myCurrentPoint);
			myCurrentPoint = myCurrentPoint.next();
		}
	}
	
	public void insertTime(double theInsertTime, double theTime){
		insertTime(theInsertTime, theTime, false);
	}
	
	public void cutRangeAndTime(double theInsertTime, double theTime){
		removeAll(theInsertTime, theInsertTime + theTime);
		CCControlPoint myCurrentPoint = ceiling(new CCControlPoint(theInsertTime, 0));
		ArrayList<CCControlPoint> myTmpList = new ArrayList<CCControlPoint>();
		double myLastTime = 0;
		while (myCurrentPoint != null) {
			CCControlPoint myPoint = myCurrentPoint.clone();
			myPoint.time(myCurrentPoint.time()- theInsertTime - theTime);
			myLastTime = myCurrentPoint.time();
			myPoint.value(myCurrentPoint.value());
			myTmpList.add(myPoint);
			myCurrentPoint = myCurrentPoint.next();
		}

		removeAll( theInsertTime + theTime, myLastTime);
		replaceAll(theInsertTime,theTime, myTmpList);
	}

	public void cutRange(double theMinValue, double theMaxValue) {
		removeAll(theMinValue, theMaxValue);
		double myRange = theMaxValue - theMinValue;
		CCControlPoint myCurrentPoint = ceiling(new CCControlPoint(theMaxValue, 0));
		while (myCurrentPoint != null) {
			this.move(myCurrentPoint, new CCControlPoint(myCurrentPoint.time() - myRange, myCurrentPoint.value()));
			myCurrentPoint = myCurrentPoint.next();
		}
	}
	
	public void scaleRange(double theStartTime, double theEndTime, double theScale) {
		double myTime = theEndTime - theStartTime;
		double myScaledTime = myTime * theScale;
		
		double myMoveRange = myScaledTime - myTime;
		
		CCControlPoint myCurrentPoint = ceiling(new CCControlPoint(theEndTime, 0));
		while (myCurrentPoint != null) {
			move(myCurrentPoint, new CCControlPoint(myCurrentPoint.time() + myMoveRange, myCurrentPoint.value()));
			myCurrentPoint = myCurrentPoint.next();
		}
		
		ArrayList<CCControlPoint> myRange = rangeList(theStartTime, theEndTime);
		if (myRange.size() == 0) {
			return;
		}
		
		for(CCControlPoint myControlPoint : myRange) {
			myTime = myControlPoint.time() - theStartTime;
			myScaledTime = myTime * theScale;
			myMoveRange = myScaledTime - myTime;
			
			move(myCurrentPoint, new CCControlPoint(myCurrentPoint.time() + myMoveRange, myCurrentPoint.value()));
			myCurrentPoint = myCurrentPoint.next();
		}
	}
	
	public void scaleRange(double theStartTime1, double theEndTime1, double theStartTime2, double theEndTime2) {
		if(theEndTime2 - theStartTime2 > theEndTime1 - theStartTime1){
			//move points at the end
			CCControlPoint myCurrentPoint = ceiling(new CCControlPoint(theEndTime1, 0));
			double myEndMove = theEndTime2 - theEndTime1;
			while (myCurrentPoint != null) {
				move(myCurrentPoint, new CCControlPoint(myCurrentPoint.time() + myEndMove, myCurrentPoint.value()));
				myCurrentPoint = myCurrentPoint.next();
			}
			
			//move points at the start
			myCurrentPoint = floor(new CCControlPoint(theStartTime1, 0));
			double myStartMove = theStartTime2 - theStartTime1;
			while (myCurrentPoint != null) {
				move(myCurrentPoint, new CCControlPoint(myCurrentPoint.time() + myStartMove, myCurrentPoint.value()));
				myCurrentPoint = myCurrentPoint.previous();
			}
			
			ArrayList<CCControlPoint> myRange = rangeList(theStartTime1, theEndTime1);
			
			for(CCControlPoint myControlPoint : myRange) {
				double myNewTime = CCMath.map(myControlPoint.time(), theStartTime1, theEndTime1, theStartTime2, theEndTime2);
				move(myControlPoint, new CCControlPoint(myNewTime, myControlPoint.value()));
				myControlPoint = myControlPoint.next();
			}
		}else{
			ArrayList<CCControlPoint> myRange = rangeList(theStartTime1, theEndTime1);
			
			for(CCControlPoint myControlPoint : myRange) {
				double myNewTime = CCMath.map(myControlPoint.time(), theStartTime1, theEndTime1, theStartTime2, theEndTime2);
				move(myControlPoint, new CCControlPoint(myNewTime, myControlPoint.value()));
				myControlPoint = myControlPoint.next();
			}
			
			//move points at the end
			CCControlPoint myCurrentPoint = ceiling(new CCControlPoint(theEndTime1, 0));
			double myEndMove = theEndTime2 - theEndTime1;
			while (myCurrentPoint != null) {
				move(myCurrentPoint, new CCControlPoint(myCurrentPoint.time() + myEndMove, myCurrentPoint.value()));
				myCurrentPoint = myCurrentPoint.next();
			}
			
			//move points at the start
			myCurrentPoint = floor(new CCControlPoint(theStartTime1, 0));
			double myStartMove = theStartTime2 - theStartTime1;
			while (myCurrentPoint != null) {
				move(myCurrentPoint, new CCControlPoint(myCurrentPoint.time() + myStartMove, myCurrentPoint.value()));
				myCurrentPoint = myCurrentPoint.previous();
			}
		}
		
	}

	public int size() {
		return _mySize;
	}

	public boolean isLeaf(CCControlPoint thePoint) {
		if (contains(thePoint)) {
			CCControlPoint myTreeLeaf = floor(thePoint);
            return myTreeLeaf.equals(thePoint);
		}
		return false;
	}
	
	public static final String TRACKDATA_ELEMENT = "TrackData";
	
	public CCDataObject data(){
		return data(0, getLastTime());
	}
	
	public CCDataObject data(double theStartTime, double theEndTime) {
		CCDataObject myTrackData = new CCDataObject();
		ArrayList<CCControlPoint> myPoints = rangeList(theStartTime, theEndTime);
	
		CCDataArray myPointDataArray = new CCDataArray();
		for (CCControlPoint myPoint:myPoints) {
			CCDataObject myControlPointData = myPoint.data(theStartTime, theEndTime);
			myPointDataArray.add(myControlPointData);
		}
		myTrackData.put("points", myPointDataArray);
		return myTrackData;
	}
	
	private CCControlPoint createPoint(CCDataObject theData){
		CCControlPoint myPoint;
		CCControlPointType myType = CCControlPointType.valueOf(theData.getString(CCControlPoint.CONTROL_POINT_TYPE_ATTRIBUTE));
			
		switch(myType) {
		case STEP:
			myPoint = new CCStepControlPoint();
			break;
		case LINEAR:
			myPoint = new CCLinearControlPoint();
			break;
		case BEZIER:
			myPoint = new CCBezierControlPoint();
			break;
		case MARKER:
			myPoint = new CCMarkerPoint();
			break;
		case TIMED_EVENT:
			myPoint = new CCTimedEventPoint();
			break;
		default:
			myPoint = new CCLinearControlPoint();
		}
		myPoint.data(theData);
		return myPoint;
	}
	
	public void data(CCDataObject theData) {
		if(theData == null)return;
		CCDataArray myPointDataArray = theData.getArray("points");
		for (Object myControlPointObject:myPointDataArray) {
			CCDataObject myControlPointData = (CCDataObject)myControlPointObject;
			add(createPoint(myControlPointData));
		}
		
	}
	
	public void insert(CCDataObject theData, double theTime) {
		CCDataArray myPointDataArray = theData.getArray("points");
		for (Object myControlPointObject:myPointDataArray) {
			CCDataObject myControlPointData = (CCDataObject)myControlPointObject;
			CCControlPoint myPoint = createPoint(myControlPointData);
			myPoint.time(myPoint.time() + theTime);
			add(myPoint);
		}
		
	}
}
