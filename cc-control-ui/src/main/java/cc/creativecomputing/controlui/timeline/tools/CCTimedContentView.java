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
package cc.creativecomputing.controlui.timeline.tools;

import cc.creativecomputing.control.timeline.point.CCControlPoint;
import cc.creativecomputing.math.CCVector2;

/**
 * @author christianriekoff
 *
 */
public interface CCTimedContentView {
	double timeToViewX(double theTime);
	
	double valueToViewY(double theValue);
	
	double viewXToTime(double theViewX);
	
	double viewWidthToTime(double theViewWidth);

	CCControlPoint viewToCurveSpace(CCVector2 theViewCoords, boolean b);

	CCVector2 curveToViewSpace(CCControlPoint myControlPoint);

	CCControlPoint quantize(CCControlPoint myTargetPosition);

	double viewTime();
}
