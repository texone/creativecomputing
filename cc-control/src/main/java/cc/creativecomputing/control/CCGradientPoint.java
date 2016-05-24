package cc.creativecomputing.control;

import cc.creativecomputing.math.CCColor;

public class CCGradientPoint implements Comparable<CCGradientPoint>{
	private double _myPosition;
	private final CCColor _myColor;

	public CCGradientPoint(double thePosition, CCColor theColor) {
		_myPosition = thePosition;
		_myColor = theColor;
	}
	
	public double position(){
		return _myPosition;
	}
	
	public void position(double thePosition){
		_myPosition = thePosition;
	}
	
	public CCColor color(){
		return _myColor;
	}
	
	public CCGradientPoint clone(){
		return new CCGradientPoint(_myPosition, _myColor.clone());
	}

	@Override
	public int compareTo(CCGradientPoint o) {
		return new Double(this.position()).compareTo(o.position());
	}
	
	@Override
	public String toString() {
		return _myPosition + " : " + _myColor;
	}
}