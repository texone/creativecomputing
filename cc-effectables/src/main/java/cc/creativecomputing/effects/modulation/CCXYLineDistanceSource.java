package cc.creativecomputing.effects.modulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCLine2;
import cc.creativecomputing.math.CCMath;

public class CCXYLineDistanceSource extends CCModulationSource {

	@CCProperty(name = "radius")
	private double _cRadius;

	@CCProperty(name = "positions")
	private List<CCLine2> _myLines = new ArrayList<>();

	public CCXYLineDistanceSource(String theName, double theRadius, CCLine2...theLines) {
		super(theName, null);
		
		_myLines.addAll(Arrays.asList(theLines));
		
		_cRadius = theRadius;
		
		_myModulationImplementation = (effectManager, effectable) -> {
			double myDistance = Double.MAX_VALUE;
			for(CCLine2 myLine:_myLines){
				myDistance = CCMath.min(myDistance, myLine.distance(effectable.position().xy()));
			}
			if(myDistance > _cRadius)return 0;
			return 1 - CCMath.saturate(myDistance / _cRadius);
		};
	}
	
	public void lines(List<CCLine2> thePositions){
		_myLines = thePositions;
		_myIsUpdated = true;
	}
	
	@Override
	public boolean isUpdated() {
		return true;
	}

}
