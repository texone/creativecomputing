package cc.creativecomputing.kle.elements.lights;

import java.util.ArrayList;
import java.util.List;



public class CCLightRGBSetup extends CCLightSetup{

	protected final CCLightChannel _myLightR;
	protected final CCLightChannel _myLightG;
	protected final CCLightChannel _myLightB;
	
	
	public CCLightRGBSetup(List<CCLightChannel> theChannels){
		super(theChannels);
		
		_myLightR = _myChannels.get(0);
		_myLightG = _myChannels.get(1);
		_myLightB = _myChannels.get(2);
	}
	
	public CCLightRGBSetup(int theIDR, int theIDG, int theIDB){
		super(new ArrayList<>());
		_myChannels.add(_myLightR = new CCLightChannel(theIDR));
		_myChannels.add(_myLightG = new CCLightChannel(theIDG));
		_myChannels.add(_myLightB = new CCLightChannel(theIDB));
	}
	
	@Override
	public void setByRelativePosition(double... theValues) {

		double myR = theValues != null && theValues.length > 0 ? theValues[0] : 0.5f;
		double myG = theValues != null && theValues.length > 1 ? theValues[1] : 0.5f;
		double myB = theValues != null && theValues.length > 2 ? theValues[2] : 0.5f;
		
		_myLightR.value(myR);
		_myLightG.value(myG);
		_myLightB.value(myB);
		
		_myColor.set(myR, myG, myB);
	}
}
