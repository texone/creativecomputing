package cc.creativecomputing.io.netty.codec.osc;

import java.util.ArrayList;
import java.util.List;

/**
 * An OSC message consists of an OSC Address Pattern followed by an OSC Type Tag
 * String followed by zero or more OSC Arguments.
 * 
 */
public class CCOSCMessage implements CCOSCPacket {
	public static final int POOL_INITIAL_CAPACITY = 32;

	private String _myAddress;
	private List<Object> _myArguments;

	public CCOSCMessage(String theAddress, Object...theArguments) {
		_myArguments = new ArrayList<>();
		_myAddress = theAddress;
		
		for(Object myArgument:theArguments){
			addArgument(myArgument);	
		}
	}

	/**
	 * Reset attributes.
	 * 
	 */
	@Override
	public void reset() {
		_myArguments.clear();
		_myAddress = "";
	}

	public String address() {
		return _myAddress;
	}

	public void addArgument(Object theArgument) {
		_myArguments.add(theArgument);
	}

	public void setAddress(String theAddress) {
		_myAddress = theAddress;
	}

	public List<Object> arguments() {
		return _myArguments;
	}
	
	public byte[] blobArgument(final int theIndex) {
		return (byte[])_myArguments.get(theIndex);
	}
	
	public boolean booleanArgument(final int theIndex){
		return (Boolean)_myArguments.get(theIndex);
	}
	
	public int intArgument(final int theIndex){
		return (Integer)_myArguments.get(theIndex);
	}
	
	public float floatArgument(final int theIndex){
		return (Float)_myArguments.get(theIndex);
	}
	
	public double doubleArgument(final int theIndex){
		return (Double)_myArguments.get(theIndex);
	}
	
	public String stringArgument(final int theIndex) {
		return (String)_myArguments.get(theIndex);
	}
	
	public List<Float> floatArguments(final int theIndex){
		final List<Float> myResultList = new ArrayList<Float>();
		for(int i = theIndex; i < _myArguments.size();i++){
			if(_myArguments.get(i) instanceof Float){
				myResultList.add((Float)_myArguments.get(i));
			}else{
				break;
			}
		}
		return myResultList;
	}

	
	public List<Integer> intArguments(final int theIndex){
		final List<Integer> myResultList = new ArrayList<Integer>();
		for(int i = theIndex; i < _myArguments.size();i++){
			if(_myArguments.get(i) instanceof Integer){
				myResultList.add((Integer)_myArguments.get(i));
			}else{
				break;
			}
		}
		return myResultList;
	}

	/**
	 * For debug only.
	 */
	@Override
	public String toString() {
		String message = _myAddress;

		int size = _myArguments.size();
		for (int i = 0; i < size; i++) {
			message += _myArguments.get(i) + ",";
		}

		return message;
	}

}