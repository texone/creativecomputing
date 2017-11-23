package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.effects.CCEffectable;

public  class CCModulationSource {
	
	public static interface CCModulationImplementation{
		public double modulation(CCEffectManager<?> theEffectManager, CCEffectable theEffectable);
	}

	private final String _myName;
	
	protected CCModulationImplementation _myModulationImplementation;
	
	protected boolean _myIsUpdated = true;
	
	public CCModulationSource(String theName, CCModulationImplementation theImplementation){
		_myName = theName;
		_myModulationImplementation = theImplementation;
	}


	public String name(){
		return _myName;
	}
	
	public void updateModulation(CCEffectManager<?> theEffectManager, CCEffectable theEffectable){
		theEffectable.addRelativeSource(_myName, _myModulationImplementation.modulation(theEffectManager, theEffectable));
	}
	
	public boolean isUpdated(){
		return _myIsUpdated;
	}
	
	public void isUpdated(boolean theIsUpdated){
		_myIsUpdated = theIsUpdated;
	}
	
	public void update(CCAnimator theAnimator, CCEffectManager<?> theManager){
		
	}
}
