package cc.creativecomputing.effects.modulation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import cc.creativecomputing.core.CCProperty;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector2;

public class CCXYRadialSource extends CCModulationSource {

	@CCProperty(name = "positions")
	private List<CCVector2> _myPositions = new ArrayList<>();

	public CCXYRadialSource(String theName, CCVector2...thePositions) {
		super(theName, null);
		
		_myPositions.addAll(Arrays.asList(thePositions));
		
		_myModulationImplementation = (effectManager, effectable) -> {
			double myMinDistance = Double.MAX_VALUE;
			double myAngle = 0;
			for(CCVector2 myPosition:_myPositions){
				CCVector2 myElementPos = effectable.position().xy();
				double myDistance = myPosition.distance(myElementPos);
				if(myDistance < myMinDistance){
					myMinDistance = myDistance;
					myAngle = (CCVector2.angle(
						new CCVector2(myElementPos.x, myElementPos.y).subtract(myPosition).normalizeLocal(), 
						new CCVector2(1,0)
					) + CCMath.PI) / CCMath.TWO_PI;
				}
				
			}
			return myAngle;
		};
	}
	
	public void positions(List<CCVector2> thePositions){
		_myPositions = thePositions;
		_myIsUpdated = true;
	}

	@Override
	public boolean isUpdated() {
		return true;
	}

}
