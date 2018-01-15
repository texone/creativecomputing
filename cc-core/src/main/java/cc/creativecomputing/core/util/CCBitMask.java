package cc.creativecomputing.core.util;

public class CCBitMask {

	private int _myMask;
	
	public CCBitMask(int theMask){
		_myMask = theMask;
	}
	
	public void setFlag(int theFlag){
		_myMask = _myMask | theFlag;
	}
	
	public boolean isFlagSet(int theFlag){
		return (_myMask & theFlag) == theFlag;
	}
	
	public void flipFlag(int theFlag){
		_myMask = _myMask & ~theFlag;
	}
	
	public void flipAll(){
		_myMask = ~_myMask;
	}
}
