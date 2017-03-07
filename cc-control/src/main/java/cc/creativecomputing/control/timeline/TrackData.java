package cc.creativecomputing.control.timeline;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.TreeSet;

import cc.creativecomputing.control.timeline.point.BezierControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.ControlPoint.ControlPointType;
import cc.creativecomputing.control.timeline.point.LinearControlPoint;
import cc.creativecomputing.control.timeline.point.MarkerPoint;
import cc.creativecomputing.control.timeline.point.StepControlPoint;
import cc.creativecomputing.control.timeline.point.TimedEventPoint;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataObject;
import cc.creativecomputing.math.CCMath;

public class TrackData extends TreeSet<ControlPoint>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6993915141725326263L;

	private static class TimelineComparator implements Comparator<ControlPoint> {
		public int compare(ControlPoint p1, ControlPoint p2) {
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
	
	protected Track _myTrack;

	public TrackData(Track theTrack) {
		super(new TimelineComparator());
		_myTrack = theTrack;
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
		List<ControlPoint> myRangeCopy = copyRange(theStartTime, theEndTime);
		removeAll(theStartTime, theEndTime);
		
		for(ControlPoint myPoint:myRangeCopy){
			double myNewTime = theEndTime - myPoint.time() + theStartTime;
			myPoint.time(myNewTime);
			add(myPoint);
		}
	}

	@Override
	/**
	 * @param thePoint
	 */
	public boolean add(ControlPoint thePoint) {

		_mySize += 1;
		_myDirtyFlag = true;
		
		if (contains(thePoint)) {
			ControlPoint myTreeLeaf = floor(thePoint);
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
			ControlPoint myLower = lower(thePoint);
			if (myLower != null) {
				myLower = getLastOnSamePosition(myLower);
				myLower.append(thePoint);
			}
			ControlPoint myHigher = higher(thePoint);
			if (myHigher != null) {
				myHigher.prepend(thePoint);
			}
		}
		return true;
	}

	public ControlPoint createSamplePoint(double theTime) {
		return new ControlPoint(theTime, 0);
	}

	/**
	 * Returns the least control point with a time greater than or equal to the 
	 * given time, or null if there is no control point after the given time. 
	 * @param theTime
	 * @return
	 */
	public ControlPoint getFirstPointAt(double theTime) {
		return ceiling(new ControlPoint(theTime, 0));
	}

	public ControlPoint getLastOnSamePosition(ControlPoint thePoint) {
		if (!contains(thePoint)) {
			return thePoint;
		} else {
			ControlPoint myTreeLeaf = floor(thePoint);
			if(myTreeLeaf == null)return thePoint;
			while (myTreeLeaf.hasNext()) {
				if (myTreeLeaf.getNext().time() != thePoint.time()) {
					return myTreeLeaf;
				} else {
					myTreeLeaf = myTreeLeaf.getNext();
				}
			}
			return myTreeLeaf;
		}
	}

	public ControlPoint getLastPointAt(double theTime) {
		return getLastOnSamePosition(ceiling(new ControlPoint(theTime, 0)));
	}

	public ControlPoint getLastPoint() {
		try {
			ControlPoint myLast = last();
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
		ControlPoint myLastPoint = getLastPoint();
		if (myLastPoint != null) {
			return myLastPoint.time();
		}
		return 0;
	}

    /**
     * Returns the accumulated value for the given time
     * @param theTime
     * @return
     */
    public double getAccumulatedValue(double theTime) {
    	return value(theTime);
    }

	public double value(double theTime) {
		if (size() == 0) {
			return 0.0;
		}
		
		ControlPoint mySample = createSamplePoint(theTime);
		ControlPoint myLower = lower(mySample);
		ControlPoint myCeiling = ceiling(mySample);

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

	private void insertBefore(ControlPoint theLocation, ControlPoint theInsertion) {
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
	public ArrayList<ControlPoint> rangeList(double theStartTime, double theEndTime) {
		ArrayList<ControlPoint> myRange = new ArrayList<ControlPoint>();
		
		ControlPoint myMinPoint = ceiling(new ControlPoint(theStartTime, 0));

		if (myMinPoint == null || myMinPoint.time() > theEndTime) {
			return myRange;
		}
		ControlPoint myMaxPoint = floor(new ControlPoint(theEndTime, 0));

		myMaxPoint = getLastOnSamePosition(myMaxPoint);
		ControlPoint myCurrentPoint = myMinPoint;

		while (myCurrentPoint != null && myCurrentPoint != myMaxPoint) {
			myRange.add(myCurrentPoint);
			myCurrentPoint = myCurrentPoint.getNext();
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
	public ArrayList<ControlPoint> copyRange(double theStartTime, double theEndTime) {
		ArrayList<ControlPoint> myRange = rangeList(theStartTime, theEndTime);
		ArrayList<ControlPoint> myCopy = new ArrayList<ControlPoint>();
		Iterator<ControlPoint> it = myRange.iterator();

		ControlPoint myControlPoint = null;
		while (it.hasNext()) {
			ControlPoint myNext = it.next().clone();
			myNext.cutLoose();
			if (myControlPoint != null) {
				myControlPoint.setNext(myNext);
				myNext.setPrevious(myControlPoint);
			}
			myCopy.add(myNext);
			myControlPoint = myNext;
		}

		return myCopy;

	}

	public ArrayList<ControlPoint> rangeList(double theMinValue) {
		ArrayList<ControlPoint> myRange = new ArrayList<ControlPoint>();
		ControlPoint myMinPoint = ceiling(new ControlPoint(theMinValue, 0));

		if (myMinPoint == null) {
			return myRange;
		}

		ControlPoint myCurrentPoint = myMinPoint;

		while (myCurrentPoint != null) {
			myRange.add(myCurrentPoint);
			myCurrentPoint = myCurrentPoint.getNext();
		}
		return myRange;
	}

	
	public boolean remove(ControlPoint thePoint) {
		boolean myResult = false;
		if (contains(thePoint)) { // we have one ore more points at the location
			ControlPoint myTreeLeaf = floor(thePoint); // get the tree leaf
			if (myTreeLeaf == thePoint) { // we are removing the leaf
				
				if (myTreeLeaf.hasNext()) { // maybe add a new leaf
					super.remove(myTreeLeaf);
					super.add(myTreeLeaf.getNext()); // this will replace the old leaf
				} else {
					super.remove(myTreeLeaf);
				}
			}
			myResult = true;
		}
		// update double linked list
		if (thePoint.hasPrevious()) {
			thePoint.getPrevious().setNext(thePoint.getNext());
		}
		if (thePoint.hasNext()) {
			thePoint.getNext().setPrevious(thePoint.getPrevious());
		}
		_mySize -= 1;
		_myDirtyFlag = true;
		assert (_mySize >= 0);
		return myResult;
	}

	public void move(ControlPoint thePoint, ControlPoint theTargetLocation) {
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
		ArrayList<ControlPoint> myRange = rangeList(theMinValue, theMaxValue);
		if (myRange.size() == 0) {
			return;
		}
		// connect the point before
		ControlPoint myLower = lower(myRange.get(0));
		ControlPoint myHigher = higher(myRange.get(myRange.size() - 1));
		if (myLower != null) {
			myLower = getLastOnSamePosition(myLower);
			myLower.setNext(myHigher);
			if (myHigher != null) {
				myHigher.setPrevious(myLower);
			}
		} else if (myHigher != null) {
			myHigher.setPrevious(null);
		}
		for (ControlPoint myCurrentPoint : myRange) {
			remove(myCurrentPoint);
		}
	}

	public void replaceAll(double theKey, double theRange, ArrayList<ControlPoint> theArray) {
		if (theArray.size() == 0) {
			return;
		}
		
		removeAll(theKey, theKey + theRange);
		for (ControlPoint myPoint : theArray) {
			myPoint.cutLoose();
			ControlPoint myClone = myPoint.clone();
			myClone.time(myPoint.time() + theKey);
			add(myClone);
		}
	}

	public void insertAll(double theInsertTime, double theRange, ArrayList<ControlPoint> theArray) {
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
		ControlPoint myCurrentPoint = ceiling(new ControlPoint(theInsertTime, 0));
		ArrayList<ControlPoint> myTmpList = new ArrayList<ControlPoint>();
		while (myCurrentPoint != null) {
			myCurrentPoint.time(myCurrentPoint.time() + theTime);
			myTmpList.add(myCurrentPoint);
			myCurrentPoint = myCurrentPoint.getNext();
		}
	}
	
	public void insertTime(double theInsertTime, double theTime){
		insertTime(theInsertTime, theTime, false);
	}
	
	public void cutRangeAndTime(double theInsertTime, double theTime){
		removeAll(theInsertTime, theInsertTime + theTime);
		ControlPoint myCurrentPoint = ceiling(new ControlPoint(theInsertTime, 0));
		ArrayList<ControlPoint> myTmpList = new ArrayList<ControlPoint>();
		double myLastTime = 0;
		while (myCurrentPoint != null) {
			ControlPoint myPoint = myCurrentPoint.clone();
			myPoint.time(myCurrentPoint.time()- theInsertTime - theTime);
			myLastTime = myCurrentPoint.time();
			myPoint.value(myCurrentPoint.value());
			myTmpList.add(myPoint);
			myCurrentPoint = myCurrentPoint.getNext();
		}

		removeAll( theInsertTime + theTime, myLastTime);
		replaceAll(theInsertTime,theTime, myTmpList);
	}

	public void cutRange(double theMinValue, double theMaxValue) {
		removeAll(theMinValue, theMaxValue);
		double myRange = theMaxValue - theMinValue;
		ControlPoint myCurrentPoint = ceiling(new ControlPoint(theMaxValue, 0));
		while (myCurrentPoint != null) {
			this.move(myCurrentPoint, new ControlPoint(myCurrentPoint.time() - myRange, myCurrentPoint.value()));
			myCurrentPoint = myCurrentPoint.getNext();
		}
	}
	
	public void scaleRange(double theStartTime, double theEndTime, double theScale) {
		double myTime = theEndTime - theStartTime;
		double myScaledTime = myTime * theScale;
		
		double myMoveRange = myScaledTime - myTime;
		
		ControlPoint myCurrentPoint = ceiling(new ControlPoint(theEndTime, 0));
		while (myCurrentPoint != null) {
			move(myCurrentPoint, new ControlPoint(myCurrentPoint.time() + myMoveRange, myCurrentPoint.value()));
			myCurrentPoint = myCurrentPoint.getNext();
		}
		
		ArrayList<ControlPoint> myRange = rangeList(theStartTime, theEndTime);
		if (myRange.size() == 0) {
			return;
		}
		
		for(ControlPoint myControlPoint : myRange) {
			myTime = myControlPoint.time() - theStartTime;
			myScaledTime = myTime * theScale;
			myMoveRange = myScaledTime - myTime;
			
			move(myCurrentPoint, new ControlPoint(myCurrentPoint.time() + myMoveRange, myCurrentPoint.value()));
			myCurrentPoint = myCurrentPoint.getNext();
		}
	}
	
	public void scaleRange(double theStartTime1, double theEndTime1, double theStartTime2, double theEndTime2) {
		if(theEndTime2 - theStartTime2 > theEndTime1 - theStartTime1){
			//move points at the end
			ControlPoint myCurrentPoint = ceiling(new ControlPoint(theEndTime1, 0));
			double myEndMove = theEndTime2 - theEndTime1;
			while (myCurrentPoint != null) {
				move(myCurrentPoint, new ControlPoint(myCurrentPoint.time() + myEndMove, myCurrentPoint.value()));
				myCurrentPoint = myCurrentPoint.getNext();
			}
			
			//move points at the start
			myCurrentPoint = floor(new ControlPoint(theStartTime1, 0));
			double myStartMove = theStartTime2 - theStartTime1;
			while (myCurrentPoint != null) {
				move(myCurrentPoint, new ControlPoint(myCurrentPoint.time() + myStartMove, myCurrentPoint.value()));
				myCurrentPoint = myCurrentPoint.getPrevious();
			}
			
			ArrayList<ControlPoint> myRange = rangeList(theStartTime1, theEndTime1);
			
			for(ControlPoint myControlPoint : myRange) {
				double myNewTime = CCMath.map(myControlPoint.time(), theStartTime1, theEndTime1, theStartTime2, theEndTime2);
				move(myControlPoint, new ControlPoint(myNewTime, myControlPoint.value()));
				myControlPoint = myControlPoint.getNext();
			}
		}else{
			ArrayList<ControlPoint> myRange = rangeList(theStartTime1, theEndTime1);
			
			for(ControlPoint myControlPoint : myRange) {
				double myNewTime = CCMath.map(myControlPoint.time(), theStartTime1, theEndTime1, theStartTime2, theEndTime2);
				move(myControlPoint, new ControlPoint(myNewTime, myControlPoint.value()));
				myControlPoint = myControlPoint.getNext();
			}
			
			//move points at the end
			ControlPoint myCurrentPoint = ceiling(new ControlPoint(theEndTime1, 0));
			double myEndMove = theEndTime2 - theEndTime1;
			while (myCurrentPoint != null) {
				move(myCurrentPoint, new ControlPoint(myCurrentPoint.time() + myEndMove, myCurrentPoint.value()));
				myCurrentPoint = myCurrentPoint.getNext();
			}
			
			//move points at the start
			myCurrentPoint = floor(new ControlPoint(theStartTime1, 0));
			double myStartMove = theStartTime2 - theStartTime1;
			while (myCurrentPoint != null) {
				move(myCurrentPoint, new ControlPoint(myCurrentPoint.time() + myStartMove, myCurrentPoint.value()));
				myCurrentPoint = myCurrentPoint.getPrevious();
			}
		}
		
	}

	public int size() {
		return _mySize;
	}

	public boolean isLeaf(ControlPoint thePoint) {
		if (contains(thePoint)) {
			ControlPoint myTreeLeaf = floor(thePoint);
			if (myTreeLeaf.equals(thePoint)) {
				return true;
			}
		}
		return false;
	}
	
	public static final String TRACKDATA_ELEMENT = "TrackData";
	
	public CCDataObject data(){
		return data(0, getLastTime());
	}
	
	public CCDataObject data(double theStartTime, double theEndTime) {
		CCDataObject myTrackData = new CCDataObject();
		ArrayList<ControlPoint> myPoints = rangeList(theStartTime, theEndTime);
	
		CCDataArray myPointDataArray = new CCDataArray();
		for (ControlPoint myPoint:myPoints) {
			CCDataObject myControlPointData = myPoint.data(theStartTime, theEndTime);
			myPointDataArray.add(myControlPointData);
		}
		myTrackData.put("points", myPointDataArray);
		return myTrackData;
	}
	
	private ControlPoint createPoint(CCDataObject theData){
		ControlPoint myPoint;
		ControlPointType myType = ControlPointType.valueOf(theData.getString(ControlPoint.CONTROL_POINT_TYPE_ATTRIBUTE));
			
		switch(myType) {
		case STEP:
			myPoint = new StepControlPoint();
			break;
		case LINEAR:
			myPoint = new LinearControlPoint();
			break;
		case BEZIER:
			myPoint = new BezierControlPoint();
			break;
		case MARKER:
			myPoint = new MarkerPoint();
			break;
		case TIMED_EVENT:
			myPoint = new TimedEventPoint();
			break;
		default:
			myPoint = new LinearControlPoint();
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
			ControlPoint myPoint = createPoint(myControlPointData);
			myPoint.time(myPoint.time() + theTime);
			add(myPoint);
		}
		
	}
}
