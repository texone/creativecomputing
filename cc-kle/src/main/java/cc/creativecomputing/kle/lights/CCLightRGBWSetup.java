package cc.creativecomputing.kle.lights;

import java.util.List;

public class CCLightRGBWSetup extends CCLightRGBSetup{

	protected final CCLightChannel _myLightW;
	
	public CCLightRGBWSetup(List<CCLightChannel> theChannels, CCLightCalculations theCalculations){
		super(theChannels, theCalculations);
		_myLightW = _myChannels.get(3);
	}
	
	public CCLightRGBWSetup(int theIDR, int theIDG, int theIDB, int theIDW, CCLightCalculations theCalculations){
		super(theIDR, theIDG, theIDB, theCalculations);
		_myChannels.add(_myLightW = new CCLightChannel(theIDW));
	}
	
	@Override
	public void setByRelativePosition(double... theValues) {
		if(theValues.length == 1){
			_myLightR.value(theValues[0]);
			_myLightG.value(theValues[0]);
			_myLightB.value(theValues[0]);
			_myLightW.value(theValues[0]);
			_myColor.set(theValues[0]);
			return;
		}

		double myR = theValues != null && theValues.length > 0 ? theValues[0] : 0.5f;
		double myG = theValues != null && theValues.length > 1 ? theValues[1] : 0.5f;
		double myB = theValues != null && theValues.length > 2 ? theValues[2] : 0.5f;
		double myW = theValues != null && theValues.length > 3 ? theValues[3] : 0.5f;
		
		_myLightR.value(myR);
		_myLightG.value(myG);
		_myLightB.value(myB);
		_myLightW.value(myW);
		
		_myColor.set(myR, myG, myB, myW);
	}
}
