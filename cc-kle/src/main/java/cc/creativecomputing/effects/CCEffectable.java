package cc.creativecomputing.effects;

import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.math.CCMath;


public abstract class CCEffectable {
	
	public static final String RANDOM_SOURCE = "random";
	
	public static final String ID_SOURCE = "id";
	public static final String COLUMN_SOURCE = "column";
	public static final String ROW_SOURCE = "row";
	public static final String GROUP_SOURCE = "group";

	protected final int _myID;
	
	public Map<String, Double> _myRelativeSources = new HashMap<String, Double>();
	public Map<String, Integer> _myIdBasedSources = new HashMap<String, Integer>();
	
	public CCEffectable(int theId){
		_myID = theId;
		addIdBasedSource(ID_SOURCE, _myID);
		addRelativeSource(RANDOM_SOURCE, CCMath.random());
	}
	
	protected void addRelativeSource(String theKey, double theValue){
		_myRelativeSources.put(theKey, theValue);
	}
	
	protected void addIdBasedSource(String theKey, int theValue){
		_myIdBasedSources.put(theKey, theValue);
	}
	
	public double relativeSource(String theKey){
		Double result = _myRelativeSources.get(theKey);
		return result == null ? 0 : result;
	}

	public int idSource(String theKey){
		Integer result = _myIdBasedSources.get(theKey);
		return result == null ? 0 : result;
	}
	
	public void column(int theColumn){
		addIdBasedSource(COLUMN_SOURCE, theColumn);
	}
	
	public void row(int theRow){
		addIdBasedSource(ROW_SOURCE, theRow);
	}
	
	public void group(int theGroup){
		addIdBasedSource(GROUP_SOURCE, theGroup);
	}
	
	public int group(){
		return idSource(GROUP_SOURCE);
	}
	
	public void update(double theDeltaTime){
		
	}
	
	public void parameters(String...theParameters){
		
	}
	
	public void apply(double...theValues){
		
	}
	
	
	
	public int id(){
		return _myID;
	}
}
