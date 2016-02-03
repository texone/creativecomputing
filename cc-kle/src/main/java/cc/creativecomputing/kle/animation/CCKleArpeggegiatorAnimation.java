package cc.creativecomputing.kle.animation;

import java.util.LinkedHashMap;
import java.util.Map;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;

public class CCKleArpeggegiatorAnimation extends CCKleAnimation{
	
	public enum CCArpegio{
		UP,
		DOWN,
		UP_DOWN
	}
	
	@CCProperty(name = "modulations")
	private Map<String, CCKleModulation> _cModulations = new LinkedHashMap<>();
	
	private String[] _myValueNames = new String[0];

	@CCProperty(name = "progress", min = 0, max = 1)
	private double _cProgress = 0;
	
	@CCProperty(name = "progress scale", min = 1, max = 10)
	private double _cProgressScale = 0;
	
	@CCProperty(name = "mode")
	public CCArpegio _cMode = CCArpegio.UP_DOWN;
	
	@Override
	public double[] animate(CCSequenceElement theElement) {
		double myBlend = elementBlend(theElement);
		double myProgress = (_cProgress * _cProgressScale) % 1;
		
		double[] myResult = new double[_myValueNames.length];
		for(int i = 0; i < _myValueNames.length;i++){
			double myBrightness = 1d;
			double myMod = _cModulations.get(_myValueNames[i]).modulation(theElement, -1, 1);
			
			switch(_cMode){
			case UP:
				myBrightness = (myProgress + myMod) % 1;
				break;
			case DOWN:
				myBrightness = 1 - ((1 - myProgress) + myMod) % 1;
				break;
			case UP_DOWN:
				if(myProgress < 0.5){
					myBrightness = (myProgress * 2 + myMod) % 1;
				}else{
					myBrightness =  ((1 - (myProgress - 0.5) * 2) + myMod) % 1;
				break;
				}
				break;
			}
			myResult[i] = myBrightness * myBlend;
		}
		return myResult;
	}
		
}
