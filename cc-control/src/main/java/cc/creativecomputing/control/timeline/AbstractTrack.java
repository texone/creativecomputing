package cc.creativecomputing.control.timeline;

import java.awt.Color;

import cc.creativecomputing.control.handles.CCPropertyHandle;

public abstract class AbstractTrack {

	protected final CCPropertyHandle<?> _myProperty;
	
	private Color _myColor;
	
	private boolean _myMuteFlag;
	
	protected boolean _myDirtyFlag;
	
	protected double _myMin = 0;
	protected double _myMax = 1;

	public AbstractTrack(CCPropertyHandle<?> theProperty) {
		_myMuteFlag = false;
		_myProperty = theProperty;
		_myColor = Color.lightGray;
	}
	
	public CCPropertyHandle<?> property() {
		return _myProperty;
	}
	
	public void mute( boolean theFlag ) {
		_myProperty.mute(theFlag);
		_myMuteFlag = theFlag;
	}
	
	public boolean mute() {
		return _myMuteFlag;
	}
	
	public double min(){
		return _myMin;
	}
	
	public void min(double theMin){
		_myMin = theMin;
	}
	
	public double max(){
		return _myMax;
	}
	
	public void max(double theMax){
		_myMax = theMax;
	}
	
	public Color color() {
		return _myColor;
	}
	
	public void color(Color theColor) {
		_myColor = theColor;
	}
}
