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
package cc.creativecomputing.demo.simulation.steering;

import java.util.List;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.util.CCClipSpaceFrustum;
import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.steering.CCAgent;

public class CCTestAgent extends CCAgent {
	static float front = 0.66F;
	static float back = -0.33F;
	static float width = 0.66F;
	static float height = 0.33F;
	static float scale = 10;

	private CCAABB _myBoundingBox = new CCAABB(new CCVector3f(), new CCVector3f(scale, scale, scale).scale(0.5f));

	static public int[] indices = new int[] { 0, 2, 1, 0, 1, 3, 0, 3, 2, 1, 2, 3 };

	@Override
	public void update(final float theDeltaTime) {
		super.update(theDeltaTime);
		_myBoundingBox.center(position);
	}

	public void frustumWrap(CCGraphics g) {
		int frustumState = g.frustum().isInFrustum(_myBoundingBox);
		if (frustumState != CCClipSpaceFrustum.INSIDE) {
			float tang = CCMath.tan(g.camera().fov() * 0.5f);

			float myWrapHeight = (g.camera().position().z - position.z) * tang * 2;
			float myWrapWidth = myWrapHeight * g.camera().aspect();

			switch (frustumState) {
			case CCClipSpaceFrustum.TOP:
				position.add(0, -myWrapHeight, 0);
				break;
			case CCClipSpaceFrustum.BOTTOM:
				position.add(0, myWrapHeight, 0);
				break;
			case CCClipSpaceFrustum.LEFT:
				position.add(myWrapWidth, 0, 0);
				break;
			case CCClipSpaceFrustum.RIGHT:
				position.add(-myWrapWidth, 0, 0);
				break;
			}
		}
	}
	
	public void centerWrap(CCGraphics g){
		int frustumState = g.frustum().isInFrustum(_myBoundingBox);
		if (frustumState != CCClipSpaceFrustum.INSIDE) {
			position.set(0,0,0);
		}
	}

	public void draw(List<CCVector3f> theVertexList) {
		CCVector3f lv0 = globalizePosition(new CCVector3f(0.0F, 0.0F, front * scale));
		CCVector3f lv1 = globalizePosition(new CCVector3f(0.0F, height * scale, back * scale));
		CCVector3f lv2 = globalizePosition(new CCVector3f(-width * scale, 0.0F, back * scale));
		CCVector3f lv3 = globalizePosition(new CCVector3f(width * scale, 0.0F, back * scale));

		theVertexList.add(lv0);
		theVertexList.add(lv2);
		theVertexList.add(lv1);

		theVertexList.add(lv0);
		theVertexList.add(lv1);
		theVertexList.add(lv3);

		theVertexList.add(lv0);
		theVertexList.add(lv3);
		theVertexList.add(lv2);

		theVertexList.add(lv1);
		theVertexList.add(lv2);
		theVertexList.add(lv3);
	}

	public CCAABB boundingBox() {
		return _myBoundingBox;
	}
}
