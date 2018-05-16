package cc.creativecomputing.control;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.creativecomputing.core.CCBlendable;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.interpolate.CCInterpolatable;

public class CCGradient extends ArrayList<CCGradientPoint> implements CCBlendable<CCGradient>, CCInterpolatable<CCColor>{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1703488547904473704L;

	public CCGradient(){
		
	}
	
	public void add(double thePosition, CCColor theColor){
		add(new CCGradientPoint(thePosition, theColor.clone()));
	}

	public CCGradient clone(){
		CCGradient myResult = new CCGradient();
		for(CCGradientPoint myPoint:new ArrayList<>(this)){
			myResult.add(myPoint.clone());
		}
		return myResult;
	}
	
	public void set(CCGradient theGradient){
		if(theGradient == this)return;
		clear();
		for(CCGradientPoint myPoint:theGradient){
			add(myPoint.clone());
		}
	}
	
	@Override
	public boolean add(CCGradientPoint e) {
		boolean myResult = super.add(e);
		Collections.sort(this);
		return myResult;
	}
	
	/**
	 * Interpolate a color on the gradient
	 * @param theBlend a value from 0 to 1 that represent the position between the first control point and the last one
	 * @return the position
	 */
	@Override
	public CCColor interpolate (double thePosition){
		
		if(size() == 0)return new CCColor();
		if(size() == 1){
			return get(0).color().clone();
		}
		
		int myIndex = -1;
		
		for(int i = 0; i < size();i++){
			if(get(i).position() > thePosition){
				myIndex = i;
				break;
			}
		}
		
		if(myIndex == -1)return get(size() - 1).color().clone();
		if(myIndex == 0)return get(0).color().clone();
		
		double myPos0 = get(myIndex - 1).position();
		double myPos1 = get(myIndex).position();
		double myBlend = CCMath.smoothStep(myPos0, myPos1, thePosition);
		
		return CCColor.blend(get(myIndex - 1).color(), get(myIndex).color(), myBlend);
	}

	@Deprecated
	/**
	 * Use interpolate instead
	 * @param theValue
	 * @return
	 */
	public CCColor color(double theValue) {
		return interpolate(theValue);
	}

	@Override
	public CCGradient blend(CCGradient theB, double theScalar) {
		if(theScalar <= 0)return  clone();
		if(theScalar >= 1)return theB.clone();
		
		CCGradient myResult = new CCGradient();
		
		for(CCGradientPoint myPoint:this) {
			myResult.add(new CCGradientPoint(myPoint.position(), color(myPoint.position()).blend(theB.color(myPoint.position()), theScalar)));
		}
		for(CCGradientPoint myPoint:theB) {
			myResult.add(new CCGradientPoint(myPoint.position(), color(myPoint.position()).blend(theB.color(myPoint.position()), theScalar)));
		}
		
		return myResult;
	}
	
	@Override
	public Map<String, Object> data() {
		List<Map<String, Object>> myPoints = new ArrayList<>();
		for(CCGradientPoint myPoint:this) {
			Map<String, Object>myPointData = myPoint.color().data();
			myPointData.put("position", myPoint.position());
			myPoints.add(myPointData);
		}
		
		Map<String, Object> myResult = new HashMap<>();
		myResult.put(CCBlendable.BLENDABLE_TYPE_ATTRIBUTE, getClass().getName());
		myResult.put("points", myPoints);
		return myResult;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void data(Map<String, Object> theData) {
		List<Map<String, Object>> myPoints = (List<Map<String, Object>>)theData.get("points");

		for(Map<String, Object> myPointData:myPoints) {
			double myPosition = (Double)myPointData.get("position");
			CCColor myColor = new CCColor();
			myColor.data(myPointData);
			add(new CCGradientPoint(myPosition, myColor));
		}
	}
}
