package cc.creativecomputing.kle.animation;

import java.util.ArrayList;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.kle.elements.CCSequenceElement;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.random.CCRandom;

public class CCKleModulation {
	@CCProperty(name = "element offset", min = -1, max = 1)
	private double _cElementOffset = 0;
	@CCProperty(name = "group offset", min = -1, max = 1)
	private double _cGroupOffset = 0;
	@CCProperty(name = "global offset", min = -1, max = 1)
	private double _cGlobalOffset = 0;
	@CCProperty(name = "offset", min = -1, max = 1)
	private double _cOffset = 0;
	@CCProperty(name = "random offset", min = -1, max = 1)
	private double _cRandomOffset = 0;
	@CCProperty(name = "x offset", min = -1, max = 1)
	private double _cXOffset = 0;
	@CCProperty(name = "y offset", min = -1, max = 1)
	private double _cYOffset = 0;
	
	@CCProperty(name = "mod", min = 2, max = 8)
	private int _cMod = 1;
	@CCProperty(name = "mod offset", min = -1, max = 1)
	private double _cModOffset = 0;
	
	@CCProperty(name = "div", min = 2, max = 8)
	private int _cDiv = 1;
	@CCProperty(name = "div offset", min = -1, max = 1)
	private double _cDivOffset = 0;
	
	@CCProperty(name = "group mod", min = 2, max = 6)
	private int _cGroupMod = 1;
	@CCProperty(name = "group mod offset", min = -1, max = 1)
	private double _cGroupModOffset = 0;
	
	@CCProperty(name = "group div", min = 2, max = 6)
	private int _cGroupDiv = 1;
	@CCProperty(name = "group div offset", min = -1, max = 1)
	private double _cGroupDivOffset = 0;
	
	private final CCRandom _myRandom;
	private final List<Float> _myRandoms = new ArrayList<>();
	

	private static int randomSeed = 0;
	
	public CCKleModulation(){
		_myRandom = new CCRandom(randomSeed++);
	}
	
	public double modulation(CCSequenceElement theElement) {
		return modulation(theElement, 0, 1);
	}
	
	private double scaleValue(double theMin, double theMax, double theValue, double theOffset){
		if(theOffset < 0){
			theOffset = -theOffset;
			return CCMath.blend(theMax * theOffset, theMin * theOffset, theValue);
		}
		return CCMath.blend(theMin * theOffset, theMax * theOffset, theValue);
	}
	
	public double offsetSum(){
		return 
			_cElementOffset + 
			_cGroupOffset + 
			_cGlobalOffset + 
			_cRandomOffset + 
			_cXOffset + 
			_cYOffset + 
			_cModOffset + 
			_cDivOffset +
			_cGroupModOffset + 
			_cGroupDivOffset +
			_cOffset;
	}
	
	public double modulation(CCSequenceElement theElement, double theMin, double theMax) {
		while(theElement.id() >= _myRandoms.size()){
			_myRandoms.add(_myRandom.random());
		}
//		T4ElementInfo myInfo = theElement.elementInfo();
		double myElementPhase = scaleValue(theMin, theMax, theElement.groupIDBlend(), _cElementOffset);
		double myGroupPhase = scaleValue(theMin, theMax, theElement.groupBlend(), _cGroupOffset);
		double myGlobalPhase = scaleValue(theMin, theMax, theElement.idBlend(), _cGlobalOffset);
		double myRandomPhase = scaleValue(theMin, theMax, _myRandoms.get(theElement.id()), _cRandomOffset); 
		double myXPhase = scaleValue(theMin, theMax, theElement.xBlend(), _cXOffset); 
		double myYPhase = scaleValue(theMin, theMax, theElement.yBlend(), _cYOffset); 
		double myModPhase = scaleValue(theMin, theMax, (theElement.id() % CCMath.max(1, _cMod)) / (double)(_cMod - 1), _cModOffset); 
	
		double myDivPhase = scaleValue(theMin, theMax, (int)((theElement.groupIDBlend() - 0.00001)   * _cDiv) / (double)(_cDiv - 1), _cDivOffset); 
		double myGroupModPhase = scaleValue(theMin, theMax, (theElement.group() % _cGroupMod) / (double)(_cGroupMod - 1), _cGroupModOffset); 
		double myGroupDivPhase = scaleValue(theMin, theMax, (int)((theElement.groupBlend() - 0.00001)   * _cGroupDiv) / (double)(_cGroupDiv - 1), _cGroupDivOffset); 
		double myConstOffset = scaleValue(theMin, theMax, 1f, _cOffset); 
		
		return 
			myElementPhase + 
			myGroupPhase + 
			myGlobalPhase + 
			myRandomPhase + 
			myXPhase + 
			myYPhase + 
			myModPhase + 
			myDivPhase +
			myGroupModPhase + 
			myGroupDivPhase +
			myConstOffset;
	}
}
