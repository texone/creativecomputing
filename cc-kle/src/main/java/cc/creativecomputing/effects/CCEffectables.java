package cc.creativecomputing.effects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import cc.creativecomputing.math.CCMath;

public class CCEffectables<Type extends CCEffectable> extends ArrayList<Type>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1200033076720174639L;
	
	private Set<String> _myRelativeSources = new HashSet<>();
	private Set<String> _myIdSources = new HashSet<>();
	
	private Map<String, Integer> _myMaxIds = new HashMap<>();
	
	public CCEffectables(){
		super();
		addRelativeSources(CCEffectable.CONSTANT_SOURCE, CCEffectable.RANDOM_SOURCE);
		addIdSources(CCEffectable.ID_SOURCE);
	}
	
	public void addRelativeSources(String...theRelativeSources){
		for(String mySource:theRelativeSources){
			_myRelativeSources.add(mySource);
		}
	}
	
	private void updateMaxIds(CCEffectable theEffectable){
		for(String myIdSource:_myIdSources){
			if(!_myMaxIds.containsKey(myIdSource)){
				_myMaxIds.put(myIdSource, 0);
			}
			int myLastMax = _myMaxIds.get(myIdSource);
			
			_myMaxIds.put(myIdSource, CCMath.max(theEffectable.idSource(myIdSource), myLastMax));
		}
	}
	
	private void updateMaxIds(){
		for(CCEffectable myEffectable:this){
			updateMaxIds(myEffectable);
		}
	}
	
	public void addIdSources(String...theIdSources){
		for(String mySource:theIdSources){
			_myIdSources.add(mySource);
			_myRelativeSources.add(mySource);
		}
		updateMaxIds();
	}
	
	public Set<String> relativeSources(){
		return _myRelativeSources;
	}
	
	public Set<String> idSources(){
		return _myIdSources;
	}
	
	public int idMax(String theSource){
		Integer result = _myMaxIds.get(theSource);
		return result == null ? 0 : result;
	}
	
	@Override
	public boolean add(Type e) {
		
		return super.add(e);
	}
	
	public void updateInfos(){
		for(Type myElement:this){
			for(String myIdSource:_myIdSources){
				if(!_myMaxIds.containsKey(myIdSource)){
					_myMaxIds.put(myIdSource, 0);
				}
				int myLastMax = _myMaxIds.get(myIdSource);
				_myMaxIds.put(myIdSource, CCMath.max(myElement.idSource(myIdSource), myLastMax));
			}
		}
	}
	
	public void update(double theDeltaTime){
		for(Type myElement:this){
			myElement.update(theDeltaTime);
		}
	}
	
	public int groups(){
		return idMax(CCEffectable.GROUP_SOURCE);
	}
}
