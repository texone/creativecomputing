package cc.creativecomputing.effects.modulation;

import cc.creativecomputing.app.modules.CCAnimator;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.effects.CCEffectManager;
import cc.creativecomputing.math.time.CCDate;

public class CCTimeSource extends CCModulationSource {

	@CCProperty(name = "second", min = 0, max = 1)
	private double _cSecond;
	@CCProperty(name = "minute", min = 0, max = 1)
	private double _cMinute;
	@CCProperty(name = "hour", min = 0, max = 1)
	private double _cHour;
	
	@CCProperty(name = "print")
	private boolean _cPrint = false;

	private CCDate _myDate = new CCDate();

	public CCTimeSource(String theName) {
		super(theName, null);
		
		
		_myModulationImplementation = (effectManager, effectable) -> {
			CCLog.info(_myDate.minuteProgress());
			return 
				_myDate.hourProgress() * 2 % 2 * _cHour + 
				_myDate.minuteProgress() * _cMinute + 
				_myDate.secondProgress() * _cSecond;
		};
	}
	
	@Override
	public void update(CCAnimator theAnimator, CCEffectManager<?> theManager) {
		_myDate = new CCDate();
	}
	
	
	@Override
	public boolean isUpdated() {
		return true;
	}

}
