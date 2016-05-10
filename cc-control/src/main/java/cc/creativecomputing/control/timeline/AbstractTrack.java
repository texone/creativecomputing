package cc.creativecomputing.control.timeline;

import java.awt.Color;

import cc.creativecomputing.control.handles.CCPropertyHandle;

public abstract class AbstractTrack {

	protected final CCPropertyHandle<?> _myProperty;
	
	private Color _myColor;
	
	private boolean _myMuteFlag;
	
	protected boolean _myDirtyFlag;

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
	
	public Color color() {
		return _myColor;
	}
	
	public void color(Color theColor) {
		_myColor = theColor;
	}
}
