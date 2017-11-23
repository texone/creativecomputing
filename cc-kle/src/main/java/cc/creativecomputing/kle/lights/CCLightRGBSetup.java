package cc.creativecomputing.kle.lights;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.math.CCColor;



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
		if(theValues.length == 1){
			_myLightR.value(theValues[0]);
			_myLightG.value(theValues[0]);
			_myLightB.value(theValues[0]);
			
			_myColor.set(theValues[0]);
			return;
		}

		double myR = theValues != null && theValues.length > 0 ? theValues[0] : 0.5f;
		double myG = theValues != null && theValues.length > 1 ? theValues[1] : 0.5f;
		double myB = theValues != null && theValues.length > 2 ? theValues[2] : 0.5f;
		
		CCColor myCol = CCColor.createFromHSB(myR, myG, myB);
		
		_myLightR.value(myCol.r);
		_myLightG.value(myCol.g);
		_myLightB.value(myCol.b);
		
		_myColor.set(myCol);
	}
}
