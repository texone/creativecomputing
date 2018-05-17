package cc.creativecomputing.ui.widget;

import cc.creativecomputing.math.CCColor;

public class CCUISeparatorStyle {
	private CCColor _myColor = new CCColor(150);
	private double _myHeight = 10;
	private double _myWeight = 2;
	
	public void color(CCColor theColor){
		_myColor = theColor;
	}
	
	public CCColor color(){
		return _myColor;
	}
	
	public void height(double theHeight){
		_myHeight = theHeight;
	}
	
	public double height(){
		return _myHeight;
	}
	
	public void weight(double theSeparatorWeight){
		_myWeight = theSeparatorWeight;
	}
	
	public double weight(){
		return _myWeight;
	}
}
