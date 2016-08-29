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
package cc.creativecomputing.demo.simulation.particles;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCMesh;
import cc.creativecomputing.graphics.util.CCClipSpaceFrustum;
import cc.creativecomputing.math.CCAABB;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3f;
import cc.creativecomputing.simulation.CCParticle;

public class CCTestParticle extends CCParticle{
	 static float front = 0.66F;
	 static float back = -0.33F;
	 static float width = 0.66F;
	 static float height = 0.33F;
	 static float scale = 1;
	 
	 private CCAABB _myBoundingBox = new CCAABB(new CCVector3f(), new CCVector3f(scale,scale,scale).scale(0.5f));
	 
	 static public int[] indices = new int[]{
		 0,2,1,
		 0,1,3,
		 0,3,2,
		 1,2,3
	 };
	 
	 @Override
	 public void update(final float theDeltaTime){
		 super.update(theDeltaTime);
		 _myBoundingBox.center(position);
	 }
	 
	 public void frustumWrap(CCGraphics g){
		 int frustumState = g.frustum().isInFrustum(_myBoundingBox);
		 if(frustumState != CCClipSpaceFrustum.INSIDE){
				float tang = CCMath.tan(g.camera().fov() * 0.5f) ;
				
				float myWrapHeight = (g.camera().position().z-position.z) * tang * 2;
				float myWrapWidth = myWrapHeight * g.camera().aspect();
				
				switch(frustumState){
				case CCClipSpaceFrustum.TOP:
					position.add(0,-myWrapHeight,0);
					break;
				case CCClipSpaceFrustum.BOTTOM:
					position.add(0,myWrapHeight,0);
					break;
				case CCClipSpaceFrustum.LEFT:
					position.add(myWrapWidth,0,0);
					break;
				case CCClipSpaceFrustum.RIGHT:
					position.add(-myWrapWidth,0,0);
					break;
				}
			}
	 }
	 
	public void draw(CCMesh theMesh){
		float x1 = up.x * 1;
		float y1 = up.y * 1;
		float z1 = up.z * 1;

		float x2 = side.x * 1;
		float y2 = side.y * 1;
		float z2 = side.z * 1;
		
		theMesh.addVertex(position.x - x1 - x2, position.y - y1 - y2, position.z - z1 - z2);
		theMesh.addVertex(position.x + x1 - x2, position.y + y1 - y2, position.z + z1 - z2);
		theMesh.addVertex(position.x + x1 + x2, position.y + y1 + y2, position.z + z1 + z2);
		theMesh.addVertex(position.x - x1 + x2, position.y - y1 + y2, position.z - z1 + z2);
	}
	
	public CCAABB boundingBox(){
		return _myBoundingBox;
	}
}
