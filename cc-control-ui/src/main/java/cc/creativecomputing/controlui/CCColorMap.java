package cc.creativecomputing.controlui;

import java.awt.Color;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.math.CCMath;

public class CCColorMap {


	private static Map<Path, Color> _myColorMap = new HashMap<>();
	
	private static int HSB_DEPTH = 3;
	
	private static float _myHue = 0;
	
	private static float FACTOR = 1.1f;
	
	public static Color getColor(Path thePath) {
		if(thePath.getNameCount() < HSB_DEPTH)return Color.GRAY;
		
		if(!_myColorMap.containsKey(thePath.getName(2))) {
			_myColorMap.put(thePath.getName(HSB_DEPTH - 1), Color.getHSBColor(_myHue, 0.5f, 0.5f));
			_myHue += 0.1f;
		}
		Color myResult = _myColorMap.get(thePath.getName(HSB_DEPTH - 1));
		
		for(int i = HSB_DEPTH; i < thePath.getNameCount();i++){
			myResult = new Color(
				CCMath.saturate(myResult.getRed() / 255f * FACTOR), 
				CCMath.saturate(myResult.getGreen() / 255f * FACTOR),
				CCMath.saturate(myResult.getBlue() / 255f * FACTOR)
			);
		}
		return myResult;
	}
}
