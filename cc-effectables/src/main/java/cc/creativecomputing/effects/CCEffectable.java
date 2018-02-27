package cc.creativecomputing.effects;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.CCSelectable;
import cc.creativecomputing.core.CCSelectionListener;
import cc.creativecomputing.core.events.CCListenerManager;
import cc.creativecomputing.math.CCMatrix4x4;
import cc.creativecomputing.math.CCVector3;


public abstract class CCEffectable implements CCSelectable{
	
	public static final String CONSTANT_SOURCE = "constant";
	public static final String RANDOM_SOURCE = "random";
	
	public static final String ID_SOURCE = "id";
	public static final String COLUMN_SOURCE = "column";
	public static final String ROW_SOURCE = "row";
	public static final String GROUP_SOURCE = "group";
	public static final String GROUP_ID_SOURCE = "group id";

	protected final int _myID;
	@CCProperty(name = "effect scale", min = 0, max = 1)
	public double _cEffectScale = 1d;
	@CCProperty(name = "active")
	public boolean _cActive = true;
	@CCProperty(name = "relative sources", hide = true)
	public Map<String, Double> _myRelativeSources = new LinkedHashMap<>();
	@CCProperty(name = "id sources", hide = true)
	public Map<String, Integer> _myIdBasedSources = new LinkedHashMap<>();
	
	private boolean _cIsSelected = false;
	
	protected CCMatrix4x4 _myMatrix;
	
	public CCEffectable(int theId){
		_myID = theId;

		addIdBasedSource(ID_SOURCE, _myID);
		
		_myMatrix = new CCMatrix4x4();
	}
	
	private CCVector3 _myPosition = null;
	
	public CCVector3 position(){
		if(_myPosition == null){
			_myPosition = _myMatrix.applyPostPoint(new CCVector3());
		}
		return _myPosition;
	}
	
	public CCVector3 normedPosition() {
		return new CCVector3();
	}
	
	public CCMatrix4x4 matrix(){
		return _myMatrix;
	}
	
	@Override
	public boolean isSelected(){
		return _cIsSelected;
	}
	
	private CCListenerManager<CCSelectionListener> _myListenerManager;
	
	@Override
	public void addListener(CCSelectionListener theListener) {
		if(_myListenerManager == null)_myListenerManager = CCListenerManager.create(CCSelectionListener.class);
		_myListenerManager.add(theListener);
	}
	
	@Override
	@CCProperty(name = "selected")
	public void select(boolean theIsSelected){
		_cIsSelected = theIsSelected;
		if(_myListenerManager != null)_myListenerManager.proxy().isSelected(theIsSelected);
	}
	
	public void addRelativeSource(String theKey, double theValue){
		_myRelativeSources.put(theKey, theValue);
	}
	
	public void addIdBasedSource(String theKey, int theValue){
		_myIdBasedSources.put(theKey, theValue);
	}
	
	public double relativeSource(String theKey){
		Double result = _myRelativeSources.get(theKey);
		return result == null ? 0 : result;
	}
	
	public double randomBlend(){
		return relativeSource(CCEffectable.RANDOM_SOURCE);
	}

	public int idSource(String theKey){
		Integer result = _myIdBasedSources.get(theKey);
		return result == null ? 0 : result;
	}
	
	public void column(int theColumn){
		addIdBasedSource(COLUMN_SOURCE, theColumn);
	}
	
	public int column(){
		return idSource(COLUMN_SOURCE);
	}
	
	public double columnBlend(){
		return relativeSource(CCEffectable.COLUMN_SOURCE);
	}
	
	public void row(int theRow){
		addIdBasedSource(ROW_SOURCE, theRow);
	}
	
	public int row(){
		return idSource(ROW_SOURCE);
	}
	
	public double rowBlend(){
		return relativeSource(CCEffectable.ROW_SOURCE);
	}
	
	public void group(int theGroup){
		addIdBasedSource(GROUP_SOURCE, theGroup);
	}
	
	public double groupBlend(){
		return relativeSource(CCEffectable.GROUP_SOURCE);
	}
	
	public int group(){
		return idSource(GROUP_SOURCE);
	}
	
	public void groupID(int theGroup){
		addIdBasedSource(GROUP_ID_SOURCE, theGroup);
	}
	
	public double groupIDBlend(){
		return relativeSource(CCEffectable.GROUP_ID_SOURCE);
	}
	
	public int groupID(){
		return idSource(GROUP_ID_SOURCE);
	}
	
	public void update(CCAnimator theAnimator){
		
	}
	
	public void parameters(String...theParameters){
		
	}
	
	public void apply(double...theValues){
		
	}
	
	
	
	public int id(){
		return _myID;
	}
}
