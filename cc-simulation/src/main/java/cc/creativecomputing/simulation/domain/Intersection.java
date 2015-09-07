/*
 * Copyright (c) 2013 christianr.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser Public License v3
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl-3.0.html
 * 
 * Contributors:
 *     christianr - initial API and implementation
 */
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
		if (theBox1.max().z < theBox2.min().z || theBox1.min().z > theBox2.max().z)return false;
		
		return true;
	}
}
