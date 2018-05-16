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
	@CCProperty(name = "day", min = 0, max = 1)
	private double _cDay;
	
	@CCProperty(name = "print")
	private boolean _cPrint = false;

	private CCDate _myDate = new CCDate();

	public CCTimeSource(String theName) {
		super(theName, null);
		
		
		_myModulationImplementation = (effectManager, effectable) -> {
			double myIDBlend = effectable.relativeSource("id");
			return 
				((myIDBlend + _myDate.dayProgress() * 2 % 2) % 1) * _cDay + 
				((myIDBlend + _myDate.hourProgress()) % 1) * _cHour + 
				((myIDBlend + _myDate.minuteProgress()) % 1) * _cMinute + 
				((myIDBlend + _myDate.secondProgress()) % 1) * _cSecond;
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
