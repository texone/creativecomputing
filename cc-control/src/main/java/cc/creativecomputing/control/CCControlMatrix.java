package cc.creativecomputing.control;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.math.CCMatrix2;

public class CCControlMatrix extends CCMatrix2{
	
	private String[] _myInputs;
	private String[] _myOutputs;
	
	private Map<String, Integer> _myInputRows = new HashMap<>();
	private Map<String, Integer> _myOutputColumns = new HashMap<>();
	
	public CCControlMatrix(String[] theInputs, String[]theOutputs) {
		super(theOutputs.length, theInputs.length);
		_myInputs = theInputs;
		_myOutputs = theOutputs;
		
		for(int i = 0; i < _myInputs.length;i++) {
			_myInputRows.put(_myInputs[i], i);
		}
		for(int i = 0; i < _myOutputs.length;i++) {
			_myOutputColumns.put(_myOutputs[i], i);
		}
	}
	
	public double value(String theInput, String theOutput) {
		return value(_myOutputColumns.get(theOutput), _myInputRows.get(theInput));
	}
	
	public double value(int theColumn, int theRow) {
		return get(theColumn, theRow)[0];
	}
	
	public String[] inputs() {
		return _myInputs;
	}
	
	public String[] outputs() {
		return _myOutputs;
	}
}
