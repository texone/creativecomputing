package cc.creativecomputing.effects;

import java.util.ArrayList;

import cc.creativecomputing.math.CCMath;

public class CCEffectables<Type extends CCEffectable> extends ArrayList<Type>{
	/**
	 * 
	 */
	private static final long serialVersionUID = -1200033076720174639L;
	
	public CCEffectables(){
		super();
	}
	
	private int _myGroups = 0;
	
	@Override
	public boolean add(Type e) {
		_myGroups = CCMath.max(e.group(), _myGroups);
		return super.add(e);
	}
	
	public void updateInfos(){
		_myGroups = 0;
		for(Type myElement:this){
			_myGroups = CCMath.max(myElement.group(), _myGroups);
		}
	}
	
	public void update(double theDeltaTime){
		for(Type myElement:this){
			myElement.update(theDeltaTime);
		}
	}
	
	public int groups(){
		return _myGroups;
	}
}
