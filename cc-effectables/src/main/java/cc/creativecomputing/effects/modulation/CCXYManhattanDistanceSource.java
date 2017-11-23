package cc.creativecomputing.effects.modulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCXYManhattanDistanceSource extends CCModulationSource {

	@CCProperty(name = "radius x")
	private double _cRadiusX;
	@CCProperty(name = "radius y")
	private double _cRadiusY;

	private List<CCVector2> _myPositions = new ArrayList<>();

	public CCXYManhattanDistanceSource(String theName, double theRadiusX, double theRadiusY, CCVector2...thePositions) {
		super(theName, null);
		
		_myPositions.addAll(Arrays.asList(thePositions));
		
		_cRadiusX = theRadiusX;
		_cRadiusY = theRadiusY;
		
		_myModulationImplementation = (effectManager, effectable) -> {
			double myXDistance = Double.MAX_VALUE;
			double myYDistance = Double.MAX_VALUE;
			for(CCVector2 myPosition:_myPositions){
				CCVector2 myElementPos = effectable.position().xy();
				myXDistance = CCMath.min(CCMath.abs(myPosition.x - myElementPos.x), myXDistance);
				myYDistance = CCMath.min(CCMath.abs(myPosition.y - myElementPos.y), myYDistance);
				
			}
			return CCMath.min(1 - CCMath.saturate(myXDistance / _cRadiusX),  1 - CCMath.saturate(myYDistance / _cRadiusY));
		};
	}
	
	public void positions(List<CCVector2> thePositions){
		_myPositions = thePositions;
		_myIsUpdated = true;
	}

}
