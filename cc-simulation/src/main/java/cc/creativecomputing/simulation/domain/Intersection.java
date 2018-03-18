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
package cc.creativecomputing.simulation.domain;

public class Intersection {
	/**
	 * @param c
	 *            sphere centre
	 * @param r
	 *            sphere radius
	 * @return true, if AABB intersects with sphere
	 */
	static public boolean intersects(final CCBox theBox, final Sphere theSphere) {
		double s, d = 0;
		// find the square of the distance
		// from the sphere to the box
		if (theSphere.center.x < theBox.min().x) {
			s = theSphere.center.x - theBox.min().x;
			d += s * s;
		} else if (theSphere.center.x > theBox.max().x) {
			s = theSphere.center.x - theBox.max().x;
			d += s * s;
		}

		if (theSphere.center.y < theBox.min().y) {
			s = theSphere.center.y - theBox.min().y;
			d += s * s;
		} else if (theSphere.center.y > theBox.max().y) {
			s = theSphere.center.y - theBox.max().y;
			d += s * s;
		}

		if (theSphere.center.z < theBox.min().z) {
			s = theSphere.center.z - theBox.min().z;
			d += s * s;
		} else if (theSphere.center.z > theBox.max().z) {
			s = theSphere.center.z - theBox.min().z;
			d += s * s;
		}

		return d <= theSphere.radOut * theSphere.radOut;
	}

	/**
	 * @param b
	 * @return
	 */
	static public boolean intersects(final CCBox theBox1, final CCBox theBox2) {
		if (theBox1.max().x < theBox2.min().x || theBox1.min().x > theBox2.max().x)return false;
		if (theBox1.max().y < theBox2.min().y || theBox1.min().y > theBox2.max().y)return false;
        return !(theBox1.max().z < theBox2.min().z) && !(theBox1.min().z > theBox2.max().z);
    }
}
