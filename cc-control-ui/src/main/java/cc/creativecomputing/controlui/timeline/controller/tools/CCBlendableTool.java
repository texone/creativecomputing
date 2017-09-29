package cc.creativecomputing.controlui.timeline.controller.tools;

import cc.creativecomputing.control.timeline.point.ControlPoint;
import cc.creativecomputing.control.timeline.point.LinearControlPoint;
import cc.creativecomputing.control.timeline.point.StepControlPoint;
import cc.creativecomputing.controlui.timeline.controller.track.CCBlendableTrackController;
import cc.creativecomputing.core.CCBlendable;
import cc.creativecomputing.core.logging.CCLog;

public class CCBlendableTool<Type extends CCBlendable<Type>> extends CCCreateTool{
	
    public CCBlendableTool(CCBlendableTrackController<Type> theController) {
		super(theController);
		_myTool = CCTimelineTools.LINEAR_POINT;
	}
    
	@SuppressWarnings("unchecked")
	public ControlPoint createPoint(ControlPoint myControlPoint) {
		switch(_myTool) {
		case LINEAR_POINT:
    			myControlPoint = new LinearControlPoint(myControlPoint);
    			break;
		case STEP_POINT:
    			myControlPoint = new StepControlPoint(myControlPoint);
    			break;
		default:
			break;
		}
		CCLog.info((Type)_myController.property().value());
		myControlPoint.blendable((Type)_myController.property().value());
		CCLog.info(myControlPoint.blendable());
	    _myController.trackData().add(myControlPoint);
	    _myController.view().render();
	    return myControlPoint;
	}
	
	@Override
	public void onSelection(ControlPoint thePoint) {
		if(thePoint.blendable() == null)return;
		_myController.property().valueCasted(thePoint.blendable() , false);
	}
}
