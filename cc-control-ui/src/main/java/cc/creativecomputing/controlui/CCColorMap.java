/*******************************************************************************
 * Copyright (C) 2018 christianr
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package cc.creativecomputing.controlui;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;

public class CCColorMap {


	private static Map<Path, CCColor> _myColorMap = new HashMap<>();
	
	public static int HSB_DEPTH = 3;
	
	private static float _myHue = 0;
	
	private static float FACTOR = 1.1f;
	
	public static CCColor getColor(Path thePath) {
		if(thePath.getNameCount() < HSB_DEPTH)return CCColor.GRAY;
		
		if(!_myColorMap.containsKey(thePath.getName(HSB_DEPTH - 1))) {
			_myColorMap.put(thePath.getName(HSB_DEPTH - 1), CCColor.createFromHSB(_myHue, 0.5f, 0.5f));
			_myHue += 0.1f;
		}
		CCColor myResult = _myColorMap.get(thePath.getName(HSB_DEPTH - 1));
		
		for(int i = HSB_DEPTH; i < thePath.getNameCount();i++){
			myResult = new CCColor(
				CCMath.saturate(myResult.r / 255f * FACTOR), 
				CCMath.saturate(myResult.g / 255f * FACTOR),
				CCMath.saturate(myResult.b / 255f * FACTOR)
			);
		}
		return myResult;
	}
}
