package cc.creativecomputing.controlui.timeline.controller;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.control.timeline.Selection;
import cc.creativecomputing.control.timeline.TimeRange;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.controlui.timeline.controller.actions.CutAction;
import cc.creativecomputing.controlui.timeline.controller.track.TrackController;
import cc.creativecomputing.controlui.timeline.view.track.SwingAbstractTrackView;
import cc.creativecomputing.controlui.util.UndoHistory;
import cc.creativecomputing.math.CCMath;


public class SelectionController extends TimeRangeController{
	
	private TrackController _myTrackDataController;
	private Selection _mySelection;
	
	private ArrayList<ControlPoint> _myLastSelection;
	private double _myLastLowerBound;
	private double _myLastUpperBound;
	
	public SelectionController(TrackContext theTrackContext) {
		super(theTrackContext);
		_mySelection = new Selection(0,0);
		_myTimeRange = _mySelection;

		_myLastSelection = new ArrayList<ControlPoint>();
	}
	
	public void assignTrackData(TrackController theController) {
		if (_myTrackDataController != null) {
			_myTrackDataController.clearSelection();
		}
		_myTrackDataController = theController;
		_myTransportView = _myTrackDataController;
		_myTrackDataController.selection(_mySelection);
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.controller.TimeRangeController#mouseReleased(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseReleased(MouseEvent theE) {
		super.mouseReleased(theE);
		_myTrackDataController.view().render();
	}
	
	private List<ControlPoint> _myMoveList;
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.controller.TimeRangeController#mousePressed(java.awt.event.MouseEvent)
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		super.mousePressed(e);
		
		_myMoveList = null;
		
		boolean _myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		if(_myPressedShift) {
			switch(_myLoopAction) {
			case MOVE_BOTH:
				_myMoveList = _myTrackDataController.trackData().rangeList(_myLoopStart, _myLoopEnd);
				break;
			default:
				break;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see de.artcom.timeline.controller.TimeRangeController#mouseDragged(java.awt.event.MouseEvent)
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		super.mouseDragged(e);
		boolean _myPressedShift = (e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) == MouseEvent.SHIFT_DOWN_MASK;
		if(_myPressedShift) {
			switch(_myLoopAction) {
			case MOVE_END:
				_myTrackDataController.trackData().scaleRange(_myLoopStart, _myLoopEnd, _myNewLoopStart, _myNewLoopEnd);
				break;
			case MOVE_BOTH:
				if(_myMoveList == null)break;
				for(ControlPoint myPoint :_myMoveList) {
					_myTrackDataController.trackData().move(myPoint, new ControlPoint(myPoint.time() + (_myNewLoopStart - _myLoopStart),myPoint.value()));
				}
				break;
			default:
				break;
			}
		}
	}
	
	public void mouseMoved(MouseEvent e){
		double myCurveX = _myTransportView.viewXToTime(e.getX(), true);
		
		int myLoopStart = _myTransportView.timeToViewX(_myTimeRange.start());
		int myLoopEnd = _myTransportView.timeToViewX(_myTimeRange.end());
		
		if(CCMath.abs(myLoopStart - e.getX()) < 5) {
			_myTrackDataController.view().moveRangeCursor();
		}else if(CCMath.abs(myLoopEnd - e.getX()) < 5) {
			_myTrackDataController.view().moveRangeCursor();
		}else if(myCurveX > _myTimeRange.start() && myCurveX < _myTimeRange.end()){
			_myTrackDataController.view().moveCursor();
		}else{
			_myTrackDataController.view().defaultCursor();
		}
		
	}
	
	public void clear() {
		_myTimeRange.range(0,0);
	}

	public void copy() {
		TrackData myTrackData = _myTrackDataController.trackData();
		
//		double myLowValue = myTrackData.getValue(_myTimeRange.start());
//		double myHighValue = myTrackData.getValue(_myTimeRange.end());
		
		ArrayList<ControlPoint> myRange = myTrackData.copyRange(_myTimeRange.start(), _myTimeRange.end());
		_myLastSelection = new ArrayList<ControlPoint>();
		_myLastLowerBound = _myTimeRange.start();
		_myLastUpperBound = _myTimeRange.end();
//		_myLastSelection.add(new ControlPoint(_myTimeRange.start(), myLowValue));
		_myLastSelection.addAll(myRange);
//		_myLastSelection.add(new ControlPoint(_myTimeRange.end(), myHighValue));
	}

	
	public void cut() {
		copy();
		
		TrackData myTrackData = _myTrackDataController.trackData();
		
		ArrayList<ControlPoint> myRange = myTrackData.copyRange(_myTimeRange.start(), _myTimeRange.end());	
		myTrackData.removeAll(_myTimeRange.start(), _myTimeRange.end() );
		
		UndoHistory.instance().apply(new CutAction(_myTrackDataController, myRange, _myTimeRange.clone()));
		_myTrackDataController.view().render();
	}
	
	public void cutTime(SwingAbstractTrackView theTimelinePanel) {
		TrackData myModel = _myTrackDataController.trackData();
		_myLastSelection = myModel.copyRange(_myTimeRange.start(), _myTimeRange.end());
		myModel.cutRange(_myTimeRange.start(), _myTimeRange.end());
		_myTrackDataController.view().render();
	}
	
	public void insert() {
		if (_myLastSelection.size() == 0) {
			return;
		}

		TrackData myModel = _myTrackDataController.trackData();
		
		ArrayList<ControlPoint> myInsertion = prepareInsertion(_myTimeRange.start(), 0,  myModel);
		
		// insert last selection at lower bound of current selection
		myModel.insertAll(_myTimeRange.start(),_myTimeRange.length(), myInsertion);
		_myTrackDataController.view().render();
	}
	
	public ArrayList<ControlPoint> prepareInsertion(double theInsertionPoint, double theRange, TrackData theModel) {
		
//		double myLowValue = theModel.getValue(theInsertionPoint);
//		double myHighValue = theModel.getValue(theInsertionPoint + theRange);
		
		// first we need a copy of our last selected points
		ArrayList<ControlPoint> myInsertion = new ArrayList<ControlPoint>();
		for (ControlPoint myControlPoint:_myLastSelection) {
			myInsertion.add(myControlPoint.clone());
		}
		
		// then add the first and the last point (
//		myInsertion.add(0, new ControlPoint(_myLastLowerBound, myLowValue));
//		myInsertion.add(new ControlPoint(_myLastUpperBound, myHighValue));
		
		for (ControlPoint myPoint : myInsertion) {
			myPoint.time(myPoint.time()-_myLastLowerBound);
		}		
		
		return myInsertion;
	}
	
	public void replace() {
		if (_myLastSelection.size() == 0) {
			return;
		}
		TrackData myModel = _myTrackDataController.trackData();

		double myRange = _myLastUpperBound - _myLastLowerBound;
		ArrayList<ControlPoint> myInsertion = prepareInsertion(_myTimeRange.start(), myRange, myModel);
		
		myModel.replaceAll(_myTimeRange.start(),myRange, myInsertion);
		_myTrackDataController.view().render();
	}
	
	public TimeRange range() {
		return _myTimeRange;
	}
}
