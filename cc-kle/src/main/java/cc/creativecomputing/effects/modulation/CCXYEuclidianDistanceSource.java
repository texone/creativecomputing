package cc.creativecomputing.effects.modulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCXYEuclidianDistanceSource extends CCModulationSource {

	@CCProperty(name = "radius")
	private double _cRadius;

	private List<CCVector2> _myPositions = new ArrayList<>();

	public CCXYEuclidianDistanceSource(String theName, double theRadius, CCVector2...thePositions) {
		super(theName, null);
		
		_myPositions.addAll(Arrays.asList(thePositions));
		
		_cRadius = theRadius;
		
		_myModulationImplementation = (effectManager, effectable) -> {
			double myDistance = Double.MAX_VALUE;
			for(CCVector2 myPosition:_myPositions){
				myDistance = CCMath.min(myDistance, myPosition.distance(effectable.position().xy()));
			}
			return 1 - CCMath.saturate(myDistance / _cRadius);
		};
	}
	
	public void positions(List<CCVector2> thePositions){
		_myPositions = thePositions;
		_myIsUpdated = true;
	}

}
