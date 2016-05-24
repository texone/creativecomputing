package cc.creativecomputing.control;

import java.util.ArrayList;
import java.util.Collections;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCGradient extends ArrayList<CCGradientPoint>{
	
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
	 * Interpolate a position on the spline
	 * @param theBlend a value from 0 to 1 that represent the position between the first control point and the last one
	 * @return the position
	 */
	public CCColor color (double thePosition){
		
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
}
