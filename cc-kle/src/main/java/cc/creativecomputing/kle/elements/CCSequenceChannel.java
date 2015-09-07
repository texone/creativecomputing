package cc.creativecomputing.kle.elements;

import cc.creativecomputing.io.xml.CCXMLElement;

public abstract class CCSequenceChannel {

	protected final int _myID;
	
	protected double _myMinimum;
	protected double _myMaximum;
	protected double _myValue;
	
	protected int _myColumn;
	protected int _myRow;
	protected int _myDepth;
	
	public CCSequenceChannel(int theChannelID){
		_myID = theChannelID;
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
	
	public double value(){
		return _myValue;
	}
	
	public abstract CCXMLElement toXML();
	
	public CCXMLElement mappingXML(){
		CCXMLElement myElement = new CCXMLElement("channel");
		myElement.addAttribute("id", _myID);

		myElement.addAttribute("min", _myMinimum);
		myElement.addAttribute("max", _myMaximum);

		myElement.addAttribute("column", _myColumn);
		myElement.addAttribute("row", _myRow);
		myElement.addAttribute("depth", _myDepth);
		return myElement;
	}
}
