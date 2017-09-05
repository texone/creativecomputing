package cc.creativecomputing.kle.elements;

import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.io.xml.CCDataElement;
import cc.creativecomputing.math.CCMath;

public abstract class CCSequenceChannel {

	protected final int _myID;
	
	protected double _myMinimum;
	protected double _myMaximum;
	protected double _myValue;
	
	protected int _myColumn;
	protected int _myRow;
	protected int _myDepth;
	
	protected String _myInterface;
	protected int _myUniverse;
	protected int _myChannel;
	
	public CCSequenceChannel(int theChannelID){
		_myID = theChannelID;
	}
	
	public String interfaceName(){
		return _myInterface;
	}
	
	public void interfaceName(String theInterfaceName){
		_myInterface = theInterfaceName;
	}
	
	public int universe(){
		return _myUniverse;
	}
	
	public void universe(int theUniverse){
		_myUniverse = theUniverse;
	}
	
	public int channel(){
		return _myChannel;
	}
	
	public void channel(int theChannel){
		_myChannel = theChannel;
	}
	
	public int id(){
		return _myID;
	}
	
	public int column(){
		return _myColumn;
	}
	
	public void column(int theColumn){
		_myColumn = theColumn;
	}
	
	public int row(){
		return _myRow;
	}
	
	public void row(int theRow){
		_myRow = theRow;
	}
	
	public int depth(){
		return _myDepth;
	}
	
	public void depth(int theDepth){
		_myDepth = theDepth;
	}
	
	public double min(){
		return _myMinimum;
	}
	
	public void min(double theMin){
		_myMinimum = theMin;
	}
	
	public void max(double theMax){
		_myMaximum = theMax;
	}
	
	public double max(){
		return _myMaximum;
	}
	
	public void value(double theValue){
		_myValue = theValue;
	}
	
	public double normalizedValue(){
		return CCMath.norm(value(), _myMinimum, _myMaximum);
	}
	
	public double value(){
		return _myValue;
	}
	
	public abstract CCDataElement toXML();
	
	public CCDataElement mappingXML(){
		CCDataElement myElement = new CCDataElement("channel");
		myElement.addAttribute("id", _myID);

		myElement.addAttribute("min", _myMinimum);
		myElement.addAttribute("max", _myMaximum);

		myElement.addAttribute("column", _myColumn);
		myElement.addAttribute("row", _myRow);
		myElement.addAttribute("depth", _myDepth);
		return myElement;
	}
}
