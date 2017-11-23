package cc.creativecomputing.kle.lights;

import java.util.List;



public class CCLightBrightnessSetup extends CCLightSetup{

	protected final CCLightChannel _myLight;
	
	
	public CCLightBrightnessSetup(List<CCLightChannel> theChannels){
		super(theChannels);
		
		_myLight = _myChannels.get(0);
		
	}
	
	@Override
	public void setByRelativePosition(double... theValues) {

		double myR = theValues != null && theValues.length > 0 ? theValues[0] : 0.5f;
		
		_myLight.value(myR);
		
		_myColor.set(myR);
	}
}
