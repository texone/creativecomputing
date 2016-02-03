package cc.creativecomputing.kle.animation;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.control.code.CCCompileObject;
import cc.creativecomputing.control.code.CCRealtimeCompile;
import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;

public class CCKleRealtimeCompileAnimation extends CCKleAnimation {
	
	public static interface CCRealtimeAnimation extends CCCompileObject{
		
		public void update(final double theDeltaTime);
		
		public double animate(CCSequenceElement theElement, CCKleModulation theModulation);
	}

	@CCProperty(name = "modulations")
	private Map<String, CCKleModulation> _cModulations = new LinkedHashMap<>();
	
	private String[] _myValueNames = new String[0];
	
	@CCProperty(name = "real time animation")
	private CCRealtimeCompile<CCRealtimeAnimation> _myRealTimeAnimation;
	
	public CCKleRealtimeCompileAnimation(){
		_myRealTimeAnimation = new CCRealtimeCompile<CCRealtimeAnimation>("cc.creativecomputing.control.CCRealtimeGraphImp", CCRealtimeAnimation.class);
		_myRealTimeAnimation.createObject();
	}

	@Override
	public void update(final double theDeltaTime) {
		if(_myRealTimeAnimation.instance() == null)return;
		
		_myRealTimeAnimation.instance().update(theDeltaTime);
	}

	public double[] animate(CCSequenceElement theElement) {
		if(_myRealTimeAnimation.instance() == null)return null;
		
		double myBlend = elementBlend(theElement);
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			try{
				myResult[i] = _myRealTimeAnimation.instance().animate(theElement,_cModulations.get(_myValueNames[i])) * myBlend;
			}catch(Exception e){
				
			}
		}
		return myResult;
	}

	@Override
	public void valueNames(String... theValueNames) {
		_cModulations.clear();
		_myValueNames = new String[theValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			_myValueNames[i] = theValueNames[i] + " modulation";
			_cModulations.put(_myValueNames[i], new CCKleModulation());
		}
	}
}

