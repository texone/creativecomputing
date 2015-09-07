package cc.creativecomputing.demo;

import cc.creativecomputing.graphics.CCGraphics;
import cc.creativecomputing.graphics.CCGraphics.CCBlendMode;
import cc.creativecomputing.graphics.CCGraphics.CCPolygonMode;
import cc.creativecomputing.math.CCColor;
import cc.creativecomputing.math.CCMath;
import cc.creativecomputing.math.CCVector3;

public class ControlDemoRenderer {
	

	CCVector3[] pos;
	CCVector3[] dim;
	CCColor color;
	int n;
	
	public void setData (CCVector3[] thePositions, CCVector3[] theDimemsions, int theN, CCColor theColor) {
		pos = thePositions;
		dim = theDimemsions;
		color = theColor;
		n = theN;
	}
	
	public void draw (CCGraphics g, float timePassed, float sizeX, float sizeY, float sizeZ, float oscillateX, float oscillateY, float oscillateZ) {
		for (int i=0; i<n; i++) {
			g.pushMatrix();
			g.translate(pos[i]);
			g.scale(sizeX, sizeY, sizeZ);
			g.scale(CCMath.cos(timePassed*oscillateX), CCMath.cos(timePassed*oscillateY), CCMath.cos(timePassed*oscillateZ));
			
			g.color(color.r,color.g,color.b,0.1f);
			g.polygonMode(CCPolygonMode.FILL);
			g.box(dim[i]);
			
			g.blendMode(CCBlendMode.ALPHA);
			g.color(0f);
			g.polygonMode(CCPolygonMode.LINE);
			g.box(dim[i]);
			
			g.popMatrix();	
		}
	}
}