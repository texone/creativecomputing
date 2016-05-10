package cc.creativecomputing.demo.kle.motorrotationdemo;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.core.logging.CCLog;
import cc.creativecomputing.demo.kle.motorrotationdemo.CCTriKiDemo.CCTriangleElement;
import cc.creativecomputing.math.signal.CCMixSignal;

public class CCTriKiSignalAnimation extends CCTriKiAnimation<CCTriKiSignalAnimation>{
	
	private class CCTriKiSignal{
		@CCProperty(name = "signal")
		private CCMixSignal _mySignal = new CCMixSignal();
		
		@CCProperty(name = "amount", min = 0, max = 1)
		private double _cAmount = 0;
		
		@CCProperty(name = "modulation")
		private CCTriKiModulation _cModulation = new CCTriKiModulation();

		@CCProperty(name = "use global phase")
		private boolean _cUseGlobalPhase = false;
		@CCProperty(name = "phase speed", min = 0, max = 10)
		private double _cSpeed = 0;
		
		private double _myPhase = 0;
		
		@CCProperty(name = "factor", min = 1, max = 10)
		private double _cFactor = 1;
		
		public void update(final double theDeltaTime){
			_myPhase += theDeltaTime * _cSpeed;
			CCLog.info(_myPhase);
		}
		
		public double value(CCTriangleElement theElement){
			double myPhase = _cUseGlobalPhase ? _myGlobalPhase : _myPhase;
			double myResult = _mySignal.value(
				(myPhase + _cModulation.modulation(theElement)) * _cFactor
			);
			return (myResult - 0.5f) * 2;
		}
	}
	
	@CCProperty(name = "x signal")
	private CCTriKiSignal _myXSignal = new CCTriKiSignal();
	@CCProperty(name = "phase speed", min = 0, max = 10)
	private double _cSpeed = 0;
	
	
	private double _myGlobalPhase = 0;
	
	
	public void update(final double theDeltaTime){
		_myGlobalPhase += theDeltaTime * _cSpeed;
		_myXSignal.update(theDeltaTime);
	}
	
	public double animate(CCTriangleElement theElement){
		return _myXSignal.value(theElement) * _cBlend * _myXSignal._cAmount;
	}

	@Override
	public CCTriKiSignalAnimation createAnimation() {
		return new CCTriKiSignalAnimation();
	}
}
