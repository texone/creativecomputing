package cc.creativecomputing.control.handles;

import cc.creativecomputing.control.CCEnvelope;
import cc.creativecomputing.control.timeline.TrackData;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.util.CCReflectionUtil.CCMember;
import cc.creativecomputing.io.data.CCDataArray;
import cc.creativecomputing.io.data.CCDataObject;

public class CCEnvelopeHandle extends CCPropertyHandle<CCEnvelope>{
	
	protected CCEnvelopeHandle(CCObjectPropertyHandle theParent, CCMember<CCProperty> theMember) {
		super(theParent, theMember);
	}
	
	@Override
	public CCDataObject data() {
		CCDataObject myResult = super.data();
		CCEnvelope myEnvelope = value();
		CCDataArray myArray = myResult.createArray("curves");
		for(String myKey:myEnvelope.curves().keySet()){
			TrackData myCurve = myEnvelope.curves().get(myKey);
			CCDataObject myCurveObject = new CCDataObject();
			myCurveObject.put("key", myKey);
			myCurveObject.put("curve", myCurve.data());
			myArray.add(myCurveObject);
		}
		return myResult;
	}
	
	@Override
	public void data(CCDataObject theData) {
		CCEnvelope myEnvelope = new CCEnvelope();
		myEnvelope.curves().clear();
		CCDataArray myCurvesData = theData.getArray("curves");
		for(int i = 0; i < myCurvesData.size();i++){
			CCDataObject myCurveObject = myCurvesData.getObject(i);
			TrackData myCurve = new TrackData(null);
			myCurve.data(myCurveObject.getObject("curve"));
			String myKey = myCurveObject.getString("key");
			myEnvelope.curves().put(myKey, myCurve);
		}
		value(myEnvelope, true);
	}

	@Override
	public CCEnvelope convertNormalizedValue(double theValue) {
		return null;
	}
	
	@Override
	public double normalizedValue() {
		return 0;
	}

	@Override
	public String valueString() {
		return null;
	}
}