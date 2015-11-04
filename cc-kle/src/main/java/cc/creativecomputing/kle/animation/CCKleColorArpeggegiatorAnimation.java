package cc.creativecomputing.kle.animation;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCColor;

public class CCKleColorArpeggegiatorAnimation extends CCKleAnimation<CCColor>{
	
	public enum CCArpegio{
		UP,
		DOWN,
		UP_DOWN
	}
	
	@CCProperty(name = "modulation")
	private CCKleModulation _cModulation = new CCKleModulation();

	@CCProperty(name = "progress", min = 0, max = 1)
	private double _cProgress = 0;
	
	@CCProperty(name = "progress scale", min = 1, max = 10)
	private double _cProgressScale = 0;
	
	@CCProperty(name = "mode")
	public CCArpegio _cMode = CCArpegio.UP_DOWN;
	
	@Override
	public CCColor animate(CCSequenceElement theElement) {
		
		double myBrightness = 1d;
		double myProgress = (_cProgress * _cProgressScale) % 1;
		double myMod = _cModulation.modulation(theElement);
		
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
		return new CCColor(myBrightness);
	}

	

		
}
